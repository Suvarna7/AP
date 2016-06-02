function yout=m20160516_calculate_pred(y)
a1=1.93;a2=-0.9305;
yout=zeros(6,1);
yout(1,1)=a1*y(end)+a2*y(end-1);
yout(2,1)=a1*yout(1,1)+a2*y(end);
yout(3,1)=a1*yout(2,1)+a2*yout(1,1);
yout(4,1)=a1*yout(3,1)+a2*yout(2,1);
yout(5,1)=a1*yout(4,1)+a2*yout(3,1);
yout(6,1)=a1*yout(5,1)+a2*yout(4,1);
