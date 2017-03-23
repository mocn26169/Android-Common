package com.bourne.android_common.ThreadDemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bourne.android_common.R;
import com.bourne.common_library.utils.Logout;


public class ThreadActivity extends AppCompatActivity {

    private Thread thread;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);
        textView = (TextView) findViewById(R.id.textView);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                textView.setText("text text text");

            }
        });
        thread.start();
        Logout.e("onCreate");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logout.e("onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logout.e("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logout.e("onResume");
    }
}
