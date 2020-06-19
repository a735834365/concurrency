package com.zyf.concurrency.chapter06;

import java.util.concurrent.*;

/**
 * 在指定时间内获取广告信息
 *
 * create by yifeng
 */
public class RenderWithTimeBudget {
    // 默认广告
    private static final Ad DEFAULT_AD = new Ad();
    // 超时时间
    private static final long TIME_BUDGET = 1000;
    // 缓存线程池 - 初始化容量为0，如果线程池当前规模超过处理需求的线程数，则会回收空闲线程，
    // 需求增加时，则可以添加新线程，线程池的规模不存在限制
    private static final ExecutorService exec = Executors.newCachedThreadPool();

    Page renderPageWithAd() throws InterruptedException{
        long endNanos = System.nanoTime() + TIME_BUDGET;
        Future<Ad> f = exec.submit(new FetchAdTask());
        // 在等待广告的同时显示页面
        Page page = renderPageBody();
        Ad ad;
        try {
            // 只等待指定的时间长度
            // timeout参数的计算方法是将指定时限减去当前时间，这可能会得到负数
            // 当java.util.concurrency中所有与时限相关的方法都将负数视为零，
            // 因此不需要额外的代码来处理这种情况
            long timeLeft = endNanos - System.nanoTime();
            // 设置等待时间限制，时间单位为微秒
            ad = f.get(timeLeft, TimeUnit.NANOSECONDS);
        } catch (ExecutionException e) {
            ad = DEFAULT_AD;
            // 超时设置默认广告并取消该任务的执行
        } catch (TimeoutException e) {
            ad = DEFAULT_AD;
            f.cancel(true);
        }
        page.setAd(ad);
        return page;
    }

    // 渲染主页面
    Page renderPageBody() { return new Page(); }

    // 广告
    static class Ad {
    }

    // 加载页面
    static class Page {
        public void setAd(Ad ad) { }
    }

    // 广告任务
    static class FetchAdTask implements Callable<Ad> {
        public Ad call() {
            return new Ad();
        }
    }
}
