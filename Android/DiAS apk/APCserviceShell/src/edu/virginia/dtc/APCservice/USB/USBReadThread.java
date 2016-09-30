package edu.virginia.dtc.APCservice.USB;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import edu.virginia.dtc.APCservice.HypoDialog;
import edu.virginia.dtc.APCservice.DataManagement.SensorsManager;
import edu.virginia.dtc.APCservice.Database.IITDatabaseManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;


public class USBReadThread extends Thread {
	private  boolean shut;
	public boolean started;
	public USBHost mHost;
	
	public static boolean processing_algorithm;
	public static double last_read_bolus;
	public static double last_read_basal;





	IITDatabaseManager mDatabase;
	Context dbContext;

	public USBReadThread(USBHost host, Context ctx){
		System.out.println("Create read");
		shut = false;
		started = false;
		mHost = host;
		dbContext = ctx;
		mDatabase = new IITDatabaseManager(ctx);
		processing_algorithm = false;
		last_read_bolus = 0;
		last_read_basal = 1;

	}

	public void run() {
		started = true;
		System.out.println( mHost.socketIn+" mm " + shut);

		while(!shut){

			if (mHost!= null && mHost.socketIn != null && mHost.socketIn.hasNext()) {
				String line = mHost.socketIn.nextLine();
				System.out.println( "Got the line " + line);

				if (line != null) {
					System.out.println("Received line: " + line);


					//System.out.println("Received command: " + line);
					//If it is a known Command --> we send data
					//SEND COMMAND
					if (line.equals(USBHost._GET_DATA)) {
						//Post result in Command field:
						showCommand(line);

						String jSon = mHost.messageToUSB(SensorsManager._EMPATICA_TABLE_NAME);
						if (jSon != null) {
							mHost.sendUSBmessage(jSon);
							mHost.sendUSBmessage(USBHost._END_COMMAND);

						} else
							//Send no data message
							mHost.sendUSBmessage(USBHost._NO_DATA);

					}
					//ACK COMMAND - Synchronized values
					else if (line.contains(USBHost._ACK_SYNCHRONIZED)) {
						//System.out.println(line);

						//We can get a JSON Object from USB line
						try {
							JSONArray arr = new JSONArray(line);
							for (int i = 0; i < arr.length(); i++) {
								JSONObject jsonObj = (JSONObject) arr.get(i);
								//TODO dbManager.updateSyncStatus(databaseContext, (String) jsonObj.get("table_name"),
								try {
									mDatabase.ackSyncStatusAllPrevious(dbContext, SensorsManager._EMPATICA_TABLE_NAME,
											(String) jsonObj.get("synchronized"), (String) jsonObj.get("time_stamp"));
								}catch (Exception e){
									System.out.println("Exception when sync from USB: "+e);
								}

								//System.out.println("JSON FROM USB:" + jsonObj.toString());
							}
						} catch (JSONException e) {
							e.printStackTrace();
							System.out.println("Wrong USB ACK Format: " + e);

						}

					}//ACK of connection established int e other side
					else if (line.equals(USBHost._CONNECTION_ESTABLISHED)){
						//Post result in Command field:
						showCommand(line);

						//TODO
						mHost.sendUSBmessage(USBHost._CONNECTION_ESTABLISHED);
						mHost.updateConnectedStatus("Connected", "USB HOST CONNECTED - established!  :) ", false);

						mHost.connected = true;


					}
					//When disconnection request
					else if (line.equals(USBHost._CONNECTION_END)){
						//Post result in Command field:
						showCommand(line);
						//TODO
						mHost.updateConnectedStatus("CONNECT USB", "USB HOST DISCONNECTED - Press Connect USB in phone ", true);
						mHost.disconnectUSBHost();
						//Try to reconnect...
						mHost.initializeConnection.run();

					}
					//When DiAS is ready with new CGM sample
					else if (line.equals(USBHost._PHONE_READY)){
						//Send to matlab

					}//Send a INSULIN command to the phone
					else if (line.contains(USBHost._INSULIN)){
						try {
						//Extract bolus value
						JSONObject json = new JSONObject(line);
						try{
							last_read_bolus= (Double)json.get("bolus");
							last_read_basal = (Double)json.get("basal");
						}catch (Exception e){
							System.out.println("Convert to double problem!"+e);
							try{
								int var = (Integer)json.get("bolus");
								last_read_bolus = (double)var;
								 var = (Integer)json.get("basal");
								 last_read_basal = (double)var;
								
								
							}catch (Exception e2){
								System.out.println("Convert to int problem!"+e);

								
							}
						}
						
						//Extract basal value
						//String basal = json.getString("basal");

						}catch (JSONException e){
							System.out.println("Problem decoding insulin command: "+e);
						}
						//IOMain can continue with its normal execution
						processing_algorithm = false;

						
					}
					//Send a HYPO ALARM command to the phone
					else if (line.contains(USBHost._HYPO)){
						//Extract carb amount:
						int carbs = 0;
						String type = "-";
						try {
							//Extract bolus value
							JSONObject json = new JSONObject(line);
							
							try{
								 carbs= (Integer)json.get("carbs");
								 type = json.getString("type");
							}catch (Exception e){
								System.out.println("HYPO Convert to double problem!"+e);
								try{
									double val = (Double)json.get("carbs");
									carbs = (int)val;
									 type = json.getString("type");
									
								}catch (Exception e2){
									System.out.println("HYPO Convert to int problem!"+e);

									
								}
							}
						}catch (JSONException e){
							System.out.println("Problem decoding hypo command: "+e);
						}

						//Display the dialog
                        Intent dialogIntent = new Intent(mHost.ioActivity, HypoDialog.class);
                        dialogIntent.putExtra("carbs", carbs); //Carbs amount
                        dialogIntent.putExtra("alarm_type", type); // Alarm typ
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mHost.ioActivity.startActivity(dialogIntent);
						
					}
					//All not syncrhonized data is requested
					else if(line.contains(USBHost._GET_ALL_NO_SYNC)){
						//Send all not sync values
						//Post result in Command field:
						showCommand(line);

						//Get JSON object:
						String[] jSons = null;
						try{
							JSONObject json = new JSONObject(line);
							String sensor = (String)json.get(USBHost._SENSOR_ID);
							if (sensor.equals(USBHost._EMPATICA))
								jSons= mHost.messageAllAsync(SensorsManager._EMPATICA_TABLE_NAME);
							else if (sensor.equals(USBHost._DEXCOM))
								jSons= mHost.messageLastDias( USBHost._DEXCOM);
							else if (sensor.equals(USBHost._ZEPHYR))
								jSons= mHost.messageLastDias( USBHost._ZEPHYR);


						}catch(JSONException e){
							System.out.println("Get all no sync wrong structure: "+e);
						}

						if (jSons != null) {
							for (String json: jSons)
								mHost.sendUSBmessage(json);

							mHost.sendUSBmessage(USBHost._END_COMMAND);


						} else
							//Send no data message
							mHost.sendUSBmessage(USBHost._NO_DATA);

					}/*else{
						//In any other case, restart
						System.out.println("Reading thread start again");
						//mHost.readingThread.start();

					}*/
				}
			}


			//System.out.println("End of while in reading thread... will start? "+ !shut);

		}

	}
	private void showCommand(String command){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String currentDateandTime = sdf.format(new Date());
		//MainActivity.setCommandValue(command + " : " + currentDateandTime);
		System.out.println("Received: "+ command + " : " + currentDateandTime);

	}
	public void shutdown(){
		shut = true;
		System.out.println("Shutdown: to true");

	}

	public void restart(){
		shut = false;
		System.out.println("Shutdown: to false");
	}


	public void shutup(){
		shut = false;
		System.out.println("Shutdown: "+ shut);

	}

}
