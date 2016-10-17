%% FDD algorithm. Kamuran Turksoy
function [fault,fault_reason]=m20160516_FDD(y,fault,fault_reason)
dcc=cell(5,1);
dcc(1,1)={[0.377777777777778,0.311111111111111,0.244444444444444,0.177777777777778,0.111111111111111,0.0444444444444444,-0.0222222222222222,-0.0888888888888889,-0.155555555555556;0.311111111111111,0.261111111111111,0.211111111111111,0.161111111111111,0.111111111111111,0.0611111111111111,0.0111111111111111,-0.0388888888888889,-0.0888888888888889;0.244444444444444,0.211111111111111,0.177777777777778,0.144444444444444,0.111111111111111,0.0777777777777778,0.0444444444444444,0.0111111111111111,-0.0222222222222222;0.177777777777778,0.161111111111111,0.144444444444444,0.127777777777778,0.111111111111111,0.0944444444444444,0.0777777777777778,0.0611111111111111,0.0444444444444444;0.111111111111111,0.111111111111111,0.111111111111111,0.111111111111111,0.111111111111111,0.111111111111111,0.111111111111111,0.111111111111111,0.111111111111111;0.0444444444444444,0.0611111111111111,0.0777777777777778,0.0944444444444444,0.111111111111111,0.127777777777778,0.144444444444444,0.161111111111111,0.177777777777778;-0.0222222222222222,0.0111111111111111,0.0444444444444444,0.0777777777777778,0.111111111111111,0.144444444444444,0.177777777777778,0.211111111111111,0.244444444444444;-0.0888888888888889,-0.0388888888888889,0.0111111111111111,0.0611111111111111,0.111111111111111,0.161111111111111,0.211111111111111,0.261111111111111,0.311111111111111;-0.155555555555556,-0.0888888888888889,-0.0222222222222222,0.0444444444444444,0.111111111111111,0.177777777777778,0.244444444444444,0.311111111111111,0.377777777777778]};
dcc(2,1)={[-0.0666666666666667,-0.0666666666666667,-0.0666666666666667,-0.0666666666666667,-0.0666666666666667,-0.0666666666666667,-0.0666666666666667,-0.0666666666666667,-0.0666666666666667;-0.0500000000000000,-0.0500000000000000,-0.0500000000000000,-0.0500000000000000,-0.0500000000000000,-0.0500000000000000,-0.0500000000000000,-0.0500000000000000,-0.0500000000000000;-0.0333333333333333,-0.0333333333333333,-0.0333333333333333,-0.0333333333333333,-0.0333333333333333,-0.0333333333333333,-0.0333333333333333,-0.0333333333333333,-0.0333333333333333;-0.0166666666666667,-0.0166666666666667,-0.0166666666666667,-0.0166666666666667,-0.0166666666666667,-0.0166666666666667,-0.0166666666666667,-0.0166666666666667,-0.0166666666666667;0,0,0,0,0,0,0,0,0;0.0166666666666667,0.0166666666666667,0.0166666666666667,0.0166666666666667,0.0166666666666667,0.0166666666666667,0.0166666666666667,0.0166666666666667,0.0166666666666667;0.0333333333333333,0.0333333333333333,0.0333333333333333,0.0333333333333333,0.0333333333333333,0.0333333333333333,0.0333333333333333,0.0333333333333333,0.0333333333333333;0.0500000000000000,0.0500000000000000,0.0500000000000000,0.0500000000000000,0.0500000000000000,0.0500000000000000,0.0500000000000000,0.0500000000000000,0.0500000000000000;0.0666666666666667,0.0666666666666667,0.0666666666666667,0.0666666666666667,0.0666666666666667,0.0666666666666667,0.0666666666666667,0.0666666666666667,0.0666666666666667]};
dcc(3,1)={[0.0606060606060606,0.0606060606060606,0.0606060606060606,0.0606060606060606,0.0606060606060606,0.0606060606060606,0.0606060606060606,0.0606060606060606,0.0606060606060606;0.0151515151515151,0.0151515151515151,0.0151515151515151,0.0151515151515151,0.0151515151515151,0.0151515151515151,0.0151515151515151,0.0151515151515151,0.0151515151515151;-0.0173160173160173,-0.0173160173160173,-0.0173160173160173,-0.0173160173160173,-0.0173160173160173,-0.0173160173160173,-0.0173160173160173,-0.0173160173160173,-0.0173160173160173;-0.0367965367965368,-0.0367965367965368,-0.0367965367965368,-0.0367965367965368,-0.0367965367965368,-0.0367965367965368,-0.0367965367965368,-0.0367965367965368,-0.0367965367965368;-0.0432900432900433,-0.0432900432900433,-0.0432900432900433,-0.0432900432900433,-0.0432900432900433,-0.0432900432900433,-0.0432900432900433,-0.0432900432900433,-0.0432900432900433;-0.0367965367965368,-0.0367965367965368,-0.0367965367965368,-0.0367965367965368,-0.0367965367965368,-0.0367965367965368,-0.0367965367965368,-0.0367965367965368,-0.0367965367965368;-0.0173160173160173,-0.0173160173160173,-0.0173160173160173,-0.0173160173160173,-0.0173160173160173,-0.0173160173160173,-0.0173160173160173,-0.0173160173160173,-0.0173160173160173;0.0151515151515151,0.0151515151515151,0.0151515151515151,0.0151515151515151,0.0151515151515151,0.0151515151515151,0.0151515151515151,0.0151515151515151,0.0151515151515151;0.0606060606060606,0.0606060606060606,0.0606060606060606,0.0606060606060606,0.0606060606060606,0.0606060606060606,0.0606060606060606,0.0606060606060606,0.0606060606060606]};
dcc(4,1)={[-0.0707070707070707,-0.0707070707070707,-0.0707070707070707,-0.0707070707070707,-0.0707070707070707,-0.0707070707070707,-0.0707070707070707,-0.0707070707070707,-0.0707070707070707;0.0353535353535354,0.0353535353535354,0.0353535353535354,0.0353535353535354,0.0353535353535354,0.0353535353535354,0.0353535353535354,0.0353535353535354,0.0353535353535354;0.0656565656565657,0.0656565656565657,0.0656565656565657,0.0656565656565657,0.0656565656565657,0.0656565656565657,0.0656565656565657,0.0656565656565657,0.0656565656565657;0.0454545454545455,0.0454545454545455,0.0454545454545455,0.0454545454545455,0.0454545454545455,0.0454545454545455,0.0454545454545455,0.0454545454545455,0.0454545454545455;0,0,0,0,0,0,0,0,0;-0.0454545454545455,-0.0454545454545455,-0.0454545454545455,-0.0454545454545455,-0.0454545454545455,-0.0454545454545455,-0.0454545454545455,-0.0454545454545455,-0.0454545454545455;-0.0656565656565657,-0.0656565656565657,-0.0656565656565657,-0.0656565656565657,-0.0656565656565657,-0.0656565656565657,-0.0656565656565657,-0.0656565656565657,-0.0656565656565657;-0.0353535353535354,-0.0353535353535354,-0.0353535353535354,-0.0353535353535354,-0.0353535353535354,-0.0353535353535354,-0.0353535353535354,-0.0353535353535354,-0.0353535353535354;0.0707070707070707,0.0707070707070707,0.0707070707070707,0.0707070707070707,0.0707070707070707,0.0707070707070707,0.0707070707070707,0.0707070707070707,0.0707070707070707]};
dcc(5,1)={[0.0979020979020978,0.0979020979020978,0.0979020979020978,0.0979020979020978,0.0979020979020978,0.0979020979020978,0.0979020979020978,0.0979020979020978,0.0979020979020978;-0.146853146853147,-0.146853146853147,-0.146853146853147,-0.146853146853147,-0.146853146853147,-0.146853146853147,-0.146853146853147,-0.146853146853147,-0.146853146853147;-0.0769230769230769,-0.0769230769230769,-0.0769230769230769,-0.0769230769230769,-0.0769230769230769,-0.0769230769230769,-0.0769230769230769,-0.0769230769230769,-0.0769230769230769;0.0629370629370630,0.0629370629370630,0.0629370629370630,0.0629370629370630,0.0629370629370630,0.0629370629370630,0.0629370629370630,0.0629370629370630,0.0629370629370630;0.125874125874126,0.125874125874126,0.125874125874126,0.125874125874126,0.125874125874126,0.125874125874126,0.125874125874126,0.125874125874126,0.125874125874126;0.0629370629370630,0.0629370629370630,0.0629370629370630,0.0629370629370630,0.0629370629370630,0.0629370629370630,0.0629370629370630,0.0629370629370630,0.0629370629370630;-0.0769230769230769,-0.0769230769230769,-0.0769230769230769,-0.0769230769230769,-0.0769230769230769,-0.0769230769230769,-0.0769230769230769,-0.0769230769230769,-0.0769230769230769;-0.146853146853147,-0.146853146853147,-0.146853146853147,-0.146853146853147,-0.146853146853147,-0.146853146853147,-0.146853146853147,-0.146853146853147,-0.146853146853147;0.0979020979020978,0.0979020979020978,0.0979020979020978,0.0979020979020978,0.0979020979020978,0.0979020979020978,0.0979020979020978,0.0979020979020978,0.0979020979020978]};

loadings=[-0.603777611908350;-0.449517377590236;0.658321139416506];
lamdas=[1.55362679418530];
lamda_rest=[0.881321380354509;0.565051825460188];
mX=[147.747455114416,-0.104431828806333,-0.0632525587215418];
sX=[31.2083122438376,3.46104725148443,0.464793933434078];
np=[1];


alpT=0.95;
alpQ=0.95;
nnn=281;
Tlimit=finv(alpT,np,nnn-np)*(np*(nnn^2-1)/(nnn*(nnn-np)));
Q1=0;Q2=0;Q3=0;
for ii=1:length(lamda_rest)
    Q1=Q1+lamda_rest(ii);
    Q2=Q2+lamda_rest(ii)^2;
    Q3=Q3+lamda_rest(ii)^3;
end
h0=1-(2*Q1*Q3/(3*Q2^2));
c1=norminv(alpQ);
if h0<0
    c1=-c1;
end
Qlimit=Q1*(1-(Q2*h0*(1-h0)/Q1^2)+c1*(2*Q2*h0^2)^0.5/Q1)^(1/h0);

Xtest=[y(end,1)...
    m20151015_sgolay_smooth_derivative_fast([y(end-4:end,1);m20160516_calculate_pred(y(end-1:end))],1,9,1,dcc{2,1})...
    m20151015_sgolay_smooth_derivative_fast([y(end-4:end,1);m20160516_calculate_pred(y(end-1:end))],2,9,2,dcc{3,1})];

nXtest=zeros(1,3);
for kk=1:3
    nXtest(1,kk)=(Xtest(1,kk)-mX(:,kk))/sX(:,kk);
end

T=(nXtest*loadings)/diag(lamdas)*(nXtest*loadings)';
Q=nXtest*(eye(3)-loadings*loadings')*nXtest';
Tcont=((nXtest*loadings)/diag(lamdas.^0.5)*(loadings)').^2;
Qcont=(nXtest*(eye(3)-loadings*loadings')).^2;
if Q>Qlimit
    fault=cat(1,fault,2);
    fault_reason=cat(1,fault_reason,Qcont);
elseif T>Tlimit
    fault=cat(1,fault,1);
    fault_reason=cat(1,fault_reason,Tcont);
    
else
    fault=cat(1,fault,0);
    fault_reason=cat(1,fault_reason,zeros(1,3));
end