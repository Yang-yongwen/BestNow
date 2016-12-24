package com.yyw.android.bestnow.userinfo;

import android.util.ArrayMap;

import com.yyw.android.bestnow.common.utils.LogUtils;
import com.yyw.android.bestnow.data.dao.AppUsage;
import com.yyw.android.bestnow.data.dao.Event;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by yangyongwen on 16/12/10.
 */

public class UserInfoPresenter implements UserInfoContract.Presenter{
    private static final String TAG=LogUtils.makeLogTag(UserInfoPresenter.class);
    UserInfoContract.Model model;
    Map<String,UserInfoContract.View> views;
    private CompositeSubscription subscriptions;

    @Inject
    UserInfoPresenter(UserInfoModel model){
        this.model=model;
        views=new ArrayMap<>();
        subscriptions=new CompositeSubscription();
    }

    @Override
    public void addView(String date, UserInfoContract.View view) {
        views.put(date,view);
        LogUtils.d(TAG,"add view: "+date);
    }

    @Override
    public void removeView(String date) {
        views.remove(date);
        LogUtils.d(TAG,"remove view: "+date);
    }

    @Override
    public void start() {

    }

    @Override
    public void loadTopAppUsages(final String date) {
        Subscription subscription=model.queryTopAppUsages(date)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<AppUsage>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.d(TAG,"loadTopAppUsages failed: "+e.getMessage());
                    }

                    @Override
                    public void onNext(List<AppUsage> appUsages) {
                        LogUtils.d(TAG,"loaded top appUsage: "+date);
                        UserInfoContract.View view=views.get(date);
                        sortAppUsages(appUsages);
                        if (view!=null){
                            view.displayTopAppUsages(appUsages);
                        }
                    }
                });
        subscriptions.add(subscription);
    }

    private void sortAppUsages(List<AppUsage> appUsageList) {
        Collections.sort(appUsageList, new Comparator<AppUsage>() {
            @Override
            public int compare(AppUsage o1, AppUsage o2) {
                if (o1.getTotalUsageTime() > o2.getTotalUsageTime()) {
                    return -1;
                } else if (o1.getTotalUsageTime() < o2.getTotalUsageTime()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }

    @Override
    public void loadEventList(final String date) {
        Subscription subscription=model.queryEventList(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Event>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Event> events) {
                        UserInfoContract.View view=views.get(date);
                        if (view!=null){
                            view.displayEventList(date,events);
                        }
                    }
                });
        subscriptions.add(subscription);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        subscriptions.clear();
        model.cleanUp();
        views.clear();
        views=null;
    }
}
