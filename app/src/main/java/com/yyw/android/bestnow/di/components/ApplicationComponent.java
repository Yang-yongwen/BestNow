package com.yyw.android.bestnow.di.components;

import com.yyw.android.bestnow.NowApplication;
import com.yyw.android.bestnow.appusage.dailyusage.DailyUsageComponent;
import com.yyw.android.bestnow.appusage.dailyusage.DailyUsageModule;
import com.yyw.android.bestnow.appusage.singleappusage.AppDailyUsageComponent;
import com.yyw.android.bestnow.appusage.singleappusage.SingleAppUsageModule;
import com.yyw.android.bestnow.archframework.BaseActivity;
import com.yyw.android.bestnow.common.utils.SPUtils;
import com.yyw.android.bestnow.data.appusage.AppPool;
import com.yyw.android.bestnow.data.appusage.AppUsageAgent;
import com.yyw.android.bestnow.data.appusage.AppUsageManager;
import com.yyw.android.bestnow.data.appusage.AppUsageProviderNew;
import com.yyw.android.bestnow.data.appusage.UsageRepository;
import com.yyw.android.bestnow.data.event.EventRepository;
import com.yyw.android.bestnow.di.modules.AppUsageModule;
import com.yyw.android.bestnow.di.modules.ApplicationModule;
import com.yyw.android.bestnow.di.modules.DaoDbModule;
import com.yyw.android.bestnow.eventlist.EventListActivity;
import com.yyw.android.bestnow.eventlist.EventListFragment;
import com.yyw.android.bestnow.executor.JobExecutor;
import com.yyw.android.bestnow.setting.activity.SettingAddTimeLimitActivity;
import com.yyw.android.bestnow.setting.activity.SettingCheckableAppActivity;
import com.yyw.android.bestnow.userinfo.UserInfoComponent;
import com.yyw.android.bestnow.userinfo.UserInfoModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by yangyongwen on 2016/10/27.
 */

@Singleton
@Component(modules = {ApplicationModule.class, AppUsageModule.class, DaoDbModule.class})
public interface ApplicationComponent {
//    void inject(UserInfoActivity splashActivity);

    void inject(NowApplication nowApplication);

    void inject(EventListFragment fragment);

    void inject(EventListActivity activity);

    void inject(SettingAddTimeLimitActivity activity);

    void inject(BaseActivity activity);

    void inject(SettingCheckableAppActivity appActivity);

    DailyUsageComponent plus(DailyUsageModule module);

    AppDailyUsageComponent plus(SingleAppUsageModule module);

    UserInfoComponent plus(UserInfoModule module);

    AppUsageAgent provideAppUsage();

    AppUsageManager provideAppUsageManager();

    AppUsageProviderNew provideAppUsageProvider();

    EventRepository provideEventRepository();

    JobExecutor provideJobExecutor();

    UsageRepository provideAppUsageRepository();

    SPUtils provideSpUtils();

    AppPool provideAppPool();

}
