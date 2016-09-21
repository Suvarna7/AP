%% Connect to Java program and:
%   - Send request data command when run button is pressed
%   - Wait and read result from Java

%Create connection and connection handler
'Press connect in Java program....'
t = connect_java_usb();
'Local connection established!'
%Create empatica and dexcom tables:
empatica = [];
dexcom = [];

sensors_tables = {dexcom; empatica};
sensors_names = {'dexcom'; 'empatica'};


%Keep reading values:
listening = true;
while (listening)
    'New algorithm iteration'
    [listening, sensors_tables] = listen_to_phone(t, sensors_names, sensors_tables);
end


