function insulin_CPA=CPA_Module_paralleled_calculation_JF(gs,ee,gsr,kj,...
        phi,phi_ee,phi_gsr,...
        armax_parameters,armax_covariance,armax_lamda,armax_err,...
        arma_parameters_ee,arma_lamda_ee,arma_covariance_ee,arma_err_ee,...
        arma_parameters_gsr,arma_lamda_gsr,arma_covariance_gsr,arma_err_gsr,...
        A_state,A_state_ee,A_state_gsr,...
        C_state,C_state_ee,C_state_gsr,...
        B_state,K_state,K_state_ee,K_state_gsr,...
        M,L,L_ee,L_gsr,M_ee,M_gsr,...
        X_state,X_state_ee,X_state_gsr,...
        ee_prediction,gsr_prediction,g_prediction,...
        reference_glucose,insulin_sensitivity_constant,basal_insulin,IOB_prediction,...
        maximum_insulin,total_daily_unit,insulin_sensitivity_factor,body_weight,meal_gpc_mu,bolus_insulin)
[I_track]=controller_assessment_index_071215_JF(maximum_insulin, L,reference_glucose, insulin_sensitivity_factor,kj,gs,basal_insulin,body_weight,g_prediction);
sensor_error=0;
flag_constrains=0;
insulin_error_feedback=0;
compensate_ins=0;
flag_ratio=0;
if kj>25
    load prevdata_error_summation error_summation ME EE
    load change_variable
    gs_CPA=gs;
    ins_CPA=basal_insulin;
    lamda_CPA=armax_lamda;
    g_prediction_CPA=g_prediction;
    denominator=60;
    factor_insulins=1;
    %% sequence: error; Model_error;Umax_error; ratio_error; weight_error;Insulin_dose_error;other;
    if error_summation(2,end)~=NaN
        trh=0.5;
        if ME<30 && (1-ME/denominator)*lamda_CPA(kj-1,1)>trh
            lamda_CPA(kj-1,1)=(1-ME/denominator)*lamda_CPA(kj-1,1);
        else
            lamda_CPA(kj-1,1)=trh;
        end
    end
    %%
    if (gs_CPA(kj)-gs_CPA(kj-1))*(gs_CPA(kj-1)-gs_CPA(kj-2))<0 &&(gs_CPA(kj-2)-gs_CPA(kj-3))*(gs_CPA(kj-1)-gs_CPA(kj-2))<0
        sensor_error(kj)=1;
        CGM_reading_factor=gs_CPA(kj-1)-(gs_CPA(kj)+gs_CPA(kj-2))/2;
        gs_CPA(kj-1)=(gs_CPA(kj)+gs_CPA(kj-2))/2;
    else
        CGM_reading_factor=0;
        sensor_error(kj)=0;
    end
    
    %%
    if error_summation(2,end)~=NaN
        %%%%%%%%%%%%%%%%%%%%%%%%%%%%%CGM tendency
        CGM_factor=error_summation(end,end);
        %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    end
    
    
    if error_summation(1,end)~=-1 && (error_summation(2,end)~=NaN || sensor_error(kj)==1)
        %error feedback
        if error_summation(end,end)+CGM_reading_factor>0 && error_summation(1,end)~=1 && (error_summation(end,end)+CGM_reading_factor)>10
            insulin_error_feedback=-log10((error_summation(end,end)+CGM_reading_factor))/(4.5*60/body_weight);
        else if error_summation(end,end)+CGM_reading_factor<0 && gs_CPA(kj)>70 && (error_summation(end,end)+CGM_reading_factor)<-10
                insulin_error_feedback=log10(abs((error_summation(end,end)+CGM_reading_factor)))/(4.5*60/body_weight)*factor_insulins;
            end
        end
    else
        insulin_error_feedback=0;
    end
    
    
    if error_summation(3,end)==0
        flag_constrains(kj)=1;
    else
        flag_constrains(kj)=0;
    end
    if error_summation(1,end)==-1
        flag_constrains(kj)=-1;
    end
    
    %%%%%%%%%%%%%%g_prediction_feedback%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    
    if kj>30
        g_prediction_feedback(:,kj)=-CGM_factor*0.35*ones(8,1);
    else
        g_prediction_feedback(:,kj)=zeros(8,1);
    end
    
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    if error_summation(4,end)==1
        flag_ratio(kj)=1;
    else
        flag_ratio(kj)=0;
    end
    if flag_ratio==1
            insulin_sensitivity_constant=ones(8,1)/10000;
        
        end
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    
    compensate_ins(kj)=insulin_error_feedback*(bolus_insulin(end)*6+basal_insulin(1,end));
    paralleled=1;
    [phi,phi_ee,phi_gsr,...
        armax_parameters,armax_covariance,lamda_CPA,armax_err,...
        arma_parameters_ee,arma_lamda_ee,arma_covariance_ee,arma_err_ee,...
        arma_parameters_gsr,arma_lamda_gsr,arma_covariance_gsr,arma_err_gsr,...
        A_state,A_state_ee,A_state_gsr,...
        C_state,C_state_ee,C_state_gsr,...
        B_state,K_state,K_state_ee,K_state_gsr,...
        M,L,L_ee,L_gsr,M_ee,M_gsr,...
        X_state,X_state_ee,X_state_gsr,...
        ee_prediction,gsr_prediction,g_prediction,...
        reference_glucose,insulin_sensitivity_constant, basal_insulin,IOB_prediction,...
        maximum_insulin,total_daily_unit,insulin_sensitivity_factor]=m20150711_gpc(gs,ee,gsr,kj,...
        phi,phi_ee,phi_gsr,...
        armax_parameters,armax_covariance,lamda_CPA,armax_err,...
        arma_parameters_ee,arma_lamda_ee,arma_covariance_ee,arma_err_ee,...
        arma_parameters_gsr,arma_lamda_gsr,arma_covariance_gsr,arma_err_gsr,...
        A_state,A_state_ee,A_state_gsr,...
        C_state,C_state_ee,C_state_gsr,...
        B_state,K_state,K_state_ee,K_state_gsr,...
        M,L,L_ee,L_gsr,M_ee,M_gsr,...
        X_state,X_state_ee,X_state_gsr,...
        ee_prediction,gsr_prediction,g_prediction,...
        reference_glucose,insulin_sensitivity_constant,basal_insulin,IOB_prediction,...
        maximum_insulin,total_daily_unit,insulin_sensitivity_factor,body_weight,meal_gpc_mu,bolus_insulin,...
        flag_constrains(kj),g_prediction_feedback(:,kj));
    ins_CPA(:,kj)=basal_insulin(:,kj);
    if ins_CPA(1,kj)+ compensate_ins(kj)>maximum_insulin(1,end)*2
        ins_CPA(1,kj)=maximum_insulin(1,end)*2;
    else
        ins_CPA(1,kj)=ins_CPA(1,kj)+ compensate_ins(kj);
    end
    if ins_CPA(1,kj)>35;
        ins_CPA(1,kj)=35;
    else if ins_CPA(1,kj)<0
            ins_CPA(1,kj)=0;
        end
    end
    save change_variable ins_CPA  compensate_ins sensor_error flag_ratio flag_constrains  lamda_CPA g_prediction_feedback
    if kj<=25
        ins_CPA(1,kj)=0;
    end
   insulin_CPA=ins_CPA(1,kj);
else
insulin_CPA=basal_insulin(1,kj);
ins_CPA(1,kj)=0;
lamda_CPA=armax_lamda;
g_prediction_feedback(:,kj)=zeros(8,1);
 save change_variable ins_CPA  compensate_ins sensor_error flag_ratio flag_constrains  lamda_CPA g_prediction_feedback
end



