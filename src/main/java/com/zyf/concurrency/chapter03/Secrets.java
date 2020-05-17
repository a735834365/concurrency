package com.zyf.concurrency.chapter03;

import java.util.HashSet;
import java.util.Set;

/**
 * 引自原文：
 *      发布一个对象最简单的形式就是将对象的引用保存到一个公有域的静态变量中
 *以便任何类和对象都能看到该对象
 * publishing an object
 * create by yifeng
 */
public class Secrets {
    static Set<Secret> knownSecrets;

    public void initialize() {
        knownSecrets = new HashSet<>();
    }
}

class Secret{}