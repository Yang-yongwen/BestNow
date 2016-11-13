package com.yyw.android.bestnow.common.utils;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by samsung on 2016/10/28.
 */

@Singleton
public class SPUtils {
    private static final String BEST_NOW_SP = "BestNow";
    SharedPreferences sp;
    Context context;

    @Inject
    public SPUtils(Context context) {
        this.context = context;
        sp = context.getSharedPreferences(BEST_NOW_SP, Context.MODE_PRIVATE);
    }

    public long getLongValue(String key, long defaultValue) {
        return sp.getLong(key, defaultValue);
    }

    public void putLongValue(String key, long value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.apply();
    }

}
