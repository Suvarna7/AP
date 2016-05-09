%% Read armband data. Kamuran Turksoy
function [armband_data_with_time,ee,phys_act,sleep,gsr]=m20150711_get_armband_data(armband_data_with_time,ee,phys_act,sleep,gsr)
    [arm_data,~,arm_data_with_time]=xlsread('D:\Phd\Research\Kamuran`s Code\Matlab\JDRF17_04222015 TO 04272015_ARMBAND.xls');% reads the last downloaded armband data
    armband_data_with_time=[armband_data_with_time;arm_data_with_time];% Updates the stored armband data
    arm_data(any(isnan(arm_data),2),:) = [];% removes missing rows
    arm_data=arm_data(end,[1:11 13 14 15]); % removes variables that currently are not used
  
    
    ee=cat(1,ee,arm_data(1,14)); % update energy expenditure
    phys_act=cat(1,phys_act,arm_data(1,13)); % update physical activity
    if ee(end,1)>14 % confirm physical activity with energy expenditure
        phys_act(end,1)=1;
    end
    sleep=cat(1,sleep,arm_data(end,12)); % update sleep
    sleep_time=clock; % check time
    if sleep_time(1,4)>23 || sleep_time(1,4)<7 % confirm sleep with time. (11PM-7AM is defined to be sleep time)
        sleep(end,1)=1;
    end
    gsr=cat(1,gsr,arm_data(end,11)); % update galvanic skin response