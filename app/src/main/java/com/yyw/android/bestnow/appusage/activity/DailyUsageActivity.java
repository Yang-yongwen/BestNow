package com.yyw.android.bestnow.appusage.activity;

import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.archframework.BaseActivity;

/**
 * Created by yangyongwen on 16/11/19.
 */

public class DailyUsageActivity extends BaseActivity {


    protected
    void setupActivityComponent(){

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
