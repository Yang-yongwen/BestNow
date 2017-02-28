package com.yyw.android.bestnow.di.modules;

import android.content.Context;

import com.yyw.android.bestnow.common.utils.SPUtils;
import com.yyw.android.bestnow.data.appusage.AppPool;
import com.yyw.android.bestnow.data.appusage.AppUsageAgent;
import com.yyw.android.bestnow.data.appusage.AppUsageManager;
import com.yyw.android.bestnow.data.appusage.AppUsageProviderNew;
import com.yyw.android.bestnow.data.appusage.UsageRepository;
import com.yyw.android.bestnow.data.dao.AppDao;
import com.yyw.android.bestnow.data.dao.AppUsageDao;
import com.yyw.android.bestnow.data.dao.EventDao;
import com.yyw.android.bestnow.data.dao.PerHourUsageDao;
import com.yyw.android.bestnow.data.event.EventRepository;
import com.yyw.android.bestnow.executor.JobExecutor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yangyongwen on 2016/10/28.
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
    UsageRepository provideUsageRepository(AppUsageDao dao, JobExecutor executor,
                                           PerHourUsageDao perHourUsageDao, AppPool appPool) {
        return new UsageRepository(dao, executor, perHourUsageDao, appPool);
    }

    @Provides
    @Singleton
    AppUsageAgent provideAppUsageAgent(Context context, SPUtils spUtils,
                                       JobExecutor executor, AppUsageManager appUsageManager) {
        return new AppUsageAgent(context, spUtils, executor, appUsageManager);
    }

    @Provides
    @Singleton
    AppUsageManager provideAppUsageManager(Context context, SPUtils spUtils,
                                           JobExecutor executor, UsageRepository repository,
                                           AppUsageProviderNew appUsageProviderNew) {
        return new AppUsageManager(context, spUtils, executor, repository, appUsageProviderNew);
    }

    @Provides
    @Singleton
    AppPool provideAppPool(Context context, SPUtils spUtils, AppDao appDao) {
        return new AppPool(context, spUtils, appDao);
    }

    @Provides
    @Singleton
    EventRepository provideEventRepository(EventDao eventDao) {
        return new EventRepository(eventDao);
    }

    @Provides
    @Singleton
    AppUsageProviderNew providerAppUsageProvider(Context context, UsageRepository repository, SPUtils spUtils, AppPool appPool) {
        return new AppUsageProviderNew(context, repository, spUtils, appPool);
    }

}
