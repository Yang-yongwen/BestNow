package com.yyw.android.bestnow.data.appusage;

import android.graphics.drawable.Drawable;
import android.util.ArrayMap;

import com.yyw.android.bestnow.data.dao.AppUsage;
import com.yyw.android.bestnow.data.dao.AppUsageDao;
import com.yyw.android.bestnow.data.dao.PerHourUsage;
import com.yyw.android.bestnow.data.dao.PerHourUsageDao;
import com.yyw.android.bestnow.executor.JobExecutor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import rx.Observable;
import rx.schedulers.Schedulers;

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

    public Observable<AppUsage> getAppUsageObservable(String packageName) {
        return Observable.just(getAppUsage(packageName))
                .subscribeOn(Schedulers.io());
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

    public Observable<Map<String,AppUsage>> getAppUsageObservableIn(Date start,Date end){
        return Observable.just(getAppUsagesIn(start,end))
                .observeOn(Schedulers.io());
    }

    private Map<String,AppUsage> getAppUsagesIn(Date start,Date end){
        Map<String,List<PerHourUsage>> perHourUsages=getPerHourUsageIn(start,end);
        Map<String,AppUsage> result=new ArrayMap<>();
        AppUsage appUsage;
        for (Map.Entry<String,List<PerHourUsage>> entry:perHourUsages.entrySet()){
            appUsage=convert(entry.getValue());
            result.put(entry.getKey(),appUsage);
        }
        addLabelAndIcon(result);
        return result;
    }

    private void addLabelAndIcon(Map<String,AppUsage> appUsages){
        Drawable icon=null;
        String label=null;
        for (Map.Entry<String,AppUsage> entry:appUsages.entrySet()){
            icon=AppInfoProvider.getInstance().getAppIcon(entry.getKey());
            label=AppInfoProvider.getInstance().getAppLabel(entry.getKey());
            entry.getValue().setAppIcon(icon);
            entry.getValue().setLabel(label);
        }
    }

    private Map<String,List<PerHourUsage>> getPerHourUsageIn(Date start,Date end){
        long startTime=Utils.getDateStart(start);
        long endTime=Utils.getDateEnd(end);

        List<PerHourUsage> perHourUsages=perHourUsageDao.queryBuilder()
                .where(PerHourUsageDao.Properties.Time.between(startTime,endTime))
                .list();

        Map<String,List<PerHourUsage>> result=new ArrayMap<>();
        String packageName;
        List<PerHourUsage> list=null;
        for (PerHourUsage usage:perHourUsages){
            packageName=usage.getPackageName();
            if (result.containsKey(packageName)){
                result.get(packageName).add(usage);
            }else{
                list=new ArrayList<>();
                list.add(usage);
                result.put(packageName,list);
            }
        }

        return result;
    }

    private AppUsage convert(List<PerHourUsage> perHourUsages){
        AppUsage appUsage=new AppUsage();
        if (perHourUsages!=null&&perHourUsages.size()!=0){
            long usageTime=0;
            int launchCount=0;
            long start=perHourUsages.get(0).getTime();
            long end=start;
            for (PerHourUsage usage:perHourUsages){
                usageTime+=usage.getUsageTime();
                launchCount+=usage.getLaunchCount();
                start=Math.min(start,usage.getTime());
                end=Math.max(end,usage.getTime());
            }
            appUsage.setPackageName(perHourUsages.get(0).getPackageName());
            appUsage.setTotalUsageTime(usageTime);
            appUsage.setTotalLaunchCount(launchCount);
            appUsage.setStartRecordTime(start);
            appUsage.setLastTimeUsed(end);
        }
        return appUsage;
    }

    public Observable<List<PerHourUsage>> getAppDailyPerHourUsagesObservable(String packageName,Date date){
        return Observable.just(getAppDailyPerHourUsages(packageName,date))
                .subscribeOn(Schedulers.io());
    }

    private List<PerHourUsage> getAppDailyPerHourUsages(String packageName,Date date){
        long startTime=Utils.getDateStart(date);
        long endTime=Utils.getDateEnd(date);

        List<PerHourUsage> perHourUsages=perHourUsageDao.queryBuilder()
                .where(PerHourUsageDao.Properties.Time.between(startTime,endTime))
                .where(PerHourUsageDao.Properties.PackageName.eq(packageName))
                .orderAsc(PerHourUsageDao.Properties.Time)
                .list();

        return perHourUsages;
    }


}
