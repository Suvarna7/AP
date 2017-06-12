package com.empatica.sample;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.empatica.empalink.ConnectionNotAllowedException;
import com.empatica.empalink.config.EmpaSensorStatus;
import com.empatica.empalink.config.EmpaSensorType;
import com.empatica.empalink.config.EmpaStatus;
import com.empatica.empalink.delegate.EmpaStatusDelegate;
import com.empatica.sample.Timers.SendDataTimer;
import com.empatica.sample.USB.USBHost;
import com.empatica.sample.USB.USBReceiver;


/**
 * EmpaLink Main Activity
 * Implements EmpaStatusDelegate and holds the GUI of the app
 * Class modified from the EmpaLink Sample code
 *
 * @author Caterina Lazaro
 * @version 2.0 May 2017
 */

public class MainActivity extends Activity implements EmpaStatusDelegate {

    //DiAS Flag:
    private boolean DiASPhone = false;
    //Empatica variables
    private static final int REQUEST_ENABLE_BT = 1;
    public static final String EMPATICA_API_KEY = "01f86c5b71ce435298d2ebc74e3e21a0"; // API Key YELLOW PHONE
    //public static final String EMPATICA_API_KEY = "f92ddb7260a54f5790038ba90ef4d1ad"; // API Key RED PHONE
    // public static final String EMPATICA_API_KEY = "bb7af54058a34b9987d31953912f11e5"; // API Key PINK PHONE

    //GUI
    //Wake lock
    protected PowerManager.WakeLock mWakeLock;

    //Connection status
    public TextView internet_conn;
    public TextView statusLabel;
    public TextView deviceNameLabel;
    private static String device;
    private static String connectionS;

    //Empatica data
    public RelativeLayout dataCnt;
    public TextView accel_xLabel;
    public TextView accel_yLabel;
    public TextView accel_zLabel;
    public TextView bvpLabel;
    public TextView edaLabel;
    public TextView ibiLabel;
    public TextView hrLabel;
    public TextView temperatureLabel;
    public TextView batteryLabel;

    //Buttons
    public static Button connectButton;
    private Button stopServiceButton;
    private Button startServiceButton;
    public Button connectUSBButton;
    private Button  deleteB;

    //App context
    private Context appContext;
    private MainActivity appMain;
    private static boolean alreadyInitialized;

    //USB Connection
    public TextView usbConenctionStatus;
    public static USBHost mHost;
    public TextView usbCommand;
    public static String usbCommandValue;
    public USBReceiver usbBroadcast;

    //public final String USB_START_CONNECT= getString(R.string.connect_usb_init);

    //IIT Server manager and automatic sending
    public static SendDataTimer sendDataTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //System.out.println("App created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Screen ON
        keepScreenON(true);

        // Initialize vars that reference UI components
        internet_conn = (TextView) findViewById(R.id.internetConnection);
        statusLabel = (TextView) findViewById(R.id.status);
        dataCnt = (RelativeLayout) findViewById(R.id.dataArea);
        accel_xLabel = (TextView) findViewById(R.id.accel_x);
        accel_yLabel = (TextView) findViewById(R.id.accel_y);
        accel_zLabel = (TextView) findViewById(R.id.accel_z);
        bvpLabel = (TextView) findViewById(R.id.bvp);
        edaLabel = (TextView) findViewById(R.id.eda);
        ibiLabel = (TextView) findViewById(R.id.ibi);
        hrLabel = (TextView) findViewById(R.id.hr);

        temperatureLabel = (TextView) findViewById(R.id.temperature);
        batteryLabel = (TextView) findViewById(R.id.battery);
        deviceNameLabel = (TextView) findViewById(R.id.deviceName);
        // Initialize button
        connectButton = (Button) findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectListener);

        stopServiceButton = (Button) findViewById(R.id.stop_service_button);
        stopServiceButton.setOnClickListener(stopListener);

        startServiceButton = (Button) findViewById(R.id.start_service_button);
        startServiceButton.setOnClickListener(startListener);

        connectUSBButton = (Button) findViewById(R.id.connect_usb_button);
        connectUSBButton.setOnClickListener(connectToPcListener);

        usbCommand = (TextView) findViewById(R.id.command_usb);
        usbConenctionStatus = (TextView) findViewById(R.id.connect_usb_status);

        deleteB = (Button) findViewById(R.id.delete_database_button);
        deleteB.setOnClickListener(deleteDatabaseListener);

        //Set context
        appContext = this;
        appMain = this;

    }




    @Override
    protected void onPause() {
        //System.out.println("App paused");
        super.onPause();
        /*if (BGService.deviceManager != null)
            BGService.deviceManager.stopScanning();*/
    }

    @Override
    public void onResume() {

        super.onResume();
        //Update context
        BGService.initContext(this);

        //Make connected screen visible
        if (statusLabel.getText().toString().contains(EmpaStatus.CONNECTED.toString()) &&
                !statusLabel.getText().toString().contains(EmpaStatus.DISCONNECTED.toString())) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connectButton.setVisibility(View.INVISIBLE);
                    dataCnt.setVisibility(View.VISIBLE);
                }
            });
            updateLabel(statusLabel, statusLabel.getText().toString());
            updateLabel(deviceNameLabel, device);
        }else{
            updateLabel(statusLabel, "ANOTHER");
            stopService(new Intent(appContext, BGService.class));
            //Start service
            BGService.initContext(appMain);
            startService(new Intent(appContext, BGService.class));
            BGService.serviceStarted = true;

        }

    }

    @Override
    public void onStart() {
        super.onStart();

       /* //Update context
        BGService.initContext(this);

        //Make connected screen visible
        if (statusLabel.getText().toString().contains(EmpaStatus.CONNECTED.toString()) &&
                !statusLabel.getText().toString().contains(EmpaStatus.DISCONNECTED.toString()))
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connectButton.setVisibility(View.INVISIBLE);
                    dataCnt.setVisibility(View.VISIBLE);
                }
            });*/

        if (!BGService.serviceStarted || !alreadyInitialized) {
            System.out.println("Starting app - mainActivity create first time!!");

            alreadyInitialized = true;

            //USB Connect start
            if (mHost == null) {
                mHost = new USBHost(this, this);
                usbCommandValue = "waiting for a command..";
                mHost.connected = false;
                //USB Broadcast receiver:
                attachUSBReceiver(mHost);

            }else{
                //There a USB host active, keep USB button connected
                mHost.updateConnectedStatus("Connected", "USB HOST CONNECTED - established!  :) ", false);
            }


            //Start background service
            //Set context
            appContext = this;
            appMain = this;
            BGService.initContext(appMain);
            startService(new Intent(appContext, BGService.class));

            //Server
            //Initialize server connector
            sendDataTimer = new SendDataTimer(this);
            //TODO SERVER BACKUP
            sendDataTimer.startTimer();

        }else{
            //Set the view for Connected
            System.out.println("Coming back - mainActivity create again!!");

            //Empatica connected status
            if (connectionS != null && connectionS.contains("CONNECTED") && !connectionS.contains("DISCONNECTED")) {
                //Make connected screen visible
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectButton.setVisibility(View.INVISIBLE);
                        dataCnt.setVisibility(View.VISIBLE);
                    }
                });
                updateLabel(internet_conn, "INTERNET");
                updateLabel(statusLabel, connectionS);
                updateLabel(deviceNameLabel, "To: " + device);
            }else {
                System.out.println("mainActivity Empatica disconnected");
            }

            //USB connected status
            if (mHost != null && mHost.isConnected()) {
                //There a USB host active, keep USB button connected
                updateLabel(usbConenctionStatus, "USB HOST CONNECTED - established!  :) ");
                updateButton(connectUSBButton, "Connected", getResources().getColor(R.color.dark_green_paleta), false);
                System.out.println("mainActivity USB CONNECTED");

            }else
                System.out.println("mainActivity USB disconnected");



        }


    }

    @Override
    protected void onDestroy() {
        //System.out.println("App destroyed");
        keepScreenON(false);
        super.onDestroy();
        //BGService.deviceManager.cleanUp();

        //Stop service
       // stopService(new Intent(this, BGService.class));

    }

    @Override
    public void onStop() {
        //startStreamingPackets();
        super.onStop();


    }

    @Override
    public void onRestart() {
        super.onRestart();
        BGService.initContext(this);
    }

    /* *******************************
    * EMULATE BACK AND HOME BUTTONS: Do not destroy app
     */
    @Override
    public void onBackPressed() {
        if (statusLabel.getText().toString().contains(EmpaStatus.CONNECTED.toString())){
            //CONNECTED
            System.out.println("Back pressed and connected");
            moveTaskToBack(true);
    }else {
            System.out.println("Back pressed and not connected");
            //Any other state... destroy app
            stopService(new Intent(this, BGService.class));
            //Stop USB receiver
            releaseUSBReceiver();
            super.onDestroy();
            finish();
            System.exit(0);
        }
    }

    /* **********************************************************************
     * Empatica Status callbacks
     */

    @Override
    public void didDiscoverDevice(BluetoothDevice bluetoothDevice, String deviceName, int rssi, boolean allowed) {
        // Check if the discovered device can be used with your API key. If allowed is always false,
        // the device is not linked with your API key. Please check your developer area at
        // https://www.empatica.com/connect/developer.php
        if (allowed) {
            // Stop scanning. The first allowed device will do.
            BGService.deviceManager.stopScanning();
            try {
                // Connect to the device
                device =  deviceName;
                //STOP CONNECTIOM ?
                BGService.deviceManager.connectDevice(bluetoothDevice);
                updateLabel(deviceNameLabel, "To: " + deviceName);

                //In case it does not connect...
                //Connect button visible, except when connected
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Hide read data
                        //Display ONLY the connect button
                        connectButton.setVisibility(View.VISIBLE);
                    }
                });

            } catch (ConnectionNotAllowedException e) {
                // This should happen only if you try to connect when allowed == false.
                Toast.makeText(MainActivity.this, "Sorry, you can't connect to this device", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void didRequestEnableBluetooth() {
        // Request the user to enable Bluetooth
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // The user chose not to enable Bluetooth
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            // You should deal with this
            updateLabel(statusLabel, "BLUETOOTH NOT ENABLED: \n Empatica can not connect");
            connectionS = "BLUETOOTH NOT ENABLED: \n Empatica can not connect";
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void didUpdateSensorStatus(EmpaSensorStatus status, EmpaSensorType type) {
        // No need to implement this right now
        System.out.println("EmpaSensor: "+status+ " and "+ type);
    }

    @Override
    public void didUpdateStatus(EmpaStatus status) {
        System.out.println("Did update status: "+status);
        // Update the UI
        updateLabel(statusLabel, status.name());
        connectionS = status.name();

        // The device manager has established a connection
        if (status == EmpaStatus.CONNECTED) {
            // Stop streaming after STREAMING_TIME
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connectButton.setVisibility(View.INVISIBLE);
                    dataCnt.setVisibility(View.VISIBLE);
                    /*new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //TODO  Disconnect device
                            //BGService.deviceManager.disconnect();
                        }
                    }, STREAMING_TIME);*/
                }
            });
            updateLabel(deviceNameLabel, device);
            connectionS  = status.name();
            //UPDATE CONNECTED STATUS
            BGService.EmpaticaDisconnected = false;

        }
        // The device manager is ready for use
        else if (status == EmpaStatus.READY) {
            //Make sure connection button is not used
            //See only connect button
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connectButton.setVisibility(View.INVISIBLE);
                }
            });
            updateLabel(statusLabel, status.name() + " - Turn on your device");
            connectionS  = status.name();
            if (BGService.deviceManager != null ) {
                BGService.deviceManager.stopScanning();
                // Start scanning
                BGService.deviceManager.startScanning();
            }
        }
        // The device manager disconnected from a device
        else if (status == EmpaStatus.DISCONNECTED) {
            //See only connect button
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connectButton.setVisibility(View.VISIBLE);
                    dataCnt.setVisibility(View.INVISIBLE);
                }
            });
            updateLabel(statusLabel, status.name());
            updateLabel(deviceNameLabel, "---");
            BGService.EmpaticaDisconnected = true;
        }else {
            //Update labels in this non recognize state
            updateLabel(statusLabel, status.name() + " - ? Turn on your device");
            connectionS  = status.name();
        }
    }

    // Update a label with some text, making sure this is run in the UI thread
    public void updateLabel(final TextView label, final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                label.setText(text);
            }
        });
    }

    public void updateButton(final TextView button, final String text, final int color, final boolean en) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setText(text);
                button.setBackgroundColor(color);
                button.setEnabled(en);
            }
        });
    }

    /* ***********************************************************
        ON CLICK LISTENERS FOR BUTTONS
        ********************************************************
     */

    /**
     * Connect Empatica
     * Starts connection process when clicked
     */
    private View.OnClickListener connectListener = new View.OnClickListener() {
        public void onClick(View view) {
            view.setBackgroundColor(getResources().getColor(R.color.dark_green_paleta));
            if (BGService.deviceManager != null) {
                //If deviceManager is scanning, we stop it
                BGService.deviceManager.stopScanning();
                //Change empa_status to ready if there is Internet connection
                if ( checkInternetConnectivity()) {
                    BGService.deviceManager.authenticateWithAPIKey(EMPATICA_API_KEY);
                    updateLabel(internet_conn, "INTERNET");

                } else
                    //No internet connection - WE CAN NOT USE THE APP
                    updateLabel(internet_conn, "NO INTERNET");
                //BGService.deviceManager.startScanning();
                view.setVisibility(View.INVISIBLE);
                view.setBackgroundColor(getResources().getColor(R.color.ligher_green_paleta));

            }

        }
    };

    /**
     * BGService STOP
     * Stops the background service when clicked
     */
    private View.OnClickListener stopListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (BGService.serviceStarted) {
                //Stop service
                stopService(new Intent(appContext, BGService.class));
                BGService.serviceStarted = false;
                view.setBackgroundColor(getResources().getColor(R.color.dark_green_paleta));
                startServiceButton.setBackgroundColor(getResources().getColor(R.color.ligher_green_paleta));
                BGService.EmpaticaDisconnected = true;

            }

        }
    };

    /**
     * BGService START
     * Starts the background service when clicked
     */
    private View.OnClickListener startListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (!BGService.serviceStarted) {
                //Start service
                BGService.initContext(appMain);
                startService(new Intent(appContext, BGService.class));
                BGService.serviceStarted = true;
                view.setBackgroundColor(getResources().getColor(R.color.dark_green_paleta));
                stopServiceButton.setBackgroundColor(getResources().getColor(R.color.ligher_green_paleta));


            }

        }
    };

    /**
     * USB Connect
     * Connect to PC via USB
     */

    View.OnClickListener connectToPcListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //
            mHost.intent = new Intent(mHost.ctx, MainActivity.class);
            mHost.mHandler = new Handler();

            //initialize server socket in a new separate thread
            new Thread(mHost.initializeConnection).start();
            String msg = "Attempting to connect…";
            Toast.makeText(mHost.ctx, msg, Toast.LENGTH_LONG).show();

        }
    };

    /**
     * Delete Database
     */

    View.OnClickListener deleteDatabaseListener  = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           //Delete permanent database content
            BGService.storingManager.myDB.deleteAllRows(BGService.empaticaTableName);

            deleteB.setBackgroundColor(Color.LTGRAY);

        }
    };

    /* **************************************************
       END OnClickLiseners
     */


    public static void setCommandValue(String val) {
        usbCommandValue = val;

    }


    public boolean checkInternetConnectivity(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;

    }


    public void popUpFragment(Class c){
        //Display the dialog
        Intent dialogIntent = new Intent(this, c);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);
        //Toast.makeText(mActivity, "No connection stablished!", Toast.LENGTH_LONG).show();
    }

    public void keepScreenON(boolean on){
        if (on){
        /* This code together with the one in onDestroy()
         * will make the screen be always on until this Activity gets destroyed. */

            //final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            //this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "EmpaticaWake");
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            //this.mWakeLock.acquire();
        }else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            //this.mWakeLock.release();


        }
    }

    /**
     * Initialize USBReceiver to monitor USB connect/disconnect
     */
    private void attachUSBReceiver(USBHost uhost){
        usbBroadcast = new USBReceiver(uhost);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(usbBroadcast, filter);
    }

    private void releaseUSBReceiver(){
        usbBroadcast.removeUsbHost();
        unregisterReceiver(usbBroadcast);

    }


}
