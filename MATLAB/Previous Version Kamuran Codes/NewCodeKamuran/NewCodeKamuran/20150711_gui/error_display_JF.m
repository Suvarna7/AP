function error_summation=error_display_JF(kj,gs_f)
% kj=38
% gs_f=[285.0 285.0 285.0 285.0 285.0 285.0 285.0 285.0 285.0 285.0 285.0 285.0 285.0 285.0 285.0 285.0 285.0 285.0 285.0 285.0 200.0 198.0 185.0 180.0 165.0 164.0 158.0 150.0 144.0 140.0 132.0 125.0 122.0 119.0 116.0 114.0 110.0 106.0]
% load prevdata_error_summation error_summation D_potential
% 
%  error_summation=[0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
%  1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 NaN NaN NaN NaN NaN NaN NaN
%  NaN NaN NaN NaN NaN NaN NaN NaN NaN NaN NaN NaN NaN NaN NaN NaN
%  NaN NaN NaN NaN NaN NaN NaN NaN NaN NaN NaN NaN NaN NaN NaN NaN
%  NaN NaN NaN NaN NaN NaN NaN NaN NaN NaN NaN NaN NaN NaN NaN NaN
%  0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 25.0
%  279.9030786538565 -66.41561607577428 -69.2207753395617 -26.213992208826397 -38.773179296595174 -25.52355608569175 -32.74937260811791 -11.97590139258935 -22.524384150335067 -8.686245033963417 -8.43797146597511 -8.56536771917871 -6.727823280358862 -2.9757809822989856 -12.084465728838993 -3.942031402226135]
% 
% D_potential=[0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 64.0 8.846153846153847 22.0 6.333333333333333 94.0 14.666666666666666 10.0 12.333333333333334 17.5 7.75 7.857142857142857 17.333333333333332 16.333333333333332 15.333333333333334 22.0 10.0]
% save prevdata_error_summation error_summation D_potential


load track
[m n]=size(data_mem);
if kj<22
    error_summation=[];
    D_potential=[];
    save prevdata_error_summation error_summation D_potential
else load prevdata_error_summation error_summation D_potential

%%%%%%%%%%%%%
 ME=data_mem(10,n);
 EE=data_mem(12,n);
 UC=data_mem(11,n);
 Umax=data_mem(14,n);
 cre=log(data_mem(13,n));

 %% dangerous area error
 error=0;
 if gs_f(kj)>70
     D_potential(kj)=(200-gs_f(kj))/(gs_f(kj)-gs_f(kj-1));
     if  D_potential(kj)<4 && D_potential(kj)>0 && (-gs_f(kj-1)+gs_f(kj))>0
         error=1;
     else
         error=0;
     end
 end
 if gs_f(kj)<=200 && error~=1
     D_potential(kj)=(gs_f(kj)-70)/(gs_f(kj-1)-gs_f(kj));
     if  D_potential(kj)<4 && D_potential(kj)>0 && (-gs_f(kj-1)+gs_f(kj))<0
         error=-1;
     else
         error=0;
     end
 end
 
 if gs_f(kj)>=200
     error=1;
 else if gs_f(kj)<=70
         error=-1;
     end
 end
 
 %% Model error
 % if error~=0
 if ME>20
     Model_error=1;
 else if ME>10
         if EE>20
             Model_error=1;
         else Model_error=NaN;
         end
     else Model_error=NaN;
     end
 end
 
 %% Umax not right
 if error==1
     if UC>2 && gs_f(kj)-gs_f(kj-1)>=gs_f(kj-1)-gs_f(kj-2) && gs_f(kj)-gs_f(kj-1)>0
         if Umax<35
             Umax_error=1;
         else Umax_error=NaN;
         end
     else Umax_error=NaN;
     end
 else Umax_error=NaN;
 end
 %% costfunction ratio error
 if error~=0
     if cre<0
         ratio_error=1;
     else ratio_error=NaN;
     end
 else ratio_error=NaN;
 end
 
 %% Other
 if error~=0
     if max([Model_error;Umax_error; ratio_error ])~=1
         other=1;
     else
         other=NaN;
     end
 else other=NaN;
 end
 %% record all
 error_summation=[error_summation,[error; Model_error;Umax_error; ratio_error;other;Umax;I_me_inst]]
 D_potential
 ME
 EE
 save prevdata_error_summation error_summation D_potential ME EE
end
end