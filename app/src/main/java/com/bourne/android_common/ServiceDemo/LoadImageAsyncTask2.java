package com.bourne.android_common.ServiceDemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by m on 2017/3/20.
 */

public class LoadImageAsyncTask2 extends AsyncTask<String,Integer,Bitmap> {
    private ProgressBar mPreogressBar;//进度条
    private ImageView mImageView;//图片显示控件

    public LoadImageAsyncTask2(ProgressBar pb, ImageView iv){
        mPreogressBar = pb;
        mImageView = iv;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mPreogressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        String urlParams = params[0];//拿到execute()传过来的图片url
        Bitmap bitmap = null;
        URLConnection conn = null;
        InputStream is = null;
        try {
            URL url = new URL(urlParams);
            conn = url.openConnection();
            is = conn.getInputStream();

            //这里只是为了演示更新进度的功能，实际的进度值需要在从输入流中读取时逐步获取
            for(int i = 0; i < 100; i++){
                publishProgress(i);
                Thread.sleep(50);//为了看清效果，睡眠一段时间
            }
            //将获取到的输入流转成Bitmap
            BufferedInputStream bis = new BufferedInputStream(is);
            bitmap = BitmapFactory.decodeStream(bis);

            is.close();
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mPreogressBar.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        mPreogressBar.setVisibility(View.GONE);
        mImageView.setImageBitmap(bitmap);
    }
}
