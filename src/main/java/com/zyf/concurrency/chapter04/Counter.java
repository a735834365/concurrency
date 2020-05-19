package com.zyf.concurrency.chapter04;

import com.zyf.concurrency.annotations.GuardedBy;
import com.zyf.concurrency.annotations.ThreadSafe;

/**
 * 使用Java监视器模式的线程安全计数器
 *
 * 引自原文：
 *      Counter中只有一个域value，因此这个域就是Counter的全部
 *      状态，对于含有n个基本类型域的对象，其状态就是这些域构成
 *      的n元组。（个人理解：如果域中存在对象，则域中对象里的
 *      状态就是元组-类似HashMap，元组就是key对应的value）
 *
 * create by yifeng
 */
@ThreadSafe
public class Counter {
    @GuardedBy
    private long value = 0;

    public synchronized long getValue() {
        return value;
    }

    public synchronized long increment() {
        if (value == Integer.MAX_VALUE) {
            throw new IllegalStateException("counter overflow");
        }
        return ++value;
    }
}
