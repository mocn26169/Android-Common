package com.bourne.common_library.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.bourne.common_library.R;
import com.bourne.common_library.interfaces.UIInterface;
import com.bourne.common_library.utils.ToastUtil;
import com.orhanobut.logger.Logger;

import butterknife.ButterKnife;

/**
 *
 */
public abstract class BaseActivity extends AppCompatActivity implements UIInterface {

    private ProgressDialog pd;

    public AlertDialog currentDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i("className=" + this.getClass().getSimpleName());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initWindows();
        initBeforeSetContentView();
        setContentView(getContentViewResId());

        ButterKnife.bind(this);
        initVariables();
        initViews(savedInstanceState);
        loadData();
    }


    /**
     * 显示Toast
     *
     * @param content
     */
    public void showToast(String content) {
        ToastUtil.showToast(this, content);
    }




    /**
     * 显示ProgressDialog
     *
     * @param context
     * @param content
     */
    public ProgressDialog showProgressDialog(Context context, String content) {
        if (pd == null) {
            pd = new ProgressDialog(context);
            pd.setMessage(content);
            pd.setCancelable(false);

            if (!pd.isShowing()) {
                pd.show();
            }
        }
        return pd;
    }

    /**
     * 取消ProgressDialog
     */
    public void dismissProgressDialog() {
        if (pd != null) {
            pd.dismiss();
            pd = null;
        }
    }

    public void showCurrentDialog(Context context, String title, String content, View.OnClickListener listener) {
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_current, null);
        currentDialog = new AlertDialog.Builder(context).create();
        currentDialog.show();
        currentDialog.setCancelable(false);
        currentDialog.getWindow().setContentView(view);
        currentDialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        currentDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        TextView tv_current_content = (TextView) view.findViewById(R.id.tv_current_content);
        tv_current_content.setText(content);

        TextView tv_current_title = (TextView) view.findViewById(R.id.tv_current_title);
        if (!TextUtils.isEmpty(title)) {
            tv_current_title.setVisibility(View.VISIBLE);
        }
        tv_current_title.setText(title);

        //滚动
        tv_current_content.setMovementMethod(ScrollingMovementMethod.getInstance());

        Button btn_current_submit = (Button) view.findViewById(R.id.btn_current_submit);
        btn_current_submit.setOnClickListener(listener);

        Button btn_current_cancel = (Button) view.findViewById(R.id.btn_current_cancel);
        btn_current_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDialog.dismiss();
            }
        });

    }

    /**
     * 设置状态栏和导航栏的颜色
     */
    private void initWindows() {
        Window window = getWindow();
//        int color = getResources().getColor(android.R.color.holo_blue_light);
        int color = getResources().getColor(R.color.colorPrimary);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(color);
            //设置导航栏颜色
            window.setNavigationBarColor(color);
            ViewGroup contentView = ((ViewGroup) findViewById(android.R.id.content));
            View childAt = contentView.getChildAt(0);
            if (childAt != null) {
                childAt.setFitsSystemWindows(true);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //设置contentview为fitsSystemWindows
            ViewGroup contentView = (ViewGroup) findViewById(android.R.id.content);
            View childAt = contentView.getChildAt(0);
            if (childAt != null) {
                childAt.setFitsSystemWindows(true);
            }
            //给statusbar着色
            View view = new View(this);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(this)));
            view.setBackgroundColor(color);
            contentView.addView(view);
        }
    }
    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    private static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    private void toActivity(Context _context, Class<? extends Activity> _class) {
        Intent intent = new Intent(_context, _class);
        startActivity(intent);
    }
}
