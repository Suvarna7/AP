/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import Jama.Matrix;
import de.xypron.jcobyla.Calcfc;
import de.xypron.jcobyla.Cobyla;
import de.xypron.jcobyla.CobylaExitStatus;
import java.io.IOException;


/**
 *
 * @author User
 */
public class opt_recursive {
    
    public double Y;
    public Matrix phi;
    public Matrix Q_old;
    public Matrix P_old;
    public double lamda_old;
    public double [] upperlimit= new double [25];
    public double [] lowerlimit= new double [25];
    public double lamda;
    public double lamda1;
    public double lamda2;
    public double err= 0;
    public Matrix Y_model;
    public Matrix Q=new Matrix(24,1);
    public Matrix P;
    public Matrix pP;
    public double [] Q_oldtemp = new double [25];
    public double f;
    public double f1;
    public double [] xtemp = new double [25];
    public double [] xtemp2 = new double [25];
    
    //public GoalType min = GoalType.MINIMIZE;
    public double rhobeg = 1;
    public double rhoend = 1.0e-6;
    public int iprint = 0;
    public int maxfun = 5000;
    double temp2;
    public double [][] A_state= new double [21][21];
    public double c;
    public int s=0;
    public Matrix Qresult= new Matrix (25,1);
    public Matrix Q_res;
    public Matrix fresult= new Matrix (maxfun+1,1);
    public int g=0;
    
    
    
    public opt_recursive (double Y,Matrix phi,Matrix Q_old,Matrix P_old,double lamda_old,double [] upperlimit,double [] lowerlimit){
        this.Y=Y;
        this.P_old=P_old;
        this.lamda_old=lamda_old;
        this.Q_old=Q_old;
        this.phi=phi;
        this.upperlimit=upperlimit;
        this.lowerlimit=lowerlimit;
    }
    
    public void optrecursive () {
        
        /////////////////////////////INPUTS OPT_RECURSIVE//////////////////////////////////////////////////////////////////////////////////////////////////////
        System.out.println("///////////////////INPUTS OPT_RECURSIVE///////////////////////////////////////////////////////////////////////////////////////////");
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
        
        System.out.println();
        System.out.println("///////////////////INPUTS OPT_RECURSIVE///////////////////////////////////////////////////////////////////////////////////////////");
        /////////////////////////////INPUTS OPT_RECURSIVE//////////////////////////////////////////////////////////////////////////////////////////////////////

        
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

  f=0;
  f1=0;
  
   Cobyla cobyla = new Cobyla();
		Calcfc calcfc = new Calcfc() {
     
                        @Override
			public double compute(int n, int m, double[] x,	double[] con) {
                        
                             double cx=constraints(x);
                          //   System.out.println(cx+"    cx");
                               
                     /*        for(int i=0;i<Q_old.getRowDimension();i++){ 
                                 con[2*i]=x[i]-lowerlimit[i];
                                 con[2*i+1]=-x[i]+upperlimit[i];  
                               }   
                           con[2*Q_old.getRowDimension()]=(cx);  */
                              
                          /*   System.out.println("Con");
                              for(int i=0;i<con.length;i++)
                                  System.out.print(con[i]+"    ");
                              System.out.println();*/
                            
                              Matrix differencematrix = new Matrix (Q_old.getRowDimension(),1);
                            
                              for(int s=0;s<Q_old.getRowDimension();s++)
                              differencematrix.set(s,0,(x[s]-Q_old.get(s, 0)));

                              f=(((differencematrix.transpose()).times(pP)).times(differencematrix)).get(0, 0);
                              
                             double differencematrix2 = 0;
                             double temp=0;
          
                             for(int a=0;a<x.length;a++)
                                 temp=phi.get(a, 0)*x[a]+temp;
                             
                             differencematrix2=temp;
                                  
                                  f1=(Y-differencematrix2)*(Y-differencematrix2);
 
                           double tempd=0;       
                                  
                         for(int i=0;i<Q_old.getRowDimension();i++) {  
                         Qresult.set(i, 0, x[i]);
                         }
                           g++;
                          fresult.set(g, 0, f+f1);
                           return (f+f1);
                           
			}  
                };
		CobylaExitStatus result1;
                
                
                 result1 = cobyla.findMinimum(calcfc,24, 0, Q_oldtemp, rhobeg, rhoend, iprint, maxfun); 
	      //  result1 = cobyla.findMinimum(calcfc, 24,2*Q_old.getRowDimension()+1, Q_oldtemp, rhobeg, rhoend, iprint, maxfun);  
               Save save1 = new Save("testnonlier");
               try{
               save1.save(fresult, "testnonlier");
               }catch(IOException e)
               {
                   System.out.print("Opt recursive: "+e);
               }
                
Q_res =new Matrix(24,1);

for(int i=0;i<24;i++)
Q_res.set(i, 0, Qresult.get(i, 0));

err=Y-(phi.transpose().times(Q_res)).get(0, 0);

Y_model=(phi.transpose().times(Q_res));

lamda1=0.9*lamda_old+(1-0.9)*0.99;

lamda2=Math.exp(-(err*err)/(1000));

lamda=lamda1*lamda2;

if (lamda<0.005)
lamda=0.005;     

////////////////////////////////////////////////////////////OUTPUT OPT_RECURSIVE//////////////////////////////////////////////////////////////////////////////////
System.out.println("/////////////////////////////////////////OUTPUT OPT_RECURSIVE/////////////////////////////////////////////////////////////////////////////");
printMatrix(Q_res,"Q_res");
System.out.println(err+"  err");
printMatrix(Y_model,"  Y_model");
System.out.println(lamda1+"  lamda1");
System.out.println(lamda2+"  lamda2");
System.out.println(lamda+"  lamda");
printMatrix(P,"P");
printMatrix(phi,"phi");
System.out.println("/////////////////////////////////////////OUTPUT OPT_RECURSIVE/////////////////////////////////////////////////////////////////////////////");
/////////////////////////////////////////////////////////////OUTPUT_RECURSIVE////////////////////////////////////////////////////////////////////////////////////
    }
    
    
    public double constraints (double [] x){
        
        double c=0;
        
                                A_state[0][0]=-x[0];
                                A_state[0][1]=-x[1];
                                A_state[0][2]=-x[2];
                                
                                A_state[0][3]=x[4];
                                A_state[0][4]=x[5];
                                A_state[0][5]=x[6];
                                A_state[0][6]=x[7];
                                A_state[0][7]=x[8];
                                A_state[0][8]=x[9];
                                A_state[0][9]=x[10];
                                A_state[0][10]=x[11];
                                A_state[0][11]=x[12];
                                A_state[0][12]=x[13];
                                A_state[0][13]=x[14];
                                
                                A_state[0][14]=x[16];
                                A_state[0][15]=x[17];
                                A_state[0][16]=x[18];
                                
                                 A_state[0][17]=x[20];
                                 A_state[0][18]=x[21];
                                 A_state[0][19]=x[22];
                                 
                                 A_state[0][20]=x[23];
                                 
                                 A_state[1][0]=1;
                                 A_state[2][1]=1;
                                 A_state[4][3]=1;
                                 A_state[5][4]=1;
                                 A_state[6][5]=1;
                                 A_state[7][6]=1;
                                 A_state[8][7]=1;
                                 A_state[9][8]=1;
                                 A_state[10][9]=1;
                                 A_state[11][10]=1;
                                 A_state[12][11]=1;
                                 A_state[13][12]=1;
                                 
                                 A_state[15][14]=1;
                                 A_state[16][15]=1;
                                  
                                 A_state[18][17]=1;
                                 A_state[19][18]=1;
                                 
                                 for(int z=1;z<21;z++)
                                     for(int r=0;r<21;r++)
                                     if(A_state[z][r]!=1)
                                         A_state[z][r]=0;
                                 
                                Matrix Astate= new Matrix(A_state);
                                Matrix Astatetemp= new Matrix(21,1);

                                
                                for(int z=0;z<21;z++)
                                Astatetemp.set(z,0,Math.abs(Astate.eig().getD().get(z,z)));
                                
                              //  printMatrix(Astatetemp,"Astatetemp");
                                
                                c=(max(Astatetemp)-0.99);
        
                               return c;
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
            
            for(int z=0;z<matrice.getRowDimension();z++)
                for(int r=0;r<matrice.getColumnDimension();r++)
                    if(matrice.get(z, r)>max)
                        max= matrice.get(z, r);      
                        
                        
            return max;
        }
    
}
