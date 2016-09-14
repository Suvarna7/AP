%% Connect to Java program and:
%   - Send request data command when run button is pressed
%   - Wait and read result from Java

%Create connection and connection handler
t = connect_java_usb();

%Read all samples
table_read = read_last_samples(t)

%Keep reading values:
listen_for_samples(t);

while(true)

    %REQUEST SAMPLES EVERY... 5 MIN?
    n = 5*60;
    pause(n);
    table_read = read_last_samples(t)

end
