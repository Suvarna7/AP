function [x,fval,exitflag,output,lambda,grad,hessian] = fmincon_test_2(x0,Aineq,bineq,Aeq,beq)
%% This is an auto generated MATLAB file from Optimization Tool.

%% Start with the default options
options = optimoptions('fmincon');
%% Modify options setting
options = optimoptions(options,'Display', 'off');
options = optimoptions(options,'Algorithm', 'active-set');
options = optimoptions(options,'Display', 'iter-detailed'); 
[x,fval,exitflag,output,lambda,grad,hessian] = ...
fmincon(@objective_2,x0,Aineq,bineq,Aeq,beq,[],[],@constraint_2,options);
