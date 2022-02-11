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

typedef void *public_client_handle_t;

typedef struct public_client_ops
{
    public_client_handle_t (*Create)();
    void (*Destroy)(public_client_handle_t client);
    int (*PublicMethod)();
} public_client_ops_t;

typedef struct public_client
{
    int api_version;
    public_client_ops_t public_ops;
} public_client_t;

typedef struct public_client_helper
{
    void *libHandle;
    public_client_t *client;
    public_client_handle_t clientHandle;
} public_client_helper_t;

static inline public_client_helper_t *HelloWorldPublic_Create()
{
    public_client_helper_t *me = (public_client_helper_t *)malloc(sizeof(public_client_helper_t));
    if (!me) return NULL;
    me->libHandle = dlopen(CONTROLLER_SERVICE_CLIENT_LIB, RTLD_NOW);
    if (!me->libHandle)
    {
        printf("%s dlopen %s failed\n", __func__, CONTROLLER_SERVICE_CLIENT_LIB);
        free(me);
        return NULL;
    }

    typedef public_client_t *(*public_client_wrapper_fn)(void);
    public_client_wrapper_fn client_;

    client_ = (public_client_wrapper_fn)dlsym(me->libHandle, "getHelloWorldPublicClientInstance");
    if (!client_)
    {
        printf("%s dlsym %s failed\n", __func__, CONTROLLER_SERVICE_CLIENT_LIB);
        dlclose(me->libHandle);
        free(me);
        return NULL;
    }

    me->client = client_();
    me->clientHandle = me->client->public_ops.Create();

    return me;
}

static inline void HelloWorldPublic_Destroy(public_client_helper_t *me)
{
    if (!me) return;

    if (me->client->public_ops.Destroy)
    {
        me->client->public_ops.Destroy(me->clientHandle);
    }

    if (me->libHandle)
    {
        dlclose(me->libHandle);
    }
    free(me);
    me = NULL;
}


class ControllerPublic_1_0_1_0
{
  public:
    static uint version;

    static int HelloWorldPublic_PublicMethod(public_client_helper_t *me);
};

class ControllerPublic_1_0_2_0
{
  public:
    static uint version;
};
