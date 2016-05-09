function varargout = CL_algorithm(varargin)
% CL_ALGORITHM MATLAB code for CL_algorithm.fig
%      CL_ALGORITHM, by itself, creates a new CL_ALGORITHM or raises the existing
%      singleton*.
%
%      H = CL_ALGORITHM returns the handle to a new CL_ALGORITHM or the handle to
%      the existing singleton*.
%
%      CL_ALGORITHM('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in CL_ALGORITHM.M with the given input arguments.
%
%      CL_ALGORITHM('Property','Value',...) creates a new CL_ALGORITHM or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before CL_algorithm_OpeningFcn gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to CL_algorithm_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help CL_algorithm

% Last Modified by GUIDE v2.5 13-Jul-2015 14:30:16

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
    'gui_Singleton',  gui_Singleton, ...
    'gui_OpeningFcn', @CL_algorithm_OpeningFcn, ...
    'gui_OutputFcn',  @CL_algorithm_OutputFcn, ...
    'gui_LayoutFcn',  [] , ...
    'gui_Callback',   []);
if nargin && ischar(varargin{1})
    gui_State.gui_Callback = str2func(varargin{1});
end

if nargout
    [varargout{1:nargout}] = gui_mainfcn(gui_State, varargin{:});
else
    gui_mainfcn(gui_State, varargin{:});
end
% End initialization code - DO NOT EDIT


% --- Executes just before CL_algorithm is made visible.
function CL_algorithm_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to CL_algorithm (see VARARGIN)

% Choose default command line output for CL_algorithm
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

% UIWAIT makes CL_algorithm wait for user response (see UIRESUME)
% uiwait(handles.figure1);


% --- Outputs from this function are returned to the command line.
function varargout = CL_algorithm_OutputFcn(hObject, eventdata, handles)
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;


% --- Executes on button press in pushbutton1.
function pushbutton1_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

%This is the main call function. It will run all the algorithm
%runs the algorithm
set(handles.pushbutton1,'Visible','off')
pause(1)
%% load global variables
try m20150711_load_global_variables;
    m20150711_load_global_variables_from_history
catch theErrorInfo  %sends an error email to Kamuran and Iman (Add your email if you want to receive data). The email includes error message, backup data and a screenshot of command window.
    diary('Error_commend_window.txt')
    disp(theErrorInfo.identifier)
    disp(theErrorInfo.message)
    disp(theErrorInfo.cause)
    disp(theErrorInfo.stack(1,1))
    disp(theErrorInfo.stack(2,1))
    disp(theErrorInfo.stack(3,1))
    disp(theErrorInfo.stack(4,1))
    diary('off')
    sendmail({'kturksoy@hawk.iit.edu','ihajizad@hawk.iit.edu','jfeng12@hawk.iit.edu'},...
        'Data Backup (Error in loading data)','An error occured during the current experiment and this email is sent to you automatically.',...
        {'Error_commend_window.txt','prevdata.mat'});
    delete('Error_commend_window.txt')
    set(handles.text12,'String','An error occured while loading data! Data is saved succesfully and sent to Kamuran and Iman. Please run the algorithm again.')
    set(handles.text12,'Visible','on')
    set(handles.pushbutton17,'Visible','on')
    
end
%% Get CGM reading and define sampling time
try gs=m20150711_get_CGM_value(gs);
    kj=length(gs);
catch theErrorInfo  %sends an error email to Kamuran and Iman (Add your email if you want to receive data). The email includes error message, backup data and a screenshot of command window.
    diary('Error_commend_window.txt')
    disp(theErrorInfo.identifier)
    disp(theErrorInfo.message)
    disp(theErrorInfo.cause)
    disp(theErrorInfo.stack(1,1))
    disp(theErrorInfo.stack(2,1))
    disp(theErrorInfo.stack(3,1))
    disp(theErrorInfo.stack(4,1))
    diary('off')
%     sendmail({'kturksoy@hawk.iit.edu','ihajizad@hawk.iit.edu','jfeng12@hawk.iit.edu'},...
%         'Data Backup (Error in receiving data from the CGM)','An error occured during the current experiment and this email is sent to you automatically.',...
%         {'Error_commend_window.txt','prevdata.mat'});
    delete('Error_commend_window.txt')
    set(handles.text12,'String','An error occured while receiving data from the CGM! Data is saved succesfully and sent to Kamuran and Iman. Please run the algorithm again.')
    set(handles.text12,'Visible','on')
    set(handles.pushbutton17,'Visible','on')
end
%% Sensor error detection and functional redundancy with nosie generator
flag_noise=1;
try CGM_retuning_with_noise_generator=CGM_SEDFR_JF(gs,bolus_insulin,basal_insulin,flag_noise)
catch theErrorInfo  %sends an error email to Kamuran and Iman (Add your email if you want to receive data). The email includes error message, backup data and a screenshot of command window.
    diary('Error_commend_window.txt')
    disp(theErrorInfo.identifier)
    disp(theErrorInfo.message)
    disp(theErrorInfo.cause)
    disp(theErrorInfo.stack(1,1))
    disp(theErrorInfo.stack(2,1))
    disp(theErrorInfo.stack(3,1))
    disp(theErrorInfo.stack(4,1))
    diary('off')
%     sendmail({'kturksoy@hawk.iit.edu','ihajizad@hawk.iit.edu','jfeng12@hawk.iit.edu'},...
%         'Data Backup (Error in receiving data from the CGM)','An error occured during the current experiment and this email is sent to you automatically.',...
%         {'Error_commend_window.txt','prevdata.mat'});
    delete('Error_commend_window.txt')
    set(handles.text12,'String','An error occured while receiving data from the CGM! Data is saved succesfully and sent to Kamuran and Iman. Please run the algorithm again.')
    set(handles.text12,'Visible','on')
    set(handles.pushbutton17,'Visible','on')
end
%% Sensor error detection and functional redundancy without nosie generator
flag_noise=0;
try CGM_retuning_without_noise_generator=CGM_SEDFR_JF(gs,bolus_insulin,basal_insulin,flag_noise)
catch theErrorInfo  %sends an error email to Kamuran and Iman (Add your email if you want to receive data). The email includes error message, backup data and a screenshot of command window.
    diary('Error_commend_window.txt')
    disp(theErrorInfo.identifier)
    disp(theErrorInfo.message)
    disp(theErrorInfo.cause)
    disp(theErrorInfo.stack(1,1))
    disp(theErrorInfo.stack(2,1))
    disp(theErrorInfo.stack(3,1))
    disp(theErrorInfo.stack(4,1))
    diary('off')
%     sendmail({'kturksoy@hawk.iit.edu','ihajizad@hawk.iit.edu','jfeng12@hawk.iit.edu'},...
%         'Data Backup (Error in receiving data from the CGM)','An error occured during the current experiment and this email is sent to you automatically.',...
%         {'Error_commend_window.txt','prevdata.mat'});
    delete('Error_commend_window.txt')
    set(handles.text12,'String','An error occured while receiving data from the CGM! Data is saved succesfully and sent to Kamuran and Iman. Please run the algorithm again.')
    set(handles.text12,'Visible','on')
    set(handles.pushbutton17,'Visible','on')
end
%% Get armband data
try [armband_data_with_time,ee,phys_act,sleep,gsr]=m20150711_get_armband_data(armband_data_with_time,ee,phys_act,sleep,gsr);
catch theErrorInfo  %sends an error email to Kamuran and Iman (Add your email if you want to receive data). The email includes error message, backup data and a screenshot of command window.
    save temp_prevdata.mat
    diary('Error_commend_window.txt')
    disp(theErrorInfo.identifier)
    disp(theErrorInfo.message)
    disp(theErrorInfo.cause)
    disp(theErrorInfo.stack(1,1))
    disp(theErrorInfo.stack(2,1))
    disp(theErrorInfo.stack(3,1))
    disp(theErrorInfo.stack(4,1))
    diary('off')
%     sendmail({'kturksoy@hawk.iit.edu','ihajizad@hawk.iit.edu','jfeng12@hawk.iit.edu'},...
%         'Data Backup (Error in receiving data from the Armband)','An error occured during the current experiment and this email is sent to you automatically.',...
%         {'Error_commend_window.txt','prevdata.mat','temp_prevdata.mat'});
    delete('Error_commend_window.txt')
    set(handles.text12,'String','An error occured while receiving data from the Armband! Data is saved succesfully and sent to Kamuran and Iman. Please run the algorithm again.')
    set(handles.text12,'Visible','on')
    set(handles.pushbutton17,'Visible','on')
end
%% Calculate IOB
try IOB_total(kj,1)=m20150711_calculate_IOB(bolus_insulin,basal_insulin(:,kj-1));
catch theErrorInfo  %sends an error email to Kamuran and Iman (Add your email if you want to receive data). The email includes error message, backup data and a screenshot of command window.
    save temp_prevdata.mat
    diary('Error_commend_window.txt')
    disp(theErrorInfo.identifier)
    disp(theErrorInfo.message)
    disp(theErrorInfo.cause)
    disp(theErrorInfo.stack(1,1))
    disp(theErrorInfo.stack(2,1))
    disp(theErrorInfo.stack(3,1))
    disp(theErrorInfo.stack(4,1))
    diary('off')
%     sendmail({'kturksoy@hawk.iit.edu','ihajizad@hawk.iit.edu','jfeng12@hawk.iit.edu'},...
%         'Data Backup (Error in calucationg of IOB)','An error occured during the current experiment and this email is sent to you automatically.',...
%         {'Error_commend_window.txt','prevdata.mat','temp_prevdata.mat'});
    delete('Error_commend_window.txt')
    set(handles.text12,'String','An error occured while calculating the insulin on board estimations! Data is saved succesfully and sent to Kamuran and Iman. Please run the algorithm again.')
    set(handles.text12,'Visible','on')
    set(handles.pushbutton17,'Visible','on')
end
%% Run meal detection and calculate bolus insulin
try [meal_states,meal_covariance,bolus_insulin,...
        meal_bolus_amount,meal_detection,meal_detection_time,...
        correction_bolus_amount,correction_detection,correction_detection_time,correction_limit,...
        meal_g_basal,meal_gpc_gs_slope_degree,meal_gpc_mu]=m20150711_run_meal_detection_bolus_algorithm(meal_states,meal_covariance,bolus_insulin,...
        meal_bolus_amount,meal_detection,meal_detection_time,...
        correction_bolus_amount,correction_detection,correction_detection_time,correction_limit,...
        gs,kj,meal_g_basal,meal_gpc_gs_slope_degree,meal_gpc_mu,...
        sleep,phys_act,IOB_total,body_weight);
catch theErrorInfo  %sends an error email to Kamuran and Iman (Add your email if you want to receive data). The email includes error message, backup data and a screenshot of command window.
    save temp_prevdata.mat
    diary('Error_commend_window.txt')
    disp(theErrorInfo.identifier)
    disp(theErrorInfo.message)
    disp(theErrorInfo.cause)
    disp(theErrorInfo.stack(1,1))
    disp(theErrorInfo.stack(2,1))
    disp(theErrorInfo.stack(3,1))
    disp(theErrorInfo.stack(4,1))
    diary('off')
%     sendmail({'kturksoy@hawk.iit.edu','ihajizad@hawk.iit.edu','jfeng12@hawk.iit.edu'},...
%         'Data Backup (Error in meal-detection and meal-bolusing algorithm)','An error occured during the current experiment and this email is sent to you automatically.',...
%         {'Error_commend_window.txt','prevdata.mat','temp_prevdata.mat'});
    delete('Error_commend_window.txt')
    set(handles.text12,'String','An error occured in meal-detection and meal bolusing algorithm! Data is saved succesfully and sent to Kamuran and Iman. Please run the algorithm again.')
    set(handles.text12,'Visible','on')
    set(handles.pushbutton17,'Visible','on')
end

%% Run GPC and calculate basal insulin
try [phi,phi_ee,phi_gsr,...
        armax_parameters,armax_covariance,armax_lamda,armax_err,...
        arma_parameters_ee,arma_lamda_ee,arma_covariance_ee,arma_err_ee,...
        arma_parameters_gsr,arma_lamda_gsr,arma_covariance_gsr,arma_err_gsr,...
        A_state,A_state_ee,A_state_gsr,...
        C_state,C_state_ee,C_state_gsr,...
        B_state,K_state,K_state_ee,K_state_gsr,...
        M,L,L_ee,L_gsr,M_ee,M_gsr,...
        X_state,X_state_ee,X_state_gsr,...
        ee_prediction,gsr_prediction,g_prediction,...
        reference_glucose,insulin_sensitivity_constant,basal_insulin,IOB_prediction,...
        maximum_insulin,total_daily_unit,insulin_sensitivity_factor]=m20150711_gpc(gs,ee,gsr,kj,...
        phi,phi_ee,phi_gsr,...
        armax_parameters,armax_covariance,armax_lamda,armax_err,...
        arma_parameters_ee,arma_lamda_ee,arma_covariance_ee,arma_err_ee,...
        arma_parameters_gsr,arma_lamda_gsr,arma_covariance_gsr,arma_err_gsr,...
        A_state,A_state_ee,A_state_gsr,...
        C_state,C_state_ee,C_state_gsr,...
        B_state,K_state,K_state_ee,K_state_gsr,...
        M,L,L_ee,L_gsr,M_ee,M_gsr,...
        X_state,X_state_ee,X_state_gsr,...
        ee_prediction,gsr_prediction,g_prediction,...
        reference_glucose,insulin_sensitivity_constant,basal_insulin,IOB_prediction,...
        maximum_insulin,total_daily_unit,insulin_sensitivity_factor,body_weight,meal_gpc_mu,bolus_insulin,...
        0,zeros(8,1));
catch theErrorInfo  %sends an error email to Kamuran and Iman (Add your email if you want to receive data). The email includes error message, backup data and a screenshot of command window.
    save temp_prevdata.mat
    diary('Error_commend_window.txt')
    disp(theErrorInfo.identifier)
    disp(theErrorInfo.message)
    disp(theErrorInfo.cause)
    disp(theErrorInfo.stack(1,1))
    disp(theErrorInfo.stack(2,1))
    disp(theErrorInfo.stack(3,1))
    disp(theErrorInfo.stack(4,1))
    diary('off')
%     sendmail({'kturksoy@hawk.iit.edu','ihajizad@hawk.iit.edu','jfeng12@hawk.iit.edu'},...
%         'Data Backup (Error in GPC and basal insulin algorithm)','An error occured during the current experiment and this email is sent to you automatically.',...
%         {'Error_commend_window.txt','prevdata.mat','temp_prevdata.mat'});
    delete('Error_commend_window.txt')
    set(handles.text12,'String','An error occured in GPC and basal insulin algorithm! Data is saved succesfully and sent to Kamuran and Iman. Please run the algorithm again.')
    set(handles.text12,'Visible','on')
    set(handles.pushbutton17,'Visible','on')
end
%% Run Controller performance assessment 

try insulin_CPA=CPA_Module_paralleled_calculation_JF(gs,ee,gsr,kj,...
        phi,phi_ee,phi_gsr,...
        armax_parameters,armax_covariance,armax_lamda,armax_err,...
        arma_parameters_ee,arma_lamda_ee,arma_covariance_ee,arma_err_ee,...
        arma_parameters_gsr,arma_lamda_gsr,arma_covariance_gsr,arma_err_gsr,...
        A_state,A_state_ee,A_state_gsr,...
        C_state,C_state_ee,C_state_gsr,...
        B_state,K_state,K_state_ee,K_state_gsr,...
        M,L,L_ee,L_gsr,M_ee,M_gsr,...
        X_state,X_state_ee,X_state_gsr,...
        ee_prediction,gsr_prediction,g_prediction,...
        reference_glucose,insulin_sensitivity_constant,basal_insulin,IOB_prediction,...
        maximum_insulin,total_daily_unit,insulin_sensitivity_factor,body_weight,meal_gpc_mu,bolus_insulin)
catch theErrorInfo  %sends an error email to Kamuran and Iman (Add your email if you want to receive data). The email includes error message, backup data and a screenshot of command window.
    save temp_prevdata.mat
    diary('Error_commend_window.txt')
    disp(theErrorInfo.identifier)
    disp(theErrorInfo.message)
    disp(theErrorInfo.cause)
    disp(theErrorInfo.stack(1,1))
    disp(theErrorInfo.stack(2,1))
    disp(theErrorInfo.stack(3,1))
    disp(theErrorInfo.stack(4,1))
    diary('off')
%     sendmail({'kturksoy@hawk.iit.edu','ihajizad@hawk.iit.edu','jfeng12@hawk.iit.edu'},...
%         'Data Backup (Error in GPC and basal insulin algorithm)','An error occured during the current experiment and this email is sent to you automatically.',...
%         {'Error_commend_window.txt','prevdata.mat','temp_prevdata.mat'});
    delete('Error_commend_window.txt')
    set(handles.text12,'String','An error occured in GPC and basal insulin algorithm! Data is saved succesfully and sent to Kamuran and Iman. Please run the algorithm again.')
    set(handles.text12,'Visible','on')
    set(handles.pushbutton17,'Visible','on')
end
%% Run HEA and suggest warning snack
try [hypo_threshold,hypo_slope_degree,...
        hypo_alarm,carb_amount,carb_type,hypo_phase,hypo_phase_old,repeated_immediate_alarm]=m20150711_hypo_alarm(hypo_threshold,hypo_slope_degree,...
        hypo_alarm,carb_amount,carb_type,hypo_phase,hypo_phase_old,repeated_immediate_alarm,gs,kj,g_prediction,phys_act,sleep);
catch theErrorInfo  %sends an error email to Kamuran and Iman (Add your email if you want to receive data). The email includes error message, backup data and a screenshot of command window.
    save temp_prevdata.mat
    diary('Error_commend_window.txt')
    disp(theErrorInfo.identifier)
    disp(theErrorInfo.message)
    disp(theErrorInfo.cause)
    disp(theErrorInfo.stack(1,1))
    disp(theErrorInfo.stack(2,1))
    disp(theErrorInfo.stack(3,1))
    disp(theErrorInfo.stack(4,1))
    diary('off')
%     sendmail({'kturksoy@hawk.iit.edu','ihajizad@hawk.iit.edu','jfeng12@hawk.iit.edu'},...
%         'Data Backup (Error in HEA algorithm)','An error occured during the current experiment and this email is sent to you automatically.',...
%         {'Error_commend_window.txt','prevdata.mat','temp_prevdata.mat'});
    delete('Error_commend_window.txt')
    set(handles.text12,'String','An error occured in HEA algorithm! Data is saved succesfully and sent to Kamuran and Iman. Please run the algorithm again.')
    set(handles.text12,'Visible','on')
    set(handles.pushbutton17,'Visible','on')
end
%% Check the current stuation
if phys_act(kj,1)==1
    batch_CL(kj,1)={'Exercise'};
elseif sleep(kj,1)==1
    batch_CL(kj,1)={'Sleep'};
elseif meal_detection(kj,1)==1
    batch_CL(kj,1)={'Meal'};
else
    batch_CL(kj,1)={'Other'};
end
%% Write everything into mdata
mdata1=cell(1,12);
mdata1(1,[1,2,3,4,5,6,9])=[{datestr(clock)},gs(kj,1),bolus_insulin(kj,1),basal_insulin(1,kj),hypo_alarm(kj,1),double(~isequal(meal_bolus_amount(kj,1)*meal_detection(kj,1),0)),batch_CL(kj,1)];
mdata=[mdata1;mdata];
set(handles.uitable1,'Data',mdata);
set(handles.text6,'String',num2str(bolus_insulin(kj,1),'%10.4f'))
set(handles.text2,'String',num2str(basal_insulin(1,kj),'%10.4f'))
set(handles.text4,'String',num2str(IOB_total(kj,1),'%10.4f'))
basal_insulin_calculated(:,kj)=basal_insulin(:,kj);
bolus_insulin_calculated(kj,1)=bolus_insulin(kj,1);
%% Check HEA and suggest Rescue Carbs
if hypo_alarm(kj,1)==1 && hypo_phase(kj,1)==5 && repeated_immediate_alarm(kj,1)==0
    set(handles.text12,'Visible','on','String',['Immediate Alarm: Consume',carb_amount(kj,1),'grams of',carb_type(kj,1)])
    set(handles.pushbutton17,'Visible','on')
elseif hypo_alarm(kj,1)==1 && hypo_phase(kj,1)==5 && repeated_immediate_alarm(kj,1)==1
    set(handles.text12,'Visible','on','String',['Immediate Alarm: This carb may not be needed. Confirm with subject or MD','Consume' carb_amount(kj,1),' grams of',carb_type(kj,1)])
    set(handles.pushbutton17,'Visible','on')
elseif hypo_alarm(kj,1)==1 && hypo_phase(kj,1)<5
    set(handles.text12,'Visible','on','String',['Early Alarm: Consume',carb_amount(kj,1),' grams of ',carb_type(kj,1)])
    set(handles.pushbutton17,'Visible','on')
end
%% Save data
save prevdata
if etime(clock,auto_backup_time)>21600
    auto_backup_time=clock;
%     sendmail({'kturksoy@hawk.iit.edu','ihajizad@hawk.iit.edu','jfeng12@hawk.iit.edu'},...
%         'Data Backup (Error in HEA algorithm)','An error occured during the current experiment and this email is sent to you automatically.',...
%         {'prevdata.mat','temp_prevdata.mat'});
end
set(handles.uipanel1,'Visible','on')

% --- Executes on button press in pushbutton17.
function pushbutton17_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton17 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
set(handles.text12,'Visible','off')
set(handles.pushbutton17,'Visible','off')
if strcmp(get(handles.pushbutton1,'Visible'),'off')==1 && strcmp(get(handles.uipanel1,'Visible'),'off')==1
    set(handles.pushbutton1,'Visible','on')
end

if strcmp(get(handles.pushbutton20,'Visible'),'off')==1
    set(handles.pushbutton20,'Visible','on')
end

% --- Executes on button press in pushbutton3.
function pushbutton3_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
set(handles.uipanel3,'Visible','on')

% --- Executes on button press in pushbutton4.
function pushbutton4_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton4 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
mdata1=cell(1,12);
mdata1(1,[1,11])=[{datestr(clock)}  'Sleep finished'];
mdata=[mdata1;mdata];
set(handles.uitable1,'Data',mdata);
save prevdata

% --- Executes on button press in pushbutton5.
function pushbutton5_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton5 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
mdata1=cell(1,12);
mdata1(1,[1,11])=[{datestr(clock)}  'Sleep started'];
mdata=[mdata1;mdata];
set(handles.uitable1,'Data',mdata);
save prevdata
% --- Executes on button press in pushbutton6.
function pushbutton6_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton6 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)


% --- Executes on button press in pushbutton7.
function pushbutton7_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton7 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
mdata1=cell(1,12);
mdata1(1,[1,10])=[{datestr(clock)}  'Exercise finished'];
mdata=[mdata1;mdata];
set(handles.uitable1,'Data',mdata);
save prevdata

% --- Executes on button press in pushbutton8.
function pushbutton8_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton8 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
mdata1=cell(1,12);
mdata1(1,[1,10])=[{datestr(clock)}  'Exercise started'];
mdata=[mdata1;mdata];
set(handles.uitable1,'Data',mdata);
save prevdata

% --- Executes on button press in pushbutton9.
function pushbutton9_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton9 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)


% --- Executes on button press in pushbutton10.
function pushbutton10_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton10 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)


% --- Executes on button press in pushbutton15.
function pushbutton15_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton15 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
%%
gs_in=118;%must be defined before the experiment
ee_in=6.1555;%must be defined before the experiment
gsr_in=0.0586;%must be defined before the experiment
sleep_in=0;%must be defined before the experiment
phys_act_in=0;%must be defined before the experiment
body_weight=63;%must be defined before the experiment
%%
gs=gs_in*ones(20,1);
%%
armband_data_with_time=[];
ee=ee_in*ones(20,1);
phys_act=phys_act_in*ones(20,1);
sleep=sleep_in*ones(20,1);
gsr=gsr_in*ones(20,1);
%%
IOB_total=zeros(20,1);
bolus_insulin=zeros(20,1);
basal_insulin=zeros(8,20);
%%
meal_states=[0.1 gs_in 0 0 0.068 0.037 1.3 20]'*ones(1,20);
meal_covariance(:,:,20)=eye(8,8);
meal_bolus_amount=zeros(20,1);
meal_detection=zeros(20,1);
meal_detection_time=zeros(20,1);
correction_bolus_amount=zeros(20,1);
correction_detection=zeros(20,1);
correction_detection_time=zeros(20,1);
correction_limit=zeros(20,1);
meal_g_basal=gs_in*ones(20,1);
meal_gpc_gs_slope_degree=45*ones(20,1);
meal_gpc_mu=0.5*ones(20,1);
%%
phi=zeros(24,20);
phi_ee=zeros(4,20);
phi_gsr=zeros(4,20);
armax_parameters=zeros(24,20);
armax_covariance(:,:,20)=eye(24);
armax_lamda=0.5*ones(20,1);
armax_err=zeros(20,1);
arma_parameters_ee=zeros(4,20);
arma_lamda_ee=0.5*ones(20,1);
arma_covariance_ee(:,:,20)=eye(4);
arma_err_ee=zeros(20,1);
arma_parameters_gsr=zeros(4,20);
arma_lamda_gsr=0.5*ones(20,1);
arma_covariance_gsr(:,:,20)=eye(4);
arma_err_gsr=zeros(20,1);
A_state(:,:,20)=zeros(21,21);
A_state_ee(:,:,20)=zeros(4,4);
A_state_gsr(:,:,20)=zeros(4,4);
C_state(:,:,20)=zeros(1,21);
C_state_ee(:,:,20)=zeros(1,4);
C_state_gsr(:,:,20)=zeros(1,4);
B_state(:,:,20)=zeros(21,3);
K_state(:,:,20)=zeros(21,1);
K_state_ee(:,:,20)=zeros(4,1);
K_state_gsr(:,:,20)=zeros(4,1);
M(:,:,20)=zeros(8,21);
L(:,:,20)=zeros(8,8);
L_ee(:,:,20)=zeros(8,8);
L_gsr(:,:,20)=zeros(8,8);
M_ee(:,:,20)=zeros(8,4);
M_gsr(:,:,20)=zeros(8,4);
X_state=zeros(21,20);
X_state_ee=zeros(4,20);
X_state_gsr=zeros(4,20);
ee_prediction=zeros(8,20);
gsr_prediction=zeros(8,20);
g_prediction=zeros(8,20);
reference_glucose=zeros(8,20);
insulin_sensitivity_constant=0.1*ones(8,20);
IOB_prediction=zeros(8,20);
maximum_insulin=25*ones(8,20);
total_daily_unit=ones(8,20);
insulin_sensitivity_factor=ones(8,20);
%%
hypo_threshold=zeros(20,1);
hypo_slope_degree=45*ones(20,1);
hypo_alarm=zeros(20,1);
carb_amount={''};
carb_type={''};
hypo_phase=zeros(20,1);
hypo_phase_old=zeros(20,1);
repeated_immediate_alarm=zeros(20,1);
%%
mdata=[];
batch_CL={''};
bolus_insulin_calculated=zeros(20,1);
basal_insulin_calculated=zeros(8,20);
auto_backup_time=clock;
%%
save prevdata
set(handles.uipanel6,'Visible','off')
set(handles.pushbutton1,'Visible','on')

% --- Executes on button press in pushbutton16.
function pushbutton16_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton16 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
set(handles.uipanel6,'Visible','off')
set(handles.pushbutton1,'Visible','on')

% --- Executes on button press in pushbutton13.
function pushbutton13_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton13 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)


% --- Executes on button press in pushbutton14.
function pushbutton14_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton14 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)



function edit4_Callback(hObject, eventdata, handles)
% hObject    handle to edit4 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit4 as text
%        str2double(get(hObject,'String')) returns contents of edit4 as a double


% --- Executes during object creation, after setting all properties.
function edit4_CreateFcn(hObject, eventdata, handles)
% hObject    handle to edit4 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in pushbutton12.
function pushbutton12_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton12 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)



function edit3_Callback(hObject, eventdata, handles)
% hObject    handle to edit3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit3 as text
%        str2double(get(hObject,'String')) returns contents of edit3 as a double


% --- Executes during object creation, after setting all properties.
function edit3_CreateFcn(hObject, eventdata, handles)
% hObject    handle to edit3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in pushbutton11.
function pushbutton11_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton11 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
mdata1=cell(1,12);
mdata1(1,[1,12])=[{datestr(clock)}  get(handles.edit3,'String')];
mdata=[mdata1;mdata];
set(handles.uitable1,'Data',mdata);
set(handles.uipanel3,'Visible','off');
set(handles.edit3,'String',' ');
save prevdata

% --- Executes on button press in pushbutton2.
function pushbutton2_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton2 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
if isnan(str2double(get(handles.edit1,'String')))==0 && isnan(str2double(get(handles.edit2,'String')))==0
bolus_insulin(kj,1)=str2double(get(handles.edit1,'String'));
basal_insulin(1,kj)=str2double(get(handles.edit2,'String'));

mdata(1,[7,8])=[{bolus_insulin(kj,1)},{basal_insulin(1,kj)}];
set(handles.uitable1,'Data',mdata);
set(handles.uipanel1,'Visible','off');
set(handles.edit1,'String',' ');
set(handles.edit2,'String',' ');
set(handles.pushbutton1,'Visible','on')

save prevdata
end

function edit1_Callback(hObject, eventdata, handles)
% hObject    handle to edit1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit1 as text
%        str2double(get(hObject,'String')) returns contents of edit1 as a double


% --- Executes during object creation, after setting all properties.
function edit1_CreateFcn(hObject, eventdata, handles)
% hObject    handle to edit1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function edit2_Callback(hObject, eventdata, handles)
% hObject    handle to edit2 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit2 as text
%        str2double(get(hObject,'String')) returns contents of edit2 as a double


% --- Executes during object creation, after setting all properties.
function edit2_CreateFcn(hObject, eventdata, handles)
% hObject    handle to edit2 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in pushbutton20.
function pushbutton20_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton20 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
set(handles.pushbutton20,'Visible','off')
% sendmail({'kturksoy@hawk.iit.edu','ihajizad@hawk.iit.edu'},...
%     'Data Backup (Saved Manually)','Data is saved manually and this email is sent to you automatically.',...
%     {'prevdata.mat'});
set(handles.text12,'String','Data is saved succesfully and sent to Kamuran and Iman.')
set(handles.text12,'Visible','on')
set(handles.pushbutton17,'Visible','on')


% --- If Enable == 'on', executes on mouse press in 5 pixel border.
% --- Otherwise, executes on mouse press in 5 pixel border or over pushbutton1.
function pushbutton1_ButtonDownFcn(hObject, eventdata, handles)
% hObject    handle to pushbutton1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
