LOCAL_PATH := $(call my-dir)

#########################################################
                    ##ionmap test#
#########################################################
include $(CLEAR_VARS)

LOCAL_SRC_FILES := ipcsocket.c \
                   ionutils.c \
                   ionmap_test.c

LOCAL_SHARED_LIBRARIES := liblog

LOCAL_MODULE := ionmap_test

LOCAL_C_INCLUDES := $(LOCAL_PATH)

#LOCAL_CFLAGS := -Werror

LOCAL_CFLAGS_64 := -DTARGET_ARM64

LOCAL_MODULE_PATH :=  $(LOCAL_PATH)

include $(BUILD_EXECUTABLE)

#########################################################
                    ##ionmap test#
#########################################################
include $(CLEAR_VARS)

LOCAL_SRC_FILES := ipcsocket.c \
                   ionutils.c \
                   ionapp_import.c

LOCAL_SHARED_LIBRARIES := liblog

LOCAL_MODULE := ionapp_import

LOCAL_C_INCLUDES := $(LOCAL_PATH)

#LOCAL_CFLAGS := -Werror

LOCAL_CFLAGS_64 := -DTARGET_ARM64

LOCAL_MODULE_PATH :=  $(LOCAL_PATH)

include $(BUILD_EXECUTABLE)

#########################################################
                    ##ionmap test#
#########################################################
include $(CLEAR_VARS)

LOCAL_SRC_FILES := ipcsocket.c \
                   ionutils.c \
                   ionapp_export.c

LOCAL_SHARED_LIBRARIES := liblog

LOCAL_MODULE := ionapp_export

LOCAL_C_INCLUDES := $(LOCAL_PATH)

#LOCAL_CFLAGS := -Werror

LOCAL_CFLAGS_64 := -DTARGET_ARM64

LOCAL_MODULE_PATH :=  $(LOCAL_PATH)

include $(BUILD_EXECUTABLE)


