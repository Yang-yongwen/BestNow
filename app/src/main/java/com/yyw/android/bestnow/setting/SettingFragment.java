package com.yyw.android.bestnow.setting;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.yyw.android.bestnow.R;

/**
 * Created by yangyongwen on 16/12/11.
 */

public class SettingFragment extends PreferenceFragment{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_setting);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key=preference.getKey();
        if (key==null){
            return false;
        }
        if (key.equals("pref_set_checkable_app")){
            Intent intent=new Intent(getActivity(), SettingCheckableAppActivity.class);
            startActivity(intent);
        }else {
            return false;
        }
        return true;
    }
}
