package com.zyf.concurrency.chapter05;

/**
 * 强制未检查的Throwable转换为RuntimeException
 * 如果Throwable是Error，那么抛出它；如果是RuntimeException，那么返回它，否则抛出IllegalStateException
 *
 * create by yifeng
 */
public class LaunderThrowable {
    public static RuntimeException launderThrowable(Throwable t) {
        if (t instanceof RuntimeException)
            return (RuntimeException) t;
        else if (t instanceof Error)
            throw (Error) t;
        else
            throw new IllegalStateException("Not unchecked", t);
    }
}
