package dias;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import de.xypron.jcobyla.Calcfc;
import de.xypron.jcobyla.Cobyla;
import de.xypron.jcobyla.CobylaExitStatus;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;

/**
 * OptRecursive class performs a recursive optimization of the given X input
 * Non-linear optimization with non-linear constrains libraries: jCobula and
 * Jama
 *
 * @author Mert Sevil & Caterina Lazaro
 */
public class OptRecursive_JCOBYLA implements Optimizer{
    

    //EIGEN VALUE CONSTRAINT
    private static final double EIGEN_CONSTRAIN_VALUE = 0.9999;

    //**********************************************
    //Parameters of the Cobyla optimization
    //TODO precission of the cosntraints around zero: _RHO_END< constraing < RHO_BEG
    //TrustRegionRadiusStart : (default = 0.5)
    //MATLAB tolerance: TolX
    //We were using RHO_BEG= 1;
    //DO NOT USE RHO_bEG > 6
    //MATLAB RHO_BEG = 1.49011611938477e-08
    // **** NOTE - RHO_BEG is used in the first N_VARIABLES evaluation of the fuction:
    //      - First: evaluate Q0
    //      - Then: add RHO_BEG to each single sample
    //private final double _RHO_BEG = 1e-6;
    private double _RHO_BEG = 1.0e-4;

    //TrustRegionRadiusEnd  : (default = 1.0e-6)
    //We were using default RHO_END = 1.0e-4
    //MATLAB - RHO_END
    //private final double _RHO_END = 1.49011611938477e-08;
    private double _RHO_END = 1.49011611938477e-8;

    //TODO Max_function - max recursive loop
    private int MAX_FUNC = 5000;

    private final int iprint = 1;
    public final static int N_VARIABLES = 24;
    private final static int M_CONSTRAINTS = 1;

    //******************************************
    //Results of the optimization
    //Inputs
    private Matrix Q_old;
    private Matrix pP;
    private double Y;
    private Matrix phi;
    private double[] lower_limit;
    private double[] upper_limit;
    
    //Intemediate results
   
    //Result of the optimization
    //public Matrix Q = new Matrix(_Q_SIZE, 1);
    private final Matrix Q_optimizing_keepValue = new Matrix(OptRecursive.Q_SIZE, 1);
    public Matrix Q_res;
    public Matrix fresult;
    public Matrix Y_model;

    //DEBUG FUNCTIONS !!!!
    public List<Double[]> Q_values;
    public List<Double[]> constraintValues;
    public double iterations;

    /**
     * Create the opt_recursive object with the given parameters
     *
     * @param inputs
     */
    public OptRecursive_JCOBYLA(OptInputs inputs) {
       
        //TODO DEBUG VALUES:
        /*this.upperlimit = new double[]{2, 2, 2, 2, 2, 
                                       2, 2, 2, 2, 2,
                                       2, 2, 2, 2, 2, 
                                       2, 2, 2, 2, 2,
                                       2, 2, 2, 2};
        this.lowerlimit = new double[] {-2, -2, -2, -2, -2, 
                                       -2, -2, -2, -2, -2,
                                       -2, -2, -2, -2, -2, 
                                       -2, -2, -2, -2, -2,
                                       -2, -2, -2, -2};*/
        //Qintermediate =  
        fresult = new Matrix(1, 1);
        constraintValues = new ArrayList<Double[]>();

        Q_values = new ArrayList<Double[]>();
        iterations = 0;
    }

    /**
     * Update optimization main parameters
     *
     * @param Q_old X0
     * @param P_old P0
     * @param rho_beg Rho_begin
     * @param rho_end Rho_end
     * @param max max_iteration_function
     */
    /*public void updateParameters(Matrix Q_old, Matrix P_old, double rho_beg, double rho_end, int max) {
        this.Q_old = Q_old;
        //Update its double array version:
        double[][] previousQold = Q_old.getArray();
        Q_old_ARRAY = new double[OptQ_SIZE];
        for (int i = 0; i < previousQold.length; i++) {
            Q_old_ARRAY[i] = previousQold[i][0];
        }
        this.P_old = P_old;
        _RHO_BEG = rho_beg;
        _RHO_END = rho_end;
        MAX_FUNC = max;
        //Build the P matrix and its pseudo-inverse (pP) from phi and P_old
        //P=(1/(lamda_old))*(P_old-(P_old*phi*pinv(lamda_old+phi'*P_old*phi)*phi'*P_old))
        //Intermediate result: partialResult = pinv(lamda_old+phi'*P_old*phi)
        double partialResult = 1 / ((((phi.transpose()).times(P_old)).times(phi)).get(0, 0) + lamda_old);
        P = (P_old.minus(P_old.times(phi).times(partialResult).times(phi.transpose()).times(P_old))).times(1 / lamda_old);
        //pP is the pseudo-inverse of P
        //Returns the seudoinvers of P only if P is not square
        pP = P.inverse();
    }*/

    @Override
    public OptInputs runOptimization(Matrix Q_old, Matrix P, Matrix pP, double Y, Matrix phi, double lambda_old) {
       
        //Update local parameters to be used in the optimization
        this.Q_old = Q_old;
        this.pP = pP;
        this.Y = Y;
        this.phi =  phi;

        //Obtain array from Q_old to use in jCobyla
        double[][] previousQold = Q_old.getArray();
        
        double [] Q_old_ARRAY = new double[N_VARIABLES];
        for (int i = 0; i < previousQold.length; i++) {
            Q_old_ARRAY[i] = previousQold[i][0];
            if (DIAS.verboseMode) {
                System.out.print(i + ", ");
            }
        }
        if (DIAS.verboseMode) {
            System.out.println("Size Qold " + Q_old_ARRAY.length + " - ");
        }
        //Run the optimization
        CobylaExitStatus result = Cobyla.findMinimum(calcfc, N_VARIABLES, M_CONSTRAINTS, Q_old_ARRAY, _RHO_BEG, _RHO_END, iprint, MAX_FUNC);
        //  result1 = cobyla.findMinimum(calcfc, 24,2*Q_old.getRowDimension()+1, Q_oldtemp, rhobeg, rhoend, iprint, maxfun);  

        //Exit status: DIVERGING ROUNDING ERRORS / MAX ITERATION REACH / NORMAL
        if (DIAS.verboseMode) {
            System.out.println("COBYLA EXIT: " + result);
        }
        /**
         * *********************************************
         * Compute the results of Cobyla optimization
         */

        Q_res = new Matrix(OptRecursive.Q_SIZE, 1);
        //RECOVER Q value:
        //Q_res = Q_optimizing = Q
        for (int i = 0; i < OptRecursive.Q_SIZE; i++) {
            Q_res.set(i, 0, Q_optimizing_keepValue.get(i, 0));
        }
        
        double err = Y - (phi.transpose().times(Q_res)).get(0, 0);
        Matrix Y_model = (phi.transpose().times(Q_res));
        double lambda1 = 0.9 * lambda_old + (1 - 0.9) * 0.99;
        double lambda2 = Math.exp(-(err * err) / (1000));
        double lambda = lambda1 * lambda2;

        if (lambda < 0.005) {
            lambda = 0.005;
        }

        //Return the values normally printed out if debug mode is on.
        // XXX DEBUG -- there are quite a few variables with similar names. 
        // Not sure that this is the correct set of variables to return. 
        OptInputs inputs = new OptInputs(this.Y, this.phi, this.Q_res, P, lambda, err, this.upper_limit, this.lower_limit, result);
        return inputs;

    }
    
    Calcfc calcfc = new Calcfc() {
            //Init with compute function
            //TODO use con for constrains ????
            @Override
            /**
             * Compute: n - number of samples m - number of constrains Q - X
             * value for f(x) con - constrains . con[i]>= 0 for all i
             */
            public double compute(int n, int m, double[] Q, double[] con) {
                //1. Get constraints for Q
                //Returns: (max(Astatetemp) - 0.99)
                //Cobyla constraint: con[0] >= 0
                //System.out.println("Con before :" +con[0]);
                //con[0] = -10;
                //TODO NOTE - COBYLA constraint behaviour:
                // con[0] + RHO_END >= 0

                con[0] = getConstraintValue(Q);
                //con[1] = getConstraintValue(Q);

                // System.out.println("Con afterwards: "+con[0]);

                /*System.out.println("Constraints: "+ con[0]);
                 con[1]= 3;*/
                //con[0] = -1;
                //2. Upper and lower limits
                /*double[] limits = limitsConstraint(Q);
                for (int i = 0; i < m-1; i ++)
                    con[i+1] = limits[i];
                //2. Set the function to optimize - V*/
                double opt = optimizationFunctionV(Q);
                double[] Q_V = new double[N_VARIABLES + 2];
                Q_V[0] = iterations;
                Q_V[1] = opt;
                for (int i = 2; i < N_VARIABLES + 2; i++) {
                    Q_V[i] = Q[i - 2];
                }
                Double[] doubleArray = ArrayUtils.toObject(Q_V);
                Q_values.add(doubleArray);
                iterations++;

                //System.out.println("V: "+opt );
                //printDoubleArrayMatrix(  new double[][] {Q}, "Q");
                return opt;
            }
        };

    

    /**
     * Save initial variables of OPT_RECURSIVE optimization
     */
    public void savejCobylaResults() {
        //TODO Debug - Save all matrices in an excel file, to use later on in MATLAB
        try {
            Save saveManager = new Save(OptRecursive.outputOpt);

            //Save intermediate results of the optimization
            Matrix Q_valuesMatrix = new Matrix(Q_values.size(), N_VARIABLES + 2);
            for (int i = 0; i < iterations; i++) {
                for (int j = 0; j < N_VARIABLES + 2; j++) {
                    Q_valuesMatrix.set(i, j, Q_values.get(i)[j]);
                }
            }

            saveManager.save(Q_valuesMatrix, "Optimization_Steps_Cons2");
            //Save the result of the constraint
            Matrix Cons_valuesMatrix = new Matrix(constraintValues.size(), N_VARIABLES + 1);
            for (int i = 0; i < iterations; i++) {
                for (int j = 0; j < N_VARIABLES + 1; j++) {
                    Cons_valuesMatrix.set(i, j, constraintValues.get(i)[j]);
                }
            }
//            saveManager.save(Cons_valuesMatrix, "Constraint_Steps_Cons");

            //Save the Q result
            saveManager.save(Q_res, "Q_res");
        } catch (Exception e) {
            System.out.println("Error saving inputOut: " + e);
        }
    }

    /**
     * Generates the constraints parameters for x = Q We will be optimizing A,
     * so that all of its eigen values are < 1
     * We return the 0.99 - maxEigen so the algorithm can check if it is >=0
     *
     * @param x input Q
     * @return (0.99 - maxEigen)
     */
    public double getConstraintValue(double[] x) {
        //Create the double list with:
        // - [0,23]: input x
        // - [24]: constraint result (1 or -1)
        double[] consInter = new double[N_VARIABLES + 1];
        //Store X value:
        System.arraycopy(x, 0, consInter, 0, x.length);
        //double [][] A_state = new double[21][21];
        double[][] A_state = new double[OptRecursive.Q_SIZE - 2][OptRecursive.Q_SIZE - 2];

        //Generate A_state
        //A --> -x
        A_state[0][0] = -x[0];
        A_state[0][1] = -x[1];
        A_state[0][2] = -x[2];
        //B1 --> x
        A_state[0][3] = x[4];
        A_state[0][4] = x[5];
        A_state[0][5] = x[6];
        A_state[0][6] = x[7];
        A_state[0][7] = x[8];
        A_state[0][8] = x[9];
        A_state[0][9] = x[10];
        A_state[0][10] = x[11];
        A_state[0][11] = x[12];
        A_state[0][12] = x[13];
        A_state[0][13] = x[14];
        //B2 --> x
        A_state[0][14] = x[16];
        A_state[0][15] = x[17];
        A_state[0][16] = x[18];
        A_state[0][17] = x[19];
        //B3 --> X
        A_state[0][18] = x[20];
        A_state[0][19] = x[21];
        A_state[0][20] = x[22];
        //C --> X
        A_state[0][21] = x[23];

        //Diagontal 1 line:
        A_state[1][0] = 1;
        A_state[2][1] = 1;
        A_state[4][3] = 1;
        A_state[5][4] = 1;
        A_state[6][5] = 1;
        A_state[7][6] = 1;
        A_state[8][7] = 1;
        A_state[9][8] = 1;
        A_state[10][9] = 1;
        A_state[11][10] = 1;
        A_state[12][11] = 1;
        A_state[13][12] = 1;
        A_state[15][14] = 1;
        A_state[16][15] = 1;
        A_state[18][17] = 1;
        A_state[19][18] = 1;
        //Last row x[20][j] is kept to zero

        //TODO THIS IS DISCOMMENTED
        //A_state only has 0 or 1 values. Any other value is turn to 0
        //??????????????????????????????????/
        //TODO A_State
        //printDoubleArrayMatrix(A_state, "A_s");
        //Check if the eigen values are right:
        //Matrix to use in this operations AstateModify
        Matrix AstateModify = new Matrix(A_state);
        Matrix AstatetEigen = new Matrix(21, 1);

        EigenvalueDecomposition eigenDec = new EigenvalueDecomposition(AstateModify);
        //Matrix vDec = eigenDec.getD();
        Matrix eigenReal = new Matrix(new double[][]{eigenDec.getRealEigenvalues()});
        Matrix eigenIm = new Matrix(new double[][]{eigenDec.getImagEigenvalues()});

        //Check if eigen values are valid
        for (int z = 0; z < 21; z++) {
            //Use absolute values of eigen values
            //A = V D V^T.
            //AstatetEigen.set(z, 0, Math.abs(AstateModify.eig().getD().get(z, z)));
            AstatetEigen.set(z, 0, Math.abs(Math.sqrt(Math.pow(eigenReal.get(0, z), 2)
                    + Math.pow(eigenIm.get(0, z), 2))));
            //AstatetEigen.print(9, 6);
        }

        //printMatrix(AstatetEigen, "Eigen matrix: ");
        //printDoubleArrayMatrix(A_state, "A_state matrix: ");
        //printDoubleArrayMatrix(new double[][]{x}, "Q state matrix:");
        //  printMatrix(Astatetemp,"Astatetemp");
        //double c = (max(Astatetemp) - 0.99);
        //We want: max(AstateEigen) -0.99 <= 0
        double resultC = (EIGEN_CONSTRAIN_VALUE - max(AstatetEigen));
        //double resultC = -1;
        //Save values of this step:
        consInter[24] = resultC;
        constraintValues.add(ArrayUtils.toObject(consInter));
        return resultC;
    }

    /**
     * Method to emulate the effecto of upper and lower bounds as constraints
     *
     * @param X
     * @return double[] array with the result of X samples vs bounds
     */

    private double[] limitsConstraint(double[] X) {
        double[] limitsResult = new double[N_VARIABLES];
        for (int i = 0; i < X.length; i++) {
            if (X[i] <= upper_limit[i] && X[i] >= lower_limit[i]) {
                //Normal behaviour - 
                limitsResult[i] = 1;
            } else {
                limitsResult[i] = -1;
            }
        }
        return limitsResult;
    }

    /**
     * Returns the values of the optimization function V
     *
     * @param Q input value
     * @return V(X)
     */
    public double optimizationFunctionV(double[] Q) {
        // V = (Q- Qold)'*(pseudo-inv(P)*(Q-Q_old) + (Y-phi'*Q)'*(Y-phi'*Q);
        //Update other variables: f, f1 /V= f + fi
        //differenceMatrix: Q - Qold
        //phiQ = phi'*Q
        Matrix differenceMatrix;
        differenceMatrix = new Matrix(Q_old.getRowDimension(), 1);
        for (int s1 = 0; s1 < Q_old.getRowDimension(); s1++) {
            differenceMatrix.set(s1, 0, Q[s1] - Q_old.get(s1, 0));
        }
        //First part of V. f = (Q - Qold)'*pseudoinvP*(Q-Qold)
        double f = (((differenceMatrix.transpose()).times(pP)).times(differenceMatrix)).get(0, 0);
        //Middle term - temp = phi'*Q
        double phiQ = 0;
        //Do the multiplication manually:
        for (int a = 0; a < Q.length; a++) {
            phiQ = phi.get(a, 0) * Q[a] + phiQ;
        }
        //System.out.println("PhiQ: "+ phiQ);
        //Partial function to optimize =(Y-phi'*Q)'*(Y-phi'*Q)
        double f1 = (Y - phiQ) * (Y - phiQ);
        //SAVE LOCAL VALUE OF Q:
        //Q_optimizing = Q
        for (int i = 0; i < Q.length; i++) {
            Q_optimizing_keepValue.set(i, 0, Q[i]);
        }
        //DEBUG - result matrix to save
        fresult = new Matrix(fresult.getRowDimension() + 1, 1);
        fresult.set(fresult.getRowDimension() - 1, 0, f + f1);

        //REAL: 
        //return (f + f1);
        //TODO DEBUG: 
        return (f + f1);
        /*} else {
                return 0;
            }*/
    }

    /**
     * printMatrix() - auxiliar function for debugging purposes Prints the given
     * matrix on the console
     *
     * @param m matrix
     * @param name
     */
    public static void printMatrix(Matrix m, String name) {
        if (m != null){
            System.out.print("\n " + name + ": \n{");
            for (double[] row : m.getArray()) {
                for (double val : row) {
                    System.out.print("\t " + val);
                }
                System.out.println();
            }
            System.out.println("}");
        }
    }

    /**
     * max() - obtains the maximum value of the matrix
     *
     * @param matrix
     * @return maximum
     */
    public double max(Matrix matrix) {
        //Init with the first value
        double max = matrix.get(0, 0);
        //Iterate thru values
        for (double[] row : matrix.getArray()) {
            for (double val : row) {
                //If it is greater than the previous, update max
                if (val > max) {
                    max = val;
                }
            }
        }
        return max;
    }

    /**
     * Function to print a double[][] kind of matrix on terminal
     *
     * @param printing
     */
    private void printDoubleArrayMatrix(double[][] printing, String matrix_name) {
        System.out.println(matrix_name);
        for (double[] column : printing) {
            for (double val : column) {
                System.out.print(val + ", ");
            }
            System.out.print("\n");
        }
    }

    

    /**
     * ***********************************************************************
     * **********************************************************************
     * APACHE OPTIMIZATION:
     *
     * http://stackoverflow.com/questions/16950115/apache-commons-optimization-troubles
     * double[] point = {1.,2.}; double[] cost = {3., 2.}; MultivariateFunction
     * function = new MultivariateFunction() { public double value(double[]
     * point) { double x = point[0]; double y = point[1]; return x * y; } };
     *
     *
     * MultivariateOptimizer optimize = new BOBYQAOptimizer(5);
     * optimize.optimize( new MaxEval(200), GoalType.MAXIMIZE, new
     * InitialGuess(point), new ObjectiveFunction(function), new
     * LinearConstraint(cost, Relationship.EQ, 30));
     *
     *
     * **********************************************************************
     */
    
    private final double eps = 2.2204e-16;
    private final double v_seed = Math.sqrt(eps); 
    
    private int sign(Double input) { 
        return (input < 0 ? -1 : 1); 
    } 
    
    private Matrix sign(Matrix input) { 
        Matrix r = DIAS.createnewMatrix(input.getRowDimension(), input.getColumnDimension(), input); 
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
    private Matrix forwardFiniteDiffStepSize(Matrix x, Matrix typicalX) { 
        return sign(x).times(DIAS.maxMatrix(DIAS.absMatrix(x), typicalX)).times(v_seed); 
    }
    
}
