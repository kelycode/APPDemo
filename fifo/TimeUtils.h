#ifndef __TIME_UTILS_H__
#define __TIME_UTILS_H__

#ifndef LOG_TAG
#define LOG_TAG "TimeUtils"
#endif

#include <android-base/macros.h>
#include <log/log.h>
#include <sched.h>
#include <sys/syscall.h>
#include <fstream>
#include <iostream>
#include <string>

#define NANOSECONDS_TO_MILLISECONDS 1e-6
#define MILLISECONDS_TO_NANOSECONDS 1e6

#define NSEC_PER_SEC (1000000000ull)
#define SEC_TO_NSEC(sec) ((sec)*NSEC_PER_SEC)

#define QTIMER_FASTER (0)
#define BOOTTIMER_FASTER (1)

class TimeUtils
{
  public:
    static inline uint64_t getCurQtimerTimeNs();
    static inline uint64_t getCurTimeNs(clockid_t clk_id);
    static inline uint64_t getQbTimerOffsetNs();
    static inline void nanoSleep(uint64_t ns, int is_busywait);

  private:
    DISALLOW_COPY_AND_ASSIGN(TimeUtils);
};

uint64_t TimeUtils::getCurTimeNs(clockid_t clk_id) 
{
    struct timespec t;
    uint64_t result;

    clock_gettime(clk_id, &t); //clk_id CLOCK_REALTIME:系统实时时间 CLOCK_MONOTONIC:从系统启动这一刻起开始计时,不受系统时间被用户改变的影响。
    result = t.tv_sec * NSEC_PER_SEC + t.tv_nsec;

    return result;
}

void TimeUtils::nanoSleep(uint64_t ns, int is_busywait)
{
    uint64_t pre_sleep = getCurTimeNs(CLOCK_MONOTONIC);

    if (!is_busywait)
    {
        struct timespec t, rem;
        t.tv_sec = 0;
        t.tv_nsec = ns;
        nanosleep(&t, &rem);
    }
    else
    {
        while (1)
        {
            uint64_t time_now = getCurTimeNs(CLOCK_MONOTONIC);
            if ((time_now - pre_sleep) > ns)
            {
                break;
            }
        }
    }
}

static uint64_t qtimer_get_freq()
{
#if defined(TARGET_ARM64)
    uint64_t val = 0;
    asm volatile("mrs %0, cntfrq_el0" : "=r"(val));
    return val;
#else
    uint32_t val = 0;
    asm volatile("mrc p15, 0, %[val], c14, c0, 0" : [val] "=r"(val));
    return val;
#endif
}

static uint64_t qtimer_get_ticks()
{
#if defined(TARGET_ARM64)
    unsigned long long val = 0;
    asm volatile("mrs %0, cntvct_el0" : "=r"(val));
    return val;
#else
    uint64_t val;
    unsigned long lsb = 0, msb = 0;
    asm volatile("mrrc p15, 1, %[lsb], %[msb], c14" : [lsb] "=r"(lsb), [msb] "=r"(msb));
    val = ((uint64_t)msb << 32) | lsb;
    return val;
#endif
}

static uint64_t qtimer_ticks_to_ns(uint64_t ticks) { return (uint64_t)((double)ticks * ((double)NSEC_PER_SEC / (double)qtimer_get_freq())); }

uint64_t TimeUtils::getCurQtimerTimeNs() { return qtimer_ticks_to_ns(qtimer_get_ticks()); }

uint64_t TimeUtils::getQbTimerOffsetNs()
{
    std::ifstream file("/sys/class/qbtimer_offset/qbtimer/offset");
    std::string state;
    if (!file.is_open())
    {
        ALOGE("open %s failed", "/sys/class/qbtimer_offset/qbtimer/offset");
        return 0;
    }
    file >> state;
    ALOGI("===> QbOffsetNs: %s", state.c_str());
    return std::stoull(state);
}


#endif
