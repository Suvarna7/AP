package com.empatica.sample.Server;

import android.os.AsyncTask;

import com.empatica.sample.BGService;
import com.empatica.sample.Database.IITDatabaseManager;
import com.empatica.sample.Database.StoringThread;
import com.empatica.sample.Timers.SendDataTimer;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * Created by Cat on 2/22/2017.
 */
public class simpleHttpResponderAsync extends AsyncHttpResponseHandler {

    public simpleHttpResponderAsync(){
        super();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        String cont = IITServerConnector.convertToString(responseBody);
        onSuccess(cont);

    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        String cont = IITServerConnector.convertToString(responseBody);
        onFailure(statusCode, error, cont);

    }

    //Handle succesful response
    public void onSuccess(String response) {
        System.out.println("Success response from server: "+response);
       // new AsyncTask<String, Integer, Long>() {
         //   @Override
           // protected Long doInBackground(String... response) {
                System.out.println("Success async response : "+response);

                try {

                    //Convert to a JSON Array and get the arguments
                    JSONArray arr = new JSONArray(response);
                    //List<String> args = new ArrayList();
                    //Analyze each JSON object
                    System.out.println("****** Start server updating");

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject jsonObj = (JSONObject) arr.get(i);


                        //Result comes from inserting new value
                        //Check updated value:
                        //It was correctly included in the server -> reset table
                        //IOMain.notSynchValues.clear();

                        //System.out.println("Received server time:"+(String) jsonObj.get("time_stamp"));

                        //TODO Update db !! delete the rows
                        //	dbManager.updateSyncStatus(databaseContext, (String) jsonObj.get("table_name"), IITDatabaseManager.upDateColumn, (String) jsonObj.get("updated"), (String) jsonObj.get("time_stamp"));

                        String last_update = (String) jsonObj.get(IITDatabaseManager.default_timeStampColumn);

                        //TODO Update database updated value
                        StoringThread.myDB.updateSyncStatus(BGService.empaticaMilTableName,
                                IITDatabaseManager.upDateColumn, (String) jsonObj.get(IITDatabaseManager.upDateColumn), last_update);
                        //Update last remote sent value
                        SendDataTimer.setLastUpdate(last_update);

                        //dbManager.updateSyncStatus(databaseContext, (String) jsonObj.get("table_name"),
                        //		IITDatabaseManager.syncColumn, (String) jsonObj.get("updated"), (String) jsonObj.get("time_stamp"));


                    }


                    System.out.println("****** End server updating");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("Sending ends!");
                IITServerConnector.sending = false;

                //return null;
            //}

           // protected void onPostExecute(Long result) {
                //Do nothing: just try and be async
                //System.out.println("Post sending");

           // }
        //}.execute(response);
    }

    //Handle failing response
    public void onFailure(int statusCode, Throwable error, String content) {

        System.out.println("Failed! server:" + statusCode);

        if (statusCode == 404) {
            System.out.println("Page not found");

        } else if (statusCode == 500) {
            System.out.println("Server failure");

        } else {
        }
        IITServerConnector.sending = false;



    }


}
