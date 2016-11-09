package com.empatica.sample.Database;

import android.content.Context;
import com.empatica.sample.BGService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Caterina Lazaro
 * @version 1.0 Nov 2016

 */
public class StoringThread implements Runnable {

    //Database
    public static IITDatabaseManager myDB;
    public static IITDatabaseManager tempDB;
    public static final String empaticaTableName= "empatica_table";
    private static final String TEMP_DB_NAME = "empaticaTempDB.db";

    public static final String[] empaticaColumnsTable = new String[]{"time_stamp", "Acc_x", "Acc_y", "Acc_z", "GSR", "BVP",
            "IBI", "HR", "temperature","battery_level"};
    private static int STORING_THRESHOLD = 2000;

    private static boolean running;



    public StoringThread(Context ctx){
        //Create the database:
        initDatabaseManagers(ctx);

        running = false;
    }

    @Override
    public void run(){
        running = true;
        System.out.println("Start storing: "+Thread.currentThread().getName());
        BGService.ackInProgress = true;
        tempDatabaseToPermanent();
        BGService.ackInProgress = false;
        //End current thread
        running = false;
        Thread.currentThread().interrupt();
    }

        /* **********************************************
     * DATABASE METHODS
     */

    /**
     * Initialize database paraemeters: creates database instance and a table for empatica
     * @param context app context
     */
    private static void initDatabaseManagers(Context context){
        //Create database object
        myDB = new IITDatabaseManager(context);

        //DELETE PREVIOUS TABLE:
        //myDB.updateDatabaseTable (empaticaTableName, null, false);

        //Create a table for Empatica
        myDB.createTable(empaticaTableName, empaticaColumnsTable[0], new ArrayList<>(Arrays.asList(empaticaColumnsTable)));
        // myDB.createTable(empaticaSecTableName, columnsTable[0], new ArrayList<>(Arrays.asList(columnsTable)));

        //DEBUG TABLE:
        //myDB.createTable("debug_table", new ArrayList<>(Arrays.asList(new String[]{"column"})));

        //Temp databasae
        tempDB = new IITDatabaseManager(context, TEMP_DB_NAME);
        //tempDB.deleteTableFromDatabase(empaticaTableName);
        //Create table
        tempDB.createTable(empaticaTableName, empaticaColumnsTable[0], new ArrayList<>(Arrays.asList(empaticaColumnsTable)));
        //In case there are any previous samples
        tempDB.deleteAllRows(empaticaTableName);



    }

    /**
     * Store a single sample in the database
     * @param sample
     * @return
     */
    public static void storeSampleInTempDatabase(ThreadSafeArrayList<String> sample, String table, String[] columns, List<ThreadSafeArrayList<String>> error){
        try{
            if(!tempDB.insertIntoDatabaseTable(table, sample, columns)) {
                //TODO when could not insert

            }
        }catch (Exception e) {
            System.out.println("Empatica store sample exception " + e);
            //Save sample! in global list

            BGService.ackInProgress = false;

        }
    }

    /**
     * Store a single sample in the database
     * @param sample
     * @return
     */
    public void storeSampleInPermanentDatabase(ThreadSafeArrayList<String> sample, String table, String[] columns, List<ThreadSafeArrayList<String>> error){
        try{
            if(!myDB.insertIntoDatabaseTable(table, sample, columns)) {
                //TODO when could not insert

            }
        }catch (Exception e) {
            System.out.println("Empatica store sample exception " + e);
            //Save sample! in global list
            if (error != null)
                error.add(sample);

            BGService.ackInProgress = false;

        }
    }


    private void tempDatabaseToPermanent(){
        //Read temp database
        List<ThreadSafeArrayList<String>> tempValues = new ArrayList<ThreadSafeArrayList<String>>();
        tempValues = tempDB.readAllValuesFromTable(empaticaTableName);


        //Store if there are enough samples
        if (tempValues.size() > STORING_THRESHOLD) {
            int n_samples=  tempValues.size();

            //Start moving values to permanent
            while (tempValues.size() > 0)
                tempValues = storeGlobalListInDatabase(tempValues);

            //Delete temp table
            tempDB.deleteNRowsFromTable(empaticaTableName, n_samples);
        }
        //Or wait till database is filled and read again
        else{
            try {
                Thread.currentThread().sleep(10000);

                tempValues = tempDB.readAllValuesFromTable(empaticaTableName);
                int n_samples = tempValues.size();

                //Start moving values to permanent
                while (tempValues.size() > 0)
                    tempValues = storeGlobalListInDatabase(tempValues);

                //Delete temp table
                tempDB.deleteNRowsFromTable(empaticaTableName, n_samples);

            }catch  (InterruptedException e) {
                e.printStackTrace();
            }


        }

    }

    /**
     * Store all values accumulated in the global list
     * @return true if done with storing
     */

    private List<ThreadSafeArrayList<String>> storeGlobalListInDatabase( List<ThreadSafeArrayList<String> > valuesToUpdate ){

        List<ThreadSafeArrayList<String>> error = new ArrayList<ThreadSafeArrayList<String>> ();
        System.out.println("Store temp values: "+valuesToUpdate.get(0).get(0));

        for (int i = 0; i < valuesToUpdate.size(); i++) {

            try {
                //System.out.println("Sample in: " + i);
                //myDB.updateDatabaseTable(empaticaMilTableName, valuesToUpdate.get(i), true);
                storeSampleInPermanentDatabase(valuesToUpdate.get(i), empaticaTableName, empaticaColumnsTable, error);
            } catch (Exception e) {
                System.out.println("Store global exception " + e);
                //Reduce i, to try again to save the sample
                error.add(valuesToUpdate.get(i));
            }
        }

        return error;

    }


    public static boolean shouldStartTransaction(){
        return (!running);
    }


}
