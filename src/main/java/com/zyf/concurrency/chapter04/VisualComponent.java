package com.zyf.concurrency.chapter04;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 将线程安全性委托给多个状态变量
 *
 * 引自原文：
 *      使用CopyOnWriteArrayList保存监听器列表，他是线程安全的链表，
 *      适用于管理监听器列表（5.2.3节介绍）。同时各个状态不存在耦合
 *      关系，因此可以将线程安全性委托给mouseListeners和keyListeners
 *      等对象。
 *
 * create by yifeng
 */
public class VisualComponent {
    private final List<KeyListener> keyListeners
            = new CopyOnWriteArrayList<>();
    private final List<MouseListener> mouseListeners
            = new CopyOnWriteArrayList<>();

    public void addKeyListener(KeyListener listener) {
        keyListeners.add(listener);
    }

    public void addmouseListener(MouseListener listener) {
        mouseListeners.add(listener);
    }

    public void removeMouseListener(MouseListener listener) {
        mouseListeners.remove(listener);
    }

    public void removeKeyListener(KeyListener listener) {
        keyListeners.remove(listener);
    }
}
