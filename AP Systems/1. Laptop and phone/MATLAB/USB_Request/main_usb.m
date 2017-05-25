% USB Connect
'Press connect in Java'
usb_con = connect_java_usb();
'(USB connection stablished)'
global empatica_data;
%Get Empatica from USB
if(check_usb(usb_con) && check_bl_connection(usb_con, 'empatica'))
    [table, new_samples] = read_last_samples(usb_con, 'empatica');
    %Save as global variable
    empatica_data = [empatica_data;new_samples(2:end,:)];
end

%Loop to get Empatica data every 5 minutes:
five_min = datenum(clock + [0, 0, 0, 0, 5, 0]);
while 1
    if (datenum(clock) > five_min)
        [table, new_samples] = read_last_samples(usb_con, 'empatica');
        %Save as global variable
        five_min = datenum(clock + [0, 0, 0, 0, 5, 0]);
    end
end



