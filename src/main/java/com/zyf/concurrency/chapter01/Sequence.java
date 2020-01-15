package com.zyf.concurrency.chapter01;

import com.zyf.concurrency.annotations.GuardedBy;
import com.zyf.concurrency.annotations.ThreadSafe;

/**
 * 通过将 getNext 修改为一个同步方法，可以修复 UnsafeSequence 中的错误
 * create by yifeng
 */
@ThreadSafe
public class Sequence {
    @GuardedBy("this")
    private int Value;

    public  synchronized int getNext() {
        return Value++;
    }
}
