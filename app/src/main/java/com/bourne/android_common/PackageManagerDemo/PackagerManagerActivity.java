package com.bourne.android_common.PackageManagerDemo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bourne.android_common.R;
import com.bourne.common_library.utils.Logout;

import java.util.Collections;
import java.util.List;

public class PackagerManagerActivity extends AppCompatActivity {
    // 所有应用程序
    public static final int FILTER_ALL_APP = 0;
    // 系统程序
    public static final int FILTER_SYSTEM_APP = 1;
    // 第三方应用程序
    public static final int FILTER_THIRD_APP = 2;
    // 安装在SDCard的应用程序
    public static final int FILTER_SDCARD_APP = 3;

    private PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packager_manager);
//        Logout.e("获取所有程序信息");
//        getPackageInfo();
//        Logout.e("根据包名获取应用信息");
//        getAppInfoByPackageName("com.bourne.android_common");
        Logout.e("获取所有程序的Activity");
        getInstalledPackages();
//        Logout.e("系统程序");
//        filterApp(FILTER_SYSTEM_APP);
//        Logout.e("第三方应用程序");
//        filterApp(FILTER_THIRD_APP);
//        Logout.e("安装在SDCard的应用程序");
//        filterApp(FILTER_SDCARD_APP);

    }

    /**
     * 获取所有程序信息
     * 通过PackageManager的queryIntentActivities方法，
     * 查询系统中所有满足ACTION_MAIN和CATEGORY_LAUNCHER的应用程序，
     * 获取他们的程序名、包名、入口类名。
     */
    private void getPackageInfo() {
        pm = this.getPackageManager();
        //action为ACTION_MAIN的
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        //根据显示名称排序
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

    /**
     * 筛选
     *
     * @param type
     */
    private void filterApp(int type) {
        pm = this.getPackageManager();

        //getInstalledApplications:获取当前系统上安装的所有的应用程序
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
        //进程名
        String processName = applicationInfo.processName;

        Logout.i("应用名:" + appName + "\n包名:" + packageName + "\n入口类名:" + launchClassName + "\n进程名:" + processName);
    }

    /**
     * 根据包名获取应用信息
     */
    private void getAppInfoByPackageName(String packageName) {
        pm = this.getPackageManager();

        ApplicationInfo ai = null;
        try {
            ai = pm.getApplicationInfo(packageName,
                    PackageManager.GET_META_DATA | PackageManager.GET_SHARED_LIBRARY_FILES);
            // 应用名
            String appName = ai.loadLabel(pm).toString();
            //入口类名
            String launchClassName = ai.name;
            //进程名
            String processName = ai.processName;
            //描述
            String loadDescription = "" + ai.loadDescription(pm);
            //共享库
            String nativeLibraryDir = ai.nativeLibraryDir;

            Logout.i("应用名:" + appName + "\n入口类名:" + launchClassName + "\n进程名:" + processName + "\n描述:" + loadDescription + "\n共享库:" + nativeLibraryDir);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前系统中安装的所有包
     */
    public void getInstalledPackages() {
        pm = this.getPackageManager();
        List<PackageInfo> piList = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES |
                PackageManager.GET_CONFIGURATIONS |
                PackageManager.GET_GIDS |
                PackageManager.GET_INSTRUMENTATION |
                PackageManager.GET_INTENT_FILTERS |
                PackageManager.GET_META_DATA |
                PackageManager.GET_PERMISSIONS |
                PackageManager.GET_PROVIDERS |
                PackageManager.GET_RECEIVERS |
                PackageManager.GET_SERVICES |
                PackageManager.GET_SHARED_LIBRARY_FILES |
                PackageManager.GET_SIGNATURES |
                PackageManager.GET_URI_PERMISSION_PATTERNS);
        if (piList != null) {
            for (PackageInfo pi : piList) {
                Logout.i("包信息:" + pi.toString());
                Logout.i("包名:" + pi.packageName);
                Logout.i("包共享user id" + pi.sharedUserId);
                final ActivityInfo[] ais = pi.activities;
                if (ais != null) {
                    for (ActivityInfo ai : ais) {
                        Logout.i("Activity:{" + pi.packageName + "}:" + ai.name);
                    }
                }

                final InstrumentationInfo[] iis = pi.instrumentation;
                if (iis != null) {
                    for (InstrumentationInfo is : iis) {
                        Logout.i("Instrumentation信息" + is.toString());
                    }
                }

                final ProviderInfo[] pis = pi.providers;
                if (pis != null) {
                    for (ProviderInfo pri : pis) {
                        Logout.i("Provider信息:" + pri);
                    }
                }
            }
        }
    }
}
