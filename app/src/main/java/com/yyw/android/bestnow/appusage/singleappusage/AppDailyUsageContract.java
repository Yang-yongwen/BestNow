package com.yyw.android.bestnow.appusage.singleappusage;

import com.yyw.android.bestnow.appusage.dailyusage.DailyUsageContract;
import com.yyw.android.bestnow.archframework.BaseModel;
import com.yyw.android.bestnow.archframework.BasePresenter;
import com.yyw.android.bestnow.archframework.BaseView;
import com.yyw.android.bestnow.data.dao.AppUsage;
import com.yyw.android.bestnow.data.dao.PerHourUsage;

import java.util.Date;
import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * Created by yangyongwen on 16/12/3.
 */

public interface AppDailyUsageContract {

    interface View extends BaseView<Presenter> {
        void displayUsageData(List<PerHourUsage> perHourUsages);
        String getPackageName();
        Date getDate();
    }

    interface Presenter extends BasePresenter {
        void loadUsage(String packageName,Date date);
    }

    interface Model extends BaseModel {
        Observable<List<PerHourUsage>> queryAppDailyUsage(String packageName,Date date);
    }

}
