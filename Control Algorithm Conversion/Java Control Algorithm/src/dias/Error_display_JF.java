/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import dias.MemoryStaticVariables.TrackData;
import Jama.Matrix;

/**
 *
 * @author User
 */
public class Error_display_JF {

    public static int a = 1;
    public int kj;
    public Matrix gs_f;
    public double ME = 0;
    public double EE = 0;
    public double error = 0;
    public double Model_error = 0;
    public double ratio_error = 0;
    public double Umax_error = 0;
    public double other = 0;
    public double cre = 0;
    public double UC;
    public double Umax;
    int i = 0;

    public Matrix errorsummation;
    public Matrix Dpotential;

    public Matrix Itrack;
    public double I_meinst;
    public double I_error_rspeed;
    public Matrix I_uconstrain;
    public Matrix datamem;

    public Error_display_JF(int kj, Matrix gs_f) {
        this.kj = kj;
        this.gs_f = gs_f;
    }

    public void error_display_JF() throws Exception {
        prevdata_error_summation pdes = new prevdata_error_summation();
        /*   /////////////////////////////////INPUTS OF ERROR_DISPLAY_JF////////////////////////////////////////////////////////////////////////////////////
        System.out.println("//////////////////////////////////////////Inputs of Error_Display_JF///////////////////////////////////////////////////////");
        System.out.println(kj+"'   kj");
        printMatrix(gs_f,"   gs_f");
        printMatrix(t.data_mem,"   t.data_mem");
        printMatrix(t.I_track,"   t.I_track");
        System.out.println(t.I_me_inst+"'   t.I_me_inst");
        System.out.println(t.I_error_rspeed+"'   t.I_error_rspeed");
        printMatrix(t.I_u_constrain,"  t.I_u_constrain");
        printMatrix(pdes.error_summation,"  pdes.error_summation");
        printMatrix(pdes.D_potential,"  pdes.D_potential");
        System.out.println("//////////////////////////////////////////Inputs of Error_Display_JF///////////////////////////////////////////////////////");
         /////////////////////////////////INPUTS OF ERROR_DISPLAY_JF//////////////////////////////////////////////////////////////////////////////////// */

        Itrack = new Matrix(14, 1);
        I_meinst = 0;
        I_error_rspeed = 0;
        I_uconstrain = new Matrix(14, 1);
        datamem = new Matrix(20, 20);

        I_error_rspeed = TrackData.I_error_rspeed;
        datamem = TrackData.data_mem;
        I_meinst = TrackData.I_me_inst;
        I_uconstrain = TrackData.I_u_constrain;
        Itrack = TrackData.I_track;

        int m = TrackData.data_mem.getColumnDimension();
        int n = TrackData.data_mem.getRowDimension();

        if (kj <= 21) {
            errorsummation = new Matrix(7, 1);
            Dpotential = new Matrix(1, 23);

            pdes.error_summation = new Matrix(7, 1);
            pdes.D_potential = new Matrix(1, 23);
        }
        if (kj > 21) {

            errorsummation = new Matrix(pdes.error_summation.getRowDimension(), pdes.error_summation.getColumnDimension());
            Dpotential = new Matrix(pdes.D_potential.getRowDimension(), pdes.D_potential.getColumnDimension());

            for (int i = 0; i < pdes.error_summation.getRowDimension(); i++) {
                for (int j = 0; j < pdes.error_summation.getColumnDimension(); j++) {
                    errorsummation.set(i, j, pdes.error_summation.get(i, j));
                }
            }

            for (int i = 0; i < pdes.D_potential.getRowDimension(); i++) {
                for (int j = 0; j < pdes.D_potential.getColumnDimension(); j++) {
                    Dpotential.set(i, j, pdes.D_potential.get(i, j));
                }
            }

            ME = datamem.get(9, m - 1);
            EE = datamem.get(11, m - 1);
            UC = datamem.get(10, m - 1);
            Umax = datamem.get(13, m - 1);
            cre = Math.log(datamem.get(12, m - 1));

            error = 0;

            if (gs_f.get(0, kj - 1) > 70) {
                Dpotential.set(0, kj, 200 - gs_f.get(0, kj - 1) / (gs_f.get(0, kj - 1) - gs_f.get(0, kj - 2)));
                if (Dpotential.get(0, kj) < 4 && Dpotential.get(0, kj) > 0 && (-gs_f.get(0, kj - 2) + gs_f.get(0, kj - 1) > 0)) {
                    error = 1;
                } else {
                    error = 0;
                }
            }

            if (gs_f.get(0, kj - 1) <= 200 && error != 1) {
                Dpotential.set(0, kj, (gs_f.get(0, kj - 1) - 70) / (gs_f.get(0, kj - 2) - gs_f.get(0, kj - 1)));
                if (Dpotential.get(0, kj) < 4 && Dpotential.get(0, kj) > 0 && (-gs_f.get(0, kj - 2) + gs_f.get(0, kj - 1)) < 0) {
                    error = -1;
                } else {
                    error = 0;
                }
            }

            if (gs_f.get(0, kj - 1) >= 200) {
                error = 1;
            } else if (gs_f.get(0, kj - 1) <= 70) {
                error = -1;
            }
////////////////////////////////////////////////CODE LINE 42 END////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////CODE LINE 43-55///////////////////////////////////////////////////////////////////////////////////
///%% Model error
///% if error~=0
            if (ME > 20) {
                Model_error = 1;
            } else if (ME > 10) {
                if (EE > 20) {
                    Model_error = 1;
                } else {
                    Model_error = 0.0 / 0.0;
                }
            } else {
                Model_error = 0.0 / 0.0;
            }
////////////////////////////////////////////////CODE LINE 55 END////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////CODE LINE 56-66///////////////////////////////////////////////////////////////////////////////////
//%% Umax not right
            if (error == 1) {
                if (UC > 2 && gs_f.get(0, kj - 1) - gs_f.get(0, kj - 2) >= gs_f.get(0, kj - 2) - gs_f.get(0, kj - 3) && gs_f.get(0, kj - 1) - gs_f.get(0, kj - 2) > 0) {
                    if (Umax < 35) {
                        Umax_error = 1;
                    } else {
                        Umax_error = 0.0 / 0.0;
                    }
                } else {
                    Umax_error = 0.0 / 0.0;
                }
            } else {
                Umax_error = 0.0 / 0.0;
            }
////////////////////////////////////////////////CODE LINE 66 END////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////CODE LINE 67-75///////////////////////////////////////////////////////////////////////////////////
//%% costfunction ratio error
            if (error != 0) {
                if (cre < 0) {
                    ratio_error = 1;
                } else {
                    ratio_error = 0.0 / 0.0;
                }
            } else {
                ratio_error = 0.0 / 0.0;
            }
////////////////////////////////////////////////CODE LINE 75 END////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////CODE LINE 76-84///////////////////////////////////////////////////////////////////////////////////
//%% Other
            if (error != 0) {
                if (max3constant(Model_error, Umax_error, ratio_error) != 1) {
                    other = 1;
                } else {
                    other = 0.0 / 0.0;
                }
            } else {
                other = 0.0 / 0.0;
            }
////////////////////////////////////////////////CODE LINE 84 END////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////CODE LINE 85-89//////////////////////////////////////////////////////////////////////////////////
            errorsummation.set(0, 0, error);
            errorsummation.set(1, 0, Model_error);
            errorsummation.set(2, 0, Umax_error);
            errorsummation.set(3, 0, ratio_error);
            errorsummation.set(4, 0, other);
            errorsummation.set(5, 0, Umax);
            errorsummation.set(6, 0, I_meinst);

            pdes.EE = EE;
            pdes.ME = ME;

            for (int i = 0; i < Dpotential.getColumnDimension(); i++) {
                pdes.D_potential.set(0, i, Dpotential.get(0, i));
            }

            pdes.D_potential = createnewMatrix(1, Dpotential.getColumnDimension() + 1, pdes.D_potential);

            for (int i = 0; i < 7; i++) {
                pdes.error_summation.set(i, a - 1, errorsummation.get(i, 0));
            }

            pdes.error_summation = createnewMatrix(7, errorsummation.getColumnDimension() + 1, pdes.error_summation);

            a = errorsummation.getColumnDimension() + 1;

        }
        /*    ////////////////////////////////////////////////OUTPUT/////////////////////////////////////////////////////////////////////////////////
        System.out.println("////////////////////////////Output Error Display//////////////////////////////////////////////////////////////////");
        printMatrix(pdes.error_summation,"pdes.error_summation");
        printMatrix(pdes.D_potential,"pdes.D_potential");
        System.out.println(pdes.ME+ "pdes.ME");
        System.out.println(pdes.EE+ "pdes.EE");
        System.out.println("////////////////////////////Output Error Display//////////////////////////////////////////////////////////////////");
        ///////////////////////////////////////////////OUTPUT/////////////////////////////////////////////////////////////////////////////////*/
    }

    public double max3constant(double constant1, double constant2, double constant3) {
        double temp = 0;
        if (constant1 > constant2) {
            if (constant1 > constant2) {
                temp = constant1;
            } else {
                temp = constant2;
            }
        } else if (constant2 > constant3) {
            temp = constant2;
        } else {
            temp = constant3;
        }
        return temp;
    }

    public static void printMatrix(Matrix m, String name) {
        System.out.print("\n " + name + ": \n{");
        for (double[] row : m.getArray()) {
            for (double val : row) {
                System.out.print(" " + val);
            }
            System.out.println();
        }
        System.out.println("}");
    }

    public Matrix createnewMatrix(int newdimensionx, int newdimensiony, Matrix oldmatrice) {
        Matrix newMatrice = new Matrix(newdimensionx, newdimensiony);

        for (int i = 0; i < oldmatrice.getRowDimension(); i++) {
            for (int j = 0; j < oldmatrice.getColumnDimension(); j++) {
                newMatrice.set(i, j, oldmatrice.get(i, j));
            }
        }

        return newMatrice;
    }

}
