package iit.pc.javainterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class USB_PCHost {
	//Field - path to android adb
		public final static  String PATH_ADB = "C:\\Users\\Cat\\Anroid-sdk\\platform-tools\\adb.exe";
		
		//Socket
		private Socket socket;
		private PrintWriter out;
		private Scanner sc;
		BufferedReader in;
		
		//Ports
		private static int ANDROID_LOCAL_HOST = 38300;
		private static int PC_LOCAL_HOST = 38300;
		
		//Read thread
		public USBReadThread readThread;
		
		//Bridge instance
		BMBridge mBridge;
		
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
				Scanner sc = new Scanner(p.getErrorStream());
				if (sc.hasNext()) {
					while (sc.hasNext()) 
						System.out.println(sc.next());
					System.out. println("Cannot start the Android debug bridge");
				}
			} catch (Exception e) {
				System.out.println(e.toString());
			}
			System.out.println("Adb bridge enabled ");
		}

		/**
		 * Initialize connection to the phone
		 *
		 */
		public boolean initializeConnection(){
			//Create socket connection
			try{
				socket = new Socket("localhost", PC_LOCAL_HOST);
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				sc=new Scanner(socket.getInputStream());
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
			} catch (UnknownHostException e) {
				System.out.println("Socket connection problem (Unknown host)"+e.getStackTrace());
				return false;
			} catch (IOException e) {
				System.out.println("Could not initialize I/O on socket "+e+ " - "+e.getStackTrace());
				return false;
			}
			return true;
		}
		
		public void usbDisconnect(){
			readThread.shutdown();
			try {
				
				out.flush();
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
		            System.out.println("send msg");
		            //socketOut.print(msg);
		            out.println(msg);
		            out.flush();
		        }else
		            System.out.println("msg not sent");

		    }
		
		
		

			

}
