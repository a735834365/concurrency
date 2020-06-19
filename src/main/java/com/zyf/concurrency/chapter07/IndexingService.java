package com.zyf.concurrency.chapter07;

import javax.print.attribute.standard.Finishings;
import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 通过“毒丸”对象来关闭服务
 * 引自原文：
 *      “毒丸（Poison Pill）”对象：“毒丸”是指一个放在队列上的对象，其含义是：“当得到这个对象时，立即停止”
 *      业务：单生产者-单消费者的桌面搜索实例
 *
 * create by yifeng
 */
public class IndexingService {
    private static final int CAPACITY = 1000;
    private static final File POISON = new File("");
    private final IndexerThread consumer = new IndexerThread();
    private final CrawlerThread produce = new CrawlerThread();
    private final BlockingQueue<File> queue;
    // 文件过滤规则
    private final FileFilter fileFilter;
    private final File root;


    public IndexingService(FileFilter fileFilter, File root) {
        this.root = root;
        this.queue = new LinkedBlockingQueue<>(CAPACITY);
        this.fileFilter = f -> f.isDirectory() || fileFilter.accept(f);
    }

    private boolean alreadyIndexed(File f) {
        return false;
    }

    // 生产者线程
    class CrawlerThread extends Thread {
        public void run() {
            try {
                crawl(root);
            } catch (InterruptedException e) {
                // fall through
            }finally {
                // 为什么要用while？如果put在等待时遇到中断，则还会重试，直到毒丸对象put成功
                while (true) {
                    try {
                        // 爬完之后放置一个“毒丸”对象
                        queue.put(POISON);
                        break;
//                    当用户中断生产者后，put会抛出一个中断异常，这时程序应该重试，直到put成功
                    } catch (InterruptedException e1) {
                        // retry
                    }
                }
            }
        }

        private void crawl(File root) throws InterruptedException {
            File[] entries = root.listFiles(fileFilter);
            if (entries != null) {
                for (File entry : entries) {
                    if (entry.isDirectory())
                        crawl(entry);
                    else if (!alreadyIndexed(entry))
                        queue.put(entry);
                }
            }
        }
    }

    // 消费者线程
    class IndexerThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    File file = queue.take();

//                  使用“毒丸对象关闭服务”
                    if (file == POISON)
                        break;
                    else
                        indexFile(file);
                }
            } catch (InterruptedException consumed) {
            }
        }

        public void indexFile(File file) {
            /*...*/
        }
    }

    public void start() {
        produce.start();
        consumer.start();
    }

    // 中断生产者
    public void stop() {
        produce.interrupt();
    }

    public void awaitTermination() throws InterruptedException {
        consumer.join();
    }

}
