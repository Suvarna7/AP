function [ connected ] = check_usb( connection )
%CHECK_USB Summary of this function goes here
%   Detailed explanation goes here

%Send request
fprintf(connection, 'usb_state'); 
ready_to_read = true;
 while (ready_to_read)
      if (connection.BytesAvailable > 0)
                %Read new data from Java and phone
                DataReceived = fscanf(connection);
                var = DataReceived(1:length(DataReceived)-2);
                if (strcmp(var, 'usb_connected'))
                    connected = true;
                    ready_to_read = false;
                else
                    connected = false;
                    ready_to_read = false;
      end
 end

end

