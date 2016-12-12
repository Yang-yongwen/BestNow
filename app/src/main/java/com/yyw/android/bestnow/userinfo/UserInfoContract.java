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

public interface UserInfoContract {

    interface View extends BaseView<Presenter> {
        void displayTopAppUsages(List<AppUsage> topAppUsages);
        void displayEventList(String date, List<String> events);
        String getDate();
    }

    interface Presenter extends BasePresenter {
        void loadTopAppUsages(String date);
        void loadEventList(String date);
        void addView(String date,View view);
        void removeView(String date);
    }

    interface Model extends BaseModel {
        Observable<List<AppUsage>> queryTopAppUsages(String date);

        Observable<List<String>> queryEventList(String date);
    }

}
