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

    7.1.6 处理不可中断的阻塞
    对于由于执行不可中断操作而被阻塞的线程池，可以使用类似于中断的手段来停止这些线程，但得知道原因：
        Java.io包中的同步Socket I/O
            通过关闭底层的套接字，使read或write抛出SocketException
        Java.io包中的同步I/O
            关闭InterruptibleChannel上等待的 - 121页
        Selector的异步I/O
        获取某个锁
