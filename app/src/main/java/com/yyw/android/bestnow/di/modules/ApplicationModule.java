package com.yyw.android.bestnow.di.modules;


import android.content.Context;

import com.yyw.android.bestnow.NowApplication;
import com.yyw.android.bestnow.common.utils.SPUtils;
import com.yyw.android.bestnow.executor.JobExecutor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by samsung on 2016/10/27.
 */

@Module
public class ApplicationModule {

    private final NowApplication application;

    public ApplicationModule(NowApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return this.application;
    }

    @Provides
    @Singleton
    SPUtils provideSPUtils() {
        return new SPUtils(application);
    }

    @Provides
    @Singleton
    JobExecutor provideJobExecutor() {
        return new JobExecutor();
    }


}
