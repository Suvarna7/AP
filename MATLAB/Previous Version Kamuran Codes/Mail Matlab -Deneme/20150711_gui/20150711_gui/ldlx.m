% Numerical Mathematics and Computing, Fifth Edition
% Ward Cheney & David Kincaid
% Brooks/Cole Publ. Co.
% (c) 2003
%
% file: ldl.m
%
%  Produces the LDL' factorization of a given symmetric matrix A. 
%  There are no square roots.
%  The test matrix  is created first by arbitrary choice
%  of L (unit lower triangular) and D (diagonal matrix).
function [LL,DD] = ldlx (matrice)

m=size(matrice,1);

% L=[1 0 0 0; 7 1 0 0; -4 3 1 0; 13 -6 5 1]
% X=[5 -1 2 -3]
% D=diag(X)
% A=L*D*L'

% The code now sets X=0, D=0, and L=0. It then should reconstruct
% them from A alone, using the LDL' factorization algorithm.

X=zeros(m);
D=diag(X);
L=zeros(m,m);
d=zeros(1,m);

for j=1:1:m
       L(j,j)=1;
       S=matrice(j,j);
              for k=1:1:j-1
                     S=S-d(k)*L(j,k)^2;
              end
       d(j)=S;
       for i=j+1:1:m
              L(j,i)=0;
              S=matrice(i,j);
              for k=1:1:j-1
                     S=S-d(k)*L(i,k)*L(j,k);
              end
              L(i,j)=S/d(j);
       end
end
LL=L
DD=diag(d)
A=L*DD*L';

