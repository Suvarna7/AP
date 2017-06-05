/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

/**
 *
 * @author Mert
 */
public class LW_PLS_original_JF {
    
    public Matrix X;
    public Matrix Y;
    public Matrix x_q;
    public double R;
    public Matrix thita_m_i;
    public double phi;
    public double J;
    public int r;
    public double t_mult_q;
    public Matrix y_bar;
    public Matrix tr;
    public Matrix pr;
    public Matrix wr;
    public Matrix qr;
    public Matrix trR;
    public Matrix prR;
    public Matrix wrR;
    public Matrix qrR;
    
    public LW_PLS_original_JF(Matrix X,Matrix Y,Matrix x_q,double R,Matrix thita_m_i,double phi){
        this.R=R;
        this.X=X;
        this.Y=Y;
        this.phi=phi;
        this.thita_m_i=thita_m_i;
        this.x_q=x_q;   
    }
    
    public double LWPLS_original(){
       int N=X.getRowDimension();
       int M=X.getColumnDimension();
            
       N=Y.getColumnDimension();
       int L=Y.getRowDimension();
       
       int j=0;
        
       Matrix x= new Matrix (X.getRowDimension(),X.getColumnDimension());
       Matrix y= new Matrix (Y.getRowDimension(),Y.getColumnDimension());
        
       x=(X.transpose());  
       y=(Y.transpose());
       
       Matrix thita= new Matrix (thita_m_i.getRowDimension(),thita_m_i.getRowDimension());
       Matrix d= new Matrix (1,x.getColumnDimension());
       double sigma_d;
       
           thita=diag(thita_m_i,j);
 
           r=1;
           
           for(int i=0;i<x.getColumnDimension();i++){
               double temp=0;
               for(int z=0;z<(x.getRowDimension()-1);z++){
                   temp=(x.get(z, i)-x_q.get(z,0))*(thita.get(z,z))*(x.get(z, i)-x_q.get(z,0))+temp;     
               }
               d.set(0,i,Math.sqrt(temp));
           }
            
           sigma_d=std(d);
          
           
           Matrix w= new Matrix (1,d.getColumnDimension());
           
           for(int i=0;i<d.getColumnDimension();i++)
           w.set(0,i,Math.exp(-d.get(0, i)/sigma_d/phi));
           
           Matrix omega= new Matrix(w.getColumnDimension(),w.getColumnDimension());
           omega=diag(w,0);
           
           Matrix x_bar= new Matrix (1,M);
           
           double sum=sum(w);
           
           
               for (int z=0;z<x.getRowDimension();z++){
                 double temp=0;
                   for (int m=0;m<x.getColumnDimension();m++){
                    temp=w.get(0, m)*x.transpose().get(m,z)+temp;
                }
                x_bar.set(0,z,temp/sum);
         }

            
            y_bar= new Matrix (1,1);
            
            
              for (int z=0;z<1;z++){
                 double temp=0;  
                  for (int l=0;l<y.getColumnDimension();l++){
                    temp=w.get(0, l)*y.transpose().get(l,z)+temp;
                  }
             y_bar.set(0, z, temp/sum);
         }
              
              
               Matrix Xr= new Matrix (X.getRowDimension()-1,X.getColumnDimension()-1); 
               Xr=X.minus(ones(X.getRowDimension(),1).times(x_bar));  
               
                   
               Matrix Yr= new Matrix (Y.getRowDimension(),1); 
               for(int i=0;i<Y.getRowDimension();i++)
               Yr.set(i,0,Y.get(i, 0)-(ones(Y.getRowDimension(),1).times(y_bar)).get(i,0));  
               
                R=12;
                
                Matrix x_q_r= new Matrix (x_q.getRowDimension(),(int)R);
               
                for(int m=0;m<x_q.getRowDimension();m++)
                x_q_r.set(m,0,x_q.get(m,0)-((x_bar).get(0, m)));
               
                t_mult_q=0;
               
                wr = new Matrix (Xr.getColumnDimension(),(int) 1);
                tr = new Matrix (Xr.getRowDimension(),(int) 1);
                pr = new Matrix (Xr.getColumnDimension(),(int) 1);
                qr = new Matrix (Xr.getColumnDimension(),(int) 1);
                Matrix tq = new Matrix (1,(int) 1);
               
                wrR = new Matrix (Xr.getColumnDimension(),(int) R);
                trR = new Matrix (Xr.getColumnDimension(),(int) R);
                prR = new Matrix (Xr.getColumnDimension(),(int) R);
                qrR = new Matrix (Xr.getColumnDimension(),(int) R);
                Matrix tqR = new Matrix (1,(int) R);
                
                Matrix x_q_rR = new Matrix (x_q_r.getRowDimension(),(int) R);
                
               for(int r=0;r<R;r++){
                   
                         if(r>0){
                             for(int i=0;i<Xr.getColumnDimension();i++){
                             wr.set(i, 0, wrR.get(i, r-1));
                             tr.set(i, 0, trR.get(i, r-1));
                             pr.set(i, 0, prR.get(i, r-1));
                             qr.set(0, 0, qrR.get(0, r-1));
                             }
                              tr.set(0, 0, trR.get(0, r-1));
                            
                         for(int i=0;i<x_q_r.getRowDimension();i++)
                         x_q_rR.set(i,r,x_q_r.get(i,r-1));
                              
                                     }
                   
               Matrix Eigenvector= new Matrix (Xr.transpose().times(omega).times(Yr).times(Yr.transpose()).times(omega).times(Xr).getRowDimension(),Xr.transpose().times(omega).times(Yr).times(Yr.transpose()).times(omega).times(Xr).getColumnDimension());
             
               
               EigenvalueDecomposition result;
               result=(((((((Xr.transpose()).times(omega)).times(Yr)).times(Yr.transpose())).times(omega)).times(Xr)).eig());
         
               Eigenvector=result.getV();
             
               for(int m=0;m<Eigenvector.getColumnDimension()-1;m++)
               wr.set(m,0,Eigenvector.get(m,0));
              
               sum=0;
               for(int m=0;m<Xr.getRowDimension();m++){
               tr.set(m,0, (Xr.times(wr)).get(m, 0));
               }
         
               double temp=0;
               temp=(tr.transpose().times(omega).times(tr)).get(0, 0);
               pr=(((Xr.transpose()).times(omega).times(tr)).times(1/temp));

                   qr=((Yr.transpose()).times(omega).times((getoneline(tr,0)))).times(1/temp);       
                   temp=0;

                   for(int i=0;i<x_q_r.getRowDimension();i++)
                   temp=x_q_r.transpose().get(0,i)*(wr.get(i, 0))+temp;
                   
                   tq.set(0,0,temp);
                  
                   t_mult_q=(tq.get(0,0)*qr.get(0,0))+t_mult_q;
                   
                   Xr=Xr.minus(tr.times(pr.transpose()));  
                   Yr=Yr.minus(tr.times(qr.transpose()));
     
                             
                   for(int i=0;i<x_q_r.getRowDimension();i++)
                   x_q_r.set(i,0,x_q_r.get(i,0)-(pr.times(tq.get(0, 0))).get(i, 0));
                   
                   for(int i=0;i<Xr.getColumnDimension();i++){
                             wrR.set(i, r, wr.get(i, 0));
                             trR.set(i, r, tr.get(i, 0));
                             prR.set(i, r, pr.get(i, 0));
                             qrR.set(0, r, qr.get(0, 0));
                             }
                                
                    tqR.set(0, r, tq.get(0, 0));
                    
                    for(int i=0;i<x_q_r.getRowDimension();i++)
                         x_q_rR.set(i,r,x_q_r.get(i,0));
               }
               double yq_estimate=y_bar.transpose().get(0,0)+(t_mult_q);
              
               return yq_estimate;
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
         
                public static void printMatrix(Matrix m, String name){
		System.out.print("\n "+name+": \n{");
		for (double[] row: m.getArray()){
			for (double val: row)
				System.out.print(" "+val);
			System.out.println();
		}
		System.out.println("}");
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
           
       public Matrix getoneline (Matrix a, int column){
       
            Matrix result= new Matrix (a.getRowDimension(),1);
        
            for(int i=0;i<a.getRowDimension();i++)
                result.set(i,0,a.get(i, column));
        
        return result;
    }
       
            public Matrix writecolumn (Matrix a, int column){
       
            Matrix result= new Matrix (a.getRowDimension(),column+1);
        
            for(int i=0;i<a.getRowDimension();i++)
                result.set(i,column,a.get(i, 0));
        
        return result;
    }
    
}
