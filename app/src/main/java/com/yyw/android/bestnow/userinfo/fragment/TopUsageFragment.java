package com.yyw.android.bestnow.userinfo.fragment;


import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.archframework.BaseFragment;
import com.yyw.android.bestnow.data.dao.AppUsage;
import com.yyw.android.bestnow.view.TopUsageItemView;

import butterknife.BindView;

/**
 * Created by samsung on 2016/10/31.
 */

public class TopUsageFragment extends BaseFragment {
    @BindView(R.id.top_usage_item_container)
    LinearLayout topUsageItemContainer;

    AppUsage[] topAppUsages = new AppUsage[3];
    TopUsageItemView[] itemViews = new TopUsageItemView[3];

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_top_usage;
    }

    private void initView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1);
        for (int i = 0; i < 3; ++i) {
            itemViews[i] = new TopUsageItemView(getContext());
        }
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(1, ViewGroup.LayoutParams.WRAP_CONTENT);
        topUsageItemContainer.addView(itemViews[0], layoutParams);
        topUsageItemContainer.addView(new View(getContext()), dividerParams);
        topUsageItemContainer.addView(itemViews[1], layoutParams);
        topUsageItemContainer.addView(new View(getContext()), dividerParams);
        topUsageItemContainer.addView(itemViews[2], layoutParams);
    }
}
