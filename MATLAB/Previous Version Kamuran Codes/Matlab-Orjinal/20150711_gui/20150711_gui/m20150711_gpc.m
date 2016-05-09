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

N1=2;N2=10;Nu=8;na=3;nb1=12;nb2=4;nb3=4;nc=1;st=5;d2=1;d3=1; % Prediction and control horizons. Model orders and delays.
IOB=[0.974082840593171,0.932309038992748,0.847452388537183,0.724680039139838,0.584206488705884,0.448368344074227,0.333152936238220,0.244180789845194,0.177141946974804,0.122686145196158,0.0757668499037829,0.0494391409323673];
upperlim=[ones(1,na) zeros(1,nb1) zeros(1,nb2) ones(1,nb3) ones(1,nc)]'; % Upper limits for parameters of ARMAX
lowerlim=[-ones(1,na) -IOB -ones(1,nb2) zeros(1,nb3) -ones(1,nc)]'; % lower limits for parameters of ARMAX

%% Rest of parameters are defined in ACC, TBME, IECR papers
phi(:,kj)=[-flipud(gs(kj-na:kj-1,1));flipud((basal_insulin(1,kj-nb1-N1:kj-1-N1))');flipud(ee(kj-nb2+1-d2:kj-d2,1));flipud(gsr(kj-nb2+1-d3:kj-d3,1));flipud(armax_err(kj-nc:kj-1,1))];
phi_ee(:,kj)=[-ee(kj-1,1) -ee(kj-2,1) -ee(kj-3,1) arma_err_ee(kj-1,1)]';
phi_gsr(:,kj)=[-gsr(kj-1,1) -gsr(kj-2,1) -gsr(kj-3,1) arma_err_gsr(kj-1,1)]';


input1=gs(kj+1,1)
input2=phi(:,kj)
input3=armax_parameters(:,kj+1)
input4=armax_covariance(:,:,kj+1)
input5=armax_lamda(kj+1,1)
input6=upperlim
input7=lowerlim


armax_covariance(:,:,kj+1)=[475.05652507272856 -764.0718764294635 209.62634699602805 859.8218552272253 2.070454976884664 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -1504.867429502526 -2666.93733837251 588.2967535456103 -2881.1741476802213 -143.8107915227896 -268.2344256478999 80.30575472597916 -291.1729099627056 -157.55859831907006;
 -764.0718772664109 2788109.051291401 -2796188.708275833 -2238297.4332662025 -5473.239177587112 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -4828563.004576273 -1.1947532066656152E7 -628870.0477238969 1.1768111357712975E7 -262434.1244520652 -1024667.0978450656 187229.92408138662 1514583.3533998653 830781.5604969219;
 209.62634760921392 -2796188.7082764823 2806055.0686981417 2243905.304724623 5491.915603301699 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 4832381.971614768 1.1976858133895746E7 608103.0365450045 -1.1827804237371689E7 444183.83969683404 1209147.8760653026 -8112.6466400406935 -1339633.8957951495 -833432.53884873;
 859.8218559016349 -2238297.4332658336 2243905.304723849 1810431.6013807077 -271977.2579315532 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 2861097.379587926 1.011346561404816E7 872053.3966773003 -9307186.585321452 10968.44801518164 787484.5636616745 -201999.87592081286 -1291898.1871368256 -638409.7294619426;
 2.0704549785357327 -5473.239177586253 5491.915603299831 -271977.2579315531 1.1829755541581285E8 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 7339.560421340096 24595.582847968846 2005.0744751858238 -20346.948026226433 92.10331011499657 1939.7176442252342 -479.0638137764294 -2872.310423506911 -1570.7319944315122;
 0.0 0.0 0.0 0.0 0.0 1.1829819046158633E8 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0;
 0.0 0.0 0.0 0.0 0.0 0.0 1.1829819046158633E8 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 1.1829819046158633E8 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 1.1829819046158633E8 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 1.1829819046158633E8 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 1.1829819046158633E8 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 1.1829819046158633E8 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 1.1829819046158633E8 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 1.1829819046158633E8 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 1.1829819046158633E8 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0;
 -1504.8674322640145 -4828563.004574911 4832381.971615924 2861097.379585297 7339.560421333937 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 9.336270164356163E7 -2.202803395488223E7 -2.8142978741000265E7 -3.081486788123169E7 -2622562.9154827613 -2311259.6992426678 -2965991.0944505148 -3252072.1247479185 -3770354.743372843;
 -2666.9373411322977 -1.194753206665667E7 1.1976858133898392E7 1.0113465614047302E7 24595.582847966918 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -2.2028033954886507E7 7.40329407740955E7 1.7410147856521558E7 -4.520612897512948E7 -2059672.956681343 -4440627.125752098 2163000.62501008 -4541367.726078704 -2374163.3466895716;
 588.2967509347302 -628870.0477193824 608103.0365434177 872053.39667197 2005.0744751731418 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -2.8142978740994673E7 1.7410147856533363E7 1.1734925352441067E7 1131746.1614268685 -3368052.5561465328 1509352.4207530536 -1.1764567210371256E7 -233587.38105626442 627692.3462363259;
 -2881.174151222629 1.1768111357713254E7 -1.182780423736903E7 -9307186.58532352 -20346.948026231286 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -3.0814867881234836E7 -4.520612897512769E7 1131746.1614159914 5.253940710474966E7 -3132168.4147945857 -4673050.758686682 288377.7905929009 -6873637.771198835 3799880.345999152;
 -143.8107918098285 -262434.12445190025 444183.8396969281 10968.448014916865 92.10331011437542 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -2622562.9154828354 -2059672.9566808485 -3368052.5561473905 -3132168.4147942523 1.180189934240972E8 -218928.0177407189 -359017.13229248335 -333760.85306554165 -341361.13595101953;
 -268.2344259347439 -1024667.0978451116 1209147.8760655678 787484.5636615377 1939.7176442249233 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -2311259.699243112 -4440627.1257520085 1509352.420751677 -4673050.7586868005 -218928.01774077254 1.1785126959111643E8 190147.58169077392 -471806.6306514468 -191869.9511405233;
 80.30575445480615 187229.92408190205 -8112.646640250613 -201999.87592141438 -479.0638137778546 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -2965991.094449935 2163000.6250113253 -1.1764567210371234E7 288377.79059401917 -359017.13229239086 190147.5816909187 1.1699710365937687E8 -10569.590220909931 129540.8276622096;
 -291.1729103336046 1514583.3533999075 -1339633.895794882 -1291898.1871370708 -2872.310423507491 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -3252072.124748212 -4541367.726078486 -233587.3810573492 -6873637.771198791 -333760.85306557146 -471806.63065142976 -10569.59022102071 1.1757666605357906E8 469189.2051358919;
 -157.55859880435202 830781.5604973064 -833432.5388486141 -638409.7294624936 -1570.731994432835 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -3770354.7433720296 -2374163.346688325 627692.3462363225 3799880.3460005242 -341361.1359509432 -191869.95114039816 129540.82766219665 469189.2051360325 312531.1559173703]

[armax_parameters(:,kj),~,armax_covariance(:,:,kj),armax_lamda(kj,1),armax_err(kj,1)]=opt_recursive(gs(kj+1,1),phi(:,kj),armax_parameters(:,kj+1),armax_covariance(:,:,kj+1),armax_lamda(kj+1,1),upperlim,lowerlim);
%[armax_parameters(:,kj),~,armax_covariance(:,:,kj),armax_lamda(kj,1),armax_err(kj,1)]=opt_recursive(gs(kj,1),phi(:,kj),armax_parameters(:,kj-1),armax_covariance(:,:,kj-1),armax_lamda(kj-1,1),upperlim,lowerlim);

output1=armax_parameters(:,kj)
output2=armax_covariance(:,:,kj)
output3=armax_lamda(kj,1)
output4=armax_err(kj,1)

inputx=ee(kj,1)
inputy=phi_ee(:,kj)
inputz=arma_parameters_ee(:,kj-1)
inputz=arma_covariance_ee(:,:,kj-1)
inputv=arma_lamda_ee(kj-1,1)
inputw=arma_err_ee(kj-1,1)

phi_ee(:,kj)=[ -3.05375075340271
 -3.05375075340271
 -3.05375075340271
 0.3567524912014055]

arma_parameters_ee(:,kj-1)=[-0.0020206083457782378
 -1.0005816343703346
 -0.004218483850207878
 0.8466096314879248]

arma_covariance_ee(:,:,kj-1)=[ 4.2183591244921486E10 -4.21835523556416E10 -38967.27097757706 -1195.1771734692954;
 -4.2183552355601524E10 4.239450790139777E10 -2.109555241273943E8 332.1147826374766;
 -38967.310802599764 -2.1095552408733535E8 2.1099455397205526E8 957.2458059000695;
 -1195.173586847155 332.11467101839906 957.2423108069459 1443.532609813942]

arma_lamda_ee(kj-1,1)=0.005

arma_err_ee(kj-1,1)=-0.18812185239097534;

ee(kj,1)=3.05375075340271;

[arma_parameters_ee(:,kj),~,arma_lamda_ee(kj,1),arma_covariance_ee(:,:,kj),arma_err_ee(kj,1)]=opt_recursive_arm(ee(kj,1),phi_ee(:,kj),arma_parameters_ee(:,kj-1),arma_covariance_ee(:,:,kj-1),arma_lamda_ee(kj-1,1),arma_err_ee(kj-1,1),ones(4,1),-ones(4,1),0.99,0.9,0.005);

outputarmaparam=arma_parameters_ee(:,kj)
outputarmalamda=arma_lamda_ee(kj,1)
outputcov=arma_covariance_ee(:,:,kj)
outputarmaerr=arma_err_ee(kj,1)




[arma_parameters_gsr(:,kj),~,arma_lamda_gsr(kj,1),arma_covariance_gsr(:,:,kj),arma_err_gsr(kj,1)]=opt_recursive_arm(gsr(kj,1),phi_gsr(:,kj),arma_parameters_gsr(:,kj-1),arma_covariance_gsr(:,:,kj-1),arma_lamda_gsr(kj-1,1),arma_err_gsr(kj-1,1),ones(4,1),-ones(4,1),0.99,0.9,0.005);




arma_parameters_gsr(:,kj)=[-0.05739 ;-0.85455; -0.01955 ; 0.30416]

arma_parameters_ee(:,kj)=[0.007833 ; -0.916450; -0.005628 ; 1.2223747]

armax_parameters(:,kj)=[ -1.0;
 -0.5616603711670207;
0.5658447674553505;
-0.6287799752166605;
 -0.1505284542918245;
-0.021288153271919585;
-0.022286036903237435;
 -0.07684029543053775;
 -0.07150887175632491;
 -0.02086548148034086;
-0.04471346706016107;
-0.025565783273489507;
 -0.04351284550545332;
 -0.02588031980270639;
-0.004181420883474344;
-0.35186694483856124;
-0.47868615982217216;
-0.3450396480328536;
-0.34734059587127564;
-1.5492743967042273E-27;
 0.04393725497644835;
0.13126678444217135;
0.0022702143018522576;
 -0.2293527179864965;
]

A=armax_parameters(1:3,kj)';B1=armax_parameters(4:15,kj)';B2=armax_parameters(16:19,kj)';B3=armax_parameters(20:23,kj)';C=armax_parameters(24,kj)';
Aee=arma_parameters_ee(1:3,kj)';Cee=arma_parameters_ee(4,kj)';
Agsr=arma_parameters_gsr(1:3,kj)';Cgsr=arma_parameters_gsr(4,kj)';

A
B1
B2
B3
C
Aee
Cee
Agsr
Cgsr


S_state(:,:,kj)=[-A B1(2:end) B2(2:end) B3(2:end) C;...
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
S_state_ee(:,:,kj)=[-Aee Cee;...
    1 zeros(1,3);...
    zeros(1,1) 1 zeros(1,2);...
    zeros(1,4)];
S_state_gsr(:,:,kj)=[-Agsr Cgsr;...
    1 zeros(1,3);...
    zeros(1,1) 1 zeros(1,2);...
    zeros(1,4)];

F_state(:,:,kj)=S_state(1,:,kj);F_state_ee(:,:,kj)=S_state_ee(1,:,kj);F_state_gsr(:,:,kj)=S_state_gsr(1,:,kj);
B_state(:,:,kj)=[B1(1,1) B2(1,1) B3(1,1);zeros(2,3);1 zeros(1,2);zeros(10,3);0 1 0;zeros(2,3);0 0 1;zeros(2,3);zeros(1,3)];
K_state(:,:,kj)=[1;zeros(19,1);1];K_state_ee(:,:,kj)=[1;zeros(2,1);1];K_state_gsr(:,:,kj)=[1;zeros(2,1);1];


S_state(:,:,kj)
B_state(:,:,kj)
F_state(:,:,kj)

N1
N2
Nu

[M(:,:,kj),L(:,:,kj),L_ee(:,:,kj),L_gsr(:,:,kj)]=controller_horizon(S_state(:,:,kj),B_state(:,:,kj),F_state(:,:,kj),N1,N2,Nu);

M_result=M(:,:,kj)
L_result=L(:,:,kj)
L_ee_result=L_ee(:,:,kj)
L_gsr_result=L_gsr(:,:,kj)



M_ee(:,:,kj)=prediction_horizon(S_state_ee(:,:,kj),F_state_ee(:,:,kj),N1,N2);
M_gsr(:,:,kj)=prediction_horizon(S_state_gsr(:,:,kj),F_state_gsr(:,:,kj),N1,N2);

M_eeresult=M_ee(:,:,kj)
M_gsrresult=M_gsr(:,:,kj)



X_state(:,kj)=[-phi(1:3,kj);phi(5:15,kj);phi(17:19,kj);phi(21:23,kj);phi(24,kj)];
X_state_ee(:,kj)=[-phi_ee(1:3,kj);phi_ee(4:end,kj)];
X_state_gsr(:,kj)=[-phi_gsr(1:3,kj);phi_gsr(4:end,kj)];

ee_prediction(:,kj)=(M_ee(:,:,kj)*(S_state_ee(:,:,kj)-K_state_ee(:,:,kj)*F_state_ee(:,:,kj))*X_state_ee(:,kj))+(M_ee(:,:,kj)*K_state_ee(:,:,kj)*(ee(kj,1)));
ee_result=ee_prediction(:,kj)
gsr_prediction(:,kj)=(M_gsr(:,:,kj)*(S_state_gsr(:,:,kj)-K_state_gsr(:,:,kj)*F_state_gsr(:,:,kj))*X_state_gsr(:,kj))+(M_gsr(:,:,kj)*K_state_gsr(:,:,kj)*(gsr(kj,1)));
gsr_result=gsr_prediction(:,kj)

kj

ee_x=[ee(kj-d2+1:kj,1);ee_prediction(1:end-d2,kj)]
gsr_x=[gsr(kj-d2+1:kj,1);gsr_prediction(1:end-d2,kj)]

M_temp=M(:,:,kj)
A_statee=S_state(:,:,kj)
K_statee=K_state(:,:,kj)
C_statee=F_state(:,:,kj)
X_statee=X_state(:,kj)
L_ee(:,:,kj)
L_gsr(:,:,kj)
K_statee=K_state(:,:,kj)

g_prediction(:,kj)=(M(:,:,kj)*(S_state(:,:,kj)-K_state(:,:,kj)*F_state(:,:,kj))*X_state(:,kj))+(M(:,:,kj)*K_state(:,:,kj)*(gs(kj,1)))+L_ee(:,:,kj)*[ee(kj-d2+1:kj,1);ee_prediction(1:end-d2,kj)]+L_gsr(:,:,kj)*[gsr(kj-d2+1:kj,1);gsr_prediction(1:end-d2,kj)];
g_prediction_result=g_prediction(:,kj)
g_prediction(:,kj)=g_prediction(:,kj)+g_prediction_feedback
g_prediction_resultwithfeedback=g_prediction(:,kj)

gsx=gs(kj,1)
N1N2=N2-N1
mealgpc=meal_gpc_mu(kj,1)

reference_glucose(:,kj)=reference_trajectory(gs(kj,1),110,N2-N1,meal_gpc_mu(kj,1)); % reference glucose trajectory
ref_result=reference_glucose(:,kj)
insulin_sensitivity_constant(:,kj)=max(0.1,g_prediction(:,kj)./reference_glucose(:,kj))

insulinresult=insulin_sensitivity_constant(:,kj)

clc
clear all
kj=5
g_prediction(:,kj)=[121.72356329280966;
 123.00089901608926;
 122.54655453851078;
 123.92326405684967;
 123.51405552211841;
 124.90341374086417;
 124.5089592947845;
 125.99257005542701]

insulin_sensitivity_constant(:,kj)=[1.0493410628690487;
 1.0885035311158342;
 1.0990722380135496;
 1.1189459508519157;
 1.1190401406307444;
 1.1335533861904858;
 1.1309357642444235;
 1.144899117357281]

bolus_insulin=[0.0
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


L(:,:,kj)=[ -0.022436930639728722 -0.0071874191963260115 0.0 0.0 0.0 0.0 0.0 0.0;
 -0.014434060907479356 -0.022436930639728722 -0.0071874191963260115 0.0 0.0 0.0 0.0 0.0;
 -0.0324920397346597 -0.014434060907479356 -0.022436930639728722 -0.0071874191963260115 0.0 0.0 0.0 0.0;
 -0.021734429169356684 -0.0324920397346597 -0.014434060907479356 -0.022436930639728722 -0.0071874191963260115 0.0 0.0 0.0;
 -0.03662150423765224 -0.021734429169356684 -0.0324920397346597 -0.014434060907479356 -0.022436930639728722 -0.0071874191963260115 0.0 0.0;
 -0.03234913017871076 -0.03662150423765224 -0.021734429169356684 -0.0324920397346597 -0.014434060907479356 -0.022436930639728722 -0.0071874191963260115 0.0;
 -0.047121094598110175 -0.03234913017871076 -0.03662150423765224 -0.021734429169356684 -0.0324920397346597 -0.014434060907479356 -0.022436930639728722 -0.0071874191963260115;
 -0.03665722773846532 -0.047121094598110175 -0.03234913017871076 -0.03662150423765224 -0.021734429169356684 -0.0324920397346597 -0.014434060907479356 -0.022436930639728722]

reference_glucose(:,kj)=[116.0;
 113.0;
 111.5;
 110.75;
 110.375;
 110.1875;
 110.09375;
 110.046875]

st=5


body_weight=85

flag_constrains=0

Nu=8

basal_insulin=[0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 4.4;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 3.0500000000000003;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 9.0;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 6.425000000000001;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -0.025]

[basal_insulin(:,kj),IOB_prediction(:,kj),maximum_insulin(:,kj),total_daily_unit(:,kj),insulin_sensitivity_factor(:,kj),umax(:,kj)]=controller_ins(g_prediction(:,kj),L(:,:,kj),bolus_insulin,basal_insulin,0,reference_glucose(:,kj),Nu,st,body_weight,insulin_sensitivity_constant(:,kj),flag_constrains);

basal_ins=basal_insulin(:,kj)
IOB_pred=IOB_prediction(:,kj)
max_ins=maximum_insulin(:,kj)
totaldaily=total_daily_unit(:,kj)
ins_sensitivity=insulin_sensitivity_factor(:,kj)
umax=umax(:,kj)
      y=2*a;
