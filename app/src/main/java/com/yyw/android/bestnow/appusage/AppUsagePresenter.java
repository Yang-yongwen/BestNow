package com.yyw.android.bestnow.appusage;

import java.util.Date;

import javax.inject.Inject;

/**
 * Created by yangyongwen on 16/11/20.
 */

public class AppUsagePresenter implements AppUsageContract.Presenter {

    private AppUsageContract.View appUsageView;
    private AppUsageContract.Model appUsageModel;

    @Inject
    AppUsagePresenter(AppUsageContract.View appUsageView,AppUsageContract.Model appUsageModel){
        this.appUsageView=appUsageView;
        this.appUsageModel=appUsageModel;
    }

    @Inject
    void setupListeners(){
        appUsageView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void loadUsage(Date start, Date end) {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        appUsageModel.cleanUp();
        appUsageModel=null;
        appUsageView=null;
    }

}
