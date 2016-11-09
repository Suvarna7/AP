package com.empatica.sample.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;

import com.empatica.sample.BGService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IITSensorsDatabaseManager - manage connection to our SQLite database
 * I can create new tables, erase table, update its connects
 * Includes function to generate JSONs (that can later be send)
 *
 * NOTE: Contents on database should be of type 'value1', 'value2'...
 *
 * SENSOR TABLES:
 *
 *  1. Bodymedia: columns{"time_stamp", "activity_type","heart_rate",
 "longitudinal_accel",  "lateral_accel", "transverse_accel", "long_accel_peak", "lat_accel_peak",
 "tran_accel_peak", "long_accel_avg", "lat_accel_avg", "tran_accel_avg", "long_accel_mad",
 "lat_accel_mad", "tran_accel_mad", "gsr", "gsr_avg", "skin_temp",  "skin_temp_avg", "cover_temp",
 "heat_flux_avg", "steps", "sleep", "calories", "vigorous",
 "METs", "memory", "battery"}

 2. Empatica: columns {"time_stamp", "Acc_x", "Acc_y", "Acc_z", "GSR", "BVP",
 "IBI", "temperature","battery_level"}
 *
 * @autor Caterina Lazaro
 * @version 2.0 Jun 2016
 */

public class IITDatabaseManager {

    private static final String TAG = "IIT_DATABASE";
    private MyDatabaseHelper dbHelper;
    //private static SQLiteDatabase db;
    private static Cursor cursorSync;
    private static int CURSOR_WINDOW_LIMIT = 2000;


    //Database info
    private static String EXTERNAL_DIRECTORY_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    //private static String DB_LOCAL_URL =  "/storage/emulated/legacy/IIT_database/";
    static String DB_LOCAL_URL =  EXTERNAL_DIRECTORY_PATH+"/IIT_database/";
    public static String DEFAULT_DB_NAME = "dbSensors.db";
    public static String DEFAULT_DB_NAME_NE = "dbSensors";


    private String db_name;

    static String databaseFile;
    //Time_Stamp column name:
    private static String timeStampColumn;
    //Updated : for the server
    public static final String updatedStatusNo = "n";
    public static final String updatedStatusYes = "y";
    public static final String upDateColumn = "updated";
    //Syncrhonized : for the internal database and USB
    public static final String syncStatusNo = "n";
    public static final String syncStatusYes = "y";
    public static final String syncColumn = "synchronized";
    //static String  DBPath ;
    // static String DBName ;
    //static File db_file;
    //static String databaseFile = "/sdcard/Android/data/com.ece.NewApp/ZephyrDB";
    //static File sdcard = Environment.getExternalStorageDirectory();
    //static String databaseFile = sdcard.getAbsolutePath() + File.separator+ "external_sd" + File.separator + "ZephyrDB" ;
    //static String databaseFile = sdcard.getAbsolutePath() + File.separator + "ZephyrDB" ;

    //Context
    private static Context dbContext;

    private static boolean initialized = false;
    //1 second = 64 samples
    //Sending every 30 seconds
    // 1 * 64 * 60 = 3840
    public static int MAX_READ_SAMPLES_UPDATE = 5*64*60;

    //1 second = 64 samples
    //Sending every 30 seconds
    //   5 * 64 * 60 = 19200
    public static int MAX_READ_SAMPLES_SYNCHRONIZE = 5*64*60;


    /**
     * Default constractor, only needs context
     * @param ctx context
     */
    public IITDatabaseManager(Context ctx){
        //Update database file:
        db_name = DEFAULT_DB_NAME;
        databaseFile = DB_LOCAL_URL + db_name;
        dbHelper = new MyDatabaseHelper(ctx, databaseFile);

        initDatabase(ctx);
    }

    /**
     * Constructor to set up the name of the database
     * @param ctx context
     * name of the database
     */
    public IITDatabaseManager(Context ctx, String databaseName){
        //Update database file:
        db_name = databaseName;
        databaseFile = DB_LOCAL_URL + db_name;
        dbHelper = new MyDatabaseHelper(ctx, databaseFile);

        initDatabase(ctx);

    }


    /*
    * initDatabase ()
     */
    private void initDatabase(Context ctx) {
        dbContext = ctx;


        //Create database:
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            //Enable Wrtie Ahead Logging (WAL)
            db.enableWriteAheadLogging();

            //Test db
            db.execSQL("CREATE TABLE IF NOT EXISTS sample_table (created CHAR(1))");
            db.setTransactionSuccessful();
        } catch(Exception e){
            System.out.println("Error creating table: " + e);
        } finally{
            if (db.inTransaction())
                db.endTransaction();
        }
        initialized= true;

    }

    /**
     * Insert values using MyDatabaseHelper
     * @param table
     * @param values
     * @param columns
     * @return
     */
    public boolean insertIntoDatabaseTable(String table, ThreadSafeArrayList<String> values, String[] columns){
        try{
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            try{
                ContentValues update = new ContentValues();
                for (int i =0; i < columns.length; i++){
                    update.put(columns[i], values.get(i));
                }
                //Add sync
                update.put(syncColumn, syncStatusNo);
                //Add update
                update.put(upDateColumn, updatedStatusNo);

                long result = db.insert(table, null, update);

                db.setTransactionSuccessful();

            }catch(Exception e){
                Log.d(TAG, "Error storing new sample in "+table +": "+e);
            }finally {
                if (db.inTransaction())
                    db.endTransaction();
            }
            return true;
        }catch (Exception e){
            System.out.println("Exception opening writable database to store: " + e);
            return false;

        }
    }

    public void deleteTableFromDatabase(String table){
        try{
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            try{
                String updateQuery = "DROP TABLE " + table;
                db.execSQL(updateQuery);

            }catch(Exception e){
                Log.d(TAG, "Error storing new sample in "+table +": "+e);
            }finally {
                if (db.inTransaction())
                    db.endTransaction();
            }
        }catch (Exception e){
            System.out.println("Exception opening writable database to store: " + e);

        }

    }


    /**
     * Dlete all rows of given table
     * @param table  table name
     * @return
     */
    public boolean deleteAllRows(String table){
        try{
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            try{
                //Delete all rows
                db.delete(table, null, null);
                db.setTransactionSuccessful();

                //Reset rowid column
               // String reset =  "UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='"+table+"'";
                //db.execSQL(reset);

                //Drop table
                //sql =  "DROP TABLE IF EXISTS "+table;
                //db.execSQL(sql);



            }catch(Exception e){
                Log.d(TAG, "Error deleting all rows in "+table +": "+e);
            }finally {
                if (db.inTransaction())
                    db.endTransaction();
                //Reset table
                //String sql =  "VACUUM "+table+"";
                //db.execSQL(sql);
            }
            return true;
        }catch (Exception e){
            System.out.println("Exception opening writable database to store: " + e);
            return false;

        }

    }

    /**
     * Dlete all rows of given table
     * @param table  table name
     * @return
     */
    public boolean deleteNRowsFromTable(String table, int rows){
        try{
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            try{
                //Delete all rows
                System.out.println("Delete Rows: " + rows);
                //String sql_statement = "DELETE FROM "+table+" WHERE rowid < "+rows;
                //String sql_statement = "DELETE FROM "+table+" WHERE ROWID IN (SELECT TOP "+rows+" ROWID FROM "+table+")";
                //String sql_statement = "DELETE FROM "+table+" WHERE ROWID IN (SELECT ROWID FROM "+table+" ORDER BY ROWID ASC LIMIT "+rows+")";

                //db.delete(table, "ROWID < ?", new String[]{""+rows});
                int del =db.delete(table, "ROWID IN (SELECT ROWID FROM "+table+" ORDER BY ROWID ASC LIMIT "+rows+")", null);
                System.out.println("Deleted rows: " + del);
                //db.execSQL(sql_statement);
                db.setTransactionSuccessful();

            }catch(Exception e){
                Log.d(TAG, "Error deleting "+rows+" rows in "+table +": "+e);
            }finally {
                if (db.inTransaction())
                    db.endTransaction();
                //Reorder table
                String sql =  "VACUUM "+table+"";
                db.execSQL(sql);
            }
            return true;
        }catch (Exception e){
            System.out.println("Exception opening writable database to store: " + e);
            return false;

        }

    }



    /**
         * Update table in database
         * @param table
         * @param values
         * @param store whether to store new values or delete table
         */
    public boolean updateDatabaseTable (String table, ThreadSafeArrayList<String> values, boolean store ){
        //TODO
        //System.out.println(EXTERNAL_DIRECTORY_PATH.getAbsolutePath());
            try {
                //Open db
                //SQLiteDatabase db = dbContext.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READWRITE, null);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.beginTransaction();
                try {
                    if (store) {
                        //Store or update new values on table
                        storeValuesInTable(table, values, db);
                    } else {
                        //Delete table
                        // String updateQuery1 = "DELETE * FROM "+ table ;
                        String updateQuery1 = "DROP TABLE " + table;
                        db.execSQL(updateQuery1);
                    }
                    db.setTransactionSuccessful();

                }catch (Exception e){
                    Log.d (TAG, "Error updating value in table "+table + " :"+e);
                }finally {
                    if (db.inTransaction())
                        db.endTransaction();
                }


            /* String updateQuery = "CREATE TABLE IF NOT EXISTS "+ table+"(" + columnValues +");";
             db.execSQL(updateQuery);*/
                //System.out.println(updateQuery);
                return true;

            }catch(Exception e){
                System.out.println("Exception in updateDatabaseTable: "+ e);
                return false;
            }
    }


    /**
     * Store values in Table
     * @param table name of the table
     * @param values List of the values: should be Strings of type 'value1', 'value2'...
     */
    private void storeValuesInTable(String table, ThreadSafeArrayList<String> values, SQLiteDatabase db){
        //System.out.println("Storing....."+values.size());

        try{
            String inQueryValues = "(";
            //Build the store query
            for (int i =0; i<values.size(); i++){
                //System.out.println("---------"+database+":"+values.get(i)+"-------------");
                inQueryValues += values.get(i)+", ";
                //Exec query if it does not include a null value and the last char is ")"

            }
            //Last column: updated in phone = no
            inQueryValues += "'"+syncStatusNo +"' , " +updatedStatusNo+"')";
            //System.out.println(inQueryValues);

            //Execute query
            if (!inQueryValues.contains("null") && inQueryValues.substring(inQueryValues.length() - 1).equals(")"))
                db.execSQL("INSERT INTO " + table + " VALUES" + inQueryValues + ";");
        }catch(SQLiteException e){
           // if (!e.toString().contains("UNIQUE"))
               // System.out.println("SQLite Exception while storing in table: "+e);

        }

    }

    public List<ThreadSafeArrayList<String>> readAllValuesFromTable(String table){
        List<ThreadSafeArrayList<String>> result = new ArrayList<ThreadSafeArrayList<String>>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.beginTransaction();
        try{
            if ( db  !=null) {
                //Create Cursor
                cursorSync = db.query(table, null, null,null, null, null, timeStampColumn+" ASC", null);
                Log.d("query", "cursor size: "+ cursorSync.getCount());

                //If Cursor is valid
                if (cursorSync != null ) {
                    if (cursorSync.moveToFirst()) {
                        int index = 0;
                        String last_updated = "";
                        do {
                            //Values to send to server
                            ThreadSafeArrayList<String> partial = new ThreadSafeArrayList<String>();
                            String[] cols = cursorSync.getColumnNames();

                            for (String column: cols){
                                String val = "0";
                                //Get the corresponding value
                                val = cursorSync.getString(cursorSync.getColumnIndex(column));
                                partial.set(val);
                            }

                            result.add(partial);

                        } while (cursorSync.moveToNext()   );

                    }
                }
            }
            db.setTransactionSuccessful();
        }catch (SQLiteException e){
            System.out.println("Error with readall function, read table: "+e);
        }     finally {
            if (db.inTransaction())
                db.endTransaction();
        }
        return result;
    }



    /**
     * Create new table
     * @param table - name of the table to create
     * @param keyColumnInTable - Key column is time_stamp
     * @param columnsOfTable - column names the table has
     */
    public void createTable(String table, String keyColumnInTable, List<String> columnsOfTable){
        if (initialized) {
            //If database is initialized
            String columnsQuery = prepareColumnsFromList(columnsOfTable, keyColumnInTable);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            try {
                db.beginTransaction();
                String initTable = "CREATE TABLE IF NOT EXISTS " + table + columnsQuery;
                db.execSQL(initTable);
                db.setTransactionSuccessful();
            }catch (Exception e){
                Log.d (TAG, "Error creating table "+table + " :"+e);
            }finally {
                if (db.inTransaction())
                    db.endTransaction();            }
            //Update time_stamp name value
            timeStampColumn = keyColumnInTable;
        }
    }
    /**
     * Prepare columns to insert into a table
     * @param names
     * @return string with columns
     */
    private String prepareColumnsFromList(List<String> names, String keyColumnInTable){
        String query = "( ";
        for (int i =0; i<names.size(); i++){
            //Get the column values
            try{
                query += names.get(i)+" CHAR(20),";
            }catch (Exception e){
                System.out.println("Get columns from JSON error: "+e);
            }
        }
        //Add update and sync columns
        query += syncColumn+ " CHAR(5), "+upDateColumn+ " CHAR(5), ";
        //Add primary key
        query += "PRIMARY KEY ("  + keyColumnInTable+"))";
        return query;
    }

    /**
     * storeInDatabase
     * @param ctx
     * @param table name of the table
     * @param values1  new values to include in database
     */

    static private void storeInDatabase( Context ctx, String table, ThreadSafeArrayList values1){
        //Add the stored values


        SQLiteDatabase sqlite =ctx.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READWRITE, null);

        //TODO Careful with the loop!! Concurrent exception
        /*for (Iterator<String> inQuery = valuesToStore.iterator(); inQuery.hasNext();){
            //Exec query if it does not include a null value and the last char is ")"
            String addValue = inQuery.next();
            if (!addValue.contains("null") && addValue.substring(addValue.length() - 1).equals(")")) {
                sqlite.execSQL("INSERT INTO " + table + " VALUES" + addValue + ";");
            }

        }*/
        for (int i =0; i < values1.size(); i ++){
            String addValue = (String)values1.get(i);
            if (!addValue.contains("null") && addValue.substring(addValue.length() - 1).equals(")")) {
                sqlite.execSQL("INSERT INTO " + table + " VALUES" + addValue + ";");
            }
        }

        sqlite.close();

    }



    /**
     * Read all not syncrhonized rowsvalues
     * @param tableRead  name of the table to read in the phone
     * @param tableNameOnServer  name of the table in the server to store this values
     * @return map with pairs column-value
     */
    public List<Map<String, String>>  readFromTableNotSyncRows(String tableRead, String tableNameOnServer){
        //Prepare values to return
        List<Map <String, String>> resultArgs = new ArrayList <Map <String, String>>();
        //Open database
        SQLiteDatabase db=dbContext.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READWRITE, null);

        if (db !=null){
            //Create Cursor
            Cursor c = db.rawQuery("SELECT * FROM " + tableRead +" WHERE "+syncColumn+" = '"+syncStatusNo+"'", null);

            //If Cursor is valid
            if (c != null ) {
                String[] cols = c.getColumnNames();
                //Move cursor to first row
                if  (c.moveToFirst()) {
                    do{
                        //Values to send to server
                        Map <String, String> partial = new HashMap<String, String>();
                        //Values to update local database
                        List<String> update = new ArrayList<String>();

                        for (String column: cols){
                            String val = "0";
                            //Get the corresponding value
                            if (column.equals("synchronized")){
                                //The syncrhonized value will be add in when it is stored
                                val = "y";
                                //val = c.getString(c.getColumnIndex(column));
                            }else {
                                val = c.getString(c.getColumnIndex(column));

                            }
                            //Add the last_update value to be updated in the table
                            if(column.equals("last_update")){
                                update.add( "'"+c.getString(c.getColumnIndex(column))+"'");
                            }

                            partial.put(column, val);

                            //updateQuery += " "+val +",";
                        }
                        //Update syncrhonized value
                        updateSyncInTable(tableRead, update, db);

                        //Include table values
                        partial.put("table_name", tableNameOnServer);
                        resultArgs.add(partial);
                    } while (c.moveToNext());
                }
            }
            //Close cursor and database
            c.close();
            db.close();
        }

        return resultArgs;

    }

    /**
     * Update the synchronized values on table
     * @param table - name of the table
     * @param lasUpdates - list of  lastUpdates to be updated
     */

    private void updateSyncInTable(String table, List<String> lasUpdates, SQLiteDatabase db){
        //"Update users set udpateStatus = '"+ status +"' where userId="+"'"+ id +"'"
        for (String id: lasUpdates){
            db.execSQL("UPDATE "+table+ " SET synchronized = "+syncStatusYes+" WHERE last_update = " + id +" ;");

        }
    }






    /**
     * Compose JSON out of SQLite records
     * @return
     */

    public  List<Map<String, String>> getNotUpdatedValues( String table, String[] columns, String col, String status, int max) {

        //NOTE: Include updated value: only in the server case
        int MAX_READ_SAMPLES;
        String updated_name;
        if (col.equals(syncColumn)){
            MAX_READ_SAMPLES = MAX_READ_SAMPLES_SYNCHRONIZE;
            updated_name = "another_update";

        }else{
            MAX_READ_SAMPLES = MAX_READ_SAMPLES_UPDATE;
            updated_name = col;

        }

        List<Map<String, String>> wordList = new ArrayList<Map<String, String>>();

        //Ordered selectiion
        //String selectQuery = "SELECT  * FROM " + table + " ORDER BY "+timeStampColumn+" ASC";
        String selectQuery = "SELECT  * FROM " + table + " WHERE "+col+" = \""+ status +"\" ORDER BY "+timeStampColumn+" ASC";
        //String selectQuery = "SELECT  * FROM " + table + " WHERE "+col+" = " + status ;

        //SQLiteDatabase db_cursor = dbContext.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READONLY, null);
        SQLiteDatabase db_cursor = dbHelper.getReadableDatabase();


        if (db_cursor !=null) {
            //Create Cursor
            //Choose the right array from table
            cursorSync = db_cursor.rawQuery(selectQuery, null);

            System.out.println("Empatica Cursor size: " + cursorSync.getCount());
            //If Cursor is valid
            if (cursorSync != null ) {
                if (cursorSync.moveToLast() && cursorSync.getPosition()>=0) {
                    int index = 0;
                   do {
                       HashMap<String, String> map = new HashMap<String, String>();
                        //map.put("table_name", MainActivityBM.dispTableName);
                        for (int i = 0; i < columns.length ; i++) {
                            map.put(columns[i], cursorSync.getString(i));
                            //lastUpd.add(cursorSync.getString(i));
                        }
                       //Include updated value: only in the server case
                       //Otherwise... we include sync value again
                       map.put(col, cursorSync.getString(columns.length + 1 ));
                       //Include sync value:
                       map.put(syncColumn, cursorSync.getString(columns.length ));

                       wordList.add(map);
                       index ++;

                    } while (cursorSync.moveToPrevious() && cursorSync.getPosition()>=0 && index < max);
                    System.out.println("Done reading: "+index);


                }

                db_cursor.close();

            }
        }

        //Use GSON to serialize Array List to JSON
        return wordList;
    }

    /**
     * Compose JSON out of SQLite records
     * @return
     */

    public List<Map<String, String>> getNotUpdatedValuesUpToN( String table, String[] columns, String col, String status, int max_samples) {

        //NOTE: Include updated value: only in the server case
        int MAX_READ_SAMPLES;
        //String updated_name;
        if (col.equals(syncColumn)){
            MAX_READ_SAMPLES = MAX_READ_SAMPLES_SYNCHRONIZE;
            //updated_name = "another_update";

        }else{
            MAX_READ_SAMPLES = MAX_READ_SAMPLES_UPDATE;
            // updated_name = col;

        }

        List<Map<String, String>> wordList = new ArrayList<Map<String, String>>();
        String selectQuery = "SELECT  * FROM " + table + " WHERE "+col+" = '" + status +"' ORDER BY "+timeStampColumn+" ASC";
        //String selectQuery = "SELECT * FROM " + table + " WHERE "+col+" = " + status ;
         //String selectQuery = "SELECT  * FROM " + table ;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //SQLiteDatabase db_cursor = dbContext.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READONLY, null);

        try{
            db.beginTransaction();

            System.out.println("Select query: "+selectQuery);
        System.out.println("Db FOR CURSOR: "+db.toString());
        if (db !=null) {
            //Create Cursor
            //Choose the right array from table
            cursorSync = db.rawQuery(selectQuery, null);
            System.out.println("Valid cursor: "+cursorSync.getCount());

            //If Cursor is valid
            if (cursorSync != null ) {
                if (cursorSync.moveToFirst()) {

                    int index = 0;
                    do {
                        HashMap<String, String> map = new HashMap<String, String>();
                        //map.put("table_name", MainActivityBM.dispTableName);
                        for (int i = 0; i < columns.length; i++) {
                            map.put(columns[i], cursorSync.getString(i));
                            //lastUpd.add(cursorSync.getString(i));
                        }
                        //Include updated value: only in the server case
                        //Otherwise... we include sync value again
                        map.put(col, cursorSync.getString(columns.length + 1));
                        //Include sync value:
                        map.put(syncColumn, cursorSync.getString(columns.length));

                        wordList.add(map);
                        index++;

                    } while (cursorSync.moveToNext() && index < MAX_READ_SAMPLES_UPDATE);
                    System.out.println("Done reading: " + index);
                }
                db.setTransactionSuccessful();
            }


            }
        }catch (Exception e){
            Log.d(TAG, "Exception retrieving not updated values: "+e);
        }finally{
            if (db.inTransaction())
                db.endTransaction();        }

        //Use GSON to serialize Array List to JSON
        return wordList;
    }

    /**
     * Compose JSON out of SQLite records
     * @return
     */

    public List<Map<String, String>> getLastNSamples (String table, String[] columns, String col, String status, int max_samples) {


        List<Map<String, String>> wordList = new ArrayList<Map<String, String>>();
        //String selectQuery = "SELECT  * FROM " + table + " WHERE "+col+" = '" + status +"' ORDER BY "+timeStampColumn+" ASC";
        //String selectQuery = "SELECT * FROM " + table + " WHERE "+col+" = " + status ;
        String selectQuery = "SELECT  * FROM " + table +" ORDER BY "+timeStampColumn+" ASC";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //SQLiteDatabase db_cursor = dbContext.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READONLY, null);

        db.beginTransaction();
        try{

            if (db !=null) {
                //Create Cursor
                //Choose the right array from table
                cursorSync = db.rawQuery(selectQuery, null);
                System.out.println("Valid cursor: "+cursorSync.getCount());

                //If Cursor is valid
                if (cursorSync != null ) {
                    if (cursorSync.moveToLast()) {

                        int index = 0;
                        do {
                            HashMap<String, String> map = new HashMap<String, String>();
                            //map.put("table_name", MainActivityBM.dispTableName);
                            for (int i = 0; i < columns.length; i++) {
                                map.put(columns[i], cursorSync.getString(i));
                                //lastUpd.add(cursorSync.getString(i));
                            }
                            //Include updated value: only in the server case
                            //Otherwise... we include sync value again
                            map.put(col, cursorSync.getString(columns.length + 1));
                            //Include sync value:
                            map.put(syncColumn, cursorSync.getString(columns.length));

                            wordList.add(map);
                            index++;

                        } while (cursorSync.moveToPrevious() && index < max_samples);
                        System.out.println("Done reading: " + index +" vs max "+max_samples);
                    }
                    db.setTransactionSuccessful();
                }


            }
        }catch (Exception e){
            Log.d(TAG, "Exception retrieving not updated values: "+e);
        }finally{
            if (db.inTransaction())
                db.endTransaction();        }

        //Use GSON to serialize Array List to JSON
        return wordList;
    }

    /**
     * Get all values which are flaged as status
     * @param table
     * @param columns
     * @param col
     * @param status
     * @return
     */

    public  List<Map<String, String>> getAllNotCheckedValues( String table, String[] columns, String col, String status) {
        //NOTE: Include updated value: only in the server case
        /*int MAX_READ_SAMPLES;
        String updated_name;
        if (col.equals(syncColumn)){
            MAX_READ_SAMPLES = MAX_READ_SAMPLES_SYNCHRONIZE;
            updated_name = "another_update";

        }else{
            MAX_READ_SAMPLES = MAX_READ_SAMPLES_UPDATE;
            updated_name = col;

        }*/

        List<Map<String, String>> wordList = new ArrayList<Map<String, String>>();
        //Ordered select:
        String selectQuery = "SELECT  * FROM " + table + " WHERE "+col+" = " + status +" ORDER BY "+timeStampColumn+" ASC";
        //String selectQuery = "SELECT  * FROM " + table + " WHERE "+col+" = " + status ;

        SQLiteDatabase db_cursor = dbContext.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READONLY, null);

        if (db_cursor !=null) {
            //Create Cursor
            //Choose the right array from table
            cursorSync = db_cursor.rawQuery(selectQuery, null);

            //If Cursor is valid
            if (cursorSync != null ) {
                if (cursorSync.moveToFirst()) {
                    int index = 0;
                    do {
                        HashMap<String, String> map = new HashMap<String, String>();
                        //map.put("table_name", MainActivityBM.dispTableName);
                        for (int i = 0; i < columns.length ; i++) {
                            map.put(columns[i], cursorSync.getString(i));
                            //lastUpd.add(cursorSync.getString(i));
                        }
                        //Include updated value: only in the server case
                        //Otherwise... we include sync value again
                        map.put(col, cursorSync.getString(columns.length + 1 ));
                        //Include sync value:
                        map.put(syncColumn, cursorSync.getString(columns.length ));

                        wordList.add(map);
                        index ++;

                    } while (cursorSync.moveToNext());
                    System.out.println("Done reading: "+index);


                }

                db_cursor.close();

            }
        }

        //Use GSON to serialize Array List to JSON
        return wordList;
    }




    /**
     * Get SQLite records that are yet to be Synced
     * @return
     */
    public static int dbSyncCount(Context ctx, String table){
        int count = 0;
        String selectQuery = "SELECT  * FROM "+table+" WHERE "+upDateColumn+" = '"+updatedStatusNo+"'";
        //SQLiteDatabase database = ctx.openOrCreateDatabase(databaseFile, Context.MODE_WORLD_WRITEABLE, null);
        SQLiteDatabase db=ctx.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READWRITE, null);

        Cursor cursor = db.rawQuery(selectQuery, null);
        count = cursor.getCount();
        //System.out.println("To synchronize.... "+ count);
        db.close();
        return count;
    }

    /**
     * Update table in database
     * @param table
     * @param values
     * @param store
     */
    public void updateNewValuesDatabase(String table, List<String> values, boolean store){
        //Open db
        SQLiteDatabase db=dbContext.openOrCreateDatabase(databaseFile,SQLiteDatabase.OPEN_READWRITE, null);
        if (store){
            //Store or update new values on table
            storeInTable(table, values, db);
        }else{
            //Delete table
            String updateQuery1 = "DELETE * FROM "+ table ;
            db.execSQL(updateQuery1);
        }
        //Close db
        db.close();


        //String updateQuery = "CREATE TABLE IF NOT EXISTS "+ table+"(" + columnValues +");";
        // db.execSQL(updateQuery);
        //System.out.println(updateQuery);



    }

    /**
     * Store values in Table
     * @param table
     * @param values
     */
    private void storeInTable(String table, List<String> values, SQLiteDatabase db){

        try{
            //Prepare the Query values
            String inQueryValues = "(";
            //Build the store query
            for (int i =0; i<values.size(); i++){
                //System.out.println("---------"+database+":"+values.get(i)+"-------------");
                inQueryValues += values.get(i)+", ";
                //Exec query if it does not include a null value and the last char is ")"

            }
            //Last column: syncrhonized in phone = yes
            //inQueryValues += "'y')";
            inQueryValues = inQueryValues.substring(0, inQueryValues.length()-2);
            inQueryValues += ")";

            //Execute query
            if (!inQueryValues.contains("null") && inQueryValues.substring(inQueryValues.length() - 1).equals(")")){
                //db.execSQL("REPLACE INTO "+table+ " VALUES" + inQueryValues+";");
                db.execSQL("INSERT INTO "+table+ " VALUES" + inQueryValues+" "
                        + "ON DUPLICATE KEY UPDATE synchronized = 'y';");
                //+ "ON DUPLICATE KEY UPDATE synchronized= y;");

            }
            else
                System.out.println("IITDatabaseManager: DID NOT UPDATE DATABASE ");
        }catch(SQLiteException e){
            //if (!e.toString().contains("UNIQUE"))
            System.out.println("SQLite Exception while storing in table: " + e);
        }

    }

    /**
     * Update Sync status against each User ID
     * @param ctx context from activity
     * @param status synchronize status
     * @param last_up last_updated value (acts as id)
     */
    public void updateSyncStatus(Context ctx, String table, String status_col, String status, String last_up){

        //SQLiteDatabase database = ctx.openOrCreateDatabase(databaseFile, Context.MODE_WORLD_WRITEABLE, null);
        //SQLiteDatabase db1=ctx.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READWRITE, null);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            //Update query: update status, searching the right last_updated value
            String updateQuery = "UPDATE " + table + " SET " + status_col + " = '" + status + "' WHERE " + timeStampColumn + " = '" + last_up + "'";
            //System.out.println("Update values: "+updateQuery);
            Log.d("query", updateQuery);
            //System.out.println("^^^^^^^^^^^UPDATED???: " + updateQuery);
            db.execSQL(updateQuery);
            db.setTransactionSuccessful();
        } catch (SQLiteException e){
            Log.d (TAG, "Error updating values in "+table+": "+e);
            System.out.println("SQLite Exception while sync values in table: " + e);

        }finally{
            if (db.inTransaction())
                db.endTransaction();
        }
    }

    /**
     * Update all previous values as sync/updated, given a time_stamped sample
     * Works as a cummulative ACK (kind of ~)
     * @param ctx - context
     * @param table - table name
     * @param status - yes/no
     * @param last_up - time_stamp acknowledged
     * @return
     */

    public boolean ackSyncStatusAllPrevious (Context ctx, String table,  String status, String last_up){
        //SQLiteDatabase database = ctx.openOrCreateDatabase(databaseFile, Context.MODE_WORLD_WRITEABLE, null);
        //SQLiteDatabase db1=ctx.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READWRITE, null);
        //If sync is successful:
        if(syncStatusYes.equals(status)){
            System.out.println("ACK Process: "+last_up);
            //Get all not sync previous columns:
            //String selectQuery = "SELECT  * FROM " + table + " WHERE "+syncColumn+" = '" + syncStatusNo +"'";
            // String selectQuery = "SELECT  * FROM " + table + " WHERE "+syncColumn+" = \"" + syncStatusNo +"\" ORDER BY "+timeStampColumn+" ASC";

            //SQLiteDatabase db =ctx.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READONLY, null);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            db.beginTransaction();
            try{

                if ( db  !=null) {
                    //Create Cursor
                    //Choose the right array from table
                    //cursorSync =    db .rawQuery(selectQuery, null);
                    //Cursor query (String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
                    // String having, String orderBy, String limit)
                    Cursor cursorSync = db.query(table, null, syncColumn+" = \"" + syncStatusNo +"\"",null, null, null, timeStampColumn+" ASC", null);
                    Log.d("query", "cursor size: "+ cursorSync.getCount());

                    //If Cursor is valid
                    if (cursorSync != null ) {
                        System.out.println("Values to ACK, cursor: "+cursorSync.getCount());
                        if (cursorSync.moveToFirst()) {
                            int index = 0;
                            String last_updated = "";
                            do {
                                //Update values
                                last_updated = cursorSync.getString(BGService._TIME_INDEX);
                                System.out.println(last_updated);
                                // ackSingleSample(ctx, table,syncColumn, status,last_updated, db);

                                updateSyncStatus(ctx, table,syncColumn, syncStatusYes,last_updated);
                                index ++;
                                if (index%CURSOR_WINDOW_LIMIT == 0){
                                    //Get new cursor!
                                    cursorSync.close();
                                    cursorSync = db.query(table, null, syncColumn+" = \"" + syncStatusNo +"\"",null, null, null, timeStampColumn+" ASC", null);
                                    cursorSync.moveToFirst();
                                }

                            } while (!last_up.equals(last_updated) && cursorSync.moveToNext()   );
                            System.out.println("Done ACK reading: "+index);

                        }
                    }
                }
                db.setTransactionSuccessful();
            }catch (SQLiteException e){
                System.out.println("Error with ACK function, read table: "+e);
            }     finally {
                if (db.inTransaction())
                    db.endTransaction();
            }
        }

        return true;
    }
    /*
    * countRows
     */

    public static int countRows(Context ctx, String table){
        int rows = 0;
        SQLiteDatabase db=ctx.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READWRITE, null);

        //Create Cursor object to read versions from the table
        Cursor c = db.rawQuery("SELECT "+timeStampColumn+" FROM " + table, null);
        //If Cursor is valid
        if (c != null ) {
            //Move cursor to first row
            if  (c.moveToFirst()) {
                do {
                    //Go to the next row
                    rows ++;
                }while (c.moveToNext()); //Move to next row
            }
        }
        db.close();
        return rows;
    }

    /*private void orderTableByColumn(String table_name, String column){

        //Open db
        db=dbContext.openOrCreateDatabase(databaseFile,SQLiteDatabase.OPEN_READWRITE, null);
        //Delete table
        String updateQuery = " SELECT * FROM "+table_name+"  ORDER BY "+column+" ASC"  ;
        System.out.println("Update query: "+ updateQuery);
        db.execSQL(updateQuery);

        //Close db
        db.close();
    }*/


}

