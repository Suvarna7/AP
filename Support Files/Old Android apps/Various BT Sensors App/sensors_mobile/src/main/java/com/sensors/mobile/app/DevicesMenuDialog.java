package com.sensors.mobile.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.sensors.mobile.app.BM.MainActivityBM;

/**
 * Created by Caterina on 4/15/2015.
 */
public class DevicesMenuDialog extends DialogFragment {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.pick_device)
                    .setItems(R.array.devices_array, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            switch (which){
                                case 0:
                                    break;
                                case 1:
                                    //Open
                                    // changeActivity( MainActivityBM.class );
                                    break;
                                case 2:
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
            return builder.create();
        }


}

