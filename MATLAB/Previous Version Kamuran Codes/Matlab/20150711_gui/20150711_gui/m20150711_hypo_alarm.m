%% HEA algorithm. Kamuran Turksoy
function [hypo_threshold,hypo_slope_degree,...
    hypo_alarm,carb_amount,carb_type,hypo_phase,hypo_phase_old,repeated_immediate_alarm]=m20150711_hypo_alarm(hypo_threshold,hypo_slope_degree,...
    hypo_alarm,carb_amount,carb_type,hypo_phase,hypo_phase_old,repeated_immediate_alarm,gs,kj,g_prediction,phys_act,sleep)

if phys_act(kj,1)==1
    hypo_threshold(kj,1)=100
else
    hypo_threshold(kj,1)=70
end

if kj>5
    hypo_slope_line=polyfit([0 5 10 15 20]',gs(kj-4:kj,1),1)
    hypo_slope_degree(kj,1)=radtodeg(atan(hypo_slope_line(1)))
else
    hypo_slope_degree(kj,1)=-45
end

kj
%% The rest is written based on subjects preferences. It must be changed before each experiments based on information from Jennifer. 
if phys_act(kj,1)==1 %Carb suggestions during exercise
    [hypo_alarm(kj,1),carb_amount(kj,1),carb_type(kj,1),hypo_phase(kj,1),hypo_phase_old(kj,1),repeated_immediate_alarm(kj,1)]=m20150510_hypo_algorithm_exercise(gs(kj,1),g_prediction(6,kj),hypo_threshold(kj,1),hypo_phase_old(kj-1,1),hypo_slope_degree(kj,1));
elseif sleep(kj,1)==1 %Carb suggestions during sleep
    [hypo_alarm(kj,1),carb_amount(kj,1),carb_type(kj,1),hypo_phase(kj,1),hypo_phase_old(kj,1),repeated_immediate_alarm(kj,1)]=m20150510_hypo_algorithm_sleep(gs(kj,1),g_prediction(6,kj),hypo_threshold(kj,1),hypo_phase_old(kj-1,1),hypo_slope_degree(kj,1));
else %Carb suggestions during day
    [hypo_alarm(kj,1),carb_amount(kj,1),carb_type(kj,1),hypo_phase(kj,1),hypo_phase_old(kj,1),repeated_immediate_alarm(kj,1)]=m20150510_hypo_algorithm(gs(kj,1),g_prediction(6,kj),hypo_threshold(kj,1),hypo_phase_old(kj-1,1),hypo_slope_degree(kj,1));
end