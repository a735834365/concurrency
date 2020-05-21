package com.zyf.concurrency.chapter04;

import com.zyf.concurrency.annotations.ThreadSafe;

import java.util.Vector;

/**
 * 扩展Vector并增加一个“若没有则添加”方法 putIfAbsent
 *
 * 引自原文：
 *      并非所有的类都想Vector那样将状态向子类公开，因此也就不适合采用这种方法
 *      “扩展”方法比直接将代码添加到类中更加脆弱，因为现在的同步策略实现被分布
 *      到多个单独维护的源代码中。
 *
 * 一句话概括：怕就怕父类修改了同步策略，坑儿子啊
 *
 * create by yifeng
 */
@ThreadSafe
public class BetterVector<E> extends Vector<E> {
    public synchronized boolean putIfAbsent(E x) {
        boolean absent = !contains(x);
        if (absent) {
            add(x);
        }
        return absent;
    }
}
