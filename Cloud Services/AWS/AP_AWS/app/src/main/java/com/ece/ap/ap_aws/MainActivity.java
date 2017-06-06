package com.ece.ap.ap_aws;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amazonaws.mobile.AWSMobileClient;

public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = Application.class.getSimpleName();
    private AWSDatabaseManager myAWS ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "Application.onCreate - Initializing application...");
        super.onCreate(savedInstanceState);
        initializeApplication();
        Log.d(LOG_TAG, "Application.onCreate - Application initialized OK");
    }

    private void initializeApplication() {

        // Initialize the AWS Mobile Client
        AWSMobileClient.initializeMobileClientIfNecessary(getApplicationContext());

        // ... Put any application-specific initialization logic here ...
        myAWS = new AWSDatabaseManager();
        AWSThread awsThread = new AWSThread(myAWS);
        awsThread.execute();
    }

    public void sendMessage(View view){
        myAWS = new AWSDatabaseManager();
        AWSThread awsThread = new AWSThread(myAWS);
        awsThread.execute();

    }
}
