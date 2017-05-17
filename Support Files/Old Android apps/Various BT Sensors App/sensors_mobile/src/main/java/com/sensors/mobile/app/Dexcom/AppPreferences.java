package com.sensors.mobile.app.Dexcom;

import android.content.*;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import com.sensors.mobile.app.R;
import com.dexcom.G4DevKit.ReceiverUpdateService;
import com.dexcom.G4DevKit.ReceiverUpdateService.ServiceBinder;
import com.dexcom.enums.UsbPowerLevel;

public class AppPreferences extends PreferenceActivity implements
        OnSharedPreferenceChangeListener
{
    private SharedPreferences ApplicationPreferences;
    private ReceiverUpdateTools m_receiverUpdateTools;

    private Preference m_updateIntervalPref;
    private ListPreference m_usbPowerLevelPref;

    private int m_serviceUpdateInterval;
    private boolean m_serviceStatus;
    private ReceiverUpdateService m_receiverService;
    private Intent m_receiverUpdateServiceIntent;
    private boolean m_isServiceBound;

    @Override
    public void onCreate(
        Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        m_receiverUpdateTools = new ReceiverUpdateTools(getApplicationContext());

        // Get the default application preferences
        addPreferencesFromResource(R.xml.app_prefs);
        ApplicationPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        m_serviceUpdateInterval = ApplicationPreferences.getInt(
                "serviceUpdateInterval", 10);
        m_serviceStatus = ApplicationPreferences.getBoolean("serviceStatus",
                false);

        // Update the Summary fields in the preferences UI according to the
        // current preference values
        m_updateIntervalPref = findPreference("serviceUpdateInterval");
        m_updateIntervalPref.setSummary("Update every "
                + Integer.toString(m_serviceUpdateInterval) + " seconds");

        // Listen to changes to preferences
        ApplicationPreferences.registerOnSharedPreferenceChangeListener(this);

        // Bind to the receiver update service
        m_receiverUpdateServiceIntent = new Intent(this,
                ReceiverUpdateService.class);
        m_isServiceBound = false;

        Context appContext = this.getApplicationContext();
        appContext.bindService(m_receiverUpdateServiceIntent,
                m_serviceConnection, Context.BIND_ABOVE_CLIENT);

        m_usbPowerLevelPref = (ListPreference)findPreference("usbPowerLevel");
    }

    // Handle preference changes and update the UI
    @Override
    public void onSharedPreferenceChanged(
        SharedPreferences sharedPreferences,
        String key)
    {
        if (key.equals("serviceUpdateInterval"))
        {
            // If user changes the data update interval, set a new Alarm with
            // the appropriate timing
            int newUpdateInterval = sharedPreferences.getInt(
                    "serviceUpdateInterval", 10);
            m_receiverUpdateTools.setPeriodicUpdate(getApplicationContext(),
                    newUpdateInterval);

            m_updateIntervalPref.setSummary("Update every "
                    + Integer.toString(newUpdateInterval) + " seconds");
            getListView().invalidate();
        }
        else if (key.equals("serviceStatus"))
        {
            // If user flips the service status switch, start/stop the service
            m_serviceStatus = sharedPreferences.getBoolean("serviceStatus",
                    false);

            if (m_serviceStatus)
            {
                int serviceUpdateInterval = sharedPreferences.getInt(
                        "serviceUpdateInterval", 10);
                m_receiverUpdateTools.setPeriodicUpdate(
                        getApplicationContext(), serviceUpdateInterval);
                startService(new Intent(getApplicationContext(),
                        ReceiverUpdateService.class));
            }
            else
            {
                m_receiverUpdateTools
                        .cancelPeriodicUpdate(getApplicationContext());
                stopService(new Intent(getApplicationContext(),
                        ReceiverUpdateService.class));
            }
        }
        else if (key.equals("usbPowerLevel"))
        {
            if (m_isServiceBound)
            {
                Byte selection = Byte.valueOf(m_usbPowerLevelPref.getValue());
                UsbPowerLevel newLevel = UsbPowerLevel
                        .getEnumFromValue(selection);

                boolean success = m_receiverService
                        .setCurrentUsbPowerLevel(newLevel);

                if (success)
                    m_usbPowerLevelPref
                            .setSummary(getPowerLevelString(newLevel));
                else
                    m_usbPowerLevelPref
                            .setSummary("Failed to set new power level");
            }
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        ApplicationPreferences.unregisterOnSharedPreferenceChangeListener(this);
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

            // Get the current USB power level and update the correponding
            // preference caption
            UsbPowerLevel powerLevel = m_receiverService
                    .readCurrentUsbPowerLevel();
            m_usbPowerLevelPref.setSummary(getPowerLevelString(powerLevel));

            m_usbPowerLevelPref.setValueIndex(powerLevel.value);
        }
    };

    private String getPowerLevelString(
        UsbPowerLevel level)
    {
        String powerLevelString = "";
        switch (level)
        {
            case Pwr100mA:
                powerLevelString = "100 mA";
                break;
            case Pwr500mA:
                powerLevelString = "500 mA";
                break;
            case PwrMax:
                powerLevelString = "Max";
                break;
            case PwrSuspend:
                powerLevelString = "Suspend";
                break;
            default:
                powerLevelString = "Unknown";
                break;
        }

        return powerLevelString;
    }
}
