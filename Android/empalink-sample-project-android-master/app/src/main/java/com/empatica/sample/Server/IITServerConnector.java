package com.empatica.sample.Server;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.text.format.Time;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;



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
	//IIT Server values:
	//JSON Identifier
	public static final String JSON_ID_dias = "empaticaJSON";
	//Server urls
	private static final String IIT_SERVER_IP = "216.47.158.133";
	public static final String IIT_SERVER_UPDATE_VALUES_URL = "http://"+ IIT_SERVER_IP+"/phpSync/insert_into_table.php";
	public static final String IIT_SERVER_READ_TABLE_URL = "http://"+IIT_SERVER_IP+"/phpSync/read_table_values.php";
	//debug page private final String IIT_SERVER_URL =   "http://216.47.158.133/phpSync/insertzephyrvalues.php";
	
	private String JSON_ID;
	private String WRITE_URL;
	private String READ_URL;
	private ArrayList<String> tableNames;
	public  static boolean sending;

	//HTTTP client
	private AsyncHttpClient httpClient;
	private simpleHttpResponderAsync myResponder;

	//Server Database:
	//public static final String _SERVER_DB_NAME =  "IITdb.db";

	/**
	 * Constructors: create the server connector object
	 * Initializes the httpClient, the table names and the JSON_ID
	 */
	/*public IITServerConnector(IITDatabaseManager mang){
		httpClient =  new AsyncHttpClient();
		JSON_ID= "json";
		tableNames = new ArrayList<String>();
		dbManager = mang;
		sending = false;
	}*/

	/**
	 * Constructors: create the server connector object
	 * Initializes the httpClient, the table names and the JSON_ID
	 * @param jsonID
	 * @param writeURL - server url tp write values
	 * @param readURL - server url to read values
	 */
	public IITServerConnector(String jsonID, String writeURL, String readURL){
		httpClient =  new AsyncHttpClient();
		myResponder =  new simpleHttpResponderAsync();

		JSON_ID= jsonID;
		WRITE_URL = writeURL;
		READ_URL = readURL;
		tableNames = new ArrayList<>();



	}
	
	/**
	 * Constructors: create the server connector object
	 * Initializes the httpClient, the table names and the JSON_ID
	 * @param ctx context

	 */
	/*public IITServerConnector(Context ctx){
		httpClient =  new AsyncHttpClient();
		JSON_ID= JSON_ID_dias;
		WRITE_URL = IIT_SERVER_UPDATE_VALUES_URL;
		READ_URL = IIT_SERVER_READ_TABLE_URL;
		tableNames = new ArrayList<String>();
		//dbManager = new IITDatabaseManager(ctx, _SERVER_DB_NAME);
		databaseContext =  ctx;


	}*/
	/**
	 * debugSendToServer()
	 * Send a test JSON to IIT server to test
	 * @param table - name of the table 
	 */

	public void debugSendToServer(String table){
		//List<Map> args
		//json = "[{\"table_name\": "+TABLE_NAME+"},{\"user\": mentira, \"heartrate\": 80, \"cgm\": 5}]";

		Map <String, String> map1 = new HashMap <String, String>();
		Map <String, String> map = new HashMap<String, String>();

		List<Map<String, String>> arg = new ArrayList<Map<String, String>>();
		map.put("table_name", table);
		map.put("user", "mentira");
		map.put("heart_rate", "80");
		map.put("cgm", "105");
		map.put("updated", "n");
		map.put("time_stamp", getCurrentTime());


		//arg.add(map1);
		arg.add(map);

		sendToIIT(convertToJSON(arg), WRITE_URL);

		System.out.println("Sent to server: "+WRITE_URL);


	}


	/**
	 * sendToIITServer()
	 * Function to send a JSON object to IIT server
	 */

	public void sendToIIT(String json, String url){

		System.out.println("Sending to server: " + json);
		System.out.println(url);

		sending = true;

		//Check internet connection:
		if (isConnectedToServer() ){
			//Set parameters
			RequestParams params = new RequestParams();
			params.put(JSON_ID, json);

			//Send http request
			httpClient.post(url, params, myResponder);
		}else{
			sending = false;
		}


}
/**
 * readTableValuesIIT
 * Function to request not sync values from IIT server
 * @param tableName
 * @param - url
 */
public void readTableValuesIIT(String tableName, String url){
	//Send the read command to IIT server
	sendToIIT("{ \"table_name\": \"" + tableName + "\"}", url);

}


/**
 * convertToJSON()
 * Function to convert the array of key maps to a JSON String format
 * 
 */
public  static String convertToJSON( List<Map<String, String>> args){
	String json = "";
	Gson gson = new GsonBuilder().create();
	//Use GSON to serialize Array List to JSON
	try {
		json = gson.toJson(args);
	}catch (Exception e){
		System.out.println("Could not convert to JSON!: "+e);
		}

	return json;
}

/**
 * convertToString()
 */
public static String convertToString(byte[] args){
	String str = "";
	try{
		str = new String(args, "UTF-8"); // for UTF-8 encoding
	}catch (Exception e){
		e.printStackTrace();
	}
	System.out.println("SERVER RESPONSE: "+str);
	return str;
}


/**
 * getCurrentTime()
 * Return the current time with a String int the given format
 */

protected static String getCurrentTime(){
	SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Time time = new Time();
	time.setToNow();
	Date date = new Date(time.toMillis(false));
	return  simpleFormat.format(date);

}

	public boolean isConnectedToServer(){
		try{
			InetAddress ipAddr = InetAddress.getByName(IIT_SERVER_IP);
			System.out.println("Connection to server: "+ ipAddr);
			return !ipAddr.equals("");
		}catch (Exception e){
			return false;
		}
	}





}
