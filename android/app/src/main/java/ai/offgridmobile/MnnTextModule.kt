package ai.offgridmobile

import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule

class MnnTextModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    init {
        // This wakes up the C++ file we are about to create
        System.loadLibrary("mnn_engine_native") 
    }

    override fun getName(): String {
        return "MnnTextModule"
    }

    // These link directly to the C++ engine
    private external fun initMnnEngine(configPath: String): Boolean
    private external fun startGeneration(prompt: String)
    private external fun stopEngineGeneration()

    @ReactMethod
    fun loadModel(modelDir: String, promise: Promise) {
        // The TS bridge sends the folder, but the C++ engine needs the config.json path
        val configPath = "$modelDir/llm_config.json"
        
        if (initMnnEngine(configPath)) {
            promise.resolve(true)
        } else {
            promise.reject("MNN_ERROR", "Failed to load MNN model in C++")
        }
    }

    @ReactMethod
    fun generateText(prompt: String, promise: Promise) {
        Thread {
            startGeneration(prompt)
            promise.resolve(null)
        }.start()
    }

    @ReactMethod
    fun stopGeneration() {
        stopEngineGeneration()
    }

    // The C++ code will trigger this function to send words back to the UI
    fun onToken(token: String) {
        reactApplicationContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit("onMnnToken", token)
    }
}
