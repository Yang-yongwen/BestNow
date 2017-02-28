package com.yyw.android.bestnow.appusage.singleappusage;

import com.yyw.android.bestnow.archframework.BaseModel;
import com.yyw.android.bestnow.archframework.BasePresenter;
import com.yyw.android.bestnow.archframework.BaseView;
import com.yyw.android.bestnow.data.dao.PerHourUsage;

import java.util.List;

import rx.Observable;

/**
 * Created by yangyongwen on 16/12/3.
 */

public interface AppDailyUsageContract {

    interface View extends BaseView<Presenter> {
        void displayUsageData(List<PerHourUsage> perHourUsages);

        String getPackageName();

        String getDate();
    }

    interface Presenter extends BasePresenter {
        void loadUsage(String packageName, String date);
    }

    interface Model extends BaseModel {
        Observable<List<PerHourUsage>> queryAppDailyUsage(String packageName, String date);
    }

}
