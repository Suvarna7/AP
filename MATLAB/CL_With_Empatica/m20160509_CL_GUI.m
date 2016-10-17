function varargout = m20160509_CL_GUI(varargin)
% M20160509_CL_GUI MATLAB code for m20160509_CL_GUI.fig
%      M20160509_CL_GUI, by itself, creates a new M20160509_CL_GUI or raises the existing
%      singleton*.
%
%      H = M20160509_CL_GUI returns the handle to a new M20160509_CL_GUI or the handle to
%      the existing singleton*.
%
%      M20160509_CL_GUI('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in M20160509_CL_GUI.M with the given input arguments.
%
%      M20160509_CL_GUI('Property','Value',...) creates a new M20160509_CL_GUI or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before m20160509_CL_GUI_OpeningFcn gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to m20160509_CL_GUI_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help m20160509_CL_GUI

% Last Modified by GUIDE v2.5 13-May-2016 10:16:04

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
    'gui_Singleton',  gui_Singleton, ...
    'gui_OpeningFcn', @m20160509_CL_GUI_OpeningFcn, ...
    'gui_OutputFcn',  @m20160509_CL_GUI_OutputFcn, ...
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


% --- Executes just before m20160509_CL_GUI is made visible.


function m20160509_CL_GUI_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to m20160509_CL_GUI (see VARARGIN)

% Choose default command line output for m20160509_CL_GUI
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

% UIWAIT makes m20160509_CL_GUI wait for user response (see UIRESUME)
% uiwait(handles.figure1);
main_figure=get(handles.gui_control_panel,'Parent');
set(handles.gui_display_panel,'Parent',main_figure);
set(handles.gui_hypo_hyper,'Parent',main_figure);
set(handles.gui_clear_history,'Parent',main_figure);
set(handles.gui_insulin_verification,'Parent',main_figure);
set(handles.gui_user_enter_information,'Parent',main_figure);
set(handles.gui_exercise_type,'Parent',main_figure);
set(handles.gui_exercise_intensity,'Parent',main_figure);
set(handles.gui_meal_ysi_finger_verification,'Parent',main_figure);
set(handles.gui_hypoglycemia_detection,'Parent',main_figure);
set(handles.gui_real_time_monitoring,'Parent',main_figure);
set(handles.gui_display_panel,'Parent',main_figure);
set(handles.gui_error_message,'Parent',main_figure);


% --- Outputs from this function are returned to the command line.
function varargout = m20160509_CL_GUI_OutputFcn(hObject, eventdata, handles)
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;

%% USB Connect - start
'Press connect in Java program....'
global usb_con;
usb_con = connect_java_usb();
%assignin('base', 'usb_con', usb_con);
'Local connection established!'

% --- Executes on button press in gui_backup_data.
function gui_backup_data_Callback(hObject, eventdata, handles)
% hObject    handle to gui_backup_data (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
set(handles.gui_backup_data,'Visible','off')
m20150711_load_global_variables
m20150711_load_global_variables_from_history
m20160623_save_backup
set(handles.gui_text_error_message,'String','Data is saved succesfully.')
set(handles.gui_error_message,'Visible','on')



% --- Executes on button press in gui_run_algorithm.
function gui_run_algorithm_Callback(hObject, eventdata, handles)
% hObject    handle to gui_run_algorithm (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
set(handles.gui_run_algorithm,'Visible','off')
pause(1)
%% load global variables
try
    m20150711_load_global_variables;
    m20150711_load_global_variables_from_history
    kj_old=length(gs);% get the length for previous gs will compared with kj_new to define missing data
catch theErrorInfo  %sends an error email to Kamuran and Iman (Add your email if you want to receive data). The email includes error message, backup data and a screenshot of command window.
    save (['temp_prevdata',datestr(clock,'yyyy-mm-dd_HH-MM-SS'),'.mat'])
    diary(['Error_commend_window',datestr(clock,'yyyy-mm-dd_HH-MM-SS'),'.txt'])
    disp(theErrorInfo.identifier)
    disp(theErrorInfo.message)
    disp(theErrorInfo.cause)
    disp(theErrorInfo.stack(1,1))
    disp(theErrorInfo.stack(2,1))
    disp(theErrorInfo.stack(3,1))
    disp(theErrorInfo.stack(4,1))
    diary('off')
    m20160623_save_backup
    set(handles.gui_text_error_message,'String','An error occured while loading data! Data is saved succesfully. Please run the algorithm again.')
    set(handles.gui_error_message,'Visible','on')
end
%% Get CGM reading and define sampling time
try
    %MANUAL GS entering
    %gs=[gs;173];
    %DEXCOM G4 USB GS entering 
    %gs=m20150711_get_CGM_value(gs);
    %DiAS GS entering
    'Dexcom...'
    global usb_con;
    global empatica_data;
    [table, gs_new] = read_last_samples(usb_con, 'dexcom');
    gs_new(2,5)
    gs=[gs;str2double(gs_new(2,5))];
    'Received!'
    
    kj=length(gs);
    flag_data_1=1;
catch theErrorInfo  %sends an error email to Kamuran and Iman (Add your email if you want to receive data). The email includes error message, backup data and a screenshot of command window.
    flag_data_1=0;
    save (['temp_prevdata',datestr(clock,'yyyy-mm-dd_HH-MM-SS'),'.mat'])
    diary(['Error_commend_window',datestr(clock,'yyyy-mm-dd_HH-MM-SS'),'.txt'])
    disp(theErrorInfo.identifier)
    disp(theErrorInfo.message)
    disp(theErrorInfo.cause)
    disp(theErrorInfo.stack(1,1))
    disp(theErrorInfo.stack(2,1))
    disp(theErrorInfo.stack(3,1))
    disp(theErrorInfo.stack(4,1))
    diary('off')
    m20160623_save_backup
    set(handles.gui_text_error_message,'String','An error occured while receiving data from the CGM! Data is saved succesfully. Please run the algorithm again.')
    set(handles.gui_error_message,'Visible','on')
end
%% Sensor error detection and functional redundancy
try
    load  temp_SEDFR_nonoise gb_angle_ret
    CGM_retuning=gb_angle_ret';
    flag_noise=0;
    kj_new=kj;
    if kj_new==kj_old || gs(end)==0
        gs_without_retuning=[CGM_retuning;NaN];
    else
        gs_without_retuning=gs;
    end
    CGM_retuning(kj_old+1)=CGM_SEDFR_JF(gs_without_retuning,bolus_insulin(1,:)',basal_insulin,flag_noise);
    if kj_new==kj_old || gs(end)==0
        gs(kj_old+1)=CGM_retuning(kj_old+1);
        kj=kj_old+1;
    end
    flag_data_2=1;
    flag_data_1=1;
catch theErrorInfo  %sends an error email to Kamuran and Iman (Add your email if you want to receive data). The email includes error message, backup data and a screenshot of command window.
    flag_data_2=0;
    save (['temp_prevdata',datestr(clock,'yyyy-mm-dd_HH-MM-SS'),'.mat'])
    diary(['Error_commend_window',datestr(clock,'yyyy-mm-dd_HH-MM-SS'),'.txt'])
    disp(theErrorInfo.identifier)
    disp(theErrorInfo.message)
    disp(theErrorInfo.cause)
    disp(theErrorInfo.stack(1,1))
    disp(theErrorInfo.stack(2,1))
    disp(theErrorInfo.stack(3,1))
    disp(theErrorInfo.stack(4,1))
    diary('off')
    m20160623_save_backup
    set(handles.gui_text_error_message,'String','An error occured while CGM retuning with noise generator! Data is saved succesfully. Please run the algorithm again.')
    set(handles.gui_error_message,'Visible','on')
end
%% Get armband data
try
    [armband_data_with_time,ee,phys_act,sleep,gsr]=m20150711_get_armband_data(armband_data_with_time,ee,phys_act,sleep,gsr);
    flag_data_3=1;
catch theErrorInfo  %sends an error email to Kamuran and Iman (Add your email if you want to receive data). The email includes error message, backup data and a screenshot of command window.
    flag_data_3=0;
    save (['temp_prevdata',datestr(clock,'yyyy-mm-dd_HH-MM-SS'),'.mat'])
    diary(['Error_commend_window',datestr(clock,'yyyy-mm-dd_HH-MM-SS'),'.txt'])
    disp(theErrorInfo.identifier)
    disp(theErrorInfo.message)
    disp(theErrorInfo.cause)
    disp(theErrorInfo.stack(1,1))
    disp(theErrorInfo.stack(2,1))
    disp(theErrorInfo.stack(3,1))
    disp(theErrorInfo.stack(4,1))
    diary('off')
    m20160623_save_backup
    set(handles.gui_text_error_message,'String','An error occured while receiving data from the Armband! Data is saved succesfully. Please run the algorithm again.')
    set(handles.gui_error_message,'Visible','on')
end
%% Get Empatica data
'Empatica...'
global usb_con;
global empatica_data;
[table, empatica_new] = read_last_samples(usb_con, 'empatica');
if (size(empatica_data)>0)
    %Other samples - skip column names
    empatica_data = [empatica_data;empatica_new(2:end,:)];
else
    %First sample - include columns names
    empatica_data = [empatica_data;empatica_new];

end
assignin('base', 'empatica', empatica_data);

'Received!'
%% Calculate IOB
try
    IOB_total(kj,1)=m20150711_calculate_IOB(bolus_insulin(1,:)',basal_insulin');
    flag_data_4=1;
catch theErrorInfo  %sends an error email to Kamuran and Iman (Add your email if you want to receive data). The email includes error message, backup data and a screenshot of command window.
    flag_data_4=0;
    save (['temp_prevdata',datestr(clock,'yyyy-mm-dd_HH-MM-SS'),'.mat'])
    diary(['Error_commend_window',datestr(clock,'yyyy-mm-dd_HH-MM-SS'),'.txt'])
    disp(theErrorInfo.identifier)
    disp(theErrorInfo.message)
    disp(theErrorInfo.cause)
    disp(theErrorInfo.stack(1,1))
    disp(theErrorInfo.stack(2,1))
    disp(theErrorInfo.stack(3,1))
    disp(theErrorInfo.stack(4,1))
    diary('off')
    m20160623_save_backup
    set(handles.gui_text_error_message,'String','An error occured while calculating the insulin on board estimations! Data is saved succesfully. Please run the algorithm again.')
    set(handles.gui_error_message,'Visible','on')
end
%% Run meal detection and calculate bolus insulin
try
    [meal_states,meal_covariance,bolus_insulin_meal,...
        meal_bolus_amount,meal_detection,meal_detection_time,...
        correction_bolus_amount,correction_detection,correction_detection_time,correction_limit,...
        meal_g_basal,meal_gpc_gs_slope_degree,meal_gpc_mu]=m20150711_run_meal_detection_bolus_algorithm(meal_states,meal_covariance,bolus_insulin_meal,...
        meal_bolus_amount,meal_detection,meal_detection_time,...
        correction_bolus_amount,correction_detection,correction_detection_time,correction_limit,...
        gs,kj,meal_g_basal,meal_gpc_gs_slope_degree,meal_gpc_mu,...
        sleep,phys_act,IOB_total,body_weight);
    flag_data_5=1;
catch theErrorInfo  %sends an error email to Kamuran and Iman (Add your email if you want to receive data). The email includes error message, backup data and a screenshot of command window.
    flag_data_5=0;
    save (['temp_prevdata',datestr(clock,'yyyy-mm-dd_HH-MM-SS'),'.mat'])
    diary(['Error_commend_window',datestr(clock,'yyyy-mm-dd_HH-MM-SS'),'.txt'])
    disp(theErrorInfo.identifier)
    disp(theErrorInfo.message)
    disp(theErrorInfo.cause)
    disp(theErrorInfo.stack(1,1))
    disp(theErrorInfo.stack(2,1))
    disp(theErrorInfo.stack(3,1))
    disp(theErrorInfo.stack(4,1))
    diary('off')
    m20160623_save_backup
    set(handles.gui_text_error_message,'String','An error occured in meal-detection and meal bolusing algorithm! Data is saved succesfully. Please run the algorithm again.')
    set(handles.gui_error_message,'Visible','on')
end

%% Run GPC and calculate basal insulin
try
    [phi,phi_ee,phi_gsr,...
        armax_parameters,armax_covariance,armax_lamda,armax_err,...
        arma_parameters_ee,arma_lamda_ee,arma_covariance_ee,arma_err_ee,...
        arma_parameters_gsr,arma_lamda_gsr,arma_covariance_gsr,arma_err_gsr,...
        A_state,A_state_ee,A_state_gsr,...
        C_state,C_state_ee,C_state_gsr,...
        B_state,K_state,K_state_ee,K_state_gsr,...
        M,L,L_ee,L_gsr,M_ee,M_gsr,...
        X_state,X_state_ee,X_state_gsr,...
        ee_prediction,gsr_prediction,g_prediction,...
        reference_glucose,insulin_sensitivity_constant,bolus_insulin,IOB_prediction,...
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
        reference_glucose,insulin_sensitivity_constant,bolus_insulin,IOB_prediction,...
        maximum_insulin,total_daily_unit,insulin_sensitivity_factor,body_weight,meal_gpc_mu,bolus_insulin_meal,basal_insulin,...
        0,zeros(8,1));
    ttt_time=clock;
    if ttt_time(4)<4
        %0.8u/h from 12am- 4am
        basal_insulin(kj,1)=0.8;
    else
        %0.9 u/h from 4am - 11.59pm
        basal_insulin(kj,1)=0.9;
    end


        
%         basal_insulin(kj,1)=1.35;
%     elseif ttt_time(4)>=10 && ttt_time(4)<14
%         basal_insulin(kj,1)=2.9;
%     elseif ttt_time(4)>=14 && ttt_time(4)<17
%         basal_insulin(kj,1)=2.7;
%     elseif ttt_time(4)>=17 && ttt_time(4)<19
%         basal_insulin(kj,1)=3.275;
%     elseif ttt_time(4)>=19 && ttt_time(4)<22
%         basal_insulin(kj,1)=3.2;
%     elseif ttt_time(4)>=22 && ttt_time(4)<23
%         basal_insulin(kj,1)=2.85;
%     else
%         basal_insulin(kj,1)=1.9;
%     
%     end   
    %%%%%%%
    flag_data_6=1;
catch theErrorInfo  %sends an error email to Kamuran and Iman (Add your email if you want to receive data). The email includes error message, backup data and a screenshot of command window.
    flag_data_6=0;
    save (['temp_prevdata',datestr(clock,'yyyy-mm-dd_HH-MM-SS'),'.mat'])
    diary(['Error_commend_window',datestr(clock,'yyyy-mm-dd_HH-MM-SS'),'.txt'])
    disp(theErrorInfo.identifier)
    disp(theErrorInfo.message)
    disp(theErrorInfo.cause)
    disp(theErrorInfo.stack(1,1))
    disp(theErrorInfo.stack(2,1))
    disp(theErrorInfo.stack(3,1))
    disp(theErrorInfo.stack(4,1))
    diary('off')
    m20160623_save_backup
    set(handles.gui_text_error_message,'String','An error occured in GPC and basal insulin algorithm! Data is saved succesfully. Please run the algorithm again.')
    set(handles.gui_error_message,'Visible','on')
end

%% Run HEA and suggest warning snack
try
    [hypo_threshold,hypo_slope_degree,...
        hypo_alarm,carb_amount,carb_type,hypo_phase,hypo_phase_old,repeated_immediate_alarm,hypo_alarm_old]=m20150711_hypo_alarm(hypo_threshold,hypo_slope_degree,...
        hypo_alarm,carb_amount,carb_type,hypo_phase,hypo_phase_old,repeated_immediate_alarm,gs,kj,g_prediction,phys_act,sleep,hypo_alarm_old);
    flag_data_7=1;
catch theErrorInfo  %sends an error email to Kamuran and Iman (Add your email if you want to receive data). The email includes error message, backup data and a screenshot of command window.
    flag_data_7=0;
    save (['temp_prevdata',datestr(clock,'yyyy-mm-dd_HH-MM-SS'),'.mat'])
    diary(['Error_commend_window',datestr(clock,'yyyy-mm-dd_HH-MM-SS'),'.txt'])
    disp(theErrorInfo.identifier)
    disp(theErrorInfo.message)
    disp(theErrorInfo.cause)
    disp(theErrorInfo.stack(1,1))
    disp(theErrorInfo.stack(2,1))
    disp(theErrorInfo.stack(3,1))
    disp(theErrorInfo.stack(4,1))
    diary('off')
    m20160623_save_backup
    set(handles.gui_text_error_message,'String','An error occured in HEA algorithm! Data is saved succesfully. Please run the algorithm again.')
    set(handles.gui_error_message,'Visible','on')
end

%% Run FDD for CGM
try
    [fault,fault_reason]=m20160516_FDD(gs,fault,fault_reason);
    if fault(kj,1)>0
        axes(handles.gui_real_monitoring_figure);
        bar(fault_reason(kj,:)/max(fault_reason(kj,:)),'FaceColor','red','EdgeColor','red','LineWidth',1.5)
        text(1,0.1,'Low or High Glucose Values','rotation',90)
        text(2,0.1,'Rapid Change in Glucose Values','rotation',90)
        text(3,0.1,'Directional Change in Glucose Values','rotation',90)
        ylim([0 1])
    else
        axes(handles.gui_real_monitoring_figure);
        bar(fault_reason(kj,:)/max(fault_reason(kj,:)),'FaceColor','red','EdgeColor','red','LineWidth',1.5)
        ylim([0 1])
    end
    flag_data_8=1;
catch theErrorInfo  %sends an error email to Kamuran and Iman (Add your email if you want to receive data). The email includes error message, backup data and a screenshot of command window.
    flag_data_8=0;
    save (['temp_prevdata',datestr(clock,'yyyy-mm-dd_HH-MM-SS'),'.mat'])
    diary(['Error_commend_window',datestr(clock,'yyyy-mm-dd_HH-MM-SS'),'.txt'])
    disp(theErrorInfo.identifier)
    disp(theErrorInfo.message)
    disp(theErrorInfo.cause)
    disp(theErrorInfo.stack(1,1))
    disp(theErrorInfo.stack(2,1))
    disp(theErrorInfo.stack(3,1))
    disp(theErrorInfo.stack(4,1))
    diary('off')
    m20160623_save_backup
    set(handles.gui_text_error_message,'String','An error occured in FDD algorithm! Data is saved succesfully. Please run the algorithm again.')
    set(handles.gui_error_message,'Visible','on')
end
%% Check the current stuation
try
    if phys_act(kj,1)==1
        batch_CL(kj,1)={'Exercise'};
    elseif sleep(kj,1)==1
        batch_CL(kj,1)={'Sleep'};
    elseif meal_detection(kj,1)==1
        batch_CL(kj,1)={'Meal'};
    else
        batch_CL(kj,1)={'Other'};
    end
    
    %% Check HEA and suggest Rescue Carbs
    if hypo_phase(kj,1)==5
        set(handles.gui_hypo_phase5,'String','X')
        set(handles.gui_hypo_phase4,'String',' ')
        set(handles.gui_hypo_phase3,'String',' ')
        set(handles.gui_hypo_phase2,'String',' ')
        set(handles.gui_hypo_phase1,'String',' ')
        set(handles.gui_hypo_phase0,'String',' ')
        basal_insulin(kj,1)=0;
    elseif hypo_phase(kj,1)==4
        set(handles.gui_hypo_phase5,'String',' ')
        set(handles.gui_hypo_phase4,'String','X')
        set(handles.gui_hypo_phase3,'String',' ')
        set(handles.gui_hypo_phase2,'String',' ')
        set(handles.gui_hypo_phase1,'String',' ')
        set(handles.gui_hypo_phase0,'String',' ')
        basal_insulin(kj,1)=0;
    elseif hypo_phase(kj,1)==3
        set(handles.gui_hypo_phase5,'String',' ')
        set(handles.gui_hypo_phase4,'String',' ')
        set(handles.gui_hypo_phase3,'String','X')
        set(handles.gui_hypo_phase2,'String',' ')
        set(handles.gui_hypo_phase1,'String',' ')
        set(handles.gui_hypo_phase0,'String',' ')
        basal_insulin(kj,1)=0;
    elseif hypo_phase(kj,1)==2
        set(handles.gui_hypo_phase5,'String',' ')
        set(handles.gui_hypo_phase4,'String',' ')
        set(handles.gui_hypo_phase3,'String',' ')
        set(handles.gui_hypo_phase2,'String','X')
        set(handles.gui_hypo_phase1,'String',' ')
        set(handles.gui_hypo_phase0,'String',' ')
        basal_insulin(kj,1)=0;
    elseif hypo_phase(kj,1)==1
        set(handles.gui_hypo_phase5,'String',' ')
        set(handles.gui_hypo_phase4,'String',' ')
        set(handles.gui_hypo_phase3,'String',' ')
        set(handles.gui_hypo_phase2,'String',' ')
        set(handles.gui_hypo_phase1,'String','X')
        set(handles.gui_hypo_phase0,'String',' ')
        basal_insulin(kj,1)=0;
    else
        set(handles.gui_hypo_phase5,'String',' ')
        set(handles.gui_hypo_phase4,'String',' ')
        set(handles.gui_hypo_phase3,'String',' ')
        set(handles.gui_hypo_phase2,'String',' ')
        set(handles.gui_hypo_phase1,'String',' ')
        set(handles.gui_hypo_phase0,'String','X')
    end
    
    if hypo_alarm(kj,1)==1 && hypo_phase(kj,1)==5 && repeated_immediate_alarm(kj,1)==0
        set(handles.gui_text_hypo_warning,'Visible','on','String',['Immediate Alarm: Consume',carb_amount(kj,1),'grams of',carb_type(kj,1)])
        set(handles.gui_hypoglycemia_detection,'Visible','on')
    elseif hypo_alarm(kj,1)==1 && hypo_phase(kj,1)==5 && repeated_immediate_alarm(kj,1)==1
        set(handles.gui_text_hypo_warning,'Visible','on','String',['Immediate Alarm: ','Consume' carb_amount(kj,1),' grams of',carb_type(kj,1),'This carb may not be needed. Confirm with subject'])
        set(handles.gui_hypoglycemia_detection,'Visible','on')
    elseif hypo_alarm(kj,1)==1 && hypo_phase(kj,1)<5
        set(handles.gui_text_hypo_warning,'Visible','on','String',['Early Alarm: Consume',carb_amount(kj,1),' grams of ',carb_type(kj,1)])
        set(handles.gui_hypoglycemia_detection,'Visible','on')
    end
    
    %% Write everything into mdata
    bolus_insulin_calculated(:,kj)=bolus_insulin(:,kj)+[bolus_insulin_meal(kj,1);zeros(7,1)];
    mdata1=cell(1,15);
    mdata1(1,[1,2,3,4,5,6,8])=[{datestr(clock)},gs(kj,1),bolus_insulin_calculated(1,kj),basal_insulin(kj),hypo_alarm(kj,1),double(~isequal(meal_bolus_amount(kj,1)*meal_detection(kj,1),0)),batch_CL(kj,1)];
    mdata=[mdata1;mdata];
    set(handles.gui_display_table,'Data',mdata);
    set(handles.gui_bolus_insulin,'String',num2str(bolus_insulin_calculated(1,kj),'%10.4f'))
    set(handles.gui_basal_insulin,'String',num2str(basal_insulin(kj,1),'%10.4f'))
    set(handles.gui_IOB,'String',num2str(IOB_total(kj,1),'%10.4f'))
    flag_data_9=1;
catch theErrorInfo  %sends an error email to Kamuran and Iman (Add your email if you want to receive data). The email includes error message, backup data and a screenshot of command window.
    flag_data_9=0;
    save (['temp_prevdata',datestr(clock,'yyyy-mm-dd_HH-MM-SS'),'.mat'])
    diary(['Error_commend_window',datestr(clock,'yyyy-mm-dd_HH-MM-SS'),'.txt'])
    disp(theErrorInfo.identifier)
    disp(theErrorInfo.message)
    disp(theErrorInfo.cause)
    disp(theErrorInfo.stack(1,1))
    disp(theErrorInfo.stack(2,1))
    disp(theErrorInfo.stack(3,1))
    disp(theErrorInfo.stack(4,1))
    diary('off')
    m20160623_save_backup
    set(handles.gui_text_error_message,'String','An error occured in FDD algorithm! Data is saved succesfully. Please run the algorithm again.')
    set(handles.gui_error_message,'Visible','on')
end
%% Save data
if etime(clock,auto_backup_time)>3600
    auto_backup_time=clock;
    save (['temp_prevdata',datestr(clock,'yyyy-mm-dd_HH-MM-SS'),'.mat'])
    m20160623_save_backup
end
if flag_data_1==1 && flag_data_2==1 && flag_data_3==1 && flag_data_4==1 && flag_data_5==1 && flag_data_6==1 && flag_data_7==1 && flag_data_8==1 && flag_data_9==1
    set(handles.gui_insulin_verification,'Visible','on')
    m20150711_save_global_variables_to_history
end

% --- Executes on button press in gui_verify_insulin.
function gui_verify_insulin_Callback(hObject, eventdata, handles)
% hObject    handle to gui_verify_insulin (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
if isnan(str2double(get(handles.gui_enter_insulin,'String')))==0
    bolus_insulin(1,kj)=str2double(get(handles.gui_enter_insulin,'String'));
    mdata(1,7)={bolus_insulin(1,kj)};
    set(handles.gui_display_table,'Data',mdata);
    set(handles.gui_insulin_verification,'Visible','off');
    set(handles.gui_enter_insulin,'String',' ');
    set(handles.gui_run_algorithm,'Visible','on')
    
    m20150711_save_global_variables_to_history
end


function gui_enter_insulin_Callback(hObject, eventdata, handles)
% hObject    handle to gui_enter_insulin (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of gui_enter_insulin as text
%        str2double(get(hObject,'String')) returns contents of gui_enter_insulin as a double


% --- Executes during object creation, after setting all properties.
function gui_enter_insulin_CreateFcn(hObject, eventdata, handles)
% hObject    handle to gui_enter_insulin (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in gui_clear_history_yes.
function gui_clear_history_yes_Callback(hObject, eventdata, handles)
% hObject    handle to gui_clear_history_yes (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
%% USB Connect - start
% 'Press connect in Java program....'
% usb_con = connect_java_usb();
% assignin('base', 'usb_con', usb_con);
% 'Local connection established!'
%% Start data
gs_in=213;%must be defined before the experiment glocuse
ee_in=6.4522;%must be defined before the experiment
gsr_in=0.0872;%must be defined before the experiment
sleep_in=0;%must be defined before the experiment
phys_act_in=0;%must be defined before the experiment
body_weight=74.8;%must be defined before the experiment kg
%% GS init
gs=gs_in*ones(20,1);
%% Armband init
armband_data_with_time=[];
ee=ee_in*ones(20,1);
phys_act=phys_act_in*ones(20,1);
sleep=sleep_in*ones(20,1);
gsr=gsr_in*ones(20,1);
%%Empatica init
global empatica_data;
empatica_data = [];
%%
IOB_total=zeros(20,1);
bolus_insulin=zeros(8,20);
bolus_insulin_meal=zeros(20,1);
basal_insulin=zeros(8,1); %should be defined according to subjects
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
maximum_insulin=6*ones(8,20);
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
bolus_insulin_calculated=zeros(8,20);
auto_backup_time=clock;
%%
fault=zeros(20,1);
fault_reason=zeros(20,3);

m20150711_save_global_variables_to_history
set(handles.gui_clear_history,'Visible','off')
set(handles.gui_run_algorithm,'Visible','on')


% --- Executes on button press in gui_clear_history_no.
function gui_clear_history_no_Callback(hObject, eventdata, handles)
% hObject    handle to gui_clear_history_no (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
set(handles.gui_clear_history,'Visible','off')
set(handles.gui_run_algorithm,'Visible','on')
%% USB Connect - start
% 'Press connect in Java program....'
% usb_con = connect_java_usb();
% assignin('base', 'usb_con', usb_con);
% 'Local connection established!'
% --- Executes on button press in gui_note.
function gui_note_Callback(hObject, eventdata, handles)
% hObject    handle to gui_note (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
type_of_entry='Note';
set(handles.gui_text_meal_ysi_finger_value,'String','Note');
set(handles.gui_meal_ysi_finger_verification,'Visible','on');
m20150711_save_global_variables_to_history

% --- Executes on button press in gui_finish_sleep.
function gui_finish_sleep_Callback(hObject, eventdata, handles)
% hObject    handle to gui_finish_sleep (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
mdata1=cell(1,15);
mdata1(1,[1,10])=[{datestr(clock)}  'Sleep finished'];
mdata=[mdata1;mdata];
set(handles.gui_display_table,'Data',mdata);
m20150711_save_global_variables_to_history

% --- Executes on button press in gui_start_sleep.
function gui_start_sleep_Callback(hObject, eventdata, handles)
% hObject    handle to gui_start_sleep (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
mdata1=cell(1,15);
mdata1(1,[1,10])=[{datestr(clock)}  'Sleep started'];
mdata=[mdata1;mdata];
set(handles.gui_display_table,'Data',mdata);
m20150711_save_global_variables_to_history

% --- Executes on button press in gui_finish_exercise.
function gui_finish_exercise_Callback(hObject, eventdata, handles)
% hObject    handle to gui_finish_exercise (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
set(handles.gui_exercise_type,'Visible','off');
mdata1=cell(1,15);
mdata1(1,[1,9])=[{datestr(clock)}  'Exercise finished'];
mdata=[mdata1;mdata];
set(handles.gui_display_table,'Data',mdata);
m20150711_save_global_variables_to_history

% --- Executes on button press in gui_start_exercise.
function gui_start_exercise_Callback(hObject, eventdata, handles)
% hObject    handle to gui_start_exercise (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
set(handles.gui_exercise_type,'Visible','on');
mdata1=cell(1,15);
mdata1(1,[1,9])=[{datestr(clock)}  'Exercise started'];
mdata=[mdata1;mdata];
set(handles.gui_display_table,'Data',mdata);
m20150711_save_global_variables_to_history

% --- Executes on button press in gui_plot.
function gui_plot_Callback(hObject, eventdata, handles)
% hObject    handle to gui_plot (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)


% --- Executes on button press in gui_finish_meal.
function gui_finish_meal_Callback(hObject, eventdata, handles)
% hObject    handle to gui_finish_meal (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
mdata1=cell(1,15);
mdata1(1,[1,11])=[{datestr(clock)}  'Meal finished'];
mdata=[mdata1;mdata];
set(handles.gui_display_table,'Data',mdata);
m20150711_save_global_variables_to_history

% --- Executes on button press in gui_start_meal.
function gui_start_meal_Callback(hObject, eventdata, handles)
% hObject    handle to gui_start_meal (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
type_of_entry='Meal';
set(handles.gui_meal_ysi_finger_verification,'Visible','on');
m20150711_save_global_variables_to_history

% --- Executes on button press in gui_finish_snack.
function gui_finish_snack_Callback(hObject, eventdata, handles)
% hObject    handle to gui_finish_snack (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
mdata1=cell(1,15);
mdata1(1,[1,11])=[{datestr(clock)}  'Snack finished'];
mdata=[mdata1;mdata];
set(handles.gui_display_table,'Data',mdata);
m20150711_save_global_variables_to_history

% --- Executes on button press in gui_start_snack.
function gui_start_snack_Callback(hObject, eventdata, handles)
% hObject    handle to gui_start_snack (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
type_of_entry='Snack';
set(handles.gui_meal_ysi_finger_verification,'Visible','on');
m20150711_save_global_variables_to_history

% --- Executes on button press in gui_ysi.
function gui_ysi_Callback(hObject, eventdata, handles)
% hObject    handle to gui_ysi (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
type_of_entry='YSI';
set(handles.gui_meal_ysi_finger_verification,'Visible','on');
m20150711_save_global_variables_to_history

% --- Executes on button press in gui_finger_stick.
function gui_finger_stick_Callback(hObject, eventdata, handles)
% hObject    handle to gui_finger_stick (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
type_of_entry='Finger-Stick';
set(handles.gui_meal_ysi_finger_verification,'Visible','on');
m20150711_save_global_variables_to_history
% --- Executes on button press in gui_leg_curl.
function gui_leg_curl_Callback(hObject, eventdata, handles)
% hObject    handle to gui_leg_curl (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
type_of_exercise='Leg Curl';
set(handles.gui_text_speed_weight,'String','Weight');
set(handles.gui_text_inclination_sets_reps,'String','Sets/Reps');
set(handles.gui_exercise_intensity,'Visible','on')
m20150711_save_global_variables_to_history

% --- Executes on button press in gui_seated_row.
function gui_seated_row_Callback(hObject, eventdata, handles)
% hObject    handle to gui_seated_row (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
type_of_exercise='Seated Row';
set(handles.gui_text_speed_weight,'String','Weight');
set(handles.gui_text_inclination_sets_reps,'String','Sets/Reps');
set(handles.gui_exercise_intensity,'Visible','on')
m20150711_save_global_variables_to_history

% --- Executes on button press in gui_chest_press.
function gui_chest_press_Callback(hObject, eventdata, handles)
% hObject    handle to gui_chest_press (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
type_of_exercise='Chest Press';
set(handles.gui_text_speed_weight,'String','Weight');
set(handles.gui_text_inclination_sets_reps,'String','Sets/Reps');
set(handles.gui_exercise_intensity,'Visible','on')
m20150711_save_global_variables_to_history

% --- Executes on button press in gui_lat_pull_down.
function gui_lat_pull_down_Callback(hObject, eventdata, handles)
% hObject    handle to gui_lat_pull_down (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
type_of_exercise='Lat Pull-Down';
set(handles.gui_text_speed_weight,'String','Weight');
set(handles.gui_text_inclination_sets_reps,'String','Sets/Reps');
set(handles.gui_exercise_intensity,'Visible','on')
m20150711_save_global_variables_to_history

% --- Executes on button press in gui_treadmill.
function gui_treadmill_Callback(hObject, eventdata, handles)
% hObject    handle to gui_treadmill (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
type_of_exercise='Treadmill';
set(handles.gui_text_speed_weight,'String','Speed');
set(handles.gui_text_inclination_sets_reps,'String','Inclination');
set(handles.gui_exercise_intensity,'Visible','on')
m20150711_save_global_variables_to_history

% --- Executes on button press in gui_leg_extension.
function gui_leg_extension_Callback(hObject, eventdata, handles)
% hObject    handle to gui_leg_extension (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
type_of_exercise='Leg Extention';
set(handles.gui_text_speed_weight,'String','Weight');
set(handles.gui_text_inclination_sets_reps,'String','Sets/Reps');
set(handles.gui_exercise_intensity,'Visible','on')
m20150711_save_global_variables_to_history

% --- Executes on button press in gui_dumbbell_shoulder.
function gui_dumbbell_shoulder_Callback(hObject, eventdata, handles)
% hObject    handle to gui_dumbbell_shoulder (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
type_of_exercise='Dumbbell Shoulder';
set(handles.gui_text_speed_weight,'String','Weight');
set(handles.gui_text_inclination_sets_reps,'String','Sets/Reps');
set(handles.gui_exercise_intensity,'Visible','on')
m20150711_save_global_variables_to_history

% --- Executes on button press in gui_resting.
function gui_resting_Callback(hObject, eventdata, handles)
% hObject    handle to gui_resting (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
type_of_exercise='Resting';
mdata1=cell(1,15);
mdata1(1,[1,9])=[{datestr(clock)}   type_of_exercise ];
mdata=[mdata1;mdata];
set(handles.gui_display_table,'Data',mdata);
m20150711_save_global_variables_to_history

% --- Executes on button press in gui_verify_intensity.
function gui_verify_intensity_Callback(hObject, eventdata, handles)
% hObject    handle to gui_verify_intensity (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
mdata1=cell(1,15);
if strcmp(type_of_exercise,'Treadmill')==1
    mdata1(1,[1,9])=[{datestr(clock)}   [type_of_exercise, ': Speed=', get(handles.gui_speed_weight,'String'), ' ,Inclination: ', get(handles.gui_inclination_sets_reps,'String')]];
else
    mdata1(1,[1,9])=[{datestr(clock)}   [type_of_exercise, ': Weight=', get(handles.gui_speed_weight,'String'), ' ,Sets/Reps: ', get(handles.gui_inclination_sets_reps,'String')]];
end
mdata=[mdata1;mdata];
set(handles.gui_display_table,'Data',mdata);
set(handles.gui_exercise_intensity,'Visible','off')
set(handles.gui_speed_weight,'String','')
set(handles.gui_inclination_sets_reps,'String','')
m20150711_save_global_variables_to_history


function gui_speed_weight_Callback(hObject, eventdata, handles)
% hObject    handle to gui_speed_weight (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of gui_speed_weight as text
%        str2double(get(hObject,'String')) returns contents of gui_speed_weight as a double


% --- Executes during object creation, after setting all properties.
function gui_speed_weight_CreateFcn(hObject, eventdata, handles)
% hObject    handle to gui_speed_weight (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function gui_inclination_sets_reps_Callback(hObject, eventdata, handles)
% hObject    handle to gui_inclination_sets_reps (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of gui_inclination_sets_reps as text
%        str2double(get(hObject,'String')) returns contents of gui_inclination_sets_reps as a double


% --- Executes during object creation, after setting all properties.
function gui_inclination_sets_reps_CreateFcn(hObject, eventdata, handles)
% hObject    handle to gui_inclination_sets_reps (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in gui_meal_ysi_finger_value_verify.
function gui_meal_ysi_finger_value_verify_Callback(hObject, eventdata, handles)
% hObject    handle to gui_meal_ysi_finger_value_verify (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
m20150711_load_global_variables
m20150711_load_global_variables_from_history
mdata1=cell(1,15);
if strcmp(type_of_entry,'Meal')==1
    mdata1(1,[1,11])=[{datestr(clock)}  ['Meal: ', get(handles.gui_meal_ysi_finger_value,'String'),' grams']];
elseif strcmp(type_of_entry,'Snack')==1
    mdata1(1,[1,12])=[{datestr(clock)}  ['Snack: ', get(handles.gui_meal_ysi_finger_value,'String'),' grams']];
elseif strcmp(type_of_entry,'Finger-Stick')==1
    mdata1(1,[1,13])=[{datestr(clock)}  ['Finger-Stick: ', get(handles.gui_meal_ysi_finger_value,'String'),' mg/dl']];
elseif strcmp(type_of_entry,'YSI')==1
    mdata1(1,[1,14])=[{datestr(clock)}  ['YSI: ', get(handles.gui_meal_ysi_finger_value,'String'),' mg/dl']];
elseif strcmp(type_of_entry,'Note')==1
    mdata1(1,[1,15])=[{datestr(clock)} get(handles.gui_meal_ysi_finger_value,'String')];
end
mdata=[mdata1;mdata];
set(handles.gui_display_table,'Data',mdata);
set(handles.gui_meal_ysi_finger_value,'String','');
set(handles.gui_meal_ysi_finger_verification,'Visible','off')
m20150711_save_global_variables_to_history



function gui_meal_ysi_finger_value_Callback(hObject, eventdata, handles)
% hObject    handle to gui_meal_ysi_finger_value (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of gui_meal_ysi_finger_value as text
%        str2double(get(hObject,'String')) returns contents of gui_meal_ysi_finger_value as a double


% --- Executes during object creation, after setting all properties.
function gui_meal_ysi_finger_value_CreateFcn(hObject, eventdata, handles)
% hObject    handle to gui_meal_ysi_finger_value (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in gui_close_hypo_window.
function gui_close_hypo_window_Callback(hObject, eventdata, handles)
% hObject    handle to gui_close_hypo_window (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
set(handles.gui_hypoglycemia_detection,'Visible','off')

% --- Executes on button press in gui_error_close.
function gui_error_close_Callback(hObject, eventdata, handles)
% hObject    handle to gui_error_close (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
set(handles.gui_error_message,'Visible','off')
if strcmp(get(handles.gui_run_algorithm,'Visible'),'off')==1 && strcmp(get(handles.gui_insulin_verification,'Visible'),'off')==1
    set(handles.gui_run_algorithm,'Visible','on')
end
set(handles.gui_backup_data,'Visible','on')


% --- Executes during object deletion, before destroying properties.
function gui_inclination_sets_reps_DeleteFcn(hObject, eventdata, handles)
% hObject    handle to gui_inclination_sets_reps (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

