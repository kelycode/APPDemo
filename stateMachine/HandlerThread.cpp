#include "HandlerThread.h"

HandlerThread::HandlerThread() : mShouldQuit(false) {}

HandlerThread::~HandlerThread() { quit(); }

sp<Looper> HandlerThread::getLooper()
{
    Mutex::Autolock autoLock(mLock);
    if (mLooper.get() == 0)
    {
        mLooperWait.wait(mLock);
    }
    return mLooper;
}

status_t HandlerThread::start(const char* name, int32_t priority, size_t stack) { return run(name, priority, stack); }

void HandlerThread::quit()
{
    if (!isRunning())
    {
        return;
    }
    sp<Looper> looper = getLooper();
    mLock.lock();
    mShouldQuit = true;
    mLock.unlock();
    looper->wake();
    requestExitAndWait();
}

bool HandlerThread::threadLoop()
{
    mLock.lock();
    mLooper = Looper::prepare(0);
    mLooperWait.broadcast();
    mLock.unlock();
    while (true)
    {
        do
        {
            Mutex::Autolock autoLock(mLock);
            if (mShouldQuit)
            {
                return false;
            }
        } while (false);
        mLooper->pollOnce(-1);
    }
    return false;
}
