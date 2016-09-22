function [ listening, sensors_out ] = listen_to_phone( t, sensors_name, sensors_tables)
%LISTEN_FOR_SAMPLES waits for incoming samples from the Java interface
% it will be waiting forever

data_to_save = [];
table_name = 'ups';
ready_command = 'dias_ready';
end_command = 'disconnect_socket';
in_process = true;
listening = true;
sensors_out = sensors_tables;


%DEXCOM: Significant value - CGM in column 6
CGM_table = 1;
CGM_value = 6;
%EMPATICA: Significat value - GSR in column 2
EMPATICA_table = 2;
Empatica_value = 2;

while(in_process)
        if (t.BytesAvailable > 0)
                %Read new data from Java and phone
                DataReceived = fscanf(t);
                var = DataReceived(1:length(DataReceived)-2);
                %Start algorithm service when READY is received:
                if (strcmp(var, ready_command))
                    %1. Request sensors values: 
                    %For each sensor:
                    for i=1:size(sensors_name)
                        data_to_save = [];
                        data_to_save =sensors_tables{i,1};

                        [table, data] = read_last_samples(t, sensors_name{i,1});
                        if (size(data_to_save)>0)
                            %Other samples - skip column names
                            data_to_save = [data_to_save;data(2:end,:)];
                        else
                            %First sample - include columns names
                            data_to_save = [data_to_save;data];
                        end
                        
                        sensors_tables{i,1} = data_to_save;
                        sensors_out = sensors_tables;

                        'New input samples:'
                        table
                        
                        assignin('base', sensors_name{i,1}, data_to_save);

                    end
                    
                    %3. Run algorithm
                    %Extract CGM value
                    cgm = sensors_tables{CGM_table,1}{end, CGM_value}

                    b_units = mock_algorithm(cgm);
                    
                    %4. Send values back
                    send_insulin_command( t, num2str(b_units), '0' );
                    
                    %5. Finish
                    in_process = false;
                   
                elseif (strcmp(var, end_command))
                    listening = false;
                    in_process = false;
                end
        end

end




end

