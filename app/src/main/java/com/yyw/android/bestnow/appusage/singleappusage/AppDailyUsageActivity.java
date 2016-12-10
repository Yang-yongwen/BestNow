package com.yyw.android.bestnow.appusage.singleappusage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;

import com.yyw.android.bestnow.NowApplication;
import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.archframework.BaseActivity;
import com.yyw.android.bestnow.common.utils.ActivityUtils;

import javax.inject.Inject;

/**
 * Created by yangyongwen on 16/12/3.
 */

public class AppDailyUsageActivity extends BaseActivity {

    @Inject
    AppDailyUsagePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_app_daily_usage;
    }

    @Override
    protected void setupActivityComponent() {
        AppDailyUsageFragment fragment = (AppDailyUsageFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_content);
        if (fragment == null) {
            Intent intent = getIntent();
            String packageName = intent.getStringExtra(AppDailyUsageFragment.PACKAGE_NAME);
            long date = intent.getLongExtra(AppDailyUsageFragment.DATE, 0);
            fragment = AppDailyUsageFragment.newInstance(packageName, date);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.main_content);
        }

        NowApplication.getApplicationComponent()
                .plus(new SingleAppUsageModule(fragment))
                .inject(this);
    }

    @Override
    protected boolean hasActionBar() {
        return true;
    }
}
