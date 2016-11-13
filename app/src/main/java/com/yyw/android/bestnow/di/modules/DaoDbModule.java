package com.yyw.android.bestnow.di.modules;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.yyw.android.bestnow.data.dao.AppUsageDao;
import com.yyw.android.bestnow.data.dao.DaoMaster;
import com.yyw.android.bestnow.data.dao.DaoSession;
import com.yyw.android.bestnow.data.dao.PerHourUsageDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by samsung on 2016/10/28.
 */

@Module
public class DaoDbModule {

    @Provides
    @Singleton
    DaoMaster provideDaoMaster(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "BestNowDb", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster;
    }

    @Provides
    @Singleton
    DaoSession provideDaoSession(DaoMaster daoMaster) {
        return daoMaster.newSession();
    }

    @Provides
    @Singleton
    AppUsageDao provideAppUsageDao(DaoSession daoSession) {
        return daoSession.getAppUsageDao();
    }

    @Provides
    @Singleton
    PerHourUsageDao providePerHourUsageDao(DaoSession daoSession) {
        return daoSession.getPerHourUsageDao();
    }


}
