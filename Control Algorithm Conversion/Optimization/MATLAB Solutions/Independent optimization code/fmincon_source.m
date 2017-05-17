function [X,FVAL,EXITFLAG,OUTPUT,LAMBDA,GRAD,HESSIAN] = fmincon_source(FUN,X,A,B,Aeq,Beq,LB,UB,NONLCON,options,varargin)
%FMINCON finds a constrained minimum of a function of several variables.
%   FMINCON attempts to solve problems of the form:
%       min F(X)  subject to:  A*X  <= B, Aeq*X  = Beq  (linear constraints)
%        X                     C(X) <= 0, Ceq(X) = 0    (nonlinear constraints)
%                              LB <= X <= UB            (bounds)
%                                                           
%   X=FMINCON(FUN,X0,A,B) starts at X0 and finds a minimum X to the function 
%   FUN, subject to the linear inequalities A*X <= B. FUN accepts input X and 
%   returns a scalar function value F evaluated at X. X0 may be a scalar,
%   vector, or matrix. 
%
%   X=FMINCON(FUN,X0,A,B,Aeq,Beq) minimizes FUN subject to the linear equalities
%   Aeq*X = Beq as well as A*X <= B. (Set A=[] and B=[] if no inequalities exist.)
%
%   X=FMINCON(FUN,X0,A,B,Aeq,Beq,LB,UB) defines a set of lower and upper
%   bounds on the design variables, X, so that a solution is found in 
%   the range LB <= X <= UB. Use empty matrices for LB and UB
%   if no bounds exist. Set LB(i) = -Inf if X(i) is unbounded below; 
%   set UB(i) = Inf if X(i) is unbounded above.
%
%   X=FMINCON(FUN,X0,A,B,Aeq,Beq,LB,UB,NONLCON) subjects the minimization to the 
%   constraints defined in NONLCON. The function NONLCON accepts X and returns 
%   the vectors C and Ceq, representing the nonlinear inequalities and equalities 
%   respectively. FMINCON minimizes FUN such that C(X)<=0 and Ceq(X)=0. 
%   (Set LB=[] and/or UB=[] if no bounds exist.)
%
%   X=FMINCON(FUN,X0,A,B,Aeq,Beq,LB,UB,NONLCON,OPTIONS) minimizes with the 
%   default optimization parameters replaced by values in the structure
%   OPTIONS, an argument created with the OPTIMSET function. See OPTIMSET
%   for details. Used options are Display, TolX, TolFun, TolCon,
%   DerivativeCheck, Diagnostics, FunValCheck, GradObj, GradConstr,
%   Hessian, MaxFunEvals, MaxIter, DiffMinChange and DiffMaxChange,
%   LargeScale, MaxPCGIter, PrecondBandWidth, TolPCG, TypicalX, Hessian,
%   HessMult, HessPattern, PlotFcns, and OutputFcn. Use the GradObj option 
%   to specify that FUN also returns a second output argument G that is the 
%   partial derivatives of the function df/dX, at the point X. Use the Hessian
%   option to specify that FUN also returns a third output argument H that
%   is the 2nd partial derivatives of the function (the Hessian) at the
%   point X. The Hessian is only used by the large-scale method, not the
%   line-search method. Use the GradConstr option to specify that NONLCON
%   also returns third and fourth output arguments GC and GCeq, where GC is
%   the partial derivatives of the constraint vector of inequalities C, and
%   GCeq is the partial derivatives of the constraint vector of equalities
%   Ceq. Use OPTIONS = [] as a  place holder if no options are set.
%  
%   X=FMINCON(PROBLEM) finds the minimum for PROBLEM. PROBLEM is a
%   structure with the function FUN in PROBLEM.objective, the start point
%   in PROBLEM.x0, the linear inequality constraints in PROBLEM.Aineq
%   and PROBLEM.bineq, the linear equality constraints in PROBLEM.Aeq and
%   PROBLEM.beq, the lower bounds in PROBLEM.lb, the upper bounds in 
%   PROBLEM.ub, the nonlinear constraint function in PROBLEM.nonlcon, the
%   options structure in PROBLEM.options, and solver name 'fmincon' in
%   PROBLEM.solver. Use this syntax to solve at the command line a problem 
%   exported from OPTIMTOOL. The structure PROBLEM must have all the fields.
%
%   [X,FVAL]=FMINCON(FUN,X0,...) returns the value of the objective 
%   function FUN at the solution X.
%
%   [X,FVAL,EXITFLAG]=FMINCON(FUN,X0,...) returns an EXITFLAG that describes the 
%   exit condition of FMINCON. Possible values of EXITFLAG and the corresponding 
%   exit conditions are listed below.
%
%   Both medium- and large-scale:
%     1  First order optimality conditions satisfied to the specified tolerance.
%     0  Maximum number of function evaluations or iterations reached.
%    -1  Optimization terminated by the output function.
%   Large-scale only: 
%     2  Change in X less than the specified tolerance.
%     3  Change in the objective function value less than the specified tolerance.
%   Medium-scale only:
%     4  Magnitude of search direction smaller than the specified tolerance and 
%         constraint violation less than options.TolCon.
%     5  Magnitude of directional derivative less than the specified tolerance
%         and constraint violation less than options.TolCon.
%    -2  No feasible point found.
%
%   [X,FVAL,EXITFLAG,OUTPUT]=FMINCON(FUN,X0,...) returns a structure OUTPUT with 
%   the number of iterations taken in OUTPUT.iterations, the number of function
%   evaluations in OUTPUT.funcCount, the norm of the final step in OUTPUT.stepsize, 
%   the algorithm used in OUTPUT.algorithm, the first-order optimality in 
%   OUTPUT.firstorderopt, and the  exit message in OUTPUT.message. The medium scale 
%   algorithm returns the final line search steplength in OUTPUT.lssteplength, and 
%   the large scale algorithm returns the number of CG iterations in OUTPUT.cgiterations.
%
%   [X,FVAL,EXITFLAG,OUTPUT,LAMBDA]=FMINCON(FUN,X0,...) returns the Lagrange multipliers
%   at the solution X: LAMBDA.lower for LB, LAMBDA.upper for UB, LAMBDA.ineqlin is
%   for the linear inequalities, LAMBDA.eqlin is for the linear equalities,
%   LAMBDA.ineqnonlin is for the nonlinear inequalities, and LAMBDA.eqnonlin
%   is for the nonlinear equalities.
%
%   [X,FVAL,EXITFLAG,OUTPUT,LAMBDA,GRAD]=FMINCON(FUN,X0,...) returns the value of 
%   the gradient of FUN at the solution X.
%
%   [X,FVAL,EXITFLAG,OUTPUT,LAMBDA,GRAD,HESSIAN]=FMINCON(FUN,X0,...) returns the 
%   value of the HESSIAN of FUN at the solution X.
%
%   Examples
%     FUN can be specified using @:
%        X = fmincon(@humps,...)
%     In this case, F = humps(X) returns the scalar function value F of the HUMPS function
%     evaluated at X.
%
%     FUN can also be an anonymous function:
%        X = fmincon(@(x) 3*sin(x(1))+exp(x(2)),[1;1],[],[],[],[],[0 0])
%     returns X = [0;0].
%
%   If FUN or NONLCON are parameterized, you can use anonymous functions to capture 
%   the problem-dependent parameters. Suppose you want to minimize the objective
%   given in the function myfun, subject to the nonlinear constraint mycon, where 
%   these two functions are parameterized by their second argument a1 and a2, respectively.
%   Here myfun and mycon are M-file functions such as
%
%        function f = myfun(x,a1)
%        f = x(1)^2 + a1*x(2)^2;
%
%   and
%
%        function [c,ceq] = mycon(x,a2)
%        c = a2/x(1) - x(2);
%        ceq = [];
%
%   To optimize for specific values of a1 and a2, first assign the values to these
%   two parameters. Then create two one-argument anonymous functions that capture 
%   the values of a1 and a2, and call myfun and mycon with two arguments. Finally, 
%   pass these anonymous functions to FMINCON:
%
%        a1 = 2; a2 = 1.5; % define parameters first
%        options = optimset('LargeScale','off'); % run medium-scale algorithm
%        x = fmincon(@(x)myfun(x,a1),[1;2],[],[],[],[],[],[],@(x)mycon(x,a2),options)
%
%   See also OPTIMSET, FMINUNC, FMINBND, FMINSEARCH, @, FUNCTION_HANDLE.
%   Copyright 1990-2007 The MathWorks, Inc.
%   $Revision: 1.1.6.3 $  $Date: 2007/03/15 19:26:16 $
defaultopt = struct('Display','final','LargeScale','on', ...
   'TolX',1e-6,'TolFun',1e-6,'TolCon',1e-6,'DerivativeCheck','off',...
   'Diagnostics','off','FunValCheck','off',...
   'GradObj','off','GradConstr','off',...
   'HessMult',[],...% HessMult [] by default
   'Hessian','off','HessPattern','sparse(ones(numberOfVariables))',...
   'MaxFunEvals','100*numberOfVariables',...
   'MaxSQPIter','10*max(numberOfVariables,numberOfInequalities+numberOfBounds)',...
   'DiffMaxChange',1e-1,'DiffMinChange',1e-8,...
   'PrecondBandWidth',0,'TypicalX','ones(numberOfVariables,1)',...
   'MaxPCGIter','max(1,floor(numberOfVariables/2))', ...
   'TolPCG',0.1,'MaxIter',400,'OutputFcn',[],'PlotFcns',[],...
   'RelLineSrchBnd',[],'RelLineSrchBndDuration',1,'NoStopIfFlatInfeas','off', ...
   'PhaseOneTotalScaling','off');
% If just 'defaults' passed in, return the default options in X
if nargin==1 && nargout <= 1 && isequal(FUN,'defaults')
   X = defaultopt;
   return
end
large = 'large-scale';
medium = 'medium-scale'; 
if nargin < 10, options=[];
   if nargin < 9, NONLCON=[];
      if nargin < 8, UB = [];
         if nargin < 7, LB = [];
            if nargin < 6, Beq=[];
               if nargin < 5, Aeq =[];
               end, end, end, end, end, end
problemInput = false;
if nargin == 1
    if isa(FUN,'struct')
        problemInput = true;
        [FUN,X,A,B,Aeq,Beq,LB,UB,NONLCON,options] = separateOptimStruct(FUN);
    else % Single input and non-structure.
        error('optim:fmincon:InputArg','The input to FMINCON should be either a structure with valid fields or consist of at least four arguments.' );
    end
end
if nargin < 4 && ~problemInput
  error('optim:fmincon:AtLeastFourInputs','FMINCON requires at least four input arguments.')
end
if isempty(NONLCON) && isempty(A) && isempty(Aeq) && isempty(UB) && isempty(LB)
   error('optim:fmincon:ConstrainedProblemsOnly', ...
         'FMINCON is for constrained problems. Use FMINUNC for unconstrained problems.')
end
% Check for non-double inputs
% SUPERIORFLOAT errors when superior input is neither single nor double;
% We use try-catch to override SUPERIORFLOAT's error message when input
% data type is integer.
try
    dataType = superiorfloat(X,A,B,Aeq,Beq,LB,UB);
    if ~isequal('double', dataType)
        error('optim:fmincon:NonDoubleInput', ...
            'FMINCON only accepts inputs of data type double.')
    end
catch
    error('optim:fmincon:NonDoubleInput', ...
        'FMINCON only accepts inputs of data type double.')
end
if nargout > 4
   computeLambda = 1;
else 
   computeLambda = 0;
end
caller='constr';
lenVarIn = length(varargin);
XOUT=X(:);
numberOfVariables=length(XOUT);
%check for empty X
if numberOfVariables == 0
   error('optim:fmincon:EmptyX','You must provide a non-empty starting point.');
end
switch optimget(options,'Display',defaultopt,'fast')
case {'off','none'}
   verbosity = 0;
case 'notify'
   verbosity = 1;  
case 'final'
   verbosity = 2;
case 'iter'
   verbosity = 3;   
otherwise
   verbosity = 2;
end
% Set to column vectors
B = B(:);
Beq = Beq(:);
% Find out what algorithm user wants to run: 
% line_search = false means large scale (trust region), line_search = 1 means medium scale (line search)
line_search = strcmp(optimget(options,'LargeScale',defaultopt,'fast'),'off'); 
[XOUT,l,u,msg] = checkbounds(XOUT,LB,UB,numberOfVariables);
if ~isempty(msg)
   EXITFLAG = -2;
   [FVAL,LAMBDA,GRAD,HESSIAN] = deal([]);
   
   % Create fields in the order they're created in either the medium
   % or large scale algorithms
   OUTPUT.iterations = 0;
   OUTPUT.funcCount = 0;
   OUTPUT.stepsize = [];
   if line_search
      OUTPUT.lssteplength = [];
   end
   if line_search
      OUTPUT.algorithm = 'medium-scale: SQP, Quasi-Newton, line-search';
   else
      OUTPUT.algorithm = 'large-scale: trust-region reflective Newton';
   end
   OUTPUT.firstorderopt = [];
   if ~line_search
      OUTPUT.cgiterations = []; 
   end
   OUTPUT.message = msg;
   
   X(:)=XOUT;
   if verbosity > 0
      disp(msg)
   end
   return
end
lFinite = l(~isinf(l));
uFinite = u(~isinf(u));
meritFunctionType = 0;
mtxmpy = optimget(options,'HessMult',defaultopt,'fast');
if isequal(mtxmpy,'hmult')
   warning('optim:fmincon:HessMultNameClash', ...
           ['Potential function name clash with a Toolbox helper function:n',...
            ' Use a name besides ''hmult'' for your HessMult function ton',...
            '  avoid errors or unexpected results.']);
end
diagnostics = isequal(optimget(options,'Diagnostics',defaultopt,'fast'),'on');
funValCheck = strcmp(optimget(options,'FunValCheck',defaultopt,'fast'),'on');
gradflag = strcmp(optimget(options,'GradObj',defaultopt,'fast'),'on');
hessflag = strcmp(optimget(options,'Hessian',defaultopt,'fast'),'on');
if isempty(NONLCON)
   constflag = 0;
else
   constflag = 1;
end
% Convert to inline function as needed
if ~isempty(FUN)  % will detect empty string, empty matrix, empty cell array
   [funfcn, msg] = optimfcnchk(FUN,'fmincon',length(varargin),funValCheck,gradflag,hessflag);
else
   error('optim:fmincon:InvalidFUN', ...
         ['FUN must be a function handle;n', ...
          ' or, FUN may be a cell array that contains function handles.']);
end
if constflag % NONLCON is non-empty
   gradconstflag = strcmp(optimget(options,'GradConstr',defaultopt,'fast'),'on');
   [confcn, msg] = optimfcnchk(NONLCON,'fmincon',length(varargin),funValCheck,gradconstflag,false,1);
else
   gradconstflag = false; 
   confcn{1} = '';
end
[rowAeq,colAeq]=size(Aeq);
% if only l and u then call sfminbx
if ~line_search && isempty(NONLCON) && isempty(A) && isempty(Aeq) && gradflag
   OUTPUT.algorithm = large;
% if only Aeq beq and Aeq has more columns than rows, then call sfminle
elseif ~line_search && isempty(NONLCON) && isempty(A) && isempty(lFinite) && isempty(uFinite) && gradflag ...
      && colAeq > rowAeq
   OUTPUT.algorithm = large;
elseif ~line_search
   warning('optim:fmincon:SwitchingToMediumScale', ...
   ['Large-scale (trust region) method does not currently solve this type of problem,n' ...
    ' using medium-scale (line search) instead.'])
   if isequal(funfcn{1},'fungradhess')
      funfcn{1}='fungrad';
      warning('optim:fmincon:HessianIgnored', ...
         ['Medium-scale method is a Quasi-Newton method and does not usen' ...
         'analytic Hessian. Hessian flag in options will be ignored.'])
   elseif  isequal(funfcn{1},'fun_then_grad_then_hess')
      funfcn{1}='fun_then_grad';
      warning('optim:fmincon:HessianIgnored', ...
         ['Medium-scale method is a Quasi-Newton method and does not usen' ...
         'analytic Hessian. Hessian flag in options will be ignored.'])
   end    
   hessflag = 0;
   OUTPUT.algorithm = medium;
elseif line_search
   OUTPUT.algorithm = medium;
   if issparse(Aeq) || issparse(A)
      warning('optim:fmincon:ConvertingToFull', ...
              'Cannot use sparse matrices with medium-scale method: converting to full.')
   end
   if line_search && hessflag % conflicting options
      hessflag = 0;
      warning('optim:fmincon:HessianIgnored', ...
         ['Medium-scale method is a Quasi-Newton method and does not use analytic Hessian.n' ...
         'Hessian flag in options will be ignored (user-supplied Hessian will not be used).']);      
      if isequal(funfcn{1},'fungradhess')
         funfcn{1}='fungrad';
      elseif  isequal(funfcn{1},'fun_then_grad_then_hess')
         funfcn{1}='fun_then_grad';
      end    
   end
   % else call nlconst
else
   error('optim:fmincon:InvalidOptions', ...
      'Unrecognized combination of OPTIONS flags and calling sequence.')
end
lenvlb=length(l);
lenvub=length(u);
if isequal(OUTPUT.algorithm,medium)
   %
   % Ensure starting point lies within bounds
   %
   i=1:lenvlb;
   lindex = XOUT(i)<l(i);
   if any(lindex),
      XOUT(lindex)=l(lindex)+1e-4; 
   end
   i=1:lenvub;
   uindex = XOUT(i)>u(i);
   if any(uindex)
      XOUT(uindex)=u(uindex);
   end
   X(:) = XOUT;
else
   %
   % If components of initial x not within bounds, set those components  
   % of initial point to a "box-centered" point
   %
   arg = (u >= 1e10); arg2 = (l <= -1e10);
   u(arg) = inf;
   l(arg2) = -inf;
   xinitOutOfBounds_idx = XOUT < l | XOUT > u;
   if any(xinitOutOfBounds_idx)
       XOUT = startx(u,l,XOUT,xinitOutOfBounds_idx);
       X(:) = XOUT;
   end
end
% Evaluate function
GRAD=zeros(numberOfVariables,1);
HESS = [];
switch funfcn{1}
case 'fun'
   try
      f = feval(funfcn{3},X,varargin{:});
   catch
     error('optim:fmincon:ObjectiveError', ...
            ['FMINCON cannot continue because user supplied objective function' ...
             ' failed with the following error:n%s'], lasterr)
   end
case 'fungrad'
   try
      [f,GRAD(:)] = feval(funfcn{3},X,varargin{:});
   catch 
      error('optim:fmincon:ObjectiveError', ...
           ['FMINCON cannot continue because user supplied objective function' ...
            ' failed with the following error:n%s'], lasterr)
   end
case 'fungradhess'
   try
      [f,GRAD(:),HESS] = feval(funfcn{3},X,varargin{:});
   catch
     error('optim:fmincon:ObjectiveError', ...
            ['FMINCON cannot continue because user supplied objective function' ...
             ' failed with the following error:n%s'], lasterr)
   end
case 'fun_then_grad'
   try
      f = feval(funfcn{3},X,varargin{:});
   catch
     error('optim:fmincon:ObjectiveError', ...
            ['FMINCON cannot continue because user supplied objective function' ...
             ' failed with the following error:n%s'], lasterr)
   end
   try
      GRAD(:) = feval(funfcn{4},X,varargin{:});
   catch
      error('optim:fmincon:GradError', ...
            ['FMINCON cannot continue because user supplied objective gradient function' ...
             ' failed with the following error:n%s'], lasterr)
   end
case 'fun_then_grad_then_hess'
   try
      f = feval(funfcn{3},X,varargin{:});
   catch
      error('optim:fmincon:ObjectiveError', ...
            ['FMINCON cannot continue because user supplied objective function' ...
             ' failed with the following error:n%s'], lasterr)
   end
   try
      GRAD(:) = feval(funfcn{4},X,varargin{:});
   catch
      error('optim:fmincon:GradientError', ...
            ['FMINCON cannot continue because user supplied objective gradient function' ...
             ' failed with the following error:n%s'], lasterr)     
   end
   try
      HESS = feval(funfcn{5},X,varargin{:});
   catch 
      error('optim:fmincon:HessianError', ...
            ['FMINCON cannot continue because user supplied objective Hessian function' ...
             ' failed with the following error:n%s'], lasterr)     
   end
otherwise
   error('optim:fmincon:UndefinedCallType','Undefined calltype in FMINCON.');
end
% Check that the objective value is a scalar
if numel(f) ~= 1
   error('optim:fmincon:NonScalarObj','User supplied objective function must return a scalar value.')
end
% Evaluate constraints
switch confcn{1}
case 'fun'
   try 
      [ctmp,ceqtmp] = feval(confcn{3},X,varargin{:});
      c = ctmp(:); ceq = ceqtmp(:);
      cGRAD = zeros(numberOfVariables,length(c));
      ceqGRAD = zeros(numberOfVariables,length(ceq));
   catch
      if findstr(xlate('Too many output arguments'),lasterr)
          if isa(confcn{3},'inline')
              error('optim:fmincon:InvalidInlineNonlcon', ...
                ['The inline function %s representing the constraintsn' ...
                 ' must return two outputs: the nonlinear inequality constraints andn' ...
                 ' the nonlinear equality constraints.  At this time, inline objects mayn' ...
                 ' only return one output argument: use an M-file function instead.'], ...
                    formula(confcn{3}))            
          elseif isa(confcn{3},'function_handle')
              error('optim:fmincon:InvalidHandleNonlcon', ...
                   ['The constraint function %s must return two outputs:n' ...
                    ' the nonlinear inequality constraints andn' ...
                    ' the nonlinear equality constraints.'],func2str(confcn{3}))            
          else
              error('optim:fmincon:InvalidFunctionNonlcon', ...
                   ['The constraint function %s must return two outputs:n' ...
                    ' the nonlinear inequality constraints andn' ...
                    ' the nonlinear equality constraints.'],confcn{3})
          end
      else
        error('optim:fmincon:NonlconError', ... 
            ['FMINCON cannot continue because user supplied nonlinear constraint functionn' ...
            ' failed with the following error:n%s'],lasterr)        
      end
   end
   
case 'fungrad'
   try
      [ctmp,ceqtmp,cGRAD,ceqGRAD] = feval(confcn{3},X,varargin{:});
      c = ctmp(:); ceq = ceqtmp(:);
   catch
      error('optim:fmincon:NonlconError', ... 
           ['FMINCON cannot continue because user supplied nonlinear constraint functionn' ...
            ' failed with the following error:n%s'],lasterr)  
   end
case 'fun_then_grad'
   try
      [ctmp,ceqtmp] = feval(confcn{3},X,varargin{:});
      c = ctmp(:); ceq = ceqtmp(:);
      [cGRAD,ceqGRAD] = feval(confcn{4},X,varargin{:});
   catch
      error('optim:fmincon:NonlconFunOrGradError', ... 
           ['FMINCON cannot continue because user supplied nonlinear constraint functionn' ...
            'or nonlinear constraint gradient function failed with the following error:n%s'],lasterr) 
   end
case ''
   c=[]; ceq =[];
   cGRAD = zeros(numberOfVariables,length(c));
   ceqGRAD = zeros(numberOfVariables,length(ceq));
otherwise
   error('optim:fmincon:UndefinedCalltype','Undefined calltype in FMINCON.');
end
non_eq = length(ceq);
non_ineq = length(c);
[lin_eq,Aeqcol] = size(Aeq);
[lin_ineq,Acol] = size(A);
[cgrow, cgcol]= size(cGRAD);
[ceqgrow, ceqgcol]= size(ceqGRAD);
eq = non_eq + lin_eq;
ineq = non_ineq + lin_ineq;
if ~isempty(Aeq) && Aeqcol ~= numberOfVariables
   error('optim:fmincon:WrongNumberOfColumnsInAeq','Aeq has the wrong number of columns.')
end
if ~isempty(A) && Acol ~= numberOfVariables
   error('optim:fmincon:WrongNumberOfColumnsInA','A has the wrong number of columns.')
end
if  cgrow~=numberOfVariables && cgcol~=non_ineq
   error('optim:fmincon:WrongSizeGradNonlinIneq', ...
         'Gradient of the nonlinear inequality constraints is the wrong size.')
end
if ceqgrow~=numberOfVariables && ceqgcol~=non_eq
   error('optim:fmincon:WrongSizeGradNonlinEq', ...
         'Gradient of the nonlinear equality constraints is the wrong size.')
end
if diagnostics > 0
   % Do diagnostics on information so far
   msg = diagnose('fmincon',OUTPUT,gradflag,hessflag,constflag,gradconstflag,...
      line_search,options,defaultopt,XOUT,non_eq,...
      non_ineq,lin_eq,lin_ineq,l,u,funfcn,confcn,f,GRAD,HESS,c,ceq,cGRAD,ceqGRAD);
end
% call algorithm
if isequal(OUTPUT.algorithm,medium)
   [X,FVAL,lambda,EXITFLAG,OUTPUT,GRAD,HESSIAN]=...
      nlconst(funfcn,X,l,u,full(A),B,full(Aeq),Beq,confcn,options,defaultopt, ...
      verbosity,gradflag,gradconstflag,hessflag,meritFunctionType,...
      f,GRAD,HESS,c,ceq,cGRAD,ceqGRAD,varargin{:});
   LAMBDA=lambda;
   
   
else
   if (isequal(funfcn{1}, 'fun_then_grad_then_hess') || isequal(funfcn{1}, 'fungradhess'))
      Hstr=[];
   elseif (isequal(funfcn{1}, 'fun_then_grad') || isequal(funfcn{1}, 'fungrad'))
      n = length(XOUT); 
      Hstr = optimget(options,'HessPattern',defaultopt,'fast');
      if ischar(Hstr) 
         if isequal(lower(Hstr),'sparse(ones(numberofvariables))')
            Hstr = sparse(ones(n));
         else
            error('optim:fmincon:InvalidHessPattern', ...
                  'Option ''HessPattern'' must be a matrix if not the default.')
         end
      end
   end
   
   if isempty(Aeq)
      [X,FVAL,LAMBDA,EXITFLAG,OUTPUT,GRAD,HESSIAN] = ...
         sfminbx(funfcn,X,l,u,verbosity,options,defaultopt,computeLambda,f,GRAD,HESS,Hstr,varargin{:});
   else
      [X,FVAL,LAMBDA,EXITFLAG,OUTPUT,GRAD,HESSIAN] = ...
         sfminle(funfcn,X,sparse(Aeq),Beq,verbosity,options,defaultopt,computeLambda,f,GRAD,HESS,Hstr,varargin{:});
   end
end