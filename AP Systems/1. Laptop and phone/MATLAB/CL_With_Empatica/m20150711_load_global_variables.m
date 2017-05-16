%% Define global variables. Kamuran Turksoy
global gs kj
%%
global armband_data_with_time ee phys_act sleep gsr
%%
global IOB_total bolus_insulin bolus_insulin_meal basal_insulin
%%
global meal_states meal_covariance meal_bolus_amount meal_detection 
global meal_detection_time correction_bolus_amount correction_detection
global correction_detection_time correction_limit
global meal_g_basal meal_gpc_gs_slope_degree meal_gpc_mu
%%
global phi phi_ee phi_gsr
global armax_parameters armax_covariance armax_lamda armax_err
global arma_parameters_ee arma_lamda_ee arma_covariance_ee arma_err_ee
global arma_parameters_gsr arma_lamda_gsr arma_covariance_gsr arma_err_gsr
global A_state A_state_ee A_state_gsr
global C_state C_state_ee C_state_gsr
global B_state K_state K_state_ee K_state_gsr
global M L L_ee L_gsr M_ee M_gsr
global X_state X_state_ee X_state_gsr
global ee_prediction gsr_prediction g_prediction
global reference_glucose insulin_sensitivity_constant IOB_prediction
global maximum_insulin total_daily_unit insulin_sensitivity_factor
%%
global body_weight
%%
global hypo_threshold hypo_slope_degree hypo_alarm hypo_alarm_old
global carb_amount carb_type hypo_phase hypo_phase_old 
global repeated_immediate_alarm
global mdata batch_CL
global bolus_insulin_calculated basal_insulin_calculated
global auto_backup_time
global CGM_retuning_with_noise_generator CGM_retuning_without_noise_generator
%%
global type_of_exercise type_of_entry fault fault_reason
