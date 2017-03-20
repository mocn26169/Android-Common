package com.bourne.android_common.ServiceDemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bourne.android_common.R;
import com.bourne.common_library.utils.Logout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AsyncTaskActivity extends AppCompatActivity {

    /**
     * 图片地址集合
     */
    private String url[] = {
            "http://img0.imgtn.bdimg.com/it/u=1597254274,1405139366&fm=23&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=3901634069,2243065451&fm=23&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=1800624712,2677106110&fm=23&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=2456066925,446683653&fm=23&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=565155430,1247415230&fm=23&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=2845715753,1348257911&fm=23&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=3634032659,2514353810&fm=23&gp=0.jpg"
    };

    private List<LoadImageAsyncTask> tasks = new ArrayList<LoadImageAsyncTask>();

    private int count=0;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.img_center)
    ImageView img_center;

    @BindView(R.id.startLoad)
    Button startLoad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.startLoad)
    public void onClick(View view) {
        clearTasks();

        LoadImageAsyncTask loadImageAsyncTask = new LoadImageAsyncTask(progressBar, img_center);
        //随机读取
//        Random random = new Random();
//        int index = url.length;
//        String path = url[random.nextInt(index)];
//        Logout.e("path:" + path);

        //按顺序读取
        int index = url.length;
        String path = url[count%index];

        loadImageAsyncTask.execute(path);
        tasks.add(loadImageAsyncTask);
        count++;
    }

    /**
     * 清理所有任务
     */
    private void clearTasks() {

        for (int i = 0; i < tasks.size(); i++) {
            AsyncTask asyncTask = tasks.get(i);
            if (asyncTask != null && asyncTask.getStatus() == AsyncTask.Status.RUNNING) {
                //cancel只是将对应的任务标记为取消状态
                asyncTask.cancel(true);
            }
        }
        tasks.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearTasks();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearTasks();
    }
}
