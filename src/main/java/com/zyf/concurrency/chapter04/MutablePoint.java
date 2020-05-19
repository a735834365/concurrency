package com.zyf.concurrency.chapter04;

import com.zyf.concurrency.annotations.NotThreadSafe;

/**
 * 与Java.awt.Point类似的可变Point类(不要这么做)
 *
 * 引自原文：
 *      虽然MutablePoint不是线程安全的，但追踪器类是线程安全的。
 *      他所包含的Map对象和可变的Point对象都未曾发布。
 *
 * Create by yifeng
 */
@NotThreadSafe
public class MutablePoint {
    public int x, y;

    public MutablePoint() {
        this.x = 0;
        this.y = 0;
    }

    public MutablePoint(MutablePoint p) {
        this.x = p.x;
        this.y = p.y;
    }


}
