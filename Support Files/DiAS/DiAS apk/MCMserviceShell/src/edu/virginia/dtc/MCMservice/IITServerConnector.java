package edu.virginia.dtc.MCMservice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.format.Time;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import edu.virginia.dtc.SysMan.Debug;

/**
* IITServerConnector is a class working as an interface between the android application and
* our IIT server. It offers functions to read tables from the server, and to send values from the
* app to the server
* 
* Uses json and sqlite libraries
* @author Caterina Lazaro
* @version 1.0, November 2015 
*/
public class IITServerConnector {
	private String JSON_ID;
	private String WRITE_URL;
	private String READ_URL;
	private AsyncHttpClient httpClient;
	private DevitesDatabaseManager dbManager;

	private ArrayList<String> tableNames;

	/**
	 * Constructors: create the server connector object
	 * Initializes the httpClient, the table names and the JSON_ID
	 */
	public IITServerConnector(DevitesDatabaseManager mang){
		httpClient =  new AsyncHttpClient();
		JSON_ID= "json";
		tableNames = new ArrayList<String>();
		dbManager = mang;
	}

	/**
	 * Constructors: create the server connector object
	 * Initializes the httpClient, the table names and the JSON_ID
	 * @param jsonID
	 * @param writeURL - server url tp write values
	 * @param readURL - server url to read values
	 */
	public IITServerConnector(String jsonID, String writeURL, String readURL, Context ctx){
		httpClient =  new AsyncHttpClient();
		JSON_ID= jsonID;
		WRITE_URL = writeURL;
		READ_URL = readURL;
		tableNames = new ArrayList<String>();
		dbManager = new DevitesDatabaseManager(ctx, "IITdb.db");

	}
	/**
	 * debugSendToServer()
	 * Send a test JSON to IIT server to test
	 * @param table - name of the table 
	 */

	private void debugSendToServer(String table){
		//List<Map> args
		//json = "[{\"table_name\": "+TABLE_NAME+"},{\"user\": mentira, \"heartrate\": 80, \"cgm\": 5}]";

		Map <String, String> map1 = new HashMap <String, String>();
		Map <String, String> map = new HashMap <String, String>();

		List<Map<String, String>> arg = new ArrayList<Map<String, String>>();
		map1.put("table_name", table);

		map.put("user", "mentira");
		map.put("heart_rate", "80");
		map.put("cgm", "105");

		arg.add(map1);
		arg.add(map);

		sendToIIT(convertToJSON(arg), WRITE_URL);


	}


	/**
	 * sendToIITServer()
	 * Function to send a JSON object to IIT server
	 */

	public void sendToIIT(String json, String url){

		//Set parameters
		RequestParams params = new RequestParams();
		params.put(JSON_ID, json);

		//Send http request
		httpClient.post(url , params, new AsyncHttpResponseHandler() {

			//Handle succesful response
			public void onSuccess(String response) {

				try {
					JSONArray arr = new JSONArray(response);

					List<String> args = new ArrayList();
					for(int i=0; i<arr.length();i++){
						JSONObject json = (JSONObject)arr.get(i);
						if (json.get("synchronized").equals('n')){
							//The value comes from a read request
							System.out.println("Synchronizing...");
							String tableN = json.get("table_name").toString();

							//Create table if it does not exist
							if (tableNames.contains(tableN)){
								tableNames.add(tableN);
								JSONArray jsonArray = json.names();
								ArrayList<String> columns = new ArrayList<String>();
								//Build the List of columns

								for (int j=0; j<jsonArray.length(); j++) {
									String val = jsonArray.getString(j);
									//Eliminate table_name column
									if (!val.equals("table_name"))
										columns.add( jsonArray.getString(j) );
								}
								dbManager.createTable(tableN, columns);
							}
							//Update values
							//Build the List of values
							ArrayList<String> values = new ArrayList<String>();
							Iterator <String> it = json.keys();
							while(it.hasNext()){
								String val = (String)it.next();
								//Eliminate table_name column and syncrhonized
								if (!val.equals("table_name")&&!val.equals("synchronized"))
									values.add(val);
							}
							//Update values in table TODO
							dbManager.updateNewValuesDatabase(tableN, values, true);

						}


					//System.out.println("Element: "+arr.get(i));

				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

			//Handle failing response
		public void onFailure(int statusCode, Throwable error, String content) {

			if(statusCode == 404){
				System.out.println("Page not found");

			}else if(statusCode == 500){
				System.out.println("Server failure");

			}else{
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			String cont = convertToString(arg2);

			onFailure(arg0, arg3, cont);

		}

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			String cont = convertToString(arg2);
			onSuccess(cont);

		}
	});

}
/**
 * readTableValuesIIT
 * Function to request not sync values from IIT server
 * @param tableName
 * @param - url
 */
public void readTableValuesIIT(String tableName, String url){
	//Send the read command to IIT server
	sendToIIT("{ \"table_name\": \""+tableName+"\"}", url);

}


/**
 * convertToJSON()
 * Function to convert the array of key maps to a JSON String format
 * 
 */
static String convertToJSON( List<Map<String, String>> args){
	String json = "";
	Gson gson = new GsonBuilder().create();
	//Use GSON to serialize Array List to JSON
	json= gson.toJson(args);
	return json;
}

/**
 * convertToString()
 */
private static String convertToString(byte[] args){
	String str = "";
	try{
		str = new String(args, "UTF-8"); // for UTF-8 encoding
	}catch (Exception e){

	}
	return str;
}


/**
 * getCurrentTime()
 * Return the current time with a String int the given format
 * @param simpleFormat - Date Format
 * @return time - String
 */

static String getCurrentTime(){
	SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Time time = new Time();
	time.setToNow();
	Date date = new Date(time.toMillis(false));
	return  simpleFormat.format(date);

}



}
