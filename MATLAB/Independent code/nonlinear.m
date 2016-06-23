%% Original Code with fmincon
% %% objective function V=(Y-w)'*(Y-w)+((du'*diag(IC)*du))
% DD = 'iter';
%options=optimset('Algorithm','interior-point','Display',DD);
% x_a = [1 2 3 4 5 6 7 8];

%lowerlim=[0 40 0 0 0.034 0.0185 0.65 10]';
%upperlim=[250 600 600 600 0.136 0.074 2.6 180]';
%[c_state_sigma,fval]=fmincon(@objective,x_a,[],[],[],[],lowerlim,upperlim,[],options);


%% Example Use of fmincon
%Optimization options structure :
%       - Algorithm interior-point: Use the algorithm "interior-point"
%       - Display off: displays no output
options=optimset('Algorithm','interior-point','Display','iter');

 %////////Lower and upper limits///////////////////////
%lb=[0 40 0 0 0.034 0.0185 0.65 10]';     
lb = [0 40];
%ub=[250 600 600 600 0.136 0.074 2.6 180]';
ub = [250 600];

% x0 -> 
x0 = [45,200];

%///////////////// PARAMS /////////////////////////////
%X = fmincon(FUN,X0,A,B,Aeq,Beq,LB,UB,NONLCON,OPTIONS) 
%FUN - function
%X0 - initial point
%A & B - inequalities coefs 
%Aeq & Beq - equalities coefs
%LB- Low boundary
%UB - Upper boundary
%NONCLON - non linear constraints
%Options

% ///////////////// min x f(x) such that: ///////////////
% c(x) ? 0
% ceq(x) = 0
% A*x ? b
% Aeq = beq
% lb ? x ? ub
%
%starts at x0 and attempts to find a minimizer x of the function described 
% in fun subject to the linear inequalities A*x ? b. x0 can be a scalar, vector, or matrix.
%x = fmincon(fun,x0,A,b,Aeq,beq,lb,ub) defines a set of lower and upper bounds
%on the design variables in x, so that the solution is always in the range lb ? x ? ub. 
%If no equalities exist, set Aeq = [] and beq = []. If x(i) is unbounded below, 
%set lb(i) = -Inf, and if x(i) is unbounded above, set ub(i) = Inf.
%x = fmincon(fun,x0,A,b,Aeq,beq,lb,ub,nonlcon) subjects the minimization to 
%the nonlinear inequalities c(x) or equalities ceq(x) defined in nonlcon. 
%fmincon optimizes such that c(x) ? 0 and ceq(x) = 0. If no bounds exist, set lb = [] and/or ub = [].
%x = fmincon(fun,x0,A,b,Aeq,beq,lb,ub,nonlcon,options) minimizes with the 
%optimization options specified in options. Use optimoptions to set these options. 
%If there are no nonlinear inequality or equality constraints, set nonlcon = [].

%***************************************************************************
% Our case: x = fmincon(func, x0, [], [], [],[], lb, ub, [], options)
%1) A = b = [] ==> No inequalities exist
%2) Aeq = beq = [] ==> No equalities exist
%3) lb ? x ? ub
%4) nonlcon = [] ==> no nonlinear inequality or equality constraints
%5) f(x(1), x(2)) = 100*[x(2) - x(1)]^2 + [1 - x(1)]^2
'Solution: x'
fmincon(@(x)100*(x(2)-x(1)^2)^2 + (1-x(1))^2,x0,[],[],[],[],lb,ub,[],options)
[x,fval, output, lambda, grad]=fmincon(@(x)100*(x(2)-x(1)^2)^2 + (1-x(1))^2,x0,[],[],[],[],lb,ub,[],options);
x
fval
output
lambda 
grad


 %////////Without Lower and upper limits///////////////////////
% options=optimset('Algorithm','active-set','Display','off');
%[c_state_sigma,fval]=fminunc(@(x)100*(x(2)-x(1)^2)^2 + (1-x(1))^2,[-1,2], options)
%c_state_sigma1
%fval1
  