cmake_minimum_required(VERSION 3.18.1)
project("mnn_native")

# 1. Load the pre-built Alibaba MNN libraries from your jniLibs folder
add_library(libMNN SHARED IMPORTED)
set_target_properties(libMNN PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libMNN.so)

add_library(libMNN_Express SHARED IMPORTED)
set_target_properties(libMNN_Express PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libMNN_Express.so)

add_library(libllm SHARED IMPORTED)
set_target_properties(libllm PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libllm.so)

# 2. Compile our custom C++ engine
add_library(mnn_engine_native SHARED mnn_engine_native.cpp)

# 3. Link everything together so the app doesn't crash
target_link_libraries(mnn_engine_native
    libMNN
    libMNN_Express
    libllm
    log
)
