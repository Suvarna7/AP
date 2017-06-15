package com.ece.ap.ap_aws;

import android.app.Application;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.models.nosql.FirstAwsDO;
import com.amazonaws.models.nosql.NotesDO;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * AWS DynamoDB to store data in tables
 * AWS code samples: https://github.com/awslabs/aws-dynamodb-examples/tree/master/src/main/java/com/amazonaws
 * @author Caterina Lazaro
 * @version 1.0 May 2017
 */
public class AWSDatabaseManager {


    private final static String LOG_TAG = Application.class.getSimpleName();
    private final static String _USER_ID = "sample_user";
    private int userNumber ;
    // Fetch the default configured DynamoDB ObjectMapper
    private DynamoDBMapper dynamoDBMapper;
    static DynamoDB dynamoDB = new DynamoDB(new AmazonDynamoDBClient(
            new ProfileCredentialsProvider()));
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
        //first = new FirstAwsDO();
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
        System.out.println("Updated AWSFirst");

        try {
            dynamoDBMapper.save(first);

        } catch (final AmazonClientException ex) {
            Log.e(LOG_TAG, "Failed saving item : " + ex.getMessage(), ex);
            lastException = ex;
        }
    }

    public void readData(){

    }

    private static void deleteTable(String tableName) {
        Table table = dynamoDB.getTable(tableName);
        try {
            System.out.println("Issuing DeleteTable request for " + tableName);
            table.delete();
            System.out.println("Waiting for " + tableName
                    + " to be deleted...this may take a while...");
            table.waitForDelete();

        } catch (Exception e) {
            System.err.println("DeleteTable request failed for " + tableName);
            System.err.println(e.getMessage());
        }
    }

    /**
     * Create a new table with only hasKey
     * @param tableName
     * @param readCapacityUnits
     * @param writeCapacityUnits
     * @param hashKeyName
     * @param hashKeyType
     */
    public static void createTable(
            String tableName, long readCapacityUnits, long writeCapacityUnits,
            String hashKeyName, String hashKeyType) {

        createTable(tableName, readCapacityUnits, writeCapacityUnits,
                hashKeyName, hashKeyType, null, null);
    }
    /**
     * Create a new table in the given database, with HashKey and rangeKey
     * @param tableName
     * @param readCapacityUnits
     * @param writeCapacityUnits
     * @param hashKeyName
     * @param hashKeyType
     * @param rangeKeyName
     * @param rangeKeyType
     */

    public static void createTable(
            String tableName, long readCapacityUnits, long writeCapacityUnits,
            String hashKeyName, String hashKeyType,
            String rangeKeyName, String rangeKeyType) {

        try {

            ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
            keySchema.add(new KeySchemaElement()
                    .withAttributeName(hashKeyName)
                    .withKeyType(KeyType.HASH));

            ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
            attributeDefinitions.add(new AttributeDefinition()
                    .withAttributeName(hashKeyName)
                    .withAttributeType(hashKeyType));

            if (rangeKeyName != null) {
                keySchema.add(new KeySchemaElement()
                        .withAttributeName(rangeKeyName)
                        .withKeyType(KeyType.RANGE));
                attributeDefinitions.add(new AttributeDefinition()
                        .withAttributeName(rangeKeyName)
                        .withAttributeType(rangeKeyType));
            }

            CreateTableRequest request = new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(keySchema)
                    .withProvisionedThroughput( new ProvisionedThroughput()
                            .withReadCapacityUnits(readCapacityUnits)
                            .withWriteCapacityUnits(writeCapacityUnits));


            request.setAttributeDefinitions(attributeDefinitions);

            System.out.println("Issuing CreateTable request for " + tableName);
            Table table = dynamoDB.createTable(request);
            System.out.println("Waiting for " + tableName
                    + " to be created...this may take a while...");
            table.waitForActive();

        } catch (Exception e) {
            System.err.println("CreateTable request failed for " + tableName);
            System.err.println(e.getMessage());
        }
    }

}
