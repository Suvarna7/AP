/**
 * Copyright (c) 2015, BodyMedia Inc. All Rights Reserved
 */

package com.sensors.mobile.app.BM;

import android.app.Application;

import com.bodymedia.mobile.sdk.Armband;
import com.bodymedia.mobile.sdk.ArmbandManager;
import com.bodymedia.mobile.sdk.BMLEArmbandManager;
import com.bodymedia.mobile.sdk.ConnectionListener;
import com.bodymedia.mobile.sdk.JawboneDevice;
import com.bodymedia.mobile.sdk.model.ArmbandConfiguration;
import com.bodymedia.mobile.sdk.model.ArmbandInfo;
import com.bodymedia.mobile.sdk.task.ConnectionResult;

import com.sensors.mobile.app.R;


/**
 * Singleton created for easy access to armband manager and connected device.
 * To access singleton instance use {@link #get() get} method.
 */
public class SenseWearApplication extends Application implements ConnectionListener {
    
    //private static final Logger LOG = Logger.getInstance(SenseWearApplication.class);
    
    private static JawboneDevice mScanDevice = null;

    private ArmbandManager mArmbandManager;
    private Armband mArmband;
    
    private static SenseWearApplication mInstance;

    private ArmbandInfo cachedArmbandInfo;
    private ArmbandConfiguration cachedArmbandConfiguration;

    public SenseWearApplication() {
        mInstance = this;
    }
    
    public static SenseWearApplication get() {
        return mInstance;
    }
    
    public boolean isConnected() {
        return mArmband != null && mArmband.isConnected();
    }
    
    @Override
    public void onCreate() {
        super.onCreate();

        AppPrefs.init(this);

        mArmbandManager = new BMLEArmbandManager(this);
        mArmbandManager.addConnectionListener(this);
    }

    @Override
    public void connectionCompleted(ConnectionResult status) {
        System.out.println("SENSEWEAR APP: connectionCompleted");
        if (status.getStatus() == ConnectionResult.ConnectionStatus.BLUETOOTH_DISABLED) {
            return;
        }

        if (status.getArmband() != null) {
            mArmband = status.getArmband();
        }
    }
    
    @Override
    public void onDisconnect(Armband armband) {
        //TODO Adder line to disconnect armband
        mArmband = null;
    }

    public Armband getArmband() {
        return mArmband;
    }

    public ArmbandManager getArmbandManager() {
        return mArmbandManager;
    }
    
    public JawboneDevice getScanDevice() {
        return mScanDevice;
    }

    public void setScanDevice(JawboneDevice jawboneDevice) {
        mScanDevice = jawboneDevice;
    }

    public ArmbandInfo getCachedArmbandInfo() {
        return cachedArmbandInfo;
    }

    public void setCachedArmbandInfo(ArmbandInfo armbandInfo) {
        cachedArmbandInfo = armbandInfo;
    }

    public ArmbandConfiguration getCachedArmbandConfiguration() {
        return cachedArmbandConfiguration;
    }

    public void setCachedArmbandConfiguration(ArmbandConfiguration configuration) {
        cachedArmbandConfiguration = configuration;
    }

}
