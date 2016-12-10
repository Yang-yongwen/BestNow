package com.yyw.android.bestnow.appusage.singleappusage;

import com.yyw.android.bestnow.data.dao.PerHourUsage;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

/**
 * Created by yangyongwen on 16/12/3.
 */

public class AppDailyUsagePresenter implements AppDailyUsageContract.Presenter {

    private AppDailyUsageContract.View view;
    private AppDailyUsageContract.Model model;
    private CompositeSubscription subscriptions;

    @Inject
    AppDailyUsagePresenter(AppDailyUsageContract.View view,AppDailyUsageContract.Model model){
        this.model=model;
        this.view=view;
        subscriptions=new CompositeSubscription();
    }

    @Inject
    void setupListeners(){
        view.setPresenter(this);
    }

    @Override
    public void start() {
        loadUsage(view.getPackageName(),view.getDate());
    }

    @Override
    public void loadUsage(String packageName,Date date) {
        Subscription subscription=model.queryAppDailyUsage(packageName,date)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
        subscriptions.add(subscription);
    }

    private final Observer<List<PerHourUsage>> observer=new Observer<List<PerHourUsage>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(List<PerHourUsage> perHourUsages) {
            view.displayUsageData(perHourUsages);
        }
    };

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        model.cleanUp();
        subscriptions.clear();
        model=null;
        view=null;
    }
}
