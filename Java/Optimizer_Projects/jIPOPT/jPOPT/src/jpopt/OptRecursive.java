package jpopt;

import org.coinor.Ipopt;
import Jama.Matrix;

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
    private final Matrix Q_optimizing_keepValue = new Matrix(N_DEFAULT, 1);
    public Matrix x_res;
    private Matrix fresult;

    //EIGEN VALUE CONSTRAINT
    private static final double EIGEN_CONSTRAIN_VALUE = 0.9999;

    //IPOPT PARAMS
    /**
     * Initialize the bounds and create the native Ipopt problem.
     */
    public OptRecursive() {

        /* Number of nonzeros in the Jacobian of the constraints */
        nele_jac = 8;
        /* Number of nonzeros in the Hessian of the Lagrangian (lower or
                 * upper triangual part only) */
        nele_hess = 10;

        /* set the number of variables and allocate space for the bounds */
        n = N_DEFAULT;
        double x_L[] = new double[n];
        double x_U[] = new double[n];
        for (int i = 0; i < x_L.length; i++) {
            x_L[i] = 1.0;
            x_U[i] = 5.0;
        }

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
     * Callback function for the objective function.
     */
    @Override
    protected boolean eval_f(int n, double[] x, boolean new_x, double[] obj_value) {

        //Set the function to be optimized - V
        obj_value[0] = optimizationFunctionV(x);

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
        return true;
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
     * Callback function for the objective function gradient
     */
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
        
        return true;
    }

    @Override
    protected boolean eval_jac_g(int n, double[] x, boolean new_x, int m, int nele_jac, int[] iRow, int[] jCol, double[] values) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return true;
    }

    @Override
    protected boolean eval_h(int n, double[] x, boolean new_x, double obj_factor, int m, double[] lambda, boolean new_lambda, int nele_hess, int[] iRow, int[] jCol, double[] values) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return true;
    }

}
