package com.yyw.android.bestnow.appusage.dailyusage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.appusage.singleappusage.AppDailyUsageActivity;
import com.yyw.android.bestnow.appusage.singleappusage.AppDailyUsageFragment;
import com.yyw.android.bestnow.archframework.BaseFragment;
import com.yyw.android.bestnow.common.utils.DateUtils;
import com.yyw.android.bestnow.data.dao.AppUsage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yangyongwen on 16/11/19.
 */

public class DailyUsageFragment extends BaseFragment implements DailyUsageContract.View {
    DailyUsageContract.Presenter presenter;
    @BindView(R.id.pie_chart)
    PieChart pieChart;
    @BindView(R.id.list_chart)
    RecyclerView recyclerView;
    @BindView(R.id.pie_chart_container)
    ViewGroup pieChartContainer;
    @BindView(R.id.selected_app_icon)
    ImageView selectedAppIconIV;
    @BindView(R.id.selected_app_usage_time)
    TextView selectedAppLabelTV;
    @BindView(R.id.usage_chart_stub)
    View chartStubView;

    DailyUsageAdapter dailyUsageAdapter;
    List<Integer> pieColors;
    List<AppUsage> appUsages;
    AppUsage selectedAppUsage;
    String date;

    public static DailyUsageFragment newInstance(String date) {
        DailyUsageFragment fragment = new DailyUsageFragment();
        Bundle args = new Bundle();
        args.putString("date", date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
        showListChart();
        initPieChar();
        date = getArguments().getString("date");
        presenter.loadUsage(date, date);
    }

    private void initRecyclerView() {
        dailyUsageAdapter = new DailyUsageAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(dailyUsageAdapter);
    }

    private void initPieChar() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setCenterText("Usage Time");
        pieChart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(47f);
        pieChart.setTransparentCircleRadius(52f);
        pieChart.setDrawCenterText(true);
        pieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setOnChartValueSelectedListener(chartValueSelectedListener);
        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
        l.setEnabled(true);

        pieColors = new ArrayList<>();
//        for (int c : ColorTemplate.VORDIPLOM_COLORS)
//            pieColors.add(c);
//        for (int c : ColorTemplate.JOYFUL_COLORS)
//            pieColors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            pieColors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            pieColors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            pieColors.add(c);
//        pieColors.add(ColorTemplate.getHoloBlue());
    }

    private final OnChartValueSelectedListener chartValueSelectedListener = new OnChartValueSelectedListener() {
        @Override
        public void onValueSelected(Entry e, Highlight h) {
            if (e.getData() instanceof AppUsage) {
                selectedAppUsage = (AppUsage) e.getData();
                selectedAppIconIV.setImageDrawable(selectedAppUsage.getAppIcon());
                selectedAppLabelTV.setText(DateUtils.toDisplayFormat(selectedAppUsage.getTotalUsageTime()));
            }
        }

        @Override
        public void onNothingSelected() {

        }
    };

    @OnClick(R.id.show_list)
    void showList() {
        showListChart();
    }

    @Override
    public void showListChart() {
        if (!recyclerView.isShown()) {
            recyclerView.setVisibility(View.VISIBLE);
            pieChartContainer.setVisibility(View.INVISIBLE);
        }

    }

    @OnClick(R.id.show_pie)
    void showPie() {
        showPieChart();
    }

    @Override
    public void showPieChart() {
        if (!pieChartContainer.isShown()) {
            pieChartContainer.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void displayUsageData(Map<String, AppUsage> appUsageMap) {
        if (appUsageMap == null || appUsageMap.size() == 0) {
            chartStubView.setVisibility(View.VISIBLE);
            pieChartContainer.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
        chartStubView.setVisibility(View.GONE);
        if (!pieChartContainer.isShown() && !recyclerView.isShown()) {
            showListChart();
        }
        appUsages = toSortedList(appUsageMap.values());
        dailyUsageAdapter.setAppUsages(appUsages);
        dailyUsageAdapter.setDate(date);
        setPieData(appUsages);
    }

    private List<AppUsage> toSortedList(Collection<AppUsage> appUsages) {
        List<AppUsage> appUsageList = new ArrayList<>(appUsages);
        sortAppUsages(appUsageList);
        return appUsageList;
    }

    private void sortAppUsages(List<AppUsage> appUsageList) {
        Collections.sort(appUsageList, new Comparator<AppUsage>() {
            @Override
            public int compare(AppUsage o1, AppUsage o2) {
                if (o1.getTotalUsageTime() > o2.getTotalUsageTime()) {
                    return -1;
                } else if (o1.getTotalUsageTime() < o2.getTotalUsageTime()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }

    private void setPieData(List<AppUsage> appUsages) {
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        long totalUsageTime = 0;
        for (AppUsage appUsage : appUsages) {
            totalUsageTime += appUsage.getTotalUsageTime();
        }
        PieEntry entry;
        float restPercent = 1.0f;
        float percent;
        for (AppUsage appUsage : appUsages) {
            percent = (float) appUsage.getTotalUsageTime() / (float) totalUsageTime;
            restPercent -= percent;
            if (percent > 0.01f) {
                entry = new PieEntry(appUsage.getTotalUsageTime(),
                        percent > 0.02f ? appUsage.getLabel() : "", appUsage);
                entries.add(entry);
            }
            if (restPercent <= 0.02f && restPercent > 0.01f) {
                entry = new PieEntry(restPercent * totalUsageTime, "other");
                entries.add(entry);
                break;
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(7f);
        dataSet.setColors(pieColors);
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);
        // undo all highlights
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }

    @OnClick(R.id.selected_app_icon)
    void showSelectedAppDailyUsage() {
        Intent intent = new Intent(getActivity(), AppDailyUsageActivity.class);
        if (selectedAppUsage == null) {
            return;
        }
        intent.putExtra(AppDailyUsageFragment.PACKAGE_NAME, selectedAppUsage.getPackageName());
        intent.putExtra(AppDailyUsageFragment.DATE, date);
        startActivity(intent);
    }

    @Override
    public void setPresenter(DailyUsageContract.Presenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public void onStart() {
        super.onStart();
        presenter.loadUsage(date, date);
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
        presenter = null;
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_daily_usage;
    }
}
