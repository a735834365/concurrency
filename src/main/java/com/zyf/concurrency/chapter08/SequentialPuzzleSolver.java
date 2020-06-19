package com.zyf.concurrency.chapter08;

import com.zyf.concurrency.annotations.Immutable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 串行的谜题解答器
 * 引自原文；
 *      其中的类型参数P和M表示位置类和移动类，以下代码示例给出了谜题框架的串行解决方案，它在你提空间中执行一个深度优先搜索。
 *
 * create by yifeng
 */
public class SequentialPuzzleSolver <P, M> {
    private final Puzzle<P, M> puzzle;
    private final Set<P> seen = new HashSet<>();

    public SequentialPuzzleSolver(Puzzle<P, M> puzzle) {
        this.puzzle = puzzle;
    }

    public List<M> solve() {
        P pos = puzzle.initialPosition();
        return search(new PuzzleNode<>(pos, null, null));
    }

    private List<M> search(PuzzleNode<P, M> node) {
        if (!seen.contains(node.pos)) {
            seen.add(node.pos);
            // 递归停止条件
            if (puzzle.isGoal(node.pos))
                return node.asMoveList();
            // 遍历所有可移动点
            for (M move : puzzle.legalMoves(node.pos)) {
                P pos = puzzle.move(node.pos, move);
                PuzzleNode<P, M> child = new PuzzleNode<>(pos, move, node);
                List<M> result = search(child);
                if (result != null)
                    return result;
            }
        }
        return null;
    }
}
