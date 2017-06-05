package iit.pc.javainterface.matlab;

import java.awt.Color;
import java.io.IOException;
import java.net.SocketException;
import iit.pc.javainterface.BMGuiProgram;
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
						
						//Start a timer .... just in case!

						/* TODO Timer timer = new Timer();
						timer.schedule(new TimerTask() {
						  @Override
						  public void run() {
							  if(!received_usb){
						    // Send end command
							  System.out.println("TIMER 5 MIN ELAPSED!");
							 matlabHost.sendMessageToMatlab(MatlabSocket._SENT_ALL); 
							  }
							}
						}, 4*60*1000);*/
						
						//FIRST SENSOR: EMPATICA
						request_data_message_N_samples(USB_PCHost._EMPATICA, MatlabSocket._EMPATICA_ONE_MIN);
						
						//SECOND SENSOR: DEXCOM
						request_data_message_N_samples(USB_PCHost._DEXCOM, MatlabSocket._ALL_SAMPLES);

						//UPDATE Number of sensors requested
						matlabHost.mBridge.mHost.readThreads[USB_PCHost._PHONE_1_INDEX].matlab_request = _NUM_SENSORS ;
						matlabHost.mBridge.gui.updateUSBConnectionStatus("Request messages", "..................", 1);

						

					}
					else if (line.equals(USB_PCHost._DEXCOM)|| line.equals(USB_PCHost._EMPATICA)){
						request_data_message_N_samples(line, MatlabSocket._ALL_SAMPLES);
						matlabHost.mBridge.mHost.readThreads[USB_PCHost._PHONE_1_INDEX].matlab_request = 1 ;
						matlabHost.mBridge.gui.updateUSBConnectionStatus("Request messages", "..................", 1);

						//Start a timer .... just in case!

						/*TODOTimer timer = new Timer();
						timer.schedule(new TimerTask() {
						  @Override
						  public void run() {
							  if(!received_usb){
								    // Send end command
									  System.out.println("TIMER 5 MIN ELAPSED!");
									 matlabHost.sendMessageToMatlab(MatlabSocket._SENT_ALL); 
							  }
							  }
						}, 4*60*1000);*/
						
					}
					//Send ACK message back to phone
					else if(line.contains(MatlabSocket._ACK)){
						//Send ack to phone
						matlabHost.mBridge.gui.updateUSBConnectionStatus("Messages processed: Sending ACKs", ".....................", 1);
						matlabHost.mBridge.mHost.sendUSBmessage(line, USB_PCHost.PC_LOCAL_HOST);
						matlabHost.mBridge.gui.updateUSBConnectionStatus("Messages processed: Sending ACKs", "", 1);
						
						//Change USB state: disconnected
						//When DiAS is ready with new CGM sample

					}
					//Send a INSULIN command to the phone
					else if (line.contains(MatlabSocket._INSULIN)){
						matlabHost.mBridge.mHost.sendUSBmessage(line, USB_PCHost.PC_LOCAL_HOST);

						
					}
					//Send a HYPO ALARM command to the phone
					else if (line.contains(MatlabSocket._HYPO)){
						matlabHost.mBridge.mHost.sendUSBmessage(line, USB_PCHost.PC_LOCAL_HOST);

						
					}
					//Check Bluetooth connections
					else if (line.contains(MatlabSocket._BL_REQ)){
						matlabHost.mBridge.mHost.sendUSBmessage(line, USB_PCHost.PC_LOCAL_HOST);


					}
					//Check USB connection
					else if (line.equals(MatlabSocket._USB_REQ)){
						//FIRST APPROACH: Send usb connection and wait for reply
						//matlabHost.mBridge.mHost.verifyConnection();
						//SECOND APPROACH: Connection reset at the end of every run period
						if (matlabHost.mBridge.mHost.isConnected(USB_PCHost.PC_LOCAL_HOST)){
							matlabHost.sendMessageToMatlab(MatlabSocket._USB_CONNECTED);
							//Update GUI
							//TODO Now we can update the GUI
							matlabHost.mBridge.gui.usbConnectButton1.setText("CONNECTED");
							matlabHost.mBridge.gui.usbConnectButton1.setEnabled(false);
							matlabHost.mBridge.gui.usbConnectButton1.setBackground(Color.GREEN);
						}else{
							matlabHost.sendMessageToMatlab(MatlabSocket._USB_DISCONNECTED);
							//Update GUI
							matlabHost.mBridge.gui.usbConnectButton1.setText("USB Connect");
							matlabHost.mBridge.gui.usbConnectButton1.setEnabled(true);
							matlabHost.mBridge.gui.usbConnectButton1.setBackground(BMGuiProgram.DULL_BUTTON_COLOR);
							
							
						}
						 System.out.println("Connection status set to false");

						matlabHost.mBridge.mHost.updateConnectionStatus(false);


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
	
	private void request_data_message_N_samples(String sensor, int samples){
		String message = "{command: "+USB_PCHost._GET_ALL_NO_SYNC+
							", "+USB_PCHost._SENSOR_ID+": "+sensor+
							", "+USB_PCHost._NUM_SAMPLES+": "+samples+"}";
		//matlabHost.mBridge.mHost.sendUSBmessage(USB_PCHost._GET_ALL_NO_SYNC);
		//TODO matlabHost.mBridge.mHost.usbResetCommunicationLink();
		matlabHost.mBridge.mHost.sendUSBmessage(message, USB_PCHost.PC_LOCAL_HOST);
		
	}

}
