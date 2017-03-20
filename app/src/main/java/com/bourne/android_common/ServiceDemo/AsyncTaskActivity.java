package com.bourne.android_common.ServiceDemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bourne.android_common.R;
import com.bourne.common_library.utils.Logout;

import java.util.Random;

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
        LoadImageAsyncTask loadImageAsyncTask = new LoadImageAsyncTask(progressBar, img_center);
        Random random = new Random();
        int index = url.length;
        String path = url[random.nextInt(index)];
        Logout.e("path:" + path);
        loadImageAsyncTask.execute(path);
    }
}
