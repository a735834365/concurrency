package com.zyf.concurrency.chapter04;

import javax.naming.ldap.PagedResultsControl;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * NumberRange类并不足以保护他的不变性条件（不要这么做）
 *
 * 引自原文：
 *      NumberRange不是线程安全的，没有维持对下界和上界进行约束的不变性约束。
 *      由于状态变量不是彼此独立的，因此NumberRange不能将线程安全性委托给他的
 *      线程安全状态变量。
 *      NumberRange可以通过加锁来维护不变性条件来确保线程安全性，例如使用一个锁。
 *      此外还需避免发布lower和upper，以防止客户端代码破坏其不变性。
 *
 * create by yifeng
 */
public class NumberRange {
    // 不变性条件：lower <= upper
    private final AtomicInteger lower = new AtomicInteger(0);
    private final AtomicInteger upper = new AtomicInteger(0);

    public void setLower(int i) {
        // 不安全的“先检查后执行”
        if (i > upper.get()) {
            throw new IllegalArgumentException(
                    "can't set lower to " + i + " > upper");
        }
        lower.set(i);
    }

    public void setUpper(int i) {
        // 不安全的“先检查后执行”
        if (i < lower.get()) {
            throw new IllegalArgumentException(
                    "can't set upper to " + i + " < lower");
        }
        upper.set(i);
    }

    public boolean isInRange(int i ) {
        return (i >= lower.get() && i < upper.get());
    }
}
