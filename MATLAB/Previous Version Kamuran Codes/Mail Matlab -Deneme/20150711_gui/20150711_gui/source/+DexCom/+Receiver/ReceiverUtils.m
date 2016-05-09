classdef (Hidden = true) ReceiverUtils < handle
%   Internal static utility class

%   Copyright 2012 DexCom, Inc.
    
    %static methods
    methods (Static = true)
        %loads DLL if the file exists and if not already loaded in the app
        %domain. Throws MATLAB exception if DLL file is not found
        function LoadDLL(dll_path, dll_name)   
            import DexCom.Receiver.*;
            
            dll = fullfile(dll_path, dll_name);
            
            if (exist(dll, 'file') == 2)
                if ~(ReceiverUtils.IsAssemblyInstalled(dll_name))
                    NET.addAssembly(dll);
                end
            else
                ME = MException('DexCom:ReceiverUtils:LoadDLL:fileNotFound', ...
                                'File not found at %s', dll);
                            
                throw(ME);
            end       
        end
        
        %Installs receiver driver if not already installed and if proper
        %admin privileges are available. Throws MATLAB exception if the
        %necessary privileges are not available.
        function InstallDriverIfNotInstalled()
            import DexCom.ReceiverTools.*;
    
            receiver_driver_installed = Utils.IsDriverInstalled();
            receiver_ever_attached = Utils.IsEvidenceOfReceiverEverAttached();
            
            is_admin = Utils.IsAdministrator();
            is_really_admin = Utils.IsReallyAdministrator();
        
            if ~(receiver_driver_installed)
                if (is_admin || is_really_admin)
                    runas = false;
                
                    if (~is_admin && is_really_admin)
                        runas = true;
                    end
                
                    %for now, assume that user will not cancel out of installation
                    Utils.RunDriverSetup(runas);
                else
                    %user doesn't have privileges
                    ME = MException('DexCom:ReceiverUtils:InstallDriver:needAdminPrivileges', ...
                                        'Admin privileges are required to install the receiver device driver');
                    throw(ME);
                end
            elseif ~(receiver_ever_attached)
                %driver is pre-installed
                
                warning('DexCom:ReceiverUtils:InstallDriver', 'Please attach (or unplug and reattach) the receiver to complete the installation of the DexCom Driver')
            else
                %driver is installed
            end
        end
        
        %Determines if the assembly (name) is loaded in the app domain
        function value = IsAssemblyInstalled(asm)
            value = false;
            
            assemblies = System.AppDomain.CurrentDomain.GetAssemblies();

            for i=1:assemblies.Length
                assembly = assemblies.Get(i-1);
                
                assembly_info = textscan(char(assembly.FullName), '%s%s%s%s', 'delimiter', ',');
                loaded_assembly_name = strcat(assembly_info{1}, '.dll');
                
                if (strcmp(loaded_assembly_name, asm))
                    value = true; 
                
                    break;
                end
            end
        end
        
        %scans registry for receiver (devices)
        function value = GetAttachedReceivers()
            import DexCom.ReceiverTools.*;
            
            value = Utils.ScanForAttachedReceivers(); %List<DexCom.ReceiverApi.DeviceRegistryInfo>
        end
    end
end

