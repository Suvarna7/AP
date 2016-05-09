%% Author:Jianyuan Feng <jfeng12@hawk.iit.edu> 2014
function [Sp,y_p,x_e,sum_wyxT,sum_wxxT,sum_xxp,sum_xpxp,sum_wyy,sum_wyx,sum_xx,sum_xxpp,Q,R,A,C,w]=m2014_3_19_kf_JF(lamda,x_r,x_e,y,k,sum_wyxT,sum_wxxT,sum_xxp,sum_xpxp,sum_wyy,sum_wyx,sum_xx,sum_xxpp,Q,R,A,C,a_w0,b_w0)
%%%%%%%%%%%%parameter updating%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
global wyxT wxxT xxp xpxp wyy wyx xx xxpp
% a_w0=1;
% b_w0=1;
% if k<408
wyxT
wxxT
xxp
xpxp
wyy
wyx
xx
xxpp
% else
%     lamda=0.99;
%     gain=1/400;
% end
d1=size(y,1);
d2=size(x_r,1);
carpim=x_e(:,end)
A
x_e(:,end+1)=A*x_e(:,end)
% w=(a_w0+0.5)/(b_w0+(y-C*A*x_e(:,k-1))'/(R)*(y-C*A*x_e(:,k-1)));
carpim2=x_e(:,k-1)
carpim3=A*x_e(:,k-1)
carpimc=C
carpima=A
carpimx_e=x_e(:,k-1)
carpim4= C*A*x_e(:,k-1)
carpim5=(b_w0+(y-C*A*x_e(:,k-1)))
carpim6=R
carpim7=y
carpim8=(R)*(y-C*A*x_e(:,k-1))
carpim9=(a_w0+0.5)
carpim29=(y-C*A*x_e(:,k-1))
carpim30=(y-C*A*x_e(:,k-1))/(R)
carpim42=(y-C*A*x_e(:,k-1))/(R)*(y-C*A*x_e(:,k-1))
carpim10=(b_w0+(y-C*A*x_e(:,k-1))/(R)*(y-C*A*x_e(:,k-1)))
carpim12=A*x_e(:,k-1)
carpim14=y
carpim11=(y-C*A*x_e(:,k-1))
carpimk=k
carpim19=(a_w0+0.5)
w=(a_w0+0.5)/(b_w0+(y-C*A*x_e(:,k-1))/(R)*(y-C*A*x_e(:,k-1)))
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
hh=1000000
if k>hh+8
    gain=1/hh
sum_wyxT=sum_wyxT-wyxT(:,:,k-hh)
sum_wxxT=sum_wxxT-wxxT(:,:,k-hh)
sum_xxp=sum_xxp-xxp(:,:,k-hh)
sum_xpxp=sum_xpxp-xpxp(:,:,k-hh)
sum_wyy=sum_wyy-wyy(:,k-hh)
sum_wyx=sum_wyx-wyx(:,k-hh)
sum_xx=sum_xx-xx(:,k-hh)
sum_xxpp=sum_xxpp-xxpp(:,:,k-hh)
else
    gain=1/k-8
end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
aradegeryeni=x_e(:,k)'
w
y
k
x_e
wyxT
ansmert=x_e(:,k)'
%wyxT=zeros(k,k,k);
wyxT(:,:,k)=w*y*x_e(:,k)'
aradegeryeni2=x_e(:,k)*x_e(:,k)'
wxxT(:,:,k)=w*(x_e(:,k)*x_e(:,k)')
aradeger3=x_e(:,k-1)'
aradeger4=x_e(:,k)'
xxp(:,:,k)=x_e(:,k)*x_e(:,k-1)'
xpxp(:,:,k)=x_e(:,k-1)*x_e(:,k-1)'
for m=1:d1
   d1
   aradegeryenii1=y(m)
   aradegeryenii2=y(m)^2
   aradegeryenii3=w
    wyy(m,k)=w*y(m)^2
   aradegeryenii4=x_e(:,k)
    wyx(:,m,k)=w*y(m)*x_e(:,k)
end
d2
for n=1:d2
    d2
    n
    k
    aradegermert=x_e(n,k)
    xx(n,k)=x_e(n,k).^2
    xxpp(:,n,k)=x_e(n,k)*x_e(:,k-1)
end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

aradegeriman=w*y*x_e(:,k)
sum_wyxT=w*y*x_e(:,k)'+sum_wyxT*lamda
sum_wxxT=w*(x_e(:,k)*x_e(:,k)')+sum_wxxT*lamda
sum_xxp=x_e(:,k)*x_e(:,k-1)'+sum_xxp*lamda
sum_xpxp=x_e(:,k-1)*x_e(:,k-1)'+sum_xpxp*lamda
for m=1:d1
    sum_wyy(m)=w*y(m)^2+sum_wyy(m)*lamda
    sum_wyx(:,m)=w*y(m)*x_e(:,k)+sum_wyx(:,m)*lamda
end
d2
for n=1:d2
    n
    ara=x_e(n,k)
    sum_xx(n)=x_e(n,k).^2+sum_xx(n)*lamda
    aracarpan=x_e(n,k)
    aracarpan1=x_e(:,k-1)
    sum_xxpp(:,n)=x_e(n,k)*x_e(:,k-1)+sum_xxpp(:,n)*lamda
end
    


%%%%%%%%%%%%%%%%%140

for m=1:d1 
    gain
    C
    sum_wyx
    arax=2*C(m,:)*sum_wyx(:,m)
    arax2=sum_wyy(m)-2*C(m,:)*sum_wyx(:,m)
    arax3=C(m,:)*sum_wxxT*C(m,:)'
    arax4= diag(C(m,:)*sum_wxxT*C(m,:)')
    arax5=(sum_wyy(m)-2*C(m,:)*sum_wyx(:,m)+diag(C(m,:)*sum_wxxT*C(m,:)'))
    arax6=R(m,m)
    arax7=gain*(sum_wyy(m)-2*C(m,:)*sum_wyx(:,m)+diag(C(m,:)*sum_wxxT*C(m,:)'))
    arax8= gain*(sum_wyy(m)-2*C(m,:)*sum_wyx(:,m)+diag(C(m,:)*sum_wxxT*C(m,:)'))
R(m,m)=gain*(sum_wyy(m)-2*C(m,:)*sum_wyx(:,m)+diag(C(m,:)*sum_wxxT*C(m,:)'))
Rdegeri=R(m,m)
if R(m,m)<=0
    Rdegeri=R(m,m)
    R(m,m)=1;
end
Rdegeri=R(m,m)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%Until now check%%%%%%%%%%%%%%%%%%%%%%%%%%%
end
for n=1:d2
    A
    aradegerA=A(n,:)
    aradegersum_xxpp=sum_xxpp(:,n)
    sum_xx(n)-2*A(n,:)*sum_xxpp(:,n)
    aray2=sum_xpxp
    aray1=A(n,:)
    aray=A(n,:)*sum_xpxp*A(n,:)'
    Q(n,n)=gain*(sum_xx(n)-2*A(n,:)*sum_xxpp(:,n)+diag(A(n,:)*sum_xpxp*A(n,:)'))
    if Q(n,n)<=0
        Qdegeri=Q(n,n)
        Q(n,n)=0.1
        Q(n,n)
    end
    Qdegeri2=Q(n,n)
    Q
end
 Qdegeri3=Q(n,n)
 Q
sum_wxxT
sum_xpxp
C=sum_wyxT*pinv(sum_wxxT)
A=sum_xxp*pinv(sum_xpxp)
    

% sigma_kp=pinv(C*Q*C'+1/w*R);
sigma_kp=Q
Sp=1/(C*Q*C'+1/w*R)
K=Q*C'*Sp
aracarpan0=A
aracarpan3=x_e(:,k-1)
aracarpan1=A*x_e(:,k-1)
aracarpan2=C*A
aracarpan4=C*A*x_e(:,k-1)
aracarpan5=y-C*A*x_e(:,k-1)
y
anss=A*x_e(:,k-1)+K*(y-C*A*x_e(:,k-1))
x_e(:,k)=A*x_e(:,k-1)+K*(y-C*A*x_e(:,k-1))
sigma_kp
sigma_k=(eye(d1)-K*C)*sigma_kp
y_p=C*x_e(:,k)
fonksiyonunicindecikmadanonce=k
     
end






