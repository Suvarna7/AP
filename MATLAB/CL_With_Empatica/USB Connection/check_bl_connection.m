function [ connected ] = check_bl_connection( con, device )
%CHECK_BL_CONNECTION checks if the device is connected via Blutooth to the
% phone
%   - con = connection (to Java program)
%   - device = device name. Current accepted names 'empatica'
if (strcmp(device, 'empatica'))
    fprintf(con, '{command: verify_device; device:empatica}'); 
end

ready_to_read = true;   
while (ready_to_read)
      if (con.BytesAvailable > 0)
                %Read new data from Java and phone
                DataReceived = fscanf(con);
                var = DataReceived(1:length(DataReceived)-2);
                if (strcmp(var, 'device_connected'))
                    connected = true;
                    ready_to_read = false;
                else
                    connected = false;
                    ready_to_read = false;
      end
 end

end

