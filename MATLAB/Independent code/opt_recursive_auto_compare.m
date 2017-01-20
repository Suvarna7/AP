
function [ Q, Y_model, P, pinvP, lamda, err]=opt_recursive_auto_compare()
%% Stand alone version of the optimiziation recursive, for debugging purposes
% It obtains Y,phi,Q_old,P_old,lamda_old,upperlim,lowerlim from an excel
% files


%% Load the result from Java
% Read Y,phi,Q_old,P_old,lamda_old,upperlim,lowerlim files
 file_location = 'C:\Users\Cat\Desktop\Java-MATLAB\outputOpt\';
% [Yin,~,~]=xlsread(strcat(file_location,'Y.xlsx'));
% [phiIN,~,~]=xlsread(strcat(file_location,'phi.xlsx'));
% [Qold,~,~]=xlsread(strcat(file_location,'Q_old.xlsx'));
% [Pold,~,~]=xlsread(strcat(file_location,'P_old.xlsx'));
% [u_lim,~,~]=xlsread(strcat(file_location,'upperlimit.xlsx'));
% [l_lim,~,~]=xlsread(strcat(file_location,'lowerlimit.xlsx'));
% [lamda_o,~,~]=xlsread(strcat(file_location,'lamda_old.xlsx'));
% 
% Y = Yin;
% phi = phiIN;
% Q_old = Qold;
% P_old = Pold;
% lamda_old = lamda_o';
% upperlim = u_lim';
% lowerlim = l_lim';
% %DEBUG - Add variables to workspace for inspection
% assignin('base', 'upperlim', upperlim);
% assignin('base', 'lowerlim', lowerlim);
% assignin('base', 'Q_old', Q_old);
% assignin('base', 'phi', phi);
% assignin('base', 'Y', Y);
% assignin('base', 'P_old', P_old);
% assignin('base', 'lamda_old', lamda_old);

%Counter to save all values of Q in an array
i = 1;
%Q_all saves:
%   - 1: i/counter 
%   - 2: V (function value)
%   - 3:26 Q 
Q_all =  zeros( 1000, 26);






%HARDCODED VALUES:
Y = [180.0];
phi= [	 
        -180.0
	 -180.0
	 -180.0
	 0.0
	 0.0

	 0.0
	 0.0
	 0.0
	 0.0
	 0.0

	 0.0
	 0.0
	 0.0
	 0.0
	 0.0

	 0.3
	 0.3
	 0.3
	 0.3
	 0.2

	 0.2
	 0.2
	 0.2
	 0.0
];


P_old =[ 
     1.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0
	 0.0	 1.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0
	 0.0	 0.0	 1.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0
	 0.0	 0.0	 0.0	 1.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0
	 0.0	 0.0	 0.0	 0.0	 1.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0
	 0.0	 0.0	 0.0	 0.0	 0.0	 1.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0
	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 1.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0
	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 1.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0
	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 1.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0
	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 1.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0
	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 1.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0
	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 1.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0
	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 1.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0
	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 1.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0
	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 1.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0
	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 1.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0
	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 1.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0
	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 1.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0
	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 1.0	 0.0	 0.0	 0.0	 0.0	 0.0
	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 1.0	 0.0	 0.0	 0.0	 0.0
	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 1.0	 0.0	 0.0	 0.0
	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 1.0	 0.0	 0.0
	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 1.0	 0.0
	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 0.0	 1.0
];

Q_old = [	 
   0.0
	 0.0
	 0.0
	 0.0
	 0.0
	 0.0
	 0.0
	 0.0
	 0.0
	 0.0
	 0.0
	 0.0
	 0.0
	 0.0
	 0.0
	 0.0
	 0.0
	 0.0
	 0.0
	 0.0
	 0.0
	 0.0
	 0.0
	 0.0
];

lamda_old = 0.5;

upperlim = [ 1.0      1.0      1.0      0.0      0.0      0.0      0.0      0.0      0.0      0.0      0.0      0.0      0.0      0.0      0.0      0.0      0.0      0.0      0.0      1.0      1.0      1.0      1.0      1.0      
];

lowerlim = [-1.0      -1.0      -1.0      -0.974082840593171      -0.932309038992748      -0.847452388537183      -0.724680039139838      -0.584206488705884      -0.448368344074227      -0.33315293623822      -0.244180789845194      -0.177141946974804      -0.122686145196158      -0.0757668499037829      -0.0494391409323673      -1.0      -1.0      -1.0      -1.0      0.0      0.0      0.0      0.0      -1.0      
    ];
%Also, load the result from Java to compare
%[Q_java,~,~]=xlsread(strcat(file_location,'Q_res.xlsx'));


%% 1. DEFINE PARAMETERS MATRIX
%Calculate P matrix and its pseudo-inverse, from he input parameters
P=(1/(lamda_old))*(P_old-(P_old*phi*pinv(lamda_old+phi'*P_old*phi)*phi'*P_old));
assignin('base', 'P', P);

pinvP=pinv(P);
%% 2. FUNCTION TO BE OPTIMIZED - V = OBJECTIVE(Q)
%Function we will be optimizing: 
    %V = (Q- Qold)'*(pseudo-inv(P)*(Q-Q_old) + (Y-phi'*Q)*(Y-phi'*Q);
 function V=objective(Q)
        V=(Q-Q_old)'*pinvP*(Q-Q_old)+(Y-phi'*Q)'*(Y-phi'*Q);
        %DEBUG: Save Q state:
        %Q_all saves:
            %   - 1: i/counter 
            %   - 2: V (function value)
            %   - 3:26 Q 
         Q_all(i, 1) = i;
         Q_all(i, 2) = V;
         Q_all (i, 4:27)=  Q;
         i = i +1;

    end
   
% Debug function - simple Q_old
    function V = debugObj(Q)
        V =  (Y-phi'*Q)'*(Y-phi'*Q);
        
    end
%% 3. CONSTRAINT FUNCTION DEFINITION - C, CEQ = CONSTRAINT(Q)
%   Function defining the constraints for our opimization:
     function [c, ceq]=constraint(Q)
        A=Q(1:3,1)';B1=Q(4:15,1)';B2=Q(16:19,1)';B3=Q(20:23,1)';C=Q(24,1)';
        %% Converting to State Space
        A_state=[-A B1(2:end) B2(2:end) B3(2:end) C;...
            1 zeros(1,20);...
            zeros(1,1) 1 zeros(1,19);...
            zeros(1,21);...
            zeros(1,3) 1 zeros(1,17);...
            zeros(1,4) 1 zeros(1,16);...
            zeros(1,5) 1 zeros(1,15);...
            zeros(1,6) 1 zeros(1,14);...
            zeros(1,7) 1 zeros(1,13);...
            zeros(1,8) 1 zeros(1,12);...
            zeros(1,9) 1 zeros(1,11);...
            zeros(1,10) 1 zeros(1,10);...
            zeros(1,11) 1 zeros(1,9);...
            zeros(1,12) 1 zeros(1,8);...
            zeros(1,21);...
            zeros(1,14) 1 zeros(1,6);...
            zeros(1,15) 1 zeros(1,5);...
            zeros(1,21);...
            zeros(1,17) 1 zeros(1,3);...
            zeros(1,18) 1 zeros(1,2);...
            zeros(1,21)];
        eigA=abs(eig(A_state));
        c=max(eigA)-0.99;
        ceq = [];
    end
function [c, ceq]=no_constraint(~)
        c=[];
        ceq = [];
end
%% 4. RUN FMINCON - ALGORITHM MINIMIZATION
%Optimization Options Reference
%Available algorithms: 'interior-point', 'active-set', 'sqp', 'trust-region-reflective')
%FinDiffRelStep=FiniteDifferenceStepSize - default 'Forward finite differences steps 
%                 delta = v.*sign'c(x).*max(abs(x),TypicalX)'
%                 v(default) is sqrt(eps)
%                 eps = 2.2204e-16
%                 TypicalX (default) =  ones = (numberofvariables,1)
%'PlotFcn', @optimplotstepsize
% options2=optimset('Algorithm','interior-point','Display','iter','FinDiffRelStep', sqrt(eps) );

options1=optimset('Algorithm','interior-point','Display','iter-detailed');


% Q = fmincon(fun,x0,A,b,Aeq,beq,lb,ub,nonlcon,options)
Q_old
[Q, fval, exitflag, output]=fmincon(@objective,Q_old,[],[],[],[],lowerlim,upperlim,@constraint,options1);%Find minimum of constrained nonlinear multivariable function:
[Q_fmin, fval, exitflag, output]=fmincon(@objective,Q_old,[],[],[],[],lowerlim,upperlim,@constraint,options1);%Find minimum of constrained nonlinear multivariable function:

%[Q, fval, exitflag, output]=fmincon_source(@objective,Q_old,[],[],[],[],[],[],@constraint,options1);%Find minimum of constrained nonlinear multivariable function:

[Q_noc, fval, exitflag, output]=fmincon(@objective,Q_old,[],[],[],[],[],[],@no_constraint,options1);%Find minimum of constrained nonlinear multivariable function:
%ERRROOOOOOR --> Warning: Large-scale (trust region) method does not currently solve this type of problem,n using medium-scale (line
% search) instead. 
[Q_noc, fval, exitflag, output]=fmincon_source(@objective,Q_old,[],[],[],[],[],[],@no_constraint,options1);%Find minimum of constrained nonlinear multivariable function:

%NOTE Constraint tolerance = 1.e-6
assignin('base', 'options', options1);
assignin('base', 'Q_values', Q_all);
%assignin('base', 'Eigen_values', Eigen_all);
%assignin('base', 'A_states', A_states);

output
fval
Q

exitflag
%Once we obtain the result of function minimization, obtain secondary
%values:
%Err
err=Y-phi'*Q;
Y_model=phi'*Q;
%Lambda values
lamda1=0.9*lamda_old+(1-0.9)*0.99;
lamda2=exp(-(err^2)/(1000));
lamda=lamda1*lamda2;
if lamda<0.005;lamda=0.005;end%Y_model




%% Plot Q results
figure;
time1 = 1:length(Q_fmin);
time2 = 1:length(Q);

plot (time2, Q_fmin, 'g', time2, Q, 'b', time2, upperlim', 'r', time2, lowerlim',  'r')
legend('Source file', 'Def MATLAB','Upperlim', 'lowerlim');

figure
plot(time1, Q_fmin,'g')
legend('Java');
end