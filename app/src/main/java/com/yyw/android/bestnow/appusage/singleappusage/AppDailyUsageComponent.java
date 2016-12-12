package com.yyw.android.bestnow.appusage.singleappusage;

import com.yyw.android.bestnow.archframework.FragmentScoped;

import dagger.Subcomponent;

/**
 * Created by yangyongwen on 16/12/3.
 */
@FragmentScoped
@Subcomponent(modules = SingleAppUsageModule.class)
public interface AppDailyUsageComponent {
    void inject(AppDailyUsageActivity activity);
}
