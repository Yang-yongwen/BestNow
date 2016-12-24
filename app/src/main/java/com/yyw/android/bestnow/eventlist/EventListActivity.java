package com.yyw.android.bestnow.eventlist;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.yyw.android.bestnow.NowApplication;
import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.archframework.BaseActivity;
import com.yyw.android.bestnow.common.utils.ActivityUtils;
import com.yyw.android.bestnow.common.utils.LogUtils;
import com.yyw.android.bestnow.data.dao.Event;
import com.yyw.android.bestnow.userinfo.fragment.DailyInfoFragment;

import java.util.List;

public class EventListActivity extends BaseActivity {
    private static final String TAG= LogUtils.makeLogTag(EventListActivity.class);

    EventListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpNavigationUp();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_event_list;
    }

    @Override
    protected void setupActivityComponent() {
        fragment = (EventListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_content);
        if (fragment==null){
            Intent intent=getIntent();
            String date=intent.getStringExtra("date");
            fragment=EventListFragment.newInstance(date);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.main_content);
        }
        NowApplication.getApplicationComponent().inject(this);
    }

    @Override
    protected boolean hasActionBar() {
        return true;
    }

}
