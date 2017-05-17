/**
 * Copyright (c) 2015, BodyMedia Inc. All Rights Reserved
 */

package com.sensors.mobile.app.BM.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bodymedia.btle.toolkit.utils.ScanMode;
import com.bodymedia.mobile.sdk.GeckoDevice;
import com.bodymedia.mobile.sdk.JawboneDevice;
import com.sensors.mobile.app.R;

import java.util.List;

public class ArmbandAdapter extends ArrayAdapter<JawboneDevice> {
    private final LayoutInflater inflater;

    private final int pairingModeColor;
    private final int normalModeColor;
    private final int factoryModeColor;
    private final int recoveryModeColor;

    public ArmbandAdapter(Context context, List<JawboneDevice> serialNumbers) {
        super(context, R.layout.listview_list_item, R.id.list_content, serialNumbers);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pairingModeColor = context.getResources().getColor(R.color.pairing_mode);
        factoryModeColor = context.getResources().getColor(R.color.cool_green);
        recoveryModeColor = context.getResources().getColor(R.color.recovery_red);
        normalModeColor = context.getResources().getColor(android.R.color.holo_blue_dark);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView != null ? convertView : inflater.inflate(R.layout.listview_list_item, null);
        TextView textView = (TextView) view.findViewById(R.id.list_content);
        JawboneDevice jawboneDevice = getItem(position);

        String label = jawboneDevice.getSerialNumber();

        if (jawboneDevice instanceof GeckoDevice) {
            label += " (gecko)";
            textView.setTextColor(getColor(((GeckoDevice) jawboneDevice).getScanMode()));
        }

        textView.setText(label);
        return view;
    }

    private int getColor (ScanMode scanMode) {
        switch (scanMode) {
        case BOOTLOADER:
            return recoveryModeColor;
        case PAIR:
            return pairingModeColor;
        default:
            if (scanMode.isFactoryMode()) {
                return factoryModeColor;
            }
            return normalModeColor;
        }
    }
}
