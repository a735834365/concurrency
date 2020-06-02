第二部分 结构化开发应用程序
6 任务执行

6.1.1 串行地执行任务 - SingleThreadWebServer

6.1.2 显式地为任务创建线程 - ThreadPerTaskWebServer
    为每一个请求启动一个新的线程
    * 任务处理过程从主线程中分离出来，使得主循环能够更快地重新等待下一个到来的连接。提高响应性
    * 任务可以并行处理，从而能同时服务多个请求
    * 任务代码必须是线程安全的，因为当有多个任务时会并发地调用这段代码

    只要请求的到达速率不超出服务器的请求处理能力，就可以同时带来更快的响应性和更快的吞吐率

6.1.3 无限制创建线程的不足
    为每一个请求启动一个新的线程的缺陷：
    * 线程生命周期的开销非常高
        线程的创建与销毁是有代价的
    * 资源消耗
        活跃的线程会消耗系统资源，尤其是内存。如果可运行的线程数量多于可用处理器的数量，那么有些线程将闲置。空闲线程会占用内存，为垃圾回收器带来压力，线程竞争CPU资源会产生其他性能开销。足够的线程使CPU忙碌，但过多的线程会降低性能
    * 稳定性
        在可创建线程的数量上存在一个限制。破坏了限制可能会抛出OutOfMemoryError异常

6.2 Executor框架
    任务时一组逻辑工作单元，而线程则是使任务异步执行的机制。
    在Java类库中，任务执行的主要抽象不是Thread，而是Executor。
    Executor基于生产者-消费者模型
        提交任务的操作相当于生产者（生成待完成的工作单元）
        执行任务的线程则相当于消费者（执行完这些工作单元）

    功能：
        生命周期的支持
        统计信息收集
        应用程序管理机制
        性能监视等

6.2.1 实例：基于Executor的Web服务器 -TaskExecutionWebServer，ThreadPerTaskExecutor，WithThreadExecutor
    基于Executor来构建Web服务器是非常容易的。

6.2.2 执行策略
    执行任务中定义了任务执行的“What、Where、When、How”等方面，包括：
    * 在什么（What）线程中执行？
    * 任务按照什么（What）顺序执行（FIFO、LIFO、优先级）？
    * 有多少个（How Many）任务能并发执行？
    * 在队列中有多少个（How Many）任务在等待执行？
    * 如果系统由于过载而需要拒绝一个任务，那么应该选择哪一个（Which）任务？另外，如何（How）通知应用程序有任务被拒绝。
    * 在执行一个任务之前或之后，应该进行哪些（What）动作？
    通过将任务的提交与任务的执行策略分离开来，有助于在部署阶段选择与可用硬件资源最匹配的执行策略。

6.2.3 线程池
    字面含义：管理一组同构工作线程的资源池。
    线程池与工作队列（Work Queue）相关，在工作队列中保存了所有等待执行的任务
    工作者线程（Work Thread）：从工作队列中获取一个任务，执行任务，然后返回线程池并等待下一个任务

    在线程池中执行任务 比 为每个任务分配一个线程 优势更多：
    1、重用现有线程而不是创建新线程，这样可分摊线程创建和销毁过程中产生的开销。
    2、请求到达，工作线程已经存在，省略了等待线程创建的时间，提高响应性。
    3、调整线程池的大小，创建足够多的线程使处理器保存忙碌，同时防止多线程相互竞争资源而使应用程序耗尽内存或失败。
        尽管服务器不会因为过多的线程而失败，但在足够长的时间内，如果任务到达的速度总是超过任务执行的速度，那么服务器仍有可能（只是更不易）
    耗尽内存，因为等待执行的Runnable队列将不断增长。可以通过使用一个有界工作队列Executor框架内部解决这个问题(详见8.3.2)
    类库：Executors中的静态方法详解
        1、newFixedThreadPool
            创建固定长度的线程池，每提交一个任务就创建一个线程，直到到达线程池的最大数量，这是线程池的规模将不再变化（如果某线程因Exception二结束，线程池会补充一个新的线程）
            TaskExecutionWebServer使用的该线程池
        2、newCachedThreadPool
            创建一个可缓存的线程池，如果线程池的当前规模超出了处理需求时，那么将回收空闲的线程，需求增加时，则可以添加新的线程，线程池的规模不存在任何限制
        3、newSingleThreadExecutor
            单线程的Executor，如果工作者线程异常结束，会创建另一个线程替代。能确保任务按照顺序串行执行（如：FIFO、LIFO、优先级）
        4、newScheduledThreadPool
            固定长度的线程池，而且以延迟或定时的方式执行任务，类似Timer（6.2.5）

    可通过Executor，实现各种调优、管理、监视、记录日志、错误报告和其他功能。


6.2.4 Executor的生命周期 - LifecycleWebServer
    Executor以异步的方式执行任务，在任何时刻，有的任务可能已经完成，有的正在运行，其他任务正在等待执行。
    平缓的关闭形式：完成所有已经启动的任务，并不接受新任务
    粗暴的关闭形式：直接关掉机房的电源
    其他的可能形式...
    Executor扩展ExecutorService接口，添加用于生命周期管理的方法和用于任务提交的便利方法。以下是ExecutorService文档
    https://docs.oracle.com/javase/1.5.0/docs/api/java/util/concurrent/ExecutorService.html#method_summary

    Executor生命周期的三种状态
    运行：初始创建处于运行状态
    关闭：shutdown执行平缓的关闭过程
            不接受新任务，等待已提交的任务执行完成-包括未开始执行的任务。
         shutdownNow执行粗暴的关闭过程
            尝试取消所有运行中的任务，并不再启动尚未开始执行的任务。
    已终止：所有任务都完成后，ExecutorService将转入终止状态，
           可调用awaitTermination来等待ExecutorService到达终止状态，
           或者调用isTerminated来轮询是否已经终止（第七章结束Executor的关闭，任务取消等）

    拒绝执行处理器（Rejected execution Handler）
        他会抛弃任务，或者使得execute方法抛出未检查的RejectedExecutionException
        ExecutorService关闭后提交的任务由拒绝执行处理器来处理（参加 8.3.3）

6.2.5 延迟任务与周期
    Timer类负责管理延迟任务（”100ms后执行“）以及周期任务（”没10ms执行一次该任务“）
    Timer支持基于绝对时间的调度机制，ScheduledThreadPoolExecutor支持基于相对时间的调度
    Timer的缺陷：
        Timer执行定时任务时只会创建一个线程（记住Timer是基于绝对时间的调度机制）
            任务A执行40ms
            任务B每10ms执行一次
            以上情况，在任务A执行完成后，任务B会丢失4次调用，线程池可以弥补这个缺陷，它提供多个线程执行延时任务和周期任务
        Timer不捕获异常 - 可看代码 OutOfTime
            当TimerTask抛出未检查异常将终止线程。这样，已被调度但尚未执行的TimerTask将不会执行，新的任务也不能被调度（线程泄露[Thread Leakage], 7.3介绍该问题）

    构建自己的调度服务可以使用DelayQueue，它实现BlockingQueue，并未ScheduleThreadPoolExecutor提供调度服务

6.3 找出可利用的并行性 - OutOfTime

6.3.1 实例：串行的页面渲染器 - SingleThreadRenderer

6.3.2 携带结果的任务 Callable 与 Future - OverrideNewTaskFor
    Runnable优点：
        能写入日志文件
        将结果放入某个共享的数据结构
    Runnable（翻译：可运行的）和Callable（翻译：可召回的）的区别：
        Runnable不能返回或抛出一个受检查的异常而Callable可以

    Callable接口：
        https://docs.oracle.com/javase/1.5.0/docs/api/java/util/concurrent/Callable.html#method_summary
    Future接口
        表示一个任务的生命周期，并提供了方法用于判断是否已经完成或取消以及获取任务的结果和取消任务
        get方法：
            该方法的行为取决于任务的状态（尚未开始、正在运行、以完成）。
            任务已完成：get会立即返回或者抛出一个Exception
            任务未完成：get将阻塞并直到任务完成
            任务抛出了异常：get将异常封装为ExecutionException并重新抛出。如果四任务被取消，那么get将抛出CancellationException，如果get抛出了ExecutionException，那么可以通过getCause来获得被封装的初始异常。

        创建方式：
            ExecutorService中所有submit方法都返回一个Future，用来获得任务的执行结果或取消任务
            可显示的只读Runnable或Callable实例化一个FutureTask
                （FutureTask实现了Runnable，也可以将FutureTask提交给Executor执行或者直接调用run方法。）

    将Runnable或Callable提交到Executor的过程中包含了一个安全发布过程（参见3.5节），即将Runnable或Callable从提交线程发布到最终执行任务的线程。设置Future德国的过程也包含了一个安全发布，即将这个结果从计算它的线程发布到任何通过get获得它的线程。

        https://docs.oracle.com/javase/1.5.0/docs/api/java/util/concurrent/Future.html#method_summary

