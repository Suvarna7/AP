package iit.pc.javainterface.usb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import iit.pc.javainterface.BMBridge;

public class USB_PCHost {
	//Field - path to android adb
		public static  String PATH_ADB = "C:\\Users\\Otro\\Anroid-sdk\\platform-tools\\adb.exe";
		
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
		public static String _END_COMMAND = "next_end" ;
	    public static String _NO_DATA = "no_data";
		public static String _ACK_SYNCHRONIZED = "usb_sync";
		//2. Connection management
		public static String _CONNECTION_ESTABLISHED = "connection_process_end";
		public static String _CONNECTION_END = "end_connection";
		public static String _WRONG_COMMAND = "wrong_command";



		
		public USB_PCHost(BMBridge bridge){
			mBridge = bridge;
			
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

			

}
