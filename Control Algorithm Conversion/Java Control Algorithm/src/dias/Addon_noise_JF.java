/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import Jama.Matrix;
import java.util.Random;
/////////////////////////////////////////////////This class call from CGM_SEDFR_JF.m//////////////////////////////////////////////////////////////////////////
/**
 *
 * @author User
 */
public class Addon_noise_JF {
 /////////////////////////////////Variables///////////////////////////////////////////////////////////////////////////////////////////////////////////   
    private  double type;
    private  double length_add;
    private  Matrix gb;
    private int kj;
    private double am;
    private Matrix addsignal=new Matrix(1,(int) length_add+2);
    private double rand;
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
   
    public Addon_noise_JF(double type,double am,int length_add,Matrix gb,int k){
        this.type = type;
        this.am=am;
        this.length_add= length_add;
        this.gb= gb;
        this.kj=kj;
    }
 
    
     public Matrix addon_noise_JF(){  
      /*   ////////////////////////////////////////Addon_noise_input/////////////////////////////////////////////////////////////////////////////////
         System.out.println("/////////////////////////////Addon_noise_input////////////////////////////////////////////////////////////////////////");
         System.out.println(type+"     type");
         System.out.println(am+"     am");
         System.out.println(length_add+"     length_add");
         System.out.println(gb+"     gb");
         System.out.println(kj+"     kj");
         System.out.println("/////////////////////////////Addon_noise_input////////////////////////////////////////////////////////////////////////");
         ////////////////////////////////////////Addon_noise_input/////////////////////////////////////////////////////////////////////////////////*/
         
          int signal=0;
           Random randomGenerator = new Random();
             double randomInt = randomGenerator.nextInt(1000);
   
             double randomDouble;
             randomDouble=(randomInt/(1000));
              if(randomDouble<0.5){   ///////////////Rand(1,1)>0.5
                  signal=1;
              }
              else{    ///////////////Rand(1,1)<0.5
                  signal=-1;
              }
              if(type==1){    //'whitenoise'
                  for(int i=0;i<length_add;i++){
                  Random randomGenerator2 = new Random();   
                  double randomInt2 = randomGenerator2.nextInt(1000);    
                  rand=(randomInt2/(1000));
                  addsignal.set(0,i,2*am*(rand-0.5));
                  }
              }
              else if(type==2){   
                   for(int i=0;i<length_add;i++){
                    addsignal.set(0,i,signal*am/length_add*(i+1)); 
                   }
              }
              else if(type==3){    //%step change
                  for(int i=0;i<length_add;i++){
                    addsignal.set(0,i,signal*am); 
                   }
              }
              else if(type==4){   //%outlier      
                   for(int i=0;i<length_add;i++){
                  addsignal.set(0,i,(signal*am)/(((length_add+1)/2)*((length_add+1)/2))*(-(i+1)*((i+1)-length_add-1)));
                   }
              }
              else if(type==5){ // % data missing
                  for(int i=0;i<length_add;i++)
                      addsignal.set(0,i,0.0/0.0);
              }
              else if(type==6){//% signal stuck
                    for(int i=0;i<length_add;i++)
                   addsignal.set(0,i,0);
              }       
       /*       ///////////////////////////////////////////////////Output////////////////////////////////////////////////////////////////////////////////////
              System.out.println("////////////////////////////////Output Addon_noise////////////////////////////////////////////////////////////////////");
              System.out.println(addsignal+"    addsignal");
              System.out.println("////////////////////////////////Output Addon_noise////////////////////////////////////////////////////////////////////");
              ///////////////////////////////////////////////////Output///////////////////////////////////////////////////////////////////////////////////*/
             
              return addsignal;   
         } 
}
