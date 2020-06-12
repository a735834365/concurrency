package com.zyf.concurrency.chapter07;

import com.zyf.concurrency.chapter05.LaunderThrowable;

import java.util.concurrent.*;

/**
 * 通过Future来取消任务
 * 引自原文：
 *      取消哪些不需要结果的任务（程序清单6-13 和 6-16中使用了相同的技术）
 *      当Future.get抛出了InterruptedException 或 TimeoutException,如果你知道不在
 *   需要结果，那么就可以调用Future.cancel来取消任务
 *
 * create by yifeng
 */
public class TimeRun {
    private static final ExecutorService taskExec = Executors.newCachedThreadPool();

    public static void timeRun(Runnable r,
                               long timeout, TimeUnit unit) throws InterruptedException {
        Future<?> task = taskExec.submit(r);
        try {
            // 使用一个定时的Future.get获得结果
            task.get(timeout, unit);
        } catch (ExecutionException e) {
            // 如果在任务中抛出了异常，那么重新抛出该异常
            throw LaunderThrowable.launderThrowable(e.getCause());
        } catch (TimeoutException e) {
            // 接下来任务将被取消
        } finally {
            // 如果任务已经结束，那么执行取消操作也不会带来任何影响
            task.cancel(true); // 如果任务正在运行，那么将被中断
        }
    }
}
