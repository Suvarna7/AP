%% Run meal detection and meal bolusing algorithms. Kamuran Turksoy
function [meal_states,meal_covariance,bolus_insulin,...
    meal_bolus_amount,meal_detection,meal_detection_time,...
    correction_bolus_amount,correction_detection,correction_detection_time,correction_limit,...
    meal_g_basal,meal_gpc_gs_slope_degree,meal_gpc_mu]=m20150711_run_meal_detection_bolus_algorithm(meal_states,meal_covariance,bolus_insulin,...
    meal_bolus_amount,meal_detection,meal_detection_time,...
    correction_bolus_amount,correction_detection,correction_detection_time,correction_limit,...
    gs,kj,meal_g_basal,meal_gpc_gs_slope_degree,meal_gpc_mu,...
    sleep,phys_act,IOB_total,body_weight)

clc
clear all
kj=2
gs_in=298;%must be defined before the experiment glocuse
ee_in=5.8512;%must be defined before the experiment
gsr_in=0.0139;%must be defined before the experiment
sleep_in=0;%must be defined before the experiment
phys_act_in=0;%must be defined before the experiment
body_weight=74.8;%must be defined before the experiment kg
%%
gs=gs_in*ones(20,1);
%%
armband_data_with_time=[];
ee=ee_in*ones(20,1);
phys_act=phys_act_in*ones(20,1);
sleep=sleep_in*ones(20,1);
gsr=gsr_in*ones(20,1);
%%
IOB_total=zeros(20,1);
bolus_insulin=zeros(20,1);
basal_insulin=zeros(8,20);
%%
meal_states=[0.1 gs_in 0 0 0.068 0.037 1.3 20]'*ones(1,20);
meal_covariance(:,:,20)=eye(8,8);
meal_bolus_amount=zeros(20,1);
meal_detection=zeros(20,1);
meal_detection_time=zeros(20,1);
correction_bolus_amount=zeros(20,1);
correction_detection=zeros(20,1);
correction_detection_time=zeros(20,1);
correction_limit=zeros(20,1);
meal_g_basal=gs_in*ones(20,1);
meal_gpc_gs_slope_degree=45*ones(20,1);
meal_gpc_mu=0.5*ones(20,1);
%%
phi=zeros(24,20);
phi_ee=zeros(4,20);
phi_gsr=zeros(4,20);
armax_parameters=zeros(24,20);
armax_covariance(:,:,20)=eye(24);
armax_lamda=0.5*ones(20,1);
armax_err=zeros(20,1);
arma_parameters_ee=zeros(4,20);
arma_lamda_ee=0.5*ones(20,1);
arma_covariance_ee(:,:,20)=eye(4);
arma_err_ee=zeros(20,1);
arma_parameters_gsr=zeros(4,20);
arma_lamda_gsr=0.5*ones(20,1);
arma_covariance_gsr(:,:,20)=eye(4);
arma_err_gsr=zeros(20,1);
A_state(:,:,20)=zeros(21,21);
A_state_ee(:,:,20)=zeros(4,4);
A_state_gsr(:,:,20)=zeros(4,4);
C_state(:,:,20)=zeros(1,21);
C_state_ee(:,:,20)=zeros(1,4);
C_state_gsr(:,:,20)=zeros(1,4);
B_state(:,:,20)=zeros(21,3);
K_state(:,:,20)=zeros(21,1);
K_state_ee(:,:,20)=zeros(4,1);
K_state_gsr(:,:,20)=zeros(4,1);
M(:,:,20)=zeros(8,21);
L(:,:,20)=zeros(8,8);
L_ee(:,:,20)=zeros(8,8);
L_gsr(:,:,20)=zeros(8,8);
M_ee(:,:,20)=zeros(8,4);
M_gsr(:,:,20)=zeros(8,4);
X_state=zeros(21,20);
X_state_ee=zeros(4,20);
X_state_gsr=zeros(4,20);
ee_prediction=zeros(8,20);
gsr_prediction=zeros(8,20);
g_prediction=zeros(8,20);
reference_glucose=zeros(8,20);
insulin_sensitivity_constant=0.1*ones(8,20);
IOB_prediction=zeros(8,20);
maximum_insulin=25*ones(8,20);
total_daily_unit=ones(8,20);
insulin_sensitivity_factor=ones(8,20);
%%
hypo_threshold=zeros(20,1);
hypo_slope_degree=45*ones(20,1);
hypo_alarm=zeros(20,1);
carb_amount={''};
carb_type={''};
hypo_phase=zeros(20,1);
hypo_phase_old=zeros(20,1);
repeated_immediate_alarm=zeros(20,1);
%%
mdata=[];
batch_CL={''};
bolus_insulin_calculated=zeros(20,1);
basal_insulin_calculated=zeros(8,20);


% kj=22;
% meal_states=[0.1 298 0 0 0.068 0.037 1.3 20]'*ones(1,20);
% sleep=zeros(1,21);
% phys_act=zeros(1,21);
% meal_gpc_mu=ones(21,1)*0.5;
% meal_gpc_gs_slope_degree= ones(21,1)*45;
% meal_g_basal=ones(1,21)*298;
% meal_detection_time=zeros(21,1);
% meal_detection=zeros(21,1);
% meal_covariance(:,:,20)=eye(24);
% meal_covariance(:,:,21)=eye(24);
% gs=ones(1,20)*298;
% gs(1,21)=200;
% meal_bolus_amount=zeros(21,1);
% correction_limit=zeros(21,1);
% correction_detection_time=zeros(21,1);
% correction_bolus_amount=zeros(21,1);
% bolus_insulin=zeros(21,1);
% body_weight=74.8;
% IOB_total=zeros(21,1);

if kj>12 % update basal glucose value for meal detection algorithm
    meal_g_basal=cat(1,meal_g_basal,mean(gs(kj-11:kj-6)));
else
    meal_g_basal=cat(1,meal_g_basal,100);
end

if kj>5 % update glucose slope
    meal_gpc_line=polyfit([0 5 10 15 20]',gs(kj-4:kj,1),1);
    meal_gpc_gs_slope_degree=cat(1,meal_gpc_gs_slope_degree,radtodeg(atan(meal_gpc_line(1))));
else
    meal_gpc_gs_slope_degree=cat(1,meal_gpc_gs_slope_degree,45);
end

if sleep(kj,1)==1 % update mu (mu is defined in our TBME paper)
    meal_gpc_mu=cat(1,meal_gpc_mu,1-1*max(0.5,meal_gpc_gs_slope_degree(kj,1)/90));
elseif phys_act(kj,1)==1
    meal_gpc_mu=cat(1,meal_gpc_mu,1-0.025*max(0.5,meal_gpc_gs_slope_degree(kj,1)/90));
else
    meal_gpc_mu=cat(1,meal_gpc_mu,1-max(0.5,meal_gpc_gs_slope_degree(kj,1)/90));
end
 % Process noise 
R_meal=100; % Measurement noise
% ex=meal_states(:,kj-1)
% ex2=meal_covariance(:,:,kj-1)
% ex3=R_meal
% ex4=Q_p_meal
% ex5=meal_g_basal(kj,1)
% ex6=gs(kj,1)
Q_p_meal=1e-6*diag([1 1 1e+3 1e+3 1e+4 1e+5 1e+4 1e+5])
[meal_states(:,kj),meal_covariance(:,:,kj)]=m20141215_ukf_meal(gs(kj,1),meal_states(:,kj-1),meal_covariance(:,:,kj-1),R_meal,Q_p_meal,meal_g_basal(kj,1));
[meal_bolus_amount(kj,1),correction_detection(kj,1),meal_detection(kj,1),meal_detection_time(kj,1),correction_detection_time(kj,1),correction_limit(kj,1),correction_bolus_amount(kj,1)]=m20150308_detection_of_meal(meal_states(:,kj),gs(kj,1),meal_detection(kj-1,1),meal_detection_time(kj-1,1),correction_detection_time(kj-1,1),correction_limit(kj-1,1),correction_bolus_amount(kj-1,1),kj,body_weight,sleep(kj,1),phys_act(kj,1),meal_gpc_mu(kj,1));


bolus_insulin=cat(1,bolus_insulin,round(max(0,meal_bolus_amount(kj,1)*meal_detection(kj,1)+correction_detection(kj,1)*correction_bolus_amount(kj,1)-IOB_total(kj,1))/0.05)*0.05);% Update bolus insulin