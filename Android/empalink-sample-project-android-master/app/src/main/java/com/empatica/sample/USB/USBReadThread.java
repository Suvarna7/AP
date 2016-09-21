package com.empatica.sample.USB;

import android.content.Context;

import com.empatica.sample.BGService;
import com.empatica.sample.Database.IITDatabaseManager;
import com.empatica.sample.MainActivity;
import com.empatica.sample.R;

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

	public USBReadThread(USBHost host, Context ctx){
    	System.out.println("Create read");
    	shut = false;
		started = false;
    	mHost = host;
		dbContext = ctx;
		mDatabase = new IITDatabaseManager(ctx);

	}

	public void run() {
		started = true;
		System.out.println( mHost.socketIn+" mm " + shut);

		while(!shut){

			if (mHost!= null && mHost.socketIn != null && mHost.socketIn.hasNext()) {
				String line = mHost.socketIn.nextLine();

				if (line != null) {
					//System.out.println("Received line: " + line);



					//System.out.println("Received command: " + line);
					//If it is a known Command --> we send data
					//SEND COMMAND
					if (line.equals(USBHost._GET_DATA)) {
						//Post result in Command field:
						showCommand(line);

						String jSon = MainActivity.messageToUSB();
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
									mDatabase.ackSyncStatusAllPrevious(dbContext, BGService.empaticaSecTableName,
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


					}
					//When disconnection request
					else if (line.equals(USBHost._CONNECTION_END)){
						//Post result in Command field:
						showCommand(line);
						//TODO
						mHost.updateConnectedStatus("CONNECT USB", "USB HOST DISCONNECTED - Press Connect USB in phone ", true);
						mHost.disconnectUSBHost();

					}
					//All not syncrhonized data is requested
					else if(line.equals(USBHost._GET_ALL_NO_SYNC)){
						//Send all not sync values
						//Post result in Command field:
						showCommand(line);

						String[] jSons = MainActivity.messageAllAsync(BGService.empaticaSecTableName,
								IITDatabaseManager.syncColumn, IITDatabaseManager.syncStatusNo);
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
