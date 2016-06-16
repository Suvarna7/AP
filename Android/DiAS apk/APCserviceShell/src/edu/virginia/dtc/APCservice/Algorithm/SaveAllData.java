/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.dtc.APCservice.Algorithm;

import edu.virginia.dtc.APCservice.DataManagement.MatrixDatabaseManager;

/**
 * SaveAllData class used to saved all variables at once
 * @author Caterina Lazaro
 * @version 2.0 March 2016
 */
public class SaveAllData{

	public static void saveAll(MatrixDatabaseManager dataManager){

		m20150711_load_global_variables lgvariables = new m20150711_load_global_variables();

		//VirtualCgm vcgm = new VirtualCgm(lgvariables.kj);

		Save savedata = new Save (dataManager);
		trackdata t = new trackdata ();
		change_variable cv = new change_variable ();
		prevdata_error_summation prdata = new prevdata_error_summation();
		temp_SEDFR_noise tempSnoise = new temp_SEDFR_noise();
		temp_SEDFR_nonoise tempSnonoise = new temp_SEDFR_nonoise();

		savedata.save(lgvariables.basal_insulin,"basal_insulin");
		savedata.save(lgvariables.bolus_insulin,"bolus_insulin");
		savedata.save(lgvariables.ee,"ee");
		savedata.save(lgvariables.phys_act,"phys_act");
		savedata.save(lgvariables.sleep,"sleep");
		savedata.save(lgvariables.gsr,"gsr");
		savedata.save(lgvariables.IOB_total,"IOB_total");
		savedata.save(lgvariables.meal_states,"meal_states");
		savedata.save(lgvariables.meal_bolus_amount,"meal_bolus_amount");
		savedata.save(lgvariables.meal_detection,"meal_detection");
		savedata.save(lgvariables.meal_detection_time,"meal_detection_time");
		savedata.save(lgvariables.correction_bolus_amount,"correction_bolus_amount");
		savedata.save(lgvariables.correction_detection,"correction_detection");
		savedata.save(lgvariables.correction_detection_time,"correction_detection_time");
		savedata.save(lgvariables.correction_limit,"correction_limit");
		savedata.save(lgvariables.meal_g_basal,"meal_g_basal");
		savedata.save(lgvariables.meal_gpc_gs_slope_degree,"meal_gpc_gs_slope_degree");
		savedata.save(lgvariables.meal_gpc_mu,"meal_gpc_mu");
		savedata.save(lgvariables.phi,"phi");
		savedata.save(lgvariables.phi_ee,"phi_ee");
		savedata.save(lgvariables.phi_gsr,"phi_gsr");
		savedata.save(lgvariables.armax_parameters,"armax_parameters");
		savedata.save(lgvariables.armax_lamda,"armax_lamda");
		savedata.save(lgvariables.armax_err,"armax_err");
		savedata.save(lgvariables.arma_parameters_ee,"arma_parameters_ee");
		savedata.save(lgvariables.arma_lamda_ee,"arma_lamda_ee");
		savedata.save(lgvariables.arma_err_ee,"arma_err_ee");
		savedata.save(lgvariables.arma_parameters_gsr,"arma_parameters_gsr");
		savedata.save(lgvariables.arma_lamda_gsr,"arma_lamda_gsr");
		savedata.save(lgvariables.arma_err_gsr,"arma_err_gsr");
		savedata.save(lgvariables.X_state,"X_state");
		savedata.save(lgvariables.X_state_ee,"X_state_ee");
		savedata.save(lgvariables.X_state_gsr,"X_state_gsr");
		savedata.save(lgvariables.ee_prediction,"ee_prediction");
		savedata.save(lgvariables.gsr_prediction,"gsr_prediction");
		savedata.save(lgvariables.g_prediction,"g_prediction");
		savedata.save(lgvariables.reference_glucose,"reference_glucose");
		savedata.save(lgvariables.insulin_sensitivity_constant,"insulin_sensitivity_constant");
		savedata.save(lgvariables.IOB_prediction,"IOB_prediction");
		savedata.save(lgvariables.maximum_insulin,"maximum_insulin");
		savedata.save(lgvariables.total_daily_unit,"total_daily_unit");
		savedata.save(lgvariables.insulin_sensitivity_factor,"insulin_sensitivity_factor");
		savedata.save(lgvariables.hypo_threshold,"hypo_threshold");
		savedata.save(lgvariables.hypo_slope_degree,"hypo_slope_degree");
		savedata.save(lgvariables.hypo_alarm,"hypo_alarm");
		savedata.save(lgvariables.hypo_phase,"hypo_phase");
		savedata.save(lgvariables.hypo_phase_old,"hypo_phase_old");
		savedata.save(lgvariables.repeated_immediate_alarm,"repeated_immediate_alarm");
		savedata.save(lgvariables.bolus_insulin_calculated,"bolus_insulin_calculated");
		savedata.save(lgvariables.basal_insulin_calculated,"basal_insulin_calculated");
		savedata.save(lgvariables.gs,"gs");

		savedata.saveDouble(lgvariables.CGM_retuning_with_noise_generator,"CGM_retuning_with_noise_generator");
		savedata.saveDouble(lgvariables.CGM_retuning_without_noise_generator,"CGM_retuning_without_noise_generator");
		savedata.saveDouble(lgvariables.body_weight,"body_weight");
		savedata.saveDouble(lgvariables.kj,"kj");

		for(int i=0;i<lgvariables.kj+1;i++){
			savedata.save3DIndexed(savedata.change(lgvariables.meal_covariance,i),"meal_covariance",i);
			savedata.save3DIndexed(savedata.change(lgvariables.armax_covariance,i),"armax_covariance",i);
			savedata.save3DIndexed(savedata.change(lgvariables.arma_covariance_ee,i),"arma_covariance_ee",i);
			savedata.save3DIndexed(savedata.change(lgvariables.arma_covariance_gsr,i),"arma_covariance_gsr",i);
			savedata.save3DIndexed(savedata.change(lgvariables.A_state,i),"A_state",i);
			savedata.save3DIndexed(savedata.change(lgvariables.A_state_ee,i),"A_state_ee",i);
			savedata.save3DIndexed(savedata.change(lgvariables.A_state_gsr,i),"A_state_gsr",i);
			savedata.save3DIndexed(savedata.change(lgvariables.C_state,i),"C_state",i);
			savedata.save3DIndexed(savedata.change(lgvariables.C_state_ee,i),"C_state_ee",i);
			savedata.save3DIndexed(savedata.change(lgvariables.C_state_gsr,i),"C_state_gsr",i);
			savedata.save3DIndexed(savedata.change(lgvariables.B_state,i),"B_state",i);
			savedata.save3DIndexed(savedata.change(lgvariables.K_state,i),"K_state",i);
			savedata.save3DIndexed(savedata.change(lgvariables.K_state_ee,i),"K_state_ee",i);
			savedata.save3DIndexed(savedata.change(lgvariables.K_state_gsr,i),"K_state_gsr",i);
			savedata.save3DIndexed(savedata.change(lgvariables.M,i),"M",i);
			savedata.save3DIndexed(savedata.change(lgvariables.M_ee,i),"M_ee",i);
			savedata.save3DIndexed(savedata.change(lgvariables.M_gsr,i),"M_gsr",i);
			savedata.save3DIndexed(savedata.change(lgvariables.L,i),"L",i);
			savedata.save3DIndexed(savedata.change(lgvariables.L_ee,i),"L_ee",i);
			savedata.save3DIndexed(savedata.change(lgvariables.L_gsr,i),"L_gsr",i);
		}

		savedata.saveStringArray(lgvariables.batch_CL,"batch_CL");
		savedata.saveStringArray(lgvariables.carb_type,"carb_type");
		savedata.saveStringArray(lgvariables.carb_amount,"carb_amount");


		savedata.saveDouble(t.I_error_rspeed,"I_error_rspeed");
		savedata.saveDouble(t.I_me_inst,"I_me_inst");
		savedata.save(t.I_track,"I_track");
		savedata.save(t.data_mem,"data_mem");
		savedata.save(t.I_u_constrain,"I_u_constrain");

		savedata.save(cv.compensate_ins,"compensate_ins");
		savedata.save(cv.flag_constrains,"flag_constrains");
		savedata.save(cv.flag_ratio,"flag_ratio");
		savedata.save(cv.g_prediction_feedback,"g_prediction_feedback");
		savedata.save(cv.ins_CPA,"ins_CPA");
		savedata.save(cv.insulin_CPA,"insulin_CPA");
		savedata.save(cv.lamda_CPA,"lamda_CPA");
		savedata.save(cv.sensor_error,"sensor_error");

		savedata.save(prdata.D_potential,"D_potential");
		savedata.save(prdata.error_summation,"error_summation");
		savedata.saveDouble(prdata.EE,"EE");
		savedata.saveDouble(prdata.ME,"ME");



	}     


}



