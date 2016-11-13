package com.yyw.android.bestnow.data.appusage;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.ArrayMap;

import com.yyw.android.bestnow.data.dao.AppUsage;

import java.util.Map;

import javax.inject.Singleton;

/**
 * Created by samsung on 2016/10/28.
 */
@Singleton
public class AppUsageProvider {
    private static AppUsageProvider instance;
    Context context;
    UsageRepository repository;

    public static AppUsageProvider getInstance(Context context, UsageRepository repository) {
        if (instance == null) {
            synchronized (AppUsageProvider.class) {
                if (instance == null) {
                    instance = new AppUsageProvider(context, repository);
                }
            }
        }
        return instance;
    }

    public AppUsageProvider(Context context, UsageRepository repository) {
        this.context = context;
        this.repository = repository;
    }

    public Map<String, Long> getBaseUsageTimes(long baseTime) {
        Map<String, UsageStats> usageStatsMap = getUsageStatsSinceBaseTime(baseTime - Utils.HOUR_IN_MILLS, baseTime);
        Map<String, Long> baseUsageTimes = new ArrayMap<>();
        for (Map.Entry<String, UsageStats> entry : usageStatsMap.entrySet()) {
            baseUsageTimes.put(entry.getKey(), usageStatsMap.get(entry.getKey()).getTotalTimeInForeground());
        }
        return baseUsageTimes;
    }

    //获得从上次更新到当前时间,所有使用过的 app 的前台时间以及启动次数
    public Map<String, AppUsage> getAppUsageSinceLastUpdate(long baseTime, long lastUpdateTime, long currentTime) {
        Map<String, UsageStats> usageStatsMap = getUsageStatsSinceBaseTime(baseTime, currentTime);
        Map<String, Long> usageTimes = computeUsageTimeSinceLastUpdate(usageStatsMap);
        Map<String, Integer> launchCounts = getLaunchCountsSinceLastUpdate(lastUpdateTime, currentTime);

        Map<String, AppUsage> appUsageSinceLastUpdate = new ArrayMap<>();
        AppUsage appUsage;
        for (Map.Entry<String, Integer> entry : launchCounts.entrySet()) {
            appUsage = new AppUsage();
            appUsage.setPackageName(entry.getKey());
            appUsage.setTotalLaunchCount(entry.getValue());
            appUsage.setTotalUsageTime(usageTimes.get(entry.getKey()));
            appUsage.setUpdateTime(currentTime);
            appUsage.setLastTimeUsed(usageStatsMap.get(entry.getKey()).getLastTimeUsed());
            appUsageSinceLastUpdate.put(entry.getKey(), appUsage);
        }
        return appUsageSinceLastUpdate;
    }

    private Map<String, UsageStats> getUsageStatsSinceBaseTime(long baseTime, long currentTime) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        return usageStatsManager.queryAndAggregateUsageStats(baseTime, currentTime);
    }

    private Map<String, Long> computeUsageTimeSinceLastUpdate(Map<String, UsageStats> usageStatsMap) {
        Map<String, Long> usageTimeSinceLastUpdate = new ArrayMap<>();
        long oldUsageTimeSinceBase;
        AppUsage appUsage;
        for (Map.Entry<String, UsageStats> entry : usageStatsMap.entrySet()) {
            appUsage = repository.getAppUsage(entry.getKey());
            oldUsageTimeSinceBase = 0;
            if (appUsage != null) {
                oldUsageTimeSinceBase = appUsage.getUsageTimeSinceBase();
                appUsage.setUsageTimeSinceBase(entry.getValue().getTotalTimeInForeground());
            }
            usageTimeSinceLastUpdate.put(entry.getKey(), entry.getValue().getTotalTimeInForeground() - oldUsageTimeSinceBase);
            repository.saveAppUsage(appUsage);
        }
        return usageTimeSinceLastUpdate;
    }


//    public  class LaunchCountProvider {
//        LaunchCountProvider() {
//
//        }
////
////        Map<String, Integer> getLaunchCountsSinceLastUpdate(long lastUpdateTime,long currentTime) {
////            Map<String, Integer> updatedLaunchCounts = getLaunchCountsSinceLastUpdate(lastUpdateTime, currentTime);
////            int originalLaunchCount;
////            int launchCount;
////            for (Map.Entry<String, Integer> entry : updatedLaunchCounts.entrySet()) {  // 加上上次更新前的启动次数,所得的启动次数就是安装以来的所有启动次数
////                originalLaunchCount=repository.getAppUsage(entry.getKey()).getLaunchCount();
////                launchCount=entry.getValue();
////                entry.setValue(launchCount+originalLaunchCount);
////            }
////            return updatedLaunchCounts;
////        }
//
//        //获取上次更新到目前的启动次数
//        public Map<String, Integer> getLaunchCountsSinceLastUpdate(long start, long end) {
//            Map<String, Integer> counts = new ArrayMap<>();
//            UsageStatsManager manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
//            UsageEvents events = manager.queryEvents(start, end);
//
//            UsageEvents.Event event = new UsageEvents.Event();
//            String lastPackageName = null;
//            while (events.getNextEvent(event)) {
//                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND
//                        && !event.getPackageName().equals(lastPackageName)) {
//                    if (counts.containsKey(event.getPackageName())) {
//                        counts.put(event.getPackageName(), counts.get(event.getPackageName()) + 1);
//                    } else {
//                        counts.put(event.getPackageName(), 1);
//                    }
//                }
//                lastPackageName = event.getPackageName();
//            }
//            return counts;
//        }
//    }

    private Map<String, Integer> getLaunchCountsSinceLastUpdate(long start, long end) {
        Map<String, Integer> counts = new ArrayMap<>();
        UsageStatsManager manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        UsageEvents events = manager.queryEvents(start, end);

        UsageEvents.Event event = new UsageEvents.Event();
        String lastPackageName = null;
        while (events.getNextEvent(event)) {
            if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND
                    && !event.getPackageName().equals(lastPackageName)) {
                if (counts.containsKey(event.getPackageName())) {
                    counts.put(event.getPackageName(), counts.get(event.getPackageName()) + 1);
                } else {
                    counts.put(event.getPackageName(), 1);
                }
            }
            lastPackageName = event.getPackageName();
        }
        return counts;
    }
}
