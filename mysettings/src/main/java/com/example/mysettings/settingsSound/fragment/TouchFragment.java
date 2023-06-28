package com.example.mysettings.settingsSound.fragment;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.example.mysettings.R;

public class TouchFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Log.e("mono", "onPreferenceClick: prepare to change");
        setPreferencesFromResource(R.xml.touch_preferences, rootKey);
        getPreferenceScreen().findPreference("touch_sounds").setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        boolean touchSoundsEnabled = ((SwitchPreference) preference).isChecked();
        Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.SOUND_EFFECTS_ENABLED, touchSoundsEnabled ? 1 : 0);
        Log.e("mono", "onPreferenceClick: change");


        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("其他提示音");
    }
}
