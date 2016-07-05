package com.empatica.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;


/**
 * ConenctionDialog - Dialog alert fragment to be shown when connection with the device is lost
 * It should set up the MainApplication so that, when going back, the device can be reconnected
 * @autor Caterina Lazaro
 * @version 2.0 Jun 2016
 */
public class ConnectionDialog extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.setTitle("Empatica");
        alertDialog.setMessage("Check device connection");
        alertDialog.setCancelable(true);
        //Stop timer till the dialog is cancelled:
        BGService.notConnectedTimer.cancel();
        //Stop the BG_Service - without connection is not doing anything
        stopService(new Intent(this, BGService.class));
        BGService.startTimer = false;


        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //Reset timer
                BGService.startTimer = true;

                //Connect button visible
                MainActivity.connectButton.setVisibility(View.VISIBLE);

                //Start service  again
                startService(new Intent(getApplicationContext(), BGService.class));

                //Finish the activity
                finish();
            }

    });
        //alertDialog.setIcon(R.drawable.icon);

        alertDialog.show();


    }
}