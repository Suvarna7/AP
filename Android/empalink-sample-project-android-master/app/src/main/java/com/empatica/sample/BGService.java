package com.empatica.sample;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;


import com.empatica.empalink.EmpaDeviceManager;
import com.empatica.empalink.delegate.EmpaDataDelegate;
import com.empatica.sample.Database.Database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    private static List<List<String>> receivedData;
    private static String[] xAccelValues;
    private static String[] yAccelValues;
    private static String[] zAccelValues;
    private static String[] gsrValues;
    private static String[] bvpValues;
    private static String[] ibiValues;
    private static String[] temperatureValues;
    private static String[] batteryValues;
    private final static int TOTAL_VARIABLES = 8;

    //Sample order
    private static final int CURRENT_SAMPLE = 0;
    private static final int NEXT_SAMPLE = 1;

    //Timestamp
    private static double currentTimeStamp;
    private static double nextTimeStamp;


    //Count all variables received for each timestamp
    private static int receivedSamples;
    private static int receivedNextSamples;

    //Database
    public static Database myDB;
    public static final String empaticaTableName= "empatica";
    private static final String[] columnsTable = new String[]{"time_stamp", "Acc_x", "Acc_y", "Acc_z", "GSR", "BVP",
            "IBI", "temperature","battery_level"};




    public BGService (){  /*Do nothing */ }
    public BGService(MainActivity main) {
        mActivity = main;
    }

    public static void initContext(MainActivity main){
        mActivity = main;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "The new Service was Binded", Toast.LENGTH_LONG).show();
        return null;
    }

    @Override
    public void onCreate() {
    	tableInit = false;
    	index = 0;
        Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG).show();
        //*** Empatica managers
        // Create a new EmpaDeviceManager. Service is both its data  delegate and MainActivity its status delegate.
        deviceManager = new EmpaDeviceManager(mActivity.getApplicationContext(), this, mActivity);
        // Initialize the Device Manager using your API key. You need to have Internet access at this point.
        deviceManager.authenticateWithAPIKey(mActivity.EMPATICA_API_KEY);

        //Set up connection check
        notConnectedTimer = new Timer();
        startTimer = true;
        connectionEstablishedFlag = false;

        //Current context
        //serviceContxt = this;

        //Initialize data collection
        receivedData = new ArrayList<List<String>>();
        currentTimeStamp = 0;
        receivedSamples = 0;
        nextTimeStamp = 0;
        receivedNextSamples = 0;
        initArrayOfValues();

        //Init database fields
        initDatabaseManager(this);

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


        //Update values in table
        return 0;

        }

        @Override
        public void onDestroy() {
            Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

            //Stop timer
            notConnectedTimer.cancel();



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
        storeNewSample("'"+x + "'", xAccelValues, time_stamp);
        storeNewSample("'" + y + "'", yAccelValues, time_stamp);
        storeNewSample("'" + z + "'", zAccelValues, time_stamp);



    }

    @Override
    public void didReceiveGSR(float gsr, double time_stamp) {
        mActivity.updateLabel(mActivity.edaLabel, "" + gsr);
        //Consider connection stablished when we receive GSR values:
        connectionEstablishedFlag = true;

        storeNewSample("'" + gsr + "'", gsrValues, time_stamp);

    }

    @Override
    public void didReceiveBVP(float bvp, double time_stamp) {
        mActivity.updateLabel(mActivity.bvpLabel, "" + bvp);

        storeNewSample("'"+bvp + "'", bvpValues, time_stamp);
    }

    @Override
    public void didReceiveIBI(float ibi, double time_stamp) {
        mActivity.updateLabel(mActivity.ibiLabel, "" + ibi);
        if (ibi < 0)
            storeNewSample("'"+0+"'", ibiValues, time_stamp);
        else
            storeNewSample("'"+ibi+"'", ibiValues, time_stamp);
    }

    @Override
    public void didReceiveTemperature(float temp, double time_stamp) {
        mActivity.updateLabel(mActivity.temperatureLabel, "" + temp);
        if (temp < 0)
            storeNewSample("'" + temp + "'", temperatureValues, time_stamp);
        else
            storeNewSample("'" + temp + "'", temperatureValues, time_stamp);
    }



    @Override
    public void didReceiveBatteryLevel(float battery, double time_stamp) {
        mActivity.updateLabel(mActivity.batteryLabel, String.format("%.0f %%", battery * 100));
        storeNewSample("'"+battery +"'", batteryValues, time_stamp);


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
                    //receivedData = new ArrayList<List<String>>();
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

    /* **********************************************
     * Store received data in local arrays
     */

    private static void  initArrayOfValues(){
        xAccelValues = new String[]{"'0'", "'0'"};
        yAccelValues = new String[]{"'0'", "'0'"};
        zAccelValues = new String[]{"'0'", "'0'"};
        gsrValues = new String[]{"'0'", "'0'"};
        bvpValues = new String[]{"'0'", "'0'"};
        ibiValues = new String[]{"'0'", "'0'"};
        temperatureValues = new String[]{"'0'", "'0'"};
        batteryValues = new String[]{"'0'", "'0'"};


    }

    private static void resetArraysNextSample(){
        //Next sample should become current sample
        xAccelValues[CURRENT_SAMPLE] = xAccelValues[NEXT_SAMPLE];
        yAccelValues[CURRENT_SAMPLE] = yAccelValues[NEXT_SAMPLE];
        zAccelValues[CURRENT_SAMPLE] = zAccelValues[NEXT_SAMPLE];
        gsrValues[CURRENT_SAMPLE] =  gsrValues[NEXT_SAMPLE];
        bvpValues[CURRENT_SAMPLE]  = bvpValues[NEXT_SAMPLE];
        ibiValues[CURRENT_SAMPLE]  = ibiValues[NEXT_SAMPLE];
        temperatureValues[CURRENT_SAMPLE] = temperatureValues[NEXT_SAMPLE];
        batteryValues[CURRENT_SAMPLE] =  batteryValues[NEXT_SAMPLE];

    }

    private void storeNewSample(String value, String[] array, double time_stamp){
        //Check here is no null value:
        if (value.contains("null"))
            value = "'0'";
        //Analyze time_stamp vs currentTimeStamp
        if (currentTimeStamp == 0.0)
            //Initialize
            currentTimeStamp = time_stamp;
        else if (currentTimeStamp == time_stamp) {
            //Current sample
            array[CURRENT_SAMPLE] = value;
            receivedSamples ++;
        }
        else if (currentTimeStamp < time_stamp) {
            //Next sample
            array[NEXT_SAMPLE] = value;
            receivedNextSamples ++;
            nextTimeStamp = time_stamp;
        }
        else {
            //Previous sample
        }

        //Analyze # of received samples
        if (receivedSamples == TOTAL_VARIABLES){

            //Store samples as part of variables string
            saveInGlobalList(CURRENT_SAMPLE, currentTimeStamp);
            //Re-start counter
            receivedSamples = receivedNextSamples;
            receivedNextSamples = 0;
            currentTimeStamp =  nextTimeStamp;
            //Move next samples to current sample position
            resetArraysNextSample();
        } else if (receivedNextSamples == TOTAL_VARIABLES){
            //We have missed one or more samples of current time stamp, but next sample is ready
            //Store next samples
            saveInGlobalList(NEXT_SAMPLE, nextTimeStamp);

            //Reset counters
            receivedSamples = 0;
            receivedNextSamples = 0;
            currentTimeStamp = 0;

        }


    }

    private static void saveInGlobalList(int sample, double time_stamp){
        if (sample == CURRENT_SAMPLE || sample == NEXT_SAMPLE){
            List<String> tempList= new ArrayList<>();

            // Add to the list string:
            //Prepare time_stamp:
            double time = time_stamp * 1000;
            /// DateTimeInstance dateTime = new DateTimeInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss", Locale.getDefault());
            String formatTimeStamp = dateFormat.format(time);
            System.out.println("Timestamp: "+ formatTimeStamp);
            tempList.add("'"+ formatTimeStamp.substring(0, formatTimeStamp.length() - 4) +"'");
            //Add the other values
            tempList.add(xAccelValues[sample]);
            tempList.add(yAccelValues[sample]);
            tempList.add(zAccelValues[sample]);
            tempList.add(gsrValues[sample]);
            tempList.add(bvpValues[sample]);
            tempList.add(ibiValues[sample]);
            tempList.add(temperatureValues[sample]);
            tempList.add(batteryValues[sample]);
            //tempList.add(updated);

            //Add the the global list
            receivedData.add(tempList);


        }else
            //Error with the sample number
            System.out.println("Sample number should be current or next only!");

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
        myDB = new Database(context);

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
