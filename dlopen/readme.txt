###############################################
#此工程是利用dlopen在工程中的应用，特别
#是接口的封装调用。qvrservice,controllerservice
#等很多地方再用。使程序方便扩展，具备通用性
#可根据接口version不同调用不同的接口
################################################

使用： 
1. 把dlopen目录copy到Android源码目录
2. 在dlopen根目录执行mm -j4
3. 生成的libhelloworld.so及dlopen_test
4. dlopen_test通过dlopen调用libhelloworld.so方法