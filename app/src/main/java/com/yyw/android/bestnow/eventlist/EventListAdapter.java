package com.yyw.android.bestnow.eventlist;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.data.dao.Event;
import com.yyw.android.bestnow.data.event.EventScheduler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangyongwen on 16/12/14.
 */

public class EventListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements EventItemView.EventEditListener {
    private static final int VIEW_TYPE_DONE_EDIT_ITEM = 1;
    private static final int VIEW_TYPE_DIVIDER = 2;
    private static final int VIEW_TYPE_TITLE = 3;
    private static final int VIEW_TYPE_TODO_EDIT_ITEM = 4;

    List<Event> doneEvents;
    List<Event> toDoEvents;
    Event editingEvent;

    Context context;
    LinearLayoutManager layoutManager;

    boolean shouldShowDoneEvents = true;
    boolean addingEvent = false;
    int newEventIndex = -1;
    int removeEventIndex = -1;


    public EventListAdapter(List<Event> events, LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        doneEvents = new ArrayList<>();
        toDoEvents = new ArrayList<>();
        for (Event event : events) {
            if (event.getDone() == null || event.getDone() == false) {
                toDoEvents.add(event);
            } else {
                doneEvents.add(event);
            }
        }
    }

    public List<Event> getEvents() {
        List<Event> events = new ArrayList<>();
        events.addAll(toDoEvents);
        if (events.size() > 0
                && events.get(events.size() - 1).getEdited() == null
                || events.get(events.size() - 1).getEdited() == false) {
            events.remove(events.size() - 1);
        }
        events.addAll(doneEvents);
        return events;
    }


    public static class EventEditItemVH extends RecyclerView.ViewHolder {
        EventItemView eventItemView;

        EventEditItemVH(View view) {
            super(view);
            eventItemView = (EventItemView) view;
        }
    }

    public static class EventTitleVH extends RecyclerView.ViewHolder {
        EventTitleVH(View view) {
            super(view);
        }
    }

    public class EventDividerVH extends RecyclerView.ViewHolder {
        @BindView(R.id.done_event_num_text)
        TextView doneEventNumTV;
        @BindView(R.id.arrow)
        View arrow;

        @OnClick(R.id.divider_container)
        void changeDividerState() {
            if (shouldShowDoneEvents) {
                shouldShowDoneEvents = false;
                ViewCompat.setRotation(arrow, 180);
            } else {
                shouldShowDoneEvents = true;
                ViewCompat.setRotation(arrow, 0);
            }
            notifyDataSetChanged();
        }

        EventDividerVH(View view) {
            super(view);
            ButterKnife.bind(this, view);
            doneEventNumTV.setText(doneEvents.size() + "个选中项");
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        if (viewType == VIEW_TYPE_TITLE) {
            View root = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_event_list_title, parent, false);
            return new EventTitleVH(root);
        } else if (viewType == VIEW_TYPE_DIVIDER) {
            View root = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_event_list_divider, parent, false);
            return new EventDividerVH(root);
        }

        EventItemView root = new EventItemView(parent.getContext());

        root.setEventEditListener(this);
        return new EventEditItemVH(root);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EventEditItemVH) {
            int viewType = getItemViewType(position);
            Event event;
            if (viewType == VIEW_TYPE_DONE_EDIT_ITEM) {
                event = doneEvents.get(position - 2 - toDoEvents.size());
            } else {
                event = toDoEvents.get(position - 1);
            }
            ((EventEditItemVH) holder).eventItemView.updateEvent(event);

            if (position == newEventIndex + 1) {
//                int lastPos=layoutManager.findLastCompletelyVisibleItemPosition();
//                if (position>lastPos){
//                    RecyclerView recyclerView;
//                    layoutManager.scrollToPosition(0);
//                }
                newEventIndex = -1;
                ((EventEditItemVH) holder).eventItemView.startEdit();
            }

            if (position == removeEventIndex) {
                removeEventIndex = -1;
                ((EventEditItemVH) holder).eventItemView.startEdit();
            }
        } else if (holder instanceof EventDividerVH) {
            ((EventDividerVH) holder).doneEventNumTV.setText(doneEvents.size() + "个选中项");
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_TITLE;
        } else if (doneEvents.size() == 0) {
            return VIEW_TYPE_TODO_EDIT_ITEM;
        } else if (position == toDoEvents.size() + 1) {
            return VIEW_TYPE_DIVIDER;
        } else if (position < toDoEvents.size() + 1) {
            return VIEW_TYPE_TODO_EDIT_ITEM;
        } else {
            return VIEW_TYPE_DONE_EDIT_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        if (doneEvents.size() == 0) {
            return toDoEvents.size() + 1;
        } else if (!shouldShowDoneEvents) {
            return toDoEvents.size() + 2;
        } else {
            return toDoEvents.size() + doneEvents.size() + 2;
        }
    }

    @Override
    public void startEdit(Event event) {

    }

    @Override
    public void removeEvent(Event currentEvent) {
        if (currentEvent.getDone() != null && currentEvent.getDone() == true) {
            for (int i = 0; i < doneEvents.size(); ++i) {
                if (currentEvent == doneEvents.get(i)) {
                    removeEventIndex = Math.max(i - 1, 0) + 2 + toDoEvents.size();
                    doneEvents.remove(i);
                    break;
                }
            }
        } else {
            for (int i = 0; i < toDoEvents.size(); ++i) {
                if (currentEvent == toDoEvents.get(i)) {
                    removeEventIndex = Math.max(i - 1, 0) + 1;
                    toDoEvents.remove(i);
                    if (toDoEvents.size() == 0) {
                        toDoEvents.add(new Event(System.currentTimeMillis()));
                        removeEventIndex = -1;
                    }
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void addNewEvent(Event currentEvent) {
        for (int i = 0; i < toDoEvents.size(); ++i) {
            if (currentEvent == toDoEvents.get(i)) {
                newEventIndex = i + 1;
                addingEvent = true;
                Event event = new Event(System.currentTimeMillis());
                event.setEdited(true);
                event.setContent("");
                toDoEvents.add(newEventIndex, event);
                notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void setEventAlarm(Event currentEvent) {
        editingEvent = currentEvent;
        showTimePickerDialog();
    }

    private TimePickerDialog timePickerDialog;

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        if (timePickerDialog == null) {
            timePickerDialog = new TimePickerDialog(context, listener,
                    currentHour, currentMinute, DateFormat.is24HourFormat(context));
            timePickerDialog.setTitle("请选择待办事项的提醒时间");
            timePickerDialog.setCanceledOnTouchOutside(true);
        } else {
            timePickerDialog.updateTime(currentHour, currentMinute);
        }
        timePickerDialog.show();
    }

    private final TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (isTimePass(hourOfDay, minute)) {
                showMessage("选择的提醒时间无效，请重新选择");
                return;
            }
            showMessage("hour: " + hourOfDay + " minute: " + minute);
            editingEvent.setHasAlarm(true);
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Calendar calendar=Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
            calendar.set(Calendar.MINUTE,minute);
            String alarmTime=simpleDateFormat.format(new Date(calendar.getTimeInMillis()));
            editingEvent.setAlarmTime(alarmTime);
            EventScheduler.getInstance().addEventJob(editingEvent);
            notifyDataSetChanged();
        }
    };

    private void showMessage(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    private boolean isTimePass(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        if (hourOfDay < currentHour) {
            return true;
        } else if (hourOfDay > currentHour) {
            return false;
        } else {
            return minute <= currentMinute;
        }
    }


    @Override
    public void eventStateChange(Event currentEvent) {
        if (currentEvent.getDone() != null && currentEvent.getDone() == true) {
            toDoEvents.remove(currentEvent);
            if (toDoEvents.size() == 0) {
                toDoEvents.add(new Event(System.currentTimeMillis()));
            }
            doneEvents.add(currentEvent);
        } else if (currentEvent.getDone() != null && currentEvent.getDone() == false) {
            doneEvents.remove(currentEvent);
            toDoEvents.add(0, currentEvent);
        }
        notifyDataSetChanged();
    }
}
