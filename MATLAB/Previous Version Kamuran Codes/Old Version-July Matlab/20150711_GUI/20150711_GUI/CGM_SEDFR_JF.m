function CGM_retuning=CGM_SEDFR_JF(CGM,bolus_insulin,basal_insulin,flag_noise)
ins_every5=basal_insulin(1,:)+bolus_insulin';%???
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

%  for noise_interval=[24 36]
%     for length_add=2:4
%        for am=[20 30 40]
if length(CGM)<22
    noise_interval=[12];
    length_add=3;
    am=[30];
    type_recording=zeros(6,3);
    score_retuning=[];
    sigma_a=0.6;
    phi_max=15;% angle maximum
    ii=0;
    delta_x=15;
    st=1;
    gb=CGM(1:st:end);
    gb_real=CGM(1:st:end)';
    iii=0;
    percentage=0;%1-100
    type=0;
    g_kal=zeros(1,50);
    g_time=zeros(1,50);
    g_pls=zeros(1,100);
    error_detection=zeros(100,4);
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
    sdd=1.8;
    sd_x=zeros(4,8);
    sd_y=zeros(1,8);
    d2=4;
    [sum_wyxT,sum_wxxT,sum_xxp,sum_xpxp,sum_wyy,sum_wyx,sum_xx,sum_xxpp]=deal(zeros(1,d2),0,0,0,[0],0*ones(d2,1),zeros(d2,1),zeros(d2,d2));
    R=eye(1)*10^cc;
    Q=eye(d2)*alpha;
    for kj=1:21
        x_e(:,kj)=100*ones(d2,1);
    end
    x_r_old=[gb(8-d2:8-1)]';
    x_ek_old=[gb(8-d2:8-1)]';
    A=eye(d2);
    C=zeros(1,d2);
    C(1,1)=1;
    
    % x_P=[gb(1),ins(1)]';
    modify_cgm=gb(1);
    gb_error(1)=gb(1);
    prediction_residual(:,1)=[0;0];
    % %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % %%%%%%%%%%%%%%%%%%%initial for pls%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    NP=10;
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
    load meanandstd
    mean_gb=gb_m;
    std_gb=gb_sd;
    mean_ins=ins_m;
    std_ins=ins_sd;
    mean_CGM_change=gbchange_m;
    std_CGM_change=gbchange_sd;
    inss=zmu_other_JF(mean_ins,std_ins,ins')';
    prediction_step_pls=0;
    noisel=0;
    clear CGM ins
    if flag_noise==1
        save temp_SEDFR_noise
    else
        save temp_SEDFR_nonoise
    end

    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%start
else
    if flag_noise==1
        load temp_SEDFR_noise
    else
        load temp_SEDFR_nonoise
    end


      gb=CGM(1:st:end);
    gb_real=CGM(1:st:end)';
     inss=zmu_other_JF(mean_ins,std_ins,ins')';
    kj=length(ins);
    gb_ret(kj)=gb(kj);
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%noise generator%%%%%%%%%%%%%%%%%%%%%
    [noise_signal]=nss_missing_data_JF(percentage);
    if kj>25 && mod(kj,noise_interval)==0 && flag_noise==1
        noisel=length_add;
        if type<6
            type=1+type;
        else
            type=1;
        end
        addsignal=addon_noise(type,am,length_add,gb,kj)
        gb(kj)=gb(kj)+addsignal(1)';
    else if noisel>1 
            gb(kj)=gb(kj)+addsignal(5-noisel)';
            noisel=noisel-1;
        elseif flag_noise==1
            gb(kj)=gb(kj)+2*(rand-0.5);
        end
    end
    if kj>25
        if isnan(gb(kj)) || gb(kj)==gb(kj-1)
            y=2*gb_angle_ret(kj-1)-gb_angle_ret(kj-2);
            
        else
            y=[gb(kj)]';
        end
    else
        y=[gb(kj)]';
    end
    gb_with_NaN(kj)=y;
    gb_kal(kj)=gb(kj);
    gb_kal_ret(kj)=gb(kj);
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%kalman filter%%%%%%%%%%%%%%%%%%%
    RR(kj)=R;
    AA(:,:,kj)=A;
    CC(:,:,kj)=C;
    QQ(:,:,kj)=Q;
    
    if kj>21
        x_r=[gb_with_NaN(kj-d2+1:kj)]';
        [Sp,y_pkal,x_e,sum_wyxT,sum_wxxT,sum_xxp,sum_xpxp,sum_wyy,sum_wyx,sum_xx,sum_xxpp,Q,R,A,C,w]=m2014_3_19_kf_JF(lamda,x_r,x_e,y,kj,sum_wyxT,sum_wxxT,sum_xxp,sum_xpxp,sum_wyy,sum_wyx,sum_xx,sum_xxpp,Q,R,A,C,a_w0,b_w0);
        kal_residual_x(:,kj)=x_e(:,kj)-AA(:,:,kj-1)*x_e(:,kj-1);
        recording_w(kj)=w;
        ssP(kj)=Sp;
        gb_kal(kj)=y_pkal;
        kal_residual_y(:,kj)=y-gb_kal(kj);
        gb_kal_xp(kj)=C*A*x_e(:,kj);
        
    end
    %             %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%sensor error detection
    if kj>23
        g_pls(kj)=y_p(1,kj-M_pls-1)*std_gb+mean_gb;
        if isnan(gb(kj))|| gb(kj)==gb(kj-1)
            gb_ret(kj)=(gb_kal(kj)+g_pls(kj))/2;
            gb_kal_ret(kj)=gb_kal(kj);
            error_detection(kj,:)=[1 1 1 1];
        else
            
            %             if abs(mean(gb(kj-2:kj)-gb(kj-3:kj-1)))>8 && abs(mean(gb(kj-1:kj)-2*gb(kj-2:kj-1)+gb(kj-3:kj-2)))<10
            %                 sigma(kj)=1.5;
            %             elseif abs(mean(gb(kj-2:kj)-gb(kj-3:kj-1)))<7 ||  mean(abs(kal_residual_y(:,kj-2:kj-1)))<2
            %                 sigma(kj)=0.7;
            %             else sigma(kj)=1;
            %             end
            %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
            sigma(kj)=sigma_a;
            
            error_detection(kj,:)=[abs(gb_kal(kj)-gb(kj))>12*sigma(kj) abs(g_pls(kj)-gb(kj))>12*sigma(kj) 0 abs(gb(kj)-gb(kj-1))>25];
            %
            if error_detection(kj,1)==1
                
                gb_kal_ret(kj)=gb_kal(kj);
                
                
            end
            if error_detection(kj,1)==1 && error_detection(kj,2)==1 &&(gb_kal(kj)-gb(kj))*(g_pls(kj)-gb(kj))>0
                gb_ret(kj)=(gb_kal(kj)+g_pls(kj))/2;
                
            elseif error_detection(kj,1)==1
                if (gb_kal(kj)-gb(kj))*(g_pls(kj)-gb(kj))>0 && abs(g_pls(kj)-gb(kj))>6*sigma(kj)
                    gb_ret(kj)=(gb_kal(kj)+g_pls(kj))/2;
                else
                    gb_ret(kj)=gb(kj);
                end
            elseif error_detection(kj,2)==1
                if (gb_kal(kj)-gb(kj))*(g_pls(kj)-gb(kj))>0 && abs(gb_kal(kj)-gb(kj))>6*sigma(kj)
                    gb_ret(kj)=(gb_kal(kj)+g_pls(kj))/2;
                else
                    gb_ret(kj)=gb(kj);
                end
            else
                gb_ret(kj)=gb(kj);
            end
            f1(kj)=abs(mean(gb(kj-2:kj)-gb(kj-3:kj-1)));
            %     f2(kj)=abs(mean(gb(kj-1:kj)-2*gb(kj-2:kj-1)+gb(kj-3:kj-2)));
            f2(kj)=abs(gb(kj)-2*gb(kj-1)+gb(kj-2));
            sigmaa(kj)=f1(kj)>8 && f2(kj)>3;
            if sum(sigmaa(kj-3:kj))+sum(error_detection(kj-3:kj,1))>3
                gb_ret(kj)=gb(kj);
            end
        end
        
        y1=gb_kal(kj);
        y2=2*gb_angle_ret(kj-1)-gb_angle_ret(kj-2);
        
        yy= gb_angle_ret(kj-1);
        angle(kj,1)=angle_detection_JF(delta_x,yy,y1,y2)/pi*180;
        y1=g_pls(kj);
        angle(kj,2)=angle_detection_JF(delta_x,yy,y1,y2)/pi*180;
        y1=gb_with_NaN(kj);
        angle(kj,3)=angle_detection_JF(delta_x,yy,y1,y2)/pi*180;
        y1=(gb_kal(kj)+g_pls(kj))/2;
        angle(kj,4)=angle_detection_JF(delta_x,yy,y1,y2)/pi*180;
        if isnan(gb(kj))|| gb(kj)==gb(kj-1)
            if min(angle(kj,[1 2 4]))==angle(kj,2)
                gb_angle_ret(kj)=g_pls(kj);
                
            elseif min(angle(kj,[1 2 4]))==angle(kj,1)
                gb_angle_ret(kj)=gb_kal(kj);
            else
                gb_angle_ret(kj)=(gb_kal(kj)+g_pls(kj))/2;
            end
            
            
        else
            if min(angle(kj,:))==angle(kj,2) && ((sum(error_detection(kj,2))>0 || (sum(gb_angle_ret(kj-1)~=gb_with_NaN(kj-1))==1 && error_detection(kj,2)==1)) && (gb_kal(kj)-gb(kj))*(g_pls(kj)-gb(kj))>0 )
                gb_angle_ret(kj)=g_pls(kj);
            elseif min(angle(kj,:))==angle(kj,1)&& (sum(error_detection(kj,1))>0 )
                gb_angle_ret(kj)=gb_kal(kj);
            elseif min(angle(kj,:))==angle(kj,4) &&( (sum(error_detection(kj,1:2))>1 || (sum(gb_angle_ret(kj-1)~=gb_with_NaN(kj-1))==1 && error_detection(kj,2)==1)))
                gb_angle_ret(kj)=(gb_kal(kj)+g_pls(kj))/2;
            else
                gb_angle_ret(kj)=gb_with_NaN(kj);
            end
            if angle(kj,3)<phi_max || sum(gb_angle_ret(kj-8:kj-1)~=gb_with_NaN(kj-8:kj-1))>4 %|| (sum(gb_angle_ret(kj-8:kj-2)~=gb_with_NaN(kj-8:kj-2))>0 && gb_angle_ret(kj-1)==gb_with_NaN(kj-1))
                gb_angle_ret(kj)=gb_with_NaN(kj);
            end
        end
        
        
        %          gb_ret(kj)=gb_angle_ret(kj);
        %%%%%%%150520
        if gb_angle_ret(kj)~=gb_with_NaN(kj) || isnan(gb(kj)) || gb(kj)==gb(kj-1)
            error_detectino_real(kj)=1;
        else
            error_detectino_real(kj)=0;
        end
    else
error_detectino_real(kj)=0;
        gb_angle_ret(kj)=gb_with_NaN(kj);
        %%%%%%%%%%%%%%%%assessment summery%%%%%%%%%%%%%%%%%%%%%%%%%
        %         if error_detectino_real(kj)==1;
        
        
    end
    
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%LW pls %%%%%%%%%%%%%%%%%%%
    if kj>M_pls
        stepp=kj-M_pls;
        gb_plss=sgolayfilt(gb_angle_ret,1,7);
        CGM_change_pls=[0,gb_plss(2:end)-gb_plss(1:end-1)];
        gb_plss=zmu_other_JF(mean_gb,std_gb,gb_plss')';%zmu_other centralized by other data
        CGM_change_pls=zmu_other_JF(mean_CGM_change,std_CGM_change,CGM_change_pls')';
        x_samp=[gb_plss(stepp+1:M_pls+stepp)';inss(stepp+1:M_pls+stepp)';CGM_change_pls(stepp+1:M_pls+stepp)'];
        [X_cross Y_cross cluster]=cluster1_JF(x_samp(1:12));
        [y_p(:,stepp)]=online_sim_3_6_JF(x_samp,R_pls,M_pls,phi_pls,I_pls,J_pls,alpha_pls,sigma1,sigma2,cluster);
    end
    if kj>23
        if noisel==1%[ better worse miss wrong]
            if sum(error_detectino_real(kj-length_add+1:kj))>0
                if sum(abs(gb_angle_ret(kj-length_add+1:kj)-gb_real(kj-length_add+1:kj))<abs(gb(kj-length_add+1:kj)'-gb_real(kj-length_add+1:kj)))>0 || (type==5 && sum(abs(gb_angle_ret(kj-length_add+1:kj)-gb_real(kj-length_add+1:kj))<10)==length_add)
                    score_retuning(end+1,:)=[1 0 0 0];
                    type_recording(type,1)= type_recording(type,1)+1;
                else
                    score_retuning(end+1,:)=[0 1 0 0];
                    type_recording(type,2)= type_recording(type,2)+1;
                end
            else
                type_recording(type,3)= type_recording(type,3)+1;
                score_retuning(end+1,:)=[0 0 1 0];
            end
        elseif error_detectino_real(kj)==1 && error_detectino_real(kj-1)==0 && noisel==0
            score_retuning(end+1,:)=[0 0 0 1];
        end
    end
    
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 
end
clear CGM ins
 if flag_noise==1
        save temp_SEDFR_noise
    else
        save temp_SEDFR_nonoise
    end
if length(gb_angle_ret)>0
CGM_retuning=gb_angle_ret(kj);
else
   CGM_retuning=88; 
end

%%%%%%%%%plot test
% if flag_noise==1
% figure(2)
% subplot(2,1,1)
% plot(gb_kal,'g--','linewidth',2)
% hold on
% plot(gb,'rx-','linewidth',2)
% pre=1;
% plot([M_pls+pre+1:M_pls+pre+length(y_p(pre,:))],y_p(pre,:)*std_gb+mean_gb,'k','linewidth',1)
% % plot(gb_ret,'b','linewidth',2)
% % plot(gb_time,'m')
% %  plot([2:length(g_prediction(1,:))+1],g_prediction(1,:),'m')
% 
% % plot(gb_kal_ret,'m','linewidth',2)
% plot(CGM(1:st:end),'--','linewidth',2)
% plot(gb_angle_ret,'ko--','linewidth',2)
% % plot(CGM,'--','linewidth',2)
% % plot([2:length(gb_kal_xp)],gb_kal_xp(1,1:end-1),'--m','linewidth',2)
% legend('Estimation from RKF','CGM with problem','Estimation from LW-PLS','real CGM value','retuned CGM')
% xlabel('Time(5 mins)','FontSize',16,'FontWeight','bold')
% ylabel('mg/dl','FontSize',16,'FontWeight','bold')
% set(gca,'FontSize',16,'FontWeight','bold')
% plot(error_detectino_real*300,'DisplayName','error_detection(:,1)','YDataSource','error_detection(:,1)');figure(gcf)
% % plot(BGC,'--g')
% hold off
% subplot(2,1,2)
% bar(error_detection(:,1)*100)
% hold on
% bar(error_detection(:,2)*50,'r')
% else
% figure(1)
% subplot(2,1,1)
% plot(gb_kal,'g--','linewidth',2)
% hold on
% plot(gb,'rx-','linewidth',2)
% pre=1;
% plot([M_pls+pre+1:M_pls+pre+length(y_p(pre,:))],y_p(pre,:)*std_gb+mean_gb,'k','linewidth',1)
% % plot(gb_ret,'b','linewidth',2)
% % plot(gb_time,'m')
% %  plot([2:length(g_prediction(1,:))+1],g_prediction(1,:),'m')
% 
% % plot(gb_kal_ret,'m','linewidth',2)
% plot(CGM(1:st:end),'--','linewidth',2)
% plot(gb_angle_ret,'ko--','linewidth',2)
% % plot(CGM,'--','linewidth',2)
% % plot([2:length(gb_kal_xp)],gb_kal_xp(1,1:end-1),'--m','linewidth',2)
% legend('Estimation from RKF','CGM with problem','Estimation from LW-PLS','real CGM value','retuned CGM')
% xlabel('Time(5 mins)','FontSize',16,'FontWeight','bold')
% ylabel('mg/dl','FontSize',16,'FontWeight','bold')
% set(gca,'FontSize',16,'FontWeight','bold')
% plot(error_detectino_real*300,'DisplayName','error_detection(:,1)','YDataSource','error_detection(:,1)');figure(gcf)
% % plot(BGC,'--g')
% hold off
% subplot(2,1,2)
% bar(error_detection(:,1)*100)
% hold on
% bar(error_detection(:,2)*50,'r')
% end
% clear CGM ins
% if flag_noise==1
%     save temp_SEDFR_noise
% else
%     save temp_SEDFR_nonoise
% end
% end
% 


% figure
% subplot(2,1,1)
% plot(gb_kal,'g--','linewidth',2)
% hold on
% plot(gb,'rx-','linewidth',2)
% pre=1;
% plot([M_pls+pre+1:M_pls+pre+length(y_p(pre,:))],y_p(pre,:)*std_gb+mean_gb,'k','linewidth',1)
% % plot(gb_ret,'b','linewidth',2)
% % plot(gb_time,'m')
% %  plot([2:length(g_prediction(1,:))+1],g_prediction(1,:),'m')
%
% % plot(gb_kal_ret,'m','linewidth',2)
% plot(CGM(1:st:end),'--','linewidth',2)
% plot(gb_angle_ret,'ko--','linewidth',2)
% % plot(CGM,'--','linewidth',2)
% % plot([2:length(gb_kal_xp)],gb_kal_xp(1,1:end-1),'--m','linewidth',2)
% legend('Estimation from RKF','CGM with problem','Estimation from LW-PLS','real CGM value','retuned CGM')
% xlabel('Time(5 mins)','FontSize',16,'FontWeight','bold')
% ylabel('mg/dl','FontSize',16,'FontWeight','bold')
% set(gca,'FontSize',16,'FontWeight','bold')
% plot(error_detectino_real*300,'DisplayName','error_detection(:,1)','YDataSource','error_detection(:,1)');figure(gcf)
% % plot(BGC,'--g')
% hold off
% subplot(2,1,2)
% bar(error_detection(:,1)*100)
% hold on
% bar(error_detection(:,2)*50,'r')
%
% save sim5_1(7)
%
%
%
