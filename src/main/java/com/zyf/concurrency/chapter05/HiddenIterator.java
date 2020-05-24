package com.zyf.concurrency.chapter05;

import com.zyf.concurrency.annotations.GuardedBy;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * 隐藏在字符串连接中的迭代操作（不要这么做）
 *
 * 引自原文：
 *      addTenThings方法可能会抛出ConcurrentModificationException，toString会对容器进行迭代。原因在于println中的set之前没有获取HiddenIterator的锁，使HiddenIterator不是线程安全的。
 *
 *
 * create by yifeng
 */
public class HiddenIterator {
    @GuardedBy("this")
    private final Set<Integer> set = new HashSet<>();

    public synchronized void add(Integer i) {set.add(i);}

    public synchronized void remove(Integer i) {set.remove(i);}

    public void addTenThings() {
        Random r = new Random();
        for (int i = 0; i < 10; i++) {
            add(r.nextInt());
        }
        // 该操作将会执行迭代操作，编译器将字符串的连接操作转换为调用StringBuilder.append(Object),而这个方法又会调用容器的toString方法，标准容器的toString方法将迭代容器，并在每个元素上调用toString来生成容器内容的格式化标识。
        System.out.println("DEBUG: added ten elements to " + set);
    }

}
