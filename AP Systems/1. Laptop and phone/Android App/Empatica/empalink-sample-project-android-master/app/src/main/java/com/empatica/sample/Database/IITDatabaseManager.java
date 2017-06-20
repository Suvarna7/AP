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
import com.empatica.sample.DataConstants;

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

 2. Empatica: columns {"time_stamp", "Acc_x", "Acc_y", "Acc_z", "GSR", "BVP",
 "IBI", "temperature","battery_level"}
 *
 * @author Caterina Lazaro
 * @version 3.0 Dec 2016
 */

public class IITDatabaseManager {

    private static final String TAG = "IIT_DATABASE";
    private MyDatabaseHelper dbHelper;
    //private static SQLiteDatabase db;
    private static int CURSOR_WINDOW_LIMIT = 2000;


    //Database info
    private static String EXTERNAL_DIRECTORY_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    //private static String DB_LOCAL_URL =  "/storage/emulated/legacy/IIT_database/";
    static String DB_LOCAL_URL =  EXTERNAL_DIRECTORY_PATH+"/IIT_database/";
    public static String DEFAULT_DB_NAME = "dbSensors.db";
    private String db_name;

    static String databaseFile;
    //Time_Stamp column name:
    public static String timeStampColumn;
    public static final String default_timeStampColumn = "time_stamp";
    //Updated : for the server
    public static final String updatedStatusNo = "n";
    public static final String updatedStatusYes = "y";
    public static final String upDateColumn = "updated";
    //Synchronized : for the internal database and USB
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

    private boolean initialized = false;


    ACKThread myACK;

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

        try {
            //Create database:
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            try {

                //Enable Write Ahead Logging (WAL)
                db.enableWriteAheadLogging();
                db.beginTransaction();

                //Test db
                db.execSQL("CREATE TABLE IF NOT EXISTS sample_table (created CHAR(1))");
                db.setTransactionSuccessful();
            } catch (Exception e) {
                System.out.println("Error creating table: " + e);
            } finally {
                if (db.inTransaction())
                    db.endTransaction();
            }
            initialized = true;
        }catch (SQLiteDatabaseLockedException eo){
            System.out.println("Error getting writable db to create table: " + eo);

        }

        //Create ACK thread
        myACK = new ACKThread();

    }

    /**
     * * Insert or update values using MyDatabaseHelper
     * @param table table name
     * @param values list of values o update
     * @param columns column names
     * @return success boolean
     */
    public boolean insertAndUpdateIntoDatabaseTable(String table, ThreadSafeArrayList<String> values, String[] columns){
        try{
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues not_zero = new ContentValues();

            try{
                ContentValues update = new ContentValues();

                for (int i =0; i < columns.length; i++){
                    String val = values.get(i);
                    update.put(columns[i], val);

                    if (!val.equals("0") && i!=BGService._TIME_INDEX) {
                        not_zero.put(columns[i], val);
                    }

                }
                //Add sync
                update.put(syncColumn, syncStatusNo);
                //Add update
                update.put(upDateColumn, updatedStatusNo);

                long result = db.insert(table, null, update);
                //System.out.println("Updated insert "+db_name+",  "+table+":  "+result);
                if (result < 0){
                    //Update values !=0
                    //WHERE timeStampColumn + " = " + columns[BGService._TIME_INDEX]
                    result = db.update(table, not_zero, String.format("%s = ?", timeStampColumn),  new String[]{(String)update.get(columns[BGService._TIME_INDEX])} );
                    if (result == 0){
                        //Did not update any value
                        result = db.insert(table, null, not_zero);
                        if(result <0) {
                            result = db.insert(table, null, update);
                            if (result < 0)
                                db.update(table, not_zero, String.format("%s = ?", timeStampColumn), new String[]{"'" + (String) update.get(columns[BGService._TIME_INDEX]) + "'"});

                        }
                    }
                }

                db.setTransactionSuccessful();

            }catch(Exception e){
                Log.d(TAG, "Error storing new sample in " + table + ": " + e);
                //Update values !=0
                long result = db.update(table, not_zero, timeStampColumn + " = " + columns[BGService._TIME_INDEX], null);
                System.out.println("Updated except "+table+":  " +result);
                db.setTransactionSuccessful();

            }finally {
                if (db.inTransaction())
                    db.endTransaction();

            }
            return true;
        }catch (Exception e){
            System.out.println("INSERT&UPDATE - Exception opening writable database to store: " + e);
            return false;

        }
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
                    String val = values.get(i);
                    update.put(columns[i], val);

                }
                //Add sync
                update.put(syncColumn, syncStatusNo);
                //Add update
                update.put(upDateColumn, updatedStatusNo);


                long result = db.insert(table, null, update);


                db.setTransactionSuccessful();

            }catch(Exception e){
                Log.d(TAG, "Error storing new sample in " + table + ": " + e);


            }finally {
                if (db.inTransaction())
                    db.endTransaction();
            }
            return true;
        }catch (Exception e){
            System.out.println("INSERT - Exception opening writable database to store: " + e);
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
                Log.d(TAG, "Error storing new sample in " + table + ": " + e);
            }finally {
                if (db.inTransaction())
                    db.endTransaction();
            }
        }catch (Exception e){
            System.out.println("DELETE TABLE - Exception opening writable database to store: " + e);

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
                Log.d(TAG, "Error deleting all rows in " + table + ": " + e);
            }finally {
                if (db.inTransaction())
                    db.endTransaction();
                //Reset table
                //String sql =  "VACUUM "+table+"";
                //db.execSQL(sql);
            }
            return true;
        }catch (Exception e){
            System.out.println("DELETE ALL ROWS - Exception opening writable database to store: " + e);
            return false;

        }

    }

    /**
     * Delete N rows of given table, starting from the oldest
     * @param table  table name
     * @param time_column timestamp column name
     * @param rows number of rows to delete
     * @return success
     */
    public boolean deleteNRowsFromTable(String table, String time_column, int rows){
        try{
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            try{
                db.beginTransaction();

                //Delete all rows
                System.out.println("Delete Rows (" + table + "): " + rows);
                //String sql_statement = "DELETE FROM "+table+" WHERE rowid < "+rows;
                //String sql_statement = "DELETE FROM "+table+" WHERE ROWID IN (SELECT TOP "+rows+" ROWID FROM "+table+")";
                //String sql_statement = "DELETE FROM "+table+" WHERE ROWID IN (SELECT ROWID FROM "+table+" ORDER BY ROWID ASC LIMIT "+rows+")";

                //db.delete(table, "ROWID < ?", new String[]{""+rows});
                int del =db.delete(table, "ROWID IN (SELECT ROWID FROM "+table+" ORDER BY "+time_column+" ASC LIMIT "+rows+")", null);
                //int del =db.delete(table, "ROWID IN (SELECT ROWID FROM "+table+" ORDER BY "+time_column+" DESC LIMIT "+rows+")", null);

                //int del =db.delete(table,  "ORDER BY "+time_column+" ASC LIMIT "+rows, null);

                System.out.println("Deleted rows ("+table+"): " + del);
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
                //Close db ?
            }
            return true;
        }catch (Exception e){
            System.out.println("DELETE N ROWS - Exception opening writable database to store: " + e);
            return false;

        }

    }

    /**
     * Delete N rows of given table, starting from the timestamp to N older values
     * @param table  table name
     * @param time_column timestamp column name
     * @param time_stamp timestamp value to start deleting
     * @param rows number of rows to delete
     * @return success
     */
    public boolean deleteNRowsFromTable(String table, String time_column, String time_stamp, int rows){
        try{
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            try{
                db.beginTransaction();
                //String query = "SELECT * FROM " + table + " WHERE " + time_column + " < '" + time_stamp+"'  LIMIT "+rows;
                //Cursor res = db.rawQuery(query, null);
                Cursor res = db.query(table, null, null, null, null, null, time_column + " < '" + time_stamp+"'  LIMIT "+rows, null);

                res.moveToFirst();
                String init_rowid = res.getString(res.getColumnIndex(time_column));
                res.moveToLast();
                String last_rowid = res.getString(res.getColumnIndex(time_column));
                res.close();

                //Delete N rows
                String delQ= "ROWID IN (SELECT "+time_column+ " FROM "+table+" WHERE "+time_column+" BETWEEN '"+last_rowid+"' AND '"+init_rowid+"')";
                System.out.println("Delete Rows (" + table + "): " + delQ);

                int del =db.delete(table,delQ, null);

                System.out.println("Deleted rows ("+table+"): " + del);
                //db.execSQL(sql_statement);

                db.setTransactionSuccessful();

            }catch(Exception e){
                Log.d(TAG, "Error deleting "+rows+" rows in "+table +": "+e);
                System.out.println(time_stamp + " Error deleting " + rows + " rows in " + table + ": " + e);

            }finally {
                if (db.inTransaction())
                    db.endTransaction();
                //Reorder table
                String sql =  "VACUUM "+table+"";
                db.execSQL(sql);
                //Close db ?
            }
            return true;
        }catch (Exception e){
            System.out.println("DELETE N ROWS - Exception opening writable database to store: " + e);
            return false;

        }

    }

    /**
     * Delete a single row from the table
     * @param table table name
     * @param time_column  timestamp column name
     * @param timestamp timestamp value of the row
     * @return success
     */
    public boolean deleteRow(String table, String time_column, String timestamp){
        try{
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            try{
                db.beginTransaction();

                //Delete a row
                System.out.println("Delete Row (" + table + "): " + timestamp);
                db.delete(table, time_column + " = " + timestamp, null);

                //db.execSQL(sql_statement);
                db.setTransactionSuccessful();

            }catch(Exception e){
                Log.d(TAG, "Error deleting "+timestamp+" rows in "+table +": "+e);
            }finally {
                if (db.inTransaction())
                    db.endTransaction();

            }
            return true;
        }catch (Exception e){
            System.out.println("DELETE ROW "+timestamp+" - Exception opening writable database to store: " + e);
            return false;

        }

    }

    public void tableOrginize(String table){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (db.inTransaction())
            db.endTransaction();

        //Close db ?
        if (db.inTransaction())
            db.endTransaction();
    }

    /**
     * Get all values of a given table except for the last 'margin' rows
     * @param table table name
     * @param margin N margin samples
     * @return
     */

    public List<ThreadSafeArrayList<String>> readAllValuesFromTableWithMargin (String table, int margin){
        List<ThreadSafeArrayList<String>> result = new ArrayList<ThreadSafeArrayList<String>>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if ( db  !=null) {

            try {
                db.beginTransaction();

                //Create Cursor
                Cursor cursorSync = db.query(table, null, null, null, null, null, timeStampColumn + " ASC", null);
                Log.d("query", "cursor size: " + cursorSync.getCount());

                //If Cursor is valid
                if (cursorSync != null) {
                    if (cursorSync.moveToFirst()) {
                        do {
                            //Values to send to server
                            ThreadSafeArrayList<String> partial = new ThreadSafeArrayList<String>();
                            String[] cols = cursorSync.getColumnNames();

                            try {
                                for (String column : cols) {
                                    String val = "0";
                                    //Get the corresponding value
                                    int column_index = cursorSync.getColumnIndex(column);
                                    if (column_index >= 0)
                                        val = cursorSync.getString(column_index);
                                    else
                                        System.out.println("Column not in cursor: " + column);
                                    partial.set(val);
                                }
                            } catch (IllegalStateException ce) {
                                System.out.println("Cursor Illegal state in readAllValues: " + ce);
                                //Reset cursor!
                                cursorSync.close();
                                cursorSync = db.query(table, null, null, null, null, null, timeStampColumn + " ASC", null);
                                cursorSync.moveToFirst();
                            }

                            result.add(partial);

                        }
                        while (cursorSync.moveToNext() && cursorSync.getPosition() < (cursorSync.getCount() - margin) && cursorSync.getPosition() > 0);

                    }
                }
                cursorSync.close();

                db.setTransactionSuccessful();
            } catch (SQLiteException e) {
                System.out.println("Error with readall function, read table: " + e);
            } finally {
                if (db.inTransaction())
                    db.endTransaction();
            }
        }
        return result;
    }

    /**
     * Read all values of a given table
     * @param table table name
     * @return
     */
    public List<ThreadSafeArrayList<String>> readAllValuesFromTable(String table){
        List<ThreadSafeArrayList<String>> result = new ArrayList<ThreadSafeArrayList<String>>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.beginTransaction();
        try{
            if ( db  !=null) {
                //Create Cursor
                Cursor cursorSync = db.query(table, null, null,null, null, null, timeStampColumn+" ASC", null);
                Log.d("query", "cursor size: "+ cursorSync.getCount());

                //If Cursor is valid
                if (cursorSync != null ) {
                    if (cursorSync.moveToFirst()) {
                        do {
                            //Values to send to server
                            ThreadSafeArrayList<String> partial = new ThreadSafeArrayList<String>();
                            String[] cols = cursorSync.getColumnNames();

                            try {
                                for (String column : cols) {
                                    String val = "0";
                                    //Get the corresponding value
                                    int column_index = cursorSync.getColumnIndex(column);
                                    if (column_index >= 0)
                                        val = cursorSync.getString(column_index);
                                    else
                                        System.out.println("Column not in cursor: "+column);
                                    partial.set(val);
                                }
                            }catch (IllegalStateException ce){
                                System.out.println("Cursor Illegal state in readAllValues: "+ce);
                                //Reset cursor!
                                cursorSync.close();
                                cursorSync = db.query(table, null, null,null, null, null, timeStampColumn+" ASC", null);
                                cursorSync.moveToFirst();
                            }

                            result.add(partial);

                        } while (cursorSync.moveToNext()  && cursorSync.getPosition()>0 );

                    }
                }
                cursorSync.close();

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
            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                try {
                    db.beginTransaction();
                    String initTable = "CREATE TABLE IF NOT EXISTS " + table + columnsQuery;
                    db.execSQL(initTable);
                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    Log.d(TAG, "Error creating table " + table + " :" + e);
                } finally {
                    if (db.inTransaction())
                        db.endTransaction();
                }
                //Update time_stamp name value
                timeStampColumn = keyColumnInTable;
            }catch (SQLiteDatabaseLockedException eo){
                System.out.println("Error getting writable db to create new table: " + eo);

            }
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
     * Get all values of a table not synchronized nor updated
     * @param table table name
     * @param columns columns in table
     * @param col updating column
     * @param status not-updated value
     * @param max max number of samples to read
     * @return
     */

    public  List<Map<String, String>> getNotCheckedValues( String table, String[] columns, String col, String status, int max, boolean most_recent) {


        List<Map<String, String>> wordList = new ArrayList<Map<String, String>>();

        //Ordered selection
        String selectQuery;
        if (most_recent)
            selectQuery= "SELECT  * FROM " + table + " WHERE "+col+" = \""+ status +"\" ORDER BY "+timeStampColumn+" DESC "
                            + "LIMIT "+max;
        else
            selectQuery= "SELECT  * FROM " + table + " WHERE "+col+" = \""+ status +"\" ORDER BY "+timeStampColumn+" ASC "
                    + "LIMIT "+max;


        //SQLiteDatabase db_cursor = dbContext.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READONLY, null);
        SQLiteDatabase db_cursor = dbHelper.getReadableDatabase();
        if (db_cursor !=null) {
            try {
                db_cursor.beginTransaction();

                //Create Cursor
                //Choose the right array from table
                Cursor cursorSync = db_cursor.rawQuery(selectQuery, null);

                if (cursorSync != null) {
                    //System.out.println("Empatica Cursor size: " + cursorSync.getCount());
                    try {
                        //If Cursor is valid
                        if (cursorSync.moveToFirst() && cursorSync.getPosition() >= 0) {
                            try {
                                do {
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    for (int i = 0; i < columns.length; i++) {
                                        map.put(columns[i], cursorSync.getString(cursorSync.getColumnIndex(columns[i])));
                                        //lastUpd.add(cursorSync.getString(i));
                                    }

                                    //Include updated value
                                    map.put(col, cursorSync.getString(columns.length + 1));

                                    //Include sync value:
                                    map.put(syncColumn, cursorSync.getString(columns.length));

                                    wordList.add(map);

                                }
                                while (cursorSync.moveToNext());
                                //while (cursorSync.moveToNext() && cursorSync.getPosition() >= 0&& index < max);

                            } catch (Exception e) {
                                //Close cursor and db:
                                cursorSync.close();
                                //db_cursor.close();

                                return wordList;
                            }

                        }
                        //Close cursor
                        cursorSync.close();
                        db_cursor.setTransactionSuccessful();

                    } catch (IllegalStateException ie) {
                        //Illegal state exception ! Do nothing, just print
                        System.out.println("Get not updated values cursor illegal state exception: ? " +
                                cursorSync);
                        return null;
                    } finally {
                        if (db_cursor.inTransaction())
                            db_cursor.endTransaction();

                        //db_cursor.close();
                    }


                }else
                    db_cursor.setTransactionSuccessful();

            }catch (SQLiteException e){
                System.out.println("Error with getNotCheckedValues function, read table: "+e);
            }finally {
                if (db_cursor.inTransaction())
                    db_cursor.endTransaction();

                //db_cursor.close();
            }
        }

        //Use GSON to serialize Array List to JSON
        return wordList;
    }


    /**
     * Return the number of not checked rows
     * @param table
     * @param col
     * @param status
     * @param max
     * @return
     */
    public  int getNotCheckedValuesNumber( String table, String col, String status, int max) {
        //Ordered selection
        String selectQuery = "SELECT  * FROM " + table + " WHERE " + col + " = \"" + status + "\" ORDER BY " + timeStampColumn + " DESC "
                + "LIMIT " + max;

        //SQLiteDatabase db_cursor = dbContext.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READONLY, null);
        SQLiteDatabase db_select = dbHelper.getReadableDatabase();
        /*TODO while (db_select.inTransaction() ){
            //Wait for it to finish
        }*/
        if (db_select != null) {

            try {
            db_select.beginTransaction();
                //Create Cursor
                //Choose the right array from table
                Cursor cursorSync = db_select.rawQuery(selectQuery, null);

                if (cursorSync != null) {
                    int res = 0;
                    try {
                        //If Cursor is valid
                        int values = cursorSync.getCount();
                        db_select.setTransactionSuccessful();
                        res =  values;

                    } catch (Exception e) {
                        res =  0;
                    } finally {
                        //Close cursor
                        cursorSync.close();
                        if (db_select.inTransaction())
                            db_select.endTransaction();
                        //TODO db.close();
                        return res;
                    }
                } else {
                    //Close db
                    db_select.setTransactionSuccessful();

                    if (db_select.inTransaction())
                        db_select.endTransaction();
                    //TODO db.close();
                    return 0;
                }

            }catch(Exception e){
                System.out.println("Begin trans error - "+e);
                return 0;
            }finally {
                if (db_select.inTransaction())
                    db_select.endTransaction();

            }
        } else {
            /*db_select.setTransactionSuccessful();
            if (db_select.inTransaction())
                db_select.endTransaction();
            //TODO db.close();
            return 0;*/
            return 0;
        }
    }

    public  int getOlderSamplesNumber(String table, String col, String last_update){
        //Ordered selection
        String selectQuery = "SELECT  * FROM " + table + " WHERE " + col + " < \"" + last_update;
        System.out.println("get older query "+selectQuery);
        SQLiteDatabase db_select = dbHelper.getReadableDatabase();

        //Obtain a cursor and count its entries
        if (db_select != null) {
            //Create Cursor
            //Choose the right array from table
            Cursor cursorSync = db_select.rawQuery(selectQuery, null);

            if (cursorSync != null) {
                try {
                    //If Cursor is valid
                    int values = cursorSync.getCount();
                    //Close cursor
                    cursorSync.close();
                    //Close db
                    return values;

                } catch (Exception e) {
                    //Close cursor and db:
                    cursorSync.close();

                    return 0;
                }
            } else {
                //Close db
                return 0;
            }

        }else{
            return 0;
        }

    }



    /**
     * Compose JSON out of SQLite records
     * @return
     */

    public List<Map<String, String>> getLastNSamples (String table, String[] columns, int max_samples) {


        List<Map<String, String>> wordList = new ArrayList<Map<String, String>>();
        String selectQuery = "SELECT  * FROM " + table +" ORDER BY "+timeStampColumn+" ASC";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //SQLiteDatabase db_cursor = dbContext.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READONLY, null);

        db.beginTransaction();
        try{

            if (db !=null) {
                //Create Cursor
                //Choose the right array from table
                Cursor cursorSync = db.rawQuery(selectQuery, null);
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
                            map.put(upDateColumn, cursorSync.getString(columns.length + 1));
                            //Include sync value:
                            map.put(syncColumn, cursorSync.getString(columns.length));

                            wordList.add(map);
                            index++;

                        } while (cursorSync.moveToPrevious() && index < max_samples);
                        System.out.println("Done reading: " + index +" vs max "+max_samples);
                    }
                    cursorSync.close();
                    db.setTransactionSuccessful();
                }


            }
        }catch (Exception e){
            Log.d(TAG, "Exception retrieving not updated values: "+e);
        }finally{
            if (db.isOpen() && db.inTransaction())
                db.endTransaction();        }

        //Use GSON to serialize Array List to JSON
        return wordList;
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
     * @param status synchronize status
     * @param last_up last_updated value (acts as id)
     */
    public void updateSyncStatus(String table, String status_col, String status, String last_up){

        //SQLiteDatabase database = ctx.openOrCreateDatabase(databaseFile, Context.MODE_WORLD_WRITEABLE, null);
        //SQLiteDatabase db1=ctx.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READWRITE, null);
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            try {
                db.beginTransaction();

                //Update query: update status, searching the right last_updated value
                String updateQuery = "UPDATE " + table + " SET " + status_col + " = '" + status + "' WHERE " + timeStampColumn + " = '" + last_up + "'";
                //System.out.println("Update values: "+updateQuery);
                //Log.d("query", updateQuery);
                //System.out.println("^^^^^^^^^^^UPDATED???: " + updateQuery);
                db.execSQL(updateQuery);
                db.setTransactionSuccessful();
            } catch (SQLiteException e) {
                Log.d(TAG, "Error updating values in " + table + ": " + e);
                System.out.println("SQLite Exception while sync values in table: " + e);

            } finally {
                if (db.inTransaction())
                    db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException eo){
            System.out.println("Error getting writable db to update sync values: " + eo);

        }
    }

    public void runAckSync(String table,  String status, String last_up){
        myACK.presetRunningParams(this, table, status, last_up);
        new Thread(myACK).start();

    }

    /**
     * Update all previous values as sync/updated, given a time_stamped sample
     * Works as a cummulative ACK (kind of ~)
     * @param table - table name
     * @param status - yes/no
     * @param last_up - time_stamp acknowledged
     * @return
     */

    public boolean ackSyncStatusAllPrevious (String table,  String status, String last_up){
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
            try{

                if ( db  !=null) {
                    db.beginTransaction();
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

                                updateSyncStatus(table,syncColumn, syncStatusYes,last_updated);
                                index ++;
                                if (index%CURSOR_WINDOW_LIMIT == 0){
                                    //Get new cursor!
                                    cursorSync.close();
                                    cursorSync = db.query(table, null, syncColumn+" = \"" + syncStatusNo +"\"",null, null, null, timeStampColumn+" ASC", null);
                                    cursorSync.moveToFirst();
                                    //Update first sample
                                    updateSyncStatus(table,syncColumn, syncStatusYes,last_updated);
                                }

                            } while (!last_up.equals(last_updated) && cursorSync.moveToNext()   );


                            System.out.println("Done ACK reading: "+index);
                        }
                    }
                    cursorSync.close();

                }
                db.setTransactionSuccessful();
            }catch (SQLiteException e){
                System.out.println("Error with ACK function, read table: "+e);
            } finally {
                if (db.inTransaction())
                    db.endTransaction();

            }
        }

        //TODO Garbage collector
        System.gc();

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
        //TODO db.close();
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

