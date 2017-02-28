package com.yyw.android.bestnow.setting.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.yyw.android.bestnow.NowApplication;
import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.archframework.BaseActivity;
import com.yyw.android.bestnow.data.appusage.AppInfoProvider;
import com.yyw.android.bestnow.data.appusage.AppPool;
import com.yyw.android.bestnow.data.dao.App;
import com.yyw.android.bestnow.setting.adapter.AppCheckedListAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yangyongwen on 16/12/11.
 */

public class SettingCheckableAppActivity extends BaseActivity {

    @BindView(R.id.app_list)
    RecyclerView appListRV;

    @Inject
    AppPool appPool;

    AppCheckedListAdapter appCheckedListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpNavigationUp();
        setTitle("设置需要统计使用情况的App");
        initView();
    }

    private void initView() {
        initData();
        appListRV.setAdapter(appCheckedListAdapter);
        appListRV.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData() {
        appCheckedListAdapter = new AppCheckedListAdapter();
        List<PackageInfo> packageInfoList = getPackageManager()
                .getInstalledPackages(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        List<App> statisticApps = new ArrayList<>();
        List<App> unStatisticApps = new ArrayList<>();

        for (PackageInfo packageInfo : packageInfoList) {
            App app = appPool.get(packageInfo.packageName);
            if (app != null) {
                if (app.getShouldStatistic()) {
                    statisticApps.add(app);
                } else {
                    unStatisticApps.add(app);
                }
            } else {
                statisticApps.add(new App(packageInfo.packageName,
                        AppInfoProvider.getInstance().getAppLabel(packageInfo.packageName), true, false, -1l));
            }
        }
        appCheckedListAdapter.statisticApps = statisticApps;
        appCheckedListAdapter.unStatisticApps = unStatisticApps;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_setting_checkable_app;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appPool.saveApps(appCheckedListAdapter.statisticApps);
        appPool.saveApps(appCheckedListAdapter.unStatisticApps);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_statistic_app_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            appCheckedListAdapter.ensureDelete();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected void setupActivityComponent() {
        NowApplication.getApplicationComponent().inject(this);
    }
}
