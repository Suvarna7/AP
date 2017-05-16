/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import Jama.Matrix;

/**
 *
 * @author User
 */
public class Zmu_other_JF {
    
    private int n;
    private int m;
    private double mean_other;
    private double std_other;
    private Matrix X;
    
   
    
    public Zmu_other_JF(double mean_other, double std_other, Matrix X){
        this.mean_other=mean_other;
        this.std_other=std_other;
        this.X=X;
        n=X.getRowDimension();
        m=X.getColumnDimension();    
    }
    
      public Matrix zmu_other_JF(){  
    /*  //////////////////////////////////Inputs Zmu_other_JF///////////////////////////////////////////////////////////////////////////////////
      System.out.println("/////////////////////////////////Inputs Zmu_other_JF///////////////////////////////////////////////////////////////");
      System.out.println(mean_other+"mean_other");
      System.out.println(std_other+"std_other");
      printMatrix(X,"X");
      System.out.println("/////////////////////////////////Inputs Zmu_other_JF///////////////////////////////////////////////////////////////");
      //////////////////////////////////Inputs Zmu_other_JF///////////////////////////////////////////////////////////////////////////////////    */
          
          Matrix Y = new Matrix (n,m);
           
             for(int i=0;i<n;i++){
                 for(int j=0;j<m;j++){
                    Y.set(i,j,((X.get(i, j)-mean_other)/(std_other)));
                 }
       }      
          /*    ///////////////////////////////////////////////////Output////////////////////////////////////////////////////////////////////////////////////
              System.out.println("////////////////////////////////Output Zmu_other_JF////////////////////////////////////////////////////////////////////");
              printMatrix(Y,"    Y");
              System.out.println("////////////////////////////////Output Zmu_other_JF////////////////////////////////////////////////////////////////////");
              ///////////////////////////////////////////////////Output///////////////////////////////////////////////////////////////////////////////////*/
             return Y;
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
