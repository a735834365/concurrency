package com.zyf.concurrency.chapter03;

/**
 * 隐式地使this引用溢出
 *
 * 引自原文：
 *      当ThisEscape发布eventListener时，也隐含地发布了ThisEscape实例本身
 *  因为在这个内部类的实例中包含了对ThisEscape实例的隐含引用
 *      不要再构造过程中使用this引用溢出
 * create by yifeng
 */
public class ThisEscape {
    public ThisEscape(EventSource source){
        source.registerListener(new EventListener() {
            @Override
            public void onEvent(Event e) {
                doSomething(e);
            }
        });
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
