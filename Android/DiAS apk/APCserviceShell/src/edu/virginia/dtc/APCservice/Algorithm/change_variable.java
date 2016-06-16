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
public class change_variable {
    
    public static Matrix insulin_CPA = new Matrix (8,21); 
    public static Matrix sensor_error = new Matrix (1,21); 
    public static Matrix flag_constrains= new Matrix (1,21); 
    public static Matrix compensate_ins = new Matrix (1,21); 
    public static Matrix flag_ratio = new Matrix (1,21); 
    public static Matrix ins_CPA = new Matrix (8,21); 
    public static Matrix lamda_CPA = new Matrix (8,21);
    public static Matrix g_prediction_feedback = new Matrix (8,21);
    
}
