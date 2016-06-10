package com.empatica.sample;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Time;
import android.widget.Toast;


import com.empatica.empalink.EmpaDeviceManager;
import com.empatica.empalink.delegate.EmpaDataDelegate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//import rx.Observer;


public class BGService extends Service implements EmpaDataDelegate{
	
	private boolean tableInit;
	private int index;
    private static MainActivity mActivity;
    public static EmpaDeviceManager deviceManager;
    public static boolean serviceStarted = false;

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
        System.out.println("!!!!!!!!!! SERVICE !!!!!!!!!!!");
        //*** Empatica managers
        // Create a new EmpaDeviceManager. MainActivity is both its data and status delegate.
        deviceManager = new EmpaDeviceManager(mActivity.getApplicationContext(), this, mActivity);
        // Initialize the Device Manager using your API key. You need to have Internet access at this point.
        deviceManager.authenticateWithAPIKey(mActivity.EMPATICA_API_KEY);

    }

    @Override
    public int  onStartCommand(Intent intent, int flags, int startId){
        // For time consuming an long tasks you can launch a new thread here...
        // Do your Bluetooth Work Here
        serviceStarted = true;


        Toast.makeText(this, " Service Started", Toast.LENGTH_LONG).show();




        System.out.println("!!!!!!!!!! SERVICE !!!!!!!!!!!");


        //Update values in table
        return 0;

        }

        @Override
        public void onDestroy() {
            Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

            //Stop these listeners


        }
    /**********************************
     * Empatica callbacks
     */

    @Override
    public void didReceiveGSR(float gsr, double time_stamp) {
        mActivity.updateLabel(mActivity.edaLabel, "" + gsr);

    }

    @Override
    public void didReceiveBVP(float bvp, double time_stamp) {
        mActivity.updateLabel(mActivity.bvpLabel, "" + bvp);

    }

    @Override
    public void didReceiveIBI(float ibi, double time_stamp) {
        mActivity.updateLabel(mActivity.ibiLabel, "" + ibi);
    }

    @Override
    public void didReceiveTemperature(float temp, double time_stamp) {
        mActivity.updateLabel(mActivity.temperatureLabel, "" + temp);


    }

    @Override
    public void didReceiveAcceleration(int x, int y, int z, double time_stamp) {
        mActivity.updateLabel(mActivity.accel_xLabel, "" + x);
        mActivity.updateLabel(mActivity.accel_yLabel, "" + y);
        mActivity.updateLabel(mActivity.accel_zLabel, "" + z);

    }

    @Override
    public void didReceiveBatteryLevel(float battery, double time_stamp) {
        mActivity.updateLabel(mActivity.batteryLabel, String.format("%.0f %%", battery * 100));


    }






}
