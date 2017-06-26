package iit.pc.javainterface.usb;

import java.awt.Color;
import java.io.IOException;
import java.net.SocketException;
import iit.pc.javainterface.BMBridge;
import iit.pc.javainterface.BMGuiProgram;
import iit.pc.javainterface.matlab.MatlabSocket;

public class USBReadThread extends Thread {
	private  boolean shut;
	private USB_PCHost mHost;
	private BMBridge mBridge;
	public int matlab_request;
	private int phoneHost;
	private int indexPhoneGui;
	

	//Indicate whether connection was successful
	//public boolean connection_succesful;

/**
 * Create reading thread
 * @param host - USB_PCHost conneced
 * @param bridge - BMBridge
 */
	public USBReadThread(USB_PCHost host, BMBridge bridge, int phone){
		System.out.println("Create read");
		shut = false;
		mHost = host;
		mBridge =  bridge;
		phoneHost = phone;
		indexPhoneGui = USB_PCHost.getPortIndex(phoneHost)+1; //Port index: 0,1,2 ...
			
		//connection_succesful = false;
		matlab_request = 0;
	}

	public void run() {
		while(!shut){
			//System.out.println("Thread loop!");
			try {

				//if (mHost!= null && mHost.sc != null && mHost.sc.hasNext()) {
				//String line = mHost.sc.nextLine(); 

				String line =mHost.ins[indexPhoneGui-1].readLine();
				//System.out.println("Line read: "+line);

				if (line != null && !line.equals("null")){
					System.out.println("Received line: "+line);
					//When DiAS is ready with new CGM sample
					mHost.updateConnectionStatus(true);
					
					if (line.equals(USB_PCHost._PHONE_READY)){
						//Send to matlab
						mBridge.matSocket.sendMessageToMatlab(line);

					}
					//Last sample was sent
					else if (line.contains(USB_PCHost._END_COMMAND)){
						System.out.println("RECOGNIZED! "+line);
						if (matlab_request > 0){
							//Send to matlab if connectIon is open
							mBridge.matSocket.sendMessageToMatlab(MatlabSocket._SENT_ALL);
							matlab_request --;
						}
						else{
							//Announce the end of MATLAB writing
							mBridge.allDataCollected = true;
							mBridge.readyToRun();
							//Update button
							mBridge.gui.usbGetButton1.setText("USB get values");
							mBridge.gui.usbGetButton1.setEnabled(true);
						}
						mBridge.gui.updateUSBConnectionStatus("Received all messages  ", "|||", indexPhoneGui);
						
						//TODO Reset USB communication
						mBridge.mHost.usbResetCommunicationLink(indexPhoneGui-1);
						
					}
					//When there is no data coming from the phone
					else if (line.contains(USB_PCHost._NO_DATA)){
						//Announce the end of MATLAB writing
						//Update button
						System.out.println("No data in the phone");
						mBridge.gui.usbGetButton1.setText("USB get values");
						mBridge.gui.usbGetButton1.setEnabled(true);

						//Send to matlab too
						mBridge.matSocket.sendMessageToMatlab(MatlabSocket._SENT_ALL);
						
						mBridge.gui.updateUSBConnectionStatus("Messages read: ", "NO DATA", indexPhoneGui);


					}
					//ACK of connection established in the other side
					else if (line.equals(USB_PCHost._CONNECTION_ESTABLISHED)){
						//Now we can update the GUI
						if (phoneHost == USB_PCHost.PC_LOCAL_HOST){
							mHost.mBridge.gui.usbConnectButton1.setText("CONNECTED");
							mHost.mBridge.gui.usbConnectButton1.setEnabled(false);
							mHost.mBridge.gui.usbConnectButton1.setBackground(Color.GREEN);
						}else if (phoneHost == USB_PCHost.PC_LOCAL_HOST2){
							mHost.mBridge.gui.usbConnectButton2.setText("CONNECTED");
							mHost.mBridge.gui.usbConnectButton2.setEnabled(false);
							mHost.mBridge.gui.usbConnectButton2.setBackground(Color.GREEN);
						}
						
						//Let's send our public key and see what happens
						//TODO mHost.sendHello();

					}
					//When disconnection request
					else if (line.equals(USB_PCHost._CONNECTION_END)){
						//TODO
						System.out.println("End connection!");
						if (phoneHost == USB_PCHost.PC_LOCAL_HOST){
							mHost.mBridge.gui.usbConnectButton1.setText("USB Connect");
							mHost.mBridge.gui.usbConnectButton1.setEnabled(true);
							mHost.mBridge.gui.usbConnectButton1.setBackground(BMGuiProgram.DULL_BUTTON_COLOR);
						}else if (phoneHost == USB_PCHost.PC_LOCAL_HOST2){
							mHost.mBridge.gui.usbConnectButton2.setText("USB Connect");
							mHost.mBridge.gui.usbConnectButton2.setEnabled(true);
							mHost.mBridge.gui.usbConnectButton2.setBackground(BMGuiProgram.DULL_BUTTON_COLOR);
						}

						 System.out.println("Connection status set to false");

						mHost.updateConnectionStatus(false);


					}
					//When Sync ack received from phone
					else if (line.equals(USB_PCHost._ACK_SYNCHRONIZED)){
						mBridge.gui.updateUSBConnectionStatus("Phone finished ACKs ", "|||", indexPhoneGui);

					}
					else if (line.equals(USB_PCHost._ACK_TEST_USB))
						mHost.updateConnectionStatus(true);
					else if (line.equals(USB_PCHost._WRONG_COMMAND))
						System.out.println("We sent a command not recognized: "+line);
					
					//BL response
					else if (line.equals(USB_PCHost._VERIFY_DEVICE_CONNECTED)
				    		|| line.equals(USB_PCHost._VERIFY_DEVICE_DISCONNECTED)){
						//Send response to MATLAB
						mBridge.matSocket.sendMessageToMatlab(line);
					}
					//first command received
					else if (line.equals(USB_PCHost._START_SENDING)){
						if (matlab_request>0)
							mBridge.matSocket.sendMessageToMatlab(line);
						else
							System.out.println("We will be receiving data");

					}

					//Anything else should be a data sample: JSON
					else if (line.contains("synchronized")){
						mBridge.gui.updateUSBConnectionStatus("Receiving messages  ", "", indexPhoneGui);

						System.out.println(line);
						if (matlab_request>0)
							//Send to matlab:
							//if (!mBridge.matSocket.sendMessageToMatlab(line)){
							mBridge.matSocket.sendMessageToMatlab(line);
						else{
							//TODO Write to excel:
							//List<String> responseBack = mBridge.handleJSONResponse(line);
							//If we were able to write in the excel file, we send an ACK Back:
							//TODO for (String ack_msg: responseBack)
								//mHost.sendUSBmessage(ack_msg);
							mBridge.gui.updateUSBConnectionStatus("Not sending to MATLAB  ", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX", indexPhoneGui);

						}
						
						mBridge.gui.updateUSBConnectionStatus("Receiving messages  ", "........................", indexPhoneGui);
					}

					else {
						System.out.println("Command received: "+line);
						//In any other case, restart
						//mHost.readThread.start();
						mHost.sendUSBmessage(USB_PCHost._WRONG_COMMAND, USB_PCHost.getPortIndex(indexPhoneGui-1));
						mBridge.gui.updateUSBConnectionStatus("Not recognized command:  ", ""+line, indexPhoneGui);

					}
				}
				else{
					//Initialize the connection 
					//mHost.readThread.start();

				}


				//}
			} catch (SocketException se){
				System.out.println("Socket closed! "+ se);
				shutdown();

			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("USB reader "+e);

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
