package com.zyf.concurrency.chapter08;

import com.zyf.concurrency.annotations.GuardedBy;
import com.zyf.concurrency.annotations.ThreadSafe;

import java.util.concurrent.CountDownLatch;

/**
 * 由ConcurrentPuzzleSolver使用的携带结果的闭锁
 *
 * create by yifeng
 */
@ThreadSafe
public class ValueLatch <T> {
    @GuardedBy
    private T value = null;
    private final CountDownLatch done = new CountDownLatch(1);

    public boolean isSet() {
        return (done.getCount() == 0);
    }
    //
    public synchronized void setValue(T newValue) {
        // 如果未设置结果，则设置结果值
        if (!isSet()) {
            value = newValue;
            // 开门
            done.countDown();
        }
    }

    // 如果找不到答案，闭锁将不会开门，这个方法将一直等待
    public T getValue() throws InterruptedException {
        done.await();
        synchronized (this) {
            return value;
        }
    }
}
