package com.zyf.concurrency.chapter05;

/**
 * 构建高效且可伸缩的结果缓存
 *
 * 引自原文：
 *      创建一个Computable包器，帮助记住之前的计算结果，并将缓存过程封装起来。（这项技术被称为记忆[Memoization]）
 *
 * create by yifeng
 */
public interface Computable<A, V> {
    V compute(A arg) throws InterruptedException;
}
