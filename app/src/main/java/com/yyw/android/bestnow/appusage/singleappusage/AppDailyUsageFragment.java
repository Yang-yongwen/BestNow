package com.yyw.android.bestnow.appusage.singleappusage;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.archframework.BaseFragment;
import com.yyw.android.bestnow.common.utils.DateUtils;
import com.yyw.android.bestnow.data.appusage.AppInfoProvider;
import com.yyw.android.bestnow.data.dao.PerHourUsage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * Created by yangyongwen on 16/12/3.
 */

public class AppDailyUsageFragment extends BaseFragment implements AppDailyUsageContract.View {
    public static final String PACKAGE_NAME = "package_name";
    public static final String DATE = "date";
    private static final long ONE_HOUR = 60 * 60 * 1000;
    @BindView(R.id.bar_char)
    BarChart barChart;
    @BindView(R.id.app_icon)
    ImageView appIconIV;
    @BindView(R.id.app_label)
    TextView appLabelTV;
    @BindView(R.id.app_usage)
    TextView appUsageTV;

    private String packageName;
    private Date date;
    private String dateString;
    private List<PerHourUsage> perHourUsages;

    private AppDailyUsageContract.Presenter presenter;

    public static AppDailyUsageFragment newInstance(String packageName, String date) {
        Bundle args = new Bundle();
        args.putString(PACKAGE_NAME, packageName);
        args.putString(DATE, date);
        AppDailyUsageFragment fragment = new AppDailyUsageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initArgs();
        initBarChart();
    }

    private void initArgs() {
        Bundle args = getArguments();
        packageName = args.getString(PACKAGE_NAME);
        dateString = args.getString(DATE);
        try {
            date=new SimpleDateFormat("yyyyMMdd").parse(dateString);
        }catch (ParseException e){

        }
    }

    private void initBarChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setMaxVisibleValueCount(24);
        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setOnChartValueSelectedListener(selectedListener);

        barChart.setDrawBarShadow(false);
        barChart.setDrawGridBackground(true);
        barChart.setDrawValueAboveBar(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(6);
        xAxis.setAxisMaximum(24f);
        xAxis.setAxisMinimum(0f);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(60f);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        MyMarkerView markerView = new MyMarkerView(getContext());
        markerView.setChartView(barChart);
        barChart.setMarker(markerView);

        barChart.getAxisLeft().setDrawGridLines(false);

        barChart.animateY(1000);
        barChart.getLegend().setEnabled(false);
    }

    public static class MyMarkerView extends MarkerView {
        private TextView textView;

        MyMarkerView(Context context) {
            super(context, R.layout.custom_marker_view);
            textView = (TextView) findViewById(R.id.tvContent);
        }

        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            textView.setText(String.valueOf((int) e.getY()) + "分钟");
            super.refreshContent(e, highlight);
        }

        @Override
        public MPPointF getOffset() {
            return new MPPointF(-(getWidth() / 2), -getHeight());
        }
    }

    private final OnChartValueSelectedListener selectedListener = new OnChartValueSelectedListener() {
        @Override
        public void onValueSelected(Entry e, Highlight h) {
//            showMessage(String.valueOf(e.getX()));
        }

        @Override
        public void onNothingSelected() {

        }
    };

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public void displayUsageData(List<PerHourUsage> perHourUsages) {
        this.perHourUsages = perHourUsages;
        setAppInfo();
        setAppUsage();
        setBarChartDate(perHourUsages);
    }

    private void setAppInfo() {
        Drawable appIcon = AppInfoProvider.getInstance().getAppIcon(packageName);
        String appLabel = AppInfoProvider.getInstance().getAppLabel(packageName);
        appIconIV.setImageDrawable(appIcon);
        appLabelTV.setText(appLabel);
    }

    private void setAppUsage() {
        long totalTime = 0;
        for (PerHourUsage perHourUsage : perHourUsages) {
            totalTime += perHourUsage.getUsageTime();
        }
        String total = DateUtils.toDisplayFormat(totalTime);
        appUsageTV.setText(total);
    }

    private void setBarChartDate(List<PerHourUsage> perHourUsages) {
        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        for (int i = 0; i < 24; ++i) {
            entries.add(new BarEntry(i, 0));
        }
        int index;
        int minutes;
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH");
        for (PerHourUsage perHourUsage : perHourUsages) {
            long hourTime = perHourUsage.getTime();
            String hour = dateFormat.format(new Date(hourTime));
            index = Integer.parseInt(hour);
            minutes = (int) Math.floor((double) perHourUsage.getUsageTime() / 1000.0 / 60.0);
            minutes %= 60;
            entries.set(index, new BarEntry(index, minutes));
        }
        BarDataSet set;
        set = new BarDataSet(entries, "Data Set");
        set.setColors(Color.BLUE);
        set.setDrawValues(true);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set);

        BarData data = new BarData(dataSets);
        barChart.setData(data);
        barChart.setFitBars(true);

        barChart.invalidate();
    }

    @Override
    public void setPresenter(AppDailyUsageContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_app_daily_usage;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.start();
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
}
