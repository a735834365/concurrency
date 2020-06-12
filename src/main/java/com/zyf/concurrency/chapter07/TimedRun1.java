package com.zyf.concurrency.chapter07;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 在外部线程中安排中断（不要这么做）
 * 引自原文：
 *      在指定时间内运行一个任意的Runnable，她在调用线程中运行任务，并安排了一个取消任务，在指定的时间间隔后中断它。这解决了从任务中抛出未检查异常的问题，因为该异常会被tmedRun的调用者捕获。
 *      破坏了规则：在中断线程之前应该了解它的中断策略。timeRun可以从任意一个线程中调用，因此它无法知道这调用线程的中断策略。
 *
 * create by yifeng
 */
public class TimedRun1 {
    private static final ScheduledExecutorService cancelExec = Executors.newScheduledThreadPool(1);

    public static void timeRun(Runnable r,
                               long timeout, TimeUnit unit) {
        final Thread taskThread = Thread.currentThread();
        cancelExec.schedule(() -> taskThread.interrupt(), timeout, unit);
        r.run();
    }
}
