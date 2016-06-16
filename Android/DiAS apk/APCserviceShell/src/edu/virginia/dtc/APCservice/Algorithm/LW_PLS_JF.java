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
public class LW_PLS_JF {
    
    public Matrix X;
    public Matrix Y;
    public Matrix x_sample;
    public double R;
    public double phi;
    public double I;
    public int J;
    public double alpha;
    public double sigma1;
    public double sigma2;
    public Matrix thita_m_offline= new Matrix (30,37);
    public Matrix thita_m_offline1= new Matrix (30,37);
    public Matrix regression_coefficient;
    
    public int r;
    
    public LW_PLS_JF(){
    
    }
   
    public LW_PLS_JF (Matrix X,Matrix Y, Matrix x_sample,double R, double phi, double I, int J,double alpha,double sigma1,double sigma2,Matrix thita_m_offline, Matrix regression_coefficient){
        
        this.I=I;
        this.J=J;
        this.R=R;
        this.X=X;
        this.Y=Y;
        this.alpha=alpha;
        this.phi=phi;
        this.regression_coefficient=regression_coefficient;
        this.sigma2=sigma2;
        this.sigma1=sigma1;
        this.thita_m_offline=thita_m_offline;
        this.x_sample=x_sample;
    }
    
    public double LWPLSJF(){
       int N=X.getRowDimension();
       int M=X.getColumnDimension();
         
       N=Y.getRowDimension();
       int L=Y.getColumnDimension();
     
       int j=0;
        
       Matrix x_q= new Matrix (x_sample.getRowDimension(),x_sample.getColumnDimension());
       Matrix x= new Matrix (X.getRowDimension(),X.getColumnDimension());
       Matrix y= new Matrix (Y.getRowDimension(),Y.getColumnDimension());
        
       x_q=x_sample;
       x=(X.transpose());  
       y=(Y.transpose());
       
       Matrix thita= new Matrix (thita_m_offline.getRowDimension(),thita_m_offline.getRowDimension());
       Matrix d= new Matrix (1,N);
       double sigma_d;
       
         for(int z=0;z<M;z++)
               thita_m_offline1.set(j,z,thita_m_offline.get(j,z));
       
       while(j!=J-1){
           thita=diag(thita_m_offline1,j);
           
           r=1;
     
           
           for(int i=0;i<N;i++){
               double temp=0;
               for(int z=0;z<x_q.getRowDimension();z++){
                   temp=(x.get(z, i)-x_q.get(z,0))*thita.get(z,z)*(x.get(z, i)-x_q.get(z,0))+temp;
               }
                d.set(0,i,Math.sqrt(temp));
           }

           sigma_d=std(d);

           Matrix w= new Matrix (1,d.getColumnDimension());
           
           for(int i=0;i<d.getColumnDimension();i++)
           w.set(0,i,Math.exp(-d.get(0, i)/sigma_d/phi));

           
           Matrix mean_rc= new Matrix (1,regression_coefficient.getRowDimension());
           
           mean_rc=w.times(regression_coefficient.transpose());
           
           double sum=sum(w);

               for(int z=0;z<regression_coefficient.getRowDimension();z++)
                   mean_rc.set(0, z, (mean_rc.get(0, z)/sum));
                 
          Matrix sigma= new Matrix (1,M);
 
           
           Matrix Var_weighted= new Matrix (N,M);
          
             for(int i=0;i<N;i++)
                       for(int z=0;z<M;z++)
                     Var_weighted.set(i,z,Math.pow((regression_coefficient.minus(mean_rc.transpose().times(ones(1,N)))).transpose().get(i, z),2)/sum);
         
             Var_weighted=w.times(Var_weighted);
                   
            for(int z=0;z<M;z++)
             thita_m_offline1.set(j+1,z,(Math.pow(Var_weighted.get(0, z), alpha)+thita_m_offline1.get(j,z))/2);  
            
           
           for( int mm=0; mm<M;mm++){
               sigma.set(0,mm,Math.abs((thita_m_offline1.get(j+1,mm)-thita_m_offline1.get(j,mm))/thita_m_offline1.get(j,mm)));
               
               if(sigma.get(0, mm)<sigma2){
                   sum++;
               }   
           }
           if(sum==0){
              j=J; 
           }
           else{
              j=j+1;
           }
               
       }
   
       Matrix thita_m= new Matrix (1,thita_m_offline1.getColumnDimension());
    
       for(int i=0;i<thita_m_offline1.getColumnDimension();i++)
           thita_m.set(0,i,thita_m_offline1.get(lastvaluereturnx(thita_m_offline1)[0]-1, i));

       
       LW_PLS_original_JF lw =new LW_PLS_original_JF(X,Y,x_q,R,thita_m,phi);
       double yp= lw.LWPLS_original();
       
       
       return yp;
     }
    
    public Matrix diag (Matrix a, int b){
        Matrix matrice = new Matrix (a.getColumnDimension(),a.getColumnDimension());
        
        for(int j=0;j<a.getColumnDimension();j++){
           for(int i=0;i<a.getColumnDimension();i++){
                if(i==j){
                    matrice.set(i,j,a.get(b,i));
                }
                else{
                    matrice.set(i,j,0);
                }
            }
        }
        return matrice;
    }
    
       public double std (Matrix d){
          
          double sum=0;
          double mean=0;
          double sum2=0;
          double std=0;
          
           for(int i=0;i<d.getColumnDimension() ; i++)
               sum=d.get(0,i)+sum;
           
           mean=sum/(d.getColumnDimension());
          
           for(int j=0; j<d.getColumnDimension(); j++)
           sum2= Math.pow(d.get(0,j)-mean,2)+sum2;
            
           std=sum2/(d.getColumnDimension()-1);
           
           return Math.sqrt(std);
       }
       
       public double sum (Matrix d){
           double sum=0;
           
           for(int i=0;i<d.getColumnDimension();i++)
              sum=sum+d.get(0, i);
           
           return sum;
       }
       
           public Matrix ones (int column, int row){
           Matrix matrice= new Matrix(column,row) ;
           
           for(int i=0;i<matrice.getRowDimension();i++)
                  for(int j=0;j<matrice.getColumnDimension();j++)
                            matrice.set(i, j, 1);
           
                      return matrice;
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
           int [] resultlocation=new int[2];
           resultlocation[0]=lastvaluex;
           resultlocation[1]=lastvaluey;
           
           return resultlocation;
       }
    
}
