/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DIAS;

import org.coinor.Ipopt; 

/**
 *
 * @author zachariemaloney
 */
public class OptSolver extends Ipopt {
    
    /** Callback function for variable bounds and constraint sides. */
    protected boolean get_bounds_info(int n, double[] x_L, double[] x_U,
            int m, double[] g_L, double[] g_U)
    {
        return true; 
    }
    
    /** Callback function for the starting point. */
    protected boolean get_starting_point(int n, boolean init_x, double[] x,
            boolean init_z, double[] z_L, double[] z_U,
            int m, boolean init_lambda,double[] lambda)
    {
        return true; 
    }
    
    protected boolean eval_f(int n, double[] x, boolean new_x, double[] obj_value)
    {   
        return true;
    }
    
    protected boolean eval_grad_f(int n, double[] x, boolean new_x, double[] grad_f)
    {
        return true;
    }
    
    protected boolean eval_g(int n, double[] x, boolean new_x, int m, double[] g)
    {
        return true;
    }
    
    protected boolean eval_jac_g(int n, double[] x, boolean new_x,
            int m, int nele_jac, int[] iRow, int[] jCol, double[] values)
    {
        return true; 
    } 
    
    protected boolean eval_h(int n, double[] x, boolean new_x, double obj_factor, int m, double[] lambda, boolean new_lambda, int nele_hess, int[] iRow, int[] jCol, double[] values)
    { 
        return true; 
    } 
    
    public boolean get_scaling_parameters(double[] obj_scaling,
            int n, double[] x_scaling,
            int m, double[] g_scaling,
            boolean[] use_x_g_scaling)
    {
        return false;
    }
    
    public void print(double[] x, String str)
    {
        System.out.println(str);
        for( int i = 0; i < x.length; ++i )
            System.out.println(x[i]);
        System.out.println();
    }
}
