package iit.pc.javainterface.matlab;

import java.awt.Color;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import iit.pc.javainterface.BMBridge;
import iit.pc.javainterface.JSONToSend;
import iit.pc.javainterface.MyHttpClient;
import iit.pc.javainterface.usb.USB_PCHost;

public class MatlabReadThread extends Thread {
    private  boolean shut;
    private MatlabSocket mHost;
    //private BMBridge mBridge;
    
	
	//Indicate whether connection was successful
	public boolean connection_succesful;


    public MatlabReadThread(MatlabSocket host){
    	System.out.println("Create read");
    	shut = false;
    	mHost = host;
    	//mBridge =  bridge;
    	connection_succesful = false;
    }

	public void run() {
		while(!shut){
			try {
				String line =mHost.in.readLine();

				if (line != null){
					System.out.println("Received line: "+line);
					
					//Request data command received:
					if (line.equals(MatlabSocket._READ_LAST_SAMPLES)){
						//TODO
						//Read samples from Phone - request with USBHost
						//TODO::: DEBUG
						mHost.mBridge.mHost.sendUSBmessage(USB_PCHost._GET_ALL_NO_SYNC);


						//When there is no more to send, send back to MATLAB Program too
						//mHost.sendMessageToMatlab(MatlabSocket._SENT_ALL);

					}else if(line.contains(MatlabSocket._ACK)){
						//Send ack to phone
						mHost.mBridge.mHost.sendUSBmessage(line);
					}

				}
				else{
					//Initialize the connection 
					//mHost.readThread.start();
					
				}

				
				//}
			} catch (SocketException se){
				System.out.println("Socket closed! "+ se);
				
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(e);

			} 
			//System.out.println("End of while in reading thread... will start? "+ !shut);

			
		}
		
	}
	public void shutdown(){
		shut = true;
		System.out.println("Shutdown: "+ shut);

	}
	
	public void shutup(){
		shut = false;
		System.out.println("Shutdown: "+ shut);

	}

}
