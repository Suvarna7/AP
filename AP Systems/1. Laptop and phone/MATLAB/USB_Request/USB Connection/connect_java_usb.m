function [ socket ] = connect_java_usb( ~ )
%CONNECT_JAVA_USB Creates the socket to java program 
%   connected via USB to the phone
%Create a tcp object
% Local host
% Port with Java - 37600 (same as Java interface)

t = tcpip('localhost', 38700, 'NetworkRole', 'server');
% t = tcpip('localhost', 37600, 'NetworkRole', 'server');
%t = tcpclient ('127.0.0.1', 38700, 'NetworkRole', 'client');
set(t, 'InputBufferSize', 50000); 
set(t, 'OutputBufferSize', 50000); 

fopen(t); 
socket = t;


end

