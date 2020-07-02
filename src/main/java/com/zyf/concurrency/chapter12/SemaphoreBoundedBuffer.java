package com.zyf.concurrency.chapter12;

import com.zyf.concurrency.annotations.GuardedBy;
import com.zyf.concurrency.annotations.ThreadSafe;
import sun.net.util.IPAddressUtil;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * 基于信号量的有界缓存
 * 引自原文：
 *      SemaphoreBoundedBuffer 实现了一个固定长度的队列，如果需要一个有界缓存，应该直接使用{@link ArrayBlockingQueue} 或 {@link LinkedBlockingQueue}
 *      无论是从put方法还是从take方法退出，这两个信号量计数值的和都会等于缓存的大小。
 * create by yifeng
 */
@ThreadSafe
public class SemaphoreBoundedBuffer <E> {
    // 使用两个计数信号量控制阻塞
    // availableItems表示可从缓存中删除的元素个数，初始值为0
    // availableSpaces 表示可以插入到缓存的元素个数，它的初始值等于缓存的大小
    private final Semaphore availableItems, availableSpaces;
    // 实现一个固定长度的队列
    @GuardedBy("this")
    private final E[] items;
    @GuardedBy("this")
    private int putPosition = 0, takePosition = 0;

    public SemaphoreBoundedBuffer(int capacity) {
        if (capacity <= 0)
            throw new IllegalArgumentException();
        availableItems = new Semaphore(0);
        availableSpaces = new Semaphore(capacity);
        items = (E[]) new Object[capacity];
    }

    public boolean isEmpty() {
        return availableItems.availablePermits() == 0;
    }

    public boolean isFull() {
        return availableSpaces.availablePermits() == 0;
    }

    // 定义可阻塞的 put
    public void put(E x) throws InterruptedException {
        // 每加入一个元素，可插入到缓存的元素数就变少
        availableSpaces.acquire();
        doInsert(x);
        // 每加入一个元素，可删除的缓存元素数就变多
        availableItems.release();
    }

    // 定义可阻塞的 take
    public E take() throws InterruptedException {
        // 每减少一个元素，可删除的缓存元素数就变多
        availableItems.acquire();
        E item = doExtract();
        // 每减少一个元素，可插入到缓存的元素数就变少
        availableSpaces.release();
        return item;
    }

    private synchronized void doInsert(E x) {
        int i = putPosition;
        items[i] = x;
        putPosition = (++i == items.length) ? 0 : i;
    }

    private synchronized E doExtract() {
        int i = takePosition;
        E x = items[i];
        items[i] = null;
        takePosition = (++i == items.length) ? 0 : i;
        return x;
    }
}