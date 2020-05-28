package com.zyf.concurrency.chapter05;

import java.util.concurrent.CountDownLatch;

/**
 * 在计时测试中使用CountDownLatch来启动和停止线程
 *
 * 引自原文：
 *      使用两个闭锁，表示开始阀门和结束阀门，开始阀门初始值为1，结束阀门计数器初始值为工作线程的数量，
 *      每个工作线程首先做的就是在启动门上等待，确保所有线程就绪后才开始执行。而每个线程要做的最后一件
 *      事情就是调用结束阀门的countDown方法减1，这样就鞥呢使主线程等待所有工作线程都执行完成，因此可以
 *      统计所消耗的时间。
 *
 * create by yifeng
 */
public class TestHarness {
    public long timeTasks(int nThreads, final Runnable task) throws InterruptedException {
        // 开始阀门初始化为1
        final CountDownLatch startGate = new CountDownLatch(1);
        // 结束阀门 初始化为工作线程的数量
        final CountDownLatch endGate = new CountDownLatch(nThreads);
        
        // 循环使所有工作线程都初始化
        for(int i = 0; i < nThreads; i++) {
            Thread t = new Thread(() -> {
                try {
                    // 等待阀门打开，等待所有工作线程都初始化
                    startGate.await();
                    try {
                        task.run();
                    } finally {
                        // 线程执行结束，则-1操作
                        endGate.countDown();
                    }
                } catch(InterruptedException ignored) {
                }
            });
            t.start();
        }
        long start = System.nanoTime();
        // 打开阀门，线程开始工作
        startGate.countDown();
        // 结束阀门-使主线程等待，直到所有工作线程都执行完毕
        endGate.await();
        long end = System.nanoTime();
        // 计算执行时间
        return end - start;
    }

}