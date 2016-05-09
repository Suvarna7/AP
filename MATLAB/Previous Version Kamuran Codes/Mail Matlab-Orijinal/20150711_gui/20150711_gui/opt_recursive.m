function [Q Y_model P lamda err]=opt_recursive(Y,phi,Q_old,P_old,lamda_old,upperlim,lowerlim)
Y
Q_old
lamda_old
P_old
phi

size(phi)
size(P_old)
phi'*P_old*phi
lamda_old
pinv(lamda_old+phi'*P_old*phi)

P=(1/(lamda_old))*(P_old-(P_old*phi*pinv(lamda_old+phi'*P_old*phi)*phi'*P_old))
pP=pinv(P)

upperlim
lowerlim
    function V=objective(Q)
        Q;
        Q_old;
        Y;
        phi;
        pP;
        size(Q_old);
        size(pP);
        size(Y);
        size(phi);
        V=(Q-Q_old)'*pP*(Q-Q_old)+(Y-phi'*Q)'*(Y-phi'*Q);
    end
    function [c, ceq]=constraint(Q)
%   Q=[-0.11402275484292101
%  -0.11624536198789129
%  -0.11337260521135324
%  -0.029745161222378965
%  -0.02919557420391995
%  -0.02914578338119681
%  -0.02914841979117914
%  -0.02989731563650298
%  -0.029114894010825215
%  -0.0299336688098286
%  -0.029644071686332252
%  -0.029158827636380495
%  -0.029668087540795987
%  -0.029677626306563783
%  -0.03017494095580201
%  -0.10680960489948392
%  -0.07507082507056881
%  -0.07469659537467176
%  -0.15446683156837163
%  6.935819212048543E-4
%  0.002470466741184491
%  5.666893382703978E-4
%  5.756266732216465E-4
%  -0.003920002874693816];
        
        Q
        A=Q(1:3,1)'
        B1=Q(4:15,1)'
        B2=Q(16:19,1)'
        B3=Q(20:23,1)'
        C=Q(24,1)'
        ilksatir=[-A B1(2:end) B2(2:end) B3(2:end) C]
        %% Converting to State Space
        A_state=[-A B1(2:end) B2(2:end) B3(2:end) C;...
            1 zeros(1,20);...
            zeros(1,1) 1 zeros(1,19);...
            zeros(1,21);...
            zeros(1,3) 1 zeros(1,17);...
            zeros(1,4) 1 zeros(1,16);...
            zeros(1,5) 1 zeros(1,15);...
            zeros(1,6) 1 zeros(1,14);...
            zeros(1,7) 1 zeros(1,13);...
            zeros(1,8) 1 zeros(1,12);...
            zeros(1,9) 1 zeros(1,11);...
            zeros(1,10) 1 zeros(1,10);...
            zeros(1,11) 1 zeros(1,9);...
            zeros(1,12) 1 zeros(1,8);...
            zeros(1,21);...
            zeros(1,14) 1 zeros(1,6);...
            zeros(1,15) 1 zeros(1,5);...
            zeros(1,21);...
            zeros(1,17) 1 zeros(1,3);...
            zeros(1,18) 1 zeros(1,2);...
            zeros(1,21)];
        size(A_state)
        
        eigA=abs(eig(A_state));
        c=max(eigA)-0.99
        ceq = [];
    end

options=optimset('Algorithm','interior-point','Display','off');
Q=fmincon(@objective,Q_old,[],[],[],[],lowerlim,upperlim,@constraint,options);
%  Q=fmincon(@objective,Q_old,[],[],[],[],lowerlim,upperlim,[],options)
% Q=fmincon(@objective,Q_old,[],[],[],[],[],[],[],options)
Q
err=Y-phi'*Q
Y_model=phi'*Q
lamda1=0.9*lamda_old+(1-0.9)*0.99
lamda2=exp(-(err^2)/(1000))
lamda=lamda1*lamda2
if lamda<0.005;lamda=0.005;end
P_old
P
Y=2*a;
end