package com.zyf.concurrency.chapter03;

/**
 * 使用工厂方法来防止this引用在构造过程中溢出
 * 引自原文：
 *      如果想在构造函数中注册一个事件监听器或启动线程，那么可以使用一个私有的构造函数和一个
 *  公共的方工厂方法，从而避免不必要的构造
 *
 * create by yifeng
 */
public class SafeListener {
    private final EventListener listener;

    private SafeListener() {
        listener = e -> doSomething(e);
    }

    public static SafeListener newInstance(EventSource source) {
        SafeListener safe = new SafeListener();
        source.registerListener(safe.listener);
        return safe;
    }

    void doSomething(Event e) {}

    interface EventSource {
        void registerListener(EventListener e);
    }

    interface EventListener{
        void onEvent(Event e);
    }

    interface  Event{}
}
