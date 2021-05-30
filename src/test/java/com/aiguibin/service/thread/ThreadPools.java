package com.aiguibin.service.thread;

import java.util.List;
import java.util.concurrent.*;

/**
 * 描述： 学习Thread类的方法特性用法
 * 以及常见的线程池用法
 * 新建状态:
 * 使用 new 关键字和 Thread 类或其子类建立一个线程对象后，该线程对象就处于新建状态。它保持这个状态直到程序 start() 这个线程。
 * <p>
 * 就绪状态:
 * 当线程对象调用了start()方法之后，该线程就进入就绪状态。就绪状态的线程处于就绪队列中，要等待JVM里线程调度器的调度。
 * <p>
 * 运行状态:
 * 如果就绪状态的线程获取 CPU 资源，就可以执行 run()，此时线程便处于运行状态。处于运行状态的线程最为复杂，它可以变为阻塞状态、就绪状态和死亡状态。
 * <p>
 * 阻塞状态:
 * 如果一个线程执行了sleep（睡眠）、suspend（挂起）等方法，失去所占用资源之后，该线程就从运行状态进入阻塞状态。在睡眠时间已到或获得设备资源后可以重新进入就绪状态。可以分为三种：
 * <p>
 * 等待阻塞：运行状态中的线程执行 wait() 方法，使线程进入到等待阻塞状态。
 * <p>
 * 同步阻塞：线程在获取 synchronized 同步锁失败(因为同步锁被其他线程占用)。
 * <p>
 * 其他阻塞：通过调用线程的 sleep() 或 join() 发出了 I/O 请求时，线程就会进入到阻塞状态。当sleep() 状态超时，join() 等待线程终止或超时，或者 I/O 处理完毕，线程重新转入就绪状态。
 * <p>
 * 死亡状态: 一个运行状态的线程完成任务或者其他终止条件发生时，该线程就切换到终止状态。
 * <p>
 * 每一个 Java 线程都有一个优先级，这样有助于操作系统确定线程的调度顺序。
 * <p>
 * Java 线程的优先级是一个整数，其取值范围是 1 （Thread.MIN_PRIORITY ） - 10 （Thread.MAX_PRIORITY ）。
 * <p>
 * 默认情况下，每一个线程都会分配一个优先级 NORM_PRIORITY（5）。
 * <p>
 * 具有较高优先级的线程对程序更重要，并且应该在低优先级的线程之前分配处理器资源。但是，线程优先级不能保证线程执行的顺序，而且非常依赖于平台。
 *
 * @author AIguibin Date time 2018/9/1 23:28
 */
public class ThreadPools {
    private ThreadFactory threadFactory;

    public ThreadPools(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    /**
     * 创建无参的可运行的任务
     *
     * @return
     */
    public Runnable runnable() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
            }
        };
        return runnable;
    }

    /**
     * 创建入参的可运行的Task
     *
     * @param a      字符串
     * @param list   集合
     * @param thread 线程
     * @param obj    任意类型对象
     * @return 一个Task任务
     */
    public Runnable runnable(String a, List list, MoreThread thread, Object obj) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
            }
        };
        return runnable;
    }

    /**
     * 线程的设置,join方法,run方法
     *
     * @param runnable
     * @throws InterruptedException
     */
    public void threadRun(Runnable runnable) throws InterruptedException {
        Thread thread = threadFactory.newThread(runnable);
        thread.setName("");
        thread.setPriority(0);
        thread.setDaemon(true);
        thread.setContextClassLoader(null);
        thread.setUncaughtExceptionHandler(null);
        thread.join();
        thread.join(1000);
        thread.join(1000, 9);
        thread.run();
    }

    /**
     * 执行一个任务
     * 执行方式thread.start() 执行完立即被销毁
     *
     * @param runnable 一个可执行任务
     */
    public void threadStart(Runnable runnable) {
        Thread thread = threadFactory.newThread(runnable);
        thread.start();
        thread.interrupt();
        thread.isAlive();
        thread.isDaemon();
        thread.isInterrupted();
        thread.getId();
        thread.getName();
        thread.getState();
        thread.getPriority();
        thread.getStackTrace();
        thread.getThreadGroup();
        thread.getContextClassLoader();
        thread.getUncaughtExceptionHandler();
        thread.checkAccess();
    }

    /**
     * 开始多个任务
     *
     * @param runnables 入参一个run方法体的集合
     */
    public void threadsStart(List<Runnable> runnables) {
        for (Runnable runnable : runnables) {
            Thread thread = threadFactory.newThread(runnable);
            thread.getState();
            thread.isAlive();
            thread.start();
        }
    }

    /**
     * 通过使用ThreadPoolExecutor构造函数实现
     * 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
     * public static ExecutorService newCachedThreadPool() {
     * return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
     * 60L, TimeUnit.SECONDS,
     * new SynchronousQueue<Runnable>());
     * }
     *
     * @return
     */
    public ExecutorService cachedThreadPool() {
        ExecutorService cachedPool = Executors.newCachedThreadPool(threadFactory);
        for (int i = 0; i < 10; i++) {
            final int index = i;
            try {
                Thread.sleep(index * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cachedPool.execute(new Runnable() {
                public void run() {
                    System.out.println(index);
                }
            });
        }
        return cachedPool;
    }

    /**
     * 通过使用ThreadPoolExecutor构造函数实现
     * 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
     * public static ExecutorService newFixedThreadPool(int nThreads) {
     * return new ThreadPoolExecutor(nThreads, nThreads,
     * 0L, TimeUnit.MILLISECONDS,
     * new LinkedBlockingQueue<Runnable>());
     * }
     *
     * @return
     */
    public ExecutorService fixedThreadPool() {
        ExecutorService fixedPool = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 10; i++) {
            final int index = i;
            fixedPool.execute(new Runnable() {
                public void run() {
                    System.out.println(index);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        }
        return fixedPool;
    }

    /**
     * 通过使用ThreadPoolExecutor构造函数实现
     * 创建一个定长线程池，支持定时及周期性任务执行。
     * 这边居然不是直接调ThreadPoolExecutor构造函数，
     * 但是我们追一下代码看看下面这个函数你就会明白
     * public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
     * return new ScheduledThreadPoolExecutor(corePoolSize);
     * }
     * 里面使用了父类的构造函数，下面就是本类和父类的继承关系
     * public ScheduledThreadPoolExecutor(int corePoolSize) {
     * super(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS,
     * new DelayedWorkQueue());
     * }
     * public class ScheduledThreadPoolExecutor extends ThreadPoolExecutor impl ScheduledExecutorService
     *
     * @return
     */
    public ExecutorService scheduleThreadPool() {
        ScheduledExecutorService schedulePool = Executors.newScheduledThreadPool(5);
        schedulePool.schedule(new Runnable() {
            public void run() {
                System.out.println("delay 3 seconds");
            }
        }, 3, TimeUnit.SECONDS);
        return schedulePool;
    }

    /**
     * 通过使用ThreadPoolExecutor构造函数实现
     * 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
     * public static ExecutorService newSingleThreadExecutor(ThreadFactory threadFactory) {
     * return new FinalizableDelegatedExecutorService
     * (new ThreadPoolExecutor(1, 1,
     * 0L, TimeUnit.MILLISECONDS,
     * new LinkedBlockingQueue<Runnable>(),
     * threadFactory));
     * }
     *
     * @return
     */
    public ExecutorService singleThreadExecutor() {
        ExecutorService singleExecutor = Executors.newSingleThreadExecutor(threadFactory);
        for (int i = 0; i < 10; i++) {
            final int index = i;
            singleExecutor.execute(new Runnable() {
                public void run() {
                    try {
                        System.out.println(index);
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return singleExecutor;
    }

    /**
     * 通过使用ThreadPoolExecutor构造函数实现
     * 创建一个自定义线程池
     * 看见有好多参数啊，现在主要对其中的构造参数进行解释：
     * <p>
     * corePoolSize:核心池大小，意思是当超过这个范围的时候，就需要将新的线程放到等待队列中了即workQueue；
     * <p>
     * maximumPoolSize:线程池最大线程数量，表明线程池能创建的最大线程数
     * <p>
     * keepAlivertime:当活跃线程数大于核心线程数，空闲的多余线程最大存活时间。
     * <p>
     * unit：存活时间的单位
     * <p>
     * workQueue:存放任务的队列---阻塞队列
     * <p>
     * threadFactory:线程工厂,创建线程
     * <p>
     * handler:超出线程范围（maximumPoolSize）和队列容量的任务的处理程序
     * <p>
     * <p>
     * 我们执行线程时都会调用到ThreadPoolExecutor的常用方法
     * execute()
     * submit()
     * shutdown()
     * shutdownNow()
     * execute()方法实际上是Executor中声明的方法，在ThreadPoolExecutor进行了具体的实现，这个方法是ThreadPoolExecutor的核心方法，通过这个方法可以向线程池提交一个任务，交由线程池去执行。
     * <p>
     * submit()方法是在ExecutorService中声明的方法，在AbstractExecutorService就已经有了具体的实现，在ThreadPoolExecutor中并没有对其进行重写，这个方法也是用来向线程池提交任务的，但是它和execute()方法不同，它能够返回任务执行的结果，去看submit()方法的实现，会发现它实际上还是调用的execute()方法，只不过它利用了Future来获取任务执行结果（Future相关内容将在下一篇讲述）。
     * <p>
     * shutdown()和shutdownNow()是用来关闭线程池的。
     * <p>
     * 还有很多其他的方法：
     * 比如：getQueue() 、getPoolSize() 、getActiveCount()、getCompletedTaskCount()等获取与线程池相关属性的方法
     *
     * @return
     */
    public ExecutorService threadPoolExecutor() {
        ExecutorService threadPoolExecutor = new ThreadPoolExecutor(3, 10,
                10000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1024),
                threadFactory, new ThreadPoolExecutor.AbortPolicy());
        return threadPoolExecutor;
    }
}
