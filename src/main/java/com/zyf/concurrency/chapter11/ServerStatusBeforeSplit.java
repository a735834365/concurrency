package com.zyf.concurrency.chapter11;

import com.sun.javafx.logging.JFRInputEvent;
import com.zyf.concurrency.annotations.GuardedBy;

import java.util.HashSet;
import java.util.Set;

/**
 * 对锁进行分解之前
 * 引自原文：
 *      业务：该范例给出了某个数据库服务器的部分监视接口，该数据库维护了当前已登录的用户以及正
 *  在执行的请求，当一个用户登录、注销、开始查询或结束查询时，都会调用相应的add和remove等方法
 *  来更新ServerStatus对象。
 *      这两种类型的信息是完全独立的，ServerStatus甚至可以被分解为两个类，同时确保不会丢失功
 *  能。
 *      缺陷：
 *          在代码中不是用 ServerStatusBeforeSplit 对象锁来保护用户状态和查询状态，而是每个状态都通过一个锁
 *      来保护
 * create by yifeng
 */
public class ServerStatusBeforeSplit {
    @GuardedBy
    public final Set<String> users;
    @GuardedBy
    public final Set<String> queries;

    public ServerStatusBeforeSplit() {
        this.users = new HashSet<>();
        this.queries = new HashSet<>();
    }

    public synchronized void addUser(String u) {
        users.add(u);
    }

    public synchronized void addQUery(String q) {
        queries.add(q);
    }

    public synchronized void remoevUser(String u) {
        users.remove(u);
    }

    public synchronized void removeQuery(String q) {
        queries.remove(q);
    }
}
