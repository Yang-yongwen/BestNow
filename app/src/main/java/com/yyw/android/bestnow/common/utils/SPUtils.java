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

    public boolean getBooleanValue(String key,boolean defaultValue){
        return sp.getBoolean(key,defaultValue);
    }

    public void putBooleanValue(String key,boolean value){
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public int getIntValue(String key,int defaultValue){
        return sp.getInt(key,defaultValue);
    }

    public void putIntValue(String key,int value){
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public String getStringValue(String key,String defaultValue){
        return sp.getString(key,defaultValue);
    }

    public void putStringValue(String key,String value){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

}
