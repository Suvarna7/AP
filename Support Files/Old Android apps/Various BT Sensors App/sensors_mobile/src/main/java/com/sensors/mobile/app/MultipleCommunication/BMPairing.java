package com.sensors.mobile.app.MultipleCommunication;

import android.content.Intent;
import com.bodymedia.btle.toolkit.SecurityEngine;
import com.bodymedia.mobile.sdk.Armband;
import com.bodymedia.mobile.sdk.ArmbandManager;
import com.bodymedia.mobile.sdk.ConnectionListener;
import com.bodymedia.mobile.sdk.GeckoDevice;
import com.bodymedia.mobile.sdk.listener.PairingListener;
import com.bodymedia.mobile.sdk.task.ConnectionResult;
import com.sensors.mobile.app.BM.SenseWearApplication;
import com.sensors.mobile.app.BM.AppPrefs;

import static android.bluetooth.BluetoothAdapter.*;


/**
 * BMPairing - class to control the pairing of BodyMedia
 * Created by Caterina on 4/21/2015.
 */
public class BMPairing {
    private static final long SCAN_TIMEOUT = 3500;
    private static final long PAIRING_TIMEOUT = 30000;
    private static final int MAX_SCAN_ATTEMPTS = 10;

    public ArmbandManager armbandManager;
    private byte[] pairingKey;

    private int scanAttempts;

    public BMPairing(){

        armbandManager  = SenseWearApplication.get().getArmbandManager();
        armbandManager.clearPairingListener();
        armbandManager.setPairingListener(pairListenerMultiple);
        armbandManager.addConnectionListener(connectionListenerMultiple);
    }

    public void doPairing(){
        scanAttempts = 0;

        // if there is connected armband we should perform disconnection
        if (armbandManager != null && SenseWearApplication.get().getArmband() != null) {
            armbandManager.disconnect(SenseWearApplication.get().getArmband());
        }


        //Set pairing params
        if (armbandManager !=null){
            armbandManager.setPairingListener(pairListenerMultiple);
            armbandManager.addConnectionListener(connectionListenerMultiple);
            startPairingProcess();
        }



    }

    public void startPairingProcess() {

        pairingKey = SecurityEngine.generatePairingKey();
        //System.out.println("Security Key: " +pairingKey);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!armbandManager.isBluetoothEnabled()) {

                    // ask the user to turn on Bluetooth
                    Intent discoverableIntent = new Intent(ACTION_REQUEST_ENABLE);
                    discoverableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MainActivityMultiple.ctx.startActivity(discoverableIntent);


                }
                    // try to connect with an armband in pairing mode
                    armbandManager.setPairingListener(pairListenerMultiple);
                    armbandManager.pairWithClosestArmband(pairingKey, SCAN_TIMEOUT, PAIRING_TIMEOUT);

            }
        }).start();
    }

    public final ConnectionListener connectionListenerMultiple = new ConnectionListener() {
        @Override
        public void connectionCompleted(ConnectionResult status) {
            if (status.getStatus() == ConnectionResult.ConnectionStatus.SUCCESS) {
                // Device connected successfully. We just showing the toast.
                // MainActivity will receive message about connection (connectionCompleted)
                // and show correct screen by itself.
                System.out.println("!!!!!!!!!!!!!! Connection sucess: " + status.getArmband());


                if (status.getArmband() != null && pairingKey != null) {
                    //TODO STORE connected device to show values
                    AppPrefs.getInstance().storePairingKey(status.getArmband().getSerialNumber(), pairingKey);
                    pairingKey = null;
                }
                MainActivityMultiple.pairedNotFinished = false;
            }
        }

        @Override
        public void onDisconnect(Armband armband) {
        }
    };

    private final PairingListener pairListenerMultiple = new PairingListener() {
        @Override
        public void onDeviceFoundInPairingMode(GeckoDevice geckoDevice) {
            // User should press button on armband to confirm the pairing
            //Prepare the Toast to show it:
           // Toast.makeText(MainActivityMultiple.ctx, "Press Armband button to pair", Toast.LENGTH_LONG).show();

            System.out.println("VVVVVVVVVVVVVVV Press Armband button to pair");
        }

        @Override
        public void noDeviceFound() {
            System.out.println(" No device found in pairing mode");

            if (scanAttempts == MAX_SCAN_ATTEMPTS){
                //Stop trying
                System.out.println("XXXXXXXXXX Stop Scanning: go back to main");
                System.out.println ("Paired devices: " + MainActivityMultiple.adapter.getBondedDevices());

                MainActivityMultiple.pairedNotFinished = false;
                scanAttempts = 0;
            }else {
                startPairingProcess();
                scanAttempts ++;
            }
        }

        @Override
        public void confirmationTimeout(GeckoDevice geckoDevice) {
            if (scanAttempts == MAX_SCAN_ATTEMPTS){
                //Stop trying
                MainActivityMultiple.pairedNotFinished = false;
                scanAttempts = 0;
            }else {
                startPairingProcess();
                scanAttempts ++;
            }        }
    };




}
