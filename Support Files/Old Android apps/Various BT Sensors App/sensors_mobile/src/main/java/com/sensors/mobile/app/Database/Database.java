package com.sensors.mobile.app.Database;

import java.sql.Timestamp;
import java.util.*;


import com.sensors.mobile.app.BM.MainActivityBM;
import com.sensors.mobile.app.zephyr.MainActivityZephyr;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.database.Cursor;

public class Database {

    public static List<String> zephyrColumns = new ArrayList();
    public static List<String> bodymediaColumns = new ArrayList();
    public static List<String> dexcomColumns = new ArrayList();
    private static int first;

    private static String zephyrColumnValues = "";
    private static String bodymediaColumnValues = "";
    private static String dexcomColumnValues = "";

    private static SQLiteDatabase db;
    private static Cursor cursorSync;
    //private  SQLiteDatabase databaseSync;
    public static String zephyrTableName = "zephyr";
    public static String bodymediaTableName = "bodymedia";
    public static String dexcomTableName = "dexcom";

    static String databaseFile;
    //static String  DBPath ;
    // static String DBName ;
    //static File db_file;
    //static String databaseFile = "/sdcard/Android/data/com.ece.NewApp/ZephyrDB";
    //static File sdcard = Environment.getExternalStorageDirectory();
    //static String databaseFile = sdcard.getAbsolutePath() + File.separator+ "external_sd" + File.separator + "ZephyrDB" ;
    //static String databaseFile = sdcard.getAbsolutePath() + File.separator + "ZephyrDB" ;


    private static boolean initialized = false;

    public Database(Context ctx){
        first = 1;

        initDatabase(ctx);
    }

    /*
    * init ZephyrDatabase
     */
    @SuppressWarnings("deprecation")
    private static void initZephyrTable(Context ctx) {
        zephyrColumns.clear();
        zephyrColumns.add("posture");
        zephyrColumns.add("activity");
        zephyrColumns.add("heart_rate");
        zephyrColumns.add("breath_rate");
        zephyrColumns.add("vertical_min");
        zephyrColumns.add("vertical_peak");
        zephyrColumns.add("lateral_min");
        zephyrColumns.add("lateral_peak");
        zephyrColumns.add("sagital_min");
        zephyrColumns.add("sagital_peak");
        zephyrColumns.add("peak_accel");
        zephyrColumns.add("ecg_amplitude");
        zephyrColumns.add("ecg_noise");
        zephyrColumns.add("heart_rate_confidence");
        zephyrColumns.add("system_confidence");
        zephyrColumns.add("battery_level");
        zephyrColumns.add("link_quality");
        zephyrColumns.add("rssi");
        zephyrColumns.add("tx_power");
        zephyrColumns.add("device_temperature");
        zephyrColumns.add("hrv");
        zephyrColumns.add("rog");
        zephyrColumns.add("rog_time");
        zephyrColumns.add("last_update");
        zephyrColumns.add("upDateStatus");

        //Create SQL command to create table
        for (int i = 0; i < zephyrColumns.size(); i++) {

            if (i < 2)
                zephyrColumnValues += zephyrColumns.get(i) + " FLOAT, ";
            else if (i < zephyrColumns.size() - 3)
                zephyrColumnValues += zephyrColumns.get(i) + " DOUBLE, ";
            else if (i == (zephyrColumns.size() - 3))
                zephyrColumnValues += zephyrColumns.get(i) + " TIME, ";
            else if (i == (zephyrColumns.size() - 2))
                zephyrColumnValues += zephyrColumns.get(i) + " VARCHAR, ";
            else
                zephyrColumnValues += zephyrColumns.get(i) + " VARCHAR";

        }


    }

    public static void initBodymediaTable(Context ctx){
        bodymediaColumns.clear();
        bodymediaColumns.add("activity_type");
        bodymediaColumns.add("heart_rate");
        bodymediaColumns.add("longitudinal_accel");
        bodymediaColumns.add("lateral_accel");
        bodymediaColumns.add("transverse_accel");
        bodymediaColumns.add("long_accel_peak");
        bodymediaColumns.add("lat_accel_peak");
        bodymediaColumns.add("tran_accel_peak");
        bodymediaColumns.add("long_accel_avg");
        bodymediaColumns.add("lat_accel_avg");
        bodymediaColumns.add("tran_accel_avg");
        bodymediaColumns.add("long_accel_mad");
        bodymediaColumns.add("lat_accel_mad");
        bodymediaColumns.add("tran_accel_mad");


        bodymediaColumns.add("skin_temp");
        bodymediaColumns.add("gsr");
        bodymediaColumns.add("cover_temp");
        bodymediaColumns.add("skin_temp_avg");
        bodymediaColumns.add("gsr_avg");
        bodymediaColumns.add("heat_flux_avg");


        bodymediaColumns.add("steps");
        bodymediaColumns.add("sleep");
        bodymediaColumns.add("calories");
        bodymediaColumns.add("vigorous");
        bodymediaColumns.add("METs");
        bodymediaColumns.add("memory");
        bodymediaColumns.add("battery");
        bodymediaColumns.add("last_update");
        bodymediaColumns.add("upDateStatus");

        //Create SQL command to create table
        for (int i = 0; i < bodymediaColumns.size(); i++) {

            if (i == 0)
                bodymediaColumnValues += bodymediaColumns.get(i) + " VARCHAR, ";
            else if (i < bodymediaColumns.size() - 2)
                bodymediaColumnValues += bodymediaColumns.get(i) + " DOUBLE, ";
            else if (i == (bodymediaColumns.size() - 2))
                bodymediaColumnValues += bodymediaColumns.get(i) + " VARCHAR, ";
            else if (i == (bodymediaColumns.size() - 1))
                bodymediaColumnValues += bodymediaColumns.get(i) + " VARCHAR";


        }


    }

    public static void initDexcom(Context ctx) {
        dexcomColumns.clear();

        dexcomColumns.add("glucose");
        dexcomColumns.add("last_update");
        dexcomColumns.add("upDateStatus");

        dexcomColumnValues = dexcomColumns.get(0) +" VARCHAR," + dexcomColumns.get(1) +" VARCHAR,"  + dexcomColumns.get(2) +" VARCHAR";
    }
    /*
    * initDatabase ()
     */
    public static void initDatabase(Context ctx){

        first = 1;
        //Initialize zephyr
        initZephyrTable(ctx);

        //Initialize Bodymedia
        initBodymediaTable(ctx);

        //Initialize Dexcom
        initDexcom(ctx);

        //Open/create database
        //Get day
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        //databaseFile =  ctx.getApplicationInfo().dataDir + "/databases/ZephyrDB"+ cal.get(Calendar.YEAR) +cal.get(Calendar.MONTH) +cal.get(Calendar.DATE) ;


        databaseFile = ctx.getApplicationInfo().dataDir + "/databases/SensorsDB";

        //databaseFile = "/sdcard/db/SensorsDB";

        //System.out.println("!!!Database direction: "+databaseFile);

        //File file = new File("/scratch/android-sdk-linux/tools/os.sqlite");
        //db=ctx.openOrCreateDatabase(databaseFile, Context.MODE_WORLD_WRITEABLE, null);
        // db=ctx.openOrCreateDatabase(databaseFile,SQLiteDatabase.OPEN_READWRITE, null);

        // DBPath = ctx.getFilesDir().getName()+"/com.ece.NewApp/databases/";
        // DBName = "ZephyrDB";
        // db_file = new File(DBPath, DBName);
        // databaseFile = db_file.getAbsolutePath();

        db=ctx.openOrCreateDatabase(databaseFile,SQLiteDatabase.OPEN_READWRITE, null);

        String updateQuery = "CREATE TABLE IF NOT EXISTS " + zephyrTableName + "(" + zephyrColumnValues + ");";
        db.execSQL(updateQuery);
        //updateQuery = "DELETE FROM " +zephyrTableName ;
        //db.execSQL(updateQuery);




        updateQuery = "CREATE TABLE IF NOT EXISTS " + bodymediaTableName + "(" + bodymediaColumnValues + ");";
        db.execSQL(updateQuery);

        updateQuery = "CREATE TABLE IF NOT EXISTS " + dexcomTableName + "(" + dexcomColumnValues + ");";
        db.execSQL(updateQuery);

        db.close();
        initialized= true;
    }




    /*
    * updateDatabase
     */
    public static void updateDatabase(Context ctx, ThreadSafeArrayList values1, String table){
        if (!initialized){
            initDatabase(ctx);
        }

        db=ctx.openOrCreateDatabase(databaseFile,SQLiteDatabase.OPEN_READWRITE, null);
        //String updateQuery1 = "DELETE FROM "+ databaseName ;
        //db.execSQL(updateQuery1);

		 /*String updateQuery = "CREATE TABLE IF NOT EXISTS zephyr1(Hour VARCHAR, Minute VARCHAR, Second VARCHAR, Posture VARCHAR, Activity VARCHAR, "
			 		+ "Heartrate VARCHAR, BreathRate VARCHAR, "+ "VerticalMin VARCHAR, VerticalPeak VARCHAR, LateralMin VARCHAR,"
			 		+ " LateralPeak VARCHAR, SagittalMin VARCHAR, SagittalPeak VARCHAR, PeakAcceleration VARCHAR,"
			 		+ "ECGAmplitude VARCHAR, ECGNoise VARCHAR, BatteryStatus VARCHAR, LinkQuality VARCHAR, ROGStatus VARCHAR, updateStatus VARCHAR);";*/
        if (table.equals(zephyrTableName)){
            //Open/Create zephyr table
            String updateQuery = "CREATE TABLE IF NOT EXISTS " + table + "(" + zephyrColumnValues + ");";
            db.execSQL(updateQuery);
        } else if (table.equals(bodymediaTableName)){
            //Delete previous bodymedia tables
            //String updateQuery1 = "DROP TABLE " + table;
            //db.execSQL(updateQuery1);

            //Open/Create bodymedia table
            String updateQuery = "CREATE TABLE IF NOT EXISTS " + table + "(" + bodymediaColumnValues + ");";
            db.execSQL(updateQuery);
            //System.out.println("Open or create bodymedia: "+ updateQuery);

        } else if (table.equals(dexcomTableName)){
            String updateQuery = "CREATE TABLE IF NOT EXISTS " + table + "(" + dexcomColumnValues + ");";
            db.execSQL(updateQuery);
           // System.out.println("Open or create dexcom: "+ updateQuery);

        }
        db.close();
        //Add values to zephyr database
        storeInDatabase(ctx, table, values1);





    }

    /*
    * storeInDatabase
     */

    static private void storeInDatabase( Context ctx, String table, ThreadSafeArrayList values1){
        //Add the stored values


        SQLiteDatabase sqlite =ctx.openOrCreateDatabase(databaseFile,SQLiteDatabase.OPEN_READWRITE, null);

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

    /*
    * readFromDatabase
     */

    static public List<String> readFromDatabase(Context ctx, String database, String column){
        List<String> results = new ArrayList();
        // SQLiteDatabase dbase = ctx.openOrCreateDatabase(databaseFile, Context.MODE_WORLD_WRITEABLE, null);
        SQLiteDatabase dbase=ctx.openOrCreateDatabase(databaseFile,SQLiteDatabase.OPEN_READWRITE, null);

        //Create Cursor object to read versions from the table
        Cursor c = dbase.rawQuery("SELECT "+column+" FROM " + database, null);

        //If Cursor is valid
        if (c != null ) {

            //Move cursor to first row
            if  (c.moveToFirst()) {
                do {
                    //Get value for column
                    String val = c.getString(c.getColumnIndex(column));
                    //Add the value to Arraylist 'results'
                    results.add(val);
                }while (c.moveToNext()); //Move to next row
            }
        }

        //Print results
        //for (int i = 0; i < results.size(); i++)
        //Log.d("Database",results.get(i));
        dbase.close();
        return results;
    }




/**********************************************************************************************
 *** Server communication part
 ********************************************************************************************/

    /**
     * Compose JSON out of SQLite records
     * @return
     */

    public static String composeJSONfromSQLite(Context ctx, String table, int next){

        ArrayList<HashMap<String, String>> wordList = null;
        wordList = new ArrayList<HashMap<String, String>>();
        List<String> lastUpd = new ArrayList();
        String selectQuery = "SELECT  * FROM "+table+" WHERE upDateStatus = '" + "no" + "'";

        //First case: for zephyr table
        if (table.equals(zephyrTableName)) {
            //SQLiteDatabase database = ctx.openOrCreateDatabase(databaseFile, Context.MODE_WORLD_WRITEABLE, null);
            if (next == 0) {
                db = ctx.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READWRITE, null);
                //Array of columns
                //Choose the right array from table
                cursorSync = db.rawQuery(selectQuery, null);
                if (cursorSync.moveToFirst()) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    //Add the name of the table first
                    map.put("table_name", MainActivityZephyr.dispTableId);
                    //map.put("create", String.valueOf(first));
                    first = 0;
                    for (int i = 0; i < zephyrColumns.size() - 1; i++) {
                        map.put(zephyrColumns.get(i), cursorSync.getString(i));
                        lastUpd.add(cursorSync.getString(i));
                    }
                    wordList.add(map);

                }
            } else if (cursorSync.moveToNext()) {
                HashMap<String, String> map = new HashMap<String, String>();
                //Add the name of the table first
                map.put("table_name", MainActivityZephyr.dispTableId);
                for (int i = 0; i < zephyrColumns.size() - 1; i++) {
                    map.put(zephyrColumns.get(i), cursorSync.getString(i));
                    lastUpd.add(cursorSync.getString(i));


                }
                wordList.add(map);
                if (next == dbSyncCount(ctx, table)) {
                    db.close();
                }
            }
        }
        //Second case: BodyMedia table
        else if(table.equals(bodymediaTableName)){
            if (next == 0) {
                db = ctx.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READWRITE, null);
                //Array of columns
                //Choose the right array from table
                cursorSync = db.rawQuery(selectQuery, null);
                if (cursorSync.moveToFirst()) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("table_name", MainActivityBM.dispTableName);
                    for (int i = 0; i < bodymediaColumns.size() - 1; i++) {
                        map.put(bodymediaColumns.get(i), cursorSync.getString(i));
                        lastUpd.add(cursorSync.getString(i));
                    }
                    wordList.add(map);

                }
            } else if (cursorSync.moveToNext()) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("table_name", MainActivityBM.dispTableName);
                for (int i = 0; i < bodymediaColumns.size() - 1; i++) {
                    map.put(bodymediaColumns.get(i), cursorSync.getString(i));
                    lastUpd.add(cursorSync.getString(i));
                }
                wordList.add(map);
                if (next == dbSyncCount(ctx, table)) {
                    db.close();
                }
            }
        }
        //Third case Dexcom
        else if (table.equals(dexcomTableName)) {
            //SQLiteDatabase database = ctx.openOrCreateDatabase(databaseFile, Context.MODE_WORLD_WRITEABLE, null);
            if (next == 0) {
                db = ctx.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READWRITE, null);
                //Array of columns
                //Choose the right array from table
                cursorSync = db.rawQuery(selectQuery, null);
                if (cursorSync.moveToFirst()) {

                    HashMap<String, String> map = new HashMap<String, String>();
                    //TODO Dexcom table name
                    //map.put("table_name", MainActivityBM.dispTableName);

                    for (int i = 0; i < dexcomColumns.size() - 1; i++) {
                        map.put(dexcomColumns.get(i), cursorSync.getString(i));
                        lastUpd.add(cursorSync.getString(i));
                    }
                    wordList.add(map);

                }
            } else if (cursorSync.moveToNext()) {
                HashMap<String, String> map = new HashMap<String, String>();
                //TODO Dexcom table name
                //map.put("table_name", MainActivityBM.dispTableName);
                for (int i = 0; i < dexcomColumns.size() - 1; i++) {
                    map.put(dexcomColumns.get(i), cursorSync.getString(i));
                    lastUpd.add(cursorSync.getString(i));
                }
                wordList.add(map);
                if (next == dbSyncCount(ctx, table)) {
                    db.close();
                }
            }
        }


        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }



    /**
     * Get SQLite records that are yet to be Synced
     * @return
     */
    public static int dbSyncCount(Context ctx, String table){
        int count = 0;
        String selectQuery = "SELECT  * FROM "+table+" WHERE upDateStatus = '"+"no"+"'";
        //SQLiteDatabase database = ctx.openOrCreateDatabase(databaseFile, Context.MODE_WORLD_WRITEABLE, null);
        db=ctx.openOrCreateDatabase(databaseFile,SQLiteDatabase.OPEN_READWRITE, null);

        Cursor cursor = db.rawQuery(selectQuery, null);
        count = cursor.getCount();
        //System.out.println("To synchronize.... "+ count);
        db.close();
        return count;
    }

    /**
     * Update Sync status against each User ID
     * @param ctx context from activity
     * @param status synchronize status
     * @param last_up last_updated value (acts as id)
     */
    public static void updateSyncStatus(Context ctx, String table, String status, String last_up){

        //SQLiteDatabase database = ctx.openOrCreateDatabase(databaseFile, Context.MODE_WORLD_WRITEABLE, null);
        db=ctx.openOrCreateDatabase(databaseFile,SQLiteDatabase.OPEN_READWRITE, null);

        //Update query: update status, searching the right last_updated value
        String updateQuery = "UPDATE "+table +" SET upDateStatus = '"+status+"' WHERE last_update = '"+ last_up+"'";
        //System.out.println("Synchronizing Database!!"+ updateQuery);
        Log.d("query",updateQuery);
       // System.out.println("^^^^^^^^^^^UPDATED???: " + updateQuery);

        db.execSQL(updateQuery);
        db.close();
    }

    /*
    * countRows
     */

    public static int countRows(Context ctx, String table){
        int rows = 0;
        db=ctx.openOrCreateDatabase(databaseFile,SQLiteDatabase.OPEN_READWRITE, null);

        //Create Cursor object to read versions from the table
        Cursor c = db.rawQuery("SELECT "+"last_update"+" FROM " + table, null);
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

}

