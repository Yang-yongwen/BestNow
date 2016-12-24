package com.yyw.android.bestnow.data.event;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.common.utils.LogUtils;
import com.yyw.android.bestnow.userinfo.activity.UserInfoActivity;

/**
 * Created by samsung on 2016/12/19.
 */

public class ScheduleService extends IntentService {
    private static final String TAG = LogUtils.makeLogTag(ScheduleService.class);

    public ScheduleService() {
        super("ScheduleService");
    }

    protected void onHandleIntent(Intent intent) {
        onSchedule(intent);
        AlarmReceiver.completeWakefulIntent(intent);
    }

    private void onSchedule(Intent intent) {
        String content = intent.getStringExtra(EventScheduler.EVENT_CONTENT);
        String alarmTime = intent.getStringExtra(EventScheduler.EVENT_ALARM_TIME);

        Notification notification = buildNotification(alarmTime, content);
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(77, notification);
    }

    private Notification buildNotification(String alarmTime, String content) {
        String msg = alarmTime + ": " + content;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_do_not_disturb_on_white_24dp)
                        .setContentTitle("待办事项提醒:")
                        .setContentText(msg);
        Intent resultIntent = new Intent(this, UserInfoActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setVibrate(new long[]{1000,1000,1000,1000});
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

        mBuilder.setContentIntent(resultPendingIntent);
        return mBuilder.build();
    }

}
