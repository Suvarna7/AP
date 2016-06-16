/**
 * Copyright (c) 2015, BodyMedia Inc. All Rights Reserved
 */

package com.sensors.mobile.app.BM.streaming;

import android.content.Context;

import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.bodymedia.android.utils.Logger;
import com.bodymedia.btle.packet.AsimovValueData;
import com.bodymedia.btle.packet.DeviceStream;
import com.bodymedia.btle.packet.Packet;
import com.bodymedia.btle.packet.PacketType;
import com.bodymedia.btle.packet.SensorsCalibratedAccelerometerECG;
import com.bodymedia.btle.packet.SensorsGSRTemperature;
import com.sensors.mobile.app.BM.AppPrefs;
import com.sensors.mobile.app.BM.BGService;
import com.sensors.mobile.app.BM.MainActivityBM;
import com.sensors.mobile.app.R;

public class StreamCombinedECGAcellGSRPlotConfig extends StreamPlotConfiguration {
    private static final Logger LOG = Logger.getInstance(StreamCombinedECGAcellGSRPlotConfig.class);

    private SimpleXYSeries ecgSeries = null;
    private SimpleXYSeries longitudinalAxisSeries = null;
    private SimpleXYSeries transverseAxisSeries = null;
    private SimpleXYSeries forwardAxisSeries = null;
    private SimpleXYSeries coverTempSeries = null;
    private SimpleXYSeries skinTempSeries = null;
    private SimpleXYSeries gsrSeries = null;
    private XYPlot ecgPlot = null;
    private XYPlot accelerometerPlot = null;
    private XYPlot temperaturePlot = null;
    private XYPlot gsrPlot = null;

    private int historySize = 64;
    private int ecgHistorySize = 512;
    private int gsrTempHistorySize = 32;

    private Context context;

    public StreamCombinedECGAcellGSRPlotConfig(Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        // Accelerometer
        accelerometerPlot = addPlot(context.getString(R.string.plot_title_accelerometer), "milli-g", -4.1, 4.1, 1, historySize, historySize + 1);

        longitudinalAxisSeries = createSeries(context.getString(R.string.stream_longitudinal_label));
        transverseAxisSeries = createSeries(context.getString(R.string.stream_transverse_label));
        forwardAxisSeries = createSeries(context.getString(R.string.stream_forward_label));

        accelerometerPlot.addSeries(longitudinalAxisSeries, createLineAndPointFormatter(0xFFFF0000));
        accelerometerPlot.addSeries(transverseAxisSeries, createLineAndPointFormatter(0xFF00FF00));
        accelerometerPlot.addSeries(forwardAxisSeries, createLineAndPointFormatter(0xFF0000FF));

        // ECG
        ecgSeries = createSeries(context.getString(R.string.stream_ecg_calibrated_label));
        if (AppPrefs.getInstance().getECGstatus()) {
            ecgPlot = addPlot(context.getString(R.string.plot_title_ecg), "", 0, 4000, 1000, ecgHistorySize, 60);
            ecgPlot.getLegendWidget().setVisible(false);
            ecgPlot.addSeries(ecgSeries, createLineAndPointFormatter(0xFF00FF00));
        }

        // Temperature
        temperaturePlot = addPlot(context.getString(R.string.plot_title_temperature), "°C", 0, 56, 10, gsrTempHistorySize, gsrTempHistorySize + 1);

        coverTempSeries = createSeries(context.getString(R.string.stream_cover_label));
        skinTempSeries = createSeries(context.getString(R.string.stream_skin_label));

        temperaturePlot.addSeries(coverTempSeries, createLineAndPointFormatter(0xFFFF0000));
        temperaturePlot.addSeries(skinTempSeries, createLineAndPointFormatter(0xFF00FF00));


        // GSR
        gsrPlot = addPlot(context.getString(R.string.plot_title_gsr), "nano-Siemens", 0, 50, 10, gsrTempHistorySize, gsrTempHistorySize + 1);

        gsrSeries = createSeries(context.getString(R.string.stream_gsr_label));

        gsrPlot.addSeries(gsrSeries, createLineAndPointFormatter(0xFF00FF00));

    }

    @Override
    public DeviceStream enableDeviceStream(DeviceStream stream) {
        stream.setStreamSensorsAccelerometerECGEnabled(true);
        stream.setStreamSensorsGSRTemperatureEnabled(true);
        return stream;
    }

    @Override
    public DeviceStream disableDeviceStream(DeviceStream stream) {
        stream.setStreamSensorsAccelerometerECGEnabled(false);
        stream.setStreamSensorsGSRTemperatureEnabled(false);
        return stream;
    }

    @Override
    public void handlePacketUpdate(Packet packet) {
        if (packet.getType() == PacketType.GEC_SENSORS_ACCEL_ECG) {
            //TODO Save this info
            SensorsCalibratedAccelerometerECG sensorEvent = (SensorsCalibratedAccelerometerECG) packet;

            int[] ecgs = sensorEvent.getECG();
            double accF = sensorEvent.getAccelerometerForward();
            double accL = sensorEvent.getAccelerometerLongitudinal();
            double accT = sensorEvent.getAccelerometerTransverse();

            //MainActivityBM.buildMessagePeak1(accF, accL, accT);

            // get rid the oldest sample in history:
            if (ecgSeries.size() > ecgHistorySize) {
                for (int i = 0; i < ecgs.length; i++) {
                    ecgSeries.removeFirst();
                }
            }
            // add the latest history sample:
            for (int ecg : ecgs) {
                ecgSeries.addLast(null, ecg);
            }

            // get rid the oldest sample in history:
            if (longitudinalAxisSeries.size() > historySize) {
                longitudinalAxisSeries.removeFirst();
                transverseAxisSeries.removeFirst();
                forwardAxisSeries.removeFirst();
            }
            // add the latest history sample:
            longitudinalAxisSeries.addLast(null, sensorEvent.getAccelerometerLongitudinal());
            transverseAxisSeries.addLast(null, sensorEvent.getAccelerometerTransverse());
            forwardAxisSeries.addLast(null, sensorEvent.getAccelerometerForward());
        } else if (packet.getType() == PacketType.GEC_SENSORS_GSRTEMP) {
            SensorsGSRTemperature tempPacket = (SensorsGSRTemperature) packet;

            double cTemp = tempPacket.getCoverTemperature()[0];
            double []gsr = tempPacket.getGSR();
            double sTemp = tempPacket.getSkinTemperature()[0];

            BGService.buildMessageTemp(sTemp, gsr, cTemp);


            if (coverTempSeries.size() > gsrTempHistorySize) {
                for (int i = 0; i < 2; i++) {
                    coverTempSeries.removeFirst();
                    skinTempSeries.removeFirst();
                    gsrSeries.removeFirst();
                }
            }

            for (int i = 0; i < 2; i++) {
                coverTempSeries.addLast(null, tempPacket.getCoverTemperature()[i]);
                skinTempSeries.addLast(null, tempPacket.getSkinTemperature()[i]);
                LOG.w("gsr = " + tempPacket.getGSR()[i]);
                LOG.w("time = " + (tempPacket.getMinutesSinceEpoch() * 60 + tempPacket.getSeconds()) + tempPacket.getTicks() / 32.0);
                gsrSeries.addLast(null, tempPacket.getGSR()[i]);
            }
        } else if (packet.getType() == PacketType.ASIMOV_HR_DATA) {
            System.out.println("Got an azimov packet!");
            AsimovValueData asimovHRData = (AsimovValueData) packet;

            int[] hr = asimovHRData.getValues();

            // get rid the oldest sample in history:
            if (ecgSeries.size() > ecgHistorySize) {
                for (int i = 0; i < hr.length; i++) {
                    ecgSeries.removeFirst();
                }
            }
            // add the latest history sample:
            for (int ecg : hr) {
                ecgSeries.addLast(null, ecg);
            }
        }
    }
}
