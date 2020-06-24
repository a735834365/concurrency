package com.zyf.concurrency.chapter10;

/**
 * 通过锁顺序来避免死锁
 * 引自原文：
 *      如果Account包含一个唯一的不可变的并且具备可比性的键值，例如账号，那么要指定锁的顺序
 *  就更加容易了:通过键值对对象进行排序，因而不需要使用”加时赛“锁。
 * 个人理解：
 *      如果主键是UUID生成的String类型，那么需要使用compareTo进行对比，还是需要“tie-Breaking”锁
 *      如果主键是自增的，那么直接比较即可
 *
 *
 * create by yifeng
 */
public class InduceLockOrder {

    private static final Object tieLock = new Object();

    public void transferMoney(final Account fromAcct,
                              final Account toAcct,
                              final DollarAmount amount)
            throws InsufficientFundsException {
        class Helper {
            private void transfer() throws InsufficientFundsException {
                if (fromAcct.getBalance().compareTo(amount) < 0)
                    throw new InsufficientFundsException();
                else {
                    fromAcct.debit(amount);
                    toAcct.credit(amount);
                }
            }
        }
        // 使用hash值来定义锁顺序
        int fromHash = System.identityHashCode(fromAcct);
        int toHash = System.identityHashCode(toAcct);

        // 通过以下判断，总是能锁住hash值较小的对象，也就预防了DynamicOrderDeadlock出现的动态顺序死锁
        if (fromHash < toHash) {
            synchronized (fromAcct) {
                synchronized (toAcct) {
                    new Helper().transfer();
                }
            }
        } else if (fromHash > toHash) {
            synchronized (toAcct) {
                synchronized (fromAcct) {
                    new Helper().transfer();
                }
            }
        // 如发生hash冲突，则使用对象锁, 不过hash冲突是极少数的情况
        // 书中定义为 tie-Breaking,书中翻译为加时赛，google翻译为决胜局
        } else {
            synchronized (tieLock) {
                synchronized (fromAcct) {
                    synchronized (toAcct) {
                        new Helper().transfer();
                    }
                }
            }
        }
    }

    // 金额
    interface DollarAmount extends Comparable<DollarAmount> {
    }
    // 账户
    interface Account {
        void debit(DollarAmount d);

        void credit(DollarAmount d);

        DollarAmount getBalance();

        int getAcctNo();
    }

    // 金额不足异常
    class InsufficientFundsException extends Exception {
    }
}
