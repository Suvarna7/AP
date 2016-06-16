/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.dtc.APCservice.Algorithm;

import Jama.Matrix;

/**
 *
 * @author User
 */
public class cluster1 {
   
    public cluster1(Matrix y){
           this.y=y;
    }
    
    
    public void cluster1_JF(){    //TESTED, WORKING
         
           X1.set(0, 0, 1);
           X1.set(0,1, y.get(0, 0));
           Y1.set(0, 0, 2);
           Y1.set(0,1, y.get(0, 1));
           
           
           X2.set(0, 0, y.getColumnDimension()-1);
           X2.set(0, 1, y.get(0,y.getColumnDimension()-2));
           
           Y2.set(0, 0, y.getColumnDimension());
           Y2.set(0, 1, y.get(0,y.getColumnDimension()-1));
          
           
           node(X1,Y1,X2,Y2);

                
           T=5;  
         
         if(X<y.getColumnDimension() && 1<=X && X<=y.getColumnDimension()){
             if(Y>=max(y)){
                 T=1;
             }else if(Y<min(y)){
                 T=2;
             }
         }
         
         double result1=0;
         double result2=0;
         double result3=0;
         double result4=0;
         
      for(int i=1;i<y.getColumnDimension();i++){
          if(y.get(0, i)-y.get(0,i-1)>0.05)
              result1++;
          if(y.get(0, i)-y.get(0,i-1)>0)
              result2++;
          if(y.get(0, i)-y.get(0,i-1)<0.05)
              result3++;
          if(y.get(0, i)-y.get(0,i-1)<0)
              result4++;
      }
         
        if (result1>=(y.getColumnDimension()+1)/2 && T!=1 && T!=2 || result2==y.getColumnDimension()){
        T=3;
       }else if (result3>=(y.getColumnDimension()+1)/2 && T!=1 && T!=2 || result4==y.getColumnDimension()){
        T=4;
       }else if (T!=1 && T!=2){
        T=5;
               }

       }
    
    public void node(Matrix X1,Matrix Y1, Matrix X2, Matrix Y2){
           X=0;
           double k2=0;
           double b2=0;
           Y=0;
           double k1=0;
           double b1=0;
            
           if (X1.get(0, 0)==Y1.get(0, 0)){
                X=X1.get(0, 0);
                k2=(Y2.get(1, 0)-X2.get(1,0))/(Y2.get(0, 0)-X2.get(0,0));
                b2=X2.get(1,0)-k2*X2.get(0,0); 
                Y=k2*X+b2; 
           }
            if (X2.get(0, 0)==Y2.get(0, 0)){
                X=X1.get(0, 0);
                k1=(Y1.get(1, 0)-X1.get(1,0))/(Y1.get(0, 0)-X1.get(0,0));
                b2=X1.get(1,0)-k1*X1.get(0,0); 
                Y=k1*X+b1; 
           }
         if (X1.get(0, 0)!=Y1.get(0, 0) && X2.get(0, 0)!=Y2.get(0, 0)){
                k1=(Y1.get(0, 1)-X1.get(0,1))/(Y1.get(0, 0)-X1.get(0,0));
                k2=(Y2.get(0, 1)-X2.get(0,1))/(Y2.get(0, 0)-X2.get(0,0));
                b1=(X1.get(0,1))-k1*(X1.get(0,0)); 
                b2=X2.get(0,1)-k2*(X2.get(0,0)+1); 
         
            if (k1==k2){
       X=100000;
       Y=100000;
            }
            else{
    X=(b2-b1)/(k1-k2);
    Y=k1*X+b1;
            }
         }
    }
    
    public double max(Matrix y){
        double maxvalue=0;
        
        for(int i=0;i<y.getColumnDimension();i++)
            if(y.get(0, i)>maxvalue)
                maxvalue=y.get(0, i);
        
        return maxvalue;
    }
    
      public double min(Matrix y){
        double minvalue=50000;
        
        for(int i=0;i<y.getColumnDimension();i++)
            if(y.get(0, i)<minvalue)
                minvalue=y.get(0, i);
        
        return minvalue;
    }
        
    public Matrix y;
    public Matrix X1= new Matrix (1,2);
    public Matrix Y1= new Matrix (1,2);
    public Matrix X2= new Matrix (1,2);
    public Matrix Y2= new Matrix (1,2);
    public double X;
    public double Y;
    public double T;
}


