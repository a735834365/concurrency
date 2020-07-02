package com.zyf.concurrency.chapter12;

/**
 * 采用基于栅栏的定时器进行测试
 * create by yifeng
 */
public class BarrierTimer implements Runnable {
    private boolean started;
    private long startTime, endTime;

    @Override
    public void run() {
        long t= System.nanoTime();
        if (!started) {
            started = true;
            startTime = t;
        } else
            endTime = t;
    }

    public synchronized void clear() {
        started = false;
    }

    public synchronized long getTime() {
        return endTime - startTime;
    }
}
