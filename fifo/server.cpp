#include<stdio.h>
#include<unistd.h>
#include<sys/types.h>
#include<string.h>
#include<sys/stat.h>
#include<fcntl.h>
#include "TimeUtils.h"

#define _PATH_NAME_ "/dev/yvr_fifo"
#define _SIZE_ 100
    
 int main()
 {
      int ret=mkfifo(_PATH_NAME_,S_IFIFO|0666);
      if(ret==-1){
           printf("make fifo error\n");
           return 1;
      }
     char buf[_SIZE_];
     memset(buf,'\0',sizeof(buf));
     int fd=open(_PATH_NAME_,O_WRONLY);
     while(1)
     {
         //scanf("%s",buf);
         fgets(buf,sizeof(buf)-1,stdin);
         printf("write time%ld \n",TimeUtils::getCurTimeNs(CLOCK_REALTIME));
         int ret=write(fd,buf,strlen(buf)+1);
         if(ret<0){
         printf("write error");
         break;
         }
     }
      close(fd);
      return 0;
 }                   