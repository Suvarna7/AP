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

import edu.virginia.dtc.APCservice.BodyMediaMatrix;
import edu.virginia.dtc.SysMan.Biometrics;
import edu.virginia.dtc.SysMan.Debug;

/**
 * SensorManager is a class to read data from our three sensors:
 * 	- Dexcom G4: data on DiAS database
 *  - Zephyr: data on DiAS database
 *  - BodyMedia: data on IIT "Sensorsdb" database
 * 
 * @author Caterina Lazaro
 * @version 1.0, February 2016 
 */
public class IITSensorsManager {

	//Values of interest from Bodymedia
	//	public static final String bmEE = "METs";
	public static final String bmEE = "calories";
	public static final String bmGSR = "gsr";
	public static final String bmACT = "vigorous";
	public static final String bmSLEEP = "sleep";


	//Dias I/O values:
	//Table names
	//private String BM_TABLE_NAME = "bm_sample";
	public static final String _SERVER_TABLE_NAME_DE = "sample_table_dexcom";
	private String _SERVER_TABLE_NAME_ZE = "sample_table_zephyr";
	private String _SERVER_TABLE_NAME_BM = "sample_table_bm";


	//private final double max_bolus_sent = Constraints.MAX_CORR;
	private final int EX_SAMPLES_NUMBER = 300;
	private final int EX_SAMPLES_INTERVAL = 1;
	private final int BM_SAMPLES = 5;

	private IITDatabaseManager dbManager;


	public IITSensorsManager(Context ctx){
		//Read bodymedia values from IIT database
		dbManager = new IITDatabaseManager(ctx);
	}
	/* *************************************************************
	 * FUNCTIONS TO READ ALL DEVICES FROM PHONE DATABASE
	 * *************************************************************/


	/**
	 * readCGMTable();
	 * Read CGM Values from Dexcom
	 * @return 
	 */

	public void readCGMTable(Context context, Map<String, String> dMap, long[] times,  ArrayList<Double> cgmArray){
		Cursor c = context.getContentResolver().query(Biometrics.CGM_URI, new String[]{  "time", "recv_time", "cgm","trend","state", "diasState"}, null, null, null);

		int cgmTrend = 0;
		int dState = 0;
		int diasState = 0;
		double phoneTime=0;
		//Map to store the key:value

		if (c != null) {

			if(c.moveToLast()) {
				times[0] = c.getLong(c.getColumnIndex("time"));  
				phoneTime = c.getLong(c.getColumnIndex("recv_time"));
				//TODO
				cgmArray.add( c.getDouble(c.getColumnIndex("cgm")));
				cgmTrend = c.getInt(c.getColumnIndex("trend"));
				dState = c.getInt(c.getColumnIndex("state"));
				diasState = c.getInt(c.getColumnIndex("diasState"));

				//Debug.i(TAG, "readCGMTable", "CGM: "+args[0]);

				//Add them to the map
				dMap.put("time", ""+times[0] );
				dMap.put("recv_time", ""+phoneTime);
				dMap.put("cgm", ""+cgmArray.get(cgmArray.size()-1));
				dMap.put("trend", ""+cgmTrend);
				dMap.put("state", ""+dState);
				dMap.put("diasState", ""+diasState);
				//Sync value = yes
				dMap.put("synchronized", "y");


			}
			//Add them to the map
			c.close();
		}
	}
	/**
	 * Read all values of CGM table and update CGM list
	 * @param context
	 * @param cgmArray
	 */
	private void resetCGMValues(Context context, ArrayList<Double> cgmArray){
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
	}


	/**
	 *  readExerciseTable();
	 * Read Exercise Values
	 * @param context
	 * @param sendArgs
	 * @param times
	 * @param simpleFormat
	 * @param zephyrArray
	 */
	public void readExerciseTable(Context context, List< Map<String, String>> sendArgs, long[] times, SimpleDateFormat simpleFormat, ArrayList<Double> zephyrArray){

		//Get cursor for the exersice table
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
					times[0] = LastTime;
					//Decode sensors values
					json_data = c.getString(c.getColumnIndex("json_data"));
					decodeInfoHR(json_data, eValues, zephyrArray);
					//Convert to date format
					String finalTime = simpleFormat.format(new Date(LastTime*1000));

					//Then include table name and time
					eValues.put("table_name", _SERVER_TABLE_NAME_ZE);
					eValues.put("last_update", ""+finalTime);
					//Synchronized in the Dias = y
					eValues.put("synchronized", "y");

					sendArgs.add(eValues);
				}

			}
			c.close();
		}       
	}
	/**
	 * decodeZephyrInfo();
	 * Decode Exercise Values and save the Heart rate
	 */

	private void decodeInfoHR(String json_data, Map<String, String> eValues, ArrayList<Double> args){
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

				if (key.equals("HR")){
					args.add( Double.parseDouble(value));
				}

			}

		}catch (JSONException e){
			Debug.e("SensorManager", "decodeZephyrInfo", "Exception: "+e);
		}
	}

	/**
	 * Read IIT tables stored in the device
	 * @param sendArgs
	 * @param tableName
	 * @param bmAlgorithm
	 */


	public void readIITDatabaseTable(List<Map<String, String>> sendArgs, String tableName, ArrayList<ArrayList<Double>> bmAlgorithm){

		//Get all samples not synchronized
		List<Map<String, String>> readValues = dbManager.readFromTableNotSyncRows(tableName, _SERVER_TABLE_NAME_BM);

		if (readValues != null){
			//Extract the necessary values for the algorithm and to send
			for (Map<String, String> sample : readValues){

				//Get a row - add to the arguments to send
				sendArgs.add(sample);

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



			}
		}

	}




}
