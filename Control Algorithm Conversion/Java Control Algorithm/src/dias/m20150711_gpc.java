/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import Jama.Matrix;
import de.xypron.jcobyla.CobylaExitStatus;
import dias.MemoryStaticVariables.m20150711_load_global_variables;

/**
 * m20150711 GPC class -
 *
 * @author Mert
 */
public class m20150711_gpc {

    public Matrix gs;
    public Matrix ee;
    public Matrix gsr;
    public int kj;
    public Matrix phi;
    public Matrix phi_ee;
    public Matrix phi_gsr;
    public Matrix armax_parameters;
    public double[][][] armax_covariance;
    public Matrix armax_lamda;
    public Matrix armax_error;
    public Matrix armax_parameters_ee;
    public Matrix arma_lamda_ee;
    public double[][][] arma_covariance_ee;
    public Matrix arma_err_ee;
    public Matrix arma_parameters_gsr;
    public Matrix arma_lamda_gsr;
    public double[][][] arma_covariance_gsr;
    public Matrix arma_err_gsr;
    public double[][][] A_state;
    public double[][][] A_state_ee;
    public double[][][] A_state_gsr;
    public double[][][] C_state;
    public double[][][] C_state_ee;
    public double[][][] C_state_gsr;
    public double[][][] B_state;
    public double[][][] K_state;
    public double[][][] K_state_ee;
    public double[][][] K_state_gsr;
    public double[][][] M;
    public double[][][] L;
    public double[][][] L_ee;
    public double[][][] L_gsr;
    public double[][][] M_ee;
    public double[][][] M_gsr;
    public Matrix X_state;
    public Matrix X_state_ee;
    public Matrix X_state_gsr;
    public Matrix ee_prediction;
    public Matrix gsr_prediction;
    public Matrix g_prediction;
    public Matrix reference_glucose;
    public Matrix insulin_sensitivity_constant;
    public Matrix basal_insulin;
    public Matrix IOB_prediction;
    public Matrix max_insulin;
    public Matrix total_daily_unit;
    public Matrix insulin_sensitivity_factor;
    public double body_weight;
    public Matrix meal_gpc_mu;
    public Matrix bolus_insulin;

    public double flag_constrains;
    public Matrix g_prediction_feedback;

    public m20150711_gpc() {

    }

    /**
     * Constructor.
     *
     * Creates a new instance of the m20150711_gpc class.
     *
     * @param gs Matrix of glucose level data
     * @param ee Matrix of energy expenditure data
     * @param gsr Matrix of galvanic skin response data
     * @param kj Int
     * @param phi Matrix
     * @param phi_ee Matrix
     * @param phi_gsr Matrix
     * @param armax_parameters Matrix
     * @param armax_covariance double[][][]
     * @param armax_lamda Matrix
     * @param armax_error Matrix
     * @param armax_parameters_ee Matrix
     * @param arma_lamda_ee Matrix
     * @param arma_covariance_ee double[][][]
     * @param arma_err_ee Matrix
     * @param arma_parameters_gsr Matrix
     * @param arma_lamda_gsr Matrix
     * @param arma_covariance_gsr double[][][]
     * @param arma_err_gsr Matrix
     * @param A_state double[][][]
     * @param A_state_ee double[][][]
     * @param A_state_gsr double[][][]
     * @param C_state double[][][]
     * @param C_state_ee double[][][]
     * @param C_state_gsr double[][][]
     * @param B_state double[][][]
     * @param K_state double[][][]
     * @param K_state_ee double[][][]
     * @param K_state_gsr double[][][]
     * @param M double[][][]
     * @param L double[][][]
     * @param L_ee double[][][]
     * @param L_gsr double[][][]
     * @param M_ee double[][][]
     * @param M_gsr double[][][]
     * @param X_state Matrix
     * @param X_state_ee Matrix
     * @param X_state_gsr Matrix
     * @param ee_prediction Matrix
     * @param gsr_prediction Matrix
     * @param g_prediction Matrix
     * @param reference_glucose Matrix
     * @param insulin_sensitivity_constant Matrix
     * @param basal_insulin Matrix
     * @param IOB_prediction Matrix
     * @param max_insulin Matrix
     * @param total_daily_unit Matrix
     * @param insulin_sensitivity_factor Matrix
     * @param body_weight double, patient body weight
     * @param meal_gpc_mu Matrix
     * @param bolus_insulin Matrix
     * @param flag_constrains double
     * @param g_prediction_feedback Matrix
     */
    public m20150711_gpc(Matrix gs, Matrix ee, Matrix gsr, int kj, Matrix phi, Matrix phi_ee, Matrix phi_gsr, Matrix armax_parameters, double[][][] armax_covariance, Matrix armax_lamda, Matrix armax_error, Matrix armax_parameters_ee, Matrix arma_lamda_ee, double[][][] arma_covariance_ee, Matrix arma_err_ee, Matrix arma_parameters_gsr, Matrix arma_lamda_gsr, double[][][] arma_covariance_gsr, Matrix arma_err_gsr, double[][][] A_state, double[][][] A_state_ee, double[][][] A_state_gsr, double[][][] C_state, double[][][] C_state_ee, double[][][] C_state_gsr, double[][][] B_state, double[][][] K_state, double[][][] K_state_ee, double[][][] K_state_gsr, double[][][] M, double[][][] L, double[][][] L_ee, double[][][] L_gsr, double[][][] M_ee, double[][][] M_gsr, Matrix X_state, Matrix X_state_ee, Matrix X_state_gsr, Matrix ee_prediction, Matrix gsr_prediction, Matrix g_prediction, Matrix reference_glucose, Matrix insulin_sensitivity_constant, Matrix basal_insulin, Matrix IOB_prediction, Matrix max_insulin, Matrix total_daily_unit, Matrix insulin_sensitivity_factor, double body_weight, Matrix meal_gpc_mu, Matrix bolus_insulin, double flag_constrains, Matrix g_prediction_feedback) {
        this.arma_lamda_ee = arma_lamda_ee;
        this.arma_lamda_gsr = arma_lamda_gsr;
        this.A_state = A_state;
        this.A_state_ee = A_state_ee;
        this.A_state_gsr = A_state_gsr;
        this.B_state = B_state;
        this.C_state = C_state;
        this.C_state_ee = C_state_ee;
        this.C_state_gsr = C_state_gsr;
        this.IOB_prediction = IOB_prediction;
        this.K_state = K_state;
        this.K_state_ee = K_state_ee;
        this.K_state_gsr = K_state_gsr;
        this.L = L;
        this.L_ee = L_ee;
        this.L_gsr = L_gsr;
        this.M = M;
        this.M_ee = M_ee;
        this.M_gsr = M_gsr;
        //XXX OPTIMIZE : It's probably not necessary to pass X_state as a parameter, 
        // since the first thing we do with it is set it to a fresh matrix. 
        this.X_state = X_state;
        //XXX OPTIMIZE : It's probably not necessary to pass X_state_ee as a parameter, 
        // since the first thing we do with it is set it to a fresh matrix. 
        this.X_state_ee = X_state_ee;
        //XXX OPTIMIZE : It's probably not necessary to pass X_state_gsr as a parameter, 
        // since the first thing we do with it is set it to a fresh matrix.
        this.X_state_gsr = X_state_gsr;
        this.arma_covariance_ee = arma_covariance_ee;
        this.arma_covariance_gsr = arma_covariance_gsr;
        this.arma_err_ee = arma_err_ee;
        this.arma_err_gsr = arma_err_gsr;
        this.arma_parameters_gsr = arma_parameters_gsr;
        this.armax_covariance = armax_covariance;
        this.armax_lamda = armax_lamda;
        this.armax_error = armax_error;
        this.armax_parameters = armax_parameters;
        this.armax_parameters_ee = armax_parameters_ee;
        this.basal_insulin = basal_insulin;
        this.body_weight = body_weight;
        this.bolus_insulin = bolus_insulin;
        this.ee = ee;
        this.ee_prediction = ee_prediction;
        this.g_prediction = g_prediction;
        this.gs = gs;
        this.gsr = gsr;
        this.gsr_prediction = gsr_prediction;
        this.insulin_sensitivity_constant = insulin_sensitivity_constant;
        this.insulin_sensitivity_factor = insulin_sensitivity_factor;
        this.kj = kj;
        this.max_insulin = max_insulin;
        this.meal_gpc_mu = meal_gpc_mu;
        this.phi = phi;
        this.phi_ee = phi_ee;
        this.phi_gsr = phi_gsr;
        this.reference_glucose = reference_glucose;
        this.meal_gpc_mu = meal_gpc_mu;
        this.total_daily_unit = total_daily_unit;
        this.flag_constrains = flag_constrains;
        this.g_prediction_feedback = g_prediction_feedback;
    }

    /**
     *
     */
    public void gpc() {

        /* /////////////////////////////////////////////////////INPUTS//////////////////////////////////////////////////////////////////////////////////
        System.out.println("/////////////////////////////////INPUTS////////////////////////////////////////////////////////////////////////////////");
        printMatrix(gs,"gs");
        printMatrix(ee,"ee");
        printMatrix(gsr,"gsr");
        System.out.println("kj   "+kj);
        printMatrix(phi,"phi");
        printMatrix(phi_ee,"phi_ee");
        printMatrix(phi_gsr,"phi_gsr");
        printMatrix(armax_parameters,"armax_parameters");
        printMatrix(armax_lamda,"armax_lamda");
        printMatrix(armax_error,"armax_error");
        printMatrix(armax_parameters_ee,"armax_parameters_ee");
        printMatrix(arma_lamda_ee,"arma_lamda_ee");
        printMatrix(arma_err_ee,"arma_err_ee");
        printMatrix(arma_parameters_gsr,"arma_parameters_gsr");
        printMatrix(arma_lamda_gsr,"arma_lamda_gsr");
        printMatrix(arma_err_gsr,"arma_err_gsr");
        printMatrix(X_state,"X_state");
        printMatrix(X_state_ee,"X_state_gsr");
        printMatrix(ee_prediction,"ee_prediction");
        printMatrix(gsr_prediction,"gsr_prediction");
        printMatrix(g_prediction,"g_prediction");
        printMatrix(reference_glucose,"reference_glucose");
        printMatrix(insulin_sensitivity_constant,"insulin_sensitivity_constant");
        printMatrix(basal_insulin,"basal_insulin");
        printMatrix(max_insulin,"max_insulin");
        printMatrix(insulin_sensitivity_factor,"insulin_sensitivity_factor");
        printMatrix(g_prediction_feedback,"g_prediction_feedback");
        System.out.println(flag_constrains+"flag_constrains");
        printMatrix(meal_gpc_mu,"meal_gpc_mu");     
        printMatrix(total_daily_unit,"total_daily_unit");
        print3DMatrice(armax_covariance,"armax_covariance");
        print3DMatrice(arma_covariance_ee,"arma_covariance_ee");
        print3DMatrice(arma_covariance_gsr,"arma_covariance_gsr");
        print3DMatrice(A_state,"A_state");
        print3DMatrice(A_state_ee,"A_state_ee");
        print3DMatrice(A_state_gsr,"A_state_gsr");
        print3DMatrice(C_state,"C_state");
        print3DMatrice(C_state_ee,"C_state_ee");
        print3DMatrice(C_state_gsr,"C_state_gsr");
        print3DMatrice(K_state,"K_state");
        print3DMatrice(K_state_ee,"K_state_ee");
        print3DMatrice(K_state_gsr,"K_state_gsr");
        print3DMatrice(M,"M_state");
        print3DMatrice(M_ee,"M_state_ee");
        print3DMatrice(M_gsr,"M_state_gsr");
        print3DMatrice(L,"L_state");
        print3DMatrice(L_ee,"L_state_ee");
        print3DMatrice(L_gsr,"L_state_gsr");
        print3DMatrice(B_state,"B_state");
        System.out.println("/////////////////////////////////INPUTS////////////////////////////////////////////////////////////////////////////////");
        /////////////////////////////////////////////////////INPUTS//////////////////////////////////////////////////////////////////////////////////*/
        int N1 = 2;
        int N2 = 10;
        int Nu = 8;
        int na = 3;
        int nb1 = 12;
        int nb2 = 4;
        int nb3 = 4;
        int nc = 1;
        int st = 5;
        int d2 = 1;
        int d3 = 1; //% Prediction and control horizons. Model orders and delays.

        //////////////////////////////////////////////////True/////////////////////////////////////////////////////////////////////////////////////    
        double[][] IOB = new double[1][15];
        IOB[0][0] = 0.974082840593171;
        IOB[0][1] = 0.932309038992748;
        IOB[0][2] = 0.847452388537183;
        IOB[0][3] = 0.724680039139838;
        IOB[0][4] = 0.584206488705884;
        IOB[0][5] = 0.448368344074227;
        IOB[0][6] = 0.333152936238220;
        IOB[0][7] = 0.244180789845194;
        IOB[0][8] = 0.177141946974804;
        IOB[0][9] = 0.122686145196158;
        IOB[0][10] = 0.0757668499037829;
        IOB[0][11] = 0.0494391409323673;

        double[] upperlim = new double[na + nb1 + nb2 + nb3 + nc];

        for (int i = 0; i < na + nb1 + nb2 + nb3 + nc; i++) {
            if (i < na + nb1 + nb2 && i >= na) {
                upperlim[i] = 0;
            } else {
                upperlim[i] = 1;
            }
        }

        double[] lowerlim = new double[na + 12 + nb2 + nb3 + nc];

        for (int i = 0; i < na; i++) {
            lowerlim[i] = -1;
        }

        for (int i = na; i < na + 12; i++) {
            lowerlim[i] = IOB[0][i - na] * (-1);
        }

        for (int i = na + 12; i < na + 12 + nb2; i++) {
            lowerlim[i] = -1;
        }

        for (int i = na + 12 + nb2; i < na + 12 + nb2 + nb3; i++) {
            lowerlim[i] = 0;
        }

        for (int i = na + 12 + nb2 + nb3; i < na + 12 + nb2 + nb3 + nc; i++) {
            lowerlim[i] = -1;
        }
        //////////////////////////////////////////////////True/////////////////////////////////////////////////////////////////////////////////////           
        /*  /////////////////////////////////////////////////Test///////////////////////////////////////////////////////////////////////////////////////
       printMatrix(ee,"ee");
       printMatrix(gsr,"gsr");
       printMatrix(arma_err_gsr,"arma_err_gsr");
       printMatrix(arma_err_ee,"arma_err_ee");
       printMatrix(gs,"gs");
       printMatrix(basal_insulin,"basal_insulin");
       printMatrix(armax_error,"armax_err");
       printMatrix(armax_parameters,"armax_parameters");
       print3Dmatrice(armax_covariance,"armax_covariance");
       printMatrix(armax_lamda,"armax_lamda");
       printMatrix(arma_lamda_ee,"armax_lamda_ee");
       printMatrix(arma_lamda_gsr,"armax_lamda_gsr");
       printMatrix(armax_parameters_ee,"arma_parameters_ee");
       printMatrix(arma_parameters_gsr,"arma_parameters_gsr");
       print3Dmatrice(arma_covariance_gsr,"arma_covariance_gsr");
       print3Dmatrice(arma_covariance_ee,"arma_covariance_ee");
       printMatrix(g_prediction_feedback,"g_prediction_feedback");
       printMatrix(meal_gpc_mu,"meal_gpc_mu");
     /////////////////////////////////////////////////Test///////////////////////////////////////////////////////////////////////////////////////  */
        phi = createnewMatrix(na + nb1 + nb2 + nb2 + nc, kj + 1, phi);

        for (int i = 0; i < na; i++) {
            phi.set((na - 1) - i, kj, gs.get(0, kj - na - 2 + i) * (-1));
        }

        for (int i = na; i < na + nb1; i++) {
            phi.set((na + nb1 - 1) - i + na, kj, basal_insulin.get(0, kj - nb1 - 5 - N1 + i));
        }

        for (int i = na + nb1; i < na + nb2 + nb1; i++) {
            phi.set((na + nb2 + nb1 - 1) - i + na + nb1, kj, ee.get(0, kj - nb2 - d2 + i - na - nb1 - 1));
        }

        for (int i = na + nb1 + nb2; i < na + nb1 + nb2 + nb2; i++) {
            phi.set((na + nb1 + nb2 + nb2 - 1) - i + na + nb1 + nb2, kj, gsr.get(0, kj - nb2 - d3 + i - 1 - (na + nb1 + nb2)));
        }

        for (int i = na + nb1 + nb2 + nb2; i < na + nb1 + nb2 + nb2 + nc; i++) {
            phi.set((na + nb1 + nb2 + nb2 + nc - 1) - i + na + nb1 + nb2 + nb2, kj, armax_error.get(kj - nc + i - 2 - (na + nb1 + nb2 + nb2), 0));
        }

        phi_ee = createnewMatrix(4, kj + 1, phi_ee);
        phi_gsr = createnewMatrix(4, kj + 1, phi_gsr);

        phi_ee.set(0, kj, -ee.get(0, kj - 2));
        phi_ee.set(1, kj, -ee.get(0, kj - 3));
        phi_ee.set(2, kj, -ee.get(0, kj - 4));
        phi_ee.set(3, kj, -arma_err_ee.get(kj - 2, 0));

        // phi_ee=phi_ee.transpose();
        Matrix phi_ee_trans = new Matrix(4, kj + 1);
        phi_ee_trans = phi_ee.transpose();

        phi_gsr.set(0, kj, -gsr.get(0, kj - 2));
        phi_gsr.set(1, kj, -gsr.get(0, kj - 3));
        phi_gsr.set(2, kj, -gsr.get(0, kj - 4));
        phi_gsr.set(3, kj, arma_err_gsr.get(kj - 2, 0));

        Matrix phi_gsr_trans = new Matrix(4, kj + 1);
        //   phi_gsr=phi_gsr.transpose();
        phi_gsr_trans = phi_gsr.transpose();

        Matrix phitemp = new Matrix(phi.getRowDimension(), 1);

        for (int i = 0; i < phi.getRowDimension(); i++) {
            phitemp.set(i, 0, phi.get(i, kj));
        }

        Matrix armax_parameterstemp = new Matrix(armax_parameters.getRowDimension(), 1);

        for (int i = 0; i < armax_parameters.getRowDimension(); i++) {
            armax_parameterstemp.set(i, 0, armax_parameters.get(i, kj - 1));
        }

        Matrix armax_covariancetemp = new Matrix((lastvaluereturnxyz(armax_covariance)[1] + 1), (lastvaluereturnxyz(armax_covariance)[2] + 1));

        for (int i = 0; i < (lastvaluereturnxyz(armax_covariance)[1] + 1); i++) {
            for (int j = 0; j < (lastvaluereturnxyz(armax_covariance)[2] + 1); j++) {
                armax_covariancetemp.set(i, j, armax_covariance[i][j][kj - 1]);
            }
        }
// <editor-fold defaultstate="collapsed" desc=" Run nonlinear optimization (OptRecursive class's .runOptimization method) based on armax parameters.Set lgvariables armax properties based on output.  ">

// (double Y, Matrix phi, Matrix Q, Matrix P, double lambda, double err, Matrix Ymodel, double[] upperlimit, double[] lowerlimit, CobylaExitStatus exit) {

        OptInputs inputs = new OptInputs((gs.get(0, kj - 1)), phitemp,
                armax_parameterstemp, armax_covariancetemp, (armax_lamda.get(kj - 1, 0)), 0, 
                upperlim, lowerlim, null);
        //TODO Recursive Optimization
        OptRecursive optRecursiveCons = new OptRecursive(inputs);
        //set the inputs to be the output of the current run. 
        inputs = optRecursiveCons.runOptimization();
        //TODO !!!
        //Load variables 
        m20150711_load_global_variables lgvariables = new m20150711_load_global_variables();

        //Update the values with Q and results of the optimization:
        //1. Initializate matrices
        lgvariables.armax_parameters = DIAS.createnewMatrix(inputs.q().getRowDimension(), kj + 1, lgvariables.armax_parameters);
        lgvariables.armax_lamda = DIAS.createnewMatrix(kj + 1, 1, armax_lamda);
        lgvariables.armax_err = DIAS.createnewMatrix(kj + 1, 1, armax_error);
        lgvariables.armax_covariance = createnew3Dmatrix(lgvariables.armax_covariance, inputs.p().getRowDimension(), inputs.p().getColumnDimension(), kj + 1);
        lgvariables.armax_lamda.set(kj, 0, inputs.lambda());
        lgvariables.armax_err.set(kj, 0, inputs.err());

        //2. Update their values:
        for (int i = 0; i < inputs.q().getRowDimension(); i++) {
            lgvariables.armax_parameters.set(i, kj, inputs.q().get(i, 0));
        }

        for (int i = 0; i < inputs.p().getColumnDimension(); i++) {
            for (int j = 0; j < inputs.p().getRowDimension(); j++) {
                lgvariables.armax_covariance[i][j][kj] = inputs.p().get(i, j);
            }
        }

// </editor-fold>
// <editor-fold defaultstate="collapsed" desc=" Set up variables for opt_recursive_arm.optrecursive processing based on energy expenditure. ">
        Matrix phi_temp = new Matrix(phi_ee_trans.getColumnDimension(), 1);

        for (int i = 0; i < phi_ee_trans.getColumnDimension(); i++) {
            phi_temp.set(i, 0, phi_ee_trans.get(kj, i));
        }

        Matrix armax_parameters_ee_temp = new Matrix(armax_parameters_ee.getRowDimension(), 1);

        for (int i = 0; i < armax_parameters_ee.getRowDimension(); i++) {
            armax_parameters_ee_temp.set(i, 0, armax_parameters_ee.get(i, kj - 1));
        }

        Matrix arma_covariance_ee_temp = new Matrix((lastvaluereturnxyz(arma_covariance_ee)[1] + 1), (lastvaluereturnxyz(arma_covariance_ee)[2] + 1));

        for (int i = 0; i < (lastvaluereturnxyz(arma_covariance_ee)[1] + 1); i++) {
            for (int j = 0; j < (lastvaluereturnxyz(arma_covariance_ee)[2] + 1); j++) {
                arma_covariance_ee_temp.set(i, j, arma_covariance_ee[i][j][kj - 1]);
            }
        }

        for (int i = 0; i < armax_parameters_ee.getRowDimension(); i++) {
            armax_parameters_ee_temp.set(i, 0, armax_parameters_ee.get(i, kj - 1));
        }

        double[] onesmatrice = new double[4];
        double[] minusonesmatrice = new double[4];

        for (int i = 0; i < 4; i++) {
            onesmatrice[i] = 1;
            minusonesmatrice[i] = -1;
        }

// </editor-fold>
// <editor-fold defaultstate="collapsed" desc=" Run opt_recursive_arm.optrecursive based on energy expenditure. Set lgvariables energy expenditure properties based on output. ">
/* 
We create a new instance of opt_recursive_arm called "orarm" (NB that the last three variables of the constructor are hardcoded).
		
Now that the new instance of opt_recursive_arm ("orarm") has been set, we initialize all the same lgvariables properties that we stored the OptRecursive data in, but here they all have _ee (energy expenditure) suffixes : 
    arma_parameters_ee gets the output from orarm.Q_res
    arma_lamda_ee gets the lgvariables.arma_lamda_ee value (did this get changed by orarm?)
    arma_covariance_ee gets the orarm.P value
    arma_err_ee gets the lgvariables.arma_err_ee value
    The kj / 0th place in arma_lamda_ee gets the orarm.lamda value
    The kj / 0th place in arma_err_ee gets the orarm.err value
         */
        opt_recursive_arm orarm = new opt_recursive_arm(ee.get(0, kj), phi_temp, armax_parameters_ee_temp, arma_covariance_ee_temp, arma_lamda_ee.get(kj - 1, 0), arma_err_ee.get(kj - 1, 0), onesmatrice, minusonesmatrice, 0.99, 0.9, 0.005);
        orarm.optrecursive();

        //Update the values with Q and results of the optimization:
        //1. Initializate matrices
        lgvariables.arma_parameters_ee = DIAS.createnewMatrix(orarm.Q_res.getRowDimension(), kj + 1, lgvariables.arma_parameters_ee);
        lgvariables.arma_lamda_ee = DIAS.createnewMatrix(kj + 1, 1, lgvariables.arma_lamda_ee);
        lgvariables.arma_covariance_ee = createnew3Dmatrix(lgvariables.arma_covariance_ee, orarm.P.getRowDimension(), orarm.P.getColumnDimension(), kj + 1);
        lgvariables.arma_err_ee = DIAS.createnewMatrix(kj + 1, 1, lgvariables.arma_err_ee);
        lgvariables.arma_lamda_ee.set(kj, 0, orarm.lamda);
        lgvariables.arma_err_ee.set(kj, 0, orarm.err);

        //2. Update their values:
        for (int i = 0; i < orarm.Q_res.getRowDimension(); i++) {
            lgvariables.arma_parameters_ee.set(i, kj, orarm.Q_res.get(i, 0));
        }

        for (int i = 0; i < orarm.P.getColumnDimension(); i++) {
            for (int j = 0; j < orarm.P.getRowDimension(); j++) {
                lgvariables.arma_covariance_ee[i][j][kj] = orarm.P.get(i, j);
            }
        }

// </editor-fold>
//<editor-fold defaultstate="collapsed" desc=" Set up variables for opt_recursive_arm.optrecursive processing based on galvanic skin response.  ">
        Matrix phi_gsr_temp = new Matrix(phi_gsr_trans.getColumnDimension(), 1);

        for (int i = 0; i < phi_gsr_trans.getColumnDimension(); i++) {
            phi_gsr_temp.set(i, 0, phi_gsr_trans.get(kj, i));
        }

        Matrix armax_parameters_gsr_temp = new Matrix(arma_parameters_gsr.getRowDimension(), 1);

        for (int i = 0; i < arma_parameters_gsr.getRowDimension(); i++) {
            armax_parameters_gsr_temp.set(i, 0, arma_parameters_gsr.get(i, kj - 1));
        }

        Matrix arma_covariance_gsr_temp = new Matrix((lastvaluereturnxyz(arma_covariance_gsr)[1] + 1), (lastvaluereturnxyz(arma_covariance_gsr)[2] + 1));

        for (int i = 0; i < (lastvaluereturnxyz(arma_covariance_gsr)[1] + 1); i++) {
            for (int j = 0; j < (lastvaluereturnxyz(arma_covariance_gsr)[2] + 1); j++) {
                arma_covariance_gsr_temp.set(i, j, arma_covariance_gsr[i][j][kj - 1]);
            }
        }

// </editor-fold>
// <editor-fold defaultstate="collapsed" desc=" Run opt_recursive_arm.optrecursive based on galvanic skin response. Set lgvariables galvanic skin response properties based on output. ">

        /* 
We now instantiate a new opt_recursive_arm, "orarm2" with the same parameters as orarm, except the following : 
    Instead of phi_ee_trans, the phi parameter is based on phi_gsr_trans. 
    Instead of armax_parameters_ee, the armax parameter is based on armax_parameters_gsr. 
The optrecursive() method of orarm2 is triggered, and returns control immediately to this method, so that this method can use the orarm2 parameters altered by the optrecursive() method.
         */
        opt_recursive_arm orarm2 = new opt_recursive_arm(gsr.get(0, kj), phi_gsr_temp, armax_parameters_gsr_temp, arma_covariance_gsr_temp, arma_lamda_gsr.get(kj - 1, 0), arma_err_gsr.get(kj - 1, 0), onesmatrice, minusonesmatrice, 0.99, 0.9, 0.005);
        orarm2.optrecursive();
        /* 
With orarm2.optrecursive() complete, the properties of lgvariables now get set the following way : 
    arma_parameters_gsr gets the value of orarm2.Q_res
    arma_lamda_gsr gets the value of orarm2.lamda
    arma_covariance_gsr gets the value of orarm2.P
    arma_err_gsr gets the value of orarm2.err
         */
        lgvariables.arma_parameters_gsr = DIAS.createnewMatrix(orarm2.Q_res.getRowDimension(), kj + 1, lgvariables.arma_parameters_gsr);
        lgvariables.arma_lamda_gsr = DIAS.createnewMatrix(kj + 1, 1, lgvariables.arma_lamda_gsr);
        lgvariables.arma_covariance_gsr = createnew3Dmatrix(lgvariables.arma_covariance_gsr, orarm2.P.getRowDimension(), orarm2.P.getColumnDimension(), kj + 1);
        lgvariables.arma_err_gsr = DIAS.createnewMatrix(kj + 1, 1, lgvariables.arma_err_gsr);

        lgvariables.arma_lamda_gsr.set(kj, 0, orarm2.lamda);
        lgvariables.arma_err_gsr.set(kj, 0, orarm2.err);

        for (int i = 0; i < orarm2.Q_res.getRowDimension(); i++) {
            lgvariables.arma_parameters_gsr.set(i, kj, orarm2.Q_res.get(i, 0));
        }

        for (int i = 0; i < orarm2.P.getColumnDimension(); i++) {
            for (int j = 0; j < orarm2.P.getRowDimension(); j++) {
                lgvariables.arma_covariance_gsr[i][j][kj] = orarm2.P.get(i, j);
            }
        }

// </editor-fold>
// <editor-fold defaultstate="collapsed" desc=" Set up a bunch of short-term matrices based on lgvariable properties, and also do some updates of those properties based on the optimization that just occurred. We'll use these for future processing. ">

        /* 
Build a bunch of new matrices based on the updated lgvariables properties.
    The first set of source properties were those updated by the OptRecursive optimization based on glucose levels (gs) : 
    From lgvariables.armax_parameters : 
            Values 0-2 go to matrix "A". 
            Values 3-14 go to matrix "B1". 
            Values 15-18 go to matrix "B2". 
            Values 19-22 go to matrix "B3". 
            Value 23 goes into "C".
    From lgvariables.armax_parameters_ee : 
            Values 0-2 go to matrix "Aee"
            Value 3 goes to matrix "Cee" 
    From lgvariables.armax_parameters_gsr : 
            Values 0-2 go to matrix "Agsr". 
            Value 3 goes to matrix "Cgsr".
         */
        Matrix A = new Matrix(1, 3);
        Matrix Aee = new Matrix(1, 3);
        Matrix Agsr = new Matrix(1, 3);
        Matrix B1 = new Matrix(1, 12);
        Matrix B2 = new Matrix(1, 4);
        Matrix B3 = new Matrix(1, 4);
        double C;
        double Cee;
        double Cgsr;

        for (int i = 0; i < 3; i++) {
            A.set(0, i, lgvariables.armax_parameters.get(i, kj));
        }

        for (int i = 3; i < 15; i++) {
            B1.set(0, i - 3, lgvariables.armax_parameters.get(i, kj));
        }

        for (int i = 15; i < 19; i++) {
            B2.set(0, i - 15, lgvariables.armax_parameters.get(i, kj));
        }

        for (int i = 19; i < 23; i++) {
            B3.set(0, i - 19, lgvariables.armax_parameters.get(i, kj));
        }

        C = lgvariables.armax_parameters.get(23, kj);

        for (int i = 0; i < 3; i++) {
            Aee.set(0, i, lgvariables.arma_parameters_ee.get(i, kj));
        }

        Cee = lgvariables.arma_parameters_ee.get(3, kj);

        for (int i = 0; i < 3; i++) {
            Agsr.set(0, i, lgvariables.arma_parameters_gsr.get(i, kj));
        }

        Cgsr = lgvariables.arma_parameters_gsr.get(3, kj);

        /*
Update lgvariables again. The following lgvariables properties are initialized with the values passed as parameters to the m20150711_gpc constructor. Then they get updated as follows : 
    lgvariables.A_state gets a subset of values from matrix "A" (multiplied by -1), matrix "B1", matrix "B2", matrix "B3", and "C", and then gets what looks like a 3D diagonal set to ones. 
    lgvariables.A_state_ee gets a subset of values from Aee and Cee. 
    lgvariables.A_state_gsr gets a subset of values from Agsr and Cgsr. 
    lgvariables.C_state gets a subset of values from A_state. 
    lgvariables.C_state_ee gets a subset of values from A_state_ee. 
    lgvariables.C_state_gsr gets a subset of values from A_state_gsr. 
    lgvariables.B_state gets a subset of values from "B1", "B2", and "B3". 
    lgvariables.K_state, K_state_ee, and K_state_gsr get their initialized values, but are overlaid with a certain pattern of ones and zeroes, which produce something like a partial (?) 3D identity matrix.
         */
        A_state = createnew3Dmatrix(A_state, 22, 24, kj + 1);
        A_state_ee = createnew3Dmatrix(A_state_ee, 4, 4, kj + 1);
        A_state_gsr = createnew3Dmatrix(A_state_gsr, 4, 4, kj + 1);

        for (int i = 0; i < A.getColumnDimension(); i++) {
            A_state[0][i][kj] = A.get(0, i) * (-1);
        }

        for (int i = 1; i < B1.getColumnDimension(); i++) {
            A_state[0][i + A.getColumnDimension() - 1][kj] = B1.get(0, i);
        }

        for (int i = 1; i < B2.getColumnDimension(); i++) {
            A_state[0][i + A.getColumnDimension() + B1.getColumnDimension() - 2][kj] = B2.get(0, i);
        }

        for (int i = 1; i < B3.getColumnDimension(); i++) {
            A_state[0][i + A.getColumnDimension() + B1.getColumnDimension() + B2.getColumnDimension() - 3][kj] = B3.get(0, i);
        }

        A_state[0][20][kj] = C;

        A_state[1][0][kj] = 1;
        A_state[2][1][kj] = 1;
        A_state[4][3][kj] = 1;
        A_state[5][4][kj] = 1;
        A_state[6][5][kj] = 1;
        A_state[7][6][kj] = 1;
        A_state[8][7][kj] = 1;
        A_state[9][8][kj] = 1;
        A_state[10][9][kj] = 1;
        A_state[11][10][kj] = 1;
        A_state[12][11][kj] = 1;
        A_state[13][12][kj] = 1;
        A_state[15][14][kj] = 1;
        A_state[16][15][kj] = 1;
        A_state[18][17][kj] = 1;
        A_state[19][18][kj] = 1;

        for (int i = 1; i < 21; i++) {
            for (int j = 0; j < 21; j++) {
                if (A_state[i][j][kj] != 1) {
                    A_state[i][j][kj] = 0;
                }
            }
        }

        lgvariables.A_state = createnew3Dmatrix(lgvariables.A_state, 22, 24, kj + 1);
        lgvariables.A_state = A_state;

        A_state_ee[0][0][kj] = (Aee.get(0, 0)) * (-1);
        A_state_ee[0][1][kj] = (Aee.get(0, 1)) * (-1);
        A_state_ee[0][2][kj] = (Aee.get(0, 2)) * (-1);
        A_state_ee[0][3][kj] = Cee;

        A_state_ee[1][0][kj] = 1;
        A_state_ee[2][1][kj] = 1;

        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (A_state_ee[i][j][kj] != 1) {
                    A_state_ee[i][j][kj] = 0;
                }
            }
        }

        lgvariables.A_state_ee = createnew3Dmatrix(lgvariables.A_state_ee, 4, 4, kj + 1);
        lgvariables.A_state_ee = A_state_ee;

        A_state_gsr[0][0][kj] = (Agsr.get(0, 0)) * (-1);
        A_state_gsr[0][1][kj] = (Agsr.get(0, 1)) * (-1);
        A_state_gsr[0][2][kj] = (Agsr.get(0, 2)) * (-1);
        A_state_gsr[0][3][kj] = Cgsr;

        A_state_gsr[1][0][kj] = 1;
        A_state_gsr[2][1][kj] = 1;

        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (A_state_gsr[i][j][kj] != 1) {
                    A_state_gsr[i][j][kj] = 0;
                }
            }
        }

        lgvariables.A_state_gsr = createnew3Dmatrix(lgvariables.A_state_gsr, 4, 4, kj + 1);
        lgvariables.A_state_gsr = A_state_gsr;

        C_state = createnew3Dmatrix(C_state, 1, 22, kj + 1);
        C_state_ee = createnew3Dmatrix(C_state_ee, 1, 4, kj + 1);
        C_state_gsr = createnew3Dmatrix(C_state_gsr, 1, 4, kj + 1);

        for (int i = 0; i < 22; i++) {
            C_state[0][i][kj] = A_state[0][i][kj];
        }

        lgvariables.C_state = createnew3Dmatrix(lgvariables.C_state, 1, 22, kj + 1);
        lgvariables.C_state = C_state;

        for (int i = 0; i < (lastvaluereturnxyz(A_state_ee)[1] + 2); i++) {
            C_state_ee[0][i][kj] = A_state_ee[0][i][kj];
        }

        lgvariables.C_state_ee = createnew3Dmatrix(lgvariables.C_state_ee, 1, 4, kj + 1);
        lgvariables.C_state_ee = C_state_ee;

        for (int i = 0; i < (lastvaluereturnxyz(A_state_gsr)[1] + 2); i++) {
            C_state_gsr[0][i][kj] = A_state_gsr[0][i][kj];
        }

        lgvariables.C_state_gsr = createnew3Dmatrix(lgvariables.C_state_gsr, 1, 4, kj + 1);
        lgvariables.C_state_gsr = C_state_gsr;

        B_state = createnew3Dmatrix(B_state, 21, 3, kj + 1);

        B_state[0][0][kj] = B1.get(0, 0);
        B_state[0][1][kj] = B2.get(0, 0);
        B_state[0][2][kj] = B3.get(0, 0);

        for (int i = 1; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                B_state[i][j][kj] = 0;
            }
        }

        B_state[3][0][kj] = 1;
        B_state[3][1][kj] = 0;
        B_state[3][2][kj] = 0;

        for (int i = 4; i < 14; i++) {
            for (int j = 0; j < 2; j++) {
                B_state[i][j][kj] = 0;
            }
        }

        B_state[14][0][kj] = 0;
        B_state[14][1][kj] = 1;
        B_state[14][2][kj] = 0;

        for (int i = 15; i < 17; i++) {
            for (int j = 0; j < 2; j++) {
                B_state[i][j][kj] = 0;
            }
        }

        B_state[17][0][kj] = 0;
        B_state[17][1][kj] = 0;
        B_state[17][2][kj] = 1;

        for (int i = 18; i < 21; i++) {
            for (int j = 0; j < 2; j++) {
                B_state[i][j][kj] = 0;
            }
        }

        lgvariables.B_state = createnew3Dmatrix(lgvariables.B_state, 21, 3, kj + 1);
        lgvariables.B_state = B_state;

        K_state = createnew3Dmatrix(K_state, 21, 1, kj + 1);
        K_state_ee = createnew3Dmatrix(K_state_ee, 4, 1, kj + 1);
        K_state_gsr = createnew3Dmatrix(K_state_gsr, 4, 1, kj + 1);

        for (int i = 1; i < 20; i++) {
            K_state[i][0][kj] = 0;
        }

        K_state[0][0][kj] = 1;
        K_state[20][0][kj] = 1;
        K_state_ee[0][0][kj] = 1;
        K_state_ee[1][0][kj] = 0;
        K_state_ee[2][0][kj] = 0;
        K_state_ee[3][0][kj] = 1;
        K_state_gsr[0][0][kj] = 1;
        K_state_gsr[1][0][kj] = 0;
        K_state_gsr[2][0][kj] = 0;
        K_state_gsr[3][0][kj] = 1;

        lgvariables.K_state = K_state;

        lgvariables.K_state_ee = K_state_ee;

        lgvariables.K_state_gsr = K_state_gsr;
// </editor-fold>     
// <editor-fold defaultstate="collapsed" desc=" Get controller horizon and update lgvariables parameters L, L_ee, L_gsr, and M. ">
/* 
We first build a couple of temp matrices in preparation for creating a controller_horizons instance. 
    A_state_temp is a 21x21 principal submatrix of the kj_th matrix in A_state
    B_state_temp is a 21x3 subset of the principal submatrix of the kj_th matrix in B_state
    C_state_temp is a 1x21 vector based on the kj_th matrix in C_state

    The "cont_hor" instance of controller_horizons is now instantiated, using A_state_temp, B_state_temp, C_state_temp, N1 (in the current code, set to 2), N2 (in the current code, set to N2), and Nu (in the current code, set to 8). 
    The method "calculate_horizons" is called on cont_hor and the control of flow returns to this function. 

    The parameters of the cont_hor instance of the controller_horizons class are now used to repopulate the parameters of lgvariables : 
    lgvariables.M is updated with values from cont_hor.M
    lgvriables.L is updated with values from cont_hor.LL
    lgvariables.L_ee is updated with values from cont_hor.LL_ee
    lgvariables.L_gsr is updated with values from cont_hor.LL_gsr
         */
        Matrix A_state_temp = new Matrix(21, 21);

        for (int i = 0; i < 21; i++) {
            for (int j = 0; j < 21; j++) {
                A_state_temp.set(i, j, A_state[i][j][kj]);
            }
        }

        Matrix B_state_temp = new Matrix(21, 3);

        for (int i = 0; i < 21; i++) {
            for (int j = 0; j < 3; j++) {
                B_state_temp.set(i, j, B_state[i][j][kj]);
            }
        }

        Matrix C_state_temp = new Matrix(1, 21);

        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 21; j++) {
                C_state_temp.set(i, j, C_state[i][j][kj]);
            }
        }

        if (DIAS.verboseMode) {
            System.out.println("N1-N2 and Nu: " + (N1 - N2) + " X " + Nu);
        }
        controller_horizons cont_hor = new controller_horizons(A_state_temp, B_state_temp, C_state_temp, N1, N2, Nu);
        cont_hor.calculateHorizons();

        lgvariables.L = createnew3Dmatrix(lgvariables.L, cont_hor.LL.getRowDimension(), cont_hor.LL.getColumnDimension(), kj + 1);
        lgvariables.L_ee = createnew3Dmatrix(lgvariables.L_ee, cont_hor.LL_ee.getRowDimension(), cont_hor.LL_ee.getColumnDimension(), kj + 1);
        lgvariables.L_gsr = createnew3Dmatrix(lgvariables.L_gsr, cont_hor.LL_gsr.getRowDimension(), cont_hor.LL_gsr.getColumnDimension(), kj + 1);
        lgvariables.M = createnew3Dmatrix(lgvariables.M, cont_hor.M.getRowDimension(), cont_hor.M.getColumnDimension(), kj + 1);

        for (int i = 0; i < cont_hor.M.getRowDimension(); i++) {
            for (int j = 0; j < cont_hor.M.getColumnDimension(); j++) {
                lgvariables.M[i][j][kj] = cont_hor.M.get(i, j);
            }
        }

        for (int i = 0; i < cont_hor.LL.getRowDimension(); i++) {
            for (int j = 0; j < cont_hor.LL.getRowDimension(); j++) {
                lgvariables.L[i][j][kj] = cont_hor.LL.get(i, j);
            }
        }

        for (int i = 0; i < cont_hor.LL_ee.getRowDimension(); i++) {
            for (int j = 0; j < cont_hor.LL_ee.getRowDimension(); j++) {
                lgvariables.L_ee[i][j][kj] = cont_hor.LL_ee.get(i, j);
            }
        }

        for (int i = 0; i < cont_hor.LL_gsr.getRowDimension(); i++) {
            for (int j = 0; j < cont_hor.LL_gsr.getRowDimension(); j++) {
                lgvariables.L_gsr[i][j][kj] = cont_hor.LL_gsr.get(i, j);
            }
        }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc=" Get prediction horizon based on energy expenditure and update lgvariables.M_ee. ">
/* 
In preparation for instantiating a new predication_horizon, we set up some new matrices : 
    A_state_temp_ee is based on A_state_ee
    C_state_temp_ee is based on C_state_ee

    The "p_hor" instance of prediction_horizon is now instantiated, using A_state_temp_ee, C_state_temp_ee, N1, and N2. 
    The output of p_hor's prediction_horizons() method is stored in a new matrix, M_ee_temp. This matrix is used to update lgvariables.M_ee. 
         */
        Matrix A_state_temp_ee = new Matrix(4, 4);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                A_state_temp_ee.set(i, j, A_state_ee[i][j][kj]);
            }
        }

        Matrix C_state_temp_ee = new Matrix((lastvaluereturnxyz(C_state_ee))[1] + 1, (lastvaluereturnxyz(C_state_ee))[2] + 1);

        for (int i = 0; i < (lastvaluereturnxyz(C_state_ee))[1] + 1; i++) {
            for (int j = 0; j < (lastvaluereturnxyz(C_state_ee))[2] + 1; j++) {
                C_state_temp_ee.set(i, j, C_state_ee[i][j][kj]);
            }
        }

        prediction_horizon p_hor = new prediction_horizon(A_state_temp_ee, C_state_temp_ee, N1, N2);
        Matrix M_ee_temp = p_hor.prediction_horizons();

        lgvariables.M_ee = createnew3Dmatrix(lgvariables.M_ee, p_hor.M.getRowDimension(), p_hor.M.getColumnDimension(), kj + 1);

        for (int i = 0; i < p_hor.M.getRowDimension(); i++) {
            for (int j = 0; j < p_hor.M.getColumnDimension(); j++) {
                lgvariables.M_ee[i][j][kj] = M_ee_temp.get(i, j);
            }
        }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc=" Get prediction horizon based on GSR and update lgvariables.M_gsr. ">
/* 
In preparation for instantiating another prediction_horizon, we set up some new matrices : 
    A_state_temp_gsr is based on A_state_gsr
    C_state_temp_gsr is based on C_state_gsr

    The "p_hor2" instance of prediction_horizon is now instantiated, using A_state_temp_gsr, C_state_temp_gsr, N1, and N2. The output of p_hor2's prediction_horizons() method is stored in the new variable M_gsr_temp. This output is used to update lgvariables : 
    lgvariables.M_gsr gets values from M_gsr_temp. 
         */
        Matrix A_state_temp_gsr = new Matrix(4, 4);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                A_state_temp_gsr.set(i, j, A_state_gsr[i][j][kj]);
            }
        }

        Matrix C_state_temp_gsr = new Matrix((lastvaluereturnxyz(C_state_gsr))[1] + 1, (lastvaluereturnxyz(C_state_gsr))[2] + 1);

        for (int i = 0; i < (lastvaluereturnxyz(C_state_gsr))[1] + 1; i++) {
            for (int j = 0; j < (lastvaluereturnxyz(C_state_gsr))[2] + 1; j++) {
                C_state_temp_gsr.set(i, j, C_state_gsr[i][j][kj]);
            }
        }

        prediction_horizon p_hor2 = new prediction_horizon(A_state_temp_gsr, C_state_temp_gsr, N1, N2);

        Matrix M_gsr_temp = p_hor2.prediction_horizons();

        lgvariables.M_gsr = createnew3Dmatrix(lgvariables.M_gsr, p_hor2.M.getRowDimension(), p_hor2.M.getColumnDimension(), kj + 1);

        for (int i = 0; i < p_hor2.M.getRowDimension(); i++) {
            for (int j = 0; j < p_hor2.M.getColumnDimension(); j++) {
                lgvariables.M_gsr[i][j][kj] = M_gsr_temp.get(i, j);
            }
        }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc=" Build a new value for lgvariables.X_state. ">
/* 
Remember phi? It was passed as a parameter to the m20150711_gpc constructor, and got a couple of updates from the gs variable at the top of this function (but it looks like it's mostly unchanged). Well, now we're going to use it in a big way. 
    X_state was initially passed as a parameter to the constructor as well. It doesn't look like that initial value is ever used (we should consider removing it as a parameter for the constructor), since this is the first time we're using it and we're immediately setting it to a new matrix. 
    Now we update X_state : values in the kj_th column of the first 20 rows get assigned values (based on) phi. 
    lgvariables.X_state gets the value of our X_state. 
    lgvariables.X_state_ee gets the value of our X_state_ee. 
    lgvariables.X_state_gsr gets the value of our X_state_gsr. 
         */
        X_state = new Matrix(21, kj + 1);
        X_state_ee = new Matrix(phi_ee_trans.getColumnDimension(), kj + 1);
        X_state_gsr = new Matrix(phi_gsr_trans.getColumnDimension(), kj + 1);

        X_state.set(0, kj, phi.get(0, kj) * (-1));
        X_state.set(1, kj, phi.get(1, kj) * (-1));
        X_state.set(2, kj, phi.get(2, kj) * (-1));
        X_state.set(3, kj, phi.get(4, kj));
        X_state.set(4, kj, phi.get(5, kj));
        X_state.set(5, kj, phi.get(6, kj));
        X_state.set(6, kj, phi.get(7, kj));
        X_state.set(7, kj, phi.get(8, kj));
        X_state.set(8, kj, phi.get(9, kj));
        X_state.set(9, kj, phi.get(10, kj));
        X_state.set(10, kj, phi.get(11, kj));
        X_state.set(11, kj, phi.get(12, kj));
        X_state.set(12, kj, phi.get(13, kj));
        X_state.set(13, kj, phi.get(14, kj));// X_state.set(14,kj-1,phi.get(15, kj)); X_state.set(15,kj-1,phi.get(16, kj));

        X_state.set(14, kj, phi.get(16, kj));
        X_state.set(15, kj, phi.get(17, kj));
        X_state.set(16, kj, phi.get(18, kj));

        X_state.set(17, kj, phi.get(20, kj));
        X_state.set(18, kj, phi.get(21, kj));
        X_state.set(19, kj, phi.get(22, kj));
        X_state.set(20, kj, phi.get(23, kj));

        lgvariables.X_state = X_state;
// </editor-fold>      

// <editor-fold defaultstate="collapsed" desc=" Build a new value for lgvariables.X_state_ee. ">
        for (int i = 0; i < phi_ee_trans.getColumnDimension(); i++) {
            if (i < 3) {
                X_state_ee.set(i, kj, phi_ee_trans.get(kj, i) * (-1));
            } else {
                X_state_ee.set(i, kj, phi_ee_trans.get(kj, i));
            }
        }

        lgvariables.X_state_ee = X_state_ee;
// </editor-fold>   

// <editor-fold defaultstate="collapsed" desc=" Build a new value for lgvariables.X_state_gsr ">
        for (int i = 0; i < phi_gsr_trans.getColumnDimension(); i++) {
            if (i < 3) {
                X_state_gsr.set(i, kj, phi_gsr_trans.get(kj, i) * (-1));
            } else {
                X_state_gsr.set(i, kj, phi_gsr_trans.get(kj, i));
            }
        }

        lgvariables.X_state_gsr = X_state_gsr;
// </editor-fold>  

// <editor-fold desc=" Build a new value for lgvariables.ee_prediction. ">
/* 
First we build a new matrix, K_state_ee_temp, which is a one-column vector that gets its values from K_state_ee's kj_th value. 
    We also build X_state_ee, which does exactly the same thing with X_state_ee.  

    Finally, we create a new matrix, ee_prediction_temp, which is the result of a lot of matrix math : 
    (M_ee_temp x (A_state_temp_ee - (K_state_ee_temp x C_state_temp_ee)) x X_state_ee_temp) + 
    (M_ee_temp x K_state_ee_temp x the value in the kj_th column of the 0th row in ee)

The instance variable ee_prediction, which was passed through the constructor, gets (partially?) loaded with values from ee_prediction_temp. lgvariables.ee_prediction then gets set to the value of ee_prediction. 
         */
        Matrix K_state_ee_temp = new Matrix((lastvaluereturnxyz(K_state_ee))[1] + 1, (lastvaluereturnxyz(K_state_ee))[2] + 1);

        for (int i = 0; i < (lastvaluereturnxyz(K_state_ee))[1] + 1; i++) {
            for (int j = 0; j < (lastvaluereturnxyz(K_state_ee))[2] + 1; j++) {
                K_state_ee_temp.set(i, j, K_state_ee[i][j][kj]);
            }
        }

        Matrix X_state_ee_temp = new Matrix(X_state_ee.getRowDimension(), 1);

        for (int i = 0; i < X_state_ee.getRowDimension(); i++) {
            X_state_ee_temp.set(i, 0, X_state_ee.get(i, kj));
        }

        Matrix ee_prediction_temp;
        ee_prediction_temp = (M_ee_temp.times((A_state_temp_ee).minus((K_state_ee_temp).times(C_state_temp_ee))).times(X_state_ee_temp)).plus((M_ee_temp).times(K_state_ee_temp).times(ee.get(0, kj)));

        ee_prediction = createnewMatrix(ee_prediction_temp.getRowDimension(), kj + 1, ee_prediction);

        for (int i = 0; i < ee_prediction_temp.getRowDimension(); i++) {
            for (int j = 0; j < ee_prediction_temp.getColumnDimension(); j++) {
                ee_prediction.set(i, kj, ee_prediction_temp.get(i, j));
            }
        }

        lgvariables.ee_prediction = ee_prediction;
// </editor-fold>      

// <editor-fold defaultstate="collapsed" desc=" Build a new value for lgvariables.gsr_prediction. ">

        /* 
With lgvariables.ee_prediction set, we start building a new value for lgvariables.gsr_prediction. This process is very similar to the process used to build lgvariables.ee_prediction. 
    We create a new matrix, K_state_gsr_temp, which gets its values from 3D array K_state_gsr. 
    We also create a new matrix, X_state_gsr_temp, which gets its values from X_state_gsr (which was passed through the constructor, but got some updated values earlier in this method). 

    We now create a new matrix, gsr_prediction_temp, which is the result of matrix math similar to ee_prediction_temp : 
    (M_gsr_temp x (A_state_temp_gsr - (K_state_gsr_temp x C_state_temp_gsr)) x X_state_gsr_temp) + 
    (M_gsr_temp x K_state_gsr_temp x the value in the kj_th column of the 0th row in gsr)

The instance variable gsr_prediction, which was passed through the constructor, gets (partially?) loaded with values from gsr_prediction_temp. 

Finally, lgvariables.gsr_prediction now gets set to the value of gsr_prediction_temp. 
         */
        Matrix K_state_gsr_temp = new Matrix((lastvaluereturnxyz(K_state_gsr))[1] + 1, (lastvaluereturnxyz(K_state_gsr))[2] + 1);

        for (int i = 0; i < (lastvaluereturnxyz(K_state_gsr))[1] + 1; i++) {
            for (int j = 0; j < (lastvaluereturnxyz(K_state_gsr))[2] + 1; j++) {
                K_state_gsr_temp.set(i, j, K_state_gsr[i][j][kj]);
            }
        }

        Matrix X_state_gsr_temp = new Matrix(X_state_gsr.getRowDimension(), 1);

        for (int i = 0; i < X_state_gsr.getRowDimension(); i++) {
            X_state_gsr_temp.set(i, 0, X_state_gsr.get(i, kj));
        }

        Matrix gsr_prediction_temp = (M_gsr_temp.times((A_state_temp_gsr).minus((K_state_gsr_temp).times(C_state_temp_gsr))).times(X_state_gsr_temp)).plus((M_gsr_temp).times(K_state_gsr_temp).times(gsr.get(0, kj)));

        gsr_prediction = createnewMatrix(gsr_prediction_temp.getRowDimension(), kj + 1, gsr_prediction);

        for (int i = 0; i < gsr_prediction_temp.getRowDimension(); i++) {
            for (int j = 0; j < gsr_prediction_temp.getColumnDimension(); j++) {
                gsr_prediction.set(i, kj, gsr_prediction_temp.get(i, j));
            }
        }

        lgvariables.gsr_prediction = createnewMatrix(gsr_prediction_temp.getRowDimension(), kj + 1, lgvariables.gsr_prediction);
        lgvariables.gsr_prediction = gsr_prediction;
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc=" Set up variable g_prediction for future use. ">

        /* 
Now we're going to set up matrix "g_prediction_temp". This got the similarly-named value passed through the constructor, but we're essentially going to overwrite its values. It gets loaded with the result of a bunch of matrix math, for which we set up a bunch of temporary variables : 
    M_temp, based on lgvariables.M
    K_temp, based on lgvariables.K_state
    X_state_temp, based on lgvariables.X_state
    L_ee_temp, based on lgvariables.L_ee
    L_gsr_temp, based on lgvariables.L_gsr
    ee_temp, based on ee
    gsr_temp, based on gsr
    so the value stored in g_prediction_temp is 
    (M_temp x (A_state_temp - (K_temp x C_state_temp)) x X_state_temp) + 
    (M_temp x K_temp x the value in the kj_th column of the 0th row in gs)
Finally, the variable g_prediction gets updated based on the values in g_prediction_temp.
         */
        Matrix M_temp = new Matrix(8, 21);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 21; j++) {
                M_temp.set(i, j, lgvariables.M[i][j][kj]);
            }
        }

        Matrix K_temp = new Matrix(21, 1);

        for (int i = 0; i < 21; i++) {
            for (int j = 0; j < 1; j++) {
                K_temp.set(i, j, K_state[i][j][kj]);
            }
        }

        Matrix X_state_temp = new Matrix(X_state.getRowDimension(), 1);

        for (int i = 0; i < X_state.getRowDimension(); i++) {
            X_state_temp.set(i, 0, X_state.get(i, kj));
        }

        Matrix L_ee_temp = new Matrix((lastvaluereturnxyz(lgvariables.L_ee)[1] + 1), (lastvaluereturnxyz(lgvariables.L_ee)[2] + 1));

        for (int i = 0; i < (lastvaluereturnxyz(lgvariables.L_ee)[1] + 1); i++) {
            for (int j = 0; j < (lastvaluereturnxyz(lgvariables.L_ee)[2] + 1); j++) {
                L_ee_temp.set(i, j, lgvariables.L_ee[i][j][kj]);
            }
        }

        Matrix L_gsr_temp = new Matrix((lastvaluereturnxyz(lgvariables.L_gsr)[1] + 1), (lastvaluereturnxyz(lgvariables.L_gsr)[2] + 1));

        for (int i = 0; i < (lastvaluereturnxyz(lgvariables.L_gsr)[1] + 1); i++) {
            for (int j = 0; j < (lastvaluereturnxyz(lgvariables.L_gsr)[2] + 1); j++) {
                L_gsr_temp.set(i, j, lgvariables.L_gsr[i][j][kj]);
            }
        }

        Matrix ee_temp = new Matrix(d2, 1);

        for (int i = kj - d2 + 1; i < kj + 1; i++) {
            for (int j = 0; j < 1; j++) {
                ee_temp.set(i - (kj - d2 + 1), j, ee.get(j, i));
            }
        }

        Matrix gsr_temp = new Matrix(d2, 1);

        for (int i = kj - d2 + 1; i < kj + 1; i++) {
            for (int j = 0; j < 1; j++) {
                gsr_temp.set(i - (kj - d2 + 1), j, gsr.get(j, i));
            }
        }

        Matrix g_prediction_temp = (M_temp.times((A_state_temp).minus((K_temp).times(C_state_temp))).times(X_state_temp)).plus((M_temp).times(K_temp).times(gs.get(0, kj - 2)));

        g_prediction = createnewMatrix(g_prediction_temp.getRowDimension(), kj + 1, g_prediction);

        for (int i = 0; i < g_prediction_temp.getRowDimension(); i++) {
            for (int j = 0; j < g_prediction_temp.getColumnDimension(); j++) {
                g_prediction.set(i, kj, g_prediction_temp.get(i, j));
            }
        }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc=" Update g_prediction and build supporting variables based on it. Update lgvariables.reference_glucose. ">

        /* 
We now build a couple more temporary matrices for processing. 
    eetemp, based on ee_temp and ee_prediction
    gsrtemp, based on gsr_temp and gsr_prediction
We update g_prediction based on L_ee_temp, L_gsr_temp, and g_prediction_feedback.
         */
        Matrix eetemp = new Matrix(ee_prediction.getRowDimension() - d2 + 1, 1);

        eetemp.set(0, 0, ee_temp.get(0, 0));

        for (int i = 0; i < ee_prediction.getRowDimension() - d2; i++) {
            eetemp.set(i + 1, 0, ee_prediction.get(i, kj));
        }

        Matrix gsrtemp = new Matrix(gsr_prediction.getRowDimension() - d2 + 1, 1);

        gsrtemp.set(0, 0, gsr_temp.get(0, 0));

        for (int i = 0; i < gsr_prediction.getRowDimension() - d2; i++) {
            gsrtemp.set(i + 1, 0, gsr_prediction.get(i, kj));
        }

        for (int i = 0; i < g_prediction.getRowDimension(); i++) {
            g_prediction.set(i, kj, (L_ee_temp.times(eetemp).plus(L_gsr_temp.times(gsrtemp))).get(i, 0) + g_prediction.get(i, kj));
        }

        for (int i = 0; i < g_prediction.getRowDimension(); i++) {
            g_prediction.set(i, kj, g_prediction_feedback.get(i, 0) + g_prediction.get(i, kj));
        }

        /* 
We now create an instance of reference_trajectory, "ref_trajectory", based on gs, N1, N2, and meal_gpc_mu. NB that there's a hardcoded "110" value being passed through here, which is interesting. The output of running the referencetrajectory() method of ref_trajectory gets stored in reference_glucose_temp. lgvariables.reference_glucose gets updated values from reference_glucose_temp. 
         */
        reference_trajectory ref_trajectory = new reference_trajectory(gs.get(0, kj - 2), 110, (N2 - N1), meal_gpc_mu.get(kj, 0));

        Matrix reference_glucose_temp = ref_trajectory.referencetrajectory();

        lgvariables.reference_glucose = createnewMatrix(8, kj + 1, lgvariables.reference_glucose);

        for (int i = 0; i < 8; i++) {
            lgvariables.reference_glucose.set(i, kj, reference_glucose_temp.get(i, 0));
        }

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc=" Update lgvariables.insulin_sensitivity_constant. ">

        /* 
Set up local matrices : 
dividematrix, based on g_prediction and reference_glucose_temp
g_predictionkj, a subset of g_prediction
We then use the matricecompareconstantmax() function on dividematrix to set the value of lgvariables.insulin_sensitivity_constant (NB that there's a hardcoded value 0.1 being supplied to the function)
         */
        Matrix dividematrix = new Matrix(g_prediction_temp.getRowDimension(), 1);

        for (int i = 0; i < g_prediction.getRowDimension(); i++) {
            dividematrix.set(i, 0, g_prediction.get(i, kj) / reference_glucose_temp.get(i, 0));
        }

        Matrix g_predictionkj = new Matrix(g_prediction.getRowDimension(), 1);

        for (int i = 0; i < g_prediction.getRowDimension(); i++) {
            g_predictionkj.set(i, 0, g_prediction.get(i, kj));
        }

        Matrix insulin_sensitivity_constant_temp = matricecompareconstantmax(0.1, dividematrix);

        lgvariables.insulin_sensitivity_constant = createnewMatrix(8, kj + 1, lgvariables.insulin_sensitivity_constant);

        for (int i = 0; i < insulin_sensitivity_constant_temp.getRowDimension(); i++) {
            lgvariables.insulin_sensitivity_constant.set(i, kj, insulin_sensitivity_constant_temp.get(i, 0));
        }
// </editor-fold>

        System.out.println("L: " + (lgvariables.L.length + 1) + " x "
                + +(lgvariables.L[0].length + 1) + " x " + (lgvariables.L[0][0].length + 1));
        Matrix L_matrix = new Matrix((lastvaluereturnxyz(lgvariables.L)[1] + 1), (lastvaluereturnxyz(lgvariables.L)[2] + 1));

        for (int i = 0; i < (lastvaluereturnxyz(lgvariables.L)[1] + 1); i++) {
            for (int j = 0; j < (lastvaluereturnxyz(lgvariables.L)[2] + 1); j++) {
                L_matrix.set(i, j, lgvariables.L[i][j][kj]);
            }
        }

        //TODO Breaks !! Input correct L
// <editor-fold defaultstate="collapsed" desc=" Set up new instance of controller_instance, supplying it with (among other things) bolus and basal insulin. ">

        controller_instance cont = new controller_instance(g_predictionkj, L_matrix, bolus_insulin, basal_insulin, 0.0, reference_glucose_temp, Nu, st, body_weight, insulin_sensitivity_constant_temp, (int) flag_constrains);

        cont.control_ins();
// </editor-fold>
        /*    printMatrix(cont.ins,"cont.ins");
        printMatrix(cont.IOB_pred,"cont.IOB_pred");
        printMatrix(cont.umaxx,"cont.umaxx");
        printMatrix(cont.total_daily_unit,"cont.total_daily_unit");
        printMatrix(cont.insulin_sensitivity_factor,"cont.insulin_sensitivity_factor");*/
// <editor-fold defaultstate="collapsed" desc=" Set lgvariables properties to controller_instance's properties (updated by calling its control_ins() function) ">

//First, we reinitialize the lgvariables matrices with the new kj + 1 dimension. 
        lgvariables.basal_insulin = createnewMatrix(cont.ins.getRowDimension(), kj + 1, lgvariables.basal_insulin);
        lgvariables.IOB_prediction = createnewMatrix(cont.IOB_pred.getRowDimension(), kj + 1, lgvariables.IOB_prediction);
        lgvariables.maximum_insulin = createnewMatrix(cont.umaxx.getRowDimension(), kj + 1, lgvariables.maximum_insulin);
        lgvariables.total_daily_unit = createnewMatrix(cont.total_daily_unit.getRowDimension(), kj + 1, lgvariables.total_daily_unit);
        lgvariables.insulin_sensitivity_factor = createnewMatrix(cont.insulin_sensitivity_factor.getRowDimension(), kj + 1, lgvariables.insulin_sensitivity_factor);
        lgvariables.g_prediction = createnewMatrix(cont.g_prediction.getRowDimension(), kj + 1, lgvariables.g_prediction);

//Now the actual assignment of controller_instance properties to the lgvariables properties. 
        //Update g_prediction values. 
        for (int i = 0; i < lgvariables.g_prediction.getRowDimension(); i++) {
            for (int j = 0; j < lgvariables.g_prediction.getColumnDimension(); j++) {
                lgvariables.g_prediction.set(i, j, g_prediction.get(i, j));
            }
        }
        //XXX OPTIMIZE
        // Is this analogous to the following line in MATLAB's controller_ins code ? : 
        // ins=round(ins/0.1)*0.1; % 0.1 is due to pump
        // If this is meant to do the same thing, we're using different values. But it shouldn't matter, right? 
        // Does the (int) apply to the numerator, or to the final value? That might make the difference. 
        for (int i = 0; i < cont.ins.getRowDimension(); i++) {
            cont.ins.set(i, 0, ((int) (cont.ins.get(i, 0) / 0.025)) * 0.025);
        }

//        DIAS.printMatrix(m20150711_load_global_variables.bolus_insulin, "gpc module bolus");
//        DIAS.printMatrix(cont.ins, "cont.ins");
        //Update basal insulin values. NB that this comes from the controller_instance.ins value. 
        // In previous version of Matlab code, this went to basal insulin; now it goes to bolus insulin. 
        for (int i = 0; i < cont.ins.getRowDimension(); i++) {
            m20150711_load_global_variables.basal_insulin.set(i, kj, cont.ins.get(i, 0));
            //m20150711_load_global_variables.bolus_insulin.set(i, kj, cont.ins.get(i, 0));
        }
        DIAS.printMatrix(m20150711_load_global_variables.bolus_insulin, "gpc module bolus");

        //Update IOB_prediction values. 
        for (int i = 0; i < cont.IOB_pred.getRowDimension(); i++) {
            lgvariables.IOB_prediction.set(i, kj, cont.IOB_pred.get(i, 0));
        }

        //Update maximum_insulin values.
        for (int i = 0; i < cont.umaxx.getRowDimension(); i++) {
            lgvariables.maximum_insulin.set(i, kj, cont.umaxx.get(i, 0));
        }

        //Update total_daily_unit values. 
        for (int i = 0; i < cont.total_daily_unit.getRowDimension(); i++) {
            lgvariables.total_daily_unit.set(i, kj, cont.total_daily_unit.get(i, 0));
        }

        //Update insulin_sensitivity_factor values. 
        for (int i = 0; i < cont.insulin_sensitivity_factor.getRowDimension(); i++) {
            lgvariables.insulin_sensitivity_factor.set(i, kj, cont.insulin_sensitivity_factor.get(i, 0));
        }
// </editor-fold>

        lgvariables.phi = new Matrix(na + nb1 + nb2 + nb2 + nc, kj + 1);

        for (int i = 0; i < phi.getRowDimension(); i++) {
            for (int j = 0; j < phi.getColumnDimension(); j++) {
                lgvariables.phi.set(i, j, phi.get(i, j));
            }
        }

        lgvariables.phi_ee = new Matrix(4, kj + 1);

        for (int i = 0; i < phi_ee.getRowDimension(); i++) {
            for (int j = 0; j < phi_ee.getColumnDimension(); j++) {
                lgvariables.phi_ee.set(i, j, phi_ee.get(i, j));
            }
        }

        lgvariables.phi_gsr = new Matrix(4, kj + 1);

        for (int i = 0; i < phi_gsr.getRowDimension(); i++) {
            for (int j = 0; j < phi_gsr.getColumnDimension(); j++) {
                lgvariables.phi_gsr.set(i, j, phi_gsr.get(i, j));
            }
        }

    }

    public Matrix flupid(Matrix flupidmatrice) {
        int a = flupidmatrice.getRowDimension() - 1;
        int i = 0;
        double tempf = 0;
        for (int j = 0; j < flupidmatrice.getRowDimension(); j++) {
            while (i <= a) {
                tempf = flupidmatrice.get(i, j);
                flupidmatrice.set(i, j, flupidmatrice.get(a, j));
                flupidmatrice.set(a, j, tempf);
                i++;
                a--;
            }
            a = flupidmatrice.getColumnDimension() - 1;
            i = 0;
            tempf = 0;
        }
        i = 0;
        return flupidmatrice;
    }

    public Matrix innermatrice(Matrix matrice, int xinnerstart, int xinnerend, int yinnerstart, int yinnerend) {

        int a = 0;
        int b = 0;
        Matrix innermatrix = new Matrix(xinnerend - xinnerstart, yinnerend - yinnerstart);
        for (int i = xinnerstart; i < xinnerend; i++) {
            for (int j = yinnerstart; j < yinnerend; j++) {
                innermatrix.set(b, a, matrice.get(j, i));
                a++;
            }
            a = 0;
            b++;
        }
        b = 0;
        a = 0;

        return innermatrix;
    }

    public int[] lastvaluereturnxyz(double s[][][]) {
        int lastvaluex = 0;
        int lastvaluey = 0;
        int lastvaluez = 0;

        for (int i = 0; i < s.length; i++) {
            for (int j = 0; j < s[0].length; j++) {
                for (int z = 0; z < s[0][0].length; z++) {
                    if (s[i][j][z] != 0) {
                        lastvaluex = i;
                        lastvaluey = j;
                        lastvaluez = z;
                    }
                }
            }
        }
        int[] dizi = new int[4];
        dizi[1] = lastvaluex;
        dizi[2] = lastvaluey;
        dizi[3] = lastvaluez;

        return dizi;
    }

    public double[][][] createnew3Dmatrix(double s[][][], int newx, int newy, int newz) {

        double[][][] newdoublematrice = new double[newx][newy][newz];

        for (int i = 0; i < s.length; i++) {
            for (int j = 0; j < s[0].length; j++) {
                for (int z = 0; z < s[0][0].length; z++) {
                    newdoublematrice[i][j][z] = s[i][j][z];
                }
            }
        }

        return newdoublematrice;
    }

    public void print3Dmatrice(double x[][][], String matricename) {

        int[] valuex;
        valuex = lastvaluereturnxyz(x);

        System.out.println(matricename);

        for (int k = 0; k < valuex[3] + 1; k++) {
            for (int i = 0; i < valuex[1] + 1; i++) {
                for (int j = 0; j < valuex[2] + 1; j++) {
                    System.out.print("\t\t\t" + x[i][j][k]);
                }
                System.out.println();
            }
            System.out.println("Matrice State:  " + (k + 1));
        }
        System.out.println("Matrice has written");

    }

    public void print3DMatrice(double x[][][], String matricename) {

        System.out.println(matricename);

        for (int k = 0; k < x[0][0].length; k++) {
            for (int j = 0; j < x.length; j++) {
                for (int i = 0; i < x[0].length; i++) {
                    System.out.print("\t\t\t" + x[j][i][k]);
                }
                System.out.println();
            }
            System.out.println("Matrice State:  " + (k + 1));
        }
        System.out.println("Matrice has written");

    }

    public void print3Dmatriceother(double x[][][], String matricename, int t, int y, int z) {
        System.out.println(matricename);

        for (int k = 0; k < z; k++) {
            for (int i = 0; i < y; i++) {
                for (int j = 0; j < t; j++) {
                    System.out.print("\t\t\t" + x[i][j][k]);
                }
                System.out.println();
            }
            System.out.println("Matrice State:  " + (k + 1));
        }
        System.out.println("Matrice has written");

    }

    public Matrix matricecompareconstantmax(double constant, Matrix matrice) {
        Matrix resultmatrice = new Matrix(matrice.getRowDimension(), matrice.getColumnDimension());

        for (int i = 0; i < matrice.getRowDimension(); i++) {
            for (int j = 0; j < matrice.getColumnDimension(); j++) {
                if (constant > matrice.get(i, j)) {
                    resultmatrice.set(i, j, constant);
                } else {
                    resultmatrice.set(i, j, matrice.get(i, j));
                }
            }
        }

        return resultmatrice;
    }

    public Matrix createnewMatrix(int newdimensionx, int newdimensiony, Matrix oldmatrice) {
        Matrix newMatrice = new Matrix(newdimensionx, newdimensiony);

        for (int i = 0; i < oldmatrice.getRowDimension(); i++) {
            for (int j = 0; j < oldmatrice.getColumnDimension(); j++) {
                newMatrice.set(i, j, oldmatrice.get(i, j));
            }
        }

        return newMatrice;
    }

    /**
     * Function to run the Optimization using 5 optimization stages
     *
     * @param in Optimization Inputs
     * @return
     */
    /*private OptInputs runOptimizationInStages(OptInputs in) {
        OptRecursive optRecursiveCons = new OptRecursive(in);
        OptInputs res = optRecursiveCons.runOptimization();
        optRecursiveCons.saveOptRecursiveVariables();
        if (res.exit() != CobylaExitStatus.NORMAL) {
            //Second optimization:
            optRecursiveCons.updateParameters(optRecursiveCons.Q_res, optRecursiveCons.P, 1.0e-2, 1.49011611938477e-6, 5000);
            res = optRecursiveCons.runOptimization();
            if (res.exit() != CobylaExitStatus.NORMAL) {
                //Third optimization:
                optRecursiveCons.updateParameters(optRecursiveCons.Q_res, optRecursiveCons.P, 1.0e-1, 1.49011611938477e-8, 5000);
                res = optRecursiveCons.runOptimization();
                if (res.exit() != CobylaExitStatus.NORMAL) {
                    //Fourth optimization:
                    optRecursiveCons.updateParameters(optRecursiveCons.Q_res, optRecursiveCons.P, 0.5, 1.49011611938477e-4, 5000);
                    res = optRecursiveCons.runOptimization();
                }
                if (res.exit() != CobylaExitStatus.NORMAL){
                    //Fifth optimization:
                    optRecursiveCons.updateParameters(optRecursiveCons.Q_res, optRecursiveCons.P, 3, 1.49011611938477e-2, 5000);
                    res = optRecursiveCons.runOptimization();
                }
                
            }
            //Last optimization:
            optRecursiveCons.updateParameters(optRecursiveCons.Q_res, optRecursiveCons.P, 1.0e-4, 1.49011611938477e-8, 5000);
            res = optRecursiveCons.runOptimization();
            optRecursiveCons.saveOptRecursiveResults();
        }
        return res;

    }*/

}
