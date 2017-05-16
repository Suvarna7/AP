/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import Jama.Matrix;
import java.io.File; 
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Mert
 */
public class Save {

    public Matrix matrice;
    public String filepath;
    public String filename;

    public Save(String filepath) {
        this.filepath = filepath;
    }

    //better in one place than all over. 
    // We should refactor to do this when the Save() class is created.
    private Boolean setupDirectory(String dirPath) { 
        File handle = new File(dirPath); 
        Boolean dirCreated = handle.mkdirs();
        return dirCreated; 
    }
    
    public void save(Matrix matrice, String filename) throws FileNotFoundException, IOException {   ///It is working
        String dirPath = DIAS.excelFilePath + File.separator + filepath; 
        setupDirectory(dirPath); 
        String fileName = dirPath + File.separator + filename + ".xlsx";
        System.out.println("Using filepath " + filepath + ", saving to address: "+ fileName); 
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet worksheet = workbook.createSheet("POI Worksheet");

            int lastvaluex = matrice.getRowDimension();
            int lastvaluey = matrice.getColumnDimension();
            int ih = 0;
            int jh = 0;

            while (ih < lastvaluex) {

                XSSFRow row = worksheet.createRow(ih);
                ih++;
                while (jh < lastvaluey) {
                    XSSFCell cell = row.createCell(jh);
                    jh++;
                    cell.setCellValue(matrice.get(ih - 1, jh - 1));
                }
                jh = 0;
            }
            workbook.write(fileOut);
            fileOut.flush();
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savedouble(double matrice, String filename) throws FileNotFoundException, IOException {   ///It is working
        String dirPath = DIAS.excelFilePath + File.separator + filepath; 
        setupDirectory(dirPath); 
        String fileName = dirPath + File.separator + filename + ".xlsx";
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet worksheet = workbook.createSheet("POI Worksheet");

            int lastvaluex = 1;
            int lastvaluey = 1;
            int ih = 0;
            int jh = 0;

            while (ih < lastvaluex) {

                XSSFRow row = worksheet.createRow(ih);
                ih++;
                while (jh < lastvaluey) {
                    XSSFCell cell = row.createCell(jh);
                    jh++;
                    cell.setCellValue(matrice);
                }
                jh = 0;
            }
            workbook.write(fileOut);
            fileOut.flush();
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save3D(Matrix matrice, String filename, int kj) throws FileNotFoundException, IOException {   ///It is working
        String dirPath = DIAS.excelFilePath + File.separator + filepath; 
        setupDirectory(dirPath); 
        String fileName = dirPath + File.separator + filename + kj + ".xlsx";
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet worksheet = workbook.createSheet("POI Worksheet");

            int lastvaluex = matrice.getRowDimension();
            int lastvaluey = matrice.getColumnDimension();
            int ih = 0;
            int jh = 0;

            while (ih < lastvaluex) {
                XSSFRow row = worksheet.createRow(ih);
                while (jh < lastvaluey) {
                    XSSFCell cell = row.createCell(jh);
                    cell.setCellValue(matrice.get(ih, jh));
                    jh++;
                }
                ih++;
                jh = 0;
            }
            workbook.write(fileOut);
            fileOut.flush();
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveString(String[] matrice, String filename) throws FileNotFoundException, IOException {   ///It is working
        String dirPath = DIAS.excelFilePath + File.separator + filepath; 
        setupDirectory(dirPath); 
        String fileName = dirPath + File.separator + filename + ".xlsx";
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet worksheet = workbook.createSheet("POI Worksheet");

            int lastvaluex = matrice.length;
            int lastvaluey = 1;
            int ih = 0;
            int jh = 0;

            while (ih < lastvaluex) {
                XSSFRow row = worksheet.createRow(ih);

                while (jh < lastvaluey) {
                    XSSFCell cell = row.createCell(jh);
                    cell.setCellValue(matrice[ih]);
                    jh++;
                }
                ih++;
                jh = 0;
            }
            workbook.write(fileOut);
            fileOut.flush();
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Matrix change(double[][][] matrice, int kj) {

        //    System.out.println(matrice.length+"matrice.length  y");
        //       System.out.println(matrice[0].length+"matrice[0].length  x");
        //     System.out.println(matrice[0][0].length+"matrice[0][0].length  z");
        Matrix newmatrice = new Matrix(matrice.length, matrice[0].length);

        for (int i = 0; i < matrice.length; i++) {
            for (int j = 0; j < matrice[0].length; j++) {
                newmatrice.set(i, j, matrice[i][j][kj]);
            }
        }

        //          printMatrix(newmatrice,"newmatrice");
        return newmatrice;
    }

    public int[] lastvaluereturnxyz(double s[][][]) {
        int lastvaluex = 0;
        int lastvaluey = 0;
        int lastvaluez = 0;

        for (int i = 0; i < s.length; i++) {
            for (int j = 0; j < s[0].length; j++) {
                for (int z = 0; z < s[0][0].length; z++) {
                    if (s[i][j][z] != 0) {
                        lastvaluex = i;
                        lastvaluey = j;
                        lastvaluez = z;
                    }
                }
            }
        }
        int[] dizi = new int[4];
        dizi[1] = lastvaluex;
        dizi[2] = lastvaluey;
        dizi[3] = lastvaluez;

        return dizi;
    }

    public static void printMatrix(Matrix m, String name) {
        System.out.print("\n " + name + ": \n{");
        for (double[] row : m.getArray()) {
            for (double val : row) {
                System.out.print(" " + val);
            }
            System.out.println();
        }
        System.out.println("}");
    }

}
