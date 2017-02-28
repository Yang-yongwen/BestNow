package com.yyw.android.bestnow.data.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.ArrayMap;

import java.util.Map;
// KEEP INCLUDES END

/**
 * Entity mapped to table "APP_USAGE".
 */
public class AppUsage {

    private String packageName;
    private String label;
    private Integer totalLaunchCount;
    private Long totalUsageTime;
    private Long lastTimeUsed;
    private Long startRecordTime;
    private Long updateTime;

    // KEEP FIELDS - put your custom fields here
    private Drawable appIcon;
    private Map<String, PerHourUsage> perHourUsages;   //直接使用 to many relationship，每次查找PerHourUsage 会获取到所有的，不合理。
    // KEEP FIELDS END

    public AppUsage() {
    }

    public AppUsage(String packageName) {
        this.packageName = packageName;
    }

    public AppUsage(String packageName, String label, Integer totalLaunchCount, Long totalUsageTime, Long lastTimeUsed, Long startRecordTime, Long updateTime) {
        this.packageName = packageName;
        this.label = label;
        this.totalLaunchCount = totalLaunchCount;
        this.totalUsageTime = totalUsageTime;
        this.lastTimeUsed = lastTimeUsed;
        this.startRecordTime = startRecordTime;
        this.updateTime = updateTime;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getTotalLaunchCount() {
        return totalLaunchCount;
    }

    public void setTotalLaunchCount(Integer totalLaunchCount) {
        this.totalLaunchCount = totalLaunchCount;
    }

    public Long getTotalUsageTime() {
        return totalUsageTime;
    }

    public void setTotalUsageTime(Long totalUsageTime) {
        this.totalUsageTime = totalUsageTime;
    }

    public Long getLastTimeUsed() {
        return lastTimeUsed;
    }

    public void setLastTimeUsed(Long lastTimeUsed) {
        this.lastTimeUsed = lastTimeUsed;
    }

    public Long getStartRecordTime() {
        return startRecordTime;
    }

    public void setStartRecordTime(Long startRecordTime) {
        this.startRecordTime = startRecordTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    // KEEP METHODS - put your custom methods here
    public void setAppIcon(Drawable drawable) {
        appIcon = drawable;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public static AppUsage combine(@NonNull AppUsage appUsage1, @NonNull AppUsage appUsage2) {
        if (!appUsage1.getPackageName().equals(appUsage2.getPackageName())) {
            throw new IllegalArgumentException("you can only combine same package AppUsage");
        }
        AppUsage appUsage = new AppUsage();
        appUsage.setPackageName(appUsage1.getPackageName());
        appUsage.setTotalUsageTime(appUsage1.getTotalUsageTime() + appUsage2.getTotalUsageTime());
        appUsage.setTotalLaunchCount(appUsage1.getTotalLaunchCount() + appUsage2.getTotalLaunchCount());
        appUsage.setStartRecordTime(Math.min(appUsage1.getStartRecordTime(), appUsage2.getStartRecordTime()));
        appUsage.setLastTimeUsed(Math.max(appUsage1.getLastTimeUsed(), appUsage2.getLastTimeUsed()));
        appUsage.setUpdateTime(Math.max(appUsage1.getUpdateTime(), appUsage2.getUpdateTime()));

        Map<String, PerHourUsage> perHourUsageMap1 = appUsage1.getPerHourUsages();
        Map<String, PerHourUsage> perHourUsageMap2 = appUsage2.getPerHourUsages();

        Map<String, PerHourUsage> perHourUsageMap;

        if (perHourUsageMap1 == null) {
            perHourUsageMap = perHourUsageMap2;
        } else if (perHourUsageMap2 == null) {
            perHourUsageMap = perHourUsageMap1;
        } else {
            perHourUsageMap = new ArrayMap<>();
            perHourUsageMap.putAll(perHourUsageMap1);
            PerHourUsage perHourUsage1;
            PerHourUsage perHourUsage2;
            for (Map.Entry<String, PerHourUsage> entry : perHourUsageMap2.entrySet()) {
                perHourUsage1 = perHourUsageMap.get(entry.getKey());
                if (perHourUsage1 == null) {
                    perHourUsageMap.put(entry.getKey(), entry.getValue());
                } else {
                    perHourUsage2 = entry.getValue();
                    perHourUsage1.setLaunchCount(perHourUsage1.getLaunchCount() + perHourUsage2.getLaunchCount());
                    perHourUsage1.setUsageTime(perHourUsage1.getUsageTime() + perHourUsage2.getUsageTime());
                    perHourUsageMap.put(entry.getKey(), perHourUsage1);
                }
            }
        }
        appUsage.setPerHourUsages(perHourUsageMap);
        return appUsage;
    }

    public void setPerHourUsages(Map<String, PerHourUsage> perHourUsages) {
        this.perHourUsages = perHourUsages;
    }

    public Map<String, PerHourUsage> getPerHourUsages() {
        return perHourUsages;
    }
    // KEEP METHODS END

}
