package com.zyf.concurrency.chapter04;

import com.zyf.concurrency.annotations.GuardedBy;

/**
 * 通过一个私有锁来保护对象
 *
 * create by yifeng
 */
public class PrivateLock {
    private final Object myLock = new Object();

    @GuardedBy("myLock")
    Widget widget;

    void someMethod() {
        synchronized (myLock) {
            // 访问并修改Widget的状态
        }
    }

}
