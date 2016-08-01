package iit.pc.javainterface;

import java.io.IOException;
import java.net.SocketException;

public class USBReadThread extends Thread {
    private  boolean shut;
    private USB_PCHost mHost;
    private BMBridge mBridge;

    public USBReadThread(USB_PCHost host, BMBridge bridge){
    	System.out.println("Create read");
    	shut = false;
    	mHost = host;
    	mBridge =  bridge;
    }

	public void run() {
		while(!shut){
			//System.out.println("mm" + shut);
			try {
				
				String line = mHost.in.readLine();
				if (line != null){
					System.out.println(line);
					mBridge.handleJSONResponse(line);
				}

				else
					mHost.initializeConnection();
			} catch (SocketException se){
				System.out.println("Socket closed!");
				
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(e);

			} 
		}
		
	}
	public void shutdown(){
		shut = true;
		System.out.println("Shutdown: "+ shut);

	}

}
