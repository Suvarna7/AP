function [M, LL,LL_ee,LL_gsr]=controller_horizon(A_state,B_state,C_state,N1,N2,Nu)
[m, n]=size(B_state);
N=N2-N1;
L=zeros(N,Nu,n);
M=zeros(N,m);
kk=N1;
for k=1:N
    M(k,:)=C_state*(A_state^(kk-1));
    kk=kk+1;
end
kk=N1;
%A_state
for k=1:N
    for j=1:Nu
        for i=1:n
            if kk-j<0
                L(k,j,i)=0;
            else
                %(A_state^(kk-j))
                %C_state
                B_state(:,i)
                L(k,j,i)=C_state*(A_state^(kk-j))*B_state(:,i)
            end
        end
    end
    kk=kk+1;
end
LL=L(:,:,1);LL_ee=L(:,:,2);LL_gsr=L(:,:,3);

end