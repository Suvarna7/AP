function [Q Y_model lamda P err]=opt_recursive_arm(Y,phi,Q_old,P_old,lamda_old,err_old,upperlim,lowerlim,lamdafinal,alp,sens)
% Y
% P_old
% phi
% Q_old
% lamda_old
% err_old
% Y=3.05375075340271;
% 
% P_old= [ 3.15023806183179E20 -3.1502342251585936E20 -3.836673196998398E14 2232064.049598683
%  -3.150234225161085E20 3.1659892137745036E20 -1.57549886134181325E18 -1348655.8292982646
%  -3.836670704273081E14 -1.57549886159109478E18 1.57588252866151322E18 -883412.5324155326
%  2329080.763696619 -948412.4137667628 -1407723.7204375335 -3504060.969410148]
% 
% phi=[ -3.05375075340271
%  -3.05375075340271
%  -3.05375075340271
%  -2.815506908060428E-6]
% 
% Q_old=[4.393883719126764E-6
%  -1.000001444811312
%  0.0
%  1.285716385474055]
% 
% lamda_old= 0.40015075810880113
% 
% upperlim=[1.0      1.0      1.0      1.0 ]
% 
% lowerlim=[-1.0      -1.0      -1.0      -1.0 ]
% 
% sens=0.005
% 
% alp=0.9
% 
% lamdafinal=0.99
% 
% err_old=4.276306887263104E-6

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
Q
err=Y-phi'*Q
Y_model=phi'*Q
lamda1=alp*lamda_old+(1-alp)*lamdafinal
lamda2=exp(-(err_old^2)/(sens))
lamda=lamda1*lamda2
if lamda<0.005;lamda=0.005;end
warning off
end