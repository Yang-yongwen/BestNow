package com.yyw.android.bestnow.appusage;

import com.yyw.android.bestnow.appusage.activity.DailyUsageActivity;
import com.yyw.android.bestnow.archframework.FragmentScoped;

import dagger.Subcomponent;

/**
 * Created by yangyongwen on 16/11/27.
 */

@FragmentScoped
@Subcomponent(modules = UsageModule.class)
public interface AppUsageComponent {
    void inject(DailyUsageActivity activity);
}
