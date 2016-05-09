function [I_track]=controller_assessment_index_071215_JF(umaxx_account, L_account,rf_account, wfcn_account,kj,gs_f,  ins_f,  B_w, g_prediction_f)
% load  controller_limit  umaxx_account L_account rf_account  wfcn_account
% load  prevdata  gs_f  ins_f  B_w g_prediction_f
%% initial condition
% B_w=85.0 
% 
% umaxx_account=[25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 14.437094625139615
%  25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 0.0 17.26365258283248 8.173162573438065
%  25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 4.477494617507036
%  25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 0.0 22.268952898957828 0.0
%  25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 0.0
%  25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 0.0 16.547988856419103 0.0
%  25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 0.0
%  25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 0.0 8.9326058325952 0.0]
% 
% rf_account=[0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 147.0 155.0 154.0
%  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 128.5 132.5 132.0
%  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 119.25 121.25 121.0
%  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 114.625 115.625 115.5
%  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 112.3125 112.8125 112.75
%  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 111.15625 111.40625 111.375
%  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 110.578125 110.703125 110.6875
%  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 110.2890625 110.3515625 110.34375]
% 
% wfcn_account=[1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 11.137507634621043 17.150719434544243 18.428313862740293
%  1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 211.76470588235293 17.7349265386016 19.203414142317044
%  1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 5.653067561049314 14.038840008110956 19.839757587470388
%  1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 211.76470588235293 16.6364356158982 26.948984710494706
%  1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 3.1737411538598517 13.647023735646995 33.0922758680235
%  1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 211.76470588235293 17.28018741330717 69.24009896605983
%  1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.8205999536322006 13.964118562993322 123.47928981970684
%  1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 211.76470588235293 18.528785160908765 211.76470588235293]
% 
% gs_f=[184.0 184.0 184.0 184.0 184.0 184.0 184.0 184.0 184.0 184.0 184.0 184.0 184.0 184.0 184.0 184.0 184.0 184.0 184.0 184.0 184.0 200.0 198.0 185.0]
% 
% g_prediction_f=[ 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 279.5006996712606 191.38281363085537 176.9655376437841
%  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -279.5801063445013 158.21223430691583 145.56235140954914
%  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 446.7121771278314 182.89595560174976 129.15243192259067
%  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -525.831602703585 147.17872675952466 90.75972171926314
%  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 749.3939290066637 175.05432939162648 72.15118924868725
%  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -946.6192787692054 136.5257864422128 34.0630566244686
%  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 1286.1993141837513 167.88037569381075 18.98270221393195
%  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -1673.4231987535875 126.12033640377344 -13.988102621247549]
% 
%  ins_f=[  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.225 0.30000000000000004 0.275
%  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.35000000000000003 0.25
%  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.125 0.4 0.225
%  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.42500000000000004 0.0
%  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.1 0.45 0.0
%  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.45 0.0
%  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.07500000000000001 0.47500000000000003 0.0
%  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.47500000000000003 0.0]
% 
% kj=24;
% 
% L_account=[		-0.0013333810204522465			-5.754387958679353E-4			0.0			0.0			0.0			0.0			0.0			0.0
% 			-0.0016727386289777215			-0.0013333810204522465			-5.754387958679353E-4			0.0			0.0			0.0			0.0			0.0
% 			-0.0025207830242333076			-0.0016727386289777215			-0.0013333810204522465			-5.754387958679353E-4			0.0			0.0			0.0			0.0
% 			-0.002437929724424327			-0.0025207830242333076			-0.0016727386289777215			-0.0013333810204522465			-5.754387958679353E-4			0.0			0.0			0.0
% 			-0.004656010798260729			-0.002437929724424327			-0.0025207830242333076			-0.0016727386289777215			-0.0013333810204522465			-5.754387958679353E-4			0.0			0.0
% 			-0.003929678979209562			-0.004656010798260729			-0.002437929724424327			-0.0025207830242333076			-0.0016727386289777215			-0.0013333810204522465			-5.754387958679353E-4			0.0
% 			-0.0058979683791930086			-0.003929678979209562			-0.004656010798260729			-0.002437929724424327			-0.0025207830242333076			-0.0016727386289777215			-0.0013333810204522465			-5.754387958679353E-4
% 			-0.004870564976562284			-0.0058979683791930086			-0.003929678979209562			-0.004656010798260729			-0.002437929724424327			-0.0025207830242333076			-0.0016727386289777215			-0.0013333810204522465]
% 
% load track I_track data_mem  I_u_constrain  I_error_rspeed  I_me_inst
% 
%  I_track= [  1323.6860247260877
%  661.0914288774304
%  3800.111174912974
%  995.5580484986324
%  3873.8245541933766
%  630.8787681709405
%  3268.908913212057
%  248.55001782977325
%  0.016414729278661288
%  81.50069967126058
%  0.0
%  20.0
%  901788.185423458
%  25.0]
%  
%  data_mem=[17549.652703584467 1323.6860247260877 
%  166509.9155083197 661.0914288774304
%  107195.91213329732 3800.111174912974
%  410111.4504659345 995.5580484986324
%  405746.2955423023 3873.8245541933766
%  1118646.382458702 630.8787681709408
%  1381675.7105903965 3268.908913212057
%  3180870.3070639195 248.55001782977328
%  0.29456477734033615 0.016414729278661288
%  200.0 81.50069967126058
%  0.0 0.0
%  10.0 20.0
%  2.3045204819662943E7 901788.185423458
%  25.0 25.0]
% 
% I_u_constrain=[ 0.0
%  0.0
%  0.0
%  0.0
%  0.0
%  0.0
%  0.0
%  0.0
%  0.0
%  0.0
%  0.0
%  0.0
%  0.0
%  0.0
%  0.0]
% 
% I_error_rspeed=20.0   
% 
% I_me_inst=81.50069967126058 ;

save track I_track data_mem  I_u_constrain  I_error_rspeed  I_me_inst

        
st=10;
start_time=22;
if  kj<=21;
I_track=zeros(21,1);
data_mem=[];
I_u_constrain=[];
I_me_inst=0;
I_error_rspeed=0;
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
I_cost_function=sum(I_yprediction_inst)/sum(I_u)% the ratio of control prower and insulin amount
end
ins_f(1,kj)
umaxx(1)
ins_f
umaxx
if abs(ins_f(1,kj)-umaxx(1))<10^-4 
I_u_constrain(end+1)=1
else
I_u_constrain(end+1)=0
end
I_me_inst=(y_prediction(1,kj-1)-y_real(kj))
I_me=(I_me_inst'*I_me_inst)^0.5;             
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
  data_mem
  I_track
  data_mem=[data_mem,I_track];
  save track I_track data_mem  I_u_constrain  I_error_rspeed  I_me_inst
  error_summation=error_display_JF(kj,gs_f);
end
I_track
data_mem
I_u_constrain
I_error_rspeed
I_me_inst


save track I_track data_mem I_u_constrain   I_error_rspeed  I_me_inst

end
