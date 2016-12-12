package com.yyw.android.bestnow;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.yyw.android.bestnow.common.utils.LogUtils;
import com.yyw.android.bestnow.common.utils.SPUtils;
import com.yyw.android.bestnow.data.appusage.AppInfoProvider;
import com.yyw.android.bestnow.data.appusage.AppUsageAgent;
import com.yyw.android.bestnow.di.components.ApplicationComponent;
import com.yyw.android.bestnow.di.components.DaggerApplicationComponent;
import com.yyw.android.bestnow.di.modules.ApplicationModule;

import javax.inject.Inject;

/**
 * Created by samsung on 2016/10/27.
 */

public class NowApplication extends Application {
    private static ApplicationComponent applicationComponent;
    private static NowApplication instance;
    private boolean isInit;
    @Inject
    AppUsageAgent appUsageAgent;
    @Inject
    SPUtils spUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Stetho.initializeWithDefaults(this);
        initializeInjector();
//        initAppUsage();
        LogUtils.init();
//        appUsageAgent.init();

    }

    public static NowApplication getInstance() {
        return instance;
    }

    private void initializeInjector() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        applicationComponent.inject(this);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level > TRIM_MEMORY_RUNNING_CRITICAL) {
            releaseUnNeedMem();
        }
    }

    private void releaseUnNeedMem() {
        AppInfoProvider.getInstance().clear();
    }

    public static ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

}
