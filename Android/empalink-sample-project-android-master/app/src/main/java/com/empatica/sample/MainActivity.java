package com.empatica.sample;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.empatica.empalink.ConnectionNotAllowedException;
import com.empatica.empalink.config.EmpaSensorStatus;
import com.empatica.empalink.config.EmpaSensorType;
import com.empatica.empalink.config.EmpaStatus;
import com.empatica.empalink.delegate.EmpaStatusDelegate;
import com.empatica.sample.Database.IITDatabaseManager;
import com.empatica.sample.Server.IITServerConnector;
import com.empatica.sample.USB.USBHost;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * EmpaLink Main Activity
 * Implements EmpaStatusDelegate and holds the GUI of the app
 * Class modfied from the EmpaLink Sample code
 *
 * @author Caterina Lazaro
 * @version 1.0 Jun 2016
 */

public class MainActivity extends AppCompatActivity implements EmpaStatusDelegate {

    private static final int REQUEST_ENABLE_BT = 1;
    public static final long STREAMING_TIME = 25 * 60000; // Check key after 25 min
    public static final String EMPATICA_API_KEY = "f92ddb7260a54f5790038ba90ef4d1ad"; // TODO insert your API Key here

    //GUI labels
    //Connetion status
    public TextView internet_conn;
    public TextView statusLabel;
    public TextView deviceNameLabel;
    private static String device;

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

    //App context
    private Context appContext;
    private MainActivity appMain;
    public boolean connected;
    private static boolean alreadyInitialized = false;

    //USB Connection
    public TextView usbConenctionStatus;
    public static USBHost mHost;
    public TextView usbCommand;
    public static String usbCommandValue;

    //IIT Server manager and automatic sending
    public static Timer sendDataTimer;
    private static final int SENDING_AMOUNT = 100;
    //IIT Server manager
    IITServerConnector myServerManager;
    private static final String jsonID = "empaticaJSON";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("App created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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



        //Set context
        appContext = this;
        appMain = this;


        if (!BGService.serviceStarted || !alreadyInitialized) {
            alreadyInitialized = true;
            //USB Connect start
            mHost = new USBHost(this, this);

            //Start service
            BGService.initContext(this);
            //Start background service
            startService(new Intent(this, BGService.class));

            usbCommandValue = "waiting for a command..";
            connected = false;

            //Server
            //Initialize server connector
            //Send data to IIT
            myServerManager = new IITServerConnector(jsonID, IITServerConnector.IIT_SERVER_UPDATE_VALUES_URL,
                    IITServerConnector.IIT_SERVER_READ_TABLE_URL, BGService.myDB, this);
            sendDataTimer = new Timer();
            //TODO NO SERVER BACKUP
            //startSendingTimer();
        }else{
            //Set the view for Connected
            System.out.println("Coming back!!");
            //Make connected screen visible

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectButton.setVisibility(View.INVISIBLE);
                        dataCnt.setVisibility(View.VISIBLE);
                        statusLabel.setText(EmpaStatus.CONNECTED.toString());


                    }
                });
            updateLabel(internet_conn, "INTERNET");
            updateLabel(statusLabel, EmpaStatus.CONNECTED.toString());
            updateLabel(deviceNameLabel, "To: " + device);

        }




    }


    @Override
    protected void onPause() {
        System.out.println("App paused");

        super.onPause();

        /*if (BGService.deviceManager != null)
            BGService.deviceManager.stopScanning();*/

    }

    @Override
    public void onResume() {
        System.out.println("App resumed");

        super.onResume();
        //Update context
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
            });


    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("App started");


        //Update context
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
            });


    }

    @Override
    protected void onDestroy() {
        System.out.println("App destroyed");

        super.onDestroy();
        //BGService.deviceManager.cleanUp();

        //Stop service
       // stopService(new Intent(this, BGService.class));

    }

    @Override
    public void onStop() {
        System.out.println("App stopped");
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
        if (statusLabel.getText().toString().contains(EmpaStatus.CONNECTED.toString()))
            //CONNECTED
            moveTaskToBack(true);
        else {
            //Any other state... destroy app
            stopService(new Intent(this, BGService.class));
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

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void didUpdateSensorStatus(EmpaSensorStatus status, EmpaSensorType type) {
        // No need to implement this right now
    }

    @Override
    public void didUpdateStatus(EmpaStatus status) {
        System.out.println("Did update status: "+status);
        // Update the UI
        updateLabel(statusLabel, status.name());

        // The device manager is ready for use
        if (status == EmpaStatus.READY) {
            //Make sure connection button is not used
            //See only conenct button
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connectButton.setVisibility(View.INVISIBLE);
                }
            });
            updateLabel(statusLabel, status.name() + " - Turn on your device");
            // Start scanning
            BGService.deviceManager.startScanning();
            // The device manager has established a connection
        } else if (status == EmpaStatus.CONNECTED) {
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
            //UPDATE CONNECTED STATUS
            connected = true;

            // The device manager disconnected from a device
        } else if (status == EmpaStatus.DISCONNECTED) {
            //See only conenct button
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connectButton.setVisibility(View.VISIBLE);
                    dataCnt.setVisibility(View.INVISIBLE);
                }
            });
            updateLabel(deviceNameLabel, "");
            connected = false;

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
        OnClickListeners
     */

    /**
     * Starts connection process when clicked
     */
    private View.OnClickListener connectListener = new View.OnClickListener() {
        public void onClick(View view) {
            view.setBackgroundColor(getResources().getColor(R.color.dark_green_paleta));
            if (BGService.deviceManager != null) {
                //If deviceManager is scanning, we stop it
                BGService.deviceManager.stopScanning();
                //Change empastatus to ready if there is Internet connection
                if ( checkInternetConnectivity() != null) {
                    BGService.deviceManager.authenticateWithAPIKey(EMPATICA_API_KEY);
                    updateLabel(internet_conn, "INTERNET");

                } else
                    //No itnernet connection - WE CAN NOT USE THE APP
                    updateLabel(internet_conn, "NO INTERNET");
                //BGService.deviceManager.startScanning();
                view.setVisibility(View.INVISIBLE);
                view.setBackgroundColor(getResources().getColor(R.color.ligher_green_paleta));

            }

        }
    };

    /**
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
                connected = false;


            }

        }
    };

    /**
     * Starts the background service when clicked
     */
    private View.OnClickListener startListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (!BGService.serviceStarted) {
                //Start service
                BGService.initContext(appMain);
                startService(new Intent(appContext, BGService.class));
                BGService.serviceStarted = true;
                int c = getResources().getColor(R.color.dark_green_paleta);
                view.setBackgroundColor(getResources().getColor(R.color.dark_green_paleta));
                stopServiceButton.setBackgroundColor(getResources().getColor(R.color.ligher_green_paleta));


            }

        }
    };

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



    public static void setCommandValue(String val) {
        usbCommandValue = val;

    }

    /********************************************************
     * SEND VALUES TIMER
     */

    /**
     * Timer to automatically send data to server
     */

    private void startSendingTimer() {

        sendDataTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //TODO
               //mHost.sendUSBmessage(USBHost._END_COMMAND);

                if (connected) {
                    //Store values in database every minute and reset:

                    new AsyncTask<URL, Integer, Long>() {
                        @Override
                       protected Long doInBackground(URL... urls) {
                            //Send saved information to IIT server
                            if ( checkInternetConnectivity() != null) {
                                //1. Obtain not synchronized values from database

                                // List<Map<String, String>> listReadToServer = null;
                                List<Map<String, String>> listReadToServer = BGService.myDB.getAllNotCheckedValues(BGService.empaticaMilTableName, BGService.columnsTable,
                                        IITDatabaseManager.upDateColumn, IITDatabaseManager.updatedStatusNo);
                                //List<Map<String, String>> listReadToServer = new ArrayList<Map<String, String>>();
                                //2. Send to Server
                                if (listReadToServer != null) {

                                    List<Map<String, String>> temp = new ArrayList<Map<String, String>>();
                                    //List too long: break in smaller chunks
                                    for (int i = 0; i < listReadToServer.size(); i++) {
                                        Map<String, String> val = listReadToServer.get(i);
                                        val.put("table_name", BGService.empaticaMilTableName);
                                        temp.add(val);
                                        if ((i + 1) % SENDING_AMOUNT == 0) {
                                            System.out.println("Send first 500: " + myServerManager.sending);
                                            String jSon = IITServerConnector.convertToJSON(temp);
                                            myServerManager.sendToIIT(jSon, IITServerConnector.IIT_SERVER_UPDATE_VALUES_URL);

                                            System.out.println("Send status: " + myServerManager.sending);

                                            while (myServerManager.sending) {
                                                //wait to receive something
                                            }

                                            try {
                                                Thread.sleep(3000); // giving time to connect to wifi
                                            } catch (Exception e) {
                                                System.out.println("Exception while waiting to send:" + e);
                                            }

                                            temp = new ArrayList<Map<String, String>>();
                                        }
                                    }
                                    if (temp.size() > 0) {
                                        //Send last values
                                        System.out.println("Send remainig");

                                        String jSon = IITServerConnector.convertToJSON(temp);
                                        myServerManager.sendToIIT(jSon, IITServerConnector.IIT_SERVER_UPDATE_VALUES_URL);


                                        try {
                                            Thread.sleep(2000); // giving time to connect to wifi
                                        } catch (Exception e) {
                                            System.out.println("Exception while waiting to send:" + e);
                                        }
                                    }
                                    //myServerManager.debugServer("samples");

                                }
                                //DEBUG Table
                                //MainActivity.myDB.updateDatabaseTable("debug_table", new ArrayList<>(Arrays.asList(new String[]{"'A'"})), true);
                            }

                           return null;


                        }


                       protected void onPostExecute(Long result) {
                            //Do nothing: just try and be async
                           //System.out.println("Post sending");

                       }
                   }.execute();


                }
            }
        }

                , 30*1000, 60*1000); // delay(seconds*1000), period(seconds*1000)

    }



    private NetworkInfo checkInternetConnectivity(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    //*******************************************
    //  Database USB interface

    /**
     * Read not synchronized values from DatabaseManager and send via USB
     *
     * @return
     */

    public static String messageToUSB() {
        List<Map<String, String>> listReadToUSB = BGService.myDB.getNotUpdatedValues(BGService.empaticaMilTableName, BGService.columnsTable,
                IITDatabaseManager.syncColumn, IITDatabaseManager.syncStatusNo, USBHost.LOCAL_SENDING_AMOUNT);
        //Send to Server
        if (listReadToUSB != null) {
            String jSon = IITServerConnector.convertToJSON(listReadToUSB);
            return jSon;

        } else
            return null;
    }

    /**
     * Get all no sync values, and return a list of the JSON Strings to be sent
     * @return
     */
    public void messageAllAsync(String table, String check_column, String check_value, int max) {
        //TODO NO ACK NOW
        //List<Map<String, String>> listReadToUSB = BGService.myDB.getLastNSamples(table, BGService.columnsTable, check_column, check_value, max);
        //Collections.reverse(listReadToUSB);
        List<Map<String, String>> listReadToUSB=  BGService.myDB.getNotUpdatedValues (table, BGService.columnsTable, check_column, check_value, max);
        if (listReadToUSB !=null){
            mHost.sendUSBMessages(listReadToUSB);
            mHost.sendUSBmessage(USBHost._END_COMMAND);

        } else
            mHost.sendUSBmessage(USBHost._NO_DATA);
    }


}
