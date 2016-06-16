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

/**
 * MatrixDatabaseManager is a class use to store and read matrices stored in a database
 * 
 * Uses sqlite libraries
 * @author Caterina Lazaro
 * @version 1.0, March 2016 
 */

public class MatrixDatabaseManager {
	//Database params
	//TODO Change for every app
	//For Nexus 5 Dias:
	private static String DB_LOCAL_URL = "/storage/emulated/legacy";
	//private  static  String DB_LOCAL_URL = "/mnt/shell/emulated/0";
	//Database manager
	private SQLiteDatabase db;
	//Name of the DB file
	private String DB_FILE;
	//Bodymedia table
	public static final String db_MATRIX_NAME = "Matrixdb.db";
	//3D matrices
	private static Map<String, Integer[]> _3D_MATRICES;
	private static final int _INDEX = 0;
	private static final int _COLS = 1;
	private static final int _ROWS = 2;

	//Context of the application
	private Context dbContext;


	/**
	 * Constructor
	 * @param ctx - context
	 * @param database - name of database
	 */
	public MatrixDatabaseManager(Context ctx, String database){
		//Set the context
		dbContext = ctx;

		//Set the file location of database 
		DB_FILE = DB_LOCAL_URL  +"/IIT_database/"+database;
		//Create database file
		db=dbContext.openOrCreateDatabase(DB_FILE,SQLiteDatabase.OPEN_READWRITE, null);
		db.close();
		//Init 3D matrices
		_3D_MATRICES = new HashMap <String, Integer[]>();

	}


	/**
	 * Create new table/matrix. Deletes any previous instance
	 * @param table - name of the table to create
	 * @param columns - number of columns for the matrix
	 * @param dim - 1D, 2D, 3D dimensional matrix
	 */
	public void createTable(String table, int columns, int dim){
		//Delete any previous matrix
		deleteMatrix(table);
		String columnsQuery = prepareNColumns(columns);
		//Init the table with the values
		db=dbContext.openOrCreateDatabase(DB_FILE,SQLiteDatabase.OPEN_READWRITE, null);
		if (dim == 1 || dim ==2){
			String initTable = "CREATE TABLE IF NOT EXISTS "+table+" "+columnsQuery+";";
			db.execSQL(initTable);	
		}
		db.close();
	}
	/**
	 * Create a matrix as a combination of tables:
	 * - All tables have same number of columns and rows
	 * - Each new value will be a new table
	 * @param table
	 * @param columns
	 * @param rows
	 * @param dim
	 */

	public void create3DMatrix(String table, int columns, int rows){
		// We will create a matrix as a combination of tables:
		//		- All tables have same number of columns and rows
		//		- Each new value will be a new table

		//Matrix will be divided in tables with its subindex included:
		//		- We will keep track of this subindexes
		if(!_3D_MATRICES.containsKey(table)){

			//Set information of the table:
			Integer[] params = new Integer[_ROWS+1];
			params[_INDEX]= -1;
			params[_COLS]= columns;
			params[_ROWS]= rows;
			//Update matrix values
			_3D_MATRICES.put(table, params);
		}else{
			//Matrix is already created
		}



	}

	/**
	 * Create a new 3D matrix. Its first table is already given
	 * @param table
	 * @param values
	 */
	public void initialize3DMatrix(String table, double[][] values){
		if(!_3D_MATRICES.containsKey(table)){
			//Set information of the table:
			Integer[] params = new Integer[_ROWS+1];
			params[_INDEX]= 0;
			params[_COLS]= values[0].length;
			params[_ROWS]= values.length;

			//Update matrix values
			_3D_MATRICES.put(table, params);

			//Update values
			addValuesFixedTable( table+0, values);
		}else{
			//Matrix already exists, do nothing
		}




	}

	/**
	 * Prepare table with N columns
	 * @param number
	 * @return String to initiate the table
	 */
	private String prepareNColumns(int number){
		//Prepare the string for the column values
		String cols = "(";
		for (int i = 0; i < number; i ++){
			cols += "col"+i+ " DOUBLE, ";
		}
		//Prepare last characters:
		cols = cols.substring(0, cols.length()-2);
		cols += ")";

		return cols;
	}
	/**
	 * Return a table values
	 * To access all values:
	 * Rows - 	for (int i=0; i < result.length; i ++){
			Columns - 	for (int j =0; j < result[0].length; j ++){
					//Matrix[row][col]
					result[i][j]
	 * @param table
	 * @return
	 */

	public double[][] readMatrix(String table){
		//Prepare values to return
		double[][] matrix = null;

		//Open database
		db=dbContext.openOrCreateDatabase(DB_FILE,SQLiteDatabase.OPEN_READWRITE, null);

		//Create Cursor
		Cursor c = db.rawQuery("SELECT * FROM " + table , null);

		//If Cursor is valid
		if (c != null ) {
			String[] cols = c.getColumnNames();
			int row = c.getCount();
			//ARRAY[ROWS][COLUMNS]
			matrix = new double[row][cols.length];
			//Move cursor to first row
			int i = 0;
			if  (c.moveToFirst()) {
				do{
					//c.getDouble(c.getColumnIndex(cols[i]));
					//Read the row
					if(i < row)
						for (int j = 0; j <cols.length; j++){
							//matrix[j][i]= c.getDouble(c.getColumnIndex(cols[i]));
							//c.getDouble(c.getColumnIndex(cols[i]));
							//ARRAY[ROWS][COLUMNS]
							matrix[i][j]=c.getDouble(j);
							//c.getDouble(c.getColumnIndex(cols[j]));
						}
					else{
						//Index out of bounds
					}
					//Go to the next row
					i++;


				} while (c.moveToNext());
			}
		}
		//Close cursor and database
		c.close();
		db.close();
		return matrix;
	}
	/**
	 * Read a 3D matrix - it is a group of 2D tables
	 * Each 2D matrix read:
	 * Rows - 	for (int i=0; i < result.length; i ++){
			Columns - 	for (int j =0; j < result[0].length; j ++){
					//Matrix[row][col]
					result[i][j]
	 * @param table - matrix to be read
	 * @retur double[][][] list of tables 
	 */
	public double[][][] readMatrix3D(String table){
		//Init the result
		double[][][] result = new double[1][1][1];
		//First, find the matrix on the list of saved matrices
		if (_3D_MATRICES.containsKey(table)){
			//Get the number of tables to read
			int num = _3D_MATRICES.get(table)[_INDEX];
			//And the characteristics of those tables
			int row = _3D_MATRICES.get(table)[_ROWS];
			int col = _3D_MATRICES.get(table)[_COLS];

			result = new double[num][row][col];

			//Read all 2D tables
			for (int i = 0; i <= num; i++){
				//Add tables to the result
				result[i] = (readMatrix(table+i));
			}

		}else{
			//There is no such a matrix
		}
		return result;

	}

	/**
	 * Add row to matrix
	 * @param table
	 * @param row
	 */
	public void addRowToMatrix(String table, double[] row){
		//First, check that the columns to insert are the same as the one in the matrix
		//Open db
		db=dbContext.openOrCreateDatabase(DB_FILE,SQLiteDatabase.OPEN_READWRITE, null);

		try{
			//Prepare the Query values
			String inQueryValues = "(";
			//Build the store query 
			for (int i =0; i<row.length; i++){
				//System.out.println("---------"+database+":"+values.get(i)+"-------------");
				inQueryValues += row[i]+", ";
				//Exec query if it does not include a null value and the last char is ")"

			}
			//Prepare last characters:
			inQueryValues = inQueryValues.substring(0, inQueryValues.length()-2);
			inQueryValues += ")";

			//Execute query
			if (!inQueryValues.contains("null") && inQueryValues.substring(inQueryValues.length() - 1).equals(")")){
				//db.execSQL("REPLACE INTO "+table+ " VALUES" + inQueryValues+";");
				db.execSQL("INSERT INTO "+table+ " VALUES" + inQueryValues+";");
				//+ "ON DUPLICATE KEY UPDATE synchronized= y;");


			}
			else
				Debug.i("what", "store db", "DID NOT UPDATE DATABASE ");
		}catch(SQLiteException e){
			System.out.println("SQLite Exception while storing in table: "+e);
			Debug.i("what", "store db", "DID NOT UPDATE DATABASE: "+ e);


		}
		//Close db
		db.close();

	}

	/**
	 * Add an String value to matrix
	 * @param table
	 * @param row
	 */
	public void addStringToMatrix(String table, String val){
		//First, check that the columns to insert are the same as the one in the matrix
		//Open db
		db=dbContext.openOrCreateDatabase(DB_FILE,SQLiteDatabase.OPEN_READWRITE, null);

		try{
			//Prepare the Query values
			String inQueryValues = "(" +val+ ")";
			db.execSQL("INSERT INTO "+table+ " VALUES (" + val+");");
			//+ "ON DUPLICATE KEY UPDATE synchronized= y;");

		}catch(SQLiteException e){
			System.out.println("SQLite Exception while storing in table: "+e);
			Debug.i("what", "store db", "DID NOT UPDATE DATABASE: "+ e);


		}
		//Close db
		db.close();

	}

	/**
	 * Add values to a 3D matrix - we should create a new table
	 * @param table - name of the matrix
	 * @param values - values to update
	 */
	public void addTableTo3DMatrix(String table, double [][] values ){
		int cols = values[0].length;
		int rows = values.length;
		//First, check that the table exist
		if (_3D_MATRICES.containsKey(table)){
			//Check that dimensions are the same:
			//	params[_COLS]= values[0].length;
			if((_3D_MATRICES.get(table))[_COLS] == cols  
					&& (_3D_MATRICES.get(table))[_ROWS] == rows){
				//Update index
				int index = 1+(_3D_MATRICES.get(table))[_INDEX];
				//Set information of the table:
				Integer[] params = new Integer[_ROWS+1];
				params[_INDEX]= index;
				params[_COLS]= cols;
				params[_ROWS]= rows;

				_3D_MATRICES.remove(table);
				_3D_MATRICES.put(table, params);
				//Create a table with the values
				addValuesFixedTable( table+index, values);
			}else{
				//Error with the input values
			}

		}else{
			//Create a new matrix and upload values
			initialize3DMatrix(table, values);
		}


	}

	/**
	 * Initialize a table with certain given values
	 * @param table - name of the matrix/table
	 * @param values - values to insert
	 */
	private void addValuesFixedTable(String table, double[][] values){
		//Prepare columns: number comes from the input array
		String columnsQuery = prepareNColumns(values[0].length);
		//First, delete table if it exists
		deleteMatrix(table);
		//Create the new table
		db=dbContext.openOrCreateDatabase(DB_FILE,SQLiteDatabase.OPEN_READWRITE, null);
		String initTable = "CREATE TABLE IF NOT EXISTS "+table+" "+columnsQuery+";";
		db.execSQL(initTable);	
		db.close();

		//Add rows
		for (int i = 0; i < values.length; i++){
			//We will be adding a new row to the table
			addRowToMatrix(table, values[i]);

		}

	}


	/**
	 * Delete the given matrix
	 * @param table
	 */
	public void deleteMatrix(String table){
		db=dbContext.openOrCreateDatabase(DB_FILE,SQLiteDatabase.OPEN_READWRITE, null);
		//Delete table
		String updateQuery1 = "DROP TABLE IF EXISTS  "+ table +";";
		db.execSQL(updateQuery1);
	}


}
