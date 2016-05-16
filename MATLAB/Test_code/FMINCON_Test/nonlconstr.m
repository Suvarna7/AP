% example from http://www.mathworks.com/help/optim/ug/optimization-tool-with-the-fmincon-solver.html
% This function represents the nonlinear constraints to which we need to
% subject our function 'objecfun'. 
function [c,ceq] = nonlconstr(x)
c = [-x(1)^2 - x(2)^2 + 1;
     -9*x(1)^2 - x(2)^2 + 9;
     -x(1)^2 + x(2);
     -x(2)^2 + x(1)];
ceq = [];