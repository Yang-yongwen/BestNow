package com.yyw.android.bestnow.userinfo;

import com.yyw.android.bestnow.archframework.BaseModel;
import com.yyw.android.bestnow.archframework.BasePresenter;
import com.yyw.android.bestnow.archframework.BaseView;
import com.yyw.android.bestnow.data.dao.AppUsage;

import java.util.Date;
import java.util.List;

import rx.Observable;

/**
 * Created by yangyongwen on 16/12/6.
 */

public interface DailyInfoContract {

    interface View extends BaseView<Presenter>{
        void displayTopAppUsages(Date date, List<AppUsage> topAppUsages);
        void displayEventList(Date date,List<String> events);
    }

    interface Presenter extends BasePresenter {
        void loadTopAppUsages(Date date);
        void loadEventList(Date date);
    }

    interface Model extends BaseModel {
        Observable<List<AppUsage>> queryTopAppUsages(Date date);
        Observable<List<String>> queryEventList(Date date);
    }

}
