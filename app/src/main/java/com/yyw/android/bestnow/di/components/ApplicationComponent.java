package com.yyw.android.bestnow.di.components;

import com.yyw.android.bestnow.NowApplication;
import com.yyw.android.bestnow.appusage.dailyusage.DailyUsageComponent;
import com.yyw.android.bestnow.appusage.dailyusage.DailyUsageModule;
import com.yyw.android.bestnow.appusage.singleappusage.AppDailyUsageComponent;
import com.yyw.android.bestnow.appusage.singleappusage.SingleAppUsageModule;
import com.yyw.android.bestnow.data.appusage.AppUsageAgent;
import com.yyw.android.bestnow.di.modules.AppUsageModule;
import com.yyw.android.bestnow.di.modules.ApplicationModule;
import com.yyw.android.bestnow.di.modules.DaoDbModule;
import com.yyw.android.bestnow.userinfo.UserInfoComponent;
import com.yyw.android.bestnow.userinfo.UserInfoModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by samsung on 2016/10/27.
 */

@Singleton
@Component(modules = {ApplicationModule.class, AppUsageModule.class, DaoDbModule.class})
public interface ApplicationComponent {
//    void inject(UserInfoActivity splashActivity);

    void inject(NowApplication nowApplication);

    DailyUsageComponent plus(DailyUsageModule module);

    AppDailyUsageComponent plus(SingleAppUsageModule module);

    UserInfoComponent plus(UserInfoModule module);

    AppUsageAgent provideAppUsage();

}
