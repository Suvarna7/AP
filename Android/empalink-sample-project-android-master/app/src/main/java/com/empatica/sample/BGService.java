package com.empatica.sample;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.widget.Toast;
import com.empatica.empalink.EmpaDeviceManager;
import com.empatica.empalink.delegate.EmpaDataDelegate;
import com.empatica.sample.Database.IITDatabaseManager;
import com.empatica.sample.Server.IITServerConnector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * BGService - Background service that will manage:
 *      x Connection with the device
 *      x Writing data to our Database
 * Extends - Service
 * Implements - EmpaDataDelegate
 *
 * @autor Caterina Lazaro
 * @version 2.0 Jun 2016
 */

public class BGService extends Service implements EmpaDataDelegate{

	private boolean tableInit;
	private int index;
    private static MainActivity mActivity;
    public static EmpaDeviceManager deviceManager;
    public static boolean serviceStarted = false;

    //Fields to detect connection lost
    public static boolean startTimer;
    public static Timer notConnectedTimer;
    private static boolean connectionEstablishedFlag;

    //Fields to store received information
    //Arrays:
    private static List<List<String>> receivedData;
    private static List<Map<String, String>> toBeSentData;
    private static String[] xAccelValues;
    private static String[] yAccelValues;
    private static String[] zAccelValues;
    private static String[] gsrValues;
    private static String[] bvpValues;
    private static String[] ibiValues;
    private static String[] temperatureValues;
    private static String[] batteryValues;
    //Flags:
    private static boolean accelerationNotReceived;
    private static boolean gsrNotReceived;
    private static boolean bvpNotReceived;
    private static boolean ibiNotReceived;
    private static boolean temperatureNotReceived;
    private static boolean batteryNotReceived;
    private final static int TOTAL_VARIABLES = 8;
    private static float last_IBI;
    private static float last_GSR;
    private static final float _ZERO_FLOAT = new Float(0.00000000);


    //Fields to verify key
    public static Timer verifyKeyTimer;

    //Sample order
    private static final int CURRENT_SAMPLE = 0;

    //Timestamp
    private static double currentTimeStamp;
    private static double nextTimeStamp;


    //Count all variables received for each timestamp
    private static int receivedSamples;

    //Database
    public static IITDatabaseManager myDB;
    public static final String empaticaTableName= "empatica";
    private static final String[] columnsTable = new String[]{"time_stamp", "Acc_x", "Acc_y", "Acc_z", "GSR", "BVP",
            "IBI", "temperature","battery_level"};


    //IIT Server manager
    IITServerConnector myServerManager;
    private static final String jsonID =  "empaticaJSON";

    public BGService (){  /*Do nothing */ }
    public BGService(MainActivity main) {
        mActivity = main;
    }

    public static void initContext(MainActivity main){
        mActivity = main;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //Toast.makeText(this, "The new Service was Binded", Toast.LENGTH_LONG).show();
        return null;
    }

    @Override
    public void onCreate() {
    	tableInit = false;
    	index = 0;
        //Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG).show();
        //*** Empatica managers
        // Create a new EmpaDeviceManager. Service is both its data  delegate and MainActivity its status delegate.
        deviceManager = new EmpaDeviceManager(mActivity.getApplicationContext(), this, mActivity);
        // Initialize the Device Manager using your API key. You need to have Internet access at this point.
        deviceManager.authenticateWithAPIKey(mActivity.EMPATICA_API_KEY);

        //Set up connection check
        notConnectedTimer = new Timer();
        startTimer = true;
        connectionEstablishedFlag = false;

        //Set up verify KEY
        verifyKeyTimer = new Timer();

        //Current context
        //serviceContxt = this;

        //Initialize data collection
        receivedData = new ArrayList<List<String>>();
        toBeSentData = new ArrayList<Map<String,String>>();
        currentTimeStamp = 0;
        receivedSamples = 0;
        nextTimeStamp = 0;
        initArrayOfValues();

        //Init database fields
        initDatabaseManager(this);

        //ibi
        last_IBI = _ZERO_FLOAT;
        last_GSR = _ZERO_FLOAT;

        //Received flags:
        resetReceivedFlags();

        //Server
        //Initialize server connector
        // IITServerConnector(String jsonID, String writeURL, String readURL, Context ctx)
        myServerManager = new IITServerConnector(jsonID, IITServerConnector.IIT_SERVER_UPDATE_VALUES_URL,
                IITServerConnector.IIT_SERVER_READ_TABLE_URL, myDB, this);

    }


    @Override
    public int  onStartCommand(Intent intent, int flags, int startId){
        // For time consuming an long tasks you can launch a new thread here...
        // Do your Bluetooth Work Here
        serviceStarted = true;
        Toast.makeText(this, " Service Started", Toast.LENGTH_LONG).show();


        //Start connection timer
        //Timer to check the connection
        startPeriodicTimer();

        //TODO Start key verification timer
        startPeriodicVerificationOfKey(MainActivity.STREAMING_TIME);

        //Update values in table
        return 0;

        }

        @Override
        public void onDestroy() {
            Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

            //Stop timer
            notConnectedTimer.cancel();

            verifyKeyTimer. cancel();




        }

    /**********************************
     * Empatica callbacks
     */

    @Override
    public void didReceiveAcceleration(int x, int y, int z, double time_stamp) {
        mActivity.updateLabel(mActivity.accel_xLabel, "" + x);
        mActivity.updateLabel(mActivity.accel_yLabel, "" + y);
        mActivity.updateLabel(mActivity.accel_zLabel, "" + z);

        //Store sample:
        storeNewSample(""+ x + "", xAccelValues, time_stamp);
        storeNewSample("" + y + "", yAccelValues, time_stamp);
        storeNewSample("" + z + "", zAccelValues, time_stamp);

        //Update number of received samples:
        if (accelerationNotReceived){
            receivedSamples ++;
            accelerationNotReceived = false;
        }



    }

    @Override
    public void didReceiveGSR(float gsr, double time_stamp) {
        mActivity.updateLabel(mActivity.edaLabel, "" + gsr);
        //Consider connection stablished when we receive GSR values:
        connectionEstablishedFlag = true;

        if (gsr >  0.000000000) {
            storeNewSample("" + gsr + "", gsrValues, time_stamp);
            last_GSR = gsr;
        }else
           storeNewSample("" + last_GSR + "", gsrValues, time_stamp);

        //Update number of received samples:
        if (gsrNotReceived){
            receivedSamples ++;
            gsrNotReceived = false;
        }


    }

    @Override
    public void didReceiveBVP(float bvp, double time_stamp) {
        mActivity.updateLabel(mActivity.bvpLabel, "" + bvp);

        storeNewSample("" + bvp + "", bvpValues, time_stamp);

        //Update number of received samples:
        if (bvpNotReceived){
            receivedSamples ++;
            bvpNotReceived = false;
        }

    }

    @Override
    public void didReceiveIBI(float ibi, double time_stamp) {
        mActivity.updateLabel(mActivity.ibiLabel, "" + ibi);
        //NOTE: IBI is updated at a slower pace
        if (ibi > _ZERO_FLOAT) {
            last_IBI = ibi;
            storeNewSample("" + ibi + "", ibiValues, time_stamp);

        }
       else {
            storeNewSample("" + last_IBI + "", ibiValues, time_stamp);

        }

        //Update number of received samples:
        if (ibiNotReceived){
            receivedSamples += TOTAL_VARIABLES;
            ibiNotReceived = false;
        }

    }

    @Override
    public void didReceiveTemperature(float temp, double time_stamp) {
        mActivity.updateLabel(mActivity.temperatureLabel, "" + temp);
        if (temp < 0)
            storeNewSample("" + temp + "", temperatureValues, time_stamp);
        else
            storeNewSample("" + temp + "", temperatureValues, time_stamp);

        //Update number of received samples:
        if (temperatureNotReceived){
            receivedSamples ++;
            temperatureNotReceived = false;
        }

    }



    @Override
    public void didReceiveBatteryLevel(float battery, double time_stamp) {

        mActivity.updateLabel(mActivity.batteryLabel, String.format("%.0f %%", battery * 100));
        storeNewSample(""+battery +"", batteryValues, time_stamp);

        //Update number of received samples:
        if (batteryNotReceived){
            receivedSamples ++;
            batteryNotReceived = false;
        }



    }
    /* **************************************
     * TIMER
     */
    private  void startPeriodicTimer() {
        //Restart the counter flag
        startTimer = false;
        //Start a a timer
        notConnectedTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //Store values in database every minute and reset:
                if (storeGlobalListInDatabase()) {
                    //Send saved information to IIT server
                    String jSon =  myServerManager.convertToJSON(toBeSentData);
                    //myServerManager.debugServer("samples");
                    myServerManager.sendToIIT(jSon, IITServerConnector.IIT_SERVER_UPDATE_VALUES_URL);
                    //Reset values:
                    toBeSentData = new ArrayList<Map<String, String>>();
                    receivedData = new ArrayList<List<String>>();
                }

                //DEBUG Table
                //MainActivity.myDB.updateDatabaseTable("debug_table", new ArrayList<>(Arrays.asList(new String[]{"'A'"})), true);


                if (!connectionEstablishedFlag) {
                    //Display the dialog
                    Intent dialogIntent = new Intent(mActivity, ConnectionDialog.class);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(dialogIntent);
                    //Toast.makeText(mActivity, "No connection stablished!", Toast.LENGTH_LONG).show();

                } else {
                    connectionEstablishedFlag = false;
                }

            }
        }, 60000, 60000);
    }

   /**
    * Verify KEY every 25 min
    * @param period_time
    */
    private void startPeriodicVerificationOfKey(long period_time){
        //Create and start periodic timer
        //Start a a timer
        verifyKeyTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //Verify key if there is internet available
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                System.out.println ("Timer went off!!!");

                if (cm.getActiveNetworkInfo() != null && deviceManager !=null) {
                    //Verify key
                    deviceManager.authenticateWithAPIKey(mActivity.EMPATICA_API_KEY);
                    System.out.println ("Verifying key !!!");
                }


            }
        }, 60000, period_time);

        // Check whether there is internet connection

    }

    /* **********************************************
     * Store received data in local arrays
     */

    private static void  initArrayOfValues(){
        xAccelValues = new String[]{"0", "0"};
        yAccelValues = new String[]{"0", "0"};
        zAccelValues = new String[]{"0", "0"};
        gsrValues = new String[]{"0", "0"};
        bvpValues = new String[]{"0", "0"};
        ibiValues = new String[]{"0", "0"};
        temperatureValues = new String[]{"0", "0"};
        batteryValues = new String[]{"0", "0"};


    }

    private static void resetArraysNextSample(){
        //Next sample should become current sample
       /* xAccelValues[CURRENT_SAMPLE] = xAccelValues[NEXT_SAMPLE];
        yAccelValues[CURRENT_SAMPLE] = yAccelValues[NEXT_SAMPLE];
        zAccelValues[CURRENT_SAMPLE] = zAccelValues[NEXT_SAMPLE];
        gsrValues[CURRENT_SAMPLE] =  gsrValues[NEXT_SAMPLE];
        bvpValues[CURRENT_SAMPLE]  = bvpValues[NEXT_SAMPLE];
        ibiValues[CURRENT_SAMPLE]  = ibiValues[NEXT_SAMPLE];
        temperatureValues[CURRENT_SAMPLE] = temperatureValues[NEXT_SAMPLE];
        batteryValues[CURRENT_SAMPLE] =  batteryValues[NEXT_SAMPLE];*/

    }

    private void storeNewSample(String value, String[] array, double time_stamp){

        //Check here is no null value:
        if (value.contains("null"))
            value = "0";

        //Analyze time_stamp vs currentTimeStamp
        if (currentTimeStamp == 0.0)
            //Initialize
            currentTimeStamp = time_stamp;
        else if (currentTimeStamp == time_stamp) {
            //Current sample
            array[CURRENT_SAMPLE] = value;
        }
        else if (currentTimeStamp < time_stamp) {
            //Next sample
            array[CURRENT_SAMPLE] = value;
            currentTimeStamp = time_stamp;
        }
        else if (currentTimeStamp > time_stamp) {
            //Previous sample
            array[CURRENT_SAMPLE] = value;

        }

        //Analyze # of received samples
        if (receivedSamples == TOTAL_VARIABLES + 4 || receivedSamples == TOTAL_VARIABLES + 5){
            //Store samples as part of variables string
            saveInGlobalList(CURRENT_SAMPLE, currentTimeStamp);
            //Re-start counter
            receivedSamples = 0;
            currentTimeStamp =  nextTimeStamp;
            //Move next samples to current sample position
            //resetArraysNextSample();
            resetReceivedFlags();
        }else if (receivedSamples == TOTAL_VARIABLES  - 4 || receivedSamples == TOTAL_VARIABLES -3){
            //Did not received an IBI update yet: update Array with last sample
            ibiValues[0] = ""+last_IBI+"";

            //Store samples as part of variables string
            saveInGlobalList(CURRENT_SAMPLE, currentTimeStamp);
            //Re-start counter
            receivedSamples = 0;
            currentTimeStamp =  nextTimeStamp;
            //Move next samples to current sample position
            //resetArraysNextSample();
            resetReceivedFlags();


        }


    }

    private static void saveInGlobalList(int sample, double time_stamp){
        if (sample == CURRENT_SAMPLE ){
            List<String> tempList= new ArrayList<>();
            Map<String, String> tempMap = new HashMap<String, String>();

            // Add to the list string:
            //Prepare time_stamp:
            double time = time_stamp * 1000;
            /// DateTimeInstance dateTime = new DateTimeInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss", Locale.getDefault());
            String formatTimeStamp = dateFormat.format(time);
            //formatTimeStamp = ""+ formatTimeStamp.substring(0, formatTimeStamp.length() ) +"";
            tempList.add("'"+ formatTimeStamp.substring(0, formatTimeStamp.length() ) +"'");
            tempMap.put(columnsTable[0], formatTimeStamp);
            //Add the other values
            tempList.add(xAccelValues[sample]);
            tempMap.put(columnsTable[1], xAccelValues[sample]);

            tempList.add(yAccelValues[sample]);
            tempMap.put(columnsTable[2], yAccelValues[sample]);

            tempList.add(zAccelValues[sample]);
            tempMap.put(columnsTable[3], zAccelValues[sample]);

            tempList.add(gsrValues[sample]);
            tempMap.put(columnsTable[4], gsrValues[sample]);

            tempList.add(bvpValues[sample]);
            tempMap.put(columnsTable[5], bvpValues[sample]);

            tempList.add(ibiValues[sample]);
            tempMap.put(columnsTable[6], ibiValues[sample]);

            tempList.add(temperatureValues[sample]);
            tempMap.put(columnsTable[7], temperatureValues[sample]);

            tempList.add(batteryValues[sample]);
            tempMap.put(columnsTable[8], batteryValues[sample]);

            //tempList.add(updated);

            //Add the the global list
            receivedData.add(tempList);


            tempMap.put("synchronized", "n");
            tempMap.put("table_name", empaticaTableName);
            toBeSentData.add(tempMap);


        }else
            //Error with the sample number
            System.out.println("Sample number should be current or next only!");

    }

    private void resetReceivedFlags(){
        accelerationNotReceived = true;
        gsrNotReceived = true;
        bvpNotReceived =  true;
        ibiNotReceived = true;
        temperatureNotReceived = true;
        batteryNotReceived = true;
    }

    /* **********************************************
     * DATABASE METHODS
     */

    /**
     * Initialize database paraemeters: creates database instance and a table for empatica
     * @param context app context
     */
    private static void initDatabaseManager(Context context){
        //Create database object
        myDB = new IITDatabaseManager(context);

        //DELETE PREVIOUS TABLE:
        //myDB.updateDatabaseTable (empaticaTableName, null, false);

        //Create a table for Empatica
        myDB.createTable(empaticaTableName, columnsTable[0], new ArrayList<>(Arrays.asList(columnsTable)));

        //DEBUG TABLE:
        //myDB.createTable("debug_table", new ArrayList<>(Arrays.asList(new String[]{"column"})));


    }



    private static boolean storeGlobalListInDatabase(){

        for (int i = 0; i < receivedData.size(); i ++)
            myDB.updateDatabaseTable(empaticaTableName, receivedData.get(i), true);

        return true;


    }







}
