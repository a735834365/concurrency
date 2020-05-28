package com.zyf.concurrency.chapter05;

import java.math.BigInteger;

/**
 * create by yifeng
 */
public class ExpensiveFunction implements Computable<String, BigInteger> {
    @Override
    public BigInteger compute(String arg) throws InterruptedException {
        // 在经过长时间的计算后
        return new BigInteger(arg);
    }
}
