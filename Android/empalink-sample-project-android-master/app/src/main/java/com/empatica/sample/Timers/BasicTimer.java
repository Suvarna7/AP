package com.empatica.sample.Timers;

import android.os.AsyncTask;

import com.empatica.sample.BGService;
import com.empatica.sample.Database.IITDatabaseManager;

import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Cat on 2/21/2017.
 */
public class BasicTimer extends Timer {
    public static boolean ready;

    public BasicTimer(){
        ready = false;
    }

    /**
     * Timer to automatically send data to server
     */

    public void startTimer() {

    }



    public static void enable(){
        ready = true;
    }

    public static void disable(){
        ready = false;
    }

    public static void set (boolean r){
        ready = r;
    }
}
