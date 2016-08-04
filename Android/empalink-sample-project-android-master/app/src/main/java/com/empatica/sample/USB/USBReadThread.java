package com.empatica.sample.USB;

import com.empatica.sample.BGService;
import com.empatica.sample.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class USBReadThread extends Thread {
    private  boolean shut;
    private USBHost mHost;
	private String LAPT_COMMAND = "get_values";
	private static String _END_COMMAND = "next_end" ;


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
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
					String currentDateandTime = sdf.format(new Date());
					MainActivity.setCommandValue(line + " : "+ currentDateandTime);
					System.out.println("Received command: " + line);

					//If it is the SEND Command --> we send data
					if (line.equals(LAPT_COMMAND)){
						for (int i =0; i <  BGService.usbReadyData.size(); i ++){
							if (i == BGService.usbReadyData.size() -1)
								//Send the end command
								mHost.sendUSBmessage(_END_COMMAND);

							mHost.sendUSBmessage(BGService.usbReadyData.get(i));
						}
						//Reset values
						BGService.usbReadyData = new ArrayList<String>();

					}
				}
			}



		}
		
	}
	public void shutdown(){
		shut = true;
		System.out.println("Shutdown: to true");

	}

}
