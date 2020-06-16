package com.zyf.concurrency.chapter07;

import com.zyf.concurrency.annotations.GuardedBy;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 使用TrackingExecutorService来保存未完成的任务以备后续执行
 * 引自原文：
 *      该程序给出了TrackingExecutor的用发，网页爬虫程序的工作通常是无穷无尽的，因此当爬虫程序必须关闭时，我们通常希望保存它的状态，以便稍后重新启动。
 *      CrawTask提供了一个getPage方法，该方法能找出正在处理的页面。
 *      爬虫程序关闭时，未开始的任务和被取消的任务都将记录它们的URL
 *
 * create by yifeng
 */
public abstract class WebCrawler {
    private volatile TrackingExecutor exec;
    @GuardedBy("this")
    private final Set<URL> urlsToCrawl = new HashSet<>();

    private final ConcurrentMap<URL, Boolean> seen = new ConcurrentHashMap<>();
    private static final long TIMEOUT = 500;
    private static final TimeUnit UNIT = TimeUnit.MILLISECONDS;

    public WebCrawler(URL startUrl) {
        urlsToCrawl.add(startUrl);
    }

    public synchronized void start() {
        exec = new TrackingExecutor(Executors.newCachedThreadPool());
        for (URL url : urlsToCrawl) {
            submitCrawlTask(url);
        }
        urlsToCrawl.clear();
    }

    public synchronized void stop() throws InterruptedException {
        try {
            saveUncrawled(exec.shutdownNow());
            if (exec.awaitTermination(TIMEOUT, UNIT))
                saveUncrawled(exec.getCancelledTasks());
        } finally {
            exec = null;
        }
    }

    protected abstract List<URL> processPage(URL url);

    public void submitCrawlTask(URL u) {
        exec.execute(new CrawlTask(u));
    }

    private void saveUncrawled(List<Runnable> uncrawled) {
        for (Runnable task : uncrawled) {
            urlsToCrawl.add(((CrawlTask)task).getPage());
        }
    }

    private class CrawlTask implements Runnable {
        private final URL url;

        public CrawlTask(URL url) {
            this.url = url;
        }

        private int count = 1;

        boolean alreadyCrawled() {
            return seen.putIfAbsent(url, true) != null;
        }

        void markUncrawled() {
            seen.remove(url);
            System.out.printf("marking %s uncrawled%n", url);
        }

        @Override
        public void run() {
            for (URL link : processPage(url)) {
                // 如果被中断了，则取消爬取任务
                if (Thread.currentThread().isInterrupted())
                    return;
                submitCrawlTask(link);
            }
        }
        // 该方法可以找出正在处理的也页面
        public URL getPage() {
            return url;
        }
    }

}
