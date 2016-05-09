function result = minimumfind ()
lowerlim=[0 40 0 0 0.034 0.0185 0.65 10]';
upperlim=[250 600 600 600 0.136 0.074 2.6 180]';
x_a=[100 25]'
fun = @(x)100*(x(2)-x(1)^2)^2 + (1-x(1))^2;
%options=optimset('Algorithm','trust-region-reflective');
GradObj = 'On'
Algorithm = 'trust-region-reflective'
options=optimset('Algorithm',Algorithm,'GradObj',GradObj);
[result]=fmincon(fun,x_a,[],[],[],[]);

end