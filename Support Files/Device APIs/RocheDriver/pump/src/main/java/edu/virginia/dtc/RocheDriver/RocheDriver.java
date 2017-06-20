/*
 * This device driver supports the IDex connected via USB to a Tablet PC.  This Tablet then 
 * communicates to this device via a Bluetooth connection.
 */
package edu.virginia.dtc.RocheDriver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import edu.virginia.dtc.Biometrics;
import edu.virginia.dtc.Constraints;
import edu.virginia.dtc.Debug;
import edu.virginia.dtc.Event;
import edu.virginia.dtc.Pump;
import edu.virginia.dtc.RocheData.Packet;
import edu.virginia.dtc.RocheDriver.Driver.Device;
import edu.virginia.dtc.RocheDriver.Driver.Events;
import edu.virginia.dtc.TwoFish.Twofish_Algorithm;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;

public class RocheDriver extends Service {
	private static final String TAG = "RocheDriver";
	
	// Commands for Pump Service
	public static final double LOW_RES_THRESH = 50.0;
	public static final int WARNING_THRESH = 30;
	
	private BroadcastReceiver stopReceiver, updateReceiver;
	
    private final Messenger messengerFromPumpService = new Messenger(new incomingPumpHandler());
    private static Messenger messengerToPumpService = null;
	
    private Driver drv = Driver.getInstance();
	
    private static final double INFUSION_RATE = 0.5;
    private boolean connecting = false;
    
    PowerManager pm; 
	PowerManager.WakeLock wl;
	
	private InterfaceData data;
	
	private Runnable connect = new Runnable()
	{
		final String FUNC_TAG = "connect";
		
		public void run() 
		{
			Debug.i(TAG, FUNC_TAG, "Sending connect to "+drv.deviceMac+"!");
			InterfaceData.remotePumpBt.connect(data.bt.getRemoteDevice(drv.deviceMac), true, true);
		}
	};
	
	private Runnable timeout = new Runnable()
	{
		final String FUNC_TAG = "timeout";
		
		public void run()
		{
			Bundle b = new Bundle();
    		b.putString("description", "Pairing has timed out!  Resetting driver...");
			Event.addEvent(drv.serv, Event.EVENT_PUMP_PAIR, Event.makeJsonString(b), Event.SET_POPUP_AUDIBLE);
			
			if(Driver.connectTimer != null)
			{
				Driver.connectTimer.cancel(true);
				Driver.connectTimer = null;
			}
			
			connecting = false;
			drv.resetDriver();
		}
	};
	
	private BroadcastReceiver btReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			final String FUNC_TAG = "onReceive";

			String action = intent.getAction();
			String address = null;
			String OrgId = null;
			
			if(action.equals(BluetoothDevice.ACTION_ACL_CONNECTED))
			{
				if(intent.getStringExtra("address") != null)
				{
					Debug.w(TAG,  FUNC_TAG, "Awesome, this is our custom intent!");	
					
					address = intent.getStringExtra("address");
					OrgId = address.substring(0, 8);
					
					if(OrgId.equals("00:0E:2F"))
					{
						Debug.i(TAG, FUNC_TAG, "Matching Organizational ID!");
						
						if(!drv.settings.getBoolean("paired", false))
						{
							data.bt.cancelDiscovery();
							
							if(!drv.sdpFound)
							{
								drv.sdpFound = true;
								
								MediaPlayer mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.chime);
						   	 	mMediaPlayer.start(); 
								
								Bundle b = new Bundle();
					    		b.putString("description", "SDP complete the pump ("+address+") has found the phone!");
								Event.addEvent(drv.serv, Event.EVENT_PUMP_PAIR, Event.makeJsonString(b), Event.SET_LOG);
								
								if(Driver.timeoutTimer != null)
									Driver.timeoutTimer.cancel(true);
							}
							
							if(Driver.uiState == DiscoveryFragment.ROCHE_SEARCH || Driver.uiState == DiscoveryFragment.STARTUP)
								Driver.uiState = DiscoveryFragment.ROCHE_CONNECTING;
							
							drv.deviceMac = address;

							if(Driver.connectTimer == null)
							{
								Debug.i(TAG, FUNC_TAG, "Firing the connect timer!");
								Driver.connectTimer = Driver.scheduler.schedule(connect, 15, TimeUnit.SECONDS);		//Start connect in 10 seconds
							}
							
							if(Driver.timeoutTimer == null || Driver.timeoutTimer.isCancelled() || Driver.timeoutTimer.isDone())
							{
								Debug.i(TAG, FUNC_TAG, "Firing the timeout timer!");
								Driver.timeoutTimer = Driver.scheduler.schedule(timeout, 90, TimeUnit.SECONDS);		//Timeout is in 90 seconds
							}
						}
						else
							Debug.i(TAG, FUNC_TAG, "Driver already paired or currently connecting!");
					}
				}
			}
		}	
	};
	
	@Override
	public void onCreate() {
		final String FUNC_TAG = "onCreate";

		super.onCreate();
		
		Debug.i(TAG, FUNC_TAG, "");
		
		drv = Driver.getInstance();		
		drv.serv = this;
		drv.db = new RocheDB(this.getApplicationContext());
		drv.updatePumpState(Pump.NONE);
		drv.settings = getSharedPreferences(Driver.PREFS, 0);
		
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2)
			InterfaceData.getInstance().bt = ((android.bluetooth.BluetoothManager)getSystemService(BLUETOOTH_SERVICE)).getAdapter();
		else
			InterfaceData.getInstance().bt = BluetoothAdapter.getDefaultAdapter();

		Debug.i(TAG, FUNC_TAG, "BT: "+InterfaceData.getInstance().bt.getName() + " " + InterfaceData.getInstance().bt.getAddress());
		
		logAction("onCreate()", "Roche Driver Started", Debug.LOG_ACTION_INFORMATION);
		
		restoreDevice();
		
		// Set up a Notification for this Service
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        int icon = R.drawable.ic_launcher;
        CharSequence tickerText = "";
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);
        Context context = getApplicationContext();
        CharSequence contentTitle = "Device Driver";
        CharSequence contentText = "Roche Pump";
        Intent notificationIntent = new Intent(this, RocheDriver.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        final int DRVR_ID = 12;
        
        //mNotificationManager.notify(DRVR_ID, notification);
        startForeground(DRVR_ID, notification);
        
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "UI");
		wl.acquire();
    	
    	Intent pumpIntent = new Intent();
		pumpIntent.setClassName("edu.virginia.dtc.PumpService", "edu.virginia.dtc.PumpService.PumpService");
		pumpIntent.putExtra("driver_intent", Driver.PUMP_INTENT);
		pumpIntent.putExtra("driver_name", Driver.DRIVER_NAME);
		pumpIntent.putExtra("PumpCommand", 9);
		startService(pumpIntent);
		
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);		//This allows us to catch the low-level connection
		registerReceiver(btReceiver, filter);	
		
		stopReceiver = new BroadcastReceiver(){
			public void onReceive(Context context, Intent intent) {
				if (intent.getStringExtra("package").equals("edu.virginia.dtc.RocheDriver")){
					Debug.d(TAG, FUNC_TAG, "Finishing...");
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			}			
		};
		registerReceiver(stopReceiver, new IntentFilter("edu.virginia.dtc.STOP_DRIVER"));
		
		updateReceiver = new BroadcastReceiver(){
			public void onReceive(Context context, Intent intent)
			{
				Debug.i(TAG, FUNC_TAG, "Updating Device Manager!");
				Driver.updateDevices();
			}
		};
		registerReceiver(updateReceiver, new IntentFilter("edu.virginia.dtc.DRIVER_UPDATE"));
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		final String FUNC_TAG = "onStartCommand";

		Debug.d(TAG, FUNC_TAG,"Received onStartCommand...");
		
		if(!intent.getBooleanExtra("auto", false))
		{
			Intent uiIntent = new Intent();
			uiIntent.setClassName("edu.virginia.dtc.RocheDriver", "edu.virginia.dtc.RocheDriver.RocheUI");
			uiIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(uiIntent);
		}
		else
		{
			
		}
		
		return Service.START_STICKY;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		final String FUNC_TAG = "onStart";

		Debug.d(TAG, FUNC_TAG,"Received onStart...");
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		Debug.i(TAG, "onDestroy", "...");
		
		//Unregister receivers
		unregisterReceiver(stopReceiver);
		unregisterReceiver(updateReceiver);
		unregisterReceiver(btReceiver);
		
		//Stop the pump BT interface
		if(InterfaceData.remotePumpBt != null)
		{
			InterfaceData.remotePumpBt.stop();
			InterfaceData.remotePumpBt = null;
		}
		
		if(wl.isHeld())
			wl.release();
	}
	
	// onBind calls this which returns the binder object
	@Override
	public IBinder onBind(Intent arg0) {
		final String FUNC_TAG = "onBind";

		Debug.i("onBind", FUNC_TAG, arg0.getAction());
		
		if(arg0.getAction().equalsIgnoreCase(Driver.PUMP_INTENT))
			return messengerFromPumpService.getBinder();
		else
			return null;
	}
	
	/*****************************************************************************************
	 * Message Handlers
	 *****************************************************************************************/
	
	private double checkLimits(double bolus)
	{
		final String FUNC_TAG = "checkLimits";
		
		double out = bolus;
		
		Debug.i(TAG, FUNC_TAG, "Input: "+bolus);
		
		if(Double.isInfinite(bolus))
		{
			Debug.e(TAG, FUNC_TAG, "Bolus is infinite, setting to zero...");
			out = 0.0;
		}
			
		if(Double.isNaN(bolus))
		{
			Debug.e(TAG, FUNC_TAG, "Bolus is NaN, setting to zero...");
			out = 0.0;
		}
		
		if(bolus < Pump.EPSILON)
		{
			Debug.e(TAG, FUNC_TAG, "Bolus is negative, setting to zero...");
			out = 0.0;
		}
		
		if(bolus > (Constraints.MAX_TOTAL))
		{
			Debug.e(TAG, FUNC_TAG, "Bolus is greater than MAX constraint ("+Constraints.MAX_TOTAL+"), setting to MAX!");
			bolus = Constraints.MAX_TOTAL;
		}
		
		
		Debug.i(TAG, FUNC_TAG, "Output: "+out);
		return out;
	}
	
	class incomingPumpHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			final String FUNC_TAG = "handleMessage";

			switch(msg.what){
				case Pump.PUMP_SERVICE2DRIVER_NULL:
					break;
				case Pump.PUMP_SERVICE2DRIVER_REGISTER:
					messengerToPumpService = msg.replyTo;
					Debug.i("incomingPumpHandler", FUNC_TAG, "Connection made to service, sending parameters");
					
					ContentValues pumpValues = new ContentValues();
					pumpValues.put("max_bolus_U", 25.0);
					pumpValues.put("min_bolus_U", 0.1);							//The lowest value is 0.0083
					pumpValues.put("min_quanta_U", 0.1);
					pumpValues.put("infusion_rate_U_sec", INFUSION_RATE);
					pumpValues.put("reservoir_size_U", 300.0);
					pumpValues.put("low_reservoir_threshold_U", LOW_RES_THRESH);			//Low reservoir message will show at 50U remaining
					pumpValues.put("unit_name", "units");
					pumpValues.put("unit_conversion", 10.0);					//Pump operates in tenths of a unit
					pumpValues.put("queryable", 1);								//It doesn't support it directly but through the use of its DB and history it can
					
					pumpValues.put("temp_basal", 1);							//Indicates if Temp Basals are possible
					pumpValues.put("temp_basal_time", 15);						//Indicates the time frame of a Temp Basal
					
					pumpValues.put("retries", 0);
					pumpValues.put("max_retries", 2);
					
					getContentResolver().update(Biometrics.PUMP_DETAILS_URI, pumpValues, null, null);
					
					sendDataMessage(messengerToPumpService, null, Pump.DRIVER2PUMP_SERVICE_PARAMETERS, msg.arg1, msg.arg2, null);
					
					//Start the BT if not already on and put into Listening mode
					data = InterfaceData.getInstance();
					
					if(!data.bt.isEnabled())
						data.bt.enable();
					
					//Phone has to be listening so the pump can use SDP to find it
					if(InterfaceData.remotePumpBt == null)	
		    		{
						Debug.i(TAG, FUNC_TAG, "New pump connection created and SDP setup!");
			    		InterfaceData.remotePumpBt = new BluetoothConn(data.bt, InterfaceData.PUMP_UUID, "SerialLink", true);
			    		InterfaceData.remotePumpBt.listen();
		    		}
					
					drv.updatePumpState(Pump.REGISTERED);
					break;
				case Pump.PUMP_SERVICE2DRIVER_BOLUS:
					Debug.i("incomingPumpHandler", FUNC_TAG, "Receiving bolus command!");
					double bolus_req = msg.getData().getDouble("bolus");
					int bolus_id = msg.getData().getInt("bolusId");
					
					Debug.i("incomingPumpHandler", FUNC_TAG,"Bolus ID: "+bolus_id+" requested for "+bolus_req+"U");
					
					bolus_req = checkLimits(bolus_req);
					Debug.i(TAG, FUNC_TAG, "Checked bolus: "+bolus_req);
					
					if(bolus_req > 0.0)
					{
						double bolus_conv = bolus_req * 10.0;
						Debug.i(TAG, FUNC_TAG, "Sending "+bolus_conv+" (conversion applied) to driver!");
						drv.a.cmdDeliverBolus((int)bolus_conv, bolus_id);
					}
					else
						Debug.i(TAG, FUNC_TAG, "Bolus is zero or negative!");
					
					break;
				case Pump.PUMP_SERVICE2DRIVER_QUERY:
					int id = msg.getData().getInt("bolusId", -1);
					if(id != -1)
					{
						Events e = drv.db.lookupBolusInHistory(id);
						
						Debug.i(TAG, FUNC_TAG, "Bolus ID "+e.bolusId+" of amount "+e.bolus+" found!");
						
						Bundle queryData = new Bundle();
						queryData.putInt("status", e.status);
						queryData.putInt("queryId", (int)e.bolusId);
						queryData.putDouble("delivered_amount_U", e.bolus);
						queryData.putString("description", e.description);
						queryData.putLong("time", e.timestamp);
						sendDataMessage(messengerToPumpService, queryData, Pump.DRIVER2PUMP_SERVICE_QUERY_RESP, 0, 0, null);
					}
					else
						Debug.i(TAG, FUNC_TAG, "Query ID is invalid: "+id);
					
					break;
				case Pump.PUMP_SERVICE2DRIVER_TBR:
					Debug.i(TAG, FUNC_TAG, "Setting TBR!");
					
					drv.tbrDuration = msg.getData().getInt("time", 30);		//Default duration is always 30 minutes
					drv.tbrTarget = msg.getData().getInt("target", 0);
					drv.cancelBolus = msg.getData().getBoolean("cancel", false);
					
					if(drv.cancelBolus)
					{
						Debug.i(TAG, FUNC_TAG, "Pump Service requests any bolus be canceled!");
						drv.a.clearBolusCancelTbr();
					}
					else
					{
						Debug.i(TAG, FUNC_TAG, "Pump Service requests TBR!");
						drv.a.setTbr();
					}
					break;
			}
		}
	}
	
	/*****************************************************************************************
	 * Messaging Utility Functions
	 *****************************************************************************************/

	private static void sendDataMessage(Messenger messenger, Bundle bundle, int what, int arg1, int arg2, Messenger reply)
	{
		final String FUNC_TAG = "sendDataMessage";

		if(messenger != null)
		{
			Message msg = Message.obtain(null, what);
			msg.arg1 = arg1;
			msg.arg2 = arg2;
			
			if(bundle!=null)
				msg.setData(bundle);
			
			if(reply!=null)
				msg.replyTo = reply;
			
			try
			{
				messenger.send(msg);
			}
			catch(RemoteException e) 
			{
				e.printStackTrace();
			}
		}
		else
			Debug.i(TAG, FUNC_TAG, "Messenger is not connected or is null!");
	}
	
	public static void sendPumpMessage(Bundle bundle, int what)
	{
		sendDataMessage(messengerToPumpService, bundle, what, 0, 0, null);
	}
	
	private void restoreDevice()
	{
		final String FUNC_TAG = "restoreDevice";

		drv = Driver.getInstance();
		drv.settings = getSharedPreferences(Driver.PREFS, 0);
		
		boolean paired = drv.settings.getBoolean("paired", false);
		String mac = drv.settings.getString("mac", "");
		
		Debug.i(TAG, FUNC_TAG, "MAC: "+mac);
		
		if(paired)
		{
			if(!mac.equalsIgnoreCase(""))
			{
				//Make sure neither is empty
				drv.deviceMac = mac;
				drv.addresses = (byte)drv.settings.getInt("address", 0x10);
				
				byte[] pd = new byte[16];
				byte[] dp = new byte[16];
				
				for(int i = 0;i<pd.length;i++)
				{
					pd[i] = (byte)drv.settings.getInt("pd"+i, 0);
				}
				
				for(int i = 0;i<dp.length;i++)
				{
					dp[i] = (byte)drv.settings.getInt("dp"+i, 0);
				}
				
				for(int i = 0;i<Packet.nonceTx.length;i++)
				{
					Packet.nonceTx[i] = (byte)drv.settings.getInt("nonce"+i, 0);
				}
				
				try {
					drv.pd_key = Twofish_Algorithm.makeKey(pd);
					drv.dp_key = Twofish_Algorithm.makeKey(dp);
				} catch (InvalidKeyException e) {
					e.printStackTrace();
				}
				
				Driver.uiState = DiscoveryFragment.ROCHE_AUTHENTICATED;
				
				//Start the command mode, since pairing is completed
				data = InterfaceData.getInstance();
				
				if(!data.bt.isEnabled())
					data.bt.enable();
				
				if(InterfaceData.remotePumpBt != null)
				{
					InterfaceData.remotePumpBt.stop();
					InterfaceData.remotePumpBt = null;
				}
				
				if(InterfaceData.remotePumpBt == null)
				{
					InterfaceData.remotePumpBt = new BluetoothConn(data.bt, InterfaceData.PUMP_UUID, "SerialLink", true);
					InterfaceData.remotePumpBt.connect(data.bt.getRemoteDevice(drv.deviceMac), true, true);
				}
				
				drv.a.startMode(Driver.COMMAND, false);
			}
			else
				Debug.i(TAG, FUNC_TAG, "Unable to restore, invalid keys or MAC!");
		}
		else
			Debug.i(TAG, FUNC_TAG, "No previous pairing!");
	}
	
	private void saveDevice()
	{
		drv = Driver.getInstance();
		
		drv.settings = getSharedPreferences(Driver.PREFS, 0);
		SharedPreferences.Editor edit = drv.settings.edit();
		
		edit.commit();
	}
	
	private void updateDeviceString()
	{
		String pumpDetails = "";
		if(drv.pump != null)
		{
			pumpDetails = 	"Name:  Roche Pump"+
							"\nInterface:  BT";
		}
		ContentValues pumpValues = new ContentValues();
		pumpValues.put("details", pumpDetails);
		getContentResolver().update(Biometrics.PUMP_DETAILS_URI, pumpValues, null, null);
	}
	
	/*****************************************************************************************
	 * Log File Utility Functions
	 *****************************************************************************************/
	
	public void logAction(String service, String action, int priority) {
		Intent i = new Intent("edu.virginia.dtc.intent.action.LOG_ACTION");
        i.putExtra("Service", service);
        i.putExtra("Status", action);
        i.putExtra("priority", priority);
        i.putExtra("time", (long)(System.currentTimeMillis()/1000));
        sendBroadcast(i);
	}
}
