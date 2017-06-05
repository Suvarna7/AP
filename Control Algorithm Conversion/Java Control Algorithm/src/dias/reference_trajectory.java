/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import Jama.Matrix;

/**
 *
 * @author User
 */
public class reference_trajectory {
    
    private double Y_b;
    private double r_c;
    private int hor;
    private double alpha;
    
    public reference_trajectory (double Y_b, double r_c, int hor, double alpha){
        this.Y_b=Y_b;
        this.r_c=r_c;
        this.alpha=alpha;
        this.hor=hor;
    }
    
       public Matrix referencetrajectory(){
      /* /////////////////////////Input Reference_trajectory///////////////////////////////////////////////////////////////////////////////////////////
          System.out.println("///////////////////////////////////////Input_ Reference_ Trajectory//////////////////////////////////////////////////////");
          System.out.println(Y_b+   "    Y_b");
          System.out.println(r_c+   "    r_c");
          System.out.println(alpha+   "   alpha");
          System.out.println(hor+   "    hor");
          System.out.println("///////////////////////////////////////Input_ Reference_ Trajectory//////////////////////////////////////////////////////");
       /////////////////////////Input Reference_trajectory///////////////////////////////////////////////////////////////////////////////////////////*/
           
          Matrix w= new Matrix(hor+1,1);  
           w.set(0,0,Y_b); 
           
           for(int i=1; i<hor+1; i++){
               w.set(i,0,alpha*w.get(i-1,0)+(1-alpha)*r_c); 
           }
       
              for(int i=1 ;i<hor+1; i++){
               w.set(i-1,0,w.get(i,0));
           }
              
            Matrix wresult= new Matrix(hor,1);  
    
            for(int i=0;i<hor;i++)
            wresult.set(i,0,w.get(i,0));
                
      /*    //////////////////////////////////////////Output Reference_trajectory//////////////////////////////////////////////////////////////////////////       
          System.out.println("///////////////////////////////////////Output_ Reference_ Trajectory//////////////////////////////////////////////////////");
          printMatrix(wresult,"wresult");
          System.out.println("///////////////////////////////////////Output_ Reference_ Trajectory//////////////////////////////////////////////////////");
          //////////////////////////////////////////Output Reference_trajectory////////////////////////////////////////////////////////////////////////// */
            
          return wresult; 
       }  
       
           public static void printMatrix(Matrix m, String name){
		System.out.print("\n "+name+": \n{");
		for (double[] row: m.getArray()){
			for (double val: row)
				System.out.print(" "+val);
			System.out.println();
		}
		System.out.println("}");
	}
}
