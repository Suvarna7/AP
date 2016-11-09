        package com.empatica.sample.USB;

        import android.content.Context;
        import android.content.Intent;
        import android.os.Handler;
        import android.util.Log;
        import android.widget.Toast;

        import com.empatica.sample.BGService;
        import com.empatica.sample.Database.IITDatabaseManager;
        import com.empatica.sample.MainActivity;
        import com.empatica.sample.R;
        import com.empatica.sample.Server.IITServerConnector;

        import java.io.IOException;
        import java.io.PrintWriter;
        import java.net.ServerSocket;
        import java.net.Socket;
        import java.net.SocketTimeoutException;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.Map;
        import java.util.Scanner;

/**
 * Created by Cat on 7/25/2016.
 */
public class USBHost {

    //Socket server variables
    ServerSocket server = null;
    public String connectionStatus = null;
    public static final int TIMEOUT = 300; //Seconds
    public Intent intent;
    private static final int ANDROID_LOCAL_HOST = 38600;

    //Socekts
    Scanner socketIn;
    PrintWriter socketOut;
    Socket client = null;

    //Reading thread
    public USBReadThread readingThread;


    private int sequence;
    //Tags
    public static final String TAG = "Connection";

    public static Context ctx;
    private MainActivity mActivity;
    public Handler mHandler;

    //Commands
    public static String _GET_DATA = "get_values";
    public static String _GET_ALL = "get_all";
    public static String _GET_ALL_NO_SYNC = "get_all_no_sync";

    public static String _END_COMMAND = "next_end";
    public static String _NO_DATA = "no_data";
    public static String _ACK_SYNCHRONIZED = "usb_sync";
    public static String _CONNECTION_ESTABLISHED = "connection_process_end";
    public static String _CONNECTION_END = "end_connection";

    //Sensor information
    public static final String _SENSOR_ID = "sensor_table";
    public static final String _EMPATICA = "empatica";
    public static final String _DEXCOM = "dexcom";


    //Max number of messages to be accepted thru usb
    public static final int LOCAL_SENDING_AMOUNT = 25;


    public USBHost(Context context, MainActivity activity) {
        mActivity = activity;
        ctx = context;
        readingThread = new USBReadThread(this, ctx);

    }

    public void sendUSBmessage(String msg) {
        if (socketOut != null) {
            System.out.println("send msg");
            //socketOut.print(msg);
            socketOut.println(msg);
            socketOut.flush();
        } else {
            System.out.println("msg not sent " + client);
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

            connectionStatus = "Connection has been started! ";

            // initialize server socket
            try {

                mHandler.post(showConnectionStatus);

                server = new ServerSocket(ANDROID_LOCAL_HOST);
                server.setSoTimeout(TIMEOUT * 100000);
                connectionStatus = "Server socket... " + server.getLocalPort() + " - " + server.getLocalSocketAddress();
                mHandler.post(showConnectionStatus);

                updateConnectedStatus("Connecting...", "USB HOST STARTED - Press Connect in laptop", false);

                //Server waits for a client to try to connect:
                //PC should start connection now
                client = server.accept();
                connectionStatus = "Client accepted... ";
                mHandler.post(showConnectionStatus);

                //Socket IN --> If refused
                createSocketIn(client);
                connectionStatus = "Socket in";
                mHandler.post(showConnectionStatus);
                socketOut = new PrintWriter(client.getOutputStream(), true);

                //Start reading thread:
                try {
                    if (!readingThread.started) {
                        System.out.println("Thread started!");
                        //USBHost temp_reference = readingThread.mHost;
                        //readingThread = new USBReadThread(temp_reference, ctx);
                        readingThread.start();
                    } else {
                        //readingThread.shutup();
                        //readingThread.restart();

                        System.out.println("Thread already started! ");
                        //Re initiate
                        USBHost temp_reference = readingThread.mHost;
                        readingThread = new USBReadThread(temp_reference, ctx);
                        readingThread.start();
                    }

                    if (readingThread.isInterrupted()) {
                        System.out.println("Thread was interrupted");

                        readingThread.restart();
                    }


                } catch (Exception e) {
                    System.out.println("Thread already started: " + e);
                    readingThread.restart();
                }

            } catch (SocketTimeoutException e) {
                // print out TIMEOUT
                connectionStatus = "Connection has timed out! Please try again";
                mHandler.post(showConnectionStatus);

            } catch (IOException e) {
                Log.e(TAG, "" + e);
                System.out.println("Server IO Exception");
                connectionStatus = "Server IO Exception - Restart connection/phone";
                mHandler.post(showConnectionStatus);
            } finally {
                //close the server socket, we already have the client ?
                try {
                    if (server != null)
                        server.close();
                    else if (client != null && socketIn != null && socketOut != null) {
                        // print out success
                        connectionStatus = "Connection was successful!";
                        mHandler.post(showConnectionStatus);
                    }
                } catch (IOException ec) {
                    Log.e(TAG, "Cannot close server socket" + ec);
                }
            }


        }
    };

    private void createSocketIn(Socket client) {
        try {
            socketIn = new Scanner(client.getInputStream());


        } catch (IOException e) {
            Log.e(TAG, "" + e);
            System.out.println("Server IO Exception");
            connectionStatus = "Server IO Exception - Restart connection/phone";
            mHandler.post(showConnectionStatus);

            //Not connected, update status
            //Change Connect USB Button
            updateConnectedStatus("USB CONNECT", "USB PC Client not established", true);

            if (client != null)
                createSocketIn(client);

        }

    }

    public void disconnectUSBHost() {
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

        } catch (IOException e) {
            System.out.println("Exception while closing server: " + e);
        }

        //Update GUI Buttons and status

    }

    public void updateConnectedStatus(String button, String status, boolean enabled) {
        mActivity.updateLabel(mActivity.usbConenctionStatus, status);
        mActivity.updateButton(mActivity.connectUSBButton, button, ctx.getResources().getColor(R.color.dark_green_paleta), enabled);


    }

    /**
     * Pops up a “toast” to indicate the connection status
     */
    private Runnable showConnectionStatus = new Runnable() {
        public void run() {
            Toast.makeText(ctx, connectionStatus, Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * Get all no sync values, and return a list of the JSON Strings to be sent
     *
     * @return
     */
    public void messageAllAsync(String table) {
        messageNAsync(table, IITDatabaseManager.MAX_READ_SAMPLES_UPDATE);
    }

    /**
     * Message N samples of the async values
     *
     * @param table
     * @param samples
     * @return
     */

    public void messageNAsync(String table, int samples) {
        //Get last not SYNCHRONIZED values (Not sent via USB)
         mActivity.messageAllAsync(table, IITDatabaseManager.syncColumn, IITDatabaseManager.syncStatusNo, samples);

    }

    //**************************************************************
    // Read, send adn received messsages


    /**
     * Send the list of values via USB
     * Conver the list into JSON messages
     * @param values List<Map<String, String></>
     */
    public void  sendUSBList(List<Map<String, String>> values, String table_name){
        int n = values.size();
        int max_jsons = 1+(int)Math.ceil(n/USBHost.LOCAL_SENDING_AMOUNT);

        //Send to Server - CUT INTO SMALLER PIECES TOO
        List<Map<String, String>> temp = new ArrayList<Map<String, String>>();
        //List too long: break in smaller chunks
        int sent = 0;
        for (int i = 0; i < n && sent < max_jsons-1; i++) {
            Map<String, String> val = values.get(i);
            val.put("table_name", table_name);
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
        if (temp != null)
            sendUSBmessage(IITServerConnector.convertToJSON(temp));
        //sendUSBmessage(USBHost._END_COMMAND);

        //Remaining
        //result[sent]= IITServerConnector.convertToJSON(temp);
    }
}