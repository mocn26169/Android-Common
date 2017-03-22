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
import com.bourne.android_common.ServiceDemo.AsyncTaskActivity;
import com.bourne.android_common.ServiceDemo.HandlerThreadActivity;
import com.bourne.android_common.ServiceDemo.ServiceActivity;
import com.bourne.android_common.ServiceDemo.ThreadPoolExecutorActivity;
import com.bourne.android_common.ThreadDemo.CommonThreadActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toThreadPoolExecutorActivity(null);
    }

    private void toActivity(Context _context, Class<? extends Activity> _class) {
        Intent intent = new Intent(_context, _class);
        startActivity(intent);
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


}
