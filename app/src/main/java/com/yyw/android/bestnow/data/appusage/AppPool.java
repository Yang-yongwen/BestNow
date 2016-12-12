package com.yyw.android.bestnow.data.appusage;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.ArrayMap;

import com.yyw.android.bestnow.common.utils.LogUtils;
import com.yyw.android.bestnow.common.utils.SPUtils;
import com.yyw.android.bestnow.data.dao.App;
import com.yyw.android.bestnow.data.dao.AppDao;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yangyongwen on 16/12/11.
 */

public class AppPool {
    private static final String TAG = LogUtils.makeLogTag(AppPool.class);
    private static final String STATISTIC_APP_REPO_INIT = "statistic_app_repo_init";
    private SPUtils spUtils;
    private boolean isInit;
    private Map<String, App> unStatisticApps;
    private AppDao appDao;
    private Context context;


    public AppPool(Context context, SPUtils spUtils, AppDao dao) {
//        this.spUtils = spUtils;
//        this.appDao = dao;
//        this.context = context;
//        isInit = spUtils.getBooleanValue(STATISTIC_APP_REPO_INIT, false);
//        if (!isInit) {
//            init();
//        } else {
//            getUnStatisticAppFromDb();
//        }
    }

    private void init() {
        isInit = true;
        spUtils.putBooleanValue(STATISTIC_APP_REPO_INIT, isInit);
        unStatisticApps = getSystemApps();
        appDao.insertInTx(unStatisticApps.values());
    }

    private void getUnStatisticAppFromDb() {
        List<App> apps = appDao.queryBuilder().list();
        unStatisticApps = new ArrayMap<>();
        for (App app : apps) {
            unStatisticApps.put(app.getPackageName(), app);
        }
    }

    private Map<String, App> getSystemApps() {
        List<PackageInfo> packageInfoList = context.getPackageManager()
                .getInstalledPackages(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        Map<String, App> systemApps = new ArrayMap<>();
        for (PackageInfo info : packageInfoList) {
            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                App App = new App(info.packageName,
                        AppInfoProvider.getInstance().getAppLabel(info.packageName));
                systemApps.put(info.packageName, App);
            }
        }
        return systemApps;
    }

    public synchronized boolean shouldStatistic(String packageName) {
        return !unStatisticApps.containsKey(packageName);
    }

    public synchronized Set<String> getUnStatisticApps() {
        return unStatisticApps.keySet();
    }

    public synchronized Set<String> getStatisticApps() {
        List<PackageInfo> packageInfoList = context.getPackageManager()
                .getInstalledPackages(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        Set set = new HashSet();
        for (PackageInfo packageInfo : packageInfoList) {
            if (!unStatisticApps.containsKey(packageInfo.packageName)) {
                set.add(packageInfo.packageName);
            }
        }
        return set;
    }

    public synchronized void setAppStatistic(String packageName, boolean shouldStatistic) {
        if (shouldStatistic && unStatisticApps.containsKey(packageName)) {
            App app = unStatisticApps.remove(packageName);
            appDao.delete(app);
        } else if (!shouldStatistic && !unStatisticApps.containsKey(packageName)) {
            App app = new App(packageName,
                    AppInfoProvider.getInstance().getAppLabel(packageName));
            unStatisticApps.put(packageName, app);
            appDao.insert(app);
        }
    }
}
