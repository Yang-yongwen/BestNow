package com.yyw.android.bestnow.ui.activity;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yyw.android.bestnow.NowApplication;
import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.data.appusage.AppUsageManager;
import com.yyw.android.bestnow.ui.behavior.DragBehavior;
import com.yyw.android.bestnow.ui.behavior.ScrollCalendarBehavior;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
    private DragBehavior behavior;
    @Inject
    Context context;
    @Inject
    AppUsageManager manager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.title)
    TextView titleTV;
    @BindView(R.id.date_picker_text_view)
    TextView datePickerTextView;
    @BindView(R.id.date_picker_arrow)
    ImageView arrowIV;
    @BindView(R.id.date_picker_button)
    View datePickerBtn;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.main_content)
    View mainContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (!checkUsagePermission()) {
            requestUsageStatsPermission();
        }
        ButterKnife.bind(this);
        initBehavior();
        initActionBar();
    }


    private void initActionBar(){
        setSupportActionBar(toolbar);
        setTitle("BestNow");
        setSubtitle(dateFormat.format(new Date()));
        datePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (behavior.getState()==DragBehavior.STATE_COLLAPSED){
                    behavior.setState(DragBehavior.STATE_EXPANDED);
                }else {
                    behavior.setState(DragBehavior.STATE_COLLAPSED);
                }
            }
        });
    }

    private void initBehavior(){
        behavior= DragBehavior.from(mainContent);
        behavior.addScrollCallback(new DragBehavior.ScrollCalendarCallback() {
            @Override
            public void onStateChanged(@NonNull View appBarLayout, @ScrollCalendarBehavior.State int newState) {

            }

            @Override
            public void onSlide(@NonNull View appBarLayout, float slideOffset) {
                ViewCompat.setRotation(arrowIV,slideOffset*180);
            }
        });
    }

    @Override
    public void setTitle(CharSequence title) {
        if (titleTV != null) {
            titleTV.setText(title);
        }
    }

    public void setSubtitle(String subtitle) {
        if (datePickerTextView != null) {
            datePickerTextView.setText(subtitle);
        }
    }

    protected void setupActivityComponent() {
        NowApplication.getApplicationComponent().inject(this);
    }

    private boolean checkUsagePermission() {
        boolean result = false;
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo info = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName);
            result = mode == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void requestUsageStatsPermission() {
        Toast.makeText(context, "请授予允许读取应用状态权限", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


}
