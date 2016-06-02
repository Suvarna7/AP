function [hypo_alarm,carb_amount,carb_type,phase,phase_old,repeated_immediate_alarm]=m20150510_hypo_algorithm_sleep(g,g_prediction,hypo_threshold,phase_old,hypo_slope_degree)
if g<70 && hypo_slope_degree<0
    phase=5;
    repeated_immediate_alarm=0;
    if abs(phase-phase_old)>=1
        hypo_alarm=1;
        phase_old=phase;
    else
        hypo_alarm=1;
        repeated_immediate_alarm=1;
    end
    if hypo_slope_degree<-60
        carb_amount={'24'};
        carb_type={'Carbs'};
    elseif -60<=hypo_slope_degree && hypo_slope_degree<-30
        carb_amount={'20'};
        carb_type={'Carbs'};
    elseif -30<=hypo_slope_degree && hypo_slope_degree<0
        carb_amount={'20'};
        carb_type={'Carbs'};
    end
elseif 70<=g && g<90 && g_prediction<hypo_threshold && hypo_slope_degree<0
    phase=4;
    repeated_immediate_alarm=0;
    if abs(phase-phase_old)>=1
        hypo_alarm=1;
        phase_old=phase;
    else
        hypo_alarm=0;
    end
    if hypo_slope_degree<-60
        carb_amount={'16'};
        carb_type={'Carbs'};
    elseif -60<=hypo_slope_degree && hypo_slope_degree<-30
        carb_amount={'16'};
        carb_type={'Carbs'};
    elseif -30<=hypo_slope_degree && hypo_slope_degree<0
        carb_amount={'12'};
        carb_type={'Carbs'};
    end
elseif 90<=g && g<110 && g_prediction<hypo_threshold && hypo_slope_degree<0
    phase=3;
    repeated_immediate_alarm=0;
    if abs(phase-phase_old)>=1
        hypo_alarm=1;
        phase_old=phase;
    else
        hypo_alarm=0;
    end
   if hypo_slope_degree<-60
        carb_amount={'12'};
        carb_type={'Carbs'};
    elseif -60<=hypo_slope_degree && hypo_slope_degree<-30
        carb_amount={'8'};
        carb_type={'Carbs'};
    elseif -30<=hypo_slope_degree && hypo_slope_degree<0
        carb_amount={'4'};
        carb_type={'Carbs'};
    end
elseif 110<=g && g<130 && g_prediction<hypo_threshold && hypo_slope_degree<0
    phase=2;
    repeated_immediate_alarm=0;
    if abs(phase-phase_old)>=1
        hypo_alarm=1;
        phase_old=phase;
    else
        hypo_alarm=0;
    end
    if hypo_slope_degree<-60
        carb_amount={'8'};
        carb_type={'Carbs'};
    elseif -60<=hypo_slope_degree && hypo_slope_degree<-30
        carb_amount={'4'};
        carb_type={'Carbs'};
    elseif -30<=hypo_slope_degree && hypo_slope_degree<0
        carb_amount={'4'};
        carb_type={'Carbs'};
    end
elseif 130<=g && g<180 && g_prediction<hypo_threshold && hypo_slope_degree<0
    phase=1;
    repeated_immediate_alarm=0;
    if abs(phase-phase_old)>=1
        hypo_alarm=1;
        phase_old=phase;
    else
        hypo_alarm=0;
    end
     if hypo_slope_degree<-60
        carb_amount={'0'};
        carb_type={'Carbs'};
    elseif -60<=hypo_slope_degree && hypo_slope_degree<-30
        carb_amount={'0'};
        carb_type={'Carbs'};
    elseif -30<=hypo_slope_degree && hypo_slope_degree<0
        carb_amount={'0'};
        carb_type={'Carbs'};
    end
else
    phase=0;
    repeated_immediate_alarm=0;
    hypo_alarm=0;
    carb_amount={'0'};
    carb_type={'None'};
end