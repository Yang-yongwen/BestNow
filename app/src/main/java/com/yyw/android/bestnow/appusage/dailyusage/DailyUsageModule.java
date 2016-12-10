package com.yyw.android.bestnow.appusage.dailyusage;

import com.yyw.android.bestnow.archframework.FragmentScoped;
import com.yyw.android.bestnow.data.appusage.UsageRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yangyongwen on 16/11/27.
 */

@Module
public class DailyUsageModule {

    private DailyUsageContract.View view;

    public DailyUsageModule(DailyUsageContract.View view){
        this.view=view;
    }

    @Provides
    @FragmentScoped
    DailyUsageContract.View providesAppUsageContractView(){
        return view;
    }

    @Provides
    @FragmentScoped
    DailyUsageContract.Model providesAppUsageContractModel(UsageRepository repository){
        return new AppUsageModel(repository);
    }

}
