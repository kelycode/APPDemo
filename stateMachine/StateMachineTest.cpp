#include "CSM.h"
#include <binder/IPCThreadState.h>

int main()
{
    sp<android::ProcessState> proc(android::ProcessState::self());

    sp<CSM> mStateMachine = new CSM();
    mStateMachine->Start();

    printf("sleep 2s state idle ---> state stop\n");
    sleep(2);
    mStateMachine->injectEvent(kEventA);

    printf("sleep 2s state stop ---> state start\n");
    sleep(2);
    mStateMachine->injectEvent(kEventB);

    printf("sleep 2s state start ---> state idle\n");
    sleep(2);
    mStateMachine->injectEvent(kEventC);

    printf("sleep 2s state idle ---> state stop\n");
    sleep(2);
    mStateMachine->injectEvent(kEventA);

    android::ProcessState::self()->startThreadPool();
    android::IPCThreadState::self()->joinThreadPool();
    return 0;
}

