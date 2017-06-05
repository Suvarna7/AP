function [ output_args ] = send_insulin_command( t, bolus_value, basal )
%SEND INSULIN COMMAND function to send a bolus value or a basal suspend, resume
%   to the phone
%   t - the tcp connection to java
%   bolus - bolus amount
%   basal - 1(resume) or 0 (suspend)

%Prepare JSON object to send
bolus_value
insulin_json = ['{command: insulin_command, bolus: ', bolus_value, ', basal: ', basal, '}']
fprintf(t, insulin_json); 



end

