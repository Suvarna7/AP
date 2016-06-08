/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias.MemoryStaticVariables;

import Jama.Matrix;

/**
 *
 * @author Mert
 */
public class m20150711_load_global_variables {
    
    public static Matrix gs;
    public static int kj;
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static Matrix armband_data_with_time;
    public static Matrix ee = new Matrix (1,1);
    public static Matrix phys_act= new Matrix (1,1);
    public static Matrix sleep= new Matrix (1,1);
    public static Matrix gsr= new Matrix (1,1);
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static Matrix IOB_total;
    public static Matrix bolus_insulin ;
    public static Matrix basal_insulin;
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static double [][][] meal_covariance;
    public static Matrix meal_states ;
    public static Matrix meal_bolus_amount;
    public static Matrix meal_detection;
    public static Matrix meal_detection_time;
    public static Matrix correction_bolus_amount;
    public static Matrix correction_detection;
    public static Matrix correction_detection_time;
    public static Matrix correction_limit;
    public static Matrix meal_g_basal;
    public static Matrix meal_gpc_gs_slope_degree ;
    public static Matrix meal_gpc_mu ;
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static Matrix phi;
    public static Matrix phi_ee;
    public static Matrix phi_gsr;
    public static Matrix armax_parameters;
    public static double [][][] armax_covariance;
    public static Matrix armax_lamda;
    public static Matrix armax_err;
    public static Matrix arma_parameters_ee;
    public static Matrix arma_lamda_ee;
    public static double [][][] arma_covariance_ee;
    public static Matrix arma_err_ee;
    public static Matrix arma_parameters_gsr;
    public static Matrix arma_lamda_gsr;
    public static double [][][] arma_covariance_gsr;
    public static Matrix arma_err_gsr;
    public static double [][][] A_state;
    public static double [][][] A_state_ee;
    public static double [][][] A_state_gsr;
    public static double [][][] C_state;
    public static double [][][] C_state_ee;
    public static double [][][] C_state_gsr;
    public static double [][][] B_state;
    public static double [][][] K_state;
    public static double [][][] K_state_ee;
    public static double [][][] K_state_gsr;
    public static double [][][] M;
    public static double [][][] L;
    public static double [][][] L_ee;
    public static double [][][] L_gsr;
    public static double [][][] M_ee;
    public static double [][][] M_gsr;
    public static Matrix X_state;
    public static Matrix X_state_ee;
    public static Matrix X_state_gsr;
    public static Matrix ee_prediction;
    public static Matrix gsr_prediction;
    public static Matrix g_prediction;
    public static Matrix reference_glucose;
    public static Matrix insulin_sensitivity_constant;
    public static Matrix IOB_prediction;
    public static Matrix maximum_insulin;
    public static Matrix total_daily_unit;
    public static Matrix insulin_sensitivity_factor;
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static double body_weight;     ///Before the experiment we should arrange
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    public static Matrix hypo_threshold;
    public static Matrix hypo_slope_degree;
    public static Matrix hypo_alarm;
    public static String [] carb_amount= new String [21];
    public static String [] carb_type=new String [21];
    public static Matrix hypo_phase;
    public static Matrix hypo_phase_old;
    public static Matrix repeated_immediate_alarm;
    public static Matrix mdata;
    public static String [] batch_CL= new String [21];
    public static Matrix bolus_insulin_calculated;
    public static Matrix basal_insulin_calculated;
    public static double CGM_retuning_with_noise_generator;
    public static double CGM_retuning_without_noise_generator;
    /////////////////////////////////////////////////////////////////////
    public static double gs_in;     ///Before the experiment we should arrange
    public static double ee_in;    ///Before the experiment we should arrange
    public static double gsr_in;   ///Before the experiment we should arrange
    public static double sleep_in;   ///Before the experiment we should arrange
    public static double phys_act_in;   ///Before the experiment we should arrange
    ///////////////////////////////////////////////////////////////////
    
    public void  m20150711_load_global_variables() {
        gs = new Matrix (1,1);
        
    /*  gs_in=298;
        ee_in=5.8512;
        gsr_in=0.0139;
        sleep_in=0;
        phys_act_in=0;
        body_weight=74.8;*/
        for(int i=0;i<21;i++){
        carb_amount[i]="0";
        carb_type[i]="None";
        batch_CL[i]="Other";
        }
        
        kj=20;
        
     //   gs=ones(1,21).times(gs_in);
        ee=ones(1,21).times(ee_in);
        phys_act=ones(1,21).times(phys_act_in);
        sleep=ones(1,21).times(sleep_in);
        gsr=ones(1,21).times(gsr_in);
        
        meal_states= new Matrix (8,21);
        IOB_total = new Matrix(21,1);
        bolus_insulin = new Matrix(21,1);
        basal_insulin = new Matrix(8,21);
        IOB_prediction = new Matrix(8,21);
        
        
        for(int i =0;i<21; i++){
        meal_states.set(0,i,0.1);
        meal_states.set(1,i,gs_in);
        meal_states.set(2,i,0);
        meal_states.set(3,i,0);
        meal_states.set(4,i,0.068);
        meal_states.set(5,i,0.037);
        meal_states.set(6,i,1.3);
        meal_states.set(7,i,20);
        }
        
        meal_covariance = new double [8][8][21];
   
        
             for(int j=0;j<8;j++)
                  for(int k=0;k<8;k++)
                      if(j==k)
                   meal_covariance[j][k][20]=1;
          
        meal_bolus_amount= new Matrix(21,1);
        meal_detection= new Matrix(21,1);
        meal_detection_time= new Matrix(21,1);
        correction_bolus_amount= new Matrix(21,1);
        correction_detection= new Matrix(21,1);
        correction_detection_time= new Matrix(21,1);
        correction_limit = new Matrix(21,1);
          
        phi= new Matrix(24,21);
        phi_ee= new Matrix(4,21);
        phi_gsr= new Matrix(4,21);
        armax_parameters= new Matrix(24,21);
        arma_parameters_ee= new Matrix(4,21);
          
        armax_covariance= new double [24][24][21];
          
          
             for(int j=0;j<24;j++)
                  for(int k=0;k<24;k++)
                      if(j==k)
                   armax_covariance[j][k][20]=1;
           
        arma_covariance_ee= new double [4][4][21];
        
     
             for(int j=0;j<4;j++)
                  for(int k=0;k<4;k++)
                      if(j==k)
                   arma_covariance_ee[j][k][20]=1;
           
        armax_err = new Matrix(21,1);
        arma_err_ee = new Matrix(21,1);
        arma_parameters_gsr= new Matrix(4,21);
        arma_err_gsr= new Matrix(21,1);
           
        A_state= new double [21][21][21];
        A_state_ee= new double [4][4][21];
        A_state_gsr= new double [4][4][21];
           
        C_state= new double [1][21][21];
        C_state_ee= new double [1][4][21];
        C_state_gsr= new double [1][4][21];
           
        B_state= new double [21][3][21];
        K_state_ee= new double [4][1][21];
        K_state_gsr= new double [4][1][21];
        K_state= new double [21][1][21];
           
        M= new double [8][20][21];
        L= new double [8][8][21];
        L_ee= new double [8][8][21];
        L_gsr= new double [8][8][21];
        M_ee = new double [8][4][21];
        M_gsr = new double [8][4][21];
           
        X_state= new Matrix(21,21);
        X_state_ee= new Matrix(4,21);
        X_state_gsr= new Matrix(4,21);
        ee_prediction= new Matrix(8,21);
        gsr_prediction= new Matrix(8,21);
        g_prediction= new Matrix(8,21);
        reference_glucose= new Matrix(8,21);
           
        hypo_threshold= new Matrix(21,1);
        hypo_alarm= new Matrix(21,1);
        hypo_phase= new Matrix(21,1);
        hypo_phase_old= new Matrix(21,1);
        repeated_immediate_alarm = new Matrix(21,1);
           
        bolus_insulin_calculated =  new Matrix(21,1);
        basal_insulin_calculated =  new Matrix(8,21);
           
        meal_g_basal=ones(21,1).times(gs_in);
        meal_gpc_gs_slope_degree=ones(21,1).times(45);
        meal_gpc_mu=ones(21,1).times(0.5);
        
        armax_lamda=ones(21,1).times(0.5);
        arma_lamda_ee=ones(21,1).times(0.5);
        arma_lamda_gsr=ones(21,1).times(0.5);
       
       
         arma_covariance_gsr= new double [4][4][21];
       
         
             for(int j=0;j<4;j++)
                  for(int k=0;k<4;k++)
                      if(j==k)
                   arma_covariance_gsr[j][k][20]=1;
        
        maximum_insulin=ones(8,21).times(25);
        total_daily_unit=ones(8,21);
        insulin_sensitivity_factor=ones(8,21);
        insulin_sensitivity_constant=ones(8,21).times(0.1);
        
        hypo_slope_degree=ones(21,1).times(45);
    }
    
    public Matrix ones (int x,int y){
        
        Matrix newmatrice = new Matrix (x,y);
        
        for(int i=0;i<x;i++){
            for(int j=0;j<y;j++){
                newmatrice.set(i, j,1);
            }
        }
        
        return newmatrice;
    }
    
}
