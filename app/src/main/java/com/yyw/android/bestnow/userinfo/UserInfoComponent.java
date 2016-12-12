package com.yyw.android.bestnow.userinfo;

import com.yyw.android.bestnow.archframework.FragmentScoped;
import com.yyw.android.bestnow.userinfo.activity.UserInfoActivity;

import dagger.Subcomponent;

/**
 * Created by yangyongwen on 16/12/10.
 */
@FragmentScoped
@Subcomponent(modules = UserInfoModule.class)
public interface UserInfoComponent {
    void inject(UserInfoActivity activity);
}
