package iit.pc.javainterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import iit.pc.javainterface.gui.ButtonsListener;
import iit.pc.javainterface.gui.PhonesList;
import iit.pc.javainterface.usb.USB_PCHost;

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
	public BMBridge bridge;
	
	//Result of sending values to IIT server
	private TextField sentResult;
	private TextField getResult;
	public JTextField tableNameField;
	public Label period;
	public Label excelTextFile;
	public JPanel errorDisplay;
	private TextField errorResult ;
	public static Label connectionLabel1;
	public Label connectionActive1;
	Label connectionLabel2;
	public Label connectionActive2;

	//USB Text field - android adb
	public JTextField androidAdb;
	
	//To show and warn
	public JButton warningButton;
	
	//USB buttons
	public JButton usbConnectButton1;
	public JButton usbDisconnectButton1;
	public JButton usbGetButton1;
	public JButton usbConnectButton2;
	public JButton usbDisconnectButton2;	
	public JButton usbGetButton2;


	//******************** COLORS
	public static final Color DULL_BUTTON_COLOR = new Color(224, 224, 224);
	private static final Color BACKGROUND_COLOR = new Color(255, 255, 240);


	/**
	 * Constructor
	 * @param l - button label
	 * @param bm - BodyMedia bridge object
	 */
	public BMGuiProgram(String l, BMBridge bm, String Title) {
		bridge = bm;
		
		ButtonsListener actionsListener = new ButtonsListener(this);
		usbConnectButton1 = actionsListener.usbConnectButton1;
		usbDisconnectButton1 = actionsListener.usbDisconnectButton1;
		usbGetButton1 = actionsListener.usbGetButton1;
		usbConnectButton2= actionsListener.usbConnectButton2;
		usbDisconnectButton2 = actionsListener.usbDisconnectButton2;
		usbGetButton2 = actionsListener.usbGetButton2;
		

		
		//Build the GUI:
		//TODO (ADD MORE PANELS)9 Panels vertically organized: 9 rows x 1 column
		//GridLayout bmLayout = new GridLayout(10,1);
		GridLayout bmLayout = new GridLayout(6,1);

		setLayout(bmLayout);
		setTitle(Title);  // "super" Frame sets title
		getContentPane().setForeground(Color.WHITE);

		setSize(620, 400);        // "super" Frame sets initial window size
		getContentPane().setBackground(BACKGROUND_COLOR);
		//*************************************************
		//First panel - Server title (NOT ADDED)
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
		
		//TODO ADD add(iitServerPanel);
		//*************************************************
		//Second panel - Set file and table name (NOT ADDED)

		iitServerPanel = new JPanel();
		iitServerPanel.setLayout(new GridLayout (3,2));
		iitServerPanel.setBackground(BACKGROUND_COLOR);

		Label table = new Label ("Write table name: ");
		tableNameField = new JTextField();
		tableNameField.setText(bm.getTABLE_NAME());
		tableNameField.setActionCommand(ButtonsListener.TABLE_FIELD_COMMAND);
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
		//TODO Open dialog excelTextFile.setText(selectFileDialog());
		//Open a dialog

		//The resulting file will appear on the program gui
		exToRead.add(excelTextFile);

		//Set up the button to select another excel file
		JButton selectExcel = new JButton("Open");
		selectExcel.setActionCommand(ButtonsListener.CHOOSE_FILE);
		selectExcel.addActionListener(actionsListener);
		exToRead.add(selectExcel);

		iitServerPanel.add(exToRead);

		//Add panel to frame
		//TODO ADD add(iitServerPanel, BorderLayout.NORTH);


		//*************************************************
		//Third panel - Send and receive buttons (NOT ADDED)
		JPanel buttonPress = new JPanel();
		buttonPress.setLayout(new GridLayout (2,2));
		buttonPress.setBackground(BACKGROUND_COLOR);

		//Add the send Button with the given label
		label = l;
		JButton Send = new JButton(label);
		Send.setEnabled(true);
		Send.setActionCommand(ButtonsListener.SEND_BUTTON_COMMAND);
		Send.setEnabled(false);
		//Set the right color
		Send.setBackground(DULL_BUTTON_COLOR);
		Send.addActionListener(actionsListener);
		buttonPress.add(Send);
		//Add the sent result label
		sentResult = new TextField("");
		sentResult.setEditable(false);
		buttonPress.add(sentResult);

		//Add Get Button
		JButton Get = new JButton("Get Server Values");
		Get.setEnabled(true);
		Get.setBackground(DULL_BUTTON_COLOR);
		Get.setActionCommand(ButtonsListener.GET_BUTTON_COMMAND);
		Get.addActionListener(actionsListener);
		buttonPress.add(Get);
		//Add the Get result label
		getResult = new TextField("");
		getResult.setEditable(false);
		buttonPress.add(getResult);

		//Add button panel
		//TODO ADD add(buttonPress);
		//add(buttonPress,  BorderLayout.AFTER_LAST_LINE);

		//*************************************************
		//Fourth panel - automatic functions (NOT ADDED)
		JPanel automaticControl = new JPanel();
		//automaticControl.setLayout(new GridLayout (1,4));
		automaticControl.setBackground(BACKGROUND_COLOR);

		//Automatic label
		Label automatic = new Label ("Automatically receive/writeExcel data:");
		automaticControl.add(automatic);

		//Automatic START & STOP buttons
		automaticControl.add(actionsListener.automaticStartButton);
		automaticControl.add(actionsListener.automaticStopButton);

		//Get interval time
		period = new Label ("Time(s):      ");
		automaticControl.add(period);


		//Add automatic panel
		//TODO ADD add(automaticControl, BorderLayout.AFTER_LAST_LINE);

		//************************************************
		//Fifth panel - USB Title

		JPanel usbDisplay = new JPanel();
		usbDisplay.setLayout(new GridLayout (2,1));
		usbDisplay.setBackground(BACKGROUND_COLOR);

		//Title
		Label usbTitle = new Label ("USB MENU");
		usbTitle.setAlignment(JLabel.HORIZONTAL);
		usbTitle.setAlignment(JTextField.CENTER);
		f  = new Font("SansSerif", Font.BOLD, 20);
		usbTitle.setFont(f);
		usbTitle.setSize(100, 20);
		usbDisplay.add(usbTitle);
		
		//Adb path
		JPanel usbAdbPath = new JPanel();
		usbAdbPath.setLayout(new GridLayout(1,2));
		usbAdbPath.setBackground(BACKGROUND_COLOR);

		Label adbLabel = new Label("Set adb path: (PRESS ENTER)");
		androidAdb = new JTextField(USB_PCHost.PATH_ADB);
		androidAdb.setSize(10, 200);
		androidAdb.setActionCommand(ButtonsListener.CHOOSE_ADB);
		androidAdb.addActionListener(actionsListener);
		usbAdbPath.add(adbLabel);
		usbAdbPath.add(androidAdb);
		usbDisplay.add(usbAdbPath);

		add(usbDisplay);
		//************************************************
		//Sixth panel - USB Connection to PHONE 1 {Empaica}
		usbDisplay = new JPanel();
		usbDisplay.setLayout(new GridLayout(3,1));
		usbDisplay.setBackground(BACKGROUND_COLOR);
		//Phone name
		JPanel usbButtons = new JPanel();
		usbButtons.setLayout(new GridLayout(1,2));
		usbButtons.setBackground(Color.WHITE);
		JLabel phone = new JLabel("Phone 1: Empatica");
		usbButtons.add(phone);
		PhonesList phonesList = new PhonesList(bridge.mHost, USB_PCHost._PHONE_1_INDEX);
		JComboBox phones = phonesList.getPhonesList();
		usbButtons.add(phones);
		usbDisplay.add(usbButtons);


		//Add a connect usb button
		usbButtons = new JPanel();
		usbButtons.setLayout(new GridLayout(1,3));
		usbButtons.setBackground(Color.WHITE);
		//Add usb CONNECT & DISCONNECT & GET Buttons
		usbButtons.add(actionsListener.usbConnectButton1);
		usbButtons.add(actionsListener.usbDisconnectButton1);
		usbButtons.add(actionsListener.usbGetButton1);

		usbDisplay.add(usbButtons);
		//Add connection status panel
		JPanel usbConnection = new JPanel();
		usbConnection.setLayout(new GridLayout(1,2));
		usbConnection.setBackground(BACKGROUND_COLOR);
		connectionLabel1 = new Label("Waiting to start USB Connection");
		usbConnection.add(connectionLabel1);
		connectionActive1  = new Label("|||");
		usbConnection.add(connectionActive1);

		usbDisplay.add(usbConnection);
		//Add "last bolus" notification panel 
		add(usbDisplay, BorderLayout.CENTER );
		
		//************************************************
		//Sixth panel - USB Connection to PHONE 2 {DiAS}
		usbDisplay = new JPanel();
		usbDisplay.setLayout(new GridLayout(3,1));
		usbDisplay.setBackground(BACKGROUND_COLOR);
		
		//Phone name
		usbButtons = new JPanel();
		usbButtons.setLayout(new GridLayout(1,2));
		usbButtons.setBackground(Color.WHITE);
		phone = new JLabel("Phone 2: DiAS");
		usbButtons.add(phone);
		phonesList = new PhonesList(bridge.mHost, USB_PCHost._PHONE_2_INDEX);
		JComboBox phones2 = phonesList.getPhonesList();
		usbButtons.add(phones2);
		usbDisplay.add(usbButtons);

		//Add a connect usb button
		usbButtons = new JPanel();
		usbButtons.setLayout(new GridLayout(1,3));
		usbButtons.setBackground(Color.WHITE);
		
		usbButtons.add(actionsListener.usbConnectButton2);
		usbButtons.add(actionsListener.usbDisconnectButton2);
		usbButtons.add(actionsListener.usbGetButton2);

		usbDisplay.add(usbButtons);
		//Add connection status panel
		usbConnection = new JPanel();
		usbConnection.setLayout(new GridLayout(1,2));
		usbConnection.setBackground(BACKGROUND_COLOR);
		connectionLabel2 = new Label("Waiting to start USB Connection");
		usbConnection.add(connectionLabel2);
		connectionActive2  = new Label("|||");
		usbConnection.add(connectionActive2);

		usbDisplay.add(usbConnection);
		//Add "last bolus" notification panel 
		add(usbDisplay, BorderLayout.CENTER );


		//************************************************
		//Eighth panel - Matlab Title

		JPanel matlabDisplay = new JPanel();
		matlabDisplay.setLayout(new GridLayout (1,1));
		matlabDisplay.setBackground(BACKGROUND_COLOR);

		//Title
		Label matlabTitle = new Label ("MATLAB MENU");
		matlabTitle.setAlignment(JLabel.HORIZONTAL);
		matlabTitle.setAlignment(JTextField.CENTER);
		f  = new Font("SansSerif", Font.BOLD, 20);
		matlabTitle.setFont(f);
		matlabTitle.setSize(100, 20);
		matlabDisplay.add(matlabTitle);

		add(matlabDisplay);
		//************************************************
		//Ninth panel - Matlab Connection
		matlabDisplay = new JPanel();
		matlabDisplay.setLayout(new GridLayout (1,1));
		matlabDisplay.setBackground(BACKGROUND_COLOR);

		
		matlabDisplay.add(actionsListener.matlabConnectButton);
		matlabDisplay.add(actionsListener.matlabDisonnectButton);

		add(matlabDisplay);

		//*************************************************
		//Tenth panel - errors
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
		warningButton.setActionCommand(ButtonsListener.WARNED);
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
	 * Select excel file where data is to be store
	 * (DEPRECATED)
	 * @return name of file
	 */
	public String selectFileDialog(){
		try{
			File f = new File(new File("file.xls").getCanonicalPath());
			JFileChooser excelChoose = new JFileChooser();
			excelChoose.setSelectedFiles(new File[]{f});
			excelChoose.setFileFilter(new FileNameExtensionFilter("Excel file", new String[]{"xls"}));
			excelChoose.showDialog(new JFrame(""), "Excel file");
			File bmFile = excelChoose.getSelectedFile();
			System.out.println("Chosen file: " + bmFile.getName());
			bridge.mExcel.setExcelFile(bmFile);
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
		warningButton.setVisible(true);



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
		//warningButton.setVisible(true);
		//TODO JOptionPane.showMessageDialog(this, message, "Check warning", JOptionPane.WARNING_MESSAGE);
	}
	
	public void updateUSBConnectionStatus(String state, String waiting, int phone){
		if (phone ==1){
			connectionLabel1.setText(state);
			connectionActive1.setText(waiting);
		}else if (phone == 2){
			connectionLabel2.setText(state);
			connectionActive2.setText(waiting);
		}
	
		
	}

}
