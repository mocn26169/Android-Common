package com.bourne.android_common.ServiceDemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.bourne.android_common.R;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ThreadPoolExecutorActivity extends AppCompatActivity {
    private final int CORE_POOL_SIZE = 3;//核心线程数
    private final int MAX_POOL_SIZE = 5;//最大线程数
    private final long KEEP_ALIVE_TIME = 30;//空闲线程超时时间
    private ThreadPoolExecutor executorPool;
    private int total = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_pool_executor);
        // 创建线程池
        // 创建一个核心线程数为1、最大线程数为3的线程池
        executorPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        executorPool.allowCoreThreadTimeOut(true);
    }

    /**
     * 点击开始售票
     *
     * @param view
     */
    public void begin(View view) {
        total = 18;
        for (int num = 0; num < 6; num++) {//每个500ms添加一个任务到队列中
            try {
                Li("execute");// 监听相关数据
                executorPool.execute(new WorkerThread(""));
            } catch (Exception e) {
                Log.e("threadtest", "AbortPolicy...");
            }
        }


        // 20s后，所有任务已经执行完毕，我们在监听一下相关数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10 * 1000);
                } catch (Exception e) {

                }
                Li("monitor after");
            }
        }).start();
    }

    private void Li(String mess) {
        Log.i("threadtest", "monitor " + mess
                + " CorePoolSize:" + executorPool.getCorePoolSize()
                + " PoolSize:" + executorPool.getPoolSize()
                + " MaximumPoolSize:" + executorPool.getMaximumPoolSize()
                + " ActiveCount:" + executorPool.getActiveCount()
                + " TaskCount:" + executorPool.getTaskCount()

        );
    }

    // 模拟耗时任务
    public class WorkerThread implements Runnable {
        private String threadName;

        public WorkerThread(String threadName) {
            this.threadName = threadName;
        }

        @Override
        public synchronized void run() {

            int i = 0;
            boolean flag = true;
            try {
                while (flag) {
                    Thread.sleep(1000);
                    i++;
                    total--;
                    String tn = Thread.currentThread().getName();
                    Log.e("threadtest",  "  卖票员  " + tn + "卖出一张：" +"剩余： "+total+" 张");
                    //卖到指定数量则退出自己的售卖任务
                    if (i >= 3) flag = false;
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
