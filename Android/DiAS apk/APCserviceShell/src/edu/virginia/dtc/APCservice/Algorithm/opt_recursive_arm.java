/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.dtc.APCservice.Algorithm;

import Jama.Matrix;
import de.xypron.jcobyla.Calcfc;
import de.xypron.jcobyla.Cobyla;
import de.xypron.jcobyla.CobylaExitStatus;

/**
 *
 * @author User
 */
public class opt_recursive_arm {
    
    public double Y;
    public Matrix phi;
    public Matrix Q_old;
    public Matrix P_old;
    public double lamda_old;
    public double [] upperlimit= new double [4];
    public double [] lowerlimit= new double [4];
    public double lamda;
    public double lamda1;
    public double lamda2;
    public double err;
    public double Y_model;
    public Matrix Q= new Matrix (4,1);
    public Matrix P;
    public Matrix pP;
    public double [] Q_oldtemp = new double [4];
    public double f;
    public double f1;
    public double [] xtemp = new double [4];
    public double [] xtemp2 = new double [4];
    
    public double rhobeg = 1;
    public double rhoend = 1.0e-6;
    public int iprint = 0;
    public int maxfun = 2000;
    double temp2;
    public double [][] A_state= new double [4][4];
    public double [] c;
    public double lamdafinal;
    public double alp;
    public double sens;
    public double err_old;
    public int s=0;
    public Matrix Qresult= new Matrix (4,1);
    public Matrix Q_res; 
    
    public opt_recursive_arm (double Y,Matrix phi,Matrix Q_old,Matrix P_old,double lamda_old,double err_old,double [] upperlimit,double [] lowerlimit,double lamdafinal,double alp,double sens){
        this.Y=Y;
        this.P_old=P_old;
        this.lamda_old=lamda_old;
        this.Q_old=Q_old;
        this.phi=phi;
        this.upperlimit=upperlimit;
        this.lowerlimit=lowerlimit;
        this.sens=sens;
        this.alp=alp;
        this.lamdafinal=lamdafinal;
        this.err_old= err_old;
    }
    
    public void optrecursive (){
        
      /* /////////////////////////////INPUTS RECURSIVE_ARM//////////////////////////////////////////////////////////////////////////////////////////////////////
        System.out.println("///////////////////INPUTS RECURSIVE_ARM///////////////////////////////////////////////////////////////////////////////////////////");
        System.out.println(Y+"Y");
        printMatrix(P_old,"P_old");
        printMatrix(phi,"phi");
        printMatrix(Q_old,"Q_old");
        System.out.println(lamda_old+"lamda_old");
        
        System.out.println("upperlimit");
        
        for(int i=0;i<upperlimit.length;i++)
        System.out.print(upperlimit[i]+"      ");
        
        System.out.println();
        System.out.println("lowerlimit");
        
        for(int i=0;i<lowerlimit.length;i++)
        System.out.print(lowerlimit[i]+"      ");
        
        System.out.println(sens+"sens");
        System.out.println(alp+"alp");
        System.out.println(lamdafinal+"lamdafinal");
        System.out.println(err_old+"err_old");
        
        System.out.println();
        System.out.println("///////////////////INPUTS RECURSIVE_ARM///////////////////////////////////////////////////////////////////////////////////////////");
        /////////////////////////////INPUTS RECURSIVE_ARM//////////////////////////////////////////////////////////////////////////////////////////////////////*/
        
  
  ///////////////////Calculate P and pP///////////////////////////////////////////////////////////////////////      
  pP= (phi.transpose());  
  pP=((phi.transpose()).times(P_old));   
  double result=((((phi.transpose()).times(P_old)).times(phi)).get(0,0)+lamda_old);
  result=1/((((phi.transpose()).times(P_old)).times(phi)).get(0,0)+lamda_old);
  pP=P_old.times(phi).times(result);
  pP=pP.times(phi.transpose()).times(P_old);
  pP=P_old.minus(pP);  
  pP=pP.times(1/(lamda_old));
  P=pP;
  if(pP.det()!=0){
  pP=pP.inverse();
  }
  else{
  for(int i=0;i<pP.getColumnDimension();i++)
      for(int j=0;j<pP.getRowDimension();j++)
       pP.set(i, j, 1/pP.get(i, j)/(pP.getColumnDimension()+pP.getRowDimension()));
  }
  
                   Cobyla cobyla = new Cobyla();
		         Calcfc calcfc = new Calcfc() {

	            @Override
                    public double compute(int n, int m, double[] x,	double[] con) {
        
                                A_state[0][0]=-x[0];
                                A_state[0][1]=-x[1];
                                A_state[0][2]=-x[2];
                                A_state[0][3]=-x[3];
                               
                                A_state[1][0]=1;
                                A_state[1][1]=0;
                                A_state[1][2]=0;
                                A_state[1][3]=0;
                                
                                A_state[2][0]=0;
                                A_state[2][1]=1;
                                A_state[2][2]=0;
                                A_state[2][3]=0;
                                
                                A_state[3][0]=0;
                                A_state[3][1]=0;
                                A_state[3][2]=0;
                                A_state[3][3]=0;
                                 
                                Matrix Astate= new Matrix(A_state);
                                Matrix Astatetemp= new Matrix(4,1);
                            
                                for(int z=0;z<4;z++)
                                Astatetemp.set(z,0,Math.abs(Astate.eig().getD().get(z,z)));

                                c= new double [Astatetemp.getRowDimension()];
                                
                                for(int ix=0;ix<Astatetemp.getRowDimension();ix++)
                                c[ix]=Astatetemp.get(ix,0)-0.99;
     
                                 for(int i=0;i<Q_old.getRowDimension();i++){
                                 con[2*i]=x[i]-lowerlimit[i];
                                 con[2*i+1]=-x[i]+upperlimit[i];
                                 
                                 }
                                 con[2*Q_old.getRowDimension()]=c[0];
                                 con[Q_old.getRowDimension()+1]=c[1];
                                 con[2*Q_old.getRowDimension()+2]=c[2];
                                 con[2*Q_old.getRowDimension()+3]=c[3];
                           
                              Matrix differencematrix = new Matrix (Q_old.getRowDimension(),1);
                            
                              for(int s=0;s<Q_old.getRowDimension();s++)
                              differencematrix.set(s,0,x[s]-Q_old.get(s, 0));
                              
                              
                              f=(((differencematrix.transpose()).times(pP)).times(differencematrix)).get(0, 0);
  
                              
                              double differencematrix2 = 0;
                              double temp=0;
                              
                             for(int a=0;a<x.length;a++)
                                 temp=phi.get(a, 0)*x[a]+temp;
                             
                             differencematrix2=temp;
                                  
                             f1=(Y-differencematrix2)*(Y-differencematrix2);
                              
                         for(int i=0;i<Q_old.getRowDimension();i++)   
                         Qresult.set(i, 0, x[i]);
                            
                            return (f+f1);
			}
                };
		CobylaExitStatus result1;

                
	        result1 = cobyla.findMinimum(calcfc, 4,2*Q_old.getRowDimension()+4, Q_oldtemp, rhobeg, rhoend, iprint, maxfun);  


Q_res =new Matrix(4,1);

for(int i=0;i<4;i++)
Q_res.set(i, 0, Qresult.get(i, 0));

err=Y-((phi.transpose().times(Q_res)).get(0, 0));
Y_model=(phi.transpose().times(Q_res)).get(0, 0);
lamda1=alp*lamda_old+(1-alp)*lamdafinal;
lamda2=Math.exp(-(err_old*err_old)/(sens));
lamda=lamda1*lamda2;
if (lamda<0.005)
lamda=0.005;     

/*////////////////////////////////////////////////////////////OUTPUT RECURSIVE_ARM//////////////////////////////////////////////////////////////////////////////////
System.out.println("/////////////////////////////////////////OUTPUT RECURSIVE_ARM/////////////////////////////////////////////////////////////////////////////");
printMatrix(Q_res,"Q_res");
System.out.println(err+"  err");
System.out.println(Y_model+"  Y_model");
System.out.println(lamda1+"  lamda1");
System.out.println(lamda2+"  lamda2");
System.out.println(lamda+"  lamda");
System.out.println("/////////////////////////////////////////OUTPUT RECURSIVE_ARM/////////////////////////////////////////////////////////////////////////////");
////////////////////////////////////////////////////////////OUTPUT_RECURSIVE_ARM//////////////////////////////////////////////////////////////////////////////////////*/

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
        
        public double max (Matrix matrice){
            double max=matrice.get(0, 0);
            
            for(int z=0;z<matrice.getColumnDimension();z++)
                for(int r=0;r<matrice.getRowDimension();r++)
                    if(matrice.get(z, r)>max)
                        max= matrice.get(z, r);      
                        
                        
            return max;
        }
    
}
