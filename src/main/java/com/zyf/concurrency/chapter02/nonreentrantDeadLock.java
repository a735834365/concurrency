package com.zyf.concurrency.chapter02;

import com.zyf.concurrency.annotations.NotRecommend;
import com.zyf.concurrency.annotations.Recommend;

/**
 * 重入 2.3.2
 * 内置锁(synchronized)是可重入的
 * 如果此时使用的锁不可重入，则会发生死锁
 *
 * NonreentrantDeadlock
 * <p/>
 * Code that would deadlock if intrinsic locks were not reentrant
 *
 * @author Brian Goetz and Tim Peierls
 */

class Widget {
    public synchronized void doSomething() {
    }
}


@Recommend
class LoggingWidget extends Widget {
    /**
     * 若子类修改父类的行为，则使用内置锁是可被推荐
     */
    @Override
    public synchronized void doSomething() {
        System.out.println(toString() + ": calling doSomething");
        super.doSomething();
    }
}