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
public class online_sim_3_6_JF {
    
    public Matrix x_samp;
    public double R;
    public double M;
    public double phi;
    public int I;
    public int J;
    public double alpha;
    public double sigma1;
    public double sigma2;
    public double cluster;
    
    public online_sim_3_6_JF(Matrix x_samp,double R, double M,double phi, int I, int J, double alpha, double sigma1, double sigma2, double cluster){
        
        this.I=I;
        this.J=J;
        this.M=M;
        this.R=R;
        this.alpha=alpha;
        this.cluster=cluster;
        this.phi=phi;
        this.sigma1=sigma1;
        this.sigma2=sigma2;
        this.x_samp=x_samp;
        
    }
    
    public double onlinesim_3_6_JF(){
        
        
        double NP=1;
        double N_data=4;
        
        plsdata_730_R_12_withcluster_16_data plsR12wcluster = new plsdata_730_R_12_withcluster_16_data();
        
        
         if (cluster==1){
            plsR12wcluster.x_cluster=(plsR12wcluster.xcluster1);
            plsR12wcluster.y_cluster=(plsR12wcluster.ycluster1);
        }else if (cluster==2){
            plsR12wcluster.x_cluster=(plsR12wcluster.xcluster2);
            plsR12wcluster.y_cluster=(plsR12wcluster.ycluster2);
        } else if (cluster==3){
            plsR12wcluster.x_cluster=(plsR12wcluster.xcluster3);
            plsR12wcluster.y_cluster=(plsR12wcluster.ycluster3);
        } else if (cluster==4){
            plsR12wcluster.x_cluster=(plsR12wcluster.xcluster4);
            plsR12wcluster.y_cluster=(plsR12wcluster.ycluster4);
        } else if (cluster==5){
            plsR12wcluster.x_cluster=(plsR12wcluster.xcluster5);
            plsR12wcluster.y_cluster=(plsR12wcluster.ycluster5);
        }
        
        
    if (cluster==1){
            plsR12wcluster.thita_m_i=(plsR12wcluster.thita_mm1);
            plsR12wcluster.regression_coefficient=(plsR12wcluster.rc1);
    }else if (cluster==2){
            plsR12wcluster.thita_m_i=(plsR12wcluster.thita_mm2);
            plsR12wcluster.regression_coefficient=(plsR12wcluster.rc2);
    }else if (cluster==3){
            plsR12wcluster.thita_m_i=(plsR12wcluster.thita_mm3);
            plsR12wcluster.regression_coefficient=(plsR12wcluster.rc3);
    }else if (cluster==4){
            plsR12wcluster.thita_m_i=(plsR12wcluster.thita_mm4);
            plsR12wcluster.regression_coefficient=(plsR12wcluster.rc4);
    }else if (cluster==5){
            plsR12wcluster.thita_m_i=(plsR12wcluster.thita_mm5);
            plsR12wcluster.regression_coefficient=(plsR12wcluster.rc5);
    }
 
    Matrix XX = new Matrix (plsR12wcluster.x_cluster.getColumnDimension(),plsR12wcluster.x_cluster.getRowDimension());
    Matrix YY = new Matrix (plsR12wcluster.y_cluster.getColumnDimension(),plsR12wcluster.y_cluster.getRowDimension());
    XX=(plsR12wcluster.x_cluster);
    YY=(plsR12wcluster.y_cluster);    
    
     LW_PLS_JF lwpls = new LW_PLS_JF(XX,YY,x_samp,R,plsR12wcluster.phi,plsR12wcluster.I,plsR12wcluster.J,plsR12wcluster.alpha,plsR12wcluster.sigma1,sigma2,plsR12wcluster.thita_m_i, plsR12wcluster.regression_coefficient);
     double yp=lwpls.LWPLSJF();
     
        return yp;
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
