5 基础构建模块
    5.1 同步容器类
        同步容器类有Vector和Hashtable
        同步的封装器类 Collections.synchronizedXxx等工厂方法
            实现方式是：将他们的状态封装起来，并对每个公有方法都进行同步，使得每次只有一个线程能访问容器的状态

    5.1.1 同步容器类的问题 - SafeVectorHelpers，UnsafeVectorHelper
        同步容器类都是线程安全的，但是在某些情况下需要额外的客户端加锁来保护符合操作。比如“先检查后执行”
        可能会抛出ArrayIndexOutOfBoundsException的迭代操作，如下代码
        for (int i = 0; i < vector.size(); i++) {
            doSomething(vector.get(i));
        }
                     代码 5-3
        与getLast一样，如果在对Vector进行迭代时，另一个线程删除了一个元素，并且这两个操作交替执行，那么这种迭代方法将抛出 将会抛出ArrayIndexOutOfBoundsException 异常
        我们可以通过在客户端加锁来解决UK鹅考迭代的问题，但要牺牲一些伸缩性，这也会降低并发性。
        synchronized (vector) {
            for (int i = 0; i < vector.size(); i++) {
                doSomething(vector.get(i));
            }
        }
                    代码 5-4 带有客户端加锁的迭代

    5.1.2  迭代器与ConcurrentModificationException
        如果不希望在迭代期间对容器加锁，那么一种替代方法就是“克隆”容器，并在副本上进行迭代（在克隆过程中仍然需要对容器加锁。）
        如果容器想代码5-4中那样对迭代器加锁在调用doSomething是持有一个锁，可能会产生死锁（第10章），同时锁竞争也很激烈（参见11章）










    5.4 阻塞和可中断的方法 - TaskRunnable
            线程被阻塞或者暂停的原因：
                * 等待I/O操作结束
                * 等待获得一个锁
                * 等待从Thread.sleep中唤醒
                * 等待另一个线程的计算结果
            当线程被阻塞时，通常被挂起，并被设置成阻塞的某个状态
                * BLOCKED
                * WATING
                * TIMED_WATING
            
            当在代码中调用了一个会抛出InterruptedException的方法时，腰围响应中断作好准备，在类库中，有两种基本选择
                * 传递 将异常传递出去，传递给调用者
                * 恢复中断 必须捕获时，可在当前线程中调用interrupt从中断中恢复
            
            当遇到中断时，可以有更复杂的中断处理方案，但是不应该使用InterruptedException捕获它后不作任何响应，除非扩展了Thread并控制了所有处于调用栈上层的代码
            关于取消和中断-第七章


    5.5 Synchronizer
            阻塞队列能够协调生产者线程和消费者线程之间的控制流，
            Synchronizer是一个对象，包含有：
                    * 信号量 semaphore
                    * 关卡 barrier
                    * 闭锁 latch
            平台类库中，存在一些Synchronizer类，如果这些不够，可以按照14章的描述，创建一个Synchronizer
    
    5.5.1 闭锁
            闭锁（latch）是一种Synchronizer，它可以延迟线程的进度知道线程到达终止（terminal）状态。
            闭锁可以用来确保特定活动知道其他的活动完成后才发生，如：
                    * 确保一个计算不会执行，直到它需要的资源被初始化。
                    * 确保一个服务不会开始，直到它依赖的其他服务都已经开始
                    * 等待，直到活动的所有部分都为继续处理作好重复准备

            CountDownLatch 是一个灵活的闭锁实现，可用于上述的各种情况；允许一个或多个线程等待一个事件集的发生。
            闭锁状态包括一个计数器，初始化为一个正数，用来表现需要等待的事件数。countDown方法对计数器做减操作，
            await方法等待计数器到达零，而await会一直阻塞直到计数器为零或者等待线程中断以及超时。
    
    5.5.2 FutureTask
            FutureTask同样可以作为闭锁。（FutureTask的实现描述了一个抽象的可携带结果的计算[CPJ 4.3.3]）。
            FutureTask的计算是通过Callable实现的，它等价于一个可携带结果的Runnale，并且有三个状态：
                    * 等待
                    * 运行
                    * 完成 完成包括所有计算以任意方式结束，包括正常结束、取消和异常。
                           一旦FutrueTask进入完成状态，它会永远停止在这个状态上。
            FutureTask.get的行为依赖于任务的状态。如果它以及完成,get可以立即得到返回结果，否则会被阻塞直到任务转入完成状态，然后返回结果或者抛出异常
            FutureTask把计算的结果从运行计算的线程传递到需要这个结果的线程；FutureTask的规约保证了这种传递建立在结果的安全发布基础之上。

            Excutor框架利用FutureTask来完成异步任务，并可以用来进行任何潜在的耗时计算，而且可以在真正需要计算结果之前就启动他们开始计算。