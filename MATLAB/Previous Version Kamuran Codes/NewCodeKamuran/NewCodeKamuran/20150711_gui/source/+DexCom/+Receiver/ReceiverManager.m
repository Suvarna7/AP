classdef ReceiverManager < handle
%   Public class that provides a means for run-time communication with DexCom receivers
%
%   This class represents a facade whereby MATLAB software can effectively 
%   integrate with one or more DexCom receivers. Access to receiver data 
%   can be synchronous (on-demand/polling) and/or asynchronous
%   (event-based/callback). Behind the facade, a ReceiverManager utilizes
%   a custom .NET assembly/DLL along with the MATLAB .NET external 
%   %interface to achieve direct communcation with a receiver, which 
%   includes the retrieval of historical receiver data and the
%   broadcast of new CGM/settings data.

%   Copyright 2012 DexCom, Inc.

    %private properties
    properties (SetAccess = private, GetAccess = private)
        Initialized = false;
        Scanner
        Receivers = [];
    end
    
    %readonly properties
    properties (SetAccess = private, GetAccess = public)
        DLLPath
        DLLName
    end
    
    %public events
    events
        ReceiverConnected
        ReceiverDisconnected
        ScannerStoppedListening
        ReceiverNeedsCalibration
        NewEstimatedGlucoseRecord
        NewMeterRecord
        NewSettingsRecord
        NewInsertionRecord
        InitialSynchronizationCompleted
        IncrementalSynchronizationCompleted
        ReceiverStoppedListening
        ScannerExceptionOccurred
        ReceiverExceptionOccurred
        ReceiverPropertyChanged
    end
    
    %public methods
    methods 
        %manager constructor
        function this = ReceiverManager(dll_path, dll_name)
            this.DLLPath = dll_path;
            this.DLLName = dll_name;
        end
        
        %manager destructor. Stops/disposes receiver scanner, stops communication
        %with any attached receivers, disposes contexts
        function delete(this)
            if ~(isempty(this.Scanner))
                if (this.Scanner.IsRunning)
                    this.Scanner.ClearAllEventsBeforeExit();
                    this.Scanner.Exit();
                    
                    this.Scanner.Dispose();
                    this.Scanner = [];
                end
            end
            
            this.RemoveReceivers();
        end
           
        %method that loads receiver DLL and checks for/installs receiver
        %device driver
        function Initialize(this) %this function may throw .NET or custom MATLAB exceptions
            import DexCom.Receiver.*;
            
            if ~(this.Initialized)
                ReceiverUtils.LoadDLL(this.DLLPath, this.DLLName);
                ReceiverUtils.LoadDLL(this.DLLPath, 'EnumHelper.dll');
                
                %dll successfully loaded
                ReceiverUtils.InstallDriverIfNotInstalled();
                    
                this.Scanner = DexCom.ReceiverTools.ReceiverScanner();
                
                %could add a public dependent property if a client needs to
                %adjust the default interval. Same goes for a receiver's
                %scan interval
                this.Scanner.Interval = System.TimeSpan.FromSeconds(1.0); %1.0 is the default in the DLL ...
                
                addlistener(this.Scanner, 'ReceiverContextCreatedEvent', @this.OnReceiverContextCreated);
                addlistener(this.Scanner, 'ReceiverContextRemovedEvent', @this.OnReceiverContextRemoved);
                addlistener(this.Scanner, 'UnhandledExceptionDuringScanning', @this.OnUnhandledExceptionDuringScanning);
                addlistener(this.Scanner, 'StateChangedEvent', @this.OnScannerStateChanged);
                
                this.Initialized = true;
            end
        end
                  
        %performs async request to start listening for receiver connects/disconnects
        function RequestStartScanningForReceivers(this) 
            if ~(isempty(this.Scanner))
                if (this.Scanner.IsReady)
                    this.Scanner.RunInBackground();
                else
                    %nothing for now
                end
            end
        end
      
        %Detect for attached/detached receivers
        function value = ScanForReceivers(this)
            attached_devices = DexCom.Receiver.ReceiverUtils.GetAttachedReceivers(); %List<DexCom.ReceiverApi.DeviceRegistryInfo>
            
            if (attached_devices.Count < length(this.Receivers)) 
                %receiver was detached
                for ireceiver=1:length(this.Receivers)
                    try
                        receiver = this.Receivers(ireceiver);
                    
                        if ~(isempty(receiver))
                            device_info = receiver.RegistryInfo;
                            
                            found_device = false;
                            for idevice=0:attached_devices.Count-1
                                if (device_info == attached_devices.Item(idevice))
                                    found_device = true;
                                    break;
                                end
                            end
                            
                            if ~(found_device)
                                delete(this.Receivers(ireceiver));
                                this.Receivers(ireceiver) = [];
                            end
                        end
                    catch e
                        %for now
                        continue;
                    end
                end
            elseif (attached_devices.Count > length(this.Receivers))       
                %receiver was attached
                for idevice=0:attached_devices.Count-1             
                    try
                        temporary_context = DexCom.ReceiverTools.ReceiverContext(attached_devices.Item(idevice));
                
                        receiver_id = char(temporary_context.ReceiverId.ToString());
                        
                        if (isempty(this.GetReceiver(receiver_id)))
                            receiver = DexCom.Receiver.Receiver(temporary_context); %create MATLAB receiver
                            this.Receivers = [this.Receivers; receiver]; %add instance to MATLAB receivers cache 
                        else
                            temporary_context.Dispose();
                        end
                    catch e
                        %for now
                        continue;
                    end
                end
            else
                assert(attached_devices.Count == length(this.Receivers))
            end
            
            value = this.GetReceivers();
        end
    
        %manually retrieves and caches records not previously read/cached from a
        %receiver
        function SynchronizeReceiver(this, id, fire_events)
            import DexCom.Receiver.*;
            
            receiver = this.GetReceiver(id);
            
            if ~(isempty(receiver))
                receiver.ReadDatabaseRecords(fire_events);
            end
        end
        
        %receiver support/facade methods
        function value = GetReceivers(this)
            value = [];
            
            if ~(isempty(this.Receivers))
                value(length(this.Receivers)).ID = [];
                value(length(this.Receivers)).SerialNumber = [];
                                
                for i=1:length(this.Receivers)
                    receiver = this.Receivers(i);
                    
                    value(i).ID = receiver.ID; %already a MATLAB string
                    value(i).SerialNumber = char(receiver.SerialNumber);
                end
            else
                %nothing for now
            end
        end
        
        %receiver serial number
        function value = GetReceiverSerialNumber(this, id)
            value = [];
            
            receiver = this.GetReceiver(id);
            
            if ~(isempty(receiver))
                value = char(receiver.SerialNumber);
            else
                %nothing for now
            end
        end

        %receiver's transmitter id
        function value = GetReceiverTransmitterId(this, id)
            value = [];
            
            receiver = this.GetReceiver(id);
            
            if ~(isempty(receiver))
                value = char(receiver.CurrentTransmitterId);
            else
                %nothing for now
            end
        end
        
        %offset in time between the host clock and the receiver display
        %time
        function value = GetPCReceiverTimeOffset(this, id)
            value = [];
            
            receiver = this.GetReceiver(id);
            
            if ~(isempty(receiver))
                value = receiver.PCTimeOffset;
            else
                %nothing for now
            end
        end
        
        %retrieves date and time from receiver's internal clock
        function value = GetReceiverSystemTime(this, id)
            import DexCom.Common.*;
            
            value = [];
            
            receiver = this.GetReceiver(id);
            
            if ~(isempty(receiver))
                value = Tools.ConvertDateTimeToMatlabDays(receiver.GetSystemTime()); %datenum
            end
        end
        
        %retrieves date and time according to a receiver's display
        function value = GetReceiverDisplayTime(this, id)
            import DexCom.Common.*;
            
            value = [];
            
            receiver = this.GetReceiver(id);
            
            if ~(isempty(receiver))
                value = Tools.ConvertDateTimeToMatlabDays(receiver.GetDisplayTime()); %datenum
            end
        end
        
        %retrieves receiver's firmware header
        function value = GetReceiverFirmwareHeader(this, id)
            import DexCom.Receiver.*;
            
            value = [];
            
            receiver = this.GetReceiver(id);
            
            if ~(isempty(receiver))
                value = ReceiverManager.MapFirmwareHeader(receiver.FirmwareHeader);
            end
        end

        %retrieves receiver's glucose display units
        function value = GetReceiverGlucoseDisplayUnits(this, id)
            value = [];
            
            receiver = this.GetReceiver(id);
            
            if ~(isempty(receiver))
                value = char(receiver.GlucoseDisplayUnits);
            end
        end
        
        %retrieves receiver's COM port validity
        function value = GetIsReceiverComPortValid(this, id)
            value = [];
            
            receiver = this.GetReceiver(id);
            
            if ~(isempty(receiver))
                value = receiver.IsComPortValid;
            end
        end
        
        %retrieves receiver's most recent glucose record (within last 5
        %minutes)
        function value = GetReceiverCurrentEstimatedGlucoseRecord(this, id)
            import DexCom.Receiver.*;
            
            value = [];
            
            receiver = this.GetReceiver(id);
            
            if ~(isempty(receiver))
                record = receiver.CurrentEstimatedGlucoseRecord; %EstimatedGlucoseRecord
                               
                value = ReceiverManager.MapEstimatedGlucoseRecord(record);
            else
                %nothing for now
            end
        end
        
        %retrieves receiver's most recent meter record
        function value = GetReceiverCurrentMeterRecord(this, id)
            import DexCom.Receiver.*;
            
            value = [];
            
            receiver = this.GetReceiver(id);
            
            if ~(isempty(receiver))
                record = receiver.CurrentMeterRecord; %MeterRecord
                
                value = ReceiverManager.MapMeterRecord(record);
            else
                %nothing for now
            end
        end
        
        %retrieves receiver's most recent settings record
        function value = GetReceiverCurrentSettingsRecord(this, id)
            import DexCom.Receiver.*;
            
            value = [];
            
            receiver = this.GetReceiver(id);
            
            if ~(isempty(receiver))
                record = receiver.CurrentSettingsRecord; %SettingsRecord
                
                value = ReceiverManager.MapSettingsRecord(record);
            else
                %nothing for now
            end
        end
        
        %retrieves receiver's most recent insertion record
        function value = GetReceiverCurrentInsertionRecord(this, id)
            import DexCom.Receiver.*;
            
            value = [];
            
            receiver = this.GetReceiver(id);
            
            if ~(isempty(receiver))
                record = receiver.CurrentInsertionRecord; %SettingsRecord
                
                value = ReceiverManager.MapInsertionTimeRecord(record);
            else
                %nothing for now
            end
        end
        
        %retrieves receiver's history of glucose records
        function value = GetReceiverEstimatedGlucoseRecords(this, id)
            import DexCom.Receiver.*;
            
            value = [];
            
            receiver = this.GetReceiver(id);
            
            if ~(isempty(receiver))
                queue = receiver.EstimatedGlucoseRecords; %AsynchronousQueue
                
                value = ReceiverManager.MapEstimatedGlucoseRecords(queue);
            else
                %nothing for now
            end
        end
        
        %retrieves receiver's history of estimated glucose records since
        %the given time
        function value = GetReceiverEstimatedGlucoseRecordsSince(this, id, timestamp)
            import DexCom.Receiver.*;
            
            value = [];
            
            receiver = this.GetReceiver(id);
            
            if ~(isempty(receiver))
                queue = receiver.GetEstimatedGlucoseRecordsSince(timestamp); %List
                
                value = ReceiverManager.MapEstimatedGlucoseRecords(queue);
            else
                %nothing for now
            end
        end
        
        %retrieves receiver's history of meter records
        function value = GetReceiverMeterRecords(this, id)
            import DexCom.Receiver.*;
            
            value = [];
            
            receiver = this.GetReceiver(id);
            
            if ~(isempty(receiver))
                queue = receiver.MeterRecords; %AsynchronousQueue
                
                value = ReceiverManager.MapMeterRecords(queue);
            else
                %nothing for now
            end
        end
        
        %retrieves receiver's history of meter records since the given time
        function value = GetReceiverMeterRecordsSince(this, id, timestamp)
            import DexCom.Receiver.*;
            
            value = [];
            
            receiver = this.GetReceiver(id);
            
            if ~(isempty(receiver))
                queue = receiver.GetMeterRecordsSince(timestamp); %List
                
                value = ReceiverManager.MapMeterRecords(queue);
            else
                %nothing for now
            end
        end
        
        %retrieves receiver's history of settings records
        function value = GetReceiverSettingsRecords(this, id)
            import DexCom.Receiver.*;
            
            value = [];
            
            receiver = this.GetReceiver(id);
            
            if ~(isempty(receiver))
                queue = receiver.SettingsRecords; %AsynchronousQueue
                
                value = ReceiverManager.MapSettingsRecords(queue);
            else
                %nothing for now
            end
        end

        %retrieves receiver's history of settings records since the given time
        function value = GetReceiverSettingsRecordsSince(this, id, timestamp)
            import DexCom.Receiver.*;
            
            value = [];
            
            receiver = this.GetReceiver(id);
            
            if ~(isempty(receiver))
                queue = receiver.GetSettingsRecordsSince(timestamp); %List
                
                value = ReceiverManager.MapSettingsRecords(queue);
            else
                %nothing for now
            end
        end

        %retrieves receiver's history of insertion records
        function value = GetReceiverInsertionRecords(this, id)
            import DexCom.Receiver.*;
            
            value = [];
            
            receiver = this.GetReceiver(id);
            
            if ~(isempty(receiver))
                queue = receiver.InsertionRecords; %AsynchronousQueue
                
                value = ReceiverManager.MapInsertionTimeRecords(queue);
            else
                %nothing for now
            end
        end

        %retrieves receiver's history of insertion records since the given time
        function value = GetReceiverInsertionRecordsSince(this, id, timestamp)
            import DexCom.Receiver.*;
            
            value = [];
            
            receiver = this.GetReceiver(id);
            
            if ~(isempty(receiver))
                queue = receiver.GetInsertionRecordsSince(timestamp); %List
                
                value = ReceiverManager.MapInsertionTimeRecords(queue);
            else
                %nothing for now
            end
        end
        
        %synchronous. retrieves packet (struct) of receiver's most recent CGM data
        function value = GetReceiverData(this, id)
            value = [];
            
            if ~(isempty(id))
                cgm_data.Id = id;
                cgm_data.SerialNumber = this.GetReceiverSerialNumber(id);
                cgm_data.CurrentEstimatedGlucoseRecord = this.GetReceiverCurrentEstimatedGlucoseRecord(id);
                cgm_data.CurrentMeterRecord = this.GetReceiverCurrentMeterRecord(id);
                cgm_data.CurrentSettingsRecord = this.GetReceiverCurrentSettingsRecord(id);
                cgm_data.CurrentInsertionRecord = this.GetReceiverCurrentInsertionRecord(id);
                cgm_data.EstimatedGlucoseRecords = this.GetReceiverEstimatedGlucoseRecords(id);
                cgm_data.MeterRecords = this.GetReceiverMeterRecords(id);
                cgm_data.SettingsRecords = this.GetReceiverSettingsRecords(id);
                cgm_data.InsertionRecords = this.GetReceiverInsertionRecords(id);
                cgm_data.PCReceiverTimeOffset = this.GetPCReceiverTimeOffset(id);
            
                value = cgm_data;
            else
                %nothing for now
            end
        end
    end
    
    %private methods
    methods (Access = private)
        %change of manager's operational state. If manager
        %aborted/exited, function raises the ScannerStoppedListening event 
        % and attempts to reset and restart the manager (scanner).
        function OnScannerStateChanged(this, scanner, arg) 
            import DexCom.Common.*;
            
            if (arg.CurrentState == LongRunningOperationState.Aborted)
                exception = scanner.AbortedException;
                
                if ~(isempty(exception))
                    this.HandleScannerException(scanner, exception);
                end
            end
            
            if (arg.CurrentState == LongRunningOperationState.Aborted || ...
                    arg.CurrentState == LongRunningOperationState.Exited)
                
                notify(this, 'ScannerStoppedListening');
            end
        end

        %receiver connect. Fires ReceiverConnected event
        function OnReceiverContextCreated(this, ~, context)
            import DexCom.Receiver.*;
            
            receiver_id = char(context.ReceiverId.ToString());
            
            if (isempty(this.GetReceiver(receiver_id)))
                receiver = Receiver(context);
                this.Receivers = [this.Receivers; receiver]; %add instance to receivers cache 
            else
                context.Dispose(); %for now. should probably throw an exception
            end
            
            addlistener(context, 'NewEstimatedGlucoseRecordEvent', @this.OnNewEstimatedGlucoseRecord);
            addlistener(context, 'NewMeterRecordEvent', @this.OnNewMeterRecord);
            addlistener(context, 'NewSettingsRecordEvent', @this.OnNewSettingsRecord);
            addlistener(context, 'NewInsertionTimeRecordEvent', @this.OnNewInsertionRecord);
            addlistener(context, 'InitialBackgroundSynchronizationCompleted', @this.OnInitialSynchronizationCompleted);
            addlistener(context, 'DatabaseRecordsSynchronized', @this.OnIncrementalSynchronizationCompleted);
            addlistener(context, 'UnhandledExceptionDuringSynchronization', @this.OnUnhandledExceptionDuringSynchronization);
            addlistener(context, 'StateChangedEvent', @this.OnReceiverStateChanged); %handler invokes GetReceiver
            addlistener(context, 'PropertyChanged', @this.OnReceiverPropertyChanged);
            
            notify(this, 'ReceiverConnected', ReceiverEventArgs(context));
            
            receiver.RequestRun();
        end
        
        %receiver disconnect. Fires ReceiverDisconnected event
        function OnReceiverContextRemoved(this, ~, context)
            import DexCom.Receiver.*;
            
            notify(this, 'ReceiverDisconnected', ReceiverEventArgs(context));
            
            receiver = this.GetReceiver(char(context.ReceiverId.ToString()));
            
            if ~(isempty(receiver))
                this.RemoveReceiver(receiver.ID);
            else
                %nothing for now
            end
        end
        
        %completion of initial sync with receiver after connect. Fires
        %InitialSynchronizationCompleted event
        function OnInitialSynchronizationCompleted(this, context, ~)
            import DexCom.Receiver.*;
            
            notify(this, 'InitialSynchronizationCompleted', ReceiverEventArgs(context));
        end
        
        %completion of incremental sync with receiver after connect. Fires
        %IncrementalSynchronizationCompleted event
        function OnIncrementalSynchronizationCompleted(this, context, ~)
            import DexCom.Receiver.*;
            
            notify(this, 'IncrementalSynchronizationCompleted', ReceiverEventArgs(context));
        end        
        
        %new egv reported by receiver. Fires NewEstimatedGlucoseRecord
        %event
        function OnNewEstimatedGlucoseRecord(this, context, record)
            import DexCom.Receiver.*;
            
            args = ReceiverEventArgs(context);
            args.EstimatedGlucoseRecord = ReceiverManager.MapEstimatedGlucoseRecord(record);
            
            notify(this, 'NewEstimatedGlucoseRecord', args);
                        
            %check to see if out of cal
            if ~(isempty(record.SpecialValue))
                if (strcmp(char(record.SpecialValue), 'SensorOutOfCal'))
                    notify(this, 'ReceiverNeedsCalibration', ReceiverEventArgs(context));
                end
            end
        end

        %new meter record reported by receiver. Fires NewMeterRecord
        %event
        function OnNewMeterRecord(this, context, record)
            import DexCom.Receiver.*;
            
            args = ReceiverEventArgs(context);
            args.MeterRecord = ReceiverManager.MapMeterRecord(record);
            
            notify(this, 'NewMeterRecord', args);
        end
        
        %new settings record reported by receiver. Fires NewSettingsRecord
        %event
        function OnNewSettingsRecord(this, context, record)
            import DexCom.Receiver.*;
            
            args = ReceiverEventArgs(context);
            args.SettingsRecord = ReceiverManager.MapSettingsRecord(record);
            
            notify(this, 'NewSettingsRecord', args);
        end
          
        %new insertion record reported by receiver. Fires NewInsertionRecord
        %event
        function OnNewInsertionRecord(this, context, record)
            import DexCom.Receiver.*;
            
            args = ReceiverEventArgs(context);
            args.InsertionRecord = ReceiverManager.MapInsertionTimeRecord(record);
            
            notify(this, 'NewInsertionRecord', args);
        end
        
        %scanner reports an unhandled exception. Fires ScannerExceptionOccurred event
        function OnUnhandledExceptionDuringScanning(this, scanner, exception)
            this.HandleScannerException(scanner, exception);
        end
        
        %receiver reports an unhandled exception during sync. Fires
        %ReceiverExceptionOccurred event
        function OnUnhandledExceptionDuringSynchronization(this, context, exception)
            this.HandleReceiverException(context, exception);
        end
        
        %change of receiver's operational state. If receiver
        %aborted/exited, function raises the ReceiverStoppedListening event 
        % and attempts to reset and restart the receiver (context).
        function OnReceiverStateChanged(this, context, arg)
            import DexCom.Common.*;
            
            if (arg.CurrentState == LongRunningOperationState.Aborted)
                exception = context.AbortedException;
                 
                if ~(isempty(exception))
                    this.HandleReceiverException(context, exception);
                end
            end
            
            if (arg.CurrentState == LongRunningOperationState.Aborted || ...
                    arg.CurrentState == LongRunningOperationState.Exited)
                notify(this, 'ReceiverStoppedListening', DexCom.Receiver.ReceiverEventArgs(context));
            end
        end
        
        %event handler for receiver property changes
        function OnReceiverPropertyChanged(this, context, property)
            import DexCom.Receiver.*;
            
            args = ReceiverEventArgs(context);
            args.PropertyName = char(property.PropertyName);
                                
            notify(this, 'ReceiverPropertyChanged', args);
        end
        
        %utility methods
        function receiver = GetReceiver(this, id)
            receiver = [];
            
            if ~(isempty(this.Receivers))
                ireceiver = find(strcmp({this.Receivers.ID}, id));
           
                if ~(isempty(ireceiver))
                    receiver = this.Receivers(ireceiver);
                else
                    %nothing for now
                end
            end
        end

        %stops communication with all receivers, disposes receiver contexts
        function RemoveReceivers(this)
            for i = 1:length(this.Receivers)
                delete(this.Receivers(i));
            end
            
            this.Receivers = [];
        end
        
        %stops communication with requested receivers disposes receiver
        %context
        function RemoveReceiver(this, id)
            ireceiver = find(strcmp({this.Receivers.ID}, id));
            
            if ~(isempty(ireceiver))
                delete(this.Receivers(ireceiver));
                
                this.Receivers(ireceiver) = [];
            else
                %nothing for now
            end
        end

        %Wraps scanner-related .NET exceptions in MATLAB exception. Fires
        %the ScannerExceptionOccurred event
        function HandleScannerException(this, ~, exception)
            import DexCom.Receiver.*
            
            ne = NET.NetException('DexCom:ReceiverManager:HandleScannerException', ...
                                    char(exception.Message), ...
                                    exception);
                       
            args = ScannerEventArgs();
            args.Exception = ne;
            
            notify(this, 'ScannerExceptionOccurred', args);
        end
        
        %Wraps receiver-related .NET exceptions in MATLAB exception. Fires
        %the ReceiverExceptionOccurred event
        function HandleReceiverException(this, context, exception)
            import DexCom.Receiver.*
            
            ne = NET.NetException('DexCom:ReceiverManager:HandleReceiverException', ...
                                    char(exception.Message), ...
                                    exception);
                                
            args = ReceiverEventArgs(context);
            args.Exception = ne;
                                
            notify(this, 'ReceiverExceptionOccurred', args);
        end
    end
    
    %static methods
    methods (Static = true) 
        %maps a List<EstimateGlucoseRecord> to an array of MATLAB structs
        function value = MapEstimatedGlucoseRecords(queue)
            import DexCom.Receiver.*;
            
            value = [];
            
            if (queue.Count > 0)
                value(queue.Count).RecordNumber = [];
                value(queue.Count).SystemTime = [];
                value(queue.Count).DisplayTime = [];
                value(queue.Count).GlucoseValue = [];
                value(queue.Count).SpecialValue = [];
                value(queue.Count).IsDisplayOnly = [];
                
                imapped = 1;
                
                if (isa(queue, 'System.Collections.Generic.List<DexCom*ReceiverTools*EstimatedGlucoseRecord>'))
                    first = 0;
                    last = queue.Count - 1;
                    
                    for i = first:last
                        record = queue.Item(i);
                        
                        value(imapped) = ReceiverManager.MapEstimatedGlucoseRecord(record);
                        
                        imapped = imapped + 1;
                    end
                elseif (isa(queue, 'DexCom.Common.AsynchronousQueue<DexCom*ReceiverTools*EstimatedGlucoseRecord>'))
                    first = queue.LogicalFirstIndex;
                    last = queue.LogicalLastIndex - 1;

                    for i = first:last
                         record = queue.GetAtLogicalIndex(i);  
                         
                         value(imapped) = ReceiverManager.MapEstimatedGlucoseRecord(record);
                         
                         imapped = imapped + 1;
                    end
                end
            end
        end
        
        %maps a DexCom.Common.EstimatedGlucoseRecord to a MATLAB struct
        function value = MapEstimatedGlucoseRecord(record)
            import DexCom.Common.*;
            
            value = [];
            
            if ~(isempty(record))
                value.RecordNumber = record.RecordNumber;
                value.SystemTime = Tools.ConvertDateTimeToMatlabDays(record.SystemTime); %datenum
                value.DisplayTime = Tools.ConvertDateTimeToMatlabDays(record.DisplayTime); %datenum
                value.GlucoseValue = record.Value; %UInt16
                value.SpecialValue = char(record.SpecialValue); %indicates cal/aberrant state
                value.IsDisplayOnly = record.IsDisplayOnly;
            end
        end
        
        %maps a List<MeterRecord> to an array of MATLAB structs
        function value = MapMeterRecords(queue)
            import DexCom.Receiver.*;
            
            value = [];
            
            if (queue.Count > 0)
                value(queue.Count).RecordNumber = [];
                value(queue.Count).MeterSystemTime = [];
                value(queue.Count).MeterDisplayTime = [];
                value(queue.Count).SystemTime = [];
                value(queue.Count).DiplayTime = [];
                value(queue.Count).GlucoseValue = [];
                
                imapped = 1;
                
                if (isa(queue, 'System.Collections.Generic.List<DexCom*ReceiverTools*MeterRecord>'))
                    first = 0;
                    last = queue.Count - 1;
                    
                    for i = first:last
                        record = queue.Item(i);
                        
                        value(imapped) = ReceiverManager.MapMeterRecord(record);
                        
                        imapped = imapped + 1;
                    end
                elseif (isa(queue, 'DexCom.Common.AsynchronousQueue<DexCom*ReceiverTools*MeterRecord>'))
                    first = queue.LogicalFirstIndex;
                    last = queue.LogicalLastIndex - 1;

                    for i = first:last
                         record = queue.GetAtLogicalIndex(i);  
                         
                         value(imapped) = ReceiverManager.MapMeterRecord(record);
                         
                         imapped = imapped + 1;
                    end
                end
            end
        end
        
        %maps a DexCom.Common.MeterRecord to a MATLAB struct
        function value = MapMeterRecord(record)
            import DexCom.Common.*;
            
            value = [];
            
            if ~(isempty(record))
                value.RecordNumber = record.RecordNumber;
                value.MeterSystemTime = Tools.ConvertDateTimeToMatlabDays(record.MeterSystemTime); %datenum
                value.MeterDisplayTime = Tools.ConvertDateTimeToMatlabDays(record.MeterDisplayTime); %datenum
                value.SystemTime = Tools.ConvertDateTimeToMatlabDays(record.SystemTime); %datenum
                value.DiplayTime = Tools.ConvertDateTimeToMatlabDays(record.DisplayTime); %datenum
                value.GlucoseValue = record.Value; %UInt16
            end
        end
        
        %maps a List<SettingsRecord> to an array of MATLAB structs
        function value = MapSettingsRecords(queue)
            import DexCom.Receiver.*;
            
            value = [];
            
            if (queue.Count > 0)
                value(queue.Count).RecordNumber = [];
                value(queue.Count).SystemTime = [];
                value(queue.Count).DisplayTime = [];
                value(queue.Count).HighAlarmLevelValue = [];
                value(queue.Count).IsHighAlarmEnabled = [];
                value(queue.Count).LowAlarmLevelValue = [];
                value(queue.Count).IsLowAlarmEnabled = [];
                value(queue.Count).TransmitterId = [];
                value(queue.Count).IsBlinded = [];
                value(queue.Count).IsTwentyFourHourTime = [];
                value(queue.Count).TimeLossOccurred = [];
                
                imapped = 1;
                
                if (isa(queue, 'System.Collections.Generic.List<DexCom*ReceiverTools*SettingsRecord>'))
                    first = 0;
                    last = queue.Count - 1;
                    
                    for i = first:last
                        record = queue.Item(i);
                        
                        value(imapped) = ReceiverManager.MapSettingsRecord(record);
                        
                        imapped = imapped + 1;
                    end
                elseif (isa(queue, 'DexCom.Common.AsynchronousQueue<DexCom*ReceiverTools*SettingsRecord>'))
                    first = queue.LogicalFirstIndex;
                    last = queue.LogicalLastIndex - 1;

                    for i = first:last
                         record = queue.GetAtLogicalIndex(i);  
                         
                             value(imapped) = ReceiverManager.MapSettingsRecord(record);
                         
                            imapped = imapped + 1;
                    end
                end
            end
        end
        
        %maps a DexCom.Common.SettingsRecord to a MATLAB struct
        function value = MapSettingsRecord(record)
            import DexCom.Common.*;
            
            value = [];
            
            if ~(isempty(record))
                value.RecordNumber = record.RecordNumber;
                value.SystemTime = Tools.ConvertDateTimeToMatlabDays(record.SystemTime); %datenum
                value.DisplayTime = Tools.ConvertDateTimeToMatlabDays(record.DisplayTime); %datenum
                value.HighAlarmLevelValue = record.HighAlarmLevelValue; %Int16
                value.IsHighAlarmEnabled = record.IsHighAlarmEnabled;
                value.LowAlarmLevelValue = record.LowAlarmLevelValue; %Int16
                value.IsLowAlarmEnabled = record.IsLowAlarmEnabled;
                value.TransmitterId = char(record.TransmitterId);
                value.IsBlinded = record.IsBlinded;
                value.IsTwentyFourHourTime = record.IsTwentyFourHourTime;
                value.TimeLossOccurred = record.TimeLossOccurred;
            end
        end      
        
        %maps a List<InsertionTimeRecord> to an array of MATLAB structs
        function value = MapInsertionTimeRecords(queue)
            import DexCom.Receiver.*;
            
            value = [];
            
            if (queue.Count > 0)
                value(queue.Count).RecordNumber = [];
                value(queue.Count).InsertionSystemTime = [];
                value(queue.Count).InsertionDisplayTime = [];
                value(queue.Count).SystemTime = [];
                value(queue.Count).DisplayTime = [];
                value(queue.Count).IsInserted = [];
                value(queue.Count).SessionState = [];
                
                imapped = 1;
                
                if (isa(queue, 'System.Collections.Generic.List<DexCom*ReceiverTools*InsertionTimeRecord>'))
                    first = 0;
                    last = queue.Count - 1;
                    
                    for i = first:last
                        record = queue.Item(i);
                        
                        value(imapped) = ReceiverManager.MapInsertionTimeRecord(record);
                        
                        imapped = imapped + 1;
                    end
                elseif (isa(queue, 'DexCom.Common.AsynchronousQueue<DexCom*ReceiverTools*InsertionTimeRecord>'))
                    first = queue.LogicalFirstIndex;
                    last = queue.LogicalLastIndex - 1;

                    for i = first:last
                         record = queue.GetAtLogicalIndex(i);  
                         
                             value(imapped) = ReceiverManager.MapInsertionTimeRecord(record);
                         
                            imapped = imapped + 1;
                    end
                end
            end
        end
        
        %maps a DexCom.Common.InsertionTimeRecord to a MATLAB struct
        function value = MapInsertionTimeRecord(record)
            import DexCom.Common.*;
            
            value = [];
            
            if ~(isempty(record))
                value.RecordNumber = record.RecordNumber;
                value.InsertionSystemTime = Tools.ConvertDateTimeToMatlabDays(record.InsertionSystemTime); %datenum
                value.InsertionDisplayTime = Tools.ConvertDateTimeToMatlabDays(record.InsertionDisplayTime); %datenum
                value.SystemTime = Tools.ConvertDateTimeToMatlabDays(record.SystemTime); %datenum
                value.DisplayTime = Tools.ConvertDateTimeToMatlabDays(record.DisplayTime); %datenum
                value.IsInserted = record.IsInserted;
                value.SessionState = char(MATLAB.NET.EnumHelper.GetEnum(record, 'SessionState'));
            end
        end 
        
        %maps a DexCom.Common.FirmwareHeader to a MATLAB struct
        function value = MapFirmwareHeader(header)
            value = [];
            
            if ~(isempty(header))
                value.SchemaVersion = header.SchemaVersion;
                value.ApiVersion = char(header.ApiVersion);
                value.TestApiVersion = char(header.TestApiVersion);
                value.ApiVersionNumber = header.ApiVersionNumber;
                value.TestApiVersionNumber = header.TestApiVersionNumber;
                value.ProductId = char(header.ProductId);
                value.ProductName = char(header.ProductName);
                value.SoftwareNumber = char(header.SoftwareNumber);
                value.FirmwareVersion = char(header.FirmwareVersion);
                value.FirmwareVersionNumber = header.FirmwareVersionNumber;
                value.PortVersion = char(header.PortVersion);
                value.PortVersionNumber = header.PortVersionNumber;
                value.RFVersion = char(header.RFVersion);
                value.RFVersionNumber = header.RFVersionNumber;
                value.DexBootVersion = char(header.DexBootVersion);
                value.DexBootVersionNumber = header.DexBootVersionNumber;
            end
        end 
    end    
end