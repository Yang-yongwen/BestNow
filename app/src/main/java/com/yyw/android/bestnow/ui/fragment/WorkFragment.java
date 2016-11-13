package com.yyw.android.bestnow.ui.fragment;

import android.widget.Button;

import com.yyw.android.bestnow.R;

import butterknife.BindView;

/**
 * Created by samsung on 2016/11/4.
 */

public class WorkFragment extends BaseFragment {
    @BindView(R.id.start_btn)
    Button startBtn;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_work;
    }

}
