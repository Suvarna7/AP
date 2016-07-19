package jpopt;

import org.coinor.Ipopt;
import Jama.Matrix;
import Jama.EigenvalueDecomposition;
import static jpopt.JPOPT.printDoubleArrayMatrix;


/**
 *
 * @author Cat
 */
public class OptRecursive extends Ipopt {

    // Problem sizes
    int n, m, nele_jac, nele_hess;

    //Default values:
    //OPTIMIZATION PARAMS
    //x variables
    public static final int N_DEFAULT = 24;
    //Constraint bounds
    public static final int M_DEFAULT = 1;
    public static final int L_CONSTRAINT = 0;
    public static final int H_CONSTRAINT = 1;

    //Variables for the optimization
    //INPUTS:
    public double Y;
    public Matrix phi;
    public Matrix x_old;
    public double[] x_old_ARRAY;
    public Matrix P_old;
    public double lamda_old;
    //Paremeters matrix P
    public Matrix P;
    //And its pseudoInverse
    private Matrix pP;

    //OUTPUTS:
    //Results of the optimization
    //Intemediate results
    public double lamda;
    public double lamda1;
    public double lamda2;
    public double err = 0;
    public Matrix Y_model;
    //Result of the optimization
    public Matrix Q_optimizing_keepValue = new Matrix(N_DEFAULT, 1);
    public Matrix x_res;
    private Matrix fresult;

    //EIGEN VALUE CONSTRAINT
    private static final double EIGEN_CONSTRAIN_VALUE = 0.9999;

    //IPOPT PARAMS
    /**
     * Initialize the bounds and create the native Ipopt problem.
     */
    public OptRecursive() {
        //1. Init parameters:
        initializeOptRecursive_Default();

        //2. Prepare the jIPOPT problem:
        /* Number of nonzeros in the Jacobian of the constraints */
        nele_jac = 1;
        /* Number of nonzeros in the Hessian of the Lagrangian (lower or
                 * upper triangual part only) */
        nele_hess = 1;

        /* set the number of variables and allocate space for the bounds */
        n = N_DEFAULT;
        double [] upperlimit = new double[]{100, 100, 100, 100, 100, 100, 100, 100, 100, 100,
                                 100, 100, 100, 100, 100,100, 100, 100, 100, 100,
                                 100, 100, 100, 100};
        
        double [] lowerlimit = new double[]{-100, -100, -100, -100, -100, -100, -100, -100, -100, -100,
                                 -100, -100, -100, -100, -100, -100, -100, -100, -100, -100,
                                 -100, -100, -100, -100};
        double x_L[] = lowerlimit;
        double x_U[] = upperlimit;


        /* set the number of constraints and allocate space for the contraint
                    bounds */
        m = M_DEFAULT;
        double g_L[] = new double[m];
        double g_U[] = new double[m];

        /* set the values of the constraint bounds */
        g_L[0] = L_CONSTRAINT;
        g_U[0] = H_CONSTRAINT;

        /* Index style for the irow/jcol elements */
        int index_style = Ipopt.C_STYLE;

        /* create the IpoptProblem */
        create(n, x_L, x_U, m, g_L, g_U, nele_jac, nele_hess, index_style);
    }

    /**
     * Initialize the bounds and create the native Ipopt problem.
     */
    public OptRecursive(double Y, Matrix phi, Matrix Q_old, Matrix P_old, double lamda_old, double[] upperlimit, double[] lowerlimit) {

        //Init the optimization problem params:
        this.Y = Y;
        this.P_old = P_old;
        this.lamda_old = lamda_old;
        this.x_old = Q_old;
        //Update its double array version:
        double[][] previousQold = Q_old.getArray();
        x_old_ARRAY = new double[N_DEFAULT];
        for (int i = 0; i < previousQold.length; i++) {
            x_old_ARRAY[i] = previousQold[i][0];
            //System.out.print(i + ", ");
        }
        this.Q_optimizing_keepValue = x_old;
        this.phi = phi;

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

        //Prepare the jIPOPT problem:
        /* Number of nonzeros in the Jacobian of the constraints */
        nele_jac = 8;
        /* Number of nonzeros in the Hessian of the Lagrangian (lower or
                 * upper triangual part only) */
        nele_hess = 10;

        /* set the number of variables and allocate space for the bounds */
        n = N_DEFAULT;
        double x_L[] = upperlimit;
        double x_U[] = lowerlimit;


        /* set the number of constraints and allocate space for the contraint
                    bounds */
        m = M_DEFAULT;
        double g_L[] = new double[m];
        double g_U[] = new double[m];

        /* set the values of the constraint bounds */
        g_L[0] = L_CONSTRAINT;
        g_U[0] = H_CONSTRAINT;

        /* Index style for the irow/jcol elements */
        int index_style = Ipopt.C_STYLE;

        /* create the IpoptProblem */
        create(n, x_L, x_U, m, g_L, g_U, nele_jac, nele_hess, index_style);
    }

    /**
     * Generates the default X0 - initial point
     *
     * @return
     */
    public double[] getInitialGuess() {
        /* allocate space for the initial point and set the values */
        double[] x =  new double[N_DEFAULT];
        for(int i = 0 ;i < N_DEFAULT; i ++)
            x[i] = 0;

        return x;
    }
    
    /**
     * Generates default parameters for OPT_RECURSIVE:
     * 
     */
    
    private void initializeOptRecursive_Default(){
        //    public OptRecursive(double Y, Matrix phi, Matrix Q_old, Matrix P_old, double lamda_old, double[] upperlimit, double[] lowerlimit) {

                //Init the optimization problem params:
        Y = 180;
        P_old = new Matrix(new double[][]{{ 1, 0 , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                                          { 0, 1 , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                                          { 0, 0 , 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                                          { 0, 0 , 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                                          { 0, 0 , 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                                          { 0, 0 , 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                                          { 0, 0 , 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                                          { 0, 0 , 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                                          { 0, 0 , 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                                          { 0, 0 , 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                                          { 0, 0 , 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                                          { 0, 0 , 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                                          { 0, 0 , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                                          { 0, 0 , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                                          { 0, 0 , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                                          { 0, 0 , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                                          { 0, 0 , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
                                          { 0, 0 , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                                          { 0, 0 , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                                          { 0, 0 , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
                                          { 0, 0 , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
                                          { 0, 0 , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
                                          { 0, 0 , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
                                          { 0, 0 , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}});
        lamda_old = 0.5;
        x_old = new Matrix (new double[][]{{0}, {0} , {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}});
        //Update its double array version:
        double[][] previousQold = x_old.getArray();
        x_old_ARRAY = new double[N_DEFAULT];
        for (int i = 0; i < previousQold.length; i++) {
            x_old_ARRAY[i] = previousQold[i][0];
            //System.out.print(i + ", ");
        }
        //Update result value too 
         Q_optimizing_keepValue = new Matrix (new double[][]{{0}, {0} , {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}});


        //Phi matrix 24x1
        double[] phiArray = new double[]{-300, -166, -162, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0.3,
            0.3, 0.3, 0.3, 0.2, 0.2, 0.2, 0.2, 0};
        double[][] phiDoubleArray = new double[24][1];
        for (int i = 0; i < phiArray.length; i++) {
            phiDoubleArray[i][0] = phiArray[i];
        }
        phi = new Matrix(phiDoubleArray);

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
     * Callback function for the objective function.
     * @param n
     * @param x
     * @param new_x
     * @param obj_value
     * @return 
     */
    @Override
    protected boolean eval_f(int n, double[] x, boolean new_x, double[] obj_value) {
        assert n == this.n;

        //Set the function to be optimized - V
        //obj_value[0] = optimizationFunctionV(x);
        double sumation = 0 ;
        for (double val: x)
            sumation += val;
        
        obj_value[0] = sumation;


        //Save results:
        /*double[] Q_V = new double[N_DEFAULT + 2];
                Q_V[0] = iterations;
                Q_V[1] = opt;
                for (int i = 2; i < Q_SIZE + 2; i++) {
                    Q_V[i] = Q[i - 2];
                }
                Double[] doubleArray = ArrayUtils.toObject(Q_V);
                Q_values.add(doubleArray);
                iterations++;
         */
        //Store X value
        double[][] array = new double[n][1];
        for (int i = 0; i < n ; i ++){
            array[i][0] = x[i];
        }
        System.out.println("\n NEW ITERATION:");
        System.out.println("X directly:");
        for (double val: x)
            System.out.println(val);
        JPOPT.printDoubleArrayMatrix(array, "X_current");
        JPOPT.printDoubleArrayMatrix(Q_optimizing_keepValue.getArray(), "X_result_keep");

        return true;
    }

    
   
    /**
     * Callback function for the objective function gradient
     *
     * @param n
     * @param x
     * @param new_x
     * @param grad_f
     * @return
     */
    @Override
    protected boolean eval_grad_f(int n, double[] x, boolean new_x, double[] grad_f) {
        assert n == this.n;


        return true;
    }

    /**
     * Callback function for the constraints
     *
     * @param n
     * @param x
     * @param new_x
     * @param m
     * @param g
     * @return
     */
    @Override
    protected boolean eval_g(int n, double[] x, boolean new_x, int m, double[] g) {
        assert n == this.n;
        assert m == this.m;
        
        double max_eigen = getConstraintValue(x); 
        g[0] = max_eigen;
        return true;
    }

    @Override
    protected boolean eval_jac_g(int n, double[] x, boolean new_x, int m, int nele_jac, int[] iRow, int[] jCol, double[] values) {
         
        assert n == this.n;
        assert m == this.m;

       
        return true;
    }

    @Override
    protected boolean eval_h(int n, double[] x, boolean new_x, double obj_factor, int m, double[] lambda, boolean new_lambda, int nele_hess, int[] iRow, int[] jCol, double[] values) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
       // assert idx == nele_hess;
            assert nele_hess == this.nele_hess;
        return true;
    }
    
    /* ******************************************************************
    * OPTIMIZATION FUNCTIONS 
    */
    
    //1.  OPTIMIZATION FUNCTION
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
        differenceMatrix = new Matrix(x_old.getRowDimension(), 1);
        for (int s1 = 0; s1 < x_old.getRowDimension(); s1++) {
            differenceMatrix.set(s1, 0, Q[s1] - x_old.get(s1, 0));
        }
        //First part of V. f = (Q - Qold)'*pseudoinvP*(Q-Qold)
        double f = (((differenceMatrix.transpose()).times(pP)).times(differenceMatrix)).get(0, 0);
        //Middle term - temp = phi'*Q
        double phiQ = 0;
        //Do the multiplication manually:
        for (int a = 0; a < Q.length; a++) {
            phiQ = phi.get(a, 0) * Q[a] + phiQ;
        }
        //Partial function to optimize =(Y-phi'*Q)'*(Y-phi'*Q)
        double f1 = (Y - phiQ) * (Y - phiQ);
        //SAVE LOCAL VALUE OF Q:
        //Q_optimizing = Q
        for (int i = 0; i < Q.length; i++) {
            Q_optimizing_keepValue.set(i, 0, Q[i]);
        }
        //DEBUG - result matrix to save
        //fresult = new Matrix(fresult.getRowDimension() + 1, 1);
        //fresult.set(fresult.getRowDimension() - 1, 0, f + f1);

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
    
    //2 CONSTRAINT
    
     /* Generates the constraints parameters for x = Q We will be optimizing A,
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
        double[] consInter = new double[N_DEFAULT  + 1];
        //Store X value:
        System.arraycopy(x, 0, consInter, 0, x.length);
        //double [][] A_state = new double[21][21];
        double[][] A_state = new double[N_DEFAULT - 2][N_DEFAULT - 2];

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
        Matrix AstateEigen = new Matrix(21, 1);
        
        EigenvalueDecomposition eigenDec =  new EigenvalueDecomposition(AstateModify);
        //Matrix vDec = eigenDec.getD();
        Matrix eigenReal= new Matrix(new double[][]{eigenDec.getRealEigenvalues()} );
        Matrix eigenIm= new Matrix(new double[][]{eigenDec.getImagEigenvalues()} );

        //Check if eigen values are valid
        for (int z = 0; z < 21; z++) {
            //Use absolute values of eigen values
            //A = V D V^T.
            //AstatetEigen.set(z, 0, Math.abs(AstateModify.eig().getD().get(z, z)));
            AstateEigen.set(z, 0, Math.abs(Math.sqrt(Math.pow(eigenReal.get(0, z),2) 
                                            + Math.pow(eigenIm.get(0,z),2))));
            //AstatetEigen.print(9, 6);
        }

        return max (AstateEigen);
        //We want: max(AstateEigen) -0.99 <= 0
        /*double resultC = (EIGEN_CONSTRAIN_VALUE - max(AstatetEigen));
        //double resultC = -1;
        //Save values of this step:
        consInter[24] = resultC;
        //constraintValues.add(ArrayUtils.toObject(consInter));
        return resultC;*/
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

    
  

}
