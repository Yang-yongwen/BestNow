package com.yyw.android.bestnow.appusage.dailyusage;

import android.os.Bundle;

import com.yyw.android.bestnow.NowApplication;
import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.archframework.BaseActivity;
import com.yyw.android.bestnow.common.utils.ActivityUtils;

import javax.inject.Inject;

/**
 * Created by yangyongwen on 16/11/19.
 */

public class DailyUsageActivity extends BaseActivity {

    @Inject
    DailyUsagePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpNavigationUp();
    }

    protected void setupActivityComponent() {
        DailyUsageFragment fragment = (DailyUsageFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_content);
        if (fragment == null) {
            String date = getIntent().getStringExtra("date");
            fragment = DailyUsageFragment.newInstance(date);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.main_content);
        }

        NowApplication.getApplicationComponent()
                .plus(new DailyUsageModule(fragment))
                .inject(this);
    }

    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected int actionBarType() {
        return ACTION_BAR_TYPE_NORMAL;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_daily_usage;
    }
}
