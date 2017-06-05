package edu.virginia.dtc.SSMservice;

import java.util.Arrays;

public class Interpolator{


    public static final double[] interpLinear(double[] x, double[] y, double[] xi) throws IllegalArgumentException {

        if (x.length != y.length) {
            throw new IllegalArgumentException("X and Y must be the same length");
        }
        if (x.length == 1) {
            throw new IllegalArgumentException("X must contain more than one value");
        }
        double[] dx = new double[x.length - 1];
        double[] dy = new double[x.length - 1];
        double[] slope = new double[x.length - 1];
        double[] intercept = new double[x.length - 1];

        // Calculate the line equation (i.e. slope and intercept) between each point
        for (int i = 0; i < x.length - 1; i++) {
            dx[i] = x[i + 1] - x[i];
//            if (dx[i] == 0) {
//                throw new IllegalArgumentException("X must be montotonic. A duplicate " + "x-value was found");
//            }
//            if (dx[i] < 0) {
//                throw new IllegalArgumentException("X must be sorted");
//            }
            
            dy[i] = y[i + 1] - y[i];
            if (dx[i]>0) {
            	slope[i] = dy[i] / dx[i];
            }
            else {
            	slope[i] = 0;
            }
            
            intercept[i] = y[i] - x[i] * slope[i];
        }

        // Perform the interpolation here
        double[] yi = new double[xi.length];
        for (int i = 0; i < xi.length; i++) {
            if ((xi[i] > x[x.length - 1]) || (xi[i] < x[0])) {
                yi[i] = 112.5;		// Double.NaN
            }
            else {
                int loc = Arrays.binarySearch(x, xi[i]);
                if (loc < -1) {
                    loc = -loc - 2;
                    yi[i] = slope[loc] * xi[i] + intercept[loc];
                }
                else {
                    yi[i] = y[loc];
                }
            }
        }

        return yi;
    }
    
//    public static final double[] interpLinear(long[] x, double[] y, long[] xi) throws IllegalArgumentException {
//
//        double[] xd = new double[x.length];
//        for (int i = 0; i < x.length; i++) {
//            xd[i] = (double) x[i];
//        }
//
//        double[] xid = new double[xi.length];
//        for (int i = 0; i < xi.length; i++) {
//            xid[i] = (double) xi[i];
//        }
//
//        return interpLinear(xd, y, xid);
//    }
    
}  