package com.yyw.android.bestnow;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.squareup.otto.Bus;
import com.yyw.android.bestnow.common.utils.LogUtils;
import com.yyw.android.bestnow.common.utils.SPUtils;
import com.yyw.android.bestnow.data.appusage.AppInfoProvider;
import com.yyw.android.bestnow.data.appusage.AppUsageAgent;
import com.yyw.android.bestnow.data.event.EventRepository;
import com.yyw.android.bestnow.di.components.ApplicationComponent;
import com.yyw.android.bestnow.di.components.DaggerApplicationComponent;
import com.yyw.android.bestnow.di.modules.AppUsageModule;
import com.yyw.android.bestnow.di.modules.ApplicationModule;
import com.yyw.android.bestnow.di.modules.DaoDbModule;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

/**
 * Created by samsung on 2016/10/27.
 */

public class NowApplication extends Application {
    private static final String TAG=LogUtils.makeLogTag(NowApplication.class);
    private static final String INSTALL_DATE="install_date";
    private static ApplicationComponent applicationComponent;
    private static NowApplication instance;
    private boolean isInit;
    @Inject
    AppUsageAgent appUsageAgent;
    @Inject
    SPUtils spUtils;
    private String installDate;
    private Bus bus;
    @Inject
    EventRepository eventRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        bus=new Bus();
        LogUtils.init();
        instance = this;
        Stetho.initializeWithDefaults(this);
        initializeInjector();
        iniInstallDate();
        eventRepository.initEventSchedule();
    }

    private void iniInstallDate(){
        installDate=spUtils.getStringValue(INSTALL_DATE,null);
        if (installDate==null){
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd");
            installDate=simpleDateFormat.format(new Date());
            spUtils.putStringValue(INSTALL_DATE,installDate);
        }
    }

    public String getInstallDate(){
//        return "20161213";
     return installDate;
    }

    public Bus getBus(){
        return bus;
    }

    public static NowApplication getInstance() {
        return instance;
    }

    private void initializeInjector() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .daoDbModule(new DaoDbModule())
                .appUsageModule(new AppUsageModule())
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
