/**
 * Copyright (c) 2015, BodyMedia Inc. All Rights Reserved
 */

package com.sensors.mobile.app.BM.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bodymedia.android.utils.Logger;
import com.sensors.mobile.app.BM.SenseWearApplication;
import com.bodymedia.mobile.sdk.Armband;

public abstract class AbstractArmbandFragment extends Fragment {
    private static final Logger LOG = Logger.getInstance(AbstractArmbandFragment.class);

    protected Armband armband = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        armband = SenseWearApplication.get().getArmband();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        return createView(inflater, container, savedInstanceState);
    }

    protected abstract View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected boolean checkConnectedShowToast() {
        if (!SenseWearApplication.get().isConnected()) {
            UIUtils.showToast(this, "No armband connection found.");
            return false;
        }
        return true;
    }

}
