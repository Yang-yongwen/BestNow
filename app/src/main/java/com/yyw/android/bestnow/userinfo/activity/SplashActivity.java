package com.yyw.android.bestnow.userinfo.activity;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.yyw.android.bestnow.NowApplication;
import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.archframework.BaseActivity;
import com.yyw.android.bestnow.data.appusage.AppUsageManager;
import com.yyw.android.bestnow.appusage.activity.DailyUsageActivity;

import java.util.Date;

import javax.inject.Inject;

public class SplashActivity extends BaseActivity {
    @Inject
    Context context;
    @Inject
    AppUsageManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkUsagePermission()) {
            requestUsageStatsPermission();
        }
        setTitle("Best Now");
    }

    @Override
    protected void onDayClick(Date dateClicked) {
        setSubtitle(dateFormat.format(dateClicked));
        startActivity(new Intent(this,DailyUsageActivity.class));
    }

    @Override
    protected void onMonthScroll(Date firstDayOfNewMonth) {
        setSubtitle(dateFormat.format(firstDayOfNewMonth));
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_splash;
    }

    protected void setupActivityComponent() {
        NowApplication.getApplicationComponent().inject(this);
    }

    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected int actionBarType() {
        return ACTION_BAR_TYPE_CALENDAR;
    }

    private boolean checkUsagePermission() {
        boolean result = false;
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo info = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName);
            result = mode == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void requestUsageStatsPermission() {
        Toast.makeText(context, "请授予允许读取应用状态权限", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
