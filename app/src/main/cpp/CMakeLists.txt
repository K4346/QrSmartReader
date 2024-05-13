cmake_minimum_required(VERSION 3.22.1)

project("qrsmartreader")


set(OpenCV_DIR ${CMAKE_SOURCE_DIR}/opencv-mobile-4.9.0-android/sdk/native/jni)
find_package(OpenCV REQUIRED core imgproc)


set(ncnn_DIR ${CMAKE_SOURCE_DIR}/ncnn-20240410-android-vulkan/${ANDROID_ABI}/lib/cmake/ncnn)
find_package(ncnn REQUIRED)

add_library(qrsmartreader SHARED segment/yolov8ncnn.cpp segment/yolo.cpp segment/ndkcamera.cpp)

target_link_libraries(qrsmartreader ncnn ${OpenCV_LIBS} camera2ndk mediandk)

add_library(qrsmartreaderpose SHARED pose/yolov8ncnn.cpp pose/yolov8pose.cpp pose/ndkcamera.cpp)

target_link_libraries(qrsmartreaderpose ncnn ${OpenCV_LIBS} camera2ndk mediandk)


# Specifies libraries CMake should link to your target library. You
# can link libraries from various origins, such as libraries defined in this
# build script, prebuilt third-party libraries, or Android system libraries.
#target_link_libraries(${CMAKE_PROJECT_NAME}
#    # List libraries link to the target library
#    android
#    log)
