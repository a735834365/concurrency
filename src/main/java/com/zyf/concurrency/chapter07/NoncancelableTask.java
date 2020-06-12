package com.zyf.concurrency.chapter07;

import java.util.concurrent.BlockingQueue;

/**
 * 不可取消的任务在退出前恢复中断
 * 引自原文：
 *   对于一些不支持取消但仍可以调用可中断阻塞方法的操作，它们必须在循环中调用这些方法，并在发现中断后重新尝试。
 *   它们应该在本地保存中断状态，并在返回前恢复状态而不是在铺货InterruptedException时恢复状态
 *
 * create by yifeng
 */
public class NoncancelableTask {
    public Task getNextTask(BlockingQueue<Task> queue) {
        // 保存中断状态
        boolean interrupted = false;
        try {
            // 如果过早的设置中断状态，就会引起无限循环，因为大多数可中断的阻塞方法都会在入口处检查中断状态并且发现该状态已被设置时
            // 会立即抛出InterruptedException
            while (true) {
                try {
                    return queue.take();
                } catch (InterruptedException e) {
                    // 设置中断状态
                    interrupted = true;
                    // 重新尝试
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    interface Task{}
    BlockingQueue<Task> queue;

    /**
     * 响应中断
     *  传递异常
     * @throws InterruptedException
     */
    public Task getNextTask() throws InterruptedException {
        return queue.take();
    }

}
