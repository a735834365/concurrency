package com.zyf.concurrency.chapter12;

import java.util.concurrent.CyclicBarrier;

/**
 * 采用基于栅栏的定时器进行测试
 * 引自原文：
 *      从TimedPutTakeTest的运行中学到的一些东西
 *          1、生成消费者在不同参数组合下的吞吐率
 *          2、有界缓存在不同线程数量下的可伸缩性
 *          3、如何选择缓存的大小
 *      这个测试的主要目的是，测量生产者和消费者在通过有界缓存传递数据时，哪些约束条件将对整体吞吐量产生影响。
 * create by yifeng
 */
public class TimedPutTakeTest extends PutTakeTest {
    private BarrierTimer timer = new BarrierTimer();

    public TimedPutTakeTest(int capacity, int nTrials, int nPairs) {
        super(capacity, nTrials, nPairs);
        // 传入命令参数timer到 barrierAction中
        barrier = new CyclicBarrier(nPairs * 2 + 1, timer);
    }

    @Override
    void test() {
        try {
            timer.clear();
            for (int i = 0; i < nPairs; i++) {
                pool.execute(new Producer());
                pool.execute(new Consumer());
            }
            // 第一波栅栏等待，使所有生产者消费者同时工作
            barrier.await();
            // 第二波栅栏等待，等待所有线程完成
            barrier.await();
            // 计算平均时间
            long nsPerItem = timer.getTime() / (nPairs * (long) nTrials);
            System.out.print("Throughput: " + nsPerItem + "ns/item");
            assertEquals(putSum.get(), takeSum.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        int tpt = 10000; // trials per thread
        for (int cap = 1; cap <= 1000; cap *= 10) {
            System.out.println("Capacity: " + cap);
            for (int pairs = 1; pairs < 128; pairs *= 2) {
                TimedPutTakeTest t = new TimedPutTakeTest(cap, pairs, tpt);
                System.out.println("Pairs: " + pairs + "\t");
                t.test();
                System.out.print("\t");
                Thread.sleep(1000);
                t.test();
                System.out.println();
                Thread.sleep(1000);
            }
        }
        PutTakeTest.pool.shutdown();
    }
}
