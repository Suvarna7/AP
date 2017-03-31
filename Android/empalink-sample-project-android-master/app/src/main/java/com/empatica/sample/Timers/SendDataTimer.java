package com.empatica.sample.Timers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.empatica.sample.BGService;
import com.empatica.sample.Database.IITDatabaseManager;
import com.empatica.sample.Database.StoringThread;
import com.empatica.sample.MainActivity;
import com.empatica.sample.Server.IITServerConnector;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Cat on 2/21/2017.
 */
public class SendDataTimer extends BasicTimer{

    private static final int SENDING_PERIOD = 2*60*1000; //1 min
    private static final int SENDING_AMOUNT = 2*IITDatabaseManager.ONE_SECOND_DATA;
    public static int MAX_READ_SAMPLES_UPDATE = 30*60* IITDatabaseManager.ONE_SECOND_DATA;

    private static final int DELETING_MARGIN = 10*60*(64+32+4+4+1); // 10 min
    private static final int DEL_AMOUNT = 3000;

    private MainActivity mainCtx;

    //IIT Server manager
    IITServerConnector myServerManager;
    private static final String jsonID = "empaticaJSON";

    private static String lastUpdate;

    public SendDataTimer(MainActivity ctx){
        super();
        mainCtx = ctx;
        //Send data to IIT
        myServerManager = new IITServerConnector(jsonID, IITServerConnector.IIT_SERVER_UPDATE_VALUES_URL,
                IITServerConnector.IIT_SERVER_READ_TABLE_URL);
        lastUpdate = "";
    }

    /**
     * Timer to automatically send data to server
     */

    @Override
    public void startTimer() {
        System.out.println("SEND TIMER is set");


        this.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //TODO
                //mHost.sendUSBmessage(USBHost._END_COMMAND);

                //if (mHost.connected) {
                if (ready) {
                    //Store values in database every minute and reset:

                    new AsyncTask<URL, Integer, Long>() {
                        @Override
                        protected Long doInBackground(URL... urls) {
                            System.out.println("SEND TIMER");
                            //Send saved information to IIT server
                            if (mainCtx.checkInternetConnectivity()) {

                                //Number of samples to read and update to server
                                int samplesToUpdate = BGService.storingManager.myDB.getNotCheckedValuesNumber(BGService.empaticaMilTableName,
                                        IITDatabaseManager.syncColumn, IITDatabaseManager.syncStatusYes, MAX_READ_SAMPLES_UPDATE);

                                //TODO Update last samples To Read
                                if (samplesToUpdate < MAX_READ_SAMPLES_UPDATE)
                                    updateDatabase(samplesToUpdate, DELETING_MARGIN);
                                else
                                    updateDatabase(MAX_READ_SAMPLES_UPDATE, DELETING_MARGIN);

                                //TODO DEBUG mock data to send to server
                               // myServerManager.debugSendToServer("sampling");
                                //TODO DEBUG Table
                                //MainActivity.myDB.updateDatabaseTable("debug_table", new ArrayList<>(Arrays.asList(new String[]{"'A'"})), true);
                            }
                            return null;
                        }

                        protected void onPostExecute(Long result) {
                            //Do nothing: just try and be async
                            //System.out.println("Post sending");

                        }
                    }.execute();


                }
            }
        }

                , SENDING_PERIOD, SENDING_PERIOD); // delay(seconds*1000), period(seconds*1000)

    }

      /*
     * Function to update local database values:
     * 1. Reads up to 20 minutes of syncrhonized samples
     * 2. Sends those values to the server
     * 3. Erase updated and synchronized samples (leaving a safety margin)
     */

    private boolean updateDatabase(int samples, int safetyMargin){
        //1. Erase all previously updated values
        //deleteUpdatedValues(safetyMargin);

        //2. Obtain not updated values from database
        List<Map<String, String>> listReadToServer = StoringThread.myDB.getNotCheckedValues (BGService.empaticaMilTableName, BGService.columnsTable,
                IITDatabaseManager.upDateColumn, IITDatabaseManager.updatedStatusNo, samples, false);

        //1. Obtain syncrhonized values from database to delete
        //List<Map<String, String>> listReadToServer =BGService.storingManager.myDB.getNotCheckedValues(BGService.empaticaMilTableName, BGService.columnsTable,
        // IITDatabaseManager.syncColumn, IITDatabaseManager.syncStatusYes, samples);


        //List<Map<String, String>> listReadToServer = new ArrayList<Map<String, String>>();
        //3. Send to Server
        if (listReadToServer != null) {

            System.out.println("Sync: "+samples +" vs Update: "+listReadToServer.size());

            //TODO SENDING TO SERVER
            List<Map<String, String>> temp = new ArrayList<Map<String, String>>();
            //List too long: break in smaller chunks
            for (int i = 0; i < listReadToServer.size(); i++) {
                Map<String, String> val = listReadToServer.get(i);
                val.put("table_name", BGService.empaticaMilTableName);
                temp.add(val);
                if ((i + 1) % SENDING_AMOUNT == 0) {
                //if ((i + 1) / SENDING_AMOUNT == 1) {
                        System.out.println("Send first packs: " + listReadToServer.size() + " vs " +temp.size());
                        String jSon = IITServerConnector.convertToJSON(temp);
                        myServerManager.sendToIIT(jSon, IITServerConnector.IIT_SERVER_UPDATE_VALUES_URL);

                        System.out.println("Send status: " + myServerManager.sending);
                        //DEBUG - Exit when we send one message
                        //TODO wait for a response
                        while (myServerManager.sending) {
                            //wait to receive something
                        }

                        /*try {
                             Thread.sleep(15000); // giving time to connect to wifi
                        } catch (Exception e) {
                             System.out.println("Exception while waiting to send:" + e);
                        }*/
                        temp = new ArrayList<Map<String, String>>();

                           /*
                        }
                    }
                                    if (temp.size() > 0) {
                                        //Send last values
                                        System.out.println("Send remainig");

                                        String jSon = IITServerConnector.convertToJSON(temp);
                                        myServerManager.sendToIIT(jSon, IITServerConnector.IIT_SERVER_UPDATE_VALUES_URL);

                                        /*try {
                                            Thread.sleep(2000); // giving time to connect to wifi
                                        } catch (Exception e) {
                                            System.out.println("Exception while waiting to send:" + e);
                                        }
                                    }*/
                        //myServerManager.debugServer("samples");
                    //}
                }
            }
            //Send remaining samples:
            System.out.println("Send last batch: " + listReadToServer.size());
            String jSon = IITServerConnector.convertToJSON(temp);
            myServerManager.sendToIIT(jSon, IITServerConnector.IIT_SERVER_UPDATE_VALUES_URL);

            System.out.println("Send status: " + myServerManager.sending);

            return true;
        } else
            return false;


    }

    private int deleteUpdatedValues(int safetyMargin){
        if (!lastUpdate.equals("")) {
            //1. Get number of samples to be erased -- UPDATED
            //Number of samples to read and update to server
            int samplesToDelete = BGService.storingManager.myDB.getNotCheckedValuesNumber(BGService.empaticaMilTableName,
                    IITDatabaseManager.upDateColumn , IITDatabaseManager.updatedStatusYes , MAX_READ_SAMPLES_UPDATE);

            int deleting = samplesToDelete - safetyMargin;

            boolean del = false;
            //To avoid memory errors, we use deleting margin
            if (deleting > 0) {
                while (deleting > DEL_AMOUNT) {
                    del = StoringThread.myDB.deleteNRowsFromTable(BGService.empaticaMilTableName,
                            BGService.columnsTable[0], DEL_AMOUNT);
                    if (del) {
                        deleting = deleting - DEL_AMOUNT;
                    } else {
                        System.out.println("Not erased");
                    }
                }
                //Finally deleting amount
                StoringThread.myDB.deleteNRowsFromTable(BGService.empaticaMilTableName,
                        BGService.columnsTable[0], deleting);
                return samplesToDelete - safetyMargin;
            } else
                return 0;
        }else{
            return 0;
        }


    }


    public static void setLastUpdate (String time_stamp){
        lastUpdate = time_stamp;
    }



}
