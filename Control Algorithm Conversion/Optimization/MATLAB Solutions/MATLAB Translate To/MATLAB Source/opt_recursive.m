function [Q Y_model P_out lamda err]=opt_recursive(Y0,phi0,Q_old0,P_old,lamda_old,upperlim,lowerlim)
%function [Q Y_model P lamda err]=opt_recursive(Y,phi,Q_old,P_old,lamda_old,upperlim,lowerlim)



%% MAIN CODE
%Start global variables:
global P pP Q_old Y phi;
Q_old = Q_old0;
Y = Y0;
phi = phi0;
P=(1/(lamda_old))*(P_old-(P_old*phi*pinv(lamda_old+phi'*P_old*phi)*phi'*P_old));
pP=pinv(P);
P_out = P;

        
%options=optimset('Algorithm','interior-point','Display','off');
options=optimset('Display','off');

Q=fmincon(@objective,Q_old,[],[],[],[],lowerlim,upperlim,@constraint,options);
err=Y-phi'*Q;
Y_model=phi'*Q;
lamda1=0.9*lamda_old+(1-0.9)*0.99;
lamda2=exp(-(err^2)/(1000));
lamda=lamda1*lamda2;
if lamda<0.005;lamda=0.005;end

end