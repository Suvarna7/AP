%% Author: Kamuran Turksoy <kturksoy@hawk.iit.edu> 2014
%% Run unscented Kalman filter
function [x_p,P_p]=m20141215_ukf_meal(y,x_a,P_a,R_n,Q_n,Gb)
x_a=[0.023822827242947774
 161.64634423267063
 0.6472607928982281
 0.5848943228375739
 0.09138685138017064
 0.05013186557916884
 1.0332814469896734
 19.817318075527954
]

P_a=[5.1858073243156E-4 -0.05774499783967518 5.022388401749316E-4 3.746911389459042E-4 -1.642740490215763E-5 -1.272210519011995E-4 0.0018444830718112922 0.0041096730653447265
 -0.05774499783967518 47.910027078589 0.04814685138607558 0.06340984552765383 0.08187751542188879 0.03811804186756693 -0.3347686942483596 -0.27819943591685464
 5.022388401749316E-4 0.04814685138607558 0.049152389231987725 0.038180372223065806 0.001490670841348234 8.095255491007967E-4 0.006237805639158537 0.009144781957778677
 3.746911389459042E-4 0.06340984552765383 0.038180372223065806 0.033254905158885815 0.001634222442468245 8.500432962435479E-4 0.004995475480713005 0.005557964965238364
 -1.642740490215763E-5 0.08187751542188879 0.001490670841348234 0.001634222442468245 0.010545332259643927 1.5798767548334076E-4 -1.9088678644257017E-4 -8.162332419919538E-4
 -1.272210519011995E-4 0.038118041867566926 8.095255491007967E-4 8.500432962435479E-4 1.5798767548334076E-4 0.10018127574725465 -5.1986147109946254E-5 -3.5293915436607524E-4
 0.0018444830718112922 -0.33476869424835953 0.006237805639158537 0.004995475480713005 -1.9088678644257017E-4 -5.1986147109946254E-5 0.05580155606615187 0.05116494573245852
 0.0041096730653447265 -0.27819943591685464 0.009144781957778677 0.005557964965238364 -8.162332419919539E-4 -3.529391543660753E-4 0.05116494573245852 1.1422334053010803]

 Q_n=[1.0E-6 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 1.0E-6 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.001 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.001 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.01 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.1 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.01 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.1]

y=175.0
Gb=182.33333333333334 
Rn=100.0  

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
X_a=[x_a x_a(:,ones(1,numel(x_a)))+sqrt(L+lamda)*LL*DD^0.5 x_a(:,ones(1,numel(x_a)))-sqrt(L+lamda)*LL*DD^0.5]

%% Propagate the sigma points through the nonlinear dynamics
X_n=zeros(L,2*L+1);
x_n=zeros(L,1);
for ii=1:2*L+1
    X_n(:,ii)=f_fcn(X_a(:,ii),1);
    X_n(:,ii)=m20141215_sigma_const(X_n(:,ii),eye(L),x_a,'off');
    x_n=x_n+Wm(:,ii)*X_n(:,ii);
end
X_n
x_n
%% Compute the predicted covariance
P_n=zeros(L,L);
for ii=1:2*L+1
    P_n=P_n+Wc(:,ii)*(X_n(:,ii)-x_n)*(X_n(:,ii)-x_n)';
end

P_n=P_n+Q_n

%% Instantiate the new sigma points through the observation model g_fnc
Y=zeros(n_y,2*L+1);
y_n=zeros(n_y,1);
for ii=1:2*L+1
    Y(:,ii)=g_fcn(X_n(:,ii));
    y_n=y_n+Wm(:,ii)*Y(:,ii);
end
Y
y_n
%% Obtain the innovation covariance and the cross covariance matrices
P_yy=zeros(n_y,n_y);
P_yx=zeros(L,n_y);


for ii=1:2*L+1
    P_yy=P_yy+Wc(:,ii)*(Y(:,ii)-y_n)*(Y(:,ii)-y_n)';
    P_yx=P_yx+Wc(:,ii)*(X_n(:,ii)-x_n)*(Y(:,ii)-y_n)';
end
P_yx
P_yy=P_yy+R_n

%% Perform the measurement update using the regular Kalman Filter equations
K_p=P_yx/P_yy
x_p=x_n+K_p*(y-y_n)

P_n
P_yy
K_p
P_p=P_n-K_p*P_yy*K_p'
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

