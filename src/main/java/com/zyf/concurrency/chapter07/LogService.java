package com.zyf.concurrency.chapter07;

import com.zyf.concurrency.annotations.GuardedBy;

import java.io.*;
import java.util.concurrent.*;

/**
 * 向LogService添加可靠的取消操作
 *
 * create by yifeng
 */
public class LogService {
    private final BlockingQueue<String> queue;
    private final LoggerThread loggerThread;
    private final PrintWriter writer;
    private final int CAPACITY = 10;
    @GuardedBy("this")
    private boolean isShutdown;
    // 为什么不用volatile？volatile无法保证原子性，比如自增等操作
    @GuardedBy("this")
    private int reservations;

    public LogService(Writer writer) {
        this.queue = new LinkedBlockingQueue<>(CAPACITY);
        this.loggerThread = new LoggerThread();
        this.writer = new PrintWriter(writer);
    }

    public void start() {
        loggerThread.start();
    }

    public void stop() {
        synchronized (this) {
            isShutdown = true;
        }
        // 设置中断状态
        loggerThread.interrupt();
    }

    public void log(String msg) throws InterruptedException {
        synchronized (this) {
            // 如果关闭则抛出异常，先判断再运行
            if (isShutdown)
                throw new IllegalStateException(/*...*/);
            // 记录被阻塞的日志任务，如果队列被阻塞了，则该变量还是会继续增加，这样就不会丢失为put的日志信息
            ++reservations;
        }
        // 阻塞put,如果队列满了，则阻塞
        queue.put(msg);
    }

    private class LoggerThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    try {
                        synchronized (LogService.this) {
                            // 检查当前是否关闭，是否有阻塞的任务，如果有阻塞的任务，则继续向下执行
                            if (isShutdown && reservations == 0)
                                break;
                        }
                        String msg = queue.take();
                        synchronized (LogService.this) {
                            // 记录被阻塞的日志任务
                            --reservations;
                        }
                        writer.println(msg);
                    } catch (InterruptedException e) {
                        // 重试
                    }
                }
            } finally {
                writer.close();
            }
        }
    }
    class WriteTask extends FutureTask {



        public WriteTask(Callable callable) {
            super(callable);
        }

        public WriteTask(Runnable runnable, Object result) {
            super(runnable, result);
        }
    }

    /**
     * 使用ExecutorService的日志服务
     */
    private final ExecutorService exec = Executors.newSingleThreadExecutor();
    private final long TIMEOUT = 1000;
    public void stop2() throws InterruptedException {
        try {
            exec.shutdown();
            exec.awaitTermination(TIMEOUT, TimeUnit.MILLISECONDS);
        } finally {
            writer.close();
        }
    }
    public void log2(String msg) {
//        exec.execute(new WriteTask(msg));
    }
}