/**
 * Copyright (c) 2015, BodyMedia Inc. All Rights Reserved
 */

package com.sensors.mobile.app.BM.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.bodymedia.android.utils.Logger;
import com.sensors.mobile.app.BM.MainActivityBM;
import com.sensors.mobile.app.R;
import com.sensors.mobile.app.BM.SenseWearApplication;
import com.bodymedia.mobile.sdk.Armband;
import com.bodymedia.mobile.sdk.ArmbandManager;
import com.bodymedia.mobile.sdk.model.ArmbandInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * This fragment assumes there is an existing connection.
 */
public class ConnectedDeviceFragment extends Fragment {

    private static final Logger LOG = Logger.getInstance(ConnectedDeviceFragment.class);
    private static final String MAP_KEY_TITLE = "title";
    private static final String MAP_KEY_VALUE = "value";

    private ProgressBar progressBar;
    private SimpleAdapter armbandDataAdapter;

    /** If true then we currently are requesting data from armband. So show progress bar and disable refresh button. */
    private boolean loadingData = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(getResources().getString(R.string.connected_device));
        // we have refresh button in menu.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.connected_device, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        checkArmbandReadInfo();
    }

    private void updateDisplay() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(loadingData || !SenseWearApplication.get().isConnected() ? ProgressBar.VISIBLE : ProgressBar.GONE);
                ListView listView = (ListView) getView().findViewById(R.id.deviceStatusListView);
                listView.setAdapter(ConnectedDeviceFragment.this.armbandDataAdapter);
                invalidateMenu();
            }
        });
    }

    /**
     * Reads armband info if it is in NORMAL mode, else shows simplified device description.
     */
    private void checkArmbandReadInfo() {
        Armband armband = SenseWearApplication.get().getArmband();
        if (armband != null && armband.getArmbandMode() == Armband.ArmbandMode.NORMAL) {
            readArmbandInfo();
        } else {
            setArmbandInfo(getMaps(armband));
            updateDisplay();
        }
    }

    private void readArmbandInfo() {
        LOG.d("Sending request for armband info.");

        SenseWearApplication.get().getArmband().readArmbandInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArmbandInfo>() {

                    @Override
                    public void onCompleted() {
                        LOG.d("Receiveing armband info completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        LOG.e(e, "We received an error reading armband information. Armband:" + SenseWearApplication.get().getArmband());
                        LOG.i("We will disconnect, to be safe");
                        UIUtils.showToast(getActivity(), "Disconnecting from armband. Reason: Security is out of sync.\n  Please, manually connect again.");
                        SenseWearApplication.get().getArmbandManager().disconnect(SenseWearApplication.get().getArmband());

                        loadingData = false;
                        disconnectBand();
                    }

                    @Override
                    public void onNext(ArmbandInfo armbandInfo) {
                        LOG.d("Armband Info: " + armbandInfo);
                        // Fill adapter with the data.
                        setArmbandInfo(getMaps(armbandInfo));
                        updateDisplay();
                    }

                });

        loadingData = true;
    }

    /**
     * Closes connection to armband. MainActivity will handle the disconnection and close this fragment by itself.
     */
    private void disconnectBand() {
        ArmbandManager mgr = SenseWearApplication.get().getArmbandManager();
        MinuteRateFragment.stopStreamingFunctions();

        if (mgr.getArmband() != null && mgr.getArmband().isConnected()) {
            mgr.disconnect(mgr.getArmband());
        }
    }

    /**
     * Generates data that will be displayed in list.
     * @param armbandInfo   Armband information that will be displayed.
     * @return  List of map objects needed for armbandDataAdapter.
     */
    private List<Map<String, String>> getMaps(ArmbandInfo armbandInfo) {
        final List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        data.add(getMap("Serial Number", Long.toString(armbandInfo.getSerialNumber())));
        data.add(getMap("Battery", String.valueOf(armbandInfo.getBatteryStatus()) + "%"));
        data.add(getMap("Memory Status", String.valueOf(armbandInfo.getMemoryStatus()) + "%"));
        data.add(getMap("On body", String.valueOf(armbandInfo.isOnBody())));
        data.add(getMap("Configured", String.valueOf(armbandInfo.isConfigured())));
        data.add(getMap("Charging", String.valueOf(armbandInfo.isCharging())));
        data.add(getMap("Cradle Powered", String.valueOf(armbandInfo.isCradlePowered())));
        data.add(getMap("Device time", armbandInfo.getArmbandTimestamp().toString()));
        data.add(getMap("Next Timezone Change", armbandInfo.getNextTimezoneChange().toString() + ", or "
                + armbandInfo.getNextTimezoneChangeMinutes() + " minutes from Jan 1 2013."));
        data.add(getMap("Next Timezone Offset", String.valueOf(armbandInfo.getNextTimezoneOffset()) + " (minutes)"));
        data.add(getMap("Product Code", String.valueOf(armbandInfo.getProductCode())));
        data.add(getMap("Board Series", String.valueOf(armbandInfo.getBoardSeries())));
        data.add(getMap("Firmware", String.valueOf(armbandInfo.getFirmwareVersion())));
        data.add(getMap("Capabilities", String.valueOf(armbandInfo.getCapabilities())));
        data.add(getMap("Communications Protocol", String.valueOf(armbandInfo.getCommunicationsProtocol())));
        data.add(getMap("Memory Size", String.valueOf(armbandInfo.getMemorySize())));
        data.add(getMap("Device Mode", getArmbandModeText(SenseWearApplication.get().getArmband())));
        data.add(getMap("Bluetooth Address", SenseWearApplication.get().getArmband().getGeckoDevice().getMacAddress()));
        return data;
    }

    /**
     * If armband is not in NORMAL mode, we cannot read its info. So this method generates simplified info from device instance.
     * @param armband   Device instance.
     * @return  List of map objects needed for armbandDataAdapter.
     */
    private List<Map<String, String>> getMaps(Armband armband) {
        final List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        data.add(getMap("Serial Number", armband.getSerialNumber()));
        data.add(getMap("Firmware Version", armband.getFirmwareVersion()));
        data.add(getMap("Device Mode", getArmbandModeText(armband)));
        return data;
    }

    private Map<String, String> getMap(String title, String value) {
        Map<String, String> datum = new HashMap<String, String>(2);
        datum.put(MAP_KEY_TITLE, title);
        datum.put(MAP_KEY_VALUE, value);
        return datum;
    }

    private String getArmbandModeText(Armband armband) {
        switch (armband.getArmbandMode()) {
            case RECOVERY:
                return "Recovery (bootloader)";
            default:
                return "Normal";
        }
    }

    private void invalidateMenu() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().invalidateOptionsMenu();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.connected_device_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final boolean refreshEnabled = canRefreshArmbandInfo();
        final int drawable = refreshEnabled ? R.drawable.ic_action_reload : R.drawable.ic_action_reload_disabled;
        menu.findItem(R.id.action_refresh_info).setEnabled(refreshEnabled);
        menu.findItem(R.id.action_refresh_info).setIcon(getResources().getDrawable(drawable));
    }

    private boolean canRefreshArmbandInfo() {
        return !loadingData && SenseWearApplication.get().isConnected();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh_info:
                if (canRefreshArmbandInfo()) {
                    LOG.d("Refreshing armband info.");
                    setArmbandInfo(Collections.<Map<String, String>>emptyList());
                    invalidateMenu();
                    updateDisplay();
                    checkArmbandReadInfo();
                }
                break;
            case R.id.show_device_configuration:
                ((MainActivityBM) getActivity()).showFragment(new UserInfoFragment());
                break;
            case R.id.show_high_rate_screen:
                ((MainActivityBM) getActivity()).showFragment(new HighRateFragment());
                break;
            case R.id.show_minute_rate:
                ((MainActivityBM) getActivity()).showFragment(new MinuteRateFragment());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Fills adapter with the data.
     * @param values    Data that will be displayed. Map object should have keys: MAP_KEY_TITLE, MAP_KEY_VALUE.
     */
    private void setArmbandInfo(List<Map<String, String>> values) {
        loadingData = false;
        armbandDataAdapter = new SimpleAdapter(getActivity(), values,
                R.layout.listview_list_item_sub_item,
                new String[]{MAP_KEY_TITLE, MAP_KEY_VALUE},
                new int[]{R.id.text_item_title, R.id.text_item_subtitle});
    }
}
