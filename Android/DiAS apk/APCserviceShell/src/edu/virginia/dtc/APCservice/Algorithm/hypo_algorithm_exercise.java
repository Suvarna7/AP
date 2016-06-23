/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.dtc.APCservice.Algorithm;

import static java.lang.Math.abs;

/**
 *
 * @author User
 */
public class hypo_algorithm_exercise {
    
    public double g;
    public double g_prediction;
    public double hypo_threshold;
    public double phase_old;
    public double hypo_slope_degree;
    public double phase;
    public double hypo_alarm;
    public double repeated_immediate_alarm;
    public String carb_type;
    public String carb_amount;
    
    public hypo_algorithm_exercise(double g,double g_prediction,double hypo_threshold,double phase_old,double hypo_slope_degree){
        this.g=g;
        this.g_prediction=g_prediction;
        this.hypo_threshold= hypo_threshold;
        this.phase_old = phase_old;
        this.hypo_slope_degree=hypo_slope_degree;   
}
    
    public void m20150510_hypo_algorithm_exercise(){
        if (g<70 && hypo_slope_degree<0){
    phase=5;
    repeated_immediate_alarm=0;
    if (Math.abs(phase-phase_old)>=1){
        hypo_alarm=1;
        phase_old=phase;
    }
    else{
        hypo_alarm=1;
        repeated_immediate_alarm=1;
    }
    if (hypo_slope_degree<-60){
        carb_amount="30";
        carb_type="Juice";
        }
        else if (-60<=hypo_slope_degree && hypo_slope_degree<-30){
        carb_amount="25";
        carb_type="Juice";
        }
        else if (-30<=hypo_slope_degree && hypo_slope_degree<0){
        carb_amount="18";
        carb_type="Dex";
                }
            }  
             else if (70<=g && g<90 && g_prediction<hypo_threshold && hypo_slope_degree<0){
    phase=4;
    repeated_immediate_alarm=0;
    if (Math.abs(phase-phase_old)>=1){
        hypo_alarm=1;
        phase_old=phase;
    }
        else{
        hypo_alarm=0;
             }
    if (hypo_slope_degree<-60){
        carb_amount="30";
        carb_type="Juice";
    }
        else if (-60<=hypo_slope_degree && hypo_slope_degree<-30){
        carb_amount="25";
        carb_type="Juice";
             }
        else if (-30<=hypo_slope_degree && hypo_slope_degree<0){
        carb_amount="18";
        carb_type="Dex";
        
    }
        }     
             else if (90<=g && g<110 && g_prediction<hypo_threshold && hypo_slope_degree<0){
    phase=3;
    repeated_immediate_alarm=0;
    if (Math.abs(phase-phase_old)>=1){
        hypo_alarm=1;
        phase_old=phase;
             }else{
        hypo_alarm=0;
             }
    if (hypo_slope_degree<-60){
        carb_amount="30";
        carb_type="Juice";
    }
        else if (-60<=hypo_slope_degree && hypo_slope_degree<-30){
        carb_amount="25";
        carb_type="Juice";
             }
        else if (-30<=hypo_slope_degree && hypo_slope_degree<0){
        carb_amount="18";
        carb_type="Dex";
                }
             }
       else if (110<=g && g<130 && g_prediction<hypo_threshold && hypo_slope_degree<0){
    phase=2;
    repeated_immediate_alarm=0;
    if (Math.abs(phase-phase_old)>=1){
        hypo_alarm=1;
        phase_old=phase;
    }
        else{
        hypo_alarm=0;
       }
    if  (hypo_slope_degree<-60){
        carb_amount="30";
        carb_type="Juice";
    }
        else if (-60<=hypo_slope_degree && hypo_slope_degree<-30){
        carb_amount="25";
        carb_type="Juice";
        }
        else if (-30<=hypo_slope_degree && hypo_slope_degree<0){
        carb_amount="18";
        carb_type="Dex";
                }
        }
             
       else if (130<=g && g<180 && g_prediction<hypo_threshold && hypo_slope_degree<0){
    phase=1;
    repeated_immediate_alarm=0;
    if (Math.abs(phase-phase_old)>=1){
        hypo_alarm=1;
        phase_old=phase;
    }
    else{
        hypo_alarm=0;
    }
    if (hypo_slope_degree<-60){
        carb_amount="30";
        carb_type="Juice";
        }
    else if (-60<=hypo_slope_degree && hypo_slope_degree<-30){
        carb_amount="25";
        carb_type="Juice";
    }
        else if (-30<=hypo_slope_degree && hypo_slope_degree<0){
        carb_amount="18";
        carb_type="Dex";
        } 
        }       
       else{
    phase=0;
    repeated_immediate_alarm=0;
    hypo_alarm=0;
    carb_amount="0";
    carb_type="None";
       }
    
       }
    
}
