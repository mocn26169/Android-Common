package com.bourne.android_common;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import com.bourne.common_library.base.BaseApplication;
import com.bourne.common_library.utils.Logout;

import java.util.List;


public class APPApplication extends BaseApplication{
    @Override
    public void onCreate() {
        super.onCreate();
        String processName = getProcessName(this, android.os.Process.myPid());

        if (!TextUtils.isEmpty(processName) && processName.equals(this.getPackageName())) {//判断进程名，保证只有主进程运行
            //主进程初始化逻辑
            Logout.e("onCreate");
        }
    }

    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }
}
