/**
 * Copyright (c) 2015, BodyMedia Inc. All Rights Reserved
 */

package com.sensors.mobile.app.BM.ui;

import android.content.Intent;


import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Time;
import android.view.*;
import android.widget.*;

import com.bodymedia.android.utils.Logger;
import com.bodymedia.btle.packet.*;
import com.bodymedia.mobile.sdk.*;
import com.bodymedia.mobile.sdk.listener.ArmbandListener;
import com.bodymedia.mobile.sdk.model.ArmbandConfiguration;
import com.bodymedia.mobile.sdk.stream.StreamingService;
import com.bodymedia.mobile.sdk.validator.ValidationException;
import com.sensors.mobile.app.BM.MainActivityBM;
import com.sensors.mobile.app.BM.SenseWearApplication;
import com.sensors.mobile.app.BM.BGService;


import com.sensors.mobile.app.BM.streaming.StreamCombinedECGAcellGSRPlotConfig;
import com.sensors.mobile.app.BM.streaming.StreamPlotConfiguration;
import com.sensors.mobile.app.Database.Database;
import com.sensors.mobile.app.R;

import java.text.SimpleDateFormat;
import java.util.*;

import android.view.View.OnClickListener;

import rx.Observer;

import com.bodymedia.btle.packet.DeviceStream;

public class MinuteRateFragment extends AbstractArmbandFragment implements CompoundButton.OnCheckedChangeListener{

    private static final Logger LOG = Logger.getInstance(MinuteRateFragment.class);

    private Switch minuteUpdates;
    private Switch highUpdates;
    private Switch cumulativeUpdates;
    private Button streamRecordON;
    private Button streamRecordOFF;

    private ScrollView scrollView;
    private TextView log;
    private static boolean aggregated;
    //private int firstAgg;

    private StreamPlotConfiguration currentPlotConfiguration;


    public static DevicePipe minutePipe;
    public static StreamingService minuteService;

    //Local database manager:
    private Database minuteDatabase;
    private static final String bmMinuteTableName = "bodymedia_minute";




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // look if armband is configured
        aggregated = false;
        //firstAgg = 0;
        checkArmbandConfigurations();

        //HighRate
        Armband armband = SenseWearApplication.get().getArmband();
        if (armband != null) {
            if (currentPlotConfiguration == null) {
                currentPlotConfiguration = new StreamCombinedECGAcellGSRPlotConfig(getActivity());
            }
            //updatePlotConfig();
        }

        //Init database
        minuteDatabase = new Database(getActivity());
        //TODO Table name is different from BG service (POSSIBLE ISSUES) !!!!!!
        minuteDatabase.createTable(bmMinuteTableName, BGService.bmColumns[0],new ArrayList<String>(Arrays.asList(BGService.bmColumns)));

        minutePipe = SenseWearApplication.get().getArmbandManager().getArmband().getGeckoDevice().getDevicePipe();
        minuteService = SenseWearApplication.get().getArmbandManager().getArmband().getStreamingService();




    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.minute_rate, container, false);
        minuteUpdates = (Switch) layout.findViewById(R.id.updateMinuteSwitch);
        minuteUpdates.setOnCheckedChangeListener(this);
        highUpdates = (Switch) layout.findViewById(R.id.updateHighSwitch);
        highUpdates.setOnCheckedChangeListener(this);
        cumulativeUpdates = (Switch) layout.findViewById(R.id.updateCumulativeSwitch);
        cumulativeUpdates.setOnCheckedChangeListener(this);

        //******** Init buttons
        //Streaming ON
        streamRecordON = (Button) layout.findViewById(R.id.streamRecordBodymediaON);
        streamRecordON.setOnClickListener(streamRecordListenerON);

        //Streaming OFF
        streamRecordOFF = (Button) layout.findViewById(R.id.streamRecordBodymediaOFF);
        streamRecordOFF.setOnClickListener(streamRecordListenerOFF);

        //Erase database
        Button eraseData = (Button) layout.findViewById(R.id.databaseErase);
        eraseData.setOnClickListener(databaseEraseListener);

        log = (TextView) layout.findViewById(R.id.logTextView);
        scrollView = (ScrollView) layout.findViewById(R.id.scrollView);

        return layout;

    }



    /**
     * Listener for the Service ON button
     */

    protected OnClickListener streamRecordListenerON = new OnClickListener(){
        public void onClick(View v) {
            //Notify we pressed the button
            streamRecordON.setBackgroundColor(Color.BLUE);
            streamRecordOFF.setBackgroundColor(Color.GRAY);

            writeLog("************ \n STREAMING ON \n ************ \n");
            //We do not use the listeners of this class anymore
            stopStreamingUpdates();
            getActivity().startService(new Intent(MainActivityBM.ctx, BGService.class));
            startStreamingPackets();

            //DevicePipe pipe = SenseWearApplication.get().getArmbandManager().getArmband().getGeckoDevice().getDevicePipe();
           // pipe.unsubscribe(packetListener);
            //pipe.subscribe(packetListener, PacketType.GEC_ALG_MINUTE, PacketType.GEC_AGG_2OF3, PacketType.GEC_AGG_3OF3, PacketType.GEC_AGG_1OF3);

            //TODO Start service

        }

    };

    /**
     * Listener for the Service OFF button
     */

    protected OnClickListener streamRecordListenerOFF = new OnClickListener(){
        public void onClick(View v) {
            //Notify button pressed
            writeLog("************ \n STREAMING OFF \n ************ \n");
            streamRecordON.setBackgroundColor(Color.GRAY);
            streamRecordOFF.setBackgroundColor(Color.BLUE);

            // DevicePipe pipe = SenseWearApplication.get().getArmbandManager().getArmband().getGeckoDevice().getDevicePipe();
            //pipe.unsubscribe(packetListener);
            //Stop streaming
            stopStreamingFunctions();

            //Stop listening

            //TODO Stop service
            getActivity().stopService(new Intent(MainActivityBM.ctx, BGService.class));



        }
        };
    /**
     * Listener for the Database Erase button
     */
    protected OnClickListener databaseEraseListener = new OnClickListener(){
        public void onClick(View v) {
            //Notify button pressed
            writeLog("************ \n Erase database \n ************ \n");

            //Erase database values
            minuteDatabase.updateDatabaseTable(bmMinuteTableName, null, false);


            /*DialogFragment newFragment = new ConnectionAlertDialogFragment();
            newFragment.show(getFragmentManager(), "missiles");*/

        }
    };


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.minute_rate_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.show_device_configuration:
                ((MainActivityBM) getActivity()).showFragment(new UserInfoFragment());
                break;
            case R.id.show_device_info:
                ((MainActivityBM) getActivity()).showFragment(new ConnectedDeviceFragment());
                break;
            case R.id.show_high_rate_screen:
                //stopStreamingUpdates();
                ((MainActivityBM) getActivity()).showFragment(new HighRateFragment());
                break;
            case R.id.clear_log:
                log.setText("");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.minute_rate_title));

        //Start streaming
        updateStreaming();
    }

    @Override
    public void onPause() {

        //stopStreamingFunctions();
        //Stop updates
        //stopStreaming();
        /*if (armband != null) {
            //TODO Do not remove the listener
            armband.getStreamingService().removeStreamingArmbandListener(highRateArmbandListener);
        }*/
        super.onPause();

    }

    @Override
    public void onStop(){
        //startStreamingPackets();
        super.onStop();

    }

    @Override
    public void onDestroy(){
        //stopStreamingFunctions();
        //TODO DO not stop streaming
        // stopStreaming();

        super.onDestroy();

    }
    ///Start streaming
    public void startStreamingPackets() {
        if(SenseWearApplication.get().getArmbandManager().getArmband() != null) {
            DevicePipe pipe = SenseWearApplication.get().getArmbandManager().getArmband().getGeckoDevice().getDevicePipe();
            //pipe.unsubscribe(packetListener);
            //pipe.subscribe(packetListener, PacketType.GEC_ALG_MINUTE, PacketType.GEC_AGG_2OF3, PacketType.GEC_AGG_3OF3, PacketType.GEC_AGG_1OF3);


            try {

                //pipe.unsubscribe(packetListener);
                //pipe.subscribe(packetListener,  PacketType.GEC_AGG_2OF3, PacketType.GEC_AGG_3OF3, PacketType.GEC_AGG_1OF3);

                //TODO Set more parameters when pressing ON
                /*DeviceRecord dRecord = new DeviceRecord();
                dRecord.setAggregatesAllEnabled(true);
                dRecord.setAlgorithmMinuteEnabled(true);
                dRecord.setAlgorithmBestFitEnabled(true);
                dRecord.setSensorPartitionOverwriteEnabled(true);
                dRecord.setLogPartitionOverwriteEnabled(true);
                dRecord.setHeartBeatPartitionOverwriteEnabled(true);
                dRecord.setAlgorithmPartitionOverwriteEnabled(true);
                dRecord.setAggregatePartitionOverwriteEnabled(true);
                pipe.writePacket(dRecord);*/

               /* UploadRequest uRequest = new UploadRequest();
                //uRequest.setPartition(Partition.Aggregate);
                uRequest.setHighBit(true);
                //pipe.writePacket(uRequest);*/

                /*UserInfo uInfo = new UserInfo();
                // uInfo.setEnableAlwaysOn(true   );
                //uInfo.setEnableEcgSensor(true);
                //uInfo.setEnableOffBodyEstimates(true);
                uInfo.setHighBit(true);
                //pipe.writePacket(uInfo);*/

                //A) Streamer
                DeviceStream streamer = new DeviceStream();

                streamer.setStreamSensorsRawEnabled(true);
                streamer.setStreamAlgorithmMinutesEnabled(true);
                streamer.setStreamSensorsAccelerometerECGEnabled(true);
                streamer.setStreamSensorsCalibratedEnabled(true);
                streamer.setStreamBeatsEnabled(true);
                streamer.setStreamSensorsGSRTemperatureEnabled(true);
                streamer.setStreamSensorsAccelerometerEnabled(true);
                streamer.setStreamSensorsAccelerometerECGEnabled(true);
                //streamer.setStreamSensorsECGCalibratedEnabled(true);
                //streamer.setStreamSensorsECGRawEnabled(false);

                //pipe.writePacket(streamer);

                //B) Device stream: current plot configuration
               // DeviceStream deviceStream = new DeviceStream();
                //deviceStream = currentPlotConfiguration.enableDeviceStream(deviceStream);
                SenseWearApplication.get().getArmband().configureStreaming(streamer).subscribe(
                        new Observer<DeviceStream>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable throwable) {

                            }

                            @Override
                            public void onNext(DeviceStream deviceStream) {

                            }
                        }
                );
                //SenseWearApplication.get().getArmbandManager().getArmband().getStreamingService().removeStreamingArmbandListener(highRateArmbandListener);
                //SenseWearApplication.get().getArmbandManager().getArmband().getStreamingService().addStreamingArmbandListener(highRateArmbandListener);

            } catch (Exception e) {
                System.out.println("Stream button: " + e);
            }
        }

    }

    //Stop streaming
    public static void stopStreamingFunctions(){
        //Stop aggregate
        if (SenseWearApplication.get().getArmbandManager().getArmband()!= null) {
            //DevicePipe pipe = SenseWearApplication.get().getArmbandManager().getArmband().getGeckoDevice().getDevicePipe();
            //Stop streaming
            DeviceStream streamer = new DeviceStream();
            streamer.setAllEnabled(false);
            streamer.setHighBit(true);

            try {
                SenseWearApplication.get().getArmband().configureStreaming(streamer).subscribe(
                        new Observer<DeviceStream>() {
                            @Override
                            public void onCompleted() {
                                System.out.println("STREAM OFF ");
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                System.out.println("STREAM OFF Something went wrong trying to send DeviceUpdates packet");
                            }

                            @Override
                            public void onNext(DeviceStream deviceStream) {
                                System.out.println("STREAM OFF Received DeviceUpdates packet: " + deviceStream);
                            }
                        }
                );

                //SenseWearApplication.get().getArmband().getStreamingService().removeStreamingArmbandListener(highRateArmbandListener);

            } catch (Exception e) {
                System.out.println("Exception with stream: "+e);
            }
        }


    }

    private void writeLog(final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                String newLine = time + ":  " + text;
                log.setText(log.getText().toString() + newLine + "\n");
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    /* OnCheckedChangeListener method implementation. Used for switches */
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        updateStreaming();
    }

    /**
     * It used to get state of each switch and enable/disable streaming.
     */
    private void updateStreaming() {

        DeviceUpdates updates = new DeviceUpdates();
        updates.setUpdateMinuteEnabled(minuteUpdates.isChecked());
        updates.setUpdateHighEnabled(highUpdates.isChecked());
        updates.setUpdateCumulativeEnabled(cumulativeUpdates.isChecked());

        updatesStateChanged(updates);

        //Activate Streaming of other values
        if(!aggregated)
            startStreamingPackets();


    }

    /**
     * To stop streaming updates we just need to disable all updates
     * in DeviceUpdates packet and send it.
     */
    private void stopStreamingUpdates() {
        //Stop all update  activities
        DeviceUpdates updates = new DeviceUpdates();
        updates.setUpdateMinuteEnabled(false);
        updates.setUpdateHighEnabled(false);
        updates.setUpdateCumulativeEnabled(false);
        updatesStateChanged(updates);
    }

    /**
     * Used to enable/disable streaming updates in device.
     * Data will be received in listeners of each updates type.
     */

    private void updatesStateChanged(DeviceUpdates updates) {

        if (SenseWearApplication.get().getArmband()!= null) {
            try {

                //Notes
                //SenseWearApplication.get().getArmband().playMusics(notes);
                SenseWearApplication.get().getArmband().configureUpdate(
                        updates, minuteListener, highListener, cumulativeListener).subscribe(
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
        }
    }

    private String makeKey(String key) {
        return String.format("%5s %s: ", "", key);
    }

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

                BGService.buildMessageMinute(actType, vigorous, met, sleep, cal, mem, currentStamp);


                String dataStr = "Received minute data:\n" +
                        makeKey("Battery status") + bat + "\n" +
                        makeKey("Memory status") + mem + "\n" +
                        makeKey("Activity type for last minute") + actType + "\n" +
                        makeKey("Activity minutes today") + activity + "\n" +
                        makeKey("Sleep minutes today") + sleep + "\n" +
                        makeKey("MET minutes today") + met + "\n" +
                        makeKey("Calories per last minute") + cal + "\n" +
                        makeKey("Calories today") + cal + "\n" +
                        makeKey("Distance for last minute") + data.getDistanceForLastMinute() + "\n" +
                        makeKey("Distance today") + data.getDistanceToday() + "\n" +
                        makeKey("Vigorous activity minutes today") + vigorous;
                writeLog(dataStr);

                UploadRequest uReq = new UploadRequest();
                //uReq.setPartition(Partition.Aggregate);
                uReq.setHighBit(true);
                //uReq.setStartRecordNumberRequested(initRecord);
                /*try {
                    //pipe.writePacket(uReq);
                } catch (Exception e) {
                    //Do nothing
                }*/




            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        };
    }

    /* Listener for receiving High Updates */
    ArmbandListener<UpdateHigh> highListener = new ArmbandListener<UpdateHigh>() {
        @Override
        public void onSuccess(UpdateHigh data) {

            int stepsToday = data.getStepsToday();
            //int steps = data.getStepsCumulative();
            int heart_rate = data.getHeartRate();


            BGService.buildMessageUpdateHigh(stepsToday, heart_rate);





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
    private static ArmbandListener<HighRate> highRateArmbandListener = new ArmbandListener<HighRate>() {



        @Override
        public void onSuccess(HighRate data) {

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
                BGService.buildMessagePeak2(accForw, accLong, accTrans);



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
                BGService.buildMessagePeak2(accForw[0], accLong[1], accTrans[2]);
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


                    BGService.buildMessagePeak2(accForward, accLong, accTrans);
                    BGService.buildMessageBattery(battery);


                } else if (data.getType() == PacketType.GEC_SENSORS_RAW) {
                    SensorsRaw raw = (SensorsRaw) data;
                    int accForward = raw.getAccelerometerForward();
                    int accLong = raw.getAccelerometerLongitudinal();
                    int accTrans = raw.getAccelerometerTransverse();
                    long bat = raw.getBattery();

                    System.out.println("Building message1a, 1b: GEC_SENSORS_ECG_RAW " + bat);
                    System.out.println("Values: " + accForward + "-" + accLong + "-" + accTrans);
                    BGService.buildMessagePeak1(accForward, accLong, accTrans, bat);

                } else if (data.getType() == PacketType.GEC_SENSORS_GSRTEMP) {
                    SensorsGSRTemperature gsrTemp = (SensorsGSRTemperature) data;
                    double[] gsr = gsrTemp.getGSR();
                    double[] skin = gsrTemp.getSkinTemperature();
                    double[] cover = gsrTemp.getCoverTemperature();

                    //Sample 1: cover&skin mili degree / gsr nano siemes
                    //Sample 2: cover&skin mili degree /gsr nano siemens
                    BGService.buildMessageTemp(skin[0], gsr, cover[0]);
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





        /**
     * We should check if the armband is configured, because streaming screen
     * will not work and data in minute-rate screen will be incorrect.
     * If the device is not configured then configurations screen will appear.
     */
    private void checkArmbandConfigurations() {
        if (armband == null) {
            ((MainActivityBM) getActivity()).showHomeScreen();
            return;
        }

        if (armband.getGeckoDevice() instanceof GeckoDevice) {
            armband.readUserConfiguration().subscribe(new Observer<ArmbandConfiguration>() {

                @Override
                public void onCompleted() {
                    LOG.d("Retrieving configuration completed");
                }

                @Override
                public void onError(Throwable e) {
                    LOG.w(e);
                }

                @Override
                public void onNext(ArmbandConfiguration data) {
                    SenseWearApplication.get().setCachedArmbandConfiguration(data);
                    if (!data.isConfigured()) {
                        UIUtils.showToast(MinuteRateFragment.this, getString(R.string.warning_configure_armband));
                        ((MainActivityBM) getActivity()).showFragment(new UserInfoFragment());
                    }
                }

            });
        }
    }



}