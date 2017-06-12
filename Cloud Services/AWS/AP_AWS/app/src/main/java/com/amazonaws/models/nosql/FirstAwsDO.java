package com.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "apmobilehub-mobilehub-1261206567-first_aws")

public class FirstAwsDO {
    private String _userId;
    private String _inputSample;9
    private String _otherThings;
    private String _timeStamp;

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBAttribute(attributeName = "userId")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBAttribute(attributeName = "input_sample")
    public String getInputSample() {
        return _inputSample;
    }

    public void setInputSample(final String _inputSample) {
        this._inputSample = _inputSample;
    }
    @DynamoDBAttribute(attributeName = "other_things")
    public String getOtherThings() {
        return _otherThings;
    }

    public void setOtherThings(final String _otherThings) {
        this._otherThings = _otherThings;
    }
    @DynamoDBAttribute(attributeName = "time_stamp")
    public String getTimeStamp() {
        return _timeStamp;
    }

    public void setTimeStamp(final String _timeStamp) {
        this._timeStamp = _timeStamp;
    }

}
