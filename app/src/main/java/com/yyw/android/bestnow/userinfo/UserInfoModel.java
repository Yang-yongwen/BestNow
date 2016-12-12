package com.yyw.android.bestnow.userinfo;

import android.util.ArrayMap;
import android.util.Pair;

import com.yyw.android.bestnow.data.appusage.UsageRepository;
import com.yyw.android.bestnow.data.dao.AppUsage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by yangyongwen on 16/12/10.
 */

public class UserInfoModel implements UserInfoContract.Model{
    UsageRepository repository;
    Map<String,List<AppUsage>> appUsagesCache;

    @Inject
    UserInfoModel(UsageRepository repository){
        this.repository=repository;
        appUsagesCache=new ArrayMap<>();
    }

    @Override
    public Observable<List<String>> queryEventList(final String date) {
        return null;
    }

    @Override
    public Observable<List<AppUsage>> queryTopAppUsages(final String date) {
        if (date.equals(getToday())){
            return repository.getAppUsageListObservable(date);
        }else if (appUsagesCache.containsKey(date)){
            return Observable.just(appUsagesCache.get(date));
        }else {
            return repository.getAppUsageListObservable(date)
                    .map(new Func1<List<AppUsage>, List<AppUsage>>() {
                        @Override
                        public List<AppUsage> call(List<AppUsage> appUsages) {
                            appUsagesCache.put(date,appUsages);
                            return appUsages;
                        }
                    });
        }
    }

    private String getToday(){
        SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
        return format.format(new Date());
    }

    @Override
    public void cleanUp() {

    }
}
