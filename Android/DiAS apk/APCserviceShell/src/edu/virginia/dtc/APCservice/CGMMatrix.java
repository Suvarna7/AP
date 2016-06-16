package edu.virginia.dtc.APCservice;

import java.util.Arrays;
import Jama.Matrix;

/**
 * CGMMatrix is a class to store and manage the cgm values
 * 
 * Uses Jama library
 * @author Caterina Lazaro
 * @version 1.0 Feb 2016
 */


public class CGMMatrix {
	//Matrix hodling cgm values
	private Matrix cgmValues;
	
	//Index for desired values
	private final static int _CGM = 0;
	
	/**
	 * CGMMatrix default constructor
	 */
	public CGMMatrix(){
		//Init cgmValues matrix - matrix 1x1
		cgmValues = new Matrix(new double[1][1]);
	}
	
	/**
	 * CGMMatrix constructor
	 * @param mat - matrix to initialize cgmValues
	 */
	public CGMMatrix(Matrix mat){
		//Init cgmValues matrix - with the given matrix
		cgmValues = mat;
	}
	
	/**
	 * addCGMvalue add a new CGM value to the matrix
	 * @param val - new value
	 */
	public void addCGMvalue(double val){
		//Make a copy of cgm array with an extra slot for the val
		double [] copy =  Arrays.copyOf(cgmValues.getArray()[_CGM], cgmValues.getArray()[_CGM].length+1);
		//Insert the new value at the end of the array
		copy[copy.length-1]=val;
		//Update matrix
		cgmValues= new Matrix(new double[][]{copy});
	}
	
	/**
	 * getCGMValues - returns the stored values of CGM
	 * @return cgmValues
	 */
	public Matrix getCGMValues(){
		return cgmValues;
	}

}
