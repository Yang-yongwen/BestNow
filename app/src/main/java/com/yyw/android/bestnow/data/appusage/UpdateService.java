package com.yyw.android.bestnow.data.appusage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.view.Display;

import com.yyw.android.bestnow.NowApplication;
import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.common.utils.DateUtils;
import com.yyw.android.bestnow.common.utils.LogUtils;
import com.yyw.android.bestnow.common.utils.SPUtils;
import com.yyw.android.bestnow.data.dao.App;
import com.yyw.android.bestnow.executor.JobExecutor;
import com.yyw.android.bestnow.userinfo.activity.UserInfoActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yangyongwen on 16/12/12.
 */

public class UpdateService extends Service {
    private static final String TAG = LogUtils.makeLogTag(UpdateService.class);
    private static final String TYPE_FUNC = "type_func";
    private static final String FUNC_UPDATE = "func_update";
    private static final String FUNC_CHANGE_NOTIFICATION = "func_change_notification";
    public static final String SHOW_NOTIFICATION = "show_notification";
    private static final int NOTIFICATION_ID = 7;
    JobExecutor jobExecutor;
    AppUsageManager appUsageManager;
    UsageRepository usageRepository;
    private static boolean screenOn = false;
    Handler handler;
    AppUsageProviderNew appUsageProviderNew;
    boolean showNotification;
    static boolean firstStart = true;
    SPUtils spUtils;
    AppPool appPool;

    public static void startUpdate(Context context) {
        if (!screenOn) {
            if (firstStart) {
                firstStart = false;
                Intent intent = new Intent(context, UpdateService.class);
                context.startService(intent);
            }
            return;
        }
        Intent intent = new Intent(context, UpdateService.class);
        intent.putExtra(TYPE_FUNC, FUNC_UPDATE);
        context.startService(intent);
    }

    public static void showNotification(Context context) {
        Intent intent = new Intent(context, UpdateService.class);
        intent.putExtra(TYPE_FUNC, FUNC_CHANGE_NOTIFICATION);
        intent.putExtra(SHOW_NOTIFICATION, true);
        context.startService(intent);
    }

    public static void hideNotification(Context context) {
        Intent intent = new Intent(context, UpdateService.class);
        intent.putExtra(TYPE_FUNC, FUNC_CHANGE_NOTIFICATION);
        intent.putExtra(SHOW_NOTIFICATION, false);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        jobExecutor = NowApplication.getApplicationComponent().provideJobExecutor();
        appUsageManager = NowApplication.getApplicationComponent().provideAppUsageManager();
        appUsageProviderNew = NowApplication.getApplicationComponent().provideAppUsageProvider();
        usageRepository = NowApplication.getApplicationComponent().provideAppUsageRepository();
        spUtils = NowApplication.getApplicationComponent().provideSpUtils();
        appPool = NowApplication.getApplicationComponent().provideAppPool();

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenReceiver, filter);

        showNotification = spUtils.getBooleanValue("show_notification", true);
        if (showNotification) {
            Notification notification = buildNotification();
            startForeground(NOTIFICATION_ID, notification);
        }

        DisplayManager dm = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        for (Display display : dm.getDisplays()) {
            if (display.getState() == display.STATE_ON) {
                screenOn = true;
                break;
            }
        }
    }

    private static boolean isScreenOn(Context context) {
        DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        for (Display display : dm.getDisplays()) {
            if (display.getState() == display.STATE_ON) {
                return true;
            }
        }
        return false;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }
        String func = intent.getStringExtra(TYPE_FUNC);
        if (func == null) {
            return START_STICKY;
        }
        if (func.equals(FUNC_UPDATE)) {
            update();
        } else if (func.equals(FUNC_CHANGE_NOTIFICATION)) {
            boolean show = intent.getBooleanExtra(SHOW_NOTIFICATION, false);
            if (show != showNotification) {
                showNotification = show;
            }
            if (show) {
                Notification notification = buildNotification();
                startForeground(NOTIFICATION_ID, notification);
            } else {
                stopForeground(true);
            }
        }

//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.drawable.ic_do_not_disturb_on_white_24dp)
//                        .setContentTitle("My notification")
//                        .setContentText("Hello World!");
//
//        NotificationManager notificationManager=(NotificationManager)
//                this.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(1,mBuilder.build());
//
//        startForeground(1,mBuilder.build());
//
//        appUsageManager.update();

        return START_STICKY;
    }

    private void update() {
        appUsageManager.update();
        checkLastAppUsage();
    }

    private void updateNotificationIfNeed() {
        if (showNotification) {
            NotificationManager notificationManager = (NotificationManager)
                    this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, buildNotification());
        }
    }

    private Notification buildNotification() {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String msg = buildUsageMsg(usageRepository.getDailyUsageTimeAndLaunchCount(date));
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_do_not_disturb_on_white_24dp)
                        .setContentTitle("今日使用情况:")
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
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setPriority(Notification.PRIORITY_MIN);
        return mBuilder.build();
    }

    private String buildUsageMsg(long[] usage) {
        long launchCount = usage[0];
        long usageTime = usage[1];
        return "启动 " + String.valueOf(launchCount) + " 次，共计" + DateUtils.toDisplayFormat(usageTime);
    }


    private final BroadcastReceiver screenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
                for (Display display : dm.getDisplays()) {
                    if (display.getState() == display.STATE_ON) {
                        onScreenOn();
                        break;
                    }
                }
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
                for (Display display : dm.getDisplays()) {
                    if (display.getState() == display.STATE_ON) {
                        return;
                    }
                }
                onScreenOff();
            }
        }
    };


    private void onScreenOff() {
        LogUtils.d(TAG, "screen off");
        screenOn = false;
        appUsageManager.update();
    }

    private void checkLastAppUsage() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String lastEventPackage = appUsageProviderNew.lastEventPackage;
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                long time = usageRepository.getAppDailyUsageTime(lastEventPackage, date);

                checkUsage();

//                String msg = "当前应用：" + lastEventPackage + " 已经使用了" + DateUtils.toDisplayFormat(time);
//                Toast.makeText(UpdateService.this, msg, Toast.LENGTH_SHORT).show();
                updateNotificationIfNeed();
            }
        }, 200);
    }


    private void checkUsage() {
        String lastEventPackage = appUsageProviderNew.lastEventPackage;
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        long usageTime = usageRepository.getAppDailyUsageTime(lastEventPackage, date);
        App app = appPool.getLimitedApps().get(lastEventPackage);
        if (app != null && app.getLimitTime() < usageTime) {
            String msg = app.getLabel() + " 已经使用了" + DateUtils.toDisplayFormat(usageTime);
            showOverUsageNotification(msg);
//            showOverUsageLimitedDialog(app,msg);
        }
    }

    private void showOverUsageNotification(String msg) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_do_not_disturb_on_white_24dp)
                        .setContentTitle("应用已超时")
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
        mBuilder.setContentIntent(resultPendingIntent);


        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(soundUri);
        mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000});
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);


        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(5, mBuilder.build());
    }


    private void showOverUsageLimitedDialog(App app, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NowApplication.getInstance());
        builder.setTitle(R.string.over_limited_title)
                .setMessage(msg)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void onScreenOn() {
        LogUtils.d(TAG, "screen on");
        screenOn = true;
        appUsageManager.update(System.currentTimeMillis());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(screenReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
