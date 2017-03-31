package com.empatica.sample.Timers;

import android.content.Context;
import android.net.ConnectivityManager;
import com.empatica.empalink.EmpaDeviceManager;
import com.empatica.sample.MainActivity;


import java.util.TimerTask;

/**
 * Created by Cat on 2/21/2017.
 */
public class VerifyKeyTimer extends BasicTimer {
    EmpaDeviceManager deviceManager;
    MainActivity mainAct;

    public static final int KEY_VERIFICATION_PERIOD =  15*60*1000;

    public VerifyKeyTimer (EmpaDeviceManager dev, MainActivity mainAct){
        super();
        deviceManager = dev;
    }

    /**
     * Verify KEY every 15 min
     */
    @Override
    public void startTimer(){
        //Create and start periodic timer
        //Start a a timer
        this.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //Verify key if there is internet available

                if (mainAct.checkInternetConnectivity() && deviceManager != null) {
                    //Verify key
                    deviceManager.authenticateWithAPIKey(mainAct.EMPATICA_API_KEY);
                    mainAct.updateLabel(mainAct.internet_conn, "INTERNET");
                    System.out.println("Verified key");
                } else
                    //No itnernet connection - WE CAN NOT USE THE APP
                    mainAct.updateLabel(mainAct.internet_conn, "NO INTERNET");


            }
        }, KEY_VERIFICATION_PERIOD, KEY_VERIFICATION_PERIOD); // delay, period (period_time)

        // Check whether there is internet connection

    }
}
