package com.zyf.concurrency.chapter07;

import com.zyf.concurrency.annotations.GuardedBy;
import com.zyf.concurrency.annotations.ThreadSafe;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 使用volatile 类型的域来保存取消状态
 *
 * 引自原文：
 *      在Java中没有一种安全的抢占式方法来停止线程。只有一些协作式机制，使请求取消的任务和代码都遵循一种协商好的协议
 *      其中一种协作机制能设置某个“已请求取消(Cancellation Requested)”标志，而任务将定期地查看该标志，如果设置了该标志，那么任务将
 *   提前结束。
 *
 * create by yifeng
 */
@ThreadSafe
public class PrimeGenerator implements Runnable {

    private static ExecutorService exec = Executors.newCachedThreadPool();

    @GuardedBy("this")
    private final List<BigInteger> primes
            = new ArrayList<>();
    // 为了使标志可靠，必须使用volatile类型
    private volatile boolean cancelled;

    @Override
    public void run() {
        BigInteger p = BigInteger.ONE;
        while (!cancelled) {
            // 获取下一个素数
            p = p.nextProbablePrime();
            synchronized (this){
                primes.add(p);
            }
        }
    }

    public void cancel() {
        cancelled = true;
    }

    public synchronized List<BigInteger> get() {
        return new ArrayList<>(primes);
    }

    static List<BigInteger> aSecondOfPrimes() throws InterruptedException {
        PrimeGenerator generator = new PrimeGenerator();
        exec.execute(generator);
        try {
            SECONDS.sleep(1);
        }finally {
            generator.cancel();
        }
        return generator.get();
    }
}
