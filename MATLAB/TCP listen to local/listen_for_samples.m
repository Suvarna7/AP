function [ output_args ] = listen_for_samples( input_args )
%LISTEN_FOR_SAMPLES waits for incoming samples from the Java interface
% it will be waiting forever

data_to_save = [];
table_name = 'ups';
while(true)
        if (t.BytesAvailable > 0)
                %Read new data from Java and phone
                DataReceived = fscanf(t);
                var = DataReceived(1:length(DataReceived)-2)
                if (strcmp(var, 'no_more_values'))
                    %End of sample sending
                    out = 'Received JSON !'
                    assignin('base', table_name, data_to_save);
                    data_to_save = [];

                elseif (length(var)>1)
                    if (var(1:1)=='[')
                        %Handle received Data - JSON ARRAY
                        [table_name, data, json, ack] = parse_json(var);
                        data_to_save =[data_to_save; data(2:end,:)];
                        fprintf(t, ack); 

                    end

                end
        end

end


end

