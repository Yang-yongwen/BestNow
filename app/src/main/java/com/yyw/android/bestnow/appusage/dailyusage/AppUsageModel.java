package com.yyw.android.bestnow.appusage.dailyusage;

import com.yyw.android.bestnow.data.appusage.UsageRepository;
import com.yyw.android.bestnow.data.dao.AppUsage;

import java.util.Date;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by yangyongwen on 16/11/20.
 */

public class AppUsageModel implements DailyUsageContract.Model {

    private UsageRepository usageRepository;

    @Inject
    AppUsageModel(UsageRepository repository) {
        usageRepository = repository;
    }

    @Override
    public Observable<Map<String, AppUsage>> queryAppUsage(Date start, Date end) {
        return usageRepository.getAppUsageObservableIn(start, end);
    }

    @Override
    public void cleanUp() {
        usageRepository = null;
    }
}
