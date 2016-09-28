//*********************************************************************************************************************
//  Copyright 2011-2013 by the University of Virginia
//	All Rights Reserved
//
//  Created by Patrick Keith-Hynes
//  Center for Diabetes Technology
//  University of Virginia
//*********************************************************************************************************************
package edu.virginia.dtc.APCservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.virginia.dtc.APCservice.DataManagement.SensorsManager;
import edu.virginia.dtc.APCservice.DataManagement.SubBolusCreator;
import edu.virginia.dtc.APCservice.Server.IITServerConnector;
import edu.virginia.dtc.APCservice.USB.USBHost;
import edu.virginia.dtc.APCservice.USB.USBReadThread;
import edu.virginia.dtc.SysMan.Biometrics;
import edu.virginia.dtc.SysMan.Controllers;
import edu.virginia.dtc.SysMan.Debug;
import edu.virginia.dtc.SysMan.Event;
import edu.virginia.dtc.SysMan.Log;
import edu.virginia.dtc.SysMan.Params;
import edu.virginia.dtc.SysMan.Safety;
import edu.virginia.dtc.SysMan.State;

import android.content.Context;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.Message;
import android.os.Handler;
import android.os.RemoteException;
import android.widget.Toast;
import android.app.Notification;
import android.app.PendingIntent;

public class IOMain extends Service {
	// Power management
	private PowerManager pm;
	private PowerManager.WakeLock wl;

	private static final String TAG = "HMSservice";

	private static Messenger mMessengerToClient = null;
	private final Messenger mMessengerFromClient = new Messenger(new IncomingHMSHandler());

	//Logger
	//private Logger mLog = Logger.getLogger("MyLogger");

	//****************************************** 
	//********* IIT I/O Fields Management ******
	//******************************************

	//Additional database for other devices
	private IITServerConnector iitConnector;
	public static final String _TABLE_NAME_BM = "'bodymedia'";
	public static final String _TABLE_NAME_EM = "'empatica'";
	public static final String _ALGORITHM_RESULTS_NAME = "'USB_Commands'";

	//Sensors reader
	public SensorsManager sManager;
	private SubBolusCreator insulinManager;

	//TODO Algorithm Manager
	//private AlgorithmManager aManager;

	//Arrays containing sensors info:
	private ArrayList<Double> cgmArray;
	private ArrayList<ArrayList<Double>> bodymediaArray;
	//private ArrayList<ArrayList<Double>> empaticaArray;

	private ArrayList<Double> zephyrArray;


	//Other
	//Dias values
	private List<Map<String, String>> dArgs;

	//APP Context
	public Context ctx;
	//final int PUMP_VAL_ID = 50;
	
    //USB Connection
    public static USBHost mHost;


	@Override
	public IBinder onBind(Intent intent) {
		return mMessengerFromClient.getBinder();
	}

	@SuppressLint({ "Wakelock", "SimpleDateFormat" })
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		Log.log_action(this, TAG, "onCreate", System.currentTimeMillis()/1000, Log.LOG_ACTION_DEBUG);

		// Set up a Notification for this Service
		String ns = Context.NOTIFICATION_SERVICE;
		getSystemService(ns);
		int icon = edu.virginia.dtc.APCservice.R.drawable.icon;
		CharSequence tickerText = "";
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		Context context = getApplicationContext();
		CharSequence contentTitle = "BRM Service";
		CharSequence contentText = "Mitigating Hyperglycemia";
		Intent notificationIntent = new Intent(this, IOMain.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		final int APC_ID = 3;
		startForeground(APC_ID, notification);

		// Keep the CPU running even after the screen dims
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		wl.acquire();

		//Set up arrays
		setupAlgorithmArrays();

		//Create BodyMedia database

		//Set context
		ctx = this.getApplicationContext();

		//Init others
		dArgs = new ArrayList<Map<String, String>>();

		//Create the sensor manager
		sManager = new SensorsManager(ctx);
		insulinManager = new SubBolusCreator( sManager.dbManager);
		
		//Create the server connector
		iitConnector = new IITServerConnector( ctx, sManager.dbManager);

		
		//Start usb host
        startUSBConnection();


	}

	@SuppressLint("HandlerLeak")
	@Override
	public void onDestroy() {
		Debug.i(TAG, "onDestroy", "");
		Log.log_action(this, TAG, "onDestroy", System.currentTimeMillis()/1000, Log.LOG_ACTION_DEBUG);
	}

	@SuppressLint("HandlerLeak")
	class IncomingHMSHandler extends Handler {
		final String FUNC_TAG = "IncomingHMSHandler";

		@SuppressLint("SimpleDateFormat")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Controllers.APC_SERVICE_CMD_START_SERVICE:	
				Debug.i(TAG, FUNC_TAG, "APC_SERVICE_CMD_START_SERVICE");
				mMessengerToClient = msg.replyTo;

				// Log the parameters for IO testing
				if (Params.getBoolean(getContentResolver(), "enableIO", false)) {
					Bundle b = new Bundle();
					b.putString(	"description", 
							" SRC: DIAS_SERVICE"+
									" DEST: APC"+
									" -"+FUNC_TAG+"-"+
									" APC_SERVICE_CMD_START_SERVICE"
							);
					Event.addEvent(getApplicationContext(), Event.EVENT_SYSTEM_IO_TEST, Event.makeJsonString(b), Event.SET_LOG);
				}
				break;
			case Controllers.APC_SERVICE_CMD_CALCULATE_STATE:
				Debug.i(TAG, FUNC_TAG, "APC_SERVICE_CMD_CALCULATE_STATE");
				Bundle paramBundle = msg.getData();
				boolean asynchronous = paramBundle.getBoolean("asynchronous");		//Flag to indicate synchronous (every 5 minutes) or asynchronous (meal entry)
				int DIAS_STATE = paramBundle.getInt("DIAS_STATE", 0);				//Current DiAs State at time of call

				// Log the parameters for IO testing
				if (Params.getBoolean(getContentResolver(), "enableIO", false)) {
					Bundle b = new Bundle();
					b.putString(	"description", 
							" SRC: DIAS_SERVICE"+
									" DEST: APC"+
									" -"+FUNC_TAG+"-"+
									" APC_SERVICE_CMD_CALCULATE_STATE"+
									" Async: "+asynchronous+
									" DIAS_STATE: "+DIAS_STATE
							);
					Event.addEvent(getApplicationContext(), Event.EVENT_SYSTEM_IO_TEST, Event.makeJsonString(b), Event.SET_LOG);
				}

				double correction = 0.0, diff_rate = 0.0;
				boolean new_rate = false;



				// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				// Closed Loop
				// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				if (DIAS_STATE == State.DIAS_STATE_CLOSED_LOOP) {
					Debug.i(TAG, FUNC_TAG, "!!!! CLOSE LOOP");



					// Only run this in synchronous mode (asynchronous is for meals)
					if (!asynchronous) {
						Debug.i(TAG, FUNC_TAG, "Synchronous call...");

						Subject subject = new Subject();
						//dbManager = new DevitesDatabaseManager(ctx, "IITdb.db");

						if(subject.read(getApplicationContext())) {


							//Reset arguments table:
							dArgs = new ArrayList<Map<String, String>>();


							/* **********************************************
							 * Read data from University of Virginia Database
							 * Get data and send to IIT
							 * ************************************************/

							/*Toast toast = Toast.makeText(ctx, "Exercise"+Biometrics.EXERCISE_SENSOR_URI, Toast.LENGTH_LONG);
							toast.show();
							toast = Toast.makeText(ctx, "All uri"+Biometrics.ALL_URI ,Toast.LENGTH_LONG);
							toast.show();
							toast = Toast.makeText(ctx, "All uri",Toast.LENGTH_LONG);
							toast.show();*/

							//READ CGM FROM DEXCOM
							//First include the sensor's reading
							Map<String, String> dTable = new HashMap<String, String>();
							dTable = sManager.readDiASCGMTable(ctx, cgmArray);
							double cgm = cgmArray.get(cgmArray.size()-1);

							dArgs.add(dTable);

							//TODO Send dexcom values to IIT
							//String jToSend ="";
							if (dArgs !=null && dArgs.size()> 0){
								//Add to the not synchronized values
								String jToSend =IITServerConnector.convertToJSON(dArgs);
								//notSynchValues.add(jToSend);
								//Send to IIT
								iitConnector.sendToIIT(jToSend, IITServerConnector.IIT_SERVER_UPDATE_VALUES_URL);
							}else
								Debug.i(TAG, FUNC_TAG, "No values to send to IIT");

							//TODO Prepare data to be send via USB
							
							//Prepare data for exercise
							dArgs = new ArrayList<Map<String, String>>();

							//READ ACTIVITY FROM EXERCISE SENSOR: ZEPHYR
							//Get Zephyr values
							//Zephyr values
							//First include the exercise values 
							dArgs = sManager.readDiASExerciseTable(ctx, zephyrArray);

							//TODO Send Bioharness values to IIT server if there is something to send
							if (dArgs !=null && dArgs.size()> 0){
								//Add to the not syncrhonized values
								String jToSend =IITServerConnector.convertToJSON(dArgs);
								//notSynchValues.add(jToSend);
								//Send to IIT
								iitConnector.sendToIIT(jToSend, IITServerConnector.IIT_SERVER_UPDATE_VALUES_URL);
							}
							else
								Debug.i(TAG, FUNC_TAG, "No values to send to IIT");
							//Prepare data for next sensor
							dArgs = new ArrayList<Map<String, String>>();

							/* ***********************************************
							 *Read BodyMedia values
							 * ***********************************************/

							//Debug how to read a database:
							/*DevitesDatabaseManager dbManager = new DevitesDatabaseManager(ctx, "dbDebugging");
							dbManager.createTable("tablaTrial",new ArrayList<String>(){{add("name"); add("whatever");}});
							List<String> readFromTable(String table, String column)*/

							//Read database:
							/*DevitesDatabaseManager dbManager = new DevitesDatabaseManager(ctx, "dbSensors.db");
							List<String> readVal = dbManager.readFromTable(DevitesDatabaseManager.TABLE_NAME_BM, "position");
							Toast.makeText(ctx, "Read service database: "+ readVal.size() , Toast.LENGTH_SHORT).show();
							 */


							//sManager.readIITDatabaseTable(dArgs, _TABLE_NAME_BM, bodymediaArray);

							//Get all samples not synchronized
							 /*List<Map<String, String>> readValues = dbManager.readFromTableAllColumns(DevitesDatabaseManager.TABLE_NAME_BM, BODYMDIA_TABLE_NAME);

							 //Extract the necessary values for the algorithm and to send
							 for (Map<String, String> sample : readValues){

							 }*/

							//TODO Send all values to IIT server if there is something to send
							/*if (dArgs !=null && dArgs.size()> 0){
								//Add to the not syncrhonized values
								String jToSend =iitConnector.convertToJSON(dArgs);
								notSynchValues.add(jToSend);
								//Send to IIT
								iitConnector.sendToIIT(jToSend, IITServerConnector.IIT_SERVER_UPDATE_VALUES_URL);
							}else
								Debug.i(TAG, FUNC_TAG, "No values to send to IIT");*/
							//Prepare data for next sensor
							//dArgs = new ArrayList<Map<String, String>>();

							
							/* ***********************************************
							 * TODO Read Empatica values
							 * ***********************************************/
							//1. Get data to be send via USB to laptop algorithm
							//TODO Prepare data to be sent via USB
							//dArgs = sManager.read_lastSamples_IITDatabaseTable(SensorsManager._EMPATICA_TABLE_NAME, false);
							//dArgs = new ArrayList<Map<String, String>>();

							
							//2. Update IIT SERVER
							//Get all samples not updated 
							dArgs = sManager.read_lastSamples_IITDatabaseTable(SensorsManager._EMPATICA_TABLE_NAME, true);
														 

							// Send all values to IIT server if there is something to send
							if (dArgs !=null && dArgs.size()> 0){
								//Add to the not synchronized values
								String jToSend =IITServerConnector.convertToJSON(dArgs);
								//notSynchValues.add(jToSend);
								//Send to IIT
								iitConnector.sendToIIT(jToSend, IITServerConnector.IIT_SERVER_UPDATE_VALUES_URL);
							}else
								Debug.i(TAG, FUNC_TAG, "No values to send to IIT");
							
							//Prepare data for next sensor
							dArgs = new ArrayList<Map<String, String>>();


							/* ***********************************************
							 * Implement the insulin Algorithm 
							 * Needed arguments:
							 * 		- CGM
							 * 		- Heart Rate
							 * 		- BodyMedia values
							 * ***********************************************
							 * Use "correction" to update the value of the pump
							 * correction = insulin bolus
							 * ***********************************************/
							
							/* ********************************************
							 * SIMPLE ALGORITHM AND INSULIN CORRECTION
							 */
							/*if (cgm>200)
								correction = 2.11;
							else if (cgm>100)
								correction = 0.5;
							else 
								correction =0;*/
							/*if(HR < 80 && cgm > 150)
								correction = 10.2;
							else if(HR <80 && cgm >100)
								correction = 6.4;
							else if (HR < 80 && cgm > 80)
								correction = 4;
							else if (HR < 80 && cgm < 80)
								correction = 0.5;
							else 
								correction =0.1;

							//}
							
							/* ********************************************
							 *  ALGORITHM - USB to LAPTOP
							 */

							//TODO Call algorithm - FROM USB Connection
							//correction = 0;
							//correction = AlgorithmManager.runAlgorithm(cgmArray.get(cgmArray.size()-1) ,getArrayFixed(bodymediaArray.get(BodyMediaMatrix._EE)), getArrayFixed(bodymediaArray.get(BodyMediaMatrix._GSR)), getArrayFixed(bodymediaArray.get(BodyMediaMatrix._ACT)), getArrayFixed(bodymediaArray.get(BodyMediaMatrix._SLEEP)));
							if (mHost.connected){
								//SEND READ READY:
								mHost.sendUSBmessage(USBHost._PHONE_READY);
								
								//Wait for bolus response or something else
								correction = waitForAlgorithmResponse();

							}else{
								//TODO USB Alert to be displayed
								
								//Run reconnect Thread:
								startUSBConnection();
							}
							//Reset arrays




						}
					}
				}

				// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				// All other modes...
				// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				else {
					Debug.i(TAG, FUNC_TAG, "DiAs State: "+State.stateToString(DIAS_STATE));

					//Defaults are all zero and false
					correction = diff_rate = 0.0;
					new_rate = false;
				}

				//****************************************************************************************
				//
				//****************************************************************************************
				//***********************
				// Calculate traffic lights here!!!
				//***********************
				int hypoLight = Safety.GREEN_LIGHT;
				int hyperLight = Safety.GREEN_LIGHT;
				Cursor c = getContentResolver().query(Biometrics.CGM_URI, null, null, null, null);
				if(c != null) {
					if(c.moveToLast()) {
						int cgm_value = (int)c.getDouble(c.getColumnIndex("cgm"));

						if (cgm_value <= 70) {
							hypoLight = Safety.RED_LIGHT;
							hyperLight = Safety.GREEN_LIGHT;
						} else if (cgm_value <= 90) {
							hypoLight = Safety.YELLOW_LIGHT;
							hyperLight = Safety.GREEN_LIGHT;
						} else if (cgm_value < 250) {
							hypoLight = Safety.GREEN_LIGHT;
							hyperLight = Safety.GREEN_LIGHT;
						} else if (cgm_value < 300) {
							hypoLight = Safety.GREEN_LIGHT;
							hyperLight = Safety.YELLOW_LIGHT;
						} else {
							hypoLight = Safety.GREEN_LIGHT;
							hyperLight = Safety.RED_LIGHT;
						}
					}
				}
				c.close();

				Bundle  responseBundle;
				//Message response;


				//response = Message.obtain(null, APC_PROCESSING_STATE_NORMAL, 0, 0);
				responseBundle = new Bundle();
				responseBundle.putBoolean("doesBolus", false);
				responseBundle.putBoolean("doesRate", true);
				responseBundle.putBoolean("doesCredit", false);
				responseBundle.putDouble("recommended_bolus", 0.0);
				responseBundle.putDouble("creditRequest", 0.0);
				responseBundle.putDouble("spendRequest", 0.0);
				responseBundle.putBoolean("new_differential_rate", false);
				responseBundle.putDouble("differential_basal_rate", 0.0);
				responseBundle.putDouble("IOB", 0.0);
				responseBundle.putBoolean("extendedBolus", false);
				responseBundle.putDouble("extendedBolusMealInsulin", 0.0);
				responseBundle.putDouble("extendedBolusCorrInsulin", 0.0);
				responseBundle.putInt("stoplight", hypoLight);
				responseBundle.putInt("stoplight2", hyperLight);

				// Log the parameters for IO testing
				if (Params.getBoolean(getContentResolver(), "enableIO", false)) {
					Bundle b = new Bundle();
					b.putString(	"description", "(APCservice) >> DiAsService, IO_TEST"+", "+FUNC_TAG+", "+
									"APC_PROCESSING_STATE_NORMAL"+", "+
									"doesBolus="+responseBundle.getBoolean("doesBolus")+", "+
									"doesRate="+responseBundle.getBoolean("doesRate")+", "+
									"doesCredit="+responseBundle.getBoolean("doesCredit")+", "+
									"recommended_bolus="+responseBundle.getDouble("recommended_bolus")+", "+
									"creditRequest="+responseBundle.getDouble("creditRequest")+", "+
									"spendRequest="+responseBundle.getDouble("spendRequest")+", "+
									"new_differential_rate="+responseBundle.getBoolean("new_differential_rate")+", "+
									"differential_basal_rate="+responseBundle.getDouble("differential_basal_rate")+", "+
									"IOB="+responseBundle.getDouble("IOB")+", "+
									"stoplight="+responseBundle.getInt("stoplight")+", "+
									"stoplight2="+responseBundle.getInt("stoplight2")+", "+
									"extendedBolus="+responseBundle.getDouble("extendedBolus")+", "+
									"extendedBolusMealInsulin="+responseBundle.getDouble("extendedBolusMealInsulin")+", "+
									"extendedBolusCorrInsulin="+responseBundle.getDouble("extendedBolusCorrInsulin")
					);
					Event.addEvent(getApplicationContext(), Event.EVENT_SYSTEM_IO_TEST, Event.makeJsonString(b), Event.SET_LOG);
				}
				responseBundle.putBoolean("asynchronous", asynchronous);

				// Send response to DiAsService
				/*response.setData(responseBundle);
				try {
					mMessengerToClient.send(response);
				}
				catch (RemoteException e) {
					e.printStackTrace();
				}*/
				//************************************************************************************************ 
				// Build the message to respond to DIAS_SERVICE
				//************************************************************************************************
				//(double correction, boolean new_rate, double diff_rate, boolean asynchronous, ArrayList<Map<String, String>> args, IITServerConnector connector, String url, ContentResolver cr, Context ctx){

				//*************************************************
				// BOLUS
				// ************************************************
				//Prepare data for insulin bolus
				dArgs = new ArrayList<Map<String, String>>();
				//Process sub boluses
				dArgs = insulinManager.handleBolusValue( correction,   asynchronous, getContentResolver(), ctx);

				
				//TODO Send all bolus values to IIT server if there is something to send
				if (dArgs !=null && dArgs.size()> 0){
					Debug.i("Subbolus", "calculating", "Sending boluses to IIT");

					//Add to the not syncrhonized values
					String jToSend =IITServerConnector.convertToJSON(dArgs);
					//Send to IIT
					iitConnector.sendToIIT(jToSend, IITServerConnector.IIT_SERVER_UPDATE_VALUES_URL);
				}else
					Debug.i("Subbolus", "calculating", "No values to send to IIT");

				// Send values that were not correctly updated on the server
				/*for (String jsonToSend: notSynchValues){
					iitConnector.sendToIIT(jsonToSend, IITServerConnector.IIT_SERVER_UPDATE_VALUES_URL);
				}*/

				//*************************************************
				// BASAL
				// ************************************************
				//Prepare data for insulin basal
				/*dArgs = new ArrayList<Map<String, String>>();
				//TODO - Nothing happens now : Process basal rate
				//TEST WITH BASAL RATE = 1
				dArgs = SubBolusCreator.handleBasalRateValue( true, 1,   asynchronous, getContentResolver(), ctx);

				//Send all basal values to IIT server if there is something to send
				if (dArgs !=null && dArgs.size()> 0){
					//Add to the not synchronized values
					String jToSend =IITServerConnector.convertToJSON(dArgs);
					//notSynchValues.add(jToSend);
					//Send to IIT
					iitConnector.sendToIIT(jToSend, IITServerConnector.IIT_SERVER_UPDATE_VALUES_URL);
				}else
					Debug.i("Basal", "calculating", "No values to send to IIT");*/

				//Send values that were not correctly updated on the server
				/*for (String jsonToSend: notSynchValues){
					iitConnector.sendToIIT(jsonToSend, IITServerConnector.IIT_SERVER_URL);
				}*/


				//***********************************************************************************************

				break;
			default:
				super.handleMessage(msg);
			}
		}
	}


	/**
	 * Send message 
	 * @param m
	 */
	public static void sendResponse(Message m)
	{
		final String FUNC_TAG = "sendMessage";

		if(mMessengerToClient != null)
		{
			try {
				mMessengerToClient.send(m);
			} catch (RemoteException e) {
				Debug.e(TAG, FUNC_TAG, "Error: "+e.getMessage());
			}
		} else
			Debug.e(TAG, FUNC_TAG, "The messenger is null!");
	}
	/**
	 * Convert from a ArrayList to a fixed Array size
	 * @param convert
	 * @return
	 */
	/*private double[] getArrayFixed(ArrayList<Double> convert){
		double[] result = new double[convert.size()];
		int i = 0;
		for (double val: convert){
			result[i] = val;
			i ++;
		}
		return result;

	}*/

	/**
	 * Set up the arrays to be inputted int he algorithm
	 */
	private void setupAlgorithmArrays(){
		//CGM ARRAY
		cgmArray = new ArrayList<Double>();
		//Bodymedia Array
		bodymediaArray = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> updateArray = new ArrayList<Double>();
		bodymediaArray.add(BodyMediaMatrix._EE, updateArray);
		bodymediaArray.add(BodyMediaMatrix._GSR, updateArray);
		bodymediaArray.add(BodyMediaMatrix._ACT, updateArray);
		bodymediaArray.add(BodyMediaMatrix._SLEEP, updateArray);

		//Zephyr
		zephyrArray= new ArrayList<Double>();

	}
	
	/**
	 * Start USB host to connect to laptop
	 */
	public void startUSBConnection(){
		//Disconenct host
		//mHost.disconnectUSBHost();
		
        //Initialize server socket in a new separate thread
        mHost = new USBHost(this, this);
        
        //Start USB Connection
        mHost.intent = new Intent(mHost.ctx, IOMain.class);
        mHost.mHandler=new Handler();
        new Thread(mHost.initializeConnection).start();
        String msg="Attempting to connect…";
        Toast.makeText(mHost.ctx, msg, Toast.LENGTH_LONG).show();
	}
	
	private double waitForAlgorithmResponse(){
		USBReadThread.processing_algorithm = true;
		while(USBReadThread.processing_algorithm ){
			//DO NOTHING
		}
		return USBReadThread.last_read_bolus;
	}


}