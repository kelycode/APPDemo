#include "HelloWorld.h"

HelloWorld* HelloWorld::mInstance = nullptr;
static std::mutex mutex_;

HelloWorld* HelloWorld::getInstance()
{
    printf("kavin ***********HelloWorld::HelloWorld_getInstance***********\n");
    if (mInstance == nullptr)
    {
        std::lock_guard<std::mutex> guard(mutex_);
        if (mInstance == nullptr)
        {
            mInstance = new HelloWorld();
        }
    }
    return mInstance;
}

int HelloWorld::HelloWorld_PublicMethod()
{
    printf("kavin ***********HelloWorld::HelloWorld_PublicMethod***********\n");
    return 0;
}

int HelloWorld::HelloWorld_PrivateMethod()
{
    printf("kavin ***********HelloWorld::HelloWorld_PrivateMethod***********\n");
    return 0;
}

int HelloWorld::HelloWorld_Destory()
{
    printf("kavin ***********HelloWorld::HelloWorld_Destory***********\n");
    return 0;
}
