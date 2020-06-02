package com.zyf.concurrency.chapter06;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 基于线程池的Web服务器
 *引自原文：
 *      使用一个固定长度的线程池
 *      通过使用Executor，将请求任务的提交与任务的实际执行解耦开来。
 *      改变Executor的实现或配置所带来的的影响要远远小于改变任务提交方式带来的影响。
 *
 * create by yifeng
 */
public class TaskExecutionWebServer {
    private static final int NTHREADS = 100;
    private static final Executor exec
            = Executors.newFixedThreadPool(NTHREADS);

    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (true) {
            final Socket connection = socket.accept();
            Runnable task = () -> handleRequest(connection);
            exec.execute(task);
        }
    }

    private static void handleRequest(Socket connection) {
        // logic here
    }
}
