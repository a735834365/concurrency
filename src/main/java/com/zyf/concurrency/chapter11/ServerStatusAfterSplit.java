package com.zyf.concurrency.chapter11;

import com.zyf.concurrency.annotations.GuardedBy;

import java.util.HashSet;
import java.util.Set;

/**
 * 将ServerStatus重新改写为使用锁分段技术
 * create by yifeng
 */
public class ServerStatusAfterSplit {
    @GuardedBy
    public final Set<String> users;
    @GuardedBy
    public final Set<String> queries;

    public ServerStatusAfterSplit() {
        this.users = new HashSet<>();
        this.queries = new HashSet<>();
    }

    public void addUser(String u) {
        synchronized (users) {
            users.add(u);
        }
    }

    public void addQUery(String q) {
        synchronized (queries) {
            queries.add(q);
        }
    }

    public void removeUser(String u) {
        synchronized (users) {
            users.remove(u);
        }
    }

    public void removeQuery(String q) {
        synchronized (queries) {
            queries.remove(q);
        }
    }
}
