package com.yyw.android.bestnow.data.appusage;

import android.content.Context;

import com.yyw.android.bestnow.common.utils.LogUtils;
import com.yyw.android.bestnow.common.utils.PermissionUtils;
import com.yyw.android.bestnow.common.utils.SPUtils;
import com.yyw.android.bestnow.data.dao.AppUsage;
import com.yyw.android.bestnow.data.dao.PerHourUsage;
import com.yyw.android.bestnow.executor.JobExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by yangyongwen on 2016/10/28.
 */
@Singleton
public class AppUsageManager {
    private static final String TAG = LogUtils.makeLogTag(AppUsageManager.class);
    Context context;
    SPUtils spUtils;
    JobExecutor executor;

    AppUsageProviderNew appUsageProvider;
    UsageRepository repository;

    @Inject
    public AppUsageManager(Context context, SPUtils spUtils,
                           JobExecutor executor, UsageRepository repository,
                           AppUsageProviderNew appUsageProviderNew) {
        this.context = context;
        this.spUtils = spUtils;
        this.executor = executor;
        this.repository = repository;
        this.appUsageProvider = appUsageProviderNew;
    }

    public synchronized void update() {
        if (!PermissionUtils.hasUsagePermission(context)) {
            LogUtils.d(TAG, "no usage state permission");
            return;
        }
        Map<String, AppUsage> updatedAppUsages = appUsageProvider.getAppUsageSinceLastUpdate();
        updateAppUsage(updatedAppUsages);
        updatePerHourUsageNew(updatedAppUsages);
    }

    public synchronized void update(long lastUpdateTime) {
        if (!PermissionUtils.hasUsagePermission(context)) {
            LogUtils.d(TAG, "no usage state permission");
            return;
        }
        Map<String, AppUsage> updatedAppUsages = appUsageProvider.getAppUsageSinceLastUpdate(lastUpdateTime);
        updateAppUsage(updatedAppUsages);
        updatePerHourUsageNew(updatedAppUsages);
    }


    private void updateAppUsage(Map<String, AppUsage> updatedAppUsages) {
        AppUsage updatedAppUsage;
        AppUsage oldAppUsage;
        for (Map.Entry<String, AppUsage> entry : updatedAppUsages.entrySet()) {
            updatedAppUsage = entry.getValue();
            oldAppUsage = repository.getAppUsage(entry.getKey());

            if (oldAppUsage == null) {
                repository.saveAppUsage(updatedAppUsage);
            } else {
                int totalLaunchCount = oldAppUsage.getTotalLaunchCount() + updatedAppUsage.getTotalLaunchCount();
                long totalUsageTime = oldAppUsage.getTotalUsageTime() + updatedAppUsage.getTotalUsageTime();

                oldAppUsage.setUpdateTime(updatedAppUsage.getUpdateTime());
                oldAppUsage.setLastTimeUsed(updatedAppUsage.getLastTimeUsed());
                oldAppUsage.setTotalUsageTime(totalUsageTime);
                oldAppUsage.setTotalLaunchCount(totalLaunchCount);
                repository.saveAppUsage(oldAppUsage);
            }
        }
    }

    private void updatePerHourUsageNew(final Map<String, AppUsage> updatedAppUsages) {
        List<PerHourUsage> updatedPerHourUsages = new ArrayList<>();
        for (Map.Entry<String, AppUsage> appUsageEntry : updatedAppUsages.entrySet()) {
            String packageName = appUsageEntry.getKey();
            Map<String, PerHourUsage> perHourUsageMap = appUsageEntry.getValue().getPerHourUsages();
            for (Map.Entry<String, PerHourUsage> perHourUsageEntry : perHourUsageMap.entrySet()) {
                PerHourUsage updatedPerHourUsage = perHourUsageEntry.getValue();
                PerHourUsage original = repository.getPerHourUsage(packageName, updatedPerHourUsage.getDate(), updatedPerHourUsage.getHour());
                if (original == null) {
                    updatedPerHourUsages.add(updatedPerHourUsage);
                } else {
                    long usageTime = updatedPerHourUsage.getUsageTime() + original.getUsageTime();
                    int launchCount = updatedPerHourUsage.getLaunchCount() + original.getLaunchCount();
                    original.setUsageTime(usageTime);
                    original.setLaunchCount(launchCount);
                    updatedPerHourUsages.add(original);
                }
            }
        }
        PerHourUsage[] perHourUsages = new PerHourUsage[updatedPerHourUsages.size()];
        perHourUsages = updatedPerHourUsages.toArray(perHourUsages);
        repository.savePerHourUsage(perHourUsages);
    }

}
