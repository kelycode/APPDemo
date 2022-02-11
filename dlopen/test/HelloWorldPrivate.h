#pragma once

#include <dlfcn.h>
#include <fcntl.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>
#include <log/log.h>

#define CONTROLLER_SERVICE_CLIENT_LIB "libhelloworld.so" /*!< library */

typedef void *private_client_handle_t;

typedef struct private_client_ops
{
    private_client_handle_t (*Create)();
    void (*Destroy)(private_client_handle_t client);
    int (*PrivateMethod)();
} private_client_ops_t;

typedef struct private_client
{
    int api_version;
    private_client_ops_t private_ops;
} private_client_t;

typedef struct private_client_helper
{
    void *libHandle;
    private_client_t *client;
    private_client_handle_t clientHandle;
} private_client_helper_t;

static inline private_client_helper_t *ControllerPrivateClient_Create()
{
    private_client_helper_t *me = (private_client_helper_t *)malloc(sizeof(private_client_helper_t));
    if (!me) return NULL;
    me->libHandle = dlopen(CONTROLLER_SERVICE_CLIENT_LIB, RTLD_NOW);
    if (!me->libHandle)
    {
        printf("%s dlopen %s failed\n", __func__, CONTROLLER_SERVICE_CLIENT_LIB);
        free(me);
        return NULL;
    }

    typedef private_client_t *(*private_client_wrapper_fn)(void);
    private_client_wrapper_fn client_;

    client_ = (private_client_wrapper_fn)dlsym(me->libHandle, "getHelloWorldPrivateClientInstance");
    if (!client_)
    {
        printf("%s dlsym %s failed\n", __func__, CONTROLLER_SERVICE_CLIENT_LIB);
        dlclose(me->libHandle);
        free(me);
        return NULL;
    }

    me->client = client_();
    me->clientHandle = me->client->private_ops.Create();

    return me;
}

static inline void ControllerPrivateClient_Destroy(private_client_helper_t *me)
{
    if (!me) return;

    if (me->client->private_ops.Destroy)
    {
        me->client->private_ops.Destroy(me->clientHandle);
    }

    if (me->libHandle)
    {
        dlclose(me->libHandle);
    }
    free(me);
    me = NULL;
}

class ControllerPrivate_1_0_1_0
{
  public:
    static uint version;
    static int HelloWorldPrivate_PrivateMethod(private_client_helper_t *me);
};

class ControllerPrivate_1_0_2_0
{
  public:
    static uint version;
};