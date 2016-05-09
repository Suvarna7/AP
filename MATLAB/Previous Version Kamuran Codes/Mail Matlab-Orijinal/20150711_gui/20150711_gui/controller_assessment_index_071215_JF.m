function [I_track]=controller_assessment_index_071215_JF(umaxx_account, L_account,rf_account, wfcn_account,kj,gs_f,  ins_f,  B_w, g_prediction_f)
% load  controller_limit  umaxx_account L_account rf_account  wfcn_account
% load  prevdata  gs_f  ins_f  B_w g_prediction_f
%% initial condition
clc
clear all
B_w=74.8
g_prediction_f=zeros(8,22);
ins_f=zeros(8,22);
gs_f=[ones(1,21)*298 200]
wfcn_account=ones(8,22);
umaxx_account=[ones(8,21)*25 zeros(8,1)]

kj=22;
rf_account=zeros(8,22);

L_account(:,:,21)=zeros(8,8);
L_account(:,:,22) =	[		-0.08750850227330874			-0.10095019816043771			0.0			0.0			0.0			0.0			0.0			0.0;
			-0.1032275349123529			-0.08750850227330874			-0.10095019816043771			0.0			0.0			0.0			0.0			0.0;
			-0.0987380793919044			-0.1032275349123529			-0.08750850227330874			-0.10095019816043771			0.0			0.0			0.0			0.0;
			-0.11012604801182047			-0.0987380793919044			-0.1032275349123529			-0.08750850227330874			-0.10095019816043771			0.0		0.0			0.0;
			-0.10929689955542111			-0.11012604801182047			-0.0987380793919044			-0.1032275349123529			-0.08750850227330874			-0.10095019816043771			0.0			0.0;
			-0.10090232425550982			-0.10929689955542111			-0.11012604801182047			-0.0987380793919044			-0.1032275349123529			-0.08750850227330874			-0.10095019816043771			0.0;
			-0.07434327920407688			-0.10090232425550982			-0.10929689955542111			-0.11012604801182047			-0.0987380793919044			-0.1032275349123529			-0.08750850227330874			-0.10095019816043771;
			-0.0908757534816805			-0.07434327920407688			-0.10090232425550982			-0.10929689955542111			-0.11012604801182047			-0.0987380793919044			-0.1032275349123529			-0.08750850227330874]

st=10
start_time=22
if  kj<=21
I_track=zeros(21,1)
data_mem=[]
I_u_constrain=[]
I_me_inst=0
I_error_rspeed=0
save track I_track data_mem I_u_constrain   I_error_rspeed  I_me_inst
else
    load track I_track data_mem  I_u_constrain  I_error_rspeed  I_me_inst
end
%% process 
if  kj>=start_time
    ref=rf_account(:,end)
    umaxx=umaxx_account(:,end)
    L=L_account(:,:,end)
    wfcn=wfcn_account(:,end)

%% objective function V=(Y-w)'*(Y-w)+((du'*diag(IC)*du))
u=ins_f(1,kj)
y_prediction=g_prediction_f
ins_f(:,kj)
L
g_prediction_f(:,kj)
y_predict=g_prediction_f(:,kj)+L*ins_f(:,kj)
wt_y=1
total_unit=B_w*wfcn
IC=1800./total_unit
y_real=gs_f
delta_u=diff([ins_f(1,kj-1);ins_f(:,kj)])
%% recusive opretor
I_yprediction_inst=((y_predict-ref)).*wt_y.*((y_predict-ref))
I_u=(delta_u'*diag(IC)*delta_u)
if sum(I_u)<0.0001
    I_cost_function=10^7 
else
I_cost_function=sum(I_yprediction_inst)/sum(I_u) % the ratio of control prower and insulin amount
end
if abs(ins_f(1,kj)-umaxx(1))<10^-4 
I_u_constrain(end+1)=1
else
I_u_constrain(end+1)=0
end
I_me_inst=(y_prediction(1,kj-1)-y_real(kj))
I_me=(I_me_inst'*I_me_inst)^0.5          
% error regression speed 
if I_me>10%%%%
    I_error_rspeed=st+I_error_rspeed
else 
        I_error_rspeed=0
end
if kj<start_time+5
    I_u_contraint_data=0
else
I_u_contraint_data=sum(I_u_constrain(end-2:end))
end 
  I_track=[ I_yprediction_inst;I_u;I_me;I_u_contraint_data;I_error_rspeed;I_cost_function;umaxx(1) ]
  data_mem=[data_mem,I_track]
  save track I_track data_mem  I_u_constrain  I_error_rspeed  I_me_inst
  error_summation=error_display_JF(kj,gs_f)
end

save track I_track data_mem I_u_constrain   I_error_rspeed  I_me_inst

end
