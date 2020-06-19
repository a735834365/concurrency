package com.zyf.concurrency.chapter08;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 在单线程Executor中任务发生死锁（不要这么做）
 * 引自原文；
 *      业务：提交两个任务来获取网页的页眉和页脚，绘制页面，等待获取页眉和页脚任务的结果，然后将页眉、页面主题和页脚组合起来并形成最终的页面。
 *
 *
 * create by yifeng
 */
public class ThreadDeadlock {
    // 使用单线程的Executor或线程池不够大，将导致线程饥饿死锁
    ExecutorService exec = Executors.newSingleThreadExecutor();

    public class LoadFileTask implements Callable<String> {

        private final String fileName;

        public LoadFileTask(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public String call() throws Exception {
            // Here's where we would actually read the file
            return "";
        }
    }

    public class RenderPageTask implements Callable<String> {

        @Override
        public String call() throws Exception {
            // TODO 不是很理解为什么会死锁
            Future<String> header, footer;
            header = exec.submit(new LoadFileTask("header.html"));
            footer = exec.submit(new LoadFileTask("footer.html"));
            String page = readerBody();
            // Will deadlock -- task waiting for result of subtask
            // 将死锁，任务等待子任务的结果
            return header.get() + page + footer.get();
        }

        private String readerBody() {
            // Here's where we would actually reader the page
            return "";
        }
    }


}
