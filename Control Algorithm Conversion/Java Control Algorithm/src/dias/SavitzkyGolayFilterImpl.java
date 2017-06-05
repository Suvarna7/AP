package dias;


import flanagan.analysis.CurveSmooth;
import flanagan.math.Matrix;

//import peakml.math.filter.SavitzkyGolayFilter;

/**
 * The SavitzkyGolayFilterImpl class offers methods to smooth the values of a given matrix 
 * The smoothing is performed with a SavitzkyGolay filter
 * 
 * Uses the library flanagan.jar
 * @author Caterina Lazaro
 * @version 1.0, November 2015 
 *
 */
public class SavitzkyGolayFilterImpl {
	private CurveSmooth smooth;
	private int width;
	private int order;
	/**
	 * Perfoms SavitzkyGolay filtering
	 * @param f
	 * @param k
	 */
	public SavitzkyGolayFilterImpl(int f, int k){
		width = f;
		order = k;
		
	}
	public double[] filter(double[] data){
		double[] result = null;
		smooth = new CurveSmooth(data);
		smooth.setSGpolyDegree(order);
		result = smooth.savitzkyGolay(width);
		return result;
	}

}
