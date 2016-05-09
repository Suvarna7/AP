classdef (Hidden = true) Receiver < handle
%   Internal class that encapulates a .NET receiver context instance
%
%   On receiver connect, the tool kit DLL instances a receiver context.
%   A context is specific to a given receiver and is the programmatic
%   interface by which receiver data is obtained via events, properties,
%   and methods. This class provides a MATLAB interface that communicates
%   with th internal .NET-based context.
%
%   This class is internal in package scope and is utilized by the 
%   ReceiverManager class. It is not intended for public use.

%   Copyright 2012 DexCom, Inc.
    
    %public, dependent properties
    properties (Dependent = true)
        SyncInterval
    end
    
    %public properties
    properties        
    end
    
    %readonly properties
    properties (SetAccess = private, GetAccess = public)
        CurrentState
        ID
        SerialNumber
        RegistryInfo
        PCTimeOffset
        CurrentEstimatedGlucoseRecord
        CurrentMeterRecord
        CurrentSettingsRecord
        CurrentInsertionRecord
        CurrentTransmitterId
        FirmwareHeader
        EstimatedGlucoseRecords
        MeterRecords
        SettingsRecords
        InsertionRecords
        GlucoseDisplayUnits
        IsComPortValid
    end
    
    %private properties
    properties (SetAccess = private, GetAccess = private)
        Context = [];
    end
      
    %public methods
    methods   
        %construct MATLAB receiver based on a DexCom.ReceiverTools.ReceiverContext
        %instance
        function this = Receiver(context)
            if (isempty(context))
                m = MException('DexCom:Receiver:Constructor', 'Input context is empty');
                
                throw(m);
            end
            
            this.Context = context; %DexCom.ReceiverTools.ReceiverContext
            this.ID = char(context.ReceiverId.ToString()); %convert System.Guid to MATLAB string to facilitate MATLAB string ops
            this.SerialNumber = context.SerialNumber; %System.String
            this.RegistryInfo = context.RegistryInfo; %DexCom.ReceiverApi.DeviceRegistryInfo
            this.Context.Interval = System.TimeSpan.FromSeconds(5.0); %5.0 is the default in the DLL ...
        end
                
        %destructor. Stops internal context (if running), disposes context
        function delete(this)
            if ~(isempty(this.Context))
                this.Context.Exit();
                this.Context.Dispose();
                this.Context = [];
            end
        end
        
        %retrieves context's sync interval
        function value = get.SyncInterval(this)
            value = [];
            
            if ~(isempty(this.Context))
                value = this.Context.Interval; %System.TimeSpan
            end
        end
        
        %sets context's sync interval (in seconds)
        function set.SyncInterval(this, value)
            if ~(isempty(this.Context))
                this.Context.Interval = System.TimeSpan.FromSeconds(value);
            end
        end
        
        %retrieves context's current operating state
        %(Ready/Starting/Running/Exiting/Exited/Pausing/Paused/Aborting/Abo
        %rted)
        function value = get.CurrentState(this)
            value = [];
            
            if ~(isempty(this.Context))
                value = this.Context.CurrentState; %DexCom.Common.LongRunningOperatingState
            end
        end
        
        %retrieves receivers's current transmitter id
        function value = get.CurrentTransmitterId(this)
            value = [];
            
            if ~(isempty(this.Context))
                if ~(isempty(this.Context.CurrentTransmitterId))
                    value = this.Context.CurrentTransmitterId; %System.String
                else
                    value = this.Context.ReadTransmitterId();
                end
            end
        end
        
        %retrieves timespan (as a datevec) between the host PC and the
        %receiver's system clocks
        function value = get.PCTimeOffset(this)
            import DexCom.Common.*;
            
            value = [];
            
            if ~(isempty(this.Context))
                pc_now = now;
                receiver_now = Tools.ConvertDateTimeToMatlabDays(this.Context.ReadDisplayTime());

                value = pc_now - receiver_now; %datenum
            end
        end
                
        %retrives receiver's system time
        function value = GetSystemTime(this)
            value = [];
            
            if ~(isempty(this.Context))
                value = this.Context.ReadSystemTime(); %System.DateTime
            end
        end

        %retrives receiver's display time
        function value = GetDisplayTime(this)
            value = [];
            
            if ~(isempty(this.Context))
                value = this.Context.ReadDisplayTime(); %System.DateTime
            end
        end
        
        %lazy loads receiver's firmware header
        function value = get.FirmwareHeader(this)
            value = [];
            
            if ~(isempty(this.Context))
                if (isempty(this.FirmwareHeader))
                    value = this.Context.FirmwareHeader; %DexCom.ReceiverTools.FirmwareHeader
                end
            end
        end

        %lazy loads receiver's glucose display units
        function value = get.GlucoseDisplayUnits(this)
            value = [];
            
            if ~(isempty(this.Context))
                if (isempty(this.GlucoseDisplayUnits))
                    value = this.Context.ReadGlucoseDisplayUnits(); %System.String
                end
            end
        end
        
        %retrieves receivers's port validity
        function value = get.IsComPortValid(this)
            value = false;
            
            if ~(isempty(this.Context))
                value = this.Context.IsValidComPort; %System.Boolean
            end
        end
        
        %retrieves all available records from receiver database, caches in
        %context instance
        function ReadDatabaseRecords(this, fire_events)
            if ~(isempty(this.Context))
                this.Context.ReadDatabaseRecords(fire_events);
            end
        end
        
        %retrieves receivers's current
        %DexCom.ReceiverTools.EstimatedGlucoseRecord
        function value = get.CurrentEstimatedGlucoseRecord(this)
            value = [];
            
            if ~(isempty(this.Context))
                value = this.Context.CurrentEstimatedGlucoseRecord; %DexCom.ReceiverTools.EstimatedGlucoseRecord
            end
        end

        %retrieves receiver's current
        %DexCom.ReceiverTools.MeterRecord
        function value = get.CurrentMeterRecord(this)
            value = [];
            
            if ~(isempty(this.Context))
                value = this.Context.CurrentMeterRecord; %DexCom.ReceiverTools.MeterRecord
            end
        end

        %retrieves receivers's current
        %DexCom.ReceiverTools.SettingsRecord
        function value = get.CurrentSettingsRecord(this)
            value = [];
            
            if ~(isempty(this.Context))
                value = this.Context.CurrentSettingsRecord; %DexCom.ReceiverTools.SettingsRecord
            end
        end

        %retrieves receivers's current DexCom.ReceiverTools.InsertionTimeRecord
        function value = get.CurrentInsertionRecord(this)
            value = [];
            
            if ~(isempty(this.Context))
                value = this.Context.CurrentInsertionTimeRecord; %DexCom.ReceiverTools.InsertionTimeRecord
            end
        end
        
        %retrieves context's history of glucose records as a
        %DexCom.Common.AsynchronousQueue<DexCom.ReceiverTools.EstimatedGlucoseRecord>
        function value = get.EstimatedGlucoseRecords(this)
            value = [];
            
            if ~(isempty(this.Context))
                value = this.Context.EstimatedGlucoseRecords; %AsynchronouseQueue<DexCom.ReceiverTools.EstimatedGlucoseRecord>
            end
        end 
        
        %retrieves context's history of glucose records after the given
        %time as a List<DexCom.ReceiverTools.EstimatedGlucoseRecord>
        function value = GetEstimatedGlucoseRecordsSince(this, timestamp)
            import DexCom.Common.*;
            
            value = [];
            
            %convert to .NET instance
            filter = Tools.ConvertMatlabDaysToDateTime(timestamp);
                
            if ~(isempty(this.Context))
                value = this.Context.GetEstimatedGlucoseRecordsSince(filter); %List<DexCom.ReceiverTools.EstimatedGlucoseRecord>
            end
        end
        
        %retrieves context's history of meter records as a
        %DexCom.Common.AsynchronousQueue<DexCom.ReceiverTools.MeterRecord>
        function value = get.MeterRecords(this)
            value = [];
            
            if ~(isempty(this.Context))
                value = this.Context.MeterRecords; %AsynchronouseQueue<DexCom.ReceiverTools.MeterRecord>
            end
        end
        
        %retrieves context's history of meter records after the given
        %time as a List<DexCom.ReceiverTools.MeterRecord>
        function value = GetMeterRecordsSince(this, timestamp)
            import DexCom.Common.*;
            
            value = [];
            
            filter = Tools.ConvertMatlabDaysToDateTime(timestamp);
                
            if ~(isempty(this.Context))
                value = this.Context.GetMeterRecordsSince(filter); %List<DexCom.ReceiverTools.MeterRecord>
            end
        end
        
        %retrieves context's history of settings records as a
        %DexCom.Common.AsynchronousQueue<DexCom.ReceiverTools.SettingsRecord>
        function value = get.SettingsRecords(this)
            value = [];
            
            if ~(isempty(this.Context))
                value = this.Context.SettingsRecords; %AsynchronouseQueue<DexCom.ReceiverTools.SettingsRecord>
            end
        end
        
        %retrieves context's history of settings records after the given
        %time as a List<DexCom.ReceiverTools.SettingsRecord>
        function value = GetSettingsRecordsSince(this, timestamp)
            import DexCom.Common.*;
            
            value = [];
            
            filter = Tools.ConvertMatlabDaysToDateTime(timestamp);
                
            if ~(isempty(this.Context))
                value = this.Context.GetSettingsRecordsSince(filter); %List<DexCom.ReceiverTools.SettingsRecord>
            end
        end
        
        %retrieves context's history of insertion records as a
        %DexCom.Common.AsynchronousQueue<DexCom.ReceiverTools.InsertionTimeRecord>
        function value = get.InsertionRecords(this)
            value = [];
            
            if ~(isempty(this.Context))
                value = this.Context.InsertionTimeRecords; %AsynchronouseQueue<DexCom.ReceiverTools.InsertionTimeRecord>
            end
        end
        
        %retrieves context's history of insertion records after the given
        %time as a List<DexCom.ReceiverTools.InsertionTimeRecord>
        function value = GetInsertionRecordsSince(this, timestamp)
            import DexCom.Common.*;
            
            value = [];
            
            filter = Tools.ConvertMatlabDaysToDateTime(timestamp);
                
            if ~(isempty(this.Context))
                value = this.Context.GetInsertionTimeRecordsSince(filter); %List<DexCom.ReceiverTools.InsertionTimeRecord>
            end
        end
             
        %receiver lifecycle methods
        function Run(this)
            if ~(isempty(this.Context))
                this.Context.Run() %synchronous
            end
        end
        
        function RequestRun(this) %asynchronous
            if ~(isempty(this.Context))
                this.Context.RunInBackground();
            end
        end
        
        function Exit(this) %synchronous
            if ~(isempty(this.Context))
                this.Context.ClearAllEventsBeforeExit();
                this.Context.Exit();
            end
        end
        
        function RequestExit(this) %asynchronous
            if ~(isempty(this.Context))
                this.Context.ClearAllEventsBeforeExit();
                this.Context.RequestExit();
            end
        end
        
        function Pause(this) %synchronous
            this.Context.Pause();
        end
        
        function RequestPause(this) %asynchronous
            this.Context.RequestPause();
        end
        
        function RequestResume(this) %asynchronous
            this.Context.RequestResume();
        end
    end
end

