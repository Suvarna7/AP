package com.sensors.mobile.app.MultipleCommunication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bodymedia.btle.packet.*;
import com.sensors.mobile.app.BM.SenseWearApplication;
import com.sensors.mobile.app.Database.DataStoring;
import com.sensors.mobile.app.Database.Database;
import com.sensors.mobile.app.R;
import rx.Observer;
import zephyr.android.BioHarnessBT.BTClient;

import java.util.Set;

/**
 * Created by Caterina on 4/16/2015.
 */
public class MainActivityMultiple extends Activity{

    /*****************************************************
     * EXPERIMENT INFO
     * *********************************************
     */
    //Experiment data
    private String dispPatientId="";
    private String dispDevice="";

    //Context
    public static Context ctx;

    // Database and server information
    ProgressDialog prgDialog;

    /*****************************************************
     * ZEPHYR PARAMS:
     * *********************************************
     */
    // Bioharness chosen MAC address
    public String chosenBH = "";


    //Called when the activity is first created

    public static BluetoothAdapter adapter = null;
    BTClient _bt;
    NewConnectedListener _NConnListener;
    private boolean connected;
    private final int HEART_RATE = 0x100;
    private final int RESPIRATION_RATE = 0x101;
    //private final int SKIN_TEMPERATURE = 0x102;
    private final int POSTURE = 0x103;
    private final int PEAK_ACCLERATION = 0x104;
    private final int VMU = 0x105;
    private final int ACTIVITY = 0x106;
    private final int TYPE = 0x107;
    private final int TEMP = 0x108;
    private final int CALORIES = 0x109;




    /*****************************************************
 * BODYMEDIA PARAMS:
 * *********************************************
 */
    private DeviceStream streamer;
    private BMConnectedListeners bmConnectedListener;
    private BMPairing bmPairing;

    private Button bmRecordON;
    private Button bmRecordOFF;

    private BroadcastReceiver bluetoothStateReceiverMultiple;

    public static boolean pairedNotFinished;


    /*****************************************************
     * HANDLER
     * *********************************************
     */
    //Handler
    final  public Handler Newhandler = new Handler(){
        public void handleMessage(Message msg)
        {
            TextView tv;
            switch (msg.what)
            {
                case HEART_RATE:
                    String HeartRatetext = msg.getData().getString("HeartRate");
                    tv = (EditText)findViewById(R.id.labelHeartRate);
                    if (tv != null)tv.setText(HeartRatetext);
                    break;

                case RESPIRATION_RATE:
                    String RespirationRatetext = msg.getData().getString("RespirationRate");
                    tv = (EditText)findViewById(R.id.labelRespRate);
                    if (tv != null)tv.setText(RespirationRatetext);

                    break;


                case POSTURE:
                    String PostureText = msg.getData().getString("Posture");
                    tv = (EditText)findViewById(R.id.labelPosture);
                    if (tv != null)tv.setText(PostureText);


                    break;

                case PEAK_ACCLERATION:
                    String PeakAccText = msg.getData().getString("PeakAcceleration");
                    tv = (EditText)findViewById(R.id.labelPeakAcc);
                    if (tv != null)tv.setText(PeakAccText);

                    break;

                case VMU:
                    String vmuText = msg.getData().getString("VMU");
                    tv = (EditText)findViewById(R.id.labelVMU);
                    if (tv != null)tv.setText(vmuText);


                    break;

                case ACTIVITY:
                    String ActivityText = msg.getData().getString("Activity");
                    tv = (EditText)findViewById(R.id.labelActivity);
                    if (tv != null)tv.setText(ActivityText);
                    break;
                case TYPE:
                    String TypeText = msg.getData().getString("Type");
                    tv = (EditText)findViewById(R.id.labelActType );
                    if (tv != null)tv.setText(TypeText);
                    break;

                case TEMP:
                    String TemperatureText = msg.getData().getString("Temperature");
                    tv = (EditText)findViewById(R.id.labelTemperature );
                    if (tv != null)tv.setText(TemperatureText);
                    break;

                case CALORIES:
                    String CaloriesText = msg.getData().getString("Calories");
                    tv = (EditText)findViewById(R.id.labelCalories );
                    if (tv != null)tv.setText(CaloriesText);
                    break;


            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiple);

        //Context
        ctx = this;
        //Start automatic saving&sending

        //Init BMConnectionmessages


        streamer = new DeviceStream();
        bmPairing = new BMPairing();
        pairedNotFinished = true;

        //Retrieve values of id and zephyr
        Intent myIntent = getIntent();
        if (myIntent != null) {
            dispPatientId = myIntent.getStringExtra("PATIENT_ID");
            dispDevice = myIntent.getStringExtra("DEVICE_SERIAL_NUM");



        }

        //Set the experiments values
        //TextView experiment = (TextView)findViewById(R.id.displayExperimentID);
        //experiment.setText("Exp 123");

        /////////////////    AUTOMATIC:    /////////////////////////
        //Init database
        Database.initDatabase(this);
        //Start the DataStoring Manager
        AlarmManager alarmManager=(AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctx, DataStoring.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 30000,
                pendingIntent);

        /////////////////    BUTTONS:    /////////////////////////
        /*Obtaining the handle to act on zephyr DISCONNECT button*/
        Button btnConnect = (Button) findViewById(R.id.zephyrON);
        if (btnConnect != null )
        {
            btnConnect.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ServiceCast")
                public void onClick(View v) {
                    //First connect to the zephyr device
                    if (!connected){
                        String BhMacID =chosenBH;
                        //String BhMacID = "00:07:80:9D:8A:E8";
                        //String BhMacID = "00:07:80:88:F6:BF";
                        adapter = BluetoothAdapter.getDefaultAdapter();

                        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();

                        if (pairedDevices.size() > 0)
                        {
                            for (BluetoothDevice device : pairedDevices)
                            {
                                if (device.getName().startsWith("BH") && device.getName().endsWith(dispDevice))
                                {
                                    //MAC Address from BioHarness paired devices list
                                    BluetoothDevice btDevice = device;
                                    BhMacID = btDevice.getAddress();
                                    break;

                                }
                                else{
                                    BhMacID = "null";
                                }

                            }


                        }
                        if (BhMacID.equals("null")){
                            TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
                            String ErrorText  = "Unable to Connect: wrong BH number !";
                            tv.setText(ErrorText);
                        }
                        else{
                            //BhMacID = btDevice.getAddress();
                            BluetoothDevice Device = adapter.getRemoteDevice(BhMacID);
                            String DeviceName = Device.getName();
                            _bt = new BTClient(adapter, BhMacID);
                            _NConnListener = new NewConnectedListener(Newhandler,Newhandler);
                            _bt.addConnectedEventListener(_NConnListener);

                            TextView tv1 = (EditText)findViewById(R.id.labelHeartRate);
                            tv1.setText("000");

                            tv1 = (EditText)findViewById(R.id.labelRespRate);
                            tv1.setText("0.0");



                            tv1 = 	(EditText)findViewById(R.id.labelPosture);
                            tv1.setText("000");

                            tv1 = 	(EditText)findViewById(R.id.labelPeakAcc);
                            tv1.setText("0.0");

                            if(_bt.IsConnected())
                            {
                                _bt.start();
                                TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
                                String ErrorText  = "Connected to BioHarness "+DeviceName;
                                tv.setText(ErrorText);

                                //Start the DataStoring Manager
                                //AlarmManager alarmManager=(AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
                                //Intent intent = new Intent(ctx, DataStoring.class);
                                //PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);
                               // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),30000,
                                        //pendingIntent);

                                connected = true;

                            }
                            else
                            {
                                TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
                                String ErrorText  = "Unable to Connect !";
                                tv.setText(ErrorText);
                            }
                        }
                    }
                }
            });
        }

        /*Obtaining the handle to act on zephyr DISCONNECT button*/
       Button btnDisconnect = (Button) findViewById(R.id.zephyrOFF);
        if (btnDisconnect != null)
        {
            btnDisconnect.setOnClickListener(new View.OnClickListener() {
                @Override
				//Functionality to act if the button DISCONNECT is touched
                public void onClick(View v) {
					//Reset the global variables
                    TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
                    String ErrorText  = "Disconnected from BioHarness!";
                    tv.setText(ErrorText);

					 //Update database values
                    //Database.updateDatabase(ctx, NewConnectedListener.sensorValues, Database.zephyrTableName);
                   // NewConnectedListener.sensorValues.clear();

					//This disconnects listener from acting on received messages
                    _bt.removeConnectedEventListener(_NConnListener);
					//Close the communication with the device & throw an exception if failure
                    //Close only if _bt is not null
                    if (_bt == null){
                        //Do nothing
                    }
                    else {
                        _bt.Close();
                    }
                    connected = false;

                    //Stop the data sotirng manager
                    AlarmManager alarmManager=(AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(ctx, DataStoring.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);
                    alarmManager.cancel(pendingIntent);

                }
            });
        }

        /*Obtaining the handle to act on the bodyMedia CONNECT button*/
        ctx = this;
        bmRecordON = (Button) findViewById(R.id.streamRecordBodymediaON);
        bmRecordON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO First, pair the device
                adapter = BluetoothAdapter.getDefaultAdapter();
                SenseWearApplication.get().getArmband();
                bmConnectedListener = new BMConnectedListeners(Newhandler);

                bmPairing.doPairing();
                setUpBluetoothStateReceiver();

                while (pairedNotFinished && SenseWearApplication.get().getArmband()==null){
                    //Do nothing, just wait
                }
                //TODO Second, connect to that device
                System.out.println("Connected BM: " + SenseWearApplication.get().getArmband());
                if (SenseWearApplication.get().getArmband()==null)
                    pairedNotFinished = true;


                //Finally, stream data packets

                startBodyMediaListeners();




            }});




        /*Obtaining the handle to act on the bodyMedia DISCONNECT button*/
        bmRecordOFF = (Button) findViewById(R.id.streamRecordBodymediaOFF);
        bmRecordOFF.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                streamer.setStreamSensorsAccelerometerEnabled(true);

                //Stop streaming
                streamer.setAllEnabled(false);
                //streamer.setAllEnabled(true);
                streamer.setHighBit(true);

                SenseWearApplication.get().getArmband().configureStreaming(streamer).subscribe(
                        new Observer<DeviceStream>() {
                            @Override
                            public void onCompleted() {
                                System.out.println("STREAM OFF DeviceUpdates packets has successfully sent");
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                System.out.println("STREAM OFF Something went wrong trying to send DeviceUpdates packet");
                            }

                            @Override
                            public void onNext(DeviceStream deviceStream) {
                                System.out.println("STREAM OFF Received DeviceUpdates packet: " + deviceStream);
                            }
                        }
                );

            }
        });


    }

   @Override
    protected void onStop(){
        super.onStop();
        //Stop the AlarmManager
        AlarmManager alarmManagerstop = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intentstop = new Intent(this, DataStoring.class);
        PendingIntent senderstop = PendingIntent.getBroadcast(this, 1234567, intentstop, 0);
        alarmManagerstop.cancel(senderstop);

       //Stop BodyMedia listeners
       bmPairing.armbandManager.stopScan();
       bmPairing.armbandManager.clearPairingListener();
       bmPairing.armbandManager.removeConnectionListener(bmPairing.connectionListenerMultiple);
       shutDownBluetoothStateReceiver();

    }
    @Override
     protected void onPause(){
        super.onPause();
         //Stop the AlarmManager
        AlarmManager alarmManagerstop = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intentstop = new Intent(this, DataStoring.class);
        PendingIntent senderstop = PendingIntent.getBroadcast(this, 1234567, intentstop, 0);
        alarmManagerstop.cancel(senderstop);
     }

    @Override
    protected void onResume(){
        super.onResume();
        //Start the AlarmManager
        AlarmManager alarmManager=(AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctx, DataStoring.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 30000,
                pendingIntent);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        shutDownBluetoothStateReceiver();    }

    //Bluetooth Receiver

    private void setUpBluetoothStateReceiver() {
        System.out.println("Set the bt");
        if(!adapter.isEnabled())
            adapter.enable();

        bluetoothStateReceiverMultiple = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                //TODO is never called!!!!
                System.out.println("We receive!!");


                if (adapter.isEnabled()) {
                    System.out.println("Bluetooth Enabled");
                    bmPairing.startPairingProcess();
                }
            }

        };
        registerReceiver(bluetoothStateReceiverMultiple, new IntentFilter(adapter.ACTION_STATE_CHANGED));

    }

    public void shutDownBluetoothStateReceiver() {
        if (bluetoothStateReceiverMultiple != null) {
            this.unregisterReceiver(bluetoothStateReceiverMultiple);
            bluetoothStateReceiverMultiple = null;
        }
    }

    public void startBodyMediaListeners(){

        try {
            //ADD stream listener
            streamer.setStreamSensorsAccelerometerEnabled(true);
            streamer.setStreamSensorsCalibratedEnabled(true);
            streamer.setStreamSensorsGSRTemperatureEnabled(true);
            streamer.setStreamSensorsECGRawEnabled(true);
            //streamer.setAllEnabled(true);
            streamer.setHighBit(true);

            SenseWearApplication.get().getArmband().configureStreaming(streamer).subscribe(
                    new Observer<DeviceStream>() {
                        @Override
                        public void onCompleted() {
                            System.out.println("STREAM DeviceUpdates packets has successfully sent");
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            System.out.println("STREAM Something went wrong trying to send DeviceUpdates packet");
                        }

                        @Override
                        public void onNext(DeviceStream deviceStream) {
                            System.out.println("STREAM Received DeviceUpdates packet: " + deviceStream);
                        }
                    }
            );
            //ADD minute listener

            SenseWearApplication.get().getArmband().getStreamingService().addStreamingArmbandListener(
                    bmConnectedListener.highRateArmbandListener);


            DeviceUpdates updates = new DeviceUpdates();
            updates.setUpdateMinuteEnabled(true);
            SenseWearApplication.get().getArmband().configureUpdate(updates, bmConnectedListener.minuteListener, null, null).subscribe(
                    new Observer<DeviceUpdates>() {
                        @Override
                        public void onCompleted() {
                            System.out.println("DeviceUpdates packets has successfully sent");
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            System.out.println("Something went wrong trying to send DeviceUpdates packet");
                        }

                        @Override
                        public void onNext(DeviceUpdates deviceUpdates) {
                            System.out.println("Received DeviceUpdates packet: " + deviceUpdates);
                        }
                    }
            );
        } catch (Exception e){
            System.out.println("BodyMedia ON button: "+ e);
        }
    }








}
