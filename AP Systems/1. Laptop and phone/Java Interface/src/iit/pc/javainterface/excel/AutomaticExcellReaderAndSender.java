package iit.pc.javainterface.excel;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import iit.pc.javainterface.BMBridge;

public class AutomaticExcellReaderAndSender {
	private Timer timer;
	//Time in seconds
	private int repeatInterval;
	//State of the timer
	private boolean started;

	private BMBridge bridge;


	/**
	 * Automatic reader constructor
	 * @param interval in seconds
	 * @param b BMBridge to connect with server
	 */

	public AutomaticExcellReaderAndSender(int interval, BMBridge b){
		repeatInterval = interval;
		timer = new Timer();
		bridge = b;
		started = false;
	}

	/**
	 * Start reading and sending automatically
	 */

	public void startAutomaticReads(){
		if (!started ){
			//Start Timer
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					// We read and send excel
					// Read excel
					
						bridge.readExcelValuesAndUpdateJSON();
					
					
						// Send values
						bridge.sendToIITServer();
				}
			}, repeatInterval*1000, repeatInterval*1000);
			//Started
			started = true;
		}

	}
	/**
	 * Stop the automatic execution
	 */
	public void stopAutomaticReads(){
		if (started){
			//Stop timer
			timer.cancel();
			started = false;
			timer = new Timer();
		}

	}
	/**
	 * Set the interval period
	 * @param period in seconds
	 */
	public void setrepeatInterval(int period){
		repeatInterval = period;
	}

	/**
	 * Getter for the repeatInterval
	 * @return repeat interval in seconds
	 */
	public int getRepeatInterval(){
		return repeatInterval;
	}


}
