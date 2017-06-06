package com.ece.ap.ap_aws;

import android.app.Application;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.models.nosql.FirstAwsDO;
import com.amazonaws.models.nosql.NotesDO;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * AWS DynamoDB to store data in tables
 * @author Caterina Lazaro
 * @version 1.0 May 2017
 */
public class AWSDatabaseManager {


    private final static String LOG_TAG = Application.class.getSimpleName();
    private final static String _USER_ID = "sample_user";
    private int userNumber ;
    // Fetch the default configured DynamoDB ObjectMapper
    private DynamoDBMapper dynamoDBMapper;
    // Table created in AWS cloud
    private FirstAwsDO first;
    private NotesDO notes;

    public AWSDatabaseManager(){
        dynamoDBMapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        first = new FirstAwsDO();
        notes = new NotesDO();
        userNumber = 0;
    }

    public void insertData() {

        // The userId has to be set to user's Cognito Identity Id for private / protected tables.
        // User's Cognito Identity Id can be fetched by using:
        // AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID()
        first = new FirstAwsDO();
        first.setUserId(_USER_ID+userNumber);
        userNumber ++;
        first.setInputSample("whatever_new");
        first.setOtherThings("another");
        //Timestamp
        double time = System.currentTimeMillis() * 1000;
        ///TODO DateTimeInstance dateTime = new DateTimeInstance();
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.[nnn]", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS", Locale.getDefault());
        String formatTimeStamp = dateFormat.format(time);
        first.setTimeStamp(formatTimeStamp);

        AmazonClientException lastException = null;

        try {
            dynamoDBMapper.save(first);

        } catch (final AmazonClientException ex) {
            Log.e(LOG_TAG, "Failed saving item : " + ex.getMessage(), ex);
            lastException = ex;
        }
    }
}
