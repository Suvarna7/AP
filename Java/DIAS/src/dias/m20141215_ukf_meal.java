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
public class m20141215_ukf_meal {
    
    public double y;
    public Matrix x_a;
    public Matrix P_a;
    public double R_n;
    public Matrix Q_n;
    public double Gb;
    public Matrix Wc =new Matrix (1,1);
    public Matrix Wm =new Matrix (1,1);
    public Matrix P_p ;
    public Matrix x_p ;
    
    public m20141215_ukf_meal (double y,Matrix x_a,Matrix P_a,double R_n,Matrix Q_n,double Gb){
        this.y=y;
        this.x_a=x_a;
        this.P_a=P_a;
        this.R_n=R_n;
        this.Q_n=Q_n;
        this.Gb=Gb;
    }
    
    public void m20141215_ukf_meal(){
   /* ////////////////////////////////////////////////INPUTS UFK_MEAL/////////////////////////////////////////////////////////////////////////////////////
    System.out.println("////////////////////////////////INPUTS UFK_MEAL/////////////////////////////////////////////////////////////////////////////////");
    printMatrix(x_a,"x_a");    
    printMatrix(P_a,"P_a");  
    printMatrix(Q_n,"Q_n"); 
    System.out.println(y+"      y");
    System.out.println(Gb+"     Gb");
    System.out.println(R_n+"     R_n");
    System.out.println("////////////////////////////////INPUTS UFK_MEAL/////////////////////////////////////////////////////////////////////////////////");
     ////////////////////////////////////////////////INPUTS UFK_MEAL/////////////////////////////////////////////////////////////////////////////////////*/      
       m20150711_load_global_variables lgvariables = new m20150711_load_global_variables();
       CGM__SEDFR_JF cs= new CGM__SEDFR_JF();

       int alp=1;
       int bet=2;
       int K=0;
       
       double n_y=1;
       
       double L=x_a.getRowDimension();
       double lamda=(alp*alp)*(L+K)-L;
       double W0m=lamda/(L+lamda);
       double W0c=W0m+(1-alp*alp+bet);

       Wm= DIAS.createnewMatrix(1, (int) (2*L+1) ,Wm);
       Wc= DIAS.createnewMatrix(1, (int) (2*L+1) ,Wc);
       
       Wm.set(0,0,W0m);
       Wc.set(0,0,W0c);
       for(int i=1;i<2*L+1;i++){
           Wm.set(0,i,(double) (1/(2*(L+lamda))));
           Wc.set(0,i,(double) (1/(2*(L+lamda))));
       }
       
      Matrix LL = new Matrix (P_a.getColumnDimension(),P_a.getColumnDimension()); 
      Matrix DD = new Matrix (P_a.getColumnDimension(),P_a.getColumnDimension()); 
      Matrix Dsqrt = new Matrix (P_a.getColumnDimension(),P_a.getColumnDimension()); 
      Matrix DDsqrt = new Matrix (P_a.getColumnDimension(),P_a.getColumnDimension()); 
      Matrix X_a = new Matrix (1,1); 
      
      if(lgvariables.kj!=21){
      Ldl ldlfind =new Ldl (P_a);
      ldlfind.LdlFind();
      
      LL=ldlfind.L;
      DD=ldlfind.D;
      }
      else if(lgvariables.kj==21){
          
          for(int i=0;i<P_a.getColumnDimension();i++)
              for(int j=0;j<P_a.getRowDimension();j++)
                  if(i==j)
                      LL.set(i, j, 1);
                  else
                      LL.set(i, j, 0);
          
           for(int i=0;i<P_a.getColumnDimension();i++)
              for(int j=0;j<P_a.getRowDimension();j++)
                 DD.set(i, j, 0);   
      }
      
       for(int i=0;i<P_a.getColumnDimension();i++)
              for(int j=0;j<P_a.getRowDimension();j++)
                 DDsqrt.set(i, j, Math.sqrt(DD.get(i, j)));
  
     
       X_a= new Matrix(x_a.getRowDimension(),(x_a.getRowDimension())*2+1);
       
        for(int i=0;i<x_a.getRowDimension();i++)
         for(int j=0;j<x_a.getColumnDimension();j++)
                  X_a.set(i,j,0);
      
     for(int i=0;i<x_a.getRowDimension();i++)
         for(int j=0;j<x_a.getColumnDimension();j++)
             X_a.set(i,j,x_a.get(i, 0));
   
     Matrix templdl=  new Matrix(8,8);
     templdl=(LL.times(DDsqrt.times(Math.sqrt(L+lamda))));

     for(int i=1;i<x_a.getRowDimension()+1;i++)
     for(int j=0;j<x_a.getRowDimension();j++)
        X_a.set(j,i,(x_a.get(j, 0)+(LL.times(DDsqrt.times(Math.sqrt(L+lamda)))).get(j,i-1)));

       for(int i=x_a.getRowDimension()+1;i<x_a.getRowDimension()+1+x_a.getRowDimension();i++)
       for(int j=0;j<x_a.getRowDimension();j++)
       X_a.set(j,i,(x_a.get(j, 0)-(LL.times(DDsqrt.times(Math.sqrt(L+lamda)))).get(j,i-(x_a.getRowDimension()+1))));
       
      // printMatrix(X_a,"X_a");
   
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     // %% Propagate the sigma points through the nonlinear dynamics 
       Matrix X_n=new Matrix ((int) L,(int) (2*L+1));
       Matrix x_n=new Matrix ((int) L,1);
       Matrix X_ntemp2=new Matrix ((int) L,1);
       
      for(int i=0;i<L;i++) 
          for(int j=0;j<(2*L+1);j++) 
           X_n.set(i, j, 0);
      
      for(int i=0;i<L;i++) 
           x_n.set(i, 0, 0);
      
        for(int i=0;i<L;i++) 
           X_ntemp2.set(i, 0, 0);
       
       double [] xa= new double [8];
       double [] xtemp= new double [8];
       
       for(int i=0;i<8;i++){
       xa[i]=0;
       xtemp[i]=0;
       }
      
       for(int i=0;i<x_a.getRowDimension();i++)
       xa[i]=x_a.get(i, 0);
       
       double [] result= new double [8];
       
       for(int i=0;i<8;i++){
       result[i]=0;
       }
      
       for(int ii=0;ii<2*L+1;ii++){
              xtemp=(f_fcn(X_a,1,ii));
              for(int j=0;j<x_a.getRowDimension();j++){
              X_n.set(j, ii, xtemp[j]);
              X_ntemp2.set(j,0,xtemp[j]);
              }
              m20141215_sigma_const sconst =new m20141215_sigma_const(X_ntemp2,xa);
              result=sconst.sigma_const();
              for(int j=0;j<x_a.getRowDimension();j++){
              X_n.set(j, ii,result[j]);
              }
              for(int j=0;j<x_a.getRowDimension();j++){
              x_n.set(j, 0, x_n.get(j,0)+X_n.get(j,ii)*Wm.get(0, ii));
              }     
       }
       
     //  printMatrix(X_n,"X_n");
     //  printMatrix(x_n,"x_n");
 ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //////////// %% Compute the predicted covariance   //Works
    
Matrix P_n= new Matrix ((int) L,(int) L);
for(int i=0;i<L;i++)
    for(int j=0;j<L;j++)
        P_n.set(i, j, 0);

Matrix X_ntemp= new Matrix(X_n.getRowDimension(),1) ;
for(int i=0;i<X_n.getRowDimension();i++)
     X_ntemp.set(i, 0, 0);

for (int ii=0;ii<2*L+1;ii++){
    for(int j=0;j<X_n.getRowDimension();j++){
         X_ntemp.set(j,0,(X_n.get(j,ii)-x_n.get(j,0)));      
}
   P_n=(X_ntemp.times(X_ntemp.transpose()).times(Wc.get(0,ii))).plus(P_n);
}
P_n=P_n.plus(Q_n);  
//printMatrix(P_n,"P_n");

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///%% Instantiate the new sigma points through the observation model g_fnc //Works
Matrix Y= new Matrix ((int) (n_y),(int) (2*L+1));
Matrix y_n= new Matrix ((int) n_y,1);
for(int i=0;i<n_y;i++)
     y_n.set(i, 0, 0);

for (int ii=0;ii<2*L+1;ii++){
    Y.set(0, ii,X_n.get(1,ii));
    y_n.set(0,0,y_n.get(0,0)+(Wm.get(0,ii)*Y.get(0,ii)));
}
//printMatrix(Y,"Y");
//printMatrix(y_n,"y_n");
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////%% Obtain the innovation covariance and the cross covariance matrices
Matrix P_yy= new Matrix ((int) (n_y),(int) (n_y));
Matrix P_yx= new Matrix ((int) L,(int) (n_y));
Matrix Y_ntemp= new Matrix ((int) (n_y),(int) (n_y));
for(int i=0;i<(n_y);i++)
    for(int j=0;j<(n_y);j++)
        P_yy.set(i, j, 0);

for(int i=0;i<L;i++)
    for(int j=0;j<(n_y);j++)
        P_yx.set(i, j, 0);


for(int i=0;i<(n_y);i++)
    for(int j=0;j<(n_y);j++)
        Y_ntemp.set(i, j, 0);

for (int ii=0;ii<2*L+1;ii++){
    for(int j=0;j<(int)(n_y);j++){
         Y_ntemp.set(j,0,(Y.get(j,ii)-y_n.get(0,0)));
}
   P_yy=(Y_ntemp.times(Y_ntemp.transpose())).times(Wc.get(0,ii)).plus(P_yy);
}

for (int ii=0;ii<2*L+1;ii++){
    for(int j=0;j<X_n.getRowDimension();j++){
         X_ntemp.set(j,0,(X_n.get(j,ii)-x_n.get(j,0)));
}
        for(int j=0;j<(int)(n_y);j++){
         Y_ntemp.set(j,0,(Y.get(j,ii)-y_n.get(0,0)));
}
   
   P_yx=X_ntemp.times(Y_ntemp.transpose()).times(Wc.get(0,ii)).plus(P_yx);
}

P_yy.set(0,0,P_yy.get(0,0)+R_n);
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//%% Perform the measurement update using the regular Kalman Filter equations
 
 Matrix K_p = new Matrix (P_yx.getRowDimension(),1);
 for(int i=0;i<P_yx.getRowDimension();i++)
        K_p.set(i, 0, 0);

for(int i=0; i<P_yx.getRowDimension();i++)
K_p.set(i,0,P_yx.get(i, 0)/P_yy.get(0, 0));


x_p = new Matrix (P_yx.getRowDimension(),1);

for(int i=0;i<P_yx.getRowDimension();i++)
    x_p.set(i, 0, 0);

for(int i=0; i<P_yx.getRowDimension();i++)
x_p.set(i,0,(x_n.get(i,0)+K_p.get(i,0)*(y-y_n.get(0, 0))));

//printMatrix(x_p,"x_p");

P_p = new Matrix (P_yx.getRowDimension(),P_yx.getRowDimension());
for(int i=0;i<P_yx.getRowDimension();i++)
    for(int j=0;j<P_yx.getRowDimension();j++)
    P_p .set(i, j, 0);

P_p=P_n.minus(K_p.times(P_yy).times(K_p.transpose()));

 /*  printMatrix(P_p,"P_p");
    ////////////////////////////////////////////////Output UFK_MEAL/////////////////////////////////////////////////////////////////////////////////////
    System.out.println("////////////////////////////////Output UFK_MEAL/////////////////////////////////////////////////////////////////////////////////");
    printMatrix(x_p,"x_p");
    printMatrix(P_p,"P_p");
    System.out.println("////////////////////////////////Output UFK_MEAL/////////////////////////////////////////////////////////////////////////////////");
     ////////////////////////////////////////////////Output UFK_MEAL/////////////////////////////////////////////////////////////////////////////////////*/ 


    }
    
    public double [] f_fcn (Matrix x,double h,int ii){
        double Ieff=x.get(0, ii);
        double Gt=x.get(1, ii);
        double Rc=x.get(2, ii);
        double Rc_1=x.get(3, ii);
        double p1=x.get(4, ii);
        double p2=x.get(5, ii);
        double p4=x.get(6, ii);
        double taom=x.get(7, ii);
        double atao=Math.exp(h/taom);
        
        double [] f = new double [8];
        
        for(int i=0;i<8;i++)
            f[i]=0;
        
        f[0]=h*(-p2*Ieff)+Ieff;
        f[1]=h*(p1*(Gb-Gt)-p4*Ieff*Gt+Rc)+Gt;
        f[2]=2*Rc/atao-Rc_1/(atao*atao);
        f[3]=Rc;
        f[4]=p1;
        f[5]=p2;
        f[6]=p4;
        f[7]=taom;
        
        
        return f;
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
