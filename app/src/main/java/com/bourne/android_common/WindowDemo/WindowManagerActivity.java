package com.bourne.android_common.WindowDemo;

import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.bourne.android_common.R;
import com.bourne.common_library.utils.Logout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class WindowManagerActivity extends AppCompatActivity {
    private static WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    private static WindowManager windowManager;
    private static View floatView;
    private int keyBackClickCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_manager);


        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Logout.e("handler");
            }
        };

        handler.sendEmptyMessage(0);

        Logout.e("onCreate");
//        ImageView m = new ImageView(this);
//        m.setBackgroundColor(Color.BLUE);
//         getWindow().addContentView(m, new
//         ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,200));
//        WindowManager.LayoutParams wl = new WindowManager.LayoutParams();
//        wl.height = 400;
//        wl.width = WindowManager.LayoutParams.MATCH_PARENT;
//        getWindowManager().addView(m, wl);
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

    public void createFloatView(View view) {
        // 判断UI控件是否存在
        if (floatView == null) {

            // 1、获取系统级别的WindowManager
            windowManager = (WindowManager) getApplication().getSystemService(WINDOW_SERVICE);

            LayoutInflater inflater = LayoutInflater.from(this);
            floatView = inflater.inflate(R.layout.floatwindow_inputwindow, null);
            Button btn_send = (Button) floatView.findViewById(R.id.btn_send);
            btn_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    windowManager.removeView(floatView);
                }
            });
            // 3、设置系统级别的悬浮窗的参数，保证悬浮窗悬在手机桌面上
            // 系统级别需要指定type 属性
            // TYPE_SYSTEM_ALERT 允许接收事件
            // TYPE_SYSTEM_OVERLAY 悬浮在系统上
            // 注意清单文件添加权限

            //系统提示。它总是出现在应用程序窗口之上。
            lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                    | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

            // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
            // FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按,不设置这个flag的话，home页的划屏会有问题
            lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

            lp.gravity = Gravity.LEFT | Gravity.TOP;
            //显示位置与指定位置的相对位置差
            lp.x = 0;
            lp.y = 0;

            //悬浮窗的宽高
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            lp.format = PixelFormat.TRANSPARENT;
            windowManager.addView(floatView, lp);


            // 设置悬浮窗监听事件
            floatView.setOnTouchListener(new View.OnTouchListener() {
                private float lastX; //上一次位置的X.Y坐标
                private float lastY;
                private float nowX;  //当前移动位置的X.Y坐标
                private float nowY;
                private float tranX; //悬浮窗移动位置的相对值
                private float tranY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    boolean ret = false;
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            // 获取按下时的X，Y坐标
                            lastX = event.getRawX();
                            lastY = event.getRawY();
                            ret = true;
                            switch (keyBackClickCount++) {
                                case 0:
                                    Timer timer = new Timer();
                                    timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            keyBackClickCount = 0;
                                        }
                                    }, 1000);
                                    break;
                                case 1:
                                    keyBackClickCount = 0;
//                                    createEditLayout();

                                    break;
                                default:
                                    break;
                            }
//
                            break;
                        case MotionEvent.ACTION_MOVE:
//

                            // 获取移动时的X，Y坐标
                            nowX = event.getRawX();
                            nowY = event.getRawY();
                            // 计算XY坐标偏移量
                            tranX = nowX - lastX;
                            tranY = nowY - lastY;
                            // 移动悬浮窗
                            lp.x += tranX;
                            lp.y += tranY;
                            //更新悬浮窗位置
                            windowManager.updateViewLayout(floatView, lp);
                            //记录当前坐标作为下一次计算的上一次移动的位置坐标
                            lastX = nowX;
                            lastY = nowY;

                            break;
                        case MotionEvent.ACTION_UP:
//
                            break;
                    }
                    return ret;
                }
            });
        }
    }
}
