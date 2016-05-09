
function [y_p]=online_sim_3_6_JF(x_samp,R,M,phi,I,J,alpha,sigma1,sigma2,cluster)
% load('C:\Users\Ali Cinar\Desktop\redunency analysis\data for large simulation\sim_results.mat') 
% % load('C:\Users\Ali Cinar\Desktop\redunency analysis\Controller modification with simulator - alarm\allpatient\adu#006 1.3\sim_results.mat')
% st=5;
% % BGC=data.results(1,1).G.signals.values(1:st:end)';
% CGM=data.results(1,1).sensor.signals.values(1:st:end);
% % 
% % gb=CGM(1:st:end);
% ins=data.results(1,1).injection.signals.values(1:st:end)/100;
% CGM_change=[0,gb(2:end)-gb(1:end-1)];
% mean_gb=mean(gb);
% std_gb=std(gb);
% mean_ins=mean(ins);
% std_ins=std(ins);
% mean_CGM_change=mean(CGM_change);
% std_CGM_change=std(CGM_change);
% gb=zmu(gb')';
% ins=zmu(ins')';
% CGM_change=zmu(CGM_change')';
NP=1;
N_data=4;
% R=4;                          %number of latent variables
% N=1600;                        %number of samples
% M=24;                        %number of input variables/2
% L=1;                         %number of output variables/2
% q=1;
% phi=20;
% I=3;
% J=3;
% R=5;
% sigma1=0.2;
% sigma2=0.3;
% alpha=0.8;
% for preiction_step=0:NP-1
%     %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%         N_old=0;
%      for data_load=1:N_data
%         if data_load==1
%             load('C:\Users\Ali Cinar\Desktop\redunency analysis\data for large simulation\data7_28.mat')
%             CGM=data_pls1(:,1);
%             ins=data_pls1(:,2);
%         elseif data_load==2
%             CGM=data_pls2(:,1);
%             ins=data_pls2(:,2);
%         elseif data_load==3
%             load('C:\Users\Ali Cinar\Desktop\redunency analysis\data for large simulation\data8_28.mat')
%             CGM=data_pls1(:,1);
%             ins=data_pls1(:,2);
%         elseif data_load==4
%             CGM=data_pls2(:,1);
%             ins=data_pls2(:,2);
%         end
%         %%
%         gb=CGM;
%         gb=sgolayfilt(gb,3,31);
%         CGM_change=[0;gb(2:end)-gb(1:end-1)];
%         N=length(gb)-M-NP+1;                        %number of samples
%         gb=zmu(gb')';
%         ins=zmu(ins')';
%         CGM_change=zmu(CGM_change')';
%         for l=1:N
%             x(:,l+N_old)=[gb(l:M-1+l);ins(l:M-1+l);CGM_change(l:M-1+l)];
%             y(:,l+N_old)=[gb(l+M+preiction_step)];
%         end
%         N_old=N+N_old;
%     end
%     xx=x';
%     yy=y';
%     X(:,:,preiction_step+1)=xx;
% Y(:,:,preiction_step+1)=yy;
%     %%%%%%%%%%%%%%%%%%%%%%
% end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
load plsdata730_R_12_withcluster_16_data
        if cluster==1
            x_cluster=x_cluster1;
            y_cluster=y_cluster1;
        elseif cluster==2
            x_cluster=x_cluster2;
            y_cluster=y_cluster2;
        elseif cluster==3
            x_cluster=x_cluster3;
            y_cluster=y_cluster3;
        elseif cluster==4
            x_cluster=x_cluster4;
            y_cluster=y_cluster4;
        elseif cluster==5
            x_cluster=x_cluster5;
            y_cluster=y_cluster5;
        end
    if cluster==1
           
            thita_m_i=thita_mm1;
            regression_coefficient=rc1;
        elseif cluster==2
           
            thita_m_i=thita_mm2;
            regression_coefficient=rc2;
        elseif cluster==3
            
            thita_m_i=thita_mm3;
            regression_coefficient=rc3;
        elseif cluster==4
          
            thita_m_i=thita_mm4;
            regression_coefficient=rc4;
        elseif cluster==5
          
            thita_m_i=thita_mm5;
            regression_coefficient=rc5;
    end

    XX=x_cluster;
YY=y_cluster
XX(1,1)
 
    [tr,pr,wr,qr,yq_estimate]=LW_PLS_JF(XX,YY,x_samp,R,phi,I,J,alpha,sigma1,sigma2,thita_m_i,regression_coefficient);
    y_p=yq_estimate;
    

end

