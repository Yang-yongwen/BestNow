package com.yyw.android.bestnow.setting.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.data.appusage.AppInfoProvider;
import com.yyw.android.bestnow.data.dao.App;

import java.util.List;

/**
 * Created by yangyongwen on 2016/12/16.
 */

public class AppListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    Context context;
    List<App> apps;

    public App selectedApp;
    public int selectIndex = -1;

    public AppListAdapter(Context context, List<App> apps) {
        this.context = context;
        this.apps = apps;
    }


    public App getSelectedApp() {
        return apps.get(selectIndex);
    }

    public void deleteSelectedApp() {
        apps.remove(selectIndex);
        selectIndex = -1;
        notifyDataSetChanged();
    }

    private class AppItemVH extends RecyclerView.ViewHolder {
        ImageView appIconIV;
        TextView appLabelTV;
        int index;

        AppItemVH(View view) {
            super(view);
            view.findViewById(R.id.select_box).setVisibility(View.GONE);
            appIconIV = (ImageView) view.findViewById(R.id.app_icon);
            appLabelTV = (TextView) view.findViewById(R.id.app_label);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectIndex = index;
                    notifyDataSetChanged();
                }
            });
        }

        void update(App app, int index) {
            appLabelTV.setText(AppInfoProvider.getInstance().getAppLabel(app.getPackageName()));
            appIconIV.setImageDrawable(AppInfoProvider.getInstance().getAppIcon(app.getPackageName()));
            this.index = index;
            if (index == selectIndex) {
                itemView.setBackgroundColor(context.getResources().getColor(R.color.colorBackground));
            } else {
                itemView.setBackgroundColor(Color.WHITE);
            }

        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_app_normal_item, parent, false);
        return new AppItemVH(root);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AppItemVH) {
            AppItemVH vh = ((AppItemVH) holder);
            App app = apps.get(position);
            vh.update(app, position);
        }
    }

    @Override
    public int getItemCount() {
        return apps == null ? 0 : apps.size();
    }

}
