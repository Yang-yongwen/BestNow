package com.yyw.android.bestnow.appusage.singleappusage;

import com.yyw.android.bestnow.archframework.FragmentScoped;
import com.yyw.android.bestnow.data.appusage.UsageRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yangyongwen on 16/12/3.
 */
@Module

public class SingleAppUsageModule {
    private AppDailyUsageContract.View view;
    public SingleAppUsageModule(AppDailyUsageContract.View view){
        this.view=view;
    }

    @Provides
    @FragmentScoped
    AppDailyUsageContract.View providesAppDailyUsageContractView(){
        return view;
    }

    @Provides
    @FragmentScoped
    AppDailyUsageContract.Model providesAppDailyUsageContractModel(UsageRepository usageRepository){
        return new AppDailyUsageModel(usageRepository);
    }

}
