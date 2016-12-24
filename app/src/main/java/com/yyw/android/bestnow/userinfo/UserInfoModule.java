package com.yyw.android.bestnow.userinfo;

import com.yyw.android.bestnow.archframework.FragmentScoped;
import com.yyw.android.bestnow.data.appusage.UsageRepository;
import com.yyw.android.bestnow.data.event.EventRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yangyongwen on 16/12/10.
 */
@Module
public class UserInfoModule {

    @Provides
    @FragmentScoped
    UserInfoContract.Model providesUserInfoContractModel(UsageRepository repository, EventRepository eventRepository){
        return new UserInfoModel(repository,eventRepository);
    }

}
