7 取消与关闭
    Java没有提供任何机制来安全地终止线程。但它提供了中断（Interruption），这是一种协作机制，能够使一个线程终止另一个线程的当前工作。
        虽然Thread.stop 和 suspend等方法提供了机制，但这存在严重的缺陷
            https://docs.oracle.com/javase/1.5.0/docs/guide/misc/threadPrimitiveDeprecation.html

    7.1 任务取消 - PrimeGenerator

    7.1.1 中断 - BrokenPrimeProducer
        在Java的API或语言规范中，并没有将中断与任何取消语义关联起来，但实际上，如果在取消之外的其他操作中使用中断，
     那么都是不合适的，并且很难支撑起更大的应用。

        Thread中的中断方法
            http://java.sun.com/j2se/1.5.0/docs/api/java/lang/Thread.html#method_summary
        调用interrupt并不以为这立即停止目标线程正在进行的工作，而只是传递了请求中断的消息，通常，中断是实现取消最合理的方式

    7.1.2 中断策略
        大多数库函数都只是抛出InterruptedException作为中断响应。它们永远不会在某个自己拥有的线程中运行，因此它们为任务或库代码
     实现了最合理的取消策略：尽快推出执行流程，并把中断信息传递给调用者，从而使调用栈找那个的上层代码可以采取进一步的策略
        如果将InterruptedException传递给调用者外还需要执行其他操作，那么应该捕获InterruptedException之后恢复中断状态：
           Thread.currentThread().interrupt();
        由于每个线程拥有各自的中断策略，因此除非你知道中断对该线程的含义，否则就不应该中断这个线程。

    7.1.3 响应中断 - NoncancelableTask
        两种实用策略用于处理InterruptedException
           * 传递异常（可能在执行某个特定于任务的清除操作之后），从而使你的方法也成为可中断的阻塞方法
           * 恢复中断状态，从而使调用栈中的上层代码能够对其进行处理

    7.1.4 计时运行 - TimedRun1，TimeRun2

    7.1.5 通过Future来实现取消 - TimeRun
        当尝试取消某个人物时，不宜直接中断线程池，因为你不知道当中断请求到达时正在运行什么人物---只能通过任务的Future来实现取消

    7.1.6 处理不可中断的阻塞 - ReaderThread
    对于由于执行不可中断操作而被阻塞的线程池，可以使用类似于中断的手段来停止这些线程，但得知道原因：
        * Java.io包中的同步Socket I/O
            通过关闭底层的套接字，使read或write抛出SocketException
        * Java.io包中的同步I/O
            关闭InterruptibleChannel上等待的线程时，将抛出ClosedByInterruptException并关闭链路（该链路上阻塞的其他线程也会抛出该异常）。关闭InterruptibleChannel时，所有链路操作上阻塞的线程都抛出AsynchronousCloseException。大多数Channel都实现了InterruptibleChanel。
        * Selector的异步I/O
        * 获取某个锁
            一个线程由于等待某个内置锁而阻塞，那么将无法响应中断，因为线程认为它肯定会获得锁，所有将不会理会中断请求。Lock类提供了lockInterruptible方法该方法允许在等待一个锁的同时仍能响应中断-13章

    7.1.7 采用newTaskFor来封装非标准的取消 - SocketUsingTask
        当把一个Callable提交给ExecutorService时，submit方法会返回一个Future，我们可以通过这个Future来取消任务。newTaskFor还能返回一个RunnableFuture接口，该接口扩展了Future和Runnable（并由FutureTask实现）。

    7.2 停止基于线程的服务
        线程有一个所有者，即创建该线程的类。因此线程池是其工作者线程的所有者，如果要中断这些线程，那么应该使用线程池。在ExecutorService中提供了shutdown和shutdownNow等方法。同样，在其他拥有线程的服务中也应该提供类似的关闭机制。
        对于持有线程的服务，只要服务的存在时间大于创建线程的方法的存在时间，那么就应该提供生命周期方法。

    7.2.1 示例：日志服务 - LogWriter，LogService
        在LogWriter中，缺少了终止日志线程的服务。
            方案：take能响应中断，可以在捕获到InterruptedException时推出，那么只需要中断日志线程就能停止服务
            缺点：
                1、直接使日志线程退出，会丢失正在能带被写入到日志的信息。
                2、日志消息队列满的，其他线程在调用log时被阻塞因此这些线程无法解除阻塞状态。
                3、取消一个生产者-消费者操作，需要同时取消生产者-消费者。中断日志线程会处理消费者，但在这个LogWriter中，由于生产者不是专门的线程，因此要取消它们将非常困难。
        在LogService中，增加了终止日志的服务
            方案：通过原子方式检查关闭请求，并且有条件地递增一个计数器来“保持”提交消息的权利

    7.2.2 关闭ExecutorService - LogService
        LogService的变化形式，将管理线程的工作委托给一个ExecutorService，而不是由其自行管理

    7.2.3 ”毒丸“对象 - IndexingService
        IndexingService中采用的解决方案也可以扩展到多个生产者：并且消费者仅当收到N（Producers）个“毒丸”对象时才停止。这种方法也可以扩展到多个消费者的情况，只需要生产者将N（consumers）个“毒丸”对象放入队列。然而，当生产者和消费者的数量较大时，这种方法将变得难以使用。

    7.2.4 示例：只执行一次的服务 - CheckForMail

    7.2.5 ShutdownNow的局限性 - TrackingExecutor，WebCrawler
        通过ShutdownNow强行关闭ExecutorService时，会取消正在执行的任务并返回未开始的任务，可将这些任务写日志或保存后续处理。

    7.3 处理非正常的线程终止
    public void run() {
        Throwable thrown = null;
        try{
            while(!isInterrupted())
                runTask(getTaskFromWorkQueue());
        } catch(Throwable e) {
            thrown = e;
        } finally {
            threadExited(this, thrown);
        }
    }
            7-23 典型的线程池工作者线程结构
    以上代码给出了如何在线程池内部构建一个工作者线程。如果任务抛出了一个未检查异常，那么它将使任务中断，但会首先通知框架该线程池正在关闭。
    未捕获的异常处理 - UEHLogger
    在Thread API中提供了UncaughtExceptionHandler，它能检测出某个线程由于未捕获的异常而终结的情况。当一个线程由于未捕获异常而退出，JVM会将异常报告给应用程序的UncaughtExceptionHandler，默认的行为是将栈追踪信息输出到System.err。
    以下是UncaughtExceptionHandler的Oracle文档,介绍了用法
    https://docs.oracle.com/javase/1.5.0/docs/api/java/lang/Thread.UncaughtExceptionHandler.html#method_summary

    7.4 JVM关闭
        JVM正常关闭的触发方式
        * 最后一个“正常（非守护）”线程结束
        * 调用System.exit
        * 通过特定平台的方法关闭：发送SIGINT信号或键入ctrl-C
        * 调用Runtime.halt
        * 在操作系统中“杀死”JVM进程（发送SIGKILL）强行关闭
    7.4.1 关闭钩子
        public void start() {
            Runtime.getRunime().addShutdownHook(new Thread() {
            public void run() {
            try {LogService.this.stop();}
            catch(InterruptedException ignored){}
            }
            });
        }
            7-26 通过注册一个关闭钩子来停止服务
        7-26在start方法中注册一个关闭钩子，确保在退出时关闭日志文件。
        当应用程序需要维护多个服务之间的显示依赖信息时，这项技术可以确保关闭操作按照正确额顺序执行

    7.4.2 守护线程
    线程分为两种，普通线程和守护线程。
    在JVM启动时创建的所有线程中，除了主线程意外，其他的线程都是守护线程（例如垃圾回收器以及其他执行辅助工作的线程）。当创建一个新线程时，新线程将继承创建它的线程的守护状态，所以在默认情况下，主线程创建的所以线程都是普通线程。
    普通线程和守护线程的差异仅仅发生在退出时的操作，当一个线程退出时，JVM会检查其他正在运行的线程，如果这些线程都是守护线程，JVM会正常退出操作。JVM停止时，所有仍然存在的守护线程都将抛弃，既不会执行finally代码，也不会执行回卷栈，而JVM只是直接退出。
    尽可能少地使用守护线程
    守护线程通常不能用来替代应用程序管理程序中各个服务的生命周期

    7.4.3 终结器
    避免使用终结器

    小节
    Java提供了一种协作式的中断机制来实现取消操作。通过使用FutureTask和Executor框架可以帮助构建可取消的任务和服务。