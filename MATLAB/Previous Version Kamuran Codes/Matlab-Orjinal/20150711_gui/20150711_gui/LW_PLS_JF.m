%%%%%%%%%%LW-PLS Jianyuan Feng 2-5-2015%%%%offline%%%%%%%%%%%%%%%%%
function [tr,pr,wr,qr,yq_estimate]=LW_PLS_JF(X,Y,x_samp,R,phi,I,J,alpha,sigma1,sigma2,thita_m_offline,regression_coefficient)
% R=4;                          %number of latent variables
% N=60;                        %number of samples
% M=120;                        %number of input variables
% L=10;                         %number of output variables
% I                             maximum iteration number
% phi                            localization parameter
%X,Y                           %training data
[N,M]=size(X);
[N,L]=size(Y);
j=1;
 thita_m(j,:)=thita_m_offline;
      x_q=x_samp;
    x=X';y=Y';
    while j~=J
    thita=diag(thita_m(j,:));
    r=1;
    for i=1:N
        d(i)=sqrt((x(:,i)-x_q)'*thita*(x(:,i)-x_q));
    end
    sigma_d=std(d);
    w=exp(-d/sigma_d/phi);
    %%%%%%%%%%%%%%%
    regression_coefficient_one(:,:)=regression_coefficient(:,:);
    %%%%%%%%%%%%%%
    mean_rc=w*regression_coefficient_one'/sum(w);
    Var_weighted=w*((regression_coefficient_one-mean_rc'*ones(1,N))').^2/sum(w);
    thita_m(j+1,:)=(Var_weighted.^alpha+thita_m(j,:))/2;
    for mm=1:M
    sigma(mm)=abs((thita_m(j+1,mm)-thita_m(j,mm))/thita_m(j,mm));
    end
    if sum(sigma<sigma2*ones(1,M))==0
        j=J;
    else j=j+1; 
    end
    end
    [tr,pr,wr,qr,yq_estimate]=LW_PLS_original_JF(X,Y,x_q,R,thita_m(end,:),phi);
end



