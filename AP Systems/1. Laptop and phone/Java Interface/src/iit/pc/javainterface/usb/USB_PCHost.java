package iit.pc.javainterface.usb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import iit.pc.javainterface.BMBridge;
import iit.pc.javainterface.encryption.EncryptionManager;

public class USB_PCHost {
	//Field - path to android adb
		//public static  String PATH_ADB = "C:\\Users\\Otro\\Anroid-sdk\\platform-tools\\adb.exe";
		public final static String _CURRENT_DIR = System.getProperty("user.dir");
		public static  String PATH_ADB = _CURRENT_DIR + "\\adb.exe";
		//Socket
		private Socket socket;
		private PrintWriter out;
		//Scanner sc;
		BufferedReader in;
		
		//Ports
		private static int ANDROID_LOCAL_HOST = 38600;
		private static int PC_LOCAL_HOST = 38500;
		
		//Read thread
		public USBReadThread readThread;
		
		//Bridge instance
		BMBridge mBridge;
		
		//MAX Number of JSON objects per message sent
		public static final int _MAX_NUM_JSON = 50;
		
		
		//COMMANDS
		//1. Get data
		public static final String _GET_DATA = "get_values";
		public static String _GET_ALL = "get_all";
	    public static String _GET_ALL_NO_SYNC = "get_all_no_sync";
	    public static String _START_SENDING = "first_value";
		public static String _END_COMMAND = "next_end" ;
	    public static String _NO_DATA = "no_data";
		public static String _ACK_SYNCHRONIZED = "usb_sync";
		public static String _PHONE_READY = "dias_ready";

		//2. Connection management
		public static String _CONNECTION_ESTABLISHED = "connection_process_end";
		public static String _CONNECTION_END = "end_connection";
		public static String _WRONG_COMMAND = "wrong_command";
		public static String _TEST_USB= "verify_usb";
		private static final int _WAIT_USB_CONN =  30*1000;
	    public static String _ACK_TEST_USB= "ACK_usb";


		//KEEP SENSORS INFORMATION:
		public static final String _SENSOR_ID = "sensor_table";
		public static final String _EMPATICA = "empatica";
		public static final String _DEXCOM = "dexcom";
		public static final String _ZEPHYR = "zephyr";
		public static final String _NUM_SAMPLES = "samples_to_read";
		
	    public static String _TEST_DEVICE= "verify_device";
	    public static String _VERIFY_DEVICE_CONNECTED= "device_connected";
	    public static String _VERIFY_DEVICE_DISCONNECTED= "device_disconnected";
		public static final String _BL_REQ = "bl_state";

		private boolean connected;
		 
		 //4. ENCRYPTION PARAMETERS
		 public static String _WHO_ARE_YOU = "who_is";
		 //public static String _I_AM = "this_is";
		 //private EncryptionManager em;


		
		public USB_PCHost(BMBridge bridge){
			mBridge = bridge;
			//Set up PATH_ADB as current
			PATH_ADB = _CURRENT_DIR + "\\adb.exe";
			
			connected = false;
			
			//TODO em = new EncryptionManager();
			
		}

		/**
		 * Runs the android debug bridge command of forwarding the ports
		 *
		 */
		public void execAdb() {
			
			// run the adb bridge
			try {
				Process p=Runtime.getRuntime().exec(PATH_ADB + " forward tcp:"+PC_LOCAL_HOST+" tcp:"+ANDROID_LOCAL_HOST);
				Scanner sc1 = new Scanner(p.getErrorStream());
				if (sc1.hasNext()) {
					String errorMsg = "";
					while (sc1.hasNext()) 
						errorMsg += sc1.next();
					System.out. println("Cannot start the Android debug bridge: "+ errorMsg);
					mBridge.gui.displayError("Cannot start Android bridge! "+ errorMsg);

				}
				sc1.close();

			} catch (Exception e) {
				System.out.println(e.toString());
				mBridge.gui.displayError("Adb.exe not found - check path ");
				

			}
			System.out.println("Adb bridge enabled ");
		}

		/**
		 * Initialize connection to the phone
		 *
		 */
		public boolean initializeConnection(){
			System.out.println("Init connection");
			boolean successful;
			//Create socket connection
			try{
				socket = new Socket("localhost", PC_LOCAL_HOST);
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				//sc=new Scanner(socket.getInputStream());
				System.out.println("Client socket started: "+socket.getLocalPort() + " "
									+ 	socket.getPort());

				// add a shutdown hook to close the socket if system crashes or exists unexpectedly
				Thread closeSocketOnShutdown = new Thread() {
					public void run() {
						try {
							System.out.println("Socket closing...");
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				};
				Runtime.getRuntime().addShutdownHook(closeSocketOnShutdown);
				
				//Add the read thread
				readThread = new USBReadThread(this, mBridge);
				//Start thread
				readThread.start();
				readThread.shutup();
				//Send connection ACK message
				//sendUSBmessage(_PHONE_GET);
				sendUSBmessage(_CONNECTION_ESTABLISHED);
				successful =  true;

			} catch (UnknownHostException e) {
				mBridge.gui.displayError("Socket connection problem (Unknown host)");
				System.out.println("Socket connection problem (Unknown host)"+e.getStackTrace());
				successful = false;
			} catch (IOException e) {
				mBridge.gui.displayError("Could not initialize I/O on socket : " + e);
				System.out.println("Could not initialize I/O on socket "+e+ " - "+e.getStackTrace());
				successful = false;
			}
			updateConnectionStatus(true);

			return successful;
		}
		
		/**
		 * Disconnect USB Client in laptop
		 */
		public void usbDisconnect(){
			//Send warning to Phone
			sendUSBmessage(_CONNECTION_END);

			//Shutdown 
			readThread.shutdown();
			try {
				out.flush();
				out = null;
				//Close the socket will also close all in/out
				socket.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				 System.out.println("Connection status set to false");

				updateConnectionStatus(false);
			}
			
			
		}
		/*
		 * Send command
		 */
		
		 public void sendUSBmessage(String msg){
		        if (out != null) {
		            System.out.println("send msg: "+msg);
		            //socketOut.print(msg);
		            out.println(msg  );
		            out.flush();
		        }else
		            System.out.println("msg not sent");
		    }
		
		/**
		 * Set new path to adb.exe
		 * @param url
		 */
		 public void setADB(String url){
			 PATH_ADB = url;
			 //Close any previous reader
			 if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 //Reset adb
			 execAdb(); 
		 }
		 
		 /**
		  * Update connected flag
		  * @param con - connection true/false
		  */
		 public void updateConnectionStatus(boolean con){
			 connected = con;
		 }
		 
		 /**
		  * Return whether USB is connected or not
		  * @return connection status
		  */
		 public boolean isConnected(){
			 //Check socket:
			 System.out.println("Check USB socket: "+out.checkError());
			 return (connected && socket != null && socket.isConnected() && out !=null && !out.checkError());
			 
		 }
		 
		 /**
		  * Verify the USB connection by sending a message and waiting for a respond 
		  * from the phone
		  */
		 
		 public void verifyConnection(){
			 
			  //FIRST APPROACH - send message and wait for reply
			 System.out.println("Connection status set to false");
			 updateConnectionStatus(false);
			 sendUSBmessage(_TEST_USB);
			 //Wait for a few seconds
			 
			 try {
				Thread.sleep(_WAIT_USB_CONN);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
		 }
		 
		 public void sendHello(){
			 //TODO Build who are you message
			 Map<String, String> message = new HashMap<String, String>();
			 message.put("command", _WHO_ARE_YOU);
			// message.put("public_key", em.getPublicKeyString(false));
			 String hello = "{command: "+_WHO_ARE_YOU+" publickey}";
		 }
		 
		 /**
		  * Reset output socket, to avoid buffer overflow
		  */
		   public void usbResetCommunicationLink(){
		     out.flush();
		   }

			

}
