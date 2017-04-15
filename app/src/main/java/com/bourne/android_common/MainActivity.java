package com.bourne.android_common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bourne.android_common.IntentFilterDemo.IntentFilterActivity;
import com.bourne.android_common.NetworkRequestDemo.NetworkRequestActivity;
import com.bourne.android_common.PackageManagerDemo.PackagerManagerActivity;
import com.bourne.android_common.ServiceDemo.ServiceActivity;
import com.bourne.android_common.ThreadDemo.AsyncTaskActivity;
import com.bourne.android_common.ThreadDemo.CommonThreadActivity;
import com.bourne.android_common.ThreadDemo.HandlerThreadActivity;
import com.bourne.android_common.ThreadDemo.ThreadActivity;
import com.bourne.android_common.ThreadDemo.ThreadPoolExecutorActivity;
import com.bourne.android_common.WindowDemo.WindowManagerActivity;
import com.bourne.common_library.utils.Logout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logout.e("onCreate");
        setContentView(R.layout.activity_main);
//        toNetworkRequestActivity(null);
        if(null != savedInstanceState)
        {
            int IntTest = savedInstanceState.getInt("IntTest");
            Logout.e("IntTest="+IntTest);

        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Logout.e("onSaveInstanceState");
        outState.putInt("IntTest", 10002);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Logout.e("onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logout.e("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logout.e("onStop");
    }

    private void toActivity(Context _context, Class<? extends Activity> _class) {
        Intent intent = new Intent(_context, _class);
        startActivity(intent);
    }

    public void click(View view) {
        toNetworkRequestActivity(null);
    }

    public void toNetworkRequestActivity(View view) {
        toActivity(this, NetworkRequestActivity.class);
    }

    public void toServiceActivity(View view) {
        toActivity(this, ServiceActivity.class);
    }

    public void toPackagerManagerActivity(View view) {
        toActivity(this, PackagerManagerActivity.class);
    }

    public void toIntentFilterActivity(View view) {
        toActivity(this, IntentFilterActivity.class);
    }

    public void toCommomThreadActivity(View view) {
        toActivity(this, CommonThreadActivity.class);
    }

    public void toHandlerThreadActivity(View view) {
        toActivity(this, HandlerThreadActivity.class);
    }

    public void toAsyncTaskActivity(View view) {
        toActivity(this, AsyncTaskActivity.class);
    }

    public void toThreadPoolExecutorActivity(View view) {
        toActivity(this, ThreadPoolExecutorActivity.class);
    }

    public void toThreadActivity(View view) {
        toActivity(this, ThreadActivity.class);
    }

    public void toWindowManagerActivity(View view) {
        toActivity(this, WindowManagerActivity.class);
    }


}
