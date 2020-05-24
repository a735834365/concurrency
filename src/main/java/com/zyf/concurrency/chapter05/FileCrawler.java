package com.zyf.concurrency.chapter05;

import com.zyf.concurrency.annotations.ThreadSafe;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 桌面搜索应用程序中的生产者任务和消费者任务
 *
 * 引自原文：
 *      代理程序适合被分解为生产者和消费者，如本代码例子，扫描驱动器上的文件并建立索引以便随后进行访问，类似于桌面搜索程序或Windows索引服务
 *
 *   业务：生产者：扫描驱动器上的文件将其加入到文件队列中
 *        消费者：从文件队列中取出文件并创建索引
 *   该程序的问题：
 *      消费者线程永远都不会退出-第七章提供了解决方案
 *   也可以通过Executor任务执行框架来实现，其本身也是生产者-消费者模式
 * create by yifeng
 */
public class FileCrawler implements Runnable {
    private final BlockingQueue<File> fileQUeue;
    private final FileFilter fileFilter;
    private final File root;

    public FileCrawler(BlockingQueue<File> fileQUeue, FileFilter fileFilter, File root) {
        this.fileQUeue = fileQUeue;
        this.root = root;
        // 如果是文件夹则进行深入该文件夹
        this.fileFilter = f -> f.isDirectory() || fileFilter.accept(f);
    }

    @Override
    public void run() {
        try {
            crawl(root);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // 为root目录下的所有文件创建索引
    private void crawl(File root) throws InterruptedException {
        // 提供过滤策略 fileFilter，拿到root路径下所有文件
        File[] entries = root.listFiles(fileFilter);
        if (entries != null) {
            for (File entry : entries)
                // 目录则继续爬取
                if (entry.isDirectory())
                    crawl(entry);
                // 该文件的索引是否已创建，为创建则加入文件队列中等待创建索引
                else if (!alreadyIndexed(entry))
                    fileQUeue.put(entry);
        }
    }

    // 判断是否存在文件f的索引
    private boolean alreadyIndexed(File f) {
        return false;
    }

    static class Indexer implements Runnable {
        private final BlockingQueue<File> queue;

        public Indexer(BlockingQueue<File> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                // 循环等待，从文件队列中拿出文件并创建索引
                while (true) {
                    indexFile(queue.take());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        // 创建索引
        public void indexFile(File file) {
            // index the file
        }
    }

    // 限制有限队列中文件的数量
    private static final int BOUND = 10;
    // 限制并发读文件的并发数
    private static final int N_CONSUMERS = Runtime.getRuntime().availableProcessors();

    /**
     * 启动桌面搜索
     * @param roots
     */
    public static void startIndexing(File[] roots) {
        BlockingQueue<File> queue = new LinkedBlockingQueue<>(BOUND);
        // 文件过滤规则
        FileFilter fileFilter = f -> true;
        // 存文件
        for (File root : roots) {
            new Thread(new FileCrawler(queue, fileFilter, root)).start();
        }

        // 取文件并建索引
        for (int i = 0; i < N_CONSUMERS; i++) {
            new Thread(new Indexer(queue)).start();
        }
    }



}
