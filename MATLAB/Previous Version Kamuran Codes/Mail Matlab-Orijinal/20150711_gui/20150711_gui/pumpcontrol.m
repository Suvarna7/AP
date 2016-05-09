function [resultbolus,activebasaltype,activebolusamount,activebolusdelivered,activebolustype,baterylevel,baterycapasity,baterystate,bateryError,lastbolusamount,lastbolustype,reservoiramount,reservoirsize,reservoirstate,tempbasalrate,tempdurationminutes,tempdurationremainingminutes,glucosevalueIsig,glucosevalueVcrit] = pumpcontrol (bolusamount,basalamount,restartserialport, resetpump, restartglucosesensor,status)
nRows=0;
load pumpvariables;

filebolus = fopen('C://TextRead//bolus.txt','wt');
filebasal = fopen('C://TextRead//basal.txt','wt');
filerestartserialport = fopen('C://TextRead//restartserialport.txt','wt');
fileresetpump = fopen('C://TextRead//resetpump.txt','wt');
filerestartglucosesensor = fopen('C://TextRead//restartglucosesensor.txt','wt');
filecontrol = fopen('C://TextRead//control.txt','wt');
filestatus = fopen('C://TextRead//status.txt','wt');



fileglucose1 = fopen('C://TextWrite//glucosevalueIsig.txt','r');
fileglucose2 = fopen('C://TextWrite//glucosevalueVctr.txt','r');
fileactivebasaltype = fopen('C://TextWrite//activebasaltype.txt','r');
fileactivebolusamount = fopen('C://TextWrite//activebolusamount.txt','r');
fileactivebolusdelivered = fopen('C://TextWrite//activebolusdelivered.txt','r');
fileactivebolustype = fopen('C://TextWrite//activebolustype.txt','r');
filebaterycapasity = fopen('C://TextWrite//baterycapasity.txt','r');
filebaterylevel = fopen('C://TextWrite//baterylevel.txt','r');
filebaterystate = fopen('C://TextWrite//baterystate.txt','r');
filebateryError = fopen('C://TextWrite//Error.txt','r');
filelastbolusamount = fopen('C://TextWrite//lastbolusamount.txt','r');
filelastbolustype = fopen('C://TextWrite//lastbolustype.txt','r');
filereservoiramount = fopen('C://TextWrite//reservoiramount.txt','r');
filereservoirsize = fopen('C://TextWrite//reservoirsize.txt','r');
filereservoirstate = fopen('C://TextWrite//reservoirstate.txt','r');
fileresultbolus = fopen('C://TextWrite//resultbolus.txt','r');
filetempbasalrate = fopen('C://TextWrite//tempbasalrate.txt','r');
filetempdurationminutes = fopen('C://TextWrite//tempdurationminutes.txt','r');
filetempdurationremainingminutes = fopen('C://TextWrite//tempbasaldurationremainingminutes.txt','r');

if(bolusamount~=0)
fprintf(filebolus,'%f\n',bolusamount);
end

if(basalamount~=0)
fprintf(filebasal,'%f\n',basalamount);
end

if(restartserialport~=0)
fprintf(filerestartserialport,'%d\n',restartserialport);
end

if(resetpump~=0)
fprintf(fileresetpump,'%d\n',resetpump);
end

if(restartglucosesensor~=0)
fprintf(filerestartglucosesensor,'%d\n',restartglucosesensor);
end

if(status~=0)
fprintf(filestatus,'%d\n',status);

tic(); 
pause(0.1); 
toc();

activebasaltype=fscanf(fileactivebasaltype, '%d\n');
activebolusamount=fscanf(fileactivebolusamount, '%d\n');
activebolusdelivered=fscanf(fileactivebolusdelivered, '%d\n');
activebolustype=fscanf(fileactivebolustype, '%d\n');
baterycapasity=fscanf(filebaterycapasity, '%d\n');
baterylevel=fscanf(filebaterylevel, '%d\n');
baterystate=fscanf(filebaterystate, '%d\n');
bateryError=fscanf(filebateryError, '%d\n');
lastbolusamount=fscanf(filelastbolusamount, '%d\n');
lastbolustype=fscanf(filelastbolustype, '%d\n');
reservoiramount=fscanf(filereservoiramount, '%d\n');
reservoirsize=fscanf(filereservoirsize, '%d\n');
reservoirstate=fscanf(filereservoirstate, '%d\n');
tempbasalrate=fscanf(filetempbasalrate, '%d\n');
tempdurationminutes=fscanf(filetempdurationminutes, '%d\n');
tempdurationremainingminutes=fscanf(filetempdurationremainingminutes, '%d\n');
else
activebasaltype=0;
activebolusamount=0;
activebolusdelivered=0;
activebolustype=0;
baterycapasity=0;
baterylevel=0;
baterystate=0;
bateryError=0;
reservoirsize=0;
lastbolusamount=0;
lastbolustype=0;
reservoiramount=0;
reservoirstate=0;
tempbasalrate=0;
tempdurationminutes=0;
tempdurationremainingminutes=0;
end

fprintf(filecontrol,'%d\n',1);

tic(); 
pause(0.1); 
toc();

if(bolusamount~=0)
resultbolus=fscanf(fileresultbolus, '%d\n');
else
resultbolus=0;   
end

fprintf(filecontrol,'%d\n',0);
fprintf(filebasal,'%d\n',0);
fprintf(filebolus,'%d\n',0);
fprintf(filerestartserialport,'%d\n',0);
fprintf(fileresetpump,'%d\n',0);
fprintf(filerestartglucosesensor,'%d\n',0);
fprintf(filestatus,'%d\n',0);

fclose(filebolus);
fclose(filebasal);
fclose(filecontrol);
fclose(filerestartserialport);
fclose(fileresetpump);
fclose(filerestartglucosesensor);
fclose(filestatus);

fclose(fileglucose1);
fclose(fileglucose2);
fclose(fileactivebasaltype);
fclose(fileactivebolusamount);
fclose(fileactivebolusdelivered);
fclose(filebaterycapasity);
fclose(filebaterylevel);
fclose(filebaterystate);
fclose(filebateryError);
fclose(filelastbolusamount);
fclose(filelastbolustype);
fclose(filereservoiramount);
fclose(filereservoirsize);
fclose(filereservoirstate);
fclose(filetempbasalrate);
fclose(filetempdurationminutes);
fclose(filetempdurationremainingminutes);
fclose(fileresultbolus);

glucosevalueIsig=0;
glucosevalueVcrit=0;

% activebasaltype=[activebasaltype;activebasaltype];
% activebolusamount=[activebolusamount;activebolusamount];
% activebolusdelivered= [activebolusdelivered;activebolusdelivered];
% activebolustype=[activebolustype;activebolustype];
% baterycapasity=[baterycapasity;baterycapasity];
% baterylevel=[baterylevel;baterylevel];
% baterystate=[baterystate;baterystate];
% bateryError=[bateryError;bateryError];
% lastbolusamount=[lastbolusamount;lastbolusamount];
% lastbolustype=[lastbolustype;lastbolustype];
% reservoiramount=[reservoiramount;reservoiramount];
% reservoirsize=[reservoirsize;reservoirsize];
% reservoirstate=[reservoirstate;reservoirstate];
% tempbasalrate=[tempbasalrate;tempbasalrate];
% tempdurationminutes=[tempdurationminutes;tempdurationminutes];
% tempdurationremainingminutes=[tempdurationremainingminutes;tempdurationremainingminutes];
% resultbolus=[resultbolus;resultbolus];
% 
% bolusamount=[bolusamount;bolusamount];
% basalamount=[basalamount;basalamount];
% restartserialport=[restartserialport;restartserialport];
% restartglucosesensor=[restartglucosesensor;restartglucosesensor]
% resetpump=[resetpump;resetpump];
% status=[status;status];
% glucosevalueIsig=[glucosevalueIsig;glucosevalueIsig];
% glucosevalueVcrit=[glucosevalueVcrit;glucosevalueVcrit];


filename = 'D://alldata.xlsx';
t = datetime('now','InputFormat','yyyy-MM-dd');
t=exceltime(t);

resultbolus=resultbolus/10;

if(nRows==0)
A = {'Time&Date','Active Basal Type','Active Bolus Amount','Active Bolus Delivered','Active Bolus Type','Batery Capasity', 'Batery Level','Batery State','Batery Error','Last Bolus Amount','Last Bolus Type','Reservoir Amount','Reservoir Size','Reservoir State','Temp Basal Rate','Temp Duration Minutes','Temp Duration Remaining Minutes','Result Bolus', 'Glucose Value Isig', 'Glucose Value Vcrit','Bolus Amount','Basal Amount','Reset Pump', 'Restart Glucose Sensor','Restart Serial Port','Status';t,activebasaltype,activebolusamount,activebolusdelivered,activebolustype,baterycapasity,baterylevel,baterystate,bateryError,lastbolusamount,lastbolustype,reservoiramount,reservoirsize,reservoirstate,tempbasalrate,tempdurationminutes,tempdurationremainingminutes,resultbolus,glucosevalueIsig,glucosevalueVcrit,bolusamount,basalamount,resetpump,restartglucosesensor,restartserialport,status}; 
else
A = {t,activebasaltype,activebolusamount,activebolusdelivered,activebolustype,baterycapasity,baterylevel,baterystate,bateryError,lastbolusamount,lastbolustype,reservoiramount,reservoirsize,reservoirstate,tempbasalrate,tempdurationminutes,tempdurationremainingminutes,resultbolus,glucosevalueIsig,glucosevalueVcrit,bolusamount,basalamount,resetpump,restartglucosesensor,restartserialport,status}; 
end    


nRows = nRows +1;

% convert number to string
b = num2str(nRows);

% if you want to add data to the collum A you make concat strings
c = strcat('A', b);


xlswrite(filename,A,'Sheet1',c);

clear A fileglucose1 fileglucose2 fileactivebasaltype fileactivebolusamount fileactivebolusdelivered fileactivebolustype filebaterycapasity filebaterylevel filebaterystate t filebateryError filelastbolusamount filelastbolustype filereservoiramount filereservoirsize filereservoirstate fileresultbolus filetempbasalrate filetempdurationminutes filetempdurationremainingminutes bolusamount basalamount resetpump restartglucosesensor restartserialport status b c filestatus filerestartserialport fileresetpump filecontrol filebolus filebasal ans filerestartglucosesensor;
save pumpvariables ;

end