package com.yyw.android.bestnow.data.event;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.yyw.android.bestnow.NowApplication;
import com.yyw.android.bestnow.common.utils.LogUtils;
import com.yyw.android.bestnow.data.dao.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by yangyongwen on 2016/12/19.
 */

public class EventScheduler {
    private static final String TAG = LogUtils.makeLogTag(EventScheduler.class);
    public static final String SCHEME = "bestnow";
    public static final String AUTHORITY = "com.yyw.android.bestnow.event.schedule";
    public static final String ACTION = "com.yyw.android.bestnow.event.schedule";
    public static final String EVENT_CONTENT = "event_content";
    public static final String EVENT_ALARM_TIME = "event_alarm_time";


    private Context context;

    private static class EventSchedulerHolder {
        public static final EventScheduler INSTANCE = new EventScheduler();
    }

    public static EventScheduler getInstance() {
        return EventSchedulerHolder.INSTANCE;
    }

    private EventScheduler() {
        context = NowApplication.getInstance();
    }

    public void addEventJob(Event event) {
        PendingIntent pendingIntent = genPendingIntent(event);
        long triggerAtMills = -1;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            triggerAtMills = simpleDateFormat.parse(event.getAlarmTime()).getTime();
        } catch (ParseException e) {
            LogUtils.d(TAG, "parse error: " + e.getMessage());
        }
        if (triggerAtMills > 0 && triggerAtMills >= System.currentTimeMillis() - 1000 * 20) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMills, pendingIntent);
        }
    }

    public void cancelEventJob(Event event) {
        PendingIntent pendingIntent = genPendingIntent(event);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private PendingIntent genPendingIntent(Event event) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME);
        builder.authority(AUTHORITY);
        builder.appendPath(String.valueOf(event.getCreateTime()));

        Uri data = builder.build();
        Intent intent = new Intent(ACTION);
        intent.setData(data);
        intent.putExtra(EVENT_CONTENT, event.getContent());
        intent.putExtra(EVENT_ALARM_TIME, event.getAlarmTime());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

}
