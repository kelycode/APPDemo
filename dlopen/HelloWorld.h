#include <log/log.h>
#include <stdio.h>
#include <mutex>

class HelloWorld
{
  public:
    virtual ~HelloWorld() { printf("kavin ***********析构函数***********\n"); };
    int HelloWorld_PublicMethod();
    int HelloWorld_PrivateMethod();
    int HelloWorld_Destory();

    static HelloWorld* getInstance();

  private:
    static HelloWorld* mInstance;
    HelloWorld() { printf("kavin ***********构造函数***********\n"); };
};
