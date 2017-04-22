package com.bourne.android_common.ServiceDemo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.bourne.android_common.R;
import com.bourne.common_library.utils.Logout;

public class ServiceActivity extends AppCompatActivity {

    MyConn conn;
    Intent intent;
    MyService.IService iService;
    MyService myService;
    private LocalBroadcastManager mLocalBroadcastManager;
    private MyBroadcastReceiver mBroadcastReceiver;
    public final static String ACTION_TYPE_MYSERVICE= "action.type.myservice";

    private int progress = 0;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        //注册广播
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_TYPE_MYSERVICE);
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, intentFilter);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    /**
     * 启动服务
     *
     * @param view
     */
    public void start(View view) {
        intent = new Intent(ServiceActivity.this, MyService.class);
        startService(intent);
    }

    /**
     * 停止服务
     */
    public void stop(View view) {
        stopService(intent);
    }

    /**
     * 绑定服务
     */
    public void bind(View view) {
        conn = new MyConn();
        intent = new Intent(ServiceActivity.this, MyService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    /**
     * 绑定服务
     */
    public void unbind(View view) {
        unbindService(conn);
    }

    /**
     * 发送消息
     */
    public void brocast(View view) {
        iService.callMethodInService();
    }

    /**
     * 连接Service
     */
    private class MyConn implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder service) {
            iService = (MyService.IService) service;
            myService =  ((MyService.MyBinder)service).getService();

            //注册回调接口来接收下载进度的变化
            myService.setOnProgressListener(new MyService.OnProgressListener() {

                @Override
                public void onProgress(int progress) {
                    mProgressBar.setProgress(progress);

                }
            });
        }

        public void onServiceDisconnected(ComponentName name) {

        }

    }

    /**
     * 接受Sercice的消息
     */
    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {

                case ACTION_TYPE_MYSERVICE:
                    Logout.e("Activity收到来自Service的消息");
                    break;
            }
        }
    }


    /**
     * 发送消息
     */
    public void listener(View view) {
        //开始下载
        myService.startDownLoad();
    }
}