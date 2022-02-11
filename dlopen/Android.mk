LOCAL_PATH := $(call my-dir)

include $(LOCAL_PATH)/../../common.mk

########################################################
                    #libhelloworld#
########################################################
include $(CLEAR_VARS)

LOCAL_SRC_FILES := HelloWorld.cpp \
				   HelloWorldWrapper.cpp

LOCAL_MODULE_TAGS := optional

LOCAL_SHARED_LIBRARIES := liblog libbase $(common_libs)

LOCAL_MODULE := libhelloworld

LOCAL_C_INCLUDES := $(LOCAL_PATH)/

LOCAL_CFLAGS := $(common_flags)

LOCAL_CFLAGS_64 := -DTARGET_ARM64

include $(BUILD_SHARED_LIBRARY)

#########################################################
                    ##Test#
#########################################################
include $(CLEAR_VARS)

LOCAL_SRC_FILES := test/HelloWorldPublic.cpp  \
                   test/HelloWorldPrivate.cpp \
				   test/Test.cpp

LOCAL_SHARED_LIBRARIES := liblog libbase

LOCAL_MODULE := dlopen_test

LOCAL_C_INCLUDES := $(LOCAL_PATH)/test/

#LOCAL_CFLAGS := -Werror

LOCAL_CFLAGS_64 := -DTARGET_ARM64

#LOCAL_MODULE_PATH :=  $(LOCAL_PATH)

include $(BUILD_EXECUTABLE)
