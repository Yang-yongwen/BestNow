package com.yyw.android.bestnow.setting.activity;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.yyw.android.bestnow.NowApplication;
import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.archframework.BaseActivity;
import com.yyw.android.bestnow.data.appusage.AppPool;
import com.yyw.android.bestnow.data.dao.App;
import com.yyw.android.bestnow.setting.adapter.AppLimitedListAdapter;
import com.yyw.android.bestnow.setting.adapter.AppListAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yangyongwen on 16/12/16.
 */

public class SettingAddTimeLimitActivity extends BaseActivity {

    @Inject
    AppPool appPool;

    @BindView(R.id.limited_app_list)
    RecyclerView limitedAppListRV;
    //    @BindView(R.id.app_list)
    RecyclerView appListRV;
    //    @BindView(R.id.app_list_container)
    ViewGroup appListContainer;

    EditText hourTV;
    EditText minTV;

    boolean showingAppList = false;
    boolean firstInit = true;
    ObjectAnimator animator;

    AlertDialog alertDialog;

    AppListAdapter appListAdapter;

    AppLimitedListAdapter appLimitedListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpNavigationUp();
        initView();
    }

    private void initView() {
        List<App> limitedApps = new ArrayList<>();
        limitedApps.addAll(appPool.getLimitedApps().values());
        appLimitedListAdapter = new AppLimitedListAdapter(limitedApps, appPool);
        limitedAppListRV.setAdapter(appLimitedListAdapter);
        limitedAppListRV.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_limit_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_limit) {
            showAppList();
        }
        return true;
    }

    private void showAppList() {
        if (alertDialog == null) {
            alertDialog = buildDialog();
        }
        alertDialog.show();
    }


    private AlertDialog buildDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.fragment_add_limited, null);

        hourTV = (EditText) view.findViewById(R.id.edit_hour);
        hourTV.setFilters(new InputFilter[]{new InputFilterMinMax(0, 23)});
        minTV = (EditText) view.findViewById(R.id.edit_minute);
        minTV.setFilters(new InputFilter[]{new InputFilterMinMax(0, 59)});

        appListRV = (RecyclerView) view.findViewById(R.id.app_list);
        List<App> statisticApps = new ArrayList<>();
        statisticApps.addAll(appPool.getStatisticAppsWithoutLimited().values());
        appListAdapter = new AppListAdapter(this, statisticApps);
        appListRV.setAdapter(appListAdapter);
        appListRV.setLayoutManager(new LinearLayoutManager(this));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.set_usage_limit_tilte)
                .setView(view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismissAppList();
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addLimitedApp(appListAdapter.getSelectedApp());
                        appListAdapter.deleteSelectedApp();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }

    private void addLimitedApp(App app) {
        app.setIsLimit(true);
        String hourStr = hourTV.getText().toString();
        long hour = hourStr.length() == 0 ? 0 : Long.parseLong(hourStr);
        String minStr = minTV.getText().toString();
        long minute = minStr.length() == 0 ? 0 : Long.parseLong(minStr);
        app.setLimitTime((hour * 60 + minute) * 60 * 1000);
        appPool.saveApps(app);
        appLimitedListAdapter.addApp(app);
    }

    private void dismissAppList() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (showingAppList) {
            dismissAppList();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void setupActivityComponent() {
        NowApplication.getApplicationComponent().inject(this);
    }

    @Override
    protected int actionBarType() {
        return super.actionBarType();
    }

    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_add_time_limit;
    }


}
