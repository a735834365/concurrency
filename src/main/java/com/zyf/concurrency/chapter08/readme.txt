8章 线程池的使用
    6章：任务执行框架
    7章：取消与关闭
    本章介绍如何对线程池进行配置与调优
8.1 在任务与执行策略之间的隐形耦合 - ThreadDeadlock
    需要明确指定执行策略的任务：
        * 依赖性任务
            大多数行为正确的任务都是独立的：它们不依赖与其他任务的执行时序、执行结果或其他效果。
        * 使用线程封闭机制的任务
            与线程池相比，单线程的Executor能够对并发性作出更强的承诺。它们能确保任务不会并发地执行，可放宽对线程安全的要求。
        * 对响应时间敏感的任务
            将一个运行时间较长的任务放入单线程的Executor中，或者将多个运行时间较长的任务提交到一个只包含少量线程的线程池中，那么将降低Executor管理的服务的响应性。
        * 使用ThreadLocal的任务
            ThreadLocal使每个线程都可以拥有某个变量的一个私有“版本”，条件Executor可以自由地重用这些线程。
            只有当线程本地值的生命周期受限于任务的生命周期时，在线程池的线程中使用ThreadLocal才有意义，而在线程池的线程中不应该使用ThreadLocal在任务之间传递值。
    只有当任务都是同类型的并且相互独立时，线程池的性能才能达到最佳。
    在一些任务中，需要拥有或排除某种特定的执行策略。如果某些任务依赖于其他的任务，那么会要求线程池足够大，从而确保它们依赖任务不会被放入等待队列中或被拒绝，而采用线程封闭机制的任务需要串行执行。将这些需求写人文档。
    8.1.1 线程饥饿死锁 - ThreadDeadlock
        在线程池中，如果任务依赖于线程池中其他任务，那么可能产生死锁。
        一个任务将另一个任务交到同一个Executor中，第二个任务停留在工作队列中，并等待第一个任务完成，而第一个任务又无法完成，因为它在等待第二个任务的完成，引发死锁。这种现象被称为线程饥饿死锁（Thread Starvation Deadlock），只要线程池中的任务需要无限期地等待一些必须由池中其他任务才能提供的资源或条件，除非线程池足够大，否则将发生线程饥饿死锁。
        每当提交了一个有依赖性的Executor任务时，要清楚地知道可能会出现线程“饥饿”死锁，因此需要在代码或配置Executor的配置文件中记录线程池的大小限制或配置限制。
    8.1.2 运行时间较长的任务
        如果任务阻塞的时间过长，即使不出现死锁，线程池的响应也会很糟糕。在平台类库的大多数可阻塞方法中，都同时定义了限时版本和无限时版本，例：
            Thread.join、BlockingQueue.put、CountDownLatch.await以及Selector.select等
        如果线程池中总是充满了被阻塞的任务，那么也可能是线程池的规模过小。
    8.2 设置线程池的大小
        对于计算密集型任务，N(cpu)个处理器的系统上，线程池最优利用率大小为N(cpu) + 1（当计算密集型的线程偶尔出现故障，额外的线程也可以确保CPU的时钟周期不会被浪费）
        对于包含I/O操作或其他阻塞操作的任务，由于线程并不会一直执行，因此线程池的规模应该更大。
        对于计算线程池的大小，给出如下定义：
            N = number of CPUs
            U = target CPU utilization，0 <= u <= 1 CPU的利用率
            w/c = ratio of wait time to compute time 等待时间与计算时间之比
        获得CPU的数目
            int N_CPUS = Runtime.getRuntime().availableProcessors()
        线程池的最优大小等于：
            N(thread) = N * U * (1 + W / C)

    8.3 配置ThreadPoolExecutor
        如果Executors中的工厂方法提供的ThreadPoolExecutor不能满足需求，则可以通过ThreadPoolExecutor的构造方法定制
        https://docs.oracle.com/javase/1.5.0/docs/api/java/util/concurrent/ThreadPoolExecutor.html#ThreadPoolExecutor(int,%20int,%20long,%20java.util.concurrent.TimeUnit,%20java.util.concurrent.BlockingQueue,%20java.util.concurrent.ThreadFactory,%20java.util.concurrent.RejectedExecutionHandler)

    8.3.1 线程的创建与销毁 - 参数Core Pool Size和Maximum Pool Size介绍
        线程池的基本大小（Core Pool Size）、最大大小（Maximum Pool Size）以及存活时间等因素共同负责线程的创建与销毁。
        有时间看看SynchronousQueue的源码。
        newFixedThreadPool工厂方法将线程的基本大小和最大大小设置为参数中指定的值，而且创建的线程池不会超时。
        newCachedThreadPool工程方法将线程池的最大大小设置为integer.MAX_VALUE，而将基本大小设置为零，并将超时设置为1分钟，这种方法创建出来的线程池可以被无限扩张，并且当需求降低时会自动收缩。

    8.3.2 管理队列任务 - 参数keepAliveTime,unit,workQueue介绍
        对于Executor，newCachedThreadPool工厂方法是一种很好的默认选择，它能提供比固定大小的线程池更好的排队性能。当需要限制当前任务的数量以满足资源管理需求时，那么可以选择固定大小的线程池。
        之所以newCachedThreadPool与固定大小线程池之间有性能差异，是由于缓存线程池使用了SynchronousQueue而不是LinkedBlockingQueue,在Java6中提供了一个新的非阻塞算法来替代SynchronousQueue，与Java5的SynchronousQueue相比，把Executor基准的吞吐量提高了3倍。

    8.3.3 饱和策略 - 参数handler介绍 - BoundedExecutor
        ThreadPoolExecutor的饱和策略可以通过调用setRejectedExecutionHandler来修改。（如果某个任务被提交到一个已被关闭的Executor时，也会用到饱和策略）
        Jdk提供的RejectedExecutionHandler实现
            AbortPolicy （终止策略）
                默认的饱和策略，该策略将抛出未检查的RejectedExecutionException。调用者可以捕获这个异常，然后根据需求编写自己的处理代码
            当提交新任务无法保存到队列中等待执行时，
                DiscardPolicy （抛弃策略）
                    会悄悄抛弃该任务
                Discard-Oldest（抛弃最旧策略）
                    会抛弃下一个将被执行的任务，然后尝试重新提交新额任务
                        如果工作队列是一个优先队列，那么“抛弃最旧的”策略将导致抛弃优先级最高的任务，因此最好不要将“抛弃最旧”的饱和策略和优先队列放在一起使用
            CallerRunsPolicy（调用者运行策略）
                该策略实现了一种调节机制，该策略既不会抛弃任务，也不会抛出异常，而是将某些任务退回到调用者，从而降低新任务的流量。

    8.3.4 线程工厂 - 参数threadFactor介绍 - MyThreadFactory，MyAppThread
        线程池创建线程会通过线程工厂，默认的线程工厂将创建新的、非守护的线程，并且不包含特殊的配置信息。
        以下以ThreadFactory接口
        https://docs.oracle.com/javase/1.5.0/docs/api/java/util/concurrent/ThreadFactory.html#method_summary
        通过制定一个线程工厂方法，可以定制线程池的配置信息
        定制Thread
        如果需要利用安全策略来控制对某些特殊代码库的访问权限，那么可以通过Executors中的privilegedThreadFactor工厂来定制自己的线程工厂。        https://docs.oracle.com/javase/1.5.0/docs/api/java/util/concurrent/Executors.html#privilegedThreadFactory()

    8.3.5 在调用构造函数后再定制 ThreadPoolExecutor
        在调用完ThreadPoolExecutor的构造函数后，仍然可以通过设置函数（Setter）来修改大多数床底给它的构造哈数的参数。
        如果Executor是通过Executors中的某个工厂方法创建的，那么可以将结果的类型转换为ThradPoolExecutor以访问设置器，如下代码片段：
            ExecutorService exec = Executors.newCachedThreadPool();
            if(exec instanceof ThreadPoolExecutor)
                ((ThreadPoolExecutor)exec).setCorePoolSize(10);
            else
                throw new AssertionError("Oops, bad assumption");
                代码片段 对通过标准工厂方法创建的Executor进行修改
        如果不想对ExecutorService进行配置，可以使用Executors中的的unconfigurableExecutorService工厂方法对现有ExecutorService进行包装，使其只暴露ExecutorService的方法。

    8.4 扩展ThreadPoolExecutor
    ThreadPoolExecutor提供几个在子类化中改写的方法，这些方法可以扩展ThreadPoolExecutor的行为
        * beforeExecute：
            可以在该方法添加日志，计时，监视或信息收集的功能
            如果beforeExecute抛出一个RuntimeException，那么任务将不会执行，afterExecute也不会被调用
        * afterExecute
            可以在该方法添加日志，计时，监视或信息收集的功能
            任务run正常返回和抛出一个除Error以外的异常，afterExecute都会被调用
        * terminated
            线程池在完成关闭操作时调用terminated
            terminated 可以用来释放Executor在其生命周期里分配的各种资源
            可以在该方法添加日志，计时，监视或收集finalize统计信息的功能

    示例：给线程池添加统计信息- TimingThreadPool
    8.5 递归算法的并行化 - TransformingSequential
        当串行循环中的各个迭代操作之间彼此独立，并且每个迭代操作执行的工作量比管理一个新任务时带来的开销更多时，那么这个串行循环就适合并行化。
        在一些递归设计中同样可以采用循环并行化的方法

    示例：谜题框架

