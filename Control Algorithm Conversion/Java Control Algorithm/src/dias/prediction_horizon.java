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
public class prediction_horizon {
     
    Matrix M; 
    Matrix A_state;
    Matrix C_state;
    int N1;
    int N2;
    
    public prediction_horizon(Matrix A_state , Matrix C_state, int N1 , int N2){
        
        this.A_state=A_state;
        this.C_state=C_state;
        this.N1= N1;
        this.N2=N2;
        
    }
    
    public Matrix prediction_horizons(){
      /*  ///////////INPUT PREDICTION_HORIZONS////////////////////////////////////////////////////////////////////////////////////////////////////////////
        System.out.println("////////////////////////////////////////////INPUT PREDICTION_HORIZONS///////////////////////////////////////////////////////");
        printMatrix(A_state,"A_state");
        printMatrix(C_state,"C_state");
        System.out.println(N1+"     N1");
        System.out.println(N2+"     N2");
        System.out.println("////////////////////////////////////////////INPUT PREDICTION_HORIZONS///////////////////////////////////////////////////////");
         ///////////INPUT PREDICTION_HORIZONS////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
           int m=A_state.getRowDimension();
           int n=A_state.getColumnDimension();
           int N=N2-N1;
           Matrix A_state1 = new Matrix (m,m);
           Matrix A_state_temp=new Matrix (m,m);
           M= new Matrix(N,m);
   
           int kk=N1;
           
           
             for(int i=0;i<m;i++)
               for(int j=0;j<m;j++)
               A_state1.set(i, i, 1);

           
           A_state_temp=A_state.times(A_state1);
                   
           for(int k=0;k<N;k++){
               
             
               for(int i=0;i<m;i++)
               for(int j=0;j<m;j++)
               if(i==j)
               A_state1.set(i, i, 1);
               else
               A_state1.set(i, j, 0);
           
               A_state=A_state_temp.times(A_state1);
               for(int i=0;i<kk-1;i++){
               A_state1=A_state.times(A_state1);
               }
                 
               int s=0;
               for(int i=0 ;i< (C_state.getRowDimension()); i++ ){
                    for(int j=0 ;j< (A_state.getColumnDimension()); j++ ){
               M.set(k,s, (C_state.times(A_state1)).get(i,j));
               s++;
                       }
               }
                   kk++;          
           }
           
      /*  ///////////OUTPUT PREDICTION_HORIZONS////////////////////////////////////////////////////////////////////////////////////////////////////////////
        System.out.println("////////////////////////////////////////////OUTPUT PREDICTION_HORIZONS///////////////////////////////////////////////////////");
        printMatrix(M,"M");
        System.out.println("////////////////////////////////////////////OUTPUT PREDICTION_HORIZONS///////////////////////////////////////////////////////");
         ///////////OUTPUT PREDICTION_HORIZONS////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
           
           return M;
           
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

