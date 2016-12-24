package com.yyw.android.bestnow;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * RxBus is Event Bus module implemented by RxJava https://github.com/ReactiveX/RxJava
 *
 * post use {@link #post(Object)}, you should define a specific Object for every type event like {@link com.samsung.android.app.sreminder.cardproviders.reservation.train.ChooseTrainStationEvent}
 * post example {@link com.samsung.android.app.sreminder.cardproviders.reservation.train.StationChooserActivity#mlistView}
 *
 * subscribe use {@link #toObservable(Class)}
 * example {@link com.samsung.android.app.sreminder.cardproviders.reservation.train.TrainCardAgent#rxSubscription}
 * remember {@link Subscription#unsubscribe()} after usage to avoid memory leak. like {@link com.samsung.android.app.sreminder.cardproviders.reservation.train.TrainCardAgent#onDestroy(Context)}
 *
 * Please notice : RECOMMEND adding try-catch blocks for all kinds off subscribe operations {@link rx.Subscriber} {@link rx.functions.ActionN} {@link rx.functions.FuncN}
 * or {@link Subscription} will NOT receive the specific event any more when {@link Exception} occurs handling subscribed event.
 * for example:
 * RxBus.getDefault().toObservableSticky(EventSticky.class)
 *          .map(new Func1<EventSticky, EventSticky>() {
 *              @Override
 *              public EventSticky call(EventSticky eventSticky) {
 *                  try {
 *                      // operations...
 *                  } catch (Exception e) {
 *                      e.printStackTrace();
 *                  }
 *                  return eventSticky;
 *              }
 *          })
 *          .subscribe(new Action1<EventSticky>() {
 *              @Override
 *              public void call(EventSticky eventSticky) {
 *                  try {
 *                      // operations...
 *                  } catch (Exception e) {
 *                      e.printStackTrace();
 *                  }
 *              }
 *      });
 *
 * Sticky Event is not support for the moment.
 *
 * Created by zhouq.wang on 2016/8/19.
 */
public class RxBus {

    private final Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create());

    private static class SingletonHolder {
        private static RxBus instance = new RxBus();
    }

    /**
     * Lazy initialization and thread-safe
     *
     * @return Default RxBus Singleton
     */
    public static RxBus getDefault() {
        return SingletonHolder.instance;
    }


    private RxBus() {

    }

    public void post(Object o) {
        bus.onNext(o);
    }

    public <T> Observable<T> toObservable(Class<T> eventType) {
        return bus.ofType(eventType);
    }
}
