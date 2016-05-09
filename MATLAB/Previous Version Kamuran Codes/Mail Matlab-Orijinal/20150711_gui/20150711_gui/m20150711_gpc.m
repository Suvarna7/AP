%% Run GPC algorithm and calculate basal insulin. Kamuran Turksoy
function [phi,phi_ee,phi_gsr,...
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
    maximum_insulin,total_daily_unit,insulin_sensitivity_factor]=m20150711_gpc(gs,ee,gsr,kj,...
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
    maximum_insulin,total_daily_unit,insulin_sensitivity_factor,body_weight,meal_gpc_mu,bolus_insulin,...
    flag_constrains,g_prediction_feedback)

clc
clear all
basal_insulin= zeros(8,21)
bolus_insulin= zeros(21,1)
arma_lamda_gsr=ones(21,1)*0.5
ee=ones(20,1)*5.8512
ee(21,1)=3.0537
gsr=ones(20,1)*0.0139
gsr(21,1)=0.5948
armax_err=zeros(21,1)
arma_err_ee=zeros(21,1)
arma_err_gsr=zeros(21,1)
armax_parameters=zeros(24,21)
armax_covariance(:,:,20)=eye(24)
armax_covariance(:,:,21)=eye(24)
armax_lamda=ones(21,1)*0.5
arma_parameters_ee=zeros(4,21)
arma_parameters_gsr=zeros(4,21)
arma_covariance_ee(:,:,20)=eye(4)
arma_covariance_ee(:,:,21)=eye(4)
arma_lamda_ee=ones(21,1)*0.5
arma_covariance_gsr(:,:,20)=eye(4)
arma_covariance_gsr(:,:,21)=eye(4)
gs=ones(20,1)*298;
gs(21,1)=200;
phi=zeros(24,20);
phi(1,21)=-298
phi(2,21)=-298
phi(3,21)=-298
phi(4,21)= 0
phi(5,21)= 0
phi(6,21)= 0
phi(7,21)= 0
phi(8,21)= 0
phi(9,21)= 0
phi(10,21)= 0
phi(11,21)= 0
phi(12,21)= 0
phi(13,21)= 0
phi(14,21)= 0
phi(15,21)= 0
phi(16,21)= 5.8512
phi(17,21)= 5.8512
phi(18,21)= 5.8512
phi(19,21)= 5.8512
phi(20,21)= 0.0139
phi(21,21)= 0.0139
phi(22,21)= 0.0139
phi(23,21)= 0.0139
phi(24,21)= 0.0139


phi_ee=zeros(4,20);
phi_ee(1,21)=-5.8512;
phi_ee(2,21)=-5.8512;
phi_ee(3,21)=-5.8512;
phi_ee(1,21)=0;

phi_gsr=zeros(4,20);
phi_gsr(1,21)=-0.0139;
phi_gsr(2,21)=-0.0139;
phi_gsr(3,21)=-0.0139;
phi_gsr(1,21)=0;


bolus_insulin= zeros(21,1)
g_prediction_feedback=zeros(8,1);
meal_gpc_mu=ones(21,1)*0.5;

arma_covariance_gsr(:,:,20)=eye(4)
arma_covariance_gsr(:,:,21)=eye(4)
arma_lamda_ee=ones(21,1)*0.5
arma_lamda_gsr=ones(21,1)*0.5
arma_covariance_ee(:,:,20)=eye(4)
arma_covariance_ee(:,:,21)=eye(4)
arma_parameters_ee=zeros(4,21)
arma_parameters_gsr=zeros(4,21)
kj=21;
body_weight=74.8;
st=5;
flag_constrains=0;

N1=2;N2=10;Nu=8;na=3;nb1=12;nb2=4;nb3=4;nc=1;st=5;d2=1;d3=1; % Prediction and control horizons. Model orders and delays.
IOB=[0.974082840593171,0.932309038992748,0.847452388537183,0.724680039139838,0.584206488705884,0.448368344074227,0.333152936238220,0.244180789845194,0.177141946974804,0.122686145196158,0.0757668499037829,0.0494391409323673];
upperlim=[ones(1,na) zeros(1,nb1) zeros(1,nb2) ones(1,nb3) ones(1,nc)]' % Upper limits for parameters of ARMAX
lowerlim=[-ones(1,na) -IOB -ones(1,nb2) zeros(1,nb3) -ones(1,nc)]' % lower limits for parameters of ARMAX


ee=ones(20,1)*5.8512
ee(21,1)=3.0537
gsr=ones(20,1)*0.0139
gsr(21,1)=0.5948
kj=21
gs=ones(20,1)*298;
gs(21,1)=200;
basal_insulin= zeros(8,21)
armax_err=zeros(21,1)
arma_err_ee=zeros(21,1)
arma_err_gsr=zeros(21,1)
armax_parameters=zeros(24,21)
armax_covariance(:,:,20)=eye(24)
armax_covariance(:,:,21)=eye(24)
armax_lamda=ones(21,1)*0.5
% gs=[15.0 13.0 47.0 298.0 64.0 59.0 74.0 36.0 48.0 96.0 200.0 210.0 140.0 142.0 156.0 158.0 184.0 175.0 17.0 19.0 20.0]'
% size(gs)
% armax_err=[625.0 613.0 618.0 633.0 638.0 639.0 645.0 649.0 666.0 669.0 672.0 679.0 688.0 689.0 690.0 691.0 692.0 693.0 695.0 696.0 698.0]';
% kj=20;
% gsr=[400.0 430.0 460.0 0.0139 480.0 490.0 420.0 421.0 431.0 442.0 456.0 496.0 443.0 485.0 498.0 451.0 428.0 438.0 408.0 474.0 472.0 0.5948460102081299]'
% basal_insulin=[586.0 592.0 596.0 574.0 570.0 555.0 511.0 518.0 521.0 532.0 537.0 538.0 565.0 528.0 517.0 513.0 514.0 595.0 587.0 588.0 589.0]
% ee=[300.0 330.0 360.0 5.8512 380.0 390.0 320.0 321.0 331.0 342.0 356.0 396.0 343.0 385.0 398.0 351.0 328.0 338.0 308.0 374.0 372.0 3.05375075340271]'
%% Rest of parameters are defined in ACC, TBME, IECR papers
gs(kj-na:kj-1,1)
(basal_insulin(1,kj-nb1-N1:kj-1-N1))'
ee(kj-nb2+1-d2:kj-d2,1)
gsr(kj-nb2+1-d3:kj-d3,1)
armax_err(kj-nc:kj-1,1)


phi(:,kj)=[-flipud(gs(kj-na:kj-1,1));flipud((basal_insulin(1,kj-nb1-N1:kj-1-N1))');flipud(ee(kj-nb2+1-d2:kj-d2,1));flipud(gsr(kj-nb2+1-d3:kj-d3,1));flipud(armax_err(kj-nc:kj-1,1))]
 phi_ee(:,kj)=[-ee(kj-1,1) -ee(kj-2,1) -ee(kj-3,1) arma_err_ee(kj-1,1)]'
phi_gsr(:,kj)=[-gsr(kj-1,1) -gsr(kj-2,1) -gsr(kj-3,1) arma_err_gsr(kj-1,1)]'

[armax_parameters(:,kj),~,armax_covariance(:,:,kj),armax_lamda(kj,1),armax_err(kj,1)]=opt_recursive(gs(kj,1),phi(:,kj),armax_parameters(:,kj-1),armax_covariance(:,:,kj-1),armax_lamda(kj-1,1),upperlim,lowerlim);
[arma_parameters_ee(:,kj),~,arma_lamda_ee(kj,1),arma_covariance_ee(:,:,kj),arma_err_ee(kj,1)]=opt_recursive_arm(ee(kj,1),phi_ee(:,kj),arma_parameters_ee(:,kj-1),arma_covariance_ee(:,:,kj-1),arma_lamda_ee(kj-1,1),arma_err_ee(kj-1,1),ones(4,1),-ones(4,1),0.99,0.9,0.005);
[arma_parameters_gsr(:,kj),~,arma_lamda_gsr(kj,1),arma_covariance_gsr(:,:,kj),arma_err_gsr(kj,1)]=opt_recursive_arm(gsr(kj,1),phi_gsr(:,kj),arma_parameters_gsr(:,kj-1),arma_covariance_gsr(:,:,kj-1),arma_lamda_gsr(kj-1,1),arma_err_gsr(kj-1,1),ones(4,1),-ones(4,1),0.99,0.9,0.005);

armax_parameters
armax_covariance
armax_lamda
armax_err
arma_parameters_ee
arma_lamda_ee
arma_covariance_ee
arma_err_ee
arma_parameters_gsr
arma_lamda_gsr
arma_covariance_gsr
arma_err_gsr

A=armax_parameters(1:3,kj)'
B1=armax_parameters(4:15,kj)'
B2=armax_parameters(16:19,kj)'
B3=armax_parameters(20:23,kj)'
C=armax_parameters(24,kj)'
Aee=arma_parameters_ee(1:3,kj)'
Cee=arma_parameters_ee(4,kj)'
Agsr=arma_parameters_gsr(1:3,kj)'
Cgsr=arma_parameters_gsr(4,kj)'


A_state(:,:,kj)=[-A B1(2:end) B2(2:end) B3(2:end) C;...
    1 zeros(1,20);...
    zeros(1,1) 1 zeros(1,19);...
    zeros(1,21);...
    zeros(1,3) 1 zeros(1,17);...
    zeros(1,4) 1 zeros(1,16);...
    zeros(1,5) 1 zeros(1,15);...
    zeros(1,6) 1 zeros(1,14);...
    zeros(1,7) 1 zeros(1,13);...
    zeros(1,8) 1 zeros(1,12);...
    zeros(1,9) 1 zeros(1,11);...
    zeros(1,10) 1 zeros(1,10);...
    zeros(1,11) 1 zeros(1,9);...
    zeros(1,12) 1 zeros(1,8);...
    zeros(1,21);...
    zeros(1,14) 1 zeros(1,6);...
    zeros(1,15) 1 zeros(1,5);...
    zeros(1,21);...
    zeros(1,17) 1 zeros(1,3);...
    zeros(1,18) 1 zeros(1,2);...
    zeros(1,21)];
A_state_ee(:,:,kj)=[-Aee Cee;...
    1 zeros(1,3);...
    zeros(1,1) 1 zeros(1,2);...
    zeros(1,4)];
A_state_gsr(:,:,kj)=[-Agsr Cgsr;...
    1 zeros(1,3);...
    zeros(1,1) 1 zeros(1,2);...
    zeros(1,4)];

C_state(:,:,kj)=A_state(1,:,kj);
C_state_ee(:,:,kj)=A_state_ee(1,:,kj);
C_state_gsr(:,:,kj)=A_state_gsr(1,:,kj);
B_state(:,:,kj)=[B1(1,1) B2(1,1) B3(1,1);zeros(2,3);1 zeros(1,2);zeros(10,3);0 1 0;zeros(2,3);0 0 1;zeros(2,3);zeros(1,3)];
K_state(:,:,kj)=[1;zeros(19,1);1];
K_state_ee(:,:,kj)=[1;zeros(2,1);1];
K_state_gsr(:,:,kj)=[1;zeros(2,1);1];

A_state
A_state_ee
A_state_gsr
C_state
C_state_ee
C_state_gsr
B_state
K_state
K_state_ee
K_state_gsr

A_state_temp=A_state(:,:,kj)
B_state_temp=B_state(:,:,kj)
C_state_temp=C_state(:,:,kj)

[M(:,:,kj),L(:,:,kj),L_ee(:,:,kj),L_gsr(:,:,kj)]=controller_horizon(A_state(:,:,kj),B_state(:,:,kj),C_state(:,:,kj),N1,N2,Nu);
M
L
L_ee
L_gsr


M_ee(:,:,kj)=prediction_horizon(A_state_ee(:,:,kj),C_state_ee(:,:,kj),N1,N2);
M_gsr(:,:,kj)=prediction_horizon(A_state_gsr(:,:,kj),C_state_gsr(:,:,kj),N1,N2);

M_ee
M_gsr

X_state(:,kj)=[-phi(1:3,kj);phi(5:15,kj);phi(17:19,kj);phi(21:23,kj);phi(24,kj)];
X_state_ee(:,kj)=[-phi_ee(1:3,kj);phi_ee(4:end,kj)];
X_state_gsr(:,kj)=[-phi_gsr(1:3,kj);phi_gsr(4:end,kj)];

return1=(M_ee(:,:,kj))
return2=(A_state_ee(:,:,kj))
return3= K_state_ee(:,:,kj)
return4= C_state_ee(:,:,kj)
return5= X_state_ee(:,kj)
return6= ee(kj,1)

returnx=M_ee(:,:,kj)*(A_state_ee(:,:,kj))
returny=K_state_ee(:,:,kj)*C_state_ee(:,:,kj)*X_state_ee(:,kj)
returnz=(M_ee(:,:,kj)*K_state_ee(:,:,kj)*(ee(kj,1)))

ee_prediction(:,kj)=(M_ee(:,:,kj)*(A_state_ee(:,:,kj)-K_state_ee(:,:,kj)*C_state_ee(:,:,kj))*X_state_ee(:,kj))+(M_ee(:,:,kj)*K_state_ee(:,:,kj)*(ee(kj,1)))

ee_prediction

return7=M_gsr(:,:,kj)
return8=K_state(:,:,kj)
return75=A_state_gsr(:,:,kj)
return76=C_state_gsr(:,:,kj)
return77=X_state_gsr(:,kj)

gsr_prediction(:,kj)=(M_gsr(:,:,kj)*(A_state_gsr(:,:,kj)-K_state_gsr(:,:,kj)*C_state_gsr(:,:,kj))*X_state_gsr(:,kj))+(M_gsr(:,:,kj)*K_state_gsr(:,:,kj)*(gsr(kj,1)))

gsr_prediction


return9=M(:,:,kj)
return10=A_state(:,:,kj)
return11=K_state(:,:,kj)
return12=C_state(:,:,kj)
return13=X_state(:,kj)

return14=size(M(:,:,kj))
return15=size(A_state(:,:,kj))
return16=size(K_state(:,:,kj))
return17=size(C_state(:,:,kj))
return18=size(X_state(:,kj))

ee(kj-d2+1:kj,1)
ee_prediction(1:end-d2,kj)
ee_prediction
return20=L_ee(:,:,kj)
return21=L_gsr(:,:,kj)

res=L_ee(:,:,kj)
res2=[ee(kj-d2+1:kj,1);ee_prediction(1:end-d2,kj)]
res3=L_gsr(:,:,kj)
res4=[gsr(kj-d2+1:kj,1);gsr_prediction(1:end-d2,kj)]
% result28=L_ee(:,:,kj)*[ee(kj-d2+1:kj,1);ee_prediction(1:end-d2,kj)]+L_gsr(:,:,kj)*[gsr(kj-d2+1:kj,1);gsr_prediction(1:end-d2,kj)
result=L_ee(:,:,kj)*[ee(kj-d2+1:kj,1);ee_prediction(1:end-d2,kj)]+L_gsr(:,:,kj)*[gsr(kj-d2+1:kj,1);gsr_prediction(1:end-d2,kj)]
A_state_temp=A_state(:,:,kj)
K_state_temp=K_state(:,:,kj)
C


g_prediction(:,kj)=(M(:,:,kj)*(A_state(:,:,kj)-K_state(:,:,kj)*C_state(:,:,kj))*X_state(:,kj))+(M(:,:,kj)*K_state(:,:,kj)*(gs(kj,1)))+L_ee(:,:,kj)*[ee(kj-d2+1:kj,1);ee_prediction(1:end-d2,kj)]+L_gsr(:,:,kj)*[gsr(kj-d2+1:kj,1);gsr_prediction(1:end-d2,kj)];
g_prediction(:,kj)=g_prediction(:,kj)+g_prediction_feedback
g_prediction_feedback
g_prediction

reference_glucose(:,kj)=reference_trajectory(gs(kj,1),110,N2-N1,meal_gpc_mu(kj,1)) % reference glucose trajectory

insulin_sensitivity_constant(:,kj)=max(0.1,g_prediction(:,kj)./reference_glucose(:,kj))

L(:,:,kj)
g_prediction(:,kj)
bolus_insulin
basal_insulin
0
reference_glucose(:,kj)
Nu
st
body_weight
insulin_sensitivity_constant(:,kj)
flag_constrains

[basal_insulin(:,kj),IOB_prediction(:,kj),maximum_insulin(:,kj),total_daily_unit(:,kj),insulin_sensitivity_factor(:,kj)]=controller_ins(g_prediction(:,kj),L(:,:,kj),bolus_insulin,basal_insulin,0,reference_glucose(:,kj),Nu,st,body_weight,insulin_sensitivity_constant(:,kj),flag_constrains);
result1=basal_insulin(:,kj)
result2=IOB_prediction(:,kj)
result3=maximum_insulin(:,kj)
result4=total_daily_unit(:,kj)
result5=insulin_sensitivity_factor(:,kj)

