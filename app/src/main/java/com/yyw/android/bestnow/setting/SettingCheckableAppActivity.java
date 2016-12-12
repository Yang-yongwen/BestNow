package com.yyw.android.bestnow.setting;

import android.os.Bundle;
import android.view.Menu;

import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.archframework.BaseActivity;

/**
 * Created by yangyongwen on 16/12/11.
 */

public class SettingCheckableAppActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpNavigationUp();
        setTitle("设置需要统计使用情况的App");
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_setting_checkable_app;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected void setupActivityComponent() {

    }
}
