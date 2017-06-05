package edu.virginia.dtc.APCservice.USB;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import edu.virginia.dtc.APCservice.IOMain;
import edu.virginia.dtc.APCservice.DataManagement.SensorsManager;
import edu.virginia.dtc.APCservice.Database.IITDatabaseManager;
import edu.virginia.dtc.APCservice.Server.IITServerConnector;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Cat on 7/25/2016.
 */
 public class USBHost {

	 //USB Broadcast receiver
	 
	//Socket server variables
	 ServerSocket server=null;
	 public  String connectionStatus=null;
	 public static final int TIMEOUT=300; //Seconds
	 public Intent intent;
	 private static final int ANDROID_LOCAL_HOST = 38600;
	 public boolean connected;

	 //Sockets
	 Scanner socketIn;
	 PrintWriter socketOut;
	 Socket client=null;

	 //Reading thread
	 public USBReadThread readingThread;

	 //Tags
	 public static final String TAG= "Connection";
	 public Handler mHandler;

	 //Commands
	 //1. Request data
	 public static String _GET_DATA = "get_values";
	 public static String _GET_ALL = "get_all";
	 public static String _GET_ALL_NO_SYNC = "get_all_no_sync";
	 
	 public static String _PHONE_READY = "dias_ready";
	 
	 public static String _END_COMMAND = "next_end" ;
	 public static String _NO_DATA = "no_data";

	 //2. Manage communication

	 public static String _ACK_SYNCHRONIZED = "usb_sync";
	 public static String _CONNECTION_ESTABLISHED = "connection_process_end";
	 public static String _CONNECTION_END = "end_connection";
	 
	 //3. Pump commands
	public static final String _INSULIN = "insulin_command";

	 //4. Hypo alert
	public static final String _HYPO = "hypo_command";
	 
	//KEEP SENSORS INFORMATION:
	 public static final String _SENSOR_ID = "sensor_table";
	 public static final String _NUM_SAMPLES = "samples_to_read";
	 public static final String _ALL_SAMPLES = "-1";
	 public static final String _EMPATICA = "empatica";
	 public static final String _DEXCOM = "dexcom";
	 public static final String _ZEPHYR = "zephyr";
	 
	 //Max number of messages to be accepted thru usb
	 private static final int LOCAL_SENDING_AMOUNT = 25;
	 
	 //Main activity variables
	 IOMain ioActivity;
	 Context ctx;


	 public USBHost (IOMain mainAct, Context ctx){

		 this.ctx = ctx;
		 readingThread = new USBReadThread( this, ctx, mainAct.sManager.dbManager);
		 connected = false;
		 ioActivity = mainAct;

	 }

	 public void sendUSBmessage(String msg){
		 if (socketOut != null) {
			 System.out.println("send msg: "+msg);
			 //socketOut.print(msg);
			 socketOut.println(msg );
			 socketOut.flush();
		 }else {
			 System.out.println("msg not sent "+client);
			 connected = false;

			 /*try {
                socketOut = new PrintWriter(client.getOutputStream(), true);
                socketOut.println(msg +" \n");
                socketOut.flush();
            }catch(Exception e){
                System.out.println("Error trying to start socket out again: "+e);
            }*/
		 }


	 }

	 //**************************************************
	 //RUNNABLE Thread to init the USB connection
	 public Runnable initializeConnection = new Thread() {
		 public void run() {

			 connectionStatus="Connection has been started! ";

			 // initialize server socket
			 try{

				 mHandler.post(showConnectionStatus);

				 if (server == null){
					server = new ServerSocket(ANDROID_LOCAL_HOST);
				 	server.setSoTimeout(TIMEOUT * 100000);
				 }else if (server.isClosed()){
					 server = new ServerSocket(ANDROID_LOCAL_HOST);
					 server.setSoTimeout(TIMEOUT * 100000);
				 }
				 connectionStatus="Server socket... " + server.getLocalPort() +" - "+ server.getLocalSocketAddress();
				 mHandler.post(showConnectionStatus);

				 updateConnectedStatus("Connecting...", "USB HOST STARTED - Press Connect in laptop", false);

				 //Server waits for a client to try to connect:
				 //PC should start connection now
				 client = server.accept();
				 connectionStatus="Client accepted... ";
				 mHandler.post(showConnectionStatus);

				 //Socket IN --> If refused
				 createSocketIn(client);
				 connectionStatus="Socket in";
				 mHandler.post(showConnectionStatus);
				 socketOut = new PrintWriter(client.getOutputStream(), true);

				 //Start reading thread:
				 try {
					 if (!readingThread.started) {
						 System.out.println("Thread started!");
						 //USBHost temp_reference = readingThread.mHost;
						 //readingThread = new USBReadThread(temp_reference, ctx);
						 readingThread.start();
					 }
					 else {
						 //readingThread.shutup();
						 //readingThread.restart();
						 System.out.println("Thread already started! ");
						 //Re initiate
						 USBHost temp_reference = readingThread.mHost;
						 readingThread = new USBReadThread(temp_reference, ctx, ioActivity.sManager.dbManager);
						 readingThread.start();
					 }

					 if (readingThread.isInterrupted()) {
						 System.out.println("Thread was interrupted");

						 readingThread.restart();
					 }


				 }
				 catch(Exception e){
					 System.out.println("Thread already started: "+e);
					 readingThread.restart();
				 }

			 } catch (SocketTimeoutException e) {
				 // print out TIMEOUT
				 connectionStatus="Connection has timed out! Please try again";
				 mHandler.post(showConnectionStatus);

			 } catch (IOException e) {
				 Log.e(TAG, "" + e);
				 System.out.println("Server IO Exception: "+e);
				 //connectionStatus="Server IO Exception - Restart connection/phone";
				 //mHandler.post(showConnectionStatus);
			 } finally {
				 //close the server socket, we already have the client ?
				 try {
					 if (server!=null)
						 server.close();
					 else if (client!=null && socketIn != null && socketOut != null) {
						 // print out success
						 connectionStatus="Connection was successful!";
						 mHandler.post(showConnectionStatus);
					 }
				 } catch (IOException ec) {
					 Log.e(TAG, "Cannot close server socket"+ec);
				 }
			 }


		 }
	 };

	 private void createSocketIn(Socket client){
		 try {
			 socketIn = new Scanner(client.getInputStream());


		 }catch(IOException e) {
			 Log.e(TAG, "" + e);
			 System.out.println("Server IO Exception");
			 connectionStatus="Socket in exception- Restart connection/phone";
			 mHandler.post(showConnectionStatus);

			 //Not connected, update status
			 //Change Connect USB Button
			 updateConnectedStatus("USB CONNECT", "USB PC Client not established", true);

			 if (client !=null)
				 createSocketIn(client);

		 }

	 }

	 public void disconnectUSBHost(){
		 //Disconnect server
		 try {
			 //Close and reset CLIENT
			 client.close();
			 //client.shutdownInput();
			 //client.shutdownOutput();
			 client = null;
			 //Stop reading thread
			 readingThread.shutdown();

			 //Sending socket
			 socketOut.close();
			 socketOut = null;

			 //Close SERVER
			 server.close();
			 
			 updateConnectedStatus("USB DISCONNECT", "USB Connection ended", true);
			 
			 

		 }catch (IOException e){
			 System.out.println("Exception while closing server: "+e);
		 }

		 //Update GUI Buttons and status
		 connected = false;


	 }

	 /**
	  * Show connection state in the GUI
	  * @param button
	  * @param status
	  * @param enabled
	  */
	 public void updateConnectedStatus(String button, String status, boolean enabled){

		 //TODO 

	 }
	 /**
	  * Pops up a Toast to indicate the connection status
	  */
	 private Runnable showConnectionStatus = new Runnable() {
		 public void run() {
			 Toast.makeText(ctx, connectionStatus, Toast.LENGTH_SHORT).show();
		 }
	 };

	 /**
	  * Read not synchronized values from DatabaseManager and send via USB
	  *
	  * @return
	  */

	 public String messageToUSB(String table) {
		 //Get USB values not synchronized
		 List<Map<String, String>> listReadToUSB = ioActivity.sManager.read_lastSamples_IITDatabaseTable(SensorsManager._EMPATICA_TABLE_NAME, false, Integer.MAX_VALUE);

		 if (listReadToUSB != null) {
			 String jSon = IITServerConnector.convertToJSON(listReadToUSB);
			 return jSon;

		 } else
			 return null;
	 }
	 
	 public boolean isConnected(){
		 try {
	            return connected && client != null && client.isConnected();
	        }catch (Exception e){
	            System.out.println("Thread to connect cannot be started: "+e);

	            return false;
	        }
	 }

	 /**
	  * Get all no sync values, and return a list of the JSON Strings to be sent
	  * @return
	  */
	 public void messageAllAsync(String table) {
		messageNAsync(table, IITDatabaseManager.MAX_READ_SAMPLES_UPDATE);
	 }
	 /**
	  * Message N samples of the async values
	  * @param table
	  * @param samples
	  * @return
	  */
	 
	 public void messageNAsync(String table, int samples) {
		 String[] result = new String[100];
		 List<Map<String, String>> listReadToUSB = ioActivity.sManager.read_lastSamples_IITDatabaseTable(table, false, samples);
	     Collections.reverse(listReadToUSB);

		 //Prepare values to be sent via USB:
		 if (listReadToUSB.size() !=0){
	            sendUSBList(listReadToUSB, table);
	            sendUSBmessage(USBHost._END_COMMAND);

	        } else
	            sendUSBmessage(USBHost._NO_DATA);
		  
		
	 }
	 /**
	  * Send all not async samples
	  * @param table
	  * @return
	  */
	 public void messageLastDias (String table){
		 //String[] result = new String[1];
		 //Get list of values
		 List<Map<String, String>> temp = null;
		 if (table.equals(_DEXCOM)){
			 Map<String, String> value = ioActivity.sManager.readDiASCGMTable(ioActivity.ctx, null);
			 
			 temp = new ArrayList< Map<String, String>>();
			 temp.add(value);
		 }else if (table.equals(_ZEPHYR)){
			 temp = ioActivity.sManager.readDiASExerciseTable(ctx, null);

		 }
		 //Convert in list of JSON Strings
		 //result[0] = IITServerConnector.convertToJSON(temp);
		 
		 //Send sample via USB
		 if (temp.size()!=0){
			 sendUSBmessage(IITServerConnector.convertToJSON(temp));
	         sendUSBmessage(USBHost._END_COMMAND);

		 }else
	         sendUSBmessage(USBHost._NO_DATA);


		 //return result;

	 }
	 
	 /**
	     * Send the list of values via USB
	     * Convert the list into JSON messages
	     * @param values List<Map<String, String></>
	     */
	    public void  sendUSBList(List<Map<String, String>> values, String table){
	        int n = values.size();
	        int max_jsons = 1+(int)Math.ceil(n/USBHost.LOCAL_SENDING_AMOUNT);

	        //Send to Server - CUT INTO SMALLER PIECES TOO
	        List<Map<String, String>> temp = new ArrayList<Map<String, String>>();
	        //List too long: break in smaller chunks
	        int sent = 0;
	        for (int i = 0; i < n && sent < max_jsons; i++) {
	            Map<String, String> val = values.get(i);
	            val.put("table_name", table);
	            temp.add(val);
	            if ((i + 1) % USBHost.LOCAL_SENDING_AMOUNT == 0) {
	                //System.out.println("Save temp");

	                //SEND RIGHT HERE
	                sendUSBmessage(IITServerConnector.convertToJSON(temp));

	                //result[sent] = IITServerConnector.convertToJSON(temp);
	                sent++;
	                temp = new ArrayList<Map<String, String>>();
	                //System.out.println("Saved");
	            }
	        }
	        //SEND RIGHT HERE
	        if (temp.size()!=0)
	        	sendUSBmessage(IITServerConnector.convertToJSON(temp));

	        //Remaining
	        //result[sent]= IITServerConnector.convertToJSON(temp);
	    }
	 

 }