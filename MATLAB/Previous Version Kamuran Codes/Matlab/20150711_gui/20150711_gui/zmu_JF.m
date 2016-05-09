%%%%%%%%% zero mean uniunit function Jianyuan Feng 2.11.2015%%%%%%%%%%%%%%%
function Y=zmu_JF(X)
[m,n]=size(X);
Y=(X-ones(m,n)*diag(mean(X)))*inv(diag(std(X)));
end