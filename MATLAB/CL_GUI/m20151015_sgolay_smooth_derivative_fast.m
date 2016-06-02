function [xp,dc]=m20151015_sgolay_smooth_derivative_fast(x,p,w,d,dc)
halfwin=((w+1)/2)-1;

if isempty(dc)==1
    dc=zeros(w,w);
    for ii1=-halfwin:halfwin
        for ii2=-halfwin:halfwin
            dc(ii1+halfwin+1,ii2+halfwin+1)=m20151208_Weight(ii1,ii2,halfwin,p,d);
        end
    end
end
[~,mx]=size(x);
xp=zeros(1,mx);
for ii1=1:mx
xp(1,ii1)=dc(:,w)'*x(:,ii1);
end


