package com.zyf.concurrency.chapter02;

import com.zyf.concurrency.annotations.NotThreadSafe;

/**
 * 包含了竞态条件
 * 特殊情况：
 *       如果有两个线程同时看到instance为空，则会创建两个实例
 * create by yifeng
 */
@NotThreadSafe
public class LazyInitRace {
    private ExpensiveObject instance = null;

    private ExpensiveObject getInstance() {
        if (instance == null) {
            instance = new ExpensiveObject();
        }
        return instance;
    }
}

class ExpensiveObject{}
