package com.yyw.android.bestnow.appusage.dailyusage;

import com.yyw.android.bestnow.data.dao.AppUsage;

import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by yangyongwen on 16/11/20.
 */

public class DailyUsagePresenter implements DailyUsageContract.Presenter {

    private DailyUsageContract.View appUsageView;
    private DailyUsageContract.Model appUsageModel;
    private CompositeSubscription subscriptions;

    @Inject
    DailyUsagePresenter(DailyUsageContract.View appUsageView, DailyUsageContract.Model appUsageModel) {
        this.appUsageView = appUsageView;
        this.appUsageModel = appUsageModel;
        subscriptions = new CompositeSubscription();
    }

    @Inject
    void setupListeners() {
        appUsageView.setPresenter(this);
    }

    @Override
    public void start() {
//        Date date = new Date();
//        loadUsage(date, date);
    }

    @Override
    public void loadUsage(String start, String end) {
        Subscription subscribe = appUsageModel
                .queryAppUsage(start, end)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(appUsagesObserver);
        subscriptions.add(subscribe);
    }

    private final Observer<Map<String, AppUsage>> appUsagesObserver = new Observer<Map<String, AppUsage>>() {
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
//        start();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        appUsageModel.cleanUp();
        subscriptions.clear();
        appUsageModel = null;
        appUsageView = null;
    }

}
