package com.empatica.sample;

/**
 * Created by Cat on 5/18/2017.
 * Class holding data constant values, to be change accordingly with the device
 */
public class DataConstants {

    //************ EMPATICA **************//
    //1 second = 64 + 32 + 4 + 4 + 1 = 105 samples
    //Sending up to 40 minutes
    public static int ONE_SECOND_DATA = (64+32+4+4+1);

    //1 second = 64 + 32 + 4 + 4 + 1 = 105 samples
    //Sending up to 10 minutes
    public static int MAX_READ_SAMPLES_SYNCHRONIZE = 6*60* ONE_SECOND_DATA;

    //Max number of samples that can be stored in a HashMap
    public static int MAX_MEMORY_SAMPLES = 10*60* ONE_SECOND_DATA;

    //Sending timer constants:
    public static final int SENDING_PERIOD = 4*60*1000; //1 min
    public static final int SENDING_AMOUNT = 2*ONE_SECOND_DATA;
    public static int MAX_READ_SAMPLES_UPDATE = 15*60*ONE_SECOND_DATA;
    public static final int DELETING_MARGIN = 10*60*(64+32+4+4+1); // 10 min
    public static final int DEL_AMOUNT = 10*1000;
}
