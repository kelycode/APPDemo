

#include <dlfcn.h>
#include <fcntl.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>
#include "HelloWorldPrivate.h"

uint ControllerPrivate_1_0_1_0::version = 0x1000100;
uint ControllerPrivate_1_0_2_0::version = 0x1000200;

//--------------------------------------------------- private interfaces -------------------------------------------------------//

int ControllerPrivate_1_0_1_0::HelloWorldPrivate_PrivateMethod(private_client_helper_t *me)
{
    if (!me) return -1; // Invalid Parameter
    if (!me->client->private_ops.PrivateMethod) return -1; // UnSupported
    return me->client->private_ops.PrivateMethod();
}
