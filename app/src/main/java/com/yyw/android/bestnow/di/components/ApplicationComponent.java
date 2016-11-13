package com.yyw.android.bestnow.di.components;

import com.yyw.android.bestnow.NowApplication;
import com.yyw.android.bestnow.data.appusage.AppUsageAgent;
import com.yyw.android.bestnow.di.modules.AppUsageModule;
import com.yyw.android.bestnow.di.modules.ApplicationModule;
import com.yyw.android.bestnow.di.modules.DaoDbModule;
import com.yyw.android.bestnow.ui.activity.SplashActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by samsung on 2016/10/27.
 */

@Singleton
@Component(modules = {ApplicationModule.class, AppUsageModule.class, DaoDbModule.class})
public interface ApplicationComponent {
    void inject(SplashActivity splashActivity);

    void inject(NowApplication nowApplication);

    AppUsageAgent provideAppUsage();

}
