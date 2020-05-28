package com.zyf.concurrency.chapter05;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Memoizer 的最终实现
 *
 * 引自原文：
 *      Memoizer没有解决缓存逾期的问题，可以通过FutureTask的子类来解决。在子类中为每个结果指定一个逾期时间，并定期扫描缓存中逾期的元素。
 *
 *      缓存污染：某个计算被取消或者失败，那么计算这个结果时将指明计算过程被取消或者失败。
 *
 * create by yifeng
 */
public class Memoizer<A, V> implements Computable<A, V>{
    private final ConcurrentHashMap<A, Future<V>> cache
            = new ConcurrentHashMap<>();
    private final Computable<A, V> c;

    public Memoizer(Computable<A, V> c) {
        this.c = c;
    }

    @Override
    public V compute(A arg) throws InterruptedException {
        while (true) {
            Future<V> f = cache.get(arg);
            if (f == null) {
                Callable<V> eval = () -> c.compute(arg);
                FutureTask<V> ft = new FutureTask<>(eval);
                // 使符合操作保持原子性
                f = cache.putIfAbsent(arg, ft);
                if (f == null) { f = ft; ft.run(); }
            }
            try {
                return f.get();
                // 为防止缓存污染（Cache Pollution）问题，如果Memoizer返现计算被取消，那么将Future从缓存中移除。
            } catch (CancellationException e) {
                cache.remove(arg, f);
            } catch (ExecutionException e) {
                throw LaunderThrowable.launderThrowable(e.getCause());
            }
        }
    }
}
