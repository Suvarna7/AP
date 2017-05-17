function [Q Y_model lamda P err]=opt_recursive_arm(Y,phi,Q_old,P_old,lamda_old,err_old,upperlim,lowerlim,lamdafinal,alp,sens)
P=(1/(lamda_old))*(P_old-(P_old*phi*pinv(lamda_old+phi'*P_old*phi)*phi'*P_old));
pP=pinv(P);
    function V=objective(Q)
        V=(Q-Q_old)'*pP*(Q-Q_old)+(Y-phi'*Q)'*(Y-phi'*Q);
    end
    function [c, ceq]=constraint(Q)
        A=Q(1:3,1)';C=Q(4,1)';
        %% Converting to State Space
        A_state=[-A C;...
            1 zeros(1,3);...
            zeros(1,1) 1 zeros(1,2);...
            zeros(1,4)];
        eigA=abs(eig(A_state));
        c=eigA-0.99*ones(length(eigA),1);
        ceq = [];
    end

options=optimset('Algorithm','active-set','Display','off');
Q=fmincon(@objective,Q_old,[],[],[],[],lowerlim,upperlim,@constraint,options);
err=Y-phi'*Q;
Y_model=phi'*Q;
lamda1=alp*lamda_old+(1-alp)*lamdafinal;
lamda2=exp(-(err_old^2)/(sens));
lamda=lamda1*lamda2;
if lamda<0.005;lamda=0.005;end
warning off
end