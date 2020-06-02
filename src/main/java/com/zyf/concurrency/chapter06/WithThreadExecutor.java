package com.zyf.concurrency.chapter06;

import java.util.concurrent.Executor;

/**
 * 在调用线程中以同步方式执行所有任务的Executor
 * create by yifeng
 */
public class WithThreadExecutor implements Executor {


    @Override
    public void execute(Runnable command) {
        command.run();
    }


}
