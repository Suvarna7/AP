function main()
%MAIN 
%   Function that demonstrates usage of the APS-RTKL bridge to develop a MATLAB client application 
%
%   The usage pattern demonstrated in this file to obtain CGM data at runtime
%   is asynchronous and event-driven, where various callback functions are defined
%   and invoked as associated events are raised by the ReceiverManager. This approach is
%   likely best suited for use in a MATLAB GUI application.
%
%   The ReceiverManager interface also supports more synchronous, 
%   or "on-demand" usage where a client can request, or poll for, CGM data. 
%   For example, MATLAB timers may be utilized to set up a regular polling 
%   See the GetCGMData(receiver_id) define below for example of how to
%   poll/request for CGM data

warning('off', 'MATLAB:NET:AddAssembly:nameConflict');

global dll_path;
global dll_name;
global manager;

%these can be defined in a separate settings file ...
dll_name = 'DexCom.ReceiverTools.dll';
[~, current_dir] = system('cd');
dll_path = strcat(current_dir, '\..\Bin');

import DexCom.Receiver.*; %scope usage of the DexCom.Receiver package

try  
    %instantiate receiver manager/facade, REQUIRED
    manager = ReceiverManager(dll_path, dll_name);

    %initialize manager to load the dev kit DLL and install the receiver
    %device driver, if necessary
    try
        manager.Initialize(); %REQUIRED
    catch inner
        disp('Unable to initialize receiver manager. Exiting ...')
        
        rethrow(inner)
    end
            
    % wire up manager events, as needed. None are required. Event handlers
    % will be invoked when the manager raises any of these events, as
    % demonstrated below using internal functions
    addlistener(manager, 'ReceiverConnected', @OnReceiverConnected);
    addlistener(manager, 'ReceiverDisconnected', @OnReceiverDisconnected);
    addlistener(manager, 'ScannerStoppedListening', @OnScannerStoppedListening);

    % receiver events
    addlistener(manager, 'ReceiverNeedsCalibration', @OnReceiverNeedsCalibration);
    addlistener(manager, 'NewEstimatedGlucoseRecord', @OnNewEstimatedGlucoseRecord);
    addlistener(manager, 'NewMeterRecord', @OnNewMeterRecord);
    addlistener(manager, 'NewSettingsRecord', @OnNewSettingsRecord);
    addlistener(manager, 'NewInsertionRecord', @OnNewInsertionRecord);
    addlistener(manager, 'InitialSynchronizationCompleted', @OnInitialSynchronizationCompleted);
    addlistener(manager, 'IncrementalSynchronizationCompleted', @OnIncrementalSynchronizationCompleted);
    addlistener(manager, 'ReceiverStoppedListening', @OnReceiverStoppedListening);
    addlistener(manager, 'ScannerExceptionOccurred', @OnScannerExceptionOccurred);
    addlistener(manager, 'ReceiverExceptionOccurred', @OnReceiverExceptionOccurred);
    addlistener(manager, 'ReceiverPropertyChanged', @OnReceiverPropertyChanged);
    
    disp('Ready to start scannning for receivers ...')
 
    %start scanning for receiver connects/disconnects
    manager.RequestStartScanningForReceivers(); %asynchronous
    
    %connected receivers should now be detected and will automatically
    %retrieve the contents of the receiver databases  
    
    pause() %keeps function from exiting to allow for receipt of receiver data 
catch e
    disp(e.message)
        
    if (isa(e, 'NET.NetException'))
        if ~isempty(e.ExceptionObject)
            disp(e.ExceptionObject)
        end
    else
        if ~isempty(e.cause)
            disp(e.cause)
        end
    end
end

%the explicit delete operation programmatically disconnects receivers and 
%shuts down the manager's receiver scanning operation. Encapulsated .NET 
%are explicitly disposed as well

delete(manager);  %Not required, but highly recommended
     
disp('Finished')

end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%event handlers
function OnReceiverConnected(manager, arg)
    assert(~isempty(arg.ID))
    
    try
        assert(~isempty(arg.SerialNumber))
        
        disp(sprintf('Receiver %s connected.', arg.SerialNumber))
    catch e2
        disp('Empty serial number')
        disp(sprintf('COM port validity: %d', manager.GetIsReceiverComPortValid(arg.ID)))
        
        disp(e2.message)
    end
    
    try
        serial_number = manager.GetReceiverSerialNumber(arg.ID);
        assert(isequal(serial_number, arg.SerialNumber))
        
        firmware_header = manager.GetReceiverFirmwareHeader(arg.ID);
        assert(~isempty(firmware_header))
        
        assert(manager.GetIsReceiverComPortValid(arg.ID))
      
        offset = manager.GetPCReceiverTimeOffset(arg.ID)*24*60;
            
        disp(sprintf('Receiver %s has a PC-Receiver time offset of %1.2g minutes', arg.SerialNumber, offset))
    catch ie
        disp(sprintf('Receiver %s: Exception occurred upon connection: %s', arg.SerialNumber, ie.message))
    end
end

function OnReceiverDisconnected(~, arg)
    assert(~isempty(arg.ID))
    assert(~isempty(arg.SerialNumber))
    
    disp(sprintf('Receiver %s disconnected', arg.SerialNumber))
end

function OnScannerStoppedListening(~, ~)
    disp('Scanner stopped listening unexpectedly ...')
end

function OnReceiverNeedsCalibration(~, arg)
    assert(~isempty(arg.ID))
    assert(~isempty(arg.SerialNumber))
    
    disp(sprintf('Receiver %s needs to be calibrated', arg.SerialNumber))
end

function OnNewEstimatedGlucoseRecord(manager, arg)
    assert(~isempty(arg.ID))
    assert(~isempty(arg.SerialNumber))
    
    egv_record = arg.EstimatedGlucoseRecord;
    assert(~isempty(egv_record));
        
    %for now, suppress display of DisplayOnly values, although these records will
    %still show up in record histories, e.g. manager.GetReceiverEstimatedGlucoseRecords()
    %ReceiverManager does not currently suppress broadcast or retrieval of
    %any glucose records recorded by the receiver, pending a client
    %application's decision on how to handle the different scenarios.
    if ~(egv_record.IsDisplayOnly) 
        state = 'Valid';
        
        if ~(isempty(egv_record.SpecialValue))
            state = egv_record.SpecialValue; %e.g. OutOfCal, Aberration, etc.
        end
        
        units = manager.GetReceiverGlucoseDisplayUnits(arg.ID);
        disp(sprintf('Receiver %s has a new estimated glucose record of %g %s recorded at %s. State = %s', ...
            arg.SerialNumber, egv_record.GlucoseValue, units, datestr(egv_record.DisplayTime), state))
    end
end

function OnNewMeterRecord(manager, arg)
    assert(~isempty(arg.ID))
    assert(~isempty(arg.SerialNumber))
    
    meter_record = arg.MeterRecord;
    assert(~isempty(meter_record));
       
    units = manager.GetReceiverGlucoseDisplayUnits(arg.ID);
    disp(sprintf('Receiver %s has a new meter record of %g %s recorded at %s', ...
            arg.SerialNumber, meter_record.GlucoseValue, units, datestr(meter_record.MeterDisplayTime)))
end

function OnNewSettingsRecord(manager, arg)
    assert(~isempty(arg.ID))
    assert(~isempty(arg.SerialNumber))    
    
    settings_record = arg.SettingsRecord;
    assert(~isempty(settings_record))
       
    units = manager.GetReceiverGlucoseDisplayUnits(arg.ID);
    disp(sprintf('Receiver %s has a new settings record recorded at %s. Low = %g %s, High = %g %s', ...
            arg.SerialNumber, ...
            datestr(settings_record.DisplayTime), ...
            settings_record.LowAlarmLevelValue, units, ...
            settings_record.HighAlarmLevelValue, units))
end

function OnNewInsertionRecord(~, arg)
    assert(~isempty(arg.ID))
    assert(~isempty(arg.SerialNumber))
        
    insertion_record = arg.InsertionRecord;
    assert(~isempty(insertion_record))
    
    disp(sprintf('Receiver %s has a new insertion record recorded at %s. Sensor inserted = %d, state = %s', ...
            arg.SerialNumber, datestr(insertion_record.DisplayTime), insertion_record.IsInserted, insertion_record.SessionState))
end

function OnInitialSynchronizationCompleted(~, arg) 
    assert(~isempty(arg.ID))
    assert(~isempty(arg.SerialNumber))
        
    disp(sprintf('Receiver %s completed its initial synchronization', arg.SerialNumber))
end

function OnIncrementalSynchronizationCompleted(~, arg) 
    assert(~isempty(arg.ID))
    assert(~isempty(arg.SerialNumber))
    
    %do nothing
end

function OnReceiverStoppedListening(~, arg) %receiver exited or aborted unexpectedly
    assert(~isempty(arg.ID))
    assert(~isempty(arg.SerialNumber))
    
    disp(sprintf('Receiver %s stopped listening unexpectedly.', arg.SerialNumber)) 
end

function OnScannerExceptionOccurred(~, arg)
    disp(sprintf('Exception on scanner: at %s: %s', datestr(now), arg.Exception.message))
end

function OnReceiverExceptionOccurred(~, arg)
    assert(~isempty(arg.ID))
    assert(~isempty(arg.SerialNumber))
    assert(~isempty(arg.Exception))
    
    disp(sprintf('Exception on receiver %s at %s: %s', arg.SerialNumber, datestr(now), arg.Exception.message))
end

function OnReceiverPropertyChanged(~, arg)
    assert(~isempty(arg.ID))
    assert(~isempty(arg.SerialNumber))
    assert(~isempty(arg.PropertyName))
    
    %do nothing
end

%example method that demonstates synchronous retrieval of CGM data from the
%receiver manager
function PollReceivers(lookback_in_days)
    global manager;
    
    try
        %scan for attached receivers, creates/caches receiver contexts
        receivers = manager.ScanForReceivers();
    
        if (isempty(receivers))
            disp('No receivers detected')
            return;
        end
        
        for ireceiver=1:length(receivers)    
            receiver = receivers(ireceiver);
            
            manager.SynchronizeReceiver(receiver.ID, false);
            
            transmitter_id = manager.GetReceiverTransmitterId(receiver.ID);
            system_time = manager.GetReceiverSystemTime(receiver.ID);
            display_time = manager.GetReceiverDisplayTime(receiver.ID);
            
            disp(sprintf('Synchronized receiver %s. Transmitter: %s System Time : %s Display Time %s', ...
                receiver.SerialNumber, transmitter_id, datestr(system_time), datestr(display_time)))
            
            %get everything in a single struct. This can be time-consuming, 
            %such as when the receiver has many glucose records. 
            data = manager.GetReceiverData(receiver.ID); %does not currently support time-filtering, and does not currently cache any records internally

            %OR retrieve in pieces
            data.Id = receiver.ID;
            data.SerialNumber = manager.GetReceiverSerialNumber(receiver.ID);
            data.CurrentEstimatedGlucoseRecord = manager.GetReceiverCurrentEstimatedGlucoseRecord(receiver.ID); 
            data.CurrentMeterRecord = manager.GetReceiverCurrentMeterRecord(receiver.ID); 
            data.CurrentSettingsRecord = manager.GetReceiverCurrentSettingsRecord(receiver.ID);
            data.CurrentInsertionRecord = manager.GetReceiverCurrentInsertionRecord(receiver.ID);
            data.PCReceiverTimeOffset = manager.GetPCReceiverTimeOffset(receiver.ID);
        
            if ~(lookback_in_days > 0)
                data.EstimatedGlucoseRecords = manager.GetReceiverEstimatedGlucoseRecords(receiver.ID); %can be time-consuming
                data.MeterRecords = manager.GetReceiverMeterRecords(receiver.ID);
                data.SettingsRecords = manager.GetReceiverSettingsRecords(receiver.ID);
                data.InsertionRecords = manager.GetReceiverInsertionRecords(receiver.ID);
            else    
                %to improve performance, we can apply a time-filter to the
                %retrieval of records, (e.g. get only last 7 days worth)
                filter = now - lookback_in_days;

                value.EstimatedGlucoseRecords = manager.GetReceiverEstimatedGlucoseRecordsSince(receiver.ID, filter); %can be relatively time-consuming
                value.MeterRecords = manager.GetReceiverMeterRecordsSince(receiver.ID, filter);
                value.SettingsRecords = manager.GetReceiverSettingsRecordsSince(receiver.ID, filter);
                value.InsertionRecords = manager.GetReceiverInsertionRecordsSince(receiver.ID, filter);
            end
        
            disp(sprintf('Receiver %s has %g glucose, %g meter, %g settings, %g insertion records', ...
                data.SerialNumber, ...
                length(data.EstimatedGlucoseRecords), ...
                length(data.MeterRecords), ...
                length(data.SettingsRecords), ...
                length(data.InsertionRecords)))
        end
    catch e
        disp(sprintf('Exception occurred while retrieving cgm data from manager: %s', e.message))
        
        if (isa(e, 'NET.NetException'))
            if ~isempty(e.ExceptionObject)
                disp(e.ExceptionObject)
            end
        else
            if ~isempty(e.cause)
                disp(e.cause)
            end
        end
    end 
end