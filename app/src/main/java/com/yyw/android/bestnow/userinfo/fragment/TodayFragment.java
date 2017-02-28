package com.yyw.android.bestnow.userinfo.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.archframework.BaseFragment;
import com.yyw.android.bestnow.common.utils.ActivityUtils;
import com.yyw.android.bestnow.common.utils.DateUtils;

import java.util.Date;

/**
 * Created by yangyongwen on 2016/11/4.
 */

public class TodayFragment extends BaseFragment {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String date = DateUtils.FORMAT_DAY.format(new Date());
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        DailyInfoFragment fragment = DailyInfoFragment.newInstance(date);
        ActivityUtils.addFragmentToActivity(fragmentManager, fragment, R.id.today_info);
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_today;
    }

}
