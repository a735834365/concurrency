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
        与迭代Vector一样，要想避免出现ConcurrentModificationException，就必须在迭代过程持有容器的锁
        如果容器想代码5-4中那样对迭代器加锁在调用doSomething是持有一个锁，可能会产生死锁（第10章），同时锁竞争也很激烈（参见11章）

    5.1.3 隐藏迭代器 - HiddenIterator
        正如封装对象的状态有助于维持不变性条件一样，封装对象的同步机制同样有助于确保实施同步策略。
        容器的hashCode和equals等方法也会间接地会先迭代操作，当容器作为另一个容器的元素或键值时，就会出现这种情况。containsAll、removeAll和retainAll等方法，以及把容器作为参数的构造函数，都会对容器进行迭代。这些间接的迭代都会抛出ConcurrentModificationException

    5.2 并发容器
        通过并发容器代替同步容器，可极大提升可伸缩性并降低风险
            同步容器将所有对容器状态的访问都串行化，这种方法的代价是降低并发性。
        并发啊容器是针对多个线程并发访问设计的。
            * ConcurrentHashMap 代替 同步且基于散列的Map以及CopyOnWriteArrayList，且增加了复合操作。Java 5
            * BlockingQueue 提供了多种实现，包括 ConcurrentLinkedQueue Java 6
            * ConcurrentSkipListMap 和 ConcurrentSkipListSet 分别作为同步的SortedMap和SortedSet的并发替代品。（同步的SynchronizedMap包装的TreeMap或TreeSet） Java 6

    5.2.1 ConcurrentHashMap
        仔细回忆HashMap.get和List.contains的源码，遍历散列桶或链表时，必须在许多元素中调用equals（需要计算）。基于散列的容器，hashCode分布不均匀，则退化成线性列表。
        如果在以上情况使用同步锁，性能可想而知。
        ConcurrentHashMap使用-分段锁-，它并不会抛出ConcurrentModificationException，返回的迭代器有若一致性（Weakly Consistent），并非及时失败。
        ConcurrentHashMap的缺点：
            * size和isEmpty等需要在整个Map上计算的方法在计算时返回的结果可能已经过时了，它实际上只是一个预估值
            * 在单线程环境中损失非常小的性能（换取在并发环境下实现更高的吞吐量）

            对于以上缺点的第一个，对于size和isEmpty在并发环境下的作用很小，因为他们的返回值总在不断变化。因此，这些操作被弱化了，以换取对其他更重要操作的性能优化，包括get、put、ContainsKey、remove等。

        关键字：分段锁 （Lock Striping） - 11.4.3节
            在分段锁的机制中任意数量的读取线程可以并发地访问Map、执行读取操作的线程和执行写入操作的线程可以并发的访问Map，包括执行读操作和执行写操作，并且一定数量的写入线程可以并发地修改Map。

    5.2.2 额外的原子Map操作
        在4.4.1章节中对Vector增加原子操作“若没有则添加”，在ConcurrentMap的接口中，有更丰富的实现，如若相等则移除，若相等则替换。可看ConcurrentMap接口。
        可看JavaDocs
        https://docs.oracle.com/javase/1.5.0/docs/api/java/util/concurrent/ConcurrentMap.html#method_summary

    5.2.3 CopyOnWriteArrayList
        用于代替同步List，（CopyOnWriteArraySet代替同步Set）
        安全性保证点：
            * 发布的是一个事实不可变的数组，所以访问时不需要同步 （将可变对象用锁或者其他方式使该对象成为线程安全的）
            * 在每次修改都会创建并重新发布一个新的容器副本，从而实现可变性
            * 迭代时保留底层基础数组的引用，该数组是事实不可变对象（使用volatile确保可见性）- volatile可查看第三章笔记

        缺点：每次修改容器都会赋值底层数组，需要一定开销

        适用场景：
            * 迭代操作多于修改操作
                如：事件通知系统
                    分发通知时需要迭代已注册监听器链表，并调用每一个监听器，大多数情况下，注册和注销事件监听器的操作远少于接收事件通知的操作

    5.3 阻塞队列和生产者-消费者模式
        队列有有界队列和无界队列
            无界队列：不需要阻塞
            有界队列：队列已满，则put方法将阻塞

        阻塞队列支持生产者消费者设计模式，该模式将“找出需要完成的工作”与”执行工作“这两个过程分离开来，并吧工作项放入一个“待完成”列表
        Executor任务执行调度框架-第六章和第8章主题
            线程池与工作队列的组合-常见的生产者消费者设计模式

        info：在构建高可靠的应用程序时，有界队列是一种强大的资源管理工具：他们能一直并防止产生过多的工作项，使引用程序在负荷过载的情况下变得更加健壮
        如果阻塞队列不能完全符合设计需求，还可使用信号量（Semaphore）创建其他的阻塞数据结构（5.5.3）
        类库中阻塞队列的实现：
            * FIFO队列：LinkedBlockingQueue，ArrayBlockingQueue
            * 优先级排序阻塞队列：PriorityBlockingQueue
            * 同步队列：SynchronousQueue-直接交付
                例子：将文件直接交给同事而不是放到邮箱中希望她能尽快拿到文件

    5.3.1 实例：桌面搜索
        生产者-消费者优点：
            提供性能优势，两者可以并发地执行
            相对于将功能都放到一个操作中实现，它有着更高的代码可读性和可重用性

    5.3.2 串行线程封闭
        线程封闭对象只能由单个线程拥有，但可以通过安全发布该对象来“转移”所有权
        线程池利用了串行线程封闭，将对象“借给”一个请求线程，只要客户代码本身不会发布池中的对象或者将对象返回给对象池后就不再使用它，那么就可以安全地在线程之间传递所有权
        可使用其他发布机制确保只有一个线程能接受被转移的对象，阻塞队列简化了这项工作。当然，也可以使用ConcurrentMap提供的原子方法remove或者AtomicReference的原子方法compareAndSet

    5.3.3 双端队列与工作密取
        Java6增加 Deque（读：deck）和BlockingDeque，具体实现包括 ArrayDeque和LinkedBlockingDeque
        适合模式：工作密取（Work Stealing）
            每个消费者都有自己的双端队列，如果一个消费者完成了自己的双端队列中的全部任务，则从其他消费者双端队列-末尾-秘密的获取工作
         工作密取与生产者消费者设计的区别：
            工作密取：每个消费者都有一个双端队列
            生产者-消费者：多个














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