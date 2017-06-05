/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dias;

import java.util.Random;

/**
 *
 * @author User
 * 
 * 
 *
 */
public class nss_missing_data {
    
    private double percentage;
    private int noise_signal=0;
    
    public nss_missing_data(double percentage){
        this.percentage=percentage ;
    }
    
       public int nss_missing_data_JF(){   ///Tested It is working//////////////////////
              Random rnd = new Random();
              double randomnumber = rnd.nextDouble();
              if(randomnumber<(percentage/100)){
                   noise_signal=0;
              }
              else{
                   noise_signal=1;
              }
              return noise_signal;
       }   
}
