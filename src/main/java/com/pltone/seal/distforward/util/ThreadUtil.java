package com.pltone.seal.distforward.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * 线程操作工具类
 *
 * @author chenlong
 * @version 1.0 2019-01-08
 */
public class ThreadUtil {
    private ThreadUtil() {}

    /**
     * 创建指定线程池名称的<code>ThreadFactory</code>
     *
     * @param threadPoolName 线程池名称
     * @return {@link ThreadFactory}
     */
    public static ThreadFactory newThreadFactory(String threadPoolName) {
        return new ThreadFactory() {
            /** 线程计数 */
            private final LongAdder threadNumber = new LongAdder();

            @Override
            public Thread newThread(Runnable runnable) {
                threadNumber.increment();
                final Thread thread = new Thread(runnable, threadPoolName + threadNumber.longValue());
                thread.setDaemon(false);
                if (thread.getPriority() != Thread.NORM_PRIORITY) {
                    thread.setPriority(Thread.NORM_PRIORITY);
                }
                return thread;
            }
        };
    }

    /**
     * 创建指定大小和名称的线程池
     *
     * @param corePoolSize   核心池大小
     * @param maxPoolSize    最大池大小
     * @param threadPoolName 线程池名称
     * @return {@link ExecutorService} 线程池
     */
    public static ExecutorService newThreadPool(int corePoolSize, int maxPoolSize, String threadPoolName) {
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                newThreadFactory(threadPoolName)
        );
    }
}
