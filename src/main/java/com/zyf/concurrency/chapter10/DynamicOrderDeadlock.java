package com.zyf.concurrency.chapter10;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 动态的锁顺序死锁（不要这么做）
 * 引自原文：
 *      业务：将资金从一个账户转入另一个账户。
 *  死锁条件：
 *      A : transferMoney(myAccount, yourAccount, 10)
 *      B : transferMoney(yourAccount, myAccount, 20)
 *      A可能获得myAccount的锁并等待yourAccount的锁
 *      B此时持有yourAccount的锁，并正在等待myAccount的锁
 *
 * create by yifeng
 */
public class DynamicOrderDeadlock {

    // 容易死锁
    public static void transferMoney(Account fromAccount, Account toAccount, DollarAmount amount) throws InsufficientFundsException{
        // 转账前，获得两个Account对象锁，确保通过原子方式更新两个账户中的余额
        synchronized (fromAccount) {
            synchronized (toAccount) {
                // 避免破坏不变性条件
                if (fromAccount.getBalance().compareTo(amount) < 0)
                    throw new InsufficientFundsException();
                else {
                    // 借方
                    fromAccount.debit(amount);
                    // 信用方
                    toAccount.credit(amount);
                }
            }
        }
    }

    /**
     * 在原代码的基础上增加了一些业务代码，以便 {@link DemonstrateDeadlock} 测试
     */
    static class DollarAmount implements Comparable<DollarAmount> {
        // Needs implementation
            // 增加的业务逻辑
        int amount;

        public DollarAmount(int amount) {
            // 增加的业务逻辑
            this.amount = amount;
        }

        public DollarAmount add(DollarAmount d) {
            // 增加的业务逻辑
            amount += d.amount;
            return this;
        }

        public DollarAmount subtract(DollarAmount d) {
            // 增加的业务逻辑
            amount -= d.amount;
            return this;
        }

        public int compareTo(DollarAmount dollarAmount) {
            return 0;
        }
    }

    /**
     * 在原代码的基础上增加了一些业务代码，以便 {@link DemonstrateDeadlock} 测试
     */
    static class Account {
            // 增加的业务逻辑
        Random random = new Random(1000);
        private DollarAmount balance = new DollarAmount(random.nextInt(1000));
        private final int acctNo;
        private static final AtomicInteger sequence = new AtomicInteger();

        public Account() {
            acctNo = sequence.incrementAndGet();
        }

        void debit(DollarAmount d) {
            balance = balance.subtract(d);
        }

        void credit(DollarAmount d) {
            balance = balance.add(d);
        }

        // balance is null
        DollarAmount getBalance() {
            return balance;
        }

        int getAcctNo() {
            return acctNo;
        }
    }

    static class InsufficientFundsException extends Exception {
    }
}
