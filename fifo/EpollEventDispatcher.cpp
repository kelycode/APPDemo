#include "EpollEventDispatcher.h"
#include <log/log.h>
#include <sys/epoll.h>
#include <sys/eventfd.h>
#include <sys/prctl.h>

#define __CLASS__ "EpollEventDispatcher"

EpollEventDispatcher::EpollEventDispatcher()
{
    epoll_fd_.Reset(epoll_create1(EPOLL_CLOEXEC));
    if (!epoll_fd_)
    {
        ALOGE("Failed to create epoll fd: %s", strerror(errno));
        return;
    }

    event_fd_.Reset(eventfd(0, EFD_CLOEXEC | EFD_NONBLOCK));
    if (!event_fd_)
    {
        ALOGE("Failed to create event for epolling: %s", strerror(errno));
        return;
    }

    // Add watch for eventfd. This should only watch for EPOLLIN, which gets set
    // when eventfd_write occurs. Use "this" as a unique sentinal value to
    // identify events from the event fd.
    epoll_event event = {.events = EPOLLIN, .data = {.ptr = this}};
    if (epoll_ctl(epoll_fd_.Get(), EPOLL_CTL_ADD, event_fd_.Get(), &event) < 0)
    {
        ALOGE("Failed to add eventfd to epoll set because: %s", strerror(errno));
        return;
    }

    thread_ = std::thread(&EpollEventDispatcher::EventThread, this);
}

EpollEventDispatcher::~EpollEventDispatcher() { Stop(); }

void EpollEventDispatcher::Stop()
{
    exit_thread_.store(true);
    eventfd_write(event_fd_.Get(), 1);
}

Status<void> EpollEventDispatcher::AddEventHandler(int fd, int event_mask, Handler handler)
{
    std::lock_guard<std::mutex> lock(lock_);

    epoll_event event;
    event.events = event_mask;
    event.data.ptr = &(handlers_[fd] = handler);

    ALOGI(" fd=%d event_mask=0x%x handler=%p", fd, event_mask, event.data.ptr);

    if (epoll_ctl(epoll_fd_.Get(), EPOLL_CTL_ADD, fd, &event) < 0)
    {
        const int error = errno;
        ALOGE("Failed to add fd to epoll set because: %s", strerror(error));
        return ErrorStatus(error);
    }
    else
    {
        return {};
    }
}

Status<void> EpollEventDispatcher::RemoveEventHandler(int fd)
{
    ALOGE(" fd=%d", fd);
    std::lock_guard<std::mutex> lock(lock_);

    epoll_event dummy;  // See BUGS in man 2 epoll_ctl.
    if (epoll_ctl(epoll_fd_.Get(), EPOLL_CTL_DEL, fd, &dummy) < 0)
    {
        const int error = errno;
        ALOGE("Failed to remove fd from epoll set because: %s", strerror(error));
        return ErrorStatus(error);
    }

    // If the fd was valid above, add it to the list of ids to remove.
    removed_handlers_.push_back(fd);

    // Wake up the event thread to clean up.
    eventfd_write(event_fd_.Get(), 1);

    return {};
}

void EpollEventDispatcher::EventThread()
{
    prctl(PR_SET_NAME, reinterpret_cast<unsigned long>("CtrlEventDispatcher"), 0, 0, 0);

    const size_t kMaxNumEvents = 128;
    epoll_event events[kMaxNumEvents];

    while (!exit_thread_.load())
    {
        const int num_events = epoll_wait(epoll_fd_.Get(), events, kMaxNumEvents, -1);
        if (num_events < 0 && errno != EINTR) break;

        for (int i = 0; i < num_events; i++)
        {
            ALOGE(" num_events %d, event[%d]: handler=%p events=0x%x", num_events, i, events[i].data.ptr, events[i].events);

            if (events[i].data.ptr == this)
            {
                // Clear pending event on event_fd_. Serialize the read with respect to
                // writes from other threads.
                std::lock_guard<std::mutex> lock(lock_);
                eventfd_t value;
                eventfd_read(event_fd_.Get(), &value);
            }
            else
            {
                auto handler = reinterpret_cast<Handler*>(events[i].data.ptr);
                if (handler) (*handler)(events[i].events);
            }
        }

        // Remove any handlers that have been posted for removal. This is done here
        // instead of in RemoveEventHandler() to prevent races between the dispatch
        // thread and the code requesting the removal. Handlers are guaranteed to
        // stay alive between exiting epoll_wait() and the dispatch loop above.
        std::lock_guard<std::mutex> lock(lock_);
        for (auto handler_fd : removed_handlers_)
        {
            ALOGE("removing handler: fd=%d", handler_fd);
            handlers_.erase(handler_fd);
        }
        removed_handlers_.clear();
    }
}

