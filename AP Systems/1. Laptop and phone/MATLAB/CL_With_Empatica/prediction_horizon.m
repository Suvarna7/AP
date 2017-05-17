function M=prediction_horizon(A_state,C_state,N1,N2)
[m n]=size(A_state);
N=N2-N1;
M=zeros(N,m);
kk=N1;
for k=1:N
    M(k,:)=C_state*(A_state^(kk-1));
    kk=kk+1;
end
