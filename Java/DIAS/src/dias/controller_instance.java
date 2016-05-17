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
 * @author Mert
 */
public class controller_instance {
    
    public Matrix umin;
    public Matrix umaxx;
    public Matrix umax;
    public Matrix total_daily_unit;
    public Matrix du;
    public Matrix g_prediction;
    public Matrix L;
    public Matrix bolus_insulin;
    public Matrix basal_insulin;
    public double minumum_basal;
    public Matrix reference_glucose;
    public int Nu;
    public int st;
    public double body_weight;
    public Matrix insulin_sensitivity_constant;
    public Matrix insulin_sensitivity_factor;
    public int flag_constrains;
    public double [] umaxx_global;
    public Matrix Y;
    public double temp=0;
    public double [] temparray;
    public double [] temparray2;
    public double [] minumumarray;
    public int insulin_max;

    public double rhobeg = 1;
    public double rhoend = 1.0e-6;
    public int iprint = 0;
    public int maxfun = 3000;
   
    public double f1=0;
    public double f2=0;
    public Matrix ins = new Matrix (8,1);
    public Matrix IOB_pred;
    public double V=0;
    public double c [] = new double [16];
    
    public controller_instance (Matrix g_prediction, Matrix L, Matrix bolus_insulin, Matrix basal_insulin ,double minumum_basal, Matrix reference_glucose,int Nu,int st,double body_weight, Matrix insulin_sensitivity_constant,int flag_constrains ){
        this.g_prediction=g_prediction;
        this.L=L;
        this.bolus_insulin=bolus_insulin;
        this.basal_insulin=basal_insulin;
        this.minumum_basal=minumum_basal;
        this.reference_glucose=reference_glucose;
        this.Nu=Nu;
        this.st=st;
        this.body_weight=body_weight;
        this.insulin_sensitivity_constant=insulin_sensitivity_constant;
        this.flag_constrains=flag_constrains;
    }
    
    public void control_ins (){
     /*  //////////////////////////////////////INPUTS////////////////////////////////////////////////////////////////////////////////////////
        System.out.println("////////////////////////INPUTS CONTROL_INS/////////////////////////////////////////////////////////////////////////////////");
        printMatrix(g_prediction,"g_prediction");
        printMatrix(L,"L");
        printMatrix(bolus_insulin,"bolus_insulin");
        printMatrix(basal_insulin,"basal_insulin");
        System.out.println(minumum_basal+"minumum_basal");
        printMatrix(reference_glucose,"reference_glucose");
        System.out.println(Nu+"Nu");
        System.out.println(st+"st");
        System.out.println(body_weight+"body_weight");
        printMatrix(insulin_sensitivity_constant,"insulin_sensitivity_constant");
        System.out.println(flag_constrains+"flag_constrains");
        System.out.println("/////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
        
        umaxx_global= new double [basal_insulin.getRowDimension()];
        
        for(int i=0;i<basal_insulin.getRowDimension();i++)
        umaxx_global[i]=basal_insulin.get(i, basal_insulin.getColumnDimension()-1);
        
        Y=new Matrix(L.getColumnDimension(), 1);
        
        
                  temparray = new double [basal_insulin.getRowDimension()];
                
                for(int i=0;i<basal_insulin.getRowDimension();i++)
                temparray[i]=basal_insulin.get(i, basal_insulin.getColumnDimension()-1);
  
                minumumarray=min(temparray,umaxx_global);

                findconstrains(minumumarray);
                           
         Cobyla cobyla = new Cobyla();
		Calcfc calcfc = new Calcfc() {

			@Override
     public double compute(int n, int m, double[] x,	double[] con) {
                      double  f1=0;
                      double  f2=0;
                      double V=0;
                      double [] cx = new double [16];
                          
                          cx=findconstrains(x);
     
                          con[0]=(-x[0]+cx[0]);
                          con[1]=(x[0]-cx[8]);
                          
                          con[2]=(-x[1]+cx[1]);
                          con[3]=(x[1]-cx[9]);
                          
                          con[4]=(-x[2]+cx[2]);
                          con[5]=(x[2]-cx[10]);
                          
                          con[6]=(-x[3]+cx[3]);
                          con[7]=(x[3]-cx[11]);
                          
                          con[8]=(-x[4]+cx[4]);
                          con[9]=(x[4]-cx[12]);
                          
                          con[10]=(-x[5]+cx[5]);
                          con[11]=(x[5]-cx[13]);
                     
                          con[12]=(-x[6]+cx[6]);
                          con[13]=(x[6]-cx[14]);
                          
                          con[14]=(-x[7]+cx[7]);
                          con[15]=(x[7]-cx[15]);
                
             
                         Matrix xtemp = new Matrix(x.length,1);
                         
                         for(int i=0;i<x.length;i++)
                             xtemp.set(i, 0, x[i]);
       
                         //TODO Change to match
                         //Y=L.times(xtemp).plus(g_prediction);
                         System.out.println(L.getRowDimension() + " x "+ L.getColumnDimension());
                         System.out.println(xtemp.getRowDimension() + "x" + xtemp.getColumnDimension());
                         System.out.println(g_prediction.getRowDimension() + "x" + g_prediction.getColumnDimension());

                         Y=L.times(xtemp).plus(g_prediction);
          
                              
                            double []  temparray2= new double [9];
  
                  temparray2[0]=basal_insulin.get(0, basal_insulin.getColumnDimension()-1);
                              
                              for( int k=1;k<9;k++)
                              temparray2[k]=x[k-1];

                  Matrix  du=diff(temparray2);
           
                              total_daily_unit= insulin_sensitivity_constant.times(body_weight);
        
                              for(int k=0;k<total_daily_unit.getRowDimension();k++)
                              insulin_sensitivity_factor.set(k,0,1800/total_daily_unit.get(k, 0));
              
                             f1=((Y.minus(reference_glucose)).transpose()).times((Y.minus(reference_glucose))).get(0, 0);
                    
                             f2= (((du.transpose()).times(diag(insulin_sensitivity_factor))).times(du)).get(0, 0);
    
                for(int s=0;s<8;s++){                 
                ins.set(s,0,x[s]);
                }
     
                   V=f1+f2;
                         
                             return (V);
                        
                            		}
                };
		CobylaExitStatus result1;
             
                System.out.println("Reference glucose samples: "+ reference_glucose.getRowDimension());
	        result1 = cobyla.findMinimum(calcfc, reference_glucose.getRowDimension(),16,minumumarray, rhobeg, rhoend, iprint, maxfun);    
                
  /*  ///////////////////////////////////////////////////////OUTPUT/////////////////////////////////////////////////////////////////////////////////            
    System.out.println("///////////////////////////////////OUTPUT CONTROLLER_INS//////////////////////////////////////////////////////////////////");
    printMatrix(ins,"ins");
    printMatrix(total_daily_unit,"total_daily_unit");
    printMatrix(insulin_sensitivity_factor,"insulin_sensitivity_factor");
    printMatrix(umaxx,"umaxx");
    printMatrix(IOB_pred,"IOB_pred");
    System.out.println("///////////////////////////////////OUTPUT CONTROLLER_INS//////////////////////////////////////////////////////////////////");
    ///////////////////////////////////////////////////////OUTPUT/////////////////////////////////////////////////////////////////////////////////    */      
                
    }
    
    public double[] min (double [] array1 , double [] array2){
        
        double[] result= new double [array1.length];
        
        for(int i=0;i<array1.length;i++)
            if(array1[i]>array2[i])
                result[i]=array2[i];
            else
               result[i]=array1[i]; 
        
        return result;
    }
    
    public Matrix diff (double [] matrice){
        
        Matrix resultmatrice = new Matrix (matrice.length-1,1);
        
        for(int i=0;i<matrice.length-1;i++)
        resultmatrice.set(i,0,(matrice[i+1]-matrice[i]));
        
        return resultmatrice;
    }
    
    public Matrix diag (Matrix matrice){
        
        Matrix resultmatrice = new Matrix (matrice.getRowDimension(),matrice.getRowDimension());
        
        for(int i=0; i<matrice.getRowDimension();i++)
              for(int j=0; j<matrice.getRowDimension();j++)
                  if(i==j)
                      resultmatrice.set(i,j,matrice.get(i, 0));
                  else
                      resultmatrice.set(i,j,0);
        
        return resultmatrice;
    }
    
    public Matrix mindouble (int integer, Matrix matrice){
        
        Matrix resultmatrice = new Matrix (matrice.getRowDimension(),matrice.getColumnDimension());
               
        for(int i=0;i<matrice.getRowDimension();i++)
            if(integer<matrice.get(i,0))
                resultmatrice.set(i, 0, integer);
            else
                resultmatrice.set(i, 0, matrice.get(i,0));
        
        return resultmatrice;
    }
    
        public Matrix maxdouble (double integer, Matrix matrice){
        
        Matrix resultmatrice = new Matrix (matrice.getRowDimension(),matrice.getColumnDimension());
               
        for(int i=0;i<matrice.getRowDimension();i++)
            if(integer<matrice.get(i,0))
                resultmatrice.set(i, 0, matrice.get(i,0));
            else
                resultmatrice.set(i, 0, integer);
        
        return resultmatrice;
    }
        
        public double [] findconstrains (double [] u){

        double c [] = new double [16];
             IOB_pred= new Matrix (Nu,1);
                       
               for(int i=0;i<Nu;i++)
                   for(int j=0;j<1;j++)
               IOB_pred.set(i, j, 0);
   
               
                            for(int ii=0; ii<Nu; ii++){
                                if (ii==0){
                                   
                                     Matrix basal_insulin_temp= new Matrix (1,basal_insulin.getColumnDimension());
                                    
                                    for(int i=0;i<basal_insulin.getColumnDimension();i++)
                                        basal_insulin_temp.set(0,i, basal_insulin.get(0,i));
     
                                    
                                    m20150711_calculate_IOB calculate_IOB = new m20150711_calculate_IOB(bolus_insulin,basal_insulin_temp);
                                    IOB_pred.set(ii,0,calculate_IOB.IOB());
 
                                }
                                else{
                                    
                                    Matrix bolus_insulin_temp= new Matrix (bolus_insulin.getRowDimension()+ii,1);
                                    
                                    for(int i=0;i<bolus_insulin.getRowDimension();i++)
                                        bolus_insulin_temp.set(i,0,bolus_insulin.get(i, 0));
                                    
                                    for(int i=0;i<ii-1;i++)
                                    bolus_insulin_temp.set(i+bolus_insulin.getRowDimension(),0,0);
                                    
                                    
                                    Matrix basal_insulin_temp2= new Matrix (basal_insulin.getColumnDimension()+ii,1);
                                    
                                    for(int i=0;i<basal_insulin.getColumnDimension();i++)
                                        basal_insulin_temp2.set(i,0, basal_insulin.transpose().get(i,0));

                                    for(int i=0;i<ii-1;i++)
                                    basal_insulin_temp2.set(i+basal_insulin.getColumnDimension(),0,u[i]);
            
                                    m20150711_calculate_IOB calculate_IOB = new m20150711_calculate_IOB(bolus_insulin_temp,basal_insulin_temp2);
                                    IOB_pred.set(ii,0,calculate_IOB.IOB());
                                }
                            }

                              total_daily_unit= insulin_sensitivity_constant.times(body_weight);
                              
                              insulin_sensitivity_factor= new Matrix(total_daily_unit.getRowDimension(),1);
                              
                              for(int k=0;k<total_daily_unit.getRowDimension();k++)
                              insulin_sensitivity_factor.set(k,0,1800/total_daily_unit.get(k, 0));
                              
                              Matrix    umax= new Matrix (Nu,1);
                              
                              for(int i=0;i<Nu;i++)
                              umax.set(i,0,(g_prediction.get(i, 0)-reference_glucose.get(i,0))/insulin_sensitivity_factor.get(i, 0));
                          
                             umaxx= maxdouble(minumum_basal,mindouble(25,(umax.minus(IOB_pred)).times(60/st)));
                            
                               insulin_max=35;
                               if (flag_constrains==1){
                                   
                                   if( umaxx.get(0, 0)*(1.5)<=insulin_max){
                                       umaxx.set(0,0,umaxx.get(0, 0)*(1.5));
                                   }
                                   else{
                                        umaxx.set(0,0,insulin_max);
                                   }
                                   if(umaxx.get(0, 0)*(1.5)<2){
                                      umaxx.set(0,0,2);
                                   }
                               }
                               else if (flag_constrains==-1){
                                   umaxx.set(0,0,minumum_basal);
                               }
                                 
                            umin= new Matrix (Nu,1); 
                               
                             for(int i=0;i<Nu;i++)
                             umin.set(i,0,minumum_basal);
                             
                      double []  umaxx_global= new double [umaxx.getRowDimension()];
                             
                           for(int i=0;i<umaxx.getRowDimension();i++)  
                           umaxx_global[i]=umaxx.get(i, 0);
                     
                           c[0]=(umaxx.get(0, 0));
                           c[1]=(umaxx.get(1, 0));
                           c[2]=(umaxx.get(2, 0));
                           c[3]=(umaxx.get(3, 0));
                           c[4]=(umaxx.get(4, 0));
                           c[5]=(umaxx.get(5, 0));
                           c[6]=(umaxx.get(6, 0));
                           c[7]=(umaxx.get(7, 0));
                           
                           c[8]=(umin.get(0, 0));
                           c[9]=(umin.get(1, 0));
                           c[10]=(umin.get(2, 0));
                           c[11]=(umin.get(3, 0));
                           c[12]=(umin.get(4, 0));
                           c[13]=(umin.get(5, 0));
                           c[14]=(umin.get(6, 0));
                           c[15]=(umin.get(7, 0));
          
                           return c;
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
