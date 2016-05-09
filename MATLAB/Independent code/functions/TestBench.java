package functions;

import Jama.Matrix;

/**
 * Test the LDL decomposition algorithm
 * Test the non-linear optimization method
 * Test the Savitzky Golay Filter
 * 
 * Uses the jama-1.0.2.jar library
 * @author Caterina Lazaro
 * @version 1.0, November 2015 
 */


import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.FunctionsUtils;
import com.joptimizer.functions.LinearMultivariateRealFunction;
import com.joptimizer.functions.PDQuadraticMultivariateRealFunction;
import com.joptimizer.functions.PSDQuadraticMultivariateRealFunction;
public class TestBench {

	/** 
	 * main()
	 * main function of the project
	 * 
	 * @param args
	 */

	public static void main (String[] args){

		//**************** Active-set OPTIMIZATION *******************/
		System.out.println("\n*************NON-LINEAR FUNCTION *****************");
		//ConvexSolver qs;


		/* ****** Objective function 
		 * --> (x)100*(x(2)-x(1)^2)^2 + (1-x(1))^2
		 * f(x(1), x(2)) = 100*[x(2) - x(1)]^2 + [1 - x(1)]^2 =	
		 * 100*x(2)^2 + 101*x(1)^2 - 200*x(1)*x(2) - 2*x(1) + 1*/

		//Matrix form of linear function
		//TODO Put current objective function in 
		double[] c = new double[] { -1, -1 };
		// 1/2 * x.P.x + q.x + r, P symmetric and positive definite
		double[][] P = new double[][] {{ 1., 0.4 }, { 0.4, 1. }};
		PDQuadraticMultivariateRealFunction objectivePDQuadFunction = new PDQuadraticMultivariateRealFunction(P, null, 0);
		//http://www.joptimizer.com/apidocs/com/joptimizer/functions/ConvexMultivariateRealFunction.html
		//1/2 * x.P.x + q.x + r, P symmetric and positive semi-definite
		PSDQuadraticMultivariateRealFunction objectivePSDQuadFunction = new PSDQuadraticMultivariateRealFunction(P, null, 0);
		//Plane - linear function
		LinearMultivariateRealFunction objectiveLinearFunction = new LinearMultivariateRealFunction(new double[] { -1., -1. }, 4);


		// ******* Initial point
		double[] x0 = new double[]{1,2};

		//*** Inequality equations -- A & B
		//1. Double matrices for inequalies
		double[][]G = new double[][]{{0,0},{0,0},{0,0},{0,0}};
		double[] h = new double[]{0,0,0,0};
		//2. Linear Multivariate Real Function for inequalities
		ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[2];
		/* inequalities[0] = new LinearMultivariateRealFunction(new double[]{-1, 0}, 0);
		 * inequalities[1] = new LinearMultivariateRealFunction(new double[]{0, -1}, 0);*/
		inequalities[0] = new LinearMultivariateRealFunction(new double[]{0, 0}, 0);
		inequalities[1] = new LinearMultivariateRealFunction(new double[]{0, 0}, 0);
		//3. Circle as an inequality
		ConvexMultivariateRealFunction[] ineq = new ConvexMultivariateRealFunction[1];
		ineq[0] = FunctionsUtils.createCircle(2, 1.75, x0);

		// ********** Equalities - Aeq & Beq
		double[][] Aeq = new double[][]{{1,1}};
		double[] beq = new double[]{1};
		//double[][] Aeq = new double[][]{{0,0}};
		//double[] beq = new double[]{0};

		// ******  Bounds on variables
		double[] lb = new double[] {0 , 0};
		double[] ub = new double[] {10, 10};


		//Create the nonlinear functions manager:
		NonLinearFunctions nFuncs = new NonLinearFunctions(lb , ub, x0);


		//Optimizer solver 1 - Linear Programing
		// http://www.joptimizer.com/linearProgramming.html
		double[] sol = nFuncs.jOptimizerSolve2DLP(c, G, h, Aeq, beq, lb, ub);
		System.out.println("JOptimizer LP: "+ sol[0] +", "+sol[1]);

		//Optimizer solver 2 - Quadratic Programming
		// http://www.joptimizer.com/linearProgramming.html
		sol = nFuncs.jOptimizerSolveQuadratic(objectivePDQuadFunction, Aeq, beq, inequalities);
		System.out.println("JOptimizer 2D: "+ sol[0] +", "+sol[1]);

		//Optimizer solver 3 - Quadratically Constraint Quadratic Programming
		sol = nFuncs.jOptimizerQCQP(objectivePDQuadFunction, ineq);
		System.out.println("JOptimizer QCQP: "+ sol[0] +", "+sol[1]);

	}
}
