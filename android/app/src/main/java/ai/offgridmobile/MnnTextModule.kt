package ai.offgridmobile // <-- CHANGE THIS TO MATCH YOUR APP

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.modules.core.DeviceEventManagerModule

class MnnTextModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String {
        return "MnnTextModule" // This is the name React Native will use to call it
    }

    // 1. Function to Load the Model
    @ReactMethod
    fun loadModel(modelDir: String, promise: Promise) {
        try {
            // We will connect this to the actual Alibaba C++ code in Phase 3.
            // For now, this acts as a placeholder so React Native doesn't crash.
            promise.resolve("MNN Engine Successfully Woken Up for: $modelDir")
        } catch (e: Exception) {
            promise.reject("MNN_LOAD_ERROR", e.message)
        }
    }

    // 2. Function to Generate Text
    @ReactMethod
    fun generateText(prompt: String, promise: Promise) {
        try {
            // Placeholder: This will eventually trigger the C++ MNN generation loop
            promise.resolve("This is a placeholder response from the MNN Android Brain!")
        } catch (e: Exception) {
            promise.reject("MNN_GENERATE_ERROR", e.message)
        }
    }

    // 3. Helper to send individual words/tokens back to the UI for streaming
    fun emitToken(token: String) {
        reactApplicationContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit("onMnnToken", token)
    }
}
