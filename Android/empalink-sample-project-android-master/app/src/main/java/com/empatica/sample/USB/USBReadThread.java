package com.empatica.sample.USB;

import android.content.Context;

import com.empatica.sample.BGService;
import com.empatica.sample.Database.IITDatabaseManager;
import com.empatica.sample.MainActivity;
import com.empatica.sample.R;
import com.empatica.sample.Timers.BasicTimer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class USBReadThread extends Thread {
    private  boolean shut;
	public boolean started;
    public USBHost mHost;

	IITDatabaseManager mDatabase;
	Context dbContext;
	boolean firstRead;

	public USBReadThread(USBHost host, Context ctx){
    	System.out.println("Create read");
    	shut = false;
		started = false;
    	mHost = host;
		dbContext = ctx;
		mDatabase = new IITDatabaseManager(ctx);
		firstRead = true;

	}

	public void run() {
		started = true;

		while(!shut) {
				if (mHost != null && mHost.socketIn != null && mHost.socketIn.hasNextLine() &&mHost.socketIn.hasNext()) {
					String line = mHost.socketIn.nextLine();

					if (line != null) {
						//If it is a known Command --> we send data
						//SEND COMMAND
						if (line.equals(USBMessageSender._GET_DATA)) {
							//Post result in Command field:
							showCommand(line);

							mHost.usbMesenger.messageAsync(BGService.empaticaMilTableName, null, null, IITDatabaseManager.MAX_READ_SAMPLES_SYNCHRONIZE, false);

						}

						//ACK of connection established int e other side
						else if (line.equals(USBMessageSender._CONNECTION_ESTABLISHED)) {
							//Post result in Command field:
							showCommand(line);

							//TODO Only phone - send connection established back
							mHost.usbMesenger.sendUSBmessage(USBMessageSender._CONNECTION_ESTABLISHED);
							mHost.updateConnectedStatus("Connected", "USB HOST CONNECTED - established!  :) ", false);
							mHost.connected = true;
							firstRead = true;

							//Timers are ready now to start functioning!
							BasicTimer.enable();


						}
						//When disconnection request
						else if (line.equals(USBMessageSender._CONNECTION_END)) {
							//Post result in Command field:
							showCommand(line);
							//TODO
							mHost.updateConnectedStatus("CONNECT USB", "USB HOST DISCONNECTED - Press Connect USB in phone ", true);
							//mHost.disconnectUSBHost();


						}//Wrong command
						else if (line.contains(USBMessageSender._WRONG_COMMAND)) {
							mHost.usbMesenger.sendUSBmessage(USBMessageSender._END_COMMAND);

						}
						else if (line.contains(USBMessageSender._TEST_USB)) {
							mHost.usbMesenger.sendUSBmessage(USBMessageSender._ACK_TEST_USB);
						} else if (line.contains(USBMessageSender._TEST_DEVICE)) {
							//Extract device information
							//But for now....
							//Test Empatica connected
							if (BGService.EmpaticaDisconnected)
								mHost.usbMesenger.sendUSBmessage(USBMessageSender._VERIFY_DEVICE_DISCONNECTED);
							else
								mHost.usbMesenger.sendUSBmessage(USBMessageSender._VERIFY_DEVICE_CONNECTED);

						}
						// All not synchronized data is requested
						else if (line.contains(USBMessageSender._GET_ALL_NO_SYNC)) {
							//Send all not sync values
							//Post result in Command field:
							showCommand(line);

							try {
								JSONObject json = new JSONObject(line);
								String sensor = (String) json.get(USBMessageSender._SENSOR_ID);
								//String num_samples = (String)json.get(USBHost._NUM_SAMPLES);
								//All samples requested
								//if (num_samples.equals(USBHost._ALL_SAMPLES)){

								//EMPATICA Special case
								if (sensor.equals(USBMessageSender._EMPATICA)) {

									if (firstRead) {
										//Send more samples of data
										mHost.usbMesenger.messageNAsync(BGService.empaticaMilTableName, (IITDatabaseManager.MAX_READ_SAMPLES_SYNCHRONIZE));
										firstRead = false;
									} else {
										//The rest, according to the interval
										mHost.usbMesenger.messageAllAsync(BGService.empaticaMilTableName);
									}
								}
								//ALL OTHER sensors
								else {
									//mHost.messageAllAsync(BGService.empaticaMilTableName);
									mHost.usbMesenger.sendUSBmessage(USBMessageSender._NO_DATA);

								}

							} catch (JSONException e) {
								System.out.println("Get all no sync wrong structure: " + e);
								mHost.usbMesenger.sendUSBmessage(USBMessageSender._NO_DATA);

							} catch (Exception e) {
								System.out.println("Retrieving JSON data exception: " + e);
								mHost.usbMesenger.sendUSBmessage(USBMessageSender._NO_DATA);
							}

							//todo  After reading, send all data
						/*if (jSons != null) {
							for (String json : jSons)
								mHost.sendUSBmessage(json);
							mHost.sendUSBmessage(USBHost._END_COMMAND);

						} else
							//Send no data message
							mHost.sendUSBmessage(USBHost._NO_DATA);*/

						}

						//ACK COMMAND - Synchronized values
						else if (line.contains(USBMessageSender._ACK_SYNCHRONIZED)) {
							//We can get a JSON Object from USB line
							try {
								JSONArray arr = new JSONArray(line);
								BGService.ackInProgress = true;
								for (int i = 0; i < arr.length(); i++) {
									JSONObject jsonObj = (JSONObject) arr.get(i);
									//TODO dbManager.updateSyncStatus(databaseContext, (String) jsonObj.get("table_name"),
									try {
										//TODO BLOOOOOOOOCKS THE APP
										// Invalid status - the only thing left to do is end transaction -> but blocks db instead
										//System.out.println("Value of synchronized: "+(String) jsonObj.get("synchronized"));
										mHost.last_time_ack = (String) jsonObj.get("time_stamp");
										//mDatabase.ackSyncStatusAllPrevious(BGService.empaticaMilTableName,
										//	(String) jsonObj.get("synchronized"), (String) jsonObj.get("time_stamp"));
										mDatabase.runAckSync(BGService.empaticaMilTableName,
												(String) jsonObj.get("synchronized"), (String) jsonObj.get("time_stamp"));
										//Inform phone process ended
										mHost.usbMesenger.sendUSBmessage(USBMessageSender._ACK_SYNCHRONIZED);
									} catch (Exception e) {
										System.out.println("Exception when sync from USB: " + e);
									}

									//System.out.println("JSON FROM USB:" + jsonObj.toString());
								}


							} catch (JSONException e) {
								e.printStackTrace();
								System.out.println("Wrong USB ACK Format: " + e);

							}
							BGService.ackInProgress = false;


						}
					}
					//System.out.println("End of while in reading thread... will start? "+ !shut);
				}

		}
		
	}
	private void showCommand(String command){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String currentDateandTime = sdf.format(new Date());
		MainActivity.setCommandValue(command + " : " + currentDateandTime);

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
