/**
 * Copyright (c) 2015, BodyMedia Inc. All Rights Reserved
 */

package com.sensors.mobile.app.BM.ui;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bodymedia.android.utils.Logger;
import com.bodymedia.btle.toolkit.SecurityEngine;
import com.bodymedia.mobile.sdk.Armband;
import com.bodymedia.mobile.sdk.ArmbandManager;
import com.bodymedia.mobile.sdk.ConnectionListener;
import com.bodymedia.mobile.sdk.GeckoDevice;
import com.bodymedia.mobile.sdk.listener.PairingListener;
import com.bodymedia.mobile.sdk.task.ConnectionResult;
import com.sensors.mobile.app.BM.AppPrefs;
import com.sensors.mobile.app.R;
import com.sensors.mobile.app.BM.SenseWearApplication;
import com.bodymedia.utils.ByteUtils;

public class PairFragment extends Fragment {

    private static final long SCAN_TIMEOUT = 3500;
    private static final long PAIRING_TIMEOUT = 30000;

    private static final Logger LOG = Logger.getInstance(PairFragment.class);
    private ArmbandManager armbandManager = SenseWearApplication.get().getArmbandManager();
    private ProgressBar progressBar;
    private byte[] pairingKey;
    private TextView pairProgressTextView;
    private BroadcastReceiver bluetoothStateReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = getResources().getString(R.string.pairing_press_registration);
        getActivity().setTitle(title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pair_armband, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.pairArmbandProgressBar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        pairProgressTextView = (TextView) view.findViewById(R.id.pairProgressTextView);

        // if there is connected armband we should perform disconnection
        if (armbandManager != null && SenseWearApplication.get().getArmband() != null) {
            armbandManager.disconnect(SenseWearApplication.get().getArmband());
            UIUtils.showToast(getActivity(), getString(R.string.armband_disconnected_during_pairing));
        }

        startPairingProcess();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        armbandManager.setPairingListener(pairListener);
        armbandManager.addConnectionListener(connectionListener);
        setUpBluetoothStateReceiver();
    }

    @Override
    public void onStop() {
        super.onStop();
        armbandManager.stopScan();
        armbandManager.clearPairingListener();
        armbandManager.removeConnectionListener(connectionListener);
        shutDownBluetoothStateReceiver();
    }

    private void startPairingProcess() {
        LOG.d("Submitting a generated pairing key.");
        pairingKey = SecurityEngine.generatePairingKey();
        // update UI
        onPairStarted();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!armbandManager.isBluetoothEnabled()) {
                    hideProgressBar();

                    // ask the user to turn on Bluetooth
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(discoverableIntent);
                } else {
                    // try to connect with an armband in pairing mode
                    armbandManager.setPairingListener(pairListener);
                    armbandManager.pairWithClosestArmband(pairingKey, SCAN_TIMEOUT, PAIRING_TIMEOUT);
                }
            }
        }).start();
    }

    private void onPairStarted() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pairProgressTextView.setText(R.string.pair_progress_scanning);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideProgressBar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private final ConnectionListener connectionListener = new ConnectionListener() {
        @Override
        public void connectionCompleted(ConnectionResult status) {
            if (status.getStatus() == ConnectionResult.ConnectionStatus.SUCCESS) {
                // Device connected successfully. We just showing the toast.
                // MainActivity will receive message about connection (connectionCompleted)
                // and show correct screen by itself.

                UIUtils.showToast(getActivity(), getString(R.string.pair_successfull));
                if (status.getArmband() != null && pairingKey != null) {
                    LOG.d("Caching the pairing key for future connections: " + ByteUtils.toHex(pairingKey));
                    AppPrefs.getInstance().storePairingKey(status.getArmband().getSerialNumber(), pairingKey);
                    pairingKey = null;
                }
            }
        }

        @Override
        public void onDisconnect(Armband armband) {
            UIUtils.showToast(getActivity(), getString(R.string.armband_disconnected));
        }
    };

    private final PairingListener pairListener = new PairingListener() {
        @Override
        public void onDeviceFoundInPairingMode(GeckoDevice geckoDevice) {
            // User should press button on armband to confirm the pairing
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pairProgressTextView.setText(R.string.pair_progress_confirm);
                }
            });
            hideProgressBar();
        }

        @Override
        public void noDeviceFound() {
            startPairingProcess();
        }

        @Override
        public void confirmationTimeout(GeckoDevice geckoDevice) {
            startPairingProcess();
        }
    };

    private void setUpBluetoothStateReceiver() {
        bluetoothStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                    startPairingProcess();
                }
            }

        };
        getActivity().registerReceiver(bluetoothStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    private void shutDownBluetoothStateReceiver() {
        if (bluetoothStateReceiver != null) {
            getActivity().unregisterReceiver(bluetoothStateReceiver);
            bluetoothStateReceiver = null;
        }
    }
}
