package com.bourne.android_common.ThreadDemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.bourne.android_common.R;
import com.bourne.common_library.utils.Logout;


public class ThreadActivity extends AppCompatActivity {

    private Thread thread;
    private TextView textView;
    public volatile static int count = 0;

    public void inc() {

        //这里延迟1毫秒，使得结果明显
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
        }

        count++;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);
        textView = (TextView) findViewById(R.id.textView);
        WindowManager
//        thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                }
//        textView.setText("text text text");
//                Logout.e("setText");
//            }
//        });
//        thread.start();
        //同时启动1000个线程，去进行i++计算，看看实际结果

        for (int i = 0; i < 1000; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    inc();
                }
            }).start();
        }

        //这里每次运行的值都有可能不同,可能为1000
        Logout.e("运行结果:Counter.count=" + count);
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
