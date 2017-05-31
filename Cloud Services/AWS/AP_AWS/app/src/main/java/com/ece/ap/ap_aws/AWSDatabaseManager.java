package com.ece.ap.ap_aws;

import android.app.Application;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.models.nosql.FirstAwsDO;

/**
 * AWS DynamoDB to store data in tables
 * @author Caterina Lazaro
 * @version 1.0 May 2017
 */
public class AWSDatabaseManager {


    private final static String LOG_TAG = Application.class.getSimpleName();
    private final static String _USER_ID = "sample_user";
    // Fetch the default configured DynamoDB ObjectMapper
    private DynamoDBMapper dynamoDBMapper;
    // Table created in AWS cloud
    private FirstAwsDO first;


    public AWSDatabaseManager(){
        dynamoDBMapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        first = new FirstAwsDO();
    }

    public void insertData() {

        // The userId has to be set to user's Cognito Identity Id for private / protected tables.
        // User's Cognito Identity Id can be fetched by using:
        // AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID()
        first.setUserId(_USER_ID);
        AmazonClientException lastException = null;

        try {
            dynamoDBMapper.save(first);

        } catch (final AmazonClientException ex) {
            Log.e(LOG_TAG, "Failed saving item : " + ex.getMessage(), ex);
            lastException = ex;
        }
    }
}
