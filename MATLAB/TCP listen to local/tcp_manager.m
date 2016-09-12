%Create a tcp object
% Local host
% Port with Java - 1045
t = tcpip('127.0.0.1', 38700, 'NetworkRole', 'server');
%tclient = tcpclient ('127.0.0.1', 38700, 'NetworkRole', 'client');
%fopen(t);
while (0==0)
    data = fread(t, 10)
end
%t = tcpclient('localhost', 7)
%t = tcpclient('127.0.0.1', 38700 )
%t = tcpclient('127.0.0.1', 38700)

%Listen to it, for java inputs
%Reads 10 bytes, with type double
%read(t, 10)

%Send result of MATLAB algorithm
% data = 1:10;
% write(t, data)