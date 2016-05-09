function [ins,IOB_pred,umaxx,total_daily_unit,insulin_sensitivity_factor]=controller_ins(g_prediction,L,bolus_insulin,basal_insulin,minimum_basal,reference_glucose,Nu,st,body_weight,insulin_sensitivity_constant,flag_constrains)
   
 g_prediction=[165.0914198596229
 148.66785773787748
 149.25810384094262
 133.67369380265436
 134.99877493697707
 120.15687237175679
 122.13901894316567
 107.9683602017182]

L=[ -0.753935976564861 -0.32354774086665794 0.0 0.0 0.0 0.0 0.0 0.0
 -0.6341976143938773 -0.753935976564861 -0.32354774086665794 0.0 0.0 0.0 0.0 0.0
 -1.0307606269205858 -0.6341976143938773 -0.753935976564861 -0.32354774086665794 0.0 0.0 0.0 0.0
 -0.9977852991586773 -1.0307606269205858 -0.6341976143938773 -0.753935976564861 -0.32354774086665794 0.0 0.0 0.0
 -1.2906908646668043 -0.9977852991586773 -1.0307606269205858 -0.6341976143938773 -0.753935976564861 -0.32354774086665794 0.0 0.0
 -1.188699843914927 -1.2906908646668043 -0.9977852991586773 -1.0307606269205858 -0.6341976143938773 -0.753935976564861 -0.32354774086665794 0.0
 -1.394818369857497 -1.188699843914927 -1.2906908646668043 -0.9977852991586773 -1.0307606269205858 -0.6341976143938773 -0.753935976564861 -0.32354774086665794
 -1.2205688283902476 -1.394818369857497 -1.188699843914927 -1.2906908646668043 -0.9977852991586773 -1.0307606269205858 -0.6341976143938773 -0.753935976564861]

bolus_insulin=[0.0
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
 0.0]

basal_insulin=[0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0
 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0]

minimum_basal=0;

reference_glucose=[146.5
 128.25
 119.125
 114.5625
 112.28125
 111.140625
 110.5703125
 110.28515625]

Nu=8;
st=5;

body_weight=85.0;

insulin_sensitivity_constant=[1.1269038898267776
 1.1592035691062572
 1.2529536523898646
 1.1668189311742878
 1.2023269685453009
 1.0811246776033228
 1.104627600136028
 0.978992675650475]

flag_constrains=0;

    global umaxx_global
    umaxx_global=basal_insulin(:,end);
    function V=objective(u)
        u
        Y=L*u+g_prediction;
        temparray2=[basal_insulin(1,end);u]
        du=diff([basal_insulin(1,end);u])
        total_daily_unit=body_weight*insulin_sensitivity_constant;
        insulin_sensitivity_factor=1800./total_daily_unit;
        f1=(Y-reference_glucose)'*(Y-reference_glucose)
        dutrans=du'
        diagins=diag(insulin_sensitivity_factor)
        dunottrans=du
        f2=((du'*diag(insulin_sensitivity_factor)*du))
        V=(Y-reference_glucose)'*(Y-reference_glucose)+((du'*diag(insulin_sensitivity_factor)*du))
    end
    function [c, ceq]=constraint(u)
        IOB_pred=zeros(Nu,1);
        for ii=1:Nu
            if ii==1
                IOB_pred(ii,1)=m20150711_calculate_IOB(bolus_insulin,basal_insulin(1,:));
            else
                IOB_pred(ii,1)=m20150711_calculate_IOB([bolus_insulin;zeros(ii-1,1)],[basal_insulin(1,:)';u(1:ii-1)]);
            end
        end
        total_daily_unit=body_weight*insulin_sensitivity_constant;
        insulin_sensitivity_factor=1800./total_daily_unit;
        umax=((g_prediction(1:Nu)-reference_glucose(1:Nu))./insulin_sensitivity_factor);
        umaxx=max(minimum_basal,min(25,(umax-IOB_pred)*(60/st))); % max of pump 25
        %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
       insulin_max=35;
        if flag_constrains==1%%
            
            if umaxx(1)*1.5<=insulin_max
                umaxx(1)=umaxx(1)*1.5;
            else
                umaxx(1)=insulin_max;
            end
            if umaxx(1)*1.5<2
                umaxx(1)=2;
            end
        else if flag_constrains==-1
                umaxx(1)=minimum_basal;
            end
        end
        %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        umin=minimum_basal*ones(Nu,1)
        umaxx
        umin
        u
        c=[u-umaxx;umin-u]
        ceq=[];
        umaxx_global=umaxx
    end
girmeden=min(basal_insulin(:,end),umaxx_global)
options=optimset('Algorithm','active-set','Display','off');
%ins=fmincon(@objective,min(basal_insulin(:,end),umaxx_global),[],[],[],[],[],[],@constraint,options);
 ins=fmincon(@objective,min(basal_insulin(:,end),umaxx_global),[],[],[],[],[],[],[],options)
% ins=round(ins/0.025)*0.025 % 0.025 is due to pump

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%CPA%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%module%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% load controller_limit umaxx_account
% umaxx_account=[umaxx_account,umaxx];
% save controller_limit umaxx umin  umaxx_account L g_prediction
end
