package com.zyf.concurrency.chapter12;

import junit.framework.TestCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试THReadPoolExecutor的线程工具类
 * 引自原文：
 *      通过使用自定义的线程工厂，可以对线程的创建过程进行公职
 *      {@link TestingThreadFactory} 将记录已创建线程的数量。
 *      还可以对TestingThreadFactory进行扩展，使其返回一个 自定义的Thread，并且该对象可以记录自己在何时结束，从而在测试方案中验证线程在被
 *      回收时是否与执行策略一致
 * create by yifeng
 */
public class TestThreadPool extends TestCase {
    public final TestingThreadFactory threadFactory = new TestingThreadFactory();

    /**
     * 程序清单 12-9 验证线程池扩展能力的测试方法
     * @throws InterruptedException
     */
    public void testPoolExpansion() throws InterruptedException {
        int MAX_SIZE = 10;
        ExecutorService exec = Executors.newFixedThreadPool(MAX_SIZE);

        for (int i = 0; i < 10 * MAX_SIZE; i++) {
            exec.execute(() -> {
                try {
                    Thread.sleep(Long.MAX_VALUE);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupted();
                }
            });
        }

        for (int i = 0; i < 20 && threadFactory.numCreated.get() < MAX_SIZE; i++) {
            Thread.sleep(100);
        }
        assertEquals(threadFactory.numCreated.get(), MAX_SIZE);
        exec.shutdownNow();
    }

    /**
     * 程序清单 12-8  测试的线程工厂类
     */
    class TestingThreadFactory implements ThreadFactory {
        // 记录创建线程的数量
        public final AtomicInteger numCreated = new AtomicInteger();
        private final ThreadFactory factory = Executors.defaultThreadFactory();

        @Override
        public Thread newThread(Runnable r) {
            // 实时更新
            numCreated.incrementAndGet();
            return factory.newThread(r);
        }
    }
}
