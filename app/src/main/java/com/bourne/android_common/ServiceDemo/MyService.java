package com.bourne.android_common.ServiceDemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.bourne.common_library.utils.Logout;

public class MyService extends Service {
    public interface IService {
        void callMethodInService();
    }

    public class MyBinder extends Binder implements IService {


        public void callMethodInService() {

            helloInservice();
        }

    }

    public void helloInservice() {
        Logout.e("hello in service");

    }

    public MyService() {
        Logout.e("MyService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Logout.e("onBind");
        return new MyBinder();
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
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        Logout.e("onDestroy");
        super.onDestroy();

    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logout.e("onUnbind");
        return super.onUnbind(intent);

    }
}
