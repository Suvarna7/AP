package com.ece.ap.ap_aws;

import android.os.AsyncTask;

/**
 * Created by Cat on 5/31/2017.
 */
public class AWSThread  extends AsyncTask<String, String,String> {
    private AWSDatabaseManager myAWS;
    public AWSThread (AWSDatabaseManager aws){
        myAWS = aws;
    }
    @Override
    protected String doInBackground(String... urls) {
        myAWS.insertData();
        return null;
    }
}
