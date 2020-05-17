package com.zyf.concurrency.chapter03;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 使用ThreadLocal来维持线程封闭性
 *
 * 引自原文：
 *      由于JDBC的连接对象不一定是线程安全的，因此，当多个
 *      线程应用程序在没有协同的情况下使用全局变量时，就不是
 *      线程安全的，通过将JDBC的连接保存到ThreadLocal对象
 *      中，每个线程都会拥有属于自己的连接
 *
 * create by yifeng
 */
public class ConnectionDispenser {
    static String DB_URL = "jdbc:mysql://localhost/mydatabase";

    private ThreadLocal<Connection> connectionHolder
            = new ThreadLocal() {
        public Connection initialValue() {
            try {
                return DriverManager.getConnection(DB_URL);
            } catch (SQLException e) {
                throw new RuntimeException("Unable to acquire Connection, e");
            }
        }
    };

    public Connection getConnection() {
        return connectionHolder.get();
    }
}
