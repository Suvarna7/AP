/**
 * Copyright (c) 2015, BodyMedia Inc. All Rights Reserved
 */

package com.sensors.mobile.app.BM.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * Utility methods for misc UI things.
 */
public final class UIUtils {

    private UIUtils() {
    }

    /**
     * Hides the device soft keyboard.
     *
     * @param activity the activity to hide the keyboard from.
     */
    public static void hideKeyboard(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Displays a toast on the screen for duration {@link Toast#LENGTH_LONG}.
     *
     * @param activity the activity the toast is being shown for.
     * @param message  the toast's message.
     */
    public static void showToast(final Activity activity, final String message) {
        if (activity == null) {
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Displays a toast on the screen for duration {@link Toast#LENGTH_LONG}.
     *
     * @param fragment the fragment displaying the toast.
     * @param message  the toast's message.
     */
    public static void showToast(final Fragment fragment, final String message) {
        if (fragment == null) {
            return;
        }
        showToast(fragment.getActivity(), message);
    }
}
