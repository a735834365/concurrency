package com.zyf.concurrency.chapter05;

import com.zyf.concurrency.annotations.GuardedBy;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用HashMap和同步机制来初始化缓存
 *
 * 引自原文：
 *      由于HashMap不是线程安全的，因此Memoizer1采用了保守的方法，对整个方法进行同步，这会带来明显的可伸缩性问题：每次只有一个线程能够执行compute
 *
 * A --> 计算f(1)
 * B------------>计算f(2)
 * C---------------------> 返回缓存中的f(1)
 *      Memoizer1糟糕的并发性
 *
 * create by yifeng
 */
public class Memoizer1<A, V> implements Computable<A, V> {
    @GuardedBy("this")
    private final Map<A, V> cache = new HashMap<A, V>();
    private final Computable<A, V> c;

    public Memoizer1(Computable<A, V> c) {
        this.c = c;
    }

    @Override
    public synchronized V compute(A arg) throws InterruptedException {
        // 结果是否已经在缓存中，存在则返回之前计算的值，否则吧计算结果缓存到HashMap中，然后再返回。
        V result = cache.get(arg);
        if (result == null) {
            result = c.compute(arg);
            cache.put(arg, result);
        }
        return result;
    }
}

