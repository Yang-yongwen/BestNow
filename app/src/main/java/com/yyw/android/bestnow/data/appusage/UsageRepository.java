package com.yyw.android.bestnow.data.appusage;

import android.graphics.drawable.Drawable;
import android.util.ArrayMap;

import com.yyw.android.bestnow.common.utils.DateUtils;
import com.yyw.android.bestnow.common.utils.LogUtils;
import com.yyw.android.bestnow.data.dao.AppUsage;
import com.yyw.android.bestnow.data.dao.AppUsageDao;
import com.yyw.android.bestnow.data.dao.PerHourUsage;
import com.yyw.android.bestnow.data.dao.PerHourUsageDao;
import com.yyw.android.bestnow.executor.JobExecutor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Singleton;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by yangyongwen on 2016/10/28.
 */

@Singleton
public class UsageRepository {
    private static final String TAG = LogUtils.makeLogTag(UsageRepository.class);
    AppUsageDao appUsageDao;
    PerHourUsageDao perHourUsageDao;
    JobExecutor executor;
    AppPool appPool;

    Map<String, AppUsage> appUsages;  // 缓存，与数据库内容保持一致。

    public UsageRepository(AppUsageDao appUsageDao, JobExecutor executor,
                           PerHourUsageDao perHourUsageDao, AppPool appPool) {
        this.appUsageDao = appUsageDao;
        this.executor = executor;
        this.perHourUsageDao = perHourUsageDao;
        this.appPool = appPool;
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

    public Observable<AppUsage> getAppUsageObservable(final String packageName) {
        return Observable.fromCallable(new Callable<AppUsage>() {
            @Override
            public AppUsage call() throws Exception {
                return getAppUsage(packageName);
            }
        }).subscribeOn(Schedulers.io());
    }

    private AppUsage getAppUsageFromDb(String packageName) {
        AppUsage appUsage = appUsageDao.queryBuilder()
                .where(AppUsageDao.Properties.PackageName.eq(packageName))
                .unique();
        if (appUsage != null) {
            appUsages.put(appUsage.getPackageName(), appUsage);
        }
        return appUsage;
    }

    public PerHourUsage getPerHourUsage(String packageName, String date, int hour) {
        return perHourUsageDao.queryBuilder()
                .where(PerHourUsageDao.Properties.PackageName.eq(packageName))
                .where(PerHourUsageDao.Properties.Date.eq(date))
                .where(PerHourUsageDao.Properties.Hour.eq(hour))
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

    public Observable<Map<String, AppUsage>> getAppUsageObservableIn(final String start, final String end) {
        return Observable.fromCallable(new Callable<Map<String, AppUsage>>() {
            @Override
            public Map<String, AppUsage> call() throws Exception {
                return getAppUsagesIn(start, end);
            }
        }).subscribeOn(Schedulers.io());
    }

    private Map<String, AppUsage> getAppUsagesIn(String start, String end) {
        Map<String, List<PerHourUsage>> perHourUsages = getPerHourUsageIn(start, end);
        Map<String, AppUsage> result = new ArrayMap<>();
        AppUsage appUsage;
        for (Map.Entry<String, List<PerHourUsage>> entry : perHourUsages.entrySet()) {
            appUsage = convert(entry.getValue());
            result.put(entry.getKey(), appUsage);
        }
        addLabelAndIcon(result.values().iterator());
        return result;
    }

//    private void addLabelAndIcon(Map<String, AppUsage> appUsages) {
//        for (Map.Entry<String, AppUsage> entry : appUsages.entrySet()) {
//            Drawable icon = AppInfoProvider.getInstance().getAppIcon(entry.getKey());
//            String label = AppInfoProvider.getInstance().getAppLabel(entry.getKey());
//            entry.getValue().setAppIcon(icon);
//            entry.getValue().setLabel(label);
//        }
//    }

    private void addLabelAndIcon(Iterator<AppUsage> appUsageIterator) {
        for (; appUsageIterator.hasNext(); ) {
            AppUsage appUsage = appUsageIterator.next();
            Drawable icon = AppInfoProvider.getInstance().getAppIcon(appUsage.getPackageName());
            String label = AppInfoProvider.getInstance().getAppLabel(appUsage.getPackageName());
            appUsage.setAppIcon(icon);
            appUsage.setLabel(label);
        }
    }

    private Map<String, List<PerHourUsage>> getPerHourUsageIn(String startDate, String endDate) {
        List<String> dates = getDateBetween(startDate, endDate);
        List<PerHourUsage> perHourUsages = perHourUsageDao.queryBuilder()
                .where(PerHourUsageDao.Properties.Date.in(dates))
                .list();
        filterUnStatisticApps(perHourUsages.iterator());

        Map<String, List<PerHourUsage>> result = new ArrayMap<>();
        String packageName;
        List<PerHourUsage> list = null;
        for (PerHourUsage usage : perHourUsages) {
            packageName = usage.getPackageName();
            if (result.containsKey(packageName)) {
                result.get(packageName).add(usage);
            } else {
                list = new ArrayList<>();
                list.add(usage);
                result.put(packageName, list);
            }
        }

        return result;
    }

    private List<String> getDateBetween(String startDate, String endDate) {
        Date start, end;
        try {
            start = DateUtils.FORMAT_DAY.parse(startDate);
            end = DateUtils.FORMAT_DAY.parse(endDate);
        } catch (ParseException e) {
            LogUtils.e(TAG, "parse failed: " + e.getMessage());
            LogUtils.e(TAG, e);
            return Collections.EMPTY_LIST;
        }
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(start);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(end);
        calendar2.set(Calendar.HOUR_OF_DAY, 0);
        calendar2.set(Calendar.MINUTE, 0);
        calendar2.set(Calendar.SECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);

        SimpleDateFormat formatter = DateUtils.FORMAT_DAY;
        List<String> dates = new ArrayList<>();
        while (calendar1.getTimeInMillis() <= calendar2.getTimeInMillis()) {
            String day = formatter.format(calendar1.getTimeInMillis());
            dates.add(day);
            calendar1.add(Calendar.DAY_OF_YEAR, 1);
        }

        return dates;
    }

    private AppUsage convert(List<PerHourUsage> perHourUsages) {
        AppUsage appUsage = new AppUsage();
        if (perHourUsages != null && perHourUsages.size() != 0) {
            long usageTime = 0;
            int launchCount = 0;
            long start = perHourUsages.get(0).getTime();
            long end = start;
            for (PerHourUsage usage : perHourUsages) {
                usageTime += usage.getUsageTime();
                launchCount += usage.getLaunchCount();
                start = Math.min(start, usage.getTime());
                end = Math.max(end, usage.getTime());
            }
            appUsage.setPackageName(perHourUsages.get(0).getPackageName());
            appUsage.setTotalUsageTime(usageTime);
            appUsage.setTotalLaunchCount(launchCount);
            appUsage.setStartRecordTime(start);
            appUsage.setLastTimeUsed(end);
        }
        return appUsage;
    }

    public Observable<List<PerHourUsage>> getAppDailyPerHourUsagesObservable(final String packageName, final String date) {
        return Observable.fromCallable(new Callable<List<PerHourUsage>>() {
            @Override
            public List<PerHourUsage> call() throws Exception {
                return getAppDailyPerHourUsages(packageName, date);
            }
        }).subscribeOn(Schedulers.io());
    }

    private List<PerHourUsage> getAppDailyPerHourUsages(String packageName, String date) {
        List<String> dates = getDateBetween(date, date);
        List<PerHourUsage> perHourUsages = perHourUsageDao.queryBuilder()
                .where(PerHourUsageDao.Properties.Date.in(dates))
                .where(PerHourUsageDao.Properties.PackageName.eq(packageName))
                .orderAsc(PerHourUsageDao.Properties.Time)
                .list();
        return perHourUsages;
    }

    public Observable<List<AppUsage>> getAppUsageListObservable(final String date) {
        return Observable.fromCallable(new Callable<List<AppUsage>>() {
            @Override
            public List<AppUsage> call() throws Exception {
                return getAppUsageList(date);
            }
        }).subscribeOn(Schedulers.io());
    }

    private List<AppUsage> getAppUsageList(String date) {
        Map<String, List<PerHourUsage>> perHourUsages = getPerHourUsageIn(date, date);
        List<AppUsage> result = new ArrayList<>();
        AppUsage appUsage;
        for (Map.Entry<String, List<PerHourUsage>> entry : perHourUsages.entrySet()) {
            appUsage = convert(entry.getValue());
            result.add(appUsage);
        }
        addLabelAndIcon(result.iterator());
        return result;
    }

    public long getAppDailyUsageTime(String packageName, String date) {
        List<PerHourUsage> perHourUsages = perHourUsageDao.queryBuilder()
                .where(PerHourUsageDao.Properties.Date.eq(date))
                .where(PerHourUsageDao.Properties.PackageName.eq(packageName))
                .list();
        long totalTime = 0;
        for (PerHourUsage perHourUsage : perHourUsages) {
            totalTime += perHourUsage.getUsageTime();
        }
        return totalTime;
    }

    public long[] getDailyUsageTimeAndLaunchCount(String date) {
        List<PerHourUsage> perHourUsages = perHourUsageDao.queryBuilder()
                .where(PerHourUsageDao.Properties.Date.eq(date))
                .list();
        filterUnStatisticApps(perHourUsages.iterator());
        long[] longs = new long[2];
        for (PerHourUsage perHourUsage : perHourUsages) {
            longs[1] += perHourUsage.getUsageTime();
            longs[0] += perHourUsage.getLaunchCount();
        }
        return longs;
    }

    private void filterUnStatisticApps(Iterator<PerHourUsage> perHourUsageIterator) {
        for (; perHourUsageIterator.hasNext(); ) {
            PerHourUsage perHourUsage = perHourUsageIterator.next();
            if (!appPool.shouldStatistic(perHourUsage.getPackageName())) {
                perHourUsageIterator.remove();
            }
        }
    }

}
