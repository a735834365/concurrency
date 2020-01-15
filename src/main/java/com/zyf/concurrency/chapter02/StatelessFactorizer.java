package com.zyf.concurrency.chapter02;

import com.zyf.concurrency.annotations.ThreadSafe;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.math.BigInteger;

/**
 * 引自原文：
 * 与大多数 Servlet 相同， StatelessFactorizer
 * 是无状态的：它既不包含任何域，也不包含任何对其
 * 他类中域的引用。计算过程中的临时状态仅存在于线
 * 程栈上的局部变量中，井且只能由正在执行的线程访
 * 问。访问 StatelessFactorizer 的线程不会影响另
 * 一个访问同一个 StatelessFactorizer 的线程的计
 * 算结果，因为这两个线程并没有共享状态，就好像它
 * 们都在访问不同的实例。由于线程访问无状态对象的
 * 行为并不会影响其他线程中操作的正确性，因此无状
 * 态对象是线程安全的。
 * 无状态对象一定是线程安全的。
 * StatelessFactorizer
 *
 * A stateless servlet
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class StatelessFactorizer extends GenericServlet implements Servlet {

    @Override
    public void service(ServletRequest req, ServletResponse resp) {
        BigInteger i = extractFromRequest(req);
        BigInteger[] factors = factor(i);
        encodeIntoResponse(resp, factors);
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