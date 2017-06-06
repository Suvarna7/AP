#include <jni.h>
#include <string>

#include <testheader.h>

extern "C"
jstring
Java_com_example_zachariemaloney_c_1helloworld_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
