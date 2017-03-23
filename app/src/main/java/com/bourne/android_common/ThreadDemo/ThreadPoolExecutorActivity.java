package com.bourne.android_common.ThreadDemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.bourne.android_common.R;
import com.bourne.common_library.utils.Logout;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ThreadPoolExecutorActivity extends AppCompatActivity {
    private final int CORE_POOL_SIZE = 4;//核心线程数
    private final int MAX_POOL_SIZE = 5;//最大线程数
    private final long KEEP_ALIVE_TIME = 10;//空闲线程超时时间
    private final int TOTALTICKET = 20;//总票数
    private final int BLOCK_SIZE = 2;//阻塞队列大小


    private ThreadPoolExecutor executorPool;
    private ExecutorService executorService;
    private int lastTicket = TOTALTICKET;
    private int windowsNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_pool_executor);
        // 创建线程池
        // 创建一个核心线程数为1、最大线程数为3的线程池
        executorPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

        //当开启线程超过最大线程数量，就增加ArrayBlockingQueue里面规定的数量，再超过的话就抛出异常了
//        executorPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
//                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(BLOCK_SIZE),
//                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
//        executorPool.allowCoreThreadTimeOut(true);

        executorService= Executors.newFixedThreadPool(1);

    }

    private void useNewThread{
        ExecutorService    singleTaskExecutor = Executors.newSingleThreadExecutor();// 每次只执行一个线程任务的线程池
        ExecutorService      limitedTaskExecutor = Executors.newFixedThreadPool(3);// 限制线程池大小为7的线程池
        ExecutorService   allTaskExecutor = Executors.newCachedThreadPool(); // 一个没有限制最大线程数的线程池
        ExecutorService    scheduledTaskExecutor = Executors.newScheduledThreadPool(3);// 一个可以按指定时间可周期性的执行的线程池
        ExecutorService     scheduledTaskFactoryExecutor = Executors.newFixedThreadPool(3, new ThreadFactoryTest());// 按指定工厂模式来执行的线程池
        ExecutorService    scheduledTaskFactoryExecutor.submit(new Runnable()
        {

            @Override
            public void run()
            {
                Log.i("KKK", "This is the ThreadFactory Test  submit Run! ! ! ");
            }
        });
    }
    /**
     * 点击增加一个窗口
     *
     * @param view
     */
    public void begin(View view) {
        view.post(new Runnable() {
            @Override
            public void run() {

            }
        });

        windowsNumber++;
//        if (windowsNumber > CORE_POOL_SIZE) {
//            Logout.e("窗口人数已超过人员，不能再增加");
//            return;
//        }
        Logout.e("开启了一个新窗口:" + windowsNumber);
        try {
            Li("execute");// 监听相关数据
            executorPool.execute(new WorkerThread("窗口" + (windowsNumber)));
        } catch (Exception e) {
            Log.e("threadtest", "AbortPolicy...已超出规定的线程数量，不能再增加了....");
        }

        // 监听一下相关数据
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

        // 所有任务已经执行完毕，我们在监听一下相关数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20 * 1000);
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
                    String tn = Thread.currentThread().getName();
                    //模拟耗时操作
                    Random random = new Random();
                    long time = (random.nextInt(5) + 1) * 1000;
//                    long time = 1000;
                    Thread.sleep(time);

                    lastTicket--;
                    //票卖完了跳出循环
                    if (lastTicket < 0) {
                        lastTicket = 0;
                        Log.e("threadtest", "  卖票员\"" + tn + "\"开始休息");
                        flag = false;
                        Li("execute");// 监听相关数据
                        return;
                    }
                    i++;
                    Log.e("threadtest", getThreadName() + "：卖票员\"" + tn + "\"耗时了(" + time / 1000 + "秒)卖出第<" + i + ">张：" + "剩余： <" + lastTicket + "> 张");

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
