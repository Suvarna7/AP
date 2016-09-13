package iit.pc.javainterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * JSONToSend class contains the values to be sent to the server.
 * It organized the rows in a sensible manner and offers the JSON String to be send with an HTTP client
 * 
 * Uses the library gson
 * 
 * @author Caterina
 *
 */

public class JSONToSend {
	private List<HashMap<String, String>> values;
	
	/**
	 * Constructor
	 */
	public JSONToSend(){
		values  = new ArrayList();
	}
	
	/**
	 * Add a new row to the read values
	 * @param row
	 */
	public void insertRow(HashMap<String, String> row){
		values.add(row);
		
	}
	
	/**
	 * Reset the stores values
	 */
	public void deleteValues(){
		//Reset the values
		values  = new ArrayList();
	}
	/**
	 * Set values: set the ArrayList values to be converted to JSON
	 * @param newValues - The array list
	 */
	public void setValues(List<HashMap<String, String>> newValues){
		values = newValues;
	}
	
	/**
	 * Create the json object from the List
	 */
	
	public String getJSONString(){
		String json = "";
		Gson gson = new GsonBuilder().create();
	     //Use GSON to serialize Array List to JSON
		json= gson.toJson(values);
		return json;
	}
	
	/**
	 * convertToString()
	 */
	public  String convertToString(byte[] args){
    	String str = "";
    	try{
				str = new String(args, "UTF-8"); // for UTF-8 encoding
    	}catch (Exception e){
    		
    	}
    	return str;
    }
	
	/**
	 * convertToJSON()
	 * Function to convert the array of key maps to a JSON String format
	 * 
	 */
	public  static String convertToJSONString( List<Map<String, String>> args){
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

}
