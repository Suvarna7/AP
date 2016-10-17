%% Run meal detection and meal bolusing algorithms. Kamuran Turksoy
function [meal_states,meal_covariance,bolus_insulin,...
    meal_bolus_amount,meal_detection,meal_detection_time,...
    correction_bolus_amount,correction_detection,correction_detection_time,correction_limit,...
    meal_g_basal,meal_gpc_gs_slope_degree,meal_gpc_mu]=m20150711_run_meal_detection_bolus_algorithm(meal_states,meal_covariance,bolus_insulin,...
    meal_bolus_amount,meal_detection,meal_detection_time,...
    correction_bolus_amount,correction_detection,correction_detection_time,correction_limit,...
    gs,kj,meal_g_basal,meal_gpc_gs_slope_degree,meal_gpc_mu,...
    sleep,phys_act,IOB_total,body_weight)

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
    meal_gpc_mu=cat(1,meal_gpc_mu,1-0.75*max(0.5,meal_gpc_gs_slope_degree(kj,1)/90));%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%changed
elseif phys_act(kj,1)==1
    meal_gpc_mu=cat(1,meal_gpc_mu,1-0.025*max(0.5,meal_gpc_gs_slope_degree(kj,1)/90));
else
    meal_gpc_mu=cat(1,meal_gpc_mu,1-1*max(0.5,meal_gpc_gs_slope_degree(kj,1)/90));%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%changed
end

Q_p_meal=1e-6*diag([1 1 1e+3 1e+3 1e+4 1e+5 1e+4 1e+5]); % Process noise 
R_meal=100; % Measurement noise
[meal_states(:,kj),meal_covariance(:,:,kj)]=m20141215_ukf_meal(gs(kj,1),meal_states(:,kj-1),meal_covariance(:,:,kj-1),R_meal,Q_p_meal,meal_g_basal(kj,1));
[meal_bolus_amount(kj,1),correction_detection(kj,1),meal_detection(kj,1),meal_detection_time(kj,1),correction_detection_time(kj,1),correction_limit(kj,1),correction_bolus_amount(kj,1)]=m20150308_detection_of_meal(meal_states(:,kj),gs(kj,1),meal_detection(kj-1,1),meal_detection_time(kj-1,1),correction_detection_time(kj-1,1),correction_limit(kj-1,1),correction_bolus_amount(kj-1,1),kj,body_weight,sleep(kj,1),phys_act(kj,1),meal_gpc_mu(kj,1));


bolus_insulin=cat(1,bolus_insulin,round(max(0,meal_bolus_amount(kj,1)*meal_detection(kj,1)+correction_detection(kj,1)*correction_bolus_amount(kj,1)-IOB_total(kj,1))/0.05)*0.05);% Update bolus insulin