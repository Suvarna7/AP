/**
 * Copyright (c) 2015, BodyMedia Inc. All Rights Reserved
 */

package com.sensors.mobile.app.BM.streaming;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.bodymedia.btle.packet.DeviceStream;
import com.bodymedia.btle.packet.Packet;
import com.sensors.mobile.app.R;
import com.sensors.mobile.app.BM.widget.StreamPlot;

import java.text.ChoiceFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class StreamPlotConfiguration {

    protected List<View> plots = new ArrayList<View>();

    public abstract DeviceStream enableDeviceStream(DeviceStream stream);
    public abstract DeviceStream disableDeviceStream(DeviceStream stream);
    public abstract void handlePacketUpdate(Packet packet);

    private Context context;

    public StreamPlotConfiguration(Context context) {
        this.context = context;
    }

    protected LineAndPointFormatter createLineAndPointFormatter(int color) {
        LineAndPointFormatter formatter = new LineAndPointFormatter(color, 0x00000000, 0x00000000, null);

        return formatter;
    }

    protected SimpleXYSeries createSeries(String name) {
        SimpleXYSeries series = new SimpleXYSeries(name);
        series.useImplicitXVals();

        return series;
    }

    protected XYPlot addPlot(String title, String units, Number lowerBound, Number upperBound, double rangeStep, Number domainSize, double domainStep) {
        LinearLayout tempPlotLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.streaming_plot, null);
        final StreamPlot ecgPlot = (StreamPlot) tempPlotLayout.findViewById(R.id.streaming_plot);
        ecgPlot.setZoomVertically(true);

        ecgPlot.setTag(R.id.min_range_lower, lowerBound);
        ecgPlot.setTag(R.id.max_range_upper, upperBound);
        ecgPlot.setTag(R.id.current_range_lower, lowerBound);
        ecgPlot.setTag(R.id.current_range_upper, upperBound);

        ecgPlot.setRangeBoundaries(lowerBound, upperBound, BoundaryMode.FIXED);
        ecgPlot.setDomainBoundaries(0, domainSize, BoundaryMode.FIXED);
        ecgPlot.setTitle(title);
        ecgPlot.setUserRangeOrigin(0);
        ecgPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, rangeStep);
        ecgPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, domainStep);
        ecgPlot.setDomainValueFormat(new ChoiceFormat(new double[0], new String[0]));

        ecgPlot.setRangeLabel(units);

        final Button zoomInButton = (Button) tempPlotLayout.findViewById(R.id.plot_zoom_in);
        final Button zoomOutButton = (Button) tempPlotLayout.findViewById(R.id.plot_zoom_out);
        final Button zoomResetButton = (Button) tempPlotLayout.findViewById(R.id.plot_zoom_reset);

        zoomResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ecgPlot.reset();
            }
        });

        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ecgPlot.setZoom(Math.min(ecgPlot.getZoom() + 1f, 8.0f));
            }
        });

        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ecgPlot.setZoom(Math.max(ecgPlot.getZoom() - 1.0f, 1.0f));
            }
        });

        tempPlotLayout.findViewById(R.id.plot_zoom_auto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToggleButton button = (ToggleButton) view;
                if (button.isChecked()) {
                    ecgPlot.setAuto(true);
                    zoomInButton.setEnabled(false);
                    zoomOutButton.setEnabled(false);
                    zoomResetButton.setEnabled(false);
                } else {
                    ecgPlot.setAuto(false);
                    zoomInButton.setEnabled(true);
                    zoomOutButton.setEnabled(true);
                    zoomResetButton.setEnabled(true);
                }
            }
        });

        plots.add(tempPlotLayout);

        return ecgPlot;
    }

    public List<View> getPlots() {
        return plots;
    }
}
