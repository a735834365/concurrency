package com.zyf.concurrency.chapter06;

import java.util.concurrent.Executor;

/**
 * 为每个请求启动一个新线程的Executor
 * TaskExecutionWebServer修改为类似ThreadPerTaskWebServer的行为，为每个请求创建新线程的Executor
 *
 * create by yifeng
 */
public class ThreadPerTaskExecutor implements Executor {
    @Override
    public void execute(Runnable r) {
        new Thread(r).start();
    }
}
