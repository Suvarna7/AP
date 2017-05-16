package com.sensors.mobile.app.Dexcom;

import android.app.Activity;
import android.app.Fragment;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.sensors.mobile.app.Database.DataStoring;
import com.sensors.mobile.app.Database.Database;
import com.sensors.mobile.app.R;
import com.dexcom.G4DevKit.*;
import com.dexcom.G4DevKit.ReceiverUpdateService.ServiceBinder;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

// Fragment to display data data in a ListView
public class DataDisplayFragment<T extends Parcelable> extends Fragment
{
    private ListView m_dataList;
    private Context m_appContext;
    private Activity m_activity;
    private final Class<T> m_recordClass;

    private Intent m_refreshAllDataIntent;
    private BroadcastReceiver m_dataBReceiver;
    private ReceiverUpdateService m_receiverService;
    private Intent m_receiverUpdateServiceIntent;

    private List<T> m_dataRecordList;
    private final IntentFactory<T> m_intents;

    private boolean m_isServiceBound;

    //To sync remote
    public static List <String> toUpdate = new ArrayList<String>();
    private boolean initialized;


    public DataDisplayFragment(
            Class<T> recordClass)
    {
        m_intents = new IntentFactory<T>(recordClass);
        m_recordClass = recordClass;
    }

    @Override
    public void onCreate(
        Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        m_appContext = getActivity().getApplicationContext();
        m_activity = getActivity();

        m_receiverUpdateServiceIntent = new Intent(m_activity,
                ReceiverUpdateService.class);
        m_isServiceBound = false;

        setHasOptionsMenu(true);

        m_refreshAllDataIntent = new Intent(ServiceIntents.UPDATE_RECEIVER_DATA);

        // Turn on the progress spinner visibility while downloading data
        getActivity().setProgressBarIndeterminateVisibility(true);

        // Bind to the receiver update service
        m_appContext.bindService(m_receiverUpdateServiceIntent,
                m_serviceConnection, Context.BIND_ABOVE_CLIENT);
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        initialized = false;
        View fragmentView = inflater.inflate(R.layout.data_list_fragment,
                container, false);

        m_dataList = (ListView)fragmentView.findViewById(R.id.dataList);

        m_dataBReceiver = new BroadcastReceiver()
        {

            @Override
            public void onReceive(
                Context context,
                Intent intent)
            {
                processNewDataIntent(intent);
            }
        };

        this.setRetainInstance(true);
        this.setHasOptionsMenu(true);

        return fragmentView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        IntentFilter newDataFilter = new IntentFilter(
                m_intents.newDataAvailableIntent());

        m_appContext.registerReceiver(m_dataBReceiver, newDataFilter);

        if (m_isServiceBound)
        {
            updateData(m_recordClass);
        }
    }

    @Override
    public boolean onOptionsItemSelected(
        MenuItem item)
    {
        boolean isHandled = false;

        switch (item.getItemId())
        {
            case R.id.refresh:
                m_appContext.sendBroadcast(m_refreshAllDataIntent);
                isHandled = true;
                break;
            default:
                isHandled = super.onOptionsItemSelected(item);
                break;
        }

        return isHandled;
    }

    @Override
    public void onPause()
    {
        super.onPause();

        m_appContext.unregisterReceiver(m_dataBReceiver);
    }

    @SuppressWarnings("unchecked")
    private void updateData(
        Class<T> recordClass)
    {
        // Struggling to make this class completely generic - need to
        // specify the proper data class using class information passed into the
        // constructor
        if (recordClass.getName()
                .equals(EstimatedGlucoseRecord.class.getName()))
        {
            m_dataRecordList = (List<T>)m_receiverService.egvRecords;
            //TODO SyncValues
            for (T dataRecord : m_dataRecordList) {
                EstimatedGlucoseRecord egv = (EstimatedGlucoseRecord) dataRecord;
                String time = egv.DisplayTime.toLocaleString();
                short val = egv.Value;
                //double val = 0;

                toUpdate.add("(" + val + ", '" + time + "', 'no')");

            }
            if (!initialized){
                Database.initDatabase(G4DevKitTestAppActivity.ctx);
                initialized = true;
            }
            //TODO Update database automatically
            //Database.updateDatabase(G4DevKitTestAppActivity.ctx, toUpdate, Database.dexcomTableName);
            //toUpdate.clear();

            try {
                DataStoring.syncSQLiteMySQLDB(G4DevKitTestAppActivity.ctx, Database.dexcomTableName);


            }catch (JSONException e){
                System.out.println(e);
            }catch (Exception e){
                System.out.println(e);
            }
        }
        else if (recordClass.getName().equals(MeterRecord.class.getName()))
        {
            m_dataRecordList = (List<T>)m_receiverService.meterRecords;
        }
        else if (recordClass.getName().equals(SettingsRecord.class.getName()))
        {
            m_dataRecordList = (List<T>)m_receiverService.settingsRecords;
        }
        else if (recordClass.getName().equals(
                InsertionTimeRecord.class.getName()))
        {
            m_dataRecordList = (List<T>)m_receiverService.insertionRecords;
        }

        ArrayAdapter<T> aAdapter = new ArrayAdapter<T>(m_appContext,
                R.layout.list_item, R.id.listItemText, m_dataRecordList);
        m_dataList.setAdapter(aAdapter);
        m_dataList.setClickable(true);

        if (!m_dataRecordList.isEmpty())
        {
            getActivity().setProgressBarIndeterminateVisibility(false);
        }
    }

    private void processNewDataIntent(
        Intent intent)
    {
        getActivity().setProgressBarIndeterminateVisibility(true);

        updateData(m_recordClass);

        getActivity().setProgressBarIndeterminateVisibility(false);
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

            updateData(m_recordClass);
        }
    };
}
