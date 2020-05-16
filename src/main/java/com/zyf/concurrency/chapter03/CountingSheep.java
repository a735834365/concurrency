package com.zyf.concurrency.chapter03;

/**
 * 引自原文：
 * 使用volatile变量的前提条件
 *      1、对变量的写入不依赖变量的当前值，或者你能确保只有单个线程更新变量的值
 *      2、该变量不会与其他状态变量一起纳入不可变性条件中
 *      3、在访问变量时不需要加锁
 *
 * create by yifeng
 */
public class CountingSheep {

    volatile boolean asleep;

    void tryToSleep() {
        // 该volatile的语义不足以确保递增操作的原子性
        // 如不能确保countSomeSeep的原子性
        while (!asleep)
            countSomeSeep();
    }

    void countSomeSeep() {
        // One, two , three..
    }

}
