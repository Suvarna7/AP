function [ success ] = store_samples( device_name, samples )
%STORE_SAMPLES Store device samples in the computer

if (~isempty(samples))
    %Get current date and time
    formatOut = 'mm-dd-yy HH';
    t = datestr(clock,formatOut);
    t = t(1:end-1);
    %Get storing directory
    dir = strcat(pwd, '\USB Connection\devices_data\' );
    filename = strcat(dir, device_name,'_',t, '.csv');
    %filename = strcat(dir, device_name, '.xls')

    %xlswrite(filename,samples);
    %dlmwrite(filename, m, ',', r, c);
    %dlmwrite (filename, cell2mat(samples), '-append');
%     try
%         pre = csvread(filename);
%         samples = [pre;samples];
%     catch ex
%          warning('Problem using USB store_samples.  Assigning a value of 0.');
%     end


    %csvwrite(filename,char(samples));
    cell2csv(filename, samples, ',');

    'End writing to file'
end


end

