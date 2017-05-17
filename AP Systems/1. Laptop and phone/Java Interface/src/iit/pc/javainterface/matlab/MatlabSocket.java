package iit.pc.javainterface.matlab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import iit.pc.javainterface.BMBridge;

public class MatlabSocket {
	//Socket
	//private ServerSocket serverSocket;
	private Socket clientSocket;
	private PrintWriter out;
	//Scanner sc;
	BufferedReader in;

	//Readthread
	MatlabReadThread readThread;

	//Ports
	private static int MATLAB_LOCAL_HOST = 38700;

	//Commands:
	public static final String _READ_LAST_SAMPLES = "read_all";
	public static final String _SENT_ALL = "no_more_values";
	public static final String _ACK = "usb_sync";
	public static final String _DISCONNECT_MATLAB = "disconnect_socket";
	public static final String _INSULIN = "insulin_command";
	public static final String _HYPO = "hypo_command";
	public static final String _USB_REQ= "usb_state";
	public static final String _USB_CONNECTED= "usb_connected";
	public static final String _USB_DISCONNECTED= "usb_disconnected";
	public static final String _BL_REQ = "verify_device";
	public static final int _EMPATICA_ONE_MIN = 64*60*2;
	public static final int _ALL_SAMPLES = -1;
	

	//Instance of the Java main bridge
	BMBridge mBridge;

	public MatlabSocket(BMBridge bridge){
		mBridge = bridge;

	}

	public boolean initSocket(){
		try{
			/*serverSocket =  new ServerSocket(MATLAB_LOCAL_HOST);
			clientSocket = serverSocket.accept();*/
			clientSocket = new Socket("localhost", MATLAB_LOCAL_HOST);
			System.out.println("Client accepted");
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		}catch (IOException e) {
			System.out.println("Fail creatint socket for matlab: "+e);
			return false;
		}

		//Add a shutdown hook to close the socket if system crashes or exists unexpectedly
		Thread closeSocketOnShutdown = new Thread() {
			public void run() {
				try {
					System.out.println("Socket closing...");
					clientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		Runtime.getRuntime().addShutdownHook(closeSocketOnShutdown);

		//Read Thread
		//Add the read thread
		readThread = new MatlabReadThread(this);
		//Start thread
		readThread.start();
		readThread.shutup();
		return true;


	}

	/**
	 * Send message to our MATLAB functiion
	 * @param msg
	 */
	public boolean sendMessageToMatlab(String msg){
		if (out != null) {
			System.out.println("send matlab msg: "+msg);
			//socketOut.print(msg);
			out.println(msg);
			out.flush();
			return true;
		}else{
			System.out.println("msg to matlab not sent");
			return false;
		}
	}
	
	/*
	 * Disconnect MATLAB socket from Java interface
	 */
	public void matlabDisconnect(){
		//Send warning to MATLAB
		sendMessageToMatlab(_DISCONNECT_MATLAB);

		//Shutdown 
		readThread.shutdown();
		try {
			out.flush();
			out = null;
			//Close the socket will also close all in/out
			clientSocket.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			 System.out.println("Connection status set to false");
		}
		
		
	}

}
