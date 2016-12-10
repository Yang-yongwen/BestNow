package com.yyw.android.bestnow.archframework;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.common.utils.LogUtils;
import com.yyw.android.bestnow.common.utils.SPUtils;
import com.yyw.android.bestnow.data.appusage.AppUsageAgent;
import com.yyw.android.bestnow.view.behavior.DragBehavior;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * Created by samsung on 2016/10/27.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = LogUtils.makeLogTag(BaseActivity.class);
    public static final int ACTION_BAR_TYPE_CALENDAR = 0;
    public static final int ACTION_BAR_TYPE_NORMAL = 1;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    protected Toolbar toolbar;
    private TextView titleTV;
    private DragBehavior behavior;
    private TextView datePickerTextView;
    private ImageView arrowIV;
    private View datePickerBtn;
    private View mainContent;
    private CompactCalendarView calendarView;
    private CompactCalendarView.CompactCalendarViewListener calendarViewListener;
    @Inject
    AppUsageAgent appUsageAgent;
    @Inject
    SPUtils spUtils;
    private static int activityCount = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        ButterKnife.bind(this);
        setupActivityComponent();
        setUpActionBar();
    }

    private void setUpActionBar() {
        if (hasActionBar()) {
            switch (actionBarType()) {
                case ACTION_BAR_TYPE_CALENDAR:
                    setUpCalendarActionBar();
                    break;
                case ACTION_BAR_TYPE_NORMAL:
                    setUpNormalActionBar();
                    break;
            }
        }
    }

    private void setUpCalendarActionBar() {
        initCalendarToolbarView();
        initBehavior();
    }

    private void initCalendarToolbarView() {
        toolbar = ButterKnife.findById(this, R.id.toolbar);
        titleTV = ButterKnife.findById(this, R.id.title);
        datePickerTextView = ButterKnife.findById(this, R.id.date_picker_text_view);
        arrowIV = ButterKnife.findById(this, R.id.date_picker_arrow);
        datePickerBtn = ButterKnife.findById(this, R.id.date_picker_button);
        datePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (behavior.getState() == DragBehavior.STATE_COLLAPSED) {
                    behavior.setState(DragBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(DragBehavior.STATE_COLLAPSED);
                }
            }
        });
        calendarView = ButterKnife.findById(this, R.id.compactcalendar_view);
        calendarView.setLocale(TimeZone.getDefault(), Locale.CHINA);
        calendarView.setShouldDrawDaysHeader(true);
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                BaseActivity.this.onDayClick(dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                BaseActivity.this.onMonthScroll(firstDayOfNewMonth);
            }
        });
        setSubtitle(dateFormat.format(new Date()));
        setSupportActionBar(toolbar);
    }

    private void initBehavior() {
        mainContent = ButterKnife.findById(this, R.id.main_content);
        if (mainContent == null) {
            throw new IllegalStateException("With Calendar toolbar should " +
                    "has main content which id is main_content");
        }
        behavior = DragBehavior.from(mainContent);
        behavior.addScrollCallback(new DragBehavior.ScrollCalendarCallback() {
            @Override
            public void onStateChanged(@NonNull View appBarLayout, @DragBehavior.State int newState) {

            }

            @Override
            public void onSlide(@NonNull View appBarLayout, float slideOffset) {
                ViewCompat.setRotation(arrowIV, slideOffset * 180);
            }
        });
    }

    private void setUpNormalActionBar() {
        toolbar = ButterKnife.findById(this, R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        if (actionBarType() == ACTION_BAR_TYPE_NORMAL) {
            super.setTitle(title);
        } else if (titleTV != null) {
            titleTV.setText(title);
        }
    }

    public void setSubtitle(String subtitle) {
        if (datePickerTextView != null) {
            datePickerTextView.setText(subtitle);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (activityCount ==-1){
            LogUtils.d(TAG, "enter foreground");
            appUsageAgent.update();
            activityCount =0;
        }
        activityCount++;
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityCount--;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isCalendarBarExpanded()) {
            behavior.setState(DragBehavior.STATE_COLLAPSED);
        }

        if (activityCount==0){
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (activityCount == 0) {
                        activityCount = -1;
                        LogUtils.d(TAG, "enter background");
                    }
                }
            }, 100);
        }
    }

    @Override
    public void onBackPressed() {
        if (isCalendarBarExpanded()) {
            behavior.setState(DragBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    private boolean isCalendarBarExpanded() {
        return hasActionBar()
                && actionBarType() == ACTION_BAR_TYPE_CALENDAR
                && behavior != null
                && behavior.getState() == DragBehavior.STATE_EXPANDED;
    }

    protected void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void onDayClick(Date dateClicked) {

    }

    protected void onMonthScroll(Date firstDayOfNewMonth) {

    }

    protected int actionBarType() {
        return ACTION_BAR_TYPE_NORMAL;
    }

    protected abstract void setupActivityComponent();

    protected abstract int getContentView();

    protected abstract boolean hasActionBar();

    protected Toolbar getToolbar() {
        return toolbar;
    }
}
