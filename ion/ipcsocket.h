
#ifndef _IPCSOCKET_H
#define _IPCSOCKET_H

// 增加这个宏，要不要编译报错
#ifndef SUN_LEN_ //In case they fix it down the road
#define SUN_LEN_(ptr) ((size_t) (((struct sockaddr_un *) 0)->sun_path) + strlen((ptr)->sun_path))
#endif

#define MAX_SOCK_NAME_LEN	64

char sock_name[MAX_SOCK_NAME_LEN];

/* This structure is responsible for holding the IPC data
 * data: hold the buffer fd
 * len: just the length of 32-bit integer fd
 */
struct socketdata {
	int data;
	unsigned int len;
};

/* This API is used to open the IPC socket connection
 * name: implies a unique socket name in the system
 * connecttype: implies server(0) or client(1)
 */
int opensocket(int *sockfd, const char *name, int connecttype);

/* This is the API to send socket data over IPC socket */
int sendtosocket(int sockfd, struct socketdata *data);

/* This is the API to receive socket data over IPC socket */
int receivefromsocket(int sockfd, struct socketdata *data);

/* This is the API to close the socket connection */
int closesocket(int sockfd, char *name);


#endif
