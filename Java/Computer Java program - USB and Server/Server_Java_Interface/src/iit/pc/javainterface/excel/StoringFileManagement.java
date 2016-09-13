package iit.pc.javainterface.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONException;
import org.json.JSONObject;


import iit.pc.javainterface.BMBridge;




public class StoringFileManagement {
	
	
		private File dataExcelPath;
		private ArrayList<String> COLUMNS_SET;
		HashMap<String, String> rowRead;
		//private final Charset charset = Charset.forName("US-ASCII");
		//Get last 5 minutes of data
		public int initialRow;
		private int lastRowPast;
		private int lastRowCurrent;
		
		private int columnNumber = 30;
		
		BMBridge mBridge;
		
		public StoringFileManagement(BMBridge bridge){
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
			
			//Reset file
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
			System.out.println("Read file: "+dataExcelPath.getAbsolutePath());
			try {
				POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(dataExcelPath));
			    HSSFWorkbook wb = new HSSFWorkbook(fs);
			    HSSFSheet sheet = wb.getSheetAt(0);
			    HSSFRow row;
			    HSSFCell cell;

				// Loop over all elements
				for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
						row = sheet.getRow(i);
						if (i==0){
							//Todo for the first row --- with column names
							cell = row.getCell((short)0);
			                if(cell != null) {
			                    // Your code here
			                }
						
					}
					//Each row will be added to form a JSON object
					else if(i>0){
						//Form the row to be added in the JSONManager						
					}
				}
				
				wb.close();
				fs.close();
			} catch (Exception  e) {
				mBridge.displayFileException(e,"Read", "check excel file");
				e.printStackTrace();
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

		public void writeExcelFileWithJSON(JSONObject jsonRow, int sheetNum) throws IOException{
			//Open excel file
			//Path readFile = Paths.get(dataExcel.getAbsolutePath());
			//System.out.println("Read file: "+dataExcel.getAbsolutePath());
			try {
				//Writer
				FileOutputStream out = new FileOutputStream(dataExcelPath);
				//Get read file
			    FileInputStream fis = new FileInputStream(dataExcelPath);
			    HSSFWorkbook wb = new HSSFWorkbook(fis);
			    HSSFSheet sheet = wb.getSheetAt(0);

				//Get number of columns from JSON 
				int numColumn = jsonRow.length();
				
				//Initialize first row with JSON Keys:
				Iterator<?> keys = jsonRow.keys();
				int rows = sheet.getPhysicalNumberOfRows();
				if ( rows == 0){
					//Generate first row:
					int j = 0;
					while( keys.hasNext() ) {
						Cell c = sheet.getRow(0).getCell(j);
						c.setCellValue((String)keys.next());
						j++;
					}
					columnNumber = j;
					rows ++;
					
				}
				Row row = sheet.getRow(rows);
				int j =0;
				Cell c ;
				
				while( keys.hasNext() ) {
				    String key = (String)keys.next();
				    String val = jsonRow.getString(key);
				    c = row.createCell(j);
				    c.setCellValue(val);
					j++;
				}

				
				
				//Once the modifications are done
				wb.write(out);
				//Close all relevant files
				wb.close();
				out.close();
				fis.close();


				//System.out.println("End excel write: "+initialRow);
				
				//Update last written row
				lastRowCurrent = initialRow;
				//For the next sample data:
				initialRow ++;


			} catch (JSONException e) {
				e.printStackTrace();
				mBridge.displayInternetException(e,"Internet", "check Internet connection");

			}catch (ArrayIndexOutOfBoundsException e){
				System.out.println("Data might be corrupted: " );
				System.out.println(dataExcelPath +"  "+jsonRow);
				
				mBridge.displayFileException(e, "Write","check excel file");

				
				restartExcelFile();
			}
			
			

		}
		
		public void updateRowsParameters() throws IOException{
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
			//From the very begining
			int start = 0;
			
			try {
				//Writer
				FileOutputStream out = new FileOutputStream(dataExcelPath);
				System.out.println("File output done: "+dataExcelPath);
				//Get read file
				//InputStream ExcelFileToRead = new FileInputStream(dataExcelPath.getAbsolutePath());
				//HSSFWorkbook wb = new HSSFWorkbook(ExcelFileToRead);
			    FileInputStream fis = new FileInputStream(dataExcelPath);
		        //POIFSFileSystem poifs = new POIFSFileSystem(fis);
				System.out.println("File input done: ");
			    HSSFWorkbook wb = new HSSFWorkbook(fis);
				System.out.println("Workbook done");
			    HSSFSheet sheet = wb.getSheetAt(0);

				//Get the first empty row of the sheet:
				int lastRowNumber = sheet.getPhysicalNumberOfRows();
				for (int i =1; i < lastRowNumber +1; i ++){
					String cont = sheet.getRow(i).getCell(columnNumber).getStringCellValue();
					System.out.println("Excel content -"+i+"- cont");
					if (cont == null || cont == ""){
						//This row is empty
						lastRowNumber = i;
						//Exit loop
						break;
					}
					
				}

				
				while (start<lastRowNumber){
					for (int j = 0; j < columnNumber; j++){
				        // Erase the cell
				        sheet.getRow(start).createCell(j).setCellValue("");
					}
					start ++;
					
				}

				//Once the modifications are done
				wb.write(out);
				//Close
				wb.close();
				out.close();
				fis.close();

			}catch(Exception e){
				System.out.println("ERROR resetting excel file: "+e);
			}

			
		}
		
		/**
		 * Write new values on the excel file
		 * @param jsonArray
		 * @param sheetNum
		 * @throws IOException
		 * @throws WriteException 
		 */

		public void eraseExcelRow(int row) throws IOException {
			//Open excel file
			try {
				//Writer
				FileOutputStream out = new FileOutputStream(dataExcelPath);
				//Get read file
			    FileInputStream fis = new FileInputStream(dataExcelPath);
			    HSSFWorkbook wb = new HSSFWorkbook(fis);
			    HSSFSheet sheet = wb.getSheetAt(0);
				

				//Erase all values on that row
				for (int i = 0; i < columnNumber; i ++)
			        //Erase the cell
					sheet.getRow(row).createCell(i).setCellValue("");				
					
				//Once the modifications are done
				wb.write(out);
				//Close
				wb.close();
				out.close();
				fis.close();




			} catch (Exception e) {
				e.printStackTrace();
				mBridge.displayInternetException(e, "Internet","check Internet connection");

			
			}

		}

	 

	


}
