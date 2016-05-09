/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import Jama.Matrix;

/**
 *
 * @author Mert
 */
public class m2014_3_19_kf_JF {
    
    public double lamda;
    public Matrix x_r;
    public Matrix x_e;
    public Matrix y;
    public int k;
    public Matrix sum_wyxT;
    public Matrix sum_wxxT;
    public Matrix sum_xxp;
    public Matrix sum_xpxp;
    public Matrix sum_wyy;
    public Matrix sum_wyx;
    public Matrix sum_xx;
    public Matrix sum_xxpp;
    public Matrix Q;
    public double R;
    public Matrix C;
    public Matrix A;
    public Matrix K;
    public double Sp;
    public Matrix sigma_kp;
    public double a_w0;
    public double b_w0;
    public Matrix sum_wxxTpinv;
    public Matrix sum_xpxppinv;
    public Matrix sigma_k;
    public double w;
    public Matrix y_p;
    
    public double [][][] wyxT =new double [5][5][60];
    public double [][][] wxxT =new double [5][5][60]; 
    public double [][][] xxp =new double [5][5][60];
    public double [][][] xpxp =new double [5][5][60];
    public double [][][] wyy =new double [5][60][1];
    public double [][][] wyx =new double [60][5][60];
    public double [][][] xx =new double [5][60][1];
    public double [][][] xxpp =new double [60][5][60];
    
    public m2014_3_19_kf_JF(){
        ///
    }
    
    public m2014_3_19_kf_JF(double lamda, Matrix x_r,Matrix x_e,Matrix y,int k,Matrix sum_wyxT,Matrix sum_wxxT,Matrix sum_xxp,Matrix sum_xpxp,Matrix sum_wyy,Matrix sum_wyx,Matrix sum_xx,Matrix sum_xxpp,Matrix Q, double R,Matrix A, Matrix C, double a_w0,double b_w0){
        
        this.C=C;
        this.Q=Q;
        this.R=R;
        this.A=A;
        this.a_w0=a_w0;
        this.b_w0=b_w0;
        this.k=k;
        this.lamda=lamda;
        this.sum_wxxT=sum_wxxT;
        this.sum_wyx=sum_wyx;
        this.sum_wyxT=sum_wyxT;
        this.sum_wyy=sum_wyy;
        this.sum_xpxp=sum_xpxp;
        this.sum_xx=sum_xx;
        this.sum_xxp=sum_xxp;
        this.sum_xxpp=sum_xxpp;
        this.x_e=x_e;
        this.x_r=x_r;
        this.y=y;
        
    }
    
    public void kf_JF(){
   /*////////////////////////////////////////////////////////////Input kf_JF/////////////////////////////////////////////////////////////////////////////////////     
   System.out.println("////////////////////////////////////////Input kj_JF/////////////////////////////////////////////////////////////////////////////////////");
   printMatrix(C,"C");
   printMatrix(Q,"Q");
   System.out.println(R+"  R");
   printMatrix(A,"A");
   System.out.println(a_w0+"  a_w0");
   System.out.println(b_w0+"  b_w0");
   System.out.println(lamda+"  lamda");
   printMatrix(x_r,"x_r");
   printMatrix(x_e,"x_e");
   printMatrix(y,"y");
   printMatrix(sum_wxxT,"sum_wxxT");
   printMatrix(sum_wyx,"sum_wyx");
   printMatrix(sum_wyxT,"sum_wyxT");
   printMatrix(sum_wyy,"sum_wyy");
   printMatrix(sum_xpxp,"sum_xpxp");
   printMatrix(sum_xx,"sum_xx");
   printMatrix(sum_xxp,"sum_xxp");
   printMatrix(sum_xxpp,"sum_xxpp");
   System.out.println("////////////////////////////////////////Input kj_JF/////////////////////////////////////////////////////////////////////////////////////");
   ////////////////////////////////////////////////////////////Input kf_JF/////////////////////////////////////////////////////////////////////////////////////  */     
       int d1=y.getColumnDimension();
       int d2=x_r.getColumnDimension();
 
        
       for(int m=0;m<x_e.getRowDimension();m++)
       x_e.set(m, lastvaluereturnx(x_e)[1], A.get(m,m)*x_e.get(m,lastvaluereturnx(x_e)[1]-1));
       
       w=0;
       for(int m=0;m<x_e.getRowDimension();m++)
       w=((a_w0+0.5)/((b_w0+(y.get(0,0)-(C.times(A).get(0,m))*(x_e.get(m,k-1))))/(R)*(y.get(0,0)-(C.times(A).get(0,m))*(x_e.get(m,k-1)))))+w;
    
       int hh=1000000;
       int gain=0;
       if(k>hh+8){
           gain=1/hh;
           sum_wyxT=sum_wyxT.minus(ThreeDimensiontoMatrix(wyxT,k-hh));
           sum_wxxT=sum_wxxT.minus(ThreeDimensiontoMatrix(wxxT,k-hh));
           sum_xxp=sum_xxp.minus(ThreeDimensiontoMatrix(xxp,k-hh));
           sum_xpxp=sum_xpxp.minus(ThreeDimensiontoMatrix(xpxp,k-hh));
           sum_wyy=sum_wyy.minus(ThreeDimensiontoMatrix(wyy,k-hh));
           sum_wyx=sum_wyx.minus(ThreeDimensiontoMatrix(wyx,k-hh));
           sum_xx=sum_xx.minus(ThreeDimensiontoMatrix(xx,k-hh));
           sum_xxpp=sum_xxpp.minus(ThreeDimensiontoMatrix(xxpp,k-hh));
       }
       else{
           gain=1/k-8;
       }
      
         for(int i=0;i<x_e.getRowDimension();i++){
            for(int j=0;j<x_e.getRowDimension();j++){
                 wyxT[0][j][k]=(getoneline(x_e,k).transpose()).times(w).get(0, j)*y.get(0,0);
                 wxxT[i][j][k]=getoneline(x_e,k).times(getoneline(x_e,k).transpose()).times(w).get(i, j);
                 xxp[i][j][k]=getoneline(x_e,k).times(getoneline(x_e,k-1).transpose()).get(i, j);
                 xpxp[i][j][k]=getoneline(x_e,k-1).times(getoneline(x_e,k-1).transpose()).get(i, j);
            }
         }
         
         for (int m=0;m<d1;m++){
             for (int j=0;j<x_e.getRowDimension();j++){
                 wyy[m][k][0]=w*y.get(0,m)*y.get(0,m);
                 wyx[j][m][k]=w*y.get(0,m)*x_e.get(j,k);
            }   
        }
         
          for (int n=0;n<d2;n++){
             for (int j=0;j<x_e.getRowDimension();j++){
                 xx[n][k][0]=x_e.get(n,k)*x_e.get(n,k);
                 xxpp[j][n][k]=x_e.get(n,k)*x_e.get(j,k-1);
            }   
        }
          
          for (int j=0;j<sum_wyxT.getRowDimension();j++)
              for (int i=0;i<sum_wyxT.getColumnDimension();i++)
        sum_wyxT.set(j,i,w*y.get(0, 0)*x_e.get(j,k)+sum_wyxT.times(lamda).get(j,i));
        
         sum_wxxT = new Matrix (x_e.getRowDimension(),x_e.getRowDimension()); 
          
                 for (int j=0;j<x_e.getRowDimension();j++)
              for (int i=0;i<x_e.getRowDimension();i++)
        sum_wxxT.set(j,i,w*x_e.get(j,k)*x_e.get(j,k)+sum_wxxT.times(lamda).get(j,i));
          
         sum_xxp = new Matrix (x_e.getRowDimension(),x_e.getRowDimension());          
               
            for (int j=0;j<sum_xxp.getRowDimension();j++)
              for (int i=0;i<sum_xxp.getRowDimension();i++)
        sum_xxp.set(j,i,x_e.get(j,k)*x_e.get(j,k-1)+sum_xxp.times(lamda).get(j,i));           
                 
        sum_xpxp= new Matrix (d2,A.getColumnDimension()) ; 
            
             for (int j=0;j<sum_xpxp.getRowDimension();j++)
              for (int i=0;i<sum_xpxp.getRowDimension();i++)
        sum_xpxp.set(j,i,x_e.get(j,k)*x_e.get(j,k-1)+sum_xpxp.times(lamda).get(j,i)); 
          
            for (int m=0;m<d1;m++){
             for (int j=0;j<x_e.getRowDimension();j++){
                 sum_wyy.set(0,m,w*y.get(0,m)*y.get(0,m)+sum_wyy.get(0, m)*lamda);
                 sum_wyx.set(j,m,w*y.get(0,m)*x_e.get(j,k)+sum_wyx.get(j, m)*lamda);
            }   
        }
    
            sum_xx = new Matrix (1,d2);
            sum_xxpp = new Matrix (x_e.getRowDimension(),d2);
            
              for (int n=0;n<d2;n++){
             for (int j=0;j<x_e.getRowDimension();j++){
                 sum_xx.set(0,n,x_e.get(n,k)*x_e.get(n,k)+sum_xx.get(0, n)*lamda);
                 sum_xxpp.set(j,n,x_e.get(n,k)*x_e.get(j,k-1)+sum_xxpp.get(j, n)*lamda);
            }   
        }
              
        Matrix RR = new Matrix (d1,d1);
        Matrix QQ = new Matrix (d1,d1);
        
        double temp=0;
        
        for (int m=0;m<d1;m++) 
                    for (int j=0;j<C.getColumnDimension();j++) 
        temp= C.get(m,j)*sum_wxxT.get(m,j)*C.get(m,j)+temp;
        
              for (int m=0;m<d1;m++) {
                    for (int j=0;j<C.getColumnDimension();j++) 
RR.set(m,m,gain*(sum_wyy.get(0, m)-2*C.get(m,j)*sum_wyx.get(j,m)+(temp)));
if (RR.get(m,m)<=0){
    RR.set(m,m,1);
    }
}   
        
        temp=0;
        Matrix tempmatrice = new Matrix (1,1);
        
         for (int n=0;n<d2;n++) 
                    for (int j=0;j<A.getColumnDimension();j++) 
         tempmatrice.set(0,0,A.get(n,j)*sum_xpxp.get(n,j)*A.get(n,j));
           
           QQ= new Matrix (d2,A.getColumnDimension());
      
           double temp1=0;
           
              for (int n=0;n<d2;n++) {
                  temp1=0;
                    for (int j=0;j<A.getColumnDimension();j++) {
                        temp1=2*A.get(n,j)*sum_xxpp.get(j,n)+temp1;
                    }
                     temp=gain*(sum_xx.get(0,n)-temp1+tempmatrice.get(0,0));
                    QQ.set(n,n,gain*(sum_xx.get(0,n)-temp1+tempmatrice.get(0,0)));
if (QQ.get(n,n)<=0){
    QQ.set(n,n,0.1);
    }
}           
           
  sum_wxxTpinv = new Matrix (sum_wxxT.getColumnDimension(),sum_wxxT.getRowDimension())  ;              
                  
  if(sum_wxxT.det()!=0){
  sum_wxxTpinv=sum_wxxT.inverse();
  }
  else{
  for(int i=0;i<sum_wxxT.getColumnDimension();i++)
      for(int j=0;j<sum_wxxT.getRowDimension();j++)
      sum_wxxTpinv.set(i, j, (1/sum_wxxT.get(i, j)/(sum_wxxT.getColumnDimension()+sum_wxxT.getRowDimension()))/2);
  }
  
  sum_xpxppinv = new Matrix (sum_xpxp.getColumnDimension(), sum_xpxp.getRowDimension());
  
   if(sum_xpxp.det()!=0){
  sum_xpxppinv=sum_xpxp.inverse();
  }
  else{
  for(int i=0;i<sum_xpxp.getColumnDimension();i++)
      for(int j=0;j<sum_xpxp.getRowDimension();j++)
      sum_xpxppinv.set(i, j, (1/sum_xpxp.get(i, j)/(sum_xpxp.getColumnDimension()+sum_xpxp.getRowDimension()))/2);
  }
         C= sum_wyxT.times(sum_wxxTpinv);
         A= sum_xxp.times(sum_xpxppinv);

         
         double temp2=0;
         
         sigma_kp=QQ;
         temp1=C.times(Q).times(C.transpose()).get(0,0);
         temp2=1/(RR.times(w).get(0, 0));
         Sp=1/(temp1+temp2);
         K=Q.times(C.transpose()).times(Sp);
         
         sigma_k = new Matrix (sigma_kp.getRowDimension(),sigma_kp.getRowDimension());
         
         for ( int i=0 ; i<sigma_kp.getRowDimension();i++)
             for ( int j=0 ; j<sigma_kp.getRowDimension();j++)
         sigma_k.set(i,j,((eye(d1).get(0,0))-(K.times(C).get(i,j))));

         
         sigma_k=sigma_k.times(sigma_kp);
         
         y_p =new Matrix (1,1);
         
         for ( int j=0 ; j<x_e.getRowDimension();j++)
         y_p.set(0,0,C.times(x_e.get(j,k)).get(0, 0)+y_p.get(0,0));
  
  /* ////////////////////////////////////////////////////////////Output kf_JF/////////////////////////////////////////////////////////////////////////////////////     
   System.out.println("////////////////////////////////////////Output kj_JF/////////////////////////////////////////////////////////////////////////////////////");
   printMatrix(C,"C");
   printMatrix(Q,"Q");
   System.out.println(R+"  R");
   printMatrix(A,"A");
   System.out.println(w+"  w");
   System.out.println(Sp+"  Sp");
   System.out.println(lamda+"  lamda");
   printMatrix(x_e,"x_e");
   printMatrix(y_p,"y_p");
   printMatrix(sum_wxxT,"sum_wxxT");
   printMatrix(sum_wyx,"sum_wyx");
   printMatrix(sum_wyxT,"sum_wyxT");
   printMatrix(sum_wyy,"sum_wyy");
   printMatrix(sum_xpxp,"sum_xpxp");
   printMatrix(sum_xx,"sum_xx");
   printMatrix(sum_xxp,"sum_xxp");
   printMatrix(sum_xxpp,"sum_xxpp");
   System.out.println("////////////////////////////////////////Output kj_JF/////////////////////////////////////////////////////////////////////////////////////");
   ////////////////////////////////////////////////////////////Output kf_JF/////////////////////////////////////////////////////////////////////////////////////  */
         
    }
    
                    public Matrix eye (int number){
           Matrix matrice= new Matrix(number,number) ;
           
           for(int i=0;i<matrice.getRowDimension();i++)
                  for(int j=0;j<matrice.getColumnDimension();j++)
                      if(i!=j)      
                      matrice.set(i, j, 0);
                      else
                      matrice.set(i, j, 1);  
           
                      return matrice;
       }
    
    
    
    public Matrix ThreeDimensiontoMatrix (double a[][][] , int z){
        Matrix result = new Matrix (lastvaluereturnxyz(a)[1],lastvaluereturnxyz(a)[2]);
        
        for(int i=0;i<lastvaluereturnxyz(a)[1];i++)
            for(int j=0;j<lastvaluereturnxyz(a)[2];j++)
                result.set(i, j, a[i][j][z]);
                
        
        return result;
    } 
    
    public int[] lastvaluereturnxyz (double s[][][]){
           int lastvaluex=0;
           int lastvaluey=0;
           int lastvaluez=0;

           for(int i=0;i<s.length;i++){
               for(int j=0;j<s[0].length;j++){
                   for(int z=0;z<s[0][0].length;z++){
                   if(s[i][j][z]!=0){
                       lastvaluex=i;
                       lastvaluey=j;
                       lastvaluez=z;
             
                   }
                   }
               }
           }
           int [] array=new int[4];
           array[1]=lastvaluex;
           array[2]=lastvaluey;
           array[3]=lastvaluez;
           
           return array;
       }
    
    public Matrix getoneline (Matrix a, int column){
       
            Matrix result= new Matrix (a.getRowDimension(),1);
        
            for(int i=0;i<a.getRowDimension();i++)
                result.set(i,0,a.get(i, column));
        
        return result;
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
     
     public void print3Dmatrice(double x[][][],String matricename){
           
           int [] valuex;
           valuex=lastvaluereturnxyz(x);
           
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
    
}
