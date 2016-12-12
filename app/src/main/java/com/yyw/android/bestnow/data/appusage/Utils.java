package com.yyw.android.bestnow.data.appusage;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.ArrayMap;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by yangyongwen on 16/10/29.
 */

public class Utils {
    public static final long HOUR_IN_MILLS = 1000 * 60 * 60;

    public static Map<String, PackageInfo> getUserInstalledPackageInfo(Context context) {
        List<PackageInfo> packageInfos = context.getPackageManager()
                .getInstalledPackages(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        Map<String, PackageInfo> userInstalledPackageInfos = new ArrayMap<>();
        for (PackageInfo packageInfo : packageInfos) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                userInstalledPackageInfos.put(packageInfo.packageName, packageInfo);
            }
        }
        return userInstalledPackageInfos;
    }

    public static boolean isSystemApp(PackageManager packageManager, String packageName) {
        boolean isSystemApp = false;
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            isSystemApp = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 ? false : true;
        } catch (Exception e) {

        }
        return isSystemApp;
    }

    public static long getDateStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getDateEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

}
