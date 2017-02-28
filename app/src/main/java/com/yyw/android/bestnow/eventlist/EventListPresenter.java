package com.yyw.android.bestnow.eventlist;

import com.yyw.android.bestnow.data.dao.Event;

import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by yangyongwen on 17/1/5.
 */

public class EventListPresenter implements EventListContract.Presenter {

    private EventListContract.View view;
    private EventListContract.Model model;
    private CompositeSubscription subscriptions;

    @Inject
    EventListPresenter(EventListContract.View view, EventListContract.Model model) {
        this.model = model;
        this.view = view;
        subscriptions = new CompositeSubscription();
    }

    @Inject
    void setupListeners() {
        view.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void loadEvents(String date) {
        Subscription subscription = model.queryEvents(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
        subscriptions.add(subscription);
    }

    private final Observer<List<Event>> observer = new Observer<List<Event>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(List<Event> events) {
            view.displayEvents(events);
        }
    };

    @Override
    public void saveEvents(List<Event> events, String date) {
        model.saveEvents(events, date);
    }

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
        view = null;
        model = null;
    }

}
