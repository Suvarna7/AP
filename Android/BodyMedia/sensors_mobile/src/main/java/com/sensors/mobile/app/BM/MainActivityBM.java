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

import android.widget.Toast;

import com.bodymedia.android.utils.Logger;
import com.bodymedia.btle.toolkit.SecurityEngine;
import com.bodymedia.mobile.sdk.Armband;
import com.bodymedia.mobile.sdk.ArmbandManager;
import com.bodymedia.mobile.sdk.ConnectionListener;
import com.bodymedia.mobile.sdk.model.SerialNumber;
import com.bodymedia.mobile.sdk.task.ConnectionResult;
import com.sensors.mobile.app.Database.Database;
import com.sensors.mobile.app.R;
import com.sensors.mobile.app.BM.ui.HomeScreenFragment;
import com.sensors.mobile.app.BM.ui.MinuteRateFragment;
import com.sensors.mobile.app.BM.ui.UIUtils;
import com.bodymedia.utils.ByteUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class MainActivityBM extends Activity implements ConnectionListener {

    //Fields for the GUI
    public final static String MAIN_FRAGMENT_TAG = "MAIN_FRAGMENT_TAG";
    public static final int CONNECT_TIMEOUT = 45000;
    private CharSequence actionBarTitle;
    private ProgressDialog pDialog;
    public static String dispExpName;
    public static String dispTableName;

    //Create Logger
    private static final Logger LOG = Logger.getInstance(MainActivityBM.class);

    //Create handler
    private Handler handler;

    public static Context ctx;

    //BGService to manage storing of data
    private static final BGService BG_SERVICE = new BGService();


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

        ctx = this;

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

    /* *******************************
* EMULATE BACK AND HOME BUTTONS: Do not destroy app
 */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
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



}
