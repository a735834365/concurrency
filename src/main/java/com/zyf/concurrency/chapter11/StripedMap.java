package com.zyf.concurrency.chapter11;

import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;

/**
 * 基于散列的Map中使用锁分段技术
 * 引自原文：
 *      基于散列的Map实现，使用了分段锁技术，拥有N_LOCKS个锁，每个锁保护散列桶的一个子集
 * create by yifeng
 */
public class StripedMap {
    // 同步策略：buckets[n] 由 locks[n % N_LOCKS] 来保护
    private static final int N_LOCKS = 16;
    private final Node[] buckets;
    private final Object[] locks;

    private static class Node {
        Node next;
        Object key;
        Object value;
    }

    // 初始化锁
    public StripedMap(int numBuckets) {
        buckets = new Node[N_LOCKS];
        this.locks = new Object[N_LOCKS];
        for (int i = 0; i < N_LOCKS; i++) {
            locks[i] = new Object();
        }
    }

    private final int hash(Object key) {
        return Math.abs(key.hashCode() % buckets.length);
    }

    // get方法只需要获取一个锁
    private Object get(Object key) {
        int hash = hash(key);
        synchronized (locks[hash % N_LOCKS]) {
            for (Node m = buckets[hash]; m != null; m = m.next) {
                if (m.key.equals(key))
                    return m.value;
            }
            return null;
        }

    }
    // clear方法需要获得所有锁，但不要求同时获得
    public void clear() {
        for (int i = 0; i < buckets.length; i++) {
            synchronized (locks[i % N_LOCKS]) {
                buckets[i] = null;
            }
        }
    }
}
