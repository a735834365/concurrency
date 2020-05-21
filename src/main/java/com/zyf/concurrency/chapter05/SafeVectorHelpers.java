package com.zyf.concurrency.chapter05;

import java.util.Vector;

/**
 * 在使用客户端加锁的Vector上的复合操作
 *
 * 引自原文：
 *      由于同步 容器类要遵守策略，即支持客户端加锁，因此可能会创建一些新的操作，
 *      只要我们知道应该使用哪一种锁，那么这些新操作就与容器的其他操作一样都是
 *      原子操作。同步容器类通过其自身的锁来保护它的每个方法。
 *
 *
 * create by yifeng
 */
public class SafeVectorHelpers {
    // 通过获得容器类的锁，可以使getLast和deleteLast成为原子操作
   public static Object getLast(Vector list) {
       synchronized (list) {
           int lastIndex = list.size() - 1;
           return list.get(lastIndex);
       }
   }

   public static void deleteLast(Vector list) {
       synchronized (list) {
           int lastIndex = list.size() - 1;
           list.remove(lastIndex);
       }
   }

}
