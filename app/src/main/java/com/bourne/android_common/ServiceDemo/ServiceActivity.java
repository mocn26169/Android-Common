package com.bourne.android_common.ServiceDemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bourne.android_common.R;

public class ServiceActivity extends AppCompatActivity {
    MyConn conn;
    Intent intent;
    MyService.IService iService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        intent = new Intent(ServiceActivity.this, MyService.class);
        conn = new MyConn();
    }

    public void start(View view) {
        startService(intent);
    }

    public void stop(View view) {
        stopService(intent);
    }

    public void bind(View view) {
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    public void unbind(View view) {
        unbindService(conn);
    }

    public void sayHello(View view) {
        iService.callMethodInService();
    }

    private class MyConn implements ServiceConnection {


        public void onServiceConnected(ComponentName name, IBinder service) {
            iService = (MyService.IService) service;

        }


        public void onServiceDisconnected(ComponentName name) {


        }

    }
}
