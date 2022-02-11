#include <fcntl.h>
#include <stdio.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>
#include "EpollEventDispatcher.h"
#include "TimeUtils.h"

#define _PATH_NAME "/dev/yvr_fifo"
#define _SIZE_ 100
char buf[_SIZE_];
int fd;

void handler(int events)
{
    printf("read time%ld \n",TimeUtils::getCurTimeNs(CLOCK_REALTIME));
    int ret = read(fd, buf, sizeof(buf));
    if (ret < 0)
    {
        printf("read end or error\n");
        return;
    }
    printf("%s  events:%d\n", buf, events);
}

int main()
{
    EpollEventDispatcher mDispatcher;
    fd = open(_PATH_NAME, O_RDONLY);
    if (fd < 0)
    {
        printf("open file error");
        return 1;
    }
    memset(buf, '\0', sizeof(buf));
    mDispatcher.AddEventHandler(fd, EPOLLIN | EPOLLHUP, handler);

    while (1){sleep(3);}

    mDispatcher.RemoveEventHandler(fd);
    close(fd);
    return 0;
}