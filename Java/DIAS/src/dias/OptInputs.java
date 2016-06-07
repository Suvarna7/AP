/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import Jama.Matrix;
import de.xypron.jcobyla.CobylaExitStatus;

/**
 *
 * @author zachariemaloney
 */
public class OptInputs {
    
    private double Y; 
    private Matrix phi; 
    private Matrix Q; 
    private Matrix P; 
    private double err;
    private double lamda; 
    private double[] upperlimit; 
    private double[] lowerlimit; 
    private CobylaExitStatus exit;

    public OptInputs(double Y, Matrix phi, Matrix Q, Matrix P, double lamda, double err, double[] upperlimit, double[] lowerlimit, CobylaExitStatus res) {
        this.Y = Y;
        this.phi = phi;
        this.err = err;
        this.Q = Q;
        this.P = P;
        this.lamda = lamda;
        this.upperlimit = upperlimit;
        this.lowerlimit = lowerlimit;
        this.exit = res;
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
    public double err(){
        return err;
    }
    
    public CobylaExitStatus cobylaExit(){
        return exit;
    }
    
}
