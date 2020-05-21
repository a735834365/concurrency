package com.zyf.concurrency.chapter04;

import com.zyf.concurrency.annotations.GuardedBy;
import com.zyf.concurrency.annotations.ThreadSafe;

/**
 * 线程安全且可变的Point类
 *
 * 引自原文：
 *      如果将拷贝构造函数实现为this(p.x, p.y),那么会产生竞态条件，而私有构造函数则可以避免
 *      这种竞态条件。这是私有构造函数捕获模式的一个实现
 *
 * create by yifeng
 */
@ThreadSafe
public class SafePoint {
    @GuardedBy("this")
    private int x, y;

    private SafePoint(int[] a) {
        this(a[0], a[1]);
    }

    public SafePoint(SafePoint p) {
        this(p.get());
    }

    public SafePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public synchronized int[] get() {
        return new int[] {x, y};
    }

    public synchronized void set(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
