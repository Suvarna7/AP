package com.sensors.mobile.app.BM;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;


/**
 * Created by Caterina on 1/26/2016.
 */
public class ConnectionDialog  extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.setTitle("BodyMedia");
        alertDialog.setMessage("Check the connection");
        alertDialog.setCancelable(true);
        //Stop the timer
        BGService.notConnectedTimer.cancel();
        //Stop the BG_Service - without connection is not doing anything
        stopService(new Intent(this, BGService.class));
        BGService.startTimer = false;


        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //Reset timer
                BGService.startTimer = true;

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