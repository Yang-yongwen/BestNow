package com.yyw.android.bestnow.appusage.dailyusage;

import com.yyw.android.bestnow.archframework.BaseModel;
import com.yyw.android.bestnow.archframework.BasePresenter;
import com.yyw.android.bestnow.archframework.BaseView;
import com.yyw.android.bestnow.data.dao.AppUsage;

import java.util.Map;

import rx.Observable;

/**
 * Created by yangyongwen on 16/11/19.
 */

public interface DailyUsageContract {

    interface View extends BaseView<Presenter> {
        void displayUsageData(Map<String, AppUsage> appUsages);

        void showPieChart();

        void showListChart();
    }

    interface Presenter extends BasePresenter {
        void loadUsage(String start, String end);
    }

    interface Model extends BaseModel {
        Observable<Map<String, AppUsage>> queryAppUsage(String start, String end);
    }

}
