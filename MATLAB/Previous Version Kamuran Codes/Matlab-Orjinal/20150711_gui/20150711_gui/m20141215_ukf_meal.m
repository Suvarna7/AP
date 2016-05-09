%% Author: Kamuran Turksoy <kturksoy@hawk.iit.edu> 2014
%% Run unscented Kalman filter
function [x_p,P_p]=m20141215_ukf_meal(y,x_a,P_a,R_n,Q_n,Gb)
%% Define parameters
alp=1;
bet=2;
K=0;
n_y=size(y,1);
%% Calculate 2N+1 sigma-points based on present state covariance
L=size(x_a,1);
lamda=alp^2*(L+K)-L;
W0m=lamda/(L+lamda);
W0c=W0m+(1-alp^2+bet);
Wm=[W0m 1/(2*(L+lamda))*ones(1,2*L)];
Wc=[W0c 1/(2*(L+lamda))*ones(1,2*L)];

[LL, DD]=ldl(P_a);
X_a=[x_a x_a(:,ones(1,numel(x_a)))+sqrt(L+lamda)*LL*DD^0.5 x_a(:,ones(1,numel(x_a)))-sqrt(L+lamda)*LL*DD^0.5];

%% Propagate the sigma points through the nonlinear dynamics
X_n=zeros(L,2*L+1);
x_n=zeros(L,1);
for ii=1:2*L+1
    X_n(:,ii)=f_fcn(X_a(:,ii),1);
    X_n(:,ii)=m20141215_sigma_const(X_n(:,ii),eye(L),x_a,'off');
    x_n=x_n+Wm(:,ii)*X_n(:,ii);
end
%% Compute the predicted covariance
P_n=zeros(L,L);
for ii=1:2*L+1
    P_n=P_n+Wc(:,ii)*(X_n(:,ii)-x_n)*(X_n(:,ii)-x_n)';
end

P_n=P_n+Q_n;

%% Instantiate the new sigma points through the observation model g_fnc
Y=zeros(n_y,2*L+1);
y_n=zeros(n_y,1);
for ii=1:2*L+1
    Y(:,ii)=g_fcn(X_n(:,ii));
    y_n=y_n+Wm(:,ii)*Y(:,ii);
end

%% Obtain the innovation covariance and the cross covariance matrices
P_yy=zeros(n_y,n_y);
P_yx=zeros(L,n_y);


for ii=1:2*L+1
    P_yy=P_yy+Wc(:,ii)*(Y(:,ii)-y_n)*(Y(:,ii)-y_n)';
    P_yx=P_yx+Wc(:,ii)*(X_n(:,ii)-x_n)*(Y(:,ii)-y_n)';
end

P_yy=P_yy+R_n;

%% Perform the measurement update using the regular Kalman Filter equations
K_p=P_yx/P_yy;
x_p=x_n+K_p*(y-y_n);
P_p=P_n-K_p*P_yy*K_p';
%% Perform the measurement update using the regular Kalman Filter equations

    function f=f_fcn(x,h)
        Ieff=x(1);
        Gt=x(2);
        Rc=x(3);
        Rc_1=x(4);
        p1=x(5);
        p2=x(6);
        p4=x(7);
        taom=x(8);
        atao=exp(h/taom);
        f=[h*(-p2*Ieff)+Ieff;...
            h*(p1*(Gb-Gt)-p4*Ieff*Gt+Rc)+Gt;...
            2*Rc/atao-Rc_1/(atao^2);...
            Rc;...
            p1;...
            p2;...
            p4;...
            taom];
    end
    function g=g_fcn(x)
        g=x(2);
    end
end

