package com.zyf.concurrency.chapter07;

import com.zyf.concurrency.chapter05.LaunderThrowable;
import com.zyf.concurrency.expmple.ReaderThread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 在专门的线程中中断任务
 * 引自原文：
 *      示例代码依赖于一个限时的join，因此存在join的不足：无法知道执行控制是因为线程正常退出而返回还是因为join超时而返回。
 *      这是ThreadAPI的一个缺陷，因为无论join是否成功地完成，在Java内存模型中都会有内存可见性结果，但join本身不会返回某个状态来表明它是否会成功。
 *
 * create by yifeng
 */
public class TimeRun2 {
    private static final ScheduledExecutorService cancelExec = Executors.newScheduledThreadPool(1);

    /**
     *
     * @param r 工作线程
     * @param timeout 超时时间
     * @param unit 时间单位
     * @throws InterruptedException
     */
    public static void timeRun(final Runnable r, long timeout, TimeUnit unit) throws InterruptedException {
        class RethrowableTask implements Runnable {
            // Throwable将在两个线程之间共享，因此该变量被声明为volatile变量
            private volatile  Throwable t;

            @Override
            public void run() {
                try {
                    r.run();
                } catch (Exception t) {
                    this.t = t;
                }
            }

            void rethrow() {
                if (t != null)
                    throw LaunderThrowable.launderThrowable(t);
            }
        }

        RethrowableTask task = new RethrowableTask();
        final Thread taskThread = new Thread(task);
        taskThread.start();
        // 创建一个定时任务，如果超时则使任务线程中断
        cancelExec.schedule(() -> taskThread.interrupt(), timeout, unit);
        // 执行限时的join方法
        taskThread.join(unit.toMillis(timeout));
        // join方法返回后，检查任务是否有任务抛出，如果有，则在-调用-timeRun的线程中在此抛出异常
        task.rethrow();
    }


}
