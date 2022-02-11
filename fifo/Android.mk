LOCAL_PATH := $(call my-dir)


#########################################################
                    ##fifo test#
#########################################################
include $(CLEAR_VARS)

LOCAL_SRC_FILES := fifo_test.cpp

LOCAL_SHARED_LIBRARIES := liblog

LOCAL_MODULE := fifo_test

LOCAL_C_INCLUDES := $(LOCAL_PATH)

#LOCAL_CFLAGS := -Werror

LOCAL_CFLAGS_64 := -DTARGET_ARM64

LOCAL_MODULE_PATH :=  $(LOCAL_PATH)

include $(BUILD_EXECUTABLE)


#########################################################
                    ##fifo_client#
#########################################################
include $(CLEAR_VARS)

LOCAL_SRC_FILES := EpollEventDispatcher.cpp \
                   client.cpp

LOCAL_SHARED_LIBRARIES := liblog libbase

LOCAL_MODULE := fifo_client

LOCAL_C_INCLUDES := $(LOCAL_PATH)

#LOCAL_CFLAGS := -Werror

LOCAL_CFLAGS_64 := -DTARGET_ARM64

LOCAL_MODULE_PATH :=  $(LOCAL_PATH)

include $(BUILD_EXECUTABLE)

#########################################################
                    ##fifo_server#
#########################################################
include $(CLEAR_VARS)

LOCAL_SRC_FILES := server.cpp

LOCAL_SHARED_LIBRARIES := liblog libbase

LOCAL_MODULE := fifo_server

LOCAL_C_INCLUDES := $(LOCAL_PATH)

#LOCAL_CFLAGS := -Werror

LOCAL_CFLAGS_64 := -DTARGET_ARM64

LOCAL_MODULE_PATH :=  $(LOCAL_PATH)

include $(BUILD_EXECUTABLE)



