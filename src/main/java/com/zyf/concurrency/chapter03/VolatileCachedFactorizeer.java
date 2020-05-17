package com.zyf.concurrency.chapter03;

import com.zyf.concurrency.annotations.ThreadSafe;

import javax.servlet.*;
import java.io.IOException;
import java.math.BigInteger;

/**
 * 引自原文：
 *      当一个线程将volatile类型的cache设置为引用一个新的OneValueCache时，其他线程
 *      就会立即看到新缓存的数据。
 *      OneValueCache是不可变的，可保证每条代码路径只会访问它一次
 *      通过使用包含多个状态变量的容器对象来维持不变性条件，并使用一个volatile类型的引用
 *      来确保可见性，使得VolatileCachedFactorizeer在没有显示地使用锁的情况下仍然是
 *      线程安全的
 *
 * 使用指向不可变容器对象的volatile类型引用以缓存最新的结果
 * Caching the last result using a volatile reference to an immutable holder object
 * create by yifeng
 */
@ThreadSafe
public class VolatileCachedFactorizeer extends GenericServlet implements Servlet {

    private volatile OneValueCache cache = new OneValueCache(null, null);

    @Override
    public void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
        BigInteger i = extractFromRequest(req);
        BigInteger[] factors = cache.getFactors(i);
        if (factors == null) {
            factors = factor(i);
            cache = new OneValueCache(i, factors);
        }

        encodeIntoResponse(resp, factors);
    }

    void encodeIntoResponse(ServletResponse resp, BigInteger[] factors) {
    }

    BigInteger extractFromRequest(ServletRequest req) {
        return new BigInteger("7");
    }

    BigInteger[] factor(BigInteger i) {
        // Doesn't really factor
        return new BigInteger[]{i};
    }
}
