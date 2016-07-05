/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication1;
import de.xypron.jcobyla.Calcfc;
import de.xypron.jcobyla.Cobyla;
import de.xypron.jcobyla.CobylaExitStatus;
import org.apache.commons.math3.analysis.MultivariateFunction;


/**
 *
 * @author Cat
 */
public class JavaApplication1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Calcfc calcfc = new Calcfc() {
            @Override
            public double compute(int i, int i1, double[] doubles, double[] doubles1) {
                System.out.println(doubles[0]);
                /*double result = 0;
                for (double val: doubles)
                    result += Math.pow(val, 2);
               
                return result;*/
                return eggholder(doubles);
            }
            //Init with compute function
        };

        double[] egg_inputs = new double[]{512,512}; 
        double [] x0 = new double[]{1, 1, 1, 1, 1,
                                   1, 1, 1, 1, 1, 
                                   1, 1, 1, 1, 1,
                                   1, 1, 1, 1, 1,
                                   1, 1, 1, 1 };
        CobylaExitStatus result = Cobyla.findMinimum(calcfc, 2, 0, egg_inputs , 100, 1e-20, 1, 50000);

        System.out.println("Exit status: "+result);
    }
    
    /**
     * Egg-holder function
     * @param point
     * @return 
     */
       public static double eggholder(double[] point) {
           double x = point[0];
           double y = point[1];
           double sin1 = Math.sin(Math.sqrt(Math.abs((x/2) + (y + 47))));
           double sin2 = Math.sin(Math.sqrt(Math.abs(x - (y + 47))));
           return (-1 * (y + 47) * sin1) - (x * sin2);
       }

    
}
