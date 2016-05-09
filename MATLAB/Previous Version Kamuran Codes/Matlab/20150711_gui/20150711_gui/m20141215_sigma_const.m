function [c_state_sigma,fval]=m20141215_sigma_const(X_n,p1,x_a,DD)
lowerlim=[0 40 0 0 0.034 0.0185 0.65 10]';
upperlim=[250 600 600 600 0.136 0.074 2.6 180]';

X_n =[
    0.0927
  142.4361
   -0.0000
    0.0000
    0.0680
    0.0370
    1.3000
   19.1056]

x_a =[
    0.0927
  146.8200
    0.0000
    0.0000
    0.0680
    0.0370
    1.0172
   20.0000]


  DD='off'
   
  p1=eye(8)

options=optimset('Algorithm','levenberg-marquardt')
[c_state_sigma,fval]=fmincon(@objective,x_a,[],[],[],[],lowerlim,upperlim,[],options)
function V=objective(Q)
        V=(Q-X_n)'*p1*(Q-X_n);
    end
end

    