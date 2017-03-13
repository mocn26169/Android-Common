package com.bourne.common_library.interfaces;

import android.os.Bundle;

/**
 * UI上一些常见的接口
 */
public interface UIInterface {
    /**
     * setContentView之前调用
     */
    void initBeforeSetContentView();

    /**
     * 得到布局文件
     *
     * @return 布局文件Id
     */
    int getContentViewResId();

    /**
     * 初始化变量
     */
    void initVariables();

    /**
     * 初始化View
     */
    void initViews(Bundle savedInstanceState);

    /**
     * 初始化界面数据
     */
    void loadData();


}
