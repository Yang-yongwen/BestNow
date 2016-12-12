package com.yyw.android.bestnow.data.appusage;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.yyw.android.bestnow.NowApplication;
import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.executor.JobExecutor;

import javax.inject.Inject;

/**
 * Created by yangyongwen on 16/12/12.
 */

public class UpdateService extends Service {
    JobExecutor jobExecutor;
    AppUsageManager appUsageManager;

    public static void startUpdate(Context context){
        Intent intent=new Intent(context,UpdateService.class);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        jobExecutor= NowApplication.getApplicationComponent().provideJobExecutor();
        appUsageManager=NowApplication.getApplicationComponent().provideAppUsageManager();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_do_not_disturb_on_white_24dp)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");

//        NotificationManager notificationManager=(NotificationManager)
//                this.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(1,mBuilder.build());

        startForeground(1,mBuilder.build());

        appUsageManager.update();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
