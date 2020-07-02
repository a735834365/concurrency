package com.zyf.concurrency.chapter03;

/**
 * 在没有同步的情况下共享变量（不要这么做）
 * 引自原文：
 *      因为在代码中没有使用足够的同步机制，因此无站保证主线程写入的
 * ready值和number相对读线程来说是可见的。
 *
 * create by yifeng
 */
public class NoVisibility {

    // volatile将保证可见性
//    private static volatile boolean ready;
    private static boolean ready;
    private static int number;

    private static class ReaderThread extends Thread {

        @Override
        public void run() {
            while (!ready)
                Thread.yield();
            //这里看起来会输出42，但事实可能输出0，或者无法终止
            System.out.println(number);
        }

    }

    public static void main(String[] args) throws InterruptedException {
        new ReaderThread().start();
        // 模拟，如果主线程执行慢了，则ReaderThread将不会停止
        Thread.currentThread().wait(1000);
        number = 42;
        ready = true;
    }
}

