function [ connected ] = check_usb( connection )
%CHECK_USB checks if the java program is connected to the phone via USB 
%   - connection = connection to Java program

%Send request
fprintf(connection, 'usb_state'); 
ready_to_read = true;
finalTime = datenum(clock + [0, 0, 0, 0, 5, 0]);
connected = false;
 while (ready_to_read && datenum(clock) < finalTime)
      if (connection.BytesAvailable > 0)
                %Read new data from Java and phone
                DataReceived = fscanf(connection);
                var = DataReceived(1:length(DataReceived)-2);
                if (strcmp(var, 'usb_connected'))
                    connected = true;
                    ready_to_read = false;
                elseif (strcmp(var, 'usb_disconnected'))
                     connected = false;
                     ready_to_read = false;
                else
                    connected = true;
                    ready_to_read = false;
                end
      end
 end

end

