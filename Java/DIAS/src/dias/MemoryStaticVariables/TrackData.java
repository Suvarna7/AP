/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias.MemoryStaticVariables;

import Jama.Matrix;

/**
 *
 * @author Mert
 */
public class TrackData {
    
    public  static Matrix I_track= new Matrix (14,1); 
    public  static Matrix data_mem=new Matrix (15,1);
    public  static Matrix I_u_constrain= new Matrix (15,1);
    public  static double I_error_rspeed;
    public  static double I_me_inst;
    
}
