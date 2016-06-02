%% Author:Jianyuan Feng <jfeng12@hawk.iit.edu> 2014
function [Sp,y_p,x_e,sum_wyxT,sum_wxxT,sum_xxp,sum_xpxp,sum_wyy,sum_wyx,sum_xx,sum_xxpp,Q,R,A,C,w]=m2014_3_19_kf_JF(lamda,x_r,x_e,y,k,sum_wyxT,sum_wxxT,sum_xxp,sum_xpxp,sum_wyy,sum_wyx,sum_xx,sum_xxpp,Q,R,A,C,a_w0,b_w0)
%%%%%%%%%%%%parameter updating%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
global wyxT wxxT xxp xpxp wyy wyx xx xxpp
% a_w0=1;
% b_w0=1;
% if k<408

% else
%     lamda=0.99;
%     gain=1/400;
% end
d1=size(y,1);
d2=size(x_r,1);
x_e(:,end+1)=A*x_e(:,end);
% w=(a_w0+0.5)/(b_w0+(y-C*A*x_e(:,k-1))'/(R)*(y-C*A*x_e(:,k-1)));
w=(a_w0+0.5)/(b_w0+(y-C*A*x_e(:,k-1))/(R)*(y-C*A*x_e(:,k-1)));
% if w<0.01
%     w=0.01;
% end
% 
% sigma_k=pinv(w*C'/(R)*C+pinv(Q));
% x_e(:,k)=sigma_k*(pinv(Q)*A*x_e(:,k-1)+w*C'/(R)*y);
% %%%%%%%%%%propagation%%%%%%%%
% x_p=A*x_e(:,k-1);
% sigma_k_p=Q;
% %%%%%%%%%%update%%%%%%%%%%%%%
% s_p=pinv(C*sigma_k_p*C'+1/w*R);%w=w_e
% K_p=sigma_k_p*C'*s_p;
% x_e(:,k)=x_p+K_p*(y-C*x_p);
% sigma_k=(eye(d2)-K_p*C)*sigma_k_p;
% w=a_w0+0.5/(b_w0+(y-C*x_p)'*inv(R)*(y-C*x_p));
hh=1000000;
if k>hh+8
    gain=1/hh;
sum_wyxT=sum_wyxT-wyxT(:,:,k-hh);
sum_wxxT=sum_wxxT-wxxT(:,:,k-hh);
sum_xxp=sum_xxp-xxp(:,:,k-hh);
sum_xpxp=sum_xpxp-xpxp(:,:,k-hh);
sum_wyy=sum_wyy-wyy(:,k-hh);
sum_wyx=sum_wyx-wyx(:,k-hh);
sum_xx=sum_xx-xx(:,k-hh);
sum_xxpp=sum_xxpp-xxpp(:,:,k-hh);
else
    gain=1/k-8;
end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
wyxT(:,:,k)=w*y*x_e(:,k)';
wxxT(:,:,k)=w*(x_e(:,k)*x_e(:,k)');
xxp(:,:,k)=x_e(:,k)*x_e(:,k-1)';
xpxp(:,:,k)=x_e(:,k-1)*x_e(:,k-1)';
for m=1:d1
    wyy(m,k)=w*y(m)^2;
    wyx(:,m,k)=w*y(m)*x_e(:,k);
end
for n=1:d2
    xx(n,k)=x_e(n,k).^2;
    xxpp(:,n,k)=x_e(n,k)*x_e(:,k-1);
end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


sum_wyxT=w*y*x_e(:,k)'+sum_wyxT*lamda;sum_wxxT=w*(x_e(:,k)*x_e(:,k)')+sum_wxxT*lamda;
sum_xxp=x_e(:,k)*x_e(:,k-1)'+sum_xxp*lamda;sum_xpxp=x_e(:,k-1)*x_e(:,k-1)'+sum_xpxp*lamda;
for m=1:d1
    sum_wyy(m)=w*y(m)^2+sum_wyy(m)*lamda;
    sum_wyx(:,m)=w*y(m)*x_e(:,k)+sum_wyx(:,m)*lamda;
end
for n=1:d2
    sum_xx(n)=x_e(n,k).^2+sum_xx(n)*lamda;
    sum_xxpp(:,n)=x_e(n,k)*x_e(:,k-1)+sum_xxpp(:,n)*lamda;
end
    


%%%%%%%%%%%%%%%%%140

for m=1:d1 
R(m,m)=gain*(sum_wyy(m)-2*C(m,:)*sum_wyx(:,m)+diag(C(m,:)*sum_wxxT*C(m,:)'));
if R(m,m)<=0
    R(m,m)=1;
end
end
for n=1:d2
    Q(n,n)=gain*(sum_xx(n)-2*A(n,:)*sum_xxpp(:,n)+diag(A(n,:)*sum_xpxp*A(n,:)'));
    if Q(n,n)<=0
        Q(n,n)=0.1;
    end
end

C=sum_wyxT*pinv(sum_wxxT);
A=sum_xxp*pinv(sum_xpxp);
    

% sigma_kp=pinv(C*Q*C'+1/w*R);
sigma_kp=Q;
Sp=1/(C*Q*C'+1/w*R);
K=Q*C'*Sp;
x_e(:,k)=A*x_e(:,k-1)+K*(y-C*A*x_e(:,k-1));

sigma_k=(eye(d1)-K*C)*sigma_kp;
y_p=C*x_e(:,k);

     
end






