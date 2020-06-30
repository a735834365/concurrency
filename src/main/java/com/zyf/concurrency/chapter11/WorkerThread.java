package com.zyf.concurrency.chapter11;

import java.util.concurrent.BlockingQueue;

/**
 * 对任务队列的串行访问
 *
 * create by yifeng
 */
public class WorkerThread extends Thread {
    private final BlockingQueue<Runnable> queue;

    public WorkerThread(BlockingQueue<Runnable> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Runnable task = queue.take();
                task.run();
            } catch (InterruptedException e) {
                break; // 允许线程退出
            }

        }
    }
}
