#include <jni.h>

JNIEXPORT jint JNICALL

Java_inc_mesa_githubuser_adapters_Adapter_getHashFromJni(JNIEnv *env, jobject instance,
                                                         jstring url_) {
    const char *url = (*env)->GetStringUTFChars(env, url_, 0);
    {
        int total=0;

        while( url[total] != '\0')
            total++;
        (*env)->ReleaseStringUTFChars(env, url_, url);
        return total;
    }


}