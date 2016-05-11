/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import Jama.Matrix;
import java.io.IOException;
import static java.lang.Double.isNaN;
import java.util.Random;
import dias.MemoryStaticVariables.m20150711_load_global_variables;
/**
 *
 * @author Mert
 */
public class CGM__SEDFR_JF {
   
    public Matrix CGM;
    public Matrix bolus_insulin;
    public Matrix basal_insulin;
    public int flag_noise;

    public Matrix ins_every5;
    public Matrix ins;

    public Matrix type_recording;
    public Matrix score_returning;
    public Matrix gb;
    public Matrix gb_real;
    public Matrix g_kal;
    public Matrix g_pls;
    public Matrix error_detection;
    public Matrix error_detectino_real;
    public Matrix recording_w;
    public Matrix gb_angle_ret;
    public Matrix sum_wyxT;
    public Matrix sum_wxxT;
    public Matrix sum_xxp;
    public Matrix sum_xpxp;
    public Matrix sum_wyx;
    public Matrix sum_xx;
    public Matrix sum_xxpp;
    public Matrix Q;
    public Matrix x_e;
    public Matrix A;
    public Matrix C;
    public Matrix gb_kal = new Matrix (1,1);
    public Matrix inss= new Matrix (1,1);
    public Matrix noisel;
    public Matrix addsignal;
    public Matrix sum_score;
    public Matrix sum_type;
    public Matrix RR= new Matrix (1,1);
    public Matrix gb_kal_ret=new Matrix (1,1);
    public Matrix gb_with_NaN =new Matrix (1,1);

    public int start_noise;
    public int noise_interval;
    public int length_add;
    public int am;
    public double sigma_a;
    public int phi_max;
    public int ii;
    public int delta_x;
    public int st;
    public int type;
    public int nn;
    public int cc;
    public double lamda;
    public double a_w0;
    public double b_w0;
    public double w;
    public double alpha;
    public int d2;
    public Matrix sum_wyy;
    public double R;
    public int kj;
    public Matrix x_r;
    public Matrix ssP = new Matrix (1,1);
    
    public double y1;
    public double y2;
    public double yy;
    
    
    double R_pls;
    double N_pls;
    double M_pls;
    double L_pls;
    double phi_pls;
    double I_pls;
    double J_pls;
    double sigma1;
    double sigma2;
    double alpha_pls;
    double mean_gb;
    double std_gb;
    double mean_ins;
    double std_ins;
    double mean_CGM_change;
    double std_CGM_change;
    int prediction_step_pls;
    public Matrix y= new Matrix (1,1);
    
    public Matrix angle = new Matrix (1,1); 
    
    public Matrix sigma = new Matrix (1,1);
    public Matrix gb_plss = new Matrix(1,1);
    public Matrix temp_gb_plss = new Matrix(1,1);
    public Matrix CGM_change_pls = new Matrix(1,1);
    public Matrix x_sample= new Matrix (1,1);
    public Matrix x_sampletemp = new Matrix (1,1);
    public Matrix cluster = new Matrix (1,1);
    public Matrix yp = new Matrix (1,1);
    public Matrix y_p = new Matrix (1,1);
    
    int a;
    int b;
    int c;
    int d;
    
    public double CGM_retuning;
    
    public double [][][] AA;
    public double [][][] CC;
    public double [][][] QQ;
    
     public double CGM() throws IOException{
         
         bolus_insulin=bolus_insulin.transpose();
         
         m20150711_load_global_variables ldv = new m20150711_load_global_variables ();
         
        kj=ldv.kj;
          
        ins_every5 = new Matrix (1,basal_insulin.getColumnDimension()); 
         
        
        for(int i=0;i<basal_insulin.getColumnDimension();i++)
        ins_every5.set(0,i,bolus_insulin.times(12).get(0, i)+basal_insulin.get(0,i));
        
   
        a=matricelocation(CGM)[0];
        b=matricelocation(CGM)[1];
        
        c=matricelocation(ins_every5)[0];
        d=matricelocation(ins_every5)[1];

        
        sum_score=new Matrix (1,1);
        sum_type=new Matrix (1,1);
        
         if(b>1)
            CGM=CGM.transpose();
        
        
        if(c>1)
        ins_every5=ins_every5.transpose();
        
        ins=ins_every5;
        
        
           if(b<22+21){
            start_noise=25;
            noise_interval=24;
            length_add=3;
            am=30;
            type_recording= new Matrix(6,3) ;
            score_returning=new Matrix (1,1);
            sigma_a=0.6;
            phi_max=25;
            ii=0;
            delta_x=15;
            st=1;
            gb= new Matrix (a,b);
            gb_real= new Matrix (b,a);
            gb=CGM;
            gb_real=CGM.transpose();
            type=0;
            g_kal=new Matrix (1,50);
            g_pls=new Matrix (1,1);
            error_detection=new Matrix(1,4);
            error_detectino_real= new Matrix (1,1);
            recording_w=ones(8,1);
            gb_angle_ret= new Matrix (b,a);
            gb_angle_ret=CGM.transpose();
            nn=2;
            cc=1;
            lamda=0.3;
            a_w0=1;
            b_w0=a_w0;
            w=1.5;
            alpha=Math.pow(10, nn);
            d2=4;
            R=10;
            Q= new Matrix (d2,d2);
            Q=eye(d2).times(alpha);
            x_e= new Matrix (d2,60);
            
            for (int kj=0;kj<21;kj++)
                for (int j=0;j<d2;j++)
                x_e.set(j,kj,100);
            
            A= new Matrix (d2,d2);
            A=eye(d2);
            C= new Matrix (1,d2);
            C.set(0,0,1);
            
            sum_wxxT=new Matrix (1,1);
            sum_xxp=new Matrix (1,1);
            sum_xpxp=new Matrix (1,1);
            sum_wyy=new Matrix (1,1);
            sum_xx= new Matrix (d2,1);
            sum_xxpp= new Matrix (d2,d2);
            sum_wyxT= new Matrix (1,d2);
            sum_wyx = new Matrix (d2,1);
            
             R_pls=12;                       //   %number of latent variables
             N_pls=1600;                      //  %number of samples
             M_pls=12;                       // %number of input variables/2
             L_pls=1;                        // %number of output variables/2
             phi_pls=0.5;
             I_pls=30;
             J_pls=30;
             sigma1=0.01;
             sigma2=0.01;
             alpha_pls=0.1;
             gb_kal.set(0,0,gb.get(0,0));
             
            plsdata_730_R_12_withcluster_16_data plsR12wcluster = new plsdata_730_R_12_withcluster_16_data();
            
            mean_gb=plsR12wcluster.gb_m;
            std_gb=plsR12wcluster.gb_sd;
            mean_ins=plsR12wcluster.ins_m;
            std_ins=plsR12wcluster.ins_sd;
            mean_CGM_change=plsR12wcluster.gbchange_m;
            std_CGM_change=plsR12wcluster.gb_change_sd;
            
            Zmu_other_JF zmu4= new Zmu_other_JF(mean_ins,std_ins,ins);
            inss=createnewMatrix(kj+1,1,inss);
            inss=zmu4.zmu_other_JF();
            prediction_step_pls=0;
           // noisel= new Matrix (start_noise*2-1,1);   //????
            noisel= new Matrix (864,1);
            
            if(flag_noise==1){
               temp_SEDFR_noise tnoise =new temp_SEDFR_noise();
               tnoise.a=a;
               tnoise.b=b;
               tnoise.c=c;
               tnoise.d=d;
               tnoise.sum_score=sum_score;
               tnoise.sum_type=sum_type;
               tnoise.ins_every5=ins_every5;
               tnoise.start_noise=start_noise;
               tnoise.noise_interval=noise_interval;
               tnoise.lenght_add=length_add;
               tnoise.am=am;
               tnoise.type_recording=type_recording;
               tnoise.score_returning=score_returning;
               tnoise.sigma_a=sigma_a;
               tnoise.phi_max=phi_max;
               tnoise.ii=ii;
               tnoise.delta_x=delta_x;
               tnoise.st=st;
               tnoise.gb=gb;
               tnoise.gb_real=gb_real;
               tnoise.type=type;
               tnoise.g_kal=g_kal;
               tnoise.g_pls=g_pls;
               tnoise.error_detection=error_detection;
               tnoise.error_detectino_real=error_detectino_real;
               tnoise.recording_w=recording_w;
               tnoise.gb_angle_ret=gb_angle_ret;
               tnoise.nn=nn;
               tnoise.cc=cc;
               tnoise.lamda=lamda;
               tnoise.a_w0=a_w0;
               tnoise.b_w0=b_w0;
               tnoise.w=w;
               tnoise.alpha=alpha;
               tnoise.d2=d2;
               tnoise.sum_wyxT=sum_wyxT;
               tnoise.sum_wxxT=sum_wxxT;
               tnoise.sum_xxp=sum_xxp;
               tnoise.sum_xpxp=sum_xpxp;
               tnoise.sum_wyy=sum_wyy;
               tnoise.sum_wyx=sum_wyx;
               tnoise.sum_xx=sum_xx;
               tnoise.sum_xxpp=sum_xxpp;
               tnoise.R=R;
               tnoise.Q=Q;
               tnoise.kj=kj;
               tnoise.x_e=x_e;
               tnoise.A=A;
               tnoise.C=C;
               tnoise.R_pls=R_pls;
               tnoise.N_pls=N_pls;
               tnoise.M_pls=M_pls;
               tnoise.L_pls=L_pls;
               tnoise.phi_pls=phi_pls;
               tnoise.I_pls=I_pls;
               tnoise.J_pls=J_pls;
               tnoise.sigma1=sigma1;
               tnoise.sigma2=sigma2;
               tnoise.alpha_pls=alpha_pls;
               tnoise.gb=gb;
               tnoise.gb_kal=gb_kal;
               tnoise.mean_gb=mean_gb;
               tnoise.std_gb=std_gb;
               tnoise.mean_ins=mean_ins;
               tnoise.std_ins=std_ins;
               tnoise.mean_CGM_change=mean_CGM_change;
               tnoise.std_CGM_change=std_CGM_change;
               tnoise.inss=inss;
               tnoise.prediction_step_pls=prediction_step_pls;
               tnoise.noisel=noisel;
               tnoise.flag_noise=flag_noise;
            }
            else{
              temp_SEDFR_nonoise tnonoise =new temp_SEDFR_nonoise();  
               tnonoise.a=a;
               tnonoise.b=b;
               tnonoise.c=c;
               tnonoise.d=d;
               tnonoise.sum_score=sum_score;
               tnonoise.sum_type=sum_type;
               tnonoise.ins_every5=ins_every5;
               tnonoise.start_noise=start_noise;
               tnonoise.noise_interval=noise_interval;
               tnonoise.lenght_add=length_add;
               tnonoise.am=am;
               tnonoise.type_recording=type_recording;
               tnonoise.score_returning=score_returning;
               tnonoise.sigma_a=sigma_a;
               tnonoise.phi_max=phi_max;
               tnonoise.ii=ii;
               tnonoise.delta_x=delta_x;
               tnonoise.st=st;
               tnonoise.gb=gb;
               tnonoise.gb_real=gb_real;
               tnonoise.type=type;
               tnonoise.g_kal=g_kal;
               tnonoise.g_pls=g_pls;
               tnonoise.error_detection=error_detection;
               tnonoise.error_detectino_real=error_detectino_real;
               tnonoise.recording_w=recording_w;
               tnonoise.gb_angle_ret=gb_angle_ret;
               tnonoise.nn=nn;
               tnonoise.cc=cc;
               tnonoise.lamda=lamda;
               tnonoise.a_w0=a_w0;
               tnonoise.b_w0=b_w0;
               tnonoise.w=w;
               tnonoise.alpha=alpha;
               tnonoise.d2=d2;
               tnonoise.sum_wyxT=sum_wyxT;
               tnonoise.sum_wxxT=sum_wxxT;
               tnonoise.sum_xxp=sum_xxp;
               tnonoise.sum_xpxp=sum_xpxp;
               tnonoise.sum_wyy=sum_wyy;
               tnonoise.sum_wyx=sum_wyx;
               tnonoise.sum_xx=sum_xx;
               tnonoise.sum_xxpp=sum_xxpp;
               tnonoise.R=R;
               tnonoise.Q=Q;
               tnonoise.kj=kj;
               tnonoise.x_e=x_e;
               tnonoise.A=A;
               tnonoise.C=C;
               tnonoise.R_pls=R_pls;
               tnonoise.N_pls=N_pls;
               tnonoise.M_pls=M_pls;
               tnonoise.L_pls=L_pls;
               tnonoise.phi_pls=phi_pls;
               tnonoise.I_pls=I_pls;
               tnonoise.J_pls=J_pls;
               tnonoise.sigma1=sigma1;
               tnonoise.sigma2=sigma2;
               tnonoise.alpha_pls=alpha_pls;
               tnonoise.gb=gb;
               tnonoise.gb_kal=gb_kal;
               tnonoise.mean_gb=mean_gb;
               tnonoise.std_gb=std_gb;
               tnonoise.mean_ins=mean_ins;
               tnonoise.std_ins=std_ins;
               tnonoise.mean_CGM_change=mean_CGM_change;
               tnonoise.std_CGM_change=std_CGM_change;
               tnonoise.inss=inss;
               tnonoise.prediction_step_pls=prediction_step_pls;
               tnonoise.noisel=noisel;
               tnonoise.flag_noise=flag_noise;
            } 
        }
        else{
             if(flag_noise==1){ 
               temp_SEDFR_noise tnoise =new temp_SEDFR_noise();
               a=tnoise.a;
               b=tnoise.b;
               c=tnoise.c;
               d=tnoise.d;
               sum_score=tnoise.sum_score;
               sum_type=tnoise.sum_type;
               ins_every5=tnoise.ins_every5;
               start_noise=tnoise.start_noise;
               noise_interval=tnoise.noise_interval;
               length_add=tnoise.lenght_add;
               am=tnoise.am;
               type_recording=tnoise.type_recording;
               score_returning=tnoise.score_returning;
               sigma_a=tnoise.sigma_a;
               phi_max=tnoise.phi_max;
               ii=tnoise.ii;
               delta_x=tnoise.delta_x;
               st=tnoise.st;
               gb=tnoise.gb;
               gb_real=tnoise.gb_real;
               type=tnoise.type;
               g_kal=tnoise.g_kal;
               g_pls=tnoise.g_pls;
               error_detection=tnoise.error_detection;
               error_detectino_real=tnoise.error_detectino_real;
               recording_w=tnoise.recording_w;
               gb_angle_ret=tnoise.gb_angle_ret;
               nn=tnoise.nn;
               cc=tnoise.cc;
               lamda=tnoise.lamda;
               a_w0=tnoise.a_w0;
               b_w0=tnoise.b_w0;
               w=tnoise.w;
               alpha=tnoise.alpha;
               d2=tnoise.d2;
               sum_wyxT=tnoise.sum_wyxT;
               sum_wxxT=tnoise.sum_wxxT;
               sum_xxp=tnoise.sum_xxp;
               sum_xpxp=tnoise.sum_xpxp;
               sum_wyy=tnoise.sum_wyy;
               sum_wyx=tnoise.sum_wyx;
               sum_xx=tnoise.sum_xx;
               sum_xxpp= tnoise.sum_xxpp;
               R=tnoise.R;
               Q=tnoise.Q;
               kj=tnoise.kj;
               x_e=tnoise.x_e;
               A=tnoise.A;
               C=tnoise.C;
               R_pls=tnoise.R_pls;
               N_pls=tnoise.N_pls;
               M_pls=tnoise.M_pls;
               L_pls=tnoise.L_pls;
               phi_pls=tnoise.phi_pls;
               I_pls=tnoise.I_pls;
               J_pls=tnoise.J_pls;
               sigma1=tnoise.sigma1;
               sigma2=tnoise.sigma2;
               alpha_pls=tnoise.alpha_pls;
               gb=tnoise.gb;
               gb_kal=tnoise.gb_kal;
               mean_gb=tnoise.mean_gb;
               std_gb=tnoise.std_gb;
               mean_ins=tnoise.mean_ins;
               std_ins=tnoise.std_ins;
               mean_CGM_change=tnoise.mean_CGM_change;
               std_CGM_change=tnoise.std_CGM_change;
               inss=tnoise.inss;
               prediction_step_pls=tnoise.prediction_step_pls;
               noisel=tnoise.noisel;
               flag_noise=tnoise.flag_noise;
            }
            else{
               temp_SEDFR_nonoise tnoise =new temp_SEDFR_nonoise();
               a=tnoise.a;
               b=tnoise.b;
               c=tnoise.c;
               d=tnoise.d;
               sum_score=tnoise.sum_score;
               sum_type=tnoise.sum_type;
               ins_every5=tnoise.ins_every5;
               start_noise=tnoise.start_noise;
               noise_interval=tnoise.noise_interval;
               length_add=tnoise.lenght_add;
               am=tnoise.am;
               type_recording=tnoise.type_recording;
               score_returning=tnoise.score_returning;
               sigma_a=tnoise.sigma_a;
               phi_max=tnoise.phi_max;
               ii=tnoise.ii;
               delta_x=tnoise.delta_x;
               st=tnoise.st;
               gb=tnoise.gb;
               gb_real=tnoise.gb_real;
               type=tnoise.type;
               g_kal=tnoise.g_kal;
               g_pls=tnoise.g_pls;
               error_detection=tnoise.error_detection;
               error_detectino_real=tnoise.error_detectino_real;
               recording_w=tnoise.recording_w;
               gb_angle_ret=tnoise.gb_angle_ret;
               nn=tnoise.nn;
               cc=tnoise.cc;
               lamda=tnoise.lamda;
               a_w0=tnoise.a_w0;
               b_w0=tnoise.b_w0;
               w=tnoise.w;
               alpha=tnoise.alpha;
               d2=tnoise.d2;
               sum_wyxT=tnoise.sum_wyxT;
               sum_wxxT=tnoise.sum_wxxT;
               sum_xxp=tnoise.sum_xxp;
               sum_xpxp=tnoise.sum_xpxp;
               sum_wyy=tnoise.sum_wyy;
               sum_wyx=tnoise.sum_wyx;
               sum_xx=tnoise.sum_xx;
               sum_xxpp= tnoise.sum_xxpp;
               R=tnoise.R;
               Q=tnoise.Q;
               kj=tnoise.kj;
               x_e=tnoise.x_e;
               A=tnoise.A;
               C=tnoise.C;
               R_pls=tnoise.R_pls;
               N_pls=tnoise.N_pls;
               M_pls=tnoise.M_pls;
               L_pls=tnoise.L_pls;
               phi_pls=tnoise.phi_pls;
               I_pls=tnoise.I_pls;
               J_pls=tnoise.J_pls;
               sigma1=tnoise.sigma1;
               sigma2=tnoise.sigma2;
               alpha_pls=tnoise.alpha_pls;
               gb=tnoise.gb;
               gb_kal=tnoise.gb_kal;
               mean_gb=tnoise.mean_gb;
               std_gb=tnoise.std_gb;
               mean_ins=tnoise.mean_ins;
               std_ins=tnoise.std_ins;
               mean_CGM_change=tnoise.mean_CGM_change;
               std_CGM_change=tnoise.std_CGM_change;
               inss=tnoise.inss;
               prediction_step_pls=tnoise.prediction_step_pls;
               noisel=tnoise.noisel;
               flag_noise=tnoise.flag_noise;
            } 
             
             
             gb=CGM;
             gb_real=CGM.transpose();
             
             Zmu_other_JF zmu= new Zmu_other_JF(mean_ins,std_ins,ins);
             Matrix inss= new Matrix(ins.getColumnDimension(),ins.getRowDimension());
             inss=zmu.zmu_other_JF().transpose();
             kj=matricelocation(gb)[0];
             
             
               if (kj-matricelocation(gb_angle_ret)[1]>1){
                  for(int i=matricelocation(gb_angle_ret)[1]; i<kj;i++)
                      gb_angle_ret.set(0, i, gb.get(i,0));
              }
             
             
              if (kj>start_noise && mod(kj,noise_interval)==0 && flag_noise==1){
                   noisel.set(kj,0,length_add);
                    
                   if (type<6)
                        type=1+type;
                    else
                        type=1;
                   
                   addsignal= new Matrix(0,length_add);
                   
                   Addon_noise_JF adnJF= new Addon_noise_JF(type,am,length_add,gb,kj);
                   addsignal=adnJF.addon_noise_JF();
                   
                   gb.set(kj,0,gb.get(kj,0)+addsignal.get(0,0));
               }
               else if(noisel.get(kj-1,0)>1){
                  gb.set(kj,0,gb.get(kj,0)+addsignal.get(5-(int)noisel.get(kj-1,0),0));
                  noisel.set(kj,0,noisel.get(kj-1,0)-1); 
                  
                  if (type==6)
                  gb.set(kj,0,gb.get(kj-1,0));
               
               }
               else if (noisel.get(kj-1,0)==1){
                  noisel.set(kj,0,0);
               }
               else if (flag_noise==1){         
                    Random rand = new Random();
                    gb.set(kj,0,gb.get(kj,0)+2*(rand.nextDouble()-0.5));
                    noisel.set(kj,0,0);
               }
               else{
                  noisel.set(kj,0,0); 
               }
             
               
              if(kj>25){
                    if (Double.isNaN(gb.get(kj,0))){
                         y.set(0,0,2*gb_angle_ret.get(kj-1,0)-gb_angle_ret.get(kj-2,0));
                    }
                    else{
                        y.set(0,0,gb.get(kj, 0));
                    }
               }
               else{
                   y.set(0,0,gb.get(kj, 0));
               }  
           
              
                
               gb_with_NaN=createnewMatrix(kj+1,1,gb_with_NaN);
               gb_kal_ret=createnewMatrix(kj+1,1,gb_kal_ret);
               gb_kal=createnewMatrix(kj+1,1,gb_kal);
               RR=createnewMatrix(kj+1,1,RR);
                
                AA= new double [A.getColumnDimension()][A.getRowDimension()][kj+1];
                CC= new double [C.getColumnDimension()][C.getColumnDimension()][kj+1];
                QQ= new double [Q.getColumnDimension()][Q.getRowDimension()][kj+1];
              
                gb_with_NaN.set(kj,0,y.get(0, 0));
                gb_kal.set(kj,0,gb.get(kj,0));
                gb_kal_ret.set(kj,0,gb.get(kj,0));
                
                RR.set(kj,0,R);
                 
                 for(int i=0;i<A.getColumnDimension();i++)
                     for(int j=0;j<A.getRowDimension();j++)
                         AA[i][j][kj]=A.get(i,j);
                           
                for(int i=0;i<C.getRowDimension();i++)
                     for(int j=0;j<C.getColumnDimension();j++)
                         CC[i][j][kj]=C.get(i,j);
                 
                for(int i=0;i<Q.getColumnDimension();i++)
                     for(int j=0;j<Q.getRowDimension();j++)
                         QQ[i][j][kj]=Q.get(i,j);
                
                
                if (kj>21){
                    x_r = new Matrix (1,d2);
                    
                    
                   for(int i=kj-d2+1;i<kj+1;i++)
                       x_r.set(0, i-(kj-d2+1), gb_with_NaN.get(i, 0));
                  
                    
                   recording_w=createnewMatrix(kj+1,1,recording_w);
                   ssP=createnewMatrix(1,kj+1,ssP);
                   gb_kal=createnewMatrix(kj+1,1,gb_kal);
                   
                    m2014_3_19_kf_JF mkfJF = new m2014_3_19_kf_JF (lamda,x_r,x_e,y,lastvaluereturnx(x_e)[1],sum_wyxT,sum_wxxT,sum_xxp,sum_xpxp,sum_wyy,sum_wyx,sum_xx,sum_xxpp,Q,R,A,C,a_w0,b_w0);
                    mkfJF.kf_JF();
                   
                    recording_w.set(kj,0,mkfJF.w);
                    ssP.set(0,kj,mkfJF.Sp);
                    gb_kal.set(kj,0,mkfJF.y_p.get(0,0));
                }
                   g_pls=createnewMatrix(1,kj+1,g_pls);
                   error_detection=createnewMatrix(kj+1,4,error_detection);
                
                if(kj>22){
                    gb_angle_ret=createnewMatrix(1,kj+1,gb_angle_ret); 
                    g_pls.set(0,kj,gb_kal.get(kj,0)*std_gb+mean_gb);
                    gb=createnewMatrix(kj+1,1,gb); 
                      
                     if( isNaN(gb.get(kj, 0)) || gb.get(kj,0)==gb.get(kj-1,0) ){
                   error_detection.set(kj,0,1);
                   error_detection.set(kj,1,1);
                   error_detection.set(kj,2,1);
                   error_detection.set(kj,3,1);
              }
                     else{
                         sigma=createnewMatrix(1,kj+1,sigma);
                          
                         sigma.set(0,kj,sigma_a);
                         
                         if(Math.abs(gb_kal.get(kj,0)-gb.get(kj,0))>12*sigma.get(0,kj))
                         error_detection.set(kj,0,1);
                         else
                         error_detection.set(kj,0,0);   
                         
                         if(Math.abs(g_pls.get(0,kj)-gb.get(kj,0))>12*sigma.get(0,kj))
                         error_detection.set(kj,1,1);
                         else
                         error_detection.set(kj,1,0);  
                         
                         error_detection.set(kj,2,0); 
                         
                         if(Math.abs(gb.get(kj,0)-gb.get(kj-1,0))>25)
                         error_detection.set(kj,3,1);
                         else
                         error_detection.set(kj,3,0); 
                     }
                    
               y1=gb_kal.get(kj,0);
               y2=2*gb_angle_ret.get(0,kj-1)-gb_angle_ret.get(0,kj-2);
               yy=gb_angle_ret.get(0,kj-1);
               
               angle=createnewMatrix(kj+1,4,angle);
               
               Angle_detection_JF adJF = new Angle_detection_JF (delta_x,yy,y1,y2);
               angle.set(kj,0,adJF.angle_detection_JF()/3.14*180);
               y1=g_pls.get(0,kj);
               adJF = new Angle_detection_JF (delta_x,yy,y1,y2);
               angle.set(kj,1,adJF.angle_detection_JF()/3.14*180);
               y1=gb_with_NaN.get(kj,0);
               adJF = new Angle_detection_JF (delta_x,yy,y1,y2);
               angle.set(kj,2,adJF.angle_detection_JF()/3.14*180);
               y1=(gb_kal.get(kj,0)+g_pls.get(0,kj))/2;
                adJF = new Angle_detection_JF (delta_x,yy,y1,y2);
               angle.set(kj,3,adJF.angle_detection_JF()/3.14*180);
               
               if(isNaN(gb.get(kj, 0))){
                   
                   if(min3constant(angle.get(kj,0),angle.get(kj,1),angle.get(kj,3))==angle.get(kj,1)){
                       gb_angle_ret.set(0,kj,g_pls.get(0,kj));
                   }
                   else if(min3constant(angle.get(kj,0),angle.get(kj,1),angle.get(kj,3))==angle.get(kj,0)){
                       gb_angle_ret.set(0,kj,gb_kal.get(kj,0));
                   }
                   else{
                       gb_angle_ret.set(0, kj,(gb_kal.get(kj,0)+g_pls.get(0,kj))/2 );
                   }
               }
               else{
                   if(min4constant(angle.get(kj,0),angle.get(kj,1),angle.get(kj,2),angle.get(kj,3))==angle.get(kj,1) && (error_detection.get(kj, 0)+error_detection.get(kj, 1)>1) ){
                       gb_angle_ret.set(0,kj,g_pls.get(0,kj));
                   }
                   else if(min4constant(angle.get(kj,0),angle.get(kj,1),angle.get(kj,2),angle.get(kj,3))==angle.get(kj,0) && (error_detection.get(kj, 0)+error_detection.get(kj, 1)>1) ){
                        gb_angle_ret.set(0,kj,gb_kal.get(kj,0));
                   }
                   else if(min4constant(angle.get(kj,0),angle.get(kj,1),angle.get(kj,2),angle.get(kj,3))==angle.get(kj,3) && (error_detection.get(kj, 0)+error_detection.get(kj, 1)>1) ){
                         gb_angle_ret.set(0, kj,(gb_kal.get(kj,0)+g_pls.get(0,kj))/2 );
                   }
                   else{
                       gb_angle_ret.set(0,kj,gb_with_NaN.get(kj,0));
                   }
               
                   int sum=0;
                   int sum2=0;
                   
                   for(int i=0;i<9;i++)
                       if(gb_angle_ret.get(0,kj-i)!=gb_with_NaN.get(kj-i,0))
                           sum++;
                   
                    for(int i=8;i<2;i++)
                       if(gb_angle_ret.get(0,kj-i)!=gb_with_NaN.get(kj-i,0))
                           sum2++;
                   
                   if(angle.get(kj,2)<phi_max || sum>4 || sum2>0 && gb_angle_ret.get(kj-1,0)==gb_with_NaN.get(kj-1,0) ){
                       gb_angle_ret.set(0,kj,gb_with_NaN.get(kj,0));
                   }
                           
               }
               
               error_detectino_real=createnewMatrix(1,kj+1,error_detectino_real);
               
               if(gb_angle_ret.get(0,kj)!=gb_with_NaN.get(kj,0)  && isNaN(gb.get(kj,0)))
                   error_detectino_real.set(0,kj,1);
               else
                   error_detectino_real.set(0,kj,0);
        
                }
                else{
                    
                     error_detectino_real=createnewMatrix(1,kj+1,error_detectino_real);
                     gb_angle_ret=createnewMatrix(1,kj+1,gb_angle_ret);
                    
                     error_detectino_real.set(0,kj,0);
                     gb_angle_ret.set(0,kj,gb_with_NaN.get(kj,0));
                }

                
            if (kj>M_pls){
            double stepp=kj-M_pls;
           
             int frameSize = 6;
		int displaced =  (frameSize-1)/2;
		int degree = 0;
               
                double[] input = new double[gb_angle_ret.getColumnDimension()];
                
                for(int i=0;i<gb_angle_ret.getColumnDimension();i++)
                    input[i]= gb_angle_ret.get(0, i);

           
                SavitzkyGolayFilterImpl sgfi= new SavitzkyGolayFilterImpl(frameSize, degree);
          
                double [] gInitResult = sgfi.filter(input);
		double [] finalResult = gInitResult;
                
                if (degree == 0)
			for (int i = 0; i <displaced; i ++){
				finalResult[i] =  gInitResult[displaced];
				finalResult[finalResult.length - (1+i)]= gInitResult[finalResult.length - (displaced+1)];
			}
                
                gb_plss=createnewMatrix(kj+1,1,gb_plss);
                CGM_change_pls=createnewMatrix(kj+1,1,CGM_change_pls);
                temp_gb_plss=createnewMatrix(kj+1,1,temp_gb_plss);
                
                for(int i=0;i<finalResult.length;i++)
                gb_plss.set(i, 0, finalResult[i]);

                 
                 for(int i=1;i<finalResult.length;i++)
                 temp_gb_plss.set(i, 0, (gb_plss.get(i, 0)-gb_plss.get(i-1, 0)));
                 
                 CGM_change_pls.set(0,0,0);
                 for(int i=0;i<temp_gb_plss.getRowDimension();i++)
                 CGM_change_pls.set(i,0,temp_gb_plss.get(i, 0));   
  
                     
                zmu= new  Zmu_other_JF(mean_gb,std_gb,gb_plss.transpose());  
                gb_plss=zmu.zmu_other_JF();
                  
              
                Zmu_other_JF zmu1= new  Zmu_other_JF(mean_CGM_change,std_CGM_change,CGM_change_pls.transpose());  
                CGM_change_pls=zmu1.zmu_other_JF();

                x_sample=createnewMatrix((int) M_pls*3+1,kj+1,x_sample);
    
               
               for(int i=(int)(kj-M_pls);i<kj;i++)
               x_sample.set(i-(int)(kj-M_pls), kj, gb_plss.get(0,i+1));
               
               for(int i=(int)(kj-M_pls-1);i<kj-1;i++)
               x_sample.set(i-(int)(kj-M_pls-1-(kj-(kj-M_pls))), kj, inss.get(i+1,0));
               
               for(int i=(int)(kj-M_pls);i<kj;i++)
               x_sample.set(i-(int)(kj-M_pls-(kj-(kj-M_pls-1-(kj-(kj-M_pls))))), kj,CGM_change_pls.get(0,i+1));
                 
               x_sampletemp=createnewMatrix((int) M_pls,kj+1,x_sampletemp);
                 
               for(int i=0;i<M_pls;i++)
               x_sampletemp.set(i,0,x_sample.get(i,kj));

            
           cluster1 clJF = new cluster1(x_sampletemp.transpose());
           clJF.cluster1_JF();
           
           double X_cross;
           double Y_cross;
         
           
           cluster=createnewMatrix(1,kj+1,cluster);
           yp=createnewMatrix(1,kj+1,yp);
           
           X_cross=clJF.X;
           Y_cross=clJF.Y;
               
           cluster.set(0,kj,clJF.T);
       
            y_p=createnewMatrix(1,kj+1,y_p);

        //    online_sim_3_6_JF onlinesJF =new online_sim_3_6_JF(x_sample,(int) R_pls,(int) M_pls,(int) phi_pls,(int) I_pls,(int) J_pls, (int) alpha_pls,sigma1,sigma2,cluster.get(0,kj));
            
       /*     load_plsdata730_R_12_withcluster lpls = new load_plsdata730_R_12_withcluster ();
     
            lpls.loadplsdata();
     
            plsdata_730_R_12_withcluster_16_data pls730R = new plsdata_730_R_12_withcluster_16_data();*/
            
        //   y_p.set(0,kj,onlinesJF.onlinesim_3_6_JF()); 
           
        }
    score_returning=createnewMatrix(lastvaluereturnx(score_returning)[0]+1,4,score_returning); 
                  
            double sum=0;   
            double sum2=0; 
            double sum3=0; 
                 if(kj>22){
                     if (noisel.get(kj-1,0)==1){
                         
                         for(int i=kj-length_add+1;i<kj;i++)
                         sum=error_detectino_real.get(0,i)+sum;
                         
                         if(sum>0){
                             
                              for(int i=kj-length_add+1;i<kj;i++)
                                  if(Math.abs(gb_angle_ret.get(0,i)-gb_real.get(0,i))<Math.abs(gb.get(0,i)-gb_with_NaN.get(0,i)))
                              sum2++;
                              
                              for(int i=kj-length_add+1;i<kj;i++)
                                  if(Math.abs(gb_angle_ret.get(0,i)-gb_real.get(0,i))<10)
                              sum3++;
                             
                             
                             if(sum2>0 || (type==5 && sum3==length_add)){
                         type_recording.set(type, 0, type_recording.get(type, 0)+1);
                         score_returning.set(lastvaluereturnx(score_returning)[0],0,1);
                         score_returning.set(lastvaluereturnx(score_returning)[0],1,0);
                         score_returning.set(lastvaluereturnx(score_returning)[0],2,0);
                         score_returning.set(lastvaluereturnx(score_returning)[0],3,0);
                             }
                             else{
                         type_recording.set(type, 1, type_recording.get(type, 1)+1);
                         score_returning.set(lastvaluereturnx(score_returning)[0],0,0);
                         score_returning.set(lastvaluereturnx(score_returning)[0],1,1);
                         score_returning.set(lastvaluereturnx(score_returning)[0],2,0);
                         score_returning.set(lastvaluereturnx(score_returning)[0],3,0);
                             }
                             
                         }
                         else{
                             type_recording.set(type, 3, type_recording.get(type, 3)+1);
                         score_returning.set(lastvaluereturnx(score_returning)[0],0,0);
                         score_returning.set(lastvaluereturnx(score_returning)[0],1,0);
                         score_returning.set(lastvaluereturnx(score_returning)[0],2,1);
                         score_returning.set(lastvaluereturnx(score_returning)[0],3,0);
                         }
                         
                     }
                     else if(error_detectino_real.get(0,kj)==1 && error_detectino_real.get(0,kj-1)==0 && noisel.get(0,kj)==0){
                         score_returning.set(lastvaluereturnx(score_returning)[0],0,0);
                         score_returning.set(lastvaluereturnx(score_returning)[0],1,0);
                         score_returning.set(lastvaluereturnx(score_returning)[0],2,0);
                         score_returning.set(lastvaluereturnx(score_returning)[0],3,1);
                     }
                 }
           }
           
          if (flag_noise==1){
              temp_SEDFR_noise tnoise =new temp_SEDFR_noise();
               tnoise.a=a;
               tnoise.b=b;
               tnoise.c=c;
               tnoise.d=d;
               tnoise.sum_score=sum_score;
               tnoise.sum_type=sum_type;
               tnoise.ins_every5=ins_every5;
               tnoise.start_noise=start_noise;
               tnoise.noise_interval=noise_interval;
               tnoise.lenght_add=length_add;
               tnoise.am=am;
               tnoise.type_recording=type_recording;
               tnoise.score_returning=score_returning;
               tnoise.sigma_a=sigma_a;
               tnoise.phi_max=phi_max;
               tnoise.ii=ii;
               tnoise.delta_x=delta_x;
               tnoise.st=st;
               tnoise.gb=gb;
               tnoise.gb_real=gb_real;
               tnoise.type=type;
               tnoise.g_kal=g_kal;
               tnoise.g_pls=g_pls;
               tnoise.error_detection=error_detection;
               tnoise.error_detectino_real=error_detectino_real;
               tnoise.recording_w=recording_w;
               tnoise.gb_angle_ret=gb_angle_ret;
               tnoise.nn=nn;
               tnoise.cc=cc;
               tnoise.lamda=lamda;
               tnoise.a_w0=a_w0;
               tnoise.b_w0=b_w0;
               tnoise.w=w;
               tnoise.alpha=alpha;
               tnoise.d2=d2;
               tnoise.sum_wyxT=sum_wyxT;
               tnoise.sum_wxxT=sum_wxxT;
               tnoise.sum_xxp=sum_xxp;
               tnoise.sum_xpxp=sum_xpxp;
               tnoise.sum_wyy=sum_wyy;
               tnoise.sum_wyx=sum_wyx;
               tnoise.sum_xx=sum_xx;
               tnoise.sum_xxpp=sum_xxpp;
               tnoise.R=R;
               tnoise.Q=Q;
               tnoise.kj=kj;
               tnoise.x_e=x_e;
               tnoise.A=A;
               tnoise.C=C;
               tnoise.R_pls=R_pls;
               tnoise.N_pls=N_pls;
               tnoise.M_pls=M_pls;
               tnoise.L_pls=L_pls;
               tnoise.phi_pls=phi_pls;
               tnoise.I_pls=I_pls;
               tnoise.J_pls=J_pls;
               tnoise.sigma1=sigma1;
               tnoise.sigma2=sigma2;
               tnoise.alpha_pls=alpha_pls;
               tnoise.gb=gb;
               tnoise.gb_kal=gb_kal;
               tnoise.mean_gb=mean_gb;
               tnoise.std_gb=std_gb;
               tnoise.mean_ins=mean_ins;
               tnoise.std_ins=std_ins;
               tnoise.mean_CGM_change=mean_CGM_change;
               tnoise.std_CGM_change=std_CGM_change;
               tnoise.inss=inss;
               tnoise.prediction_step_pls=prediction_step_pls;
               tnoise.noisel=noisel;
               tnoise.flag_noise=flag_noise;
          }
          else{
               temp_SEDFR_nonoise tnonoise =new temp_SEDFR_nonoise();  
               tnonoise.a=a;
               tnonoise.b=b;
               tnonoise.c=c;
               tnonoise.d=d;
               tnonoise.sum_score=sum_score;
               tnonoise.sum_type=sum_type;
               tnonoise.ins_every5=ins_every5;
               tnonoise.start_noise=start_noise;
               tnonoise.noise_interval=noise_interval;
               tnonoise.lenght_add=length_add;
               tnonoise.am=am;
               tnonoise.type_recording=type_recording;
               tnonoise.score_returning=score_returning;
               tnonoise.sigma_a=sigma_a;
               tnonoise.phi_max=phi_max;
               tnonoise.ii=ii;
               tnonoise.delta_x=delta_x;
               tnonoise.st=st;
               tnonoise.gb=gb;
               tnonoise.gb_real=gb_real;
               tnonoise.type=type;
               tnonoise.g_kal=g_kal;
               tnonoise.g_pls=g_pls;
               tnonoise.error_detection=error_detection;
               tnonoise.error_detectino_real=error_detectino_real;
               tnonoise.recording_w=recording_w;
               tnonoise.gb_angle_ret=gb_angle_ret;
               tnonoise.nn=nn;
               tnonoise.cc=cc;
               tnonoise.lamda=lamda;
               tnonoise.a_w0=a_w0;
               tnonoise.b_w0=b_w0;
               tnonoise.w=w;
               tnonoise.alpha=alpha;
               tnonoise.d2=d2;
               tnonoise.sum_wyxT=sum_wyxT;
               tnonoise.sum_wxxT=sum_wxxT;
               tnonoise.sum_xxp=sum_xxp;
               tnonoise.sum_xpxp=sum_xpxp;
               tnonoise.sum_wyy=sum_wyy;
               tnonoise.sum_wyx=sum_wyx;
               tnonoise.sum_xx=sum_xx;
               tnonoise.sum_xxpp=sum_xxpp;
               tnonoise.R=R;
               tnonoise.Q=Q;
               tnonoise.kj=kj;
               tnonoise.x_e=x_e;
               tnonoise.A=A;
               tnonoise.C=C;
               tnonoise.R_pls=R_pls;
               tnonoise.N_pls=N_pls;
               tnonoise.M_pls=M_pls;
               tnonoise.L_pls=L_pls;
               tnonoise.phi_pls=phi_pls;
               tnonoise.I_pls=I_pls;
               tnonoise.J_pls=J_pls;
               tnonoise.sigma1=sigma1;
               tnonoise.sigma2=sigma2;
               tnonoise.alpha_pls=alpha_pls;
               tnonoise.gb=gb;
               tnonoise.gb_kal=gb_kal;
               tnonoise.mean_gb=mean_gb;
               tnonoise.std_gb=std_gb;
               tnonoise.mean_ins=mean_ins;
               tnonoise.std_ins=std_ins;
               tnonoise.mean_CGM_change=mean_CGM_change;
               tnonoise.std_CGM_change=std_CGM_change;
               tnonoise.inss=inss;
               tnonoise.prediction_step_pls=prediction_step_pls;
               tnonoise.noisel=noisel;
               tnonoise.flag_noise=flag_noise;
          }
          
          if(gb_angle_ret.getColumnDimension()>gb_angle_ret.getRowDimension())
              gb_angle_ret=gb_angle_ret.transpose();
 
          if ((lastvaluereturnx(gb_angle_ret)[1]+1)>0)
         CGM_retuning=gb_angle_ret.get(kj-1,0);
         else
         CGM_retuning=88;
          
          
          return CGM_retuning;
     }
    
     public CGM__SEDFR_JF(){
    
    }
     
     
     public CGM__SEDFR_JF(Matrix CGM,Matrix bolus_insulin,Matrix basal_insulin,int flag_noise){
        this.basal_insulin=basal_insulin;
        this.bolus_insulin=bolus_insulin;
        this.CGM=CGM;
        this.flag_noise=flag_noise;
    }
     
     
       public double min3constant (double constant1,double constant2, double constant3){
           double temp=0;
           if(constant1<constant2){  
               if(constant1<constant2){
                   temp=constant1;
               }
               else{
                   temp=constant2;
               }
           }
           else{
                 if(constant2<constant3){
                   temp=constant2;
               }
               else{
                   temp=constant3;
               }
           }
           return temp;
       }
       
       public double min4constant (double constant1,double constant2, double constant3,double constant4){
           double temp=0;
           if(constant1<constant2){  
               if(constant1<constant2){
                   temp=constant1;
               }
               else{
                   temp=constant2;
               }
           }
           else{
                 if(constant2<constant3){
                   temp=constant2;
               }
               else{
                   temp=constant3;
               }
           }
           
           if(temp>constant4)
           temp=constant4;
           
           return temp;
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
     
   
       public static int[] matricelocation (Matrix s){
           int lastvaluex=0;
           int lastvaluey=0;
     
           for(int i=0;i<s.getRowDimension();i++){
               for(int j=0;j<s.getColumnDimension();j++){
                   if(s.get(i, j)!=0){
                       lastvaluex=i;
                       lastvaluey=j;
                   }
               }
           }
           int [] resultlocation=new int[2];
           resultlocation[0]=lastvaluex;
           resultlocation[1]=lastvaluey;
           
           return resultlocation;
       }
      
           public Matrix ones (int column, int row){
           Matrix matrice= new Matrix(column,row) ;
           
           for(int i=0;i<matrice.getRowDimension();i++)
                  for(int j=0;j<matrice.getColumnDimension();j++)
                            matrice.set(i, j, 1);
           
                      return matrice;
       }
           
           public Matrix createnewMatrix (int newdimensionx,int newdimensiony, Matrix oldmatrice){
               Matrix newMatrice = new Matrix (newdimensionx,newdimensiony);
               
               for( int i=0; i<oldmatrice.getRowDimension();i++)
                      for( int j=0; j<oldmatrice.getColumnDimension();j++)
                         newMatrice.set(i,j,oldmatrice.get(i, j));
                          
                         return newMatrice;
                   }
           
           public Matrix ones2 (int column, int row, int matricedimesionx, int matricedimesiony){
           Matrix matrice= new Matrix(matricedimesionx,matricedimesiony) ;
           
           for(int i=0;i<column;i++)
                  for(int j=0;j<row;j++)
                            matrice.set(i, j, 1);
           
                      return matrice;
       }
      
                public Matrix eye (int number){
           Matrix matrice= new Matrix(number,number) ;
           
           for(int i=0;i<matrice.getRowDimension();i++)
                  for(int j=0;j<matrice.getColumnDimension();j++)
                      if(i!=j)      
                      matrice.set(i, j, 0);
                      else
                      matrice.set(i, j, 1);  
           
                      return matrice;
       }
                
                 public int mod (int x, int y){
           int result;
           while((x-y)>0){
               x=x-y;   
           }
           result=x; 
           return result;
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
                 
                   public static int[] lastvaluereturnx (Matrix s){
           int lastvaluex=0;
           int lastvaluey=0;
     
           for(int i=0;i<s.getRowDimension();i++){
               for(int j=0;j<s.getColumnDimension();j++){
                   if(s.get(i, j)!=0){
                       lastvaluex=i;
                       lastvaluey=j;
                   }
               }
           }
           int [] resultlocation=new int[2];
           resultlocation[0]=lastvaluex;
           resultlocation[1]=lastvaluey;
           
           return resultlocation;
       }
                   
    public static void clearScreen() {  
    System.out.print("\033[H\033[2J");  
    System.out.flush();  
   }  
    
}
