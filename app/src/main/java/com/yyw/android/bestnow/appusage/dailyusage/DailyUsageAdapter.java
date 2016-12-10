package com.yyw.android.bestnow.appusage.dailyusage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.common.utils.DateUtils;
import com.yyw.android.bestnow.data.appusage.UsageRepository;
import com.yyw.android.bestnow.data.dao.AppUsage;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * Created by yangyongwen on 16/11/27.
 */

public class DailyUsageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AppUsage> appUsages;

    public void setAppUsages(List<AppUsage> appUsages) {
        this.appUsages = appUsages;
        notifyDataSetChanged();
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
        vh.usageTimeTV.setText(DateUtils.toDisplayFormat(appUsage.getTotalUsageTime()));
        vh.launchCountTV.setText(String.valueOf(appUsage.getTotalLaunchCount())+"次");
        vh.appLabelTV.setText(appUsage.getLabel());
        vh.appIconIV.setImageDrawable(appUsage.getAppIcon());
    }

    @Override
    public int getItemCount() {
        return appUsages == null ? 0 : appUsages.size();
    }


}
