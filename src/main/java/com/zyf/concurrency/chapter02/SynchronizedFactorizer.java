package com.zyf.concurrency.chapter02;

import com.zyf.concurrency.annotations.GuardedBy;

import javax.servlet.*;
import java.io.IOException;
import java.math.BigInteger;

/**
 * 引用原文：
 * 在同一时刻只有一个钱程可以执行server方法。现在的 SynchronizedFactorizer 是线程安全的。然而，这种方法却过于极端，因为多个客户端无法同时使用因数分解 Servlet ，服务的响应性非常低，无能令人接受。这是个性能问题，而不是线程安全问题，2.5节解决
 * create by yifeng
 */
public class SynchronizedFactorizer extends GenericServlet implements Servlet {
    @GuardedBy("this")
    private BigInteger lastNumber;
    @GuardedBy("this")
    private BigInteger[] lastFactors;


    @Override
    public synchronized void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        BigInteger i = extractFromRequest(req);
        if (i.equals(lastNumber)) {
            encodeIntoResponse(res, lastFactors);
        }
        else {
            BigInteger[] factors = factor(i);
            lastNumber = i;
            lastFactors = factors;
            encodeIntoResponse(res, factors);
        }


    }

    void encodeIntoResponse(ServletResponse resp, BigInteger[] factors) {
    }

    BigInteger extractFromRequest(ServletRequest req) {
        return new BigInteger("7");
    }

    BigInteger[] factor(BigInteger i) {
        // Doesn't really factor
        return new BigInteger[] { i };
    }
}
