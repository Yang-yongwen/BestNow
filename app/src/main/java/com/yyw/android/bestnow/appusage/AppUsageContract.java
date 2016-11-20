package com.yyw.android.bestnow.appusage;

import com.yyw.android.bestnow.archframework.BaseModel;
import com.yyw.android.bestnow.archframework.BasePresenter;
import com.yyw.android.bestnow.archframework.BaseView;
import com.yyw.android.bestnow.archframework.UserActionEnum;
import com.yyw.android.bestnow.data.dao.AppUsage;

import java.util.Date;

import rx.Observable;

/**
 * Created by yangyongwen on 16/11/19.
 */

public interface AppUsageContract {

    interface View extends BaseView<Presenter>{
        void displayUsageData(Date start, Date end, AppUsage appUsage);
        void showPieChart();
        void showListChart();
    }

    interface Presenter extends BasePresenter{
        void loadUsage(Date start,Date end);
    }

    interface Model extends BaseModel{
        Observable<AppUsage> queryAppUsage(Date start, Date end);
    }

}