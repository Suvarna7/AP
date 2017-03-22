#include <jni.h>
#include <string>
#include <time.h>

#include <cmath>

extern "C"
jstring
Java_com_example_zachariemaloney_c_1helloworld_MainActivity_stringTimeFromC(
        JNIEnv *env,
        jobject /* this */) {
    time_t rawtime;
    struct tm * timeinfo;
    time ( &rawtime );
    timeinfo = localtime ( &rawtime );
    std::string timeoutput = "Hello world! The current date and time here on the computer is : ";
    timeoutput += asctime(timeinfo);
    return env->NewStringUTF(timeoutput.c_str());
}

extern "C"
jstring
Java_com_example_zachariemaloney_c_1helloworld_MainActivity_stringNumberFromCMath(
    JNIEnv *env,
    jobject /*this*/) {
    double x;
    x = sin(2.);
    char output[50];
    snprintf(output, 50, "%f", x);
    std::string sinoutput = "sin(2.) is ";
    sinoutput += output;
    sinoutput += ".";
    return env->NewStringUTF(sinoutput.c_str());
};