/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.virginia.dtc.APCservice.Algorithm;

import Jama.Matrix;

/**
 *
 * @author Mert
 */
public class controller_assessment_index_071215_JF {
    
    public Matrix umaxx_account;
    public double [][][] L_account;
    public Matrix rf_account;
    public Matrix wfcn_account;
    public int kj;
    public Matrix gs_f;
    public Matrix ins_f;
    public double B_w;
    public Matrix g_prediction_f;
    public static int b=1;
    
    
    public Matrix Itrack;
    public double I_meinst;
    public double I_error_rspeed;
    public Matrix I_uconstrain;
    public Matrix datamem;
    public int st;
    public int start_time;
    public double I_me;
    public double I_u_contraint_data;
    public double I_cost_function;
    
    public controller_assessment_index_071215_JF (Matrix umaxx_account,double [][][] L_account,Matrix rf_account,Matrix wfcn_account,int kj,Matrix gs_f, Matrix ins_f, double B_w, Matrix g_prediction_f){
        this.B_w=B_w;
        this.L_account=L_account;
        this.g_prediction_f=g_prediction_f;
        this.gs_f=gs_f;
        this.ins_f=ins_f;
        this.kj=kj;
        this.rf_account=rf_account;
        this.umaxx_account=umaxx_account;
        this.wfcn_account=wfcn_account;
    }
    
    public Matrix controller_assessment_index_071215_JF() throws Exception{
         trackdata t= new trackdata();
      /* /////////////////////////////////////////INPUTS//////////////////////////////////////////////////////////////////////////////////////////////////
        System.out.println("////////////////////////////////////Inputs controller_assessment_index_071215_JF////////////////////////////////////////////");
        System.out.println(B_w+"     B_w");
        printMatrix(umaxx_account,"umaxx_account");
        printMatrix(rf_account,"rf_account");
        printMatrix(wfcn_account,"wfcn_account");
        printMatrix(gs_f,"gs_f");
        printMatrix(g_prediction_f,"g_prediction_f");
        printMatrix(ins_f,"ins_f");
        System.out.println(kj+"     kj");
        print3Dmatrice(L_account,"L_account");
        printMatrix(t.I_track,"   t.Itrack");
        printMatrix(t.data_mem,"   t.data_mem");
        printMatrix(t.I_u_constrain,"   t.I_u_constrain");
        System.out.println(t.I_error_rspeed+"     t.I_error_rspeed");
        System.out.println(t.I_me_inst+"     t.I_me_inst");
        System.out.println("////////////////////////////////////Inputs controller_assessment_index_071215_JF////////////////////////////////////////////");
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/
        
        st=10;
        start_time=20;
        
        if(kj<=21){
        Itrack = new Matrix (14,1);
        I_meinst=0;
        I_error_rspeed=0;
        I_uconstrain= new Matrix (20,1);
        datamem= new Matrix (15,1);
        
        I_me=0;
        I_u_contraint_data=0;
        I_cost_function=0;
        
        
         
         t.I_track=Itrack;
         t.I_error_rspeed=I_error_rspeed;
         t.data_mem=datamem;
         t.I_me_inst=I_meinst;
         
           for(int i=0;i<t.I_u_constrain.getRowDimension();i++)
                for(int j=0;j<t.I_u_constrain.getColumnDimension();j++)
            t.I_u_constrain.set(i, j, I_uconstrain.get(i, j));
        
        }
        if(kj>21){
           
            
            I_uconstrain =new Matrix(t.I_u_constrain.getRowDimension()+1,t.I_u_constrain.getColumnDimension());
            I_error_rspeed=t.I_error_rspeed;
            datamem=t.data_mem;
            I_meinst=t.I_me_inst;
            
            for(int i=0;i<t.I_u_constrain.getRowDimension();i++)
                for(int j=0;j<t.I_u_constrain.getColumnDimension();j++)
            I_uconstrain.set(i, j, t.I_u_constrain.get(i, j));
            
            
            Itrack= t.I_track;
        }

        
        Matrix umaxx = new Matrix (umaxx_account.getRowDimension(),1);
        Matrix ref = new Matrix (rf_account.getRowDimension(),1);
        Matrix wfcn = new Matrix (wfcn_account.getRowDimension(),1);
        Matrix L = new Matrix (lastvaluereturnxyz(L_account)[0],lastvaluereturnxyz(L_account)[1]);
        Matrix y_prediction = new Matrix (g_prediction_f.getRowDimension(),g_prediction_f.getColumnDimension());
        Matrix y_predict = new Matrix (g_prediction_f.getRowDimension(),g_prediction_f.getColumnDimension());
        Matrix total_unit = new Matrix (wfcn.getRowDimension(),wfcn.getColumnDimension());
        Matrix IC = new Matrix (total_unit.getRowDimension(),total_unit.getColumnDimension());
        Matrix y_real = new Matrix (gs_f.getRowDimension(),gs_f.getColumnDimension());
        Matrix I_yprediction_inst = new Matrix (y_predict.getRowDimension(),1);
        Matrix delta_u = new Matrix (y_predict.getRowDimension(),1);
        Matrix I_u = new Matrix (delta_u.getRowDimension(),delta_u.getColumnDimension());
        /////////////////////////////////////  %% process //////////////////////////////////////////////////////////////////////////////////////////
        
        if(kj>start_time){
  
            ref=getoneline(rf_account,rf_account.getColumnDimension()-1);
            umaxx=getoneline(umaxx_account,umaxx_account.getColumnDimension()-1);
            wfcn=getoneline(wfcn_account,wfcn_account.getColumnDimension()-1);
            L=ThreeDimensiontoMatrix(L_account,lastvaluereturnxyz(L_account)[3]);

       ////////////////////////////////////////////////// %% objective function V=(Y-w)'*(Y-w)+((du'*diag(IC)*du))///////////////////////////////////////
       double u= ins_f.get(0, kj-1);

       y_prediction=g_prediction_f;

       y_predict= (L.times(getoneline(ins_f,kj))).plus(getoneline(g_prediction_f,kj));
     
       double wt_y=1;
       total_unit=wfcn.times(B_w);
 
       for(int i=0;i<total_unit.getRowDimension();i++)
            for(int j=0;j<total_unit.getColumnDimension();j++)
              IC.set(i, j,1800/total_unit.get(i, j));
       
          y_real=gs_f;

          Matrix temparray = new Matrix(ins_f.getRowDimension()+1,1);
          
          temparray.set(0,0,ins_f.get(0,kj-1));
          
          for(int i=0;i<ins_f.getRowDimension();i++)
          temparray.set(i+1,0,ins_f.get(i,kj));

          for(int i=0;i<ins_f.getRowDimension()-1;i++)
          delta_u.set(i,0,temparray.get(i+1, 0)-temparray.get(i, 0));
 
      //////////////////////////////////////////////// %% recusive opretor ///////////////////////////////////////////////////////////////////////////
       
       for(int i=0;i<y_predict.getRowDimension();i++){
            for(int j=0;j<y_predict.getColumnDimension();j++){
             I_yprediction_inst.set(i,0,Math.pow((y_predict.minus(ref)).get(i, 0),2));
            }
       }

       I_u=((delta_u.transpose()).times(diag(IC,0))).times(delta_u);
   

       if(sum(I_u)<0.0001){
          I_cost_function=10000000;
       }
       else{
           I_cost_function=sum(I_yprediction_inst)/sum(I_u);
       }
       
       if(Math.abs(ins_f.get(0, kj)-umaxx.get(0, 0))<0.0001){
           I_uconstrain.set(t.I_u_constrain.getRowDimension(), 0,1);
       }
       else{
            I_uconstrain.set(t.I_u_constrain.getRowDimension(), 0,0);
       }


        I_meinst=(y_prediction.get(0, kj-1)-y_real.get(0,kj-1));
        I_me=Math.sqrt(I_meinst*I_meinst); 

        
        if (I_me>10)
        I_error_rspeed=st+I_error_rspeed;
        else 
        I_error_rspeed=0;
   
        if (kj<start_time+5)
        I_u_contraint_data=0;
        else
        I_u_contraint_data=I_uconstrain.get(t.I_u_constrain.getRowDimension()-2,0)+I_uconstrain.get(t.I_u_constrain.getRowDimension()-1,0)+I_uconstrain.get(t.I_u_constrain.getRowDimension(),0);  
        
        for(int i=0;i<I_yprediction_inst.getRowDimension();i++){
        Itrack.set(i, 0, I_yprediction_inst.get(i, 0));
        }
        
        Itrack.set(I_yprediction_inst.getRowDimension(), 0, I_u.get(0, 0));
        Itrack.set(I_yprediction_inst.getRowDimension()+1, 0, I_me);
        Itrack.set(I_yprediction_inst.getRowDimension()+2, 0, I_u_contraint_data);
        Itrack.set(I_yprediction_inst.getRowDimension()+3, 0, I_error_rspeed);
        Itrack.set(I_yprediction_inst.getRowDimension()+4, 0, I_cost_function);
        Itrack.set(I_yprediction_inst.getRowDimension()+5, 0, umaxx.get(0, 0));

        
          for(int i=I_yprediction_inst.getRowDimension()+6;i<Itrack.getRowDimension();i++){
          Itrack.set(i, 0, 0);
          }

        
        
        for(int i=0;i<Itrack.getRowDimension();i++)
        t.data_mem.set(i, b-1, Itrack.get(i,0));
        
 
        t.I_track=Itrack;
        
          t.I_u_constrain =new Matrix(t.I_u_constrain.getRowDimension()+1,t.I_u_constrain.getColumnDimension());
        
         for(int i=0;i<t.I_u_constrain.getRowDimension();i++)
                for(int j=0;j<t.I_u_constrain.getColumnDimension();j++)
            t.I_u_constrain.set(i, j, I_uconstrain.get(i, j));
        
        t.I_error_rspeed=I_error_rspeed;
        t.I_me_inst=I_meinst;

        Error_display_JF edJF = new Error_display_JF(kj,gs_f);
        edJF.error_display_JF();
       
        
        prevdata_error_summation pdes= new prevdata_error_summation();

        
        t.I_track=Itrack;
        
           for(int i=0;i<t.I_u_constrain.getRowDimension();i++)
           for(int j=0;j<t.I_u_constrain.getColumnDimension();j++)
           t.I_u_constrain.set(i, j, I_uconstrain.get(i, j));
        
        t.I_error_rspeed=I_error_rspeed;
        t.I_me_inst=I_meinst;  
        
      
        
        for(int i=0;i<Itrack.getRowDimension();i++)
        t.data_mem.set(i, b-1, Itrack.get(i,0));
        
        b++;
        
        t.data_mem=createnewMatrix(15,b, t.data_mem);
        
        }
        
 
        /* //////////////////////////////////////////////////OUTPUT/////////////////////////////////////////////////////////////////////////////////////
        System.out.println("//////////////////////////////Output Controller_assesment_index///////////////////////////////////////////////////////");
        printMatrix(t.I_track,"   t.Itrack");
        printMatrix(t.data_mem,"   t.data_mem");
        printMatrix(t.I_u_constrain,"   t.I_u_constrain");
        System.out.println(t.I_error_rspeed+"     t.I_error_rspeed");
        System.out.println(t.I_me_inst+"     t.I_me_inst");
        System.out.println("//////////////////////////////Output Controller_assesment_index///////////////////////////////////////////////////////");
        /////////////////////////////////////////////////OUTPUT///////////////////////////////////////////////////////////////////////////////////////*/
 
       
        return Itrack;
    }
    
    
      public Matrix getoneline (Matrix a, int column){
  
            Matrix result= new Matrix (a.getRowDimension(),1);
    
            for(int i=0;i<a.getRowDimension();i++)
                result.set(i,0,a.get(i, column));
       
        return result;
    }
      
      
      public Matrix ThreeDimensiontoMatrix (double a[][][] , int z){
        Matrix result = new Matrix (lastvaluereturnxyz(a)[1]+1,lastvaluereturnxyz(a)[2]+1);
        
        for(int i=0;i<lastvaluereturnxyz(a)[1]+1;i++)
            for(int j=0;j<lastvaluereturnxyz(a)[2]+1;j++)
                result.set(i, j, a[i][j][z]);
                
        
        return result;
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
           int [] array=new int[4];
           array[1]=lastvaluex;
           array[2]=lastvaluey;
           array[3]=lastvaluez;
           
           return array;
       }
    
       public Matrix diag (Matrix a, int b){
        Matrix matrice = new Matrix (a.getRowDimension(),a.getRowDimension());
        
        for(int j=0;j<a.getRowDimension();j++){
           for(int i=0;i<a.getRowDimension();i++){
                if(i==j){
                    matrice.set(i,j,a.get(i,b));
                }
                else{
                    matrice.set(j,i,0);
                }
            }
        }
        return matrice;
    }
       
       public double sum (Matrix a){
           
           double sum=0;
           
           for(int i=0;i<a.getRowDimension();i++)
                for(int j=0;j<a.getColumnDimension();j++)
           sum=a.get(i, j)+sum;
           
           return sum;
           
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
                    
                                 public Matrix createnewMatrix (int newdimensionx,int newdimensiony, Matrix oldmatrice){
               Matrix newMatrice = new Matrix (newdimensionx,newdimensiony);
               
               for( int i=0; i<oldmatrice.getRowDimension();i++)
                      for( int j=0; j<oldmatrice.getColumnDimension();j++)
                         newMatrice.set(i,j,oldmatrice.get(i, j));
                          
                         return newMatrice;
                   }
    
}
