/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.dtc.APCservice.Algorithm;

import Jama.Matrix;
import edu.virginia.dtc.APCservice.DataManagement.MatrixDatabaseManager;

/**
 * Save class used to save variables of the algorithm
 * @author Caterina Lazaro
 * @version 2.0 March 2016
 */
public class Save {

	//Matrices database manager
	private MatrixDatabaseManager mDataManager;

	/**
	 * Create a Save object 
	 * @param dataManager - to start the database Manager
	 */
	public Save(MatrixDatabaseManager dataManager){
		mDataManager = dataManager;
	}


	/**
	 * Save a 2D matrix into the database
	 * @param matrix - Matrix to be saved
	 * @param matrix_name - name of the amtrix
	 */
	public void save (Matrix matrix, String matrix_name) { 
		//Convert matrix into a double[][]
		double [][] data = matrix.getArray();
		//Create the table
		mDataManager.createTable(matrix_name, data[0].length, data.length);
		//Update rows of the table
		for (int i = 0; i < data.length; i ++){
			mDataManager.addRowToMatrix(matrix_name, data[i]);
		}
	}


	/**
	 * Save a double number in the database
	 * @param value  - to be saved
	 * @param matrix_name - name of the matrix where it is saved
	 */
	public void saveDouble (double value,String matrix_name) { 
		//Create the table in the database
		mDataManager.createTable(matrix_name, 1, 1);
		//Update the double value
		mDataManager.addRowToMatrix(matrix_name, new double[]{value});

	}

	/**
	 * Save a 2D matrix indexed as part of a 3D matrix
	 * @param matrix - 2D matrix values
	 * @param matrix_name - matrix name
	 * @param index - index inside the 3D
	 */
	public void save3DIndexed (Matrix matrix,String matrix_name, int index){  
		//Create the 2D matrix part of the 3D whole
		save(matrix, matrix_name+index);

	}

	//TODO
	public void saveStringArray (String [] matrix,String matrix_name){   ///It is working
		//Create the table
		mDataManager.createTable(matrix_name, 1, matrix.length);
		//Update String values
		for (int i=0; i < matrix.length; i ++)
			mDataManager.addStringToMatrix(matrix_name, matrix[i]);

	}

	/**
	 * Change a given 2D matrix in a 3D matrix
	 * @param matrix
	 * @param kj - index in 3D matrix
	 * @return
	 */
	public Matrix change (double [][][] matrix,int kj){
		//Matrix updated
		Matrix newMatrix= new Matrix (matrix.length,matrix[0].length);

		for(int i=0;i<matrix.length;i++){
			for(int j=0;j<matrix[0].length;j++){
				newMatrix.set(i, j,matrix[i][j][kj]);
			}
		}

		return newMatrix;
	} 


	
	/**
	 * Print the values of a given matrix
	 * @param m - matrix
	 * @param name - name to be printed
	 */
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
