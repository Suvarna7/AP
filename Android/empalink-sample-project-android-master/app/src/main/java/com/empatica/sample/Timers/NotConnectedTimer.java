package com.empatica.sample.Timers;

import android.content.Intent;

import com.empatica.sample.ConnectionDialog;
import com.empatica.sample.MainActivity;
import com.empatica.sample.USB.USBHost;
import com.empatica.sample.USB.USBMessageSender;

import java.util.TimerTask;

/**
 * Created by Cat on 2/21/2017.
 */
public class NotConnectedTimer extends BasicTimer {

    private static boolean startTimer;
    private static boolean connectionEstablishedFlag;
    private MainActivity mAct;

    public NotConnectedTimer(MainActivity act){
        super();
        startTimer = true;
        connectionEstablishedFlag = false;
        mAct = act;
    }

    public static void connectionACK(){
        connectionEstablishedFlag = true;
    }

    public static void setTime(boolean started){
        startTimer = started;
    }
    @Override
    public void startTimer(){
        //Restart the counter flag
        startTimer = false;
        //Start a a timer
        this.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (mAct.mHost.isConnected()) {

                    if (!connectionEstablishedFlag) {
                        //Inform laptop that no data will be send
                        //Send no data message
                        mAct.mHost.usbMesenger.sendUSBmessage(USBMessageSender._END_COMMAND);
                        mAct.mHost.usbMesenger.sendUSBmessage(USBMessageSender._NO_DATA);

                        mAct.popUpFragment(ConnectionDialog.class);


                    } else {
                        connectionEstablishedFlag = false;
                    }
                }

            }
        }, 60*1000, 5*60*1000); // delay(seconds*1000), period(seconds*1000)
    }


}

