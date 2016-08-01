package iit.pc.javainterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Button;
import java.awt.GradientPaint;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.DimensionUIResource;

/**
 * The BMGuiProgram class provides a graphical interface for the BMBridge class
 * It is contains:
 * 		- Init function to select the excel file to read
 * 		- Init function to select the server address
 * 		- Button to read and send
 * 
 * @author Caterina Lazaro
 * @version 1.0, November 2015
 *
 */

public class BMGuiProgram extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Button send label
	private String label;
	//Bodymedia bridge - reads and sends values
	BMBridge bridge;
	//Result of sending values to IIT server
	private TextField sentResult;
	private TextField getResult;
	private JTextField tableNameField;
	private JButton automaticStartButton;
	private JButton automaticStopButton;
	private Label period;
	private Label excelTextFile;
	private JPanel errorDisplay;
	private TextField errorResult ;
	//To show and warn
	private JButton checkFile;
	private JButton checkInternet;
	private JButton checkServer;
	private JButton warningButton;
	//USB buttons
	private JButton usbConnectButton;
	private JButton usbDisconnectButton;
	private JButton usbGetButton;
	//*************** ACTIONS COMMAND
	private static final String SEND_BUTTON_COMMAND = "SendButton";
	private static final String GET_BUTTON_COMMAND = "GetButton";
	private static final String TABLE_FIELD_COMMAND = "TableField";
	private static final String AUTOMATIC_START_COMMAND = "AutomaticStart";
	private static final String AUTOMATIC_STOP_COMMAND = "AutomaticStop";
	private static final String WARNED = "WarningOK";
	private static final String CHOOSE_FILE = "ChooseFile";
	private static final String CONNECT_USB = "ConnectUSB";
	private static final String DISCONNECT_USB = "DisconnectUSB";
	private static final String SEND_COMMAND = "SendUSB";

	
	//******************** COLORS
	private static final Color DULL_BUTTON_COLOR = new Color(224, 224, 224);
	private static final Color BACKGROUND_COLOR = new Color(255, 255, 240);




	/**
	 * Constructor
	 * @param l - button label
	 * @param bm - BodyMedia bridge object
	 */
	public BMGuiProgram(String l, BMBridge bm, String Title) {
		bridge = bm;
		//Build the GUI:
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//6 Panels vertically organized: 6 rows x 1 column
		GridLayout bmLayout = new GridLayout(7,1);
		setLayout(bmLayout);
		setTitle(Title);  // "super" Frame sets title
		getContentPane().setForeground(Color.WHITE);
		
		setSize(620, 400);        // "super" Frame sets initial window size
		getContentPane().setBackground(BACKGROUND_COLOR);
		//*************************************************
		//First panel - Server title
		JPanel iitServerPanel = new JPanel();
		iitServerPanel.setLayout(new GridLayout (1,1));
		iitServerPanel.setBackground(BACKGROUND_COLOR);

		//Title
		Label serverTitle = new Label ("SERVER MENU");
		serverTitle.setAlignment(JLabel.HORIZONTAL);
		serverTitle.setAlignment(JTextField.CENTER);
		Font f  = new Font("SansSerif", Font.BOLD, 20);
		serverTitle.setFont(f);
		serverTitle.setSize(100, 20);
		iitServerPanel.add(serverTitle);
		
		add(iitServerPanel);
		//*************************************************
		//Second panel - Set file and table name
		
		iitServerPanel = new JPanel();
		iitServerPanel.setLayout(new GridLayout (3,2));
		iitServerPanel.setBackground(BACKGROUND_COLOR);
		
		Label table = new Label ("Write table name: ");
		tableNameField = new JTextField();
		tableNameField.setText(bm.getTABLE_NAME());
		tableNameField.setActionCommand(TABLE_FIELD_COMMAND);
		tableNameField.addActionListener(actionsListener);
		iitServerPanel.add(table);
		iitServerPanel.add(tableNameField);

		//Select SERVER to send
		Label server = new Label ("Server:");
		iitServerPanel.add(server);
		//Add the sent result label
		Label serverURL = new Label(bm.getIIT_SERVER_IP());
		//serverURL.setEditable(false);
		iitServerPanel.add(serverURL);

		//Select excel file
		Label excelFileLabel = new Label ("Excel File location:");
		iitServerPanel.add(excelFileLabel, BorderLayout.AFTER_LAST_LINE);
		
		//Add the excel file to read
		Panel exToRead = new Panel();
		exToRead.setLayout(new GridLayout(1,2));
		excelTextFile = new Label("--");
		excelTextFile.setText(selectFileDialog());
		//Open a dialog

	
		
		//The resulting file will appear on the program gui
		exToRead.add(excelTextFile);
		
		//Set up the button to select another excel file
		JButton selectExcel = new JButton("Open");
		selectExcel.setActionCommand(CHOOSE_FILE);
		selectExcel.addActionListener(actionsListener);
		exToRead.add(selectExcel);
		
		iitServerPanel.add(exToRead);
		
		//Add panel to frame
		add(iitServerPanel, BorderLayout.NORTH);


		//*************************************************
		//Third panel - Send and receive buttons
		JPanel buttonPress = new JPanel();
		buttonPress.setLayout(new GridLayout (2,2));
		buttonPress.setBackground(BACKGROUND_COLOR);

		//Add the send Button with the given label
		label = l;
		JButton Send = new JButton(label);
		Send.setEnabled(true);
		Send.setActionCommand(SEND_BUTTON_COMMAND);
		//TODO Set the right color
		//Check some rgb values in: cloford.com/resources/colours/500col.htm
		// new Color(float r, float g, float b)
		//GradientPaint gp = new GradientPaint ();
		//Send.setP ;
		Send.setBackground(DULL_BUTTON_COLOR);
		Send.addActionListener(actionsListener);
		buttonPress.add(Send);
		//Add the sent result label
		sentResult = new TextField("");
		sentResult.setEditable(false);
		buttonPress.add(sentResult);

		//Add Get Button
		JButton Get = new JButton("Get");
		Get.setEnabled(true);
		Get.setBackground(DULL_BUTTON_COLOR);
		Get.setActionCommand(GET_BUTTON_COMMAND);
		Get.addActionListener(actionsListener);
		buttonPress.add(Get);
		//Add the Get result label
		getResult = new TextField("");
		getResult.setEditable(false);
		buttonPress.add(getResult);

		//Add button panel
		add(buttonPress);
		//add(buttonPress,  BorderLayout.AFTER_LAST_LINE);
		
		//*************************************************
		//Fourth panel - automatic functions
		JPanel automaticControl = new JPanel();
		//automaticControl.setLayout(new GridLayout (1,4));
		automaticControl.setBackground(BACKGROUND_COLOR);

		//Automatic label
		Label automatic = new Label ("Automatically receive/writeExcel data:");
		automaticControl.add(automatic);
		//Add the START automatic button
		automaticStartButton = new JButton("START");
		//TODO Unactive
		automaticStartButton.setEnabled(false);
		automaticStartButton.setActionCommand(AUTOMATIC_START_COMMAND);
		automaticStartButton.addActionListener(actionsListener);
		automaticStartButton.setBackground(DULL_BUTTON_COLOR);
		automaticControl.add(automaticStartButton);

		//Add the STOP automatic button	
		automaticStopButton = new JButton("STOP");
		//TODO Unactive
		automaticStopButton.setEnabled(false);
		automaticStopButton.setActionCommand(AUTOMATIC_STOP_COMMAND);
		automaticStopButton.addActionListener(actionsListener);
		automaticStopButton.setBackground(DULL_BUTTON_COLOR);
		automaticControl.add(automaticStopButton);

		//Get interval time
		period = new Label ("Time(s):      ");
		automaticControl.add(period);


		//Add automatic panel
		add(automaticControl, BorderLayout.AFTER_LAST_LINE);
		
		//************************************************
		//Fifth panel - USB Title

		JPanel usbDisplay = new JPanel();
		usbDisplay.setLayout(new GridLayout (1,1));
		usbDisplay.setBackground(BACKGROUND_COLOR);

		//Title
		Label usbTitle = new Label ("USB MENU");
		usbTitle.setAlignment(JLabel.HORIZONTAL);
		usbTitle.setAlignment(JTextField.CENTER);
		f  = new Font("SansSerif", Font.BOLD, 20);
		usbTitle.setFont(f);
		usbTitle.setSize(100, 20);
		usbDisplay.add(usbTitle);
		
		add(usbDisplay);
		//************************************************
		//Sixth panel - USB Connection
		
		
	    usbDisplay = new JPanel();
		usbDisplay.setLayout(new GridLayout(1,1));
		usbDisplay.setBackground(Color.WHITE);
		
		//Add a connect usb button
		usbConnectButton = new JButton("USB Connect");
		usbConnectButton.setEnabled(true);
		usbConnectButton.setActionCommand(CONNECT_USB);
		usbConnectButton.addActionListener(actionsListener);
		usbConnectButton.setBackground(DULL_BUTTON_COLOR);
		usbDisplay.add(usbConnectButton);
		
		//Add a disconnect usb button
		usbDisconnectButton = new JButton("USB Disonnect");
		usbDisconnectButton.setEnabled(true);
		usbDisconnectButton.setActionCommand(DISCONNECT_USB);
		usbDisconnectButton.addActionListener(actionsListener);
		usbDisconnectButton.setBackground(DULL_BUTTON_COLOR);
		usbDisplay.add(usbDisconnectButton);
		
		//Add a retreive data button
		usbGetButton = new JButton ("USB Command");
		usbGetButton.setEnabled(true);
		usbGetButton.setActionCommand(SEND_COMMAND);
		usbGetButton.addActionListener(actionsListener);
		usbGetButton.setBackground(DULL_BUTTON_COLOR);
		usbDisplay.add(usbGetButton);
		
		add(usbDisplay, BorderLayout.CENTER );

		//*************************************************
		//Seventh panel - errors
		errorDisplay = new JPanel();
		errorDisplay.setLayout(new GridLayout (2,1));
		errorDisplay.setBackground(Color.WHITE);

		//Error label
		Label errorLabel = new Label ("Error: ");
		errorLabel.setForeground(Color.RED);
		//errorLabel.setAlignment(10);
		errorDisplay.add(errorLabel);

		//Actual error display textfield
		errorResult = new TextField();
		//errorResult.setText(":                                :");
		errorResult.setForeground(Color.RED);
		errorResult.setEditable(false);
		errorDisplay.add(errorResult);

		errorDisplay.setBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.RED));
		errorDisplay.setVisible(false);

		//WARNING
		warningButton = new JButton ("WARNING!!");
		warningButton.setVisible(false);
		warningButton.setBackground(Color.YELLOW);
		warningButton.setActionCommand(WARNED);
		warningButton.addActionListener(actionsListener);
		errorDisplay.add(new Label(""));
		errorDisplay.add(warningButton);

		add(errorDisplay, BorderLayout.CENTER );

		//**********************************
		//What happens when closing
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		
		//*******************************
		//Make frame visible
		setVisible(true);

	}

	/**
	 * Listener for all actions on frame
	 * 	- Send button
	 * 	- Table name field
	 */
	private ActionListener actionsListener = new ActionListener(){

		//First, clean errors windows
		public void actionPerformed(ActionEvent e) {
			String actionCommand = e.getActionCommand();
			if(actionCommand.equals(SEND_BUTTON_COMMAND)){
				
				//Clean previous error valeus
				cleanErrorsScreen();
				bridge.resetExceptionFlags();

				// Read excel

				//Read values from excel file
				//List<HashMap<String, String>> readValues = bridge.readExcelValues();
				//Update bridge json object
				bridge.readExcelValuesAndUpdateJSON();

				//Update the result of reading values in the GUI
				//setSentLabel(bridge.getSendResult());
				// Send values
				bridge.sendToIITServer();
				//Update the result of sending values in the GUI
				//setSentLabel(bridge.getSendResult());


			}else if(actionCommand.equals(GET_BUTTON_COMMAND)){
				//Clean any previous error
				cleanErrorsScreen();
				bridge.resetExceptionFlags();

				//Read values from excel file
				//List<HashMap<String, String>> readValues = bridge.readExcelValues();
				//Update bridge json object
				bridge.readFromIITServer();
				//Update the result of reading values in the GUI
				setSentLabel(bridge.getSendResult());
				// Send values
				//bridge.sendToIITServer();
				//Update the result of sending values in the GUI
				//setSentLabel(bridge.getSendResult());


			}else if(actionCommand.equals(TABLE_FIELD_COMMAND)&& e.getSource()==tableNameField){
				//Update table name 
				System.out.println("Set table name");
				bridge.setTABLE_NAME(tableNameField.getText());
			}else if(actionCommand.equals(AUTOMATIC_START_COMMAND)&& bridge.automaticWriter !=null){
				
				//Clean previous error values
				cleanErrorsScreen();
				bridge.resetExceptionFlags();
				bridge.automaticWriter.startAutomaticWrites();
				//Change colors of buttons
				period.setText("Time(s):"+bridge.automaticWriter.getRepeatInterval());
				automaticStartButton.setBackground(Color.GREEN);
				automaticStopButton.setBackground(DULL_BUTTON_COLOR);


			}else if(actionCommand.equals(AUTOMATIC_STOP_COMMAND) && bridge.automaticWriter !=null){
				
				
				//Clean previous error valeus
				cleanErrorsScreen();
				bridge.resetExceptionFlags();

				bridge.automaticWriter.stopAutomaticWrites();
				//Change colors of buttons
				automaticStopButton.setBackground(Color.RED);
				automaticStartButton.setBackground(DULL_BUTTON_COLOR);



			}else if (actionCommand.equals(WARNED)){
				//The user was warned
				cleanErrorsScreen();
				warningButton.setVisible(false);
				bridge.resetExceptionFlags();
				errorDisplay.setVisible(false);


				

			}else if(actionCommand.equals(CHOOSE_FILE)){
				//Select a new file and update the name
				excelTextFile.setText(selectFileDialog());
			}else if (actionCommand.equals(CONNECT_USB)){
				//Start USB connection
				if (bridge.mHost.initializeConnection()){
					usbConnectButton.setText("USB Connected");
					usbConnectButton.setEnabled(false);
					usbConnectButton.setBackground(Color.GREEN);
					//Start reading thread
					bridge.mHost.readThread.start();
				}
				
				
			}else if (actionCommand.equals(DISCONNECT_USB)){
				//Stop reading thread
				//bridge.mHost.readThread.shutdown();
					
				//bridge.mHost.readThread.destroy();
				bridge.mHost.usbDisconnect();

				//Close all connections
				//Enable USB Connect
				usbConnectButton.setText("USB Connect");
				usbConnectButton.setEnabled(true);
				usbConnectButton.setBackground(DULL_BUTTON_COLOR);
				
				
			}else if (actionCommand.equals(SEND_COMMAND)){
				//Get info from USB and generate excel
				bridge.mHost.sendUSBmessage("Send command!");
			}
		}
		

	};
	
	private String selectFileDialog(){
		try{
			File f = new File(new File("file.xls").getCanonicalPath());
			JFileChooser excelChoose = new JFileChooser();
			excelChoose.setSelectedFiles(new File[]{f});
			excelChoose.setFileFilter(new FileNameExtensionFilter("Excel file", new String[]{"xls"}));
			int returnVal = excelChoose.showDialog(new JFrame(""), "Excel file");
			File bmFile = excelChoose.getSelectedFile();
			System.out.println("Chosen file: " + bmFile.getName());
			bridge.setExcelFile(bmFile);
			return bmFile.getName();
			//add(excelChoose);
		}catch (IOException e){
			System.out.println ("Error with new file: "+e);
		}
		return "";
	}


	/**
	 * Set send label
	 */
	public void setSentLabel(String sent){
		sentResult.setText(sent);
	}

	/**
	 * Set get label
	 */
	public void setGetLabel(String sent){
		getResult.setText(sent);
	}

	/**
	 * Display an error on the GUI
	 * @param error
	 */
	public void displayError(String error){
		errorDisplay.setVisible(true);
		String errors = "";
		errors = errorResult.getText();
		errors += "*  "+ error;
		errorResult.setText(errors);


	}

	/**
	 * Clean the error window
	 */
	public void cleanErrorsScreen(){
		errorResult.setText("");

	}
	/**
	 * Popup message to check 
	 * @param message
	 */
	public void checkWindow(String message){
		warningButton.setVisible(true);
		JOptionPane.showMessageDialog(this, message, "Check warning", JOptionPane.WARNING_MESSAGE);
	}

}
