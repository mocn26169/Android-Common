package com.bourne.android_common.ThreadDemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadImageAsyncTask extends AsyncTask<String, Integer, Bitmap> {

    /**
     * 进度条
     */
    private ProgressBar progressBar;

    /**
     * 图片控件
     */
    private ImageView img_center;

    public LoadImageAsyncTask(ProgressBar pb, ImageView iv) {
        this.progressBar = pb;
        this.img_center = iv;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setProgress(0);
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        //获取路径
        String path = params[0];
        //获取图片
        Bitmap bitmap = downloadUrlBitmap(path);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        img_center.setImageBitmap(bitmap);
        progressBar.setProgress(0);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        //通过isCancelled()判断任务任务是否被取消
        if (isCancelled()) {
            return;
        }
        //显示进度
        progressBar.setProgress(values[0]);
    }

    /**
     * 下载图片
     *
     * @param urlString
     * @return
     */
    private Bitmap downloadUrlBitmap(String urlString) {
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;
        Bitmap bitmap = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream is = urlConnection.getInputStream();

            //这里只是为了演示更新进度的功能，实际的进度值需要在从输入流中读取时逐步获取
            for (int i = 0; i < 100; i++) {
                if (isCancelled()) {//通过isCancelled()判断任务任务是否被取消
                    break;
                }
                publishProgress(i);
                Thread.sleep(10);//为了看清效果，睡眠一段时间
            }

            //实际项目中如何获取文件大小作为进度值及更新进度值
//            int totalSize = urlConnection.getContentLength();//获取文件总大小
//            Logout.e("总长度:" + totalSize);
//            int size = 0;//保存当前下载文件的大小，作为进度值
//            int count = 0;
//            byte[] buffer = new byte[1024];

//            while ((count = is.read(buffer)) != -1) {
//                size += count;//获取已下载的文件大小
//                //调用publishProgress更新进度，它内部会回调onProgressUpdate()方法
//                float rsult = (float) size / totalSize * 100;
//                DecimalFormat decimalFormat = new DecimalFormat(".00");
//                String rsult2 = decimalFormat.format(rsult);
//                float i = Float.valueOf(rsult2);
//                int i2 = (int) i;
//                publishProgress(i2);
//                Thread.sleep(100);//为了看清效果，睡眠一段时间
//            }

            in = new BufferedInputStream(is, 8 * 1024);
            bitmap = BitmapFactory.decodeStream(in);

        } catch (final IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
}
