function yout=m20160516_calculate_pred(y)
a1=1.902079262463561;a2=-0.902805333061209;
yout=zeros(4,1);
yout(1,1)=a1*y(end)+a2*y(end-1);
yout(2,1)=a1*yout(1,1)+a2*y(end);
yout(3,1)=a1*yout(2,1)+a2*yout(1,1);
yout(4,1)=a1*yout(3,1)+a2*yout(2,1);
% yout(5,1)=a1*yout(4,1)+a2*yout(3,1);
% yout(6,1)=a1*yout(5,1)+a2*yout(4,1);
