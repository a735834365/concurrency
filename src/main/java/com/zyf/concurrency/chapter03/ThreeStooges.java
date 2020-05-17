package com.zyf.concurrency.chapter03;

import com.zyf.concurrency.annotations.Immutable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * 引自原文：
 *      1、从ThreeStooges的设计可以看到，虽然Set对象是
 *      可变的，但在在Set对象构造完成后，无法对其修改
 *      2、ThreeStooges是一个final类型的引用变量，因此
 *      所有对象状态都通过一个final域来访问
 *      3、构造函数能使该引用由除了构造函数及其调用者之外
 *      的代码来访问
 *
 * Immutable class built out of mutable underlying objects,
 * demonstration of candidate for lock elision
 *
 * create by yifeng
 */
@Immutable
public final class ThreeStooges {
    private final Set<String> stooges = new HashSet<>();

    public ThreeStooges() {
        stooges.add("Moe");
        stooges.add("Larry");
        stooges.add("Curly");
    }

    public boolean isStooge(String name) {
        return stooges.contains(name);
    }

    public String getStoogeNames() {
        List<String> stooges = new Vector<>();
        stooges.add("Moe");
        stooges.add("Larry");
        stooges.add("Curly");
        return stooges.toString();
    }
}
