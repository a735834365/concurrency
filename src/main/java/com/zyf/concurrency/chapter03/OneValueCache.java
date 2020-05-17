package com.zyf.concurrency.chapter03;

import com.zyf.concurrency.annotations.Immutable;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * 引自原文：
 *      可考虑使用一个不可变的类包含一些数据：缓存结果和因式分解的结果
 *      对于在访问和更新多个相关变量时出现的竞争条件问题，可以通过将这些
 *      变量全部保存在一个不可变对象中来消除。（感觉这里可以参照final域的特殊语义,readme.txt中）
 *      如果是一个可变的对象，那么就必须使用锁来确保原子性
 * Immutable holder for caching a number and its factors
 *
 * create by yifeng
 */
@Immutable
public class OneValueCache {
    private final BigInteger lastNumber;
    private final BigInteger[] lastFactors;

    public OneValueCache(BigInteger lastNumber, BigInteger[] factors) {
        this.lastNumber = lastNumber;
        this.lastFactors = Arrays.copyOf(factors, factors.length);
    }

    public BigInteger[] getFactors(BigInteger i) {
        if (lastNumber == null || !lastNumber.equals(i))
            return null;
        else
            return Arrays.copyOf(lastFactors, lastFactors.length);
    }
}
