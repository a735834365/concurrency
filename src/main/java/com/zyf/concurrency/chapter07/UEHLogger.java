package com.zyf.concurrency.chapter07;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 将异常写入日志的UncaughtExceptionHandler
 *
 * create by yifeng
 */
public class UEHLogger implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Logger logger = Logger.getAnonymousLogger();
        logger.log(Level.SEVERE, "THread terminated whth exception:" + t.getName(), e);
    }
}
