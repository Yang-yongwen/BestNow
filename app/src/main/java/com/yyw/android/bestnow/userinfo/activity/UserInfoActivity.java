package com.yyw.android.bestnow.userinfo.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.yyw.android.bestnow.NowApplication;
import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.appusage.dailyusage.DailyUsageActivity;
import com.yyw.android.bestnow.archframework.BaseActivity;
import com.yyw.android.bestnow.common.utils.DateUtils;
import com.yyw.android.bestnow.common.utils.LogUtils;
import com.yyw.android.bestnow.common.utils.PermissionUtils;
import com.yyw.android.bestnow.data.appusage.AppUsageAgent;
import com.yyw.android.bestnow.userinfo.UserInfoContract;
import com.yyw.android.bestnow.userinfo.UserInfoModule;
import com.yyw.android.bestnow.userinfo.UserInfoPresenter;
import com.yyw.android.bestnow.userinfo.fragment.DailyPagerFragment;

import java.util.Date;

import javax.inject.Inject;

public class UserInfoActivity extends BaseActivity {
    private static final String TAG = LogUtils.makeLogTag(UserInfoActivity.class);
    @Inject
    AppUsageAgent appUsageAgent;
    @Inject
    UserInfoPresenter presenter;
    DailyPagerFragment pagerFragment;

    private AlertDialog usagePermissionDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Best Now");
        setSubtitle("今天");
        pagerFragment = (DailyPagerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_content);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof UserInfoContract.View) {
            UserInfoContract.View view = (UserInfoContract.View) fragment;
            view.setPresenter(presenter);
        }
    }

    public UserInfoContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    protected void onDayClick(final Date dateClicked) {
        if (!checkDate(dateClicked)) {
            showMessage("暂无数据，请选择有效日期");
            return;
        }
        setSubtitle(dateFormat.format(dateClicked));
        Intent intent = new Intent(this, DailyUsageActivity.class);
        intent.putExtra("date", DateUtils.FORMAT_DAY.format(dateClicked));
        startActivity(intent);
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                pagerFragment.changeToDate(dateClicked);
            }
        }, 200);
    }

    private boolean checkDate(Date date) {
        String installDateStr = NowApplication.getInstance().getInstallDate();
        Date installDate = null;
        try {
            installDate = DateUtils.FORMAT_DAY.parse(installDateStr);
        } catch (Exception e) {
        }
        if (date.getTime() < installDate.getTime()
                || date.getTime() > new Date().getTime()) {
            return false;
        } else {
            return true;
        }
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
        NowApplication.getApplicationComponent().plus(new UserInfoModule()).inject(this);
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
        if (!PermissionUtils.hasUsagePermission(this)) {
            if (usagePermissionDialog == null) {
                usagePermissionDialog = buildDialog();
            }
            if (!usagePermissionDialog.isShowing()) {
                usagePermissionDialog.show();
            }
        } else {
            if (!appUsageAgent.isUpdating()) {
                appUsageAgent.startUpdate();
            }
            checkStoragePermission();
        }
    }

    private AlertDialog buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }

    private void requestUsageStatsPermission() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    7);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == 7) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LogUtils.init();
            } else {
                finish();
            }
        }
    }
}
