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
public class detection_of_meal {
   public  double meal_bolus=0; 
   public double correction_detection=0; 
   public double [] x_p;
   public double y;
   public double meal_detection;
   public double meal_detection_time;
   public double correction_time;
   public double correction_limit;
   public double correction_bolus;
   public int kj;
   public double body_weight;
   public double sleep;
   public double phys_act;
   public double meal_mu;
   
   public detection_of_meal(double [] x_p,double y,double meal_detection,double meal_detection_time,double correction_time,double correction_limit,double correction_bolus,int kj,double body_weight,double sleep,double phys_act,double meal_mu){
       this.body_weight=body_weight;
       this.correction_bolus=correction_bolus;
       this.correction_detection=correction_detection;
       this.correction_limit=correction_limit;
       this.correction_time=correction_time;
       this.kj=kj;
       this.meal_bolus=meal_bolus;
       this.meal_detection=meal_detection;
       this.meal_detection_time=meal_detection_time;
       this.meal_mu=meal_mu;
       this.phys_act=phys_act;
       this.sleep=sleep;
       this.x_p=x_p;
       this.y=y;
   }
   
   public void detection_of_meal(){
       
    /*   /////////////////////////////////////////////INPUTS////////////////////////////////////////////////////////////////////////////////////////   INPUTS OF DETECTION_MEAL
       System.out.println("////////////////////////INPUTS detection of meal//////////////////////////////////////////////////////////////////////////////////////");
       System.out.println("x_p");
       for(int i=0;i<x_p.length;i++)
           System.out.print(x_p[i]+"   ");
       System.out.println();
       System.out.println(meal_detection+"meal_detection");
       System.out.println(meal_detection_time+"meal_detection_time");
       System.out.println(y+"y");
       System.out.println(correction_time+"correction_time");
       System.out.println(correction_limit+"correction_limit");
       System.out.println(correction_bolus+"correction_bolus");
       System.out.println(sleep+"sleep");
       System.out.println(phys_act+"phys_act");
       System.out.println(meal_mu+"meal_mu");
       System.out.println(kj+"kj");
       //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
       
   CGM__SEDFR_JF cs = new CGM__SEDFR_JF();
     
   if(x_p[2]>0.85 && meal_detection==0 && y>100 && sleep==0 && phys_act==0){
    meal_detection=1;
    meal_detection_time=kj;
    correction_time=kj;
    meal_bolus=3;
    correction_limit=x_p[2]+1;    
      }
      else if (x_p[2]>0.85 && meal_detection==0 && y>140 && sleep==1){
    meal_detection=1;
    meal_detection_time=kj;
    correction_time=kj;
    meal_bolus=1;
    correction_limit=x_p[2]+1;
       }
      else if (x_p[2]>0.85 && meal_detection==0 && y>100 && phys_act==1){
    meal_detection=1;
    meal_detection_time=kj;
    correction_time=kj;
    meal_bolus=0;
    correction_limit=x_p[2]+1;
      }
      else{
   meal_bolus=0;
      }
   
 //////////////////////////////CODE LINE 24 END/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////            
///////////////////////             %% End of meal   ////////////////////////////////////////////////////
//////////////////////////////CODE LINE 25-31/////////////////////////////////////////////////////////////////////////////////////////////////////////////
if (kj-meal_detection_time>6 && x_p[2]<=0.85)
    meal_detection=0;



reference_trajectory rtj = new reference_trajectory(y,100,8,meal_mu);
Matrix rr1=rtj.referencetrajectory();

double [][] rr= new double [rr1.getRowDimension()][rr1.getColumnDimension()];

for(int i=0;i<rr1.getRowDimension();i++)
    for(int j=0;j<rr1.getColumnDimension();j++)
    rr[i][j]=rr1.get(i, j);

//////////////////////%% Correction bolus////////////////////////////////////////////////////////////////////////////////////////////////////////////
if (kj-meal_detection_time>=3 && x_p[2]>=correction_limit && kj-correction_time>=3 && meal_detection==1 && y>100 && sleep==0 && phys_act==0){
    correction_detection=1;
    correction_limit=correction_limit+1;
    double ISC=y/rr[0][0];
    double TDD=ISC*body_weight;
    double ISF=1800/TDD;
    correction_bolus=(y-rr[0][0])/ISF;
    correction_time=kj;
}
    else if (kj-meal_detection_time>=3 && x_p[2]>=correction_limit && kj-correction_time>=3 && meal_detection==1 && y>140 && sleep==1){
    correction_detection=1;
    correction_limit=correction_limit+1;
    double ISC=y/rr[0][0];
    double TDD=ISC*body_weight;
    double ISF=1800/TDD;
    correction_bolus=(y-rr[0][0])/ISF;
    correction_time=kj;
    }
    else if (kj-meal_detection_time>=3 && x_p[2]>=correction_limit && kj-correction_time>=3 && meal_detection==1 && y>300 && phys_act==1){
    correction_detection=1;
    correction_limit=correction_limit+1;
    double ISC=y/rr[0][0];
    double TDD=ISC*body_weight;
    double ISF=1800/TDD;
    correction_bolus=(y-rr[0][0])/ISF;
    correction_time=kj;
    }
    else{
    correction_detection=0;
    }

/*/////////////////////////////////////////////////////////OUTPUT///////////////////////////////////////////////////////////////////////////////////////////
System.out.println("//////////////////////////////////////////OUTPUT MEAL_DETECTION////////////////////////////////////////////////////////////////////");
System.out.println(meal_detection+"      meal_detection");
System.out.println(meal_detection_time+"      meal_detection_time");
System.out.println(correction_time+"      correction_time");
System.out.println(meal_bolus+"      meal_bolus");
System.out.println(correction_limit+"      correction_limit");
System.out.println(correction_bolus+"      correction_bolus");
System.out.println(correction_detection+"      correction_detection");
System.out.println("//////////////////////////////////////////OUTPUT MEAL_DETECTION////////////////////////////////////////////////////////////////////");
/////////////////////////////////////////////////////////OUTPUT///////////////////////////////////////////////////////////////////////////////////////////*/
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
