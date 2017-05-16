/**
 * Copyright (c) 2015, BodyMedia Inc. All Rights Reserved
 */

package com.sensors.mobile.app.BM;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.bodymedia.android.utils.Logger;
import com.bodymedia.utils.ByteUtils;

import java.util.ArrayList;
import java.util.Map;

/**
 * Singleton class to manage application's SharedPreferences. Main function is to save paired devices and paired keys for connection.
 */
public class AppPrefs {
    private static final Logger LOG = Logger.getInstance(AppPrefs.class);

    private static AppPrefs instance = null;
    private final Context appContext;
    private final String keyPrefix = "pk.";
    private static final String ECG_STATUS = "ECGstatus";

    private AppPrefs(Context appContext) {
        this.appContext = appContext;
    }

    public static void init(Context appContext) {
        instance = new AppPrefs(appContext);
    }

    public static AppPrefs getInstance() {
        return instance;
    }

    /**
     * Stores pairing key (in SharedPreferences) for device with the given serial number.
     * @param serialNumber  Device's serial number will be used as a key in preferences file.
     * @param pairingKey    Pairing key for device. It can be used to connect with device.
     */
    public void storePairingKey(String serialNumber, byte[] pairingKey) {
        SharedPreferences.Editor editor = getPrefs().edit();
        String base64 = Base64.encodeToString(pairingKey, 0);
        editor.putString(keyPrefix + serialNumber, base64);
        editor.commit();
        LOG.d(String.format("Stored pairing key %s for serial number %s.", base64, serialNumber));
    }

    /**
     * Loads saved pairing key for device with a given serial number.
     * @param serialNumber
     * @return  Returns a pairing key that is needed to connect to a device with the given serial number.
     */
    public byte[] loadPairingKey(String serialNumber) {
        String pairingKey = getPrefs().getString(keyPrefix + serialNumber, null);
        if (pairingKey != null) {
            byte[] bPairingKey = Base64.decode(pairingKey, 0);

            LOG.d(String.format("Loaded pairing key for serial %s: %s",
                                serialNumber,
                                ByteUtils.toHex(bPairingKey)));

            return bPairingKey;
        }
        return null;
    }

    /**
     * Removes a device with the given serial number from the preferences file.
     * @param serialNumber
     */
    public void clearDevice(String serialNumber) {
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.remove(keyPrefix + serialNumber);
        editor.commit();
    }

    /**
     * @return  Returns a list of devices that was paired and saved to preferences file.
     */
    public ArrayList<PairedDevice> getPairedDeviceList() {
        ArrayList<PairedDevice> list = new ArrayList<PairedDevice>();

        Map <String, ?> all = getPrefs().getAll();
        for (String key : all.keySet()) {
            if ( key.startsWith(keyPrefix)) {
                String serialNumber = key.replaceFirst(keyPrefix, "");
                String pairingKey = (String) all.get(key);
                list.add(new PairedDevice(serialNumber, Base64.decode(pairingKey, 0)));
            }
        }

        return list;
    }

    private SharedPreferences getPrefs() {
        return appContext.getSharedPreferences("gecko-demo-client", Context.MODE_PRIVATE);
    }

    public void setECGstatus(boolean status){
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putBoolean(ECG_STATUS, status).commit();
    }

    public boolean getECGstatus(){
        return getPrefs().getBoolean(ECG_STATUS, false);
    }

}
