package com.zyf.concurrency.chapter03;

import com.zyf.concurrency.annotations.GuardedBy;
import com.zyf.concurrency.annotations.ThreadSafe;

/**
 * 引自原文：
 *      仅对set方法进行同步是不够的，调用get的线程仍然会看见失效值
 *
 * 线程安全的可变整数类
 * create by yifeng
 */
@ThreadSafe
public class SynchronizedInteger {
    @GuardedBy("this")
    private int value;

    public synchronized int get() {
        return value;
    }

    public synchronized void set(int value) {
        this.value = value;
    }
}
