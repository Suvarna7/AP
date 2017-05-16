function error_summation=error_display_JF(kj,gs_f)
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
 error_summation=[error_summation,[error; Model_error;Umax_error; ratio_error;other;Umax;I_me_inst]];
 save prevdata_error_summation error_summation D_potential ME EE
end
end