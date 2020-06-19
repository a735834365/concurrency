package com.zyf.concurrency.chapter08;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * 将串行执行转换为并行执行
 *
 * create by yifeng
 */
public abstract class TransformingSequential {

    // 串行执行
    void processSequentially(List<Element> elements) {
        for (Element e : elements) {
            process(e);
        }
    }
    public abstract void process(Element e);

    interface Element {}

    interface Node <T> {
        T compute();

        List<Node<T>> getChildren();
    }

    // 并行递归，遍历的顺序是串行的，节点的计算时串行的
    // parallelRecursive 能比 processSequentially更快的返回
    public <T> void parallelRecursive(final Executor exec,
                                      List<Node<T>> nodes,
                                      final Collection<T> results) {
        for (final Node<T> n : nodes) {
            exec.execute(() -> results.add(n.compute()));
            parallelRecursive(exec, n.getChildren(), results);
        }
    }

    public <T> Collection<T> getParallelResults(List<Node<T>> nodes) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        ConcurrentLinkedQueue<T> resultQueue = new ConcurrentLinkedQueue<>();parallelRecursive(exec, nodes, resultQueue);
//        shutdown 会取消任务的提交，并会将当前等待的任务和正在执行的任务执行完毕
        // shutdownNow会停止所有正在执行的任务，并将等待的任务返回给一个列表
        exec.shutdown();
        // 等待任务执行完成
        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        return resultQueue;
    }
}
