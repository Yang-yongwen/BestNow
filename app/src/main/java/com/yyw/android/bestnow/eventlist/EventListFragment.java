package com.yyw.android.bestnow.eventlist;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.yyw.android.bestnow.NowApplication;
import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.archframework.BaseFragment;
import com.yyw.android.bestnow.data.dao.Event;
import com.yyw.android.bestnow.data.event.EventRepository;
import com.yyw.android.bestnow.userinfo.fragment.DailyInfoFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yangyongwen on 16/12/14.
 */

public class EventListFragment extends BaseFragment {

    @BindView(R.id.event_list)
    RecyclerView eventListRV;
    String date;

    @Inject
    EventRepository eventRepository;

    List<Event> events;
    EventListAdapter adapter;
    LinearLayoutManager layoutManager;

    private TimePickerDialog timePickerDialog;

    public static EventListFragment newInstance(String date){
        EventListFragment fragment=new EventListFragment();
        Bundle args=new Bundle();
        args.putString("date",date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        showTimePickerDialog();
        NowApplication.getApplicationComponent().inject(this);
        initArgs();
        initView();
    }

    private void initArgs(){
        Bundle args=getArguments();
        date=args.getString("date");
    }

    private List<Event> getEvents(){
        List<Event> events=eventRepository.getEvents(date);
        events.add(new Event(System.currentTimeMillis()));
        return events;
    }

    private void initView(){
        events=getEvents();
        layoutManager=new LinearLayoutManager(getContext());
        adapter=new EventListAdapter(events,layoutManager);
        eventListRV.setLayoutManager(layoutManager);
        eventListRV.setAdapter(adapter);
    }

    private void saveEvents(List<Event> events){
        for (Event event:events){
            event.setDate(date);
        }
        eventRepository.saveEvents(events,date);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        List<Event> events=adapter.getEvents();
        saveEvents(events);
        DailyInfoFragment.Events events1=new DailyInfoFragment.Events(events);
        NowApplication.getInstance().getBus().post(events1);
//        List<Event> events=adapter.getEvents();F
//        String eventsString=new Gson().toJson(events);
//        Intent data=new Intent();
//        data.putExtra("events",eventsString);
//        getActivity().setResult(DailyInfoFragment.REQUEST_CODE_EVENT,data);
//        saveEvents(events);
    }


    private void showTimePickerDialog(){
        Calendar calendar=Calendar.getInstance();
        int currentHour=calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute=calendar.get(Calendar.MINUTE);
        if (timePickerDialog==null){
            timePickerDialog= new TimePickerDialog(getContext(),listener,
                    currentHour,currentMinute,DateFormat.is24HourFormat(getContext()));
            timePickerDialog.setTitle("请选择待办事项的提醒时间");
            timePickerDialog.setCanceledOnTouchOutside(true);
        }else {
            timePickerDialog.updateTime(currentHour,currentMinute);
        }
        timePickerDialog.show();
    }

    private final TimePickerDialog.OnTimeSetListener listener=new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (isTimePass(hourOfDay,minute)){
                showMessage("选择的提醒时间无效，请重新选择");
                return;
            }
            showMessage("hour: "+hourOfDay+" minute: "+minute);
        }
    };

    private boolean isTimePass(int hourOfDay,int minute){
        Calendar calendar=Calendar.getInstance();
        int currentHour=calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute=calendar.get(Calendar.MINUTE);
        if (hourOfDay<currentHour){
            return true;
        }else if (hourOfDay>currentHour){
            return false;
        }else {
            return minute<=currentMinute;
        }
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_event_list;
    }
}
