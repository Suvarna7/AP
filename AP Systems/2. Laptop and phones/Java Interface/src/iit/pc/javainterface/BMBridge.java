package iit.pc.javainterface;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import iit.pc.javainterface.encryption.TestEncryption;
import iit.pc.javainterface.excel.AutomaticExcelWriterAndDownloader;
import iit.pc.javainterface.excel.AutomaticExcellReaderAndSender;
import iit.pc.javainterface.excel.ExcelManager;
import iit.pc.javainterface.matlab.MatlabSocket;
import iit.pc.javainterface.usb.USB_PCHost;
import jxl.write.WriteException;


/**
 * The BMBridge class offers a interface between BodyMedia excel file and the server
 * It will read the BM obtained data from and excel file and send it to one of the selected servers 
 * 
 * Uses the library jxl.jar (to read excel files)
 * 
 * @author Caterina Lazaro
 * @version 2.0, August 2016
 *
 */

public class BMBridge {
	//***** FIELDS FOR IIT SERVER ******
	private  String IIT_SERVER_ADDRESS_IP = "http://216.47.158.133";
	private String IIT_SERVER_ADDRESS_WRITE = IIT_SERVER_ADDRESS_IP+"/phpSync/insert_into_Table.php";
	private  String IIT_SERVER_ADDRESS_READ = IIT_SERVER_ADDRESS_IP +"/phpSync/read_table_values.php";
	public static String JSON_ID ="empaticaJSON";
	private static String TABLE_NAME = "empatica";
	
	private MyHttpClient httpClient = new MyHttpClient();
	private String sendResult;
	private String getResult;


	//Graphical Interface
	public BMGuiProgram gui;

	//Automatic function
	AutomaticExcellReaderAndSender automaticReader;
	public AutomaticExcelWriterAndDownloader automaticWriter;

	//Exceptions flags
	static boolean fileException;
	public boolean internetException;
	//private boolean serverException;
	public boolean allDataCollected;

	//******* USB Host *******
	public USB_PCHost mHost;

	//***** EXCEL MANAGER ******
	public ExcelManager mExcel;

	//************ HANDLE DATA **********
	private ArrayList<String> COLUMNS_SET;
	private JSONToSend jsonManager;
	HashMap<String, String> rowRead;
	//Number of extra columns sent thru USB:
	//	- table_name
	//	- update from server / sync (bis) from USB
	public static int COLUMNS_DIFF = 1;
	
	//************ MATLAB COMMUNCIATIO *******
	public MatlabSocket matSocket ;



	/**
	 * Create the BodyMedia Bridge
	 * @param args
	 */
	public BMBridge(String excel, String server){
		setIIT_SERVER_ADDRESS(server);
		COLUMNS_SET = new ArrayList<String>();
		System.out.println("columns" + COLUMNS_SET);
		jsonManager = new JSONToSend();	
		rowRead =  new HashMap<String, String>();
		//Excel file: Set row values
		mExcel = new ExcelManager(this);
				
		//Creates the Graphical interface
		gui = new BMGuiProgram("Send",this, "IIT Connect Interface");

		//Set exception flags
		fileException = false;
		internetException = false;
		//serverException = false;
		
		//Start matlab socket
		matSocket = new MatlabSocket(this);

		

	}

	/**
	 * Create the BodyMedia Bridge
	 * @param args
	 */
	public BMBridge(){
		COLUMNS_SET = new ArrayList<String>();
		jsonManager = new JSONToSend();	
		rowRead =  new HashMap<String, String>();
		//File reset
		mExcel = new ExcelManager(this);
		//USB Connection
		mHost = new USB_PCHost(this);

		//Creates the Graphical interface
		gui = new BMGuiProgram("Send",this, "IIT Server Interface");

		//Set exception flags
		fileException = false;
		internetException = false;
		//serverException = false;

		//Start matlab socket
		matSocket = new MatlabSocket(this);


	}

	/* *****************************************
	 * SERVER BRIDGE
	 */

	/**
	 * Handles server response
	 */
	private ResponseHandler<String> handler = new ResponseHandler<String>(){

		/**
		 * handleRepsonse
		 * @param httpResponse
		 */
		public String handleResponse(HttpResponse arg0)
				throws ClientProtocolException, IOException {

			//StatusLine status = arg0.getStatusLine();
			//Get all the input characters:
			InputStream inputstream = arg0.getEntity().getContent();
			int data = inputstream.read();
			String response = "";
			while(data != -1) {
				//do something with data...
				response += (char)data;
				data = inputstream.read();
			}
			inputstream.close();
			//Try to retrieve the JSON Object
			//String content = EntityUtils.getContentCharSet(arg0.getEntity());
			//TODO System.out.println("Content: "+response);
			handleJSONResponse(response);

			readyToRun();

			return null;
		}



	};


	/**
	 * Handles a JSON kind of response
	 * @param response
	 * @throws IOException
	 */

	public  List<String> handleJSONResponse(String response) throws IOException{
		List<String> jsonsBack = new ArrayList<String>();
		String jsonACK = "";
		List<Map<String, String>> sammplesACK = new ArrayList<Map<String, String>>();
		Map<String, String> currentSample = new HashMap<String, String>();
		try {

			System.out.println(response);
			JSONArray arr = new JSONArray(response);
			List<String> args = new ArrayList<String>();
			if(arr !=null){
				for (int i=0;i<arr.length();i++){ 
					//String jsonStr= arr.get(i).toString();
					//System.out.println("JSON object index: "+i);

					JSONObject obj = (JSONObject)arr.get(i);
					//System.out.println("Array object: " + i);
					//TODO System.out.println("JSON Convert: " + obj.toString());
					currentSample = new HashMap<String, String>();
					currentSample.put(USB_PCHost._ACK_SYNCHRONIZED, "ACK");
					currentSample.put("time_stamp", obj.getString("time_stamp"));


					if( searchForUpdateStatus(obj)){
						//Update request
						args.add(arr.get(i).toString());
					}
					else{
						try{

							mExcel.writeExcelFileWithJSON(obj, 0);
							//If written successful - prepare a JSON ACK with sync = 'y'
							currentSample.put("synchronized", "y");


						}catch (IOException e){
							//If written not successful - prepare a JSON ACK with sync = 'n'
							currentSample.put("synchronized", "n");

							System.out.println("Write excel with JSON Exception: "+e);
							if (e.toString().contains("FileNotFoundException"))
								displayFileException(e, "Excel", "Check excel file");
							else
								displayInternetException(e, "JSON Exception", "run again");


						}catch (ArrayIndexOutOfBoundsException ioE){
							//If written not successful - prepare a JSON ACK with sync = 'n'
							currentSample.put("synchronized", "n");
							
							System.out.println("Write excel with ArrayIndexOutOfBounds Exception: "+ioE);
								displayFileException(ioE, "Excel", "Corrupted! Open excel file");
							
							
						}/*catch (WriteException wE){
							//If written not successful - prepare a JSON ACK with sync = 'n'
							currentSample.put("synchronized", "n");

							displayFileException(wE, "Write", "Check excel file");

						}*/
						//Send the resulting
						catch (WriteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					//System.out.println(i);
					//gui.setSentLabel(sendResult);
					
					//Update the JSON ACK ARRAY
					sammplesACK.add(currentSample);
				} 

				//If there are more past samples, we need to erase them
				mExcel.updateRowsParameters();
				List<Map<String, String>> singlePacket = new ArrayList<Map<String, String>>();

				for (int i = 0; i < sammplesACK.size(); i++){
					//Convert the JSONACK ARRRAY to String
					singlePacket.add(sammplesACK.get(i));
					if ((i%USB_PCHost._MAX_NUM_JSON)== 0){
						jsonsBack.add(JSONToSend.convertToJSONString(singlePacket));
						singlePacket = new ArrayList<Map<String, String>>();

					}
				}
				//Send last samples
				jsonsBack.add(JSONToSend.convertToJSONString(singlePacket));

			}
			//TODO Inform that writing process is done!
			/*if(!internetException){
				gui.checkWindow("Ready to run MATLAB algorithm");
			}*/



		}catch (JSONException je){

			//Error
			System.out.println(response);
			System.out.println("JSON response error: "+ je);
			//Values not updated
			sendResult = "values_posted... Not updated";
			gui.setSentLabel(sendResult);
			gui.displayError(je.toString());
			if(!internetException){
				internetException = true;
				gui.checkWindow("There is a exception: Check Internet Connection");
			}

		} /*catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			gui.setSentLabel(sendResult);
			displayFileException(e, "Erasing", "Check excel file");

		}*/ catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("ACK: "+jsonACK);
		return jsonsBack;
	}



	/**
	 * Read values from Excel file and update the JSONToSend object
	 * @throws IOException
	 */
	public void readExcelValuesAndUpdateJSON(){

		jsonManager.setValues(mExcel.readExcelValues());
		sendResult = "values_posted...Values read";
		gui.setSentLabel(sendResult);

	}


	/**
	 * Send values to the Server
	 * @param args
	 */

	public void sendToIITServer(){
		//Get the JSON String and reset the list
		String JSON= jsonManager.getJSONString();
		jsonManager.deleteValues();

		//Set POST parameters
		ArrayList<NameValuePair>params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(JSON_ID, JSON));
		HttpHost host = new HttpHost(getIIT_SERVER_ADDRESS_Write());
		//HttpParams params = new BasicHttpParams();
		//System.out.println("Set id: "+ JSON_ID);
		//params.setParameter(JSON_ID, JSON);

		//Update GUI
		sendResult = "values_posted...";
		gui.setSentLabel(sendResult);

		//TODO Send values to IIT
		try{
			httpClient.executePOST(host, params, handler);
		}catch (Exception exc){
			gui.displayError(exc.toString());
			if (!internetException){
				internetException = true;	
				gui.checkWindow("There is a exception when sending: Check Internet Connection");
			}


		}
	}
	/**
	 * Request read values from IIT server
	 */

	public void readFromIITServer(){

		//Request values from IIT
		//Set parameters
		String JSON = "{ \"table_name\": \""+TABLE_NAME+"\"}";
		System.out.println(JSON);
		System.out.println(IIT_SERVER_ADDRESS_READ);

		//Set POST parameters
		ArrayList<NameValuePair>params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(JSON_ID, JSON));
		HttpHost host = new HttpHost(IIT_SERVER_ADDRESS_READ);
		//Update GUI
		getResult = "request_posted...";
		gui.setGetLabel(getResult);

		//Reset excel file
		mExcel.resetInitRow();
		//TODO Send values to IIT
		try{
			httpClient.executePOST(host, params, handler);
		}catch (IndexOutOfBoundsException exc){
			//TODO Ignore this exception
			/*gui.displayError(exc.toString());
			if(!internetException){
				internetException = true;
				gui.checkWindow("There is a exception (excel file too long): change/modify .xls file");
			}*/

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Get results
	 * @return send result
	 */
	public String getSendResult() {
		return sendResult;
	}

	/**
	 * Obtain update status
	 * @param obj JSON object
	 * @return status
	 */
	private boolean searchForUpdateStatus(JSONObject obj){
		try{
			//First check whether the object is being syncrhonized:
			if(obj.get("updated").equals("'y'")){
				//Values updated
				sendResult = "values_posted... Updated";
				gui.setSentLabel(sendResult);
				return true;
			}else{
				sendResult = "values_posted... Not updated";
				gui.setSentLabel(sendResult);

				return false;
			}
		}catch (JSONException je){
			//If there is no update_status ==> read request
			getResult = "";
			gui.setGetLabel(getResult);
			//writeExcelFileWithJSON(obj, 0);
			//Do nothing, but!
			return false;
		}

	}


	//**************************************************************
	//	GETTERS AND SETTERS
	/**
	 * Getter for the IIT_SERVER_ADDRESS field
	 * @return server address
	 */

	public String getIIT_SERVER_ADDRESS_Write() {
		return IIT_SERVER_ADDRESS_WRITE;
	}

	public String getIIT_SERVER_IP(){
		return IIT_SERVER_ADDRESS_IP;
	}
	/**
	 * Setter for the IIT_SERVER_ADDRESS field
	 * @param sAddress
	 */

	public void setIIT_SERVER_ADDRESS(String sAddress) {
		IIT_SERVER_ADDRESS_WRITE = sAddress;
	}

	/**
	 * Getter for the TABLE_NAME field
	 * @return - table name in server
	 */
	public String getTABLE_NAME() {
		return TABLE_NAME;
	}

	/**
	 * Setter for the TABLE_NAME field
	 * @param tName
	 */

	public void setTABLE_NAME(String tName) {
		TABLE_NAME = tName;
	}

	public void setAutomaticReader(AutomaticExcellReaderAndSender automatic){
		automaticReader = automatic;
	}

	public void setAutomaticWriter(AutomaticExcelWriterAndDownloader automatic){
		automaticWriter = automatic;
	}

	/**
	 * Set the Server address
	 */
	public void setTableName(String table){
		setTABLE_NAME(table);
	}

	/**
	 * Set the Server address
	 */
	public void setServerAddress(String server){
		setIIT_SERVER_ADDRESS(server);
	}


	/* *********************************************************8
	 * EXCEPTIONS POP UPS
	 */

	/**
	 * Restart the exceptions popups
	 */
	public void resetExceptionFlags(){
		internetException = false;
		//serverException = false;
		fileException = false;
		allDataCollected = false;
	}
	/**
	 * Displays the results of a File Exception
	 * @param e exception
	 * @param type type of exception
	 * @param todo recommended action
	 */

	public void displayFileException(Exception e, String type, String todo){
		gui.displayError(e.toString());
		if (!fileException){
			fileException = true;
			gui.checkWindow("There is a "+type+" exception: "+todo);
		}
	}
	/**
	 * Displays the results of an Internet exception
	 * @param e exception
	 * @param type type of exception
	 * @param todo recommended acction
	 */

	public void displayInternetException(Exception e, String type, String todo){
		gui.displayError(e.toString());
		if(!internetException){
			internetException = true;
			gui.checkWindow("There is a "+type+" exception:"+todo);
		}
	}
	
	public void readyToRun(){
		//Announce the end of MATLAB writing
		if(allDataCollected){
			gui.checkWindow("Ready to run MATLAB algorithm");
		}
	}

	/* **********************************************************
	 * MAIN
	 */


	/**
	 * main function - creates the bodymedia interface 
	 * It will read data from an excel file and send it to a remote server
	 * @param args
	 */

	public static void main (String[] args){
		//Start the server bridge
		BMBridge bmBridge =  new BMBridge();

		//Set up an automatic reading function
		AutomaticExcellReaderAndSender aReader = new AutomaticExcellReaderAndSender(30, bmBridge);
		//aReader.startAutomaticReads();
		//System.out.println("Automatic reading...");
		//Link to the bridge
		bmBridge.setAutomaticReader(aReader);

		AutomaticExcelWriterAndDownloader aWriter = new AutomaticExcelWriterAndDownloader(60, bmBridge);
		bmBridge.setAutomaticWriter(aWriter);

		//Prepare USB connection

		bmBridge.mHost.execAdb(USB_PCHost._PHONE_1_INDEX);
		
		//Test encryption
		TestEncryption test = new TestEncryption();
		test.testSignatureEncrypted();
		

	}




}
