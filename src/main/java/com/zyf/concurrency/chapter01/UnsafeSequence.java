package com.zyf.concurrency.chapter01;

import com.zyf.concurrency.annotations.NotThreadSafe;

/**
 * 摘录：UnsafeSequence的问题在于，如果执行时机不到，那么两个个钱程在调用 getNext 时会得到
 * 相同的值。
 * create by yifeng
 */
@NotThreadSafe
public class UnsafeSequence {
    private int value;

    public int getNext() {
        return value++;
    }
}
