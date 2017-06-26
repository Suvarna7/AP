function [ table_name, data_to_save, usb_state ] = read_last_samples( t , table_name)
%read_last_samples Request new samples and waits for java
%   program to send them all
% We request data for a certain sensor. Available sensors:
%   - 'empatica'
%   - 'dexcom'

%Send request
fprintf(t, table_name); 
done_reading = false;
usb_state = true;
init_flag = false;
data_to_save = [];
table_name = 'no_data';
global ack_data;
ack_data = '';
%Wait till we read all samples
initialTime = datenum(clock + [0, 0, 0, 0, 0, 30]);
finalTime = datenum(clock + [0, 0, 0, 0, 3, 0]);

% Break while loop if:
%   - last command is been received: ready_to_read = true
%   - initial command is not received in 10 seconds: 10 seconds passed &&
%   no initial command is received
%   - 5 minutes elapsed
 while (~done_reading && usb_state)
            %data = fread(t, 10)
            if (t.BytesAvailable > 0)
                %Read new data from Java and phone
                DataReceived = fscanf(t);
                var = DataReceived(1:length(DataReceived)-2);
                if (strcmp(var, 'first_value'))
                    'Initial message !'
                    init_flag = true;
                elseif(strcmp(var, 'no_more_values'))
                    %End of sample sending
                    'Received all messages !'
                    %assignin('base', table_name, data_to_save);
                    %data_to_save = [];
                    done_reading = true;
                    fprintf(t, ack_data);
                    %Store samples
                    %data_to_save(1:size(data_to_save,1), end)= num2cell(zeros(size(data_to_save,1), 1));
                    %data_to_save(1,end)= cellstr('ReceivedBatch');
                    %data_to_save(2, end)= cellstr('y');

                    store_samples(table_name, data_to_save);
                elseif(strcmp(var, 'usb_sync'))
                    'USB related ?'
                else
                    if (var(1:1)=='[')
                        %Handle received Data - JSON ARRAY
                        [table_name, data, json, ack_data] = extract_json(var);
                        
                        if (length(data_to_save)<1)
                            data_to_save = [data_to_save;data];
                        else
                            data_to_save =[data_to_save; data(2:end,:)];
                        end
                        %fprintf(t, ack);
                    else
                        'Received other:'
                        var

                    end

                end
            end
            
            %USB Connection
            if((datenum(clock) > initialTime && ~init_flag)|| datenum(clock) > finalTime)
                usb_state = false;
            end
end

end

