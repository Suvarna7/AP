function [ ] = send_hypo_alert( con, carbohydrates, type )
%SEND_HYPO_ALERT Send a hypo alert to the phone
%   Alert contains amount of carbohydrates and type of alert (EARLY or
%   IMMEDIATE)

hypo_json = ['{command: hypo_command, carbs: ', carbohydrates, ', type: ', type, '}'];
fprintf(con, hypo_json); 




end

