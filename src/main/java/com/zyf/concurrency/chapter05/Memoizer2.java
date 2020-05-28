package com.zyf.concurrency.chapter05;

import org.jboss.logging.Param;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用ConcurrentHash替换HashMap
 *
 * 引自原文：
 *      Memoizer2的问题在于，如果某个线程启动了一个开销很大的计算，而其他线程并不知道这个计算正在进行，那么可能重复这个计算。
 *      如下图所示，当一个线程X正在计算时，得让另一个线程知道线程X正在计算，然后再去查询结果：FutureTask能基本实现这个功能，通过使用FutureTask.get将立刻返回结果，否则会一直阻塞，直到计算结果出来。
 *
 * A --> f(1)不在缓存中--> 计算f(1) -------> 将f(1)放入缓存-->
 * B ------------------> f(1)不在缓存中 --> 计算f(1)--------> 将f(1)放入缓存
 *              当使用Memoizer2时，两个线程计算相同的值
 *
 * create by yifeng
 */
public class Memoizer2<A, V> implements Computable<A, V>{
    // 相对于HashMap，多线程可以并发的使用它
    private final Map<A, V> cache = new ConcurrentHashMap<>();
    private final Computable<A, V> c;
    // 指定计算器
    public Memoizer2(Computable<A, V> c) {
        this.c = c;
    }

    /**
     * 这里存在原子性的操作问题，当两个线程同时调用compute时存在一个漏洞，可能会导致计算得到相同的值。
     *
     */
    @Override
    public V compute(A arg) throws InterruptedException {

        V result = cache.get(arg);
        if (result == null) {
            result = c.compute(arg);
            cache.put(arg, result);
        }
        return result;
    }

}
