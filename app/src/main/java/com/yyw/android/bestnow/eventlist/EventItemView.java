package com.yyw.android.bestnow.eventlist;

import android.content.Context;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.common.utils.LogUtils;
import com.yyw.android.bestnow.data.dao.Event;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yangyongwen on 16/12/14.
 */

public class EventItemView extends RelativeLayout {
    private static final String TAG = LogUtils.makeLogTag(EventItemView.class);
    @BindView(R.id.event_icon)
    ImageView eventIcon;
    @BindView(R.id.done_box)
    CheckBox doneBox;
    @BindView(R.id.cancel_button)
    View cancelBtn;
    @BindView(R.id.alarm_icon)
    ImageView alarmIV;
    @BindView(R.id.alarm_time)
    TextView alarmTimeTV;
    @BindView(R.id.start_edit_button)
    View startEditBtn;
    @BindView(R.id.event_edit_text)
    EditText eventEditText;
    @BindView(R.id.event_hint)
    TextView eventHintTV;
    Event event;

    private EventEditListener eventEditListener;

    public EventItemView(Context context) {
        this(context, (AttributeSet) null);
    }

    public EventItemView(Context context, Event event) {
        this(context, (AttributeSet) null);
        this.event = event;
    }

    public void setEventEditListener(EventEditListener listener) {
        eventEditListener = listener;
    }

    public EventItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_event_edit_item, this, true);
        ButterKnife.bind(this);
//        event = new Event();
//        init();
    }

    public void updateEvent(Event event) {
        this.event = event;
        init();
        invalidate();
    }

    public void update() {
        init();
        invalidate();
    }

    private void init() {
//        if (event.getDone()!=null&&event.getDone()==true){
//            eventIcon.setVisibility(INVISIBLE);
//            alarmIV.setVisibility(INVISIBLE);
//            alarmTimeTV.setVisibility(INVISIBLE);
//            startEditBtn.setVisibility(INVISIBLE);
//            doneBox.setVisibility(VISIBLE);
//            doneBox.setChecked(true);
//            cancelBtn.setVisibility(INVISIBLE);
//            return;
//        }
        initEditBtn();
        initAlarmView();
        initEventEditView();
    }

    private void initEditBtn() {
        if (!isEventEdited()) {
            eventEditText.setText("");
            startEditBtn.setVisibility(VISIBLE);
            startEditBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventHintTV.setVisibility(GONE);
                    eventEditText.setVisibility(VISIBLE);
                    startEdit();
                }
            });
            doneBox.setVisibility(GONE);
        } else {
            startEditBtn.setVisibility(GONE);
        }
        if (event.getDone() != null && event.getDone() == true) {
            doneBox.setChecked(true);
            int flags = eventEditText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG;
            eventEditText.setPaintFlags(flags);
            eventIcon.setVisibility(INVISIBLE);
            alarmTimeTV.setVisibility(INVISIBLE);
            alarmIV.setVisibility(INVISIBLE);
        } else {
            doneBox.setChecked(false);
            int flags = eventEditText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG);
            eventEditText.setPaintFlags(flags);
            eventIcon.setVisibility(VISIBLE);
            initAlarmView();
        }
        doneBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                doneBox.setOnCheckedChangeListener(null);
                event.setDone(isChecked);
                if (eventEditListener != null) {
                    eventEditListener.eventStateChange(event);
                }
                if (isChecked) {
                    int flags = eventEditText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG;
                    eventEditText.setPaintFlags(flags);
                    eventIcon.setVisibility(INVISIBLE);
                    alarmTimeTV.setVisibility(INVISIBLE);
                    alarmIV.setVisibility(INVISIBLE);
                } else {
                    int flags = eventEditText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG);
                    eventEditText.setPaintFlags(flags);
                    eventIcon.setVisibility(VISIBLE);
                    initAlarmView();
                }
                if (event.getContent() != null) {
                    eventEditText.setText(event.getContent());
                }
                eventEditText.clearFocus();
                hideKeyboard();
                cancelBtn.setVisibility(INVISIBLE);
            }
        });
    }


    private boolean isEventEdited() {
        return !(event.getEdited() == null || event.getEdited() == false);
    }


    private void initEventEditView() {
        if (event.getEdited() == null || event.getEdited() == false) {
            eventHintTV.setVisibility(VISIBLE);
            eventEditText.setVisibility(INVISIBLE);
            eventEditText.setText("");
            eventHintTV.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventHintTV.setVisibility(GONE);
                    eventEditText.setVisibility(VISIBLE);
                    startEdit();
                }
            });
        } else {
            eventHintTV.setVisibility(GONE);
            eventEditText.setVisibility(VISIBLE);
            if (event.getContent() != null) {
                eventEditText.setText(event.getContent());
            }
        }
        OnFocusChangeListener listener = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    startEdit();
                } else {
                    cancelBtn.setVisibility(INVISIBLE);
//                    event.setContent(eventEditText.getText().toString());
                }
            }
        };
        eventEditText.setOnFocusChangeListener(listener);
        eventEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                event.setContent(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
//                event.setContent(s.toString());
            }
        });
        eventEditText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent e) {
                if (e.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                }
                if (e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event.getDone() != null && event.getDone() == true) {
                        hideKeyboard();
                        return true;
                    }
                    if (eventEditListener != null) {
                        eventEditListener.addNewEvent(event);
                        eventEditText.clearFocus();
                    }
                    LogUtils.d(TAG, "add new event");
                    return true;
                } else if (e.getKeyCode() == KeyEvent.KEYCODE_DEL
                        && TextUtils.isEmpty(eventEditText.getText().toString())) {
                    if (eventEditListener != null) {
                        eventEditListener.removeEvent(event);
                    }
                    LogUtils.d(TAG, "delete current event");
                    eventEditText.clearFocus();
                    return true;
                }
                return false;
            }
        });
    }

    private void initAlarmView() {
        if (event.getEdited() == null || event.getEdited() == false
                || (event.getDone() != null && event.getDone() == true)) {
            alarmIV.setVisibility(GONE);
            alarmTimeTV.setVisibility(GONE);
            return;
        }
        if (event.getHasAlarm() == null || event.getHasAlarm() == false) {
            alarmTimeTV.setVisibility(GONE);
            alarmIV.setVisibility(VISIBLE);
            alarmIV.setImageResource(R.drawable.ic_alarm_add_black_36dp);
        } else {
            alarmTimeTV.setVisibility(VISIBLE);
            alarmIV.setVisibility(VISIBLE);
            alarmIV.setImageResource(R.drawable.ic_alarm_black_36dp);
            if (event.getAlarmTime() != null) {
                alarmTimeTV.setText(event.getAlarmTime().substring(11));
            }
        }
        alarmIV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventEditListener != null) {
                    eventEditListener.setEventAlarm(event);
                }
            }
        });
    }

    public void startEdit() {
        event.setEdited(true);
        initAlarmView();
        startEditBtn.setVisibility(GONE);
        doneBox.setVisibility(VISIBLE);
        cancelBtn.setVisibility(VISIBLE);
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventEditListener != null) {
                    eventEditListener.removeEvent(event);
                }
            }
        });
        eventEditText.requestFocus();
        showKeyboard();
        if (eventEditListener != null) {
            eventEditListener.startEdit(event);
        }
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(eventEditText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindowToken(), 0);
        eventEditText.clearFocus();
    }

    public interface EventEditListener {
        void addNewEvent(Event currentEvent);

        void removeEvent(Event currentEvent);

        void eventStateChange(Event currentEvent);

        void setEventAlarm(Event currentEvent);

        void startEdit(Event event);
    }
}
