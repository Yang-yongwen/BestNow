package com.yyw.android.bestnow.setting.adapter;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.data.appusage.AppInfoProvider;
import com.yyw.android.bestnow.data.dao.App;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangyongwen on 16/12/16.
 */

public class AppCheckedListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_STATISTIC_HEADER = 0;
    private static final int TYPE_UN_STATISTIC_HEADER = 1;
    private static final int TYPE_APP_ITEM = 2;


    public List<App> statisticApps;
    public List<App> unStatisticApps;
    private boolean showStatisticApps = true;
    private boolean showUnStatisticApps = false;

    private List<App> selectedApps = new ArrayList<>();

    public class HeaderVH extends RecyclerView.ViewHolder {
        int type;
        @BindView(R.id.done_event_num_text)
        TextView titleTV;
        @BindView(R.id.arrow)
        View arrow;

        @OnClick(R.id.divider_container)
        public void changeShowList() {
            if (type == TYPE_STATISTIC_HEADER) {
                if (showStatisticApps) {
                    showStatisticApps = false;
                    selectedApps.clear();
                    ViewCompat.setRotation(arrow, 180);
                } else {
                    showStatisticApps = true;
                    showUnStatisticApps = false;
                    ViewCompat.setRotation(arrow, 0);
                }
            } else {
                if (showUnStatisticApps) {
                    selectedApps.clear();
                    showUnStatisticApps = false;
                    ViewCompat.setRotation(arrow, 180);
                } else {
                    showUnStatisticApps = true;
                    showStatisticApps = false;
                    ViewCompat.setRotation(arrow, 0);
                }
            }
            notifyDataSetChanged();
        }

        HeaderVH(View view, int type) {
            super(view);
            ButterKnife.bind(this, view);
            this.type = type;
            update();
        }

        void update() {
            if (type == TYPE_STATISTIC_HEADER) {
                titleTV.setText(statisticApps.size() + "个 App 正在统计使用情况");
            } else {
                titleTV.setText(unStatisticApps.size() + "个 App 已忽略");
                if (showStatisticApps) {
                    ViewCompat.setRotation(arrow, 180);
                }
            }
        }
    }

    public class AppItemVH extends RecyclerView.ViewHolder {

        @BindView(R.id.app_label)
        TextView appLabelTV;
        @BindView(R.id.app_icon)
        ImageView appIconIV;
        @BindView(R.id.select_box)
        CheckBox selectBox;

        App app;


        AppItemVH(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectBox.setChecked(!selectBox.isChecked());
                }
            });

        }

        public void update(App a) {
            this.app = a;
            appIconIV.setImageDrawable(AppInfoProvider.getInstance().getAppIcon(app.getPackageName()));
            appLabelTV.setText(app.getLabel());
            selectBox.setOnCheckedChangeListener(null);
            selectBox.setChecked(selectedApps.contains(app));
            selectBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selectedApps.add(app);
                    } else {
                        selectedApps.remove(app);
                    }
                }
            });
        }
    }

    public void ensureDelete() {
        if (selectedApps.size() == 0) {
            return;
        }
        if (selectedApps.get(0).getShouldStatistic()) {
            for (App app : selectedApps) {
                app.setShouldStatistic(false);
            }
            statisticApps.removeAll(selectedApps);
            unStatisticApps.addAll(selectedApps);
        } else {
            for (App app : selectedApps) {
                app.setShouldStatistic(true);
            }
            unStatisticApps.removeAll(selectedApps);
            statisticApps.addAll(selectedApps);
        }
        selectedApps.clear();
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_STATISTIC_HEADER) {
            View root = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_event_list_divider, parent, false);
            return new HeaderVH(root, viewType);
        } else if (viewType == TYPE_UN_STATISTIC_HEADER) {
            View root = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_event_list_divider, parent, false);
            return new HeaderVH(root, viewType);
        } else {
            View root = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_app_normal_item, parent, false);
            return new AppItemVH(root);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AppItemVH) {
            if (showStatisticApps) {
                ((AppItemVH) holder).update(statisticApps.get(position - 1));
            } else {
                ((AppItemVH) holder).update(unStatisticApps.get(position - 2));
            }
        } else if (holder instanceof HeaderVH) {
            ((HeaderVH) holder).update();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (showStatisticApps) {
            if (position == 0) {
                return TYPE_STATISTIC_HEADER;
            } else if (position == statisticApps.size() + 1) {
                return TYPE_UN_STATISTIC_HEADER;
            } else {
                return TYPE_APP_ITEM;
            }
        } else if (showUnStatisticApps) {
            if (position == 0) {
                return TYPE_STATISTIC_HEADER;
            } else if (position == 1) {
                return TYPE_UN_STATISTIC_HEADER;
            } else {
                return TYPE_APP_ITEM;
            }
        } else {
            if (position == 0) {
                return TYPE_STATISTIC_HEADER;
            } else {
                return TYPE_UN_STATISTIC_HEADER;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (showStatisticApps) {
            return statisticApps.size() + 2;
        } else if (showUnStatisticApps) {
            return unStatisticApps.size() + 2;
        } else {
            return 2;
        }
    }
}
