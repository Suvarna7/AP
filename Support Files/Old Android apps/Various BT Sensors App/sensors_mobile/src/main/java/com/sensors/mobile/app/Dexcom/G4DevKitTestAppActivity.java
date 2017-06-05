package com.sensors.mobile.app.Dexcom;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.*;
import com.sensors.mobile.app.R;
import com.dexcom.G4DevKit.*;

public class G4DevKitTestAppActivity extends Activity implements
        View.OnClickListener, Runnable
{
    public SharedPreferences applicationPreferences;

    private Intent m_receiverUpdateServiceIntent;
    private ReceiverUpdateTools m_receiverUpdateTools;
    private ActionBar m_actionBar;

    private final int DEFAULT_DATA_UPDATE_INTERVAL = 10; // seconds

    public static Context ctx;

    @Override
    public void onCreate(
        Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dexcom);
        ctx=this;

        // Get the application preferences
        applicationPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        int currentUpdateIntervalSetting = applicationPreferences.getInt(
                "serviceUpdateInterval", DEFAULT_DATA_UPDATE_INTERVAL);

        // Repeating intent to check for new receiver data
        m_receiverUpdateTools = new ReceiverUpdateTools(this);
        m_receiverUpdateTools.setPeriodicUpdate(this,
                currentUpdateIntervalSetting);

        // Start the receiver update service
        m_receiverUpdateServiceIntent = new Intent(this,
                ReceiverUpdateService.class);
        startService(m_receiverUpdateServiceIntent);
        applicationPreferences.edit().putBoolean("serviceStatus", true)
                .commit();

        // Create and populate the action bar
        m_actionBar = getActionBar();
        m_actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        m_actionBar.setDisplayShowTitleEnabled(true);

        Tab tab = m_actionBar.newTab();
        tab.setText("EGV");
        tab.setTabListener(new TabListener<EstimatedGlucoseRecord>("EGV",
                EstimatedGlucoseRecord.class));
        m_actionBar.addTab(tab);

        tab = m_actionBar.newTab();
        tab.setText("Meter");
        tab.setTabListener(new TabListener<MeterRecord>("Meter",
                MeterRecord.class));
        m_actionBar.addTab(tab);

        tab = m_actionBar.newTab();
        tab.setText("User Settings");
        tab.setTabListener(new TabListener<SettingsRecord>("Settings",
                SettingsRecord.class));
        m_actionBar.addTab(tab);

        tab = m_actionBar.newTab();
        tab.setText("Insertion");
        tab.setTabListener(new TabListener<InsertionTimeRecord>("Insertion",
                InsertionTimeRecord.class));
        m_actionBar.addTab(tab);

        // Restore the last tab selection
        if (savedInstanceState != null)
        {
            m_actionBar.setSelectedNavigationItem(savedInstanceState
                    .getInt("LastSelectedTab"));
        }

    }

    @Override
    public void onSaveInstanceState(
        Bundle outState)
    {
        outState.putInt("LastSelectedTab", m_actionBar.getSelectedTab()
                .getPosition());
    }

    @Override
    public void run()
    {

    }

    @Override
    public void onClick(
        View v)
    {

    }

    @Override
    public boolean onCreateOptionsMenu(
        Menu menu)
    {
        MenuInflater infl = getMenuInflater();
        infl.inflate(R.menu.dexcom_menu, menu);

        return true;
    }

    // Handle menu clicks
    @Override
    public boolean onOptionsItemSelected(
        MenuItem item)
    {
        boolean isHandled = false;

        switch (item.getItemId())
        {
            case R.id.refresh:
                m_receiverUpdateTools.performUpdate(this);
                isHandled = true;
                break;

            case R.id.status:
                showStatusDialog();
                isHandled = true;
                break;

            case R.id.preferences:
                startActivity(new Intent(getApplicationContext(),
                        AppPreferences.class));
                isHandled = true;
                break;

            default:
                isHandled = super.onOptionsItemSelected(item);
                break;
        }

        return isHandled;
    }

    // Show a dialog containing information about latest receiver data
    private void showStatusDialog()
    {
        FragmentTransaction fTransaction = getFragmentManager()
                .beginTransaction();
        StatusDialogFragment dialog = new StatusDialogFragment();
        dialog.show(fTransaction, "Status");
    }

    // Handle tab clicks. When a tab is clicked, display the appropriate
    // fragment
    public static class TabListener<T extends Parcelable> implements
            ActionBar.TabListener
    {
        private Fragment m_fragment;
        private final String m_tag;
        private final Class<T> m_recordClass;

        public TabListener(
            String tag,
            Class<T> recordClass)
        {
            m_tag = tag;
            m_recordClass = recordClass;
        }

        @Override
        public void onTabReselected(
            Tab tab,
            FragmentTransaction ft)
        {

        }

        @Override
        public void onTabSelected(
            Tab tab,
            FragmentTransaction ft)
        {
            if (m_fragment == null)
            {
                m_fragment = new DataDisplayFragment<T>(m_recordClass);
                ft.add(android.R.id.content, m_fragment, m_tag);
            }
            else
            {
                ft.attach(m_fragment);
            }
        }

        @Override
        public void onTabUnselected(
            Tab tab,
            FragmentTransaction ft)
        {
            if (m_fragment != null)
            {
                ft.detach(m_fragment);
            }
        }

    }
}
