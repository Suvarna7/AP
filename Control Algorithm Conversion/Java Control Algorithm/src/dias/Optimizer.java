/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import Jama.Matrix;

/**
 * Optimizer interface for the OptRecursive optimizers
 * @author Cat
 */
public interface Optimizer {
    
    
    public OptInputs runOptimization(Matrix Q_old, Matrix P, Matrix pP, double Y, Matrix phi, double lambda_old);
    
    
}
