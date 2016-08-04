package edu.virginia.dtc.APCservice.USB;

import java.io.IOException;
import java.net.SocketException;

public class USBReadThread extends Thread {
    private  boolean shut;
    private USBHost mHost;

    public USBReadThread(USBHost host){
    	System.out.println("Create read");
    	shut = false;
    	mHost = host;
    }

	public void run() {
		while(!shut){
			//System.out.println("mm" + shut);

			if (mHost!= null && mHost.socketIn != null && mHost.socketIn.hasNext()) {
				String line = mHost.socketIn.nextLine();
				if (line != null) {
					System.out.println(line);
					//TODO Use the input command
					//Post result in Command field:
					System.out.println("Received command: " + line);
				}
			}



		}
		
	}
	public void shutdown(){
		shut = true;
		System.out.println("Shutdown: "+ shut);

	}

}
