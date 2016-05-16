% example from http://www.mathworks.com/help/optim/ug/optimization-tool-with-the-fmincon-solver.html
% This function is the equality to be solved. 
function f = objecfun(x)
f = x(1)^2 + x(2)^2;
