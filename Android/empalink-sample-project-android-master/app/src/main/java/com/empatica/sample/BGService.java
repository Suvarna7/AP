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
import com.empatica.sample.Database.StoringThread;
import com.empatica.sample.Database.ThreadSafeArrayList;
import com.empatica.sample.USB.USBHost;

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
 * @version 3.0 Nov 2016
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
    private static int xAccelValue = 0;
    private static int yAccelValue = 1;
    private static int zAccelValue = 2;
    private static int gsrValue = 3;
    private static int bvpValue = 4;
    private static int ibiValue = 5;
    private static int hrValue = 6;
    private static int temperatureValue = 7;
    private static int batteryValue = 8;
    private static String[] allValues;
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


    //Timestamp
    private static double currentTimeStamp;
    private static double nextTimeStamp;


    //Count all variables received for each timestamp
    private static int receivedSamples;

    //Database
    //public static IITDatabaseManager myDB;
    public static final String empaticaMilTableName= "empatica_table";
    //public static final String empaticaSecTableName= "empatica_seconds";

    public static final String[] columnsTable = new String[]{"time_stamp", "Acc_x", "Acc_y", "Acc_z", "GSR", "BVP",
            "IBI", "HR", "temperature","battery_level"};
    public static final int _TIME_INDEX = 0;
    private static int STORING_AMOUNT = 1000;
    private static int storing_counter;

    private static List<ThreadSafeArrayList<String> > failedToUpdate;

    //Manage access to Database
    public static boolean ackInProgress;

    //****************** STORING THREAD ************************

    public static StoringThread storingManager;


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
        ackInProgress = false;
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
        //initDatabaseManager(this);

        //Create the storing Thread and manager
        storingManager = new StoringThread(this);
        storing_counter = 0;

        //ibi
        last_IBI = _ZERO_FLOAT;
        last_GSR = _ZERO_FLOAT;
        last_HR = _ZERO_FLOAT;

        //Received flags:
        resetReceivedFlags();


        //Create:
        failedToUpdate = new ArrayList<ThreadSafeArrayList<String> >();





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
        connectionEstablishedFlag = true;

        //Store sample:
        storeNewSample(""+ x + "", xAccelValue, time_stamp, false);
        storeNewSample("" + y + "", yAccelValue, time_stamp, false);
        storeNewSample("" + z + "", zAccelValue, time_stamp, false);

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
            storeNewSample("" + gsr + "", gsrValue, time_stamp, false);
            last_GSR = gsr;
        }else
           storeNewSample("" + last_GSR + "", gsrValue, time_stamp, false);

        //Update number of received samples:
        if (gsrNotReceived){
            receivedSamples ++;
            gsrNotReceived = false;
        }






    }

    @Override
    public void didReceiveBVP(float bvp, double time_stamp) {
        mActivity.updateLabel(mActivity.bvpLabel, "" + bvp);
        connectionEstablishedFlag = true;

        storeNewSample("" + bvp + "", bvpValue, time_stamp, true);

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
            storeNewSample("" + ibi + "", ibiValue, time_stamp, false);

        }
       else {
            storeNewSample("" + last_IBI + "", ibiValue, time_stamp, false);

        }
        connectionEstablishedFlag = true;

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
            storeNewSample("" + hr + "", hrValue, time_stamp, false);

        }
        else {
            storeNewSample("" + last_HR + "", hrValue, time_stamp, false);

        }
    }

    @Override
    public void didReceiveTemperature(float temp, double time_stamp) {
        mActivity.updateLabel(mActivity.temperatureLabel, "" + temp);
        if (temp < 0)
            storeNewSample("" + temp + "", temperatureValue, time_stamp, false);
        else
            storeNewSample("" + temp + "", temperatureValue, time_stamp, false);
        connectionEstablishedFlag = true;

        //Update number of received samples:
        if (temperatureNotReceived){
            receivedSamples ++;
            temperatureNotReceived = false;
        }

    }



    @Override
    public void didReceiveBatteryLevel(float battery, double time_stamp) {

        mActivity.updateLabel(mActivity.batteryLabel, String.format("%.0f %%", battery * 100));
        storeNewSample("" + battery + "", batteryValue, time_stamp, false);
        connectionEstablishedFlag = true;

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
                        //Inform laptop that no data will be send
                        //Send no data message
                        mActivity.mHost.sendUSBmessage(USBHost._END_COMMAND);
                        mActivity.mHost.sendUSBmessage(USBHost._NO_DATA);

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
        }, 60*1000, 5*60*1000); // delay(seconds*1000), period(seconds*1000)
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
        allValues  = new String[]{"0", "0", "0", "0", "0", "0", "0", "0", "0"};



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

    private void storeNewSample(String value, int array, double time_stamp, boolean isBVP){

        //Check here is no null value:
        if (value.contains("null"))
            allValues[array] = "0";
        else
            allValues[array]=value;

        //Analyze time_stamp vs currentTimeStamp
       /* if (currentTimeStamp == 0.0)
            //Initialize
            currentTimeStamp = time_stamp;
        else if (currentTimeStamp == time_stamp) {
            //Current sample
            array = value;
        }
        else if (currentTimeStamp < time_stamp) {
            //Next sample
            array = value;
            currentTimeStamp = time_stamp;
        }
        else if (currentTimeStamp > time_stamp) {
            //Previous sample
            array = value;

        }*/


        //Analyze # of received samples
        if (isBVP){
       // if (isBVP || receivedSamples == TOTAL_VARIABLES + 4 || receivedSamples == TOTAL_VARIABLES + 5){
            //Store samples as part of variables string
            saveInGlobalList(0, time_stamp);
            //Re-start counter
            receivedSamples = 0;
            currentTimeStamp =  nextTimeStamp;
            //Move next samples to current sample position
            //resetArraysNextSample();
            resetReceivedFlags();
        }else if (receivedSamples == TOTAL_VARIABLES  - 4 || receivedSamples == TOTAL_VARIABLES -3){
            //Did not received an IBI update yet: update Array with last sample
            /*ibiValue = ""+last_IBI+"";
            hrValue = ""+last_HR+"";

            //Store samples as part of variables string
            saveInGlobalList(0, currentTimeStamp);
            //Re-start counter
            receivedSamples = 0;
            currentTimeStamp =  nextTimeStamp;
            //Move next samples to current sample position
            //resetArraysNextSample();
            resetReceivedFlags();*/
        }
    }

    private void saveInGlobalList(int sample, double time_stamp){
        if (sample == 0 ){
            ThreadSafeArrayList<String> tempList= new ThreadSafeArrayList<>();
            Map<String, String> tempMap = new HashMap<String, String>();

            //ThreadSafeArrayList<String> tempListSec= new ThreadSafeArrayList<>();

            // Add to the list string:
            //Prepare time_stamp:
            double time = time_stamp * 1000;
            ///TODO DateTimeInstance dateTime = new DateTimeInstance();
            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.[nnn]", Locale.getDefault());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS", Locale.getDefault());
           // SimpleDateFormat dateFormatSec = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            String formatTimeStamp = dateFormat.format(time);
            //String formatTimeStampSec = dateFormatSec.format(time);

            //formatTimeStamp = ""+ formatTimeStamp.substring(0, formatTimeStamp.length() ) +"";
            tempList.set(formatTimeStamp);
            tempMap.put(columnsTable[0], formatTimeStamp);

            //tempListSec.set("'" + formatTimeStamp.substring(0, formatTimeStamp.length()) + "'");

            //Add the other values
            tempList.set(allValues[xAccelValue]);
            tempMap.put(columnsTable[1], allValues[xAccelValue]);

           // tempListSec.set(xAccelValues[sample]);


            tempList.set(allValues[yAccelValue]);
            tempMap.put(columnsTable[2], allValues[yAccelValue]);

           // tempListSec.set(yAccelValues[sample]);


            tempList.set(allValues[zAccelValue]);
            tempMap.put(columnsTable[3], allValues[zAccelValue]);

           // tempListSec.set(zAccelValues[sample]);


            tempList.set(allValues[gsrValue]);
            tempMap.put(columnsTable[4], allValues[gsrValue]);

           // tempListSec.set(gsrValues[sample]);


            tempList.set(allValues[bvpValue]);
            tempMap.put(columnsTable[5], allValues[bvpValue]);

           // tempListSec.set(bvpValues[sample]);


            tempList.set(allValues[ibiValue]);
            tempMap.put(columnsTable[6], allValues[ibiValue]);

           // tempListSec.set(ibiValues[sample]);


            tempList.set(allValues[hrValue]);
            tempMap.put(columnsTable[7], allValues[hrValue]);

            //  tempListSec.set(hrValues[sample]);


            tempList.set(allValues[temperatureValue]);
            tempMap.put(columnsTable[8], allValues[temperatureValue]);

            //tempListSec.set(temperatureValues[sample]);

            tempList.set(allValues[batteryValue]);
            tempMap.put(columnsTable[9], allValues[batteryValue]);

            //tempListSec.set(batteryValues[sample]);


            //tempList.add(updated);


            //TODO Add to database: instantly
            //System.out.println("ACK STATE: "+ackInProgress);
            //Store current sample
            storingManager.storeSampleInTempDatabase(tempList, empaticaMilTableName, columnsTable, null);

            //Run storing thread to store all previous
            if (storingManager.shouldStartTransaction())
                    new Thread (storingManager).start();

           // storeSampleInDatabase(tempListSec, empaticaSecTableName);

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









}
