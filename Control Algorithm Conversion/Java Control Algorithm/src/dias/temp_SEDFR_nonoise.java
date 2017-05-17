/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import Jama.Matrix;

/**
 *
 * @author Mert
 */
public class temp_SEDFR_nonoise {
    
    public static Matrix bolus_insulin;
    public static Matrix basal_insulin;
    public static int flag_noise;
    public static Matrix ins_every5;
    public static int a;
    public static int b;
    public static int c;
    public static int d;
    public static Matrix sum_score;
    public static Matrix sum_type;
    public static int start_noise;
    public static int noise_interval;
    public static int lenght_add;
    public static int am;
    public static Matrix type_recording;
    public static Matrix score_returning;
    public static double sigma_a;
    public static int phi_max;
    public static int ii;
    public static int delta_x;
    public static int st;
    public static Matrix gb;
    public static Matrix gb_real;
    public static int type;
    public static Matrix g_kal;
    public static Matrix g_pls;
    public static Matrix error_detection;
    public static Matrix error_detectino_real;
    public static Matrix recording_w;
    public static Matrix gb_angle_ret;
    public static int nn;
    public static int cc;
    public static double lamda;
    public static double a_w0;
    public static double b_w0;
    public static double alpha;
    public static double w;
    public static int d2;
    public static Matrix sum_wyy;
    public static Matrix sum_wyxT;
    public static Matrix sum_wxxT;
    public static Matrix sum_xx;
    public static Matrix sum_xxp;
    public static Matrix sum_xpxp;
    public static Matrix sum_wyx;
    public static Matrix sum_xxpp;
    public static double R;
    public static Matrix Q;
    public static int kj;
    public static Matrix x_e;
    public static Matrix A;
    public static Matrix C;
    public static double R_pls;
    public static double N_pls;
    public static double M_pls;
    public static double L_pls;
    public static double phi_pls;
    public static double I_pls;
    public static double J_pls;
    public static double sigma1;
    public static double sigma2;
    public static double alpha_pls;
    public static Matrix gb_kal;
    public static double gb_m;
    public static double gb_sd;
    public static double ins_m;
    public static double ins_sd;
    public static double gb_change_m;
    public static double gb_change_sd;
    public static double mean_gb;
    public static double std_gb;
    public static double mean_ins;
    public static double std_ins;
    public static double mean_CGM_change;
    public static double std_CGM_change;
    public static Matrix inss;
    public static int prediction_step_pls;
    public static Matrix noisel;
    public static double y;
    public static Matrix gb_with_NaN;
    public static Matrix gb_kal_ret;
    public static Matrix RR;
    public static Matrix AA;
    public static Matrix CC;
    public static Matrix QQ;
    public static Matrix x_r;
    public static double Sp;
    public static double y_pkal;
    public static Matrix kal_residual_x;
    public static Matrix ssP;
    public static Matrix y_p;
    public static Matrix sigma;
    public static double y1;
    public static double y2;
    public static double yy;
    public static Matrix angle;
    public static int step;
    public static Matrix gb_pls;
    public static Matrix CGM_change_pls;
    public static Matrix x_samp;
    public static double X_cross;
    public static double Y_cross;
    public static Matrix cluster;
    
}
