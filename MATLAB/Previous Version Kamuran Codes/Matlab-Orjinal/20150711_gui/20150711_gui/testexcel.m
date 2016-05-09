function []= testexcel()
clc
clear all
kj=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\kj');
body_weight=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\body_weight');
basal_insulin=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\basal_insulin');
bolus_insulin=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\bolus_insulin');
ee=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\ee');
phys_act=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\phys_act');
sleep=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\sleep');
gsr=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\gsr');
IOB_total=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\IOB_total');
meal_states=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\meal_states');
meal_bolus_amount=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\meal_bolus_amount');
meal_detection=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\meal_detection');
meal_detection_time=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\meal_detection_time');
correction_bolus_amount=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\correction_bolus_amount');
correction_detection=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\correction_detection');
correction_detection_time=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\correction_detection_time');
correction_limit=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\correction_limit');
meal_g_basal=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\meal_g_basal');
meal_gpc_gs_slope_degree=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\meal_gpc_gs_slope_degree');
meal_gpc_mu=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\meal_gpc_mu');
phi=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\phi');
phi_ee=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\phi_ee');
phi_gsr=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\phi_gsr');
armax_parameters=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\armax_parameters');
armax_lamda=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\armax_lamda');
armax_err=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\armax_err');
arma_parameters_ee=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\arma_parameters_ee');
arma_lamda_ee=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\arma_lamda_ee');
arma_err_ee=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\arma_err_ee');
arma_parameters_gsr=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\arma_parameters_gsr');
arma_lamda_gsr=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\arma_lamda_gsr');
arma_err_gsr=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\arma_err_gsr');
X_state=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\X_state');
X_state_ee=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\X_state_ee');
X_state_gsr=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\X_state_gsr');
ee_prediction=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\ee_prediction');
gsr_prediction=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\gsr_prediction');
g_prediction=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\g_prediction');
reference_glucose=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\reference_glucose');
insulin_sensitivity_constant=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\insulin_sensitivity_constant');
insulin_sensitivity_factor=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\insulin_sensitivity_factor');
IOB_prediction=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\IOB_prediction');
maximum_insulin=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\maximum_insulin');
total_daily_unit=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\total_daily_unit');
hypo_threshold=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\hypo_threshold');
hypo_slope_degree=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\hypo_slope_degree');
hypo_alarm=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\hypo_alarm');
hypo_phase=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\hypo_phase');
hypo_phase_old=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\hypo_phase_old');
repeated_immediate_alarm=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\repeated_immediate_alarm');
bolus_insulin_calculated=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\bolus_insulin_calculated');
basal_insulin_calculated=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\basal_insulin_calculated');
gs=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\gs');
CGM_retuning_with_noise_generator=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\CGM_retuning_with_noise_generator');
CGM_retuning_without_noise_generator=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\CGM_retuning_without_noise_generator');

for i=1:kj
   str = num2str(i-1);
   meal_covariance(:,:,i+1)=xlsread(['D:\Phd\Research\Kamuran`s Code\Test Save\meal_covariance',str]);
   armax_covariance(:,:,i+1)=xlsread(['D:\Phd\Research\Kamuran`s Code\Test Save\armax_covariance',str]);
   arma_covariance_ee(:,:,i+1)=xlsread(['D:\Phd\Research\Kamuran`s Code\Test Save\arma_covariance_ee',str]);
   arma_covariance_gsr(:,:,i+1)=xlsread(['D:\Phd\Research\Kamuran`s Code\Test Save\arma_covariance_gsr',str]);
   A_state(:,:,i+1)=xlsread(['D:\Phd\Research\Kamuran`s Code\Test Save\A_state',str]);
   A_state_ee(:,:,i+1)=xlsread(['D:\Phd\Research\Kamuran`s Code\Test Save\A_state_ee',str]);
   A_state_gsr(:,:,i+1)=xlsread(['D:\Phd\Research\Kamuran`s Code\Test Save\A_state_gsr',str]);
   C_state(:,:,i+1)=xlsread(['D:\Phd\Research\Kamuran`s Code\Test Save\C_state',str]);
   C_state_ee(:,:,i+1)=xlsread(['D:\Phd\Research\Kamuran`s Code\Test Save\C_state_ee',str]);
   C_state_gsr(:,:,i+1)=xlsread(['D:\Phd\Research\Kamuran`s Code\Test Save\C_state_gsr',str]);
   B_state(:,:,i+1)=xlsread(['D:\Phd\Research\Kamuran`s Code\Test Save\B_state',str]);
   K_state(:,:,i+1)=xlsread(['D:\Phd\Research\Kamuran`s Code\Test Save\K_state',str]);
   K_state_ee(:,:,i+1)=xlsread(['D:\Phd\Research\Kamuran`s Code\Test Save\K_state_ee',str]);
   K_state_gsr(:,:,i+1)=xlsread(['D:\Phd\Research\Kamuran`s Code\Test Save\K_state_gsr',str]);
   M(:,:,i+1)=xlsread(['D:\Phd\Research\Kamuran`s Code\Test Save\M',str]);
   M_ee(:,:,i+1)=xlsread(['D:\Phd\Research\Kamuran`s Code\Test Save\M_ee',str]);
   M_gsr(:,:,i+1)=xlsread(['D:\Phd\Research\Kamuran`s Code\Test Save\M_gsr',str]);
   L(:,:,i+1)=xlsread(['D:\Phd\Research\Kamuran`s Code\Test Save\L',str]);
   L_ee(:,:,i+1)=xlsread(['D:\Phd\Research\Kamuran`s Code\Test Save\L_ee',str]);
   L_gsr(:,:,i+1)=xlsread(['D:\Phd\Research\Kamuran`s Code\Test Save\L_gsr',str]);
end

carb_type=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\carb_type');
carb_amount=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\carb_amount');
batch_CL=xlsread('D:\Phd\Research\Kamuran`s Code\Test Save\batch_CL');

ee=ee';
gs=gs';
gsr=gsr';
sleep=sleep';

save testdata;

end