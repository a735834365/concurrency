package com.zyf.concurrency.chapter10;

import java.util.concurrent.CountDownLatch;

/**
 * 简单的锁顺序死锁（不要这么做）
 * 引自原文：
 *      A --> 锁住left --> --> 尝试锁住right --> 永久等待
 *      B --> --> 锁住right --> --> 尝试锁住left --> 永久等待
 *             LeftRightDeadlock中的不当执行时机
 *
 * create by yifeng
 */
public class LeftRightDeadlock {
    private final Object left = new Object();
    private final Object right = new Object();
    // 容易发生死锁
    public void leftRight() {
        synchronized(left) {
            synchronized(right) {
                dosomething();
            }
        }
    }
    // 容易发生死锁
    public void rightLeft() {
        synchronized(right) {
            synchronized(left) {
                doSomethingElse();
            }
        }
    }

    void dosomething() {}

    void doSomethingElse() {}

    public static void main(String[] args) throws InterruptedException {
        // 使用闭锁可以模拟极端情况
        CountDownLatch cdt = new CountDownLatch(1);

        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                new Thread(() -> {
                    try {
                        LeftRightDeadlock leftRightDeadlock = new LeftRightDeadlock();
                        cdt.await();
                        leftRightDeadlock.leftRight();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            if (i == 1) {
                new Thread(() -> {
                    try {
                        LeftRightDeadlock leftRightDeadlock = new LeftRightDeadlock();
                        cdt.await();
                        leftRightDeadlock.rightLeft();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();

            }
        }
        Thread.sleep(1000);
        cdt.countDown();
    }
}
