/**
 * Copyright (c) 2015, BodyMedia Inc. All Rights Reserved
 */

package com.sensors.mobile.app.BM.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sensors.mobile.app.BM.PairedDevice;
import com.sensors.mobile.app.R;
import com.sensors.mobile.app.BM.SenseWearApplication;
import com.bodymedia.mobile.sdk.DeviceCache;
import com.bodymedia.mobile.sdk.JawboneDevice;

import java.util.List;

public class PairedDeviceAdapter extends ArrayAdapter<PairedDevice> {
    private final LayoutInflater inflater;

    private final int normalModeColor;


    public PairedDeviceAdapter(Context context, List<PairedDevice> serialNumbers) {
        super(context, R.layout.listview_list_item_sub_item, R.id.list_content, serialNumbers);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        normalModeColor = context.getResources().getColor(R.color.basic_orange);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView != null ? convertView : inflater.inflate(R.layout.listview_list_item_sub_item, null);

        TextView textView = (TextView) view.findViewById(R.id.text_item_title);

        PairedDevice geckoDevice = getItem(position);
        textView.setText(geckoDevice.getSerialNumber());
        textView.setTextColor(normalModeColor);
        textView.setGravity(Gravity.CENTER);

        textView = (TextView) view.findViewById(R.id.text_item_subtitle);
        textView.setTextColor(normalModeColor);
        textView.setGravity(Gravity.CENTER);

        DeviceCache deviceCache = SenseWearApplication.get().getArmbandManager().getDeviceCache();
        if (deviceCache != null) {
            JawboneDevice cachedDevice = deviceCache.getGeckoDevice(geckoDevice.getSerialNumber());
            if (cachedDevice != null){
                textView.setText(cachedDevice.getMacAddress());
            }
        }

        return view;
    }
}