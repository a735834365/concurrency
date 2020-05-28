package com.zyf.concurrency.chapter05;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static com.zyf.concurrency.chapter05.LaunderThrowable.launderThrowable;

/**
 * 使用FutureTask预加载稍后需要的数据
 *
 * 引自原文：
 *      Preloader创建了一个FutureTask，其中包含从数据库加载产品信息的任务，以及执行运算的线程
 *
 *
 *
 * create by yifeng
 */
public class Preloader{
    // 从数据库中加载产品信息
    ProductInfo loadProductInfo() throws DataLoadException {
        return null;
    }

    private final FutureTask<ProductInfo> future =
            new FutureTask<>(() -> loadProductInfo());
    
    private final Thread thread = new Thread(future);

    // 不在构造函数和静态方法中启动，这里提供一个start方法来启动线程
    public void start() {thread.start();}

    public ProductInfo get() throws DataLoadException, InterruptedException {
        try{ 
            return future.get();
            // 所有的异常最终都会通过future.get抛出，并封装到ExecutionException中，所以有可能会抛出各种异常，需要对这些异常分别处理
        } catch(ExecutionException e) {
            Throwable cause = e.getCause();
            // 最先检查已知异常
            if (cause instanceof DataLoadException)
                throw (DataLoadException) cause;
            // 再检查未知异常
            else
                throw launderThrowable(cause);
        }
    }

    interface ProductInfo{}


}

class DataLoadException extends Exception{}