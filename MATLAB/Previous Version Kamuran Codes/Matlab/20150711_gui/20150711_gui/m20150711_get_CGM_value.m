%% Get CGM values. Kamuran Turksoys
function gs=m20150711_get_CGM_value(gs)
warning('off', 'MATLAB:NET:AddAssembly:nameConflict');
dll_name = 'DexCom.ReceiverTools.dll';
[~, current_dir] = system('cd');
dll_path = strcat(current_dir, '\Bin');
import DexCom.Receiver.*;
manager = ReceiverManager(dll_path, dll_name);
manager.Initialize();
receivers = manager.ScanForReceivers();
manager.SynchronizeReceiver(receivers.ID, false);
dexcom_data = manager.GetReceiverData(receivers.ID);
dexcom_data.CurrentEstimatedGlucoseRecord = manager.GetReceiverCurrentEstimatedGlucoseRecord(receivers.ID);
delete(manager)
g=double(dexcom_data.CurrentEstimatedGlucoseRecord.GlucoseValue); % g is used as read CGM value
gs=cat(1,gs,g);% update subcutanous glucose readings