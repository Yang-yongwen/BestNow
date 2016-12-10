package com.yyw.android.bestnow.appusage.dailyusage;

import com.yyw.android.bestnow.archframework.FragmentScoped;

import dagger.Subcomponent;

/**
 * Created by yangyongwen on 16/11/27.
 */

@FragmentScoped
@Subcomponent(modules = DailyUsageModule.class)
public interface DailyUsageComponent {
    void inject(DailyUsageActivity activity);
}
