package com.zyf.concurrency.chapter08;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 在解决器中找不到答案
 * 解决ConcurrentPuzzleSolver的问题
 * 引自原文：
 *      解决方案1：在ValueLatch中实现一个限时的getValue（其中使用限时版本的await）
 *      解决方案2：某种特定于谜题的标准，如只搜索特定数量的位置
 *      此外还可以提供一种取消机制，有用户自己决定何时停止搜索
 *
 *
 * create by yifeng
 */
public class PuzzleSolver<P, M> extends ConcurrentPuzzleSolver<P, M>{

    public PuzzleSolver(Puzzle<P, M> puzzle) {
        super(puzzle);
    }

    // 记录所有任务的数量，当taskCount回到0时，则遍历结束
    private final AtomicInteger taskCount = new AtomicInteger(0);

    protected Runnable newTask(P p, M m, PuzzleNode<P, M> n) {
        return new CountingSolverTask(p, m, n);
    }

    class CountingSolverTask extends SolverTask {

        public CountingSolverTask(P pos, M move, PuzzleNode<P, M> prev) {
            super(pos, move, prev);
            // 每个任务执行，taskCount都会做增操作
            taskCount.incrementAndGet();
        }

        @Override
        public void run() {
            try {
                super.run();
            } finally {
                // 每个任务结束，taskCount都会做减操作
                if (taskCount.decrementAndGet() == 0) {
                    solution.setValue(null);
                }
            }
        }
    }
}
