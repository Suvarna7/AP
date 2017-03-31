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

    private final int _INIT_DELAY  = 5*60*1000; //miliseconds
    private final int _PERIOD = 60*1000;//miliseconds

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
                    //Send a connection ACK
                    mAct.mHost.usbMesenger.sendUSBmessage(USBMessageSender._ACK_TEST_USB);

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
        }, _INIT_DELAY, _PERIOD); // delay(seconds*1000), period(seconds*1000)
    }


}

