function [c_state_sigma,fval]=m20141215_sigma_const(X_n,p1,x_a,DD)
lowerlim=[0 40 0 0 0.034 0.0185 0.65 10]';
upperlim=[250 600 600 600 0.136 0.074 2.6 180]';

options=optimset('Algorithm','active-set','Display',DD);
[c_state_sigma,fval]=fmincon(@objective,x_a,[],[],[],[],lowerlim,upperlim,[],options);
    function V=objective(Q)
        V=(Q-X_n)'*p1*(Q-X_n);
    end
end