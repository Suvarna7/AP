%% Connect to Java program and:
%   - Send request data command when run button is pressed
%   - Wait and read result from Java

%Create connection and connection handler
t = connect_java_usb();

%Read all samples
table_read = read_last_samples(t)

%Keep reading values:
% data_to_save = [];
% table_name = 'ups';
while(true)
%         if (t.BytesAvailable > 0)
%                 %Read new data from Java and phone
%                 DataReceived = fscanf(t);
%                 var = DataReceived(1:length(DataReceived)-2)
%                 if (strcmp(var, 'no_more_values'))
%                     %End of sample sending
%                     out = 'Received JSON !'
%                     assignin('base', table_name, data_to_save);
%                     data_to_save = [];
% 
%                 elseif (length(var)>1)
%                     if (var(1:1)=='[')
%                         %Handle received Data - JSON ARRAY
%                         [table_name, data, json, ack] = parse_json(var);
%                         data_to_save =[data_to_save; data(2:end,:)];
%                         fprintf(t, ack); 
% 
%                     end
% 
%                 end
%         end

    %REQUEST SAMPLES EVERY... 5 MIN?
    n = 5*60;
    pause(n);
    table_read = read_last_samples(t)

end
