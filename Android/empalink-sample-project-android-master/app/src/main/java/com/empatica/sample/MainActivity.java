package com.empatica.sample;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
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


/**
 * EmpaLink Main Activity
 * Implements EmpaStatusDelegate and holds the GUI of the app
 * Class modfied from the EmpaLink Sample code
 *
 * @author Caterina Lazaro
 * @version 1.0 Jun 2016
 */

public class MainActivity extends AppCompatActivity implements EmpaStatusDelegate   {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long STREAMING_TIME = 600000; // Stops streaming 10 min after connection
    public static final String EMPATICA_API_KEY = "f92ddb7260a54f5790038ba90ef4d1ad"; // TODO insert your API Key here

    //GUI labels
    public TextView accel_xLabel;
    public TextView accel_yLabel;
    public TextView accel_zLabel;
    public TextView bvpLabel;
    public TextView edaLabel;
    public TextView ibiLabel;
    public TextView temperatureLabel;
    public TextView batteryLabel;
    public TextView statusLabel;
    public TextView deviceNameLabel;
    public RelativeLayout dataCnt;
    //Buttons
    public static Button connectButton;
    private Button stopServiceButton;
    private Button startServiceButton;


    //App context
    private Context appContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize vars that reference UI components
        statusLabel = (TextView) findViewById(R.id.status);
        dataCnt = (RelativeLayout) findViewById(R.id.dataArea);
        accel_xLabel = (TextView) findViewById(R.id.accel_x);
        accel_yLabel = (TextView) findViewById(R.id.accel_y);
        accel_zLabel = (TextView) findViewById(R.id.accel_z);
        bvpLabel = (TextView) findViewById(R.id.bvp);
        edaLabel = (TextView) findViewById(R.id.eda);
        ibiLabel = (TextView) findViewById(R.id.ibi);
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

        //Set context
        appContext = this;

        //Start service
        BGService.initContext(this);
        if (!BGService.serviceStarted) {
            //Start background service
            startService(new Intent(this, BGService.class));
        }


    }




    @Override
    protected void onPause() {
        super.onPause();

        if (BGService.deviceManager !=null)
            BGService.deviceManager.stopScanning();
    }

    @Override
    public void onResume() {
        super.onResume();

        //Update context
        BGService.initContext(this);


    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        //BGService.deviceManager.cleanUp();

        //Stop service
        stopService(new Intent(this, BGService.class));

    }

    @Override
    public void onStop(){
        //startStreamingPackets();
        super.onStop();

    }

    @Override
    public void onRestart(){
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

    /* ***********************************************************
        OnClickListeners
     */

    /**
     * Starts connection process when clicked
     */
    private  View.OnClickListener connectListener = new View.OnClickListener() {
        public void onClick(View view) {
            view.setBackgroundColor(getResources().getColor(R.color.dark_green_paleta));
            if (BGService.deviceManager != null){
                //If deviceManager is scanning, we stop it
                BGService.deviceManager.stopScanning();
                //Change empastatus to ready
                BGService.deviceManager.authenticateWithAPIKey(EMPATICA_API_KEY);
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
            if (BGService.serviceStarted ){
                //Stop service
                stopService(new Intent(appContext, BGService.class));
                BGService.serviceStarted = false;
                view.setBackgroundColor(getResources().getColor(R.color.dark_green_paleta));
                startServiceButton.setBackgroundColor(getResources().getColor(R.color.ligher_green_paleta));


            }

        }
    };

    /**
     * Starts the background service when clicked
     */
    private View.OnClickListener startListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (!BGService.serviceStarted ){
                //Stop service
                startService(new Intent(appContext, BGService.class));
                BGService.serviceStarted = true;
                view.setBackgroundColor(getResources().getColor(R.color.dark_green_paleta));
                stopServiceButton.setBackgroundColor(getResources().getColor(R.color.ligher_green_paleta));


            }

        }
    };
}