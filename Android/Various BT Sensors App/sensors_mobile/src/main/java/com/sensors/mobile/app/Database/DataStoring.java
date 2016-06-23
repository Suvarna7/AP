package com.sensors.mobile.app.Database;



import com.sensors.mobile.app.BM.MainActivityBM;
import com.sensors.mobile.app.InitActivity;
import com.sensors.mobile.app.zephyr.NewConnectedListener;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class DataStoring extends BroadcastReceiver {

	private static String _SERVER_ADDRESS_ZEPHYR = "http://216.47.158.133/mysqlsync/insert_zephyr_table.php";
	//private static String _SERVER_ADDRESS_ZEPHYR = "http://216.47.158.133/mysqlsync/insertzephyrvalues.php";
	//private static String _SERVER_ADDRESS_ZEPHYR = "http://216.47.158.133/mysqlSync/insertzephyrvalues%20_mock.php";

	private static String _SERVER_ADDRESS_BODYMEDIA = "http://216.47.158.133/mysqlsync/insert_bodymedia_table.php";
	//private static String _SERVER_ADDRESS_BODYMEDIA = "http://216.47.158.133/mysqlsync/insert_bodymedia.php";
	//private static String _SERVER_ADDRESS_BODYMEDIA = "http://216.47.158.133/mysqlsync/insert_bodymedia_mock.php";

	private static String _SERVER_ADDRESS_DEXCOM = "http://216.47.158.133/mysqlsync/insert_dexcom_mock.php";

	boolean initialized = false;

	@Override
	public void onReceive(Context context, Intent intent)
	{
		//Init database
		if (!initialized) {
			Database.initDatabase(context);
			initialized = true;
			InitActivity.startStoring = true;

		}
		//Save zephyr Info on the Mobile phone
		//For Zephyr
		if (NewConnectedListener.sensorSafeValues !=null && NewConnectedListener.sensorSafeValues.size() > 0) {
			for (int i = 0; i < NewConnectedListener.sensorSafeValues.size(); i ++)
				System.out.println(NewConnectedListener.sensorSafeValues.get(i));
			Database.updateDatabase(context, NewConnectedListener.sensorSafeValues, Database.zephyrTableName);
			NewConnectedListener.sensorSafeValues.clear();

		}

		//For Dexcom
		/*if(SavedValues.glucoseValues.size() > 0){
			Database.updateDatabase(context, SavedValues.glucoseValues, Database.dexcomTableName);
			SavedValues.glucoseValues.clear();
		}*/

		//For Bodymedia
		if (MainActivityBM.sensorValues != null && MainActivityBM.sensorValues.size() > 0) {
			Database.updateDatabase(context, MainActivityBM.sensorValues, Database.bodymediaTableName);
			MainActivityBM.sensorValues.clear();
		}

		//If there is an Internet connection, save on server too
		if (isNetworkAvailable(context)){
			//System.out.println("****There is Internet****");
			try {

				syncSQLiteMySQLDB(context, Database.zephyrTableName);
				syncSQLiteMySQLDB(context, Database.bodymediaTableName);
				//syncSQLiteMySQLDB(context, Database.dexcomTableName);

			}
			catch (JSONException e) {
				e.printStackTrace();
			}




		}

		//System.out.println("************ Actualizando!!!****");
	}

	private boolean isNetworkAvailable(Context ctx) {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}


/*
 * Sending to server
 *
 * */

	public static void syncSQLiteMySQLDB(final Context ctx, final String tableName) throws JSONException{

		//Create AsycHttpClient object
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();

		//Choose JSON id name
		String jID = tableName+"JSON";
		String serverAddress ="";

		if(tableName.equals(Database.zephyrTableName)){
			//serverAddress = MainActivity._SERVER_ADDRESS;
			serverAddress = _SERVER_ADDRESS_ZEPHYR;
		}
		else if(tableName.equals(Database.bodymediaTableName)){
			serverAddress = _SERVER_ADDRESS_BODYMEDIA;
		}

		else if(tableName.equals(Database.dexcomTableName)){
			serverAddress = _SERVER_ADDRESS_DEXCOM;
		}
		//System.out.println("JSON name: "+jID);

		if( Database.countRows(ctx, tableName)!=0){
			int toSync = Database.dbSyncCount(ctx, tableName);
			if(toSync != 0){
				for (int i = 0; i < toSync; i ++){
					String sentJSON = Database.composeJSONfromSQLite(ctx, tableName, i);
					params.put(jID, sentJSON);

					//System.out.println("Soon to be JSON: " + i + " | " + sentJSON);
					client.post(serverAddress,params ,new AsyncHttpResponseHandler() {

						public void onSuccess(String response) throws JSONException {
							//System.out.println(response);
							try {
								JSONArray arr = new JSONArray(response);


								//List<String> args = new ArrayList();
								for(int i=0; i<arr.length();i++){
									JSONObject obj = (JSONObject)arr.get(i);



									//Find the last_update value: to identify the row
									String last = (String) obj.get("last_update");
									//Find the updateStatus: to be updated
									String update =  (String) obj.get("update_status");

        						/*for (int j =0; j < Database.zephyrColumns.size(); j++){
        								String arg = (String) obj.get(Database.zephyrColumns.get(j));
        								//args.add(arg);
        								if(j==Database.zephyrColumns.size()-2)
        									last = arg;
        								else if (j== Database.zephyrColumns.size()-1)
        									update =arg;

        							}*/


									Database.updateSyncStatus(ctx,tableName,update, last);

								}
							} catch (JSONException e) {
								throw e;
							}
						}

						public void onFailure(int statusCode, Throwable error, String content) {


							if(statusCode == 404){
							}else if(statusCode == 500){
							}else{
								System.out.println("Error in Datastoring: " + error);
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1, byte[] arg2,
											  Throwable arg3) {
							String cont = convertToString(arg2);
							onFailure(arg0, arg3, cont);

						}

						@Override
						public void onSuccess(int arg0, Header[] arg1, byte[] arg2)  {
							String cont = convertToString(arg2);
							try {onSuccess(cont);}
							catch (JSONException e){
								//TODO Show exception in Android APP
								System.out.println(e);
							}

						}
					});
				}

			}else{
			}
		}else{
		}
	}

	public static String convertToString(byte[] args){
		String str = "";
		try{
			str = new String(args, "UTF-8"); // for UTF-8 encoding
		}catch (Exception e){

		}
		return str;
	}



}
