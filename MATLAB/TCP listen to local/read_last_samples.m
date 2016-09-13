function [ table_name ] = read_last_samples( t )
%read_last_samples Request new samples and waits for java
%   program to send them all

%Send request
fprintf(t, 'read_values'); 
ready_to_read = true;
data_to_save = [];
table_name = 'ups';
%Wait till we read all samples
 while (ready_to_read)
            %data = fread(t, 10)
            if (t.BytesAvailable > 0)
                %Read new data from Java and phone
                DataReceived = fscanf(t);
                var = DataReceived(1:length(DataReceived)-2);
                if (strcmp(var, 'no_more_values'))
                    %End of sample sending
                    out = 'Received JSON !'
                    assignin('base', table_name, data_to_save);
                    data_to_save = [];
                    ready_to_read = false;


                else
                    if (var(1:1)=='[')
                        %Handle received Data - JSON ARRAY
                        [table_name data json ack] = parse_json(var);
                        if (length(data_to_save)<1)
                            data_to_save = [data_to_save;data];
                        else
                            data_to_save =[data_to_save; data(2:end,:)];
                        end
                        fprintf(t, ack);

                    end

                end
            end
end

end

