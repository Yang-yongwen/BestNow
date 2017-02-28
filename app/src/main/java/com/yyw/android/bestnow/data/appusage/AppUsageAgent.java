package com.yyw.android.bestnow.data.appusage;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import com.yyw.android.bestnow.common.utils.DateUtils;
import com.yyw.android.bestnow.common.utils.LogUtils;
import com.yyw.android.bestnow.common.utils.SPUtils;
import com.yyw.android.bestnow.executor.JobExecutor;

import java.util.Calendar;

import javax.inject.Singleton;

/**
 * Created by yangyongwen on 2016/10/31.
 */

@Singleton
public class AppUsageAgent {
    private static final String TAG = LogUtils.makeLogTag(AppUsageAgent.class);
    private static final int USAGE_UPDATE_JOB_ID = 0;
    Context context;
    SPUtils spUtils;
    JobExecutor jobExecutor;
    AppUsageManager appUsageManager;
    private boolean isUpdating = false;

    public AppUsageAgent(Context context, SPUtils spUtils,
                         JobExecutor executor, AppUsageManager appUsageManager) {
        this.appUsageManager = appUsageManager;
        this.context = context;
        this.spUtils = spUtils;
        this.jobExecutor = executor;
    }

    public boolean isUpdating() {
        return isUpdating;
    }

    public void startUpdate() {
        isUpdating = true;
        LogUtils.d(TAG, "update, time: " + DateUtils.formatTime(System.currentTimeMillis()));
//        appUsageManager.update();
        UpdateService.startUpdate(context);
        setNextUpdateSchedule();
    }

    public void update() {
        LogUtils.d(TAG, "update, time: " + DateUtils.formatTime(System.currentTimeMillis()));
//        appUsageManager.update();
        UpdateService.startUpdate(context);
    }

    private void setNextUpdateSchedule() {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(USAGE_UPDATE_JOB_ID,
                new ComponentName(context.getPackageName(), JobScheduleService.class.getName()));
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
//        calendar.add(Calendar.MINUTE, 1);
//        calendar.set(Calendar.SECOND, 10);
//        calendar.add(Calendar.HOUR, 1);
        calendar.add(Calendar.SECOND, 20);
        long time = calendar.getTimeInMillis();
        LogUtils.d(TAG, "next update time is: " + DateUtils.formatTime(time));
        return time;
    }

}
