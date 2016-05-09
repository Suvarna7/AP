/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import static java.lang.Math.abs;
import static java.lang.Math.atan;

/**
 *
 * @author User
 */
public class Angle_detection_JF {
    
    private double delta_x ;
    private double yy ;
    private double y1 ;
    private double y2 ;   
    private double angle1=0;
    private double angle2=0;
    private double angle=0;
    
    public Angle_detection_JF(double delta_x, double yy,double y1,double y2){
        this.delta_x=delta_x ;
        this.yy=yy ;
        this.y1=y1 ;
        this.y2=y2 ;
    }
    
    
    public double angle_detection_JF(){ 
        
     /*    ////////////////////////////////////////Angle_detection_JF_input/////////////////////////////////////////////////////////////////////////////////
         System.out.println("/////////////////////////////Angle_detection_JF_input////////////////////////////////////////////////////////////////////////");
         System.out.println(delta_x+"     delta_x");
         System.out.println(yy+"     yy");
         System.out.println(y1+"     y1");
         System.out.println(y2+"     y2");
         System.out.println("/////////////////////////////Angle_detection_JF_input////////////////////////////////////////////////////////////////////////");
         ////////////////////////////////////////Angle_detection_JF_input/////////////////////////////////////////////////////////////////////////////////*/
        
        
        angle1=atan(abs((y1-yy)/delta_x));
        angle2=atan(abs((y2-yy)/delta_x));  
        if ((y1-yy)*(y2-yy)>=0){
        angle=abs(angle1-angle2);
        }
        else{
        angle=angle2+angle1;
        }
        
         /*  ///////////////////////////////////////////////////Output/////////////////////////////////////////////////////////////////////////////////////////////
              System.out.println("////////////////////////////////Output Angle_detection_JF////////////////////////////////////////////////////////////////////");
              System.out.println(angle+"    angle");
              System.out.println("////////////////////////////////Output Angle_detection_JF////////////////////////////////////////////////////////////////////");
              ///////////////////////////////////////////////////Output//////////////////////////////////////////////////////////////////////////////////////////*/
        
        return angle;
       }
    
    
}
