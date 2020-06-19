package com.zyf.concurrency.chapter08;

import java.util.List;
import java.util.concurrent.*;

/**
 * 并发的谜题解答器
 *
 *
 * create by yifeng
 */
public class ConcurrentPuzzleSolver <P, M> {
    private final Puzzle<P, M> puzzle;
    // 使用线程池的内部工作队列而不是调用栈来保存搜索状态
    private final ExecutorService exec;
    // 保存之前已经搜索过的位置，使用ConcurrentMap避免了更新共享集合时存在的竞态条件
    private final ConcurrentMap<P, Boolean> seen;
    final ValueLatch<PuzzleNode<P, M>> solution
            = new ValueLatch<>();

    public ConcurrentPuzzleSolver(Puzzle<P, M> puzzle) {
        this.puzzle = puzzle;
        this.exec = initThreadPool();
        this.seen = new ConcurrentHashMap<>();
        if (exec instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) exec;
            tpe.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        }
    }

    private ExecutorService initThreadPool() {
        return Executors.newCachedThreadPool();
    }

    public List<M> solve() throws InterruptedException {
        try {
            // 初始化位置
            P p = puzzle.initialPosition();
            // 开始广度优先搜索
            exec.execute(newTask(p, null, null));
            // 阻塞直到找到答案 - 问题：如果找不到答案，getValue调用将永远等下去
            PuzzleNode<P, M> solnNode = solution.getValue();
            return (solnNode == null) ? null : solnNode.asMoveList();
        } finally {
            exec.shutdown();
        }
    }

    protected Runnable newTask(P p, M m, PuzzleNode<P, M> n) {
        return new SolverTask(p, m, n);
    }

    protected class SolverTask extends PuzzleNode<P, M> implements Runnable {


        public SolverTask(P pos, M move, PuzzleNode<P, M> prev) {
            super(pos, move, prev);
        }

        @Override
        public void run() {
            // 这层判断是为了看到其他线程可能会设置的答案，同时如果遍历了该位置则不必再遍历
            if (solution.isSet()
                    || seen.putIfAbsent(pos, true) != null)
                return; // 已经找到答案或者已经遍历了这个位置
            // 是否是结果
            if (puzzle.isGoal(pos))
                solution.setValue(this);
            // 否则继续广度优先搜索
            else
                for (M m : puzzle.legalMoves(pos)) {
                    // 在线程池汇总使用新线程执行
                    exec.execute(newTask(puzzle.move(pos, m), m, this));
                }
        }
    }
}
