package com.sensors.mobile.app.BM.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import com.sensors.mobile.app.R;

/**
 * Created by Caterina on 1/22/2016.
 *
 * Use it with:
 *       DialogFragment newFragment = new ConnectionAlertDialogFragment();
        newFragment.show(getFragmentManager(), "missiles");
 */
public class ConnectionAlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_message)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity

                    }
                });
        return builder.create();
    }
}
