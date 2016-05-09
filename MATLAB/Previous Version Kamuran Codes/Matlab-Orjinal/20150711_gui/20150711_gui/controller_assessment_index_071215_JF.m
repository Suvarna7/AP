function [I_track]=controller_assessment_index_071215_JF(umaxx_account, L_account,rf_account, wfcn_account,kj,gs_f,  ins_f,  B_w, g_prediction_f)
% load  controller_limit  umaxx_account L_account rf_account  wfcn_account
% load  prevdata  gs_f  ins_f  B_w g_prediction_f
%% initial condition

umaxx_account=[25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 0.0 0.14557401845222248 25.0 25.0 0.0 0.0 4.500288903030808;
 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 0.0 0.0 25.0 25.0 1.0947981602473371 0.0 6.856245647137467;
 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 0.0 0.0 25.0 25.0 0.0 0.0 13.868630538501467;
 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 0.0 0.0 25.0 25.0 0.0 0.0 11.691419555159683;
 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 0.0 0.0 25.0 25.0 0.0 0.0 18.639318733776026;
 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 0.0 0.0 25.0 25.0 0.0 0.0 17.878806386531227;
 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 0.0 0.0 25.0 25.0 0.0 0.0 25.0;
 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 25.0 0.0 0.0 25.0 25.0 0.0 0.0 25.0]

rf_account=[0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 147.5 155.0 154.0 147.5 145.0 137.5 137.0;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 128.75 132.5 132.0 128.75 127.5 123.75 123.5;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 119.375 121.25 121.0 119.375 118.75 116.875 116.75;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 114.6875 115.625 115.5 114.6875 114.375 113.4375 113.375;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 112.34375 112.8125 112.75 112.34375 112.1875 111.71875 111.6875;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 111.171875 111.40625 111.375 111.171875 111.09375 110.859375 110.84375;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 110.5859375 110.703125 110.6875 110.5859375 110.546875 110.4296875 110.421875;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 110.29296875 110.3515625 110.34375 110.29296875 110.2734375 110.21484375 110.2109375]

 wfcn_account=[1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 40.83594461905547 21.141488795610535 15.675520563333974 16.79096122958377 19.749292300316245 21.003750171991793 19.33407661668053;
 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 211.76470588235293 25.30574506114743 12.677128942129503 13.881277428378635 19.707252843813663 20.293745039336287 19.04266113803759;
 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 211.76470588235293 25.91694676111685 11.763122062489733 13.511079575523228 19.517614943632534 22.045400882183248 18.093163221751684;
 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 211.76470588235293 41.929449822497325 10.621322476024769 12.420924868579018 22.04399561140731 23.14058517463662 19.34295823063906;
 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 211.76470588235293 44.788275683548164 10.47498397573893 12.96393826742547 23.36882312225163 26.94148395044736 19.049975034440124;
 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 211.76470588235293 144.0519719821192 9.80834671180423 12.545579958636166 28.62193017321061 29.129396390252953 20.996042561183753;
 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 211.76470588235293 209.1124567830399 9.82878632500036 13.778868395105475 31.834027798325078 35.77661221218754 20.78027439610872;
 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 211.76470588235293 211.76470588235293 9.305840550247696 13.587546668800552 41.72661159382924 38.715396223517374 23.010961460075386]

gs_f=[ 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 185.0 200.0 198.0 185.0 180.0 165.0 164.0 158.0 158];

  g_prediction_f=[0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 76.48970633355101 155.25647095666997 208.0426265534257 186.0244550062686 155.47839328120878 138.63070556633977 150.05508295571957;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 5.418337883473977 110.87926264021053 220.49898919601156 196.41352190406818 137.00539701795938 129.13280571006052 137.33868909860638;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 10.418205301402578 99.07212768888986 217.82932520502416 187.1013461463305 128.8428883147602 112.26831452179474 136.6456993105659;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -30.84371461272788 58.3964116421805 230.28039667020772 195.53104911953963 109.87385709131816 103.80921071027598 124.12177725422586;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -3.7485118451776187 53.33919539824619 227.93801540446736 183.51245343592007 101.66259899735766 87.81286241993095 124.15486395901415;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -30.32459186094777 16.377361198244678 240.4614642064236 187.65397446260425 82.19478963070466 80.5924799356016 111.79627803309076;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 2.5548810233301014 11.210721286778789 238.48016537639077 169.9573423440953 73.53743176607998 65.36423894868211 112.52717570818838;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -22.728714707334703 -24.413796139372085 251.0994212562982 171.8938573500191 55.964338264832776 60.28509649195005 101.42464844504339]

ins_f=[ 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -0.125 -0.125 8.85 0.0 -4.75 -6.300000000000001;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -0.07500000000000001 -0.125 12.5 -1.0250000000000001 -9.275 -8.275;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -0.125 12.5 -1.475 -12.600000000000001 -10.925;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -0.15000000000000002 -0.125 12.5 -1.5250000000000001 -15.025 -13.075000000000001;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -0.2 -0.125 12.475000000000001 -1.375 -16.475 -14.9;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -0.225 -0.125 12.5 -0.925 -17.325 -15.925;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -0.025 -0.25 -0.125 12.475000000000001 -0.55 -17.675 -16.35;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -0.025 -0.275 -0.125 12.5 -0.15000000000000002 -17.8 -16.475];

L_account(:,:,24)=zeros(8,8);

L_account(:,:,end)=[			4.7491611869108824E-18			4.735368396014492E-18			0.0			0.0			0.0			0.0			0.0			0.0;
			-7.28890267136675E-18			4.7491611869108824E-18			4.735368396014492E-18			0.0			0.0			0.0			0.0			0.0;
			1.2231999103075032E-18			-7.28890267136675E-18			4.7491611869108824E-18			4.735368396014492E-18			0.0			0.0			0.0			0.0;
			-3.3148647523826275E-18			1.2231999103075032E-18			-7.28890267136675E-18			4.7491611869108824E-18			4.735368396014492E-18			0.0			0.0			0.0;
			7.926863673713925E-18			-3.3148647523826275E-18			1.2231999103075032E-18			-7.28890267136675E-18			4.7491611869108824E-18			4.735368396014492E-18			0.0			0.0;
			3.629533485551548E-18			7.926863673713925E-18			-3.3148647523826275E-18			1.2231999103075032E-18			-7.28890267136675E-18			4.7491611869108824E-18			4.735368396014492E-18			0.0;
			9.997080882929908E-18			3.629533485551548E-18			7.926863673713925E-18			-3.3148647523826275E-18			1.2231999103075032E-18			-7.28890267136675E-18			4.7491611869108824E-18			4.735368396014492E-18;
			-2.4029343979240114E-17			9.997080882929908E-18			3.629533485551548E-18			7.926863673713925E-18			-3.3148647523826275E-18			1.2231999103075032E-18			-7.28890267136675E-18			4.7491611869108824E-18]
kj=28;
B_w=85



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

I_track=[7.112988076734787;
 77.58635887751852;
 2.736376002905232;
 0.05481528623758062;
 91.32529376324352;
 119.71701911787882;
 405.6517547458458;
 415.949707841366;
 67.67241274999775;
 8.521606718791219;
 0.0;
 0.0;
 16.552303489604302;
 0.0;
 ]

 data_mem=[0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 7.112988076734787;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 77.58635887751852;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 2.736376002905232;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.05481528623758062;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 91.32529376324352;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 119.71701911787882;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 405.6517547458458;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 415.949707841366;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 67.67241274999775;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 8.521606718791219;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0;
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0]

I_meinst=-8.521606718791219

 I_error_rspeed=0
 
 I_uconstrain=[ 0.0;
 0.0;
 0.0;
 0.0;
 0.0;
 0.0;
 0.0;
 0.0;
 0.0;
 0.0;
 0.0;
 0.0;
 0.0;
 0.0;
 0.0;
 0.0]



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
B_w
total_unit=B_w*wfcn
IC=1800./total_unit
y_real=gs_f
delta_u=diff([ins_f(1,kj-1);ins_f(:,kj)])
delta_u
%% recusive opretor
I_yprediction_inst=((y_predict-ref)).*wt_y.*((y_predict-ref))
delta_u
diag(IC)
I_u=(delta_u'*diag(IC)*delta_u)
if sum(I_u)<0.0001
    I_cost_function=10^7
else
I_cost_function=sum(I_yprediction_inst)/sum(I_u)% the ratio of control prower and insulin amount
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
I_u_constrain
I_u_constrain=zeros(7,1)
I_u_contraint_data=sum(I_u_constrain(end-2:end))
end 
  I_track=[ I_yprediction_inst;I_u;I_me;I_u_contraint_data;I_error_rspeed;I_cost_function;umaxx(1)]
  
  data_mem=[data_mem,I_track]
  save track I_track data_mem  I_u_constrain  I_error_rspeed  I_me_inst
  error_summation=error_display_JF(kj,gs_f)
end

save track I_track data_mem I_u_constrain   I_error_rspeed  I_me_inst

end
