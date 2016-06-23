/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import Jama.Matrix;
import static java.lang.Math.atan;
import dias.MemoryStaticVariables.m20150711_load_global_variables;

/**
 *
 * @author User
 */
public class hypo_alarm {

    double[][] matrix = {{0, 0}};
    public Matrix hypo_slope_line = new Matrix(matrix);
    public Matrix hypo_threshold;
    public Matrix hypo_slope_degree;
    public Matrix hypo_alarm;
    public Matrix hypo_phase;
    public Matrix gs;
    public Matrix g_prediction;
    public Matrix repeated_immediate_alarm;
    public Matrix sleep;
    public Matrix phys_act;
    public Matrix hypo_phase_old;
    public int kj;
    public String[] carb_type;
    public String[] carb_amount;

    public hypo_alarm(Matrix hypo_threshold, Matrix hypo_slope_degree, Matrix hypo_alarm, String[] carb_amount, String[] carb_type, Matrix hypo_phase, Matrix hypo_phase_old, Matrix repeated_immediate_alarm, Matrix gs, int kj, Matrix g_prediction, Matrix phys_act, Matrix sleep) {
        this.hypo_alarm = hypo_alarm;
        this.hypo_phase = hypo_phase;
        this.carb_amount = carb_amount;
        this.carb_type = carb_type;
        this.g_prediction = g_prediction;
        this.gs = gs;
        this.hypo_slope_degree = hypo_slope_degree;
        this.hypo_threshold = hypo_threshold;
        this.phys_act = phys_act;
        this.sleep = sleep;
        this.repeated_immediate_alarm = repeated_immediate_alarm;
        this.kj = kj;
        this.hypo_phase_old = hypo_phase_old;
    }

    public void m20150711_hypo_alarm() {
        /*///////////////////////////////////////////////////////INPUTS HYPO_ALARMS/////////////////////////////////////////////////////////////////////////////////
  System.out.println("////////////////////////////////////Hypo_alarm_inputs///////////////////////////////////////////////////////////////////////////////////////");
  printMatrix(hypo_alarm,"hypo_alarm");
  printMatrix(hypo_phase,"hypo_phase");
  printMatrix(g_prediction,"g_prediction");
  printMatrix(gs,"gs");
  printMatrix(hypo_slope_degree,"hypo_slope_degree");
  printMatrix(hypo_threshold,"hypo_threshold");
  printMatrix(phys_act,"phys_act");
  printMatrix(sleep,"sleep");
  printMatrix(repeated_immediate_alarm,"repeated_immediate_alarm");
  printMatrix(hypo_phase_old,"hypo_phase_old");
  System.out.println(kj+ "       kj");
  System.out.println(carb_amount+ "       carb_amount");
  System.out.println(carb_type+ "       carb_type");
  System.out.println("////////////////////////////////////Hypo_alarm_inputs///////////////////////////////////////////////////////////////////////////////////////");
  ///////////////////////////////////////////////////////INPUTS HYPO_ALARMS////////////////////////////////////////////////////////////////////////////////*/
        if (phys_act.get(0, kj - 1) == 1) {
            hypo_threshold.set(0, kj - 1, 100);
        } else {
            hypo_threshold.set(0, (kj - 1), 70);
        }

        if (kj > 5) {
            double[] tempmatrice = new double[]{0, 5, 10, 15, 20};
            double[] gs_temp = new double[gs.getRowDimension() * gs.getColumnDimension()];
            double[] hypo_slope_line_temp = new double[24];

            gs_temp[0] = gs.get(0, kj - 5);
            gs_temp[1] = gs.get(0, kj - 4);
            gs_temp[2] = gs.get(0, kj - 3);
            gs_temp[3] = gs.get(0, kj - 2);
            gs_temp[4] = gs.get(0, kj - 1);

            hypo_slope_line_temp = PolyFit.fit(5, tempmatrice, gs_temp, 1);

            hypo_slope_line.set(0, 0, hypo_slope_line_temp[0]);
            hypo_slope_line.set(0, 1, hypo_slope_line_temp[1]);

            hypo_slope_degree.set(0, kj - 1, 57.295779513 * (atan(hypo_slope_line.get(0, 1))));

        } else {
            hypo_slope_degree.set(0, kj - 1, -45);
        }

        if (phys_act.get(0, kj - 1) == 1) { //%Carb suggestions during exercise

            hypo_algorithm_exercise mhypexercise = new hypo_algorithm_exercise(gs.get(0, kj - 1), g_prediction.get(5, kj - 1), hypo_threshold.get(0, kj - 1), hypo_phase_old.get(0, kj - 2), hypo_slope_degree.get(0, kj - 1));
            mhypexercise.m20150510_hypo_algorithm_exercise();
            carb_amount[kj - 1] = mhypexercise.carb_amount;
            carb_type[kj - 1] = mhypexercise.carb_type;
            hypo_alarm.set(0, kj - 1, mhypexercise.hypo_alarm);
            hypo_phase.set(0, kj - 1, mhypexercise.phase);
            hypo_phase_old.set(0, kj - 1, mhypexercise.phase_old);
            repeated_immediate_alarm.set(0, kj - 1, mhypexercise.repeated_immediate_alarm);
        } else if (sleep.get(0, kj - 1) == 1) { //Carb suggestions during sleep
            hypo_algorithm_sleep mhypoalgorithm = new hypo_algorithm_sleep(gs.get(0, kj - 1), g_prediction.get(5, kj - 1), hypo_threshold.get(0, kj - 1), hypo_phase_old.get(0, kj - 2), hypo_slope_degree.get(0, kj - 1));
            mhypoalgorithm.m20150510_hypo_algorithm_sleep();
            carb_amount[kj - 1] = mhypoalgorithm.carb_amount;
            carb_type[kj - 1] = mhypoalgorithm.carb_type;
            hypo_alarm.set(0, kj - 1, mhypoalgorithm.hypo_alarm);
            hypo_phase.set(0, kj - 1, mhypoalgorithm.phase);
            hypo_phase_old.set(0, kj - 1, mhypoalgorithm.phase_old);
            repeated_immediate_alarm.set(0, kj - 1, mhypoalgorithm.repeated_immediate_alarm);
        } else {  // %Carb suggestions during day
            hypo_algorithm mhypalgortihm = new hypo_algorithm(gs.get(0, kj - 1), g_prediction.get(5, kj - 1), hypo_threshold.get(0, kj - 1), hypo_phase_old.get(0, kj - 2), hypo_slope_degree.get(0, kj - 1));
            mhypalgortihm.m20150510_hypo_algorithm();

            carb_amount[kj - 1] = mhypalgortihm.carb_amount;
            carb_type[kj - 1] = mhypalgortihm.carb_type;
            hypo_alarm.set(0, kj - 1, mhypalgortihm.hypo_alarm);
            hypo_phase.set(0, kj - 1, mhypalgortihm.phase);
            hypo_phase_old.set(0, kj - 1, mhypalgortihm.phase_old);
            repeated_immediate_alarm.set(0, kj - 1, mhypalgortihm.repeated_immediate_alarm);
        }

        m20150711_load_global_variables lgvariables = new m20150711_load_global_variables();

        lgvariables.hypo_threshold = createnewMatrix(kj + 1, 1, lgvariables.hypo_threshold);
        lgvariables.hypo_slope_degree = createnewMatrix(kj + 1, 1, lgvariables.hypo_slope_degree);
        lgvariables.g_prediction = createnewMatrix(8, kj + 1, lgvariables.g_prediction);

        lgvariables.hypo_alarm = createnewMatrix(kj + 1, 1, lgvariables.hypo_alarm);
        lgvariables.hypo_alarm.set(kj, 0, hypo_alarm.get(0, kj - 1));
        lgvariables.hypo_phase = createnewMatrix(kj + 1, 1, lgvariables.hypo_phase);
        lgvariables.hypo_phase.set(kj, 0, hypo_phase.get(0, kj - 1));
        lgvariables.hypo_phase_old = createnewMatrix(kj + 1, 1, lgvariables.hypo_phase_old);
        lgvariables.hypo_phase_old.set(kj, 0, hypo_phase_old.get(0, kj - 1));
        lgvariables.repeated_immediate_alarm = createnewMatrix(kj + 1, 1, lgvariables.repeated_immediate_alarm);
        lgvariables.repeated_immediate_alarm.set(kj, 0, repeated_immediate_alarm.get(0, kj - 1));
        lgvariables.carb_amount = createnewString(kj + 1, lgvariables.carb_amount);
        lgvariables.carb_amount[kj] = carb_amount[kj - 1];
        lgvariables.carb_type = createnewString(kj + 1, lgvariables.carb_type);
        lgvariables.carb_type[kj] = carb_type[kj - 1];

        /*///////////////////////////////////////////////////////OUTPUT/////////////////////////////////////////////////////////////////////////////////////////////   
  System.out.println("////////////////////////////////////Hypo_alarm_outputs///////////////////////////////////////////////////////////////////////////////////////");
  printMatrix(lgvariables.hypo_alarm,"lgvariables.hypo_alarm");
  printMatrix(lgvariables.hypo_phase,"lgvariables.hypo_phase");
  printMatrix(lgvariables.hypo_slope_degree,"lgvariables.hypo_slope_degree");
  printMatrix(lgvariables.hypo_threshold,"lgvariables.hypo_threshold");
  printMatrix(lgvariables.repeated_immediate_alarm,"lgvariables.repeated_immediate_alarm");
  printMatrix(lgvariables.hypo_phase_old,"lgvariables.hypo_phase_old");
  System.out.println(lgvariables.carb_amount+ "       lgvariables.carb_amount");
  System.out.println(lgvariables.carb_type+ "       lgvariables.carb_type");
  System.out.println("////////////////////////////////////Hypo_alarm_outputs///////////////////////////////////////////////////////////////////////////////////////");          
///////////////////////////////////////////////////////OUTPUT/////////////////////////////////////////////////////////////////////////////////////////////  */
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

    public String[] createnewString(int newdimension, String[] oldstring) {
        String[] newString = new String[newdimension];

        for (int i = 0; i < oldstring.length; i++) {
            newString[i] = oldstring[i];
        }

        return newString;
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
}
