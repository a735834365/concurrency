package com.zyf.concurrency.chapter07;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 不支持关闭的生产者-消费者日志服务
 * 引自原文：
 *      这是一个多生产者单消费者(Multiple-Producer,Single-Consumer)的设计方式
 *
 * create by yifeng
 */
public class LogWriter {
    private final BlockingQueue<String> queue;
    private final LoggerThread logger;
    private static final int CAPACITY = 1000;

    public LogWriter(Writer writer) {
        this.queue = new LinkedBlockingQueue<>(CAPACITY);
        this.logger = new LoggerThread(writer);
    }

    private class LoggerThread extends Thread {
     private final PrintWriter writer;

     public LoggerThread(Writer writer) {
         this.writer = new PrintWriter(writer, true);
     }

     @Override
     public void run() {
         try {
             while (true) {
                 // 如果消费者的速度低于生产者的生成速度，那么BlockingQueue将阻塞生产者，直到日志线程有能力处理新的日志消息
                 writer.println(queue.take());
             }
         } catch (InterruptedException ignore) {
         } finally {
             writer.close();
         }
     }
 }

}