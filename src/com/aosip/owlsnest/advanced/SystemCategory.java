/*
 * Copyright (C) 2017 Android Open Source Illusion Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aosip.owlsnest.advanced;

import android.os.Bundle;
import android.content.ContentResolver;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;

import com.android.settings.Utils;

public class SystemCategory extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String HEADSET_CONNECT_PLAYER = "headset_connect_player";
    private static final String RINGTONE_FOCUS_MODE = "ringtone_focus_mode";
    private static final String SYSTEMUI_THEME_STYLE = "systemui_theme_style";
    private static final String INCALL_VIB_OPTIONS = "incall_vib_options";

    private ListPreference mLaunchPlayerHeadsetConnection;
    private ListPreference mHeadsetRingtoneFocus;
    private ListPreference mSystemUIThemeStyle;

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.OWLSNEST;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.system);

        final ContentResolver resolver = getActivity().getContentResolver();

        PreferenceScreen prefScreen = getPreferenceScreen();

        mLaunchPlayerHeadsetConnection = (ListPreference) findPreference(HEADSET_CONNECT_PLAYER);
        int mLaunchPlayerHeadsetConnectionValue = Settings.System.getIntForUser(resolver,
                Settings.System.HEADSET_CONNECT_PLAYER, 4, UserHandle.USER_CURRENT);
        mLaunchPlayerHeadsetConnection.setValue(Integer.toString(mLaunchPlayerHeadsetConnectionValue));
        mLaunchPlayerHeadsetConnection.setSummary(mLaunchPlayerHeadsetConnection.getEntry());
        mLaunchPlayerHeadsetConnection.setOnPreferenceChangeListener(this);

        mHeadsetRingtoneFocus = (ListPreference) findPreference(RINGTONE_FOCUS_MODE);
        int mHeadsetRingtoneFocusValue = Settings.Global.getInt(resolver,
                Settings.Global.RINGTONE_FOCUS_MODE, 0);
        mHeadsetRingtoneFocus.setValue(Integer.toString(mHeadsetRingtoneFocusValue));
        mHeadsetRingtoneFocus.setSummary(mHeadsetRingtoneFocus.getEntry());
        mHeadsetRingtoneFocus.setOnPreferenceChangeListener(this);

        mSystemUIThemeStyle = (ListPreference) findPreference(SYSTEMUI_THEME_STYLE);
        int systemUIThemeStyle = Settings.System.getInt(resolver,
                Settings.System.SYSTEM_UI_THEME, 0);
        int valueIndex = mSystemUIThemeStyle.findIndexOfValue(String.valueOf(systemUIThemeStyle));
        mSystemUIThemeStyle.setValueIndex(valueIndex >= 0 ? valueIndex : 0);
        mSystemUIThemeStyle.setSummary(mSystemUIThemeStyle.getEntry());
        mSystemUIThemeStyle.setOnPreferenceChangeListener(this);

        PreferenceCategory incallVibCategory = (PreferenceCategory) findPreference(INCALL_VIB_OPTIONS);
        if (!Utils.isVoiceCapable(getActivity())) {
            prefScreen.removePreference(incallVibCategory);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mLaunchPlayerHeadsetConnection) {
            int mLaunchPlayerHeadsetConnectionValue = Integer.valueOf((String) newValue);
            int index = mLaunchPlayerHeadsetConnection.findIndexOfValue((String) newValue);
            mLaunchPlayerHeadsetConnection.setSummary(
                    mLaunchPlayerHeadsetConnection.getEntries()[index]);
            Settings.System.putIntForUser(resolver, Settings.System.HEADSET_CONNECT_PLAYER,
                    mLaunchPlayerHeadsetConnectionValue, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mHeadsetRingtoneFocus) {
            int mHeadsetRingtoneFocusValue = Integer.valueOf((String) newValue);
            int index = mHeadsetRingtoneFocus.findIndexOfValue((String) newValue);
            mHeadsetRingtoneFocus.setSummary(
                    mHeadsetRingtoneFocus.getEntries()[index]);
            Settings.Global.putInt(resolver, Settings.Global.RINGTONE_FOCUS_MODE,
                    mHeadsetRingtoneFocusValue);
            return true;
        } else if (preference == mSystemUIThemeStyle) {
            String value = (String) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.SYSTEM_UI_THEME, Integer.valueOf(value));
            int valueIndex = mSystemUIThemeStyle.findIndexOfValue(value);
            mSystemUIThemeStyle.setSummary(mSystemUIThemeStyle.getEntries()[valueIndex]);
            return true;
        }
        return false;
    }
}

