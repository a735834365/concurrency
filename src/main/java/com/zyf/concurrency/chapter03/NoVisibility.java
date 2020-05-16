package com.zyf.concurrency.chapter03;

/**
 * 因为在代码中没有使用足够的同步机制，因此无站保证主线程写入的
 * ready值和number相对读线程来说是可见的。
 *
 * create by yifeng
 */
public class NoVisibility {

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

    public static void main(String[] args) {
        new ReaderThread().start();
        number = 42;
        ready = true;
    }
}

