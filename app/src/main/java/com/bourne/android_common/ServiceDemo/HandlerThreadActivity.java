package com.bourne.android_common.ServiceDemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bourne.android_common.R;
import com.bourne.common_library.utils.Logout;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HandlerThreadActivity extends AppCompatActivity {
    class ImageBean {
        private String url;
        private Bitmap bitmap;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }
    }

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
    private ImageView imageView;
    private HandlerThread handlerThread;
    private Thread loadImageThread;
    private int count = 0;

    /**
     * 处理UI
     */
    Handler mainThreadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Logout.e("次数:" + msg.what);
            ImageBean imageBean = (ImageBean) msg.obj;
            imageView.setImageBitmap(imageBean.getBitmap());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler_thread);
        imageView = (ImageView) findViewById(R.id.imageView);
        createHandlerThread();
    }

    public void loadByHandlerThread(View view) {
        Handler.Callback callBack = new loadImageCallBack();
        Handler handlerThreadHandler = new Handler(handlerThread.getLooper(), callBack);
        for (int i = 0; i < url.length; i++) {
            handlerThreadHandler.sendEmptyMessageDelayed(i, 1000 * i);
        }
    }


    public void loadByThread(View view) {
        if (loadImageThread != null && !loadImageThread.isInterrupted()) {
            loadImageThread.interrupt();
        }
        count = 0;
        loadImageThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(1000);
                        //在子线程中进行网络请求
                        Bitmap bitmap = downloadUrlBitmap(url[count]);
                        ImageBean imageBean = new ImageBean();
                        imageBean.setBitmap(bitmap);
                        imageBean.setUrl(url[count]);
                        Message message = new Message();
                        message.what = count;
                        message.obj = imageBean;
                        count++;
                        mainThreadHandler.sendMessage(message);
                        //最后一张时停止加载
                        if (count >= url.length) {
                            loadImageThread.interrupt();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Logout.e("加载完毕，停止线程");
                }
            }
        });

        loadImageThread.start();
    }

    /**
     * 处理下载图片的
     */
    class loadImageCallBack implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            //在子线程中进行网络请求
            Bitmap bitmap = downloadUrlBitmap(url[msg.what]);
            ImageBean imageBean = new ImageBean();
            imageBean.setBitmap(bitmap);
            imageBean.setUrl(url[msg.what]);
            Message message = new Message();
            message.what = msg.what;
            message.obj = imageBean;
            mainThreadHandler.sendMessage(message);
            return false;
        }
    }

    /**
     * 创建一个HandlerThread
     */

    private void createHandlerThread() {
        //创建实例对象
        handlerThread = new HandlerThread("downloadImage");
        handlerThread.start();
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
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            bitmap = BitmapFactory.decodeStream(in);
        } catch (final IOException e) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerThread.quit();
    }
}
