package com.aiguibin.service.thread;

import org.glassfish.jersey.internal.guava.ThreadFactoryBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * 描述： 初探Java多线程
 *
 * @author AIguibin Date time 2018/9/1 17:33
 */
public class MoreThread {
    /**
     * 创建一个java.util.concurrent.ThreadFactory
     *
     * @return null
     */
    public ThreadFactory factory() {
        ThreadFactory factory = new ThreadFactory() {
            @Override
            public Thread newThread(@NotNull Runnable r) {
                return null;
            }
        };
        return null;
    }

    /**
     * JERSEY提供了一个创建线程工厂的ThreadFactoryBuilder()类
     */
    public ThreadFactory jerseyFactory() {
        ThreadFactory factory = new ThreadFactoryBuilder().build();
        ThreadFactoryBuilder builder1 = new ThreadFactoryBuilder().setDaemon(true);
        ThreadFactoryBuilder builder2 = new ThreadFactoryBuilder().setNameFormat("线程工厂名称");
        ThreadFactoryBuilder builder3 = new ThreadFactoryBuilder().setThreadFactory(factory);
        ThreadFactoryBuilder builder4 = new ThreadFactoryBuilder().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                //TODO
            }
        });
        return null;
    }

    /**
     * 工厂方法创建线程常规写法
     */
    public ThreadFactory jerseyCommon() {
        ThreadFactory factory = new ThreadFactoryBuilder().build();
        Thread thread = factory.newThread(new Runnable() {
            @Override
            public void run() {
                //TODO
            }
        });
        return null;
    }

    /**
     * 工厂方法创建线程
     * Lambda表达式写法
     */
    public ThreadFactory jerseyLambda() {
        ThreadFactory factory = new ThreadFactoryBuilder().build();
        Thread thread = factory.newThread(() -> {
            //TODO
        });
        return null;
    }

    /**
     * 创建N个线程
     * @return 线程集合(可以使list||map等集合)
     */
    public List<Thread> threadList() {
        ThreadFactory factory = new ThreadFactoryBuilder().build();
        List<Thread> threads=new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Thread thread = factory.newThread(() -> System.out.println(Thread.currentThread().getName()));
            thread.setName("线程名称");
            threads.add(thread);
        }
        return null;
    }
}
