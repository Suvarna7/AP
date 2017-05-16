
ins_every5=zeros(20,1);%
CGM=zeros(20,1);
[a,b]=size(CGM);
[c,d]=size(ins_every5);
sum_score=[];
sum_type=[];
if b>1
    CGM=CGM';
end
if c>1
    ins_every5=ins_every5';
end
ins=ins_every5;
start_noise=25;
    noise_interval=[24];
    length_add=3;
    am=[30];
    type_recording=zeros(6,3);
    score_retuning=[];
    sigma_a=0.6;
    phi_max=25;% angle maximum
    ii=0;
    delta_x=15;
    st=1;
    gb=CGM(1:st:end);
    gb_real=CGM(1:st:end)';
    type=0;
    g_kal=zeros(1,50);
    g_pls=zeros(1,100);
    error_detection=zeros(100,4);
    error_detectino_real=[];
    recording_w=ones(8,1);
    gb_angle_ret=CGM';
    %%%%%%%%%%%%%%%%initial for kalman filter%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % sim use nn=1 cc=1
    nn=2;
    cc=1;
    lamda=0.3;
    a_w0=1;
    b_w0=a_w0;
    w=1.5;
    alpha=10^nn;
    d2=4;
    [sum_wyxT,sum_wxxT,sum_xxp,sum_xpxp,sum_wyy,sum_wyx,sum_xx,sum_xxpp]=deal(zeros(1,d2),0,0,0,[0],0*ones(d2,1),zeros(d2,1),zeros(d2,d2));
    R=eye(1)*10^cc;
    Q=eye(d2)*alpha;
    for kj=1:21
        x_e(:,kj)=100*ones(d2,1);
    end
    A=eye(d2);
    C=zeros(1,d2);
    C(1,1)=1;
    % %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % %%%%%%%%%%%%%%%%%%%initial for pls%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    R_pls=12;                          %number of latent variables
    N_pls=1600;                        %number of samples
    M_pls=12;                        %number of input variables/2
    L_pls=1;                         %number of output variables/2
    phi_pls=0.5;
    I_pls=30;
    J_pls=30;
    sigma1=0.01;
    sigma2=0.01;
    alpha_pls=0.1;
    gb_kal(1)=gb(1);
    load plsdata730_R_12_withcluster_16_data gb_m gb_sd ins_m ins_sd gbchange_m gbchange_sd
    mean_gb=gb_m;
    std_gb=gb_sd;
    mean_ins=ins_m;
    std_ins=ins_sd;
    mean_CGM_change=gbchange_m;
    std_CGM_change=gbchange_sd;
    inss=zmu_other_JF(mean_ins,std_ins,ins')';
    prediction_step_pls=0;
    noisel=zeros(start_noise*2-1,1);
    y=[gb(end)]';
      gb_with_NaN(kj)=y;
    gb_kal(kj)=gb(end);
    gb_kal_ret(kj)=gb(end);
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%kalman filter%%%%%%%%%%%%%%%%%%%
    RR(kj)=R;
    AA(:,:,kj)=A;
    CC(:,:,kj)=C;
    QQ(:,:,kj)=Q;
        error_detectino_real(kj)=0;
        gb_angle_ret(kj)=gb_with_NaN(kj);
        stepp=kj-M_pls;
        gb_plss=sgolayfilt(gb_angle_ret,1,7);
        CGM_change_pls=[0,gb_plss(2:end)-gb_plss(1:end-1)];
        gb_plss=zmu_other_JF(mean_gb,std_gb,gb_plss')';%zmu_other centralized by other data
        CGM_change_pls=zmu_other_JF(mean_CGM_change,std_CGM_change,CGM_change_pls')';
        x_samp(:,kj)=[gb_plss(kj-M_pls+1:kj)';inss(end-M_pls:end-1)';CGM_change_pls(kj-M_pls+1:kj)'];
        [X_cross Y_cross cluster(kj)]=cluster1_JF(x_samp(1:M_pls,kj));
        [y_p(:,kj)]=online_sim_3_6_JF(x_samp(:,kj),R_pls,M_pls,phi_pls,I_pls,J_pls,alpha_pls,sigma1,sigma2,cluster(kj));
    clear CGM ins
    save temp_SEDFR_nonoise
    save temp_SEDFR_noise