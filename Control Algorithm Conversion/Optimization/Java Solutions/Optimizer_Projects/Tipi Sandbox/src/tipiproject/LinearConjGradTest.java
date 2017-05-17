/*
* Adapted from https://github.com/emmt/TiPi/blob/master/src/mitiv/tests/LinearConjGradTest.java
*/

/*
 * This file is part of TiPi (a Toolkit for Inverse Problems and Imaging)
 * developed by the MitiV project.
 *
 * Copyright (c) 2014 the MiTiV project, http://mitiv.univ-lyon1.fr/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package tipiproject;

import mitiv.exception.IllegalLinearOperationException;
import mitiv.exception.IncorrectSpaceException;
import mitiv.linalg.*;
import mitiv.linalg.shaped.DoubleShapedVector;
import mitiv.linalg.shaped.DoubleShapedVectorSpace;

public class LinearConjGradTest {

    /* Coefficients of the LHS matrix A as a Java array (A must be positive definite). 
     */
    static double[][] arrA = new double[][] { { 3, 2 }, { 2, 3 } };

    /* True vector to recover. */
    static double[] arrX = new double[] { 7, -13 };

    public static void test() throws IncorrectSpaceException {

        // We work in 1D
        DoubleShapedVectorSpace vsp = new DoubleShapedVectorSpace(arrX.length);

        // we create vector b and x = {0,0}
        DoubleShapedVector x0 = vsp.wrap(arrX); // create a vector sharing its values with Java array arrX 
        DoubleShapedVector x = vsp.create(0); // initial solution (filled with zeros)
        DoubleShapedVector b = vsp.create(); // RHS vector (undefined contents)

        // Create LHS matrix A as a linear operator:
        LinearOperator A = new LinearOperator(vsp) {
            @Override
            protected void privApply(final Vector src, Vector dst, int job) {
                if (job != DIRECT && job != ADJOINT) {
                    throw new IllegalLinearOperationException();
                }
                final double[] x = ((DoubleShapedVector) src).getData();
                double[] y = ((DoubleShapedVector) dst).getData();
                for (int i = 0; i < y.length; ++i) {
                    y[i] = ArrayOps.dot(arrA[i], x);
                }
            }
        };

        /* Compute RHS vector b. */
        A.apply(x0,b);

        /* Create conjugate gradient method. */
        int maxiter = 100; // algorithm should converge in, at most, as many iterations as the size of the problem
        double atol = 0.0;
        double rtol = 1e-6;
        int out = LinearConjugateGradient.solve(A, b, x, atol, rtol, maxiter, true);
        if (out != 1) {
            System.out.println("NOT ENDED");
        }
        System.out.println("result: x = " + x + ";");

    }
}