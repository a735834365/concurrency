package com.zyf.concurrency.chapter06;

import java.util.*;
import java.util.concurrent.*;

/**
 * 在预订时间内请求旅游报价
 *
 * 引自原文：
 *      业务：
 *          获取不同公司报价过程：可能调用web服务、访问数据库，执行事务等。
 *          不应该让页面的响应时间受限于最慢的响应时间，而应该只显示指定时间内手动的信息
 *          对于没有及时响应的服务提供者，页面可以忽略它们
 *
 * create by yifeng
 */
public class TimeBudget {
    private static ExecutorService exec = Executors.newCachedThreadPool();

    public List<TravelQuote> getRankedQuotes(TravelInfo travelInfo, Set<TravelCompany> companies,
                                             Comparator<TravelQuote> ranking, long time, TimeUnit unit) throws InterruptedException {
        List<QuoteTask> tasks = new ArrayList<>();
        // 拿到所有报价任务
        for (TravelCompany company : companies) {
            tasks.add(new QuoteTask(company, travelInfo));
        }

        // 执行报价任务
        // invokeAll按照任务集合中迭代器的顺序将所有Future添加到返回的集合中，从而使调用者能将各个Future与其表示的Callable关联起来
        // java.util.concurrent.AbstractExecutorService.invokeAll
        List<Future<TravelQuote>> futures = exec.invokeAll(tasks, time, unit);

        List<TravelQuote> quotes =
                new ArrayList<>(tasks.size());
        Iterator<QuoteTask> taskIter = tasks.iterator();
        for (Future<TravelQuote> future : futures) {
            QuoteTask task = taskIter.next();
            try {
                // 可以调用 get 或 isCancelled判断是否正常完成或取消
                quotes.add(future.get());
                // 添加错误的任务
            }catch (ExecutionException e) {
                quotes.add(task.getFailureQuote(e.getCause()));
            } catch (CancellationException e) {
                quotes.add(task.getTimeuotQuote(e));
            }
        }
        Collections.sort(quotes, ranking);
        return quotes;
    }

}

// 报价任务
class QuoteTask implements Callable<TravelQuote> {
    private final TravelCompany company;
    private final TravelInfo travelInfo;

    public QuoteTask(TravelCompany company, TravelInfo travelInfo) {
        this.company = company;
        this.travelInfo = travelInfo;
    }

    TravelQuote getFailureQuote(Throwable t) {
        return null;
    }

    TravelQuote getTimeuotQuote(CancellationException e) {
        return null;
    }

    @Override
    public TravelQuote call() throws Exception {
        return company.solicitQuote(travelInfo);
    }
}

// 旅游公司
interface TravelCompany {
    // 报价
    TravelQuote solicitQuote(TravelInfo travelInfo) throws Exception;
}

// 旅游报价
interface TravelQuote {
}
// 旅游信息
interface TravelInfo {
}

