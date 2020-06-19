package com.zyf.concurrency.chapter08;

import com.zyf.concurrency.annotations.Immutable;

import java.util.LinkedList;
import java.util.List;

/**
 * 拼图解决方案的链表解决方案
 *
 * create by yifeng
 */
@Immutable
public class PuzzleNode <P, M> {
    final P pos;
    final M move;
    final PuzzleNode<P, M> prev;

    public PuzzleNode(P pos, M move, PuzzleNode<P, M> prev) {
        this.pos = pos;
        this.move = move;
        this.prev = prev;
    }

    List<M> asMoveList() {
        List<M> solution = new LinkedList<>();
        // 由于这是链表，故只需沿着Node链表逐步回溯，就可以重新构建出到达当前位置的移动序列
        for (PuzzleNode<P, M> n = this; n.move != null; n = n.prev) {
            solution.add(0, n.move);
        }
        return solution;
    }
}
