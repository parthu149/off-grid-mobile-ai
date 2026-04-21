#include <jni.h>
#include <string>
#include <memory>
#include <thread>
#include "llm/llm.hpp" // Alibaba MNN LLM Header

using namespace MNN::Transformer;

static std::shared_ptr<Llm> g_mnn_llm = nullptr;
static bool g_mnn_is_generating = false;

extern "C"
JNIEXPORT jboolean JNICALL
Java_ai_offgridmobile_MnnTextModule_initMnnEngine(JNIEnv *env, jobject thiz, jstring configPath) {
    const char *config_path = env->GetStringUTFChars(configPath, nullptr);
    
    // Create and load the MNN LLM
    g_mnn_llm.reset(Llm::createLLM(config_path));
    env->ReleaseStringUTFChars(configPath, config_path);

    if (g_mnn_llm != nullptr) {
        g_mnn_llm->load();
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

extern "C"
JNIEXPORT void JNICALL
Java_ai_offgridmobile_MnnTextModule_startGeneration(JNIEnv *env, jobject thiz, jstring prompt) {
    if (!g_mnn_llm) return;
    
    const char *prompt_cstr = env->GetStringUTFChars(prompt, nullptr);
    std::string prompt_str(prompt_cstr);
    env->ReleaseStringUTFChars(prompt, prompt_cstr);

    g_mnn_is_generating = true;
    
    // Setup streaming callback to send tokens back to Kotlin
    auto stream_callback = [env, thiz](const char* token) -> bool {
        if (!g_mnn_is_generating) return false; // Stop requested
        
        jclass clazz = env->GetObjectClass(thiz);
        jmethodID onTokenMethod = env->GetMethodID(clazz, "onToken", "(Ljava/lang/String;)V");
        jstring jToken = env->NewStringUTF(token);
        env->CallVoidMethod(thiz, onTokenMethod, jToken);
        env->DeleteLocalRef(jToken);
        
        return true; 
    };

    // Run the generation
    g_mnn_llm->response(prompt_str, stream_callback);
    g_mnn_is_generating = false;
}

extern "C"
JNIEXPORT void JNICALL
Java_ai_offgridmobile_MnnTextModule_stopEngineGeneration(JNIEnv *env, jobject thiz) {
    g_mnn_is_generating = false;
}
