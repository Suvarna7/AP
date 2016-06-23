classdef opt_params
    %OPT_PARAMS Summary of this class goes here
    %   Detailed explanation goes here
    

    
    methods (Static)
        function [ P_out, pseudoinvP_out, Q_old_out, Y_out, phi_out ]= setGetParams(Y0,phi0,Q_old0,P_old,lamda_old)
            persistent P;
            persistent pseudoinvP;
            persistent Q_old;
            persistent Y;
            persistent phi;
            if nargin
            % Start Q_old and Y_old
            Q_old = Q_old0;
            Y = Y0;
            phi = phi0;
            %Compute P matrix
                P=(1/(lamda_old))*(P_old-(P_old*phi*pinv(lamda_old+phi'*P_old*phi)*phi'*P_old));
                pseudoinvP=pinv(P);
              
            end
            
            P_out = P;
            pseudoinvP_out = pseudoinvP;
            Q_old_out = Q_old;
            Y_out = Y;
            phi_out =  phi;
        end
        
    end
    
end

