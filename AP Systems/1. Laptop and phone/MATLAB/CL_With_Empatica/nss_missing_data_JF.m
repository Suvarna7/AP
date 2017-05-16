function [noise_signal]=nss_missing_data(percentage)
%%%%%%%%%%%%%%noise signal simulation%%%%%%%%%%%%%%%%%%%
if rand(1)<percentage/100
    noise_signal=0;
else
    noise_signal=1;
end
end
    