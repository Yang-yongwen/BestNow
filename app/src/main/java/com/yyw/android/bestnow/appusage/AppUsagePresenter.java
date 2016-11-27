package com.yyw.android.bestnow.appusage;

import com.yyw.android.bestnow.data.dao.AppUsage;

import java.util.Date;
import java.util.Map;

import javax.inject.Inject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by yangyongwen on 16/11/20.
 */

public class AppUsagePresenter implements AppUsageContract.Presenter {

    private AppUsageContract.View appUsageView;
    private AppUsageContract.Model appUsageModel;
    private CompositeSubscription subscriptions;

    @Inject
    AppUsagePresenter(AppUsageContract.View appUsageView,AppUsageContract.Model appUsageModel){
        this.appUsageView=appUsageView;
        this.appUsageModel=appUsageModel;
        subscriptions=new CompositeSubscription();
    }

    @Inject
    void setupListeners(){
        appUsageView.setPresenter(this);
    }

    @Override
    public void start() {
        Date date=new Date();
        loadUsage(date,date);
    }

    @Override
    public void loadUsage(Date start, Date end) {
        appUsageModel.queryAppUsage(start,end)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(appUsagesObserver);
    }

    private final Observer<Map<String,AppUsage>> appUsagesObserver=new Observer<Map<String, AppUsage>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(Map<String, AppUsage> appUsageMap) {
            appUsageView.displayUsageData(appUsageMap);
        }
    };

    @Override
    public void onResume() {
        start();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        appUsageModel.cleanUp();
        subscriptions.clear();
        appUsageModel=null;
        appUsageView=null;
    }

}
