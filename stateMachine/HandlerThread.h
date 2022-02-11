#pragma once

#include <utils/Looper.h>
#include <utils/threads.h>
#include <android/log.h>
#include <utils/RefBase.h>

using namespace android;

class HandlerThread : public Thread
{
  public:
    HandlerThread();
    virtual ~HandlerThread();

    sp<Looper> getLooper();
    status_t start(const char* name = 0, int32_t priority = PRIORITY_DEFAULT, size_t stack = 0);
    void quit();

  private:
    bool threadLoop();

  private:
    sp<Looper> mLooper;
    mutable Mutex mLock;
    bool mShouldQuit;
    Condition mLooperWait;
};

