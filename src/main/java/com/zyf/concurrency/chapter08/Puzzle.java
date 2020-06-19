package com.zyf.concurrency.chapter08;

import java.util.Set;

/**
 * “搬箱子”之类谜题的抽象类
 *
 * create by yifeng
 */
public interface Puzzle<P, M> {
    // 初始位置
    P initialPosition();
    // 目标位置
    boolean isGoal(P position);
    /**
     * 用于判断是否是有效移动的规则集
     * 1、计算从指定位置开始的所有合法移动
     * 2、每次移动的结果位置
     * 返回所有合法的位置
     */
    Set<M> legalMoves(P position);
//    移动，返回移动后的位置
    P move(P position, M move);
}
