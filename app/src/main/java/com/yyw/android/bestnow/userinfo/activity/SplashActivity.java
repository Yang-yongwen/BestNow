package com.yyw.android.bestnow.userinfo.activity;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.yyw.android.bestnow.NowApplication;
import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.archframework.BaseActivity;
import com.yyw.android.bestnow.common.utils.LogUtils;
import com.yyw.android.bestnow.common.utils.SPUtils;
import com.yyw.android.bestnow.data.appusage.AppUsageAgent;
import com.yyw.android.bestnow.data.appusage.AppUsageManager;
import com.yyw.android.bestnow.appusage.dailyusage.DailyUsageActivity;

import java.util.Date;

import javax.inject.Inject;

public class SplashActivity extends BaseActivity {
    private static final String TAG= LogUtils.makeLogTag(SplashActivity.class);
    @Inject
    Context context;
    @Inject
    AppUsageAgent appUsageAgent;
    @Inject
    SPUtils spUtils;
    private AlertDialog usagePermissionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (!hasUsagePermission()){
            if (usagePermissionDialog==null){
                usagePermissionDialog=buildDialog();
            }
            if (!usagePermissionDialog.isShowing()){
                usagePermissionDialog.show();
            }
        }else {
            if (!appUsageAgent.isInit()){
                appUsageAgent.init();
            }
        }
    }

    private AlertDialog buildDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(R.string.usage_permission_dialog_message)
                .setTitle(R.string.usage_permission_dialog_title)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestUsageStatsPermission();
                    }
                });
        AlertDialog alertDialog=builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }

    private boolean hasUsagePermission() {
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
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
