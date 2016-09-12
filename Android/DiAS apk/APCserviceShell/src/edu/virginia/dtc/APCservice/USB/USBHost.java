package edu.virginia.dtc.APCservice.USB;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

/**
 * Created by Cat on 7/25/2016.
 */
public class USBHost {

	// Socket server variables
	ServerSocket server = null;
	public String connectionStatus = null;
	public static final int TIMEOUT = 300; // Seconds
	public Intent intent;
	private static final int ANDROID_LOCAL_HOST = 38300;

	// Sockets
	Scanner socketIn;
	PrintWriter socketOut;
	Socket client = null;

	// Reading thread
	public USBReadThread readingThread;

	// Tags
	public static final String TAG = "Connection";

	public static Context ctx;
	public Handler mHandler;
	
	//Connection state
	public boolean connected;
	
	//COMMANDS:
	public static final String _PHONE_GET = "get_values";
	private static String _END_COMMAND = "next_end" ;
    private static String _NO_DATA = "no_data";
	public static String _ACK_SYNCRHONIZED = "usb_sync";
	public static String _CONNECTION_ESTABLISHED = "connection_process_end";

	public USBHost(Context context) {

		ctx = context;
		readingThread = new USBReadThread(this);
		connected = false;

	}

	public void sendUSBmessage(String msg) {
		if (socketOut != null) {
			System.out.println("send msg");
			// socketOut.print(msg);
			socketOut.println(msg);
			socketOut.flush();
		} else
			System.out.println("msg not sent");

	}

	// **************************************************
	// RUNNABLE Thread to init the USB connection
	public Runnable initializeConnection = new Thread() {
		public void run() {

			while(!connected){
			connectionStatus = "Connection has been started! ";
			// initialize server socket
			try {

				initializeSteps();

			} catch (SocketTimeoutException e) {
				// print out TIMEOUT
				connectionStatus = "Connection has timed out! We will try again";
				mHandler.post(showConnectionStatus);
				
			} catch (IOException e) {
				Log.e(TAG, "" + e);
				System.out.println("Server IO Exception");
			} finally {
				// close the server socket
				try {
					if (server != null)
						server.close();
				} catch (IOException ec) {
					Log.e(TAG, "Cannot close server socket" + ec);
				}
			}

			if (client != null) {
				// Globals.connected=true;
				// print out success

				connectionStatus = "Connection was successful!";
				mHandler.post(showConnectionStatus);
				// startActivity(intent);
				connected = true;
			}
			}
		}
	};
	
	private boolean initializeSteps() throws IOException, SocketTimeoutException{
		mHandler.post(showConnectionStatus);

		server = new ServerSocket(ANDROID_LOCAL_HOST);
		server.setSoTimeout(TIMEOUT * 100000);
		connectionStatus = "Server socket... " + server.getLocalPort() + " - " + server.getLocalSocketAddress();
		mHandler.post(showConnectionStatus);

		// Server waits for a client to try to connect:
		// PC should start connection now
		client = server.accept();
		connectionStatus = "Client accepted... ";
		mHandler.post(showConnectionStatus);
		socketIn = new Scanner(client.getInputStream());
		connectionStatus = "Socket in";
		mHandler.post(showConnectionStatus);
		socketOut = new PrintWriter(client.getOutputStream(), true);

		// Start reading thread:
		readingThread.run();
		
		//Send connection message:
		sendUSBmessage(_CONNECTION_ESTABLISHED);

		
		return true;
		
	}

	/**
	 * Pops up a â€œtoastâ€� to indicate the connection status
	 */
	private Runnable showConnectionStatus = new Runnable() {
		public void run() {
			Toast.makeText(ctx, connectionStatus, Toast.LENGTH_SHORT).show();
		}
	};
}