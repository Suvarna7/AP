/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import Jama.Matrix;

public class PolyFit {

	/**
	 * fit:
	 * returns the coefficients of the polynomial which best approximates f
	 * (in the mean square error sense) for the given data.
	 *  y = f(x).
	 *  y' = a0 + a1 x + ..... + an x^(order)
	 *
	 *  @param  ndata   number of data;
	 *  @param  x       values of the indipendent variable
	 *  @param  y       values of the dipendent variable (data)
	 *  @param  order   the order of the polynomial
	 *
	 *  @return array (of order+1 double) of the coefficients of the polynomial
	 */
	public static double[] fit(int ndata, double[] x, double[] y, int order) {
		double[] a = new double[order + 1];//coefficient of polynomial
		double[][] alfa = new double[order + 1][order + 1];
		double[][] beta = new double[order + 1][1];
		double[] poli = new double[order + 1];//values of y'

		if ((y.length < ndata) || (x.length < ndata)) {
			return a;
		}

		//create the matrix to solve to find the polynomial coefficient
		poli[0] = 1;
		for (int i = 0; i < ndata; i++) {
			for (int j = 1; j <= order; j++) {
				poli[j] = Math.pow(x[i], j);//value of polynomial at x
			}
			for (int k = 0; k <= order; k++) {
				beta[k][0] += y[i] * poli[k];
				for (int j = 0; j <= order; j++) {
					alfa[k][j] += poli[k] * poli[j];
				}
			}
		}

		Matrix mAlfa = new Matrix(alfa);
		Matrix mBeta = new Matrix(beta);
		Matrix mA = new Matrix(order + 1, 1);
		mA = mAlfa.solve(mBeta);
		beta = mA.getArrayCopy();
		for (int i = 0; i <= order; i++) {
			a[i] = beta[i][0];
		}
		return a;
	}
}
