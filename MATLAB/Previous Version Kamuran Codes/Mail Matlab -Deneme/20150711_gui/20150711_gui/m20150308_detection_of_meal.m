function [meal_bolus,correction_detection,meal_detection,meal_detection_time,correction_time,correction_limit,correction_bolus]=m20150308_detection_of_meal(x_p,y,meal_detection,meal_detection_time,correction_time,correction_limit,correction_bolus,kj,body_weight,sleep,phys_act,meal_mu)
%% Calculate bolus
if x_p(3)>0.85 && meal_detection==0 && y>100 && sleep==0 && phys_act==0
    meal_detection=1;
    meal_detection_time=kj;
    correction_time=kj;
    meal_bolus=3;
    correction_limit=x_p(3)+1;
elseif x_p(3)>0.85 && meal_detection==0 && y>140 && sleep==1
    meal_detection=1;
    meal_detection_time=kj;
    correction_time=kj;
    meal_bolus=1;
    correction_limit=x_p(3)+1;
elseif x_p(3)>0.85 && meal_detection==0 && y>100 && phys_act==1
    meal_detection=1;
    meal_detection_time=kj;
    correction_time=kj;
    meal_bolus=0;
    correction_limit=x_p(3)+1;
else
    meal_bolus=0;
end

%% End of meal
if kj-meal_detection_time>6 && x_p(3)<=0.85
    meal_detection=0;
end

rr=reference_trajectory(y,100,8,meal_mu);

%% Correction bolus
if kj-meal_detection_time>=3 && x_p(3)>=correction_limit && kj-correction_time>=3 && meal_detection==1 && y>100 && sleep==0 && phys_act==0
    correction_detection=1;
    correction_limit=correction_limit+1;
    ISC=y/rr(1);
    TDD=ISC*body_weight;
    ISF=1800/TDD;
    correction_bolus=(y-rr(1))/ISF;
    correction_time=kj;
elseif kj-meal_detection_time>=3 && x_p(3)>=correction_limit && kj-correction_time>=3 && meal_detection==1 && y>140 && sleep==1
    correction_detection=1;
    correction_limit=correction_limit+1;
    ISC=y/rr(1);
    TDD=ISC*body_weight;
    ISF=1800/TDD;
    correction_bolus=(y-rr(1))/ISF;
    correction_time=kj;
elseif kj-meal_detection_time>=3 && x_p(3)>=correction_limit && kj-correction_time>=3 && meal_detection==1 && y>300 && phys_act==1
    correction_detection=1;
    correction_limit=correction_limit+1;
    ISC=y/rr(1);
    TDD=ISC*body_weight;
    ISF=1800/TDD;
    correction_bolus=(y-rr(1))/ISF;
    correction_time=kj;
else
    correction_detection=0;
end
