classdef ReceiverEventArgs < event.EventData
%   Public container class used to create receiver/CGM data event payloads
%
%   Properties:
%   ID
%   SerialNumber
%   EstimatedGlucoseRecord (optional)
%   MeterRecord (optional)
%   SettingsRecord (optional)
%   InsertionRecord (optional)
%   Exception (optional

%   Copyright 2012 DexCom, Inc.
    
    %public properties
    properties
        ID
        SerialNumber
        EstimatedGlucoseRecord
        MeterRecord
        SettingsRecord
        InsertionRecord
        Exception
        PropertyName
    end
    
    %public methods
    methods
        %constructor
        function this = ReceiverEventArgs(context)
            this.ID = char(context.ReceiverId.ToString());
            this.SerialNumber = char(context.SerialNumber);
        end
    end
end

