function [ bolus, basal, hypo_amount ] = mock_algorithm( cgm_in )
%MOCK_ALGORITHM runs a simple if/else algorithm to generate
%   a bolus depending on CGM value
cgm = str2double(cgm_in)
hypo_amount = 0;

if cgm > 250
    bolus = 5;
    basal =1;
elseif cgm > 200 && cgm <=250
    bolus = 2;
    basal =1;
elseif cgm >150 && cgm <=200
    bolus = 1; 
    basal = 1;
elseif cgm >100 && cgm <= 150
    bolus = 0.3;
    basal = 0.5;
else
    hypo_amount = 12;
    basal = 0;
    bolus =0.1;

end
    

end

