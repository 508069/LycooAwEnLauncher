package com.example.mysettings.settingsMain.fragment;

import android.content.Intent;
import com.example.mysettings.settingDisplay.activity.DisplaySettingActivity;
import com.example.mysettings.settingInputAndLanguage.activity.InputAndLanguageActivity;
import com.example.mysettings.settingStorage.activity.StorageSettingActivity;
import com.example.mysettings.settingWifi.activity.WifiActivity;
import com.example.mysettings.settingsSound.activity.SoundSettingsActivity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.mysettings.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    private Preference mSoundPref;
    private Preference mWifiPref;
    private Preference mDisplayPref;
    private Preference mLanguagePref;
    private Preference mStoragePref;
    //    private Context mContext;
//
//    public SettingsFragment(Context mContext) {
//        this.mContext = mContext;
//    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey);

        mSoundPref = findPreference("sounds_setting");
        mSoundPref.setOnPreferenceClickListener(preference -> {
            Intent soundIntent = new Intent(getContext(), SoundSettingsActivity.class);
            startActivity(soundIntent);
            return true;
        });

        mWifiPref = findPreference("wifi_setting");
        mWifiPref.setOnPreferenceClickListener(preference -> {
            Intent wifiIntent = new Intent(getContext(), WifiActivity.class);
            startActivity(wifiIntent);
            return true;
        });

        mDisplayPref = findPreference("display_setting");
        mDisplayPref.setOnPreferenceClickListener(preference -> {
            Intent displayIntent = new Intent(getContext(), DisplaySettingActivity.class);
            startActivity(displayIntent);
            return true;
        });

        mLanguagePref = findPreference("language_setting");
        mLanguagePref.setOnPreferenceClickListener(preference -> {
            Intent languageIntent = new Intent(getContext(), InputAndLanguageActivity.class);
            startActivity(languageIntent);
            return true;
        });

        mStoragePref = findPreference("storage");
        mStoragePref.setOnPreferenceClickListener(preference -> {
            Intent StorageIntent = new Intent(getContext(), StorageSettingActivity.class);
            startActivity(StorageIntent);
            return true;
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("设置");
    }
}
