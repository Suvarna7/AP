package optrecursive_testbench;

import Jama.Matrix;
import Jama.EigenvalueDecomposition;
import de.xypron.jcobyla.Calcfc;
import de.xypron.jcobyla.Cobyla;
import de.xypron.jcobyla.CobylaExitStatus;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

/**
 * OptRecursive class performs a recursive optimization of the given X input
 * Non-linear optimization with non-linear constrains libraries: jCobula and
 * Jama
 *
 * @author Mert Sevil & Caterina Lazaro
 */
public class OptRecursive_Cons {

    //TODO Output file
    private final String outputOpt = "outputOpt";
    //Size of Q matrix
    private static final int Q_SIZE = 24;
    private static final int Q_INIT_DISPLACEMENT = 3;

    //Variables for the optimization
    //INPUTS:
    public double Y;
    public Matrix phi;
    public Matrix Q_old;
    public double[] Q_old_ARRAY;
    public Matrix P_old;
    public double lamda_old;
    public double[] upperlimit = new double[Q_SIZE  + 1];
    public double[] lowerlimit = new double[Q_SIZE + 1];
    //Paremeters matrix P
    public Matrix P;
    //And its pseudoInverse
    private Matrix pP;

    //EIGEN VALUE CONSTRAINT
    private static final double EIGEN_CONSTRAIN_VALUE = 0.9999;

    /* *********************************************
    //Parameters of the Cobyla optimization
    ***********************************************/
    //Adjust : _RHO_END< step_size < RHO_BEG
    //TrustRegionRadiusStart : (default = 0.5)
    //MATLAB RHO_BEG = 1.49011611938477e-08
    // **** NOTE - RHO_BEG is used in the first N_VARIABLES evaluation of the fuction:
    //      - First: evaluate Q0
    //      - Then: add RHO_BEG to each single sample
    //private final double _RHO_BEG = 1e-6;
    private  double _RHO_BEG = 1.0e-3;
    public final static  double _RHO_BEG_DEF = 1.0e-3;


    //TrustRegionRadiusEnd  : (default = 1.0e-6)
    //We were using default RHO_END = 1.0e-4    
    //Step tolerance - {MATLAB TolX = 1.e-10}
    private  double _RHO_END = 1.0e-16;
    public final static  double _RHO_END_DEF = 1.0e-16;

    
    //TODO Max_function - max recursive loop
    private int MAX_FUNC = 50000;
    public static final int MAX_FUNC_DEF = 50000;

    //iprint = {0, 1, 2, 3}
    private final int iprint = 1;
    private final static int M_CONSTRAINTS = 1;


    //******************************************
    //Results of the optimization
    //Intemediate results
    public double lamda;
    public double lamda1;
    public double lamda2;
    public double err = 0;
    public Matrix Y_model;
    //Result of the optimization
    private final Matrix Q_optimizing_keepValue = new Matrix(Q_SIZE + Q_INIT_DISPLACEMENT, 1);
    public Matrix Q_res;
    private Matrix fresult;

    //DEBUG FUNCTIONS !!!!
    private List<Double[]> Q_values;
    private List<Double[]> constraintValues;
    private double iterations;

    /**
     * Create the opt_recursive object with the given parameters
     *
     * @param Y
     * @param phi
     * @param Q_old
     * @param P_old
     * @param lamda_old
     * @param upperlimit
     * @param lowerlimit
     */
    public OptRecursive_Cons(double Y, Matrix phi, Matrix Q_old, Matrix P_old, double lamda_old, double[] upperlimit, double[] lowerlimit) {
        this.Y = Y;
        this.P_old = P_old;
        this.lamda_old = lamda_old;
        this.Q_old = Q_old;
        //Update its double array version:
        double[][] previousQold = Q_old.getArray();
        Q_old_ARRAY = new double[Q_SIZE + Q_INIT_DISPLACEMENT];
        for (int i = 0; i < previousQold.length; i++) {
            Q_old_ARRAY[i] = previousQold[i][0];
            //System.out.print(i + ", ");
        }
        this.phi = phi;
        //TODO
        this.upperlimit = upperlimit;
        this.lowerlimit = lowerlimit;
        
        //Build the P matrix and its pseudo-inverse (pP) from phi and P_old
        //P=(1/(lamda_old))*(P_old-(P_old*phi*pinv(lamda_old+phi'*P_old*phi)*phi'*P_old))
        //Intermediate result: partialResult = pinv(lamda_old+phi'*P_old*phi)
        double partialResult = 1 / ((((phi.transpose()).times(P_old)).times(phi)).get(0, 0) + lamda_old);
        P = (P_old.minus(P_old.times(phi).times(partialResult).times(phi.transpose()).times(P_old))).times(1 / lamda_old);

        //pP is the pseudo-inverse of P
        //Returns the seudoinvers of P only if P is not square
        pP = P.inverse();
        //Qintermediate =  
        fresult = new Matrix(1, 1);

        Q_values = new ArrayList<Double[]>();
        constraintValues = new ArrayList<Double[]>();
        iterations = 0;
    }
    
    /**
     * Update optimization main parameters
     * @param Q_old X0
     * @param P_old P0
     * @param rho_beg Rho_begin
     * @param rho_end Rho_end
     * @param max max_iteration_function
     */
    public void updateParameters(Matrix Q_old, Matrix P_old, double rho_beg,  double rho_end, int max){
        this.Q_old = Q_old;
        //Update its double array version:
        double[][] previousQold = Q_old.getArray();
        Q_old_ARRAY = new double[Q_SIZE + Q_INIT_DISPLACEMENT];
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
    }
    /**
     * Method to run the optimization. Use parameters included the OptRecursive instance.
     * (Implements Cobyla optimization algorithm)
     * 
     */

    public CobylaExitStatus runOptimization(Matrix Q_old, Matrix P_old, double rho_beg,  double rho_end, int max) {

        //Update parameters:
        updateParameters( Q_old,  P_old, rho_beg,  rho_end, max);
        /******************************************
         * PRINT THE INPUT VALUES 
         ******************************************
         */
        System.out.println("///////////////////INPUTS OPT_RECURSIVE/////////////");
        System.out.println(Y + "Y");
        printMatrix(P_old, "P_old");
        printMatrix(phi, "phi");
        printMatrix(Q_old, "Q_old");
        System.out.println("lamda_old - " + lamda_old);
        System.out.println("upperlimit");
        for (int i = 0; i < upperlimit.length; i++) {
            System.out.print(upperlimit[i] + "      ");
        }
        System.out.println();
        System.out.println("lowerlimit");
        for (int i = 0; i < lowerlimit.length; i++) {
            System.out.print(lowerlimit[i] + "      ");
        }

        System.out.println("\n///////////////////INPUTS /////////////");

        /**
         * ************************************************************
         * Run optimization algorithm
         * ************************************************************
         */
        

        //1. Use Cobyla functions 
        Calcfc calcfc = new Calcfc() {
            //Init with compute function
            //TODO use con for constrains ????
            
             /**
             * Compute: n - number of samples m - number of constrains Q - X
             * value for f(x) con - constrains . con[i]>= 0 for all i
             * 
             * @param x - Variable values to be employed in function and constraints calculation.
             * @param  con - Calculated function values of the constraints.
             */
            @Override
            public double compute(int n, int m, double[] Q_large, double[] con) {
               //0. Build Q from its larger version Q_large
               double[] Q = new double[Q_SIZE];
               for (int i =0; i < Q_SIZE; i ++)
                   Q[i] =  Q_large[i + Q_INIT_DISPLACEMENT];
                //1. Get constraints for Q
                //Returns: (max(Astatetemp) - 0.99)
                //Cobyla constraint: con[0] >= 0
                //System.out.println("Con before :" +con[0]);
                //con[0] = -10;
                //TODO NOTE - COBYLA constraint behaviour:
                // con[0] + RHO_END >= 0
                if (M_CONSTRAINTS > 0)
                    con[0] = getConstraintValue(Q) ;
                //con[1] = getConstraintValue(Q);

                // System.out.println("Con afterwards: "+con[0]);

                /*System.out.println("Constraints: "+ con[0]);
                 con[1]= 3;*/
                //con[0] = -1;

                //2. Upper and lower limits
                double[] limits = limitsConstraint(Q);
                for (int i = 0; i < m-1; i ++)
                    con[i+1] = limits[i];
                //2. Set the function to optimize - V
                double opt = optimizationFunctionV(Q);
                double[] Q_V = new double[Q_SIZE + 2];
                Q_V[0] = iterations;
                Q_V[1] = opt;
                for (int i = 2; i < Q_SIZE + 2; i++) {
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

        //Q_oldtemp = Q_old.getArray()[1];
        //printMatrix( "Q_old to findmin: "+ Q_oldtemp);
        //Prepare the Qold matrix to be pass to the min function, as double[]
        double[][] previousQold = Q_old.getArray();
        double[] Q_oldMIN = new double[Q_SIZE + Q_INIT_DISPLACEMENT];
        for (int i = Q_INIT_DISPLACEMENT; i < previousQold.length + Q_INIT_DISPLACEMENT; i++) {
            Q_oldMIN[i  ] = previousQold[i - Q_INIT_DISPLACEMENT][0];
            System.out.print(i + ", ");
        }
        
        //Update Qold to be equal to previous one

        System.out.println("Size Qold " + Q_oldMIN.length + " - ");
        System.out.println("RHO_BEG: "+_RHO_BEG);
        //Run the optimization
        CobylaExitStatus result = Cobyla.findMinimum(calcfc, Q_SIZE+ Q_INIT_DISPLACEMENT, M_CONSTRAINTS, Q_old_ARRAY, _RHO_BEG, _RHO_END, iprint, MAX_FUNC);
        //  result1 = cobyla.findMinimum(calcfc, 24,2*Q_old.getRowDimension()+1, Q_oldtemp, rhobeg, rhoend, iprint, maxfun);  

        //Exit status: DIVERGING ROUNDING ERRORS / MAX ITERATION REACH / NORMAL
        System.out.println("COBYLA EXIT: " + result);
       
        /**
         * *********************************************
         * Save the result of Cobyla optimization
         */
        
        Q_res = new Matrix(Q_SIZE, 1);

        //RECOVER Q value:
        //Q_res = Q_optimizing = Q
        for (int i = Q_INIT_DISPLACEMENT; i < Q_SIZE + Q_INIT_DISPLACEMENT; i++) {
            Q_res.set(i - Q_INIT_DISPLACEMENT, 0, Q_optimizing_keepValue.get(i, 0));
        }

        err = Y - (phi.transpose().times(Q_res)).get(0, 0);
        Y_model = (phi.transpose().times(Q_res));
        lamda1 = 0.9 * lamda_old + (1 - 0.9) * 0.99;
        lamda2 = Math.exp(-(err * err) / (1000));
        lamda = lamda1 * lamda2;

        if (lamda < 0.005) {
            lamda = 0.005;
        }

        /* *******************************************
         * PRINT THE OUTPUT VALUES 
         *********************************************/
        System.out.println("////////////////////OUTPUT OPT_RECURSIVE///////////");
        printMatrix(Q_res, "Q_res");
        System.out.println(err + "  err");
        printMatrix(Y_model, "  Y_model");
        System.out.println(lamda1 + "  lamda1");
        System.out.println(lamda2 + "  lamda2");
        System.out.println(lamda + "  lamda");
        printMatrix(P, "P");
        printMatrix(pP, "Pinv");
        System.out.println("Pinv sizes: " + pP.getColumnDimension() + "x" + pP.getRowDimension());
        printMatrix(phi, "phi");
        System.out.println("\n////////////////////OUTPUT ///////////");
        
        
        return result;


    }
    
    /**
     * Save initial variables of OPT_RECURSIVE optimization
     */
    public void saveOptRecursiveVariables(){
        //TODO Debug - Save all matrices in an excel file, to use later on in MATLAB
        try {
            Save saveManager = new Save(outputOpt);

            //Save intermediate results of the optimization
            Matrix Q_valuesMatrix = new Matrix(Q_values.size(), Q_SIZE + 2);
            for (int i = 0; i < iterations; i++) {
                for (int j = 0; j < Q_SIZE + 2; j++) {
                    Q_valuesMatrix.set(i, j, Q_values.get(i)[j]);
                }
            }

            saveManager.save(Q_valuesMatrix, "Optimization_Steps_Cons");
            //Save the result of the constraint
           /* Matrix Cons_valuesMatrix = new Matrix(constraintValues.size(), N_VARIABLES + 1);
            for (int i = 0; i < iterations; i++) {
                for (int j = 0; j < N_VARIABLES + 1; j++) {
                    Cons_valuesMatrix.set(i, j, constraintValues.get(i)[j]);
                }
            }*/
//            saveManager.save(Cons_valuesMatrix, "Constraint_Steps_Cons");
            //Save the inputs
            Matrix Y_m = new Matrix(1, 1);
            Y_m.set(0, 0, Y);
            saveManager.save(Y_m, "Y");
            saveManager.save(P_old, "P_old");
            saveManager.save(phi, "phi");
            saveManager.save(Q_old, "Q_old");
            Matrix lambda_oldM = new Matrix(1, 1);
            lambda_oldM.set(0, 0, lamda_old);
            saveManager.save(lambda_oldM, "lamda_old");

            Matrix upper = new Matrix(1, upperlimit.length);
            for (int i = 0; i < upperlimit.length; i++) {
                upper.set(0, i, upperlimit[i]);
            }
            saveManager.save(upper, "upperlimit");

            Matrix lower = new Matrix(1, lowerlimit.length);
            for (int i = 0; i < lowerlimit.length; i++) {
                lower.set(0, i, lowerlimit[i]);
            }
            saveManager.save(lower, "lowerlimit");

            //Save the Q result
            saveManager.save(Q_res, "Q_res");
        } catch (Exception e) {
            System.out.println("Error saving inputOut: " + e);
        }
    }
    
     /**
     * Save initial variables of OPT_RECURSIVE optimization
     */
    public void saveOptRecursiveResults(){
        //TODO Debug - Save all matrices in an excel file, to use later on in MATLAB
        try {
            Save saveManager = new Save(outputOpt);

            //Save intermediate results of the optimization
            Matrix Q_valuesMatrix = new Matrix(Q_values.size(), Q_SIZE + 2);
            for (int i = 0; i < iterations; i++) {
                for (int j = 0; j < Q_SIZE + 2; j++) {
                    Q_valuesMatrix.set(i, j, Q_values.get(i)[j]);
                }
            }

           // saveManager.save(Q_valuesMatrix, "Optimization_Steps_Cons2");
            //Save the result of the constraint
            Matrix Cons_valuesMatrix = new Matrix(constraintValues.size(), Q_SIZE + 1);
            /*for (int i = 0; i < iterations; i++) {
                for (int j = 0; j < N_VARIABLES + 1; j++) {
                    Cons_valuesMatrix.set(i, j, constraintValues.get(i)[j]);
                }
            }*/
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
        double[] consInter = new double[Q_SIZE + Q_INIT_DISPLACEMENT + 1];
        //Store X value:
        System.arraycopy(x, 0, consInter, 0, x.length);
        //double [][] A_state = new double[21][21];
        double[][] A_state = new double[Q_SIZE - 2][Q_SIZE - 2];

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

       
        //Check if the eigen values are right:
        //Matrix to use in this operations AstateModify
        Matrix AstateModify = new Matrix(A_state);
        Matrix AstatetEigen = new Matrix(21, 1);
        
        EigenvalueDecomposition eigenDec =  new EigenvalueDecomposition(AstateModify);
        //Matrix vDec = eigenDec.getD();
        Matrix eigenReal= new Matrix(new double[][]{eigenDec.getRealEigenvalues()} );
        Matrix eigenIm= new Matrix(new double[][]{eigenDec.getImagEigenvalues()} );

        //Check if eigen values are valid
        for (int z = 0; z < 21; z++) {
            //Use absolute values of eigen values
            //A = V D V^T.
            //AstatetEigen.set(z, 0, Math.abs(AstateModify.eig().getD().get(z, z)));
            AstatetEigen.set(z, 0, Math.abs(Math.sqrt(Math.pow(eigenReal.get(0, z),2) 
                                            + Math.pow(eigenIm.get(0,z),2))));
            //AstatetEigen.print(9, 6);
        }

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
        double[] limitsResult = new double[Q_SIZE];
        for (int i = Q_INIT_DISPLACEMENT; i < X.length; i++) {
            if (X[i] <= upperlimit[i - Q_INIT_DISPLACEMENT] && X[i ] >= lowerlimit[i - Q_INIT_DISPLACEMENT]) {
                //Normal behaviour - 
                limitsResult[i-Q_INIT_DISPLACEMENT] = 1;
            } else {
                limitsResult[i-Q_INIT_DISPLACEMENT] = -1;
            }
        }
        return limitsResult;
    }


    /**
     * Returns the values of the optimization function V
     * @param Q  input value 
     * @return  V(X)
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
        for (int a = Q_INIT_DISPLACEMENT; a < Q.length; a++) {
            phiQ = phi.get(a-Q_INIT_DISPLACEMENT, 0) * Q[a] + phiQ;
        }
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
        //TODO DEBUG: -infinite
       /*double result = 0 ;
        for (double val: Q)
            result = result + val;
        return result;*/
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
        System.out.print("\n " + name + ": \n{");
        for (double[] row : m.getArray()) {
            for (double val : row) {
                System.out.print("\t " + val);
            }
            System.out.println();
        }
        System.out.println("}");
    }

    /**
     * max() - obtains the maximum value of the matrix
     *
     * @param matrix
     * @return maximum
     */
    public static double max(Matrix matrix) {
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

    
}
