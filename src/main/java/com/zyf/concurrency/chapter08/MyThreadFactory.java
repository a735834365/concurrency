package com.zyf.concurrency.chapter08;

import com.zyf.concurrency.expmple.MyAppThread;

import java.util.concurrent.ThreadFactory;

/**
 * 自定义的线程工厂
 * 引自原文：
 *      该范例给出了一个自定义工厂，可在线程转储和错误日志信息中区分来自不同线程池的线程（个人理解；因为自定义了线程名称），
 *      在应用程序的其他地方也可以用MyAppThread，以便所有线程都能使用它的调试功能
 *
 * create by yifeng
 */
public class MyThreadFactory implements ThreadFactory {

    private final String poolName;

    public MyThreadFactory(String poolName) {
        this.poolName = poolName;
    }

    @Override
    public Thread newThread(Runnable r) {
        // 将特定于线程池的名字传递给MyAppThread的构造函数
        return new MyAppThread(r, poolName);
    }
}
