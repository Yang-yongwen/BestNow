package com.yyw.android.bestnow.data.appusage;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.ArrayMap;

import com.yyw.android.bestnow.common.utils.SPUtils;
import com.yyw.android.bestnow.data.dao.AppUsage;
import com.yyw.android.bestnow.data.dao.PerHourUsage;
import com.yyw.android.bestnow.executor.JobExecutor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by samsung on 2016/10/28.
 */
@Singleton
public class AppUsageManager {
    private static final String BASE_TIME = "base_time";
    private static final String LAST_UPDATE_TIME = "last_update_time";
    Context context;
    SPUtils spUtils;
    JobExecutor executor;
    long baseTime;
    long lastUpdateTime;

    AppUsageProvider appUsageProvider;
    UsageRepository repository;


    @Inject
    public AppUsageManager(Context context, SPUtils spUtils, JobExecutor executor, UsageRepository repository) {
        this.context = context;
        this.spUtils = spUtils;
        this.executor = executor;
        this.repository = repository;
        this.baseTime = spUtils.getLongValue(BASE_TIME, System.currentTimeMillis());
        this.lastUpdateTime = spUtils.getLongValue(LAST_UPDATE_TIME, System.currentTimeMillis());
        appUsageProvider = new AppUsageProvider(context, repository);
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        spUtils.putLongValue(LAST_UPDATE_TIME, lastUpdateTime);
        Map<String, AppUsage> updatedAppUsages = appUsageProvider.getAppUsageSinceLastUpdate(baseTime, lastUpdateTime, currentTime);
        updatedAppUsages = filterSystemApp(updatedAppUsages);
        updateAppUsage(updatedAppUsages);
        updatePerHourUsage(updatedAppUsages, lastUpdateTime);
        lastUpdateTime = currentTime;
        spUtils.putLongValue(LAST_UPDATE_TIME, lastUpdateTime);
    }

    private Map<String, AppUsage> filterSystemApp(Map<String, AppUsage> appUsages) {
        PackageManager packageManager = context.getPackageManager();
        Map<String, AppUsage> result = new ArrayMap<>();
        for (Map.Entry<String, AppUsage> entry : appUsages.entrySet()) {
            if (!Utils.isSystemApp(packageManager, entry.getKey())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    private void updateAppUsage(Map<String, AppUsage> updatedAppUsages) {
        AppUsage updatedAppUsage;
        AppUsage oldAppUsage;
        for (Map.Entry<String, AppUsage> entry : updatedAppUsages.entrySet()) {
            updatedAppUsage = entry.getValue();
            oldAppUsage = repository.getAppUsage(entry.getKey());
            int totalLaunchCount = oldAppUsage.getTotalLaunchCount() + updatedAppUsage.getTotalLaunchCount();
            long totalUsageTime = oldAppUsage.getTotalUsageTime() + updatedAppUsage.getTotalUsageTime();

            oldAppUsage.setUpdateTime(updatedAppUsage.getUpdateTime());
            oldAppUsage.setLastTimeUsed(updatedAppUsage.getLastTimeUsed());
            oldAppUsage.setTotalUsageTime(totalUsageTime);
            oldAppUsage.setTotalLaunchCount(totalLaunchCount);
            repository.saveAppUsage(oldAppUsage);
        }
    }

    // 必须保证每小时刚起始时更新一次,这样以下函数才有效
    private void updatePerHourUsage(final Map<String, AppUsage> updatedAppUsages, long lastUpdateTime) {
        long hourTime = getHourTime(lastUpdateTime);
        String packageName;
        List<PerHourUsage> updatedPerHourUsage = new ArrayList<>();
        for (Map.Entry<String, AppUsage> entry : updatedAppUsages.entrySet()) {
            packageName = entry.getKey();
            PerHourUsage perHourUsage = repository.getPerHourUsage(packageName, hourTime);
            if (perHourUsage == null) {
                perHourUsage = new PerHourUsage();
                perHourUsage.setTime(hourTime);
                perHourUsage.setPackageName(packageName);
                perHourUsage.setLaunchCount(updatedAppUsages.get(packageName).getTotalLaunchCount());
                perHourUsage.setUsageTime(updatedAppUsages.get(packageName).getTotalUsageTime());
            } else {
                long usageTime = perHourUsage.getUsageTime() + updatedAppUsages.get(packageName).getTotalUsageTime();
                int launchCount = perHourUsage.getLaunchCount() + updatedAppUsages.get(packageName).getTotalLaunchCount();
                perHourUsage.setUsageTime(usageTime);
                perHourUsage.setLaunchCount(launchCount);
            }
            updatedPerHourUsage.add(perHourUsage);
        }
        PerHourUsage[] perHourUsages = new PerHourUsage[updatedPerHourUsage.size()];
        perHourUsages = updatedPerHourUsage.toArray(perHourUsages);
        repository.savePerHourUsage(perHourUsages);
    }

    private long getHourTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }


    public void init() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                baseTime = System.currentTimeMillis();
                lastUpdateTime = baseTime;
                spUtils.putLongValue(BASE_TIME, baseTime);
                spUtils.putLongValue(LAST_UPDATE_TIME, lastUpdateTime);
                resetAppUsageBase();
                Map<String, Long> baseAppUsageTimes = appUsageProvider.getBaseUsageTimes(baseTime);
                initBaseAppUsage(baseAppUsageTimes, baseTime);
            }
        });
    }

    private void resetAppUsageBase() {
        Map<String, AppUsage> appUsages = repository.getAllAppUsages();
        for (Map.Entry<String, AppUsage> entry : appUsages.entrySet()) {
            entry.getValue().setUsageTimeSinceBase(0L);
        }
    }

    private void initBaseAppUsage(Map<String, Long> baseAppUsageTimes, long baseTime) {
        Map<String, PackageInfo> userInstalledPackageInfo = Utils.getUserInstalledPackageInfo(context);
        Map<String, AppUsage> oldAppUsages = repository.getAllAppUsages();
        Map<String, AppUsage> newAppUsages = new ArrayMap<>();
        PackageManager packageManager = context.getPackageManager();
        Long baseAppUsageTime;
        AppUsage appUsage;
        for (Map.Entry<String, PackageInfo> entry : userInstalledPackageInfo.entrySet()) {
            baseAppUsageTime = baseAppUsageTimes.get(entry.getKey());
            long usageTimeSinceBase = baseAppUsageTime == null ? 0L : baseAppUsageTime;
            if (oldAppUsages.containsKey(entry.getKey())) {
                appUsage = oldAppUsages.get(entry.getKey());
                appUsage.setUpdateTime(baseTime);
                appUsage.setUsageTimeSinceBase(usageTimeSinceBase);
            } else {
                appUsage = new AppUsage();
                appUsage.setPackageName(entry.getKey());
                appUsage.setLabel(packageManager.getApplicationLabel(entry.getValue().applicationInfo).toString());
                appUsage.setUsageTimeSinceBase(usageTimeSinceBase);
                appUsage.setTotalLaunchCount(0);
                appUsage.setTotalUsageTime(0L);
                appUsage.setLastTimeUsed(-1L);
                appUsage.setStartRecordTime(baseTime);
                appUsage.setUpdateTime(baseTime);
            }
            newAppUsages.put(entry.getKey(), appUsage);
        }
        repository.saveAppUsages(newAppUsages);
    }

}
