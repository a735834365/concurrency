package com.zyf.concurrency.chapter04;

import com.zyf.concurrency.annotations.Immutable;

/**
 * 引自原文：
 *      不可变的Point代替MutablePoint，
 *
 * 在DelegatingVehicleTracker中使用的不可变Point类
 *
 * create by yifeng
 */
@Immutable
public class Point {
    private final int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
