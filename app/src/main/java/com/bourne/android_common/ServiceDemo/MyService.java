package com.bourne.android_common.ServiceDemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.bourne.common_library.utils.Logout;

public class MyService extends Service {

    public interface IService {
        void callMethodInService();
    }

    public class MyBinder extends Binder implements IService {

        public void callMethodInService() {
            helloInservice();
        }

        /**
         * 获取当前Service的实例
         * @return
         */
        public MyService getService(){
            return MyService.this;
        }
    }

    /**
     * 广播
     */
    private LocalBroadcastManager mLocalBroadcastManager;


    /**
     * 发送消息
     */
    private void sendBroadcast() {
        Intent intent = new Intent(ServiceActivity.ACTION_TYPE_MYSERVICE);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    public void helloInservice() {
        Logout.e("service收到来自activity的消息");
        sendBroadcast();
    }

    public MyService() {
        Logout.e("MyService");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Logout.e("onStart");
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logout.e("onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Logout.e("onCreate");
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        Logout.e("onDestroy");
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        Logout.e("onBind");
        return new MyBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logout.e("onUnbind");
        return super.onUnbind(intent);
    }

    public interface OnProgressListener {
        void onProgress(int progress);
    }
    /**
     * 进度条的最大值
     */
    public static final int MAX_PROGRESS = 100;

    /**
     * 进度条的进度值
     */
    private int progress = 0;
    /**
     * 更新进度的回调接口
     */
    private OnProgressListener onProgressListener;


    /**
     * 注册回调接口的方法，供外部调用
     * @param onProgressListener
     */
    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    /**
     * 增加get()方法，供Activity调用
     * @return 下载进度
     */
    public int getProgress() {
        return progress;
    }

    /**
     * 模拟下载任务，每秒钟更新一次
     */
    public void startDownLoad(){
        new Thread(new Runnable() {

            @Override
            public void run() {
                while(progress < MAX_PROGRESS){
                    progress += 5;

                    //进度发生变化通知调用方
                    if(onProgressListener != null){
                        onProgressListener.onProgress(progress);
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }


}
