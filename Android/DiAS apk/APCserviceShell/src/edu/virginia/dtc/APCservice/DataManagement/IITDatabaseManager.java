package edu.virginia.dtc.APCservice.DataManagement;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.virginia.dtc.SysMan.Debug;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

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


	private static SQLiteDatabase db;
	private static Cursor cursorSync;


	//Database info
	private static String DB_LOCAL_URL =  "/storage/emulated/legacy/IIT_database/";
	public static String DB_NAME = "dbSensors.db";

	static String databaseFile;
	//Time_Stamp column name:
	private static String timeStampColumn;
	//Updated : for the server
	private static final String updatedStatusNo = "'n'";
	private static final String updatedStatusYes = "'y'";
	private static final String upDateColumn = "upDateStatus";
	//Syncrhonized : for the internal database
	private static final String syncStatusNo = "'n'";
	private static final String syncStatusYes = "'y'";
	private static final String syncColumn = "synchronized";
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

	/**
	 * Default constractor, only needs context
	 * @param ctx context
	 */
	public IITDatabaseManager(Context ctx){
		initDatabase(ctx);

	}

	/**
	 * Constructor to set up the name of the database
	 * @param ctx context
	 * @param databaseName name of the database
	 */
	public IITDatabaseManager(Context ctx, String databaseName){
		DB_NAME = databaseName;
		initDatabase(ctx);

	}


	/*
    * initDatabase ()
     */
	private void initDatabase(Context ctx){
		dbContext = ctx;
		//Update database file:
		databaseFile = DB_LOCAL_URL+DB_NAME;

		//Create database:
		db=ctx.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READWRITE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS sample_table (created CHAR(1))");
		db.close();
		initialized= true;

	}


	/**
	 * Update table in database
	 * @param table
	 * @param values
	 * @param store whether to store new values or delete table
	 */
	public void updateDatabaseTable (String table, List<String> values, boolean store){
		//Open db
		db=dbContext.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READWRITE, null);
		if (store){
			//Store or update new values on table
			storeValuesInTable(table, values);
		}else{
			//Delete table
			// String updateQuery1 = "DELETE * FROM "+ table ;
			String updateQuery1 = "DROP TABLE "+ table ;
			db.execSQL(updateQuery1);
		}
		//Close db
		db.close();


		/* String updateQuery = "CREATE TABLE IF NOT EXISTS "+ table+"(" + columnValues +");";
		 db.execSQL(updateQuery);*/
		//System.out.println(updateQuery);


	}


	/**
	 * Store values in Table
	 * @param table name of the table
	 * @param values List of the values: should be Strings of type 'value1', 'value2'...
	 */
	private void storeValuesInTable(String table, List<String> values){

		try{
			String inQueryValues = "(";
			//Build the store query
			for (int i =0; i<values.size(); i++){
				//System.out.println("---------"+database+":"+values.get(i)+"-------------");
				inQueryValues += values.get(i)+", ";
				//Exec query if it does not include a null value and the last char is ")"

			}
			//Last column: updated in phone = no
			inQueryValues += syncStatusNo + ", " +updatedStatusNo+")";
			System.out.println(inQueryValues);

			//Execute query
			if (!inQueryValues.contains("null") && inQueryValues.substring(inQueryValues.length() - 1).equals(")"))
				db.execSQL("INSERT INTO " + table + " VALUES" + inQueryValues + ";");
		}catch(SQLiteException e){
			System.out.println("SQLite Exception while storing in table: "+e);

		}

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
			db = dbContext.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READWRITE, null);
			String initTable = "CREATE TABLE IF NOT EXISTS " + table + columnsQuery;
			db.execSQL(initTable);
			db.close();
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
	 * Read values from a given database
	 * @param ctx
	 * @param database
	 * @param column
	 * @return
	 */

	static public List<String> readFromDatabase(Context ctx, String database, String column){
		List<String> results = new ArrayList();
		// SQLiteDatabase dbase = ctx.openOrCreateDatabase(databaseFile, Context.MODE_WORLD_WRITEABLE, null);
		SQLiteDatabase dbase=ctx.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READWRITE, null);

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
		Debug.i("IIT_DATABASE", "readFromTalbeNotSyncRows", "Open db");
		db=dbContext.openOrCreateDatabase(databaseFile,SQLiteDatabase.OPEN_READWRITE, null);

		if (db !=null){
			//TODO Verify table exists in the db

			//Create Cursor
			//String query = "SELECT * FROM " + tableRead +" WHERE "+syncColumn+ " = " + syncStatusNo + ";";
			String query = "SELECT * FROM " + tableRead ;
			//TODO!!! Failing queryDebug.i("IIT_DATABASE", "readFromTalbeNotSyncRows", "Read query:"+ query);
			//Cursor c = db.rawQuery(query, null);

			//If Cursor is valid
			/*if (c != null ) {
				String[] cols = c.getColumnNames();
				//Move cursor to first row
				if  (c.moveToFirst()) {
					do{
						//Values to send to server
						Map <String, String> partial = new HashMap<String, String>();
						//Values to update local database
						List<String> update = new ArrayList<String>();
						//Init the upgrade query
						//String updateQuery = "(";
						for (String column: cols){
							String val = "0";
							//String val1 = "0";

							//Get the corresponding value
							if (column.equals("synchronized")){
								//The syncrhonized value will be add in when it is stored
								val = syncStatusYes;
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
						//TODO updateSyncInTable(tableRead, update);

						//Include table values
						partial.put("table_name", tableNameOnServer);
						resultArgs.add(partial);
					} while (c.moveToNext());
				}
			}
			//Close cursor and database
			c.close();*/
			db.close();
		}

		return resultArgs;

	}

	/**
	 * Update the synchronized values on table
	 * @param table - name of the table
	 * @param lasUpdates - list of  lastUpdates to be updated
	 */

	private void updateSyncInTable(String table, List<String> lasUpdates){
		//"Update users set udpateStatus = '"+ status +"' where userId="+"'"+ id +"'"

		for (String id: lasUpdates){
			Debug.i("IIT_DATABASE", "readFromTalbeNotSyncRows", "Read update value:"+ table);
			db.execSQL("UPDATE "+table+ " SET synchronized = "+syncStatusYes+" WHERE last_update = " + id +" ;");

		}
	}




/**********************************************************************************************
 *** Server communication part
 ********************************************************************************************/

	/**
	 * Compose JSON out of SQLite records
	 * @return
	 */

	public static String composeJSONfromSQLite(Context ctx, String table, int next) {

		ArrayList<HashMap<String, String>> wordList = null;
		wordList = new ArrayList<HashMap<String, String>>();
		List<String> lastUpd = new ArrayList();
		String selectQuery = "SELECT  * FROM " + table + " WHERE "+upDateColumn+" = '" + updatedStatusNo + "'";

		//First case: for zephyr table
        /*if (table.equals(zephyrTableName)) {
            //SQLiteDatabase database = ctx.openOrCreateDatabase(databaseFile, Context.MODE_WORLD_WRITEABLE, null);
            if (next == 0) {
                db = ctx.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READWRITE, null);
                //Array of columns
                //Choose the right array from table
                cursorSync = db.rawQuery(selectQuery, null);
                if (cursorSync.moveToFirst()) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    //Add the name of the table first
                    //map.put("table_name", MainActivityZephyr.dispTableId);
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
                //map.put("table_name", MainActivityZephyr.dispTableId);
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
        else if (table.equals(bodymediaTableName)) {
            if (next == 0) {
                db = ctx.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READWRITE, null);
                //Array of columns
                //Choose the right array from table
                cursorSync = db.rawQuery(selectQuery, null);
                if (cursorSync.moveToFirst()) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    //map.put("table_name", MainActivityBM.dispTableName);
                    for (int i = 0; i < bodymediaColumns.size() - 1; i++) {
                        map.put(bodymediaColumns.get(i), cursorSync.getString(i));
                        lastUpd.add(cursorSync.getString(i));
                    }
                    wordList.add(map);

                }
            } else if (cursorSync.moveToNext()) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("table_name", bodymediaTableName);
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

            //Fourth case: Empatica
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
        }


            Gson gson = new GsonBuilder().create();
            //Use GSON to serialize Array List to JSON
            return gson.toJson(wordList);*/
		return "";
	}




	/**
	 * Get SQLite records that are yet to be Synced
	 * @return
	 */
	public static int dbSyncCount(Context ctx, String table){
		int count = 0;
		String selectQuery = "SELECT  * FROM "+table+" WHERE "+upDateColumn+" = '"+updatedStatusNo+"'";
		//SQLiteDatabase database = ctx.openOrCreateDatabase(databaseFile, Context.MODE_WORLD_WRITEABLE, null);
		db=ctx.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READWRITE, null);

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
		db=dbContext.openOrCreateDatabase(databaseFile,SQLiteDatabase.OPEN_READWRITE, null);
		if (store){
			//Store or update new values on table
			storeInTable(table, values);
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
	private void storeInTable(String table, List<String> values){

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
				Debug.i("IITDatabaseManager", "store db", "DID NOT UPDATE DATABASE ");
		}catch(SQLiteException e){
			System.out.println("SQLite Exception while storing in table: "+e);
			Debug.i("what", "store db", "DID NOT UPDATE DATABASE: "+ e);


		}

	}

	/**
	 * Update Sync status against each User ID
	 * @param ctx context from activity
	 * @param status synchronize status
	 * @param last_up last_updated value (acts as id)
	 */
	public static void updateSyncStatus(Context ctx, String table, String status, String last_up){

		//SQLiteDatabase database = ctx.openOrCreateDatabase(databaseFile, Context.MODE_WORLD_WRITEABLE, null);
		db=ctx.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READWRITE, null);

		//Update query: update status, searching the right last_updated value
		String updateQuery = "UPDATE "+table +" SET "+upDateColumn+" = '"+status+"' WHERE "+timeStampColumn+" = '"+ last_up+"'";
		//System.out.println("Synchronizing Database!!"+ updateQuery);
		Log.d("query", updateQuery);
		// System.out.println("^^^^^^^^^^^UPDATED???: " + updateQuery);

		db.execSQL(updateQuery);
		db.close();
	}

    /*
    * countRows
     */

	public static int countRows(Context ctx, String table){
		int rows = 0;
		db=ctx.openOrCreateDatabase(databaseFile, SQLiteDatabase.OPEN_READWRITE, null);

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


}