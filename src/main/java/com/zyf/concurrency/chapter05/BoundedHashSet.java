package com.zyf.concurrency.chapter05;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * 使用Semaphore为容器设置边界
 *
 * 引自原文：
 *      信号量的计数值会初始化为容器的最大值。add操作在向底层容器中添加一个元素之前，首先要获取一个许可。
 *      如果add操作没有添加任何元素，那么会立刻释放许可。remove操作释放一个许可。
 *      底层的Set实现并不知道关于边界的任何信息，这是由BoundedHashSet来处理的
 *
 * create by yifeng
 */
public class BoundedHashSet<T> {
    private final Set<T> set;
    private final Semaphore sem;

    public BoundedHashSet(int bound) {
        this.set = Collections.synchronizedSet(new HashSet<>());
        sem = new Semaphore(bound);
    }

    public boolean add(T o) throws InterruptedException {
        sem.acquire();
        boolean wasAdded = false;
        try {
            wasAdded = set.add(o);
            return wasAdded;
        } finally {
            if (!wasAdded)
                sem.release();
        }
    }

    public boolean remove(Object o) {
        boolean wasRemoved = set.remove(o);
        if (wasRemoved)
            // 在一个线程中获得的许可可以在另一个线程中释放
            sem.release();
        return wasRemoved;
    }
}
