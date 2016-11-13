package com.yyw.android.bestnow.data.appusage;

import android.util.ArrayMap;

import com.yyw.android.bestnow.data.dao.AppUsage;
import com.yyw.android.bestnow.data.dao.AppUsageDao;
import com.yyw.android.bestnow.data.dao.PerHourUsage;
import com.yyw.android.bestnow.data.dao.PerHourUsageDao;
import com.yyw.android.bestnow.executor.JobExecutor;

import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

/**
 * Created by samsung on 2016/10/28.
 */

@Singleton
public class UsageRepository {
    AppUsageDao appUsageDao;
    PerHourUsageDao perHourUsageDao;
    JobExecutor executor;

    Map<String, AppUsage> appUsages;  // 缓存，与数据库内容保持一致。

    public UsageRepository(AppUsageDao appUsageDao, JobExecutor executor, PerHourUsageDao perHourUsageDao) {
        this.appUsageDao = appUsageDao;
        this.executor = executor;
        this.perHourUsageDao = perHourUsageDao;
        appUsages = new ArrayMap<>();
    }

//    public Observable<Map<String, AppUsage>> getAppUsagesObservable() {
//        return Observable.just(getAllAppUsagesFromDb())
//                .subscribeOn(Schedulers.io());
//    }

    public Map<String, AppUsage> getAllAppUsages() {
        return getAllAppUsagesFromDb();
    }

    private Map<String, AppUsage> getAllAppUsagesFromDb() {
        List<AppUsage> appUsageList = appUsageDao.loadAll();
        Map<String, AppUsage> appUsages = new ArrayMap<>();
        for (AppUsage appUsage : appUsageList) {
            appUsages.put(appUsage.getPackageName(), appUsage);
        }
        this.appUsages = appUsages;
        return appUsages;
    }

    public void saveAppUsages(final Map<String, AppUsage> appUsages) {
        if (appUsages == null) {
            return;
        }
        this.appUsages.putAll(appUsages);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                appUsageDao.insertOrReplaceInTx(appUsages.values());
            }
        });
    }

    public void saveAppUsage(final AppUsage appUsage) {
        if (appUsage == null) {
            return;
        }
        appUsages.put(appUsage.getPackageName(), appUsage);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                appUsageDao.insertOrReplace(appUsage);
            }
        });
    }

    //可能返回 null
    public AppUsage getAppUsage(String packageName) {
        return appUsages.get(packageName) == null
                ? getAppUsageFromDb(packageName)
                : appUsages.get(packageName);
    }
//
//    public Observable<AppUsage> getAppUsageObservable(String packageName) {
//        return Observable.just(getAppUsage(packageName))
//                .subscribeOn(Schedulers.io());
//    }

    private AppUsage getAppUsageFromDb(String packageName) {
        AppUsage appUsage = appUsageDao.queryBuilder()
                .where(AppUsageDao.Properties.PackageName.eq(packageName))
                .unique();
        if (appUsage != null) {
            appUsages.put(appUsage.getPackageName(), appUsage);
        }
        return appUsage;
    }


    public PerHourUsage getPerHourUsage(String packageName, long hourTime) {
        return perHourUsageDao.queryBuilder()
                .where(PerHourUsageDao.Properties.PackageName.eq(packageName))
                .where(PerHourUsageDao.Properties.Time.eq(hourTime))
                .unique();
    }


    public void savePerHourUsage(final PerHourUsage... perHourUsage) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                perHourUsageDao.insertOrReplaceInTx(perHourUsage);
            }
        });
    }

}
