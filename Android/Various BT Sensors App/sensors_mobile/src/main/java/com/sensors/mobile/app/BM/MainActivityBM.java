/**
 * Copyright (c) 2015, BodyMedia Inc. All Rights Reserved
 */

package com.sensors.mobile.app.BM;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bodymedia.android.utils.Logger;
import com.bodymedia.btle.toolkit.SecurityEngine;
import com.bodymedia.mobile.sdk.Armband;
import com.bodymedia.mobile.sdk.ArmbandManager;
import com.bodymedia.mobile.sdk.ConnectionListener;
import com.bodymedia.mobile.sdk.model.SerialNumber;
import com.bodymedia.mobile.sdk.task.ConnectionResult;
import com.sensors.mobile.app.Database.DataStoring;
import com.sensors.mobile.app.Database.Database;
import com.sensors.mobile.app.Database.ThreadSafeArrayList;
import com.sensors.mobile.app.R;
import com.sensors.mobile.app.BM.ui.HomeScreenFragment;
import com.sensors.mobile.app.BM.ui.MinuteRateFragment;
import com.sensors.mobile.app.BM.ui.UIUtils;
import com.bodymedia.utils.ByteUtils;
import org.joda.time.DateTime;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivityBM extends Activity implements ConnectionListener {

    public final static String MAIN_FRAGMENT_TAG = "MAIN_FRAGMENT_TAG";
    public static final int CONNECT_TIMEOUT = 45000;

    private CharSequence actionBarTitle;

    private static final Logger LOG = Logger.getInstance(MainActivityBM.class);

    private Handler handler;

    private ProgressDialog pDialog;

    public static ThreadSafeArrayList<String> sensorValues;
    public static String msg1;
    private static boolean ready1;
    public static String msg2;
    private static boolean ready2;
    public static String msg3;
    private static boolean ready3;
    public static String msg4;
    private static boolean ready4;
    public static String msg5;
    private static boolean ready5;
    public static String msg6;
    private static boolean ready6;
    public static String msg7;
    private static boolean ready7;
    public static String msg8;
    public static String msg9;
    public static String msg10;
    public static String msg11;
    public static String msg12;
    private static boolean ready8;


    private static int lastMinute;
    private static double gsrAvg;
    private static int gsrCounter;



    public static String dispExpName;
    public static String dispTableName;


    public static double[] gsrValues;


    public static Context ctx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);

        //Retrieve values of id and zephyr
        Intent myIntent = getIntent();
        if (myIntent != null) {
            dispTableName = myIntent.getStringExtra("TABLE_ID");
            dispExpName = myIntent.getStringExtra("EXPERIMENT_ID");

        }


        //Init array
        sensorValues = new ThreadSafeArrayList<String>();
        gsrValues = new double[500];
        gsrAvg = 0;
        gsrCounter = 1;
        ctx = this;

        ready1= false;
        ready2 = false;
        ready3=false;
        ready4=false;
        ready5=false;
        ready6=false;
        ready7=false;
        ready8 = false;
        this.handler = new Handler();
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        showHomeScreen();


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add disconnect button to action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean disconnectEnabled = SenseWearApplication.get().isConnected();
        final int drawable = disconnectEnabled ? R.drawable.ic_action_link : R.drawable.ic_action_link_disabled;
        final MenuItem disconnectItem = menu.findItem(R.id.action_disconnect);
        disconnectItem.setIcon(getResources().getDrawable(drawable));
        disconnectItem.setEnabled(disconnectEnabled);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_disconnect)
            disconnectBand();

        return super.onOptionsItemSelected(item);
    }

    private void disconnectBand() {
        try {
            ArmbandManager mgr = SenseWearApplication.get().getArmbandManager();

        if (mgr.getArmband() != null && mgr.getArmband().isConnected()) {
            mgr.disconnect(mgr.getArmband());
            UIUtils.showToast(this, "Armband disconnected");
        } else {
            UIUtils.showToast(this, "No armband found");

        }
        }catch (Exception e){
            System.out.println("Error disconenct armband: "+ e);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // adding activity as a connection listener to receive connect/disconnect messages from device manager.
        SenseWearApplication.get().getArmbandManager().addConnectionListener(this);
    }

    @Override
    protected void onDestroy() {
        try {
            SenseWearApplication.get().getArmbandManager().removeConnectionListener(this);
        } catch (Exception e) {
            LOG.e(e, "Failed to shut down mgr.  Must not have been initialized.");
        }
        Intent sIntent = new Intent(this, SenseWearApplication.class);

        super.onDestroy();
    }

    /**
     * Returns pairing key for the given serial number, that is saved in preferences file.
     * If there is no pair key method will generate the new one and write it to preferences file.
     *
     * @param serialNumber  Serial number of the device that you want to connect to.
     * @return              Returns pairing key that you can use to connect to the device with the given serial number.
     */
    public byte[] getPairingKey(String serialNumber) {
        byte[] pairingKey = AppPrefs.getInstance().loadPairingKey(serialNumber);
        if (pairingKey == null || pairingKey.length != 16) {
            LOG.d("Valid pairing key not found in preferences for device " + serialNumber
                    + ". Generating valid pairing key.");
            pairingKey = SecurityEngine.generatePairingKey();
            AppPrefs.getInstance().storePairingKey(serialNumber, pairingKey);
        }
        LOG.d("Returning pairing key:" + ByteUtils.toHex(pairingKey));
        return pairingKey;
    }

    /* ConnectionListener method implementation. */
    @Override
    public void connectionCompleted(ConnectionResult status) {
        if (status.getArmband() != null) {
            // refresh disconnect button
            invalidateOptionsMenu();

            Armband armband = status.getArmband();
            if (armband != null && armband.getArmbandMode() == Armband.ArmbandMode.NORMAL) {
                // All is OK. We can now show minute rate screen.
                pDialog.dismiss();
                showFragment(new MinuteRateFragment());
            }

        } else if (status.getStatus() == ConnectionResult.ConnectionStatus.BLUETOOTH_DISABLED) {
            // Bluetooth is disabled on device. Start intent to ask the user about turning on the Bluetooth.
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(discoverableIntent);
        } else {
            pDialog.dismiss();
            LOG.e(status.getError(), "Armband connection failed: " + getMessage(status));
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showHomeScreen();
                }
            }, 3500);
        }
    }

    /* ConnectionListener method implementation. */
    @Override
    public void onDisconnect(Armband armband) {
        // disconnection occurred
        // refresh disconnect button and show home screen
        invalidateOptionsMenu();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showHomeScreen();
                if (!SenseWearApplication.get().getArmbandManager().isBluetoothEnabled()) {
                    Toast.makeText(MainActivityBM.this, "Recording is stopped because Bluetooth was disabled", Toast.LENGTH_LONG).show();
                }
            }
        }, 500);
        // we should also dismiss progress dialog if it's shown
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    /**
     * @param connectionResult
     * @return  Returns human readable description of the connection error.
     */
    private String getMessage(ConnectionResult connectionResult) {
        if (connectionResult.getStatus() != null) {
            switch (connectionResult.getStatus()) {
                case CONNECTION_LOST:
                    return "The connection was unexpectedly dropped.";
                case DEVICE_NOT_FOUND:
                    return "Device with serial number " + connectionResult.getTargetSerialNumber() + " was not found.";
                case INVALID_PAIRING_KEY:
                    return "Authorization failed when attempting to connect.";
                case PAIR_REQUIRED:
                    return "The armband must be paired with first before connecting.";
                case TIMED_OUT:
                    return "Timed out during the connect request.  Please submit log.";
                case CONNECTION_REJECTED_AUTO_CONNECT_ACTIVE:
                    return "Auto-connect is currently active.";
                default:
                    break;
            }
        }

        if (connectionResult.getError() != null) {
            return connectionResult.getError().getMessage();
        }

        return "Please submit log for analysis.";
    }

    public void showHomeScreen() {
        if (SenseWearApplication.get().isConnected()) {
            showFragment(new MinuteRateFragment());
        } else {
            showFragment(new HomeScreenFragment());
        }
    }

    public void showFragment(Fragment fragment) {
        try {
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment, MAIN_FRAGMENT_TAG)
                    .commit();
        } catch (IllegalStateException ise) {
            LOG.w(ise, "Unable to show fragment due to an illegal state exception.");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        actionBarTitle = title;
        getActionBar().setTitle(actionBarTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Registering for BroadcastReceiver events.
        IntentFilter filter = new IntentFilter();
        filter.addAction(HomeScreenFragment.ACTION_CONNECT_BY_SERIAL);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    /**
     * This receiver will receive messages from screens that will ask as to connect to the selected device.
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String serialNumber = intent.getStringExtra(HomeScreenFragment.KEY_SERIAL_NO);
            final byte[] pairingKey = getPairingKey(serialNumber);

            final ArmbandManager mgr = SenseWearApplication.get().getArmbandManager();
            final String action = intent.getAction();

            // ArmbandManager provides to ways of how we can connect to the device:
            // - by its serial number
            // - by using JawboneDevice instance. (GeckoDevice is a subclass of JawboneDevice)
           if (HomeScreenFragment.ACTION_CONNECT_BY_SERIAL.equals(action)) {
                if (mgr.connectToDevice(new SerialNumber(serialNumber), pairingKey, CONNECT_TIMEOUT)) {
                    onConnectInProgress(serialNumber);
                }
            }
        }
    };

    private void onConnectInProgress(final String serialNumber) {
        pDialog.setMessage("Connecting to device: " + serialNumber);
        pDialog.show();
    }

    /*********************************************************************************************
     * SERVER CONNECTION
     * ******************************************************************************************
     */

    /*
    * buildMessage1()
    * Called with GEC_SENSORS_ECG_RAW
     */
    public static void buildMessagePeak1(double accFor, double accLong, double accTrans, double bat){
        /*messageToStore( String aType, double longitudinal_accel,double lateral_accel, double transverse_accel, double long_accel_peak,
        double lat_accel_peak,double tran_accel_peak, double long_accel_avg, double lat_accel_avg,
        double tran_accel_avg, double long_accel_mad, double lat_accel_mad, double tran_accel_mad,
        double skin_temp, double gsr, double cover_temp, double skin_temp_avg, double gsr_avg,
        double heat_flux_avg, double steps, double sleep, double calories, double vigorous,
        double METs, int memory, int battery, int message)*/

        messageToStore("", 0, accLong, accFor, accTrans, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, bat, "", 1);

    }

    /*
    * buildMessage2()
    * Called with GEC_SENSORS_CAL
     */
    public static void buildMessagePeak2(double accFor, double accLong, double accTrans){
       // System.out.println("build Message1a");
       // messageToStore( "", 0, accFor, accLong, accTrans, 0, 0, 0, 0, 0, 0, 1);
        messageToStore("", 0, accLong, accFor, accTrans, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,"", 1);
    }
    /*
    * buildMessage3()
    * Called with GEC_SENSORS_ECG_CAL
     */
    public static void buildMessagePeak3(double accFor, double accLong, double accTrans){
        //System.out.println("build Message1a");
       // messageToStore( "", 0, accFor, accLong, accTrans, 0, 0, 0, 0, 0, 0, 1);
        messageToStore("", 0, accLong, accFor, accTrans, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "", 1);
    }

    /*
    * Build 2nd kind of message
     */
    public static void buildMessageTemp(double skinTemp, double [] gsr, double coverTemp){
        /*messageToStore( String aType, double longitudinal_accel,double lateral_accel, double transverse_accel, double long_accel_peak,
        double lat_accel_peak,double tran_accel_peak, double long_accel_avg, double lat_accel_avg,
        double tran_accel_avg, double long_accel_mad, double lat_accel_mad, double tran_accel_mad,
        double skin_temp, double gsr, double cover_temp, double skin_temp_avg, double gsr_avg,
        double heat_flux_avg, double steps, double sleep, double calories, double vigorous,
        double METs, int memory, int battery, int message)*/
        double gsr1 = gsr[0];
        double gsr2 = gsr[1];

        //GSR Average: per minute

        Date date = new Date();   // given date
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date
        int currentMinute =  calendar.get(Calendar.MINUTE); // gets current minute



        if (currentMinute == lastMinute){
            gsrCounter += 2;
            gsrAvg += gsr1+gsr2;

        }else{
            gsrAvg = gsrAvg/gsrCounter;
            //GSR Value in Siemens
            gsrAvg = gsrAvg/1000;
            messageToStore("", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0,skinTemp, gsrAvg, coverTemp, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,"", 2);
            lastMinute = currentMinute;
            gsrAvg = gsr1+gsr2;
            gsrCounter = 2;

        }






    }

    /*
    * build 3rd kind of message
    * Get actType, activity, vigorous, MET, SLEEP CALORIES (Energy expenditure)

     */
    public static void buildMessageMinute( String actType, double vigorous, double met, double sleep, int cal, int mem, String time){
       /*messageToStore( String aType, double longitudinal_accel,double lateral_accel, double transverse_accel, double long_accel_peak,
        double lat_accel_peak,double tran_accel_peak, double long_accel_avg, double lat_accel_avg,
        double tran_accel_avg, double long_accel_mad, double lat_accel_mad, double tran_accel_mad,
        double skin_temp, double gsr, double cover_temp, double skin_temp_avg, double gsr_avg,
        double heat_flux_avg, double steps, double sleep, double calories, double vigorous,
        double METs, int memory, int battery, int message)*/


        messageToStore(actType, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, sleep, cal, vigorous, met, mem, 0, time, 3);
    }

    /*
    * build 4th kind of message
     */
    public static void buildMessageUpdateHigh (int steps, int heart_rate){
        /*messageToStore( String aType, double longitudinal_accel,double lateral_accel, double transverse_accel, double long_accel_peak,
        double lat_accel_peak,double tran_accel_peak, double long_accel_avg, double lat_accel_avg,
        double tran_accel_avg, double long_accel_mad, double lat_accel_mad, double tran_accel_mad,
        double skin_temp, double gsr, double cover_temp, double skin_temp_avg, double gsr_avg,
        double heat_flux_avg, double steps, double sleep, double calories, double vigorous,
        double METs, int memory, int battery, int message)*/

        messageToStore("", heart_rate, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, steps, 0, 0, 0, 0, 0, 0, "",4);

    }
    /*
    * build 5th kind of message
     */

    public static void buildMessageAggregate1(double peakAccLo, double peakAccFw, double peakAccTr, double avgAccLo, double avgAccFr, double avgAccTr,
                                        double avgTskin, double avgTgsr){
        /*messageToStore( String aType, double longitudinal_accel,double lateral_accel, double transverse_accel, double long_accel_peak,
        double lat_accel_peak,double tran_accel_peak, double long_accel_avg, double lat_accel_avg,
        double tran_accel_avg, double long_accel_mad, double lat_accel_mad, double tran_accel_mad,
        double skin_temp, double gsr, double cover_temp, double skin_temp_avg, double gsr_avg,
        double heat_flux_avg, double steps, double sleep, double calories, double vigorous,
        double METs, int memory, int battery, int message)*/

        if (avgTgsr == 0) {
            messageToStore("", 0, 0, 0, peakAccLo, peakAccFw, peakAccTr, avgAccLo, avgAccFr, avgAccTr, 0, 0, 0, 0, 0, 0, avgTskin, avgTgsr, 0, 0, 0, 0, 0, 0, 0, 0, 0, "", 5);
        }else{
            messageToStore ("", 0, 0, 0, peakAccLo, peakAccFw, peakAccTr, avgAccLo, avgAccFr, avgAccTr, 0, 0, 0, 0, 0, 0, avgTskin, avgTgsr, 0, 0, 0, 0, 0, 0, 0, 0, 0,"", 7);
        }

    }
    /*
    * build 6th kind of message
     */
    public static void buildMessageAggregate2(double madAccTr, double madAccLo, double madAccFw){
        /*messageToStore( String aType, double longitudinal_accel,double lateral_accel, double transverse_accel, double long_accel_peak,
        double lat_accel_peak,double tran_accel_peak, double long_accel_avg, double lat_accel_avg,
        double tran_accel_avg, double long_accel_mad, double lat_accel_mad, double tran_accel_mad,
        double skin_temp, double gsr, double cover_temp, double skin_temp_avg, double gsr_avg,
        double heat_flux_avg, double steps, double sleep, double calories, double vigorous,
        double METs, int memory, int battery, int message)*/

        messageToStore("", 0, 0, 0, 0, 0, 0, 0, 0, 0, madAccLo,  madAccFw, madAccTr, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,"", 6);


    }
    public static void buildMessageCumulative(int stepsToday, int stepsCum, int heart_rate){
            //Prepare in case we want it!
    }

    public static void buildMessageBattery (double bat){
        messageToStore("", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, bat,"", 8);


    }

    /*
    * messageToStore
     */

    private static void messageToStore( String aType, int heart_rate, double longitudinal_accel,double lateral_accel, double transverse_accel, double long_accel_peak,
                                        double lat_accel_peak,double tran_accel_peak, double long_accel_avg, double lat_accel_avg,
                                        double tran_accel_avg, double long_accel_mad, double lat_accel_mad, double tran_accel_mad,
                                        double skin_temp, double gsr, double cover_temp, double skin_temp_avg, double gsr_avg,
                                        double heat_flux_avg, double steps, double sleep, double calories, double vigorous,
                                        double METs, int memory, double battery, String last_updt, int message){


             /*('activity_type',"longitudinal_accel", "lateral_accel", "transverse_accel", "long_accel_peak", "lat_accel_peak",
        "tran_accel_peak", "long_accel_avg, "lat_accel_avg", "tran_accel_avg", "long_accel_mad", "lat_accel_mad", "tran_accel_mad"
       "skin_temp", "gsr", "cover_temp", "skin_temp_avg", "gsr_avg", "heat_flux_avg", "steps", "sleep", "calories", "vigorous",
        "METs", "memory", "battery", "last_update", 'no')*/


        switch (message){
            case 1:
                //("", 0, accLong, accFor, accTrans, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1)
                msg3 = longitudinal_accel+", "+ lateral_accel+", "+transverse_accel +", ";
                ready1 = true;
                if (msg11 == "")
                    msg11 = battery+", ";
                break;
            case 2:
                //("", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0,skinTemp, gsr, coverTemp, 0, 0, 0, 0, 0, 0, 0, 0, 0, bat, 2)
                msg6 =skin_temp+", "+ gsr+", "+cover_temp +", ";
                ready2 = true;
                break;
            case 3:
                //(actType, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, sleep, cal, vigorous, met, mem, 0, 3)
                msg1 ="( '"+aType+"', ";
                msg10 = +sleep+", "+calories+", "+vigorous+", "+METs+", "+memory+", ";
                msg12 = "'"+ last_updt +"', 'no')";
                ready3 = true;
                break;
            case 4:
                //("", heart_rate, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, steps, 0, 0, 0, 0, 0, 0, 4)
                ready4 = true;
                msg2 = heart_rate+ ", ";
                msg9 = steps + ", ";
                break;
            case 5:
                ready5 = true;
                //("", 0, 0, 0, peakAccLo, peakAccFw, peakAccTr, avgAccLo, avgAccFr, avgAccTr, 0,  0, 0, 0, 0, 0, avgTskin, avgTgsr, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5)
                msg4 = long_accel_peak+ ", "+ lat_accel_peak+ ", "+tran_accel_peak+ ", "+long_accel_avg+ ", "+lat_accel_avg+ ", "+tran_accel_avg+ ", ";
                msg7= skin_temp_avg+ ", ";
                break;
            case 6:
                ready6 = true;
                msg5=long_accel_mad+ ", "+lat_accel_mad+ ", "+tran_accel_mad+ ", ";
                //("", 0, 0, 0, 0, 0, 0, 0, 0, 0, madAccLo,  madAccFw, madAccTr, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6)
                break;
            case 7:
                msg8 = gsr_avg+ ", 0,";
                ready7 = true;
            case 8:
                msg11 = battery+", ";
                ready8 = true;


            default:
                System.out.println ("Message not supported to store");
        }

        //if (ready1 && ready2 &&ready3 &&ready4 &&ready5&&ready6&&ready7){
        if (ready1 && ready2 &&ready3 &&ready4 &&ready8){
                if (!ready5) {

                    msg4 = "0, 0, 0, 0, 0, 0,";
                    msg7 = "0,";

                }
                else{
                    ready5=false;

                }
                if(!ready6) {
                    msg5 = "0, 0, 0, ";

                }
                else
                    ready6 = false;



                if (!ready7) {
                    msg8 = " 0, 0,";

                }
                else
                    ready7 = false;


                String query = msg1+ msg2 + msg3+ msg4+ msg5+ msg6+ msg7+ msg8+ msg9+ msg10 + msg11 + msg12;
                sensorValues.set(query);
                //Reset values
                ready1=false;
                ready2=false;
                ready3=false;
                ready4=false;
                ready8 = false;

                //Reset intermediate message
                msg1 = "";
                msg2 = "";
                msg3 = "";
                msg6 = "";
                msg9 = "";
                msg10 = "";
                msg11 = "";
                msg12 = "";



                //TODO Save automatically when the information is received
                //Database.updateDatabase(ctx, sensorValues, Database.bodymediaTableName);
                //sensorValues.clear();
            /*
            try {
                DataStoring.syncSQLiteMySQLDB(ctx, Database.bodymediaTableName);
            }catch (JSONException e){
                //Nothing
            }*/

        }
    }
}
