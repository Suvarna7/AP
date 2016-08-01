package iit.pc.javainterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * The BMBridge class offers a interface between BodyMedia excel file and the server
 * It will read the BM obtained data from and excel file and send it to one of the selected servers 
 * 
 * Uses the library jxl.jar (to read excel files)
 * 
 * @author Caterina Lazaro
 * @version 1.0, November 2015
 *
 */

public class BMBridge {
	//***** FIELDS FOR THE IIT SERVER ******
	private  String IIT_SERVER_ADDRESS_IP = "http://216.47.158.133";
	private String IIT_SERVER_ADDRESS_WRITE = IIT_SERVER_ADDRESS_IP+"/phpSync/insert_into_Table.php";
	private  String IIT_SERVER_ADDRESS_READ = IIT_SERVER_ADDRESS_IP +"/phpSync/read_table_values.php";

	public static String JSON_ID ="empaticaJSON";
	private String TABLE_NAME = "empatica";
	private MyHttpClient httpClient = new MyHttpClient();
	private String sendResult;
	private String getResult;


	//Graphical Interface
	public BMGuiProgram gui;

	//Automatic function
	AutomaticExcellReaderAndSender automaticReader;
	AutomaticExcelWriterAndDownloader automaticWriter;


	//Exceptions flags
	private boolean fileException;
	private boolean internetException;
	private boolean serverException;
	
	//Get last 5 minutes of data
	private int initialRow;
	private int lastRowPast;
	private int lastRowCurrent;
	
	//USB Host
	public USB_PCHost mHost;
	
	//***** FIELDS FOR BODYMEDIA EXCEL FILE ******
		private  String EXCEL_FILE = "";
		private File dataExcel;
		private final Charset charset = Charset.forName("US-ASCII");

		//************ HANDLE DATA **********
		private ArrayList<String> COLUMNS_SET;
		private JSONToSend jsonManager;
		HashMap<String, String> rowRead;

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

			StatusLine status = arg0.getStatusLine();
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


			return null;
		}



	};
	
	/**
	 * Handles a JSON kind of response
	 * @param response
	 * @throws IOException
	 */

	public  void handleJSONResponse(String response) throws IOException{
		try {

			System.out.println(response);
			JSONArray arr = new JSONArray(response);
			List<String> args = new ArrayList<String>();
			if(arr !=null){
				for (int i=0;i<arr.length();i++){ 
					//String jsonStr= arr.get(i).toString();
					//TODO System.out.println("Array object: " + arr.get(i));
					JSONObject obj = (JSONObject)arr.get(i);

					//TODO System.out.println("JSON Convert: " + obj.toString());


					if( searchForUpdateStatus(obj)){
						//Update request
						args.add(arr.get(i).toString());
					}
					else{
						try{
							//TODO Reading request
							//TODO System.out.println("JSON READING: "+obj);
							writeExcelFileWithJSON(obj, 0);
						}catch (IOException e){
							System.out.println("Write excel with JSON Exception: "+e);
							gui.displayError(e.toString());
							if(!internetException){
								internetException = true;
								String messageOut = "There is a JSON Exception: run again";
								if (e.toString().contains("FileNotFoundException"))
									messageOut = "There is an excel exception: Check excel file";
								gui.checkWindow(messageOut);
							}
						}catch (WriteException wE){
							gui.displayError(wE.toString());
							if(!internetException){
								internetException = true;
								gui.checkWindow("There is a Write exception: Check excel");
							}
							
						}
					}
					//System.out.println(i);
					//gui.setSentLabel(sendResult);
				} 
				
				//If there are more past samples, we need to erase them
				int saveLastRow = lastRowCurrent;
				while (lastRowCurrent <= lastRowPast){
					lastRowCurrent ++;
					eraseExcelRow(lastRowCurrent);
				}
				lastRowPast = saveLastRow;
			}
			
			//Inform that writing process is done!
			if(!internetException){
				gui.checkWindow("Ready to run MATLAB algorithm");
			}
			


		}catch (JSONException je){

			//Error
			System.out.println("JSON response error: "+ je);
			//Values not updated
			sendResult = "values_posted... Not updated";
			gui.setSentLabel(sendResult);
			gui.displayError(je.toString());
			if(!internetException){
				internetException = true;
				gui.checkWindow("There is a exception: Check Internet Connection");
			}

		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			gui.setSentLabel(sendResult);
			gui.displayError(e.toString());
			if(!internetException){
				internetException = true;
				gui.checkWindow("There is a exception erasing: Check excel file");
			}
		}
	}

	

	/**
	 * Create the BodyMedia Bridge
	 * @param args
	 */
	public BMBridge(String excel, String server){
		setIIT_SERVER_ADDRESS(server);
		EXCEL_FILE=  excel;
		COLUMNS_SET = new ArrayList<String>();
		System.out.println("columns" + COLUMNS_SET);
		jsonManager = new JSONToSend();	
		rowRead =  new HashMap<String, String>();
		//Creates the Graphical interface
		gui = new BMGuiProgram("Send",this, "IIT Server Interface");

		//Set exception flags
		fileException = false;
		internetException = false;
		serverException = false;
		
		//Excel file: Set row values
		initialRow = 1;
		lastRowPast = 1;
		lastRowCurrent = 0;
	}

	/**
	 * Create the BodyMedia Bridge
	 * @param args
	 */
	public BMBridge(){
		COLUMNS_SET = new ArrayList<String>();
		jsonManager = new JSONToSend();	
		rowRead =  new HashMap<String, String>();
		//Creates the Graphical interface
		gui = new BMGuiProgram("Send",this, "IIT Server Interface");

		//Set exception flags
		fileException = false;
		internetException = false;
		serverException = false;
		
		//File reset
		initialRow = 1;



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

	/**
	 * Set the excel file
	 */
	public void setExcelFile(File excel){
		dataExcel = excel;
	}

	/**
	 * Read info from an excel file
	 * @param args
	 * @throws IOException 
	 */
	public List<HashMap<String, String>> readExcelValues(){
		List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		//Open excel file
		Path readFile = Paths.get(dataExcel.getAbsolutePath());
		System.out.println("Read file: "+dataExcel.getAbsolutePath());
		Workbook w;
		try {
			w = Workbook.getWorkbook(dataExcel);
			// Get the first sheet
			Sheet sheet = w.getSheet(0);
			// Loop over all elements
			for (int i = 0; i < sheet.getRows(); i++) {
				for (int j = 0; j < sheet.getColumns(); j++) {
					if (i == 0){
						//TODO First row: name of columns in the table
						COLUMNS_SET.add(sheet.getCell(j, i).getContents());

					}else{
						Cell cell = sheet.getCell(j, i);
						//CellType type = cell.getType();
						rowRead.put(COLUMNS_SET.get(j), sheet.getCell(j, i).getContents() );
						//System.out.println(cell.getContents());

					}

				}
				//Each row will be added to form a JSON object
				if(i>0){
					//Form the row to be added in the JSONManager
					rowRead.put("synchronized", "n");
					rowRead.put("table_name", getTABLE_NAME());
					result.add(rowRead);
					//jsonManager.insertRow(rowRead);
					//Reset the rowRead
					rowRead =  new HashMap<String, String>();
				}

			}
			w.close();
		} catch (BiffException e) {
			gui.displayError(e.toString());
			if (!fileException){
				fileException = true;
				gui.checkWindow("There is a biffException: Check Excel file");
			}
			e.printStackTrace();
		} catch (IOException io){
			gui.displayError(io.toString());
			if (!fileException){
				fileException = true;
				gui.checkWindow("There is a IOException: Check Excel file");
			}


		}
		return result;
	}
	/**
	 * Read values from Excel file and update the JSONToSend object
	 * @throws IOException
	 */
	public void readExcelValuesAndUpdateJSON(){

		jsonManager.setValues(readExcelValues());
		sendResult = "values_posted...Values read";
		gui.setSentLabel(sendResult);

	}
	/**
	 * Write new values on the excel file
	 * @param jsonArray
	 * @param sheetNum
	 * @throws IOException
	 * @throws WriteException 
	 */

	public void writeExcelFileWithJSON(JSONObject jsonRow, int sheetNum) throws IOException, WriteException{
		//Open excel file
		//Path readFile = Paths.get(dataExcel.getAbsolutePath());
		//System.out.println("Read file: "+dataExcel.getAbsolutePath());
		try {
			//FileInputStream fileIn = new FileInputStream(bodymediaExcel);
			Workbook workbook = Workbook.getWorkbook(dataExcel);
			FileOutputStream fileOut = new FileOutputStream(dataExcel);
			//System.out.println("Read file: "+dataExcel);

			WritableWorkbook wb = Workbook.createWorkbook(fileOut, workbook);

			WritableSheet sheet = wb.getSheet(0);

			//Get the first empty row of the sheet:
			/*int lastRow = sheet.getRows();
			for (int i =1; i < lastRow +1; i ++){
				String cont = sheet.getCell(0, i).getContents();
				System.out.println("Excel content -"+i+"- cont");
				if (cont == null || cont == ""){
					//This row is empty
					lastRow = i;
					//Exit loop
					break;
				}
				
			}*/
			int numColumn = sheet.getColumns();
			int jCol = 0;
			//System.out.println("Columns vs json: "+numColumn +" - "+(jsonRow.length()-2));
			//Iterate thru the JSON Array 
			//NOTE: Json contains two extra columns (table_name and synchronized)

			//First, get the order of fields in excel file:
			HashMap<String, Integer> colAssociation = new HashMap<String, Integer>();
			for (int i = 0; i < numColumn; i ++){
				colAssociation.put(sheet.getCell(i, 0).getContents(), i);


			}
			//System.out.println("Sizes: "+jsonRow.length() + " vs " + numColumn);
			if (jsonRow.length() -1 == numColumn ){
				//Add each value of json at the after the last row
				Iterator <String> keys = jsonRow.keys();
				//keys.next();
				while (keys.hasNext()){
					String k = (String)keys.next();
					//System.out.println("Pre Key: " + k);

					if(colAssociation.containsKey(k)){
						//System.out.println("Key: " + k);
						String val = jsonRow.getString(k);
						sheet.addCell(new Label (colAssociation.get(k), initialRow , String.valueOf(val)));
						//TODO
					}
				}
						


			}
			//Once the modifications are done
			wb.write(); 
			wb.close();
			fileOut.close();
			workbook.close();

			//System.out.println("End excel write: "+initialRow);
			
			//Update last written row
			lastRowCurrent = initialRow;
			//For the next sample data:
			initialRow ++;


		} catch (BiffException e) {
			e.printStackTrace();
			gui.displayError(e.toString());
			if(!internetException){
				internetException = true;
				gui.checkWindow("There is a exception: Check excel file");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			gui.displayError(e.toString());
			if(!internetException){
				internetException = true;
				gui.checkWindow("There is a exception: Check Internet Connection");
			}
		}

	}
	
	/**
	 * Write new values on the excel file
	 * @param jsonArray
	 * @param sheetNum
	 * @throws IOException
	 * @throws WriteException 
	 */

	public void eraseExcelRow(int row) throws IOException, WriteException{
		//Open excel file
		try {
			Workbook workbook = Workbook.getWorkbook(dataExcel);
			FileOutputStream fileOut = new FileOutputStream(dataExcel);
			WritableWorkbook wb = Workbook.createWorkbook(fileOut, workbook);
			WritableSheet sheet = wb.getSheet(0);

			
			int numColumn = sheet.getColumns();

			//Erase all values on that row
			for (int i = 0; i < numColumn; i ++)
				sheet.addCell(new Label (i, row , ""));
			
		
				
			//Once the modifications are done
			wb.write(); 
			wb.close();
			fileOut.close();
			workbook.close();


		} catch (BiffException e) {
			e.printStackTrace();
			gui.displayError(e.toString());
			if(!internetException){
				internetException = true;
				gui.checkWindow("There is a exception: Check excel file");
			}
		
		}

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
		initialRow = 1;
		
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

	public String getSendResult() {
		return sendResult;
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

	private boolean searchForUpdateStatus(JSONObject obj){
		try{
			if(obj.get("update_status").equals("yes")){
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
			getResult = "values_posted... Values_read";
			gui.setGetLabel(getResult);
			//writeExcelFileWithJSON(obj, 0);
			//Do nothing, but!
			return false;
		}

	}

	/**
	 * Restart the exceptions popups
	 */
	public void resetExceptionFlags(){
		internetException = false;
		serverException = false;
		fileException = false;
	}
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
		bmBridge.mHost = new USB_PCHost(bmBridge);
		bmBridge.mHost.execAdb();

	}




}
