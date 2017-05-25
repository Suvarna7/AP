package iit.pc.javainterface.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import iit.pc.javainterface.BMGuiProgram;
import iit.pc.javainterface.usb.USB_PCHost;

/**
 * Listener for all actions on frame
 * 	- Send button
 * 	- Table name field
 */
public class ButtonsListener implements ActionListener{

	//USB buttons
	public JButton usbConnectButton1;
	public JButton usbDisconnectButton1;
	public JButton usbGetButton1;
	public JButton usbConnectButton2;
	public JButton usbDisconnectButton2;
	public JButton usbGetButton2;

	//MATLAB Buttons
	public JButton matlabConnectButton;
	public JButton matlabDisonnectButton;

	//Send and receive to server
	public JButton automaticStartButton;
	public JButton automaticStopButton;


	//*************** ACTIONS COMMAND
	public static final String SEND_BUTTON_COMMAND = "SendButton";
	public static final String GET_BUTTON_COMMAND = "GetButton";
	public static final String TABLE_FIELD_COMMAND = "TableField";
	public static final String AUTOMATIC_START_COMMAND = "AutomaticStart";
	public static final String AUTOMATIC_STOP_COMMAND = "AutomaticStop";
	public static final String WARNED = "WarningOK";
	public static final String CHOOSE_FILE = "ChooseFile";
	public static final String CONNECT_USB1 = "ConnectUSB1";
	public static final String DISCONNECT_USB1 = "DisconnectUSB1";
	public static final String CONNECT_USB2 = "ConnectUSB2";
	public static final String DISCONNECT_USB2 = "DisconnectUSB2";
	public static final String SEND_COMMAND = "SendUSB";
	public static final String USB_GET_VALUES1 = "GetUSBValues1";
	public static final String USB_GET_VALUES2 = "GetUSBValues2";
	public static final String CHOOSE_ADB = "ChooseAdbLocation";
	public static final String CONNECT_MATLAB = "ConnectMAT";
	public static final String DISCONNECT_MATLAB = "DisconnectMAT";

	BMGuiProgram mainGUI;

	public ButtonsListener(BMGuiProgram mainGUI){
		this.mainGUI = mainGUI;
		createButtons();
	}

	private void createButtons(){

		//****** TODO Unactive - Add the START, STOP automatic button	
		automaticStartButton= newButton("START", ButtonsListener.AUTOMATIC_START_COMMAND, false);
		automaticStopButton= newButton("STOP", ButtonsListener.AUTOMATIC_STOP_COMMAND, false);


		//******** Add a connect, disconnect and get usb button 2
		usbConnectButton1= newButton("USB Connect", ButtonsListener.CONNECT_USB1, true);
		usbDisconnectButton1 = newButton("USB Disonnect", ButtonsListener.DISCONNECT_USB1, true);
		usbGetButton1 = newButton("USB Get values", ButtonsListener.USB_GET_VALUES1, true);

		//******** Add a connect, disconnect and get usb button 2
		usbConnectButton2= newButton("USB Connect", ButtonsListener.CONNECT_USB2, true);
		usbDisconnectButton2 = newButton("USB Disonnect", ButtonsListener.DISCONNECT_USB2, true);
		usbGetButton2 = newButton("USB Get values", ButtonsListener.USB_GET_VALUES2, true);


		//******* Add MATLAB connect & disconnect usb button
		matlabConnectButton = newButton("MATLAB Connect", ButtonsListener.CONNECT_MATLAB, true);
		matlabDisonnectButton = newButton("MATLAB Disconnect", ButtonsListener.DISCONNECT_MATLAB, true);
		


	}
	
	private JButton newButton(String name, String actionCommand, boolean active){
		JButton button = new JButton(name);
		button.setEnabled(active);
		button.setActionCommand(actionCommand);
		button.addActionListener(this);
		button.setBackground(BMGuiProgram.DULL_BUTTON_COLOR);
		return button;
	}


	@Override
	public void actionPerformed(ActionEvent e) {

		String actionCommand = e.getActionCommand();
		if(actionCommand.equals(SEND_BUTTON_COMMAND)){

			//Clean previous error valeus
			mainGUI.cleanErrorsScreen();
			mainGUI.bridge.resetExceptionFlags();

			// Read excel

			//Read values from excel file
			//List<HashMap<String, String>> readValues = bridge.readExcelValues();
			//Update bridge json object
			mainGUI.bridge.readExcelValuesAndUpdateJSON();

			//Update the result of reading values in the GUI
			//setSentLabel(bridge.getSendResult());
			// Send values
			mainGUI.bridge.sendToIITServer();
			//Update the result of sending values in the GUI
			//setSentLabel(bridge.getSendResult());


		}else if(actionCommand.equals(GET_BUTTON_COMMAND)){
			//Clean any previous error
			mainGUI.cleanErrorsScreen();
			mainGUI.bridge.resetExceptionFlags();

			//Read values from excel file
			//List<HashMap<String, String>> readValues = bridge.readExcelValues();
			//Update bridge json object
			mainGUI.bridge.readFromIITServer();
			//Update the result of reading values in the GUI
			mainGUI.setSentLabel(mainGUI.bridge.getSendResult());
			// Send values
			//bridge.sendToIITServer();
			//Update the result of sending values in the GUI
			//setSentLabel(bridge.getSendResult());


		}else if(actionCommand.equals(TABLE_FIELD_COMMAND)&& e.getSource()== mainGUI.tableNameField){
			//Update table name 
			System.out.println("Set table name");
			mainGUI.bridge.setTABLE_NAME((mainGUI.tableNameField).getText());
		}else if(actionCommand.equals(AUTOMATIC_START_COMMAND)&& mainGUI.bridge.automaticWriter !=null){

			//Clean previous error values
			mainGUI.cleanErrorsScreen();
			mainGUI.bridge.resetExceptionFlags();
			mainGUI.bridge.automaticWriter.startAutomaticWrites();
			//Change colors of buttons
			mainGUI.period.setText("Time(s):"+mainGUI.bridge.automaticWriter.getRepeatInterval());
			automaticStartButton.setBackground(Color.GREEN);
			automaticStopButton.setBackground(BMGuiProgram.DULL_BUTTON_COLOR);


		}else if(actionCommand.equals(AUTOMATIC_STOP_COMMAND) && mainGUI.bridge.automaticWriter !=null){


			//Clean previous error values
			mainGUI.cleanErrorsScreen();
			mainGUI.bridge.resetExceptionFlags();

			mainGUI.bridge.automaticWriter.stopAutomaticWrites();
			//Change colors of buttons
			automaticStopButton.setBackground(Color.RED);
			automaticStartButton.setBackground(BMGuiProgram.DULL_BUTTON_COLOR);



		}else if (actionCommand.equals(WARNED)){
			//The user was warned
			mainGUI.cleanErrorsScreen();
			mainGUI.warningButton.setVisible(false);
			mainGUI.bridge.resetExceptionFlags();
			mainGUI.errorDisplay.setVisible(false);

		}else if(actionCommand.equals(CHOOSE_FILE)){
			//Select a new file and update the name
			mainGUI.excelTextFile.setText(mainGUI.selectFileDialog());
		}else if (actionCommand.equals(CONNECT_USB1)){
			//Start USB connection
			if (mainGUI.bridge.mHost.initializeConnection(USB_PCHost.PC_LOCAL_HOST)){
				//Start reading thread
				//bridge.mHost.readThread.start();
				//Only if connection successful
				usbConnectButton1.setText("Waiting for phone response...");

				//Start reading thread
				//bridge.mHost.readThread.start();
			}
			mainGUI.updateUSBConnectionStatus("USB Connected", "|||", 1);



		}else if (actionCommand.equals(DISCONNECT_USB1)){

			//Disconnect
			mainGUI.bridge.mHost.usbDisconnect(USB_PCHost.PC_LOCAL_HOST);

			//Close all connections
			//Enable USB Connect
			usbConnectButton1.setText("USB Connect");
			usbConnectButton1.setEnabled(true);
			usbConnectButton1.setBackground(BMGuiProgram.DULL_BUTTON_COLOR);

			//Update button
			usbGetButton1.setText("USB get values");
			usbGetButton1.setEnabled(true);

			mainGUI.updateUSBConnectionStatus("USB Disconnected", "|||", 1);


		}else if (actionCommand.equals(CONNECT_USB2)){
			//Start USB connection
			if (mainGUI.bridge.mHost.initializeConnection(USB_PCHost.PC_LOCAL_HOST2)){
				//Start reading thread
				//bridge.mHost.readThread.start();
				//Only if connection successful
				usbConnectButton2.setText("Waiting for phone response...");

				//Start reading thread
				//bridge.mHost.readThread.start();
			}
			mainGUI.updateUSBConnectionStatus("USB Connected", "|||", 2);



		}else if (actionCommand.equals(DISCONNECT_USB2)){

			//Disconnect
			mainGUI.bridge.mHost.usbDisconnect(USB_PCHost.PC_LOCAL_HOST2);

			//Close all connections
			//Enable USB Connect
			usbConnectButton2.setText("USB Connect");
			usbConnectButton2.setEnabled(true);
			usbConnectButton2.setBackground(BMGuiProgram.DULL_BUTTON_COLOR);

			//Update button
			usbGetButton2.setText("USB get values");
			usbGetButton2.setEnabled(true);

			mainGUI.updateUSBConnectionStatus("USB Disconnected", "|||", 2);


		}else if (actionCommand.equals(SEND_COMMAND)){
			//Debug send command
			//Get info from USB and generate excel
			mainGUI.bridge.mHost.sendUSBmessage("Send command!", USB_PCHost.PC_LOCAL_HOST);
		}else if (actionCommand.equals(USB_GET_VALUES1)){
			//TODO Test matlab socket
			//Clean any previous error
			mainGUI.cleanErrorsScreen();
			mainGUI.bridge.resetExceptionFlags();

			//Reset excel file
			mainGUI.updateUSBConnectionStatus("Getting USB values", "................", 1);
			mainGUI.bridge.mExcel.initialRow = 1;

			//Avoid the READY windows to appear before the writing is done
			//bridge.internetException = true;


			//TODO Select kind of messages to receive:
			//	- All not synchronized
			//  - Last 5 min
			//Send get values command
			mainGUI.bridge.mHost.readThreads[USB_PCHost._PHONE_1_INDEX].matlab_request = 0;

			//GET ALL VALUES and select sensor:
			String message = "{command: "+USB_PCHost._GET_ALL_NO_SYNC+", "+USB_PCHost._SENSOR_ID+": "+USB_PCHost._EMPATICA+"}";
			//bridge.mHost.sendUSBmessage(USB_PCHost._GET_ALL_NO_SYNC);
			mainGUI.bridge.mHost.sendUSBmessage(message, USB_PCHost.PC_LOCAL_HOST);

			//Update info in button
			usbGetButton1.setText("Getting values..");
			usbGetButton1.setEnabled(false);

			//Schedule timer: in case no data is received after 3 minutes
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					//Announce the end of MATLAB writing
					mainGUI.bridge.allDataCollected = false;
					//Update button
					usbGetButton1.setText("USB get values");
					usbGetButton1.setEnabled(true);
				}
			}, 3*60*1000);

		}else if (actionCommand.equals(USB_GET_VALUES2)){
			//TODO Test matlab socket
			//Clean any previous error
			mainGUI.cleanErrorsScreen();
			mainGUI.bridge.resetExceptionFlags();

			//Reset excel file
			mainGUI.updateUSBConnectionStatus("Getting USB values", "................", 2);
			mainGUI.bridge.mExcel.initialRow = 1;

			//Avoid the READY windows to appear before the writing is done
			//bridge.internetException = true;


			//TODO Select kind of messages to receive:
			//	- All not synchronized
			//  - Last 5 min
			//Send get values command
			mainGUI.bridge.mHost.readThreads[USB_PCHost._PHONE_2_INDEX].matlab_request = 0;

			//GET ALL VALUES and select sensor:
			String message = "{command: "+USB_PCHost._GET_ALL_NO_SYNC+", "+USB_PCHost._SENSOR_ID+": "+USB_PCHost._EMPATICA+"}";
			//bridge.mHost.sendUSBmessage(USB_PCHost._GET_ALL_NO_SYNC);
			mainGUI.bridge.mHost.sendUSBmessage(message, USB_PCHost.PC_LOCAL_HOST2);

			//Update info in button
			usbGetButton1.setText("Getting values..");
			usbGetButton1.setEnabled(false);

			//Schedule timer: in case no data is received after 3 minutes
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					//Announce the end of MATLAB writing
					mainGUI.bridge.allDataCollected = false;
					//Update button
					usbGetButton1.setText("USB get values");
					usbGetButton1.setEnabled(true);
				}
			}, 3*60*1000);

		}else if(actionCommand.equals(CHOOSE_ADB)){
			//Update table name 
			System.out.println("Set adb URL");
			mainGUI.bridge.mHost.setADB(mainGUI.androidAdb.getText());

		}else if (actionCommand.equals(CONNECT_MATLAB)){
			//Connect to matlab function:
			if (mainGUI.bridge.matSocket.initSocket()){
				matlabConnectButton.setText("CONNECTED");
				matlabConnectButton.setEnabled(false);
				matlabConnectButton.setBackground(Color.GREEN);
			}
		}else if (actionCommand.equals(DISCONNECT_MATLAB)){
			//1. DISCONNECT MATLAB
			//Send disconnect command:
			mainGUI.bridge.matSocket.matlabDisconnect();


			//Update GUI
			matlabConnectButton.setText("MATLAB Connect");
			matlabConnectButton.setEnabled(true);
			matlabConnectButton.setBackground(BMGuiProgram.DULL_BUTTON_COLOR);
			//2. DISCONNECT USB TOO

			//Disconnect
			//bridge.mHost.usbDisconnect();

			//Close all connections
			//Enable USB Connect
			/*usbConnectButton.setText("USB Connect");
			usbConnectButton.setEnabled(true);
			usbConnectButton.setBackground(DULL_BUTTON_COLOR);*/

			//3. UPDATE OTHER BUTTONS
			//Update button
			usbGetButton1.setText("USB get values");
			usbGetButton1.setEnabled(true);

			//updateUSBConnectionStatus("USB Disconnected", "|||", connectionLabel2);

		}
	}

}
