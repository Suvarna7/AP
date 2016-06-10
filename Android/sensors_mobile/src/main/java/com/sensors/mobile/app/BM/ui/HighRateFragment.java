/**
 * Copyright (c) 2015, BodyMedia Inc. All Rights Reserved
 */

package com.sensors.mobile.app.BM.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.androidplot.xy.XYPlot;
import com.bodymedia.android.utils.Logger;
import com.bodymedia.btle.packet.*;
import com.sensors.mobile.app.BM.MainActivityBM;
import com.sensors.mobile.app.R;
import com.bodymedia.mobile.sdk.GeckoDevice;
import com.bodymedia.mobile.sdk.listener.ArmbandListener;
import com.sensors.mobile.app.BM.streaming.StreamCombinedECGAcellGSRPlotConfig;
import com.sensors.mobile.app.BM.streaming.StreamPlotConfiguration;

import rx.Observer;

import java.util.Calendar;

/**
 * Created by asiri on 2/11/14.
 */
public class HighRateFragment extends AbstractArmbandFragment {

    private static final Logger LOG = Logger.getInstance(HighRateFragment.class);

    /** Variable used to setup and configure device streaming. */
    private DeviceStream currentState = null;

    private StreamPlotConfiguration currentPlotConfiguration;
    private ListView plotList;
    private PlotArrayAdapter plotAdapter;

    private Observer<DeviceStream> deviceStreamObserver = new Observer<DeviceStream>() {

        @Override
        public void onCompleted() {
            LOG.d("Configuring streaming completed");
        }

        @Override
        public void onError(Throwable e) {
            LOG.w(e);
        }

        @Override
        public void onNext(DeviceStream data) {
            currentState = data;
        }

    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setHasOptionsMenu(true);

        // initialize our XYPlot reference:
        plotAdapter = new PlotArrayAdapter(getActivity());
        plotList = (ListView) getActivity().findViewById(R.id.plot_list);
        plotList.setAdapter(plotAdapter);

        currentState = new DeviceStream();


        if (armband != null) {
            if (currentPlotConfiguration == null) {
                currentPlotConfiguration = new StreamCombinedECGAcellGSRPlotConfig(getActivity());
            }
            updatePlotConfig();
        }
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.streaming, container, false);
    }

    @Override
    public void onDestroy() {
        // Stop streaming data before leaving this screen.
        stopStreaming();
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.high_rate_menu, menu);
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
            case R.id.show_minute_rate:
                ((MainActivityBM) getActivity()).showFragment(new MinuteRateFragment());
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Listener for receiving High Rate data and using it to update graphics */
    private ArmbandListener<HighRate> highRateArmbandListener = new ArmbandListener<HighRate>() {

        @Override
        public void onSuccess(HighRate data) {
            //Plot handle event
            handleEvent(data);

            //Get current time
            Calendar c = Calendar.getInstance();
            Time now = new Time();
            now.setToNow();
            //System.out.println(c);
            //System.out.println(now);

            //TODO Save data
            if (data.getType() == PacketType.GEC_SENSORS_ACCEL_ECG){
                SensorsCalibratedAccelerometerECG sensorEvent = (SensorsCalibratedAccelerometerECG) data;
                double accForw = sensorEvent.getAccelerometerForward();
                double accLong = sensorEvent.getAccelerometerLongitudinal();
                double accTrans = sensorEvent.getAccelerometerTransverse();
                //int[] ecg = sensorEvent.getECG();
                //int sec = sensorEvent.getSeconds();
                //MainActivityBM.buildMessage3(ecg);

                //System.out.println("SENSOR_ACCEL_ECG: " + data);
                //System.out.println("ECG: "+ ecg);
                //System.out.println("Time: "+ sec);

            }
            else if (data.getType() == PacketType.GEC_SENSORS_GSRTEMP){
                //Calibrated GSR&Temp Readings
                SensorsGSRTemperature tempPacket = (SensorsGSRTemperature) data;
                //System.out.println("SENSORS_GRSTEMP: "+ data);

            } else if (data.getType() == PacketType.GEC_SENSORS_RAW) {
                SensorsRaw raw = (SensorsRaw) data;
                int accForward =raw.getAccelerometerForward();
                int accLong = raw.getAccelerometerLongitudinal();
                int accTrans = raw.getAccelerometerTransverse();
                long bat = raw.getBattery();
                MainActivityBM.buildMessagePeak1(accForward, accLong, accTrans, bat);
                //System.out.println("Values: " +accForward + "-" + accLong + "-" + accTrans);

            }else if (data.getType() == PacketType.GEC_SENSORS_CAL){
                //Calibrated sensors readings: same accel as in GEC_SENSORS_ACCEL
                SensorsCalibrated calib = (SensorsCalibrated) data;
                double accForward =calib.getAccelerometerForward();
                double accLong =calib.getAccelerometerLongitudinal();
                double accTrans =calib.getAccelerometerTransverse();

                double battery = calib.getBattery();
                double skinTemp = calib.getSkinTemperature();
                double coverTemp = calib.getCoverTemperature();
                double gsr = calib.getGSR();
                double []gsrArray = new double [2];
                gsrArray[0] = gsr;
                gsrArray[1]= 0;



                MainActivityBM.buildMessagePeak2(accForward, accLong, accTrans);
                MainActivityBM.buildMessageBattery(battery);
                //Long + accTrans);
                //System.out.println("SENSORS CAL Temperatures : " +skinTemp + coverTemp);
                //System.out.println("SENSORS CAL Battery : " +battery);


            }else if (data.getType() == PacketType.GEC_SENSORS_ECG_CAL ){
                SensorsCalibrated calib = (SensorsCalibrated) data;
                double accForward =calib.getAccelerometerForward();
                double accLong =calib.getAccelerometerLongitudinal();
                double accTrans =calib.getAccelerometerTransverse();
                calib.getSkinTemperature();
                calib.getGSR();

                double battery = calib.getBattery();
                double skinTemp = calib.getSkinTemperature();
                double coverTemp = calib.getCoverTemperature();
                double gsr = calib.getGSR();
                double []gsrArray = new double [2];
                gsrArray[0] = gsr;
                gsrArray[1]= 0;



                MainActivityBM.buildMessagePeak2(accForward, accLong, accTrans);
                MainActivityBM.buildMessageBattery(battery);
                //System.out.println("SENSORS CAL Acceleration : " +accForward + accLong + accTrans);
                //System.out.println("SENSORS CAL Temperatures : " +skinTemp + coverTemp);
                //System.out.println("SENSORS CAL Battery : " +battery);


            }else if (data.getType() == PacketType.GEC_AGG_1OF3){
            }


        }

        @Override
        public void onError(Exception e) {}
    };

    private void updatePlotConfig() {
        plotAdapter.clear();
        for (View plot : currentPlotConfiguration.getPlots()) {
            plotAdapter.add(plot);
        }

        plotAdapter.notifyDataSetChanged();
        startStreaming();
        armband.getStreamingService().addStreamingArmbandListener(highRateArmbandListener);
    }

    private void startStreaming() {
        DeviceStream deviceStream = new DeviceStream();
        deviceStream = currentPlotConfiguration.enableDeviceStream(deviceStream);
        streamingStateChanged(deviceStream);
    }

    private void stopStreaming() {
        DeviceStream deviceStream = new DeviceStream();
        deviceStream = currentPlotConfiguration.disableDeviceStream(deviceStream);
        streamingStateChanged(deviceStream);
    }

    private void streamingStateChanged(DeviceStream deviceStream) {
        if (checkConnectedShowToast()) {
            if (armband.getGeckoDevice() instanceof GeckoDevice) {
                currentState = deviceStream;
            }
            armband.configureStreaming(currentState).subscribe(deviceStreamObserver);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.high_rate_title));
        if (checkConnectedShowToast()) {
            armband.getStreamingService().addStreamingArmbandListener(highRateArmbandListener);

            if (!(armband.getGeckoDevice() instanceof GeckoDevice) || currentState != null) {
                armband.configureStreaming(currentState).subscribe(deviceStreamObserver);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (armband != null) {
            armband.getStreamingService().removeStreamingArmbandListener(highRateArmbandListener);
        }
    }

    private void handleEvent(HighRate highRatePacket) {
        // Update data
        currentPlotConfiguration.handlePacketUpdate(highRatePacket);


        // Redraw plots
        for (View view : currentPlotConfiguration.getPlots()) {
            ((XYPlot) view.findViewById(R.id.streaming_plot)).redraw();
        }
    }

    private class PlotArrayAdapter extends ArrayAdapter<View> {

        public PlotArrayAdapter(Context context) {
            super(context, R.layout.streaming_plot);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getItem(position);
        }
    }

}
