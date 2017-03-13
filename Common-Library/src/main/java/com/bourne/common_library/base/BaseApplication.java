package com.bourne.common_library.base;

import android.app.Application;

import com.orhanobut.logger.Logger;

/**
 *
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init().setMethodCount(2).hideThreadInfo();
    }
}
