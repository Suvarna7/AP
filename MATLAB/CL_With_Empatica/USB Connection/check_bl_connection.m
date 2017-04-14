function [ connected ] = check_bl_connection( con, device )
%CHECK_BL_CONNECTION checks if the device is connected via Blutooth to the
% phone
%   - con = connection (to Java program)
%   - device = device name. Current accepted names 'empatica'
if (strcmp(device, 'empatica'))
    fprintf(con, '{command: verify_device; device:empatica}'); 
end

ready_to_read = true;   
finalTime = datenum(clock + [0, 0, 0, 0, 1, 0]);
connected = false;

while (ready_to_read && datenum(clock) < finalTime)
      if (con.BytesAvailable > 0)
                %Read new data from Java and phone
                DataReceived = fscanf(con);
                var = DataReceived(1:length(DataReceived)-2);
                if (strfind(var, 'device_connected'))
                    connected = true;
                    ready_to_read = false;
                elseif (strfind(var, 'device_disconnected'))
                    connected = false;
                    ready_to_read = false;
                else
                    connected = true;
                    ready_to_read = false;
                        
      end
 end

end

