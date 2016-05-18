/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import Jama.Matrix;
import javax.mail.MessagingException;

/**
 *
 * @author Mert
 */
public class test {
   
   public void Test_of_Addon_noise_JF(){
        Matrix gb= new Matrix (2,2);
        gb.set(0,0,1);
        Matrix result;
        
       Addon_noise_JF anJF =new Addon_noise_JF(6, 3, 2, gb, 6);
       result=anJF.addon_noise_JF();
      
       printMatrix (result, "Result");
    }
   
   public void Test_of_Angle_detection_JF(){
       Angle_detection_JF adJF = new Angle_detection_JF(7,41,58,69);
       double result=adJF.angle_detection_JF();
       System.out.println(result);
   }
   
    public void Test_of_Zmu_other_JF(){
        double [][] matrix1 = {{1,2,3},{48,5,62},{72,8,10}};
        Matrix matrix= new Matrix (matrix1);
        Zmu_other_JF zoJF= new Zmu_other_JF (6,4,matrix);
        Matrix result;
        result=zoJF.zmu_other_JF();
        printMatrix (result, "Result");
    }
   
    public void Test_of_send_text_message() throws MessagingException{
        send_text_message stm = new send_text_message ("mertsevil1991@gmail.com", "mertsevil1991@hotmail.com", "Hypo Alarm","Glucose=198");
        stm.generateAndSendEmail(); 
    }
    
    
    public void Test_of_reference_trajectory(){
        Matrix result;             
        reference_trajectory rft =new reference_trajectory (2,3,4,5);
        result=rft.referencetrajectory();
        printMatrix (result, "Result");
    } 
    
     public void Test_of_prediction_horizon(){
        double [][] matrix = {{1,3,2},{5,8,9},{4,5,0}};  
        double [][] matrix0 = {{1,5,3}};
        
        Matrix matrix1 = new Matrix (matrix);
        Matrix matrix2 = new Matrix (matrix0);
        Matrix result;
       
        prediction_horizon prehor= new prediction_horizon (matrix1, matrix2, 4,7);
        result=prehor.prediction_horizons();
         
        printMatrix (result, "Result");
     }   
     
      public void Test_of_m20150711_calculate_IOB(){
        double [][] matrix = {{1,3,2,5,8,9,4,5,0,51,4,8,9,7,2,14,12,11}}; 
        double [][] matrix0 = {{1,5,3,4,5,4,1,2,5,3,6,8,9,6,5,15}};
        
        Matrix matrix1 = new Matrix (matrix);
        Matrix matrix2 = new Matrix (matrix0);
        double result;
        
        
        m20150711_calculate_IOB calciob= new m20150711_calculate_IOB(matrix1,matrix2);
        result=calciob.IOB();
        System.out.println("Result:   "+result);
      }
      
      public void Test_of_nss_missing_data(){
        int result; 
        double percantage = 99;
        nss_missing_data nmdata = new nss_missing_data (percantage);
        result=nmdata.nss_missing_data_JF();
        System.out.println(result+"    Result");
       }
      
       public void Test_of_hypo_algortihm_sleep(){
        hypo_algorithm_sleep mhypoalgorithm = new hypo_algorithm_sleep (5,15,85,90,-80);
        mhypoalgorithm.m20150510_hypo_algorithm_sleep();
        System.out.println("Carb Amount:    "+mhypoalgorithm.carb_amount);
        System.out.println("Carb Type:    "+mhypoalgorithm.carb_type);
        System.out.println("Hypo_alarm:    "+mhypoalgorithm.hypo_alarm);
        System.out.println("Phase:    "+mhypoalgorithm.phase);
        System.out.println("Repeated_immediate_alarm:    "+mhypoalgorithm.repeated_immediate_alarm);
       }
       
       public void Test_of_hypo_algorithm_exercise(){
        hypo_algorithm_exercise mhypexercise = new hypo_algorithm_exercise (5,15,85,90,-80);
        mhypexercise.m20150510_hypo_algorithm_exercise();
        System.out.println("Carb Amount:    "+mhypexercise.carb_amount);
        System.out.println("Carb Type:    "+mhypexercise.carb_type);
        System.out.println("Hypo_alarm:    "+mhypexercise.hypo_alarm);
        System.out.println("Phase:    "+mhypexercise.phase);
        System.out.println("Repeated_immediate_alarm:    "+mhypexercise.repeated_immediate_alarm);
       }   
       
       public void Test_of_hypo_algorithm(){
        hypo_algorithm mhypalgortihm = new hypo_algorithm (5,15,85,90,-80);  
        mhypalgortihm.m20150510_hypo_algorithm();
        System.out.println("Carb Amount:    "+mhypalgortihm.carb_amount);
        System.out.println("Carb Type:    "+mhypalgortihm.carb_type);
        System.out.println("Hypo_alarm:    "+mhypalgortihm.hypo_alarm);
        System.out.println("Phase:    "+mhypalgortihm.phase);
        System.out.println("Repeated_immediate_alarm:    "+mhypalgortihm.repeated_immediate_alarm);
       }
       
        public void Test_of_m20150510_hypo_alarm(){
        double [][] hypo_threshold = {{25,25,25,25,25,25,25}}; 
        double [][] hypo_slope_degree = {{-70,-70,-70,-70,-70,-70,-70}};
        double [][] hypo_alarm = {{8,7,2,9,15,10,9}};
        String [] carb_type=new String [7];
        String [] carb_amount=new String [7];
        double [][] hypo_phase = {{18,3,5,7,4,2,8}};
        double [][] hypo_phase_old = {{-5,-9,-4,-6,-1,-5,-5}};
        double [][] repeated_immediate_alarm = {{9,1,3,2,3,4,6}};
        double [][] gs_prediction = {{8,1,1,5,7,7,7},{8,1,1,5,7,7,7},{8,1,1,5,7,7,7},{8,1,1,5,7,7,7},{8,1,1,5,7,7,7},{8,1,1,5,7,7,7},{8,1,1,5,7,7,7}};
        int kj=6;
        double [][] gs = {{80,80,80,80,80,80,80}};
        double [][] phys_act = {{2,1,1,1,1,1,1}};
        double [][] sleep= {{2,1,1,1,1,1,1}};
        
        Matrix hypo_threshold1 = new Matrix (hypo_threshold);
        Matrix hypo_slope_degree1 = new Matrix (hypo_slope_degree);
        Matrix hypo_alarm1 = new Matrix (hypo_alarm);
        Matrix hypo_phase1 = new Matrix (hypo_phase);
        Matrix hypo_phase_old1 = new Matrix (hypo_phase_old);
        Matrix repeated_immediate_alarm1 = new Matrix (repeated_immediate_alarm);
        Matrix gs1 = new Matrix (gs);
        Matrix gs_prediction1 = new Matrix (gs_prediction);
        Matrix phys_act1 = new Matrix (phys_act);
        Matrix sleep1 = new Matrix (sleep);
        hypo_alarm mhypalarm = new hypo_alarm (hypo_threshold1,hypo_slope_degree1,hypo_alarm1,carb_amount,carb_type,hypo_phase1,hypo_phase_old1,repeated_immediate_alarm1,gs1,kj,gs_prediction1,phys_act1,sleep1);
        mhypalarm.m20150711_hypo_alarm();
        printMatrix(mhypalarm.hypo_alarm,"Hypo_alarm:    ");
        printMatrix(mhypalarm.hypo_phase,"Phase: ");
        printMatrix(mhypalarm.repeated_immediate_alarm,"Repeated_immediate_alarm:    ");
        printMatrix(mhypalarm.hypo_phase_old,"Phase_old:    ");
        printMatrix(mhypalarm.hypo_alarm,"Hypo_alarm:    ");
        printMatrix(mhypalarm.hypo_slope_degree,"Hypo_slope_degree");
        }   
        
        public void Test_of_controller_horizons(){
        double [][] A_state = {{3,2,1},{4,5,6},{7,9,8}};
        double [][] B_state = {{5,3,2},{1,2,3},{2,1,6}};
        double [][] C_state = {{4,2,3}};
        int N1=4;
        int N2=6;
        int Nu=3;
        Matrix Astate = new Matrix (A_state);
        Matrix Bstate = new Matrix (B_state);
        Matrix Cstate = new Matrix (C_state);
        controller_horizons chorizon= new controller_horizons(Astate,Bstate,Cstate,N1,N2,Nu);
        chorizon.calculateHorizons();
        printMatrix(chorizon.LL,"LL");
        printMatrix(chorizon.LL_ee,"LL_ee");
        printMatrix(chorizon.LL_gsr,"LL_gsr");
        printMatrix(chorizon.M,"M");
        }
        
        public void Test_of_cluster(){ 
        double [][] ytest = {{10,9,8,7,6,5,4,3,2,1}};
        Matrix y = new Matrix (ytest);
        cluster1 cluster= new cluster1(y);
        cluster.cluster1_JF();
        System.out.println("X:"+cluster.X);
        System.out.println("Y:"+cluster.Y);
        System.out.println("T:"+cluster.T);
        }
        
     public void Test_of_detection_of_meal(){
      
       double [] x_p= new double [100];   
       x_p[0]=0.1;
       x_p[1]=298;
       x_p[0]=0;
       x_p[0]=0;
       x_p[0]=0.0680;
       x_p[0]=0.0370;
       x_p[0]=1.30;
       x_p[0]=20;
       
       double y=298;
       double mealdetection=0;
       double meal_detection_time=0;
       double correction_time=0;
       double correction_limit=0;
       double correction_bolus=0;
       double kj=5;
       double body_weight=74.80;
       double sleep=0;
       double phys_act=0;
       double meal_mu=0.50;
     
       x_p[0]=0.85;
       x_p[1]=0.96;
       x_p[2]=0.97;
       x_p[3]=0.97;
       x_p[4]=0.0680;
       x_p[5]=0.0370;
       x_p[6]=1.30;
       x_p[7]=20;
       
      detection_of_meal dtcmeal =new detection_of_meal(x_p, y, mealdetection, meal_detection_time, correction_time,correction_limit,correction_bolus,(int) kj, body_weight, sleep,phys_act, meal_mu);
      dtcmeal.detection_of_meal();
      
      System.out.println("meal_bolus:"+dtcmeal.meal_bolus);
      System.out.println("correction_detection:"+dtcmeal.correction_detection);
      System.out.println("meal_detection:"+dtcmeal.meal_detection);
      System.out.println("meal_detection_time:"+dtcmeal.meal_detection_time);
      System.out.println("correction_time:"+dtcmeal.correction_time);
      System.out.println("correction_limit:"+dtcmeal.correction_limit);
      System.out.println("correction_bolus:"+dtcmeal.correction_bolus);
         
     }
     
    public void Test_of_m20141215_sigma_const(){ 
     double [][] Xn = {{5,300,8,6,4,1,8,9,11}};
     double [] p_1 = {1,296,3,7,4,5,61,42,10};
     Matrix X_n = new Matrix (Xn);
    
    m20141215_sigma_const sconst =new m20141215_sigma_const(X_n,p_1);
    sconst.sigma_const();
    System.out.println("Sol:"+sconst.xtemp[0]);
    System.out.println("Sol:"+sconst.xtemp[1]);
    System.out.println("Sol:"+sconst.xtemp[2]);
    System.out.println("Sol:"+sconst.xtemp[3]);
    System.out.println("Sol:"+sconst.xtemp[4]);
    System.out.println("Sol:"+sconst.xtemp[5]);
    System.out.println("Sol:"+sconst.xtemp[6]);
    System.out.println("Sol:"+sconst.xtemp[7]);
    }      
    
       
    ////////////////////////////////////////sigma_const End//////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*System.out.format("%nOutput from test problem 6 (Equation (9.1.15) in Fletcher's book)%n");
        Calcfc calcfc = new Calcfc() {
            @Override
            public double Compute(int n, int m, double[] x, double[] con) {
                con[0] = x[1] - x[0] * x[0];
                con[1] = 1.0 - x[0] * x[0] - x[1] * x[1];
                return -x[0] - x[1];
            }
        };
        double[] x = {1.0, 1.0 };
        CobylaExitStatus result = Cobyla.FindMinimum(calcfc, 2, 2, x, rhobeg, rhoend, iprint, maxfun);
        assertArrayEquals(null, new double[] { Math.sqrt(0.5), Math.sqrt(0.5) }, x, 1.0e-5);*/
       //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /*double [] x= new double [10];
        double [] y= new double [10];
        double [] z= new double [10];
        NonlinearFunctions nlfun = new NonlinearFunctions (x,y,z); 
        nlfun.activeSetOptimizationCobyla();*/
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      /* double [][] X_n = new double [8][1];    //Working, Non-linear functions
        X_n[0][0]=75;
        X_n[1][0]=12;
        X_n[2][0]=320;
        X_n[3][0]=350;
        X_n[4][0]=0.05;
        X_n[5][0]=0.03;
        X_n[6][0]=1.8;
        X_n[7][0]=42;
        /*X_n[8][0]=8;
        X_n[9][0]=0.9;
        X_n[10][0]=1;*/
        
       /*Matrix X_n1= new Matrix (X_n);
        
       double [][] p1 = new double [1][1];
       p1[0][0]=1;
       Matrix p= new Matrix (p1);
        
        double [] x_a=new double [8];
        x_a[0]=196;
        x_a[1]=57;
        x_a[2]=480;
        x_a[3]=510;
        x_a[4]=0.1;
        x_a[5]=0.06;
        x_a[6]=2.4;
        x_a[7]=130;
       /* x_a[8]=9;
      / x_a[9]=9;
        x_a[10]=9;*/
        //m20141215_sigma_const msconst = new m20141215_sigma_const (X_n1,x_a); 
        //msconst.sigma_const();
       ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////      
       ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
       /* double [][] phi1= new double [25][25];
        phi1[0][0]=5;
        phi1[1][0]=9;
        phi1[2][0]=6;
        phi1[3][0]=16;
        phi1[4][0]=22;
        phi1[5][0]=35;
        phi1[6][0]=44;
        phi1[7][0]=16;
        phi1[8][0]=-9;
        phi1[9][0]=-6;
        phi1[10][0]=1;
        phi1[11][0]=2;
        phi1[12][0]=0.9;
        phi1[13][0]=6.5;
        phi1[14][0]=3;
        phi1[15][0]=4;
        phi1[16][0]=16;
        phi1[17][0]=9;
        phi1[18][0]=62;
        phi1[19][0]=19;
        phi1[20][0]=61;
        phi1[21][0]=92;
        phi1[22][0]=44;
        phi1[23][0]=11;
        phi1[24][0]=101;
        Matrix phi= new Matrix (phi1);
        
        double [][] Q_old1= new double [25][1];
        Q_old1[0][0]=8;
        Q_old1[1][0]=15.12;
        Q_old1[2][0]=18;
        Q_old1[3][0]=1.12;
        Q_old1[4][0]=9;
        Q_old1[5][0]=6;
        Q_old1[6][0]=2;
        Q_old1[7][0]=5;
        Q_old1[8][0]=7;
        Q_old1[9][0]=14;
        Q_old1[10][0]=6;
        Q_old1[11][0]=2;
        Q_old1[12][0]=9;
        Q_old1[13][0]=12;
        Q_old1[14][0]=13;
        Q_old1[15][0]=0.1;
        Q_old1[16][0]=9;
        Q_old1[17][0]=98;
        Q_old1[18][0]=18;
        Q_old1[19][0]=28;
        Q_old1[20][0]=6;
        Q_old1[21][0]=32;
        Q_old1[22][0]=28;
        Q_old1[23][0]=14;
        Q_old1[24][0]=19;
        Matrix Q_old= new Matrix (Q_old1);
        
        double [][] P_old1= new double [25][25];
      /* P_old1[0][0]=5;
        P_old1[0][1]=4;
        P_old1[1][0]=9;
        P_old1[1][1]=6;*/
      /*  for(int i=0;i<25;i++)
            for(int j=0;j<25;j++)
                P_old1[i][j]=6;
        
        Matrix P_old= new Matrix (P_old1);
        
        double [] upperlimit= new double [25];
        upperlimit[0]=7;
        upperlimit[1]=9;
        upperlimit[2]=6;
        upperlimit[3]=6;
        upperlimit[4]=5;
        upperlimit[5]=8;
        upperlimit[6]=18;
        upperlimit[7]=23;
        upperlimit[8]=72;
        upperlimit[9]=9;
        upperlimit[10]=16;
        upperlimit[11]=29;
        upperlimit[12]=100;
        upperlimit[13]=91;
        upperlimit[14]=5;
        upperlimit[15]=4;
        upperlimit[16]=16;
        upperlimit[17]=12;
        upperlimit[18]=11;
        upperlimit[19]=230;
        upperlimit[20]=6;
        upperlimit[21]=2;
        upperlimit[22]=14;
        upperlimit[23]=20;
        upperlimit[24]=30;
        
        double [] lowerlimit= new double [25];
        lowerlimit[0]=1;
        lowerlimit[1]=1;
        lowerlimit[2]=0;
        lowerlimit[3]=0;
        lowerlimit[4]=1;
        lowerlimit[5]=1;
        lowerlimit[6]=0;
        lowerlimit[7]=0;
        lowerlimit[8]=1;
        lowerlimit[9]=1;
        lowerlimit[10]=0;
        lowerlimit[11]=0;
        lowerlimit[12]=1;
        lowerlimit[13]=1;
        lowerlimit[14]=0;
        lowerlimit[15]=0;
        lowerlimit[16]=0;
        lowerlimit[17]=0;
        lowerlimit[18]=1;
        lowerlimit[19]=1;
        lowerlimit[20]=0;
        lowerlimit[21]=0;
        lowerlimit[22]=1;
        lowerlimit[23]=0;
        lowerlimit[24]=0;
        
        opt_recursive opt = new opt_recursive (12,phi,Q_old,P_old,0.7,upperlimit,lowerlimit);
        opt.optrecursive();*/
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
        /*double [][] phi1= new double [4][4];
        phi1[0][0]=5;
        phi1[1][0]=9;
        phi1[2][0]=6;
        phi1[3][0]=16;
        
        double [][] Q_old1= new double [4][1];
        Q_old1[0][0]=8;
        Q_old1[1][0]=15.12;
        Q_old1[2][0]=18;
        Q_old1[3][0]=1.12;
        
        double [][] P_old1= new double [4][4];
        P_old1[0][0]=5;
        P_old1[0][1]=4;
        P_old1[0][2]=9;
        P_old1[0][3]=6;
        
        P_old1[1][0]=1;
        P_old1[1][1]=2;
        P_old1[1][2]=3;
        P_old1[1][3]=4;
        
        P_old1[2][0]=10;
        P_old1[2][1]=21;
        P_old1[2][2]=33;
        P_old1[2][3]=48;
        
        P_old1[2][0]=1;
        P_old1[2][1]=6;
        P_old1[2][2]=12;
        P_old1[2][3]=17;
        
        double [] upperlimit= new double [4];
        upperlimit[0]=7;
        upperlimit[1]=9;
        upperlimit[2]=6;
        upperlimit[3]=6;
        
        double [] lowerlimit= new double [4];
        lowerlimit[0]=1;
        lowerlimit[1]=1;
        lowerlimit[2]=0;
        lowerlimit[3]=0;
        
          Matrix phi= new Matrix (phi1);
          Matrix P_old= new Matrix (P_old1);
          Matrix Q_old= new Matrix (Q_old1);
        
         opt_recursive_arm opt_arm = new opt_recursive_arm (12,phi,Q_old,P_old,0.7,upperlimit,lowerlimit);
         opt_arm.optrecursive();*/
      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      /* double [][] meal_states1 = new double [8][31];
       double [][][] meal_covariance = new double [8][8][538];
       double [][] bolus_insulin1 = new double [31][1];
       double [][] meal_bolus_amount1= new double [31][1];
       double [][] meal_detection1= new double [31][1];
       double [][] meal_detection_time1= new double [31][1];
       double [][] correction_amount1 = new double [31][1];
       double [][] correction_detection1 = new double [31][1];
       double [][] correction_detection_time1 = new double [31][1];
       double [][] correction_limit1 = new double [31][1] ;
       double [][] gs1 = new double [31][1]; 
       int kj=13;
       double [][] meal_g_basal1= new double [31][1];
       double [][] meal_gpc_gs_slope_degree1= new double [31][1];
       double [][] meal_gpc_mu1 = new double [31][1];
       double [][] sleep1 = new double [31][1];
       double [][] phys_act1= new double [31][1];
       double [][] IOB_total1= new double [31][1];
       double body_weight=78.5;
       
       meal_g_basal1[0][0]=2;
       meal_g_basal1[1][0]=3;
       
       gs1[0][0]=152;
       gs1[1][0]=112;
       gs1[2][0]=140;
       gs1[3][0]=110;
       gs1[4][0]=98;
       gs1[5][0]=95;
       gs1[6][0]=99;
       gs1[7][0]=79;
       gs1[8][0]=144;
       gs1[9][0]=132;
       
       Matrix meal_states= new Matrix (meal_states1);
       Matrix bolus_insulin= new Matrix (bolus_insulin1);
       Matrix meal_bolus_amount= new Matrix (meal_bolus_amount1);
       Matrix meal_detection= new Matrix (meal_detection1);
       Matrix meal_detection_time= new Matrix (meal_detection_time1);
       Matrix correction_amount= new Matrix (correction_amount1);
       Matrix correction_detection= new Matrix (correction_detection1);
       Matrix correction_detection_time= new Matrix (correction_detection_time1);
       Matrix correction_limit= new Matrix (correction_limit1);
       Matrix gs= new Matrix (gs1);
       Matrix meal_g_basal= new Matrix (meal_g_basal1);
       Matrix meal_gpc_gs_slope_degree= new Matrix (meal_gpc_gs_slope_degree1);
       Matrix meal_gpc_mu= new Matrix (meal_gpc_mu1);
       Matrix sleep= new Matrix (sleep1);
       Matrix phys_act= new Matrix (phys_act1);
       Matrix IOB_total= new Matrix (IOB_total1);
       
       m20150711_run_meal_detection_bolus_algorithm runmdba =new m20150711_run_meal_detection_bolus_algorithm(meal_states,meal_covariance,bolus_insulin,meal_bolus_amount,meal_detection,meal_detection_time,correction_amount,correction_detection,correction_detection_time,correction_limit,gs,kj,meal_g_basal,meal_gpc_gs_slope_degree,meal_gpc_mu,sleep,phys_act,IOB_total,body_weight);
       runmdba.run_meal_detection_bolus_algorithm();
       meal_g_basal.equals(runmdba.meal_g_basal);
       printMatrix(meal_g_basal,"1.");
       
       kj=10;
       
       runmdba =new m20150711_run_meal_detection_bolus_algorithm(meal_states,meal_covariance,bolus_insulin,meal_bolus_amount,meal_detection,meal_detection_time,correction_amount,correction_detection,correction_detection_time,correction_limit,gs,kj,meal_g_basal,meal_gpc_gs_slope_degree,meal_gpc_mu,sleep,phys_act,IOB_total,body_weight);
       
       runmdba.run_meal_detection_bolus_algorithm();
       meal_g_basal.equals(runmdba.meal_g_basal);
       printMatrix(meal_g_basal,"2.");
       
       kj=12;
       
       runmdba =new m20150711_run_meal_detection_bolus_algorithm(meal_states,meal_covariance,bolus_insulin,meal_bolus_amount,meal_detection,meal_detection_time,correction_amount,correction_detection,correction_detection_time,correction_limit,gs,kj,meal_g_basal,meal_gpc_gs_slope_degree,meal_gpc_mu,sleep,phys_act,IOB_total,body_weight);
       
       runmdba.run_meal_detection_bolus_algorithm();
       meal_g_basal.equals(runmdba.meal_g_basal);
       printMatrix(meal_g_basal,"3.");*/
       ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
       /* double [][] thita= new double [2][7];
        thita[0][0]=5;
        thita[0][1]=4;
        thita[0][2]=2;
        thita[0][3]=3;
        thita[0][4]=1;
        thita[0][5]=2;
        thita[0][6]=9;
        
        thita[1][0]=500;
        thita[1][1]=400;
        thita[1][2]=200;
        thita[1][3]=300;
        thita[1][4]=100;
        thita[1][5]=200;
        thita[1][6]=900;
        
        Matrix thita1= new Matrix (thita);
        Matrix result= new Matrix (7,7);
        
      //  LW_PLS_JF lwpls = new LW_PLS_JF();
        //result=lwpls.diag(thita1, 0);
        
      //  printMatrix(result,"Result1");
        
       // lwpls = new LW_PLS_JF();
       // result=lwpls.diag(thita1, 1);
        //printMatrix(result,"Result2");
        
       double [][] X= new double [3][3];
        X[0][0]=1;
        X[0][1]=2;
        X[0][2]=3;
        X[1][0]=4;
        X[1][1]=5;
        X[1][2]=6;
        X[2][0]=7;
        X[2][1]=8;
        X[2][2]=9;
        
        Matrix X1= new Matrix (X);
        
        double [][] Y= new double [3][3];
        Y[0][0]=9;
        Y[0][1]=8;
        Y[0][2]=7;
        Y[1][0]=6;
        Y[1][1]=5;
        Y[1][2]=4;
        Y[2][0]=3;
        Y[2][1]=2;
        Y[2][2]=1;
        
        Matrix Y1= new Matrix (Y);
        
        double [][] x_sample= new double [3][1];
        x_sample[0][0]=4;
        x_sample[1][0]=3;
        x_sample[2][0]=2;
        
        Matrix xsample= new Matrix (x_sample);
        
        double R=10;
        double phi=8;
        double I=7;
        int J=6;
        double alpha=3;
        double sigma1=8;
        double sigma2=9;
        
        double [][] thita_m_offline= new double [6][3];
        
        thita_m_offline[0][0]=5;
        thita_m_offline[0][1]=6;
        thita_m_offline[0][2]=7;
        
         Matrix thitamoffline= new Matrix (thita_m_offline);
        
        double [][] regression_coefficient= new double [3][3];
        
        regression_coefficient[0][0]=6;
        regression_coefficient[0][1]=5;
        regression_coefficient[0][2]=4;
        regression_coefficient[1][0]=7;
        regression_coefficient[1][1]=8;
        regression_coefficient[1][2]=9;
        regression_coefficient[2][0]=2;
        regression_coefficient[2][1]=0;
        regression_coefficient[2][2]=4;
        
        Matrix regressioncoefficient= new Matrix (regression_coefficient);
        
        
        LW_PLS_JF lwpls = new LW_PLS_JF(X1,Y1,xsample,R,phi,I,J,alpha,sigma1,sigma2,thitamoffline,regressioncoefficient);
        lwpls.LWPLSJF();*/
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////Error_display//////////////////////////////////////////////////////////////////////////////////////////
      /*  double [][] gs_f = new double [100][1];
        gs_f[0][0]=4;
        gs_f[1][0]=0.05;
        gs_f[2][0]=8;
        gs_f[3][0]=1.2;
        gs_f[4][0]=0.3;
        gs_f[5][0]=80;
        gs_f[6][0]=86;
        gs_f[7][0]=25;
        gs_f[8][0]=0.005;
        gs_f[9][0]=49;
        gs_f[10][0]=75;
        gs_f[11][0]=98;
        gs_f[12][0]=75;
        gs_f[13][0]=65;
        gs_f[14][0]=180;
        gs_f[15][0]=0.1;
        gs_f[16][0]=48;
        gs_f[17][0]=42;
        gs_f[18][0]=96;
        gs_f[19][0]=37;
        gs_f[20][0]=22;
        gs_f[21][0]=36;
        gs_f[22][0]=17;
        gs_f[23][0]=18;
        gs_f[24][0]=79;
       
        
         trackdata tr = new trackdata ();
         tr.I_error_rspeed=5;
         
         System.out.println(tr.I_error_rspeed);
         
         double x=tr.I_error_rspeed;
         
         System.out.println(x);
        
        Matrix gsf = new Matrix (gs_f);
        int kj=23;
        
         Error_display_JF edJF = new Error_display_JF(kj,gsf.transpose());
         edJF.error_display_JF();
        
         prevdata_error_summation pvdes = new prevdata_error_summation();
         
         printMatrix(pvdes.error_summation,"error_summation");
         
         edJF = new Error_display_JF(kj,gsf.transpose());
         edJF.error_display_JF();
         
          printMatrix(pvdes.error_summation,"error_summation2");
          
           edJF = new Error_display_JF(kj,gsf.transpose());
         edJF.error_display_JF();
         
          printMatrix(pvdes.error_summation,"error_summation3");*/
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////controller_assessment_index_071215_JF//////////////////////////////////////////////////////////////////
        /*double [][] Umaxx_account = new double [32][1];
        
        Umaxx_account[0][0]=26;
        Umaxx_account[1][0]=17;
        Umaxx_account[2][0]=19;
        Umaxx_account[3][0]=200;
        Umaxx_account[4][0]=210;
        Umaxx_account[5][0]=18;
        Umaxx_account[6][0]=186;
        Umaxx_account[7][0]=196;
        Umaxx_account[8][0]=246;
        Umaxx_account[9][0]=254;
        Umaxx_account[10][0]=63;
        Umaxx_account[11][0]=163;
        Umaxx_account[12][0]=144;
        Umaxx_account[13][0]=187;
        Umaxx_account[14][0]=0.15;
        Umaxx_account[15][0]=124;
        Umaxx_account[16][0]=220;
        Umaxx_account[17][0]=11;
        Umaxx_account[18][0]=17;
        Umaxx_account[19][0]=175;
        Umaxx_account[20][0]=250;
        Umaxx_account[21][0]=45;
        Umaxx_account[22][0]=120;
        Umaxx_account[23][0]=15;
        Umaxx_account[24][0]=85;
        Umaxx_account[25][0]=16;
        Umaxx_account[26][0]=98;
        Umaxx_account[27][0]=74;
        Umaxx_account[28][0]=13;
        Umaxx_account[29][0]=310;
        Umaxx_account[30][0]=200;
        
        double [][][] L_account= new double [4][4][10];
        
        L_account[0][0][0]=0.15;
        L_account[0][1][0]=0.21;    
        L_account[0][2][0]=0.28;
        L_account[0][3][0]=0.39;
        
        L_account[1][0][0]=0.11;
        L_account[1][1][0]=0.96;    
        L_account[1][2][0]=0.75;
        L_account[1][3][0]=0.63;
        
        L_account[2][0][0]=0.4;
        L_account[2][1][0]=0.79;    
        L_account[2][2][0]=0.49;
        L_account[2][3][0]=0.170;
        
        
        L_account[3][0][0]=0.5;
        L_account[3][1][0]=2;    
        L_account[3][2][0]=5;
        L_account[3][3][0]=0.4;
        
        L_account[0][0][1]=85;
        L_account[0][1][1]=17;    
        L_account[0][2][1]=66;
        L_account[0][3][1]=77;
        
        L_account[1][0][1]=99;
        L_account[1][1][1]=77;    
        L_account[1][2][1]=58;
        L_account[1][3][1]=45;
        
        L_account[2][0][1]=96;
        L_account[2][1][1]=92;    
        L_account[2][2][1]=29;
        L_account[2][3][1]=33;
        
        
        L_account[3][0][1]=19;
        L_account[3][1][1]=14;    
        L_account[3][2][1]=21;
        L_account[3][3][1]=4;
        
        double [][] rf_account = new double [4][32];
        double [][] wfcn_account = new double [4][32];
        double [][] ins_f = new double [4][32];
        double [][] g_prediction_f = new double [4][32];
        
        double B_w=71.30;
        
        int kj=30;
        
        for(int i=0;i<4;i++ ){
        for(int j=0;j<31;j++ ){
            wfcn_account[i][j]=6;
            g_prediction_f[i][j]=8;
            ins_f[i][j]=9;
            rf_account[i][j]=5;
        }
        }
        
        double [][] gs_f = new double [32][1];
        
        gs_f[0][0]=260;
        gs_f[1][0]=170;
        gs_f[2][0]=190;
        gs_f[3][0]=200;
        gs_f[4][0]=210;
        gs_f[5][0]=180;
        gs_f[6][0]=186;
        gs_f[7][0]=196;
        gs_f[8][0]=246;
        gs_f[9][0]=254;
        gs_f[10][0]=260;
        gs_f[11][0]=163;
        gs_f[12][0]=144;
        gs_f[13][0]=187;
        gs_f[14][0]=150;
        gs_f[15][0]=124;
        gs_f[16][0]=220;
        gs_f[17][0]=110;
        gs_f[18][0]=170;
        gs_f[19][0]=175;
        gs_f[20][0]=250;
        gs_f[21][0]=145;
        gs_f[22][0]=120;
        gs_f[23][0]=150;
        gs_f[24][0]=285;
        gs_f[25][0]=160;
        gs_f[26][0]=198;
        gs_f[27][0]=174;
        gs_f[28][0]=113;
        gs_f[29][0]=310;
        gs_f[30][0]=200;
        
        trackdata t=new trackdata();
        
        Matrix Itrack= new Matrix (14,1);
        
        Itrack.set(0,0,1545.83024286011);
        Itrack.set(1,0,2112.44712888847);
        Itrack.set(2,0,2502.68894705019);
        Itrack.set(3,0,1758.44778831880);
        Itrack.set(4,0,1498.00744042839);
        Itrack.set(5,0,752.918813670067);
        Itrack.set(6,0,448.847225276340);
        Itrack.set(7,0,97.4330661389158);
        Itrack.set(8,0,35.3489192603490);
        Itrack.set(9,0,6.27171448832129);
        Itrack.set(10,0,1);
        Itrack.set(11,0,303.1669);
        Itrack.set(12,0,4.8102);
         
        double I_error_rspeed=60;
        double I_meinst=-6.2717;
        
        double [][] I_ucostrain = new double [14][1];
        
        I_ucostrain[0][0]=1 ; 
        I_ucostrain[1][0]=1 ;
        I_ucostrain[2][0]=1 ;
        I_ucostrain[3][0]=1 ;
        I_ucostrain[4][0]=1 ;
        I_ucostrain[5][0]=1 ;
        I_ucostrain[6][0]=1 ;
        I_ucostrain[7][0]=0 ;
        I_ucostrain[8][0]=1 ;
        I_ucostrain[9][0]=0 ;
        
        Matrix I_uconstrain = new Matrix (I_ucostrain);
         
        double [][] datmem = new double [14][10];
        
        datmem [0][0]=8222.17316057611;
        datmem [1][0]=9543.71334696453;
        datmem [2][0]=11024.5523716329;
        datmem [3][0]=11479.4568466347;
        datmem [4][0]=11829.6105320000;
        datmem [5][0]=12016.5171284141;
        datmem [6][0]=12086.4408296587;
        datmem [7][0]=12111.3370245756;
        datmem [8][0]=0;
        datmem [9][0]=120.504905753039;
        datmem [10][0]=0;
        datmem [11][0]=10;
        datmem [12][0]=10000000;
        datmem [13][0]=0; 
        
        datmem [0][1]=9596.73108408463;
        datmem [1][1]=7675.24200517480;
        datmem [2][1]=11214.0995511362;
        datmem [3][1]=12340.5714371676;
        datmem [4][1]=11402.3184327467;
        datmem [5][1]=12273.4842613477;
        datmem [6][1]=12709.7346148148;
        datmem [7][1]=12275.1354737320;
        datmem [8][1]=0;
        datmem [9][1]=98.6761995265357;
        datmem [10][1]=0;
        datmem [11][1]=20;
        datmem [12][1]=10000000;
        datmem [13][1]=0;
        
        datmem [0][2]=7003.03900520608;
        datmem [1][2]=11322.2554496123;
        datmem [2][2]=13078.4464932753;
        datmem [3][2]=13281.9079472046;
        datmem [4][2]=12880.8562423452;
        datmem [5][2]=12342.2790520055;
        datmem [6][2]=11876.2374773294;
        datmem [7][2]=11741.7608528157;
        datmem [8][2]=0;
        datmem [9][2]=105.962906674336;
        datmem [10][2]=0;
        datmem [11][2]=30;
        datmem [12][2]=10000000;
        datmem [13][2]=0;
        
        datmem [0][3]=8546.25505607478;
        datmem [1][3]=17324.2716583909;
        datmem [2][3]=23331.8726639608;
        datmem [3][3]=24823.6565258202;
        datmem [4][3]=23342.3769824358;
        datmem [5][3]=20937.1208901627;
        datmem [6][3]=18914.0299519637;
        datmem [7][3]=16691.6492296349;
        datmem [8][3]=0;
        datmem [9][3]=94.6841622124885;
        datmem [10][3]=0;
        datmem [11][3]=40;
        datmem [12][3]=10000000;
        datmem [13][3]=0;
        
        datmem [0][4]=474.572336599921;
        datmem [1][4]=1112.24545271357;
        datmem [2][4]=2195.56502254126;
        datmem [3][4]=3714.03858616201;
        datmem [4][4]=5571.07209520350;
        datmem [5][4]=7676.86416521676;
        datmem [6][4]=9948.02959442661;
        datmem [7][4]=11738.0056097553;
        datmem [8][4]=0;
        datmem [9][4]=107.945957489091;
        datmem [10][4]=0;
        datmem [11][4]=50;
        datmem [12][4]=10000000;
        datmem [13][4]=0;
        
        datmem [0][5]=284.724407077429;
        datmem [1][5]=595.977856535647;
        datmem [2][5]=792.218064357813;
        datmem [3][5]=897.320940156200;
        datmem [4][5]=941.418495997766;
        datmem [5][5]=946.968438746742;
        datmem [6][5]=925.893508286302;
        datmem [7][5]=868.568492962497;
        datmem [8][5]=6.40731792904153;
        datmem [9][5]=37.2846812370510;
        datmem [10][5]=3;
        datmem [11][5]=60;
        datmem [12][5]=975.929440894124;
        datmem [13][5]=0;
        
        datmem [0][6]=406.910468728146;
        datmem [1][6]=826.339552702659;
        datmem [2][6]=1137.32747948376;
        datmem [3][6]=1232.25142483159;
        datmem [4][6]=1276.22364739076;
        datmem [5][6]=1183.21356011668;
        datmem [6][6]=1071.67828327387;
        datmem [7][6]=888.325908793166;
        datmem [8][6]=17.1208582286120;
        datmem [9][6]=1.12622131597581;
        datmem [10][6]=3;
        datmem [11][6]=0;
        datmem [12][6]=468.567067036042;
        datmem [13][6]=0;
        
        datmem [0][7]=2827.03795391579;
        datmem [1][7]=5222.78101936766;
        datmem [2][7]=8703.80835205241;
        datmem [3][7]=10337.3311318927;
        datmem [4][7]=14004.0516328875;
        datmem [5][7]=15663.8726319175;
        datmem [6][7]=19818.1018999869;
        datmem [7][7]=21678.9425667266;
        datmem [8][7]=191.264813957554;
        datmem [9][7]=12.8279780704029;
        datmem [10][7]=2;
        datmem [11][7]=10;
        datmem [12][7]=513.716690256225;
        datmem [13][7]=12.2963532021536;
        
        datmem [0][8]=1153.73378404342;
        datmem [1][8]=2328.68332355987;
        datmem [2][8]=1912.79288823539;
        datmem [3][8]=1831.56714074804;
        datmem [4][8]=974.218490257120;
        datmem [5][8]=636.885150278046;
        datmem [6][8]=121.894439888217;
        datmem [7][8]=13.4886497150148;
        datmem [8][8]=29.7275912540431;
        datmem [9][8]=24.2957502895102;
        datmem [10][8]=2;
        datmem [11][8]=20;
        datmem [12][8]=301.849678638350;
        datmem [13][8]=0;
        
        datmem [0][9]=1545.83024286011;
        datmem [1][9]=2112.44712888847;
        datmem [2][9]=2502.68894705019;
        datmem [3][9]=1758.44778831880;
        datmem [4][9]=1498.00744042839;
        datmem [5][9]=752.918813670067;
        datmem [6][9]=448.847225276340;
        datmem [7][9]=97.4330661389158;
        datmem [8][9]=35.3489192603490;
        datmem [9][9]=6.27171448832129;
        datmem [10][9]=1;
        datmem [11][9]=20;
        datmem [12][9]=303.166854231161;
        datmem [13][9]=4.81022472817284;
     
        Matrix datamem = new Matrix (datmem);
        
        t.data_mem=datamem;
        t.I_track=Itrack;
        t.I_u_constrain=I_uconstrain;
        t.I_error_rspeed=I_error_rspeed;
        t.I_me_inst=I_meinst; 
        
        Matrix rfaccount= new Matrix (rf_account);
        Matrix wfcnaccount= new Matrix (wfcn_account);
        Matrix gprediction_f= new Matrix (g_prediction_f);
        Matrix gs= new Matrix (gs_f);
        Matrix Umaxxaccount= new Matrix (Umaxx_account);
        Matrix insf= new Matrix (ins_f);
        
        controller_assessment_index_071215_JF  caiJF = new controller_assessment_index_071215_JF (Umaxxaccount, L_account,rfaccount, wfcnaccount,kj,gs,  insf,  B_w, gprediction_f);
        caiJF.controller_assessment_index_071215_JF();
        
        caiJF.controller_assessment_index_071215_JF();
        
        Matrix result= new Matrix (15,1);
        
        result= caiJF.controller_assessment_index_071215_JF();
        
        printMatrix(result,"result");*/
       ///////////////////////////////////////////////////////controller_assessment_index_071215_JF//////////////////////////////////////////////////////////////            
       //////////////////////////////////////////////////////ldl///////////////////////////////////////////////////////////////////////////////////////////////////
      /*    double [][] Y= new double [3][3];
        Y[0][0]=9;
        Y[0][1]=8;
        Y[0][2]=7;
        Y[1][0]=6;
        Y[1][1]=5;
        Y[1][2]=4;
        Y[2][0]=3;
        Y[2][1]=2;
        Y[2][2]=1;
        
        Matrix Y1= new Matrix (Y);
        
        Ldl ldlfind = new Ldl(Y1);
        
        ldlfind.LdlFind();
        
        printMatrix(ldlfind.A,"A");
        printMatrix(ldlfind.L,"L");
        printMatrix(ldlfind.d,"d");
        
        Y[0][0]=15;
        Y[0][1]=18;
        Y[0][2]=17;
        Y[1][0]=61;
        Y[1][1]=51;
        Y[1][2]=14;
        Y[2][0]=31;
        Y[2][1]=20;
        Y[2][2]=11;
        
          ldlfind.LdlFind();
        
        printMatrix(ldlfind.A,"A2");
        printMatrix(ldlfind.L,"L2");
        printMatrix(ldlfind.d,"d2");
         printMatrix(ldlfind.D,"D2");*/
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
       ///////////////////////////////////////////////////////m20141215_ukf_meal TEST//////////////////////////////////////////////////////////////////////////////
      /*  double y=54.5;
       double [][] x_a1 = new double [8][1];
       double [][] P_a1 = new double [8][8];
       double [][] Q_n1 = new double [8][8];
       double Gb=41;
       double R_n=100;
       
       x_a1[0][0]=1;
       x_a1[1][0]=8;
       x_a1[2][0]=7;
       x_a1[3][0]=6;
       x_a1[4][0]=5;
       x_a1[5][0]=4;
       x_a1[6][0]=9;
       x_a1[7][0]=10;
       
       P_a1[0][0]=3;
       P_a1[0][1]=3;
       P_a1[0][2]=9;
       P_a1[0][3]=22;
       P_a1[0][4]=17;
       P_a1[0][5]=16;
       P_a1[0][6]=4;
       P_a1[0][7]=9;
       
       P_a1[1][0]=8;
       P_a1[1][1]=7;
       P_a1[1][2]=6;
       P_a1[1][3]=5;
       P_a1[1][4]=4;
       P_a1[1][5]=9;
       P_a1[1][6]=4;
       P_a1[1][7]=11;
       
       P_a1[2][0]=5;
       P_a1[2][1]=1;
       P_a1[2][2]=4;
       P_a1[2][3]=5;
       P_a1[2][4]=8;
       P_a1[2][5]=9;
       P_a1[2][6]=4;
       P_a1[2][7]=18;
       
       P_a1[3][0]=2;
       P_a1[3][1]=2;
       P_a1[3][2]=1;
       P_a1[3][3]=5;
       P_a1[3][4]=7;
       P_a1[3][5]=8;
       P_a1[3][6]=4;
       P_a1[3][7]=20;
       
       P_a1[4][0]=9;
       P_a1[4][1]=8;
       P_a1[4][2]=7;
       P_a1[4][3]=1;
       P_a1[4][4]=2;
       P_a1[4][5]=63;
       P_a1[4][6]=4;
       P_a1[4][7]=25;
       
       P_a1[5][0]=8;
       P_a1[5][1]=9;
       P_a1[5][2]=6;
       P_a1[5][3]=5;
       P_a1[5][4]=1;
       P_a1[5][5]=2;
       P_a1[5][6]=4;
       P_a1[5][7]=32;
       
       P_a1[6][0]=8;
       P_a1[6][1]=9;
       P_a1[6][2]=6;
       P_a1[6][3]=5;
       P_a1[6][4]=1;
       P_a1[6][5]=2;
       P_a1[6][6]=4;
       P_a1[6][7]=42;
       
       P_a1[7][0]=4;
       P_a1[7][1]=4;
       P_a1[7][2]=4;
       P_a1[7][3]=4;
       P_a1[7][4]=4;
       P_a1[7][5]=4;
       P_a1[7][6]=4;
       P_a1[7][7]=4;
       
       Q_n1[0][0]=9;
       Q_n1[0][1]=1;
       Q_n1[0][2]=6;
       Q_n1[0][3]=4;
       Q_n1[0][4]=9;
       Q_n1[0][5]=2;
       Q_n1[0][6]=4;
       Q_n1[0][7]=60;
       
       Q_n1[1][0]=55;
       Q_n1[1][1]=41;
       Q_n1[1][2]=21;
       Q_n1[1][3]=1;
       Q_n1[1][4]=5;
       Q_n1[1][5]=9;
       Q_n1[1][6]=4;
       Q_n1[1][7]=60;
       
       Q_n1[2][0]=20;
       Q_n1[2][1]=14;
       Q_n1[2][2]=12;
       Q_n1[2][3]=17;
       Q_n1[2][4]=1;
       Q_n1[2][5]=18;
       Q_n1[2][6]=4;
       Q_n1[2][7]=60;
 
       
       Q_n1[3][0]=6;
       Q_n1[3][1]=9;
       Q_n1[3][2]=8;
       Q_n1[3][3]=4;
       Q_n1[3][4]=1;
       Q_n1[3][5]=3;
       Q_n1[3][6]=4;
       Q_n1[3][7]=60;
       
       Q_n1[4][0]=5;
       Q_n1[4][1]=6;
       Q_n1[4][2]=7;
       Q_n1[4][3]=8;
       Q_n1[4][4]=9;
       Q_n1[4][5]=10;
       Q_n1[4][6]=4;
       Q_n1[4][7]=60;
       
       Q_n1[5][0]=2;
       Q_n1[5][1]=3;
       Q_n1[5][2]=0;
       Q_n1[5][3]=1;
       Q_n1[5][4]=4;
       Q_n1[5][5]=6;
       Q_n1[5][6]=4;
       Q_n1[5][7]=60;
       
       Q_n1[6][0]=8;
       Q_n1[6][1]=9;
       Q_n1[6][2]=6;
       Q_n1[6][3]=5;
       Q_n1[6][4]=1;
       Q_n1[6][5]=2;
       Q_n1[6][6]=4;
       Q_n1[6][7]=60;
      
       Q_n1[7][0]=60;
       Q_n1[7][1]=6;
       Q_n1[7][2]=60;
       Q_n1[7][3]=6;
       Q_n1[7][4]=60;
       Q_n1[7][5]=6;
       Q_n1[7][6]=60;
       Q_n1[7][7]=6;
       
       Matrix x_a= new Matrix(x_a1);
       Matrix P_a= new Matrix(P_a1);
       Matrix Q_n= new Matrix(Q_n1);
       
       printMatrix(x_a,"x_a");
       printMatrix(P_a,"P_a");
       printMatrix(Q_n,"Q_n");
            
       
       m20141215_ukf_meal mukfm= new m20141215_ukf_meal(y,x_a,P_a,R_n,Q_n,Gb);
       
       mukfm.m20141215_ukf_meal(); */
      ///////////////////////////////////////////////////////m20141215_ukf_meal_ END OF TEST//////////////////////////////////////////////////////////////////////////////
      ///////////////////////////////////////////////////////gpc-flupid//////////////////////////////////////////////////////////////////////////////////////  
     /* double [][] A1= new double [4][4]; //It is working
      A1[0][0]=9;
      A1[0][1]=4;
      A1[0][2]=3;
      A1[1][0]=1;
      A1[1][1]=8;
      A1[1][2]=7;
      A1[2][0]=6;
      A1[2][1]=8;
      A1[2][2]=5;
      A1[3][0]=11;
      A1[3][1]=12;
      A1[3][2]=13;
      
      Matrix A = new Matrix (A1);
        
       m20150711_gpc gp = new m20150711_gpc();
       A= gp.flupid(A);
       printMatrix(A,"A");
      
       A=gp.innermatrice(A, 1, 3, 0, 3);
       printMatrix(A,"A");*/
      /////////////////////////////////////////////////////gpc-flupid/////////////////////////////////////////////////////////////////////////////////////////
   
     ///////////////////////////////////////////////////////online_sim_3_6_JF.m////////////////////////////////////////////////////////////////////////////////
    /* load_plsdata730_R_12_withcluster lpls = new load_plsdata730_R_12_withcluster ();
     
     lpls.loadplsdata();
     
     plsdata_730_R_12_withcluster_16_data pls730R = new plsdata_730_R_12_withcluster_16_data();
     
     //printMatrix(pls730R.rc1,"Result");
       
     double [][] x_p=new double [36][1];
     x_p[0][0]=5;
     x_p[1][0]=4;
     x_p[2][0]=9;
     x_p[3][0]=3;
     x_p[4][0]=2;
     x_p[5][0]=1;
     x_p[6][0]=6;
     x_p[7][0]=7;
     x_p[8][0]=8;
     x_p[9][0]=9;
     x_p[10][0]=6;
     x_p[11][0]=3;
     x_p[12][0]=2;
     x_p[13][0]=1;
     x_p[14][0]=3;
     x_p[15][0]=19;
     x_p[16][0]=1;
     x_p[17][0]=2;
     x_p[18][0]=3;
     x_p[19][0]=4;
     x_p[20][0]=5;
     x_p[21][0]=6;
     x_p[22][0]=3;
     x_p[23][0]=2;
     x_p[24][0]=1;
     x_p[25][0]=3;
     x_p[26][0]=6;
     x_p[27][0]=9;
     x_p[28][0]=8;
     x_p[29][0]=7;
     x_p[30][0]=1;
     x_p[31][0]=5;
     x_p[32][0]=2;
     x_p[33][0]=3;
     x_p[34][0]=6;
     x_p[35][0]=4;
     
     Matrix x_samp= new Matrix (x_p);
     
     int R=3;
     int M=2;
     int phi=3;
     int I=2;
     int J=2;
     int alpha=3;
     int sigma1=3;
     int sigma2=2;
     int cluster=2;     
     
     online_sim_3_6_JF osJF = new online_sim_3_6_JF(x_samp,R,M,phi,I,J,alpha,sigma1,sigma2,cluster);
     osJF.onlinesim_3_6_JF();*/
     
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   // m20150711_load_global_variables lglvr = new m20150711_load_global_variables();  
    
     /*double [][] A1= new double [10][10];
    
      A1[0][0]=9;
      A1[0][1]=4;
      A1[0][2]=3;
      A1[1][0]=1;
      A1[1][1]=8;
      A1[1][2]=7;
      A1[2][0]=6;
      A1[2][1]=8;
      A1[2][2]=5;
      A1[3][0]=11;
      A1[3][1]=12;
      A1[3][2]=13;
      A1[4][0]=11;
      A1[4][1]=12;
      A1[4][2]=13;
    
      Matrix A = new Matrix (A1);
      
      int [] f= new int [3];
      f=lastvaluereturnx(A);
      
      System.out.println(f[0]+"   f[0]");
      System.out.println(f[1]+"   f[1]");
      System.out.println(A.getColumnDimension()+"   A Column");
      System.out.println(A.getRowDimension()+"   A Row");*/
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
         /*       int frameSize = 6;
		int displaced =  (frameSize-1)/2;
		int degree = 0;
                
                double[] input = new double[]{5, 2, 1, 3, 8, 9, 4, 7};
//		DecompositionFunctions.printMatrix (new Matrix (new double[][]{input}), "Golay input");
           
                SavitzkyGolayFilterImpl sgfi= new SavitzkyGolayFilterImpl(frameSize, degree);
          
                double [] gInitResult = sgfi.filter(input);
		double [] finalResult = gInitResult;
                
                if (degree == 0)
			for (int i = 0; i <displaced; i ++){
				finalResult[i] =  gInitResult[displaced];
				finalResult[finalResult.length - (1+i)]= gInitResult[finalResult.length - (displaced+1)];
			}
		
		DecompositionFunctions.printMatrix (new Matrix (new double[][]{finalResult}), "Golay results");*/
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
   /////////////////////////////////////////////////////////////CGM_SEDFR_JF///////////////////////////////////////////////////////////////////////////////////////////////////////////////         
       
     /*   
        
        double [][] bolus_insulin = new double [28][1] ;
        double [][] basal_insulin = new double [28][28] ;
        double [][] CGM = new double [1][24] ;
                  bolus_insulin[0][0]=8;
                  bolus_insulin[1][0]=4;
                  bolus_insulin[2][0]=2;
                  bolus_insulin[3][0]=1;
                  bolus_insulin[4][0]=0;
                  bolus_insulin[5][0]=15;
                  bolus_insulin[6][0]=20;
                  bolus_insulin[7][0]=22;
                  bolus_insulin[8][0]=23;
                  bolus_insulin[9][0]=12;
                  bolus_insulin[10][0]=6;
                  bolus_insulin[11][0]=8;
                  bolus_insulin[12][0]=1;
                  bolus_insulin[13][0]=1; 
                  bolus_insulin[14][0]=8;
                  bolus_insulin[15][0]=4;
                  bolus_insulin[16][0]=2;
                  bolus_insulin[17][0]=1;
                  bolus_insulin[18][0]=0;
                  bolus_insulin[19][0]=15;
                  bolus_insulin[20][0]=20;
                  bolus_insulin[21][0]=22;
                  bolus_insulin[22][0]=23;
                  bolus_insulin[23][0]=12;
                  bolus_insulin[24][0]=6;
                  bolus_insulin[25][0]=8;
                  bolus_insulin[26][0]=1;
                  bolus_insulin[27][0]=1; 
                  
                  basal_insulin[0][0]=2;
                  basal_insulin[0][1]=7;
                  basal_insulin[0][2]=8;
                  basal_insulin[0][3]=5;
                  basal_insulin[0][4]=2;
                  basal_insulin[0][5]=3;
                  basal_insulin[0][6]=12;
                  basal_insulin[0][7]=1;
                  basal_insulin[0][8]=4;
                  basal_insulin[0][9]=9;
                  basal_insulin[0][10]=8;
                  basal_insulin[0][11]=3;
                  basal_insulin[0][12]=2;
                  basal_insulin[0][13]=1;
                  basal_insulin[0][14]=2;
                  basal_insulin[0][15]=7;
                  basal_insulin[0][16]=8;
                  basal_insulin[0][17]=5;
                  basal_insulin[0][18]=2;
                  basal_insulin[0][19]=3;
                  basal_insulin[0][20]=12;
                  basal_insulin[0][21]=1;
                  basal_insulin[0][22]=4;
                  basal_insulin[0][23]=9;
                  basal_insulin[0][24]=8;
                  basal_insulin[0][25]=3;
                  basal_insulin[0][26]=2;
                  basal_insulin[0][27]=1;
                  
                  basal_insulin[1][0]=5;
                  basal_insulin[1][1]=7;
                  basal_insulin[1][2]=6;
                  basal_insulin[1][3]=8;
                  basal_insulin[1][4]=4;
                  basal_insulin[1][5]=3;
                  basal_insulin[1][6]=2;
                  basal_insulin[1][7]=7;
                  basal_insulin[1][8]=8;
                  basal_insulin[1][9]=5;
                  basal_insulin[1][10]=2;
                  basal_insulin[1][11]=3;
                  basal_insulin[1][12]=2;
                  basal_insulin[1][13]=3;
                  basal_insulin[1][14]=5;
                  basal_insulin[1][15]=7;
                  basal_insulin[1][16]=6;
                  basal_insulin[1][17]=8;
                  basal_insulin[1][18]=4;
                  basal_insulin[1][19]=3;
                  basal_insulin[1][20]=2;
                  basal_insulin[1][21]=7;
                  basal_insulin[1][22]=8;
                  basal_insulin[1][23]=5;
                  basal_insulin[1][24]=2;
                  basal_insulin[1][25]=3;
                  basal_insulin[1][26]=2;
                  basal_insulin[1][27]=3;
                  
            double [][] CGM2 = new double [1][24] ;      
                  
                  CGM2[0][0]=5;
                  CGM2[0][1]=18;
                  CGM2[0][2]=51;
                  CGM2[0][3]=76;
                  CGM2[0][4]=2;
                  CGM2[0][5]=3;
              /*    
                  CGM2[1][0]=18;
                  CGM2[1][1]=14;
                  CGM2[1][2]=22;
                  CGM2[1][3]=75;
                  CGM2[1][4]=13;
                  CGM2[1][5]=19;   
                  
                  CGM2[2][0]=51;
                  CGM2[2][1]=16;
                  CGM2[2][2]=33;
                  CGM2[2][3]=74;
                  CGM2[2][4]=26;
                  CGM2[2][5]=30; 
                  
                  CGM2[3][0]=76;
                  CGM2[3][1]=14;
                  CGM2[3][2]=55;
                  CGM2[3][3]=73;
                  CGM2[3][4]=70;
                  CGM2[3][5]=79;   
                  
                  CGM2[4][0]=60;
                  CGM2[4][1]=13;
                  CGM2[4][2]=26;
                  CGM2[4][3]=70;
                  CGM2[4][4]=69;
                  CGM2[4][5]=67;
                  
                  CGM2[5][0]=81;
                  CGM2[5][1]=19;
                  CGM2[5][2]=30;
                  CGM2[5][3]=57;
                  CGM2[5][4]=82;
                  CGM2[5][5]=83;   */
                  
           /*       Matrix CGM3 = new Matrix (CGM2);
                          Matrix CGM1 = new Matrix (CGM);
                  Matrix basal_insulin1 = new Matrix (basal_insulin);
                  Matrix bolus_insulin1 = new Matrix (bolus_insulin);
                  int flag_noise=1;
                  
                    CGM__SEDFR_JF cgm = new CGM__SEDFR_JF(CGM3,bolus_insulin1,basal_insulin1,flag_noise);
                  cgm.CGM();
                  
                  CGM[0][0]=5;
                  CGM[0][1]=2;
                  CGM[0][2]=3;
                  CGM[0][3]=5;
                  CGM[0][4]=2;
                  CGM[0][5]=3;
                
                  CGM[0][6]=18;
                  CGM[0][7]=14;
                  CGM[0][8]=16;
                  CGM[0][9]=14;
                  CGM[0][10]=13;
                  CGM[0][11]=19;
                  
                  CGM[0][12]=51;
                  CGM[0][13]=22;
                  CGM[0][14]=33;
                  CGM[0][15]=55;
                  CGM[0][16]=26;
                  CGM[0][17]=30;
                  
                  CGM[0][18]=76;
                  CGM[0][19]=75;
                  CGM[0][20]=74;
                  CGM[0][21]=73;
                  CGM[0][22]=70;
                  CGM[0][23]=79;
                  
                
                  
          
                  
                  cgm = new CGM__SEDFR_JF(CGM1,bolus_insulin1,basal_insulin1,flag_noise);
                  double Cgmvalue=cgm.CGM();
                  
                  System.out.println(Cgmvalue+  "   Cgmvalue");
                 /* temp_SEDFR_noise tnoise =new temp_SEDFR_noise();
                  
              
                   System.out.println(tnoise.sigma_a+ "sigma_a");
                   System.out.println(tnoise.phi_max+ "phi_max");*/
    /////////////////////////////////////////////////////CGM_SEDFR_JF ///////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////Main Function////////////////////////////////////////////////////////////////////////////////////////
           /*    double [][] ee = new double [8000][30];
               double [][] phys_act = new double [8000][30];
               double [][] sleep = new double [8000][30];
               double [][] gsr = new double [8000][30];
               double [][] armbanddata = new double [8000][30];
               
               Matrix ee1=new Matrix (ee);
               Matrix gsr1=new Matrix (gsr);
               Matrix sleep1=new Matrix (sleep);
               Matrix phys_act1=new Matrix (phys_act);
               Matrix armbanddata1=new Matrix (phys_act);
               
               Matrix result = new Matrix(7500,5);
        
               m20150711_get_armband_data getarmbanddata = new m20150711_get_armband_data(armbanddata1,ee1,phys_act1,sleep1,phys_act1);   
               result=getarmbanddata.m20150711_get_armband_data();
               System.out.println(getarmbanddata.eedouble+"   ee double");
               System.out.println(getarmbanddata.gsrdouble+"   gsr double");
               System.out.println(getarmbanddata.sleepdouble+"   sleep double");
               System.out.println(getarmbanddata.phys_actdouble+"   phys_actdouble double");*/
      /*  Matrix P_a= new Matrix (2,2);
        P_a.set(0, 0, 0.2638);
        
        P_a.set(0, 1, 0.1361);
        
        P_a.set(1, 0, 0.1455);
        
        P_a.set(1,  1, 0.8693);
        
        
        Ldl ldlfind =new Ldl (P_a.times(P_a.transpose()));
        ldlfind.LdlFind();
         
        Matrix LL=ldlfind.L;
      
        printMatrix(LL,"LL");
      
        Matrix DD=ldlfind.D;
       
        printMatrix(DD,"DD");*/
        
      //  test tests = new test();
      //  tests.Test_of_m20141215_sigma_const();
               
    
        
        
       public static void printMatrix(Matrix m, String name){
		System.out.print("\n "+name+": \n{");
		for (double[] row: m.getArray()){
			for (double val: row)
				System.out.print(" "+val);
			System.out.println();
		}
		System.out.println("}");
	}
   
   
}
