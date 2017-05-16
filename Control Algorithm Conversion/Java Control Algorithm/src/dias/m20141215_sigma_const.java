/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import Jama.Matrix;
import de.xypron.jcobyla.Calcfc;
import de.xypron.jcobyla.Cobyla;
import de.xypron.jcobyla.CobylaExitStatus;


/**
 *
 * @author User
 */
public class m20141215_sigma_const {
    
    public Matrix X_n= new Matrix (1,9);
    public double [] x_a;
    public double [] x= new double [9];
    public double[] sol;
    public double f;
    public double [] xtemp=new double [8];
    
    public double rhobeg = 1;
   // public double rhoend = 1.0e-45;
    public double rhoend = 1.0e-6;
    public int iprint = 0;
   // public int maxfun = 500000;
    public int maxfun = 3000;
    
    public m20141215_sigma_const(Matrix X_n,double [] x_a){
        this.X_n=X_n;
        this.x_a=x_a;
    }
    
    public double [] sigma_const(){
/*////////////////////////////////////////////////////////////Sigma_const_inputs/////////////////////////////////////////////////////////////////
System.out.println("/////////////////////////////////Sigma_const_inputs///////////////////////////////////////////////////////////////////////");
printMatrix(X_n,"X_n");
System.out.println("x_a");
for(int i=0;i<x_a.length;i++)
System.out.print(x_a[i]+"            ");
System.out.println();
System.out.println("/////////////////////////////////Sigma_const_inputs///////////////////////////////////////////////////////////////////////");
////////////////////////////////////////////////////////////Sigma_const_inputs/////////////////////////////////////////////////////////////////*/
        
        for(int i=0;i<8;i++)
        xtemp[i]=0;
        
        f=0;

        Cobyla cobyla = new Cobyla();
		Calcfc calcfc = new Calcfc() {

			@Override
			public double compute(int n, int m, double[] x,	double[] con) {
                            f=0;
                            double [] temp= new double [10];
                           for(int i=0;i<X_n.getRowDimension();i++){
                             
                               if(i==0){
                                   con[0]=x[i];
                                   con[1]=-x[i]+250;
                               }
                               else if(i==1){
                                   con[2]=x[i]-40;
                                   con[3]=-x[i]+600;
                               }
                               else if(i==2){
                                   con[4]=x[i];
                                   con[5]=-x[i]+600;
                               }
                               else if(i==3){
                                   con[6]=x[i];
                                   con[7]=-x[i]+600;
                               }
                                 else if(i==4){
                                   con[8]=x[i]-0.034;
                                   con[9]=-x[i]+0.136;
                               }
                                   else if(i==5){
                                   con[10]=x[i]-0.0185;
                                   con[11]=-x[i]+0.074;
                               }
                                        else if(i==6){
                                   con[12]=x[i]-0.65;
                                   con[13]=-x[i]+2.6;
                               }
                                       else if(i==7){
                                   con[14]=x[i]-10;
                                   con[15]=-x[i]+180;
                               }
                                   else{
                                   con[16]=0;
                                   con[17]=0;  
                                    }
                               
                             
                                 f =(x[i]-X_n.get(i, 0))*(x[i]-X_n.get(i, 0))+f;
                                 
                                 xtemp[i]=x[i];
                            }
                            
                            return f;
			}
                };
		CobylaExitStatus result1;
	        result1 = cobyla.findMinimum(calcfc, 8, 18, x_a, rhobeg, rhoend, iprint, maxfun);
    
/* ////////////////////////////////////////////////////////////Sigma_const_output/////////////////////////////////////////////////////////////////
System.out.println("/////////////////////////////////Sigma_const_output///////////////////////////////////////////////////////////////////////////////////");
System.out.println("xtemp");
for(int i=0;i<xtemp.length;i++)
System.out.print(xtemp[i]+"         ");
System.out.println();
System.out.println("/////////////////////////////////Sigma_const_output///////////////////////////////////////////////////////////////////////////////////");
////////////////////////////////////////////////////////////Sigma_const_output/////////////////////////////////////////////////////////////////////////////*/
                             
                return xtemp;
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
