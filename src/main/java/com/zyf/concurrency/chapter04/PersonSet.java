package com.zyf.concurrency.chapter04;

import com.zyf.concurrency.annotations.GuardedBy;
import com.zyf.concurrency.annotations.ThreadSafe;

import java.util.HashSet;
import java.util.Set;

/**
 * 通过封闭机制来确保线程安全
 *
 * 引自原文：
 *      HashSet不是线程安全的。但由于mySet是私有的并且不会溢出，
 *      因此HashSet被封闭在PersonSet中。只能通过addPerson与
 *      containsPerson访问mySet，因而PersonSet是线程安全的类
 *
 * create by yifeng
 */
@ThreadSafe
public class PersonSet {
    @GuardedBy
    private final Set<Person> mySet = new HashSet<>();
    public synchronized void addPerson(Person p) {
        mySet.add(p);
    }

    public synchronized boolean containsPerson(Person p) {
        return mySet.contains(p);
    }

    private interface  Person{}
}
