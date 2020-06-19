package com.zyf.concurrency.chapter07;

import com.zyf.concurrency.annotations.GuardedBy;
import com.zyf.concurrency.annotations.ThreadSafe;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * 通过newTaskFor将非标准的取消操作封装在一个任务中
 * 引自原文：
 *      业务：使取消操作技能关闭套接字又能关闭任务，还能操作SocketI/O
 *      如果SocketUsingTask通过其自己的Future来取消，那么底层的套接字将被关闭并且线程将被撞断。因此它提高了任务对取消操作的响应性：不仅能够在调用可中断方法的同时确保响应取消操作，而且还能调用可阻塞的套接字I/O方法
 *
 * create by yifeng
 */
public abstract class SocketUsingTask <T> implements CancellableTask<T> {
    // 使该任务可调用可阻塞的套接字I/O方法
    @GuardedBy
    private Socket socket;

    protected synchronized void setSocket(Socket s) {
        socket = s;
    }

    // 包含了取消套接字的操作
    public synchronized void cancel() {
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException ignore) {
            }
    }

    public RunnableFuture<T> newTask() {
        // 定义Future.cancel来关闭套接字和调用super.cancel
        // RunnableFuture由FutureTask实现
        return new FutureTask<T>(this) {
            // 定制取消代码可以实现日志记录或手机取消操作的统计信息，以及一些不响应中断的操作。
            public boolean cancel(boolean mayInterruptIfRunning) {
                // 扩展Cancel方法
                try {
                    SocketUsingTask.this.cancel();
                } finally {
                    return super.cancel(mayInterruptIfRunning);
                }
            }
        };
    }
}


interface CancellableTask<T> extends Callable<T> {
    void cancel();
    // 可以创建自己的Future
    RunnableFuture newTask();
}

@ThreadSafe
class CancellingExecutor extends ThreadPoolExecutor {

    // newTaskFor是一个工厂方法，它将创建Future来代表任务
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        // 扩展ThreadPoolExecutor，CanncellableTask可以创建自己的Future
        if (callable instanceof CancellableTask)
            return ((CancellableTask<T>)callable).newTask();
        else
            return super.newTaskFor(callable);
    }

    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }


    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }
}