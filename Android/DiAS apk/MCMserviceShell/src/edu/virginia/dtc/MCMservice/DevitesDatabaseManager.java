package edu.virginia.dtc.MCMservice;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.virginia.dtc.SysMan.Debug;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

/**
 * DevitesDatabaseManager is a class to control the writing and reading to a local database
 * 
 * Uses sqlite libraries
 * @author Caterina Lazaro
 * @version 1.0, November 2015 
 */

public class DevitesDatabaseManager {
	//Database params
	//TODO Change for every app
	//For Nexus 5 Dias:
	private static String DB_LOCAL_URL = "/storage/emulated/legacy";
	//private  static  String DB_LOCAL_URL = "/mnt/shell/emulated/0";
	private SQLiteDatabase db;
	private String DB_FILE;
	//Bodymedia table
	public static final String TABLE_NAME_BM = "bodymedia";
	public static final String TABLE_NAME = "timerTable";

	//Context of the application
	private Context dbContext;

	/**
	 * Constructor
	 * @param ctx - context
	 * @param database - name of database
	 */
	public DevitesDatabaseManager(Context ctx, String database){
		//Set the context
		dbContext = ctx;
		//DB_LOCAL_URL =  ctx.getFilesDir().getAbsolutePath();
		//DB_LOCAL_URL = Environment.getDataDirectory();
		//Set the file location of database and create it
		//DB_FILE =  Environment.getRootDirectory().getAbsolutePath() +"/direct/"+database;
		DB_FILE = DB_LOCAL_URL  +"/direct/"+database;
		db=dbContext.openOrCreateDatabase(DB_FILE,SQLiteDatabase.OPEN_READWRITE, null);
		db.close();
	}

	/**
	 * Constructor
	 * @param ctx - context
	 * @param database - name of database
	 * @param table - name of table
	 * @param columnsOfTable
	 */
	public DevitesDatabaseManager(Context ctx, String database, String table, List<String> columnsOfTable){
		//Set the context
		dbContext = ctx;
		//Set the file location of database and create it
		DB_FILE =  DB_LOCAL_URL +"/direct/"+database;
		db=dbContext.openOrCreateDatabase(DB_FILE,SQLiteDatabase.OPEN_READWRITE, null);
		db.close();
		//Create table
		createTable(table, columnsOfTable);
	}

	/**
	 * Create new table
	 * @param table - name of the table to create
	 * @param columnsOfTable - column names the table has
	 */
	public void createTable(String table, List<String> columnsOfTable){
		String columnsQuery = prepareColumnsFromList(columnsOfTable);
		db=dbContext.openOrCreateDatabase(DB_FILE,SQLiteDatabase.OPEN_READWRITE, null);
		String initTable = "CREATE TABLE IF NOT EXISTS "+table+" "+columnsQuery+";";
		db.execSQL(initTable);		
		db.close();
	}

	/**
	 * Update table in database
	 * @param table
	 * @param values
	 * @param store
	 */
	public void updateNewValuesDatabase(String table, List<String> values, boolean store){
		//Open db
		db=dbContext.openOrCreateDatabase(DB_FILE,SQLiteDatabase.OPEN_READWRITE, null);
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
				Debug.i("what", "store db", "DID NOT UPDATE DATABASE ");
		}catch(SQLiteException e){
			System.out.println("SQLite Exception while storing in table: "+e);
			Debug.i("what", "store db", "DID NOT UPDATE DATABASE: "+ e);


		}

	}
	/**
	 * Update the syncrhonized values on table
	 * @param table - name of the table
	 * @param lasUpdates - list of  lastUpdates to be updated
	 */
	
	private void updateSyncInTable(String table, List<String> lasUpdates){
		//"Update users set udpateStatus = '"+ status +"' where userId="+"'"+ id +"'"
		for (String id: lasUpdates){
			db.execSQL("UPDATE "+table+ " SET synchronized = 'y' WHERE last_update = " + id +" ;");
			
		}
	}
	/**
	 * Read values from table
	 * @param table
	 * @param column
	 * @return
	 */

	public List<String> readFromTable(String table, String column){
		List<String> results = new ArrayList();

		// SQLiteDatabase dbase = ctx.openOrCreateDatabase(databaseFile, Context.MODE_WORLD_WRITEABLE, null); 
		db=dbContext.openOrCreateDatabase(DB_FILE,SQLiteDatabase.OPEN_READWRITE, null);

		//Create Cursor object to read versions from the table
		Cursor c = db.rawQuery("SELECT " +column +" FROM " + table, null);

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
		c.close();

		//Print results
		//for (int i = 0; i < results.size(); i++)
		//Log.d("Database",results.get(i));
		db.close();
		return results;
	}
	/**
	 * Read a row from the table
	 * @param table
	 * @param rowFromLast
	 * @return map with pairs column-value
	 */
	public void readFromTableAllColumns(String tableRead, String tableSend, List<Map <String, String>> resultArgs){
		//Open database
		db=dbContext.openOrCreateDatabase(DB_FILE,SQLiteDatabase.OPEN_READWRITE, null);

		//Create Cursor
		Cursor c = db.rawQuery("SELECT * FROM " + tableRead +" WHERE synchronized = 'n'", null);

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
					//Init the upgrade query
					//String updateQuery = "(";
					for (String column: cols){
						String val = "0";
						//String val1 = "0";

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
					updateSyncInTable(tableRead, update);
					
					//Include table values
					partial.put("table_name", tableSend);
					resultArgs.add(partial);
				} while (c.moveToNext());
			}
		}
		//Close cursor and database
		c.close();
		db.close();

	}
	/**
	 * 
	 * @param tableRead
	 * @param tableSend
	 * @param rowFromLast
	 * @return
	 */
	/*public Map<String, String> readFromTableAllColumnsOfRow(String tableRead, String tableSend, int rowFromLast){
		Map <String, String> result = new HashMap<String, String>();
		//Open database
		db=dbContext.openOrCreateDatabase(DB_FILE,SQLiteDatabase.OPEN_READWRITE, null);

		//Create Cursor
		//Cursor c = db.rawQuery("SELECT * FROM " + tableRead +" WHERE synchronized = '"+"n"+"'", null);
		Cursor c = db.rawQuery("SELECT * FROM " + tableRead, null);

		//If Cursor is valid
		if (c != null ) {
			//Move cursor to last row
			if  (c.moveToLast()) {
				String[] cols = c.getColumnNames();


				int cursorPosition = c.getPosition();
				//If there are enough values on the table, go to selected row
				if (cursorPosition > rowFromLast)
					c.moveToPosition(cursorPosition-rowFromLast);
				//Get all values of the rows
				for (String column: cols){
					String val = c.getString(c.getColumnIndex(column));
					result.put(column, val);

				}
				result.put("table_name", tableSend);

			}
		}
		//Close cursor and database
		c.close();
		db.close();

		return result;
	}*/

	/**
	 * Prepare columns
	 * @param names
	 * @return
	 */

	private String prepareColumnsFromList(List<String> names){
		String query = "( ";
		for (int i =0; i<names.size(); i++){
			//Get the column values
			try{
				query += names.get(i)+" CHAR(20),";
			}catch (Exception e){
				System.out.println("Get columns from JSON error: "+e);
			}
		}
		query +="updated CHAR(5))";
		return query;
	}

}
