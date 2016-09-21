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
import com.empatica.sample.Database.ThreadSafeArrayList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
   // private static List<ThreadSafeArrayList<String>> receivedData;
    //public static List<String> usbReadyData;
    private static String[] xAccelValues;
    private static String[] yAccelValues;
    private static String[] zAccelValues;
    private static String[] gsrValues;
    private static String[] bvpValues;
    private static String[] ibiValues;
    private static String[] hrValues;
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
    private static float last_HR;
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
    public static final String empaticaMilTableName= "empatica";
    public static final String empaticaSecTableName= "empatica_seconds";

    public static final String[] columnsTable = new String[]{"time_stamp", "Acc_x", "Acc_y", "Acc_z", "GSR", "BVP",
            "IBI", "HR", "temperature","battery_level"};
    public static final int _TIME_INDEX = 0;
    private static int STORING_AMOUNT = 1000;
    private static int storing_counter;






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
        if (mActivity != null) {
            deviceManager = new EmpaDeviceManager(mActivity.getApplicationContext(), this, mActivity);
            // Initialize the Device Manager using your API key. You need to have Internet access at this point.
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null) {
                deviceManager.authenticateWithAPIKey(mActivity.EMPATICA_API_KEY);
                mActivity.updateLabel(mActivity.internet_conn, "INTERNET");
            } else
                //No itnernet connection - WE CAN NOT USE THE APP
                mActivity.updateLabel(mActivity.internet_conn, "NO INTERNET");
        }


        //Set up connection check
        notConnectedTimer = new Timer();
        startTimer = true;
        connectionEstablishedFlag = false;

        //Set up verify KEY
        verifyKeyTimer = new Timer();

        //Current context
        //serviceContxt = this;

        //Initialize data collection
        //receivedData = new ArrayList<ThreadSafeArrayList<String>>();
        //usbReadyData = new ArrayList<String>();
        currentTimeStamp = 0;
        receivedSamples = 0;
        nextTimeStamp = 0;
        initArrayOfValues();

        //Init database fields
        initDatabaseManager(this);
        storing_counter = 0;

        //ibi
        last_IBI = _ZERO_FLOAT;
        last_GSR = _ZERO_FLOAT;
        last_HR = _ZERO_FLOAT;

        //Received flags:
        resetReceivedFlags();






    }


    @Override
    public int  onStartCommand(Intent intent, int flags, int startId){
        // For time consuming an long tasks you can launch a new thread here...
        // Do your Bluetooth Work Here
        serviceStarted = true;
        Toast.makeText(this, " Service Started", Toast.LENGTH_LONG).show();


        //Start connection timer
        //Timer to check the connection
        startConnectionTimer();

        //TODO Start key verification timer
        startPeriodicVerificationOfKey(MainActivity.STREAMING_TIME);

        //Reading thread
        MainActivity.mHost.readingThread.shutup();


        //Update values in table
        return 0;

        }

        @Override
        public void onDestroy() {
            Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

            //Stop timer
            notConnectedTimer.cancel();
            verifyKeyTimer. cancel();

            //Stop reader thread
            MainActivity.mHost.readingThread.shutdown();





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
        storeNewSample(""+ x + "", xAccelValues, time_stamp, false);
        storeNewSample("" + y + "", yAccelValues, time_stamp, false);
        storeNewSample("" + z + "", zAccelValues, time_stamp, false);

        //Update number of received samples:
        if (accelerationNotReceived){
            receivedSamples ++;
            accelerationNotReceived = false;
        }

        //TODO USB COMMAND UPDATE
        mActivity.updateLabel(mActivity.usbCommand, "---");
        mActivity.updateLabel(mActivity.usbCommand, MainActivity.usbCommandValue);


    }

    @Override
    public void didReceiveGSR(float gsr, double time_stamp) {
        mActivity.updateLabel(mActivity.edaLabel, "" + gsr);
        //Consider connection stablished when we receive GSR values:
        connectionEstablishedFlag = true;

        if (gsr >  0.000000000) {
            storeNewSample("" + gsr + "", gsrValues, time_stamp, false);
            last_GSR = gsr;
        }else
           storeNewSample("" + last_GSR + "", gsrValues, time_stamp, false);

        //Update number of received samples:
        if (gsrNotReceived){
            receivedSamples ++;
            gsrNotReceived = false;
        }






    }

    @Override
    public void didReceiveBVP(float bvp, double time_stamp) {
        mActivity.updateLabel(mActivity.bvpLabel, "" + bvp);

        storeNewSample("" + bvp + "", bvpValues, time_stamp, true);

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
            storeNewSample("" + ibi + "", ibiValues, time_stamp, false);

        }
       else {
            storeNewSample("" + last_IBI + "", ibiValues, time_stamp, false);

        }

        //Update number of received samples:
        if (ibiNotReceived){
            receivedSamples += TOTAL_VARIABLES;
            ibiNotReceived = false;
        }
        //Process HEART RATE
        computeHeartRate(ibi, time_stamp);


    }

    private void computeHeartRate(float ibi, double time_stamp){
        float hr = 60/ibi;
        mActivity.updateLabel(mActivity.hrLabel, "" + hr);



        if (hr > _ZERO_FLOAT) {
            last_HR = hr;
            storeNewSample("" + hr + "", hrValues, time_stamp, false);

        }
        else {
            storeNewSample("" + last_HR + "", hrValues, time_stamp, false);

        }
    }

    @Override
    public void didReceiveTemperature(float temp, double time_stamp) {
        mActivity.updateLabel(mActivity.temperatureLabel, "" + temp);
        if (temp < 0)
            storeNewSample("" + temp + "", temperatureValues, time_stamp, false);
        else
            storeNewSample("" + temp + "", temperatureValues, time_stamp, false);

        //Update number of received samples:
        if (temperatureNotReceived){
            receivedSamples ++;
            temperatureNotReceived = false;
        }

    }



    @Override
    public void didReceiveBatteryLevel(float battery, double time_stamp) {

        mActivity.updateLabel(mActivity.batteryLabel, String.format("%.0f %%", battery * 100));
        storeNewSample("" + battery + "", batteryValues, time_stamp, false);

        //Update number of received samples:
        if (batteryNotReceived){
            receivedSamples ++;
            batteryNotReceived = false;
        }



    }
    /* **************************************
     * TIMER
     */

    /**
     * Timer to check whether the connection is lost
     */
    private  void startConnectionTimer() {
        //Restart the counter flag
        startTimer = false;
        //Start a a timer
        notConnectedTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (mActivity.connected) {

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

            }
        }, 60*1000, 60*1000); // delay(seconds*1000), period(seconds*1000)
    }

    /**
     * Verify KEY every 15 min
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
                System.out.println("Timer went off!!!");

                if (cm.getActiveNetworkInfo() != null && deviceManager != null) {
                    //Verify key
                    deviceManager.authenticateWithAPIKey(mActivity.EMPATICA_API_KEY);
                    mActivity.updateLabel(mActivity.internet_conn, "INTERNET");
                    System.out.println("Verified key");
                } else
                    //No itnernet connection - WE CAN NOT USE THE APP
                    mActivity.updateLabel(mActivity.internet_conn, "NO INTERNET");


            }
        }, 1 * 5000, period_time); // delay, period (period_time)

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
        hrValues = new String[]{"0", "0"};
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

    private void storeNewSample(String value, String[] array, double time_stamp, boolean isBVP){

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
        if (isBVP || receivedSamples == TOTAL_VARIABLES + 4 || receivedSamples == TOTAL_VARIABLES + 5){
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
            hrValues[0] = ""+last_HR+"";

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
            ThreadSafeArrayList<String> tempList= new ThreadSafeArrayList<>();
            Map<String, String> tempMap = new HashMap<String, String>();

            ThreadSafeArrayList<String> tempListSec= new ThreadSafeArrayList<>();

            // Add to the list string:
            //Prepare time_stamp:
            double time = time_stamp * 1000;
            ///TODO DateTimeInstance dateTime = new DateTimeInstance();
            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.[nnn]", Locale.getDefault());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS", Locale.getDefault());
            SimpleDateFormat dateFormatSec = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            String formatTimeStamp = dateFormat.format(time);
            String formatTimeStampSec = dateFormatSec.format(time);

            //formatTimeStamp = ""+ formatTimeStamp.substring(0, formatTimeStamp.length() ) +"";
            tempList.set("'"+ formatTimeStamp.substring(0, formatTimeStamp.length() ) +"'");
            tempMap.put(columnsTable[0], formatTimeStamp);

            tempListSec.set("'" + formatTimeStampSec.substring(0, formatTimeStampSec.length()) + "'");

            //Add the other values
            tempList.set(xAccelValues[sample]);
            tempMap.put(columnsTable[1], xAccelValues[sample]);

            tempListSec.set(xAccelValues[sample]);


            tempList.set(yAccelValues[sample]);
            tempMap.put(columnsTable[2], yAccelValues[sample]);

            tempListSec.set(yAccelValues[sample]);


            tempList.set(zAccelValues[sample]);
            tempMap.put(columnsTable[3], zAccelValues[sample]);

            tempListSec.set(zAccelValues[sample]);


            tempList.set(gsrValues[sample]);
            tempMap.put(columnsTable[4], gsrValues[sample]);

            tempListSec.set(gsrValues[sample]);


            tempList.set(bvpValues[sample]);
            tempMap.put(columnsTable[5], bvpValues[sample]);

            tempListSec.set(bvpValues[sample]);


            tempList.set(ibiValues[sample]);
            tempMap.put(columnsTable[6], ibiValues[sample]);

            tempListSec.set(ibiValues[sample]);


            tempList.set(hrValues[sample]);
            tempMap.put(columnsTable[7], hrValues[sample]);

            tempListSec.set(hrValues[sample]);


            tempList.set(temperatureValues[sample]);
            tempMap.put(columnsTable[8], temperatureValues[sample]);

            tempListSec.set(temperatureValues[sample]);

            tempList.set(batteryValues[sample]);
            tempMap.put(columnsTable[9], batteryValues[sample]);

            tempListSec.set(batteryValues[sample]);


            //tempList.add(updated);


            //TODO Add to database: instantly
           // storeSampleInDatabase(tempList, empaticaMilTableName);
            storeSampleInDatabase(tempListSec, empaticaSecTableName);

            //TODO Add to database: every given num of samples
            //Add the the global list
            //receivedData.add(tempList);
           // if (storing_counter == STORING_AMOUNT) {

                //storeGlobalListInDatabase();
                //Reset
                storing_counter =0;
                //receivedData = new ArrayList<ThreadSafeArrayList<String>>();

            //}else{
                storing_counter ++;
           // }




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
        myDB.createTable(empaticaMilTableName, columnsTable[0], new ArrayList<>(Arrays.asList(columnsTable)));
        myDB.createTable(empaticaSecTableName, columnsTable[0], new ArrayList<>(Arrays.asList(columnsTable)));

        //DEBUG TABLE:
        //myDB.createTable("debug_table", new ArrayList<>(Arrays.asList(new String[]{"column"})));


    }

    /**
     * Store a single sample in the database
     * @param sample
     * @return
     */
    private static void storeSampleInDatabase(ThreadSafeArrayList<String> sample, String table){
        try {
            //System.out.println("Sample in: " + i);
            myDB.updateDatabaseTable(table, sample, true);
        } catch (Exception e) {
            System.out.println("Store sample exception " + e);
        }
    }

    /**
     * Store all values accumulated in the global list
     * @return true if done with storing
     */

   /* private static boolean storeGlobalListInDatabase(){

        for (int i = 0; i < receivedData.size(); i++) {
            try {
                //System.out.println("Sample in: " + i);
                myDB.updateDatabaseTable(empaticaTableName, receivedData.get(i), true);
            } catch (Exception e) {
                System.out.println("Store global exception " + e);
            }
        }

        return true;


    }*/







}
