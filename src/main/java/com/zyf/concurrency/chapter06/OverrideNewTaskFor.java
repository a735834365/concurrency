package com.zyf.concurrency.chapter06;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * 改写AbstractExecutorService中enwTaskFor的默认实现
 * 该类非书中案例
 *
 *  java.util.concurrent.AbstractExecutorService.newTaskFor(java.util.concurrent.Callable<T>)
 *
 *  ThreadPoolExecutor继承自AbstractExecutorService类
 *
 * create by yifeng
 */
public class OverrideNewTaskFor extends AbstractExecutorService {

    // 可改写该方法，从而根据以提交的Runnable或Callable来控制Future的实例化扩充，默认实现中创建了一个新的FutureTask
    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        // before
        RunnableFuture<T> tRunnableFuture = super.newTaskFor(callable);
        // after
        return tRunnableFuture;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public List<Runnable> shutdownNow() {
        return null;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void execute(Runnable command) {

    }
}
