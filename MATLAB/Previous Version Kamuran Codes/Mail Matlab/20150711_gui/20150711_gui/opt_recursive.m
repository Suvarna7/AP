function [Q Y_model P lamda err]=opt_recursive(Y,phi,Q_old,P_old,lamda_old,upperlim,lowerlim)
clc
P=(1/(lamda_old))*(P_old-(P_old*phi*pinv(lamda_old+phi'*P_old*phi)*phi'*P_old))
pP=pinv(P)
%pP=[1 -2;3 -4]
    function V=objective(Q)
     %    parca0=phi'*Q
      %   parca1=(Y-phi'*Q)'
       %  parca2=(Y-phi'*Q)
        % parca3=(Y-phi'*Q)'*(Y-phi'*Q)
%         digerparca=(Q-Q_old)'
%         digerparca2=(Q-Q_old)'*pP
%         digerparca3=(Q-Q_old)'*pP*(Q-Q_old)
       V=(Q-Q_old)'*pP*(Q-Q_old)+(Y-phi'*Q)'*(Y-phi'*Q)
     %  V=(Q-Q_old)'*pP*(Q-Q_old)
    end
    function [c, ceq]=constraint(Q)
        A=Q(1:3,1)';B1=Q(4:15,1)';B2=Q(16:19,1)';B3=Q(20:23,1)';C=Q(24,1)';
        % Converting to State Space
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
        A_state
        eigA=abs(eig(A_state))
        c=max(eigA)-0.99
        ceq = [];
    end

options=optimset('Algorithm','interior-point','Display','off')
%Q=fmincon(@objective,Q_old,[],[],[],[])
% Q=fmincon(@objective,Q_old,[],[],[],[],lowerlim,upperlim);
 Q=fmincon(@objective,Q_old,[],[],[],[],lowerlim,upperlim,@constraint,options);
err=Y-phi'*Q;
Y_model=phi'*Q;
lamda1=0.9*lamda_old+(1-0.9)*0.99;
lamda2=exp(-(err^2)/(1000));
lamda=lamda1*lamda2;
if lamda<0.005;lamda=0.005;end
end