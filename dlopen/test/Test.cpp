#include <stdio.h>
#include "HelloWorldPublic.h"
#include "HelloWorldPrivate.h"

int main()
{
#ifndef PRIVATE_
    public_client_helper_t * publicClient = HelloWorldPublic_Create();

    printf("<%s> sleep 1s, call PublicMethod \n",__func__);
    sleep(1);
    ControllerPublic_1_0_1_0::HelloWorldPublic_PublicMethod(publicClient);

    printf("<%s> sleep 1s, over \n",__func__);
    sleep(1);
    HelloWorldPublic_Destroy(publicClient);
#else
    private_client_helper_t * privateClient = ControllerPrivateClient_Create();

    printf("<%s> sleep 1s, call PrivateMethod \n",__func__);
    
    sleep(1);
    ControllerPrivate_1_0_1_0::HelloWorldPrivate_PrivateMethod(privateClient);

    printf("<%s> sleep 1s, over \n",__func__);
    sleep(1);
    ControllerPrivateClient_Destroy(privateClient);
#endif
    return 0;
}