package iit.pc.javainterface.usb;

import java.awt.Color;
import java.io.IOException;
import java.net.SocketException;
import java.util.List;

import iit.pc.javainterface.BMBridge;
import iit.pc.javainterface.matlab.MatlabSocket;

public class USBReadThread extends Thread {
	private  boolean shut;
	private USB_PCHost mHost;
	private BMBridge mBridge;
	public int matlab_request;
	


	//Indicate whether connection was successful
	public boolean connection_succesful;


	public USBReadThread(USB_PCHost host, BMBridge bridge){
		System.out.println("Create read");
		shut = false;
		mHost = host;
		mBridge =  bridge;
		connection_succesful = false;
		matlab_request = 0;
	}

	public void run() {
		while(!shut){
			//System.out.println("Thread loop!");
			try {

				//if (mHost!= null && mHost.sc != null && mHost.sc.hasNext()) {
				//String line = mHost.sc.nextLine(); 

				String line =mHost.in.readLine();
				//System.out.println("Line read: "+line);

				if (line != null && !line.equals("null")){
					System.out.println("Received line: "+line);
					//When DiAS is ready with new CGM sample
					if (line.equals(USB_PCHost._PHONE_READY)){
						//Send to matlab
						mBridge.matSocket.sendMessageToMatlab(line);

					}
					//Last sample was sent
					else if (line.equals(USB_PCHost._END_COMMAND)){
						if (matlab_request > 0){
							//Send to matlab if conneciton is open
							mBridge.matSocket.sendMessageToMatlab(MatlabSocket._SENT_ALL);
							matlab_request --;
						}
						else{
							//Announce the end of MATLAB writing
							mBridge.allDataCollected = true;
							mBridge.readyToRun();
							//Update button
							mBridge.gui.usbGetButton.setText("USB get values");
							mBridge.gui.usbGetButton.setEnabled(true);
						}

						
					}
					//When there is no data coming from the phone
					else if (line.equals(USB_PCHost._NO_DATA)){
						//Announce the end of MATLAB writing
						//Update button
						System.out.println("No data in the phone");
						mBridge.gui.usbGetButton.setText("USB get values");
						mBridge.gui.usbGetButton.setEnabled(true);

						//Send to matlab too
						mBridge.matSocket.sendMessageToMatlab(MatlabSocket._SENT_ALL);


					}
					//ACK of connection established int e other side
					else if (line.equals(USB_PCHost._CONNECTION_ESTABLISHED)){
						//TODO Now we can update the GUI
						mHost.mBridge.gui.usbConnectButton.setText("CONNECTED");
						mHost.mBridge.gui.usbConnectButton.setEnabled(false);
						mHost.mBridge.gui.usbConnectButton.setBackground(Color.GREEN);


					}
					//When disconnection request
					else if (line.equals(USB_PCHost._CONNECTION_END)){
						//TODO
						System.out.println("End connection!");

					}else if (line.equals(USB_PCHost._WRONG_COMMAND))
						System.out.println("We sent a command not recognized: "+line);
					//Anything else should be a data sample: JSON
					else if (line.contains("synchronized")){
						System.out.println(line);
						if (matlab_request>0)
							//Send to matlab:
							//if (!mBridge.matSocket.sendMessageToMatlab(line)){
							mBridge.matSocket.sendMessageToMatlab(line);
						else{
						//TODO Write to excel:
						List<String> responseBack = mBridge.handleJSONResponse(line);

							//If we were able to write in the excel file, we send an ACK Back:
						for (String ack_msg: responseBack)
							mHost.sendUSBmessage(ack_msg);
						}
						
					}

					else {
						System.out.println("Command received: "+line);
						//In any other case, restart
						//mHost.readThread.start();
						mHost.sendUSBmessage(USB_PCHost._WRONG_COMMAND);

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
