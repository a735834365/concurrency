package com.zyf.concurrency.chapter04;

import com.zyf.concurrency.annotations.NotThreadSafe;
import com.zyf.concurrency.annotations.ThreadSafe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * create by yifeng
 */
public class ListHelper {
    /**
     * 非线程安全的“若没有则添加”(不要这么做)
     * @param <E>
     */
    @NotThreadSafe
    class BadListHelper<E> {
        public List<E> list = Collections.synchronizedList(new ArrayList<>());

        public synchronized boolean putIfAbsent(E x) {
            boolean absent = !list.contains(x);
            if (absent)
                list.add(x);
            return absent;
        }
    }

    /**
     * 通过客户端加锁来实现“若没有则添加”
     */
    @ThreadSafe
    class GoodListHelper<E> {
        public List<E> list = Collections.synchronizedList(new ArrayList<>());

        public boolean putIfAbsent(E x) {
            synchronized (list) {
                boolean absent = !list.contains(x);
                if (absent)
                    list.add(x);
                return absent;
            }
        }
    }
}
