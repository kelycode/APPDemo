#include "CSM.h"


void CSM::LogSmStateEvent(const char* state, uint32_t event)
{
    printf("CurState:%s: event:%d tid:%d\n", state, event, gettid());
}


void CSM::StateIdle::OnEnter() 
{
    printf("StateIdle::OnEnter\n");
}

void CSM::StateIdle::OnExit() 
{
    printf("StateIdle::OnExit\n");
}
bool CSM::StateIdle::ProcessEvent(uint32_t event, void* p_data)
{
    LogSmStateEvent("StateIdle", event);

    switch (event)
    {
        case kEventA:
        {
            TransitionTo(kStateTrackingStopped);
            break;
        }
        case kEventB:
        {
            break;
        }
        case kEventC:
        {
            break;
        }
        case kEventD:
        case kEventE:
        {
            break;
        }
        default:
        {
            printf("invliad csm event\n");
            break;
        }
    }

    return true;
}

void CSM::StateTrackingStopped::OnEnter()
{
    printf("StateTrackingStopped::OnEnter\n");
}
void CSM::StateTrackingStopped::OnExit() 
{
    printf("StateTrackingStopped::OnExit\n");
}
bool CSM::StateTrackingStopped::ProcessEvent(uint32_t event, void* p_data)
{
    LogSmStateEvent("StateTrackingStopped", event);
    switch (event)
    {
        case kEventA:
        {
            break;
        }
        case kEventB:
        {
            TransitionTo(kStateTrackingStarted);
            break;
        }
        case kEventC:
        {
            break;
        }
        case kEventD:
        case kEventE:
        {
            break;
        }
        default:
        {
            printf("invliad csm event\n");
            break;
        }
    }
    return true;
}

void CSM::StateTrackingStarted::OnEnter()
{
    printf("StateTrackingStarted::OnEnter\n");
}

void CSM::StateTrackingStarted::OnExit()
{
    printf("StateTrackingStarted::OnExit\n");
}
bool CSM::StateTrackingStarted::ProcessEvent(uint32_t event, void* p_data)
{
    LogSmStateEvent("StateTrackingStarted", event);
    switch (event)
    {
        case kEventA:
        {
            break;
        }
        case kEventB:
        {
            break;
        }
        case kEventC:
        {
            TransitionTo(kStateIdle);
            break;
        }
        case kEventD:
        case kEventE:
        {
            break;
        }
        default:
        {
            printf("invliad csm event\n");
            break;
        }
    }
    return true;
}

CSM::CSM()
{
    mStateIdle = new StateIdle(*this);
    mStateTrackingStopped = new StateTrackingStopped(*this);
    mStateTrackingStarted = new StateTrackingStarted(*this);
    AddState(mStateIdle);
    AddState(mStateTrackingStopped);
    AddState(mStateTrackingStarted);
    SetInitialState(mStateIdle);

    mHandlerThread = new HandlerThread();
    status_t r = mHandlerThread->start("StateMachine.Test");
    if (r != NO_ERROR)
    {
        printf("cannot start handler thread, error:%d\n", r);
        return;
    }
}

void CSM::injectEvent(CSMEvent event)
{
    printf("inject event:%d tid:%d\n", event, gettid());
    mHandlerThread->getLooper()->sendMessage(this, Message(event));
}

void CSM::handleMessage(const Message& message) { ProcessEvent(message.what, nullptr); }

void CSM::onFirstRef() { printf("onFirstRef\n"); }

CSM::~CSM()
{
    printf("~CSM\n");
    mHandlerThread->quit();
}

