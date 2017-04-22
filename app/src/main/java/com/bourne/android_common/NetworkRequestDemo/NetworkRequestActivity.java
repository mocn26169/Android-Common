package com.bourne.android_common.NetworkRequestDemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bourne.android_common.MainActivity;
import com.bourne.android_common.R;
import com.bourne.common_library.utils.Logout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkRequestActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private TextView textView;

    private static final int MSG_NEW_PIC = 2;
    private static final int MSG_CACHE_PIC = 1;
    private static final int ERROR = 3;
    private static final int EXCEPTION = 4;

    //1.在主线程里面声明消息处理器 handler
    private Handler handler = new Handler() {
        //处理消息的
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CACHE_PIC:
                    //3.处理消息 运行在主线程
                    Bitmap bitmap = (Bitmap) msg.obj;
                    imageView.setImageBitmap(bitmap);
                    System.out.println("(不用下载)缓存图片");
                    break;
                case MSG_NEW_PIC:
                    Bitmap bitmap2 = (Bitmap) msg.obj;
                    imageView.setImageBitmap(bitmap2);
                    System.out.println("新下载(还没有缓存)下载的图片");
                    break;
                case ERROR:
                    Toast.makeText(NetworkRequestActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                    break;
                case EXCEPTION:
                    Toast.makeText(NetworkRequestActivity.this, "发生异常，请求失败", Toast.LENGTH_SHORT).show();
                    break;
            }

            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_request);
        Logout.e("onCreate:"+ getIntent().getIntExtra("number",-1));


        findViewById(R.id.btn_downloadImage).setOnClickListener(this);
        imageView = (ImageView) findViewById(R.id.imageView);
        findViewById(R.id.btn_downloadFile).setOnClickListener(this);
        findViewById(R.id.btn_downloadText).setOnClickListener(this);
        textView = (TextView) findViewById(R.id.textView);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_downloadImage:
                downloadBitmap();
                break;
            case R.id.btn_downloadFile:
                downloadFile();
                break;
            case R.id.btn_downloadText:
                downloadText();
                break;
        }
    }

    private void downloadText() {

    }

    private void downloadFile() {

    }

    private void downloadBitmap() {

        final String path = "http://images.csdn.net/20170413/andr1_meitu_1.jpg";

        new Thread() {
            public void run() {

                File file = new File(getCacheDir(), Base64.encodeToString(
                        path.getBytes(), Base64.DEFAULT));

                if (file.exists() && file.length() > 0) {
                    System.out.println("图片存在，拿缓存");
                    Bitmap bitmap = BitmapFactory.decodeFile(file
                            .getAbsolutePath());

                    Message msg = new Message();//声明消息
                    msg.what = MSG_CACHE_PIC;
                    msg.obj = bitmap;//设置数据
                    handler.sendMessage(msg);//让handler帮我们发送数据
                } else {
                    Logout.e("图片不存在，获取数据生成缓存");
                    // 通过http请求把图片获取下来。
                    try {
                        // 1.声明访问的路径， url 网络资源 http ftp rtsp
                        URL url = new URL(path);
                        // 2.通过路径得到一个连接 http的连接
                        HttpURLConnection conn = (HttpURLConnection) url
                                .openConnection();
                        // 3.判断服务器给我们返回的状态信息。
                        // 200 成功 302 从定向 404资源没找到 5xx 服务器内部错误
                        int code = conn.getResponseCode();
                        if (code == 200) {
                            InputStream is = conn.getInputStream();// png的图片
                            FileOutputStream fos = new FileOutputStream(file);
                            byte[] buffer = new byte[1024];
                            int len = -1;
                            while ((len = is.read(buffer)) != -1) {
                                fos.write(buffer, 0, len);
                            }
                            is.close();
                            fos.close();
                            Bitmap bitmap = BitmapFactory.decodeFile(file
                                    .getAbsolutePath());
                            //更新ui ，不能写在子线程
                            Message msg = new Message();
                            msg.obj = bitmap;
                            msg.what = MSG_NEW_PIC;
                            handler.sendMessage(msg);

                        } else {
                            // 请求失败
                            //土司更新ui，不能写在子线程
                            //Toast.makeText(this, "请求失败", 0).show();
                            Message msg = new Message();
                            msg.what = ERROR;
                            handler.sendMessage(msg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //土司不能写在子线程
                        //Toast.makeText(this, "发生异常，请求失败", 0).show();
                        Message msg = new Message();
                        msg.what = EXCEPTION;
                        handler.sendMessage(msg);
                    }
                }
            }

            ;
        }.start();
    }


}
