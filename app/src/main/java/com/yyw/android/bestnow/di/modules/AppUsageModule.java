package com.yyw.android.bestnow.di.modules;

import android.content.Context;

import com.yyw.android.bestnow.common.utils.SPUtils;
import com.yyw.android.bestnow.data.appusage.AppPool;
import com.yyw.android.bestnow.data.appusage.AppUsageAgent;
import com.yyw.android.bestnow.data.appusage.AppUsageManager;
import com.yyw.android.bestnow.data.appusage.UsageRepository;
import com.yyw.android.bestnow.data.dao.AppDao;
import com.yyw.android.bestnow.data.dao.AppUsageDao;
import com.yyw.android.bestnow.data.dao.PerHourUsageDao;
import com.yyw.android.bestnow.executor.JobExecutor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by samsung on 2016/10/28.
 */

@Module
public class AppUsageModule {

    /**
     * module 是输出某个模块的接口,对于不需要输出的接口,不应该暴露在此。
     * 暴露在此的接口,它们的生命周期是由相应的 component 控制。
     * 不暴露的部分,它们的生命周期应该在模块内部自己控制,该用单例的还是用单例。
     */

    @Provides
    @Singleton
    UsageRepository provideUsageRepository(AppUsageDao dao, JobExecutor executor, PerHourUsageDao perHourUsageDao) {
        return new UsageRepository(dao, executor, perHourUsageDao);
    }

    @Provides
    @Singleton
    AppUsageAgent provideAppUsageAgent(Context context, SPUtils spUtils,
                                       JobExecutor executor, UsageRepository repository,AppPool appPool) {
        return new AppUsageAgent(context, spUtils, executor, repository,appPool);
    }

    @Provides
    @Singleton
    AppUsageManager provideAppUsageManager(Context context,SPUtils spUtils,
                                           JobExecutor executor,UsageRepository repository,AppPool appPool){
        return new AppUsageManager(context,spUtils,executor,repository,appPool);
    }

    @Provides
    @Singleton
    AppPool provideAppPool(Context context, SPUtils spUtils, AppDao appDao){
        return new AppPool(context,spUtils,appDao);
    }

}
