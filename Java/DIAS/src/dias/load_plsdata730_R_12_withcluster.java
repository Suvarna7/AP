/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import Jama.Matrix;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Mert
 */
public class load_plsdata730_R_12_withcluster {

    public Matrix matrix;

    public Matrix loadwithsheetname(String filename, String Sheetname) throws FileNotFoundException, IOException {
        FileInputStream fis = null;
        try {

            String fileName = DIAS.excelFilePath + filename + ".xlsx";
//////////////////////////////////////////////////Optimization of memory usage/////////////////////////////////////////////////////////////////////////////

            if (Sheetname.equals("x_cluster1")) {
                matrix = new Matrix(1899, 37);
            } else if (Sheetname.equals("x_cluster2")) {
                matrix = new Matrix(1865, 37);
            } else if (Sheetname.equals("x_cluster3")) {
                matrix = new Matrix(1844, 37);
            } else if (Sheetname.equals("x_cluster4")) {
                matrix = new Matrix(1876, 37);
            } else if (Sheetname.equals("x_cluster5")) {
                matrix = new Matrix(2238, 37);
            } else if (Sheetname.equals("y_cluster1")) {
                matrix = new Matrix(1899, 37);
            } else if (Sheetname.equals("y_cluster2")) {
                matrix = new Matrix(1865, 37);
            } else if (Sheetname.equals("y_cluster3")) {
                matrix = new Matrix(1844, 37);
            } else if (Sheetname.equals("y_cluster4")) {
                matrix = new Matrix(1876, 37);
            } else if (Sheetname.equals("y_cluster5")) {
                matrix = new Matrix(2238, 37);
            } else if (Sheetname.equals("thita_mm1")) {
                matrix = new Matrix(1, 37);
            } else if (Sheetname.equals("thita_mm2")) {
                matrix = new Matrix(1, 37);
            } else if (Sheetname.equals("thita_mm3")) {
                matrix = new Matrix(1, 37);
            } else if (Sheetname.equals("thita_mm4")) {
                matrix = new Matrix(1, 37);
            } else if (Sheetname.equals("thita_mm5")) {
                matrix = new Matrix(1, 37);
            } else if (Sheetname.equals("rc1")) {
                matrix = new Matrix(37, 1899);
            } else if (Sheetname.equals("rc2")) {
                matrix = new Matrix(37, 1865);
            } else if (Sheetname.equals("rc3")) {
                matrix = new Matrix(37, 1844);
            } else if (Sheetname.equals("rc4")) {
                matrix = new Matrix(37, 1876);
            } else if (Sheetname.equals("rc5")) {
                matrix = new Matrix(37, 2238);
            }
//////////////////////////////////////////////////////Optimization of memory usage/////////////////////////////////////////////////////////////////////////////

            fis = new FileInputStream(fileName);
            XSSFWorkbook calismaKitap = new XSSFWorkbook(fis);
            XSSFSheet sheet = calismaKitap.getSheet(Sheetname);
            Iterator rows = sheet.rowIterator();
            int ih = 0;
            int jh = 0;

            while (rows.hasNext()) {
                XSSFRow row = (XSSFRow) rows.next();
                Iterator cells = row.cellIterator();
                ih++;
                while (cells.hasNext()) {
                    XSSFCell cell = (XSSFCell) cells.next();
                    jh++;
                    matrix.set(ih - 1, jh - 1, cell.getNumericCellValue());
                }
                jh = 0;
            }
            ih = 0;
            jh = 0;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return matrix;
    }

    public void loadplsdata() throws IOException {

        plsdata_730_R_12_withcluster_16_data pls730 = new plsdata_730_R_12_withcluster_16_data();

        pls730.rc1 = loadwithsheetname("pls_raw", "rc1");
        pls730.rc2 = loadwithsheetname("pls_raw", "rc2");
        pls730.rc3 = loadwithsheetname("pls_raw", "rc3");
        pls730.rc4 = loadwithsheetname("pls_raw", "rc4");
        pls730.rc5 = loadwithsheetname("pls_raw", "rc5");

        pls730.xcluster1 = loadwithsheetname("pls_raw", "x_cluster1");
        pls730.xcluster2 = loadwithsheetname("pls_raw", "x_cluster2");
        pls730.xcluster3 = loadwithsheetname("pls_raw", "x_cluster3");
        pls730.xcluster4 = loadwithsheetname("pls_raw", "x_cluster4");
        pls730.xcluster5 = loadwithsheetname("pls_raw", "x_cluster5");

        pls730.ycluster1 = loadwithsheetname("pls_raw", "y_cluster1");
        pls730.ycluster2 = loadwithsheetname("pls_raw", "y_cluster2");
        pls730.ycluster3 = loadwithsheetname("pls_raw", "y_cluster3");
        pls730.ycluster4 = loadwithsheetname("pls_raw", "y_cluster4");
        pls730.ycluster5 = loadwithsheetname("pls_raw", "y_cluster5");

        pls730.thita_mm1 = loadwithsheetname("pls_raw", "thita_mm1");
        pls730.thita_mm2 = loadwithsheetname("pls_raw", "thita_mm2");
        pls730.thita_mm3 = loadwithsheetname("pls_raw", "thita_mm3");
        pls730.thita_mm4 = loadwithsheetname("pls_raw", "thita_mm4");
        pls730.thita_mm5 = loadwithsheetname("pls_raw", "thita_mm5");
    }

}
