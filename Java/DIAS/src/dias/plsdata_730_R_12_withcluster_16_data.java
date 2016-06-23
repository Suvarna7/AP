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
public class plsdata_730_R_12_withcluster_16_data {
    
    public static double phi=0.5;
    public static double M=12;
    public static double L=1;
    public static Matrix CGM;
    public static double gbchange_m =-0.0352;
    public static Matrix xcluster1 = new Matrix(1899,37);
    public static Matrix xcluster2 = new Matrix(1867,37);
    public static Matrix xcluster3 = new Matrix(1843,37);
    public static Matrix xcluster4 = new Matrix(1876,37);
    public static Matrix xcluster5 = new Matrix(2238,37);
    public static Matrix ycluster1 = new Matrix(1899,1);
    public static Matrix ycluster2 = new Matrix(1865,1);;
    public static Matrix ycluster3 = new Matrix(1844,1);;
    public static Matrix ycluster4 = new Matrix(1876,1);;
    public static Matrix ycluster5 = new Matrix(2238,1);;
    public static double run=0;
    public static int I=30;
    public static int J=2;
    public static double sigma1=0.01;
    public static double N_data=16;
    public static double NP=1;
    public static double numberofcluster=5;
    public static double gb_m=149.9568;
    public static double gb_sd=54.3790;
    public static double ins_m=1.7679;
    public static double ins_sd=4.1588;
    public static double gb_change_sd=4.9719;
    public static double R=1;
    public static double alpha=0.10;
    public static double N_old=97.33;
    public static double data_load=16;
    public static Matrix data_CGM;
    public static Matrix data_ins;
    public static Matrix gb;
    public static Matrix CGM_change;
    public static double N=508;
    public static double l=508;
    public static Matrix x;
    public static Matrix y;
    public static Matrix X_cross;
    public static Matrix Y_cross;
    public static Matrix T;
    public static Matrix x_cluster = new Matrix(2238,37);
    public static Matrix y_cluster= new Matrix(2237,1);
    public static double i=5;
    public static Matrix thita_m_i = new Matrix(1,37);
    public static Matrix regression_coefficient = new Matrix(37,2237);
    public static Matrix y_offline;
    public static Matrix y_p1;
    public static Matrix thita_mm1= new Matrix(1,37);
    public static Matrix rc1 = new Matrix(37,1899);
    public static Matrix y_p2;
    public static Matrix thita_mm2 =new Matrix(1,37);
    public static Matrix rc2 = new Matrix(37,1865);
    public static Matrix y_p3;
    public static Matrix thita_mm3 = new Matrix(1,37);
    public static Matrix rc3 = new Matrix(37,1844);
    public static Matrix y_p4;
    public static Matrix thita_mm4 = new Matrix(1,37);
    public static Matrix rc4 = new Matrix(37,1876);
    public static Matrix y_p5;
    public static Matrix thita_mm5 = new Matrix(1,37);
    public static Matrix rc5 = new Matrix(37,2237);
}
