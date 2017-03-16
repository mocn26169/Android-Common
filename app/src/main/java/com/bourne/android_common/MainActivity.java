package com.bourne.android_common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bourne.android_common.IntentFilterDemo.IntentFilterActivity;
import com.bourne.android_common.NetworkRequestDemo.NetworkRequestActivity;
import com.bourne.android_common.PackageManagerDemo.PackagerManagerActivity;
import com.bourne.android_common.ServiceDemo.ServiceActivity;
import com.bourne.android_common.ThreadDemo.CommonThreadActivity;
import com.bourne.common_library.utils.Logout;

public class MainActivity extends AppCompatActivity {

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Logout.e("msg.what:" + msg.what);
            super.handleMessage(msg);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Message message = new Message();
        message.what = 0;
        mHandler.sendMessage(message);
        setContentView(R.layout.activity_main);
        message = new Message();
        message.what = 1;
        mHandler.sendMessage(message);
        toCommomThreadActivity(null);
        Logout.e("MainActivity");
        message = new Message();
        message.what = 2;
        mHandler.sendMessage(message);
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


}
