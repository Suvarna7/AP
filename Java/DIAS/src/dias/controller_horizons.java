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
public class controller_horizons {
    public Matrix A_state;
    public Matrix B_state;
    public Matrix C_state;
    public int N1;
    public int N2;
    public int Nu;
    public int N;
    public int m;
    public int n;
    public int Acol;
    public int Arow;
  
   
    public controller_horizons (Matrix A_state,Matrix B_state,Matrix C_state, int N1, int N2, int Nu){
        this.A_state=A_state;
        this.B_state=B_state;
        this.C_state=C_state;
        this.N1=N1;
        this.N2=N2;
        this.Nu=Nu;
    }
    
      public void controller_horizon(){
          
       /*   /////////////////////////////////INPUT CONTROLLER_HORIZONS//////////////////////////////////////////////////////////////////////////////////////
          System.out.println("//////////////////////////INPUT_CONTROLLER_HORIZONS//////////////////////////////////////////////////////////////////////");
          printMatrix(A_state,"A_state");
          printMatrix(B_state,"B_state");
          printMatrix(C_state,"C_state");
          System.out.println(N1+"    N1");
          System.out.println(N2+"    N2");
          System.out.println(Nu+"    Nu");
          System.out.println("//////////////////////////INPUT_CONTROLLER_HORIZONS//////////////////////////////////////////////////////////////////////");
          /////////////////////////////////INPUT CONTROLLER_HORIZONS//////////////////////////////////////////////////////////////////////////////////////*/
          
           Matrix A=new Matrix(A_state.getColumnDimension(),A_state.getRowDimension());
           
          
           Acol=A_state.getColumnDimension();
           Arow=A_state.getRowDimension();
            
           Matrix A_state1 = new Matrix (Acol,Arow);
           Matrix A_state_temp=new Matrix (Acol,Arow);
            
           for(int is=0;is<Acol;is++)
               for(int js=0;js<Arow;js++)
               A.set(is, js,A_state.get(is, js) );
                
           m=B_state.getRowDimension();
           n=B_state.getColumnDimension();
           
           N=(N2-N1);
         
           M= new Matrix(N,m);
           double [][][] L= new double [N][Nu][n];
           int kk=N1;
           
             for(int i=0;i<Acol;i++)
               for(int j=0;j<Arow;j++)
               A_state1.set(i, i, 1);
               A_state_temp=A_state.times(A_state1);
                   
           for(int k=0;k<N;k++){
               
               for(int i=0;i<Acol;i++)
               for(int j=0;j<Arow;j++)
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
                    for(int j=0 ;j< (A_state1.getColumnDimension()); j++ ){
                          M.set(k,s, (C_state.times(A_state1)).get(i,j));
               s++;
                       }
               }
                   kk++;          
           }
 
           kk=N1;
           
             for(int i=0;i<Acol;i++){
               for(int j=0;j<Arow;j++){
               A_state1.set(i, i, 1);
               A_state_temp.set(i, i, 1);
               }
             }
             
                 for(int i=0;i<Acol;i++)
               for(int j=0;j<Arow;j++)
               A_state.set(i, j, A.get(i, j));
                   
           for(int k=0;k<N;k++){
                for(int j=0;j<Nu;j++){
                          for(int i=0;i<n;i++){
                              if(kk-j-1<0)
                                  L[k][j][i]=0;
                              else{  
               for(int ix=0;ix<Acol;ix++)
               for(int jx=0;jx<Arow;jx++)
                   if(ix==jx)
               A_state1.set(ix, ix, 1);
                   else
               A_state1.set(ix, jx, 0);         
                  
               A_state_temp=A_state.times(A_state1);
               
               A_state=A_state_temp.times(A_state1);
               
               for(int t=0;t<kk-j-1;t++){
               A_state1=A_state.times(A_state1);
 
               }  
                 Matrix B_state1=new Matrix(m,1);
               
                 for(int d=0;d<m;d++){
                     B_state1.set(d, 0, B_state.get(d, i));
                 }
                 L[k][j][i]=(C_state.times(A_state1).times(B_state1).get(0, 0));  
                               }
                              }
                 }
                kk++;
           }
    
                  LL=new Matrix(N,Nu);
                  LL_ee=new Matrix(N,Nu);
                  LL_gsr=new Matrix(N,Nu);
             
            for(int b=0;b<N;b++){
               for(int z=0;z<Nu;z++){   
                      LL.set(b, z, L[b][z][0]);
                      LL_ee.set(b, z, L[b][z][1]);
                      LL_gsr.set(b, z, L[b][z][2]);
               }
               }
      /*   /////////////////////////////////OUTPUT CONTROLLER_HORIZONS//////////////////////////////////////////////////////////////////////////////////////
          System.out.println("//////////////////////////OUTPUT_CONTROLLER_HORIZONS//////////////////////////////////////////////////////////////////////");
          printMatrix(LL,"LL");
          printMatrix(LL_ee,"LL_ee");
          printMatrix(LL_gsr,"LL_gsr");
          printMatrix(M,"M");
          System.out.println("//////////////////////////OUTPUT_CONTROLLER_HORIZONS//////////////////////////////////////////////////////////////////////");
          /////////////////////////////////OUTPUT CONTROLLER_HORIZONS//////////////////////////////////////////////////////////////////////////////////////*/       
            
            
      }
      
      public Matrix LL;
      public Matrix LL_ee;
      public Matrix LL_gsr;
      public Matrix M;  
     
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


