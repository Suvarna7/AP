package functions;
import org.apache.commons.math3.optim.nonlinear.*;
import org.apache.commons.math3.optimization.GoalType;

import static org.junit.Assert.assertArrayEquals;

import org.apache.commons.math3.optimization.direct.*;

import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.FunctionsUtils;
import com.joptimizer.functions.LinearMultivariateRealFunction;
import com.joptimizer.functions.PDQuadraticMultivariateRealFunction;
import com.joptimizer.functions.PSDQuadraticMultivariateRealFunction;
import com.joptimizer.optimizers.JOptimizer;
import com.joptimizer.optimizers.LPOptimizationRequest;
import com.joptimizer.optimizers.LPPrimalDualMethod;
import com.joptimizer.optimizers.OptimizationRequest;

import de.xypron.jcobyla.Calcfc;
import de.xypron.jcobyla.Cobyla;
import de.xypron.jcobyla.CobylaExitStatus;


/**
 * The NonLinearFunctions class offers a method to perform non-linear operation
 * The non-linear method will minimize a certain function, using the active-set algorithm
 * using upper and lower constraints 
 * 
 * Uses the library commons-math3-3.5.jar jcobyla.jar
 * Uses the library jOptimizer, colt-1.1, apache-commons, slf4j, commons-lang3, csparsej
 * JOptimizer examples: http://www.joptimizer.com/examples.html
 * @author Caterina Lazaro
 * @version 1.0, November 2015
 *
 */

public class NonLinearFunctions {
	private double[] lowerBound;
	private double[] higherBound;
	private double[] initialPoint;

	/**
	 * Create the nonlinear function object
	 * @param lower - lower boundary
	 * @param upper - upper boundary
	 * @param initialX - x0 initial point
	 */
	public NonLinearFunctions(double[] lower, double[] upper, double initialX[]){
		lowerBound = lower;
		higherBound = upper;
		initialPoint = initialX;

	}

	
	/**
	 * Linear optimization
	 * http://www.joptimizer.com/apidocs/index.html?com/joptimizer/optimizers/LPPrimalDualMethod.html
	 * Following the example http://www.joptimizer.com/linearProgramming.html
	 * @param c - objective function
	 * @param G - inequality equation 1
	 * @param h - inequality equation 2
	 * @param A - equality equation 1
	 * @param b - equatily equation 2
	 * @param lb - lower bound
	 * @param ub - upper bound
	 * @return solution
	 */

	public double[] jOptimizerSolve2DLP(double[] c, double[][]G, double[] h, double[][]A, 
										double[]b, double[] lb, double[] ub){
		
		// ********* Optimization problem
		LPOptimizationRequest or = new LPOptimizationRequest();
		or.setInteriorPointMethod("avtive-set");
		//or.setF0(objectiveLinearFunction);
		//Objective function
		//or.setF0(objectiveLinearFunction);
		or.setC(c);
		or.setInitialPoint(new double[]{2,3});
		//Equalitites:
		or.setA(A);
		or.setB(b);
		//Inequalitites 
		or.setG(G);
		or.setH(h);
		//Boundaries
		or.setLb(lb);
		or.setUb(ub);
		//or.setDumpProblem(true); 

		//optimization
		LPPrimalDualMethod opt = new LPPrimalDualMethod();

		opt.setLPOptimizationRequest(or);
		try {
			int returnCode = opt.optimize();
		} catch (Exception e) {
			System.out.println("Optimize error: "+e);
			e.printStackTrace();
		}
		//Return the solution of optimization
		return opt.getOptimizationResponse().getSolution();

	}

	
	/**
	 * Quadratic optimization
	 * Following the example: http://www.joptimizer.com/quadraticProgramming.html
	 * @param objectiveFunction
	 * @param A - equality eq 1
	 * @param b - equality eq 2
	 * @param inequalities
	 * @return solution
	 */

	public double[] jOptimizerSolveQuadratic(PDQuadraticMultivariateRealFunction objectiveFunction, 
											 double[][] A, double[] b, ConvexMultivariateRealFunction[] inequalities){		

		//******* Optimization problem 
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		//Set x0
		or.setInitialPoint(initialPoint);
		//or.setFi(inequalities); //if you want x>0 and y>0
		//Set active-set as interior point method
		or.setInteriorPointMethod("active-set");
		or.setA(A);
		or.setB(b);
		or.setToleranceFeas(1.E-12);
		or.setTolerance(1.E-12);

		//************* Optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		try {
			int returnCode = opt.optimize();
		} catch (Exception e) {
			System.out.println("Optimize error: "+e);
			e.printStackTrace();
		}

		//Return the solution of optimization
		return opt.getOptimizationResponse().getSolution();

	}
	
	
	/**
	 * Quadratic optimization with constrains
	 * example: http://www.joptimizer.com/qcQuadraticProgramming.html
	 * @param objectiveFunction
	 * @param inequalities
	 * @return solution
	 */
	public double[] jOptimizerQCQP(PDQuadraticMultivariateRealFunction objectiveFunction, ConvexMultivariateRealFunction[] inequalities){
		/* ******** Objective function - 1/2 * x.P.x + q.x + r, P symmetric and positive definite
		//(x)100*(x(2)-x(1)^2)^2 + (1-x(1))^2
		double[][] P = new double[][] { { 1., 0.4 },{ 0.4, 1. }};
		PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P, null, 0);*/
		

		//*********** Optimization problem
		OptimizationRequest or = new OptimizationRequest();
		or.setF0(objectiveFunction);
		//init point = new double[] { -2., -2.}
		or.setInitialPoint(initialPoint);
		or.setFi(inequalities);
		or.setCheckKKTSolutionAccuracy(true);

		//********** Optimization
		JOptimizer opt = new JOptimizer();
		opt.setOptimizationRequest(or);
		try {
			int returnCode = opt.optimize();
		} catch (Exception e) {
			System.out.println("Optimize error: "+e);
			e.printStackTrace();
		}
		//Return the solution of optimization

		return opt.getOptimizationResponse().getSolution();

	}
	
	/**
	 * Find the fmin using Cobyla library
	 */

	public static void activeSetOptimizationCobyla(){
		GoalType min = GoalType.MINIMIZE;

		//Cobyla Library
		System.out.format("%nOutput from test problem 5 (Intermediate Rosenbrock)%n");
		Cobyla cobyla = new Cobyla();
		Calcfc calcfc = new Calcfc() {

			@Override
			public double compute(int n, int m, double[] x,	double[] con) {
				return 100 * Math.pow(x[1] - x[0] * x[0], 2.0)
						+ Math.pow(1 - x[0], 2.0);
			}
		};
		double[] x = { -1, 2 };
		CobylaExitStatus result1;

		//CobylaExitStatus FindMinimum(Calcfc calcfc, int n, int m, double[] x, double rhobeg, double rhoend, int iprint, int maxfun);
		result1 = cobyla.findMinimum(calcfc, 2, 0, x, 1, 2, 0, 3500);
		//assertArrayEquals(null, new double[] { 2, 1 }, x, 3.0e-6);

		//result1.values()
		System.out.println("Results:  "+x[0] +"," + x[1]);

	}



	/**
	 * To be implemented
	 */
	public void optimizeBOBYQAOptimizer(){

	}
}
