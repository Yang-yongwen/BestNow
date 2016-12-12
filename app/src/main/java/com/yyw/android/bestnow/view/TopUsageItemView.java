package com.yyw.android.bestnow.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.data.dao.AppUsage;

import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by samsung on 2016/10/31.
 */

public class TopUsageItemView extends LinearLayout {

    @BindView(R.id.app_icon)
    ImageView iconIV;
    @BindView(R.id.usage_time)
    TextView usageTimeTV;
    @BindView(R.id.usage_percent)
    TextView usagePercentTV;


    public TopUsageItemView(Context context) {
        this(context, null);
    }

    public TopUsageItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.item_view_top_usage, this, true);
        ButterKnife.bind(this);
    }

    public void setAppUsage(AppUsage appUsage){
        setAppIcon(appUsage.getAppIcon());
        setUsageTime(appUsage.getTotalUsageTime());
    }

    private void setAppIcon(Drawable bitmap) {
        iconIV.setImageDrawable(bitmap);
    }

    private void setUsageTime(long timeInMills) {
        String time = convertTimeFormat(timeInMills);
        usageTimeTV.setText(time);
    }

    private String convertTimeFormat(long timeInMills) {
        long second = timeInMills / 1000;
        if (second < 60) {
            return Long.toString(second) + "秒";
        }
        long minute = second / 60;
        if (minute < 60) {
            return Long.toString(minute) + "分钟";
        }
        long hour=minute/60;
        minute%=60;
        return String.valueOf(hour)+"小时"+String.valueOf(minute)+"分钟";
    }

    public void setUsagePercent(double percent) {
        NumberFormat numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMinimumFractionDigits(0);
        String p = numberFormat.format(percent);
        usagePercentTV.setText(p);
    }

}
