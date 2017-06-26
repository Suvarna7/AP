package com.empatica.sample;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.widget.Toast;
import com.empatica.empalink.EmpaDeviceManager;
import com.empatica.empalink.delegate.EmpaDataDelegate;
import com.empatica.sample.Database.StoringThread;
import com.empatica.sample.Database.ThreadSafeArrayList;
import com.empatica.sample.Timers.NotConnectedTimer;
import com.empatica.sample.Timers.VerifyKeyTimer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * BGService - Background service that will manage:
 *      x Connection with the device
 *      x Writing data to our Database
 * Extends - Service
 * Implements - EmpaDataDelegate
 *
 * @author Caterina Lazaro
 * @version 4.0 Jan 2017
 * */

public class BGService extends Service implements EmpaDataDelegate{

    private static MainActivity mActivity;
    public static EmpaDeviceManager deviceManager;
    public static boolean serviceStarted = false;

    //Fields to detect connection lost
    public static NotConnectedTimer notConnectedTimer;

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
    //Total values
    private static double BATTERY;

    //Fields to verify key
    public static VerifyKeyTimer verifyKeyTimer;

    //Database
    //public static IITDatabaseManager myDB;
    public static final String empaticaTableName= "empatica_table";
    //public static final String empaticaSecTableName= "empatica_seconds";

    public static final String[] columnsTable = new String[]{"time_stamp", "Acc_x", "Acc_y", "Acc_z", "GSR", "BVP",
            "IBI", "HR", "temperature","battery_level"};
    public static final int _TIME_INDEX = 0;
    //private static List<ThreadSafeArrayList<String> > failedToUpdate;

    //Manage access to Database
    public static boolean ackInProgress;

    //****************** STORING THREAD ************************
    public static StoringThread storingManager;

    //****************** IBI PREPROCESSING ********************
    private static double t1;
    private static double d1;
    private static double t2;
    private static double t3;

    private static final double    _IBI_MAX = 1.5;
    private static final double    _IBI_MIN= 0.25;

    public static boolean EmpaticaDisconnected;


    public BGService (){  /*Do nothing */ EmpaticaDisconnected = true;}
    public BGService(MainActivity main) {
        mActivity = main;
        EmpaticaDisconnected = true;
    }

    public static void initContext(MainActivity main){

        mActivity = main;

        //Empatica disconnected
        //EmpaticaDisconnected = true;
    }

    /* *************************************************************************
     * BACKGROUND SERVICE CALLBACKS
     * *************************************************************************
     */

    @Override
    public IBinder onBind(Intent intent) {
        //Toast.makeText(this, "The new Service was Binded", Toast.LENGTH_LONG).show();
        return null;
    }

    @Override
    public void onCreate() {
        ackInProgress = false;


        // Create a new EmpaDeviceManager. Service is both its data  delegate and MainActivity its status delegate.
        if (mActivity != null) {
            deviceManager = new EmpaDeviceManager(mActivity.getApplicationContext(), this, mActivity);
            // Initialize the Device Manager using your API key. You need to have Internet access at this point.
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null) {
                deviceManager.authenticateWithAPIKey(MainActivity.EMPATICA_API_KEY);
                mActivity.updateLabel(mActivity.internet_conn, "INTERNET");
            } else
                //No itnernet connection - WE CAN NOT USE THE APP
                mActivity.updateLabel(mActivity.internet_conn, "NO INTERNET");


        }


        //Set up connection check
        notConnectedTimer = new NotConnectedTimer(mActivity);

        //Set up verify KEY
        verifyKeyTimer = new VerifyKeyTimer(deviceManager,mActivity);
        //TODO verifyKeyTimer.startTimer();

        //Initialize data collection
        BATTERY = 0;
        initArrayOfValues();

        //Received flags:
        resetReceivedFlags();

        //IBI
        t1 = 0;
        d1 = 0;
        t2 = 0;
        t3 = 0;

        //Empatica disconnected
        EmpaticaDisconnected = true;

    }


    @Override
    public int  onStartCommand(Intent intent, int flags, int startId){
        // For time consuming an long tasks you can launch a new thread here...
        // Do your Bluetooth Work Here
        serviceStarted = true;
        Toast.makeText(this, " Service Started", Toast.LENGTH_LONG).show();

        //Create the storing Thread and manager
        storingManager = new StoringThread(this);

        //Start connection timer
        //Timer to check the connection
        notConnectedTimer.startTimer();

        //TODO Start key verification timer
        //startPeriodicVerificationOfKey(MainActivity.STREAMING_TIME);

        //Reading thread
        if(MainActivity.mHost !=null && MainActivity.mHost.readingThread != null)
            MainActivity.mHost.readingThread.shutup();

        //Update values in table
        return 0;

        }

        @Override
        public void onDestroy() {
            Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();

            //Stop timer
            notConnectedTimer.cancel();
            verifyKeyTimer. cancel();

            //Stop reader thread
            MainActivity.mHost.readingThread.shutdown();

        }



    /* *************************************************************************
     * EMPATICA  CALLBACKS
     * *************************************************************************
     */

    @Override
    public void didReceiveAcceleration(int x, int y, int z, double time_stamp) {
        mActivity.updateLabel(mActivity.accel_xLabel, "" + x);
        mActivity.updateLabel(mActivity.accel_yLabel, "" + y);
        mActivity.updateLabel(mActivity.accel_zLabel, "" + z);
        notConnectedTimer.connectionACK();

        //Save in local array
        //Check here is no null value:
        //X
        String xval = (""+ x + "");
        String yval = (""+ y + "");
        String zval = (""+ z + "");

        if (xval.contains("null"))
            allValues[xAccelValue] = "0";
        else
            allValues[xAccelValue]=xval;
        //Y
        if (yval.contains("null"))
            allValues[yAccelValue] = "0";
        else
            allValues[yAccelValue]=yval;
        //Z
        if (zval.contains("null"))
            allValues[zAccelValue] = "0";
        else
            allValues[zAccelValue]=zval;


        //Update number of received samples:
        if (accelerationNotReceived){
            accelerationNotReceived = false;
        }

        //Store database
        storeNewSample(time_stamp);

        //TODO USB COMMAND UPDATE
        mActivity.updateLabel(mActivity.usbCommand, "---");
        mActivity.updateLabel(mActivity.usbCommand, MainActivity.usbCommandValue);

    }

    @Override
    public void didReceiveGSR(float gsr, double time_stamp) {
        mActivity.updateLabel(mActivity.edaLabel, "" + gsr);
        //Consider connection stablished when we receive GSR values:
        notConnectedTimer.connectionACK();

        //Store in local array
        String gsrval = (""+ gsr + "");

        if (gsrval.contains("null"))
            allValues[gsrValue] = "0";
        else
            allValues[gsrValue]=gsrval;

        //Update number of received samples:
        if (gsrNotReceived ){
            gsrNotReceived = false;
        }

        //Store in DB
        storeNewSample(time_stamp);
    }

    @Override
    public void didReceiveBVP(float bvp, double time_stamp) {
        mActivity.updateLabel(mActivity.bvpLabel, "" + bvp);
        notConnectedTimer.connectionACK();


        //Store in local array
        String bvpval  = (""+ bvp + "");
        if (bvpval.contains("null"))
            allValues[bvpValue] = "0";
        else
            allValues[bvpValue]=bvpval;


        //Update number of received samples:
        if (bvpNotReceived){
            bvpNotReceived = false;
        }

        //Store database
        storeNewSample(time_stamp);


    }

    @Override
    public void didReceiveIBI(float ibi, double time_stamp) {
        mActivity.updateLabel(mActivity.ibiLabel, "" + ibi);
        //NOTE: IBI is updated at a slower pace

        //Check whether we have to generat new IBI samples
        //https://support.empatica.com/hc/en-us/articles/201912319-How-is-IBI-csv-obtained-

        //DETECTED lost sample in previous IBI
       /* if (t3 != 0) {
            //Get d3
            double d3 = t3 - time_stamp;
            //New sample in <t3 - t1 - d1>
            double d2 = t3 - t2;

            //Reset t3
            t3=0;

        }
        //Normal Execution
        else{
            // - First sample: t1 ==0
            if (t1 ==0)
                t1 = time_stamp;
            // - Second sample: t1 !=0 && d1 ==0
            else if (d1 ==0) {
                d1 = time_stamp - t1;
                t2 = time_stamp;

            }else{
                // - Condition for lost sample: if (t1 +d1 < time_stamp)
                if((t1 + d1) > time_stamp){
                    //IBI is OK timed
                    //Update t1, d1, t2
                    t1 = t2;
                    t2 = time_stamp;
                    d1 =  t2 -t1;

                    // reset next samples values
                    t3 = 0;

                }else{
                    //IBI lost some samples after t2
                    t3 = time_stamp;


                }
            }
        }*/


        //Store in local array
        String ibival = (""+ ibi + "");
        if (ibival.contains("null"))
            allValues[ibiValue] = "1.11";
        else
            allValues[ibiValue]=ibival;

        //storeNewSample("" + ibi + "", ibiValue, time_stamp, false);

        notConnectedTimer.connectionACK();

        //Update number of received samples:
        if (ibiNotReceived){
            ibiNotReceived = false;
        }

        //Process HEART RATE
        //IBI limits:
        if (ibi<_IBI_MAX && ibi > _IBI_MIN)
            computeHeartRate(ibi, time_stamp);
        else
            allValues[hrValue] = "-1";




    }

    private void computeHeartRate(float ibi, double time_stamp){
        //TODO - Do not use 60, use Var(time_stamp)
        float hr = 60/ibi;
        mActivity.updateLabel(mActivity.hrLabel, "" + hr);

        //Store in local array
        String hrval = (""+ hr + "");
        if (hrval.contains("null"))
            allValues[hrValue] = "111";
        else
            allValues[hrValue]=hrval;

        //TODO add to IBI table directly
        /*double time = time_stamp * 1000;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS", Locale.getDefault());
        String formatTimeStamp = dateFormat.format(time);

        ThreadSafeArrayList<String> tempList= new ThreadSafeArrayList<>();
        tempList.set(formatTimeStamp);
        tempList.set(allValues[ibiValue]);
        tempList.set(allValues[hrValue]);
        storingManager.storeSampleInPermanentDatabase(tempList, StoringThread.ibiTableName, StoringThread.ibiColumnsTable, null);*/

        //Store in DB
        storeNewSample(time_stamp);


    }

    @Override
    public void didReceiveTemperature(float temp, double time_stamp) {
        //NOTE: temp value received -273 ??
        mActivity.updateLabel(mActivity.temperatureLabel, "" + temp);

        //Store in local array
        String tempval = (""+ temp + "");
        if (tempval.contains("null") || temp < 0)
            //            allValues[temperatureValue] = "0";
            allValues[temperatureValue] = allValues[temperatureValue]; //prev
        else
            allValues[temperatureValue]=tempval;


        notConnectedTimer.connectionACK();

        //Update number of received samples:
        if (temperatureNotReceived){
            temperatureNotReceived = false;
        }

        //Store in DB
        storeNewSample(time_stamp);

    }


    @Override
    public void didReceiveBatteryLevel(float battery, double time_stamp) {

        mActivity.updateLabel(mActivity.batteryLabel, String.format("%.0f %%", battery * 100));

        //Store in local array
        String batval = ""+ battery * 100;
        BATTERY = battery*100;

        if (batval.contains("null") )
            allValues[batteryValue] = "0";
        else
            allValues[batteryValue]=batval;


        //Store in DB
        storeNewSample(time_stamp);

        notConnectedTimer.connectionACK();

        //Update number of received samples:
        if (batteryNotReceived ){
            batteryNotReceived = false;
        }
    }



    /* **********************************************
     * Store received data in local arrays
     */

    private static void  initArrayOfValues(){
        //TODO String temp =  allValues[temperatureValue];
        allValues  = new String[]{"0", "0", "0", "0", "0", "0", "0", "0", ""+BATTERY};


    }


    private void storeNewSample( double time_stamp){

            //Store samples as part of variables string
            saveInGlobalList( time_stamp);
            //Re-start counter
            //Move next samples to current sample position
            initArrayOfValues();

    }

    private void saveInGlobalList(double time_stamp){

            ThreadSafeArrayList<String> tempList= new ThreadSafeArrayList<>();
            // Add to the list string:
            //Prepare time_stamp:
            double time = time_stamp * 1000;
            ///TODO DateTimeInstance dateTime = new DateTimeInstance();
            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.[nnn]", Locale.getDefault());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS", Locale.getDefault());
            String formatTimeStamp = dateFormat.format(time);

            // SimpleDateFormat dateFormatSec = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            //String formatTimeStampSec = dateFormatSec.format(time);
            //formatTimeStamp = ""+ formatTimeStamp.substring(0, formatTimeStamp.length() ) +"";

            //Populate temp list
            tempList.set(formatTimeStamp);
            tempList.set(allValues[xAccelValue]);
            tempList.set(allValues[yAccelValue]);
            tempList.set(allValues[zAccelValue]);
            tempList.set(allValues[gsrValue]);
            tempList.set(allValues[bvpValue]);
            tempList.set(allValues[ibiValue]);
            tempList.set(allValues[hrValue]);
            tempList.set(allValues[temperatureValue]);
            tempList.set(allValues[batteryValue]);

            //Populate temporal map of values - col:value
            /*tempMap.put(columnsTable[0], formatTimeStamp);
            tempMap.put(columnsTable[1], allValues[xAccelValue]);
            tempMap.put(columnsTable[2], allValues[yAccelValue]);
            tempMap.put(columnsTable[3], allValues[zAccelValue]);
            tempMap.put(columnsTable[4], allValues[gsrValue]);
            tempMap.put(columnsTable[5], allValues[bvpValue]);
            tempMap.put(columnsTable[6], allValues[ibiValue]);
            tempMap.put(columnsTable[7], allValues[hrValue]);
            tempMap.put(columnsTable[8], allValues[temperatureValue]);
            tempMap.put(columnsTable[9], allValues[batteryValue]);*/


            //TODO Add to database: instantly
            //Store current sample
            List<ThreadSafeArrayList<String>> error = new ArrayList<ThreadSafeArrayList<String>>();
            storingManager.storeSampleInTempDatabase(tempList, empaticaTableName, columnsTable, error);

            for  (ThreadSafeArrayList<String> sample: error){
                storingManager.storeSampleInTempDatabase(sample, empaticaTableName, columnsTable, error);
            }

            //Run storing thread to store all previous
            if (receivedAll() && StoringThread.shouldStartTransaction()) {
                new Thread(storingManager).start();
                resetReceivedFlags();

            }

    }

    private void resetReceivedFlags(){
        accelerationNotReceived = true;
        gsrNotReceived = true;
        bvpNotReceived =  true;
        ibiNotReceived = true;
        temperatureNotReceived = true;
        batteryNotReceived = true;
    }

    private boolean receivedAll(){
        return !(accelerationNotReceived || gsrNotReceived || bvpNotReceived || temperatureNotReceived  );
    }









}
