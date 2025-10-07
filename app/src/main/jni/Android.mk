LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := keys
LOCAL_SRC_FILES := keys.c

# ✅ Correct 16KB alignment flag
LOCAL_LDFLAGS += -Wl,-z,max-page-size=0x4000

include $(BUILD_SHARED_LIBRARY)