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

    public USBHost (Context context, MainActivity activity){
        mActivity = activity;
        ctx = context;
        readingThread = new USBReadThread(this);

    }

    public void sendUSBmessage(String msg){
        if (socketOut != null) {
            System.out.println("send msg");
            //socketOut.print(msg);
            socketOut.println(msg);
            socketOut.flush();
        }else
            System.out.println("msg not sent");

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

                //Server waits for a client to try to connect:
                //PC should start connection now
                client = server.accept();
                connectionStatus="Client accepted... ";
                mHandler.post(showConnectionStatus);
                socketIn=new Scanner(client.getInputStream());
                connectionStatus="Socket in";
                mHandler.post(showConnectionStatus);
                socketOut = new PrintWriter(client.getOutputStream(), true);

                //Change Conenct USB Button
                mActivity.updateButton(mActivity.connectUSBButton, "CONNECTED", ctx.getResources().getColor(R.color.dark_green_paleta));




                //Start reading thread:
                readingThread.run();

            } catch (SocketTimeoutException e) {
                // print out TIMEOUT
                connectionStatus="Connection has timed out! Please try again";
                mHandler.post(showConnectionStatus);
            } catch (IOException e) {
                Log.e(TAG, "" + e);
                System.out.println("Server IO Exception");
            } finally {
                //close the server socket
                try {
                    if (server!=null)
                        server.close();
                } catch (IOException ec) {
                    Log.e(TAG, "Cannot close server socket"+ec);
                }
            }

            if (client!=null) {
                //Globals.connected=true;
                // print out success

                connectionStatus="Connection was successful!";
                mHandler.post(showConnectionStatus);
                //startActivity(intent);
            }
        }
    };

    /**
     * Pops up a “toast” to indicate the connection status
     */
    private Runnable showConnectionStatus = new Runnable() {
        public void run() {
            Toast.makeText(ctx, connectionStatus, Toast.LENGTH_SHORT).show();
        }
    };
}