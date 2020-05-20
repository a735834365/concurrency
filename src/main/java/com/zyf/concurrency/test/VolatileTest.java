package com.zyf.concurrency.test;

/**
 * 测试奇怪现象 摘自-敖丙
 * create by yifeng
 */
public class VolatileTest {

    public static void main(String[] args) {
        Foo f = new Foo();
        f.start();
        for (; ; ) {
            if (f.isFlag()) {
                synchronized (f) {
                    System.out.println("有点东西");
                    return;
                }
            }
        }
    }
}

class Foo extends Thread {
//    private volatile boolean flag = false;
    private volatile boolean flag = false;

    public boolean isFlag() {
        return flag;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        flag = true;
        System.out.println("flag = " + flag);
    }
}
