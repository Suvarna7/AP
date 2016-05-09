    function w=reference_trajectory(Y_b,r_c,hor,alpha)
        w=Y_b;
        for i=2:hor+1
            w(i,1)=alpha*w(i-1,1)+(1-alpha)*r_c;
        end
        w=w(2:hor+1,1);
    end