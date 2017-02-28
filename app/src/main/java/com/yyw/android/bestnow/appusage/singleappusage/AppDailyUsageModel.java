package com.yyw.android.bestnow.appusage.singleappusage;

import com.yyw.android.bestnow.data.appusage.UsageRepository;
import com.yyw.android.bestnow.data.dao.PerHourUsage;

import java.util.List;

import rx.Observable;

/**
 * Created by yangyongwen on 16/12/3.
 */

public class AppDailyUsageModel implements AppDailyUsageContract.Model {

    private UsageRepository usageRepository;

    public AppDailyUsageModel(UsageRepository usageRepository) {
        this.usageRepository = usageRepository;
    }

    @Override
    public Observable<List<PerHourUsage>> queryAppDailyUsage(String packageName, String date) {
        return usageRepository.getAppDailyPerHourUsagesObservable(packageName, date);
    }

    @Override
    public void cleanUp() {
        usageRepository = null;
    }
}
