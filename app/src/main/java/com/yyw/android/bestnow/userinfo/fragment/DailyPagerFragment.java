package com.yyw.android.bestnow.userinfo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;

import com.yyw.android.bestnow.NowApplication;
import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.archframework.BaseActivity;
import com.yyw.android.bestnow.archframework.BaseFragment;
import com.yyw.android.bestnow.common.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by yangyongwen on 2016/11/3.
 */

public class DailyPagerFragment extends BaseFragment {
    @BindView(R.id.daily_view_pager)
    ViewPager dailyPager;
    DailyPagerAdapter adapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        adapter = new DailyPagerAdapter(getFragmentManager());
        dailyPager.setAdapter(adapter);
        dailyPager.setCurrentItem(adapter.getCount() - 1, false);
        dailyPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                String date = genSubTitle(position);
                ((BaseActivity) getActivity()).setSubtitle(date);
            }
        });
    }

    private String genSubTitle(int position) {
        int end = adapter.getCount() - 1;
        int offset = position - end;
        if (offset == 0) {
            return "今天";
        } else if (offset == -1) {
            return "昨天";
        }
        Date today = new Date();
        Date date = DateUtils.offsetDays(today, offset);
        return DateUtils.FORMAT_DAY.format(date);
    }

    public void changeToDate(Date date) {
        int pos = adapter.computeFragmentPos(date);
        dailyPager.setCurrentItem(pos);
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_daily_pager;
    }

    public class DailyPagerAdapter extends FragmentStatePagerAdapter {
        private Map<Integer, Fragment> fragmentMap;

        public DailyPagerAdapter(FragmentManager fm) {
            super(fm);
            fragmentMap = new ArrayMap<>();
        }

        @Override
        public Fragment getItem(int position) {
//            loadFragmentInfo(position);
            if (position == getCount() - 1) {
                return new TodayFragment();
            } else {
                String date = computeFragmentDate(position);
                return DailyInfoFragment.newInstance(date);
            }
        }

//        private void loadFragmentInfo(int position) {
//            Date date = computeFragmentDate(position);
//            presenter.loadEventList(String);
//            presenter.loadTopAppUsages(date);
//        }

        private String computeFragmentDate(int position) {
            int end = getCount() - 1;
            int offset = position - end;
            Date today = new Date();
            Date date = DateUtils.offsetDays(today, offset);
            return DateUtils.FORMAT_DAY.format(date);
        }

        public int computeFragmentPos(Date date) {
            int end = getCount() - 1;
            Date today = new Date();
            int days = DateUtils.daysBetween(date, today);
            int result = end - days;
            return result;
        }

        @Override
        public int getCount() {
            String installDate = NowApplication.getInstance().getInstallDate();
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(installDate);
            } catch (Exception e) {

            }
            int installedDays = DateUtils.daysBetween(date, new Date());
            return installedDays + 1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            fragmentMap.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            fragmentMap.remove(position);
            super.destroyItem(container, position, object);
        }

        // 当 fragment 尚未初始化或者被回收了，就会返回null
        public Fragment getFragment(int position) {
            return fragmentMap.get(position);
        }
    }

}
