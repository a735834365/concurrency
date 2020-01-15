* 线程安全类中封装了必要的同步机制，因此在客户端无需进一步采取同步措施
* 无状态对象一定是线程安全的 - StatelessFactorizer
* 原子性
    * 竞态条件：由于不恰当的执行时序而出现不正确的结果 - UnsafeCountingFactorizer，LazyInitRace
        “先检查后执行” 操作
    * 复合操作 - CountingFactorizer
        要保证复合操作必须是原子的
            例：读取 - 修改 - 写入 这三个操作（符合操作）是原子的
        要多实用现有的线程安全对象(如 AtomicLong)管理类的状态
* 加锁机制
    条件：当状态从1个变成多个时，线程安全状态变量并不是从1个变成多个那么简单 - UnsafeCachingFactorizer
        要保持状态的一致性，就需要在单个原子操作中，更新所有的相关状态
    * 内置锁 - SynchronizedFactorizer
        互斥体(互斥锁)










补充：
    竞态条件 容易与 数据竞争 相混淆，可看16章，了解 数据竞争