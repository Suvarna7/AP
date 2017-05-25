package iit.pc.javainterface.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import iit.pc.javainterface.BMBridge;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelManager {
	private File dataExcelPath;
	private ArrayList<String> COLUMNS_SET;
	HashMap<String, String> rowRead;
	//private final Charset charset = Charset.forName("US-ASCII");
	//Get last 5 minutes of data
	public int initialRow;
	private int lastRowPast;
	private int lastRowCurrent;
	
	BMBridge mBridge;
	
	public ExcelManager(BMBridge bridge){
		//Bridge
		mBridge = bridge;
		//Excel file: Set row values
		initialRow = 1;
		lastRowPast = 1;
		lastRowCurrent = 0;
		//Row read
		rowRead =  new HashMap<String, String>();


	}
	
	/**
	 * Set the excel file
	 */
	public void setExcelFile(File excel){
		dataExcelPath = excel;
		//Restart file
		restartExcelFile();
		
	}
	
	/*****************************************88
	 * READING FUNCTIONS
	 */
	/**
	 * Read info from an excel file
	 * @param args
	 * @throws IOException 
	 */
	public List<HashMap<String, String>> readExcelValues(){
		List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		//Open excel file
		//Path readFile = Paths.get(dataExcelPath.getAbsolutePath());
		Workbook w;
		try {
			w = Workbook.getWorkbook(dataExcelPath);
			// Get the first sheet
			Sheet sheet = w.getSheet(0);
			// Loop over all elements
			for (int i = 0; i < sheet.getRows(); i++) {
				for (int j = 0; j < sheet.getColumns(); j++) {
					if (i == 0){
						//TODO First row: name of columns in the table
						COLUMNS_SET.add(sheet.getCell(j, i).getContents());

					}else{
						//Cell cell = sheet.getCell(j, i);
						//CellType type = cell.getType();
						rowRead.put(COLUMNS_SET.get(j), sheet.getCell(j, i).getContents() );
						//System.out.println(cell.getContents());

					}

				}
				//Each row will be added to form a JSON object
				if(i>0){
					//Form the row to be added in the JSONManager
					rowRead.put("synchronized", "n");
					rowRead.put("table_name", mBridge.getTABLE_NAME());
					result.add(rowRead);
					//jsonManager.insertRow(rowRead);
					//Reset the rowRead
					rowRead =  new HashMap<String, String>();
				}

			}
			w.close();
		} catch (BiffException e) {
			mBridge.displayFileException(e,"Read", "check excel file");
			e.printStackTrace();
		} catch (IOException io){
			mBridge.displayFileException(io,"Read", "check excel file");
			io.printStackTrace();

		}
		return result;
	}
	
	/******************************************************
	 * WRITING FUNCTIONS
	 */
	
	/**
	 * Write new values on the excel file
	 * @param jsonArray
	 * @param sheetNum
	 * @throws IOException
	 * @throws WriteException 
	 */

	public void writeExcelFileWithJSON(JSONObject jsonRow, int sheetNum) throws IOException, WriteException,  java.lang.ArrayIndexOutOfBoundsException{
		//Open excel file
		//Path readFile = Paths.get(dataExcel.getAbsolutePath());
		Workbook workbook;
		FileOutputStream fileOut;
		WritableWorkbook wb;
		WritableSheet sheet; 
		try {


			//Get workbock
			//TODO - IF THE EXCEL WAS WRITTEN WRONGLY NEXT LINE WILL FAIL !
			 workbook = Workbook.getWorkbook(dataExcelPath);
			 //Prepare writing
			 fileOut = new FileOutputStream(dataExcelPath);
			 
			 //wRITE AND READ
			 wb = Workbook.createWorkbook(fileOut, workbook);
			 sheet = wb.getSheet(0);


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
			 try{

			int numColumn = sheet.getColumns();

			//First, get the order of fields in excel file:
			HashMap<String, Integer> colAssociation = new HashMap<String, Integer>();
			for (int i = 0; i < numColumn; i ++){
				//System.out.println("Celll for column: "+i);

				colAssociation.put(sheet.getCell(i, 0).getContents(), i);
				

			}
			//System.out.println("Sizes: "+jsonRow.length() + " vs " + numColumn);\

			//if (jsonRow.length() -BMBridge.COLUMNS_DIFF == numColumn ){
				//Add each value of json at the after the last row
				@SuppressWarnings("unchecked")
				Iterator <String> keys = jsonRow.keys();
				//keys.next();
				while (keys.hasNext()){
					String k = (String)keys.next();
					//System.out.println("next key: "+k);

					if(colAssociation.containsKey(k)){
						String val = null;
						try {
							val = jsonRow.getString(k);
						} 
						catch (JSONException e) {
							e.printStackTrace();
							mBridge.displayInternetException(e,"Internet", "check Internet connection");

						}catch (ArrayIndexOutOfBoundsException e){
							System.out.println("Data might be corrupted: " );
							System.out.println(dataExcelPath +"  "+jsonRow);
							
							mBridge.displayFileException(e, "Write","check excel file");

							restartExcelFile();
						}
						if (val != null){
							//System.out.println("Writing: col "+k+ colAssociation.get(k)+", val"+val);;
							//TODO set numeric format row.createCell(colAssociation.get(k));
							Label l = new Label (colAssociation.get(k), initialRow , String.valueOf(val));
					        /*CellStyle cs = new CellStyle();
					        cs.setDataFormat(df.getFormat("%"));
							l.setCellFormat(arg0);*/
							sheet.addCell(l);
						}else{
							System.out.println(".Closing excel.");
							wb.write(); 
							wb.close();
							fileOut.close();
							workbook.close();
							break;
							
						}//TODO
					}
				}

			//}
						
			//Update last written row
			lastRowCurrent = initialRow;
			//For the next sample data:
			initialRow ++;
			 }finally{
				//Close
				//Once the modifications are done
				//System.out.println("Closing excel!!!");

				wb.write(); 
				wb.close();
				fileOut.close();
				workbook.close();
			}


		} catch (BiffException e) {
			e.printStackTrace();
			mBridge.displayInternetException(e, "Internet", "check Internet connection");
			
			

		} 
		
		

	}
	
	public void updateRowsParameters() throws WriteException, IOException{
		//If there are more past samples, we need to erase them
		int saveLastRow = lastRowCurrent;
		while (lastRowCurrent <= lastRowPast){
				lastRowCurrent ++;
				eraseExcelRow(lastRowCurrent);
		}
		lastRowPast = saveLastRow;
	}
	
	/**********************************************************
	 * RESET EXCEL FILE VALUES
	 */
	
	/**
	 * Reset initial row counter
	 */
	public void resetInitRow(){
		initialRow = 1;

	}
	/**
	 * Erase all rows except for the first one
	 */
	private void restartExcelFile(){
		int start = 1;
		
		try {
			//FileInputStream fileIn = new FileInputStream(bodymediaExcel);
			Workbook workbook = Workbook.getWorkbook(dataExcelPath);
			FileOutputStream fileOut = new FileOutputStream(dataExcelPath);
			//System.out.println("Read file: "+dataExcel);

			WritableWorkbook wb = Workbook.createWorkbook(fileOut, workbook);
			WritableSheet sheet = wb.getSheet(0);

			//Get the first empty row of the sheet:
			int lastRowNumber = sheet.getRows();
			for (int i =1; i < lastRowNumber +1; i ++){
				String cont = sheet.getCell(0, i).getContents();
				System.out.println("Excel content -"+i+"- cont");
				if (cont == null || cont == ""){
					//This row is empty
					lastRowNumber = i;
					//Exit loop
					break;
				}
				
			}
			int numColumn = sheet.getColumns();

			
			
			while (start<lastRowNumber){
				for (int j = 0; j < numColumn; j++)
					 sheet.addCell(new Label (j, start , ""));
				start ++;
			}

			//Once the modifications are done
			wb.write(); 
			wb.close();
			fileOut.close();
			workbook.close();
		}catch(Exception e){
			System.out.println("ERROR resetting excel file");
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
			Workbook workbook = Workbook.getWorkbook(dataExcelPath);
			FileOutputStream fileOut = new FileOutputStream(dataExcelPath);
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
			mBridge.displayInternetException(e, "Internet","check Internet connection");
			
			

		
		}

	}

 

}
