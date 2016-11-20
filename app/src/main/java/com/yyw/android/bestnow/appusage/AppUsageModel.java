package com.yyw.android.bestnow.appusage;

import com.yyw.android.bestnow.data.appusage.UsageRepository;
import com.yyw.android.bestnow.data.dao.AppUsage;

import java.util.Date;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by yangyongwen on 16/11/20.
 */

public class AppUsageModel implements AppUsageContract.Model {

    private UsageRepository usageRepository;

    @Inject
    AppUsageModel(UsageRepository repository){
        usageRepository=repository;
    }


    @Override
    public Observable<AppUsage> queryAppUsage(Date start, Date end) {

        return null;
    }

    @Override
    public void cleanUp() {
        usageRepository=null;
    }
}