package com.zyf.concurrency.chapter07;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

/**
 * 通过中断来取消
 * 引自原文：
 *      BrokenPrimeProducer中的问题很容易解决（和简化）：使用中断而不是boolean来请求和取消
 *
 * create by yifeng
 */
public class PrimeProducer extends Thread{
    private final BlockingQueue<BigInteger> queue;

    public PrimeProducer(BlockingQueue<BigInteger> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            BigInteger p = BigInteger.ONE;
            // 在循环开始处检查中断状态 - 显示的检测中断
            while (!Thread.currentThread().isInterrupted()) {
                queue.put(p = p.nextProbablePrime());
            }
        } catch (InterruptedException consumed) {
            /* 允许线程退出 */
        }
    }

    public void cancel() {
        // 使用中断请求取消
        interrupt();
    }
}
