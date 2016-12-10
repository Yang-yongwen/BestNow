package com.yyw.android.bestnow.userinfo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.ArrayMap;
import android.view.ViewGroup;

import com.yyw.android.bestnow.userinfo.fragment.DailyInfoFragment;
import com.yyw.android.bestnow.userinfo.fragment.TodayFragment;

import java.util.Map;

/**
 * Created by yangyongwen on 16/12/6.
 */

public class DailyPagerAdapter extends FragmentStatePagerAdapter {
    private Map<Integer,Fragment> fragmentMap;

    public DailyPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentMap=new ArrayMap<>();
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
