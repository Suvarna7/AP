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
    private MatlabSocket matlabHost;
    private final static int _NUM_SENSORS = 2;
    //private BMBridge mBridge;
    
	
	//Indicate whether connection was successful
	public boolean connection_succesful;


    public MatlabReadThread(MatlabSocket host){
    	System.out.println("Create read");
    	shut = false;
    	matlabHost = host;
    	//mBridge =  bridge;
    	connection_succesful = false;
    }

	public void run() {
		while(!shut){
			try {
				String line = matlabHost.in.readLine();

				if (line != null){
					System.out.println("MATLAB received line: "+line);
					
					//Request data command received:
					if (line.equals(MatlabSocket._READ_LAST_SAMPLES)){
						//TODO
						//Read samples from Phone - request with USBHost
						//TODO::: DEBUG
						
						//FIRST SENSOR: EMPATICA
						request_data_message(USB_PCHost._EMPATICA);
						
						//SECOND SENSOR: DEXCOM
						request_data_message(USB_PCHost._DEXCOM);

						//UPDATE Number of sensors requested
						matlabHost.mBridge.mHost.readThread.matlab_request = _NUM_SENSORS ;

					}
					else if (line.equals(USB_PCHost._DEXCOM)|| line.equals(USB_PCHost._EMPATICA)){
						request_data_message(line);
						matlabHost.mBridge.mHost.readThread.matlab_request = 1 ;
						
					}
					//Send ACK message back to phone
					else if(line.contains(MatlabSocket._ACK)){
						//Send ack to phone
						matlabHost.mBridge.mHost.sendUSBmessage(line);
					}
					//Send a INSULIN command to the phone
					else if (line.contains(MatlabSocket._INSULIN)){
						matlabHost.mBridge.mHost.sendUSBmessage(line);

						
					}
					//Send a HYPO ALARM command to the phone
					else if (line.contains(MatlabSocket._HYPO)){
						matlabHost.mBridge.mHost.sendUSBmessage(line);

						
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
	
	private void request_data_message(String sensor){
		String message = "{command: "+USB_PCHost._GET_ALL_NO_SYNC+", "+USB_PCHost._SENSOR_ID+": "+sensor+"}";
		//matlabHost.mBridge.mHost.sendUSBmessage(USB_PCHost._GET_ALL_NO_SYNC);
		matlabHost.mBridge.mHost.sendUSBmessage(message);
		
	}

}
