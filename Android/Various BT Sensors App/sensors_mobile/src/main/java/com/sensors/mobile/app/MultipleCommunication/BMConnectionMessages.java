package com.sensors.mobile.app.MultipleCommunication;

import com.sensors.mobile.app.Database.Database;
import com.sensors.mobile.app.Database.ThreadSafeArrayList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Caterina on 4/17/2015.
 */
public class BMConnectionMessages {
    //Variables
    public ThreadSafeArrayList<String> sensorValues;
    public  String msg1a;
    private  boolean ready1a;
    public  String msg1b;
    private  boolean ready1b;
    public  String msg2;
    private  boolean ready2;
    public  String msg3;
    private  boolean ready3;

    /*
    * Constructor
     */

    public BMConnectionMessages(){

        sensorValues = new ThreadSafeArrayList<String>();
    }

    /*
* buildMessage1()
*/
    public  void buildMessage1a(double accFor, double accLong, double accTrans){
        System.out.println("build Message1a");

        messageToStore( "", 0, accFor, accLong, accTrans, 0, 0, 0, 0, 0, 0, 1);

    }
    public  void buildMessage1b(double skinTemp, double gsr, double coverTemp, double bat){
        System.out.println("build Message1b");

        messageToStore( "", 0, 0, 0, 0, skinTemp, gsr, coverTemp, 0, bat, 0, 2);

    }

    /*
    * buildMessage2()
     */
    public  void buildMessage2( String actType, int cal, int mem){
        System.out.println("build Message2");
        messageToStore(actType, 0, 0, 0, 0, 0, 0, 0, cal, 0, mem, 3);
    }

    /*
    * buildMessage3
     */
    public  void buildMessage3 (int[] ecg){
        //TODO Figure out ecg right values
        // get rid the oldest sample in history:


        System.out.println("build Message3");
        messageToStore("", ecg[1], 0, 0, 0, 0, 0, 0, 0, 0, 0, 4);
    }

    /*
    * messageToStore
     */

    private  void messageToStore( String aType, int ecg, double vAcc, double lAcc, double sAcc,
                                        double sTemp, double gsr, double cTemp, int cal, double bat, double mem, int message){
        //Message structure:($activity, $actity_type, $ecg, $vertical_accel, $lateral_accel,"+
        //" $sagittal_accel, $skin_temp, $gsr, $cover_temp, $calories, $battery, $memory, '$time_stamp')

        //Actual message: ( $vertical_accel, $lateral_accel,"+" $sagittal_accel, $skin_temp, $gsr,
        // $cover_temp, $battery, $actity_type, $calories, $memory, $ecg, '$time_stamp')
        String currentTimeStamp ="";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
        currentTimeStamp = dateFormat.format(new Date());
        String currentStamp = currentTimeStamp.substring(0,currentTimeStamp.length()-4);

        switch (message){
            case 1:
                msg1a ="("+vAcc+", "+ lAcc+", "+sAcc +", ";
                ready1a = true;
                break;
            case 2:

                msg1b =sTemp+", "+ gsr+", "+cTemp +", "+bat +", '";
                ready1b = true;
                break;
            case 3:
                msg2 =aType+"', "+cal+", "+mem+", ";
                ready2 = true;
                break;
            case 4:
                ready3 = true;
                msg3 = ecg+ ", '"+ currentStamp +"', 'no')";
                break;
            default:
                System.out.println ("Message not supported to store");
        }

        if (ready1a && ready1b &&ready2 &&ready3){
            String query = msg1a + msg1b+ msg2 + msg3;
            sensorValues.set(query);
            //Reset values
            ready1a=false;
            ready1b=false;
            ready2=false;
            ready3=false;

            msg1a = "";
            msg1b = "";
            msg2 ="";
            msg3 = "";


           // Database.updateDatabase(MainActivityMultiple.ctx, sensorValues, Database.bodymediaTableName);

        }
    }
}
