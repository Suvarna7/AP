%%%%%%%%%%%%%%%%
function [tr,pr,wr,qr,yq_estimate]=LW_PLS_original_JF(X,Y,x_q,R,thita_m_i,phi)
[N,M]=size(X);
[N,L]=size(Y);
%%%%%%%%%%%%the number of samples should be the N*times
thita=diag(thita_m_i)
x=X';y=Y';
    r=1;
    x_q
    x;
    for i=1:N
        d(i)=sqrt((x(:,i)-x_q)'*thita*(x(:,i)-x_q));
    end
    d
    sigma_d=std(d);
    
    w=exp(-d/sigma_d/phi);
  
    omega=diag(w);
    x(1,1);
 x;
 w;
 sum(w);
    for m=1:M
        x_bar(m)=w*x(m,:)'/sum(w);
    end
      x_bar;
     
    for l=1:L
        y_bar(l)=w*y(l,:)'/sum(w);
    end
    y_bar;
    X;
    x_bar;
    Xr=X-ones(N,1)*x_bar;
    Xr;
    Y;
    y_bar;
    Yr=Y-ones(N,1)*y_bar;
    Yr;
    x_q;
    x_bar;
    x_q_r(:,r)=x_q-x_bar';
    t_mult_q=zeros(1,L);
      
    for r=1:R
    Xr;
   % s=d1
    omega;
    Yr;
    (Xr'*omega*Yr*Yr'*omega*Xr);
    [eig_vector,eig_value]=eigs(Xr'*omega*Yr*Yr'*omega*Xr);
    wr(:,r)=eig_vector(:,1);
    wr(:,r);
    
    Xr;
    wr(:,r);
    tr(:,r)=Xr*wr(:,r);
    tr;
    Xr'*omega*tr(:,r);
    (tr(:,r)'*omega*tr(:,r));
   tr;
   (tr(:,r)'*omega*tr(:,r));
    pr(:,r)=Xr'*omega*tr(:,r)/(tr(:,r)'*omega*tr(:,r));
  pr;   
    qr(:,r)=Yr'*omega*tr(:,r)/(tr(:,r)'*omega*tr(:,r));
    x_q_r(:,r);
    wr(:,r);
    tq(:,r)=x_q_r(:,r)'*wr(:,r);
    tq ; 
    t_mult_q;
    tq(:,r);
    qr(:,r);
    tq(:,r)*qr(:,r);
    qr(:,1);
    t_mult_q= tq(:,r)*qr(:,r)+t_mult_q;
    t_mult_q;
    tq(:,r);
    
    tr(:,r);
    pr(:,r)';
    qr(:,r)';
    
    mert=tr(:,r)*pr(:,r)';
    Xr=Xr-tr(:,r)*pr(:,r)';
    Yr=Yr-tr(:,r)*qr(:,r)';
    x_q_r(:,r+1)=x_q_r(:,r)-tq(:,r)*pr(:,r);
    x_q_r;
    wr;
    end
    t_mult_q;
    y_bar';
    yq_estimate=y_bar'+t_mult_q;
end
    
    
  