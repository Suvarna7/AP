package com.sensors.mobile.app.BM;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.os.IBinder;
import android.text.format.Time;

import android.widget.Toast;

import com.bodymedia.btle.packet.*;
import com.bodymedia.mobile.sdk.PacketListener;
import com.bodymedia.mobile.sdk.listener.ArmbandListener;
import com.bodymedia.mobile.sdk.validator.ValidationException;
import com.sensors.mobile.app.BM.ui.MinuteRateFragment;
import com.sensors.mobile.app.Database.Database;

import rx.Observer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;




public class BGService extends Service{
	
	//private boolean tableInit;
	//private int index;
    private Context serviceContxt;

    //Connection flag
    public static  Timer notConnectedTimer;
    private boolean connectionFlag;
    public static boolean startTimer;

    //Fields to manage the data storage:
    //Local array:
    public static ArrayList<List<String>> sensorValues;
    //BODYMEDIA TABLE PARAMS:
    //Database manager
    private static Database myDB;
    // Table name
    public static final String bodymediaTableName = "bodymedia";
    // Columns:
    public static final String[] bmColumns = new String[]{"last_update", "activity_type","heart_rate",
            "longitudinal_accel",  "lateral_accel", "transverse_accel", "long_accel_peak", "lat_accel_peak",
            "tran_accel_peak", "long_accel_avg", "lat_accel_avg", "tran_accel_avg", "long_accel_mad",
            "lat_accel_mad", "tran_accel_mad", "gsr", "gsr_avg", "skin_temp",  "skin_temp_avg", "cover_temp",
            "heat_flux_avg", "steps", "sleep", "calories", "vigorous",
            "METs", "memory", "battery"};
    //Partial messages:
    //Array for the values
    public static String[] readValues;
    //Location of each variable:
    private static final int TIME_STAMP = 0;
    private static final int ACT_TYPE = 1;
    private static final int HEART_RATE = 2;
    private static final int LONG_ACC = 3;
    private static final int LAT_ACC = 4;
    private static final int TRAN_ACC = 5;
    private static final int LONG_PEAK = 6;
    private static final int LAT_PEAK = 7;
    private static final int TRAN_PEAK = 8;
    private static final int LONG_AVG = 9;
    private static final int LAT_AVG = 10;
    private static final int TRAN_AVG = 11;
    private static final int LONG_MAD = 12;
    private static final int LAT_MAD = 13;
    private static final int TRAN_MAD = 14;
    private static final int GSR = 15;
    private static final int GSR_AVG = 16;
    private static final int SKIN_TEMP = 17;
    private static final int SKIN_TEMP_AVG = 18;
    private static final int COVER_TEMP = 19;
    private static final int HEAT_FLUX = 20;
    private static final int STEPS = 21;
    private static final int SLEEP = 22;
    private static final int CALORIES = 23;
    private static final int VIGOROUS = 24;
    private static final int MET = 25;
    private static final int MEMORY = 26;
    private static final int BATTERY = 27;

    private static final int _TOTAL_VARIABLES = 28;

    private static boolean ready1;
    private static boolean ready2;
    private static boolean ready3;
    private static boolean ready4;
    private static boolean ready8;

    //Time field
    private static int lastMinute;

    //GSR Calculations
    private static double gsrAvg;
    private static int gsrCounter;
    public static double[] gsrValues;


	public BGService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    	//tableInit = false;
    	//index = 0;
        Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG).show();
        serviceContxt = this;


        //Connection check
        notConnectedTimer = new Timer();
        connectionFlag = false;
        startTimer = true;

        //Storing arrays
        initStoringVariables();

        //Start database:
        myDB = new Database(this);
        myDB.createTable(bodymediaTableName, bmColumns[0], new ArrayList<String>(Arrays.asList(bmColumns)));



    }

    public static void initStoringVariables(){
        //Init array
        sensorValues = new ArrayList<List<String>>();
        gsrValues = new double[500];
        gsrAvg = 0;
        gsrCounter = 1;
        ready1= false;
        //Intermediate values:
        readValues = new String[_TOTAL_VARIABLES];
        for (int i = 0; i < readValues.length; i ++)
            readValues[i]="'0'";

        ready2 = false;
        ready3=false;
        ready4=false;
        ready8 = false;


    }

    @Override
    public int  onStartCommand(Intent intent, int flags, int startId) {
        // For time consuming an long tasks you can launch a new thread here...
        // Do your Bluetooth Work Here
        Toast.makeText(this, " Service Started", Toast.LENGTH_LONG).show();

     	//*** START LISTENERS
        MinuteRateFragment.minutePipe.subscribe(packetListener);
        MinuteRateFragment.minuteService.addStreamingArmbandListener(highRateArmbandListener);

        try {

            //Notes
            //SenseWearApplication.get().getArmband().playMusics(notes);
            DeviceUpdates updates = new DeviceUpdates();
            updates.setUpdateHighEnabled(true);
            updates.setUpdateMinuteEnabled(true);
            updates.setUpdateCumulativeEnabled(true);

            SenseWearApplication.get().getArmband().configureUpdate(updates, minuteListener, highListener, cumulativeListener).subscribe(
                    new Observer<DeviceUpdates>() {
                        @Override
                        public void onCompleted() {
                            System.out.println("DeviceUpdates packets has successfully sent");
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            System.out.println("Something went wrong trying to send DeviceUpdates packet");
                        }

                        @Override
                        public void onNext(DeviceUpdates deviceUpdates) {
                            System.out.println("Received DeviceUpdates packet: " + deviceUpdates);
                        }
                    }
            );
        } catch (ValidationException e) {
            e.printStackTrace();
        }


        //Start the connection lost timer

        return 0;
    }

        @Override
        public void onDestroy() {
            Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

            //Stop these listeners
            MinuteRateFragment.minutePipe.unsubscribe(packetListener);
            MinuteRateFragment.stopStreamingFunctions();

            //Stop timer
            notConnectedTimer.cancel();

        }

    /**********************************
     * LISTENERS
     */
     /*
        * Packet Listener
         */
    public PacketListener packetListener = new PacketListener(){
        @Override
        public void onReceive(Packet packet) {

            if (packet.getType() == PacketType.GEC_AGG_1OF3){

                AggregateOne agg1 = (AggregateOne) packet;

                double avgTskin = agg1.getAvgTskin();
                double avgTgsr = agg1.getAvgGsr();
                //double avgTcov = agg1.getAvgTcov();

                double avgAccTr = agg1.getAvgAccTr();
                double peakAccTr = agg1.getPeaksAccTr();
                double avgAccFr = agg1.getAvgAccFw();
                double peakAccFw = agg1.getPeaksAccFw();
                double avgAccLo = agg1.getAvgAccLo();
                double peakAccLo = agg1.getPeaksAccLo();



                buildMessageAggregate1(peakAccLo, peakAccFw, peakAccTr, avgAccLo, avgAccFr, avgAccTr,
                        avgTskin, avgTgsr);


            } else if (packet.getType() == PacketType.GEC_AGG_2OF3){
                AggregateTwo agg2 = (AggregateTwo) packet;

                double madAccTr = agg2.getMadAccTr();
                double madAccFw = agg2.getMadAccFw();
                double madAccLo = agg2.getMadAccLo();
                buildMessageAggregate2(madAccTr, madAccLo, madAccFw);

            }else if (packet.getType() == PacketType.GEC_AGG_3OF3){
                //Do nothing
                AggregateThree agg3 = (AggregateThree) packet;
                agg3.getNinetyRangeFw();
                /*if(!aggregated){
                   // aggregated = true;
                   // UIUtils.showToast(MinuteRateFragment.this, "Good to start recording !!");
                }*/

            }else if (packet.getType() == PacketType.GEC_DEVICE_RECORD){
                DeviceRecord dev = (DeviceRecord) packet;
                System.out.println("GEC_DEVICE_RECORD Aggregates " + dev.isAggregatesAllEnabled());
            }/*else if (packet.getType() == PacketType.GEC_ALG_MINUTE ) {

                 AlgorithmMinute algorithmMinute = (AlgorithmMinute) packet;
                //AlgorithmMinuteData algMinuteData = (AlgorithmMinuteData) packet;
                //double calories = algMinuteData.getkCalories();
                //int  hr =algMinuteData.getHeartRate();
            }*/else if(packet.getType() == PacketType.GEC_UPLOAD_REQUEST){
                UploadRequest ur = (UploadRequest) packet;
                Partition agg = ur.getPartition();
                int upState = ur.getNextRecordToSend();
                //initRecord = ur.getNumRecordsRequested();
                System.out.println(agg.getValue());


                UploadRequest uReq = new UploadRequest();
                uReq.setNextRecordToSend( upState );
                uReq.setHighBit(true);
                //uReq.setStartRecordNumberRequested(initRecord);


            }else if(packet.getType() == PacketType.GEC_UPLOAD_CONFIRM){
                UploadConfirm uC = (UploadConfirm) packet;
                int time = uC.getEndTime();
                System.out.println(time);

            }else if (packet.getType() == PacketType.GEC_SENSORS_GSRTEMP){
                SensorsGSRTemperature gsrTemp = (SensorsGSRTemperature)packet;
                double [] gsr = gsrTemp.getGSR();
                double [] skin = gsrTemp.getSkinTemperature();
                double [] cover = gsrTemp.getCoverTemperature();
                //Sample 1: cover&skin mili degree / gsr nano siemes
                //Sample 2: cover&skin mili degree /gsr nano siemens
                buildMessageTemp(skin[0], gsr, cover[0]);
                //MainActivityBM.buildMessageAggregate1(0, 1, 0, 1, 0, 1,skin[1], gsr[1]);

            }


        }
    };

    /* Listener for receiving High Updates */
    ArmbandListener<UpdateHigh> highListener = new ArmbandListener<UpdateHigh>() {
        @Override
        public void onSuccess(UpdateHigh data) {

            int stepsToday = data.getStepsToday();
            //int steps = data.getStepsCumulative();
            int heart_rate = data.getHeartRate();


            buildMessageUpdateHigh(stepsToday, heart_rate);



        }

        @Override
        public void onError(Exception e) {
            e.printStackTrace();
        }
    };

    /* Listener for receiving Cumulative Updates*/
    ArmbandListener<UpdateCumulative> cumulativeListener = new ArmbandListener<UpdateCumulative>() {
        @Override
        public void onSuccess(UpdateCumulative data) {


            //TODO Get accumulative MET, CALORIES (Energy expenditure), SLEEP, VIGOROUS, ACTIVITY (Physical)
            // MainActivityBM.buildMessageCumulative( data.getVigorousActivityMinutesCumulative(),  data.getSleepMinutesCumulative(),  data.getMetMinutesCumulative(), data.getCaloriesCumulative(), 0);


        }

        @Override
        public void onError(Exception e) {
            e.printStackTrace();
        }
    };

    /*  Listener for reaciving HighRate      */
    private  ArmbandListener<HighRate> highRateArmbandListener = new ArmbandListener<HighRate>() {



        @Override
        public void onSuccess(HighRate data) {

            //Timer to check the connection
            if(startTimer) {
                //Restart the counter flag
                startTimer = false;
                //Start a a timer
                notConnectedTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (!connectionFlag) {
                        //if(true){
                            //Try to display the dialog
                            Intent dialogIntent = new Intent(serviceContxt, ConnectionDialog.class);
                            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(dialogIntent);
                        } else {
                            connectionFlag = false;
                        }

                    }
                }, 10, 60000);
            }

            connectionFlag = true;

            //Get current time
            //Calendar c = Calendar.getInstance();
            Time now = new Time();
            now.setToNow();

            if (data.getType() == PacketType.GEC_SENSORS_ACCEL_ECG){
                SensorsCalibratedAccelerometerECG sensorEvent = (SensorsCalibratedAccelerometerECG) data;
                double accForw = sensorEvent.getAccelerometerForward();
                double accLong = sensorEvent.getAccelerometerLongitudinal();
                double accTrans = sensorEvent.getAccelerometerTransverse();
                //int[] ecg = sensorEvent.getECG();
                //int sec = sensorEvent.getSeconds();
                buildMessagePeak2(accForw, accLong, accTrans);



            }
            else  if (data.getType() == PacketType.GEC_SENSORS_ACCEL){
                SensorsCalibratedAccelerometer sensorEvent = (SensorsCalibratedAccelerometer) data;
                double[] accForw = sensorEvent.getAccelerometerForward();
                double[] accLong = sensorEvent.getAccelerometerLongitudinal();
                double[] accTrans = sensorEvent.getAccelerometerTransverse();
                int sec = sensorEvent.getSeconds();


                //What if: peak, avg and mad ??

                //System.out.println("SENSOR_ACCEL: " + accForw + accLong + accTrans);
                System.out.println("Time: " + sec);
                buildMessagePeak2(accForw[0], accLong[1], accTrans[2]);
                //MainActivityBM.buildMessageAggregate1(accForw[0], accLong[0], accTrans[0], accForw[1], accLong[1], accTrans[1],
                //avgTskin, avgTgsr);
                // MainActivityBM.buildMessageAggregate2(madAccTr, madAccLo, madAccFw);
                // writeLog("GEC_SENSORS_ACCEL");



            }else{
                if (data.getType() == PacketType.GEC_SENSORS_CAL) {
                    SensorsCalibrated calib = (SensorsCalibrated) data;
                    double accForward = calib.getAccelerometerForward();
                    double accLong = calib.getAccelerometerLongitudinal();
                    double accTrans = calib.getAccelerometerTransverse();

                    double battery = calib.getBattery();
                    //double skinTemp = calib.getSkinTemperature();
                    //double coverTemp = calib.getCoverTemperature();
                    //double gsr = calib.getGSR();
                    /*double[] gsrArray = new double[2];
                    gsrArray[0] = gsr;
                    gsrArray[1] = 0;*/


                   buildMessagePeak2(accForward, accLong, accTrans);
                   buildMessageBattery(battery);


                } else if (data.getType() == PacketType.GEC_SENSORS_RAW) {
                    SensorsRaw raw = (SensorsRaw) data;
                    int accForward = raw.getAccelerometerForward();
                    int accLong = raw.getAccelerometerLongitudinal();
                    int accTrans = raw.getAccelerometerTransverse();
                    long bat = raw.getBattery();

                    System.out.println("Building message1a, 1b: GEC_SENSORS_ECG_RAW " + bat);
                    System.out.println("Values: " + accForward + "-" + accLong + "-" + accTrans);
                    buildMessagePeak1(accForward, accLong, accTrans, bat);

                } else if (data.getType() == PacketType.GEC_SENSORS_GSRTEMP) {
                    SensorsGSRTemperature gsrTemp = (SensorsGSRTemperature) data;
                    double[] gsr = gsrTemp.getGSR();
                    double[] skin = gsrTemp.getSkinTemperature();
                    double[] cover = gsrTemp.getCoverTemperature();

                    //Sample 1: cover&skin mili degree / gsr nano siemes
                    //Sample 2: cover&skin mili degree /gsr nano siemens
                    buildMessageTemp(skin[0], gsr, cover[0]);
                    //MainActivityBM.buildMessageAggregate1(0, 1, 0, 1, 0, 1,skin[1], gsr[1]);

                }/*else if (data.getType() == PacketType.GEC_SENSORS_ECG_CAL ){
                //DO NOTHING!!!
                //SensorsCalibratedECG calEcg = (SensorsCalibratedECG) data;


            }*//* else if (data.getType() == PacketType.GEC_SENSORS_ECG_RAW) {
                //DO NOTHING!!!
                //SensorsRawECG rawEcg = (SensorsRawECG) data;


            }*/
            }
            /*else if (data.getType() == PacketType.GEC_SENSORS_ECG_RAW) {
                //DO NOTHING
                //SensorsRawECG ECGraw = (SensorsRawECG) data;
                //ECGraw.getECG();

            }*/

        }


        @Override
        public void onError(Exception e) {
            e.printStackTrace();
        }
    };

    /* Listener for receiving Minute Updates */
    ArmbandListener<UpdateMinute> minuteListener;

    {
        minuteListener = new ArmbandListener<UpdateMinute>() {
            @Override
            public void onSuccess(UpdateMinute data) {


                int mem = data.getMemoryStatus();
                //int cal = data.getCaloriesPerLastMinute();
                int cal = data.getCaloriesToday();
                ActivityType type = data.getActivityTypeForLastMinute();
                String actType = type.name();
                double bat = data.getBatteryStatus();
                double sleep = data.getSleepMinutesToday();
                double vigorous = data.getVigorousActivityMinutesToday();
                double activity = data.getActivityMinutesToday();
                double met = (double) data.getMetMinutesToday();

                //Get the time
                /// DateTimeInstance dateTime = new DateTimeInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss", Locale.getDefault());
                String currentTimeStamp = dateFormat.format(new Date());
                String currentStamp = currentTimeStamp.substring(0, currentTimeStamp.length() - 4);

                buildMessageMinute(actType, vigorous, met, sleep, cal, mem, currentStamp);


                UploadRequest uReq = new UploadRequest();
                //uReq.setPartition(Partition.Aggregate);
                uReq.setHighBit(true);





            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        };
    }

    /*********************************************************************************************
     * STORAGE METHODS
     * ******************************************************************************************
     */

    /*
    * buildMessage1()
    * Called with GEC_SENSORS_ECG_RAW
     */
    public static void buildMessagePeak1(double accFor, double accLong, double accTrans, double bat){
        /*messageToStore( String aType, double longitudinal_accel,double lateral_accel, double transverse_accel, double long_accel_peak,
        double lat_accel_peak,double tran_accel_peak, double long_accel_avg, double lat_accel_avg,
        double tran_accel_avg, double long_accel_mad, double lat_accel_mad, double tran_accel_mad,
        double skin_temp, double gsr, double cover_temp, double skin_temp_avg, double gsr_avg,
        double heat_flux_avg, double steps, double sleep, double calories, double vigorous,
        double METs, int memory, int battery, int message)*/

        messageToStore("", 0, accLong, accFor, accTrans, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, bat, "", 1);

    }

    /*
    * buildMessage2()
    * Called with GEC_SENSORS_CAL
     */
    public static void buildMessagePeak2(double accFor, double accLong, double accTrans){
        // System.out.println("build Message1a");
        // messageToStore( "", 0, accFor, accLong, accTrans, 0, 0, 0, 0, 0, 0, 1);
        messageToStore("", 0, accLong, accFor, accTrans, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,"", 1);
    }
    /*
    * buildMessage3()
    * Called with GEC_SENSORS_ECG_CAL
     */
    public static void buildMessagePeak3(double accFor, double accLong, double accTrans){
        //System.out.println("build Message1a");
        // messageToStore( "", 0, accFor, accLong, accTrans, 0, 0, 0, 0, 0, 0, 1);
        messageToStore("", 0, accLong, accFor, accTrans, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "", 1);
    }

    /*
    * Build 2nd kind of message
     */
    public static void buildMessageTemp(double skinTemp, double [] gsr, double coverTemp){
        /*messageToStore( String aType, double longitudinal_accel,double lateral_accel, double transverse_accel, double long_accel_peak,
        double lat_accel_peak,double tran_accel_peak, double long_accel_avg, double lat_accel_avg,
        double tran_accel_avg, double long_accel_mad, double lat_accel_mad, double tran_accel_mad,
        double skin_temp, double gsr, double cover_temp, double skin_temp_avg, double gsr_avg,
        double heat_flux_avg, double steps, double sleep, double calories, double vigorous,
        double METs, int memory, int battery, int message)*/
        double gsr1 = gsr[0];
        double gsr2 = gsr[1];

        //GSR Average: per minute

        Date date = new Date();   // given date
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date
        int currentMinute =  calendar.get(Calendar.MINUTE); // gets current minute



        if (currentMinute == lastMinute){
            gsrCounter += 2;
            gsrAvg += gsr1+gsr2;

        }else{
            gsrAvg = gsrAvg/gsrCounter;
            //GSR Value in Siemens
            gsrAvg = gsrAvg/1000;
            messageToStore("", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0,skinTemp, gsrAvg, coverTemp, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,"", 2);
            lastMinute = currentMinute;
            gsrAvg = gsr1+gsr2;
            gsrCounter = 2;

        }






    }

    /*
    * build 3rd kind of message
    * Get actType, activity, vigorous, MET, SLEEP CALORIES (Energy expenditure)

     */
    public static void buildMessageMinute( String actType, double vigorous, double met, double sleep, int cal, int mem, String time){
       /*messageToStore( String aType, double longitudinal_accel,double lateral_accel, double transverse_accel, double long_accel_peak,
        double lat_accel_peak,double tran_accel_peak, double long_accel_avg, double lat_accel_avg,
        double tran_accel_avg, double long_accel_mad, double lat_accel_mad, double tran_accel_mad,
        double skin_temp, double gsr, double cover_temp, double skin_temp_avg, double gsr_avg,
        double heat_flux_avg, double steps, double sleep, double calories, double vigorous,
        double METs, int memory, int battery, int message)*/


        messageToStore(actType, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, sleep, cal, vigorous, met, mem, 0, time, 3);
    }

    /*
    * build 4th kind of message
     */
    public static void buildMessageUpdateHigh (int steps, int heart_rate){
        /*messageToStore( String aType, double longitudinal_accel,double lateral_accel, double transverse_accel, double long_accel_peak,
        double lat_accel_peak,double tran_accel_peak, double long_accel_avg, double lat_accel_avg,
        double tran_accel_avg, double long_accel_mad, double lat_accel_mad, double tran_accel_mad,
        double skin_temp, double gsr, double cover_temp, double skin_temp_avg, double gsr_avg,
        double heat_flux_avg, double steps, double sleep, double calories, double vigorous,
        double METs, int memory, int battery, int message)*/

        messageToStore("", heart_rate, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, steps, 0, 0, 0, 0, 0, 0, "",4);

    }
    /*
    * build 5th kind of message
     */

    public static void buildMessageAggregate1(double peakAccLo, double peakAccFw, double peakAccTr, double avgAccLo, double avgAccFr, double avgAccTr,
                                              double avgTskin, double avgTgsr){
        /*messageToStore( String aType, double longitudinal_accel,double lateral_accel, double transverse_accel, double long_accel_peak,
        double lat_accel_peak,double tran_accel_peak, double long_accel_avg, double lat_accel_avg,
        double tran_accel_avg, double long_accel_mad, double lat_accel_mad, double tran_accel_mad,
        double skin_temp, double gsr, double cover_temp, double skin_temp_avg, double gsr_avg,
        double heat_flux_avg, double steps, double sleep, double calories, double vigorous,
        double METs, int memory, int battery, int message)*/

        if (avgTgsr == 0) {
            messageToStore("", 0, 0, 0, peakAccLo, peakAccFw, peakAccTr, avgAccLo, avgAccFr, avgAccTr, 0, 0, 0, 0, 0, 0, avgTskin, avgTgsr, 0, 0, 0, 0, 0, 0, 0, 0, 0, "", 5);
        }else{
            messageToStore ("", 0, 0, 0, peakAccLo, peakAccFw, peakAccTr, avgAccLo, avgAccFr, avgAccTr, 0, 0, 0, 0, 0, 0, avgTskin, avgTgsr, 0, 0, 0, 0, 0, 0, 0, 0, 0,"", 7);
        }

    }
    /*
    * build 6th kind of message
     */
    public static void buildMessageAggregate2(double madAccTr, double madAccLo, double madAccFw){
        /*messageToStore( String aType, double longitudinal_accel,double lateral_accel, double transverse_accel, double long_accel_peak,
        double lat_accel_peak,double tran_accel_peak, double long_accel_avg, double lat_accel_avg,
        double tran_accel_avg, double long_accel_mad, double lat_accel_mad, double tran_accel_mad,
        double skin_temp, double gsr, double cover_temp, double skin_temp_avg, double gsr_avg,
        double heat_flux_avg, double steps, double sleep, double calories, double vigorous,
        double METs, int memory, int battery, int message)*/

        messageToStore("", 0, 0, 0, 0, 0, 0, 0, 0, 0, madAccLo,  madAccFw, madAccTr, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,"", 6);


    }
    public static void buildMessageCumulative(int stepsToday, int stepsCum, int heart_rate){
        //Prepare in case we want it!
    }

    public static void buildMessageBattery (double bat){
        messageToStore("", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, bat,"", 8);


    }

    /*
    * messageToStore
     */

    private static void messageToStore( String aType, int heart_rate, double longitudinal_accel,double lateral_accel, double transverse_accel, double long_accel_peak,
                                        double lat_accel_peak,double tran_accel_peak, double long_accel_avg, double lat_accel_avg,
                                        double tran_accel_avg, double long_accel_mad, double lat_accel_mad, double tran_accel_mad,
                                        double skin_temp, double gsr, double cover_temp, double skin_temp_avg, double gsr_avg,
                                        double heat_flux_avg, double steps, double sleep, double calories, double vigorous,
                                        double METs, int memory, double battery, String last_updt, int message){


             /*('activity_type',"longitudinal_accel", "lateral_accel", "transverse_accel", "long_accel_peak", "lat_accel_peak",
        "tran_accel_peak", "long_accel_avg, "lat_accel_avg", "tran_accel_avg", "long_accel_mad", "lat_accel_mad", "tran_accel_mad"
       "skin_temp", "gsr", "cover_temp", "skin_temp_avg", "gsr_avg", "heat_flux_avg", "steps", "sleep", "calories", "vigorous",
        "METs", "memory", "battery", "last_update", 'n')*/


        switch (message){
            case 1:
                //("", 0, accLong, accFor, accTrans, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1)
                readValues[LONG_ACC] = "'"+longitudinal_accel+"'";
                readValues[LAT_ACC] = "'"+lateral_accel+"'";
                readValues[TRAN_ACC]= "'"+transverse_accel+"'";
                ready1 = true;
                if (readValues[BATTERY]  == "")
                    readValues[BATTERY] = "'"+battery+"'";
                break;
            case 2:
                //("", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0,skinTemp, gsr, coverTemp, 0, 0, 0, 0, 0, 0, 0, 0, 0, bat, 2)
                readValues[SKIN_TEMP]= "'"+skin_temp+"'";
                readValues[SKIN_TEMP_AVG] = "'"+0+"'";
                readValues[GSR]="'"+gsr+"'";
                readValues[COVER_TEMP] = "'"+cover_temp+"'";
                ready2 = true;
                break;
            case 3:
                //(actType, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, sleep, cal, vigorous, met, mem, 0, 3)
                readValues[ACT_TYPE] ="'"+aType+"'";
                readValues[SLEEP]="'"+sleep+"'";
                readValues[CALORIES]="'"+calories+"'";
                readValues[VIGOROUS]="'"+vigorous+"'";
                readValues[MET]="'"+METs+"'";
                readValues[MEMORY]="'"+memory+"'";
                readValues[TIME_STAMP]="'"+last_updt+"'";
                ready3 = true;
                break;
            case 4:
                //("", heart_rate, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, steps, 0, 0, 0, 0, 0, 0, 4)
                ready4 = true;
                readValues[HEART_RATE]= "'"+heart_rate+"'";
                readValues[STEPS]="'"+steps+"'";
                break;
            case 5:
                //("", 0, 0, 0, peakAccLo, peakAccFw, peakAccTr, avgAccLo, avgAccFr, avgAccTr, 0,  0, 0, 0, 0, 0, avgTskin, avgTgsr, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5)
                readValues[LONG_PEAK]="'"+long_accel_peak+"'";
                readValues[LAT_PEAK]="'"+lat_accel_peak+"'";
                readValues[TRAN_PEAK]="'"+tran_accel_peak+"'";
                readValues[LONG_AVG]="'"+long_accel_avg+"'";
                readValues[LAT_AVG]="'"+lat_accel_avg+"'";
                readValues[TRAN_AVG]="'"+tran_accel_avg+"'";
                break;
            case 6:
                //("", 0, 0, 0, 0, 0, 0, 0, 0, 0, madAccLo,  madAccFw, madAccTr, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6)
                readValues[LONG_MAD]= "'"+long_accel_mad+"'";
                readValues[LAT_MAD]= "'"+lat_accel_mad+"'";
                readValues[TRAN_MAD]="'"+tran_accel_mad+"'";
                break;
            case 7:
                readValues[GSR_AVG]="'"+gsr_avg;
                readValues[HEAT_FLUX]="'"+0+"'";
            case 8:
                readValues[BATTERY]="'"+battery+"'";
                ready8 = true;


            default:
                System.out.println ("Message not supported to store");
        }

        //if (ready1 && ready2 &&ready3 &&ready4 &&ready5&&ready6&&ready7){
        if (ready1 && ready2 &&ready3 &&ready4 &&ready8){
            //Aggregate message 1 not received
            //Aggregate message 2 not received
            //Aggregate message 3 not received


            //Create a list with the values:
            ArrayList<String> tempValues = new ArrayList<String>(Arrays.asList(readValues));
            sensorValues.add(new ArrayList<String>(Arrays.asList(readValues)));
            //Save new values in the database
            myDB.updateDatabaseTable(bodymediaTableName, tempValues, true);

            //Reset values
            ready1=false;
            ready2=false;
            ready3=false;
            ready4=false;
            ready8 = false;

            //Reset intermediate message
            readValues = new String[_TOTAL_VARIABLES];
            for (int i = 0; i < readValues.length; i ++)
                readValues[i]="'0'";


            //TODO Save automatically when the information is received
            //Database.updateDatabase(ctx, sensorValues, Database.bodymediaTableName);
            //sensorValues.clear();

            /*
            try {
                DataStoring.syncSQLiteMySQLDB(ctx, bodymediaTableName);
            }catch (JSONException e){
                //Nothing
            }*/

        }
    }





}
