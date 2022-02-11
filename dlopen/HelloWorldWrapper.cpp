#include <log/log.h>
#include "HelloWorld.h"
#include "version.h"
#include "HelloWorldWrapper.h"

// -------------------------------------------------- Public Interfaces ---------------------------------------------------------------//
static public_client_t* public_client = nullptr;

static public_client_handle_t HelloWorldPublic_Create() { return nullptr; }

static void HelloWorldPublic_Destroy(public_client_handle_t handle)
{
    if (public_client != nullptr)
    {
        free(public_client);
        public_client = nullptr;
    }
    HelloWorld::getInstance()->HelloWorld_Destory();
}

static int HelloWorldPublic_PublicMethod()
{
    int ret = -1;
    HelloWorld* client = HelloWorld::getInstance();
    if(client != nullptr)
    {
        ret = client->HelloWorld_PublicMethod();
    }
    return ret;
}

// -------------------------------------------------- Private Interfaces ---------------------------------------------------------------//
static private_client_t* private_client = nullptr;

static private_client_handle_t HelloWorldPrivate_Create() { return nullptr; }

static void HelloWorldPrivate_Destroy(private_client_handle_t handle)
{
    if (private_client != nullptr)
    {
        free(private_client);
        private_client = nullptr;
    }
    HelloWorld::getInstance()->HelloWorld_Destory();
}

static int HelloWorldPrivate_PrivateMethod() 
{
    int ret = -1;
    HelloWorld* client = HelloWorld::getInstance();
    if(client != nullptr)
    {
        ret = client->HelloWorld_PrivateMethod();
    }
    return ret;
}

public_client_t* getHelloWorldPublicClientInstance(void)
{
    if (public_client == nullptr)
    {
        public_client = (public_client_t*)malloc(sizeof(public_client_t));
        public_client->api_version = ASSEMBLE_VERSION;
        public_client->public_ops.Create = HelloWorldPublic_Create;
        public_client->public_ops.Destroy = HelloWorldPublic_Destroy;
        public_client->public_ops.PublicMethod = HelloWorldPublic_PublicMethod;
    }
    return public_client;
}

private_client_t* getHelloWorldPrivateClientInstance(void)
{
    if (private_client == nullptr)
    {
        private_client = (private_client_t*)malloc(sizeof(private_client_t));
        private_client->api_version = ASSEMBLE_VERSION;
        private_client->private_ops.Create = HelloWorldPrivate_Create;
        private_client->private_ops.Destroy = HelloWorldPrivate_Destroy;
        private_client->private_ops.PrivateMethod = HelloWorldPrivate_PrivateMethod;
    }
    return private_client;
}
