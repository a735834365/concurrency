package com.zyf.concurrency.chapter03;


/**

 *
 * 在没有足够同步的情况下发布对象
 *
 * StuffInfoPublic
 * Unsafe publication
 *
 * create by yifeng
 */
public class StuffInfoPublic {
    public Holder holder;

    public void initialize() {
        holder = new Holder(42);
    }
}
