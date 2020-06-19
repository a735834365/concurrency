package com.zyf.concurrency.chapter08;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 定制Thread基类
 * 引自原文：
 *      定制行为包括：
 *          为线程指定名字
 *          设置自定义UncaughtExceptionHandler
 *              向logger中写入日志
 *              维护统计信息（包括有多少线程被创建和销毁）
 *          在线程被创建或者终止时把调试信息写入日志
 *
 * create by yifeng
 */
public class MyAppThread extends Thread{
    public static final String DEFAULT_NAME = "MyAppThread";
    private static volatile boolean debugLifecycle = false;
    // 已创建的线程数
    private static final AtomicInteger created = new AtomicInteger();
    // 正在运行的线程数
    private static final AtomicInteger alive = new AtomicInteger();
    private static final Logger log = Logger.getAnonymousLogger();

    public MyAppThread(Runnable target) {
        this(target, DEFAULT_NAME);
    }

    public MyAppThread(Runnable target, String name) {
        // 已经创建线程的数量
        super(target, name + "-" + created.incrementAndGet());
        setUncaughtExceptionHandler((t, e) -> log.log(Level.SEVERE, "UNCAUGHT in thread " + t.getName(), e));
    }

    @Override
    public void run() {
        // 复制debug标志以确保一致的值
        boolean debug = debugLifecycle;
        if (debug) log.log(Level.FINE, "Created" + getName());
        try {
            alive.incrementAndGet();
            super.run();
        } finally {
            alive.decrementAndGet();
            if (debug) log.log(Level.FINE, "Exiting" + getName());
        }
    }

    public static int getThreadsCreated() {
        return created.get();
    }

    public static int getThreadsAlive() {
        return alive.get();
    }

    public static boolean getDebug() {
        return debugLifecycle;
    }

    public static void setDebugLifecycle(boolean b) {
        debugLifecycle = b;
    }
}
