/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpopt;

/**
 *
 * @author Cat
 */
public class JPOPT {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Create the problem
        HS071 hs071 = new HS071();

        // Get the default initial guess
        double x[] = hs071.getInitialGuess();

        // solve the problem
        hs071.solve(x);

        //Dispose of the optimization object
        hs071.dispose();

        /*System.out.println("RUN AGAIN!!!");
                hs071 = new HS071();
                hs071.solve(x);*/
        
        //OptRecursive
        //OptRecursive(double Y, Matrix phi, Matrix Q_old, Matrix P_old, double lamda_old, double[] upperlimit, double[] lowerlimit) {

        OptRecursive opt = new OptRecursive();
        int solution = opt.solve(opt.getInitialGuess());
        System.out.println("Obj function Val: "+opt.getObjVal());
        System.out.println("Status: " +opt.getStatus() + " vs (solve) "+solution);
        printDoubleArrayMatrix(opt.Q_optimizing_keepValue.getArray(), "X_result");
        
    }
    
    /**
     * Function to print a double[][] kind of matrix on terminal
     *
     * @param printing
     */
    public static void printDoubleArrayMatrix(double[][] printing, String matrix_name) {
        System.out.println(matrix_name);
        for (double[] column : printing) {
            for (double val : column) {
                System.out.print(val + ", ");
            }
            System.out.print("\n");
        }
    }

}
