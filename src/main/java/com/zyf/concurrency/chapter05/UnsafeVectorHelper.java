package com.zyf.concurrency.chapter05;

import java.util.Vector;

/**
 * Vector上可能导致混乱结果的混合操作
 *
 * 引自原文：
 *      A --> size->10 -->          --> get(9) --> 出错
 *      B --> size->10 --> remove(9)
 *      如果A在包括10个元素的Vector上调用getLast，同时线程B在同一个
 *      Vector上调用deleteLast，这些操作的交替执行如上图，egtLast
 *      将会抛出ArrayIndexOutOfBoundsException 异常。
 *      这虽然遵守了Vector的规范，但是这个是getLast的调用者所希望的结果。
 *
 * create by yifeng
 */
public class UnsafeVectorHelper {
    public static Object getLast(Vector list) {
        int lastIndex = list.size() - 1;
        return list.get(lastIndex);
    }

    public static void deleteLast(Vector list) {
        int lastIndex = list.size() - 1;
        list.remove(lastIndex);
    }
}
