clear all,close all,clear all
% glucose=main()
% pause off

warning('off', 'MATLAB:NET:AddAssembly:nameConflict');

%these can be defined in a separate settings file ...
dll_name = 'DexCom.ReceiverTools.dll';
[~, current_dir] = system('cd');
dll_path = strcat(current_dir, '\..\Bin');
import DexCom.Receiver.*; %scope usage of the DexCom.Receiver package
manager = ReceiverManager(dll_path, dll_name);
manager.Initialize();
receivers = manager.ScanForReceivers();
manager.SynchronizeReceiver(receivers.ID, false);
data = manager.GetReceiverData(receivers.ID);
data.CurrentEstimatedGlucoseRecord = manager.GetReceiverCurrentEstimatedGlucoseRecord(receivers.ID);
delete(manager)
% addlistener(manager, 'NewEstimatedGlucoseRecord', @OnNewEstimatedGlucoseRecord);

%  manager.RequestStartScanningForReceivers()
