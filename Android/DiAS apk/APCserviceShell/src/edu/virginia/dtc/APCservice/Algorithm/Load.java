/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.dtc.APCservice.Algorithm;


import edu.virginia.dtc.APCservice.DataManagement.MatrixDatabaseManager;
import Jama.Matrix;



/**
 * SaveAllData class used to load variables for the algrotihm
 * @author Caterina Lazaro
 * @version 2.0 March 2016
 */
public class Load {
	//Matrices database manager
	private MatrixDatabaseManager mDataManager;

	/**
	 * Create a Load object 
	 * @param dataManager - to start the database Manager
	 */
	public Load( MatrixDatabaseManager dataManager){
		mDataManager = dataManager;
	}


	/**
	 * Loads a 2D matrix from the database
	 * @param matrix - name of the matrix
	 * @return (Matrix) the matrix 
	 */

	public Matrix loadMatrix(String matrix) {

		//Read data from database  & Update the return matrix
		return new Matrix (mDataManager.readMatrix(matrix));
	}


	/**
	 * Loads a 3D matrix from the database
	 * @param matrix - name of the matrix
	 * @return (double[][][]) the matrix
	 */
	public double [][][] load3D(String matrix){
		//Read data from database & create return list
		return mDataManager.readMatrix3D(matrix);
	}




	/**
	 * Loads a dobule number from the database
	 * @param matrix - name of the matrix containing the double
	 * @return the double 
	 */

	public double loadDouble(String matrix) {
		double sol = 0;
		//Read data from database  & Update the return matrix
		double[][] readData = mDataManager.readMatrix(matrix);
		
		if(readData.length == 1 && readData[0].length ==1){
			//Return double if the result is a 1x1 matrix
			sol = readData[0][0];
		}else{
			//Wrong table chosen
		}
		return sol;
	}


	//TODO
	/*public String [] loadString(String filename,int kj){

		return null;
	}*/







}
