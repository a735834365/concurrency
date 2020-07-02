package com.zyf.concurrency.chapter12;

import junit.framework.TestCase;

import javax.validation.constraints.AssertFalse;

/**
 * BoundedBuffer的基本单元测试
 * 引自原文：
 *      在测试集中包含一组串行测试通常是有帮助的，因为它们有助于在开始分析数据竞争之前就找出与并发无关的问题。
 * create by yifeng
 */
public class TestBoundedBuffer extends TestCase {
   private static final long LOCKUP_DETECT_TIMEOUT = 1000;
   private static final int CAPACITY = 10000; // 书中容量为 100000
   private static final int THRESHOLD = 10000;

   public void testIsEmptyWhenConstructed() {
       SemaphoreBoundedBuffer<Integer> bb = new SemaphoreBoundedBuffer<>(10);
       assertTrue(bb.isEmpty());
       assertFalse(bb.isFull());
   }

   public void testIsFullAfterputs() throws InterruptedException {
       SemaphoreBoundedBuffer<Integer> bb = new SemaphoreBoundedBuffer<>(10);
       for (int i = 0; i < 10; i++) {
           bb.put(i);
       }
       assertTrue(bb.isFull());
       assertFalse(bb.isEmpty());
   }

    /**
     * 程序清单12-3 测试阻塞行为以及对中断的响应性
     * 引自原文
     *      业务：该线程将尝试从空缓存中获取一个元素。如果take方法成功，那么表示测试失败
     *  如果“获取”线程正确地在take方法中阻塞，那么将抛出InterruptedExecution，而捕获到这个异常的catch块将把这个异常视为测试成功，并让线程
     *  退出，然后，主测试线程会尝试与“获取”线程合并，通过调用{@code Thread.isAlive}来验证join方法是否成功返回，如果“获取”线程可以中断，
     *  那么join能很快地完成。
     * @see Thread#isAlive()
     * @see Thread#join()
     * @see TestCase#fail()
     */
   public void testTaskBlocksWhenEmpty() {
       final SemaphoreBoundedBuffer<Integer> bb = new SemaphoreBoundedBuffer<>(10);
       Thread taker = new Thread(() -> {
           try {
               Integer unused = bb.take(); // 一直阻塞，知道线程被中断，然后退出
               fail();
           } catch (InterruptedException success) {
           }
       });
       try {
           taker.start();
           Thread.sleep(LOCKUP_DETECT_TIMEOUT); // 主线程睡眠，尝试与“获取”线程合并，
           taker.interrupt();
           // 阻塞，如果taker线程可以响应中断，那么join能很快完成
           // 如果take操作由于某种意料之外的原因停滞了，那么支持限时的join方法能确保测试最终完成
           taker.join(LOCKUP_DETECT_TIMEOUT);
           assertFalse(taker.isAlive()); // 检查taker是否还存活
       } catch (Exception unexpected) {
           fail();
       }
   }

   class Big {
       double[] data = new double[10000];
   }

    /**
     * 程序清单 12-7 测试资源泄露
     * 引自原文：
     *      testLeak将多个大型对象插入到一个有界缓存中，然后再将它们移除。
     */
   // 测试泄露，takeLeak方法
   public void testLeak() throws InterruptedException {
       SemaphoreBoundedBuffer<Big> bb = new SemaphoreBoundedBuffer<>(CAPACITY);
       int heapSize1 = snapshotHeap(); // 生成堆的快照
       for (int i = 0; i < CAPACITY; i++) {
           // 将抛出 java.lang.OutOfMemoryError: Java heap space
           bb.put(new Big());
       }

       for (int i = 0; i < CAPACITY; i++) {
           bb.take();
       }

       int heapSize2 = snapshotHeap(); // 生成堆的快照
       assertTrue(Math.abs(heapSize1 - heapSize2) < THRESHOLD);
   }

   private int snapshotHeap() {
       // 快照堆和返回堆大小
       return 0;
   }
}
