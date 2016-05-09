function [X Y T]=cluster1_JF(y)
y
X1=[1 y(1)]
Y1=[2 y(2)]
length(y)
X2=[length(y)-1, y(length(y)-1)]
Y2=[length(y) y(length(y))]
[X Y]= node( X1,Y1,X2,Y2 )
T=5;
if X<length(y) && 1<=X && X<=length(y)
    if Y>=max(y)
        T=1;
    elseif Y<min(y)
        T=2;
    end
    
end
if sum((y(2:end)-y(1:end-1))>0.05)>=length(y)/2 && T~=1 && T~=2 || sum((y(2:end)-y(1:end-1))>0)==length(y)-1
        T=3;
elseif sum((y(2:end)-y(1:end-1))<-0.05)>=length(y)/2 && T~=1 && T~=2 || sum((y(2:end)-y(1:end-1))<0)==length(y)-1
        T=4;
elseif T~=1 && T~=2
    T=5;

end

function [X Y]= node( X1,Y1,X2,Y2 )

    if X1(1)==Y1(1)
    X=X1(1);
    k2=(Y2(2)-X2(2))/(Y2(1)-X2(1));
    b2=X2(2)-k2*X2(1); 
    Y=k2*X+b2;
end
if X2(1)==Y2(1)
    X=X2(1);
    k1=(Y1(2)-X1(2))/(Y1(1)-X1(1));
    b1=X1(2)-k1*X1(1);
    Y=k1*X+b1;
end

if X1(1)~=Y1(1)&& X2(1)~=Y2(1)
    k1=(Y1(2)-X1(2))/(Y1(1)-X1(1))
    k2=(Y2(2)-X2(2))/(Y2(1)-X2(1))
    b1=X1(2)-k1*X1(1)
    b2=X2(2)-k2*X2(1)
    if k1==k2
       X=[100000];
       Y=[100000];
    else
    X=(b2-b1)/(k1-k2);
    Y=k1*X+b1;
    end
end
end
end


