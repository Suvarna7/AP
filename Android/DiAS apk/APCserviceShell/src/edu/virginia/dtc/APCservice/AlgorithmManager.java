package edu.virginia.dtc.APCservice;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import edu.virginia.dtc.APCservice.Algorithm.CGM__SEDFR_JF;
import edu.virginia.dtc.APCservice.Algorithm.CPA_Module_paralleled_calculation_JF;
import edu.virginia.dtc.APCservice.Algorithm.m20150711_calculate_IOB;
import edu.virginia.dtc.APCservice.Algorithm.m20150711_gpc;
import edu.virginia.dtc.APCservice.Algorithm.m20150711_load_global_variables;
import edu.virginia.dtc.APCservice.Algorithm.m20150711_run_meal_detection_bolus_algorithm;
import edu.virginia.dtc.APCservice.Algorithm.send_text_message;
import edu.virginia.dtc.APCservice.DataManagement.MatrixDatabaseManager;
import edu.virginia.dtc.SysMan.Debug;
import Jama.Matrix;
import android.content.Context;
import android.widget.Toast;
import edu.virginia.dtc.APCservice.Algorithm.hypo_alarm;

/**
 * ALgorithmManager is a class to call all functions of the Insulin control algorithm
 * It also keeps and updates the values of the sensors each time
 * @author Caterina Lazaro
 *
 */
public class AlgorithmManager {


	//CGM values & initialization
	private   CGMMatrix cgmM;
	//BodyMedia values & initialization
	private   BodyMediaMatrix bmM;
	//Database manager for matrices storage
	private MatrixDatabaseManager matrixDB;
	//Local Global variables
	private m20150711_load_global_variables LGVariables;
	
	//DEBUGGING: Hardcoded values of basal and bolus
	private double _FORCE_BOLUS = 0.3;
	private double _FORCE_BASAL = 0.1;
	


	/**
	 * Constructor
	 * @param context
	 */
	public AlgorithmManager(Context ctx){
		//Start matrices
		cgmM =  new CGMMatrix();
		bmM = new BodyMediaMatrix();
		matrixDB = new MatrixDatabaseManager(ctx, MatrixDatabaseManager.db_MATRIX_NAME);
		
		//Create the container  to hold all global variables
		LGVariables = new m20150711_load_global_variables();
	}

	/**
	 * Run the Insulin algorithm functions 
	 * @param cgmValue
	 * @param ee - energy expenditure values for the last 5 min
	 * @param gsr - gsr values for the last 5 min
	 * @param activity - values for the last 5 min
	 * @param sleep - sleep detected for the last 5 min
	 */
	public double runAlgorithm(double cgmValue, double[] ee, double [] gsr, double[] activity, double [] sleep){
		CGM__SEDFR_JF cs= new CGM__SEDFR_JF();

		//Store all global variables in a single object
		m20150711_load_global_variables loadGlobalVariables = new m20150711_load_global_variables();

		//TODO Update sensor matrices
		//1. Add cgm Values to the CGM Matrix
		cgmM.addCGMvalue(cgmValue);
		//2. Add bodymedia values to BM Matrix
		bmM.addBMvalue(ee, gsr, activity, sleep);

		//TODO Update cgm values
		//Update values on the algorithm tables
		loadGlobalVariables.gs=cgmM.getCGMValues();
		//Debug.i("ALG INS", "Run method", "algorithm insulin cgm: " 				+cgmM.getCGMValues());



		int flag_noise=1;


		//Prepare the matrices for bolus_insulin & basal_insulin
		if(loadGlobalVariables.kj>20){
			//TODO Solve !!!!!!!
			/*loadGlobalVariables.bolus_insulin=cs.createnewMatrix(loadGlobalVariables.kj,1 , loadGlobalVariables.bolus_insulin);
			loadGlobalVariables.basal_insulin=cs.createnewMatrix(8, loadGlobalVariables.kj, loadGlobalVariables.basal_insulin);
			 */

			// PROBLEM !!!! -> cs.createnewMatrix(loadGlobalVariables.kj,1 , loadGlobalVariables.bolus_insulin);

			return 5;

		}


		//TODO Tested and working till here
		return 1;
		/*
		CGM__SEDFR_JF csedfrJF = new CGM__SEDFR_JF(loadGlobalVariables.gs,loadGlobalVariables.bolus_insulin,loadGlobalVariables.basal_insulin,flag_noise);

		try {
			LGVariables.CGM_retuning_with_noise_generator =csedfrJF.CGM();
		} catch (IOException ex) {
			System.out.println("IoError" +ex);
		}

		flag_noise=0;

		csedfrJF = new CGM__SEDFR_JF(LGVariables.gs,LGVariables.bolus_insulin,LGVariables.basal_insulin,flag_noise);
		try {
			LGVariables.CGM_retuning_without_noise_generator =csedfrJF.CGM();
		} catch (IOException ex) {
			System.out.println("IoError" +ex);
		}    



		LGVariables.ee= cs.createnewMatrix(1,LGVariables.ee.getColumnDimension()+1 ,LGVariables.ee );
		LGVariables.gsr= cs.createnewMatrix(1,LGVariables.gsr.getColumnDimension()+1 ,LGVariables.gsr );
		LGVariables.sleep= cs.createnewMatrix(1,LGVariables.sleep.getColumnDimension()+1 ,LGVariables.sleep );
		LGVariables.phys_act= cs.createnewMatrix(1,LGVariables.phys_act.getColumnDimension()+1 ,LGVariables.phys_act );


		//TODO Update Bodymedia values
		//m20150711_get_armband_data getarmbanddata = new m20150711_get_armband_data(LGVariables.ee, LGVariables.gsr,LGVariables.sleep, 
		//LGVariables.phys_act);   




		//TODO LGVariables.armband_data_with_time=getarmbanddata.m20150711_get_armband_data();
		LGVariables.ee.set(0,LGVariables.kj,bmM.getBMColumnLast(BodyMediaMatrix._EE));
		LGVariables.gsr.set(0,LGVariables.kj,bmM.getBMColumnLast(BodyMediaMatrix._GSR));
		LGVariables.sleep.set(0,LGVariables.kj,bmM.getBMColumnLast(BodyMediaMatrix._SLEEP));
		LGVariables.phys_act.set(0,LGVariables.kj,bmM.getBMColumnLast(BodyMediaMatrix._ACT));
		Debug.i("ALG INS", "Run method", "algorithm insulin Bodymedia: "
				+bmM.getBMColumnLast(BodyMediaMatrix._EE) +" - "
				+bmM.getBMColumnLast(BodyMediaMatrix._GSR) +" - "
				+bmM.getBMColumnLast(BodyMediaMatrix._SLEEP) +" - "
				+bmM.getBMColumnLast(BodyMediaMatrix._ACT) +" - ");



		//******************************   
		Matrix basal_temp = new Matrix (1,LGVariables.basal_insulin.getColumnDimension());

		for(int i=0;i<LGVariables.basal_insulin.getColumnDimension();i++)
			basal_temp.set(0, i, LGVariables.basal_insulin.get(0, i));


		m20150711_calculate_IOB IOBcalculate = new m20150711_calculate_IOB(LGVariables.bolus_insulin,basal_temp);   


		LGVariables.IOB_total=cs.createnewMatrix(LGVariables.kj+1,1, LGVariables.IOB_total);

		LGVariables.IOB_total.set(LGVariables.kj, 0, IOBcalculate.IOB());


		m20150711_run_meal_detection_bolus_algorithm rmdetectionbolusalgorithm = new m20150711_run_meal_detection_bolus_algorithm(lgvariables
				.meal_states,LGVariables.meal_covariance,LGVariables.bolus_insulin,LGVariables.meal_bolus_amount,LGVariables.meal_detection,
				LGVariables.meal_detection_time,LGVariables.correction_bolus_amount,LGVariables.correction_detection,LGVariables.
				correction_detection_time,LGVariables.correction_limit,LGVariables.gs,LGVariables.kj,LGVariables.meal_g_basal,LGVariables.
				meal_gpc_gs_slope_degree,LGVariables.meal_gpc_mu,LGVariables.sleep,LGVariables.phys_act,LGVariables.IOB_total,LGVariables.body_weight
				);
		rmdetectionbolusalgorithm.run_meal_detection_bolus_algorithm();

		//TODO Return the bolus insulin 
		Debug.i("ALG INS", "Run method", "algorithm insulin Bodymedia: "
				+LGVariables.bolus_insulin.get(LGVariables.bolus_insulin.getRowDimension()-1,0));

		return    LGVariables.bolus_insulin.get(LGVariables.bolus_insulin.getRowDimension()-1,0);
		//     Matrix matrice = new Matrix (8,1);
		/*	               
		               m20150711_gpc m_gpc = new m20150711_gpc (LGVariables.gs,LGVariables.ee,LGVariables.gsr,LGVariables.kj,LGVariables.phi,LGVariables.phi_ee
		,LGVariables.phi_gsr,LGVariables.armax_parameters,LGVariables.armax_covariance,LGVariables.armax_lamda,LGVariables.armax_err,
		LGVariables.arma_parameters_ee,LGVariables.arma_lamda_ee,LGVariables.arma_covariance_ee,LGVariables.arma_err_ee,LGVariables.
		arma_parameters_gsr,LGVariables.arma_lamda_gsr,LGVariables.arma_covariance_gsr,LGVariables.arma_err_gsr,LGVariables.A_state,
		LGVariables.A_state_ee,LGVariables.A_state_gsr,LGVariables.C_state,LGVariables.C_state_ee,LGVariables.C_state_gsr,LGVariables.B_state
		,LGVariables.K_state,LGVariables.K_state_ee,LGVariables.K_state_gsr,LGVariables.M,LGVariables.L,LGVariables.L_ee,LGVariables.L_gsr,
		LGVariables.M_ee,LGVariables.M_gsr,LGVariables.X_state,LGVariables.X_state_ee,LGVariables.X_state_gsr, LGVariables.ee_prediction, 
		LGVariables.gsr_prediction, LGVariables.g_prediction,LGVariables.reference_glucose,LGVariables.insulin_sensitivity_constant,
		LGVariables.basal_insulin,LGVariables.IOB_prediction,LGVariables.maximum_insulin,LGVariables.total_daily_unit,LGVariables.
		insulin_sensitivity_factor,LGVariables.body_weight,LGVariables.meal_gpc_mu,LGVariables.bolus_insulin,0,matrice);
		               m_gpc.gpc();*/
		/*          
		            CPA_Module_paralleled_calculation_JF CPA_module_paralleled_cal = new 
		CPA_Module_paralleled_calculation_JF(vcgm.gstemp,LGVariables.ee,LGVariables.gsr,LGVariables.kj,LGVariables.phi,LGVariables.phi_ee,lgvaria
		bles.phi_gsr,LGVariables.armax_parameters,LGVariables.armax_covariance,LGVariables.armax_lamda,LGVariables.armax_err,LGVariables.arma_par
		ameters_ee,LGVariables.arma_lamda_ee,LGVariables.arma_covariance_ee,LGVariables.arma_err_ee,LGVariables.arma_parameters_gsr,LGVariables.a
		rma_lamda_gsr,LGVariables.arma_covariance_gsr,LGVariables.arma_err_gsr,LGVariables.A_state,LGVariables.A_state_ee,LGVariables.A_state_gsr
		,LGVariables.C_state,LGVariables.C_state_ee,LGVariables.C_state_gsr,LGVariables.B_state,LGVariables.K_state,LGVariables.K_state_ee,lgvari
		ables.K_state_gsr,LGVariables.M,LGVariables.L,LGVariables.L_ee,LGVariables.L_gsr,LGVariables.M_ee,LGVariables.M_gsr,LGVariables.X_state,l
		gvariables.X_state_ee,LGVariables.X_state_gsr, LGVariables.ee_prediction, LGVariables.gsr_prediction, 
		LGVariables.g_prediction,LGVariables.reference_glucose,LGVariables.insulin_sensitivity_constant,LGVariables.basal_insulin,LGVariables.IOB
		_prediction,LGVariables.maximum_insulin,LGVariables.total_daily_unit,LGVariables.insulin_sensitivity_factor,LGVariables.body_weight,lgvar
		iables.meal_gpc_mu,LGVariables.bolus_insulin);

		        try {
		            CPA_module_paralleled_cal.CPA();
		        } catch (Exception ex) {
		            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
		        }


		           hypo_alarm hypo =  new hypo_alarm 
		(LGVariables.hypo_threshold.transpose(),LGVariables.hypo_slope_degree.transpose(),LGVariables.hypo_alarm.transpose(),LGVariables.carb_amou
		nt,LGVariables.carb_type,LGVariables.hypo_phase.transpose(),LGVariables.hypo_phase_old.transpose(),LGVariables.repeated_immediate_alarm.tr
		anspose(),vcgm.gstemp,LGVariables.kj,LGVariables.g_prediction,LGVariables.phys_act,LGVariables.sleep);
		           hypo.m20150711_hypo_alarm();

		          if(LGVariables.phys_act.get(0, LGVariables.kj-1)==1)
		             LGVariables.batch_CL[LGVariables.kj-1]="Exercise";
		          else if(LGVariables.sleep.get(0 ,LGVariables.kj-1)==1) 
		             LGVariables.batch_CL[LGVariables.kj-1]="Sleep";
		           else if(LGVariables.meal_detection.get(LGVariables.kj-1,0)==1) 
		             LGVariables.batch_CL[LGVariables.kj-1]="Meal";
		          else
		             LGVariables.batch_CL[LGVariables.kj-1]="Other";

		          LGVariables.batch_CL=hypo.createnewString(LGVariables.kj+1, LGVariables.batch_CL);

		          LGVariables.basal_insulin_calculated=cs.createnewMatrix(8,LGVariables.basal_insulin.getColumnDimension()+1 , 
		LGVariables.basal_insulin_calculated);
		          LGVariables.bolus_insulin_calculated=cs.createnewMatrix(LGVariables.bolus_insulin.getRowDimension()+1,1, 
		LGVariables.bolus_insulin_calculated);

		          LGVariables.basal_insulin_calculated=LGVariables.basal_insulin;
		          LGVariables.bolus_insulin_calculated=LGVariables.bolus_insulin;*/
	}

	/**
	 * Test the inputting of values to the algorithm
	 * @param ctx - context
	 * @param cgmValue - cgmValues for the last 5 min
	 * @param ee - energy expenditure for the last 5 min
	 * @param gsr - galvanic skin response
	 * @param activity - kind of activity
	 * @param sleep - sleep detected
	 * @return
	 */
	public  double testAlgorithmInputs(Context ctx, double cgmValue, double[] ee, double [] gsr, double[] activity, double [] sleep){
		//Update sensor matrices
		//1. Add cgm Values to the CGM Matrix
		cgmM.addCGMvalue(cgmValue);
		//2. Add bodymedia values to BM Matrix
		bmM.addBMvalue(ee, gsr, activity, sleep);

		Toast toast = Toast.makeText(ctx, "CGM Algorithm" + cgmM.getCGMValues().getColumnDimension(),Toast.LENGTH_LONG);
		toast.show();

		toast = Toast.makeText(ctx, "Bodymedia Algorithm" + bmM.getBMColumnLast(0),Toast.LENGTH_LONG);
		toast.show();


		//3. Test the 2D Matrix database function
		//matrixDB.deleteMatrix("test");
		/*String matrixName = "test2D29";
		matrixDB.createTable(matrixName, 10, 2);
		matrixDB.addRowToMatrix(matrixName, new double[]{1.0, 2.1, 3.44, 4.2, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0});
		//ARRAY[ROWS][COLUMNS] --> array[0].lenght == rows
		double[][] result = matrixDB.readMatrix(matrixName);
		//double[][] result =new double[][]{{1, 2, 3},{4, 5, 6},{7, 8, 9}};

		//Prepare matrix values to output as toast,
		//ARRAY[ROWS][COLUMNS]
		String output = "Matrix "+result.length+"x"+result[0].length+" {";
		//Rows
		for (int i=0; i < result.length; i ++){
			//Columns
			for (int j =0; j < result[0].length; j ++){
				//Result[col][row]
				output += result[i][j]+", ";
				//double var= result[i][j];

			}
			output += " | \br |";
		}
		output +=" }";
		toast = Toast.makeText(ctx, "Test matrix: "+output ,Toast.LENGTH_LONG);
		toast.show();

		//4. Test the 3D matrix
		String matrixName1 =  "Alpha29";
		String matrixName2 = "Beta29";
		//Create without init
		matrixDB.create3DMatrix(matrixName1,4, 3);
		//Create with init
		matrixDB.initialize3DMatrix(matrixName2, new double[][]{{1,2,3},{4,5,6}});

		//Add matrices
		matrixDB.addTableTo3DMatrix(matrixName1, new double [][]{{4,5,6,7},{7,8, 9, 10}, {10, 11, 12, 13}});
		matrixDB.addTableTo3DMatrix(matrixName2, new double [][]{{4,5,6},{7,8,9}} );

		//Read values
		double[][][] resultPrime = matrixDB.readMatrix3D(matrixName1);
		double[][][] resultBeta = matrixDB.readMatrix3D(matrixName2);

		toast = Toast.makeText(ctx, "Test PRIME matrix: "+resultPrime.length ,Toast.LENGTH_LONG);
		toast.show();

		toast = Toast.makeText(ctx, "Test BETA matrix: "+resultBeta.length ,Toast.LENGTH_LONG);
		toast.show();

		//Prepare matrix values to output as toast,
		//ARRAY[ROWS][COLUMNS]
		if(resultPrime.length !=0){
			double[][] result3D = resultPrime[0];
			String output3D = "Matrix Prime "+ resultPrime.length +" -"+result3D.length+"x"+result3D[0].length+" {";

			//Rows
			for (int i=0; i < result3D.length; i ++){
				//Columns
				for (int j =0; j < result3D[0].length; j ++){
					//Result[row][col]
					output3D += result3D[i][j]+", ";
					//double var= result[i][j];

				}
				output3D += " | \br |";
			}
			output3D +=" }";
			toast = Toast.makeText(ctx, "Test matrix: "+output3D ,Toast.LENGTH_LONG);
			toast.show();

			//Show the end of the testing
			toast = Toast.makeText(ctx, "ENDING result not empty..." ,Toast.LENGTH_LONG);
			toast.show();
		}

		//Prepare matrix values to output as toast,
		//ARRAY[ROWS][COLUMNS]
		if(resultBeta.length !=0){

			result = resultBeta[0];
			output = "Matrix Beta"+ resultBeta.length +" -"+result.length+"x"+result[0].length+" {";
			//Rows
			for (int i=0; i < result.length; i ++){
				//Columns
				for (int j =0; j < result[0].length; j ++){
					//Result[row][col]
					output += result[i][j]+", ";
					//double var= result[i][j];

				}
				output += " | \br |";
			}
			output +=" }";
			//sHOW
			toast = Toast.makeText(ctx, "Test matrix: "+output ,Toast.LENGTH_LONG);
			toast.show();
		}




		//Show the end of the testing
		toast = Toast.makeText(ctx, "ENDING THE TESTING..." ,Toast.LENGTH_LONG);
		toast.show();*/

		return 1;

	}



	/**
	 * Test some other functions fo the algorithm 
	 * @param ctx - context
	 * @param cgm - last cgm value
	 * @param ee - last energy expenditure value
	 * @param gsr - last gsr value
	 * @param sleep - last sleep detected value
	 * @param phys_act - kind of activity
	 * @param bodyweight - kg
	 * @return
	 */

	public Matrix testAlgortihm (Context ctx,double cgm,double ee,double gsr, double sleep, double phys_act,double bodyweight){

		/* Random rand = new Random();
         int min=90;
		 int max=250;
		    // nextInt is normally exclusive of the top value,
		    // so add 1 to make it inclusive
		    cgm = rand.nextInt((max - min) + 1) + min;*/

		/* ***************************************
		 *  Initialize variables
		 */

		//Create a CGM Sedfr
		CGM__SEDFR_JF cgmJSF= new CGM__SEDFR_JF();


		

		//If LGVariables.kj is 21 (initial size) --> there is a new user
		//We will load variables and insert bodyweight values
		if(LGVariables.kj==21){
			//Load all global variables
			LGVariables.m20150711_load_global_variables();
			//Include the bodyweight
			LGVariables.body_weight=bodyweight;
		}

		/* ***************************************
		 *  Update values of the sensors
		 */


		//1. Increase size of matrices (keeping old values):
		//gs (CGM Matrix)
		LGVariables.gs=cgmJSF.createnewMatrix(1,LGVariables.kj+1,LGVariables.gs);		
		//Energy expenditure matrix
		LGVariables.ee= cgmJSF.createnewMatrix(1,LGVariables.kj+1 ,LGVariables.ee );
		//GSR matrix
		LGVariables.gsr= cgmJSF.createnewMatrix(1,LGVariables.kj+1 ,LGVariables.gsr );
		//Sleep matrix
		LGVariables.sleep= cgmJSF.createnewMatrix(1,LGVariables.kj+1,LGVariables.sleep );
		//Physical activity matrix
		LGVariables.phys_act= cgmJSF.createnewMatrix(1,LGVariables.kj+1 ,LGVariables.phys_act );

		//2. Update values of matrices:
		LGVariables.gs.set(0,LGVariables.kj,cgm);
		LGVariables.ee.set(0,LGVariables.kj,ee);
		LGVariables.gsr.set(0,LGVariables.kj,gsr);
		LGVariables.sleep.set(0,LGVariables.kj,sleep);
		LGVariables.phys_act.set(0,LGVariables.kj,phys_act);

		//Set temporal basal rate Matrix:
		//1. Create a new Matrix
		Matrix basal_temp = new Matrix (1,LGVariables.basal_insulin.getColumnDimension());
		//2. Set values read from stored Local Global Varibles
		for(int i=0;i<LGVariables.basal_insulin.getColumnDimension();i++)
			basal_temp.set(0, i, LGVariables.basal_insulin.get(0, i));


		/* ***************************************
		 *  Insulin on Board calculation
		 */

		//Create the instance for IOBcalculations
		m20150711_calculate_IOB IOBcalculate = new m20150711_calculate_IOB(LGVariables.bolus_insulin,basal_temp);   


		//Update IOB_total matrix in LGVariables
		LGVariables.IOB_total=cgmJSF.createnewMatrix(LGVariables.kj+1,1, LGVariables.IOB_total);
		LGVariables.IOB_total.set(LGVariables.kj, 0, IOBcalculate.IOB());


		/* * ***************************************
		 *  Meal detection/Bolus algorithm
		 */

		//Create the instance running meal_detection_bolus algorithm
		m20150711_run_meal_detection_bolus_algorithm mealDetectionBolus = new m20150711_run_meal_detection_bolus_algorithm(LGVariables.meal_states,LGVariables.meal_covariance,LGVariables.bolus_insulin,
				LGVariables.meal_bolus_amount,LGVariables.meal_detection,LGVariables.meal_detection_time,LGVariables.correction_bolus_amount,
				LGVariables.correction_detection,LGVariables.correction_detection_time,LGVariables.correction_limit,LGVariables.gs,LGVariables.kj,
				LGVariables.meal_g_basal,LGVariables.meal_gpc_gs_slope_degree,LGVariables.meal_gpc_mu,LGVariables.sleep,LGVariables.phys_act,
				LGVariables.IOB_total,LGVariables.body_weight);
		mealDetectionBolus.run_meal_detection_bolus_algorithm();

		//Create the gpc instance:
		// Use matrix 8x1 for
		Matrix matrix = new Matrix (8,1);
		m20150711_gpc m_gpc = new m20150711_gpc (LGVariables.gs,LGVariables.ee,LGVariables.gsr,LGVariables.kj,LGVariables.phi,
				LGVariables.phi_ee,LGVariables.phi_gsr,LGVariables.armax_parameters,LGVariables.armax_covariance,
				LGVariables.armax_lamda,LGVariables.armax_err,LGVariables.arma_parameters_ee,LGVariables.arma_lamda_ee,
				LGVariables.arma_covariance_ee,LGVariables.arma_err_ee,LGVariables.arma_parameters_gsr,
				LGVariables.arma_lamda_gsr,LGVariables.arma_covariance_gsr,LGVariables.arma_err_gsr,LGVariables.A_state,
				LGVariables.A_state_ee,LGVariables.A_state_gsr,LGVariables.C_state,LGVariables.C_state_ee,LGVariables.C_state_gsr,
				LGVariables.B_state,LGVariables.K_state,LGVariables.K_state_ee,LGVariables.K_state_gsr,LGVariables.M,LGVariables.L,
				LGVariables.L_ee,LGVariables.L_gsr,LGVariables.M_ee,LGVariables.M_gsr,LGVariables.X_state,LGVariables.X_state_ee,
				LGVariables.X_state_gsr, LGVariables.ee_prediction, LGVariables.gsr_prediction, LGVariables.g_prediction,
				LGVariables.reference_glucose,LGVariables.insulin_sensitivity_constant,LGVariables.basal_insulin,
				LGVariables.IOB_prediction,LGVariables.maximum_insulin,LGVariables.total_daily_unit,LGVariables.insulin_sensitivity_factor,
				LGVariables.body_weight,LGVariables.meal_gpc_mu,LGVariables.bolus_insulin,0,matrix);
		m_gpc.gpc();

		//Create the CPA_paralleled_calculation_JF module to 
		CPA_Module_paralleled_calculation_JF CPA_module_paralleled_cal = new CPA_Module_paralleled_calculation_JF(LGVariables.gs,LGVariables.ee,LGVariables.gsr,LGVariables.kj,LGVariables.phi,LGVariables.phi_ee,LGVariables.phi_gsr,LGVariables.armax_parameters,LGVariables.armax_covariance,LGVariables.armax_lamda,LGVariables.armax_err,LGVariables.arma_parameters_ee,LGVariables.arma_lamda_ee,LGVariables.arma_covariance_ee,LGVariables.arma_err_ee,LGVariables.arma_parameters_gsr,LGVariables.arma_lamda_gsr,LGVariables.arma_covariance_gsr,LGVariables.arma_err_gsr,LGVariables.A_state,LGVariables.A_state_ee,LGVariables.A_state_gsr,LGVariables.C_state,LGVariables.C_state_ee,LGVariables.C_state_gsr,LGVariables.B_state,LGVariables.K_state,LGVariables.K_state_ee,LGVariables.K_state_gsr,LGVariables.M,LGVariables.L,LGVariables.L_ee,LGVariables.L_gsr,LGVariables.M_ee,LGVariables.M_gsr,LGVariables.X_state,LGVariables.X_state_ee,LGVariables.X_state_gsr, LGVariables.ee_prediction, LGVariables.gsr_prediction, LGVariables.g_prediction,LGVariables.reference_glucose,LGVariables.insulin_sensitivity_constant,LGVariables.basal_insulin,LGVariables.IOB_prediction,LGVariables.maximum_insulin,LGVariables.total_daily_unit,LGVariables.insulin_sensitivity_factor,LGVariables.body_weight,LGVariables.meal_gpc_mu,LGVariables.bolus_insulin);

		//Run CPA fuction
		try {
			CPA_module_paralleled_cal.CPA();
		} catch (Exception ex) {
			Logger.getLogger(AlgorithmManager.class.getName()).log(Level.SEVERE, null, ex);
		}

		/* *****************************************
		 * Hypoglycemia detection algorithm - alarm 
		 */

		//Hypo_alarm manager instance
		hypo_alarm hypo =  new hypo_alarm (LGVariables.hypo_threshold.transpose(),LGVariables.hypo_slope_degree.transpose(),
				LGVariables.hypo_alarm.transpose(),LGVariables.carb_amount,LGVariables.carb_type,LGVariables.hypo_phase.transpose(),
				LGVariables.hypo_phase_old.transpose(),LGVariables.repeated_immediate_alarm.transpose(),LGVariables.gs,
				LGVariables.kj,LGVariables.g_prediction,LGVariables.phys_act,LGVariables.sleep);
		//Run the hypo algorithm
		hypo.m20150711_hypo_alarm();


		//CLOSE LOOP Data Batch Information:
		//Check if exercise, sleep or meal has been detected, to categorize the batch
		if(LGVariables.phys_act.get(0, LGVariables.kj-1)==1)
			LGVariables.batch_CL[LGVariables.kj-1]="Exercise";
		else if(LGVariables.sleep.get(0 ,LGVariables.kj-1)==1) 
			LGVariables.batch_CL[LGVariables.kj-1]="Sleep";
		else if(LGVariables.meal_detection.get(LGVariables.kj-1,0)==1) 
			LGVariables.batch_CL[LGVariables.kj-1]="Meal";
		else
			//If none detected, batch is "Other"
			LGVariables.batch_CL[LGVariables.kj-1]="Other";

		//Batch
		LGVariables.batch_CL=hypo.createnewString(LGVariables.kj+1, LGVariables.batch_CL);

		//Basal and bolus insulin calculated
		LGVariables.basal_insulin_calculated=cgmJSF.createnewMatrix(8,LGVariables.basal_insulin.getColumnDimension()+1 , LGVariables.basal_insulin_calculated);
		LGVariables.bolus_insulin_calculated=cgmJSF.createnewMatrix(LGVariables.bolus_insulin.getRowDimension()+1,1, LGVariables.bolus_insulin_calculated);
		LGVariables.basal_insulin_calculated=LGVariables.basal_insulin;
		LGVariables.bolus_insulin_calculated=LGVariables.bolus_insulin;


		//LGVaribales index is increased
		LGVariables.kj++;

		//TODO DEBUG: Check if a temporal basal rate value is around 0, and force a greater value
		if((LGVariables.basal_insulin.get(0, LGVariables.basal_insulin.getColumnDimension()-1))>-0.01 && (LGVariables.basal_insulin.get(0, LGVariables.basal_insulin.getColumnDimension()-1))<0.01){
			LGVariables.basal_insulin.set(0, LGVariables.basal_insulin.getColumnDimension()-1,_FORCE_BASAL);
		}

		//TODO DEBUG: Check if a bolus value is around 0, and force a greater value
		if((LGVariables.bolus_insulin.get(LGVariables.bolus_insulin.getRowDimension()-1, 0))>-0.01 && (LGVariables.bolus_insulin.get(LGVariables.bolus_insulin.getRowDimension()-1, 0))<0.01){
			LGVariables.bolus_insulin.set(LGVariables.bolus_insulin.getRowDimension()-1, 0,_FORCE_BOLUS);
		}

		//Initialize the result matrix: it contains basal and bolus
		Matrix insulinSuggestion= new Matrix (1,2);
		insulinSuggestion.set(0,0,LGVariables.bolus_insulin.get(LGVariables.bolus_insulin.getRowDimension()-1, 0) );
		insulinSuggestion.set(0,1,LGVariables.basal_insulin.get(0, LGVariables.basal_insulin.getColumnDimension()-1) );

		/*
		if (LGVariables.hypo_alarm.get(LGVariables.kj-1,0)==1 && LGVariables.hypo_phase.get(LGVariables.kj-1,0)==5 && LGVariables.repeated_immediate_alarm.get(LGVariables.kj-1,0)==0){
			Toast toast = Toast.makeText(ctx, "Immediate Alarm: Consume  "+LGVariables.carb_amount[LGVariables.kj-1]+"grams of,  "+LGVariables.carb_type[LGVariables.kj-1],Toast.LENGTH_LONG);
			toast.show();
			send_text_message stm = new send_text_message ("mertsevil1991@gmail.com", "mertsevil1991@hotmail.com", "Immediate Alarm: Consume  "+LGVariables.carb_amount[LGVariables.kj]+"grams of,  "+LGVariables.carb_type[LGVariables.kj],"Immediate Alarm: Consume  "+LGVariables.carb_amount[LGVariables.kj]+"grams of,  "+LGVariables.carb_type[LGVariables.kj]);
			try {
				stm.generateAndSendEmail();
			} catch (AddressException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
		else if (LGVariables.hypo_alarm.get(LGVariables.kj-1,0)==1 && LGVariables.hypo_phase.get(LGVariables.kj-1,0)==5 && LGVariables.repeated_immediate_alarm.get(LGVariables.kj-1,0)==1){
			send_text_message stm = new send_text_message ("mertsevil1991@gmail.com", "mertsevil1991@hotmail.com", "Immediate Alarm: This carb may not be needed. Confirm with subject or MD,Consume "+LGVariables.carb_amount[LGVariables.kj]+"grams of "+LGVariables.carb_type[LGVariables.kj],"Immediate Alarm: This carb may not be needed. Confirm with subject or MD,Consume "+LGVariables.carb_amount[LGVariables.kj]+"grams of "+LGVariables.carb_type[LGVariables.kj]);
			try {
				stm.generateAndSendEmail();
			} catch (AddressException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			} 
			Toast toast = Toast.makeText(ctx, "Immediate Alarm: This carb may not be needed. Confirm with subject or MD,Consume "+LGVariables.carb_amount[LGVariables.kj-1]+"grams of "+LGVariables.carb_type[LGVariables.kj-1],Toast.LENGTH_LONG);
			toast.show();
		}
		else if (LGVariables.hypo_alarm.get(LGVariables.kj-1,0)==1 && LGVariables.hypo_phase.get(LGVariables.kj-1,0)<5){
			send_text_message stm = new send_text_message ("mertsevil1991@gmail.com", "mertsevil1991@hotmail.com", "Early Alarm: Consume  "+LGVariables.carb_amount[LGVariables.kj]+"grams of "+LGVariables.carb_type[LGVariables.kj],"Early Alarm: Consume  "+LGVariables.carb_amount[LGVariables.kj]+"grams of "+LGVariables.carb_type[LGVariables.kj]);
			Toast toast = Toast.makeText(ctx, "Early Alarm: Consume  "+LGVariables.carb_amount[LGVariables.kj-1]+"grams of "+LGVariables.carb_type[LGVariables.kj-1],Toast.LENGTH_LONG);
			toast.show();
		}

		if(true){
			send_text_message stm = new send_text_message ("mertsevil1991@gmail.com", "mertsevil1991@hotmail.com", "Hyperalarm "+100+"grams of "+100,"HYPER ALARM: Consume  "+100+"grams of "+50);  
			try {
				stm.generateAndSendEmail();
			} catch (AddressException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}*/


		// return (LGVariables.kj-20);
		//return (LGVariables.basal_insulin.get(0, LGVariables.basal_insulin.getColumnDimension()-1));

		//TODO Debug values to return - basalBolus
		//Matrix basalBolus = new Matrix (new double[][]{{1},{0}});
		return  insulinSuggestion;
	}

}
