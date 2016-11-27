package com.yyw.android.bestnow.appusage;

import com.yyw.android.bestnow.archframework.FragmentScoped;
import com.yyw.android.bestnow.data.appusage.UsageRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yangyongwen on 16/11/27.
 */

@Module
public class UsageModule {

    private AppUsageContract.View view;

    public UsageModule(AppUsageContract.View view){
        this.view=view;
    }

    @Provides
    @FragmentScoped
    AppUsageContract.View providesAppUsageContractView(){
        return view;
    }

    @Provides
    @FragmentScoped
    AppUsageContract.Model providesAppUsageContractModel(UsageRepository repository){
        return new AppUsageModel(repository);
    }

}
