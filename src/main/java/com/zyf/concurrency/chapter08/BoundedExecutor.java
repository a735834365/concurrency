package com.zyf.concurrency.chapter08;

import com.zyf.concurrency.annotations.ThreadSafe;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

/**
 * 使用 Semaphore来控制任务提交的速率
 * 引自原文：
 *      当工作队列被填满后，没有预定义的饱和策略来阻塞execute，然而，通过使用Semaphore（信号量）来限制任务的到达率，就可以实现这个功能。
 *      该程序使用了一个无界队列（因为不能限制队列的大小和到达率），并设置信号量的上界设置为线程池的大小加上可排队任务的数量，这是因为信号量需要控制正在执行的和等待执行的任务数量
 *
 * create by yifeng
 */
@ThreadSafe
public class BoundedExecutor {
    private final Executor exec;
    private final Semaphore semaphore;

    public BoundedExecutor(Executor exec, int bound) {
        this.exec = exec;
        this.semaphore = new Semaphore(bound);
    }

    public void submitTask(final Runnable command) throws InterruptedException {
        // 获得许可
        // 限制了任务提交的数量，超过bound数量的任务将会被阻塞
        semaphore.acquire();
        try {
            exec.execute(() -> {
                try {
                    command.run();
                } finally {
                    // 释放许可
                    semaphore.release();
                }
            });
        // AbortPolicy会抛出该异常
        } catch (RejectedExecutionException e){
            // 释放许可
            semaphore.release();
        }

    }


}
