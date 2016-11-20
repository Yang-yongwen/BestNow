package com.yyw.android.bestnow.userinfo.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.archframework.BaseFragment;

import butterknife.BindView;

/**
 * Created by samsung on 2016/11/4.
 */

public class EventListFragment extends BaseFragment {
    @BindView(R.id.event_type)
    TextView eventTypeTV;
    RecyclerView eventListRV;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_event_list;
    }

}
