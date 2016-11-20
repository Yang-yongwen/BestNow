package com.yyw.android.bestnow.appusage.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.appusage.AppUsageContract;
import com.yyw.android.bestnow.archframework.BaseFragment;
import com.yyw.android.bestnow.data.dao.AppUsage;

import java.util.Date;

import butterknife.BindView;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by yangyongwen on 16/11/19.
 */

public class DailyUsageFragment extends BaseFragment implements AppUsageContract.View{
    AppUsageContract.Presenter presenter;
    @BindView(R.id.pie_chart)
    PieChartView pieChartView;
    @BindView(R.id.list_chart)
    RecyclerView recyclerView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pieChartView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showListChart(){
        if (!recyclerView.isShown()){
            recyclerView.setVisibility(View.VISIBLE);
            pieChartView.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void showPieChart(){
        if (!pieChartView.isShown()){
            pieChartView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void displayUsageData(Date start, Date end, AppUsage appUsage) {

    }

    @Override
    public void setPresenter(AppUsageContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_daily_usage;
    }
}