#include <dlfcn.h>
#include <fcntl.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>
#include "HelloWorldPublic.h"

uint ControllerPublic_1_0_1_0::version = 0x1000100;
uint ControllerPublic_1_0_2_0::version = 0x1000200;

int ControllerPublic_1_0_1_0::HelloWorldPublic_PublicMethod(public_client_helper_t *me)
{
    if (!me) return -1;
    if (!me->client->public_ops.PublicMethod) return -1;

    return me->client->public_ops.PublicMethod();
}
