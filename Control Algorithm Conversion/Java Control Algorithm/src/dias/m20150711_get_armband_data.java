/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import Jama.Matrix;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author Mert
 */
public class m20150711_get_armband_data {
    public static Matrix armband_data_with_time= new Matrix (7165,30);
    public static Matrix ee;
    public static Matrix phys_act;
    public static Matrix sleep;
    public static Matrix gsr;
    public static Matrix armband_data= new Matrix (8000,30);
    public static double eedouble;
    public static double gsrdouble;
    public static double sleepdouble;
    public static double phys_actdouble;
    
    public m20150711_get_armband_data(Matrix ee, Matrix phys_act, Matrix sleep, Matrix gsr){
        this.ee=ee;
        this.phys_act=phys_act;
        this.sleep=sleep;
        this.gsr=gsr;
    }
    
    
    public Matrix m20150711_get_armband_data (){

        eedouble=0;
        gsrdouble=0;
        phys_actdouble=0;
        sleepdouble=0;
               
    try {
    FileInputStream file = new FileInputStream(new File(DIAS.bodymediaFileUrl));

    HSSFWorkbook workbook = new HSSFWorkbook(file);

    HSSFSheet sheet = workbook.getSheetAt(0);
    
    Iterator<Row> rowIterator = sheet.iterator();
    int s=0;
    int i=0;
    int j=0;
    int kx=0;
    while(rowIterator.hasNext()) {
        Row row = rowIterator.next();
        Iterator<org.apache.poi.ss.usermodel.Cell> cellIterator = row.cellIterator();
        i++;
        s=0; 
        while(cellIterator.hasNext()) {
            j++;
            org.apache.poi.ss.usermodel.Cell cell = cellIterator.next();
            switch(cell.getCellType()) {
                case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN:
                    break;
                 case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC:
                    armband_data.set(i,j,cell.getNumericCellValue());
                    
                    
                    if(kx==28)
                    armband_data_with_time.set(i,5,cell.getNumericCellValue()); // Heat-Flux Average
                    
                    if(kx==27)
                    armband_data_with_time.set(i,4,cell.getNumericCellValue()); //Sleep Classification
                    
                    if(kx==26)
                    armband_data_with_time.set(i,3,cell.getNumericCellValue()); //Activity Class
                    
                    if(kx==25)
                    armband_data_with_time.set(i,2,cell.getNumericCellValue()); //Distance
                      
                    if(kx==24)
                    armband_data_with_time.set(i,1,cell.getNumericCellValue()); //Speed
                        
                    if(kx==23)
                    armband_data_with_time.set(i,0,cell.getNumericCellValue()); //MET 's
                    
                    kx++;
                    
                 break;

                case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING:
                    if(cell.getStringCellValue().equals("NAN")){
                        if(s==0){
                            i--;
                            s=1;
                            }
                        }
                break;
                }
            }
            kx=0;
            j=0;
        }

        file.close();
        s=0;

        eedouble=0;
        gsrdouble=0;
        sleepdouble=0;
        phys_actdouble=0;

        //XXX OPTIMIZE : These are hardcoded to get the last row with real data. If we're feeling ambitious, let's make it target the last nonzero row. Anyway.
        eedouble= armband_data.get(7164,18);
        gsrdouble= armband_data.get(7164,14);
        sleepdouble= armband_data.get(7164,16);
        phys_actdouble= armband_data.get(7164,17);


        } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace(); 
    }

    return armband_data;

   }
    
           public static int[] lastvaluereturnx (Matrix s){
           int lastvaluex=0;
           int lastvaluey=0;
     
           for(int i=0;i<s.getRowDimension();i++){
               for(int j=0;j<s.getColumnDimension();j++){
                   if(s.get(i, j)!=0){
                       lastvaluex=i;
                       lastvaluey=j;
                   }
               }
           }
           int [] resultlocation=new int[3];
           resultlocation[1]=lastvaluex;
           resultlocation[2]=lastvaluey;
           
           return resultlocation;
       }
           
             public static void printMatrix(Matrix m, String name){
		System.out.print("\n "+name+": \n{");
		for (double[] row: m.getArray()){
			for (double val: row)
				System.out.print(" "+val);
			System.out.println();
		}
		System.out.println("}");
	}
     
     
}
