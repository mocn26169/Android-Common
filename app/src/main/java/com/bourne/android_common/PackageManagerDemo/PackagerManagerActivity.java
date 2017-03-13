package com.bourne.android_common.PackageManagerDemo;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bourne.android_common.R;
import com.bourne.common_library.utils.Logout;

import java.util.Collections;
import java.util.List;

public class PackagerManagerActivity extends AppCompatActivity {
    public static final int FILTER_ALL_APP = 0; // 所有应用程序
    public static final int FILTER_SYSTEM_APP = 1; // 系统程序
    public static final int FILTER_THIRD_APP = 2; // 第三方应用程序
    public static final int FILTER_SDCARD_APP = 3; // 安装在SDCard的应用程序
    private PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packager_manager);
//        getPackageInfo();
        Logout.e("系统程序");
        filterApp(FILTER_SYSTEM_APP);
        Logout.e("第三方应用程序");
        filterApp(FILTER_THIRD_APP);
        Logout.e("安装在SDCard的应用程序");
        filterApp(FILTER_SDCARD_APP);
    }

    /**
     * 通过PackageManager的queryIntentActivities方法，
     * 查询系统中所有满足ACTION_MAIN和CATEGORY_LAUNCHER的应用程序，
     * 获取他们的程序名、包名、入口类名。
     */
    private void getPackageInfo() {
        pm = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        Collections.sort(list, new ResolveInfo.DisplayNameComparator(pm));

        for (ResolveInfo info : list) {
            //应用名
            String appName = info.loadLabel(pm).toString();
            //包名
            String packageName = info.activityInfo.packageName;
            //入口类名
            String launchClassName = info.activityInfo.name;
            Logout.i("应用名:" + appName + "\n包名:" + packageName + "\n入口类名:" + launchClassName);
        }
    }

    private void filterApp(int type) {
        pm = this.getPackageManager();
        List<ApplicationInfo> list = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(list, new ApplicationInfo.DisplayNameComparator(pm));
        switch (type) {
            case FILTER_ALL_APP:
                for (ApplicationInfo info : list) {
                    getAppInfo(info);
                }
                break;
            case FILTER_SYSTEM_APP:
                for (ApplicationInfo info : list) {
                    if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        getAppInfo(info);
                    }
                }
                break;
            case FILTER_THIRD_APP:
                for (ApplicationInfo info : list) {
                    if ((info.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                        getAppInfo(info);
                    }
                }
                break;
            case FILTER_SDCARD_APP:
                for (ApplicationInfo info : list) {
                    if (info.flags == ApplicationInfo.FLAG_SYSTEM) {
                        getAppInfo(info);
                    }
                }
                break;

        }
    }

    /**
     * 获取应用信息
     */
    private void getAppInfo(ApplicationInfo applicationInfo) {
        // 应用名
        String appName = applicationInfo.loadLabel(pm).toString();
        // 包名
        String packageName = applicationInfo.packageName;
        //入口类名
        String launchClassName = applicationInfo.name;

        Logout.i("应用名:" + appName + "\n包名:" + packageName + "\n入口类名:" + launchClassName);
    }


}
