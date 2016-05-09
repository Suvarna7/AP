%%%%%%%%%%%%%%%%
function [tr,pr,wr,qr,yq_estimate]=LW_PLS_original_JF(X,Y,x_q,R,thita_m_i,phi)
[N,M]=size(X)
[N,L]=size(Y)
%%%%%%%%%%%%the number of samples should be the N*times
thita=diag(thita_m_i)
x=X' 
y=Y
    r=1;
    d=0;
    for i=1:N
        x(:,i)
        x_q
        (x(:,i)-x_q)'
        thita
        (x(:,i)-x_q)'*thita
        d(i)=sqrt((x(:,i)-x_q)'*thita*(x(:,i)-x_q));
    end
    d
    sigma_d=std(d)
    w=exp(-d/sigma_d/phi)
    omega=diag(w)
    x
    for m=1:M
        aradeger=m
        aradeger1=x(m,:)'
        aradeger2=M
        aradeger3=sum(w)
        aradeger4=w*x(m,:)'
        x_bar(m)=w*x(m,:)'/sum(w);
    end
    x_bar
    y
    for l=1:L
        aradeger5=l
        aradeger6=y(l,:)'
        aradeger7=L
        aradeger8=sum(w)
        aradeger9=w*y(l,:)'
        y_bar(l)=w*y(l,:)'/sum(w);
    end
    y_bar
    X
    aradeger10=ones(N,1)*x_bar
    Xr=X-ones(N,1)*x_bar
    Yr=Y-ones(N,1)*y_bar
    x_q_r(:,r)=x_q-x_bar'
    t_mult_q=0 %  t_mult_q=zeros(1,L)
   % t_mult_q=zeros(1,L)
    for r=1:R
       aradeger11= Xr'
       aradeger12=omega
       aradeger13=Xr'*omega
       aradeger14=Xr'*omega*Yr
       aradeger15=Xr'*omega*Yr*Yr'
       aradeger16=Xr'*omega*Yr*Yr'*omega
       aradeger17=Xr'*omega*Yr*Yr'*omega*Xr
    [eig_vector,eig_value]=eigs(Xr'*omega*Yr*Yr'*omega*Xr)
    wr(:,r)=eig_vector(:,1)
    tr(:,r)=Xr*wr(:,r)
    aradeger18=Xr'*omega
    aradeger19=Xr'*omega*tr(:,r)
    aradeger20=(tr(:,r)'*omega)
    aradeger21=(tr(:,r)'*omega*tr(:,r))
    pr(:,r)=Xr'*omega*tr(:,r)/(tr(:,r)'*omega*tr(:,r))
    qr(:,r)=Yr'*omega*tr(:,r)/(tr(:,r)'*omega*tr(:,r))
    tq(:,r)=x_q_r(:,r)'*wr(:,r)
    aradeger22=tq(:,r)*qr(:,r)
    t_mult_q= tq(:,r)*qr(:,r)+t_mult_q   %t_mult_q= tq(:,r)*qr(:,r)+t_mult_q
    aradeger23=tr(:,r)*pr(:,r)'
    Xr=Xr-tr(:,r)*pr(:,r)'
    Yr=Yr-tr(:,r)*qr(:,r)'
    x_q_r(:,r+1)=x_q_r(:,r)-tq(:,r)*pr(:,r)
    end
    yq_estimate=y_bar'+t_mult_q
end
    
    
  