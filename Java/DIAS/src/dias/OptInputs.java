/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import Jama.Matrix;

/**
 *
 * @author zachariemaloney
 */
public class OptInputs {
    
    private double Y; 
    private Matrix phi; 
    private Matrix Q; 
    private Matrix P; 
    private double lamda; 
    private double[] upperlimit; 
    private double[] lowerlimit; 

    public OptInputs(double Y, Matrix phi, Matrix Q, Matrix P, double lamda, double[] upperlimit, double[] lowerlimit) {
        this.Y = Y;
        this.phi = phi;
        this.Q = Q;
        this.P = P;
        this.lamda = lamda;
        this.upperlimit = upperlimit;
        this.lowerlimit = lowerlimit;
    }

    public double y() { 
        return Y; 
    }

    public Matrix phi() {
        return phi;
    }

    public Matrix q() {
        return Q;
    }

    public Matrix p() {
        return P;
    }

    public double lamda() {
        return lamda;
    }

    public double[] upperLimit() {
        return upperlimit;
    }


    public double[] lowerLimit() {
        return lowerlimit;
    }
    
}
