package com.ece.ap.awsbackup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ece.ap.awsbackup.user.IdentityManager;

public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Application.onCreate - Initializing application...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeApplication();
        Log.d(LOG_TAG, "Application.onCreate - Application initialized OK");
    }

    private void initializeApplication() {

        // Initialize the AWS Mobile Client
        AWSMobileClient.initializeMobileClientIfNecessary(getApplicationContext());

        // ... Put any application-specific initialization logic here ...
        AWSMobileClient.defaultMobileClient().getIdentityManager().getUserID(new IdentityManager.IdentityHandler() {

            @Override
            public void handleIdentityID(String identityId) {

                // User's identity retrieved. You can use the identityId value
                // to uniquely identify the user.
                System.out.println("User retreived! "+identityId);

            }

            @Override
            public void handleError(Exception exception) {

                // We failed to retrieve the user's identity. Set unknown user identifier
                // in text view. Perhaps there was no network access available.

                // ... add error handling logic here ...
                System.out.println("Error retreived! "+exception);

            }
        });

    }


}
