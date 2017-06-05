//
// Created by Caterina on 2/10/2017.
//

#include "mock_alg.h"


#include <stdio.h>

// Required for the default JNI implementation


jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    return JNI_VERSION_1_6;
}
JNIEXPORT double JNICALL  Java_com_example_caterina_ctestandplay_MainActivity_simpleAlg(JNIEnv *env, jobject o, int cgm ) {
     if (cgm > 250) {
        //High Glucose level
        return 3.5;
    }else if (cgm > 150) {
        return 2;
    }else if (cgm > 120) {
        return 1;
    }else if (cgm > 50) {
        return -1;
    }else{
        return 1234;
    }


}