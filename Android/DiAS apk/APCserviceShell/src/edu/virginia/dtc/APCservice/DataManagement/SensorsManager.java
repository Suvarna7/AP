package edu.virginia.dtc.APCservice.DataManagement;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import edu.virginia.dtc.APCservice.Database.IITDatabaseManager;
import edu.virginia.dtc.SysMan.Biometrics;
import edu.virginia.dtc.SysMan.Debug;

/**
 * SensorManager is a class to read data from our three sensors:
 * 	- Dexcom G4: data on DiAS database
 *  - Zephyr: data on DiAS database
 *  - BodyMedia: data on IIT "Sensorsdb" database
 *  - Empatica : data on IIT "Sensorsdb" database
 * 
 * @author Caterina Lazaro
 * @version 2.0, September 2016 
 */
public class SensorsManager {

	//Values of interest from Bodymedia
	//	public static final String bmEE = "METs";
	/*public static final String bmEE = "calories";
	public static final String bmGSR = "gsr";
	public static final String bmACT = "vigorous";
	public static final String bmSLEEP = "sleep";
	private String BODYMEDIA_TABLE_NAME = "sample_table_bm";
	private final int BM_SAMPLES = 5;*/




	//Dias I/O values:
	//Table names
	//private String BM_TABLE_NAME = "bm_sample";
	public static final String _DEXCOM_TABLE_NAME = "sample_table_dexcom";
	private String _ZEPHYR_TABLE_NAME = "sample_table_zephyr";

	//Empataica values
	public static final String _EMPATICA_TABLE_NAME = "empatica_table";
	public static final String[] empatica_columnsTable = new String[]{"time_stamp", "Acc_x", "Acc_y", "Acc_z", "GSR", "BVP",
			"IBI", "HR", "temperature","battery_level"};
    public static final int _TIME_INDEX = 0;




	//private final double max_bolus_sent = Constraints.MAX_CORR;
	private final int EX_SAMPLES_NUMBER = 300;
	private final int EX_SAMPLES_INTERVAL = 1;

	public IITDatabaseManager dbManager;
	
	private SimpleDateFormat simpleFormat;



	public SensorsManager(Context ctx){
		//Read sensors values from IIT database
		dbManager = new IITDatabaseManager(ctx, IITDatabaseManager.DEFAULT_DB_NAME);
		
		simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


	}
	/* *************************************************************
	 * FUNCTIONS TO READ ALL DEVICES FROM PHONE DATABASE
	 * *************************************************************/


	/**
	 * Read CGM Values from Dexcom
	 * @return 
	 */

	public Map<String, String>  readDiASCGMTable(Context context,  ArrayList<Double> cgmArray){
		Map<String, String> dMap =  new HashMap<String, String>();
		Cursor c = context.getContentResolver().query(Biometrics.CGM_URI, new String[]{  "time", "recv_time", "cgm","trend","state", "diasState"}, null, null, null);

		Double cgm = 0.0;
		int cgmTrend = 0;
		int dState = 0;
		int diasState = 0;
		double phoneTime=0;
		//Map to store the key:value
		if (c != null) {
			if(c.moveToLast()) {
				long time = c.getLong(c.getColumnIndex("time"));  
				phoneTime = c.getLong(c.getColumnIndex("recv_time"));
				//TODO
				cgm = c.getDouble(c.getColumnIndex("cgm"));
				cgmTrend = c.getInt(c.getColumnIndex("trend"));
				dState = c.getInt(c.getColumnIndex("state"));
				diasState = c.getInt(c.getColumnIndex("diasState"));

				//Debug.i(TAG, "readCGMTable", "CGM: "+args[0]);
				//Add table name:
				dMap.put("table_name", _DEXCOM_TABLE_NAME);

				//Add them to the map
				dMap.put("time", ""+time );
				dMap.put("recv_time", ""+phoneTime);
				dMap.put("cgm", ""+cgm);
				dMap.put("trend", ""+cgmTrend);
				dMap.put("state", ""+dState);
				dMap.put("diasState", ""+diasState);
				//Sync value = yes
				dMap.put("synchronized", "y");
				//Time stamp:
				//Convert to date format
				Date parseDate=new Date(time*1000);
				String finalTime = simpleFormat.format(parseDate);

				//Add table name and time to JSON
				dMap.put("time_stamp", finalTime);


			}
			//Add them to the map
			c.close();
		}
		if (cgmArray !=null)
			cgmArray.add(cgm);
		
		//Store Map in database:
		
		return dMap;
	}
	/**
	 * Read all values of CGM table and update CGM list
	 * @param context
	 * @param values
	 */
	/*private void resetCGMValues(Context context, ArrayList<Double> cgmArray){
		//Initialize cgmValues again
		cgmArray = new ArrayList<Double>();
		Cursor c = context.getContentResolver().query(Biometrics.CGM_URI, new String[]{  "time", "recv_time", "cgm","trend","state", "diasState"}, null, null, null);
		if (c != null) {
			if (c.moveToFirst()){
				while (!c.isAfterLast()){
					cgmArray.add(  c.getDouble(c.getColumnIndex("cgm")));


				}
			}
		}
	}*/


	/**
	 *  readExerciseTable();
	 * Read Exercise Values
	 * @param context
	 * @param sendArgs
	 * @param times
	 * @param simpleFormat
	 * @param zephyrArray
	 */
	public List< Map<String, String>> readDiASExerciseTable(Context context,  ArrayList<Double> zephyrArray){

		List< Map<String, String>> sendArgs = new ArrayList< Map<String, String>>();
		//Get cursor for the exercise table
		Cursor c = context.getContentResolver().query(Biometrics.EXERCISE_SENSOR_URI , new String[]{"json_data", "time"}, null, null, null);

		//Set tables and values where to store the read info
		Map<String, String> eValues = new HashMap<String, String>();
		long LastTime=0;
		String json_data ="";
		if (c != null) {
			if(c.moveToLast()) {
				int lastPosition = c.getPosition();
				int currentPosition = 0;
				for (int i = 0; i < EX_SAMPLES_NUMBER ;  i ++){
					eValues = new HashMap<String, String>();
					currentPosition =lastPosition - (EX_SAMPLES_NUMBER - i)*EX_SAMPLES_INTERVAL;

					if (currentPosition >0)
						c.moveToPosition( currentPosition );                			
					LastTime = c.getLong(c.getColumnIndex("time"));  
					eValues.put("time", ""+LastTime);
					
					//Decode sensors values
					json_data = c.getString(c.getColumnIndex("json_data"));
					decodeInfoExercise(json_data, eValues, zephyrArray);
					
					//Convert to date format
					String finalTime = simpleFormat.format(new Date(LastTime*1000));

					//Then include table name and time
					eValues.put("table_name", _ZEPHYR_TABLE_NAME);
					eValues.put("time_stamp", finalTime);
					//Synchronized in the Dias = y
					eValues.put("synchronized", "y");

					sendArgs.add(eValues);
				}

			}
			c.close();
		}       
		return sendArgs;
	}
	/**
	 * decodeZephyrInfo();
	 * Decode Exercise Values and save the Heart rate
	 */

	private void decodeInfoExercise(String json_data, Map<String, String> eValues, ArrayList<Double> args){
		//Extract parameters from json object

		try{
			//Obtain the Array containing all JSON objects
			/*JSONArray jArr = new JSONArray(json_data);
        	for(int i=0; i<jArr.length();i++){
				JSONObject obj = (JSONObject)jArr.get(i);

        	}*/
			//Obtain the JSON object
			JSONObject obj = new JSONObject (json_data);

			//Obtain all keys
			Iterator<?> iter = obj.keys();
			while (iter.hasNext()){
				String key = (String)iter.next();
				String value = obj.getString(key);				
				eValues.put(key,value);
				//TODO
				/*
				 * 
			  //Get the values we need for the algorithm:
			 //EE
			 ArrayList<Double> updated_column1 = bmAlgorithm.get(BodyMediaMatrix._EE);
			 //updated_column1.add(Double.parseDouble("1.0"));
			 updated_column1.add(Double.parseDouble(sample.get(bmEE)));

			 //GSR
			 ArrayList<Double> updated_column2 =  bmAlgorithm.get(BodyMediaMatrix._GSR);
			 //updated_column2.add(Double.parseDouble("1.0"));
			 updated_column2.add(Double.parseDouble(sample.get(bmGSR)));
			 //VIGOROUS ACTIVITY
			 ArrayList<Double> updated_column3 = bmAlgorithm.get(BodyMediaMatrix._ACT);
			 //updated_column3.add((double)Integer.parseInt(sample.get(bmACT)));
			 updated_column3.add((double)Integer.parseInt(sample.get(bmACT)) );

			 //SLEEP
			 ArrayList<Double> updated_column4 =  bmAlgorithm.get(BodyMediaMatrix._SLEEP);
			 //updated_column4.add((double)Integer.parseInt("1")));
			 updated_column4.add((double)Integer.parseInt(sample.get(bmSLEEP)) );

			 //Add values to the array
			 bmAlgorithm.clear();
			 bmAlgorithm.add(BodyMediaMatrix._EE, updated_column1);
			 bmAlgorithm.add(BodyMediaMatrix._GSR, updated_column2);
			 bmAlgorithm.add(BodyMediaMatrix._ACT, updated_column3);
			 bmAlgorithm.add(BodyMediaMatrix._SLEEP, updated_column4);
				 * */

				if (key.equals("HR") && args != null){
					args.add( Double.parseDouble(value));
				}

			}

		}catch (JSONException e){
			Debug.e("SensorManager", "decodeZephyrInfo", "Exception: "+e);
		}
	}


	/**
	 * Reads last samples not syncrhonized from IIT database
	 * @param table_name - table to read
	 * @param column_names - column names of the table
	 * @param check_column - name of the column to be used as flag
	 * @param check_value - flag value
	 * @return read samples
	 */


	public List<Map<String, String>> read_lastSamples_IITDatabaseTable( String table_name, boolean remote, int max_samples){
		//Depending on the name, select column names:
		String[] column_names;
		 String check_column;
		 String check_value;
		if (table_name.equals(_EMPATICA_TABLE_NAME))
			column_names = empatica_columnsTable;
		else{
			System.out.println("Wrong table name ! there is no such a sensor");
			return null;
		}
		//Check value depending on:
		if (remote){
			//REMOTE UPDATE - Server
			check_column = IITDatabaseManager.upDateColumn;
			check_value = IITDatabaseManager.updatedStatusNo;
		}else{
			//LOCAL UPDATE - USB
			check_column = IITDatabaseManager.syncColumn;
			check_value = IITDatabaseManager.syncStatusNo;
		}
		//Get last not updated values
		List<Map<String, String>> listReadToUSB = dbManager.getNotUpdatedValuesUpToN(table_name, column_names,
			 check_column, check_value, max_samples);
		//Include table name
		List<Map<String, String>> temp = new ArrayList<Map<String, String>>();
		 for (int i = 0; i < listReadToUSB.size(); i++) {
             Map<String, String> val = listReadToUSB.get(i);
             val.put("table_name", table_name);
             temp.add(val);
		 }
	   //Prepare a JSON list with the values
		 return temp;
		//TODO DEBUG
		/*debugDatabase();
		dbManager.getNotUpdatedValues(table_name, empatica_columnsTable,
				IITDatabaseManager.upDateColumn, IITDatabaseManager.updatedStatusNo);
		return null;*/


	}

	/*private void debugDatabase(){
		//TODO DEBUG CODE:
		//1. Create a new table
		String[] columns = new String[]{"user", "name", "time"};
		dbManager.createTable("table_what", "time", new ArrayList<String>(Arrays.asList(columns)));

		//2. Update values
		ThreadSafeArrayList<String> values = new ThreadSafeArrayList<String>();
		values.set("u1");
		values.set("nombre");
		values.set("cero:cero");

		dbManager.updateDatabaseTable("table_what", values , true);
		

		//3 .Read 
		//dbManager.getAllNotCheckedValues("table_what", columns, "name", "nombre");

	}*/




}
