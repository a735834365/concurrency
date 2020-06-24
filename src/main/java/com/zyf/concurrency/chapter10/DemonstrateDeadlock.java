package com.zyf.concurrency.chapter10;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

import static com.zyf.concurrency.chapter10.DynamicOrderDeadlock.*;

/**
 * 在典型条件下会发生死锁的循环
 * 引发动态锁顺序死锁
 * create by yifeng
 */
public class DemonstrateDeadlock {
    private static final int NUM_THREADS = 20;
    private static final int NUM_ACCOUNTS = 5;
    // 迭代次数
    private static final int NUM_ITERATIONS = 100000;

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1);
        final Random rnd = new Random();
        final Account[] accounts = new Account[NUM_ACCOUNTS];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account();
        }
        
        class TransferThread extends Thread {
            @Override
            public void run() {
                // 100000次顺序转账
                for (int i = 0; i < NUM_ITERATIONS; i++) {
                    try {
                        latch.await();
                    } catch (Exception ignore) {
                    }
                    int fromAcct = rnd.nextInt(NUM_ACCOUNTS);
                    int toAcct = rnd.nextInt(NUM_ACCOUNTS);
                    DollarAmount amount = new DollarAmount(rnd.nextInt(1000));
                    try {
                        transferMoney(accounts[fromAcct], accounts[toAcct], amount);
                    } catch (InsufficientFundsException ignore) {
                    }
                }
            }
        }
        // 20个转账线程并发执行
        for (int i = 0; i < NUM_THREADS; i++) {
            new TransferThread().start();
        }
        latch.countDown();
    }

}
