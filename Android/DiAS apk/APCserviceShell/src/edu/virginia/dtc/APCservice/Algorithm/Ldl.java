/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.dtc.APCservice.Algorithm;

import Jama.Matrix;

/**
 *
 * @author Mert
 */
public class Ldl {

    public Matrix L;
    public Matrix A;
    public Matrix X;
    public Matrix D;
    public double S;
    public Matrix d;

    
    public Ldl (Matrix A){
        this.A=A;
    }
    
    public void LdlFind(){  
      int  n= A.getColumnDimension();
      L= new Matrix (n,n);
      X= new Matrix (1,n);
      D= new Matrix (n,n);
      d= new Matrix (1,n);
       
      for (int j=0;j<n;j++){
       L.set(j,j,1);
       S=A.get(j,j);
       
       for(int k=0;k<j;k++)
           S=S-d.get(0, k)*(Math.pow(L.get(j,k),2));
       
       d.set(0,j,S);
       
         for (int i=j+1;i<n;i++){
              L.set(j,i,0);
              S=A.get(i,j);
              
               for(int k=0;k<j;k++)
               S=S-d.get(0, k)*L.get(j,k)*L.get(i, k);
               
              L.set(i,j,S/d.get(0,j));
      } 
    }
      D=diag(d);
    }
    
    
     public Matrix diag (Matrix a){
        Matrix matrice = new Matrix (a.getColumnDimension(),a.getColumnDimension());
        
        for(int j=0;j<matrice.getColumnDimension();j++){
           for(int i=0;i<matrice.getColumnDimension();i++){
                if(i==j){
                    matrice.set(i,j,a.get(0,i));
                }
                else{
                    matrice.set(i,j,0);
                }
            }
        }
        return matrice;
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
    