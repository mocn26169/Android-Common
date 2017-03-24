package com.bourne.android_common.ThreadDemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.bourne.android_common.R;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class ThreadPoolExecutorActivity extends AppCompatActivity {
    private final int CORE_POOL_SIZE = 4;//核心线程数
    private final int MAX_POOL_SIZE = 5;//最大线程数
    private final long KEEP_ALIVE_TIME = 10;//空闲线程超时时间
    private ThreadPoolExecutor executorPool;
    private int songIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_pool_executor);
        // 创建线程池
        // 创建一个核心线程数为4、最大线程数为5的线程池
//        executorPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
//                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
//                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());


        begin(null);
    }

    /**
     * 点击下载
     *
     * @param view
     */
    public void begin(View view) {
        int count = 30;
//-------------------AbortPolicy

        //new LinkedBlockingDeque<Runnable>() + AbortPolicy
        //和newFixedThreadPool、newSingleThreadExecutor一样
        //一批4个。即使任务数量超过核心线程，也不会用第五个
//        executorPool = new ThreadPoolExecutor(4, 5, 10,
//                TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(),
//                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

        //new LinkedBlockingDeque<Runnable>(2) + AbortPolicy
        //new ArrayBlockingQueue<Runnable>(2) + AbortPolicy
        //数量6，4个线程，一批4个，一批2个。
        //数量10，5个线程，只下载了前面7个，3个忽略，1个报错
//        executorPool = new ThreadPoolExecutor(4, 5, 10,
//                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2),
//                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

        //SynchronousQueue
        //只下载了5个
//        executorPool = new ThreadPoolExecutor(0, 5, 10,
//                TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
//                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

        //AsyncTask
        AsyTask();
        try {
            for (int i = 0; i < count; i++) {
//                ThreadManager.getThreadPool().execute(new WorkerThread("歌曲" + i));
                executorPool.execute(new WorkerThread("歌曲" + i));
            }
        } catch (Exception e) {
            Log.e("threadtest", "歌曲" + songIndex + "下载失败...");
        }

//        songIndex++;
//        try {
//            executorPool.execute(new WorkerThread("歌曲" + songIndex));
//        } catch (Exception e) {
//            Log.e("threadtest", "歌曲" + songIndex + "下载失败...");
//        }

        // 所有任务已经执行完毕，我们在监听一下相关数据
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(20 * 1000);
//                } catch (Exception e) {
//
//                }
//                Li("monitor after");
//            }
//        }).start();

    }


    private void AsyTask() {
        BlockingQueue<Runnable> sPoolWorkQueue =
                new LinkedBlockingQueue<Runnable>(10);

        executorPool = new ThreadPoolExecutor(5, 10, 1,
                TimeUnit.SECONDS, sPoolWorkQueue,
                sThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };

    private void Li(String mess) {
        Log.i("threadtest", "monitor " + mess
                + " CorePoolSize:" + executorPool.getCorePoolSize()
                + " PoolSize:" + executorPool.getPoolSize()
                + " MaximumPoolSize:" + executorPool.getMaximumPoolSize()
                + " ActiveCount:" + executorPool.getActiveCount()
                + " TaskCount:" + executorPool.getTaskCount()
        );
    }

    public class WorkerThread implements Runnable {
        private String threadName;

        public WorkerThread(String threadName) {
            this.threadName = threadName;
        }

        @Override
        public synchronized void run() {
            boolean flag = true;
            try {
                while (flag) {
                    String tn = Thread.currentThread().getName();
                    //模拟耗时操作
                    Random random = new Random();
//                    long time = (random.nextInt(5) + 1) * 1000;
                    long time = 5000;
                    Thread.sleep(time);
                    Log.e("threadtest", "线程\"" + tn + "\"耗时了(" + time / 1000 + "秒)下载了第<" + threadName + ">");
                    //下载完了跳出循环
                    flag = false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public String getThreadName() {
            return threadName;
        }
    }
}
