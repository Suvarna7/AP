        package com.empatica.sample.USB;

        import android.content.Context;
        import android.content.Intent;
        import android.os.Handler;
        import android.util.Log;
        import android.widget.Toast;

        import com.empatica.sample.MainActivity;
        import com.empatica.sample.R;

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

    //Socket server variables
    ServerSocket server=null;
    public  String connectionStatus=null;
    public static final int TIMEOUT=300; //Seconds
    public Intent intent;
    private static final int ANDROID_LOCAL_HOST = 38600;

    //Socekts
    Scanner socketIn;
    PrintWriter socketOut;
    Socket client=null;

    //Reading thread
    public USBReadThread readingThread;


    private int sequence;
    //Tags
    public static final String TAG= "Connection";

    public static Context ctx;
    private MainActivity mActivity;
    public Handler mHandler;

    //Commands
    public static String _GET_DATA = "get_values";
    public static String _GET_ALL = "get_all";
    public static String _GET_ALL_NO_SYNC = "get_all_no_sync";

    public static String _END_COMMAND = "next_end" ;
    public static String _NO_DATA = "no_data";
    public static String _ACK_SYNCHRONIZED = "usb_sync";
    public static String _CONNECTION_ESTABLISHED = "connection_process_end";
    public static String _CONNECTION_END = "end_connection";


    public USBHost (Context context, MainActivity activity){
        mActivity = activity;
        ctx = context;
        readingThread = new USBReadThread( this, ctx);

    }

    public void sendUSBmessage(String msg){
        if (socketOut != null) {
            System.out.println("send msg");
            //socketOut.print(msg);
            socketOut.println(msg );
            socketOut.flush();
        }else {
            System.out.println("msg not sent "+client);
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

                server = new ServerSocket(ANDROID_LOCAL_HOST);
                server.setSoTimeout(TIMEOUT * 100000);
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
                        readingThread = new USBReadThread(temp_reference, ctx);
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
                System.out.println("Server IO Exception");
                connectionStatus="Server IO Exception - Restart connection/phone";
                mHandler.post(showConnectionStatus);
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
            connectionStatus="Server IO Exception - Restart connection/phone";
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

        }catch (IOException e){
            System.out.println("Exception while closing server: "+e);
        }

        //Update GUI Buttons and status

    }

    public void updateConnectedStatus(String button, String status, boolean enabled){
        mActivity.updateLabel(mActivity.usbConenctionStatus, status);
        mActivity.updateButton(mActivity.connectUSBButton,button , ctx.getResources().getColor(R.color.dark_green_paleta), enabled);


    }
    /**
     * Pops up a “toast” to indicate the connection status
     */
    private Runnable showConnectionStatus = new Runnable() {
        public void run() {
            Toast.makeText(ctx, connectionStatus, Toast.LENGTH_SHORT).show();
        }
    };
}