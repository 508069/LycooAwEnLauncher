package com.mono.mysettings.settingsSound.activity;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mono.mysettings.settingsSound.fragment.SoundSettingsFragment;
import com.example.mysettings.R;


public class SoundSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sound_settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SoundSettingsFragment(this))
                .commit();
    }
}

