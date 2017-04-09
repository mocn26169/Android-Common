package com.bourne.android_common.ThreadDemo;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.bourne.android_common.R;
import com.bourne.common_library.utils.Logout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bourne.android_common.ThreadDemo.ThreadManager.ThreadPool.executor;


public class ThreadPoolCanStopActivity extends AppCompatActivity {

    private List<DownLoadEntity> downLoadList = new ArrayList<DownLoadEntity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_pool_can_stop);
        initView();
    }

    @Override
    protected void onDestroy() {
        ThreadManager.getThreadPool().closeAll();
        super.onDestroy();
    }

    public void initView() {

    }

    public void closeAll(View view) {
        ThreadManager.getThreadPool().closeAll();
    }

    public void addTask(View view) {
//        ThreadManager.getThreadPool().execute(downLoadRunnable);
        DownloadTask downloadTask = new DownloadTask("http://dldir1.qq.com/qqmi/TencentVideo_V5.5.0.11776_848.apk");
        //开始下载，并设定超时限额为3000毫秒
        beginToLoad(downloadTask, 300000, TimeUnit.MILLISECONDS);
    }

    /**
     * 下载线程
     */
    Runnable downLoadRunnable = new Runnable() {
        DownLoadEntity mEntity;

        @Override
        public void run() {
            // 新建一个下载任务
            mEntity = new DownLoadEntity("下载任务" + (downLoadList.size() + 1));
            //将新任务添加到任务列表
            downLoadList.add(mEntity);

            for (int i = 0; i <= 100; i++) {
                try {
                    Thread.sleep(1000);
                    mEntity.setCount(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Logout.e("被打断：" + mEntity.getName());
                    //被打断之后手动停止所有的任务
                    break;
                }
                Logout.e("下载中：" + mEntity.getName() + "  进度--------" + mEntity.getCount() + "%");

            }

        }
    };

    public void beginToLoad(DownloadTask task, long timeout,
                            TimeUnit timeType) {
        Future<?> future = ThreadManager.getThreadPool().executeBySubmit(task);

        if (future == null) {
            return;
        }

        try {
            future.get(timeout, timeType);
            task.throwException();
        } catch (InterruptedException e) {
            System.out.println("下载任务已经取消");
        } catch (ExecutionException e) {
            System.out.println("下载中发生错误，请重新下载");
        } catch (TimeoutException e) {
            System.out.println("下载超时，请更换下载点");
        } catch (FileNotFoundException e) {
            System.out.println("请求资源找不到");
        } catch (IOException e) {
            System.out.println("数据流出错");
        } finally {
            task.setStop(true);
            // 因为这里的下载测试不用得到返回结果，取消任务不会影响结果
            future.cancel(true);
        }
    }

    class DownloadTask implements Runnable {
        private String urlStr;
        // 接收在run方法中捕获的异常，然后自定义方法抛出异常
        private Throwable exception;

        //是否关闭此下载任务
        private boolean isStop = false;

        public void setStop(boolean isStop) {
            this.isStop = isStop;
        }

        public DownloadTask(String url) {
            this.urlStr = url;
        }

        /**
         * 下载大数据
         *
         * @throws FileNotFoundException , IOException
         */
        private void download() throws FileNotFoundException, IOException {

            String path = "file";
            String fileName = "test.apk";
            OutputStream output = null;
            try {
                /*
                 * 通过URL取得HttpURLConnection
                 * 要网络连接成功，需在AndroidMainfest.xml中进行权限配置
                 * <uses-permission android:name="android.permission.INTERNET" />
                 */
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //取得inputStream，并将流中的信息写入SDCard

                /*
                 * 写前准备
                 * 1.在AndroidMainfest.xml中进行权限配置
                 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
                 * 取得写入SDCard的权限
                 * 2.取得SDCard的路径： Environment.getExternalStorageDirectory()
                 * 3.检查要保存的文件上是否已经存在
                 * 4.不存在，新建文件夹，新建文件
                 * 5.将input流中的信息写入SDCard
                 * 6.关闭流
                 */
                String SDCard = Environment.getExternalStorageDirectory() + "";
                String pathName = SDCard + "/" + path + "/" + fileName;//文件存储路径

                File file = new File(pathName);
                InputStream input = conn.getInputStream();
//                if (file.exists()) {
//                    System.out.println("exits");
//                    return;
//                }
                    String dir = SDCard + "/" + path;
                    new File(dir).mkdir();//新建文件夹
                    file.createNewFile();//新建文件
                    output = new FileOutputStream(file);
                    //读取大文件
                    byte[] buffer = new byte[4 * 1024];
                    while (input.read(buffer) != -1) {
                        System.out.println(""+buffer);
                        output.write(buffer);
                    }
                    output.flush();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    output.close();
                    System.out.println("success");
                } catch (IOException e) {
                    System.out.println("fail");
                    e.printStackTrace();
                }
            }
        }


        public void throwException() throws FileNotFoundException, IOException {
            if (exception instanceof FileNotFoundException)
                throw (FileNotFoundException) exception;
            if (exception instanceof IOException)
                throw (IOException) exception;
        }

        @Override
        public void run() {
            try {
                download();
            } catch (FileNotFoundException e) {
                exception = e;
            } catch (IOException e) {
                exception = e;
            }
        }
    }
}
