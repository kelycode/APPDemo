LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES := CSM.cpp \
                   HandlerThread.cpp \
                   StateMachineTest.cpp

LOCAL_SHARED_LIBRARIES := liblog libbase libutils libandroid libbinder

LOCAL_MODULE := StateMachineTest

LOCAL_C_INCLUDES := $(LOCAL_PATH)

#LOCAL_CFLAGS := -Werror

LOCAL_CFLAGS_64 := -DTARGET_ARM64

LOCAL_MODULE_PATH :=  $(LOCAL_PATH)

include $(BUILD_EXECUTABLE)



