
package javaapplication1;

import Jama.Matrix;


/**
 * StepSizeCalculator - generates step size within an optimization, emulating 
 * MATLAB's behavior
 * @author Cat
 */
public class StepSizeCalculator {
    
    private static final double eps = 2.2204e-16;
    private static final double v_seed = Math.sqrt(eps); 
    
    private static int sign(Double input) { 
        return (input < 0 ? -1 : 1); 
    } 
    
    private static Matrix sign(Matrix input) { 
        Matrix r = createnewMatrix(input.getRowDimension(), input.getColumnDimension(), input); 
        for (int i = 0; i < r.getRowDimension(); i++) {
            for (int j = 0; j < r.getColumnDimension(); j++) { 
                r.set(i, j, sign(r.get(i, j)));  
            }
	}
        return r; 
    }
    
    /** 
     * 
     * Scalar or vector step size factor for finite differences.
     * 
     * Per http://www.mathworks.com/help/optim/ug/optimization-options-reference.html :  
     * Scalar or vector step size factor for finite differences. 
     * When you set FiniteDifferenceStepSize to a vector v, forward finite differences steps delta are
delta = v.*sign′(x).*max(abs(x),TypicalX);
    * where sign′(x) = sign(x) except sign′(0) = 1. 
    * Central finite differences are delta = v.*max(abs(x),TypicalX);
    * Scalar FiniteDifferenceStepSize expands to a vector. The default is sqrt(eps) for forward finite differences, and eps^(1/3) for central finite differences.
     * 
     * In other words, we're setting v to the default for FiniteDifferenceStepSize (sqrt(eps)) and then calculating delta based on it and the x (in the case of OptRecursive, x = Q). 
     * 
     * @param x
     * @param typicalX See http://www.mathworks.com/matlabcentral/answers/101930-what-is-the-typicalx-parameter-in-the-optimization-toolbox -- this is a 1eZ value, where the value is roughly on the scale of the solution we expect to get for X. 
     * @return 
     */
    private static Matrix forwardFiniteDiffStepSize(Matrix x, Matrix typicalX) { 
//        Matrix foo = DIAS.maxMatrix(DIAS.absMatrix(x), typicalX); 
//        DIAS.printMatrix(DIAS.absMatrix(x), "absMatrix(x)");
//        DIAS.printMatrix(typicalX, "typicalX");
//        DIAS.printMatrix(foo, "DIAS.maxMatrix(DIAS.absMatrix(x), typicalX)");
//        DIAS.printMatrix(x, "x");
//        DIAS.printMatrix(sign(x), "sign(x)");
        Matrix tmp = sign(x).arrayTimes(maxMatrix(absMatrix(x), typicalX)); 
        return tmp.times(v_seed); 
    }
    
    //////////////////////////////Matrix Handling functions for the Program/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * createNewMatrix - creates a new 2D matrix from an old matrix. 
     * Increases the size
     * 
     * Typically used to duplicate a matrix as : 
     * Matrix newMatrix = DIAS.createnewMatrix(oldMatrix.getRowDimension(), oldMatrix.getColumnDimension(), oldMatrix); 
     * @param newdimensionx
     * @param newdimensiony
     * @param oldMatrix
     * @return 
     */
    public static Matrix createnewMatrix(int newdimensionx, int newdimensiony, Matrix oldMatrix) {
        Matrix newMatrix = new Matrix(newdimensionx, newdimensiony);

        for (int i = 0; i < oldMatrix.getRowDimension(); i++) {
            for (int j = 0; j < oldMatrix.getColumnDimension(); j++) {
                newMatrix.set(i, j, oldMatrix.get(i, j));
            }
        }

        return newMatrix;
    }
    
     public static Matrix absMatrix(Matrix m1) { 
        Matrix r = new Matrix(m1.getRowDimension(), m1.getColumnDimension());
        for (int i = 0; i < m1.getRowDimension(); i++) {
            for (int j = 0; j < m1.getColumnDimension(); j++) {
                Double curValue = m1.get(i, j); 
                r.set(i, j, (curValue < 0 ? -curValue : curValue)); 
            }
        }
        return r; 
    } 
     
      //XXX OPTIMIZE : Make sure that the matrices are the same dimensions. 
    /**
     * maxMatrix : takes two matrices of the same size and returns a matrix filled with the value from m1 or m2, whichever is greater. 
     * 
     * @param m1
     * @param m2
     * @return 
     */
    public static Matrix maxMatrix(Matrix m1, Matrix m2) { 
        Matrix r = new Matrix(m1.getRowDimension(), m1.getColumnDimension()); 
        for (int i = 0; i < m1.getRowDimension(); i++) {
            for (int j = 0; j < m1.getColumnDimension(); j++) {
                r.set(i, j, Math.max(m1.get(i, j), m2.get(i, j))); 
            }
        }
        return r; 
    } 
    
    
}
