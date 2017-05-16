package dias;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import de.xypron.jcobyla.Calcfc;
import de.xypron.jcobyla.Cobyla;
import de.xypron.jcobyla.CobylaExitStatus;
import static dias.OptRecursive_JCOBYLA.printMatrix;
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
public class OptRecursive {
    //OUTPUT FILE TO SAVE Optimization results
    //TODO Output file
    public static final String outputOpt = "outputOpt";
    
    //OPTIMIZATION PARAMETERS:
    //INPUTS:
    //Size of Q matrix
    public static final int Q_SIZE = 24;
    public double Y;
    public Matrix phi;
    public Matrix Q_old;
    public double[] Q_old_ARRAY;
    public Matrix P_old;
    public double lambda_old;
    public double[] upperlimit = new double[Q_SIZE + 1];
    public double[] lowerlimit = new double[Q_SIZE + 1];
    //Paremeters matrix P
    public Matrix P;
    //And its pseudoInverse
    private Matrix pP;
    
    //Outputs
    //Results of the optimization
    //Intemediate results
    public double lamda;
    public double err = 0;
    public Matrix Y_model;
    //Result of the optimization
    //public Matrix Q = new Matrix(_Q_SIZE, 1);
    private final Matrix Q_optimizing_keepValue = new Matrix(OptRecursive.Q_SIZE, 1);
    public Matrix Q_res;
    private Matrix fresult;

    
    //OPTIMIZERS
    OptRecursive_JCOBYLA jCobylaOptimizer;

    /**
     * 
     * @param inputs 
     */
   public OptRecursive(OptInputs inputs){
       //Initialize parameters
        this.Y = inputs.y();
        this.P_old = inputs.p();
        this.lambda_old = inputs.lambda();
        this.Q_old = inputs.q();
        //Update its double array version:
        double[][] previousQold = Q_old.getArray();
        Q_old_ARRAY = new double[Q_SIZE];
        for (int i = 0; i < previousQold.length; i++) {
            Q_old_ARRAY[i] = previousQold[i][0];
            System.out.print(i + ", ");
        }
        this.phi = inputs.phi();
        //TODO
        this.upperlimit = inputs.upperLimit();
        this.lowerlimit = inputs.lowerLimit();
       //Initialize the Optimizer 
       jCobylaOptimizer = new OptRecursive_JCOBYLA(inputs);
   }
   
   /**
    * 
    * @return 
    */
   public OptInputs runOptimization(){
        if (DIAS.verboseMode) {
            this.printInputs();
        }
   
          /**
         * ************************************************************
         * Run the optimization algorithm
         * ************************************************************
         */
        //1. Build the P matrix and its pseudo-inverse (pP) from phi and P_old
        //P=(1/(lamda_old))*(P_old-(P_old*phi*pinv(lamda_old+phi'*P_old*phi)*phi'*P_old))
        //Intermediate result: partialResult = pinv(lamda_old+phi'*P_old*phi)
        if (DIAS.verboseMode) {
            System.out.println("Size of phi: " + phi.getRowDimension() + "x" + phi.getColumnDimension());
        }
        double partialResult = 1 / ((((phi.transpose()).times(P_old)).times(phi)).get(0, 0) + lambda_old);
        P = (P_old.minus(P_old.times(phi).times(partialResult).times(phi.transpose()).times(P_old))).times(1 / lambda_old);

        //pP is the pseudo-inverse of P
        //Returns the seudoinvers of P only if P is not square
        pP = P.inverse();

        //2. Use Cobyla functions 
        OptInputs output = jCobylaOptimizer.runOptimization(Q_old, P, pP, Y, phi, lambda_old);
        
        //3. Update results of optimization
        moveJCobylaResults(output);
        
        if (DIAS.verboseMode) {
            this.printOutputs();
        }
        
        return output;
        
        
   }
   
   private void  moveJCobylaResults( OptInputs io){
    //Results of the optimization
    //Intemediate results
    this.lamda = io.lambda();
    this.err = io.err();
    this.Y_model = jCobylaOptimizer.Y_model;
    //Result of the optimization
    //public Matrix Q = new Matrix(_Q_SIZE, 1);
    this.Q_res = io.q();
    this.fresult = jCobylaOptimizer.fresult;

   
   }
   
   private void printInputs() {
        System.out.println("///////////////////INPUTS OPT_RECURSIVE/////////////");
        System.out.println(Y + "Y");
        printMatrix(P_old, "P_old");
        printMatrix(phi, "phi");
        printMatrix(Q_old, "Q_old");
        System.out.println("lambda_old - " + lambda_old);
        System.out.println("upperlimit");
        for (int i = 0; i < upperlimit.length; i++) {
            System.out.print(upperlimit[i] + "      ");
        }
        System.out.println();
        System.out.println("lowerlimit");
        for (int i = 0; i < lowerlimit.length; i++) {
            System.out.print(lowerlimit[i] + "      ");
        }
        System.out.println("\n///////////////////INPUTS OPT_RECURSIVE/////////////");
    }

    private void printOutputs() {
        System.out.println("////////////////////OUTPUT OPT_RECURSIVE///////////");
        printMatrix(Q_res, "Q_res");
        System.out.println(err + "  err");
        printMatrix(Y_model, "  Y_model");
        System.out.println(lamda + "  lamda");
        printMatrix(P, "P");
        printMatrix(pP, "Pinv");
        System.out.println("Pinv sizes: " + pP.getColumnDimension() + "x" + pP.getRowDimension());
        printMatrix(phi, "phi");
        System.out.println("\n////////////////////OUTPUT OPT_RECURSIVE///////////");
    }
    
    /**
     * Save initial variables of OPT_RECURSIVE optimization
     */
    public void savejCobylaVariables() {
        //TODO Debug - Save all matrices in an excel file, to use later on in MATLAB
        try {
            Save saveManager = new Save(outputOpt);

            //Save intermediate results of the optimization
            //DEBUG JCOBYLA functions
            Matrix Q_valuesMatrix = new Matrix(jCobylaOptimizer.Q_values.size(), jCobylaOptimizer.N_VARIABLES + 2);
            for (int i = 0; i < jCobylaOptimizer.iterations; i++) {
                for (int j = 0; j < jCobylaOptimizer.N_VARIABLES + 2; j++) {
                    Q_valuesMatrix.set(i, j, jCobylaOptimizer.Q_values.get(i)[j]);
                }
            }
            saveManager.save(Q_valuesMatrix, "Optimization_Steps_Cons");
            //Save the result of the constraint
            Matrix Cons_valuesMatrix = new Matrix(jCobylaOptimizer.constraintValues.size(), jCobylaOptimizer.N_VARIABLES + 1);
            for (int i = 0; i < jCobylaOptimizer.iterations; i++) {
                for (int j = 0; j < jCobylaOptimizer.N_VARIABLES + 1; j++) {
                    Cons_valuesMatrix.set(i, j, jCobylaOptimizer.constraintValues.get(i)[j]);
                }
            }
//            saveManager.save(Cons_valuesMatrix, "Constraint_Steps_Cons");
            //Save the inputs
            Matrix Y_m = new Matrix(1, 1);
            Y_m.set(0, 0, Y);
            saveManager.save(Y_m, "Y");
            saveManager.save(P_old, "P_old");
            saveManager.save(phi, "phi");
            saveManager.save(Q_old, "Q_old");
            Matrix lambda_oldM = new Matrix(1, 1);
            lambda_oldM.set(0, 0, lambda_old);
            saveManager.save(lambda_oldM, "lambda_old");

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
    
}
