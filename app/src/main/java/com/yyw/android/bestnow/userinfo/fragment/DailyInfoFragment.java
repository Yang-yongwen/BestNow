package com.yyw.android.bestnow.userinfo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.yyw.android.bestnow.NowApplication;
import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.appusage.singleappusage.AppDailyUsageActivity;
import com.yyw.android.bestnow.appusage.singleappusage.AppDailyUsageFragment;
import com.yyw.android.bestnow.archframework.BaseFragment;
import com.yyw.android.bestnow.data.dao.AppUsage;
import com.yyw.android.bestnow.data.dao.Event;
import com.yyw.android.bestnow.eventlist.EventListActivity;
import com.yyw.android.bestnow.userinfo.UserInfoContract;
import com.yyw.android.bestnow.userinfo.activity.UserInfoActivity;
import com.yyw.android.bestnow.userinfo.adapter.EventListAdapter;
import com.yyw.android.bestnow.view.TopUsageItemView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by samsung on 2016/11/3.
 */

public class DailyInfoFragment extends BaseFragment implements UserInfoContract.View {
    @BindView(R.id.top_usage_item_container)
    LinearLayout topUsageItemContainer;
    @BindView(R.id.event_type)
    TextView eventTypeTV;
    @BindView(R.id.event_list)
    RecyclerView eventListRV;
    @BindView(R.id.event_list_container)
    ViewGroup eventListContainer;
    @BindView(R.id.stub_text)
    View stubView;


    UserInfoContract.Presenter presenter;
    AppUsage[] topAppUsages = new AppUsage[3];
    TopUsageItemView[] itemViews = new TopUsageItemView[3];
    String date;


    public static DailyInfoFragment newInstance(String date) {
        DailyInfoFragment fragment = new DailyInfoFragment();
        Bundle args = new Bundle();
        args.putString("date", date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initArgs();
        initView();
        if (presenter == null) {
            presenter = ((UserInfoActivity) getActivity()).getPresenter();
        }
        presenter.addView(date, this);
        NowApplication.getInstance().getBus().register(this);
        presenter.loadTopAppUsages(date);
        presenter.loadEventList(date);
    }

    @Subscribe
    public void onReceiveEvents(Events events) {
        if (events == null || events.data == null || events.data.size() <= 0) {
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String today = simpleDateFormat.format(new Date());
        if (today.equals(date)) {
            displayEventList(date, events.data);
        }
    }

    public static class Events {
        public List<Event> data;

        public Events(List<Event> events) {
            data = events;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.loadTopAppUsages(date);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initArgs() {
        Bundle args = getArguments();
        if (args == null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            date = format.format(new Date());
        } else {
            date = args.getString("date");
        }
    }

    @Override
    public void displayTopAppUsages(List<AppUsage> topAppUsages) {
        long totalUsageTime = 0;
        for (AppUsage appUsage : topAppUsages) {
            totalUsageTime += appUsage.getTotalUsageTime();
        }
        for (int i = 0; i < 3; ++i) {
            itemViews[i].reset();
        }
        int size = topAppUsages.size() < 3 ? topAppUsages.size() : 3;
        for (int i = 0; i < size; ++i) {
            final AppUsage appUsage = topAppUsages.get(i);
            double percent = (double) appUsage.getTotalUsageTime() / (double) totalUsageTime;
            itemViews[i].setAppUsage(appUsage);
            itemViews[i].setUsagePercent(percent);
            itemViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAppDailyDetail(appUsage.getPackageName(), date);
                }
            });
        }
    }

    private void showAppDailyDetail(String packageName, String date) {
        Intent intent = new Intent(getActivity(), AppDailyUsageActivity.class);
        intent.putExtra(AppDailyUsageFragment.PACKAGE_NAME, packageName);
        intent.putExtra(AppDailyUsageFragment.DATE, date);
        startActivity(intent);
    }

    @Override
    public void displayEventList(String date, List<Event> events) {
        if (events.size() == 0) {
            return;
        }
        stubView.setVisibility(View.INVISIBLE);
        EventListAdapter eventListAdapter = new EventListAdapter(events, NowApplication.getApplicationComponent().provideEventRepository(), getActivity());
        eventListRV.setAdapter(eventListAdapter);
        eventListRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        eventListRV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EventListActivity.class);
                intent.putExtra("date", DailyInfoFragment.this.date);
                startActivity(intent);
            }
        });

    }

    @OnClick(R.id.event_list_container)
    void startEventListActivity() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String today = dateFormat.format(new Date());
        if (!today.equals(date)) {
            return;
        }
        Intent intent = new Intent(getActivity(), EventListActivity.class);
        intent.putExtra("date", date);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.removeView(date);
        presenter = null;
        NowApplication.getInstance().getBus().unregister(this);
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public void setPresenter(UserInfoContract.Presenter presenter) {
        this.presenter = presenter;
    }

    private void initView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(1,
                ViewGroup.LayoutParams.MATCH_PARENT, 1);
        for (int i = 0; i < 3; ++i) {
            itemViews[i] = new TopUsageItemView(getContext());
        }
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(1, ViewGroup.LayoutParams.WRAP_CONTENT);
        topUsageItemContainer.addView(itemViews[0], layoutParams);
        topUsageItemContainer.addView(new View(getContext()), dividerParams);
        topUsageItemContainer.addView(itemViews[1], layoutParams);
        topUsageItemContainer.addView(new View(getContext()), dividerParams);
        topUsageItemContainer.addView(itemViews[2], layoutParams);
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_daily_info;
    }
}
