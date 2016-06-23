/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optrecursive_testbench;

import Jama.Matrix;
import de.xypron.jcobyla.CobylaExitStatus;
import java.io.File;
import java.util.List;

import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.ex.*;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.tree.xpath.XPathExpressionEngine;

/**
 * Class to test different values of OptRecursive class Should be use to compare
 * with MATLAB
 *
 * @author Cat
 */
public class OptRecursive_Testbench {

    //Parameter : current processing environment. 
    // This allows us to switch between <processing> nodes in the configuration XML 
    // by using the @env attribute. 
    public static String configurationEnvironment = "none";
    //Parameter: excel files to save/load variables
    //TODO make sure files exist in the given path
    public static String excelFilePath;
    //Parameter: File for Bodymedia read values
    //TODO make sure this file is the same as the xls generated by BodyMedia
    public static String bodymediaFileUrl;
    //Parameter: Email to receive messages
    public static String[] privateMails;

    // Optimization fields:
    private static double Y;
    private static Matrix phi;
    private static Matrix Q_old;
    private static Matrix P_old;
    private static double lamda_old;
    private static double[] upperlimit;
    private static double[] lowerlimit;
    
    //Optimizer
    private static OptRecursive_Cons testOptRecursiveCons;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Set up configuration here so we can read from the configuration file for our previously-static 
        // variables. 
        boolean configureOK = configureSession();

        System.out.println(excelFilePath + " : " + new File(excelFilePath).exists());
        System.out.println(bodymediaFileUrl + " : " + new File(bodymediaFileUrl).exists());

        //Start Graphical interface
        //XXX restore this next line before merging with master. 
        if (configureOK) {
            //Use testbench to test OptRecursive
            /*createOptRecursiveDefaultParameters();
            OptRecursive testOptRecursive = new OptRecursive(Y, phi, Q_old, P_old, lamda_old, upperlimit, lowerlimit);
            testOptRecursive.runOptimization();
            //Recursive
            int i = 0;
            while (i < 10) {
                Y += i*10;
                updateOptRecursiveSetParametersQP(testOptRecursive.Q_res, testOptRecursive.P);
                testOptRecursive = new OptRecursive(Y, phi, Q_old, P_old, lamda_old, upperlimit, lowerlimit);
                testOptRecursive.runOptimization();
                i++;
            }*/

            //Try higher numbers of Q
            /*createOptRecursiveDefaultParameters();
            updateOptRecursiveSetParametersQRANDOM();
            testOptRecursive = new OptRecursive(Y, phi, Q_old, P_old, lamda_old, upperlimit, lowerlimit);
            testOptRecursive.runOptimization(); */
            /**
             * *****************************************************
             * CONSTRAINTS
             */
            createOptRecursiveDefaultParameters();
            optimizeFunctionStages(Q_old, P_old);
            
            
            //Recursive
           int i = 0;
            while (i < 5) {
                Y += i * 20;
                optimizeFunctionStages(testOptRecursiveCons.Q_res, testOptRecursiveCons.P);
                i++;
            }
            //Try higher numbers of Q
            /*createOptRecursiveDefaultParameters();
            updateOptRecursiveSetParametersQRANDOM();
            OptRecursive_Cons testOptRecursiveCons = new OptRecursive_Cons(Y, phi, Q_old, P_old, lamda_old, upperlimit, lowerlimit);
            testOptRecursiveCons.runOptimization();
            testOptRecursiveCons.saveOptRecursiveVariables();
            //Second optimization:
            testOptRecursiveCons.updateParameters(testOptRecursiveCons.Q_res, testOptRecursiveCons.P,1.0e-2, 1.49011611938477e-6, 5000);
            testOptRecursiveCons.runOptimization();
            testOptRecursiveCons.updateParameters(testOptRecursiveCons.Q_res, testOptRecursiveCons.P,1.0e-2, 1.49011611938477e-8, 5000);
            testOptRecursiveCons.runOptimization();
            testOptRecursiveCons.updateParameters(testOptRecursiveCons.Q_res, testOptRecursiveCons.P,1.0e-4, 1.49011611938477e-8, 5000);
            testOptRecursiveCons.runOptimization();
            testOptRecursiveCons.saveOptRecursiveResults();*/
            /**
             * *******************************************************
             * TEST CONSTRAINT FUNCTION ITSELF
             */
            //testOptRecursiveConstraintFunction(testOptRecursiveCons);
            /**
             * *********************************************************
             * TEST OPTIMIZATION FUNCTION DEFITINION ITSELF
             */
            //createOptRecursiveDefaultParameters();
            //updateOptRecursiveSetParametersQRANDOM();
            /*OptRecursive_Cons testOptRecursiveCons = new OptRecursive_Cons(Y, phi, Q_old, P_old, lamda_old, upperlimit, lowerlimit);
            testOptRecursiveFunctionEval(testOptRecursiveCons);*/
        }

        //  ChocaNonLinear ch = new ChocaNonLinear ();
        //   ch.Choca();
    }

    //Is it bad that this method is referencing our now-global variables? Eh, maybe. 
    //This only needs to run here, though. We can expand to a full class with a factory etc. 
    // for all our platform-specific global variables if we need to. 
    public static boolean configureSession() {
        boolean output = false; //be pessimistic. 
        Configurations configs = new Configurations();
        try {
            System.out.println("User directory is " + System.getProperty("user.dir"));
            XMLConfiguration config = configs.xml("config/configuration.xml"); //this is a really nice factory implementation we're eliding
            //use XPATH so we can query attributes. NB that this means we'll be using slash-style lookup as in 
            // "processing/paths/excelFilePath" 
            // instead of 
            // "processing.paths.excelFilePath"
            config.setExpressionEngine(new XPathExpressionEngine());
            configurationEnvironment = config.getString("environment/env");
            System.out.println(configurationEnvironment);
            excelFilePath = config.getString("processing[@env='" + configurationEnvironment + "']/paths/excelFilePath");
            bodymediaFileUrl = config.getString("processing[@env='" + configurationEnvironment + "']/paths/bodymediaFileUrl");
            //HierarchicalConfiguration node = (HierarchicalConfiguration) config.configurationAt("/nodes/node[@id='"+(str)+"']");
            List<String> emails = config.getList(String.class, "processing[@env='" + configurationEnvironment + "']/emails/email");
            privateMails = new String[emails.size()];
            privateMails = emails.toArray(privateMails);
            output = true;
        } catch (ConfigurationException cex) {
            //Something went wrong; we should probably check to see if the configuration file wasn't found, 
            // but otherwise just keep the output as false.
            System.out.println(cex.getMessage());
        }
        return output;
    }

    /**
     * Set default parameters for optRecursive
     */
    private static void createOptRecursiveDefaultParameters() {
        //Y
        Y = 189;
        //Phi matrix 24x1
        double[] phiArray = new double[]{-300, -166, -162, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0.3,
            0.3, 0.3, 0.3, 0.2, 0.2, 0.2, 0.2, 0};
        double[][] phiDoubleArray = new double[24][1];
        for (int i = 0; i < phiArray.length; i++) {
            phiDoubleArray[i][0] = phiArray[i];
        }
        phi = new Matrix(phiDoubleArray);

        //Q_old matrix 24x1
        double[][] Q_oldArray = new double[][]{{1},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0}
        };
        Q_old = new Matrix(Q_oldArray);

        //P_old matrix 24 x 24
        double[][] P_oldArray = new double[][]{{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}
        };
        P_old = new Matrix(P_oldArray);
        //Lambda
        lamda_old = 0.5;

        //Upperlimit array: 1x24
        upperlimit = new double[]{1, 1, 1, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 1, 1, 1, 1, 1};

        //Lowerlimit array: 1x24
        lowerlimit = new double[]{-1, -1, -1, -0.974082841, -0.932309039, -0.847452389, -0.724680039, -0.584206489,
            -0.448368344, -0.333152936, -0.24418079, -0.177141947, -0.122686145, -0.07576685, -0.049439141, -1,
            -1, -1, -1, 0, 0, 0, 0, -1};

    }

    /**
     * Update Q and P parameters only, with previous values
     *
     * @param Qprev
     * @param Pprev
     */
    private static void updateOptRecursiveSetParametersQP(Matrix Qprev, Matrix Pprev) {
        Q_old = Qprev;
        P_old = Pprev;

    }

    /**
     * Update Q parameters only, with RANDOM values
     *
     * @param Qprev
     */
    private static void updateOptRecursiveSetParametersQRANDOM() {
        //Q_old matrix 24x1
        double[][] Q_oldArray = new double[][]{{1},
        {0},
        {3},
        {0},
        {0},
        {0},
        {1},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {0},
        {5},
        {0},
        {0},
        {5},
        {0},
        {0},
        {6},
        {0}
        };
        Q_old = new Matrix(Q_oldArray);

    }

    /**
     * Test constraint evaluation function of OptRecursive x values: are copied
     * from MATLAB result --> Check when constraint is not satisfied
     *
     * @param tORC - test OptRecursive_Cons instance
     */
    private static void testOptRecursiveConstraintFunction(OptRecursive_Cons tORC) {

        //Sample 2 of MATLAB / Result -0.990000000000000
        //CONSTRAINT SATISFIED IN JAVA (0.9899999850988388) / CONSTRAINT SATISFIED IN MATLAB (-0.99)
        double[] x = new double[]{1.49011611938477e-08, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0};
        System.out.println("Constraint eval for x0: " + tORC.getConstraintValue(x));

        //JAVA Sample 2 is different:
        //However, it is still satisfied (0.9899)
        x = new double[]{1.0e-4, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0};
        System.out.println("Constraint eval for java x0: " + tORC.getConstraintValue(x));

        //Get X value to test: from MATLAB, one that is giving a non satisfied constraint
        //Sample 26 MATLAB/ Result x1 -> 113399.149473697
        //CONSTRAINT NOT SATISFIED IN JAVA (-113399.14947369644)| CONSTRAINED NOT SATISFIED IN MATLAB (113399.149473697)
        x = new double[]{-113399.614221458, -59563.7031614780, 2955.4205651858, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0,
            113.399902343750, 113.399902343750, 113.399902343750, 113.399902343750, 75.6000976562500, 75.6000976562500, 75.6000976562500, 75.6000976562500, 0};
        System.out.println("Constraint eval for x1: " + tORC.getConstraintValue(x));

        //Sample 29 / Result x2 - 3.81501042427541
        //CONSTRAINT NOT SATISFIED IN JAVA (-14174.487011283138)| NOT SATISFIED IN MATLAB (14174.4870112831)
        x = new double[]{-14174.9517776823, -7445.46289518475, 369.427570648231, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            14.1749877929688, 14.1749877929688, 14.1749877929688, 14.1749877929688, 9.45001220703125, 9.45001220703125, 9.45001220703125, 9.45001220703125};
        System.out.println("Constraint eval for x2  : " + tORC.getConstraintValue(x));

        //Result in Java - constraint is satisfied
        //However, MATLAB constraint is not satisfied ( 0.5144)
        x = new double[]{-0.5987204248582807, 0.8035418767168442, 2.139382685300193, -0.0015302190279335268,
            -6.351784523786271E-4, -6.899284863495495E-4, 0.9997794768795635, 0.001435863478895225,
            -5.286298602979141E-4, -9.730475020866823E-4, -0.002314825356585741, 8.196254443002608E-4,
            1.566450595098825E-4, -0.0011451294282543416, -0.005444165656198248, 9.687475076423268E-4,
            4.998134573582582, 0.002242676479573011, 0.004374662431126445, 4.9998748646698585,
            -8.894432215417106E-4, 0.004305866352121422, 6.00244167565629, 3.714650350355675E-4};
        System.out.println("Constraint eval for x3  : " + tORC.getConstraintValue(x));

        //If we get x3 value from MATLAB, there are slight variations: ( 0.5998)
        x = new double[]{-0.6597, -0.9177, -0.8916, 0.0030, 0.0025,
            -0.0012, 0.0041, 0.0006, 0.0028, 0.0012, 0.0005,
            -0.0031, -0.0011, -0.0065, -0.0086, 0.0111, 0.0104,
            0.0098, 0.0098, 0.0082, 0.0036, 0.0116, 0.0111,
            -0.0064};
        System.out.println("Constraint eval for x3 (MATLAB) : " + tORC.getConstraintValue(x));

        //Another problematic value:
        //MATLAB constraint eval =  0.2543
        x = new double[]{-0.0223, -0.0278, 1.9267, -0.0515,
            -0.2118, -0.0821, 0.9847, -0.0390,
            -0.0448, 0.0430, 0.0853, 0.0435,
            -0.1123, -0.2102, 0.0580, -0.0317,
            5.2164, 0.5113, 0.3487, 4.9604,
            0.1955, 0.2967, 6.4045, -0.1705};

        System.out.println("Constraint eval for x4 (MATLAB) : " + tORC.getConstraintValue(x));

    }

    /**
     * Test our definition of the Optimization function vs MATLAB Use values
     * already generated in MATLAB
     *
     * @param tORC - instance of the OptRecursive_Cons
     */
    private static void testOptRecursiveFunctionEval(OptRecursive_Cons tORC) {
        //Sample 2 of MATLAB / V = 35721.0016897917
        //CONSTRAINT SATISFIED IN JAVA (0.9899999850988388) / CONSTRAINT SATISFIED IN MATLAB (-0.99)
        double[] x = new double[]{1.49011611938477e-08, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0};
        System.out.println("Function V  eval for x0: " + tORC.optimizationFunctionV(x) + " vs. 35721.0016897917 {MAT}");

        //JAVA Sample 2 is different:
        //However, it is still satisfied (0.9899)
        x = new double[]{1.0e-4, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0};
        System.out.println("Function V eval for java x1: " + tORC.optimizationFunctionV(x));

        //Get X value to test: from MATLAB, one that is giving a non satisfied constraint
        //Sample 26 MATLAB/ Result V(x1) -> 3.77212657982506e+15
        //CONSTRAINT NOT SATISFIED IN JAVA (-113399.14947369644)| CONSTRAINED NOT SATISFIED IN MATLAB (113399.149473697)
        x = new double[]{-113399.614221458, -59563.7031614780, 2955.4205651858, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0,
            113.399902343750, 113.399902343750, 113.399902343750, 113.399902343750, 75.6000976562500, 75.6000976562500, 75.6000976562500, 75.6000976562500, 0};
        System.out.println("Function V  eval for x2: " + tORC.optimizationFunctionV(x) + " vs 3.77212657982506e+15 (MAT)");

        //Sample 29 / Result V(x2) =58937682332278.6
        x = new double[]{-14174.9517776823, -7445.46289518475, 369.427570648231, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            14.1749877929688, 14.1749877929688, 14.1749877929688, 14.1749877929688, 9.45001220703125, 9.45001220703125, 9.45001220703125, 9.45001220703125};
        System.out.println("Function V  eval for x3  : " + tORC.optimizationFunctionV(x) + " vs 58937682332278.6 (MAT)");

    }

    /**
     * 
     * @param Qin
     * @param Pin 
     */
    private static void optimizeFunctionStages(Matrix Qin, Matrix Pin) {
        //First step, we do it manually
        testOptRecursiveCons = new OptRecursive_Cons(Y, phi, Qin, Pin, lamda_old, upperlimit, lowerlimit);
        CobylaExitStatus exit = testOptRecursiveCons.runOptimization();
        testOptRecursiveCons.saveOptRecursiveVariables();
        
        //Next ones, we use the stage function
        Matrix next_Q = testOptRecursiveCons.Q_res;
        //Typical X value is 1, so we build a matrix the same size as Q_res filled with ones. 
        Matrix typicalX = new Matrix(testOptRecursiveCons.Q_res.getRowDimension(), testOptRecursiveCons.Q_res.getColumnDimension(), 1).times(1e0); 
        Matrix next_rho_m = forwardFiniteDiffStepSize(next_Q, typicalX); 
        
        System.out.println("Output: "+exit+ " vs " + CobylaExitStatus.NORMAL);
        System.out.println(exit.compareTo(CobylaExitStatus.NORMAL));

        //We stay in the loop until Cobyla optimization has a NORMAL exit
        int i = 14;
       
       while (exit.compareTo(CobylaExitStatus.NORMAL)!=0 && i > -3){
                Double next_rho_beg = next_rho_m.get(0, 0); 
                //next_rho_beg = ((1.0+(i%2))/2.0)*Math.pow(10, -(Math.floor(i/2)));
                System.out.println("Next rho_beg value is : " + Double.toString(next_rho_beg) + " ; ");
                exit = optimizeFunctionSingleStage(testOptRecursiveCons, next_rho_beg);
                next_Q = testOptRecursiveCons.Q_res;
                next_rho_m = forwardFiniteDiffStepSize(next_Q, typicalX); 
                i --;
        }
        //Save outputs:
        testOptRecursiveCons.saveOptRecursiveResults();
        
        //Second optimization:
        /*testOptRecursiveCons.updateParameters(testOptRecursiveCons.Q_res, testOptRecursiveCons.P, 1.0e-2, 1.49011611938477e-6, 5000);
        testOptRecursiveCons.runOptimization();
        //Third optimization:
        testOptRecursiveCons.updateParameters(testOptRecursiveCons.Q_res, testOptRecursiveCons.P, 1.0e-1, 1.49011611938477e-8, 5000);
        testOptRecursiveCons.runOptimization();
        //Fourth optimization:
        testOptRecursiveCons.updateParameters(testOptRecursiveCons.Q_res, testOptRecursiveCons.P, 0.5, 1.49011611938477e-4, 5000);
        testOptRecursiveCons.runOptimization();
        //Fifth optimization:
        testOptRecursiveCons.updateParameters(testOptRecursiveCons.Q_res, testOptRecursiveCons.P, 1.0e-4, 1.49011611938477e-8, 5000);
        testOptRecursiveCons.runOptimization();
        testOptRecursiveCons.saveOptRecursiveResults();*/

    }
    private static CobylaExitStatus optimizeFunctionSingleStage(OptRecursive_Cons optim, double rho_beg){
        testOptRecursiveCons.updateParameters(optim.Q_res, optim.P, 1.0e-10, rho_beg, 5000);        
        return testOptRecursiveCons.runOptimization() ;
    
    }

    
    private static final double eps = 2.2204e-16;
    private static final double v_seed = Math.sqrt(eps); 
    
    private static int sign(Double input) { 
        return (input < 0 ? -1 : 1); 
    } 
    
    private static Matrix sign(Matrix input) { 
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
    private static Matrix forwardFiniteDiffStepSize(Matrix x, Matrix typicalX) { 
//        Matrix foo = DIAS.maxMatrix(DIAS.absMatrix(x), typicalX); 
//        DIAS.printMatrix(DIAS.absMatrix(x), "absMatrix(x)");
//        DIAS.printMatrix(typicalX, "typicalX");
//        DIAS.printMatrix(foo, "DIAS.maxMatrix(DIAS.absMatrix(x), typicalX)");
//        DIAS.printMatrix(x, "x");
//        DIAS.printMatrix(sign(x), "sign(x)");
        Matrix tmp = sign(x).arrayTimes(DIAS.maxMatrix(DIAS.absMatrix(x), typicalX)); 
        return tmp.times(v_seed); 
    }

}
