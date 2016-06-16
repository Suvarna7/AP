package com.sensors.mobile.app.Dexcom;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sensors.mobile.app.Database.Database;
import com.dexcom.G4DevKit.EstimatedGlucoseRecord;
import com.sensors.mobile.app.R;
import com.dexcom.G4DevKit.ReceiverUpdateService;
import com.dexcom.G4DevKit.ReceiverUpdateService.ServiceBinder;

import java.util.ArrayList;

public class StatusDialogFragment extends DialogFragment
{
    private TextView m_currentEgvTime;
    private TextView m_currentEgvValue;
    private ImageView m_currentTrendArrow;

    private Activity m_activity;
    private Intent m_receiverUpdateServiceIntent;
    private ReceiverUpdateService m_receiverService;

    private boolean m_isServiceBound;

    // Most recent receiver data
    // private String m_transmitterId;
    // private SettingsRecord m_settingsRecord;
    // private MeterRecord m_meterRecord;
    private EstimatedGlucoseRecord m_egvRecord;

    // private InsertionTimeRecord m_insertionRecord;

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState)
    {
        m_activity = getActivity();

        m_receiverUpdateServiceIntent = new Intent(m_activity,
                ReceiverUpdateService.class);

        // Inflate the layout and get references to resources
        View v = inflater.inflate(R.layout.status_dialog, container, false);

        getDialog().setTitle("Latest Receiver Info");

        m_currentEgvTime = (TextView)v.findViewById(R.id.currentEgvTimeText);
        m_currentEgvValue = (TextView)v.findViewById(R.id.currentEgvText);
        m_currentTrendArrow = (ImageView)v.findViewById(R.id.currentArrow);

        // Bind to the receiver update service
        m_activity.bindService(m_receiverUpdateServiceIntent,
                m_serviceConnection, Context.BIND_ABOVE_CLIENT);

        //Update Database
        //TODO Changed
        //ArrayList <String> toUpdate = new ArrayList();
       // String update = "(" +m_currentEgvValue +", "+m_currentEgvTime +", no)";
        //toUpdate.add(update);
        //Database.updateDatabase(G4DevKitTestAppActivity.ctx, toUpdate, Database.dexcomTableName);
        //toUpdate.clear();

        return v;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (m_isServiceBound)
        {
            m_activity.unbindService(m_serviceConnection);
        }
    }

    private void getCurrentReceiverData()
    {
        m_egvRecord = m_receiverService.currentEstimatedGlucoseRecord;
        // m_meterRecord = m_receiverService.currentMeterRecord;
        // m_settingsRecord = m_receiverService.currentSettingsRecord;
        // m_insertionRecord = m_receiverService.currentInsertionRecord;
    }

    private void updateDisplay()
    {
        m_currentEgvTime.setText(m_egvRecord.DisplayTime.toLocaleString());
        m_currentEgvValue.setText(Integer.toString(m_egvRecord.Value));

        switch (m_egvRecord.getTrendArrowCode())
        {
            case 1: // Double Up
                m_currentTrendArrow
                        .setImageResource(R.drawable.arrow_white_up_two);
                break;
            case 2: // Single Up
                m_currentTrendArrow
                        .setImageResource(R.drawable.arrow_white_up_two);
                break;
            case 3: // 45 deg Up
                m_currentTrendArrow
                        .setImageResource(R.drawable.arrow_white_up_one);
                break;
            case 4: // Flat
                m_currentTrendArrow.setImageResource(R.drawable.arrow_white_nc);
                break;
            case 5: // 45 deg Down
                m_currentTrendArrow
                        .setImageResource(R.drawable.arrow_white_down_one);
                break;
            case 6: // Single Down
                m_currentTrendArrow
                        .setImageResource(R.drawable.arrow_white_down_two);
                break;
            case 7: // Double Down
                m_currentTrendArrow
                        .setImageResource(R.drawable.arrow_white_down_two);
                break;
            default:
                m_currentTrendArrow.setImageResource(R.drawable.question_mark);
        }
    }

    private final ServiceConnection m_serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceDisconnected(
            ComponentName name)
        {
            m_isServiceBound = false;
        }

        @Override
        public void onServiceConnected(
            ComponentName name,
            IBinder service)
        {
            ServiceBinder binder = (ServiceBinder)service;
            m_receiverService = binder.getService();

            m_isServiceBound = true;

            getCurrentReceiverData();
            updateDisplay();
        }
    };
}
