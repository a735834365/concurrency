package com.zyf.concurrency.chapter05;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 基于FutureTask的Memoizing封装器
 *
 * 引自原文：
 *      Memoizer3首先检查某个响应的计算是否已经开始（Memoizer2与之相反，它首先判断某个计算是否已经完成）。如果还没有启动，那么就创建一个FutureTask，并注册到Map中，然后计算。如果已经启动，那么等待现有计算的结果。结果可能很快得到，也可能还在运算过程中，但这对于Future.get调用者来说是透明的。
 *
 * A --> f(1)不在缓存中 -->将f(1)的Future放入缓存 --> 计算f(1) --> 设置结果
 * B ---> f(1)不在缓存中 -->将f(1)的Future放入缓存 --> 计算f(1) --> 设置结果
 *
 * create by yifeng
 */
public class Memoizer3<A, V> implements Computable<A, V> {
    private final Map<A, Future<V>> cache
            = new ConcurrentHashMap<>();
    private final Computable<A, V> c;

    public Memoizer3(Computable<A, V> c) {
        this.c = c;
    }

    @Override
    public V compute(A arg) throws InterruptedException {
        // 尽管Memoizer3很完美，但是以下if判断仍然会有两个或多个线程同时进入的可能
        Future<V> f = cache.get(arg);
        if (f == null) {
            Callable<V> eval = () -> { // 开启线程运算
                return c.compute(arg);
            };
            FutureTask<V> ft = new FutureTask<>(eval);
            f = ft;
            // 仍然存在两个线程计算出相同值的漏洞，符合若没有则添加的原子性操作问题，这里需要使用方法putIfAbsent
            cache.put(arg, ft);
            ft.run(); // 在这里将调用 c.compute
        }
        try {
            return f.get();
        } catch (ExecutionException e) {
            throw LaunderThrowable.launderThrowable(e.getCause());
        }
    }



}
