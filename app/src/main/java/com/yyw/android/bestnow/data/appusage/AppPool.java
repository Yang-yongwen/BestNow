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
    private Map<String, App> apps;
    private AppDao appDao;
    private Context context;


    public AppPool(Context context, SPUtils spUtils, AppDao dao) {
        this.spUtils = spUtils;
        this.appDao = dao;
        this.context = context;
        isInit = spUtils.getBooleanValue(STATISTIC_APP_REPO_INIT, false);
        if (!isInit) {
            init();
        } else {
            getAppsFromDb();
        }
    }

    private void init() {
        isInit = true;
        spUtils.putBooleanValue(STATISTIC_APP_REPO_INIT, isInit);

        List<PackageInfo> packageInfoList = context.getPackageManager()
                .getInstalledPackages(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        apps = new ArrayMap<>();
        App app;
        for (PackageInfo info : packageInfoList) {
            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                app = new App(info.packageName,
                        AppInfoProvider.getInstance().getAppLabel(info.packageName), false, false, -1l);
            } else {
                app = new App(info.packageName,
                        AppInfoProvider.getInstance().getAppLabel(info.packageName), true, false, -1l);
            }
            apps.put(app.getPackageName(), app);
        }
        appDao.insertInTx(apps.values());
    }

    private void getAppsFromDb() {
        apps = new ArrayMap<>();
        List<App> appList = appDao.queryBuilder().list();
        for (App app : appList) {
            apps.put(app.getPackageName(), app);
        }
    }


    public synchronized boolean shouldStatistic(String packageName) {
        return !(apps.containsKey(packageName) && apps.get(packageName).getShouldStatistic() == false);
    }

    public synchronized Set<String> getUnStatisticApps() {
        Set<String> sets = new HashSet<>();
        for (Map.Entry<String, App> entry : apps.entrySet()) {
            if (entry.getValue().getShouldStatistic() == false) {
                sets.add(entry.getKey());
            }
        }
        return sets;
    }

    public synchronized Map<String, App> getStatisticApps() {
        Map<String, App> result = new ArrayMap<>();
        for (Map.Entry<String, App> entry : apps.entrySet()) {
            if (entry.getValue().getShouldStatistic() != null
                    && entry.getValue().getShouldStatistic() == true) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public synchronized Map<String, App> getStatisticAppsWithoutLimited() {
        Map<String, App> result = new ArrayMap<>();
        for (Map.Entry<String, App> entry : apps.entrySet()) {
            if (entry.getValue().getShouldStatistic() != null
                    && entry.getValue().getShouldStatistic() == true
                    && (entry.getValue().getIsLimit() == null
                    || entry.getValue().getIsLimit() == false)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }


    public App get(String packageName) {
        return apps.get(packageName);
    }

    public boolean containApp(String packageName) {
        return apps.containsKey(packageName);
    }

    public void saveApps(App app) {
        apps.put(app.getPackageName(), app);
        appDao.insertOrReplace(app);
    }

    public void saveApps(List<App> savedApps) {
        for (App app : savedApps) {
            apps.put(app.getPackageName(), app);
        }
        appDao.insertOrReplaceInTx(savedApps);
    }

    public Map<String, App> getLimitedApps() {
        Map<String, App> result = new ArrayMap<>();
        for (Map.Entry<String, App> entry : apps.entrySet()) {
            if (entry.getValue().getShouldStatistic() != null
                    && entry.getValue().getShouldStatistic() == true
                    && entry.getValue().getIsLimit() != null
                    && entry.getValue().getIsLimit() == true) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

}
