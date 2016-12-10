package com.yyw.android.bestnow.userinfo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;

import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.archframework.BaseFragment;
import com.yyw.android.bestnow.common.utils.DateUtils;
import com.yyw.android.bestnow.data.dao.AppUsage;
import com.yyw.android.bestnow.userinfo.DailyInfoContract;

import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by samsung on 2016/11/3.
 */

public class DailyPagerFragment extends BaseFragment implements DailyInfoContract.View {
    @BindView(R.id.daily_view_pager)
    ViewPager dailyPager;
    DailyPagerAdapter adapter;
    DailyInfoContract.Presenter presenter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        adapter = new DailyPagerAdapter(getFragmentManager());
        dailyPager.setAdapter(adapter);
        dailyPager.setCurrentItem(adapter.getCount() - 1, false);
    }

    @Override
    public void setPresenter(DailyInfoContract.Presenter presenter) {
        this.presenter=presenter;
    }

    @Override
    public void displayTopAppUsages(Date date, List<AppUsage> topAppUsages) {

    }

    @Override
    public void displayEventList(Date date, List<String> events) {

    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_daily_pager;
    }

    public class DailyPagerAdapter extends FragmentStatePagerAdapter {
        private Map<Integer,Fragment> fragmentMap;

        public DailyPagerAdapter(FragmentManager fm) {
            super(fm);
            fragmentMap=new ArrayMap<>();
        }

        @Override
        public Fragment getItem(int position) {
//            loadFragmentInfo(position);
            if (position == getCount() - 1) {
                return new TodayFragment();
            } else {
                return new DailyInfoFragment();
            }
        }

        private void loadFragmentInfo(int position){
            Date date=computeFragmentDate(position);
            presenter.loadEventList(date);
            presenter.loadTopAppUsages(date);
        }

        private Date computeFragmentDate(int position){
            int end=getCount()-1;
            int offset=position-end;
            Date today=new Date();
            Date date=DateUtils.offsetDays(today,offset);
            return date;
        }

        private int computeFragmentPos(Date date){
            int end=getCount()-1;
            Date today=new Date();
            int days= DateUtils.daysBetween(date,today);
            int result=end-days;
            return result;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment=(Fragment)super.instantiateItem(container,position);
            fragmentMap.put(position,fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            fragmentMap.remove(position);
            super.destroyItem(container, position, object);
        }

        // 当 fragment 尚未初始化或者被回收了，就会返回null
        public Fragment getFragment(int position){
            return fragmentMap.get(position);
        }
    }


}
