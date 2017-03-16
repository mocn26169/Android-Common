package com.bourne.android_common.ThreadDemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.bourne.android_common.R;
import com.bourne.common_library.utils.Logout;

public class CommonThreadActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_thread);
        textView = (TextView) findViewById(R.id.textView);
        Logout.e("CommonThreadActivity");
    }

    public void startThreadMethod1(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Logout.e("startThreadMethod1");
                handler();
            }
        }).start();
    }

    public void startThreadMethod2(View view) {
        new Thread() {
            @Override
            public void run() {
                Logout.e("startThreadMethod2");
                post();
            }
        }.start();
    }

    public void startThreadMethod3(View view) {
        new Thread() {
            @Override
            public void run() {
                Logout.e("startThreadMethod3");
                postDelayed();
            }
        }.start();
    }

    public void startThreadMethod4(View view) {
        new Thread() {
            @Override
            public void run() {
                Logout.e("startThreadMethod4");
                asyncTask();
            }
        }.start();
    }

    public void startThreadMethod5(View view) {
        new Thread() {
            @Override
            public void run() {
                Logout.e("startThreadMethod5");
                runOnUI();
            }
        }.start();
    }

    Handler hander = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            textView.setText("hander");
        }
    };

    private void handler() {
        hander.sendEmptyMessage(0);
    }

    private void post() {
        textView.post(new Runnable() {
            @Override
            public void run() {
                textView.setText("post");
            }
        });
    }

    private void postDelayed() {
        textView.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setText("postDelayed");
            }
        }, 1000);
    }

    private void asyncTask() {
        new AsyncTask() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                textView.setText("AsyncTask");
            }

            @Override
            protected void onProgressUpdate(Object[] values) {
                super.onProgressUpdate(values);
            }


            @Override
            protected Object doInBackground(Object[] params) {

                return null;
            }
        }.execute();

    }

    private void runOnUI() {
        CommonThreadActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText("runOnUI");
            }
        });
    }
}
