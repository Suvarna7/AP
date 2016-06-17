//*********************************************************************************************************************
//  Copyright 2011-2013 by the University of Virginia
//	All Rights Reserved
//
//  Created by Patrick Keith-Hynes
//  Center for Diabetes Technology
//  University of Virginia
//*********************************************************************************************************************
package edu.virginia.dtc.APCservice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


import edu.virginia.dtc.APCservice.DataManagement.IITServerConnector;
import edu.virginia.dtc.APCservice.DataManagement.IITSensorsManager;
import edu.virginia.dtc.APCservice.DataManagement.SubBolusCreator;


import edu.virginia.dtc.SysMan.Controllers;
import edu.virginia.dtc.SysMan.Debug;
import edu.virginia.dtc.SysMan.Event;
import edu.virginia.dtc.SysMan.Log;
import edu.virginia.dtc.SysMan.Params;
import edu.virginia.dtc.SysMan.State;
import Jama.Matrix;

import android.content.Context;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.Message;
import android.os.Handler;
import android.os.RemoteException;
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
	public static final String _TABLE_NAME_BM = "bodymedia";
	public static final String _TABLE_NAME_EM = "empatica";

	//Sensors reader
	private IITSensorsManager sManager;

	//Algorithm Manager
	private AlgorithmManager aManager;

	//Arrays containing sensors info:
	private ArrayList<Double> cgmArray;
	private ArrayList<ArrayList<Double>> bodymediaArray;
	private ArrayList<ArrayList<Double>> empaticaArray;

	private ArrayList<Double> zephyrArray;


	//Other
	//Dias values
	private List<Map<String, String>> dArgs;
	public static List<String> notSynchValues;
	private SimpleDateFormat simpleFormat;

	//APP Context
	public static Context ctx;
	//final int PUMP_VAL_ID = 50;


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
		dArgs = new ArrayList<>();
		notSynchValues = new ArrayList<>();

		simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		//Create the sensor manager
		sManager = new IITSensorsManager(ctx);

		//Create algorithm manager
		aManager = new AlgorithmManager(ctx);




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
						iitConnector = new IITServerConnector(ctx);

						if(subject.read(getApplicationContext())) {


							//Reset arguments table:
							dArgs = new ArrayList<>();


							/* **********************************************
							 * Read data from University of Virginia Server
							 * Get data and send to IIT
							 * ************************************************/

							/*Toast toast = Toast.makeText(ctx, "Exercise"+Biometrics.EXERCISE_SENSOR_URI, Toast.LENGTH_LONG);
							toast.show();
							toast = Toast.makeText(ctx, "All uri"+Biometrics.ALL_URI ,Toast.LENGTH_LONG);
							toast.show();
							toast = Toast.makeText(ctx, "All uri",Toast.LENGTH_LONG);
							toast.show();*/

							//First include the sensor's reading
							Map<String, String> dTable = new HashMap<>();
							long[] dexcomTime = new long [1];
							sManager.readCGMTable(ctx, dTable, dexcomTime, cgmArray);

							//Convert to date format
							long time = dexcomTime[0];
							Debug.i(TAG, FUNC_TAG, "DiAs Read sensors, time: "+time);
							Date parseDate=new Date(time*1000);
							Debug.i(TAG, FUNC_TAG, "DiAs Read sensors, parse date: "+parseDate);
							String finalTime = simpleFormat.format(parseDate);
							Debug.i(TAG, FUNC_TAG, "DiAs Read sensors, date time: "+finalTime);

							//Add table name and time to JSON
							dTable.put("table_name", IITSensorsManager._SERVER_TABLE_NAME_DE);
							dTable.put("last_update", finalTime);
							dArgs.add(dTable);


							//Send dexcom values to IIT
							if (dArgs !=null && dArgs.size()> 0){
								//Add to the not syncrhonized values
								String jToSend =iitConnector.convertToJSON(dArgs);
								notSynchValues.add(jToSend);
								//Send to IIT
								iitConnector.sendToIIT(jToSend, IITServerConnector.IIT_SERVER_UPDATE_VALUES_URL);
							}else
								Debug.i(TAG, FUNC_TAG, "No values to send to IIT");

							//Prepare data for exercise
							dArgs = new ArrayList<>();

							//READ ACTIVITY FROM EXERCISE SENSOR: ZEPHYR
							//Get Zephyr values
							//Zephyr values
							//First include the exercise values 
							long[] zephyrTime = new long [1];
							sManager.readExerciseTable(ctx, dArgs, zephyrTime, simpleFormat, zephyrArray);

							//Send Bioharness values to IIT server if there is something to send
							if (dArgs !=null && dArgs.size()> 0){
								//Add to the not syncrhonized values
								String jToSend =iitConnector.convertToJSON(dArgs);
								notSynchValues.add(jToSend);
								//Send to IIT
								iitConnector.sendToIIT(jToSend, IITServerConnector.IIT_SERVER_UPDATE_VALUES_URL);
							}
							else
								Debug.i(TAG, FUNC_TAG, "No values to send to IIT");
							//Prepare data for next sensor
							dArgs = new ArrayList<>();

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


							//TODO
							sManager.readIITDatabaseTable(dArgs, _TABLE_NAME_BM, bodymediaArray);

							//Get all samples not synchronized
							/*DevitesDatabaseManager dbManager = new DevitesDatabaseManager(ctx, "dbSensors.db");
							 List<Map<String, String>> readValues = dbManager.readFromTableAllColumns(DevitesDatabaseManager.TABLE_NAME_BM, BODYMDIA_TABLE_NAME);

							 //Extract the necessary values for the algorithm and to send
							 for (Map<String, String> sample : readValues){

							 }*/

							//Send all values to IIT server if there is something to send
							if (dArgs !=null && dArgs.size()> 0){
								//Add to the not syncrhonized values
								String jToSend =iitConnector.convertToJSON(dArgs);
								notSynchValues.add(jToSend);
								//Send to IIT
								iitConnector.sendToIIT(jToSend, IITServerConnector.IIT_SERVER_UPDATE_VALUES_URL);
							}else
								Debug.i(TAG, FUNC_TAG, "No values to send to IIT");
							//Prepare data for next sensor
							dArgs = new ArrayList<Map<String, String>>();

							/* ***********************************************
							 *Read Empatica values
							 * ***********************************************/
							//TODO
							sManager.readIITDatabaseTable(dArgs, _TABLE_NAME_EM, empaticaArray);


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

							//DEBUG Algorithm
							//ExercIse - if HR > 80 --> inject 0
							//Amount of insulin depends on cgm value
							/*double cgm;
							correction = 0;
							if (cgmArray.size()>0)
								cgm = cgmArray.get(cgmArray.size() - 1);
							else
								cgm = 50;
							if(cgm <=120)
								correction = 10.4;
							else if(120< cgm && cgm<= 250){
								correction = 10.4;
							}else if (cgm >250){
								correction = 18.2;
							}*/
							//double cgm = 100;
							//double HR = 80;
							/*double HR;
							if (zephyrArray.size()>0)
								HR = zephyrArray.get(zephyrArray.size() - 1);
							else
								HR = 0;*/
							//double HR =
							/*Debug.i(TAG, FUNC_TAG, "Algorithm params: cgm-"+cgm+" hr-"+HR);
							Toast toast1 = Toast.makeText(ctx,  "Algorithm params: cgm-"+cgm+" hr-"+HR, 100);
							toast1.show();*/

							double cgm;
							correction = 0;
							if (cgmArray.size()>0)
								cgm = cgmArray.get(cgmArray.size() - 1);
							else
								cgm = 50;
							Random rand = new Random();
							/*		 Random rand = new Random();
					         int min=90;
							 int max=250;
							    // nextInt is normally exclusive of the top value,
							    // so add 1 to make it inclusive
							    cgm = rand.nextInt((max - min) + 1) + min;*/


							//TODO Debug values to test the algorithm
							double phys_act=0;
							double sleep=0;
							double ee;
							double gsr;
							int minee=3;
							int maxee=15;
							// nextInt is normally exclusive of the top value, so add 1 to make it inclusive
							ee = (rand.nextInt((maxee - minee) + 1) + minee)/10;
							int mingsr=0;
							int maxgsr=7;
							// nextInt is normally exclusive of the top value,  so add 1 to make it inclusive
							gsr = (rand.nextInt((maxgsr - mingsr) + 1) + mingsr)/10;

							/* ********************************************
							 * ALGORITHM AND INSULIN CORRECTION
							 */
							/*if (readVal.size()>0){
								correction = 1.11;
							}else{*/
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

							//}*/

							//TODO Call algorithm 
							correction = 0.5;
							//correction = AlgorithmManager.runAlgorithm(cgmArray.get(cgmArray.size()-1) ,getArrayFixed(bodymediaArray.get(BodyMediaMatrix._EE)), getArrayFixed(bodymediaArray.get(BodyMediaMatrix._GSR)), getArrayFixed(bodymediaArray.get(BodyMediaMatrix._ACT)), getArrayFixed(bodymediaArray.get(BodyMediaMatrix._SLEEP)));
							if (cgmArray.size()>0){
								aManager.testAlgorithmInputs(ctx, cgmArray.get(cgmArray.size()-1) ,getArrayFixed(bodymediaArray.get(BodyMediaMatrix._EE)), getArrayFixed(bodymediaArray.get(BodyMediaMatrix._GSR)), getArrayFixed(bodymediaArray.get(BodyMediaMatrix._ACT)), getArrayFixed(bodymediaArray.get(BodyMediaMatrix._SLEEP)));
								correction = aManager.runAlgorithm(cgmArray.get(cgmArray.size()-1) ,getArrayFixed(bodymediaArray.get(BodyMediaMatrix._EE)), getArrayFixed(bodymediaArray.get(BodyMediaMatrix._GSR)), getArrayFixed(bodymediaArray.get(BodyMediaMatrix._ACT)), getArrayFixed(bodymediaArray.get(BodyMediaMatrix._SLEEP)));
								Matrix res = aManager.testAlgortihm(ctx, cgm, ee, gsr, sleep, phys_act,subject.weight);								
								correction = res.get(0, 0);
								/*diff_rate = Result.get(0, 1);*/

							}else{
								//correction = 4;
							}
							//Reset arrays
							setupAlgorithmArrays();
							

							// (�command�,TempBasal.TEMP_BASAL_START);
							// differential_basal_rate= Result.get(0, 1);
							/*Toast toast1 = Toast.makeText(ctx,  "Algorithm params: bolus-"+result, Toast.LENGTH_LONG);
							toast1.show();*/
							//Reset arrays
							/*cgmArray =  new ArrayList<Double>();
							bodymediaArray = new ArrayList<ArrayList<Double>> ();*/

							//TODO Send result of algorithm to IIT server
							/*Map<String, String> testValue = new HashMap <String, String>();
							testValue. put("result",""+ correction);
							testValue. put("table_name",""+ correction);							
							testValue.put("synchronized", "y");
							testValue.put("last_update", ""+0);
							//Update args
							dArgs.add(testValue);

							//Send all values to IIT server if there is something to send
							if (dArgs !=null && dArgs.size()> 0){
								//Add to the not syncrhonized values
								String jToSend =IITServerConnector.convertToJSON(dArgs);
								notSynchValues.add(jToSend);
								//Send to IIT
								iitConnector.sendToIIT(jToSend, IITServerConnector.IIT_SERVER_URL);
							}else
								Debug.i(TAG, FUNC_TAG, "No values to send to IIT");
							//Prepare data for next sensor
							dArgs = new ArrayList<Map<String, String>>();*/



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

				//************************************************************************************************ 
				// Build the message to respond to DIAS_SERVICE
				//************************************************************************************************
				//(double correction, boolean new_rate, double diff_rate, boolean asynchronous, ArrayList<Map<String, String>> args, IITServerConnector connector, String url, ContentResolver cr, Context ctx){

				//*************************************************
				// BOLUS
				// ************************************************
				//Prepare data for insulin bolus
				dArgs = new ArrayList<Map<String, String>>();
				//Process subboluses
				dArgs = SubBolusCreator.handleBolusValue( correction,   asynchronous, getContentResolver(), ctx);

				//Send all bolus values to IIT server if there is something to send
				if (dArgs !=null && dArgs.size()> 0){
					//Add to the not syncrhonized values
					String jToSend =iitConnector.convertToJSON(dArgs);
					notSynchValues.add(jToSend);
					//Send to IIT
					iitConnector.sendToIIT(jToSend, IITServerConnector.IIT_SERVER_UPDATE_VALUES_URL);
				}else
					Debug.i("Subbolus", "calculating", "No values to send to IIT");

				// Send values that were not correctly updated on the server
				for (String jsonToSend: notSynchValues){
					iitConnector.sendToIIT(jsonToSend, IITServerConnector.IIT_SERVER_UPDATE_VALUES_URL);
				}

				//*************************************************
				// BASAL
				// ************************************************
				//Prepare data for insulin basal
				/*dArgs = new ArrayList<Map<String, String>>();
				//TODO - Nothing happens now : Process basal rate
				dArgs = SubBolusCreator.handleBasalRateValue( true, 1,   asynchronous, getContentResolver(), ctx);

				//Send all basal values to IIT server if there is something to send
				if (dArgs !=null && dArgs.size()> 0){
					//Add to the not syncrhonized values
					String jToSend =IITServerConnector.convertToJSON(dArgs);
					notSynchValues.add(jToSend);
					//Send to IIT
					iitConnector.sendToIIT(jToSend, IITServerConnector.IIT_SERVER_URL);
				}else
					Debug.i("Basal", "calculating", "No values to send to IIT");

				//Send values that were not correctly updated on the server
				for (String jsonToSend: notSynchValues){
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
	private double[] getArrayFixed(ArrayList<Double> convert){
		double[] result = new double[convert.size()];
		int i = 0;
		for (double val: convert){
			result[i] = val;
			i ++;
		}
		return result;

	}

	/**
	 * Set up the arrays to be inputted int he algorithm
	 */
	private void setupAlgorithmArrays(){
		//CGM ARRAY
		cgmArray = new ArrayList<>();
		//Bodymedia Array
		bodymediaArray = new ArrayList<>();
		ArrayList<Double> updateArray = new ArrayList<Double>();
		bodymediaArray.add(BodyMediaMatrix._EE, updateArray);
		bodymediaArray.add(BodyMediaMatrix._GSR, updateArray);
		bodymediaArray.add(BodyMediaMatrix._ACT, updateArray);
		bodymediaArray.add(BodyMediaMatrix._SLEEP, updateArray);

		//Zephyr
		zephyrArray= new ArrayList<>();

	}


}