package com.zyf.concurrency.chapter07;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 在ExecutorService中跟踪在关闭之后被取消的任务
 * 引自原文：
 *      该程序给出了如何在关闭过程中判断正在执行的任务，封装ExecutorService的Execute（还可以用于submit）
 *   记录关闭后被取消的任务，已经开始且没有正常完成的任务将被记录。
 *      任务返回时，必须维持线程的中断状态。
 * 个人理解：
 *      两种情况：
 *          1：如果线程池有足够的的空间存放任务，那么所有已经开始且没有正常完成额的任务将被记录
 *          2：如果线程池没有足够的空间存放任务，那么就会有阻塞的任务，这时，需要借助awaitTermination进行shutdownNow，
 *              这样做的话，被阻塞的任务将会被抛弃。
 *          一句话嘛，已经开始且没有正常完成的任务总会被保存，而因为线程池空间不足而被阻塞的任务将会被抛弃
 *
 * create by yifeng
 */
public class TrackingExecutor extends AbstractExecutorService {
    private final ExecutorService exec;
    private final Set<Runnable> tasksCancelledAtSHutDown =
            Collections.synchronizedSet(new HashSet<Runnable>());

    public TrackingExecutor(ExecutorService exec) {
        this.exec = exec;
    }

// 获得所有取消的任务
    public List<Runnable> getCancelledTasks() {
        if (!exec.isTerminated())
            throw new IllegalStateException("/*..*/");
        return new ArrayList<>(tasksCancelledAtSHutDown);
    }

    @Override
    public void execute(Runnable runnable) {
        exec.execute(() -> {
            try {
                runnable.run();
            } finally {
                // 关闭过程中，记录正在取消的任务
                if (isShutdown() && Thread.currentThread().isInterrupted())
                    tasksCancelledAtSHutDown.add(runnable);
            }
        });
    }

    @Override
    public void shutdown() {
        exec.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return exec.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return exec.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return exec.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return exec.awaitTermination(timeout, unit);
    }

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch startGate = new CountDownLatch(1);
        TrackingExecutor exec = new TrackingExecutor(Executors.newCachedThreadPool());
        // 在线程池中阻塞的线程并不会被保留，可以使用该线程池替换缓存线程池
        TrackingExecutor exec1 = new TrackingExecutor(Executors.newFixedThreadPool(1));
        // 执行以下两个任务，如果使用Executors.newFixedThreadPool(1),则阻塞的任务会被抛弃
        exec.execute(() -> {
            try {
                startGate.await();
                // 创建一个阻塞任务，模拟还没有完成的任务
                ArrayBlockingQueue queue = new ArrayBlockingQueue(1);
                queue.put(1);
                queue.put(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        exec.execute(() -> {
            try {
                startGate.await();
                // 创建一个阻塞任务，模拟还没有完成的任务
                ArrayBlockingQueue queue = new ArrayBlockingQueue(1);
                queue.put(1);
                queue.put(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        startGate.countDown();
        // 该取消示例可看JDK文档 java.util.concurrent.ExecutorService
        /**
         * 以下注释来自
         * java.util.concurrent.ExecutorService#shutdown()
         *  This method does not wait for previously submitted tasks to
         *  complete execution.  Use {@link #awaitTermination awaitTermination}
         *  to do that.
         *  如果使用以下的关闭方式，可达到抛弃正在阻塞任务的目的，
         *  相反，如果不配合awaitTermination进行shutdownNow，
         *  那么下面的exec.getCancelledTasks();将会报错，
         *  因为线程池无法被关闭
         */
        exec.shutdown();
        if (!exec.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
            exec.shutdownNow();
            if (!exec.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                // 如果还是没关闭，则日志警告
                System.out.println("线程池关闭失败");
            }
        }
        // 拿到未完成的问题，正在执行的线程将会被保留
        List<Runnable> cancelledTasks = exec.getCancelledTasks();
        System.out.println(cancelledTasks.size());
    }
}
