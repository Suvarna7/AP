/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import Jama.Matrix;
import dias.MemoryStaticVariables.m20150711_load_global_variables;


/**
 *
 * @author User
 */
public class m20150711_run_meal_detection_bolus_algorithm {
    
    public Matrix meal_states;
    public double [][][] meal_covariance;
    public Matrix bolus_insulin;
    public Matrix meal_bolus_amount;
    public Matrix meal_detection;
    public Matrix meal_detection_time;
    public Matrix correction_bolus_amount;
    public Matrix correction_detection;
    public Matrix correction_detection_time;
    public Matrix correction_limit;
    public Matrix gs;
    public int kj;
    public Matrix meal_g_basal;
    public Matrix meal_gpc_gs_slope_degree;
    public Matrix meal_gpc_mu;
    public Matrix sleep;
    public Matrix phys_act;
    public Matrix IOB_total;
    public double body_weight;
    public double R_meal;
    public Matrix Q_p_meal;
    public Matrix meal_covariance1;
    
    public m20150711_run_meal_detection_bolus_algorithm(Matrix meal_states,double [][][] meal_covariance, Matrix bolus_insulin, Matrix meal_bolus_amount, Matrix meal_detection, Matrix meal_detection_time, Matrix correction_bolus_amount, Matrix correction_detection, Matrix correction_detection_time, Matrix correction_limit, Matrix gs, int kj, Matrix meal_g_basal, Matrix meal_gpc_gs_slope_degree, Matrix meal_gpc_mu, Matrix sleep, Matrix phys_act, Matrix IOB_total, double body_weight){
   
    this.IOB_total=IOB_total;
    this.body_weight=body_weight;
    this.bolus_insulin=bolus_insulin;
    this.correction_bolus_amount=correction_bolus_amount;
    this.correction_detection=correction_detection;
    this.correction_detection_time=correction_detection_time;
    this.correction_limit=correction_limit;
    this.gs=gs;
    this.kj=kj;
    this.meal_bolus_amount=meal_bolus_amount;
    this.meal_covariance=meal_covariance;
    this.meal_detection=meal_detection;
    this.meal_detection_time=meal_detection_time;
    this.meal_g_basal=meal_g_basal;
    this.meal_gpc_gs_slope_degree=meal_gpc_gs_slope_degree;
    this.meal_gpc_mu= meal_gpc_mu;
    this.meal_states= meal_states;
    this.phys_act=phys_act;
    this.sleep=sleep;
    }
    
    
    public void run_meal_detection_bolus_algorithm(){
/*////////////////////////////////////////////////INPUTS OF RUN_MEAL_DETECTION_BOLUS_ALGORITHM/////////////////////////////////////////////////////
System.out.println("////////////////////////////////////Inputs of Run_meal_detection_bolus_algorith///////////////////////////////////////////////");
System.out.println(body_weight+"     body_weight");
printMatrix(bolus_insulin,"bolus_insulin");
printMatrix(correction_bolus_amount,"correction_bolus_amount");
printMatrix(correction_detection,"correction_detection");
printMatrix(correction_detection_time,"correction_detection_time");
printMatrix(correction_limit,"correction_limit");
printMatrix(gs,"gs");
printMatrix(meal_bolus_amount,"meal_bolus_amount");
printMatrix(meal_detection,"meal_detection");
printMatrix(meal_detection_time,"meal_detection_time");
printMatrix(meal_g_basal,"meal_g_basal");
printMatrix(meal_gpc_gs_slope_degree,"meal_gpc_gs_slope_degree");
printMatrix(meal_gpc_mu,"meal_gpc_mu");
printMatrix(meal_states,"meal_states");
printMatrix(phys_act,"phys_act");
printMatrix(sleep,"sleep");
printMatrix(IOB_total,"IOB_total");
System.out.println(kj+"kj");
print3Dmatrice(meal_covariance,"meal_covariance");
System.out.println("////////////////////////////////////Inputs of Run_meal_detection_bolus_algorith///////////////////////////////////////////////");
////////////////////////////////////////////////INPUTS OF RUN_MEAL_DETECTION_BOLUS_ALGORITHM/////////////////////////////////////////////////////*/
        
         m20150711_load_global_variables lgvariables = new m20150711_load_global_variables();
         kj=lgvariables.kj;   
         
         lgvariables.meal_gpc_mu= DIAS.createnewMatrix(meal_gpc_mu.getRowDimension()+1, 1 ,lgvariables.meal_gpc_mu);
      
         if(kj>(12)){
              lgvariables.meal_g_basal= DIAS.createnewMatrix(meal_g_basal.getRowDimension()+1,1,lgvariables.meal_g_basal );
              
         double mean=(gs.get(0,kj-12)+gs.get(0,kj-11)+gs.get(0,kj-10)+gs.get(0,kj-9)+gs.get(0,kj-8)+gs.get(0,kj-7))/6;
         lgvariables.meal_g_basal.set(meal_g_basal.getRowDimension(),0,mean);
         }
         else{
         lgvariables.meal_g_basal= DIAS.createnewMatrix(meal_g_basal.getRowDimension()+1,1,lgvariables.meal_g_basal );
         lgvariables.meal_g_basal.set(meal_g_basal.getRowDimension(),0,100);    
         }
        
        if(kj>(5)){
        double [] polyfitinput = new double [5];
        
        polyfitinput[0]=0;
        polyfitinput[1]=5;
        polyfitinput[2]=10;
        polyfitinput[3]=15;
        polyfitinput[4]=20;
        double [] polyfittemp = new double [3];
        double [] polyfitinput2 = new double [5];
        polyfitinput2[0]=gs.get(0,kj-5);
        polyfitinput2[1]=gs.get(0,kj-4);
        polyfitinput2[2]=gs.get(0,kj-3);
        polyfitinput2[3]=gs.get(0,kj-2);
        polyfitinput2[4]=gs.get(0,kj-1);
        
        Matrix meal_gpc_line= new Matrix (3,1);
        polyfittemp=PolyFit.fit(5,polyfitinput,polyfitinput2,1);
        meal_gpc_line.set(0, 0, polyfittemp[1]);
        
        
        lgvariables.meal_gpc_gs_slope_degree= DIAS.createnewMatrix(meal_gpc_gs_slope_degree.getRowDimension()+1,1,lgvariables.meal_gpc_gs_slope_degree);
        lgvariables.meal_gpc_gs_slope_degree.set(meal_gpc_gs_slope_degree.getRowDimension(),0,57.2958*(Math.atan(meal_gpc_line.get(0,0))));
        }
        else{
        lgvariables.meal_gpc_gs_slope_degree= DIAS.createnewMatrix(meal_gpc_gs_slope_degree.getRowDimension()+1,1,lgvariables.meal_gpc_gs_slope_degree);
        lgvariables.meal_gpc_gs_slope_degree.set(meal_gpc_gs_slope_degree.getRowDimension(),0,45);  
        }
       
         if(sleep.get(0,kj)==1){
             lgvariables.meal_gpc_mu.set(meal_gpc_mu.getRowDimension(),0,1-max(0.5,lgvariables.meal_gpc_gs_slope_degree.get(meal_gpc_gs_slope_degree.getRowDimension(),0)/90));
        }
         else if(phys_act.get(0, kj)==1){
            lgvariables.meal_gpc_mu.set(meal_gpc_mu.getRowDimension(),0,1-0.025*max(0.5,lgvariables.meal_gpc_gs_slope_degree.get(meal_gpc_gs_slope_degree.getRowDimension(),0)/90)); 
         }
         else{
            lgvariables.meal_gpc_mu.set(meal_gpc_mu.getRowDimension(),0,1-max(0.5,lgvariables.meal_gpc_gs_slope_degree.get(meal_gpc_gs_slope_degree.getRowDimension(),0)/90));   
             }
   
         double [][] Q_p_meal1=new double [8][8];
         for(int i=0;i<8;i++)
           for(int j=0;j<8;j++)
               if(i==j && i==0)
                   Q_p_meal1[i][j]=0.000001;
               else if(i==j && i==1)
                   Q_p_meal1[i][j]=0.000001;
               else if(i==j && i==2)
                   Q_p_meal1[i][j]=0.001;
               else if(i==j && i==3)
                   Q_p_meal1[i][j]=0.001;
               else if(i==j && i==4)
                   Q_p_meal1[i][j]=0.01;
               else if(i==j && i==5)
                   Q_p_meal1[i][j]=0.1;
               else if(i==j && i==6)
                   Q_p_meal1[i][j]=0.01;
               else if(i==j && i==7)
                   Q_p_meal1[i][j]=0.1;
         
         
         Q_p_meal = new Matrix (Q_p_meal1);
         R_meal=100;
         
           
       Matrix meal_statesx = new Matrix (meal_states.getRowDimension(),1);
       
       for(int i=0;i<lgvariables.meal_states.getRowDimension();i++)
       meal_statesx.set(i, 0,lgvariables.meal_states.get(i, kj-1));
       
       Matrix meal_covariance1 = new Matrix(8,8);
       
       m20150711_gpc gpc =new m20150711_gpc();
       
         for(int i=0;i<DIAS.lastValueReturnXYZ(lgvariables.meal_covariance)[1]+1;i++)
              for(int j=0;j<DIAS.lastValueReturnXYZ(lgvariables.meal_covariance)[2]+1;j++)
                  meal_covariance1.set(i, j,lgvariables.meal_covariance[i][j][kj-1]);
         
       m20141215_ukf_meal ukf = new m20141215_ukf_meal(gs.get(0,kj-1),meal_statesx,meal_covariance1,R_meal,Q_p_meal,meal_g_basal.get(kj-1,0)); 
       ukf.m20141215_ukf_meal();

       
       lgvariables.meal_states= DIAS.createnewMatrix(ukf.x_p.getRowDimension(), kj+1 ,lgvariables.meal_states);
       
       for(int j=0; j<ukf.x_p.getRowDimension();j++)
       lgvariables.meal_states.set(j, kj, ukf.x_p.get(j,0));
             
       lgvariables.meal_covariance= createnew3Dmatrix (lgvariables.meal_covariance,ukf.P_p.getRowDimension(),ukf.P_p.getColumnDimension(),kj+1);
       
       for(int i=0; i<ukf.P_p.getRowDimension();i++)
            for(int j=0; j<ukf.P_p.getColumnDimension();j++)
                lgvariables.meal_covariance[i][j][kj]=ukf.P_p.get(i,j);
       
       
       double [] meal_states1 = new double [8];
       
       for(int i=0;i<meal_states.getRowDimension();i++)
       meal_states1[i]=  lgvariables.meal_states.get(i, kj);

       
      detection_of_meal dtcmeal =new detection_of_meal(meal_states1, gs.get(0,kj-1), meal_detection.get(kj-1,0), meal_detection_time.get(kj-1,0), correction_detection_time.get(kj-1,0),correction_limit.get(kj-1,0),correction_bolus_amount.get(kj-1,0),(int) kj, body_weight, sleep.get(0,kj-1),phys_act.get(0,kj-1), meal_gpc_mu.get(kj-1,0));
      dtcmeal.detection_of_meal();
       
      lgvariables.meal_bolus_amount= DIAS.createnewMatrix(kj+1,1,lgvariables.meal_bolus_amount);
      lgvariables.correction_detection= DIAS.createnewMatrix(kj+1,1,lgvariables.correction_detection);
      lgvariables.meal_detection= DIAS.createnewMatrix(kj+1,1, lgvariables.meal_detection);
      lgvariables.meal_detection_time= DIAS.createnewMatrix(kj+1,1, lgvariables.meal_detection_time);
      lgvariables.correction_detection_time= DIAS.createnewMatrix(kj+1,1,  lgvariables.correction_detection_time);
      lgvariables.correction_limit= DIAS.createnewMatrix(kj+1,1,  lgvariables.correction_limit);
      lgvariables.correction_bolus_amount= DIAS.createnewMatrix(kj+1,1,lgvariables.correction_bolus_amount);
       
      
      lgvariables.meal_bolus_amount.set(kj, 0,dtcmeal.meal_bolus);
      lgvariables.correction_detection.set(kj, 0,dtcmeal.correction_detection);
      lgvariables.meal_detection.set(kj, 0,dtcmeal.meal_detection);
      lgvariables.meal_detection_time.set(kj, 0,dtcmeal.meal_detection_time);
      lgvariables.correction_detection_time.set(kj, 0,dtcmeal.correction_time);
      lgvariables.correction_limit.set(kj, 0,dtcmeal.correction_limit);
      lgvariables.correction_bolus_amount.set(kj, 0,dtcmeal.correction_bolus);

    m20150711_load_global_variables.bolus_insulin=DIAS.createnewMatrix(bolus_insulin.getRowDimension()+1, 1,lgvariables.bolus_insulin);
    m20150711_load_global_variables.bolus_insulin.set(bolus_insulin.getRowDimension(), 0,((int)(max(0,lgvariables.meal_bolus_amount.get(kj,0)*lgvariables.meal_detection.get(kj,0)+lgvariables.correction_detection.get(kj,0)*lgvariables.correction_bolus_amount.get(kj,0)-(lgvariables.IOB_total.get(kj-1,0)))/0.05)*0.05));
   
    DIAS.printMatrix ( m20150711_load_global_variables.bolus_insulin, "meal detection module bolus" );

 /*   ////////////////////////////////////////////////OUTPUTS OF RUN_MEAL_DETECTION_BOLUS_ALGORITHM/////////////////////////////////////////////////////
System.out.println("////////////////////////////////////Outputs of Run_meal_detection_bolus_algorith///////////////////////////////////////////////");
printMatrix(lgvariables.bolus_insulin,"lgvariables.bolus_insulin");
printMatrix(lgvariables.correction_bolus_amount,"lgvariables.correction_bolus_amount");
printMatrix(lgvariables.correction_detection,"lgvariables.correction_detection");
printMatrix(lgvariables.correction_detection_time,"lgvariables.correction_detection_time");
printMatrix(lgvariables.correction_limit,"lgvariables.correction_limit");
printMatrix(lgvariables.meal_bolus_amount,"lgvariables.meal_bolus_amount");
printMatrix(lgvariables.meal_detection,"lgvariables.meal_detection");
printMatrix(lgvariables.meal_detection_time,"lgvariables.meal_detection_time");
printMatrix(lgvariables.meal_g_basal,"lgvariables.meal_g_basal");
printMatrix(lgvariables.meal_gpc_gs_slope_degree,"lgvariables.meal_gpc_gs_slope_degree");
printMatrix(lgvariables.meal_gpc_mu,"lgvariables.meal_gpc_mu");
printMatrix(lgvariables.meal_states,"lgvariables.meal_states");
print3Dmatrice(lgvariables.meal_covariance,"lgvariables.meal_covariance");
System.out.println("////////////////////////////////////Outputs of Run_meal_detection_bolus_algorith///////////////////////////////////////////////");
////////////////////////////////////////////////OUTPUTS OF RUN_MEAL_DETECTION_BOLUS_ALGORITHM/////////////////////////////////////////////////////*/
    
    
    
    }
    
    
         public void print3Dmatrice(double x[][][],String matricename){
           
           int [] valuex;
           valuex=DIAS.lastValueReturnXYZ(x);
           
           System.out.println(matricename);
           
           for(int k=0; k<valuex[3]+1; k++){
           for(int i=0; i<valuex[1]+1; i++){
               for(int j=0; j<valuex[2]+1 ; j++){
                   System.out.print("\t\t\t"+x[i][j][k]);
               }  
               System.out.println();
           }
           System.out.println("Matrice State:  "+(k+1));
           }
           System.out.println("Matrice has written");   
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
    
    public double max (double a, double b){
        double max=0;
        if(a>b)
        max=a;
        else
        max=b;
        
        return max;
    }
    
    
     
     public double[][][] createnew3Dmatrix (double s[][][],int newx,int newy, int newz){
         
         double [][][] newdoublematrice = new double [newx][newy][newz];
         
         for(int i=0;i<s.length;i++)
             for(int j=0;j<s[0].length;j++)
                 for(int z=0;z<s[0][0].length;z++)
                      newdoublematrice[i][j][z]=s[i][j][z];
         
         return newdoublematrice;
     }
    
    
}
