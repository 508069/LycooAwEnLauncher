package com.mono.mysettings.settingsSound.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.mono.mysettings.settingsSound.activity.TouchSoundActivity;
import com.lycoo.commons.util.SystemPropertiesUtils;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.TwoStatePreference;

import com.example.mysettings.R;

public class SoundSettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener
        ,Preference.OnPreferenceClickListener{
    private AudioManager audioManager;

    private Context mContext;
    private Preference otherSoundSettings;

    private static final String PROPERTY_SALUTATORY_STATUS = "persist.sys.salutatory.enable";
    private TwoStatePreference mSalutatoryPref;
    private static final String SALUTATORY_SHOW = "ro.sys.salutatory.show";
    private SeekBarPreference volumePreference;

    public SoundSettingsFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.sound_preferences, rootKey);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        volumePreference = findPreference("volume");
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volumePreference.setMax(maxVolume);
        volumePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int volume = (int) newValue;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                return true;
            }
        });

        otherSoundSettings = findPreference("other_sound_settings");
        otherSoundSettings.setOnPreferenceClickListener(this);


        initUpdateSalutatory();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("声音设置");
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("volume")) {
            int volume = sharedPreferences.getInt("volume", 50);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        }
    }

    @Override
    public boolean onPreferenceClick(@NonNull Preference preference) {
        if (preference == otherSoundSettings){
            Log.e("mono", "onPreferenceClick: touch sound");
            Intent intent = new Intent(mContext, TouchSoundActivity.class);
            startActivity(intent);
        }
        return true;
    }


    private void initUpdateSalutatory() {
        mSalutatoryPref = (TwoStatePreference) getPreferenceScreen().findPreference("salutatory");
        if (mSalutatoryPref == null) {
            return;
        }
        if (!isSalutatoryShow()) {
            getPreferenceScreen().removePreference(mSalutatoryPref);
            mSalutatoryPref = null;
            return;
        }
        mSalutatoryPref.setPersistent(false);
        mSalutatoryPref.setChecked(isSalutatoryEnable());
        mSalutatoryPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final boolean val = (Boolean) newValue;
                toggleSalutatoryStatus();
                return true;
            }
        });
    }


    private boolean isSalutatoryEnable(){
        return SystemPropertiesUtils.getBoolean(PROPERTY_SALUTATORY_STATUS, false);
    }

    private boolean isSalutatoryShow(){
        return SystemPropertiesUtils.getBoolean(SALUTATORY_SHOW, true);
    }

    private void toggleSalutatoryStatus() {
        boolean enable = isSalutatoryEnable();
        SystemPropertiesUtils.set(mContext, PROPERTY_SALUTATORY_STATUS, enable ? "false" : "true");
        mSalutatoryPref.setChecked(!enable);

    }
}
