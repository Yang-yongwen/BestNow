package com.yyw.android.bestnow.appusage.dailyusage;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.appusage.singleappusage.AppDailyUsageActivity;
import com.yyw.android.bestnow.appusage.singleappusage.AppDailyUsageFragment;
import com.yyw.android.bestnow.common.utils.DateUtils;
import com.yyw.android.bestnow.data.dao.AppUsage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yangyongwen on 16/11/27.
 */

public class DailyUsageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AppUsage> appUsages;
    private String date;

    public void setAppUsages(List<AppUsage> appUsages) {
        this.appUsages = appUsages;
        notifyDataSetChanged();
    }

    public void setDate(String date){
        this.date=date;
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

        AppUsageViewHolder(final View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void update(final AppUsage appUsage, final String date){
            usageTimeTV.setText(DateUtils.toDisplayFormat(appUsage.getTotalUsageTime()));
            launchCountTV.setText(String.valueOf(appUsage.getTotalLaunchCount()) + "次");
            appLabelTV.setText(appUsage.getLabel());
            appIconIV.setImageDrawable(appUsage.getAppIcon());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), AppDailyUsageActivity.class);
                    intent.putExtra(AppDailyUsageFragment.PACKAGE_NAME, appUsage.getPackageName());
                    intent.putExtra(AppDailyUsageFragment.DATE, date);
                    v.getContext().startActivity(intent);
                }
            });
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
        vh.update(appUsage,date);
    }

    @Override
    public int getItemCount() {
        return appUsages == null ? 0 : appUsages.size();
    }


}
