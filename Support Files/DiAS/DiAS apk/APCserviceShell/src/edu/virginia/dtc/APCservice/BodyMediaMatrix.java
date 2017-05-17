package edu.virginia.dtc.APCservice;

import java.util.Arrays;

/**
 * BodyMediaMatrix is a class to store and manage the bodynedia values
 * 
 * Uses Jama libraries
 * @author Caterina Lazaro
 * @version 1.0 Feb 2016
 */
import Jama.Matrix;

public class BodyMediaMatrix {
	//Matrix holding bodymedia values
	private Matrix bmValues;
	
	//Index for desired values
	public final static int _NUM_SENSORS = 4;
	public final static int _EE = 0;
	public final static int _GSR = 1;
	public final static int _ACT = 2;
	public final static int _SLEEP = 3;



	/**
	 * CGMMatrix default constructor
	 */
	public BodyMediaMatrix(){
		//Init cgmValues matrix - matrix 4x1
		bmValues = new Matrix(new double[_NUM_SENSORS][1]);
	}
	
	/**
	 * CGMMatrix constructor
	 * @param mat - matrix to initialize cgmValues
	 */
	public BodyMediaMatrix(Matrix mat){
		//Init cgmValues matrix - with the given matrix
		bmValues = mat;
	}
	
	/**
	 * addBMvalue add a new BM value to the matrix
	 * @param val - new value
	 */
	public void addBMvalue(double[] ee, double [] gsr, double[] activity, double [] sleep){
		// Check the input values - their length should be the same
		if(ee.length ==  gsr.length && activity.length == sleep.length && ee.length == activity.length){
			//1. Update Energy Expenditure
			double [] eeCopy = updateMatrixValues(ee, _EE);
			//2. Update GSR
			double [] gsrCopy = updateMatrixValues(gsr, _GSR);
			//3. Update Activity
			double [] actCopy = updateMatrixValues(activity, _ACT);
			//4. Update Energy Expenditure
			double [] sleepCopy = updateMatrixValues(sleep, _SLEEP);
			
			//Update matrix
			bmValues= new Matrix(new double[][]{eeCopy, gsrCopy, actCopy, sleepCopy});
		}
		else {
			//Error in the input values!!
			System.out.println("BodyMedia values to update should have the same length!");
			
		}
	}
	
	private double[] updateMatrixValues(double[] values, int position){
		//Create new array with extra slots for the new values
		int matrixLenght = bmValues.getArray()[position].length;
		double [] result =  Arrays.copyOf(bmValues.getArray()[position], matrixLenght+values.length);
		//Update those values at the end of the array
		System.arraycopy(values, 0, result, matrixLenght, values.length );
		return result;
	}
	
	/**
	 * getCGMValues - returns the stored values of CGM
	 * @return cgmValues
	 */
	public Matrix getBMValues(){
		return bmValues;
	}
	/**
	 * getBMColumn  - return values at the given column
	 * @param col - column number
	 * @return
	 */
	public double[] getBMColumn(int col){
		return bmValues.getArray()[col];
	}
	
	/**
	 * getBMColumnLast - return the last value of the selected column
	 * @param col - column
	 * @return
	 */
	public double getBMColumnLast(int col){
		int last = bmValues.getArray()[col].length -1;
		return bmValues.getArray()[col][last];
	}


}
