function [addsignal]=addon_noise_JF(type,am,length_add,gb,kj)
if rand(1,1)>0.5
    signal=1;
else
    signal=-1;
end
if type==1       %'whitenoise'
  addsignal(1:length_add)=2*am*(rand(length_add,1)-0.5);
else if type==2  %'driftchange'
      addsignal(1:length_add)=signal*am/length_add*[1:length_add]';  
    else if type==3 %step change
          addsignal(1:length_add)=signal*am*ones(length_add,1); 
        else if type==4 %outlier
            addsignal(1:length_add)=signal*am/((length_add+1)/2)^2*(-[1:length_add].*([1:length_add]-length_add-1)); 
            else if type==5% data missing
                    addsignal(1:length_add)=NaN;
                else if type==6% signal stuck
                        addsignal(1:length_add)=-(gb(kj:kj+length_add-1)-gb(kj-1)*ones(length_add,1));
                    end
                end     
            end
        end
    end
end
          
end
  