package com.yyw.android.bestnow.setting.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.yyw.android.bestnow.NowApplication;
import com.yyw.android.bestnow.R;
import com.yyw.android.bestnow.common.utils.SPUtils;
import com.yyw.android.bestnow.data.appusage.UpdateService;
import com.yyw.android.bestnow.setting.activity.SettingAddTimeLimitActivity;
import com.yyw.android.bestnow.setting.activity.SettingCheckableAppActivity;

/**
 * Created by yangyongwen on 16/12/11.
 */

public class SettingFragment extends PreferenceFragment {
    CheckBoxPreference notificationBox;
    SPUtils spUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spUtils = NowApplication.getApplicationComponent().provideSpUtils();
        addPreferencesFromResource(R.xml.preference_setting);
        initView();
    }

    private void initView() {
        notificationBox = (CheckBoxPreference) findPreference("pref_show_notification");
        notificationBox.setChecked(spUtils.getBooleanValue(UpdateService.SHOW_NOTIFICATION, true));
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        if (key == null) {
            return false;
        }
        if (key.equals("pref_set_checkable_app")) {
            Intent intent = new Intent(getActivity(), SettingCheckableAppActivity.class);
            startActivity(intent);
        } else if (key.equals("pref_show_notification")) {
            spUtils.putBooleanValue(UpdateService.SHOW_NOTIFICATION, notificationBox.isChecked());
            if (notificationBox.isChecked()) {
                UpdateService.showNotification(getActivity());
            } else {
                UpdateService.hideNotification(getActivity());
            }
            return true;
        } else if (key.equals("pref_set_usage_limit")) {
            Intent intent = new Intent(getActivity(), SettingAddTimeLimitActivity.class);
            startActivity(intent);
            return true;
        }
        return true;
    }
}
