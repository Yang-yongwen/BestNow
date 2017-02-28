package com.yyw.android.bestnow.setting.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.common.utils.DateUtils;
import com.yyw.android.bestnow.data.appusage.AppInfoProvider;
import com.yyw.android.bestnow.data.appusage.AppPool;
import com.yyw.android.bestnow.data.dao.App;
import com.yyw.android.bestnow.setting.activity.InputFilterMinMax;

import java.util.List;

/**
 * Created by yangyongwen on 2016/12/16.
 */

public class AppLimitedListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<App> apps;
    AppPool appPool;
    AlertDialog alertDialog;

    public AppLimitedListAdapter(List<App> apps, AppPool appPool) {
        this.apps = apps;
        this.appPool = appPool;
    }

    public void addApp(App app) {
        this.apps.add(app);
        notifyDataSetChanged();
    }


    private class AppLimiteditemVH extends RecyclerView.ViewHolder {

        View cancelBtn;
        ImageView icon;
        TextView label;
        TextView limitedTV;

        int index;

        AppLimiteditemVH(View view) {
            super(view);
            icon = (ImageView) view.findViewById(R.id.app_icon);
            label = (TextView) view.findViewById(R.id.app_label);
            limitedTV = (TextView) view.findViewById(R.id.limit_usage_time);
            cancelBtn = view.findViewById(R.id.delete_btn);
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    App app = apps.remove(index);
                    app.setIsLimit(false);
                    app.setLimitTime(-1l);
                    appPool.saveApps(app);
                    notifyDataSetChanged();
                }
            });
        }

        void update(final App app, int index) {
            String packageName = app.getPackageName();
            icon.setImageDrawable(AppInfoProvider.getInstance().getAppIcon(packageName));
            label.setText(AppInfoProvider.getInstance().getAppLabel(packageName));
            limitedTV.setText("限制：" + DateUtils.toDisplayFormat(app.getLimitTime()));
            this.index = index;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog = buildDialog(v.getContext(), app);
                    alertDialog.show();
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_limited_app_item, parent, false);
        return new AppLimiteditemVH(root);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AppLimiteditemVH) {
            ((AppLimiteditemVH) holder).update(apps.get(position), position);
        }
    }


    private AlertDialog buildDialog(Context context, final App app) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_modify_limited_app, null);
        if (app.getLimitTime() == null) {
            return null;
        }

        long limitTime = app.getLimitTime();
        long hour = limitTime / 1000 * 60 * 60;
        long minute = limitTime / 1000 / 60 % 60;

        final EditText hourTV = (EditText) view.findViewById(R.id.edit_hour);
        hourTV.setFilters(new InputFilter[]{new InputFilterMinMax(0, 23)});
        hourTV.setText(String.valueOf(hour));
        final EditText minTV = (EditText) view.findViewById(R.id.edit_minute);
        minTV.setFilters(new InputFilter[]{new InputFilterMinMax(0, 59)});
        minTV.setText(String.valueOf(minute));
        ((ImageView) view.findViewById(R.id.app_icon)).setImageDrawable(AppInfoProvider.getInstance().getAppIcon(app.getPackageName()));
        ((TextView) view.findViewById(R.id.app_label)).setText(AppInfoProvider.getInstance().getAppLabel(app.getPackageName()));

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.set_usage_limit_tilte)
                .setView(view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (alertDialog != null) {
                            alertDialog.dismiss();
                            alertDialog = null;
                        }
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeLimitedTime(app, hourTV.getText().toString(), minTV.getText().toString());

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }

    private void changeLimitedTime(App app, String hourStr, String minStr) {
        app.setIsLimit(true);
        long hour = hourStr.length() == 0 ? 0 : Long.parseLong(hourStr);
        long minute = minStr.length() == 0 ? 0 : Long.parseLong(minStr);
        app.setLimitTime((hour * 60 + minute) * 60 * 1000);
        appPool.saveApps(app);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return apps.size();
    }
}
