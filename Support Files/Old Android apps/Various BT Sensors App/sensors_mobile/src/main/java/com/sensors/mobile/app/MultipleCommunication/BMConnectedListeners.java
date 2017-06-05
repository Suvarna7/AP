package com.sensors.mobile.app.MultipleCommunication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import com.bodymedia.btle.packet.*;
import com.bodymedia.mobile.sdk.listener.ArmbandListener;

import java.util.Calendar;

/**
 * Created by Caterina on 4/17/2015.
 */
public class BMConnectedListeners {

    private  BMConnectionMessages bmMessages;
    private  Handler aHandler;

    private final int TYPE = 0x107;
    private final int TEMP = 0x108;
    private final int CALORIES = 0x109;

    public BMConnectedListeners (Handler handler){
        aHandler = handler;
        bmMessages =  new BMConnectionMessages();
    }
      /*
    * High rate listener
     */

    public  ArmbandListener<HighRate> highRateArmbandListener = new ArmbandListener<HighRate>() {


        @Override
        public void onSuccess(HighRate data) {

            //Get current time
            Calendar c = Calendar.getInstance();
            Time now = new Time();
            now.setToNow();

            if (data.getType() == PacketType.GEC_SENSORS_ACCEL_ECG){
                SensorsCalibratedAccelerometerECG sensorEvent = (SensorsCalibratedAccelerometerECG) data;
                double accForw = sensorEvent.getAccelerometerForward();
                double accLong = sensorEvent.getAccelerometerLongitudinal();
                double accTrans = sensorEvent.getAccelerometerTransverse();
                int[] ecg = sensorEvent.getECG();

                int sec = sensorEvent.getSeconds();

                System.out.println("GEC_SENSORS_ACCEL_ECG buildMessage3");
                bmMessages.buildMessage3(ecg);

                // System.out.println("SENSOR_ACCEL_ECG: " + data);
                //System.out.println("ECG: "+ ecg);
                //System.out.println("Time: "+ sec);

            }
            else  if (data.getType() == PacketType.GEC_SENSORS_ACCEL){
                SensorsCalibratedAccelerometer sensorEvent = (SensorsCalibratedAccelerometer) data;
                double[] accForw = sensorEvent.getAccelerometerForward();
                double[] accLong = sensorEvent.getAccelerometerLongitudinal();
                double[] accTrans = sensorEvent.getAccelerometerTransverse();
                int sec = sensorEvent.getSeconds();


                System.out.println("SENSOR_ACCEL: " + accForw + accLong + accTrans);
                System.out.println("Time: " + sec);

            }else if (data.getType() == PacketType.GEC_SENSORS_ECG_CAL ){
                SensorsCalibratedECG tempPacket = (SensorsCalibratedECG) data;
                int [] ecg = tempPacket.getECG();

                System.out.println("GEC_SENSORS_ECG_CAL buildMessage3");
                bmMessages.buildMessage3(ecg);
                //System.out.println("GEC_SENSORS_ECG_CAL: "+ ecg);



            } else if (data.getType() == PacketType.GEC_SENSORS_ECG_RAW) {
                SensorsRawECG raw = (SensorsRawECG) data;
                int[] ecg =raw.getECG();

                System.out.println("GEC_SENSORS_ACCEL_ECG_RAW buildMessage3");
                bmMessages.buildMessage3(ecg);

                //System.out.println("GEC_SENSORS_ECG_RAW: " +ecg[0]);

            }else if (data.getType() == PacketType.GEC_SENSORS_CAL){
                SensorsCalibrated calib = (SensorsCalibrated) data;
                double accForward =calib.getAccelerometerForward();
                double accLong =calib.getAccelerometerLongitudinal();
                double accTrans =calib.getAccelerometerTransverse();

                double battery = calib.getBattery();
                double skinTemp = calib.getSkinTemperature();
                double coverTemp = calib.getCoverTemperature();
                int gsr = calib.getGSR();

                System.out.println("Building message1a, 1b: GEC_SENSORS_CAL");

                bmMessages.buildMessage1a(accForward, accLong, accTrans);
                bmMessages.buildMessage1b(skinTemp, gsr, coverTemp, battery);
                //System.out.println("SENSORS CAL Acceleration : " +accForward + accLong + accTrans);
                // System.out.println("SENSORS CAL Temperatures : " +skinTemp + coverTemp);
                //System.out.println("SENSORS CAL Battery : " +battery);

                //Update Screen value
                //TODO SCREEN
                Message text1 = aHandler.obtainMessage(TEMP);
                Bundle b1 = new Bundle();
                b1.putString("Temperature", String.valueOf(skinTemp));
                text1.setData(b1);
                aHandler.sendMessage(text1);


            }
            else if (data.getType() == PacketType.GEC_SENSORS_GSRTEMP){
                SensorsGSRTemperature tempPacket = (SensorsGSRTemperature) data;
                //System.out.println("SENSORS_GRSTEMP: "+ data);

            } else if (data.getType() == PacketType.GEC_SENSORS_ECG_RAW) {
                SensorsRaw raw = (SensorsRaw) data;
                int accForward =raw.getAccelerometerForward();
                int accLong = raw.getAccelerometerLongitudinal();
                int accTrans = raw.getAccelerometerTransverse();
                long bat = raw.getBattery();

                System.out.println("Building message1a, 1b: GEC_SENSORS_ECG_RAW " + bat);
                System.out.println("Values: " +accForward + "-" + accLong + "-" + accTrans);
                bmMessages.buildMessage1a(accForward, accLong, accTrans);


            }

        }

        @Override
        public void onError(Exception e) {
            e.printStackTrace();
        }
    };

    /* Listener for receiving Minute Updates */
    public  ArmbandListener<UpdateMinute> minuteListener = new ArmbandListener<UpdateMinute>() {
        @Override
        public void onSuccess(UpdateMinute data) {

            int mem = data.getMemoryStatus();
            int cal = data.getCaloriesPerLastMinute();
            ActivityType type = data.getActivityTypeForLastMinute();
            String actType = type.name();
            double bat = data.getBatteryStatus();

            bmMessages.buildMessage2(actType, cal, mem);

            //TODO update values
            Message text1 = aHandler.obtainMessage(CALORIES);
            Bundle b1 = new Bundle();
            b1.putString("Calories", String.valueOf(String.valueOf(cal)));
            text1.setData(b1);
            aHandler.sendMessage(text1);

            Message text2 = aHandler.obtainMessage(TYPE);
            Bundle b2 = new Bundle();
            b2.putString("Type", String.valueOf(actType));
            text2.setData(b2);
            aHandler.sendMessage(text2);





        }

        @Override
        public void onError(Exception e) {
            e.printStackTrace();
        }
    };
}
