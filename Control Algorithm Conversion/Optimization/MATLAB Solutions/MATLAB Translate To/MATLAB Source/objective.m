function [ V ] = objective( Q )
%OBJECTIVE Summary of this function goes here
%   Detailed explanation goes here
global P pP Q_old Y phi;
V=(Q-Q_old)'*pP*(Q-Q_old)+(Y-phi'*Q)'*(Y-phi'*Q);

 end

