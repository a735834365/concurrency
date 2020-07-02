package com.zyf.concurrency.chapter12;

import junit.framework.TestCase;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试BoundedBuffer的生产者-消费者程序
 * 引自原文：
 *      在putTakeTest中启动了N个生产者线程来生成元素并把它们插入到队列，同时还启动了N个消费者线程从队列中取出元素。
 * create by yifeng
 */
public class PutTakeTest extends TestCase {
    protected static final ExecutorService pool = Executors.newCachedThreadPool();
    protected CyclicBarrier barrier;
    protected final SemaphoreBoundedBuffer<Integer> bb;
    // nPairs 工作者线程数 = npairs * 2
    protected final int nTrials, nPairs;
    protected final AtomicInteger putSum = new AtomicInteger(0);
    protected final AtomicInteger takeSum = new AtomicInteger(0);

    public static void main(String[] args) throws Exception {
        new PutTakeTest(10, 10, 10000).test();
        pool.shutdown();
    }

    public PutTakeTest(int capacity, int nTrials, int nPairs) {
        this.bb = new SemaphoreBoundedBuffer<>(capacity);
        this.nTrials = nTrials;
        this.nPairs = nPairs;
        // 将计数值指定为工作者线程数量再加1
        // 使用countDownLatch也可以实现同样的效果
        this.barrier = new CyclicBarrier(nPairs * 2 + 1);
    }

    void test() {
        try {
            for (int i = 0; i < nPairs; i++) {
                pool.execute(new Producer());
                pool.execute(new Consumer());
            }
            // 第一波栅栏等待，使所有生产者消费者同时工作
            barrier.await();
            // 第二波栅栏等待，等待所有线程完成
            barrier.await();
            assertEquals(putSum.get(), takeSum.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static int xorShift(int y) {
        y ^= (y << 6);
        y ^= (y >>> 21);
        y ^= (y << 7);
        return y;
    }

    class Producer implements Runnable {

        @Override
        public void run() {
            try {
                // 生成消费者数据
                int seed = (this.hashCode() ^ (int) System.nanoTime());
                // 统计当前线程生成数据的数量，
                // 每个线程都拥有一个校验和，并在测试结束后将它们合并起来，从而在测试缓存时就不会引入过多的同步或竞争
                int sum = 0;
                // 第一波栅栏等待，使所有生产者消费者同时工作
                barrier.await();
                for (int i = nTrials; i > 0; -- i) {
                    // 将种子put到盘子中以供消费者消费
                    bb.put(seed);
                    // 统计校验和
                    sum += seed;
                    // 重新生成种子
                    seed = xorShift(seed);
                }
                // 统计生产者数据的总量-合并校验和
                putSum.getAndAdd(sum);
                // 第二波栅栏等待，等待所有线程完成
                barrier.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    class Consumer implements Runnable {

        @Override
        public void run() {
            try {
                // 第一波栅栏等待，使所有生产者消费者同时工作
                barrier.await();
                int sum = 0;
                for (int i = nTrials; i > 0; -- i) {
                    sum += bb.take();
                }
                takeSum.getAndAdd(sum);
                // 第二波栅栏等待，等待所有线程完成
                barrier.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
