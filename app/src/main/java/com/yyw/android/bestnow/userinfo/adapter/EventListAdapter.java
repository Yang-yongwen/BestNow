package com.yyw.android.bestnow.userinfo.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.common.utils.DateUtils;
import com.yyw.android.bestnow.data.dao.Event;
import com.yyw.android.bestnow.data.event.EventRepository;
import com.yyw.android.bestnow.eventlist.EventListActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by yangyongwen on 2016/11/4.
 */

public class EventListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Event> todoEvents;
    List<Event> doneEvents;
    EventRepository eventRepository;
    Context context;
    String date;


    public EventListAdapter(List<Event> events, EventRepository eventRepository, Context context) {
        todoEvents = new ArrayList<>();
        doneEvents = new ArrayList<>();
        this.context = context;
        this.eventRepository = eventRepository;
        for (Event event : events) {
            if (event.getDone() != null && event.getDone() == true) {
                doneEvents.add(event);
            } else {
                todoEvents.add(event);
            }
        }
        date = events.size() == 0 ? null : events.get(0).getDate();
    }

    private class EventItemVH extends RecyclerView.ViewHolder {

        CheckBox selectBox;
        TextView eventContentTV;
        ImageView alarmIcon;
        TextView alarmTime;


        EventItemVH(View view) {
            super(view);
            selectBox = ButterKnife.findById(view, R.id.done_box);
            eventContentTV = ButterKnife.findById(view, R.id.event_content);
            alarmIcon = ButterKnife.findById(view, R.id.alarm_icon);
            alarmTime = ButterKnife.findById(view, R.id.alarm_time);

            eventContentTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String today = DateUtils.FORMAT_DAY.format(new Date());
                    if (!today.equals(date)) {
                        return;
                    }
                    Intent intent = new Intent(context, EventListActivity.class);
                    intent.putExtra("date", date);
                    context.startActivity(intent);
                }
            });
        }

        public void update(final Event event) {
            selectBox.setOnCheckedChangeListener(null);
            if (event.getDone() != null && event.getDone() == true) {
                selectBox.setChecked(true);
                int flags = eventContentTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG;
                eventContentTV.setPaintFlags(flags);
            } else {
                selectBox.setChecked(false);
                int flags = eventContentTV.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG);
                eventContentTV.setPaintFlags(flags);
            }
            eventContentTV.setText(event.getContent());
            if (event.getHasAlarm() != null && event.getHasAlarm() == true) {
                alarmIcon.setVisibility(View.VISIBLE);
                alarmTime.setVisibility(View.VISIBLE);
                alarmTime.setText(event.getAlarmTime().substring(11));
            } else {
                alarmIcon.setVisibility(View.INVISIBLE);
                alarmTime.setVisibility(View.INVISIBLE);
            }
            selectBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    selectBox.setOnCheckedChangeListener(null);
                    event.setDone(isChecked);
                    if (isChecked) {
                        todoEvents.remove(event);
                        doneEvents.add(event);
                    } else {
                        doneEvents.remove(event);
                        todoEvents.add(event);
                    }
                    eventRepository.saveEvent(event);
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_event_item, parent, false);
        return new EventItemVH(root);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EventItemVH) {
            Event event;
            if (position < todoEvents.size()) {
                event = todoEvents.get(position);
            } else {
                event = doneEvents.get(position - todoEvents.size());
            }
            ((EventItemVH) holder).update(event);
        }
    }

    @Override
    public int getItemCount() {
        return todoEvents.size() + doneEvents.size();
    }

}
