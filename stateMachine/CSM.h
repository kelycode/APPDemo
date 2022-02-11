#pragma once
#include "HandlerThread.h"
#include "StateMachine.h"

typedef enum CSMState_
{
    kStateIdle,
    kStateTrackingStopped,
    kStateTrackingStarted,
} CSMState;

typedef enum CSMEvent_
{
    kEventA,
    kEventB,
    kEventC,
    kEventD,
    kEventE,
} CSMEvent;

class CSM : public StateMachine, public MessageHandler
{
  public:
    class StateIdle : public State
    {
      public:
        StateIdle(CSM& sm) : State(sm, kStateIdle){}
        void OnEnter() override;
        void OnExit() override;
        bool ProcessEvent(uint32_t event, void* p_data) override;
    };

    class StateTrackingStopped : public State
    {
      public:
        StateTrackingStopped(CSM& sm) : State(sm, kStateTrackingStopped){}
        void OnEnter() override;
        void OnExit() override;
        bool ProcessEvent(uint32_t event, void* p_data) override;
    };

    class StateTrackingStarted : public State
    {
      public:
        StateTrackingStarted(CSM& sm) : State(sm, kStateTrackingStarted){}
        void OnEnter() override;
        void OnExit() override;
        bool ProcessEvent(uint32_t event, void* p_data) override;
    };

    void injectEvent(CSMEvent event);

    CSM();
    ~CSM();

  private:
    static void LogSmStateEvent(const char* state, uint32_t event);

    virtual void onFirstRef();
    virtual void handleMessage(const Message& message);

    StateIdle* mStateIdle;
    StateTrackingStopped* mStateTrackingStopped;
    StateTrackingStarted* mStateTrackingStarted;

    sp<HandlerThread> mHandlerThread;
};

