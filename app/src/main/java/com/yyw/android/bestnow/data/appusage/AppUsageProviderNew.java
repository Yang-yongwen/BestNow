package com.yyw.android.bestnow.data.appusage;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.ArrayMap;

import com.yyw.android.bestnow.common.utils.DateUtils;
import com.yyw.android.bestnow.common.utils.LogUtils;
import com.yyw.android.bestnow.common.utils.SPUtils;
import com.yyw.android.bestnow.data.dao.AppUsage;
import com.yyw.android.bestnow.data.dao.PerHourUsage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by samsung on 2016/12/7.
 */

public class AppUsageProviderNew {
    private static final String TAG = LogUtils.makeLogTag(AppUsageProviderNew.class);
    private static final String LAST_EVENT_TYPE = "last_event_type";
    private static final String LAST_EVENT_PACKAGE = "last_event_package";
    private static final String LAST_EVENT_TIME_STAMP = "last_event_time_stamp";
    private static final String LAST_UPDATE_TIME = "last_update_time";
    private static final int MOVE_TO_BACKGROUND = UsageEvents.Event.MOVE_TO_BACKGROUND;
    private static final int MOVE_TO_FOREGROUND = UsageEvents.Event.MOVE_TO_FOREGROUND;
    private static final long HOUR_IN_MILLS = 1000 * 60 * 60;
    private List<String> lastUsePackage;

    Context context;
    UsageRepository repository;
    Map<String, PackageInfo> userInstallApp;
    int lastEventType;
    String lastEventPackage;
    long lastEventTimeStamp;
    SPUtils spUtils;
    long lastUpdateTime;
    AppPool appPool;


    public AppUsageProviderNew(Context context, UsageRepository repository, SPUtils spUtils, AppPool appPool) {
        this.context = context;
        this.repository = repository;
        this.spUtils = spUtils;
        this.appPool = appPool;
        userInstallApp = Utils.getUserInstalledPackageInfo(context);
        lastEventType = spUtils.getIntValue(LAST_EVENT_TYPE, -1);
        lastEventPackage = spUtils.getStringValue(LAST_EVENT_PACKAGE, "");
        long currentTime = System.currentTimeMillis();
        lastEventTimeStamp = spUtils.getLongValue(LAST_EVENT_TIME_STAMP, currentTime);
        lastUpdateTime = spUtils.getLongValue(LAST_UPDATE_TIME, currentTime);
        lastUsePackage = new LinkedList<>();
    }

    public Map<String, AppUsage> getAppUsageSinceLastUpdate() {
        return computeUsage(lastUpdateTime, System.currentTimeMillis());
    }

    public Map<String, AppUsage> getAppUsageSinceLastUpdate(long updateTime) {
        return computeUsage(updateTime, System.currentTimeMillis());
    }

    public Map<String, AppUsage> getAppUsageBetween() {
        return computeUsage(lastUpdateTime, System.currentTimeMillis());
    }

    private boolean shouldStatistic(String packageName) {
        return appPool.shouldStatistic(packageName);
    }

    private static final class MyEvent {
        int eventType = -1;
        long timeStamp;
        String packageName;
        String className;

        public MyEvent() {

        }

        public MyEvent(UsageEvents.Event event) {
            timeStamp = event.getTimeStamp();
            eventType = event.getEventType();
            packageName = event.getPackageName();
            className = event.getClassName();
        }

        public void reset() {
            eventType = -1;
            packageName = null;
            className = null;
        }

        public boolean isBackground() {
            return eventType == UsageEvents.Event.MOVE_TO_BACKGROUND;
        }

        public boolean isForeground() {
            return eventType == UsageEvents.Event.MOVE_TO_FOREGROUND;
        }

    }

    private static final class UsageSlice {
        String packageName;
        long usageTime;
        long startTime;
        long endTime;

        UsageSlice combine(UsageSlice right) {
            if (right.packageName == null || !right.packageName.equals(packageName)) {
                throw new IllegalArgumentException("package name must be same");
            }
            usageTime += right.usageTime;
            startTime = Math.min(right.startTime, startTime);
            endTime = Math.max(right.endTime, endTime);
            return this;
        }

        AppUsage toAppUsage() {
            AppUsage appUsage = new AppUsage();
            appUsage.setPackageName(packageName);
            appUsage.setTotalUsageTime(usageTime);
            appUsage.setStartRecordTime(startTime);
            appUsage.setLastTimeUsed(endTime);
            appUsage.setUpdateTime(endTime);
            appUsage.setTotalLaunchCount(1);
            appUsage.setLabel(AppInfoProvider.getInstance().getAppLabel(packageName));
            appUsage.setPerHourUsages(computePerHourUsages());
            return appUsage;
        }

        private Map<String, PerHourUsage> computePerHourUsages() {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(startTime);
            long timePoint = startTime;
            long usageTime;

            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            long hourTime = calendar.getTimeInMillis();
            hourTime += HOUR_IN_MILLS;

            PerHourUsage perHourUsage;
            Map<String, PerHourUsage> perHourUsageMap = new ArrayMap<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
            String startHour = simpleDateFormat.format(startTime);
            String date;
            String hour;
            String dateAndHour;
            while (hourTime < endTime) {
                usageTime = hourTime - timePoint;

                dateAndHour = simpleDateFormat.format(timePoint);
                date = dateAndHour.substring(0, 10);
                hour = dateAndHour.substring(11, 13);

                perHourUsage = new PerHourUsage();
                perHourUsage.setTime(timePoint);
                perHourUsage.setUsageTime(usageTime);
                perHourUsage.setPackageName(packageName);
                perHourUsage.setDate(date);
                perHourUsage.setHour(Integer.parseInt(hour));
                perHourUsageMap.put(dateAndHour, perHourUsage);

                timePoint = hourTime;
                hourTime += HOUR_IN_MILLS;
            }

            perHourUsage = new PerHourUsage();
            perHourUsage.setUsageTime(endTime - Math.max(hourTime - HOUR_IN_MILLS, startTime));
            perHourUsage.setTime(endTime);
            perHourUsage.setPackageName(packageName);
            dateAndHour = simpleDateFormat.format(endTime);
            date = dateAndHour.substring(0, 10);
            hour = dateAndHour.substring(11, 13);
            perHourUsage.setDate(date);
            perHourUsage.setHour(Integer.parseInt(hour));
            perHourUsageMap.put(dateAndHour, perHourUsage);

            for (Map.Entry<String, PerHourUsage> entry : perHourUsageMap.entrySet()) {
                entry.getValue().setLaunchCount(0);
            }
            perHourUsageMap.get(startHour).setLaunchCount(1);

            return perHourUsageMap;
        }
    }

    private Map<String, AppUsage> computeUsage(long startTime, long endTime) {
        List<UsageSlice> usageSlices = new ArrayList<>();
        String lastPkg=null;
        MyEvent startEvent = new MyEvent();
        startEvent.packageName = lastEventPackage;
        startEvent.timeStamp = startTime;
        MyEvent endEvent = new MyEvent();
        endEvent.packageName = lastEventPackage;
        endEvent.timeStamp = startTime;

        logDivider();

        LogUtils.d(TAG, "start time: " + DateUtils.formatTime(startTime));

        LogUtils.d(TAG, "last event package: " + AppInfoProvider.getInstance().getAppLabel(lastEventPackage));

        LogUtils.d(TAG, "last event type: " + lastEventType);

        UsageStatsManager manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        UsageEvents events = manager.queryEvents(startTime, endTime);
        UsageEvents.Event event = new UsageEvents.Event();

        logDivider();

        while (events.getNextEvent(event)) {
            LogUtils.d(TAG, AppInfoProvider.getInstance().getAppLabel(event.getPackageName()) + " -- event type: " + event.getEventType() + " time:" + DateUtils.formatTime(event.getTimeStamp()));
            if (event.getEventType() != UsageEvents.Event.MOVE_TO_BACKGROUND
                    && event.getEventType() != UsageEvents.Event.MOVE_TO_FOREGROUND) {
                continue;
            }
            lastPkg=event.getPackageName();
            if (event.getPackageName().equals(endEvent.packageName)) {
                endEvent = new MyEvent(event);
                continue;
            }
            UsageSlice slice = genUsageSlice(startEvent, endEvent);
            usageSlices.add(slice);
            startEvent = new MyEvent(event);
            endEvent = startEvent;
        }
        endEvent = new MyEvent();
        endEvent.timeStamp = endTime;
        endEvent.packageName = startEvent.packageName;
        UsageSlice slice = genUsageSlice(startEvent, endEvent);
        usageSlices.add(slice);

        logDivider();

        usageSlices = combineUsageSlice(usageSlices);  // 合并相邻的 foreground 和 background
        Map<String, AppUsage> appUsageMap = genAppUsages(usageSlices);

        LogUtils.d(TAG, "end time: " + DateUtils.formatTime(endTime));

        logDivider();

        lastUpdateTime = endTime;
        spUtils.putLongValue(LAST_UPDATE_TIME, lastUpdateTime);
        if (lastPkg!=null) {
            lastEventPackage = lastPkg;
            spUtils.putStringValue(LAST_EVENT_PACKAGE, lastEventPackage);
        }

        return appUsageMap;
    }

    private boolean isEventOfSameUsageSlice(MyEvent foregroundEvent, MyEvent backgroundEvent) {
        if (foregroundEvent == null || foregroundEvent.className == null
                || backgroundEvent == null || backgroundEvent.className == null
                || !foregroundEvent.className.equals(backgroundEvent.className)) {
            return false;
        }

        if (backgroundEvent.timeStamp > foregroundEvent.timeStamp
                || foregroundEvent.timeStamp - backgroundEvent.timeStamp < 170) {
            return true;
        }
        return false;
    }

    private UsageSlice genUsageSlice(MyEvent foregroundEvent, MyEvent backgroundEvent) {
        if (foregroundEvent == null || foregroundEvent.packageName == null
                || backgroundEvent == null || backgroundEvent.packageName == null
                || !backgroundEvent.packageName.equals(foregroundEvent.packageName)) {
            return null;
        }
        UsageSlice slice = new UsageSlice();
        slice.packageName = backgroundEvent.packageName;
        slice.usageTime = backgroundEvent.timeStamp - foregroundEvent.timeStamp;
        slice.startTime = foregroundEvent.timeStamp;
        slice.endTime = backgroundEvent.timeStamp;
        return slice;
    }

    private List<UsageSlice> combineUsageSlice(List<UsageSlice> usageSlices) {
        filterUsageSlice(usageSlices);

        for (UsageSlice slice : usageSlices) {
            LogUtils.d(TAG, "slice: " + AppInfoProvider.getInstance().getAppLabel(slice.packageName));
        }

        if (usageSlices == null || usageSlices.size() == 0) {
            return usageSlices;
        }
        List<UsageSlice> result = new ArrayList<>();
        UsageSlice lastSlice = usageSlices.get(0);
        UsageSlice currentSlice;
        for (int i = 1; i < usageSlices.size(); ++i) {
            currentSlice = usageSlices.get(i);
            if (lastSlice.packageName.equals(currentSlice.packageName)) {
                lastSlice.combine(currentSlice);
            } else {
                result.add(lastSlice);
                lastSlice = currentSlice;
            }
        }
        result.add(lastSlice);

        logDivider();

        for (UsageSlice slice : result) {
            LogUtils.d(TAG, "slice: " + AppInfoProvider.getInstance().getAppLabel(slice.packageName));
        }

        return result;
    }

    private void filterUsageSlice(List<UsageSlice> usageSlices) {
        for (Iterator<UsageSlice> iterator = usageSlices.iterator(); iterator.hasNext(); ) {
            UsageSlice usageSlice = iterator.next();
            if (usageSlice.packageName.equals("com.yyw.android.bestnow")) {
                continue;
            }
            if (Math.abs(usageSlice.usageTime) <= 170) {    // 某个页面的停留时间小于200ms，一般不是用户手动点击打开的。腾讯新闻会出现这种情况，明明没有手动点击却会有 移动到前台 的事件。
                iterator.remove();
            }
        }
    }

    private Map<String, AppUsage> genAppUsages(List<UsageSlice> usageSlices) {
        Map<String, AppUsage> appUsages = new ArrayMap<>();
        if (usageSlices == null || usageSlices.size() == 0) {
            return appUsages;
        }
        AppUsage appUsage;
        AppUsage oldAppUsage;

        if (shouldStatistic(usageSlices.get(0).packageName)) {
            appUsage = usageSlices.get(0).toAppUsage();
            if (lastEventPackage.equals(appUsage.getPackageName())) {
                appUsage.setTotalLaunchCount(0);
                for (Map.Entry<String, PerHourUsage> entry : appUsage.getPerHourUsages().entrySet()) {
                    entry.getValue().setLaunchCount(0);
                }
            }
            appUsages.put(appUsage.getPackageName(), appUsage);
        }

        for (int i = 1; i < usageSlices.size(); ++i) {
            if (!shouldStatistic(usageSlices.get(i).packageName)) {
                continue;
            }
            appUsage = usageSlices.get(i).toAppUsage();
            oldAppUsage = appUsages.get(appUsage.getPackageName());
            if (oldAppUsage == null) {
                appUsages.put(appUsage.getPackageName(), appUsage);
            } else {
                appUsages.put(appUsage.getPackageName(), AppUsage.combine(appUsage, oldAppUsage));
            }
        }

        logDivider();

        for (Map.Entry<String, AppUsage> entry : appUsages.entrySet()) {
            LogUtils.d(TAG, AppInfoProvider.getInstance().getAppLabel(entry.getKey()) + "--launch count: " + entry.getValue().getTotalLaunchCount()
                    + " usage time: " + entry.getValue().getTotalUsageTime());
        }
        logDivider();

        return appUsages;
    }

    private void logDivider() {
        LogUtils.d(TAG, "-------------");
    }

}