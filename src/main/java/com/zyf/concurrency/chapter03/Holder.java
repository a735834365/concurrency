package com.zyf.concurrency.chapter03;

/**
 * 引自原文：
 *      问题不在于Holder类本身，而是在于Holder类未被正确的发布。
 *  如果将n声明为final类型，那么Holder将不可变，从而避免出现不正确
 *  发布的问题
 *
 * 由于未正确发布，这个类可能出现故障
 *
 * Holder
 * <p/>
 * Class at risk of failure if not properly published
 *
 * @author Brian Goetz and Tim Peierls
 */
public class Holder {
    private int n;

    public Holder(int n) {
        this.n = n;
    }

    public void assertSanity() {
        if (n != n)
            throw new AssertionError("This statement is false.");
    }
}
