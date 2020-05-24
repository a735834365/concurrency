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


        