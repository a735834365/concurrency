package com.zyf.concurrency.chapter07;

import javax.swing.*;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 使用私有的ExecutorService，并且该Executor的生命周期受限于方法调用
 * 引自原文：
 *      如果某个方法需要处理一批任务，并且当所有任务都处理完成后才返回，那么可以通过一个私有的Executor来简化服务的生命周期管理
 *      业务：checkMail能在多台主机上并行地检查新邮件，它创建一个私有的Executor，并向每台主机提交一个任务。
 *
 * create by yifeng
 */
public class CheckForMail {
    public boolean checkMail(Set<String> hosts, long timeout, TimeUnit unit) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        // 之所以采用AtomicBoolean来代替volatile类型的boolean，是因为能从内部的Runnable中访问hasNewMail标志，因此它必须是final类型以免被修改
        final AtomicBoolean hasNewMail = new AtomicBoolean(false);
        try {
            for (String host : hosts) {
                exec.execute(() -> {
                    if (checkMail(host))
                        hasNewMail.set(true);
                });
            }
            // 所有邮件检查任务都执行完成后，关闭Executor并等待结束
        } finally {
            exec.shutdown();
            exec.awaitTermination(timeout, unit);
        }
        return hasNewMail.get();
    }




    private boolean checkMail(String host) {
        // check for mail
        return false;
    }
}
