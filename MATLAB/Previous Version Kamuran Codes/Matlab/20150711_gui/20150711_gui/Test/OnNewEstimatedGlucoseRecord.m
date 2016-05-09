function OnNewEstimatedGlucoseRecord(manager, arg)
global glucose

assert(~isempty(arg.ID))
assert(~isempty(arg.SerialNumber))
arg
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
    glucose=egv_record.GlucoseValue;
    units = manager.GetReceiverGlucoseDisplayUnits(arg.ID);
    disp(sprintf('Receiver %s has a new estimated glucose record of %g %s recorded at %s. State = %s', ...
        arg.SerialNumber, egv_record.GlucoseValue, units, datestr(egv_record.DisplayTime), state))
    
end
end
