%two line angle detection function for two line with same start point jianyuan feng 150520
function angle=angle_detection_JF(delta_x,yy,y1,y2)
angle1=atan(abs((y1-yy)/delta_x));
angle2=atan(abs((y2-yy)/delta_x));
if (y1-yy)*(y2-yy)>=0
    angle=abs(angle1-angle2);
else
    angle=angle2+angle1;
end
end

    
    
