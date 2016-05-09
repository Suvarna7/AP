function [c_state_sigma,fval]=m20141215_sigma_const(X_n,p1,x_a,DD)
lowerlim=[0 40 0 0 0.034 0.0185 0.65 10]';
upperlim=[250 600 600 600 0.136 0.074 2.6 180]';

X_n=[0.09273680247274323
 142.43608745282637
 -9.09564993011939E-8
 3.098636113808439E-8
 0.06800017315152491
 0.03700019008578548
 1.3000002222313223
 19.105572870053386]

x_a=[0.09273725182172585            146.82000046944435            1.4516292528888533E-7            1.7536228986124187E-22            0.06800025902248848            0.03699979674776825            1.017157432659038            20.00000065934651  ]'

p1=eye(8)

DD='off'
options = optimoptions(@fmincon,'Algorithm','trust-region-reflective','GradObj','on','GradConstr','on')

%options = optimoptions('fmincon','Algorithm','trust-region-reflective','GradObj','On');
%options=optimset('Algorithm','trust-region-reflective','Display',DD);
[c_state_sigma,fval]=fmincon(@objective,x_a,[],[],[],[],lowerlim,upperlim,[],options);

    function V=objective(Q)
        V=(Q-X_n)'*p1*(Q-X_n);
    end
end