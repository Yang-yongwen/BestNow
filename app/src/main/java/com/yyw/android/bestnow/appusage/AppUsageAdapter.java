package com.yyw.android.bestnow.appusage;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yyw.android.bestnow.NowApplication;
import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.data.dao.AppUsage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.pm.PackageManager.GET_ACTIVITIES;

/**
 * Created by yangyongwen on 16/11/27.
 */

public class AppUsageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AppUsage> appUsages;

    public void setAppUsages(Collection<AppUsage> appUsages) {
        this.appUsages = new ArrayList<>(appUsages);
        sortAppUsages();
        notifyDataSetChanged();
    }

    private void sortAppUsages() {
        Collections.sort(appUsages, new Comparator<AppUsage>() {
            @Override
            public int compare(AppUsage o1, AppUsage o2) {
                if (o1.getTotalUsageTime() > o2.getTotalUsageTime()) {
                    return -1;
                } else if (o1.getTotalUsageTime() < o2.getTotalUsageTime()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }

    public static class AppUsageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.launch_count)
        TextView launchCountTV;
        @BindView(R.id.usage_time)
        TextView usageTimeTV;
        @BindView(R.id.app_label)
        TextView appLabelTV;
        @BindView(R.id.app_icon)
        ImageView appIconIV;
        AppUsageViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_app_usage, parent, false);
        return new AppUsageViewHolder(root);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AppUsage appUsage = appUsages.get(position);
        AppUsageViewHolder vh = (AppUsageViewHolder) holder;
        vh.usageTimeTV.setText(converTime(appUsage.getTotalUsageTime()));
        vh.launchCountTV.setText(String.valueOf(appUsage.getTotalLaunchCount())+"次");
        PackageManager packageManager= NowApplication.getInstance().getPackageManager();
        try{
            PackageInfo info=packageManager.getPackageInfo(appUsage.getPackageName(),PackageManager.GET_ACTIVITIES);
            String appLabel=packageManager.getApplicationLabel(info.applicationInfo).toString();
            vh.appLabelTV.setText(appLabel);

            Drawable icon=packageManager.getApplicationIcon(info.applicationInfo);
            vh.appIconIV.setImageDrawable(icon);

        }catch (Exception e){

        }
    }

    private String converTime(long time) {
        int timeInSec = (int) (time / 1000);
        int sec = timeInSec % 60;
        int timeInMin = timeInSec / 60;
        int min = timeInMin % 60;
        int hour = timeInMin / 60;
        String text = "";
        if (hour == 0) {
            if (min == 0) {
                text = sec + "秒";
            } else {
                text = text + min + "分钟" + sec + "秒";
            }
        } else {
            text = text + hour + "小时" + min + "分钟";
        }
        return text;
    }

    @Override
    public int getItemCount() {
        return appUsages == null ? 0 : appUsages.size();
    }
}
