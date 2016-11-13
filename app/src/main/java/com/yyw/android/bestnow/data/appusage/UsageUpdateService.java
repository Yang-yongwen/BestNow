package com.yyw.android.bestnow.data.appusage;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.yyw.android.bestnow.NowApplication;

/**
 * Created by samsung on 2016/10/31.
 */

public class UsageUpdateService extends JobService {
    AppUsageAgent appUsageAgent;

    @Override
    public boolean onStartJob(JobParameters parameters) {
        if (appUsageAgent == null) {
            appUsageAgent = NowApplication.getApplicationComponent().provideAppUsage();
        }
        appUsageAgent.startUpdate();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters parameters) {
        return false;
    }
}
