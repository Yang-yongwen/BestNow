package com.yyw.android.bestnow.eventlist;

import com.yyw.android.bestnow.archframework.BaseModel;
import com.yyw.android.bestnow.archframework.BasePresenter;
import com.yyw.android.bestnow.archframework.BaseView;
import com.yyw.android.bestnow.data.dao.Event;

import java.util.List;

import rx.Observable;

/**
 * Created by yangyongwen on 17/1/5.
 */

public interface EventListContract {

    interface View extends BaseView<EventListContract.Presenter> {
        void displayEvents(List<Event> events);
    }

    interface Presenter extends BasePresenter {
        void loadEvents(String date);

        void saveEvents(List<Event> events, String date);
    }

    interface Model extends BaseModel {
        void saveEvents(List<Event> events, String date);

        Observable<List<Event>> queryEvents(String date);
    }

}
