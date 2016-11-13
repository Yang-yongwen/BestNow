package com.yyw.android.bestnow.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.yyw.android.bestnow.R;

import butterknife.BindView;

/**
 * Created by samsung on 2016/11/3.
 */

public class DailyPagerFragment extends BaseFragment {
    @BindView(R.id.daily_view_pager)
    ViewPager dailyPager;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        DailyPagerAdapter adapter = new DailyPagerAdapter(getFragmentManager());
        dailyPager.setAdapter(adapter);
        dailyPager.setCurrentItem(adapter.getCount() - 1, false);
    }


    public static class DailyPagerAdapter extends FragmentStatePagerAdapter {
        public DailyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == getCount() - 1) {
                return new TodayFragment();
            } else {
                return new DailyInfoFragment();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_daily_pager;
    }
}
