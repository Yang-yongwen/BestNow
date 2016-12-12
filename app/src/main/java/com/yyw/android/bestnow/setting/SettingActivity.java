package com.yyw.android.bestnow.setting;

import android.os.Bundle;
import android.view.Menu;

import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.archframework.BaseActivity;

/**
 * Created by yangyongwen on 16/12/11.
 */

public class SettingActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpNavigationUp();
        setTitle("设置");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void setupActivityComponent() {

    }

    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_setting;
    }
}
