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

ee=[0.4 0.4 0.4 0.4 0.4 0.4 0.4 0.4 0.4 0.4 0.4 0.4 0.4 0.4 0.4 0.4 0.4 0.4 0.4 0.4 0.4 0.3]'
gsr=[0.3 0.3 0.3 0.3 0.3 0.3 0.3 0.3 0.3 0.3 0.3 0.3 0.3 0.3 0.3 0.3 0.3 0.3 0.3 0.3 0.3 0.1]'

arma_err_gsr=[0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0]

arma_err_ee=[0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0]

gs=[ 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0]';

basal_insulin=[ 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0]

armax_err=[0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0]

kj=21

armax_parameters=[0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0]

	armax_covariance(:,:,kj-1)=[	1.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0
			0.0			1.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0
			0.0			0.0			1.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0
			0.0			0.0			0.0			1.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0
			0.0			0.0			0.0			0.0			1.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0
			0.0			0.0			0.0			0.0			0.0			1.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0
			0.0			0.0			0.0			0.0			0.0			0.0			1.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0
			0.0			0.0			0.0			0.0			0.0			0.0			0.0			1.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0
			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			1.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0
			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			1.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0
			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			1.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0
			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			1.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0
			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			1.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0
			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			1.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0
			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			1.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0
			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			1.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0
			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			1.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0
			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			1.0			0.0			0.0			0.0			0.0			0.0			0.0
			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			1.0			0.0			0.0			0.0			0.0			0.0
			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			1.0			0.0			0.0			0.0			0.0
			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			1.0			0.0			0.0			0.0
			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			1.0			0.0			0.0
			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			1.0			0.0
			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			0.0			1.0]

 armax_lamda=[0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5]       

arma_lamda_ee=[0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5]

arma_lamda_gsr=[0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5]

arma_parameters_ee=[0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0]

arma_parameters_gsr=[0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0]

	arma_covariance_gsr(:,:,kj-1)=[1.0			0.0			0.0			0.0
			0.0			1.0			0.0			0.0
			0.0			0.0			1.0			0.0
			0.0			0.0			0.0			1.0]
        
        	arma_covariance_ee(:,:,kj-1)=[1.0			0.0			0.0			0.0
			0.0			1.0			0.0			0.0
			0.0			0.0			1.0			0.0
			0.0			0.0			0.0			1.0]
 
g_prediction_feedback=[ 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0
 0.0]

meal_gpc_mu=[ 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5
 0.5]
        
N1=2;N2=10;Nu=8;na=3;nb1=12;nb2=4;nb3=4;nc=1;st=5;d2=1;d3=1; % Prediction and control horizons. Model orders and delays.
IOB=[0.974082840593171,0.932309038992748,0.847452388537183,0.724680039139838,0.584206488705884,0.448368344074227,0.333152936238220,0.244180789845194,0.177141946974804,0.122686145196158,0.0757668499037829,0.0494391409323673];
upperlim=[ones(1,na) zeros(1,nb1) zeros(1,nb2) ones(1,nb3) ones(1,nc)]'; % Upper limits for parameters of ARMAX
lowerlim=[-ones(1,na) -IOB -ones(1,nb2) zeros(1,nb3) -ones(1,nc)]'; % lower limits for parameters of ARMAX

%% Rest of parameters are defined in ACC, TBME, IECR papers
phi(:,kj)=[-flipud(gs(kj-na:kj-1,1));flipud((basal_insulin(1,kj-nb1-N1:kj-1-N1))');flipud(ee(kj-nb2+1-d2:kj-d2,1));flipud(gsr(kj-nb2+1-d3:kj-d3,1));flipud(armax_err(kj-nc:kj-1,1))];
phi_ee(:,kj)=[-ee(kj-1,1) -ee(kj-2,1) -ee(kj-3,1) arma_err_ee(kj-1,1)]';
phi_gsr(:,kj)=[-gsr(kj-1,1) -gsr(kj-2,1) -gsr(kj-3,1) arma_err_gsr(kj-1,1)]';

[armax_parameters(:,kj),~,armax_covariance(:,:,kj),armax_lamda(kj,1),armax_err(kj,1)]=opt_recursive(gs(kj,1),phi(:,kj),armax_parameters(:,kj-1),armax_covariance(:,:,kj-1),armax_lamda(kj-1,1),upperlim,lowerlim);
[arma_parameters_ee(:,kj),~,arma_lamda_ee(kj,1),arma_covariance_ee(:,:,kj),arma_err_ee(kj,1)]=opt_recursive_arm(ee(kj,1),phi_ee(:,kj),arma_parameters_ee(:,kj-1),arma_covariance_ee(:,:,kj-1),arma_lamda_ee(kj-1,1),arma_err_ee(kj-1,1),ones(4,1),-ones(4,1),0.99,0.9,0.005);
[arma_parameters_gsr(:,kj),~,arma_lamda_gsr(kj,1),arma_covariance_gsr(:,:,kj),arma_err_gsr(kj,1)]=opt_recursive_arm(gsr(kj,1),phi_gsr(:,kj),arma_parameters_gsr(:,kj-1),arma_covariance_gsr(:,:,kj-1),arma_lamda_gsr(kj-1,1),arma_err_gsr(kj-1,1),ones(4,1),-ones(4,1),0.99,0.9,0.005);

arma_parameters_gsr(:,kj)=[ -0.028846342947240932
 -0.028846971137147466
 -0.028845636324184857
 -3.2988952146107895E-7]

arma_parameters_ee(:,kj)=[-0.08219176243274258
 -0.08219296321658226
 -0.08219125627471231
 -1.3851805149259717E-8]

armax_parameters(:,kj)=[0.7585645141756496
 -0.9975172925148196
 -0.2580556216524697
 -0.0024821282166065145
 -0.001559592188418081
 -0.002172845840241107
 -0.00959422945269117
 -5.630391972836145E-4
 -3.422002520221137E-5
 -1.9694904103012777E-5
 -3.0083632590337847E-4
 -4.241071260287638E-5
 -1.2935685347742294E-4
 -8.5594393686769E-5
 -1.8801744152404236E-4
 -1.645711111718703E-4
 -0.15215655162535127
 -2.224193497457353E-4
 -2.0370712821965881E-4
 0.5019395797238265
 0.5270622038526239
 0.5141071092374062
 0.5061702466262537
 -0.04574323333159212]

A=armax_parameters(1:3,kj)';B1=armax_parameters(4:15,kj)';B2=armax_parameters(16:19,kj)';B3=armax_parameters(20:23,kj)';C=armax_parameters(24,kj)';
Aee=arma_parameters_ee(1:3,kj)';Cee=arma_parameters_ee(4,kj)';
Agsr=arma_parameters_gsr(1:3,kj)';Cgsr=arma_parameters_gsr(4,kj)';


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

A_state
A_state_ee
A_state_gsr

C_state(:,:,kj)=A_state(1,:,kj);C_state_ee(:,:,kj)=A_state_ee(1,:,kj);C_state_gsr(:,:,kj)=A_state_gsr(1,:,kj)
B_state(:,:,kj)=[B1(1,1) B2(1,1) B3(1,1);zeros(2,3);1 zeros(1,2);zeros(10,3);0 1 0;zeros(2,3);0 0 1;zeros(2,3);zeros(1,3)]
K_state(:,:,kj)=[1;zeros(19,1);1];K_state_ee(:,:,kj)=[1;zeros(2,1);1];K_state_gsr(:,:,kj)=[1;zeros(2,1);1]

[M(:,:,kj),L(:,:,kj),L_ee(:,:,kj),L_gsr(:,:,kj)]=controller_horizon(A_state(:,:,kj),B_state(:,:,kj),C_state(:,:,kj),N1,N2,Nu)
M_ee(:,:,kj)=prediction_horizon(A_state_ee(:,:,kj),C_state_ee(:,:,kj),N1,N2)
M_gsr(:,:,kj)=prediction_horizon(A_state_gsr(:,:,kj),C_state_gsr(:,:,kj),N1,N2)

X_state(:,kj)=[-phi(1:3,kj);phi(5:15,kj);phi(17:19,kj);phi(21:23,kj);phi(24,kj)]
X_state_ee(:,kj)=[-phi_ee(1:3,kj);phi_ee(4:end,kj)]
X_state_gsr(:,kj)=[-phi_gsr(1:3,kj);phi_gsr(4:end,kj)]


M_ee(:,:,kj)
(A_state_ee(:,:,kj))
K_state_ee(:,:,kj)
C_state_ee(:,:,kj)
X_state_ee(:,kj)
ee(kj,1)=0.3

ee_prediction(:,kj)=(M_ee(:,:,kj)*(A_state_ee(:,:,kj)-K_state_ee(:,:,kj)*C_state_ee(:,:,kj))*X_state_ee(:,kj))+(M_ee(:,:,kj)*K_state_ee(:,:,kj)*(ee(kj,1)))
gsr_prediction(:,kj)=(M_gsr(:,:,kj)*(A_state_gsr(:,:,kj)-K_state_gsr(:,:,kj)*C_state_gsr(:,:,kj))*X_state_gsr(:,kj))+(M_gsr(:,:,kj)*K_state_gsr(:,:,kj)*(gsr(kj,1)))
g_prediction(:,kj)=(M(:,:,kj)*(A_state(:,:,kj)-K_state(:,:,kj)*C_state(:,:,kj))*X_state(:,kj))+(M(:,:,kj)*K_state(:,:,kj)*(gs(kj,1)))+L_ee(:,:,kj)*[ee(kj-d2+1:kj,1);ee_prediction(1:end-d2,kj)]+L_gsr(:,:,kj)*[gsr(kj-d2+1:kj,1);gsr_prediction(1:end-d2,kj)]
g_prediction(:,kj)=g_prediction(:,kj)+g_prediction_feedback
reference_glucose(:,kj)=reference_trajectory(gs(kj,1),110,N2-N1,meal_gpc_mu(kj,1)) % reference glucose trajectory

insulin_sensitivity_constant(:,kj)=max(0.1,g_prediction(:,kj)./reference_glucose(:,kj))
[basal_insulin(:,kj),IOB_prediction(:,kj),maximum_insulin(:,kj),total_daily_unit(:,kj),insulin_sensitivity_factor(:,kj)]=controller_ins(g_prediction(:,kj),L(:,:,kj),bolus_insulin,basal_insulin,0,reference_glucose(:,kj),Nu,st,body_weight,insulin_sensitivity_constant(:,kj),flag_constrains)

