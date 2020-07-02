package com.zyf.concurrency.chapter12;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 适合在测试中使用的随机数生成器
 * 引自原文：
 *      简单的伪随机函数（Marsaglia，2003）。
 * create by yifeng
 */
public class XorShift {
    static final AtomicInteger seq = new AtomicInteger(8862213);
    int x = -1831433054;

    public XorShift(int seed) {
        this.x = seed;
    }

    public XorShift() {
        // 基于hashCode和nanoTime来生成随机数，所得的结果即是不可预测的，而且基本上每次运行都不同。
        this((int) System.nanoTime() + seq.getAndAdd(129));
    }

    public int next() {
        x ^= x << 6;
        x ^= x >>> 21;
        x ^= (x << 7);
        return x;
    }
}