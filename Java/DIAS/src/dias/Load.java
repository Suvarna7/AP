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
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Mert
 */
public class Load {
    
    public String filepath;
    double [][][] matrice3D;
    
    public Load(String filepath){
        this.filepath=filepath;
    }
    
    
         public Matrix load(Matrix matrice,String filename)throws FileNotFoundException , IOException{
        
         FileInputStream fis = null;
         try {
    
      String fileName= DIAS.excelFilePath+"\\" +filepath+"\\"+filename+".xlsx" ; 
fis = new FileInputStream(fileName);
XSSFWorkbook calismaKitap = new XSSFWorkbook(fis);
XSSFSheet sheet = calismaKitap.getSheetAt(0);
Iterator rows = sheet.rowIterator();
int ih=0;
int jh=0;

  while(rows.hasNext()) {
                    XSSFRow row = (XSSFRow) rows.next();
                    Iterator cells = row.cellIterator();
                    ih++;
          while (cells.hasNext()) {
                        XSSFCell cell = (XSSFCell) cells.next();
                         jh++;
                    //     matrice=createnewMatrix(ih,jh, matrice);
                         matrice.set(ih-1,jh-1, cell.getNumericCellValue());
          }
                   jh=0;
    }
    ih=0;
    jh=0;
         }
catch (FileNotFoundException e) {
    e.printStackTrace();
} 
catch (IOException e) {
    e.printStackTrace();
}
         
        return matrice;
    }
         
         
        public double [][][] load3D(double [][][] matrice3d,String filename,int kj)throws FileNotFoundException , IOException{
        Matrix matrice =new Matrix (matrice3d.length,matrice3d[0].length);
        
         FileInputStream fis = null;
         
         
   for(int t=0;t<kj;t++){
            
         try {
    

             String fileName= "D:\\Phd\\Research\\Kamuran`s Code\\"+filepath+"\\"+filename+t+".xlsx" ; 
fis = new FileInputStream(fileName);
XSSFWorkbook calismaKitap = new XSSFWorkbook(fis);
XSSFSheet sheet = calismaKitap.getSheetAt(0);
Iterator rows = sheet.rowIterator();
int ih=0;
int jh=0;

  while(rows.hasNext()) {
                    XSSFRow row = (XSSFRow) rows.next();
                    Iterator cells = row.cellIterator();
                    ih++;
          while (cells.hasNext()) {
                        XSSFCell cell = (XSSFCell) cells.next();
                         jh++;
              //           matrice=createnewMatrix(ih,jh, matrice);
                         matrice.set(ih-1,jh-1, cell.getNumericCellValue());
          }
                   jh=0;
    }
    ih=0;
    jh=0;
         }
catch (FileNotFoundException e) {
    e.printStackTrace();
} 
catch (IOException e) {
    e.printStackTrace();
}
         matrice3D = new double [matrice.getRowDimension()][matrice.getColumnDimension()][kj];
         
         for(int i=0;i<matrice.getRowDimension();i++)
           for(int j=0;j<matrice.getColumnDimension();j++)
                    matrice3D[i][j][t]=matrice.get(i, j);
         }
        
   return matrice3D;
    }
         
         
         
         
         
       /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
         public double loaddouble(String filename)throws FileNotFoundException , IOException{
      ///  double[][] matrice=new double[150][150];
        double matrice=0;
         FileInputStream fis = null;
         try {
    
      String fileName= "D:\\Phd\\Research\\Kamuran`s Code\\"+filepath+"\\"+filename+".xlsx" ; 
fis = new FileInputStream(fileName);
XSSFWorkbook calismaKitap = new XSSFWorkbook(fis);
XSSFSheet sheet = calismaKitap.getSheetAt(0);
Iterator rows = sheet.rowIterator();
int ih=0;
int jh=0;

  while(rows.hasNext()) {
                    XSSFRow row = (XSSFRow) rows.next();
                    Iterator cells = row.cellIterator();
                    ih++;
          while (cells.hasNext()) {
                        XSSFCell cell = (XSSFCell) cells.next();
                         jh++;
                         matrice= cell.getNumericCellValue();
          }
                   jh=0;
    }
    ih=0;
    jh=0;
         }
catch (FileNotFoundException e) {
    e.printStackTrace();
} 
catch (IOException e) {
    e.printStackTrace();
}
         
        return matrice;
    }
         
         
             public String [] loadString(String filename,int kj)throws FileNotFoundException , IOException{
      ///  double[][] matrice=new double[150][150];
         String [] matrice= new String [kj];
         FileInputStream fis = null;
         try {
    
      String fileName= "D:\\Phd\\Research\\Kamuran`s Code\\"+filepath+"\\"+filename+".xlsx" ; 
fis = new FileInputStream(fileName);
XSSFWorkbook calismaKitap = new XSSFWorkbook(fis);
XSSFSheet sheet = calismaKitap.getSheetAt(0);
Iterator rows = sheet.rowIterator();
int ih=0;
int jh=0;

  while(rows.hasNext()) {
                    XSSFRow row = (XSSFRow) rows.next();
                    Iterator cells = row.cellIterator();
                    ih++;
          while (cells.hasNext()) {
                        XSSFCell cell = (XSSFCell) cells.next();
                         jh++;
                      matrice[ih-1]= cell.getStringCellValue();
          }
                   jh=0;
    }
    ih=0;
    jh=0;
         }
catch (FileNotFoundException e) {
    e.printStackTrace();
} 
catch (IOException e) {
    e.printStackTrace();
}
         
        return matrice;
    }
         
         
         
         
                  public Matrix createnewMatrix (int newdimensionx,int newdimensiony, Matrix oldmatrice){
               Matrix newMatrice = new Matrix (newdimensionx,newdimensiony);
               
               for( int i=0; i<oldmatrice.getRowDimension();i++)
                      for( int j=0; j<oldmatrice.getColumnDimension();j++)
                         newMatrice.set(i,j,oldmatrice.get(i, j));
                          
                         return newMatrice;
                   }
    
    
}
