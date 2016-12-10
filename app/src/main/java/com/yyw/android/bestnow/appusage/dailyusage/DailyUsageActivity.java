package com.yyw.android.bestnow.appusage.dailyusage;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;

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

    protected
    void setupActivityComponent(){
        DailyUsageFragment fragment=(DailyUsageFragment)getSupportFragmentManager()
                .findFragmentById(R.id.main_content);
        if (fragment==null){
            fragment= DailyUsageFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),fragment,R.id.main_content);
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
        return ACTION_BAR_TYPE_CALENDAR;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_daily_usage;
    }
}
