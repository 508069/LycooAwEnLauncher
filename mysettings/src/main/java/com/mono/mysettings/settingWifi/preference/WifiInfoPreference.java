package com.mono.mysettings.settingWifi.preference;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.example.mysettings.R;


public class WifiInfoPreference extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.wifiinfo_dialog, rootKey);

    }
}
