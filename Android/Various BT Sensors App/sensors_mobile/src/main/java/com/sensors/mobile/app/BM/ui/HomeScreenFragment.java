/**
 * Copyright (c) 2015, BodyMedia Inc. All Rights Reserved
 */

package com.sensors.mobile.app.BM.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bodymedia.android.utils.Logger;
import com.sensors.mobile.app.BM.AppPrefs;
import com.sensors.mobile.app.BM.PairedDevice;
import com.sensors.mobile.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that shows all devices that was paired before and saved to preferences file.
 * From here we can open PairFragment and ScanDevicesFragment.
 *
 * Created by handerson on 7/26/13.
 */
public class HomeScreenFragment extends Fragment {

    private static final Logger LOG = Logger.getInstance(HomeScreenFragment.class);

    public static final String ACTION_CONNECT_BY_SERIAL = "com.bodymedia.mobile.testapp.BM.ui.CONNECT_BY_SERIAL";
    public static final String KEY_SERIAL_NO = "com.bodymedia.mobile.testapp.BM.ui.SERIAL_NO";

    private Activity mActivity;
    private PairedDeviceAdapter deviceAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(getString(R.string.select_cached_device));
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);

        final ListView listview = (ListView) view.findViewById(R.id.knownDeviceListView);

        deviceAdapter = new PairedDeviceAdapter(
                getActivity().getApplicationContext(),
                new ArrayList<PairedDevice>());
        listview.setAdapter(deviceAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                // MainActivity will handle the connection. We just need to say to what device to connect.
                final PairedDevice armband = (PairedDevice) parent.getItemAtPosition(position);
                Intent intent = new Intent(ACTION_CONNECT_BY_SERIAL);
                intent.putExtra(KEY_SERIAL_NO, armband.getSerialNumber());
                getActivity().getApplication().sendBroadcast(intent);
            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view,
                                           int position, long id) {
                clearPairedDevice(position);
                return true;
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.device_list_menu, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity = getActivity();
        loadCachedDeviceList();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Loads saved devices from preferences and shows them via List.
     */
    private void loadCachedDeviceList() {
        List<PairedDevice> deviceList = AppPrefs.getInstance().getPairedDeviceList();
        deviceAdapter.clear();
        for (PairedDevice device : deviceList) {
            deviceAdapter.add(device);
        }

        if (deviceList.size() <= 0) {
            // if we do not have any saved devices then we should show pairing screen.
            showPairing();
        }
    }

    protected void clearPairedDevice(int position) {
        final int deletePosition = position;

        AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);

        alert.setMessage(R.string.remove_paired);
        alert.setPositiveButton(R.string.command_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user click YES, so we removing device from adapter and preferences file
                PairedDevice paired = deviceAdapter.getItem(deletePosition);
                AppPrefs.getInstance().clearDevice(paired.getSerialNumber());

                deviceAdapter.remove(paired);
                deviceAdapter.notifyDataSetChanged();
                deviceAdapter.notifyDataSetInvalidated();
                loadCachedDeviceList();
            }
        });

        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_pair_device:
            showPairing();
            return true;
        default:
            break;
        }
        return false;
    }

    private void showPairing() {
        showFragment(new PairFragment());
    }

    private void showFragment(Fragment fragment) {
        try {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (IllegalStateException ise) {
            LOG.w(ise, "Unable to show fragment due to an illegal state exception.");
        }
    }
}
