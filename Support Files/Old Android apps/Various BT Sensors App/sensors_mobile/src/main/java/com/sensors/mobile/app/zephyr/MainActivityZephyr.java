package com.sensors.mobile.app.zephyr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.os.Bundle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import com.sensors.mobile.app.Database.DataStoring;
import com.sensors.mobile.app.Database.Database;
import com.sensors.mobile.app.InitActivity;
import com.sensors.mobile.app.R;


import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import org.json.JSONException;
import zephyr.android.BioHarnessBT.*;


public class MainActivityZephyr extends Activity {
	
	//Experiment data
	public static String dispTableId="";
	private String dispBhDevice="";
	private static String BhMacIDAddress = "";

	// Database and server information
	ProgressDialog prgDialog;
	//public static Database controller;
	public static String _SERVER_ADDRESS = "http://216.47.158.133/mysqlSync/insertzephyrvalues.php";
	
	// Bioharness chosen MAC address
	public String chosenBH = "";

	
    //Called when the activity is first created
	
	BluetoothAdapter adapter = null;
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

	
	//Database button
	private static Button dbButton;
	private static Button sendDbButton;
	private static Button plotButton;

	//Context
	public Context ctx = this;
	
	//Handler
	 final  Handler Newhandler = new Handler(){
	    	public void handleMessage(Message msg)
	    	{
	    		TextView tv;
	    		switch (msg.what)
	    		{
	    		case HEART_RATE:
	    			String HeartRatetext = msg.getData().getString("HeartRate");
	    			tv = (EditText)findViewById(R.id.labelHeartRate);
	    			//System.out.println("Heart Rate Info is "+ HeartRatetext);
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
	    		
	    		
	    		}
	    	}

	    };

	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_zephyr);
        
	      //Retrieve values of id and zephyr
        	Intent myIntent = getIntent();	      
        	if (myIntent != null) {
				dispTableId = myIntent.getStringExtra("TABLE_ID");
	          dispBhDevice = myIntent.getStringExtra("DEVICE_SERIAL_NUM");
	          
	          TextView patientId = (TextView)findViewById(R.id.displayPatientID);
	          patientId.setText("ID: " + dispTableId);
	          
	          TextView zephyr = (TextView)findViewById(R.id.displayZephyrUsed);
	          zephyr.setText("BH: " + dispBhDevice);

	      }
	      
	      //Set the experiments values
          TextView experiment = (TextView)findViewById(R.id.displayExperimentID);
          experiment.setText("Exp 123");

        //Starting point: no connection
        connected = false;
        _bt = null;
        
        /*Sending a message to android that we are going to initiate a pairing request*/
        IntentFilter filter = new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST");
        /*Registering a new BTBroadcast receiver from the Main Activity context with pairing request event*/
       this.getApplicationContext().registerReceiver(new BTBroadcastReceiver(), filter);
        // Registering the BTBondReceiver in the application that the status of the receiver has changed to Paired
        IntentFilter filter2 = new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED");
       this.getApplicationContext().registerReceiver(new BTBondReceiver(), filter2);
       
     //Initialize Progress Dialog properties
       prgDialog = new ProgressDialog(this);
       prgDialog.setMessage("Synching SQLite Data with Remote MySQL DB. Please wait...");
       prgDialog.setCancelable(false);
        
      //Obtaining the handle to act on the CONNECT button
        TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
		String ErrorText  = "Not Connected to BioHarness !";
		 tv.setText(ErrorText);
		 
		 //controller = new Database(this);

        /***************************************************************************
         * BUTTONS
         * *************************************************************************
          */
        Button btnConnect = (Button) findViewById(R.id.ButtonConnect);
        if (btnConnect != null )
        {
        	btnConnect.setOnClickListener(new OnClickListener() {
        		@SuppressLint("ServiceCast")
				public void onClick(View v) {

					//Check if Bluetooth is on
					if (adapter ==null || !adapter.isEnabled()) {
						// Bluetooth is disabled on device. Start intent to ask the user about turning on the Bluetooth.
						Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivity(discoverableIntent);
					}
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
                        	if (device.getName().startsWith("BH") && device.getName().endsWith(dispBhDevice)) 
                        	{
                        		//MAC Address from BioHarness paired devices list
                        		BluetoothDevice btDevice = device;
                        		BhMacIDAddress = btDevice.getAddress();
                                break;

                        	}
                        	else{
								BhMacIDAddress = "null";
                        	}
                        		
                        }
                        
                        
        			}
        			if (BhMacIDAddress.equals("null")){
        				TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
    			 		String ErrorText  = "Unable to Connect: wrong BH number !";
    			 		tv.setText(ErrorText);
        			}
        			else{
        				//BhMacID = btDevice.getAddress();
        				BluetoothDevice Device = adapter.getRemoteDevice(BhMacIDAddress);
        				String DeviceName = Device.getName();
        				_bt = new BTClient(adapter, BhMacIDAddress);
        				_NConnListener = new NewConnectedListener(Newhandler,Newhandler);
        				_bt.addConnectedEventListener(_NConnListener);

						//First, scan to get the rssi value:
						//adapter.startLeScan(scanListener);

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
							if (!InitActivity.startStoring) {
								AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
								Intent intent = new Intent(ctx, DataStoring.class);
								PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);
								alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 30000,
										pendingIntent);
								InitActivity.startStoring = true;
							}
						 
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
        
        /*Obtaining the handle to act on the DISCONNECT button*/
        Button btnDisconnect = (Button) findViewById(R.id.ButtonDisconnect);
        if (btnDisconnect != null)
        {
        	btnDisconnect.setOnClickListener(new OnClickListener() {
				@Override
				/*Functionality to act if the button DISCONNECT is touched*/
				public void onClick(View v) {
					/*Reset the global variables*/
					TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
    				String ErrorText  = "Disconnected from BioHarness!";
					 tv.setText(ErrorText);
					 
					 /*Update database values*/
					 Database.updateDatabase(ctx, NewConnectedListener.sensorSafeValues, Database.zephyrTableName);
					 NewConnectedListener.sensorValues.clear();

					/*This disconnects listener from acting on received messages*/	
					_bt.removeConnectedEventListener(_NConnListener);
					/*Close the communication with the device & throw an exception if failure*/
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
        
        /*Obtaining the handle to act on the SAVE button*/
        dbButton = (Button) findViewById(R.id.ButtonDBSave);
        if (dbButton != null)
        {
        	dbButton.setOnClickListener(new OnClickListener() {
				@Override
				/*Functionality to act if the button SAVE is touched*/
				public void onClick(View v) {
					/*Reset the global variables*/
					TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
    				String ErrorText  = "Database saved!";
					tv.setText(ErrorText);

					 /*Update database values*/
					 Database.updateDatabase(ctx, NewConnectedListener.sensorSafeValues, Database.zephyrTableName);
					 NewConnectedListener.sensorValues.clear();

				
				}
        	});
        }
        
        /*Obtaining the handle to act on the SEND button*/
        sendDbButton = (Button) findViewById(R.id.ButtonDBSend);
        if (sendDbButton != null)
        {
        	sendDbButton.setOnClickListener(new OnClickListener() {
				@Override
				/*Functionality to act if the button SEND is touched*/
				public void onClick(View v) {
					/*Reset the global variables*/
					TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
    				String ErrorText  = "Database sent to server!";
					 tv.setText(ErrorText);


					 /*Update database values*/
					 Database.updateDatabase(ctx, NewConnectedListener.sensorSafeValues, Database.zephyrTableName);
					 NewConnectedListener.sensorValues.clear();

					 Database.initDatabase(ctx);
					 syncSQLiteMySQLDB();
				
				}
        	});
        }
        
        /*Obtaining the handle to act on the PLOT button*/
        plotButton = (Button) findViewById(R.id.ButtonPlot);
        if (plotButton != null)
        {
        	plotButton.setOnClickListener(new OnClickListener() {
				@Override
				/*Functionality to act if the button SEND is touched*/
				public void onClick(View v) {
					/*Update database values*/
					 Database.updateDatabase(ctx, NewConnectedListener.sensorSafeValues, Database.zephyrTableName);
					 NewConnectedListener.sensorValues.clear();

					openPlot();
				
				}
        	});
        }
        
        
    }

    /*
      **************************************************************************
      * BROADCAST RECEIVERS
      * ************************************************************************
     */

    /*
    * BTBondReceiver class: BroadcastReceiver
    * */
    private class BTBondReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            BluetoothDevice device = adapter.getRemoteDevice(b.get("android.bluetooth.device.extra.DEVICE").toString());

            Log.d("Bond state", "BOND_STATED = " + device.getBondState());
        }
    }


    /*
     * BTBroadcastReceiver class: BroadcastReceiver
     * */
    private class BTBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BTIntent", intent.getAction());
            Bundle b = intent.getExtras();
            Log.d("BTIntent", b.get("android.bluetooth.device.extra.DEVICE").toString());
            Log.d("BTIntent", b.get("android.bluetooth.device.extra.PAIRING_VARIANT").toString());
            try {
                BluetoothDevice device = adapter.getRemoteDevice(b.get("android.bluetooth.device.extra.DEVICE").toString());


                Method m = BluetoothDevice.class.getMethod("convertPinToBytes", String.class);
                byte[] pin = (byte[])m.invoke(device, "1234");
                m = device.getClass().getMethod("setPin", pin.getClass());
                Object result = m.invoke(device, pin);
                Log.d("BTTest", result.toString());
            } catch (SecurityException e1) {
                e1.printStackTrace();
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    *******************************************************************************************************
    *  AUXILIAR FUNCTIONS
    *  ****************************************************************************************************
     */


    /*
     * openPlot: opens the PlotActivity
     * */
    
    private void openPlot(){
		Intent intent = new Intent(this, PlotActivity.class);
    	startActivity(intent);
    }



   /*
    * syncSQLiteMySQLDB - synchornizes the local database with the server
    * */
    
    public void syncSQLiteMySQLDB(){
        prgDialog.show();

    	try {DataStoring.syncSQLiteMySQLDB(ctx, Database.zephyrTableName);}
        catch (JSONException e){
            Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        prgDialog.hide();



    }


    


  

    
}


