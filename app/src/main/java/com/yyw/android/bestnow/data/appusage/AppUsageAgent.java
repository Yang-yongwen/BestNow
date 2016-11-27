package com.yyw.android.bestnow.data.appusage;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import com.yyw.android.bestnow.common.utils.LogUtils;
import com.yyw.android.bestnow.common.utils.SPUtils;
import com.yyw.android.bestnow.executor.JobExecutor;

import java.util.Calendar;

import javax.inject.Singleton;

/**
 * Created by samsung on 2016/10/31.
 */

@Singleton
public class AppUsageAgent {
    private static final String TAG = LogUtils.makeLogTag(AppUsageAgent.class);
    private static final int USAGE_UPDATE_JOB_ID = 0;
    Context context;
    private AppUsageManager appUsageManager;

    public AppUsageAgent(Context context, SPUtils spUtils, JobExecutor executor, UsageRepository repository) {
        appUsageManager = new AppUsageManager(context, spUtils, executor, repository);
        this.context = context;
    }

    public void init() {
        appUsageManager.init();
    }

    public void startUpdate() {
        LogUtils.d(TAG, "update!");
        appUsageManager.update();
        setNextUpdateSchedule();
    }

    public void update(){
        LogUtils.d(TAG, "update!");
        appUsageManager.update();
    }

    private void setNextUpdateSchedule() {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(USAGE_UPDATE_JOB_ID,
                new ComponentName(context.getPackageName(), UsageUpdateService.class.getName()));
        long latency = getNextUpdateTime() - System.currentTimeMillis();
        builder.setMinimumLatency(latency);
        builder.setOverrideDeadline(latency + 1000);
        builder.setRequiresDeviceIdle(true);
        try {
            jobScheduler.schedule(builder.build());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private long getNextUpdateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 10);
        calendar.add(Calendar.HOUR, 1);
//        calendar.add(Calendar.SECOND,5);
        return calendar.getTimeInMillis();
    }

}
