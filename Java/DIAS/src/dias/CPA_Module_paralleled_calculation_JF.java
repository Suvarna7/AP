/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import Jama.Matrix;
import dias.MemoryStaticVariables.ChangeVariable;
/**
 *
 * @author Mert
 */
public class CPA_Module_paralleled_calculation_JF {
    
    public Matrix gs;
    public Matrix ee;
    public Matrix gsr;
    public int kj;
    public Matrix phi;
    public Matrix phi_ee;
    public Matrix phi_gsr;
    public Matrix armax_parameters;
    public double [][][] armax_covariance;
    public Matrix armax_lamda;
    public Matrix armax_error;
    public Matrix armax_parameters_ee;
    public Matrix arma_lamda_ee;
    public double [][][] arma_covariance_ee;
    public Matrix arma_err_ee;
    public Matrix arma_parameters_gsr;
    public Matrix arma_lamda_gsr;
    public double [][][] arma_covariance_gsr;
    public Matrix arma_err_gsr;
    public double [][][] A_state;
    public double [][][] A_state_ee;
    public double [][][] A_state_gsr;
    public double [][][] C_state;
    public double [][][] C_state_ee;
    public double [][][] C_state_gsr;
    public double [][][] B_state;
    public double [][][] K_state;
    public double [][][] K_state_ee;
    public double [][][] K_state_gsr;
    public double [][][] M; 
    public double [][][] L;
    public double [][][] L_ee;
    public double [][][] L_gsr;
    public double [][][] M_ee;
    public double [][][] M_gsr;
    public Matrix X_state;
    public Matrix X_state_ee; 
    public Matrix X_state_gsr; 
    public Matrix ee_prediction;
    public Matrix gsr_prediction;
    public Matrix g_prediction;
    public Matrix reference_glucose;
    public Matrix insulin_sensitivity_constant;
    public Matrix basal_insulin;
    public Matrix IOB_prediction;
    public Matrix max_insulin;
    public Matrix total_daily_unit;
    public Matrix insulin_sensitivity_factor;
    public double body_weight;
    public Matrix meal_gpc_mu;
    public Matrix bolus_insulin;
    
    public Matrix gs_CPA;
    public Matrix ins_CPA;
    public Matrix lamda_CPA;
    public Matrix g_prediction_CPA;
    public double denominator;
    public double factor_insulins;
    public Matrix I_track;
    public Matrix insulin_CPA=new Matrix (8,1);
    
    public Matrix g_prediction_feedback =new Matrix (8,21);
    public Matrix flag_constrains=new Matrix (1,21);
    public double insulin_error_feedback;
    
    public Matrix sensor_error = new Matrix (20,1);
    public Matrix compensate_ins= new Matrix (20,1);
    public Matrix flag_ratio = new Matrix (20,1);
    
    public double EE;
    public double ME;
    
    public CPA_Module_paralleled_calculation_JF(Matrix gstemp, Matrix ee, Matrix gsr, int kj, Matrix phi, Matrix phi_ee, Matrix phi_gsr, Matrix armax_parameters, double[][][] armax_covariance, Matrix armax_lamda, Matrix armax_err, Matrix arma_parameters_ee, Matrix arma_lamda_ee, double[][][] arma_covariance_ee, Matrix arma_err_ee, Matrix arma_parameters_gsr, Matrix arma_lamda_gsr, double[][][] arma_covariance_gsr, Matrix arma_err_gsr, double[][][] A_state, double[][][] A_state_ee, double[][][] A_state_gsr, double[][][] C_state, double[][][] C_state_ee, double[][][] C_state_gsr, double[][][] B_state, double[][][] K_state, double[][][] K_state_ee, double[][][] K_state_gsr, double[][][] M, double[][][] L, double[][][] L_ee, double[][][] L_gsr, double[][][] M_ee, double[][][] M_gsr, Matrix X_state, Matrix X_state_ee, Matrix X_state_gsr, Matrix ee_prediction, Matrix gsr_prediction, Matrix g_prediction, Matrix reference_glucose, Matrix insulin_sensitivity_constant, Matrix basal_insulin, Matrix IOB_prediction, Matrix maximum_insulin, Matrix total_daily_unit, Matrix insulin_sensitivity_factor, double body_weight, Matrix meal_gpc_mu, Matrix bolus_insulin) {
    this.A_state=A_state;
    this.A_state_ee=A_state_ee;
    this.A_state_gsr=A_state_gsr;
    this.B_state=B_state;
    this.C_state=C_state;
    this.C_state_ee=C_state_ee;
    this.C_state_gsr=C_state_gsr;
    this.IOB_prediction=IOB_prediction;
    this.K_state=K_state;
    this.K_state_ee=K_state_ee;
    this.K_state_gsr=K_state_gsr;
    this.L=L;
    this.L_ee=L_ee;
    this.L_gsr=L_gsr;
    this.M=M;
    this.M_ee=M_ee;
    this.M_gsr=M_gsr;
    this.X_state=X_state;
    this.X_state_ee=X_state_ee;
    this.X_state_gsr=X_state_gsr;
    this.arma_covariance_ee=arma_covariance_ee;
    this.arma_covariance_gsr=arma_covariance_gsr;
    this.arma_err_ee=arma_err_ee;
    this.arma_err_gsr=arma_err_gsr;
    this.arma_parameters_gsr=arma_parameters_gsr;
    this.armax_covariance=armax_covariance;
    this.arma_lamda_ee=arma_lamda_ee;
    this.arma_lamda_gsr=arma_lamda_gsr;
    this.armax_lamda=armax_lamda;
    this.armax_error=armax_err;
    this.armax_parameters=armax_parameters;
    this.armax_parameters_ee=arma_parameters_ee;
    this.basal_insulin=basal_insulin;
    this.body_weight=body_weight;
    this.bolus_insulin=bolus_insulin;
    this.ee=ee;
    this.ee_prediction=ee_prediction;
    this.g_prediction=g_prediction;
    this.gs=gstemp;
    this.gsr=gsr;
    this.gsr_prediction=gsr_prediction;
    this.insulin_sensitivity_constant=insulin_sensitivity_constant;
    this.insulin_sensitivity_factor=insulin_sensitivity_factor;
    this.kj=kj;
    this.max_insulin=maximum_insulin;
    this.meal_gpc_mu=meal_gpc_mu;
    this.phi=phi;
    this.phi_ee=phi_ee;
    this.phi_gsr=phi_gsr;
    this.reference_glucose=reference_glucose;
    this.total_daily_unit=total_daily_unit;
    }

    
    public void CPA () throws Exception{
        prevdata_error_summation pesm = new prevdata_error_summation ();
         
     /*   /////////////////////////////////////////INPUTS CPA////////////////////////////////////////////////////////////////////////////////////////////////
        System.out.println("////////////////////////////////////////////////INPUTS_CPA///////////////////////////////////////////////////////////////////");
        System.out.println(kj+"kj");
        printMatrix(total_daily_unit,"total_daily_unit");
        printMatrix(reference_glucose,"reference_glucose");
        printMatrix(phi_gsr,"phi_gsr");
        printMatrix(phi_ee,"phi_ee");
        printMatrix(phi,"phi");
        printMatrix(meal_gpc_mu,"meal_gpc_mu");
        printMatrix(max_insulin,"max_insulin");
        printMatrix(insulin_sensitivity_factor,"insulin_sensitivity_factor");
        printMatrix(insulin_sensitivity_constant,"insulin_sensitivity_constant");
        printMatrix(gsr_prediction,"gsr_prediction");
        printMatrix(gsr,"gsr");
        printMatrix(gs,"gs");
        printMatrix(g_prediction,"g_prediction");
        printMatrix(ee_prediction,"ee_prediction");
        printMatrix(ee,"ee");
        printMatrix(bolus_insulin,"bolus_insulin");
        printMatrix(basal_insulin,"basal_insulin");
        printMatrix(armax_parameters_ee,"armax_parameters_ee");
        printMatrix(armax_parameters,"armax_parameters");
        printMatrix(armax_error,"armax_error");
        printMatrix(armax_lamda,"armax_lamda");
        printMatrix(arma_lamda_gsr,"armax_lamda_gsr");
        printMatrix(arma_lamda_ee,"arma_lamda_ee");
        print3Dmatrice(armax_covariance,"armax_covariance");
        printMatrix(arma_parameters_gsr,"arma_parameters_gsr");
        printMatrix(arma_err_gsr,"arma_err_gsr");
        printMatrix(arma_err_ee,"arma_err_ee");
        print3Dmatrice(arma_covariance_gsr,"arma_covariance_gsr");
        print3Dmatrice(arma_covariance_ee,"arma_covariance_ee");
        printMatrix(X_state_gsr,"X_state_gsr");
        printMatrix(X_state_ee,"X_state_ee");
        printMatrix(X_state,"X_state");
        print3Dmatrice(M_gsr,"M_gsr");
        print3Dmatrice(M_ee,"M_ee");
        print3Dmatrice(M,"M");
        print3Dmatrice(L_gsr,"L_gsr");
        print3Dmatrice(L_ee,"L_ee");
        print3Dmatrice(L,"L");
        print3Dmatrice(K_state_gsr,"K_state_gsr");
        print3Dmatrice(K_state_ee,"K_state_ee");
        print3Dmatrice(K_state,"K_state");
        printMatrix(IOB_prediction,"IOB_prediction");
        print3Dmatrice(C_state_gsr,"C_state_gsr");
        print3Dmatrice(C_state_ee,"C_state_ee");
        print3Dmatrice(C_state,"C_state");
        print3Dmatrice(B_state,"B_state");
        print3Dmatrice(A_state_gsr,"A_state_gsr");
        print3Dmatrice(A_state_ee,"A_state_ee");
        print3Dmatrice(A_state,"A_state");
        System.out.println("//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
        
        printMatrix(t.I_track,"   t.Itrack");
        printMatrix(t.data_mem,"   t.data_mem");
        printMatrix(t.I_u_constrain,"   t.I_u_constrain");
        System.out.println(t.I_error_rspeed+"     t.I_error_rspeed");
        System.out.println(t.I_me_inst+"     t.I_me_inst");
        System.out.println("//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
        
        printMatrix(c.insulin_CPA,"   c.insulin_CPA");
        printMatrix(c.sensor_error,"   c.sensor_error");
        printMatrix(c.flag_constrains,"   c.flag_constrains");
        printMatrix(c.flag_ratio,"   c.flag_ratio");
        printMatrix(c.lamda_CPA,"   c.lamda_CPA");
        printMatrix(c.g_prediction_feedback,"   c.g_prediction_feedback");
        printMatrix(c.ins_CPA,"   c.ins_CPA");
        printMatrix(c.compensate_ins,"  c.compensate_ins");
        
        System.out.println("//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
         
        printMatrix(pesm.error_summation,"pesm.error_summation");
        printMatrix(pesm.D_potential,"pesm.D_potential");
        System.out.println(pesm.EE+pesm.EE);
        System.out.println(pesm.ME+pesm.ME);
    
        System.out.println("////////////////////////////////////////////////INPUTS_CPA///////////////////////////////////////////////////////////////////");
        ///////////////////////////////////////INPUTS CPA//////////////////////////////////////////////////////////////////////////////////////////////////*/
      
        controller_assessment_index_071215_JF cont_assesment_index = new controller_assessment_index_071215_JF(max_insulin, L,reference_glucose, insulin_sensitivity_factor,kj,gs,basal_insulin,body_weight,g_prediction);
        I_track=cont_assesment_index.controller_assessment_index_071215_JF();
       
        if(kj>25){
             
             ins_CPA = new Matrix (ChangeVariable.ins_CPA.getRowDimension(),ChangeVariable.ins_CPA.getColumnDimension()); 
             
             for(int i=0;i<ChangeVariable.ins_CPA.getRowDimension();i++)
                 for(int j=0;j<ChangeVariable.ins_CPA.getColumnDimension();j++)
             ins_CPA.set(i,j, ChangeVariable.ins_CPA.get(i, j));

             g_prediction_feedback = new Matrix (ChangeVariable.g_prediction_feedback.getRowDimension(),
                     ChangeVariable.g_prediction_feedback.getColumnDimension());
             
                for(int i=0;i<ChangeVariable.g_prediction_feedback.getRowDimension();i++)
                 for(int j=0;j<ChangeVariable.g_prediction_feedback.getColumnDimension();j++)
                         g_prediction_feedback.set(i,j, ChangeVariable.g_prediction_feedback.get(i, j));

             flag_constrains = new Matrix (ChangeVariable.flag_constrains.getRowDimension(),
                     ChangeVariable.flag_constrains.getColumnDimension());
             
               for(int i=0;i<ChangeVariable.flag_constrains.getRowDimension();i++)
                 for(int j=0;j<ChangeVariable.flag_constrains.getColumnDimension();j++)
                     flag_constrains.set(i, j, ChangeVariable.flag_constrains.get(i, j));
   
             compensate_ins = new Matrix (ChangeVariable.compensate_ins.getRowDimension(),
                     ChangeVariable.compensate_ins.getColumnDimension());
             
              for(int i=0;i<ChangeVariable.compensate_ins.getRowDimension();i++)
                 for(int j=0;j<ChangeVariable.compensate_ins.getColumnDimension();j++)
                     compensate_ins.set(i, j,ChangeVariable.compensate_ins.get(i, j));
     
            lamda_CPA = new Matrix (ChangeVariable.lamda_CPA.getRowDimension(),ChangeVariable.lamda_CPA.getColumnDimension());
             
              for(int i=0;i<ChangeVariable.lamda_CPA.getRowDimension();i++)
                 for(int j=0;j<ChangeVariable.lamda_CPA.getColumnDimension();j++)
                     lamda_CPA.set(i, j, ChangeVariable.lamda_CPA.get(i, j));
  
            sensor_error = new Matrix (ChangeVariable.sensor_error.getRowDimension(),ChangeVariable.sensor_error.getColumnDimension());
             
             for(int i=0;i<ChangeVariable.sensor_error.getRowDimension();i++)
                 for(int j=0;j<ChangeVariable.sensor_error.getColumnDimension();j++)
                        sensor_error.set(i, j, ChangeVariable.sensor_error.get(i, j));
   
            flag_ratio = new Matrix (ChangeVariable.flag_ratio.getRowDimension(),ChangeVariable.flag_ratio.getColumnDimension());  
              
             for(int i=0;i<ChangeVariable.flag_ratio.getRowDimension();i++)
                 for(int j=0;j<ChangeVariable.flag_ratio.getColumnDimension();j++)
                        flag_ratio.set(i, j, ChangeVariable.flag_ratio.get(i, j));

             insulin_CPA = new Matrix (ChangeVariable.insulin_CPA.getRowDimension(),ChangeVariable.insulin_CPA.getColumnDimension());  
              
             for(int i=0;i<ChangeVariable.insulin_CPA.getRowDimension();i++)
                 for(int j=0;j<ChangeVariable.insulin_CPA.getColumnDimension();j++)
                        insulin_CPA.set(i, j, ChangeVariable.insulin_CPA.get(i, j));
 
            prevdata_error_summation pdes = new prevdata_error_summation ();
            
            EE=pdes.EE;
            ME=pdes.ME;

            ins_CPA = new Matrix (basal_insulin.getRowDimension(),basal_insulin.getColumnDimension());
            
            for(int i=0;i<basal_insulin.getRowDimension();i++)
                 for(int j=0;j<basal_insulin.getColumnDimension();j++)
                         ins_CPA.set(i, j, basal_insulin.get(i, j));

             lamda_CPA = new Matrix (armax_lamda.getRowDimension(),armax_lamda.getColumnDimension());
            
            for(int i=0;i<armax_lamda.getRowDimension();i++)
                 for(int j=0;j<armax_lamda.getColumnDimension();j++)
                         lamda_CPA.set(i, j, armax_lamda.get(i, j));
  
             gs_CPA =new Matrix (gs.getRowDimension(),gs.getColumnDimension());
             
             for( int i=0;i<gs.getRowDimension(); i++)
                 for( int j=0;j<gs.getColumnDimension(); j++)
                     gs_CPA.set(i, j, gs.get(i, j));
          
             g_prediction_CPA= new Matrix (g_prediction.getRowDimension(),g_prediction.getColumnDimension());
             
             for(int i=0;i<g_prediction.getRowDimension();i++)
                 for(int j=0;j<g_prediction.getColumnDimension();j++)
                             g_prediction_CPA.set(i, j, g_prediction.get(i, j));
    
             denominator=60;
             factor_insulins=1;
             
             
             double trh=0.5;
             double tempy=(pesm.error_summation.get(0,pesm.error_summation.getColumnDimension()-2));
             double tempx=(pesm.error_summation.get(1,pesm.error_summation.getColumnDimension()-2));
    
            //  %% sequence: error; Model_error;Umax_error; ratio_error; weight_error;Insulin_dose_error;other;
             if (!Double.isNaN(tempx)){
    //    trh=0.5;
        if (pesm.ME<30 && (1-pesm.ME/denominator)*lamda_CPA.get(kj,0)>trh)
            lamda_CPA.set(kj,0,(1-pesm.ME/denominator)*lamda_CPA.get(kj,0));
        else
            lamda_CPA.set(kj,0,trh);
        }
  
             double CGM_reading_factor;

             if ((gs_CPA.get(0,kj-1)-gs_CPA.get(0,kj-2))*(gs_CPA.get(0,kj-2)-gs_CPA.get(0,kj-3))<0 &&(gs_CPA.get(0,kj-3)-gs_CPA.get(0,kj-4))*(gs_CPA.get(0,kj-2)-gs_CPA.get(0,kj-3))<0){
        sensor_error.set(kj-1,0,1);
        CGM_reading_factor=gs_CPA.get(0,kj-2)-(gs_CPA.get(0,kj-1)+gs_CPA.get(0,kj-3))/2;
        gs_CPA.set(0,kj-2,(gs_CPA.get(0,kj-1)+gs_CPA.get(0,kj-3))/2);
             } else{
        CGM_reading_factor=0;
        sensor_error.set(kj-1,0,0);
             }
             
            double CGM_factor=0;
           
      //      System.out.println(pesm.error_summation.get(pesm.error_summation.getRowDimension()-1, pesm.error_summation.getColumnDimension()-2)+"  pesm.error_summation");
            
               if (!Double.isNaN(tempx))
        CGM_factor=pesm.error_summation.get(pesm.error_summation.getRowDimension()-1,pesm.error_summation.getColumnDimension()-2);
        
         if ((tempy!=-1) && (!Double.isNaN(tempx)) || (sensor_error.get(kj-1,0)==1)){
        if ((pesm.error_summation.get(pesm.error_summation.getRowDimension()-1,pesm.error_summation.getColumnDimension()-2)+CGM_reading_factor)>0 && pesm.error_summation.get(0,pesm.error_summation.getColumnDimension()-2)!=1 && (pesm.error_summation.get(pesm.error_summation.getRowDimension()-1,pesm.error_summation.getColumnDimension()-2)+CGM_reading_factor)>10)
            insulin_error_feedback=-(Math.log10(pesm.error_summation.get(pesm.error_summation.getRowDimension()-1,pesm.error_summation.getColumnDimension()-2)+CGM_reading_factor))/(4.5*60/body_weight);
        else if ((pesm.error_summation.get(pesm.error_summation.getRowDimension()-1,pesm.error_summation.getColumnDimension()-2)+CGM_reading_factor)<0 && gs_CPA.get(0,kj-1)>70 && (pesm.error_summation.get(pesm.error_summation.getRowDimension()-1,pesm.error_summation.getColumnDimension()-2)+CGM_reading_factor)<-10)
                insulin_error_feedback=Math.log10(Math.abs(pesm.error_summation.get(pesm.error_summation.getRowDimension()-1,pesm.error_summation.getColumnDimension()-2)+CGM_reading_factor))/(4.5*60/body_weight)*factor_insulins;
         }
         else{
        insulin_error_feedback=0;
        } 
         
        if (pesm.error_summation.get(2,pesm.error_summation.getColumnDimension()-2)==0)
        flag_constrains.set(0,kj-1,1);
        else
        flag_constrains.set(0,kj-1,0);
         
    if (pesm.error_summation.get(0,pesm.error_summation.getColumnDimension()-2)==-1)
       flag_constrains.set(0,kj-1,-1);

     if (kj>30){
         for(int i=0;i<8;i++)
        g_prediction_feedback.set(i,kj-1,CGM_factor*(-0.35));
     }else{
         for(int i=0;i<8;i++)
        g_prediction_feedback.set(i,kj-1,0);  
     }

      if (pesm.error_summation.get(3,pesm.error_summation.getColumnDimension()-2)==1)
        flag_ratio.set(kj-1,0,1);
    else
        flag_ratio.set(kj-1,0,0);

  /*  if (flag_ratio.get(0, 0)==1)     ////************************************* ASK !!!!!!!!!!!!!!!!!!!!!!
        for(int i=0;i<8;i++)
             for(int j=0;j<kj-1;j++)
            insulin_sensitivity_constant.set(i,j,1/10000);*/  ////////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     
     compensate_ins.set(kj-1,0,insulin_error_feedback*(bolus_insulin.get(bolus_insulin.getRowDimension()-1,0)*12+basal_insulin.get(0,basal_insulin.getColumnDimension()-1)));
     double paralleled=1;

     Matrix tempg_predict = new Matrix (g_prediction_feedback.getRowDimension(),1);

     for( int i=0;i<g_prediction_feedback.getRowDimension();i++)
         tempg_predict.set(i,0,g_prediction_feedback.get(i, kj-2));
    
     m20150711_gpc m_gpc = new m20150711_gpc(gs,ee,gsr,kj,previousvalue(phi),previousvalue(phi_ee),previousvalue(phi_gsr),previousvalue(armax_parameters),previousvalue3(armax_covariance),previousvalue2(lamda_CPA),previousvalue2(armax_error),previousvalue(armax_parameters_ee),previousvalue2(arma_lamda_ee),previousvalue3(arma_covariance_ee),previousvalue2(arma_err_ee),previousvalue(arma_parameters_gsr),previousvalue2(arma_lamda_gsr),previousvalue3(arma_covariance_gsr),previousvalue(arma_err_gsr),previousvalue3(A_state),previousvalue3(A_state_ee),previousvalue3(A_state_gsr),previousvalue3(C_state),previousvalue3(C_state_ee),previousvalue3(C_state_gsr),previousvalue3(B_state),previousvalue3(K_state),previousvalue3(K_state_ee),previousvalue3(K_state_gsr),previousvalue3(M),previousvalue3(L),previousvalue3(L_ee),previousvalue3(L_gsr),previousvalue3(M_ee),previousvalue3(M_gsr),previousvalue(X_state),previousvalue(X_state_ee),previousvalue(X_state_gsr),previousvalue(ee_prediction),previousvalue(gsr_prediction),previousvalue(g_prediction),previousvalue(reference_glucose),previousvalue(insulin_sensitivity_constant),previousvalue(basal_insulin),previousvalue(IOB_prediction),previousvalue(max_insulin),previousvalue(total_daily_unit),previousvalue(insulin_sensitivity_factor),body_weight,previousvalue(meal_gpc_mu),previousvalue2(bolus_insulin),flag_constrains.get(0, flag_constrains.getColumnDimension()-1),tempg_predict);
        
      for(int i=0;i<m_gpc.basal_insulin.getRowDimension();i++)
      ins_CPA.set(i,kj-1,m_gpc.basal_insulin.get(i,kj-1));
      
      if (ins_CPA.get(0,kj-1)+ compensate_ins.get(kj-1,0)>max_insulin.get(0,max_insulin.getColumnDimension()-1)*2)
        ins_CPA.set(0,kj-1,max_insulin.get(0,max_insulin.getColumnDimension()-1)*2);
    else
        ins_CPA.set(0,kj-1,ins_CPA.get(0,kj-1)+ compensate_ins.get(kj-1,0));

       if (ins_CPA.get(0,kj-1)>35)
        ins_CPA.set(0,kj-1,35);
    else if (ins_CPA.get(0,kj-1)<0)
            ins_CPA.set(0,kj-1,0);
      
   if (kj<=25)
        ins_CPA.set(0,kj-1,0);

   
   double tempz=(pesm.error_summation.get(1,pesm.error_summation.getColumnDimension()-2));
   double tempv=(pesm.error_summation.get(2,pesm.error_summation.getColumnDimension()-2));
   double tempw=(pesm.error_summation.get(3,pesm.error_summation.getColumnDimension()-2));
   
   if ((Double.isNaN(tempz)) &&  (Double.isNaN(tempv)) &&  (Double.isNaN(tempw)) && (sensor_error.get(kj-1,0)==0)) {
       for(int i=0;i<m_gpc.basal_insulin.getRowDimension();i++){
       insulin_CPA.set(i,kj-2,m_gpc.basal_insulin.get(i,kj-1));
            }
   }
    else {
        for(int i=0;i<ins_CPA.getRowDimension();i++)
        insulin_CPA.set(i,kj-2,ins_CPA.get(i,kj-1));
   }

   for(int i=0;i<insulin_CPA.getRowDimension();i++)
        for(int j=0;j<insulin_CPA.getColumnDimension();j++)
           ChangeVariable.insulin_CPA.set(i, j, insulin_CPA.get(i, j));

    for(int i=0;i<compensate_ins.getRowDimension();i++)
        for(int j=0;j<compensate_ins.getColumnDimension();j++)
           ChangeVariable.compensate_ins.set(i, j, compensate_ins.get(i, j));
  
    for(int i=0;i<sensor_error.getRowDimension();i++)
        for(int j=0;j<sensor_error.getColumnDimension();j++)
           ChangeVariable.sensor_error.set(i, j, sensor_error.get(i, j));
  
      for(int i=0;i<flag_ratio.getRowDimension();i++)
        for(int j=0;j<flag_ratio.getColumnDimension();j++)
           ChangeVariable.flag_ratio.set(i, j, flag_ratio.get(i, j));

       for(int i=0;i<flag_constrains.getRowDimension();i++)
        for(int j=0;j<flag_constrains.getColumnDimension();j++)
           ChangeVariable.flag_constrains.set(i, j, flag_constrains.get(i, j));
  
       ChangeVariable.lamda_CPA = new Matrix (lamda_CPA.getRowDimension(),lamda_CPA.getColumnDimension());
       
        for(int i=0;i<lamda_CPA.getRowDimension();i++)
        for(int j=0;j<lamda_CPA.getColumnDimension();j++)
           ChangeVariable.lamda_CPA.set(i, j, lamda_CPA.get(i, j));
       
        ChangeVariable.ins_CPA = new Matrix (ins_CPA.getRowDimension(),ins_CPA.getColumnDimension());
        
        for(int i=0;i<ins_CPA.getRowDimension();i++)
        for(int j=0;j<ins_CPA.getColumnDimension();j++)
           ChangeVariable.ins_CPA.set(i, j, ins_CPA.get(i, j));
   
        for(int i=0;i<g_prediction_feedback.getRowDimension();i++)
        for(int j=0;j<g_prediction_feedback.getColumnDimension();j++)
           ChangeVariable.g_prediction_feedback.set(i, j, g_prediction_feedback.get(i, j));

             ChangeVariable.sensor_error= createnewMatrix(kj+1,1,ChangeVariable.sensor_error);
             ChangeVariable.compensate_ins= createnewMatrix(kj+1,1,ChangeVariable.compensate_ins);
             ChangeVariable.flag_ratio= createnewMatrix(kj+1,1,ChangeVariable.flag_ratio);
             ChangeVariable.insulin_CPA= createnewMatrix(8,kj+1,ChangeVariable.insulin_CPA);
             ChangeVariable.flag_constrains= createnewMatrix(1,kj+1,ChangeVariable.flag_constrains);
             ChangeVariable.g_prediction_feedback= createnewMatrix(8,kj+1,ChangeVariable.g_prediction_feedback);
             ChangeVariable.ins_CPA= createnewMatrix(8,kj+1,ChangeVariable.ins_CPA);
             ChangeVariable.lamda_CPA= createnewMatrix(kj+1,1,ChangeVariable.lamda_CPA);
       
        }
        else{
           
        insulin_CPA = new Matrix (basal_insulin.getRowDimension(),basal_insulin.getColumnDimension()) ;   
        ins_CPA = new Matrix (basal_insulin.getRowDimension(),basal_insulin.getColumnDimension()) ;   
        lamda_CPA = new Matrix (armax_lamda.getRowDimension(),armax_lamda.getColumnDimension()) ;   
            
        for(int i=0;i<basal_insulin.getRowDimension();i++)
           for(int j=0;j<basal_insulin.getColumnDimension();j++)
               insulin_CPA.set(i, j, basal_insulin.get(i, j));
        
        for(int i=0;i<basal_insulin.getRowDimension();i++)
           for(int j=0;j<basal_insulin.getColumnDimension();j++)
               ins_CPA.set(i, j, basal_insulin.get(i, j));

        for(int i=0;i<armax_lamda.getRowDimension();i++)
           for(int j=0;j<armax_lamda.getColumnDimension();j++)
               lamda_CPA.set(i, j, armax_lamda.get(i, j));
 
        for(int i=0;i<ChangeVariable.g_prediction_feedback.getRowDimension();i++)
                 ChangeVariable.g_prediction_feedback.set(i,ChangeVariable.g_prediction_feedback.getRowDimension()-1,0); 
    
        ChangeVariable.insulin_CPA= new Matrix (insulin_CPA.getRowDimension(),insulin_CPA.getColumnDimension());  
        
       for(int i=0;i<insulin_CPA.getRowDimension();i++)
        for(int j=0;j<insulin_CPA.getColumnDimension();j++)
           ChangeVariable.insulin_CPA.set(i, j, insulin_CPA.get(i, j));
 
      
       ChangeVariable.compensate_ins= new Matrix (compensate_ins.getRowDimension(),compensate_ins.getColumnDimension());
       
       for(int i=0;i<compensate_ins.getRowDimension();i++)
        for(int j=0;j<compensate_ins.getColumnDimension();j++)
           ChangeVariable.compensate_ins.set(i, j, compensate_ins.get(i, j));
    
      ChangeVariable.sensor_error= new Matrix (sensor_error.getRowDimension(),sensor_error.getColumnDimension()); 
       
      for(int i=0;i<sensor_error.getRowDimension();i++)
        for(int j=0;j<sensor_error.getColumnDimension();j++)
           ChangeVariable.sensor_error.set(i, j, sensor_error.get(i, j));
   
      ChangeVariable.flag_ratio= new Matrix (flag_ratio.getRowDimension(),flag_ratio.getColumnDimension()); 
    
      for(int i=0;i<flag_ratio.getRowDimension();i++)
        for(int j=0;j<flag_ratio.getColumnDimension();j++)
           ChangeVariable.flag_ratio.set(i, j, flag_ratio.get(i, j));
 
       ChangeVariable.flag_constrains= new Matrix (flag_constrains.getRowDimension(),flag_constrains.getColumnDimension()); 
    
       for(int i=0;i<flag_constrains.getRowDimension();i++)
        for(int j=0;j<flag_constrains.getColumnDimension();j++)
           ChangeVariable.flag_constrains.set(i, j, flag_constrains.get(i, j));
  
       ChangeVariable.lamda_CPA= new Matrix (lamda_CPA.getRowDimension(),lamda_CPA.getColumnDimension()); 
       
        for(int i=0;i<lamda_CPA.getRowDimension();i++)
        for(int j=0;j<lamda_CPA.getColumnDimension();j++)
           ChangeVariable.lamda_CPA.set(i, j, lamda_CPA.get(i, j));
 
        ChangeVariable.ins_CPA= new Matrix (ins_CPA.getRowDimension(),ins_CPA.getColumnDimension()); 
        
        for(int i=0;i<ins_CPA.getRowDimension();i++)
        for(int j=0;j<ins_CPA.getColumnDimension();j++)
           ChangeVariable.ins_CPA.set(i, j, ins_CPA.get(i, j));
        
        ChangeVariable.g_prediction_feedback= new Matrix (g_prediction_feedback.getRowDimension(),g_prediction_feedback.getColumnDimension()); 
   
        for(int i=0;i<g_prediction_feedback.getRowDimension();i++)
        for(int j=0;j<g_prediction_feedback.getColumnDimension();j++)
          ChangeVariable.g_prediction_feedback.set(i, j, g_prediction_feedback.get(i, j));
          
             ChangeVariable.sensor_error= createnewMatrix(kj+1,1,ChangeVariable.sensor_error);
             ChangeVariable.compensate_ins= createnewMatrix(kj+1,1,ChangeVariable.compensate_ins);
             ChangeVariable.flag_ratio= createnewMatrix(kj+1,1,ChangeVariable.flag_ratio);
             ChangeVariable.insulin_CPA= createnewMatrix(8,kj+1,ChangeVariable.insulin_CPA);
             ChangeVariable.flag_constrains= createnewMatrix(1,kj+1,ChangeVariable.flag_constrains);
             ChangeVariable.g_prediction_feedback= createnewMatrix(8,kj+1,ChangeVariable.g_prediction_feedback);
             ChangeVariable.ins_CPA= createnewMatrix(8,kj+1,ChangeVariable.ins_CPA);
             ChangeVariable.lamda_CPA= createnewMatrix(kj+1,1,ChangeVariable.lamda_CPA);
        
        }    
    }
    
            public static void printMatrix(Matrix m, String name){
		System.out.print("\n "+name+": \n{");
		for (double[] row: m.getArray()){
			for (double val: row)
				System.out.print(" "+val);
			System.out.println();
		}
		System.out.println("}");
	}
            
              public Matrix createnewMatrix (int newdimensionx,int newdimensiony, Matrix oldmatrice){
               Matrix newMatrice = new Matrix (newdimensionx,newdimensiony);
               
               for( int i=0; i<oldmatrice.getRowDimension();i++)
                      for( int j=0; j<oldmatrice.getColumnDimension();j++)
                         newMatrice.set(i,j,oldmatrice.get(i, j));
                          
                         return newMatrice;
                   }
              
                            public void print3Dmatrice(double x[][][],String matricename){
           
           int [] valuex;
           valuex=lastvaluereturnxyz(x);
           
           System.out.println(matricename);
           
           for(int k=0; k<valuex[3]+1; k++){
           for(int i=0; i<valuex[1]+1; i++){
               for(int j=0; j<valuex[2]+1 ; j++){
                   System.out.print("\t\t\t"+x[i][j][k]);
               }  
               System.out.println();
           }
           System.out.println("Matrice State:  "+(k+1));
           }
           System.out.println("Matrice has written");
           
       }
                            
                                     public int[] lastvaluereturnxyz (double s[][][]){
           int lastvaluex=0;
           int lastvaluey=0;
           int lastvaluez=0;

           for(int i=0;i<s.length;i++){
               for(int j=0;j<s[0].length;j++){
                   for(int z=0;z<s[0][0].length;z++){
                   if(s[i][j][z]!=0){
                       lastvaluex=i;
                       lastvaluey=j;
                       lastvaluez=z;
                   }
                   }
               }
           }
           int [] dizi=new int[4];
           dizi[1]=lastvaluex;
           dizi[2]=lastvaluey;
           dizi[3]=lastvaluez;
           
           return dizi;
       }
                                     
       public  Matrix previousvalue (Matrix matrice){
           
           int lastrow = matrice.getRowDimension();
           int lastcolumn = matrice.getColumnDimension();
           
           Matrix newmatrice = new Matrix (lastrow,lastcolumn-1);
           
           for( int i=0;i<lastrow;i++)
               for( int j=0;j<lastcolumn-1;j++)
                   newmatrice.set(i, j, matrice.get(i, j));
           
           return newmatrice;
           
       } 
       
           public  Matrix previousvalue2 (Matrix matrice){
           
           int lastrow = matrice.getRowDimension();
           int lastcolumn = matrice.getColumnDimension();
           
           Matrix newmatrice = new Matrix (lastrow-1,lastcolumn);
           
           for( int i=0;i<lastrow-1;i++)
               for( int j=0;j<lastcolumn;j++)
                   newmatrice.set(i, j, matrice.get(i, j));
           
           return newmatrice;
           
       } 
    
                 public  double[][][] previousvalue3 (double [][][] matrice){
                     
                 double [][][] newmatrice = new double [matrice.length][matrice[0].length][matrice[0][0].length-1];   
                 
                     for(int i=0;i<matrice.length;i++){
               for(int j=0;j<matrice[0].length;j++){
                   for(int z=0;z<matrice[0][0].length-1;z++){
                           newmatrice[i][j][z]= matrice[i][j][z];
                   }
               }
           }
                 
                 
        
                return newmatrice ; 
       }    
           
           
}
