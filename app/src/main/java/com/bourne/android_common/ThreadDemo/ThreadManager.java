package com.bourne.android_common.ThreadDemo;

import android.util.Log;

import com.bourne.common_library.utils.Logout;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程管理者。
 */
public class ThreadManager {

    // 单列的线程池对象。
    private static ThreadPool threadPool;

    /**
     * 单列，线程安全
     * 获取一个线程池对象
     *
     * @return
     */
    public static ThreadPool getThreadPool() {
        if (threadPool == null) {
            //枷锁
            synchronized (ThreadManager.class) {
                if (threadPool == null) {
                    //核心线程数，等于处理器个数乘2
                    int corePoolSize = Runtime.getRuntime().availableProcessors() * 2;
                    int maximumPoolSize = 10;
                    long keepAliveTime = 0L;
                    threadPool = new ThreadPool(corePoolSize, maximumPoolSize, keepAliveTime);
                }
            }
        }

        return threadPool;
    }


    public static class ThreadPool {
        public static ThreadPoolExecutor executor = null;

        private int corePoolSize;
        private int maximumPoolSize;
        private long keepAliveTime = 0; // 限制线程的的最大存活时间

        public ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
            super();
            this.corePoolSize = corePoolSize;  //核心线程数
            this.maximumPoolSize = maximumPoolSize; //最大线程 ，当核心线程用完时。决定是否开启最大线程
            this.keepAliveTime = keepAliveTime;  //线程排队时间，
        }

        /**
         * 线程池：就是把一堆线程放在一起来管理。 1.通过一定的管理机制。来处理线程额执行顺序 2.管理最多可以同时执行的线程数。
         * 3.其他线程通过队列的形式，也就是排队的形式来管理线程的并发数。
         *
         * @param runnable
         */
        public void execute(Runnable runnable) {
            if (runnable == null) {
                return;
            }

            if (executor == null) {

                executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
                        TimeUnit.MILLISECONDS,// 时间单位
                        new LinkedBlockingQueue<Runnable>(),// 线程队列
                        Executors.defaultThreadFactory(),//线程工厂
                        new ThreadPoolExecutor.AbortPolicy());
            }
            // 给线程池里面添加一个线程
            executor.execute(runnable);
            Li("ThreadManager");
        }

        public Future<?> executeBySubmit(Runnable runnable) {

            if (runnable == null) {
                return null;
            }

            if (executor == null) {

                executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
                        TimeUnit.MILLISECONDS,// 时间单位
                        new LinkedBlockingQueue<Runnable>(),// 线程队列
                        Executors.defaultThreadFactory(),//线程工厂
                        new ThreadPoolExecutor.AbortPolicy());
            }
            Li("ThreadManager");
            // 给线程池里面添加一个线程
            return executor.submit(runnable);

        }

        /**
         * 关闭所有的任务
         */
        public void closeAll() {
            Logout.e("closeAll");
            if (executor == null) {
                return;
            }

            executor.shutdown(); // Disable new tasks from being submitted
            try {
                // Wait a while for existing tasks to terminate
                if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    executor.shutdownNow(); // Cancel currently executing tasks
                    // Wait a while for tasks to respond to being cancelled
                    if (!executor.awaitTermination(1, TimeUnit.SECONDS))
                        System.err.println("Pool did not terminate");
                }
            } catch (InterruptedException ie) {
                // (Re-)Cancel if current thread also interrupted
                executor.shutdownNow();
                // Preserve interrupt status
                Thread.currentThread().interrupt();
            }


//            try {
//                // 向学生传达“问题解答完毕后请举手示意！”
//                executor.shutdown();
//
//                // 传达“XX分之内完成所有的任务”后等待任务执行完成
//                // (所有的任务都结束的时候，返回TRUE)
//                if(!executor.awaitTermination(1, TimeUnit.SECONDS)){
//                    // 超时的时候向线程池中所有的线程发出中断(interrupted)。
//                    executor.shutdownNow();
//
//                }
//            } catch (InterruptedException e) {
//                // awaitTermination方法被中断的时候也中止线程池中全部的线程的执行。
//                Logout.e("awaitTermination interrupted: " + e);
//                executor.shutdownNow();
//
//            }
//
//            Li("ThreadManager");

        }

        /**
         * 输入日志
         *
         * @param mess
         */
        private void Li(String mess) {
            Log.i("threadtest", "monitor " + mess
                    + " CorePoolSize:" + executor.getCorePoolSize()
                    + " PoolSize:" + executor.getPoolSize()
                    + " MaximumPoolSize:" + executor.getMaximumPoolSize()
                    + " ActiveCount:" + executor.getActiveCount()
                    + " TaskCount:" + executor.getTaskCount()
            );
        }
    }

}
