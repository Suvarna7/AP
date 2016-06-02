%%%%%%%%%%%%%%%%
function [tr,pr,wr,qr,yq_estimate]=LW_PLS_original_JF(X,Y,x_q,R,thita_m_i,phi)
[N,M]=size(X);
[N,L]=size(Y);
%%%%%%%%%%%%the number of samples should be the N*times
thita=diag(thita_m_i);x=X';y=Y';
    r=1;
    for i=1:N
        d(i)=sqrt((x(:,i)-x_q)'*thita*(x(:,i)-x_q));
    end
    sigma_d=std(d);
    w=exp(-d/sigma_d/phi);
    omega=diag(w);
    for m=1:M
        x_bar(m)=w*x(m,:)'/sum(w);
    end
    for l=1:L
        y_bar(l)=w*y(l,:)'/sum(w);
    end
    Xr=X-ones(N,1)*x_bar;
    Yr=Y-ones(N,1)*y_bar;
    x_q_r(:,r)=x_q-x_bar';
    t_mult_q=zeros(1,L);
    for r=1:R
    [eig_vector,eig_value]=eigs(Xr'*omega*Yr*Yr'*omega*Xr);
    wr(:,r)=eig_vector(:,1);
    tr(:,r)=Xr*wr(:,r);
    pr(:,r)=Xr'*omega*tr(:,r)/(tr(:,r)'*omega*tr(:,r));
    qr(:,r)=Yr'*omega*tr(:,r)/(tr(:,r)'*omega*tr(:,r));
    tq(:,r)=x_q_r(:,r)'*wr(:,r);
    t_mult_q= tq(:,r)*qr(:,r)+t_mult_q;
    Xr=Xr-tr(:,r)*pr(:,r)';
    Yr=Yr-tr(:,r)*qr(:,r)';
    x_q_r(:,r+1)=x_q_r(:,r)-tq(:,r)*pr(:,r);
    end
    yq_estimate=y_bar'+t_mult_q;
end
    
    
  