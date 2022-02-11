#pragma once

#include <utils/String8.h>

/****************************************public***********************************************/
typedef void* public_client_handle_t;

typedef struct public_client_ops_t
{
    public_client_handle_t (*Create)();
    void (*Destroy)(public_client_handle_t client);
    int (*PublicMethod)();

} public_client_ops;

typedef struct public_client
{
    int api_version;
    public_client_ops_t public_ops;
} public_client_t;

typedef struct public_client_helper
{
    void* libHandle;
    public_client_t* client;
    public_client_handle_t clientHandle;
} public_client_helper_t;


/****************************************private***********************************************/

typedef void* private_client_handle_t;

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
    void* libHandle;
    private_client_t* client;
    private_client_handle_t clientHandle;
} private_client_helper_t;

extern "C"
{
    public_client_t* getHelloWorldPublicClientInstance(void);
    private_client_t* getHelloWorldPrivateClientInstance(void);
}
