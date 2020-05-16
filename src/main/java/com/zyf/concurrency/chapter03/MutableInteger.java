package com.zyf.concurrency.chapter03;

import com.zyf.concurrency.annotations.NotThreadSafe;

/**
 * 引自原文：
 *      如果某个线程调用了set，那么另一个正在调用get的线程
 *   可能会看到更新后的value值，也可能看不到。
 *
 * 非线程安全的可变整数类
 * create by yifeng
 */
@NotThreadSafe
public class MutableInteger {
    private int value;

    public int get() {
        return value;
    }

    public void set(int value) {
        this.value = value;
    }
}
