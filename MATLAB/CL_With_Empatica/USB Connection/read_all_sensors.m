function [ dexcom_data, empatica_data ] = read_all_sensors( t )
%read_all_sensors Request new samples and waits for java
%   program to send them all
%   Sensors we ask data from:
%       - Empatica
%       - Dexcom CGM

%Send request - DEXCOM
[~, dexcom_data] = read_last_samples(t, 'dexcom');

%Send request - EMPATICA
[~, empatica_data] = read_last_samples(t, 'empatica');


end

