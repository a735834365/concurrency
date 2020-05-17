package com.zyf.concurrency.chapter03;

/**
 * 引自原文：
 *      当把一个对象传递给某个外部方法时，就相当于发布了这个对象。
 *      你无法知道哪些代码会执行，也不知道在外部方法中究竟会发布这个对象，还是会保留这个对象的引用并在随后由另一个线程使用
 * create by yifeng
 */
public class UnsafeStates {
    private String[] states = new String[] {
            "AL", "AK" , "..."
    };

    public String[] getStates() {return states;}
}
